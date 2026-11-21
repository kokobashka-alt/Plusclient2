package wtf.onetap.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import wtf.onetap.Onetap;

public class TickExecutor {
   private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(Thread.ofPlatform().name("OnetapTick").daemon().factory());
   private static final LatencyStats.StateHolder C0 = LatencyStats.getOrCreate("tick async wait");
   private static CompletableFuture<Void> completableFuture;

   private TickExecutor() {
   }

   public static void m_4(Runnable runnable) {
      ChatUtil.m_3_IArr();
      CompletableFuture completableFuture2 = completableFuture;
      completableFuture = completableFuture2 == null ? CompletableFuture.runAsync(runnable, EXECUTOR_SERVICE) : completableFuture2.thenRunAsync(runnable, EXECUTOR_SERVICE);
   }

   public static boolean m_0_boolean() {
      CompletableFuture completableFuture2 = completableFuture;
      ChatUtil.m_3_IArr();
      completableFuture = null;
      if (completableFuture2 == null) {
         return false;
      }

      long longValue = System.nanoTime();

      try {
         completableFuture2.join();
         return true;
      } catch (CompletionException completionException) {
         Throwable e = completionException.getCause() != null ? completionException.getCause() : completionException;
         Onetap.LOGGER.error("tick work failed", e);
         return false;
      } finally {
         C0.recordSince(longValue);
      }
   }
}
