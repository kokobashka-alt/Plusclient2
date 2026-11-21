package wtf.onetap.setting;

import java.util.function.BooleanSupplier;

public class FloatSetting extends Setting<Float> {
   private final float min;
   private final float max;
   private final float step;
   private static int[] valueArray;

   public FloatSetting(String text, float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      super(text, clamp(floatValue, floatValue2, floatValue3));
      this.min = floatValue2;
      this.max = floatValue3;
      this.step = floatValue4;
   }

   public FloatSetting(String text, double doubleValue, double doubleValue2, double doubleValue3, double doubleValue4) {
      this(text, (float)doubleValue, (float)doubleValue2, (float)doubleValue3, (float)doubleValue4);
   }

   public FloatSetting build() {
      SettingRegistry.register(this);
      return this;
   }

   public FloatSetting visibleWhen(BooleanSupplier booleanSupplier) {
      super.visibleWhen(booleanSupplier);
      return this;
   }

   public void setValue(Float floatObj) {
      super.setValue(clamp(floatObj, this.min, this.max));
   }

   public float getFloat() {
      return (Float)super.value;
   }

   public int getInt() {
      return ((Float)super.value).intValue();
   }

   public double getDouble() {
      return ((Float)super.value).doubleValue();
   }

   public float getMin() {
      return this.min;
   }

   public float getMax() {
      return this.max;
   }

   public float getStep() {
      return this.step;
   }

   private static float clamp(float floatValue, float floatValue2, float floatValue3) {
      return Math.max(floatValue2, Math.min(floatValue3, floatValue));
   }

   public static void m_3(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_4_IArr() {
      return valueArray;
   }
}
