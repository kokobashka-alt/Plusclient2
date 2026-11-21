package wtf.onetap.obf;

import wtf.onetap.module.Module;

public class C7n {
   private static C7n.C4 f_6 = C7n.C4.NONE;
   private static boolean f_8 = false;
   private static boolean f_3 = false;
   private static Module[] f_5;

   private C7n() {
   }

   public static void m_6_void() {
      f_6 = C7n.C4.NONE;
      f_8 = false;
      f_3 = false;
   }

   public static boolean m_3_boolean() {
      return f_8;
   }

   public static boolean m_8_boolean() {
      return f_6 == C7n.C4.SELL;
   }

   public static boolean m_7_boolean() {
      return f_6 == C7n.C4.RESELL;
   }

   public static boolean m_0_boolean() {
      return !f_8 && !m_7_boolean();
   }

   public static boolean m_4_boolean() {
      if (!m_0_boolean()) {
         return false;
      }

      f_6 = C7n.C4.SELL;
      return true;
   }

   public static void m_1(boolean flag) {
      if (f_6 == C7n.C4.SELL) {
         f_6 = C7n.C4.NONE;
      }
   }

   public static boolean m_6_boolean() {
      if (f_3 && !m_8_boolean()) {
         f_6 = C7n.C4.RESELL;
         return true;
      } else {
         return false;
      }
   }

   public static void m_5_void(boolean flag) {
      if (f_6 == C7n.C4.RESELL) {
         f_6 = C7n.C4.NONE;
      }
   }

   public static void m_0_void() {
      f_8 = false;
      f_3 = true;
   }

   public static void m_5_void() {
      m_1_C3_sArr();
      f_8 = true;
      f_3 = true;
      if (f_6 == C7n.C4.SELL) {
         f_6 = C7n.C4.NONE;
      }
   }

   public static void m_7_void() {
      f_8 = false;
      f_3 = true;
   }

   static {
      m_3(null);
   }

   public static void m_3(Module[] moduleArray) {
      f_5 = moduleArray;
   }

   public static Module[] m_1_C3_sArr() {
      return f_5;
   }

   private enum C4 {
      NONE,
      SELL,
      RESELL;

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
}
