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

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class MulticatchWithInstanceofVsTwoCatchBenchmark {

  private static final Random RANDOM = new Random();

  @Benchmark
  public void benchmarkTwoCatches(Blackhole bh) {
    try {
      throwRandomException();
    } catch (ExceptionOne e) {
      bh.consume(new Object());
      bh.consume(e);
    } catch (ExceptionTwo e) {
      bh.consume(e);
    }

  }

  @Benchmark
  public void benchmarkMulticatchWithInstanceOf(Blackhole bh) {
    try {
      throwRandomException();
    } catch (ExceptionOne | ExceptionTwo e) {
      if (e instanceof ExceptionOne) {
        bh.consume(new Object());
      }
      bh.consume(e);
    }
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(MulticatchWithInstanceofVsTwoCatchBenchmark.class.getSimpleName())
        .forks(1)
        .build();
    new Runner(opt).run();
  }

  private static class ExceptionOne extends Exception {

  }

  private static class ExceptionTwo extends Exception {

  }

  private static void throwRandomException() throws ExceptionOne, ExceptionTwo {
    if (RANDOM.nextBoolean()) {
      throw new ExceptionOne();
    } else {
      throw new ExceptionTwo();
    }
  }
}
