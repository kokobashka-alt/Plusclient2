package wtf.onetap.util;

import wtf.onetap.module.Module;

public class TimeHelper {
   private long longValue;
   private static Module[] moduleArray;

   public TimeHelper() {
      this.longValue = System.currentTimeMillis();
   }

   public boolean hasElapsed(long longValue2) {
      return System.currentTimeMillis() - this.longValue >= longValue2;
   }

   public void reset() {
      this.longValue = System.currentTimeMillis();
   }

   public long getElapsed() {
      return System.currentTimeMillis() - this.longValue;
   }

   public static void m_5(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_4_C3_sArr() {
      return moduleArray;
   }
}
