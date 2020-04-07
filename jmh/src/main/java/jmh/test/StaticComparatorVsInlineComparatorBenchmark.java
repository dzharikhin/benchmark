package jmh.test;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class StaticComparatorVsInlineComparatorBenchmark {

  List<StaticWeight> staticWeights;
  List<Weight> staticGetterWeights;
  List<InlineWeight> inlineWeights;

  @Setup
  public void setUp() {
    int size = 1000;
    staticWeights = IntStream.range(0, size).mapToObj(i -> {
      var w = new StaticWeight();
      w.currentLoad = i;
      w.differentDC = (i % 2 == 0);
      w.sameRack = !w.differentDC;
      w.statLoad = i * 2.0f;
      return w;
    }).collect(toList());
    staticGetterWeights = IntStream.range(0, size).mapToObj(i -> new Weight(i % 2 == 0, i % 2 == 1, i, i * 2.0f)).collect(toList());
    inlineWeights = IntStream.range(0, size).mapToObj(i -> {
      var w = new InlineWeight();
      w.currentLoad = i;
      w.differentDC = (i % 2 == 0);
      w.sameRack = !w.differentDC;
      w.statLoad = i * 2.0f;
      return w;
    }).collect(toList());
  }

  @Benchmark
  public void inlineCompare(Blackhole bh) {
    bh.consume(inlineWeights.stream().min(Comparator.naturalOrder()));
  }

  @Benchmark
  public void staticCompare(Blackhole bh) {
    bh.consume(staticWeights.stream().min(Comparator.naturalOrder()));
  }

  @Benchmark
  public void staticGetterCompare(Blackhole bh) {
    bh.consume(staticWeights.stream().min(Comparator.naturalOrder()));
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(StaticComparatorVsInlineComparatorBenchmark.class.getSimpleName())
        .forks(1)
        .build();
    new Runner(opt).run();
  }

  private static class StaticWeight implements Comparable<StaticWeight> {
    private static final Comparator<StaticWeight> weightComparator = Comparator.comparing((StaticWeight weight) -> weight.differentDC)
        .thenComparing(weight -> weight.sameRack)
        .thenComparingDouble(weight -> weight.currentLoad)
        .thenComparingDouble(weight -> weight.statLoad);
    boolean differentDC;
    boolean sameRack;
    float currentLoad;
    float statLoad;

    @Override
    public int compareTo(StaticWeight other) {
      return weightComparator.compare(this, other);
    }
  }

  private static class Weight implements Comparable<Weight> {
    private static final Comparator<Weight> weightComparator = Comparator.comparing(Weight::isDifferentDC)
        .thenComparing(Weight::isSameRack)
        .thenComparingDouble(Weight::getCurrentLoad)
        .thenComparingDouble(Weight::getStatLoad);
    private final boolean differentDC;
    private final boolean sameRack;
    private final float currentLoad;
    private final float statLoad;

    Weight(boolean differentDC, boolean sameRack, float currentLoad, float statLoad) {
      this.differentDC = differentDC;
      this.sameRack = sameRack;
      this.currentLoad = currentLoad;
      this.statLoad = statLoad;
    }

    public boolean isDifferentDC() {
      return differentDC;
    }

    public boolean isSameRack() {
      return sameRack;
    }

    public float getCurrentLoad() {
      return currentLoad;
    }

    public float getStatLoad() {
      return statLoad;
    }

    @Override
    public int compareTo(Weight other) {
      return weightComparator.compare(this, other);
    }
  }

  private static class InlineWeight implements Comparable<InlineWeight> {
    boolean differentDC;
    boolean sameRack;
    float currentLoad;
    float statLoad;

    @Override
    public int compareTo(InlineWeight other) {
      return Comparator.comparing((InlineWeight weight) -> weight.differentDC)
          .thenComparing(weight -> weight.sameRack)
          .thenComparingDouble(weight -> weight.currentLoad)
          .thenComparingDouble(weight -> weight.statLoad).compare(this, other);
    }
  }
}
