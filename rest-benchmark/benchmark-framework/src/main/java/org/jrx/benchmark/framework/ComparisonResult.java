package org.jrx.benchmark.framework;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

public class ComparisonResult {
  public enum ComparisonStatus {
    EQUALS,
    REFERENCE_WORSE,
    TEST_WORSE
  }

  static ComparisonResult aggregate(Collection<ComparisonResult> results) {
    Double averageDiff = results.stream().mapToDouble(ComparisonResult::getAbsoluteDiff).average().orElse(0);
    Double averageBase = results.stream().mapToDouble(ComparisonResult::getBaseValue).average().orElse(0);
    ComparisonStatus averageStatus = Arrays.stream(ComparisonStatus.values())
      .map(status -> toEntry(results, status))
      .max(Comparator.comparingLong(Map.Entry::getValue)).map(Map.Entry::getKey)
      .orElse(ComparisonStatus.EQUALS);
    return new ComparisonResult(averageStatus, averageDiff, averageBase);
  }

  private final ComparisonStatus status;
  private final double absoluteDiff;
  private final double baseValue;

  protected ComparisonResult(ComparisonStatus status, double absoluteDiff, double baseValue) {
    this.status = status;
    this.absoluteDiff = absoluteDiff;
    this.baseValue = baseValue;
  }

  public ComparisonStatus getStatus() {
    return status;
  }

  public double getBaseValue() {
    return baseValue;
  }

  public double getAbsoluteDiff() {
    return absoluteDiff;
  }

  public double getWorseOnPercent() {
    return absoluteDiff/baseValue;
  }

  public boolean is(ComparisonStatus status) {
    return this.status == status;
  }

  private static SimpleEntry<ComparisonStatus, Long> toEntry(Collection<ComparisonResult> results, ComparisonStatus status) {
    return new SimpleEntry<>(status, results.stream().map(ComparisonResult::getStatus).filter(resultStatus -> status == resultStatus).count());
  }
}
