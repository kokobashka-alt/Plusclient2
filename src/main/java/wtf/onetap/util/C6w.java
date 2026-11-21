package wtf.onetap.util;

import java.util.function.IntSupplier;
import net.minecraft.util.Mth;
import wtf.onetap.animation.Interpolator;

public class C6w {
   private final Interpolator interpolator;
   private final Interpolator interpolator2;
   private final IntSupplier intSupplier;
   private final IntSupplier intSupplier2;
   private final IntSupplier intSupplier3;
   private double doubleValue;
   private double doubleValue2;
   private boolean flag;
   private double doubleValue3;
   private double doubleValue4;
   private int value;
   private boolean flag2;
   private double doubleValue5;

   public C6w(Interpolator interpolator3, Interpolator interpolator4, IntSupplier intSupplier4, IntSupplier intSupplier5, IntSupplier intSupplier6) {
      this.interpolator = interpolator3;
      this.interpolator2 = interpolator4;
      this.intSupplier = intSupplier4;
      this.intSupplier2 = intSupplier5;
      this.intSupplier3 = intSupplier6;
   }

   public void m_7(boolean flag3, int value2) {
      this.m_9(flag3, 0.05);
      this.m_2(value2, 0.05);
   }

   private void m_9(boolean flag3, double doubleValue6) {
      if (flag3 && !this.flag) {
         this.flag2 = false;
      }

      double doubleValue7 = flag3 ? 1.0 : 0.0;
      this.doubleValue = this.doubleValue2;
      this.doubleValue2 = this.interpolator.interpolate(doubleValue7, this.doubleValue2, doubleValue6);
      this.doubleValue = this.interpolator.m_9_double(this.doubleValue);
      if (!this.interpolator.isActive()) {
         this.doubleValue = this.doubleValue2;
      }

      this.flag = flag3;
   }

   private void m_2(int value2, double doubleValue6) {
      if (value2 > this.value) {
         this.flag2 = false;
      }

      int value3 = this.intSupplier3.getAsInt();
      double doubleValue7 = value3 > 0 ? (double)value2 / value3 : 0.0;
      this.doubleValue3 = this.doubleValue4;
      this.doubleValue4 = this.interpolator2.interpolate(doubleValue7, this.doubleValue4, doubleValue6);
      this.doubleValue3 = this.interpolator2.m_9_double(this.doubleValue3);
      this.value = value2;
   }

   public double m_7_double(float floatValue) {
      Interpolator.StateHolder.m_7_String();
      double doubleValue6 = this.m_3_double(floatValue);
      double doubleValue7 = 1.0 / doubleValue6;
      double doubleValue8 = 0.0;
      if (!this.flag2) {
         doubleValue8 = this.interpolator2.isActive() ? this.interpolator2.m_1_double(Mth.lerp(floatValue, this.doubleValue3, this.doubleValue4)) : this.doubleValue4;
      }

      double doubleValue9 = this.intSupplier2.getAsInt() / 100.0;
      double doubleValue10 = doubleValue8 * this.intSupplier3.getAsInt();
      double doubleValue11 = Mth.clamp(doubleValue7 * Math.pow(doubleValue9, doubleValue10), 0.5, 500.0);
      if (this.doubleValue2 == 0.0 && this.doubleValue4 == 0.0) {
         this.flag2 = false;
      }

      if (!this.flag2) {
         this.doubleValue5 = 1.0 / doubleValue11;
      }

      return doubleValue11;
   }

   private double m_3_double(float floatValue) {
      double doubleValue6 = this.interpolator.isActive() ? this.interpolator.m_1_double(Mth.lerp(floatValue, this.doubleValue, this.doubleValue2)) : this.doubleValue2;
      double doubleValue7 = !this.flag2 ? 1.0 / Math.max(1, this.intSupplier.getAsInt()) : this.doubleValue5;
      return Mth.lerp(doubleValue6, 1.0, doubleValue7);
   }

   public void m_4_void() {
      if (!this.flag2 && this.doubleValue4 > 0.0) {
         this.flag2 = true;
         this.doubleValue4 = 0.0;
         this.doubleValue3 = 0.0;
      }
   }

   public void reset() {
      this.doubleValue2 = 0.0;
      this.doubleValue = 0.0;
      this.flag = false;
      this.doubleValue4 = 0.0;
      this.doubleValue3 = 0.0;
      this.value = 0;
      this.flag2 = false;
   }
}
