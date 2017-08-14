package ru.hh.test.jersey;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import static java.lang.System.currentTimeMillis;

public class Client {
  private static final HttpHost v1HttpHost = new HttpHost("localhost", 8081, "http");
  private static final HttpRequest v1StringHttpRequest = new HttpGet("/rest/v1/string");
  private static final HttpRequest v1JacksonHttpRequest = new HttpGet("/rest/v1/testGetJackson");
  private static final HttpHost v2HttpHost = new HttpHost("localhost", 8084, "http");
  private static final HttpRequest v2StringHttpRequest = new HttpGet("/rest/resteasy/string");
  private static final HttpRequest v2JacksonHttpRequest = new HttpGet("/rest/resteasy/testGetJackson");

  private static final long TICK = TimeUnit.SECONDS.toMillis(1);

  public static void main(String[] args) throws IOException {

    PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
    poolingHttpClientConnectionManager.setMaxTotal(64);
    poolingHttpClientConnectionManager.setDefaultMaxPerRoute(64);


    try (CloseableHttpClient httpClient = HttpClients.createMinimal(poolingHttpClientConnectionManager)) {
      int testTime = 30;
      System.out.println("Testing v1 string:");
      testResourse(httpClient, testTime, v1HttpHost, v1StringHttpRequest);
      System.out.println();

      System.out.println("Testing v2 string:");
      testResourse(httpClient, testTime, v2HttpHost, v2StringHttpRequest);
      System.out.println();

      System.out.println("Testing v1 jackson:");
      testResourse(httpClient, testTime, v1HttpHost, v1JacksonHttpRequest);
      System.out.println();

      System.out.println("Testing v2 jackson:");
      testResourse(httpClient, testTime, v2HttpHost, v2JacksonHttpRequest);
    }
  }

  private static void testResourse(CloseableHttpClient httpClient, int testTime, HttpHost host, HttpRequest request) throws IOException {
    long start = currentTimeMillis();
    long prevMarkMillis = start;
    long prevRequests = 0;
    long requests = 0;
    long interval = TimeUnit.SECONDS.toMillis(testTime);
    while (prevMarkMillis - start < interval) {
      HttpResponse response = httpClient.execute(host, request);

      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        throw new RuntimeException("non 200 response: " + response.getStatusLine().getStatusCode());
      }
      EntityUtils.consumeQuietly(response.getEntity());

      requests++;
      long currentTimeMillis = currentTimeMillis();
      if (currentTimeMillis - prevMarkMillis >= TICK) {
        System.out.println((requests - prevRequests) + " responses/sec");
        prevMarkMillis = currentTimeMillis;
        prevRequests = requests;
      }
    }
  }
}
