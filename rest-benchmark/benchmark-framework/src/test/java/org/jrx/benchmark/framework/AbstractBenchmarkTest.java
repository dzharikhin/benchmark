package org.jrx.benchmark.framework;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.Function;
import java.util.function.LongToDoubleFunction;
import java.util.function.Supplier;
import java.util.stream.LongStream;

import static java.text.MessageFormat.format;
import static org.jrx.benchmark.framework.ComparisonResult.ComparisonStatus.TEST_WORSE;

public abstract class AbstractBenchmarkTest {

  protected static TestContainer buildContainer(Class<?>... classes) {
    Application application = new DefaultResourceConfig(classes);
    Servlet servlet = new ServletContainer(application);
    ServletHolder servletHolder = new ServletHolder("MainServlet", servlet);
    int port = getFreePort();
    Server server = new Server(port);
    server.setHandler(createHandler(servletHolder));
    return new TestContainer(server, port);
  }

  protected static ResponseTimeStats executeLoad(Load load, long iterations) {
    TestContainer testContainer = load.containerSupplier.get();
    testContainer.start();
    ResponseTimeStats stats = executeIterations(load.getLoadFunction(testContainer), iterations);
    testContainer.stop();
    return stats;
  }

  protected static String buildMessage(Metric metric, TestResults testResults) {
    ComparisonResult result = testResults.getResult(metric);
    if (result.is(ComparisonResult.ComparisonStatus.EQUALS)) {
      return format("{0}: {1} and {2} has same value={3,number,integer}{4}",
          metric.getHumanReadableValue(),
          testResults.getTestWorkloadName(),
          testResults.getReferenceWorkloadName(),
          result.getBaseValue(),
          metric.getUnit()
      );
    } else if (result.is(TEST_WORSE)) {
      return format("{0}: {1} is worse than {2} on {3,number,integer}{4}({5, number, percent})",
          metric.getHumanReadableValue(),
          testResults.getTestWorkloadName(),
          testResults.getReferenceWorkloadName(),
          result.getAbsoluteDiff(),
          metric.getUnit(),
          result.getWorseOnPercent()
      );
    } else {
      return format("{0}: {2} is worse than {1} on {3,number,integer}{4}({5, number, percent})",
          metric.getHumanReadableValue(),
          testResults.getTestWorkloadName(),
          testResults.getReferenceWorkloadName(),
          result.getAbsoluteDiff(),
          metric.getUnit(),
          result.getWorseOnPercent()
      );
    }
  }

  private static ResponseTimeStats executeIterations(LongToDoubleFunction processingFunction, long iterations) {
    double[] responses = LongStream.range(0, iterations).mapToDouble(processingFunction).toArray();
    return ResponseTimeStats.of(responses);
  }

  private static int getFreePort() {
    try(ServerSocket serverSocket = new ServerSocket(0)){
      return serverSocket.getLocalPort();
    } catch (IOException e) {
      throw new RuntimeException("failed to find free port", e);
    }
  }

  private static Handler createHandler(ServletHolder servletHolder) {
    ServletContextHandler contextHandler = new ServletContextHandler();
    contextHandler.addServlet(servletHolder, "/*");
    return contextHandler;
  }

  protected static class Load implements Workload {
    private final String name;
    private final Supplier<TestContainer> containerSupplier;
    private final Function<TestContainer, CloseableHttpResponse> workload;

    protected Load(String name, Supplier<TestContainer> containerSupplier, Function<TestContainer, CloseableHttpResponse> workload) {
      this.name = name;
      this.containerSupplier = containerSupplier;
      this.workload = workload;
    }

    @Override
    public String getName() {
      return name;
    }

    public LongToDoubleFunction getLoadFunction(TestContainer container) {
      return l -> {
        long init = System.nanoTime();
        try (CloseableHttpResponse response = workload.apply(container)) {
          if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new RuntimeException("non 200 response: " + response.getStatusLine().getStatusCode());
          }
          EntityUtils.consumeQuietly(response.getEntity());
          return System.nanoTime() - init;
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      };
    }
  }

  protected static final class TestContainer {

    private final Server delegate;
    private final HttpHost host;
    private final CloseableHttpClient client;

    private TestContainer(Server delegate, int port) {
      this.delegate = delegate;
      this.host = new HttpHost("localhost", port);
      this.client = buildClient();
    }

    public void start() {
      try {
        this.delegate.start();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    public void stop() {
      try {
        this.client.close();
        this.delegate.stop();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    public CloseableHttpResponse execute(HttpRequest req) {
      try {
        return client.execute(host, req);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    private static CloseableHttpClient buildClient() {
      PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
      manager.setMaxTotal(64);
      manager.setDefaultMaxPerRoute(64);
      return HttpClients.createMinimal(manager);
    }
  }
}
