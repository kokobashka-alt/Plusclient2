package wtf.onetap.setting;

import java.util.function.BooleanSupplier;
import wtf.onetap.module.Module;

public class TextSetting extends Setting<String> {
   private boolean numericOnly;
   private static Module[] moduleArray;

   public TextSetting(String text, String text2) {
      super(text, text2 == null ? "" : text2);
   }

   public TextSetting build() {
      SettingRegistry.register(this);
      return this;
   }

   public TextSetting visibleWhen(BooleanSupplier booleanSupplier) {
      super.visibleWhen(booleanSupplier);
      return this;
   }

   public void setValue(String text) {
      super.setValue(text == null ? "" : text);
   }

   public TextSetting numericOnly() {
      this.numericOnly = true;
      return this;
   }

   public boolean isNumericOnly() {
      return this.numericOnly;
   }

   public int getInt() {
      String text = super.value == null ? "" : ((String)super.value).replaceAll("[^0-9]", "");
      if (text.isEmpty()) {
         return 0;
      }

      try {
         return Integer.parseInt(text);
      } catch (NumberFormatException numberFormatException) {
         return 0;
      }
   }

   public static void m_6(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_8_C3_sArr() {
      return moduleArray;
   }

   private static String a(byte[] byteArray) {
      int value2 = 0;
      int value3;
      char[] charArray = new char[value3 = byteArray.length];

      for (int i = 0; i < value3; i++) {
         int value4;
         if ((value4 = 255 & byteArray[i]) < 192) {
            charArray[value2++] = (char)value4;
         } else if (value4 < 224) {
            char ch = (char)((char)(value4 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value2++] = ch;
         } else if (i < value3 - 2) {
            char ch2 = (char)((char)(value4 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value2++] = ch2;
         }
      }

      return new String(charArray, 0, value2);
   }
}
