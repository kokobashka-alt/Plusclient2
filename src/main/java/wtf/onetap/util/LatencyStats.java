package wtf.onetap.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import wtf.onetap.module.Module;

public class LatencyStats {
   private static final Map<String, LatencyStats.StateHolder> STRING_MAP = new ConcurrentHashMap<>();
   private static String text;

   private LatencyStats() {
   }

   public static LatencyStats.StateHolder getOrCreate(String text2) {
      return STRING_MAP.computeIfAbsent(text2, LatencyStats.StateHolder::new);
   }

   public static JsonArray m_7_JsonArray() {
      LatencyStats.StateHolder.m_6_C3_sArr();
      JsonArray jsonArray = new JsonArray();

      for (LatencyStats.StateHolder c0 : STRING_MAP.values()) {
         JsonObject jsonObject = c0.m_8_JsonObject();
         if (jsonObject != null) {
            jsonArray.add(jsonObject);
         }
      }

      return jsonArray;
   }

   static {
      m_5_void(null);
   }

   public static void m_5_void(String text2) {
      text = text2;
   }

   public static String m_3_String() {
      return text;
   }

   public static final class StateHolder {
      private final String text;
      private final int[] valueArray;
      private long longValue;
      private long longValue2;
      private long longValue3;
      private static Module[] moduleArray;

      private StateHolder(String text2) {
         this.valueArray = new int[256];
         this.text = text2;
      }

      public synchronized void m_4_void(long longValue4) {
         long longValue5 = Math.max(longValue4, 0L);
         this.longValue++;
         this.longValue2 += longValue5;
         if (longValue5 > this.longValue3) {
            this.longValue3 = longValue5;
         }

         this.valueArray[m_4_int(longValue5)]++;
      }

      public void recordSince(long longValue4) {
         this.m_4_void(System.nanoTime() - longValue4);
      }

      private synchronized JsonObject m_8_JsonObject() {
         if (this.longValue == 0L) {
            return null;
         }

         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("n", this.text);
         jsonObject.addProperty("c", this.longValue);
         jsonObject.addProperty("s", roundMs(this.longValue2));
         jsonObject.addProperty("p50", roundMs(this.m_7_double(0.5)));
         jsonObject.addProperty("p95", roundMs(this.m_7_double(0.95)));
         jsonObject.addProperty("max", roundMs(this.longValue3));
         this.longValue = 0L;
         this.longValue2 = 0L;
         this.longValue3 = 0L;
         Arrays.fill(this.valueArray, 0);
         return jsonObject;
      }

      private double m_7_double(double doubleValue) {
         long longValue4 = Math.min(Math.max((long)(doubleValue * this.longValue), 1L), this.longValue);
         m_6_C3_sArr();
         long longValue5 = 0L;

         for (int i = 0; i < 256; i++) {
            longValue5 += this.valueArray[i];
            if (longValue5 >= longValue4) {
               return Math.min(m_8_double(i), this.longValue3);
            }
         }

         return this.longValue3;
      }

      private static int m_4_int(long longValue4) {
         if (longValue4 < 4L) {
            return (int)longValue4;
         }

         int value = 63 - Long.numberOfLeadingZeros(longValue4);
         int value2 = (int)(longValue4 >>> value - 2 & 3L);
         return value * 4 + value2;
      }

      private static double m_8_double(int value) {
         if (value < 4) {
            return value;
         }

         int value2 = value / 4;
         int value3 = value % 4;
         long longValue4 = (1L << value2) + value3 * (1L << value2 - 2);
         return longValue4 + (1L << value2 - 2) / 2.0;
      }

      private static double roundMs(double doubleValue) {
         return Math.round(doubleValue / 1000.0) / 1000.0;
      }

      public static void m_2(Module[] moduleArray2) {
         moduleArray = moduleArray2;
      }

      public static Module[] m_6_C3_sArr() {
         return moduleArray;
      }

      static {
         m_2(null);
      }
   }
}
