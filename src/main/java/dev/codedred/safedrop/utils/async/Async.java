package dev.codedred.safedrop.utils.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ForkJoinPool;

public class Async {

  private static final ForkJoinPool worker = new ForkJoinPool(
    8,
    ForkJoinPool.defaultForkJoinWorkerThreadFactory,
    (t, e) -> e.printStackTrace(),
    false
  );

  private Async() {}

  public static CompletableFuture<Void> run(Runnable runnable) {
    return CompletableFuture.runAsync(
      () -> {
        try {
          runnable.run();
        } catch (Exception exception) {
          throw new CompletionException(exception);
        }
      },
      worker
    );
  }
}
