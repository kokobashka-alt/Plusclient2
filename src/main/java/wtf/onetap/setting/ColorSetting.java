package wtf.onetap.setting;

import java.util.function.BooleanSupplier;

public class ColorSetting extends Setting<Integer> {
   private static boolean flag;

   public ColorSetting(String text, int value2) {
      super(text, value2);
   }

   public ColorSetting build() {
      SettingRegistry.register(this);
      return this;
   }

   public ColorSetting visibleWhen(BooleanSupplier booleanSupplier) {
      super.visibleWhen(booleanSupplier);
      return this;
   }

   public int getAlpha() {
      return (Integer)super.value >>> 24 & 0xFF;
   }

   public int getRed() {
      return (Integer)super.value >>> 16 & 0xFF;
   }

   public int getGreen() {
      return (Integer)super.value >>> 8 & 0xFF;
   }

   public int getBlue() {
      return (Integer)super.value & 0xFF;
   }

   public static void m_0_void(boolean flag2) {
      flag = flag2;
   }

   public static boolean m_3_boolean() {
      return flag;
   }

   public static boolean m_8_boolean() {
      return false;
   }
}
