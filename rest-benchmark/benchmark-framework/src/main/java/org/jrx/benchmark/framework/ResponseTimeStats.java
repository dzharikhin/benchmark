package org.jrx.benchmark.framework;

import org.apache.commons.math3.random.EmpiricalDistribution;

public class ResponseTimeStats {

  private final EmpiricalDistribution distribution;

  public static ResponseTimeStats of(double[] responses) {
    return new ResponseTimeStats(responses);
  }

  protected ResponseTimeStats(double[] responses) {
    this.distribution = new EmpiricalDistribution(responses.length);
    distribution.load(responses);
  }

  public long getCount() {
    return distribution.getSampleStats().getN();
  }

  EmpiricalDistribution getDistribution() {
    return distribution;
  }
}
