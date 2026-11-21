package wtf.onetap.animation;

import java.util.function.Supplier;
import wtf.onetap.util.C5_w;

public interface Interpolator {
   boolean isActive();

   double interpolate(double doubleValue, double doubleValue2, double doubleValue3);

   default double m_1_double(double doubleValue) {
      return doubleValue;
   }

   default double m_9_double(double doubleValue) {
      return doubleValue;
   }

   abstract class StateHolder implements Interpolator {
      public boolean flag;
      private static String text;

      public StateHolder() {
         this.flag = true;
      }

      public abstract double m_9(boolean flag2, double doubleValue, double doubleValue2, double doubleValue3);

      @Override
      public boolean isActive() {
         return true;
      }

      @Override
      public double interpolate(double doubleValue, double doubleValue2, double doubleValue3) {
         if (doubleValue > doubleValue2) {
            this.flag = true;
            return Math.min(doubleValue2 + this.m_9(false, doubleValue3, doubleValue, doubleValue2), doubleValue);
         } else if (doubleValue < doubleValue2) {
            this.flag = false;
            return Math.max(doubleValue2 - this.m_9(true, doubleValue3, doubleValue, doubleValue2), doubleValue);
         } else {
            this.flag = true;
            return doubleValue;
         }
      }

      public static void m_5_void(String text2) {
         text = text2;
      }

      public static String m_7_String() {
         return text;
      }
   }

   class StateHolder2 extends Interpolator.StateHolder {
      private final Supplier<Double> supplier;
      private final Supplier<Double> supplier2;

      public StateHolder2(Supplier<Double> supplier3, Supplier<Double> supplier4) {
         this.supplier = supplier3;
         this.supplier2 = supplier4;
      }

      @Override
      public double m_9(boolean flag2, double doubleValue, double doubleValue2, double doubleValue3) {
         double doubleValue4 = flag2 ? this.supplier2.get() : this.supplier.get();
         return doubleValue4 <= 0.0 ? Double.POSITIVE_INFINITY : doubleValue / doubleValue4;
      }

      @Override
      public boolean isActive() {
         return super.flag && this.supplier.get() > 0.0 || !super.flag && this.supplier2.get() > 0.0;
      }
   }

   class StateHolder3 extends Interpolator.StateHolder {
      private final Supplier<Double> supplier;

      public StateHolder3(Supplier<Double> supplier2) {
         this.supplier = supplier2;
      }

      @Override
      public double m_9(boolean flag2, double doubleValue, double doubleValue2, double doubleValue3) {
         double doubleValue4 = flag2 ? doubleValue3 - doubleValue2 : doubleValue2 - doubleValue3;
         return doubleValue4 * this.supplier.get() / 0.05 * doubleValue;
      }

      @Override
      public boolean isActive() {
         return this.supplier.get() != 1.0;
      }

      @Override
      public double interpolate(double doubleValue, double doubleValue2, double doubleValue3) {
         return !this.isActive() ? doubleValue : super.interpolate(doubleValue, doubleValue2, doubleValue3);
      }
   }

   class StateHolder4 extends Interpolator.StateHolder2 {
      private final Supplier<C5_w> supplier;
      private final Supplier<C5_w> supplier2;
      private C5_w c5_w = C5_w.LINEAR;
      private C5_w c5_w2 = C5_w.LINEAR;
      private double doubleValue;
      private boolean flag;

      public StateHolder4(Supplier<C5_w> supplier3, Supplier<C5_w> supplier4, Supplier<Double> supplier5, Supplier<Double> supplier6) {
         super(supplier5, supplier6);
         this.supplier = supplier3;
         this.supplier2 = () -> ((C5_w)supplier4.get()).m_8_C5_w();
      }

      @Override
      public boolean isActive() {
         return !this.flag && super.isActive() && this.c5_w != C5_w.INSTANT;
      }

      @Override
      public double m_1_double(double doubleValue2) {
         return this.c5_w.m_1_double(doubleValue2);
      }

      @Override
      public double m_9_double(double doubleValue2) {
         if (!this.flag) {
            return doubleValue2;
         }

         this.flag = false;
         return this.c5_w.m_0_double(this.c5_w2.m_1_double(doubleValue2));
      }

      @Override
      public double interpolate(double doubleValue2, double doubleValue3, double doubleValue4) {
         Interpolator.StateHolder.m_7_String();
         double doubleValue5 = doubleValue3;
         if (doubleValue2 > doubleValue3) {
            this.c5_w = this.supplier.get();
            this.c5_w2 = this.supplier2.get();
            if (this.doubleValue < doubleValue2 && this.c5_w.m_2_boolean()) {
               this.flag = true;
               doubleValue5 = this.c5_w.m_0_double(this.c5_w2.m_1_double(doubleValue3));
            }
         } else if (doubleValue2 < doubleValue3) {
            this.c5_w = this.supplier2.get();
            this.c5_w2 = this.supplier.get();
            if (this.doubleValue > doubleValue2 && this.c5_w.m_2_boolean()) {
               this.flag = true;
               doubleValue5 = this.c5_w.m_0_double(this.c5_w2.m_1_double(doubleValue3));
            }
         }

         this.doubleValue = doubleValue2;
         return this.c5_w == C5_w.INSTANT ? doubleValue2 : super.interpolate(doubleValue2, doubleValue5, doubleValue4);
      }
   }
}
