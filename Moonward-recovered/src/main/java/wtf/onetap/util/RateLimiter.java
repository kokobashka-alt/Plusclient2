package wtf.onetap.util;

public class RateLimiter {
   private double doubleValue;
   private long longValue;

   public RateLimiter() {
      this.doubleValue = 5.0;
      this.longValue = System.nanoTime();
   }

   public synchronized boolean m_6_boolean() {
      LatencyStats.StateHolder.m_6_C3_sArr();
      long longValue2 = System.nanoTime();
      this.doubleValue = Math.min(5.0, this.doubleValue + (longValue2 - this.longValue) / 1.0E9 * 22.0);
      this.longValue = longValue2;
      if (this.doubleValue < 1.0) {
         return false;
      }

      this.doubleValue--;
      return true;
   }
}
