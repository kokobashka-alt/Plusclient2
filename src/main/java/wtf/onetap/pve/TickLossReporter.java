package wtf.onetap.pve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import wtf.onetap.platform.Diagnostics;
import wtf.onetap.util.LatencyStats;

public class TickLossReporter {
   public static final TickLossReporter TICK_LOSS_REPORTER = new TickLossReporter("pve");
   public static final TickLossReporter TICK_LOSS_REPORTER2 = new TickLossReporter("combat");
   private final String text;
   private final List<Double> doubleList;
   private final List<Double> doubleList2;
   private final List<Double> doubleList3;
   private int value;
   private final List<Double> doubleList4;
   private final List<Double> doubleList5;
   private final List<Double> doubleList6;
   private final List<Double> doubleList7;
   private final List<Double> doubleList8;
   private long longValue;
   private int value2;
   private int value3;
   private int value4;
   private long longValue2;
   private int value5;

   private TickLossReporter(String text2) {
      this.doubleList = new ArrayList<>();
      this.doubleList2 = new ArrayList<>();
      this.doubleList3 = new ArrayList<>();
      this.doubleList4 = new ArrayList<>();
      this.doubleList5 = new ArrayList<>();
      this.doubleList6 = new ArrayList<>();
      this.doubleList7 = new ArrayList<>();
      this.doubleList8 = new ArrayList<>();
      this.longValue = System.currentTimeMillis();
      this.text = text2;
   }

   public synchronized void m_7_void() {
      this.value2++;
   }

   public synchronized void m_3_void() {
      this.value3++;
   }

   public synchronized void addSample(double doubleValue) {
      this.doubleList.add(doubleValue);
   }

   public synchronized void m_5_void(double doubleValue) {
      this.doubleList2.add(doubleValue);
   }

   public synchronized void m_6_void(double doubleValue) {
      this.doubleList3.add(doubleValue);
   }

   public synchronized void m_2(double doubleValue, double doubleValue2, int value6) {
      this.doubleList4.add(doubleValue);
      this.doubleList5.add(doubleValue2);
      this.doubleList6.add((double)value6);
   }

   public synchronized void m_4(int value6, double doubleValue) {
      this.doubleList7.add((double)value6);
      this.doubleList8.add(doubleValue);
   }

   public synchronized void m_8_void() {
      this.value++;
   }

   public synchronized void m_2_void(String text2) {
      this.value4++;
      this.value5++;
      long longValue3 = System.currentTimeMillis();
      if (longValue3 - this.longValue2 >= 10000L) {
         Diagnostics.warn("[ston] {}: {} tick(s) lost ({})", this.text, this.value5, text2);
         this.longValue2 = longValue3;
         this.value5 = 0;
      }
   }

   public synchronized void m_0_void() {
      long longValue3 = System.currentTimeMillis();
      if (longValue3 - this.longValue >= 60000L) {
         if (this.value2 > 0) {
            ArrayList list = new ArrayList<>(this.doubleList);
            Collections.sort(list);
            if (list.isEmpty()) {
               Diagnostics.warn("[ston] {}: no answers in {} s, {} sent, {} lost", this.text, (longValue3 - this.longValue) / 1000L, this.value2, this.value4);
            } else {
               double doubleValue = 0.0;

               for (double doubleValue2 : (Iterable<Double>)list) {
                  doubleValue += doubleValue2;
               }

               double doubleValue3 = doubleValue / list.size();
               double doubleValue4 = (Double)list.get(list.size() / 2);
               double doubleValue5 = 0.0;

               for (double doubleValue6 : (Iterable<Double>)list) {
                  doubleValue5 += Math.abs(doubleValue6 - doubleValue4);
               }

               doubleValue5 /= list.size();
               double doubleValue7 = (Double)list.get(Math.min(list.size() - 1, (int)Math.ceil(list.size() * 0.95) - 1));
               double doubleValue8 = (Double)list.get(list.size() - 1);
               Diagnostics.info(
                  "[ston] {}: rtt {}/{}/{} ms (avg/p95/max), jitter {} ms; {} sent, {} late, {} lost{}",
                  this.text,
                  Math.round(doubleValue3),
                  Math.round(doubleValue7),
                  Math.round(doubleValue8),
                  Math.round(doubleValue5),
                  this.value2,
                  this.value3,
                  this.value4,
                  this.m_5_String()
               );
            }
         }

         this.doubleList.clear();
         this.doubleList2.clear();
         this.doubleList3.clear();
         this.value = 0;
         this.doubleList4.clear();
         this.doubleList5.clear();
         this.doubleList6.clear();
         this.doubleList7.clear();
         this.doubleList8.clear();
         this.value2 = 0;
         this.value3 = 0;
         this.value4 = 0;
         this.longValue = longValue3;
      }
   }

   private String m_5_String() {
      LatencyStats.StateHolder.m_6_C3_sArr();
      StringBuilder stringBuilder = new StringBuilder();
      if (!this.doubleList2.isEmpty()) {
         ArrayList list = new ArrayList<>(this.doubleList2);
         Collections.sort(list);
         long longValue3 = list.stream().filter(object -> (java.lang.Double)object < 0.0).count();
         stringBuilder.append(
            String.format(
               "; before the tick %d/%d ms (p50/p10), %d%% after it began",
               Math.round(percentile(list, 0.5)),
               Math.round(percentile(list, 0.1)),
               Math.round(100.0 * longValue3 / list.size())
            )
         );
      }

      if (!this.doubleList3.isEmpty()) {
         ArrayList list2 = new ArrayList<>(this.doubleList3);
         Collections.sort(list2);
         stringBuilder.append(String.format("; server %d/%d ms (p50/p95)", Math.round(percentile(list2, 0.5)), Math.round(percentile(list2, 0.95))));
      }

      if (this.value > 0) {
         stringBuilder.append("; ").append(this.value).append(" shed");
      }

      if (!this.doubleList4.isEmpty()) {
         stringBuilder.append(
            String.format(
               Locale.ROOT,
               "; built %.1f/%.1f ms (p50/p95), CrystalAura %.1f/%.1f",
               percentile(sorted(this.doubleList4), 0.5),
               percentile(sorted(this.doubleList4), 0.95),
               percentile(sorted(this.doubleList5), 0.5),
               percentile(sorted(this.doubleList5), 0.95)
            )
         );
         stringBuilder.append(String.format(Locale.ROOT, "; request %.1f/%.1f KB", percentile(sorted(this.doubleList6), 0.5) / 1024.0, percentile(sorted(this.doubleList6), 0.95) / 1024.0));
      }

      if (!this.doubleList7.isEmpty()) {
         stringBuilder.append(
            String.format(
               Locale.ROOT,
               ", answer %.1f/%.1f KB; network and link %d/%d ms (p50/p95)",
               percentile(sorted(this.doubleList7), 0.5) / 1024.0,
               percentile(sorted(this.doubleList7), 0.95) / 1024.0,
               Math.round(percentile(sorted(this.doubleList8), 0.5)),
               Math.round(percentile(sorted(this.doubleList8), 0.95))
            )
         );
      }

      return stringBuilder.toString();
   }

   private static List<Double> sorted(List<Double> doubleList9) {
      ArrayList list = new ArrayList(doubleList9);
      Collections.sort(list);
      return list;
   }

   private static double percentile(List<Double> doubleList9, double doubleValue) {
      return (Double)doubleList9.get((int)Math.floor((doubleList9.size() - 1) * doubleValue));
   }
}
