package ru.hh.test.java;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureTest {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    CompletableFuture<Integer> a = CompletableFuture.supplyAsync(() -> 1);
    CompletableFuture<Integer> b = CompletableFuture.supplyAsync(() -> 2);
    CompletableFuture<Integer> c = new CompletableFuture<>();
    CompletableFuture.allOf(a, b, c).thenAccept(System.out::println).exceptionally(ex -> {
      System.out.println(ex);
      return null;
    });
    c.complete(4);
  }
}
