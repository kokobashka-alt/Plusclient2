package wtf.onetap.setting;

import java.util.List;
import wtf.onetap.module.ModuleManager;

public class SettingRegistry {
   private static final ThreadLocal<List<Setting<?>>> CURRENT = new ThreadLocal<>();
   private static boolean flag;

   public static void begin(List<Setting<?>> settingList) {
      CURRENT.set(settingList);
   }

   public static void register(Setting<?> setting) {
      ModuleManager.m_1_StringArr();
      var settingList = CURRENT.get();
      if (settingList == null) {
         throw new IllegalStateException("Setting created outside a Module constructor; call .build() only inside a Module's field initializers");
      }

      settingList.add(setting);
   }

   public static void end() {
      CURRENT.remove();
   }

   private SettingRegistry() {
   }

   public static void m_3_void(boolean flag2) {
      flag = flag2;
   }

   public static boolean m_6_boolean() {
      return flag;
   }

   public static boolean m_0_boolean() {
      return true;
   }

   private static String a(byte[] byteArray) {
      int value = 0;
      int value2;
      char[] charArray = new char[value2 = byteArray.length];

      for (int i = 0; i < value2; i++) {
         int value3;
         if ((value3 = 255 & byteArray[i]) < 192) {
            charArray[value++] = (char)value3;
         } else if (value3 < 224) {
            char ch = (char)((char)(value3 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value++] = ch;
         } else if (i < value2 - 2) {
            char ch2 = (char)((char)(value3 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value++] = ch2;
         }
      }

      return new String(charArray, 0, value);
   }
}
