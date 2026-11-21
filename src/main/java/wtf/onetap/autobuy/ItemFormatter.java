package wtf.onetap.autobuy;

import net.minecraft.world.item.ItemStack;

public class ItemFormatter {
   private final ItemStack itemStack;
   private final String text;
   private final int value;
   private final int value2;
   private final String text2;
   private final long longValue;
   private static String[] textArray;

   public ItemFormatter(ItemStack itemStack2, String text3, int value3, int value4, String text4) {
      this.itemStack = itemStack2 != null && !itemStack2.isEmpty() ? itemStack2.copy() : ItemStack.EMPTY;
      this.text = text3 == null ? "" : text3;
      this.value = Math.max(1, value3);
      this.value2 = Math.max(0, value4);
      this.text2 = text4 != null && !text4.isBlank() ? text4 : "Неизвестно";
      this.longValue = System.currentTimeMillis();
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public String getText() {
      return this.text;
   }

   public int getValue() {
      return this.value;
   }

   public int getValue2() {
      return this.value2;
   }

   public String getText2() {
      return this.text2;
   }

   public long getLongValue() {
      return this.longValue;
   }

   public String m_4_String() {
      long longValue2 = System.currentTimeMillis() - this.longValue;
      long longValue3 = longValue2 / 1000L % 60L;
      long longValue4 = longValue2 / 60000L % 60L;
      long longValue5 = longValue2 / 3600000L;
      long longValue6 = longValue3;
      long longValue7 = longValue4;
      return longValue5 + "ч " + longValue7 + "м " + longValue6 + "с";
   }

   public String m_6_String() {
      String text3 = String.valueOf(this.value2);
      StringBuilder stringBuilder = new StringBuilder();
      int value3 = 0;

      for (int i = text3.length() - 1; i >= 0; i--) {
         if (value3 > 0 && value3 % 3 == 0) {
            stringBuilder.insert(0, '.');
         }

         stringBuilder.insert(0, text3.charAt(i));
         value3++;
      }

      return "$" + stringBuilder;
   }

   public String format() {
      return this.text + (this.value > 1 ? " x" + this.value : "");
   }

   public static void m_0(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_5_StringArr() {
      return textArray;
   }

   static {
      m_0(new String[1]);
   }
}
