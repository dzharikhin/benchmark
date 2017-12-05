package org.jrx.benchmark.framework;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

import static org.jrx.benchmark.framework.TestResults.executeBenchmark;

@Ignore
public class ExampleBenchmarkTest extends AbstractBenchmarkTest {

  private static final HttpRequest TEST_REQ = new HttpGet("/test");
  private static final int WARMUP = 2;
  private static final int REPETITIONS = 20;
  private static final long ITERATIONS = 1_00_000;
  private static final double SUCCESS_THRESHOLD = 0.1;
  private static final Collection<Metric> METRICS = Collections.unmodifiableCollection(EnumSet.allOf(Metric.class));
  private static final Collection<Metric> CRITICAL_METRICS = Collections.unmodifiableCollection(Arrays.asList(Metric.AVERAGE, Metric.MEDIAN));

  @Test
  public void benchmark() {
    Load testWorkload = new Load("testWorkload", () -> buildContainer(StubResource.class), container -> container.execute(TEST_REQ));
    Load referenceWorkload = new Load("referenceWorkload", () -> buildContainer(StubResource.class), container -> container.execute(TEST_REQ));
    TestResults testResults = executeBenchmark(
      ExampleBenchmarkTest::executeLoad,
      referenceWorkload, testWorkload,
      METRICS, WARMUP, REPETITIONS, ITERATIONS
    );
    METRICS.forEach(metric -> buildMessage(metric, testResults));
    testResults.assertResults(CRITICAL_METRICS, SUCCESS_THRESHOLD);
  }

  @Path("test")
  public static class StubResource {
    @GET
    public Response test() {
      return Response.ok().build();
    }
  }
}
