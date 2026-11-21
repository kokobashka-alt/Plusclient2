package wtf.onetap.setting;

import java.util.function.BooleanSupplier;

public class BooleanSetting extends Setting<Boolean> {
   private int key = -1;
   private static int[] valueArray;

   public BooleanSetting(String text, boolean flag) {
      super(text, flag);
   }

   public int getKey() {
      return this.key;
   }

   public void setKey(int value2) {
      this.key = value2;
   }

   public BooleanSetting build() {
      SettingRegistry.register(this);
      return this;
   }

   public BooleanSetting visibleWhen(BooleanSupplier booleanSupplier) {
      super.visibleWhen(booleanSupplier);
      return this;
   }

   public static void m_5(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_8_IArr() {
      return valueArray;
   }
}
