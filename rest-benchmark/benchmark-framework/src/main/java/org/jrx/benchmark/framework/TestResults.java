package org.jrx.benchmark.framework;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.jrx.benchmark.framework.ComparisonResult.ComparisonStatus.TEST_WORSE;
import static org.junit.Assert.fail;

public class TestResults {
  private final String testWorkloadName;
  private final String referenceWorkloadName;
  private final Map<Metric, ComparisonResult> comparisonResults;

  public static <T extends Workload> TestResults executeBenchmark(
      BiFunction<T, Long, ResponseTimeStats> executor,
      T testWorkload,
      T referenceWorkload,
      Collection<Metric> metrics,
      int warmup,
      int repetitions,
      long iterations
  ) {
    print("Starting warmup");
    IntStream.range(0, warmup).forEach(i -> {
      executor.apply(testWorkload, iterations);
      executor.apply(referenceWorkload, iterations);
    });
    print("Warmup done. Starting to collect stats");
    Map<Metric, ComparisonResult> comparisonResultMap = IntStream.range(0, repetitions).mapToObj(i -> {
      ResponseTimeStats testStats = executor.apply(testWorkload, iterations);
      print(format("Processed test workload, iteration: {0,number,integer}", i));
      ResponseTimeStats referenceStats = executor.apply(referenceWorkload, iterations);
      print(format("Processed reference workload, iteration: {0,number,integer}", i));
      return metrics.stream().map(metric -> new AbstractMap.SimpleImmutableEntry<>(metric, metric.compareStats(testStats, referenceStats)));
    }).flatMap(Function.identity()).collect(groupingBy(
      Map.Entry::getKey,
      collectingAndThen(mapping(Map.Entry::getValue, toList()), ComparisonResult::aggregate)
    ));
    return new TestResults(testWorkload.getName(), referenceWorkload.getName(), comparisonResultMap);
  }

  protected TestResults(String testWorkloadName, String referenceWorkloadName, Map<Metric, ComparisonResult> comparisonResults) {
    this.testWorkloadName = testWorkloadName;
    this.referenceWorkloadName = referenceWorkloadName;
    this.comparisonResults = new EnumMap<>(comparisonResults);
  }

  public void assertResults(Collection<Metric> criticalMetrics, double threshold) {
    for (Metric metric : criticalMetrics) {
      ComparisonResult comparisonResult = comparisonResults.get(metric);
      if (comparisonResult != null && comparisonResult.is(TEST_WORSE) && comparisonResult.getWorseOnPercent() > threshold) {
        fail(format("{0} in {1} is too bad against {2}: {3,number,percent} worse",
          metric.getHumanReadableValue(), testWorkloadName, referenceWorkloadName, comparisonResult.getWorseOnPercent())
        );
      }
    }
    comparisonResults.entrySet().forEach(e -> {
      Metric metric = e.getKey();
      ComparisonResult result = e.getValue();
      print(format("{0} in {1} is ok against {2}: {3} on {4,number,percent}",
        metric.getHumanReadableValue(), testWorkloadName, referenceWorkloadName, result.getStatus(), result.getWorseOnPercent()
      ));
    });
  }

  public ComparisonResult getResult(Metric metric) {
    return comparisonResults.get(metric);
  }

  public String getReferenceWorkloadName() {
    return referenceWorkloadName;
  }

  public String getTestWorkloadName() {
    return testWorkloadName;
  }

  private static void print(String value) {
    System.out.println(value);
  }
}
