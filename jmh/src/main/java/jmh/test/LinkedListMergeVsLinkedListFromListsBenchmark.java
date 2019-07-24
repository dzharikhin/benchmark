package jmh.test;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class LinkedListMergeVsLinkedListFromListsBenchmark {

  @Benchmark
  public void benchmarkToCollectionLinkedListNew(Blackhole bh) {
    LinkedList<Integer> result = IntStream.range(0, 5000).boxed().collect(Collectors.toCollection(LinkedList::new));
    bh.consume(result);
  }

  @Benchmark
  public void benchmarkAndThenLinkedListNewOfToList(Blackhole bh) {
    LinkedList<Integer> result = IntStream.range(0, 5000).boxed().collect(collectingAndThen(toList(), LinkedList::new));
    bh.consume(result);
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(LinkedListMergeVsLinkedListFromListsBenchmark.class.getSimpleName())
        .forks(1)
        .build();
    new Runner(opt).run();
  }
}
