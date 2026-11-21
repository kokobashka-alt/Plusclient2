package wtf.onetap.platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import ston.base.client.link.StonLink;
import ston.common.ErrorCode;
import ston.common.StonException;
import wtf.onetap.Onetap;
import wtf.onetap.module.Module;
import wtf.onetap.util.LatencyStats;

public class Diagnostics {
   private static final ArrayDeque<Diagnostics.DataRecord> ARRAY_DEQUE = new ArrayDeque<>();
   private static long longValue;
   private static long longValue2 = System.nanoTime();
   private static long longValue3;
   private static volatile boolean flag;
   private static final LatencyStats.StateHolder C0 = LatencyStats.getOrCreate("gc pause");
   private static long longValue4 = -1L;
   private static Module[] moduleArray;

   private Diagnostics() {
   }

   public static void info(String text, Object... objectArray) {
      log("i", text, objectArray);
   }

   public static void warn(String text, Object... objectArray) {
      log("w", text, objectArray);
   }

   public static void m_3(String text, Object... objectArray) {
      log("i", text, objectArray);
   }

   private static void log(String text, String text2, Object... objectArray) {
      FormattingTuple formattingTuple = MessageFormatter.arrayFormat(text2, objectArray);
      String text3 = formattingTuple.getMessage();
      Throwable e = formattingTuple.getThrowable();
      if (e != null) {
         text3 = text3 + ": " + e;
      }

      if (text3.length() > 2000) {
         text3 = text3.substring(0, 2000);
      }

      synchronized (ARRAY_DEQUE) {
         if (ARRAY_DEQUE.size() >= 500) {
            ARRAY_DEQUE.pollFirst();
            longValue++;
         }

         ARRAY_DEQUE.addLast(new Diagnostics.DataRecord(System.nanoTime(), text, text3));
      }
   }

   public static void m_7(StonLink stonLink) {
      ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(runnable -> {
         Thread thread = new Thread(runnable, "ston-diagnostics");
         thread.setDaemon(true);
         return thread;
      });
      scheduledExecutorService.scheduleWithFixedDelay(() -> {
         try {
            reportGc();
            m_1(stonLink);
         } catch (Throwable e) {
            Onetap.LOGGER.warn("[ston] diagnostics report failed", e);
         }
      }, 5000L, 5000L, TimeUnit.MILLISECONDS);
      scheduledExecutorService.scheduleWithFixedDelay(() -> {
         try {
            reportGc();
         } catch (Throwable e2) {
         }
      }, 250L, 250L, TimeUnit.MILLISECONDS);
   }

   private static void reportGc() {
      long longValue5 = 0L;

      for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
         long longValue6 = garbageCollectorMXBean.getCollectionTime();
         if (longValue6 > 0L) {
            longValue5 += longValue6;
         }
      }

      long longValue7 = longValue4;
      longValue4 = longValue5;
      if (longValue7 >= 0L && longValue5 > longValue7) {
         C0.m_4_void((longValue5 - longValue7) * 1000000L);
      }
   }

   private static void m_1(StonLink stonLink) {
      if (!flag && stonLink.isOnline() && System.currentTimeMillis() >= longValue3) {
         long longValue5 = System.nanoTime();
         JsonObject jsonObject = new JsonObject();
         if (longValue5 - longValue2 >= 60000000000L) {
            JsonArray jsonArray = LatencyStats.m_7_JsonArray();
            if (!jsonArray.isEmpty()) {
               JsonObject jsonObject2 = new JsonObject();
               jsonObject2.addProperty("ms", (longValue5 - longValue2) / 1000000L);
               jsonObject2.add("s", jsonArray);
               jsonObject.add("perf", jsonObject2);
            }

            longValue2 = longValue5;
         }

         JsonArray jsonArray2 = new JsonArray();
         int value;
         synchronized (ARRAY_DEQUE) {
            Diagnostics.DataRecord c5;
            while (jsonArray2.size() < 200 && (c5 = ARRAY_DEQUE.pollFirst()) != null) {
               JsonObject jsonObject3 = new JsonObject();
               jsonObject3.addProperty("ago", (longValue5 - c5.f_3_v) / 1000000L);
               jsonObject3.addProperty("l", c5.f_1_y);
               jsonObject3.addProperty("m", c5.f_1_q);
               jsonArray2.add(jsonObject3);
            }

            value = jsonArray2.size();
            if (longValue > 0L) {
               jsonObject.addProperty("lost", longValue);
            }

            longValue = 0L;
         }

         if (value != 0 || jsonObject.has("perf") || jsonObject.has("lost")) {
            if (value > 0) {
               jsonObject.add("lines", jsonArray2);
            }

            flag = true;
            stonLink.call("diag.report", jsonObject.toString(), 10000L).whenComplete((text, e) -> {
               flag = false;
               if (e != null) {
                  if ((e.getCause() != null ? e.getCause() : e) instanceof StonException stonException && stonException.getCode() == ErrorCode.NOT_FOUND) {
                     longValue3 = System.currentTimeMillis() + 600000L;
                  }

                  synchronized (ARRAY_DEQUE) {
                     longValue += value;
                  }
               }
            });
         }
      }
   }

   static {
      m_3(null);
   }

   public static void m_3(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_2_C3_sArr() {
      return moduleArray;
   }

   private record DataRecord(long f_3_v, String f_1_y, String f_1_q) {
      public long m_3_v() {
         return this.f_3_v;
      }

      public String m_1_y_String() {
         return this.f_1_y;
      }

      public String m_1_q_String() {
         return this.f_1_q;
      }
   }
}
