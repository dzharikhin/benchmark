package jmh.test;

import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
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

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.joining;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class PrintStackTraceVsStackWalker {

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(PrintStackTraceVsStackWalker.class.getSimpleName())
        .forks(1)
        .build();
    new Runner(opt).run();
  }
  private static final Set<String> DEFAULT_PACKAGES_TO_SKIP = Set.of("org.jrx", "org.test", "org.springframework");
  private PrintingStackStackedCall printingStackStackedCall = new PrintingStackStackedCall();
  private StackWalkerStackedCall stackWalkerStackedCall = new StackWalkerStackedCall();
  private StringBasedStackWalkerStackedCall stringBasedstackWalkerStackedCall = new StringBasedStackWalkerStackedCall();

  @Benchmark
  public void benchmarkPrintException(Blackhole bh) {
    printingStackStackedCall.method(bh);
  }

  @Benchmark
  public void benchmarkStackWalking(Blackhole bh) {
    stackWalkerStackedCall.method(bh);
  }

  @Benchmark
  public void benchmarkStringBasedStackWalking(Blackhole bh) {
    stringBasedstackWalkerStackedCall.method(bh);
  }

  private static final class PrintingStackStackedCall extends StackedCall {
    @Override
    void printStack(Blackhole bh) {
      ThrowableProxy throwableProxy = new ThrowableProxy(new Exception());
      bh.consume(ThrowableProxyUtil.asString(throwableProxy));
    }
  }

  private static final class StringBasedStackWalkerStackedCall extends StackedCall {
    private static final StackWalker STACK_WALKER = StackWalker.getInstance();
    @Override
    void printStack(Blackhole bh) {
      bh.consume(
        STACK_WALKER.walk(stackStream -> stackStream
          .filter(frame -> DEFAULT_PACKAGES_TO_SKIP.stream().noneMatch(packageToSkip -> frame.getClassName().startsWith(packageToSkip)))
          .limit(10)
          .map(StackWalker.StackFrame::toStackTraceElement)
          .map(StackTraceElement::toString).collect(joining(System.lineSeparator())))
      );
    }
  }

  private static final class StackWalkerStackedCall extends StackedCall {
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    @Override
    void printStack(Blackhole bh) {
      bh.consume(
        STACK_WALKER.walk(stackStream -> stackStream
          .filter(frame -> DEFAULT_PACKAGES_TO_SKIP.stream().noneMatch(packageToSkip -> frame.getDeclaringClass().getPackageName().startsWith(packageToSkip)))
          .limit(10)
          .map(StackWalker.StackFrame::toStackTraceElement)
          .map(StackTraceElement::toString).collect(joining(System.lineSeparator())))
      );
    }
  }

  abstract static class StackedCall {

    abstract void printStack(Blackhole bh);

    public void method(Blackhole bh) {
      method2(bh);
    }
    public void method2(Blackhole bh) {
      method3(bh);
    }
    public void method3(Blackhole bh) {
      method4(bh);
    }
    public void method4(Blackhole bh) {
      method5(bh);
    }
    public void method5(Blackhole bh) {
      method6(bh);
    }
    public void method6(Blackhole bh) {
      method7(bh);
    }
    public void method7(Blackhole bh) {
      method8(bh);
    }
    public void method8(Blackhole bh) {
      method9(bh);
    }
    public void method9(Blackhole bh) {
      method10(bh);
    }
    public void method10(Blackhole bh) {
      method11(bh);
    }
    public void method11(Blackhole bh) {
      method12(bh);
    }
    public void method12(Blackhole bh) {
      method13(bh);
    }
    public void method13(Blackhole bh) {
      method14(bh);
    }
    public void method14(Blackhole bh) {
      method15(bh);
    }
    public void method15(Blackhole bh) {
      method16(bh);
    }
    public void method16(Blackhole bh) {
      method17(bh);
    }
    public void method17(Blackhole bh) {
      method18(bh);
    }
    public void method18(Blackhole bh) {
      method19(bh);
    }
    public void method19(Blackhole bh) {
      method20(bh);
    }
    public void method20(Blackhole bh) {
      method21(bh);
    }
    public void method21(Blackhole bh) {
      method22(bh);
    }
    public void method22(Blackhole bh) {
      method23(bh);
    }
    public void method23(Blackhole bh) {
      method24(bh);
    }
    public void method24(Blackhole bh) {
      method25(bh);
    }
    public void method25(Blackhole bh) {
      method26(bh);
    }
    public void method26(Blackhole bh) {
      method27(bh);
    }
    public void method27(Blackhole bh) {
      method28(bh);
    }
    public void method28(Blackhole bh) {
      method29(bh);
    }
    public void method29(Blackhole bh) {
      method30(bh);
    }
    public void method30(Blackhole bh) {
      method31(bh);
    }
    public void method31(Blackhole bh) {
      method32(bh);
    }
    public void method32(Blackhole bh) {
      method33(bh);
    }
    public void method33(Blackhole bh) {
      method34(bh);
    }
    public void method34(Blackhole bh) {
      method35(bh);
    }
    public void method35(Blackhole bh) {
      method36(bh);
    }
    public void method36(Blackhole bh) {
      method37(bh);
    }
    public void method37(Blackhole bh) {
      method38(bh);
    }
    public void method38(Blackhole bh) {
      method39(bh);
    }
    public void method39(Blackhole bh) {
      method40(bh);
    }
    public void method40(Blackhole bh) {
      method41(bh);
    }
    public void method41(Blackhole bh) {
      method42(bh);
    }
    public void method42(Blackhole bh) {
      method43(bh);
    }
    public void method43(Blackhole bh) {
      method44(bh);
    }
    public void method44(Blackhole bh) {
      method45(bh);
    }
    public void method45(Blackhole bh) {
      method46(bh);
    }
    public void method46(Blackhole bh) {
      method47(bh);
    }
    public void method47(Blackhole bh) {
      method48(bh);
    }
    public void method48(Blackhole bh) {
      method49(bh);
    }
    public void method49(Blackhole bh) {
      method50(bh);
    }
    public void method50(Blackhole bh) {
      printStack(bh);
    }
  }

//  public static void main(String[] args) {
//    for (int i = 1; i <= 50; i++) {
//      System.out.println("    public void method" + i + "(Blackhole bh) {\n      method" + (i + 1) + "(bh);\n    }");
//    }
//  }
}
