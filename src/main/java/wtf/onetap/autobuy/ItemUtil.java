package wtf.onetap.autobuy;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemLore;

public class ItemUtil {
   private final String text;
   private String text2;
   private final Item item;
   private final String text3;
   private final boolean flag;
   private final Predicate<ItemStack> predicate;
   private int value;
   private int value2;
   private boolean flag2;
   private boolean flag3;
   private int value3;
   private static int[] valueArray;

   public ItemUtil(String text4, Item item2, int value4) {
      this(text4, text4, item2, null, false, value4);
   }

   public ItemUtil(String text4, Item item2, int value4, String text5) {
      this(text4, text4, item2, text5, true, value4);
   }

   public ItemUtil(String text4, Item item2, int value4, Predicate<ItemStack> predicate2) {
      this.flag2 = true;
      this.text = text4;
      this.text2 = text4;
      this.item = item2;
      this.text3 = null;
      this.flag = false;
      this.predicate = predicate2;
      this.value = value4;
      this.value2 = 1;
   }

   public ItemUtil(String text4, String text5, Item item2, String text6, boolean flag4, int value4) {
      this.flag2 = true;
      this.text = text4;
      this.text2 = text5;
      this.item = item2;
      this.text3 = text6;
      this.flag = flag4 || text6 != null;
      this.predicate = null;
      this.value = value4;
      this.value2 = 1;
   }

   public String getText() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public Item getItem() {
      return this.item;
   }

   public int getValue() {
      return this.value;
   }

   public void setCount(int value4) {
      this.value = Math.max(0, value4);
   }

   public int getValue2() {
      return this.value2;
   }

   public void setMinCount(int value4) {
      this.value2 = Math.max(1, value4);
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public void setFlag2(boolean flag4) {
      this.flag2 = flag4;
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public void setFlag3(boolean flag4) {
      this.flag3 = flag4;
   }

   public void toggleEnabled() {
      this.flag3 = !this.flag3;
   }

   public boolean isStackable() {
      return this.item.getDefaultInstance().getMaxStackSize() > 1;
   }

   public ItemUtil withValue3(int value4) {
      this.value3 = value4;
      return this;
   }

   public ItemUtil withText2(String text4) {
      this.text2 = text4;
      return this;
   }

   public ItemStack m_9_ItemStack() {
      ItemStack itemStack = new ItemStack(this.item);
      if (this.value3 != 0) {
         itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(this.value3), List.of(), Optional.empty()));
      }

      return itemStack;
   }

   public boolean matches(ItemStack itemStack) {
      if (itemStack == null || itemStack.isEmpty()) {
         return false;
      } else if (this.predicate != null) {
         return this.predicate.test(itemStack);
      } else {
         String text4 = stripFormatting(itemStack.getHoverName().getString()).toLowerCase();
         String text5 = m_6_String(itemStack).toLowerCase();
         String text6 = stripFormatting(this.text).toLowerCase();
         if (this.text3 != null && !this.text3.isEmpty()) {
            String text7 = this.text3.toLowerCase();
            return itemStack.getItem() != this.item && this.item != Items.PLAYER_HEAD ? false : text4.contains(text7) || text5.contains(text7) || text4.contains(text6);
         } else if (this.flag) {
            return itemStack.getItem() != this.item ? false : text4.contains(text6) || text6.contains(text4);
         } else {
            return itemStack.getItem() == this.item;
         }
      }
   }

   private static String m_6_String(ItemStack itemStack) {
      ItemLore itemLore = itemStack.get(DataComponents.LORE);
      if (itemLore == null) {
         return "";
      }

      StringBuilder stringBuilder = new StringBuilder();

      for (Component component : itemLore.lines()) {
         stringBuilder.append(component.getString()).append(' ');
      }

      return stringBuilder.toString();
   }

   public static String stripFormatting(String text4) {
      return text4 == null ? "" : text4.replaceAll("§.", "").trim();
   }

   public static void m_3(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_5_IArr() {
      return valueArray;
   }

   private static String a(byte[] byteArray) {
      int value4 = 0;
      int value5;
      char[] charArray = new char[value5 = byteArray.length];

      for (int i = 0; i < value5; i++) {
         int value6;
         if ((value6 = 255 & byteArray[i]) < 192) {
            charArray[value4++] = (char)value6;
         } else if (value6 < 224) {
            char ch = (char)((char)(value6 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value4++] = ch;
         } else if (i < value5 - 2) {
            char ch2 = (char)((char)(value6 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value4++] = ch2;
         }
      }

      return new String(charArray, 0, value4);
   }
}
