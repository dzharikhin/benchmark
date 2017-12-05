package org.jrx.benchmark.framework;

public enum Metric {
  MIN("Minimum response time", "ns") {
    @Override
    public double calculate(ResponseTimeStats stats) {
      return stats.getDistribution().getSampleStats().getMin();
    }
  },
  MAX("Maximum response time", "ns") {
    @Override
    public double calculate(ResponseTimeStats stats) {
      return stats.getDistribution().getSampleStats().getMax();
    }
  },
  AVERAGE("Average response time", "ns") {
    @Override
    public double calculate(ResponseTimeStats stats) {
      return stats.getDistribution().getSampleStats().getMean();
    }
  },
  MEDIAN("Median response time", "ns") {
    @Override
    public double calculate(ResponseTimeStats stats) {
      return stats.getDistribution().inverseCumulativeProbability(0.5);
    }
  },
  NINTY_FIFTH_PERC("Response time in 95%", "ns") {
    @Override
    public double calculate(ResponseTimeStats stats) {
      return stats.getDistribution().inverseCumulativeProbability(0.95);
    }
  };

  private final String humanReadableValue;
  private final String unit;

  Metric(String humanReadableValue, String unit) {
    this.humanReadableValue = humanReadableValue;
    this.unit = unit;
  }

  public String getHumanReadableValue() {
    return humanReadableValue;
  }

  public String getUnit() {
    return unit;
  }

  public abstract double calculate(ResponseTimeStats stats);

  public ComparisonResult compareStats(ResponseTimeStats testStats, ResponseTimeStats referenceStats) {
    double testValue = calculate(testStats);
    double referenceValue = calculate(referenceStats);
    int compareResult = Double.compare(testValue, referenceValue);
    if (compareResult == 0) {
      return new ComparisonResult(ComparisonResult.ComparisonStatus.EQUALS, 0, testValue);
    } else if (compareResult > 0) {
      return new ComparisonResult(ComparisonResult.ComparisonStatus.TEST_WORSE, testValue - referenceValue, referenceValue);
    } else {
      return new ComparisonResult(ComparisonResult.ComparisonStatus.REFERENCE_WORSE, referenceValue - testValue, testValue);
    }
  }
}
