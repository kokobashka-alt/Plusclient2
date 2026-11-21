package wtf.onetap.setting;

import java.util.function.BooleanSupplier;

public class KeybindSetting extends Setting<Integer> {
   private static int[] valueArray;

   public KeybindSetting(String text, int value2) {
      super(text, value2);
   }

   public KeybindSetting build() {
      SettingRegistry.register(this);
      return this;
   }

   public KeybindSetting visibleWhen(BooleanSupplier booleanSupplier) {
      super.visibleWhen(booleanSupplier);
      return this;
   }

   public static void m_6(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_9_IArr() {
      return valueArray;
   }
}
