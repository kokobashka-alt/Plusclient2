package wtf.onetap.autobuy;

import java.util.Locale;
import net.minecraft.world.item.ItemStack;
import wtf.onetap.module.Module;

public abstract class ItemMatcher {
   private ItemMatcher.ModeKind c0;
   private int value;
   private static String text;

   public ItemMatcher(ItemMatcher.ModeKind c02, int value2) {
      this.c0 = c02;
      this.value = value2;
   }

   public boolean isActive() {
      return this.c0 != ItemMatcher.ModeKind.OFF;
   }

   public boolean isDeny() {
      return this.c0 == ItemMatcher.ModeKind.DENY;
   }

   public boolean m_6_boolean() {
      return this.value > 0;
   }

   public int getValue() {
      return this.value;
   }

   public ItemMatcher.ModeKind getC0() {
      return this.c0;
   }

   public void m_2_void(boolean flag) {
      this.c0 = flag ? ItemMatcher.ModeKind.C0 : ItemMatcher.ModeKind.OFF;
   }

   public void setValue(int value2) {
      this.value = value2;
   }

   public void m_4_void(int value2) {
      if (this.c0 == ItemMatcher.ModeKind.C0 && this.value != 0) {
         this.value = Math.max(1, this.m_7_int(this.value + value2));
      }
   }

   public abstract int m_7_int(int value2);

   public abstract boolean matches(ItemStack itemStack);

   public abstract String getText();

   public abstract String m_5_String();

   public static String toRoman(int value2) {
      String[] stringArray = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
      return value2 >= 0 && value2 < stringArray.length ? stringArray[value2] : String.valueOf(value2 + 1);
   }

   public static int m_0_int(String text2) {
      if (text2 != null && !text2.isBlank()) {
         String text3 = text2.replaceAll("§.", "").toLowerCase(Locale.ROOT).replaceAll("\\s+", "").trim();
         String[] stringArray = new String[]{"i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", "x"};

         for (int i = stringArray.length - 1; i >= 0; i--) {
            if (text3.contains(stringArray[i])) {
               return i + 1;
            }
         }

         String text4 = text3.replaceAll("[^0-9]", "");
         if (!text4.isEmpty()) {
            try {
               int value2 = Integer.parseInt(text4);
               if (value2 >= 1 && value2 <= 10) {
                  return value2;
               }
            } catch (NumberFormatException numberFormatException) {
            }
         }

         return 0;
      } else {
         return 0;
      }
   }

   public static void m_0_void(String text2) {
      text = text2;
   }

   public static String m_4_String() {
      return text;
   }

   static {
      m_0_void(null);
   }

   public enum ModeKind {
      C0,
      OFF,
      DENY;

      private static Module[] moduleArray;

      public static void m_9(Module[] moduleArray2) {
         moduleArray = moduleArray2;
      }

      public static Module[] m_7_C3_sArr() {
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
}
