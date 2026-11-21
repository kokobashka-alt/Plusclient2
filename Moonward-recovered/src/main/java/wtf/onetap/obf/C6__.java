package wtf.onetap.obf;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;
import wtf.onetap.module.Module;
import wtf.onetap.util.TimeHelper;

public class C6__ {
   private final TimeHelper f_3;
   private final List<C6__.C2> f_9;
   private int f_6;
   private boolean f_5;
   private final C6__.C9 f_4;
   private static Module[] f_1;

   public C6__() {
      this.f_3 = new TimeHelper();
      this.f_9 = new CopyOnWriteArrayList<>();
      this.f_4 = new C6__.C9(1);
      this.m_5_C6__();
   }

   public C6__ m_3(int value, Runnable runnable) {
      return this.m_5(value, runnable, () -> true, 0);
   }

   public C6__ m_5(int value, Runnable runnable, BooleanSupplier booleanSupplier, int value2) {
      this.f_9.add(new C6__.C2(value, runnable, booleanSupplier, value2));
      Collections.sort(this.f_9);
      return this;
   }

   public C6__ m_5_C6__() {
      this.f_9.clear();
      this.f_3.reset();
      this.f_6 = 0;
      this.f_5 = false;
      this.f_4.m_9_void();
      return this;
   }

   public void m_1_void() {
      if (!this.f_9.isEmpty() && !this.f_5) {
         if (this.f_6 < this.f_9.size()) {
            C6__.C2 c2 = this.f_9.get(this.f_6);
            if (c2.f_4.getAsBoolean() && c2.f_0 <= 0) {
               c2.f_5.run();
               this.f_6++;
               this.f_3.reset();
               if (this.f_4.m_5_boolean(this.f_6, this.f_9.size())) {
                  this.f_6 = 0;
                  this.f_4.m_7_void();
               }
            }

            c2.f_0--;
         }

         this.f_6 = Math.min(this.f_6, this.f_9.size());
      }
   }

   public boolean m_2_boolean() {
      return this.f_6 >= this.f_9.size() && !this.f_5 && this.f_4.m_8_boolean();
   }

   public static void m_2(Module[] moduleArray) {
      f_1 = moduleArray;
   }

   public static Module[] m_4_C3_sArr() {
      return f_1;
   }

   private static final class C2 implements Comparable<C6__.C2> {
      private int f_0;
      private final Runnable f_5;
      private final BooleanSupplier f_4;
      private final int f_7;

      public C2(int value, Runnable runnable, BooleanSupplier booleanSupplier, int value2) {
         this.f_0 = value;
         C6__.m_4_C3_sArr();
         this.f_5 = runnable;
         this.f_4 = booleanSupplier;
         this.f_7 = value2;
      }

      public int compareTo(C6__.C2 c2) {
         return Integer.compare(c2.f_7, this.f_7);
      }
   }

   private static final class C9 {
      private final int f_3;
      private int f_7;

      public C9(int value) {
         this.f_3 = value - 1;
      }

      public boolean m_5_boolean(int value, int value2) {
         return value >= value2 && this.f_7 < this.f_3;
      }

      public void m_7_void() {
         this.f_7++;
      }

      public boolean m_8_boolean() {
         return this.f_7 >= this.f_3;
      }

      public void m_9_void() {
         this.f_7 = 0;
      }
   }
}
