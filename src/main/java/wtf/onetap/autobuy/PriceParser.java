package wtf.onetap.autobuy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class PriceParser {
   private static final Pattern PATTERN = Pattern.compile(
      "(?:\\$\\s*([\\d.,\\s]+(?:kk|кк|k|к|m|м|b|б)?)|([\\d.,\\s]+(?:kk|кк|k|к|m|м|b|б)?)\\s*\\$|(?:цена|стоимость|монет|купить\\s*за|price|cost|buy\\s*for)[:\\s]*([\\d.,\\s]+(?:kk|кк|k|к|m|м|b|б)?))",
      66
   );
   private static final Pattern PATTERN2 = Pattern.compile("\\$|цена|стоим|монет|купить\\s*за|price|cost|buy\\s*for|coins?", 66);
   private static final Pattern PATTERN3 = Pattern.compile("\\d[\\d.,\\s]*(?:kk|кк|k|к|m|м|b|б)?", 66);
   private static final Pattern PATTERN4 = Pattern.compile("(?:\\$|цена)[^0-9]*([0-9][0-9 ,.]*)(?:$|[^0-9])", 66);
   private static int value;

   private PriceParser() {
   }

   public static int m_8_int(ItemStack itemStack) {
      ItemLore itemLore = itemStack.get(DataComponents.LORE);
      if (itemLore == null) {
         return 0;
      }

      for (Component component : itemLore.lines()) {
         Matcher matcher = PATTERN4.matcher(component.getString());
         if (matcher.find()) {
            try {
               long longValue = Long.parseLong(matcher.group(1).replaceAll("[^0-9]", ""));
               return longValue > 2147483647L ? Integer.MAX_VALUE : (int)longValue;
            } catch (NumberFormatException numberFormatException) {
               return 0;
            }
         }
      }

      return 0;
   }

   public static int getPrice(ItemStack itemStack) {
      long longValue = getPriceLong(itemStack);
      return longValue <= 0L ? -1 : (int)Math.min(2147483647L, longValue);
   }

   public static long getPriceLong(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         PriceParser.StateHolder c3 = PriceParser.StateHolder.C3;

         try {
            ItemLore itemLore = itemStack.get(DataComponents.LORE);
            if (itemLore != null) {
               for (Component component : itemLore.lines()) {
                  if (component != null) {
                     PriceParser.StateHolder c32 = m_7_C3(component.getString());
                     if (c32.m_7(c3)) {
                        c3 = c32;
                     }

                     for (Component component2 : component.getSiblings()) {
                        if (component2 != null) {
                           c32 = m_7_C3(component2.getString());
                           if (c32.m_7(c3)) {
                              c3 = c32;
                           }
                        }
                     }
                  }
               }
            }
         } catch (Exception e) {
         }

         try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.level != null) {
               for (Component component3 : itemStack.getTooltipLines(TooltipContext.of(mc.level), mc.player, TooltipFlag.NORMAL)) {
                  if (component3 != null) {
                     PriceParser.StateHolder c33 = m_7_C3(component3.getString());
                     if (c33.m_7(c3)) {
                        c3 = c33;
                     }
                  }
               }
            }
         } catch (Exception e2) {
         }

         return c3.longValue;
      } else {
         return -1L;
      }
   }

   public static int m_5_int(ItemStack itemStack) {
      long longValue = getPriceLong(itemStack);
      return longValue <= 0L ? -1 : (int)Math.min(2147483647L, longValue / Math.max(1, itemStack.getCount()));
   }

   public static int getPriceInt(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         if (!BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath().equals("gray_dye")
            && !itemStack.getItem().getName(itemStack).getString().contains("Товар не актуален")) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.level != null) {
               var componentList = itemStack.getTooltipLines(TooltipContext.of(mc.level), mc.player, TooltipFlag.NORMAL);

               for (int i = 1; i < componentList.size(); i++) {
                  String text = ((Component)componentList.get(i)).getString();
                  String text2 = text.contains("$ Ценa: ") ? "$ Ценa: " : (text.contains("$ Цена: ") ? "$ Цена: " : null);
                  if (text2 != null) {
                     String text3 = text.substring(text.indexOf(text2) + text2.length()).trim().replace(",", "").replace("$", "").replaceAll("\\s+", "");
                     if (!text3.isEmpty()) {
                        try {
                           return Integer.parseInt(text3) / Math.max(1, itemStack.getCount());
                        } catch (NumberFormatException numberFormatException) {
                           return -1;
                        }
                     }
                  }
               }

               return -1;
            } else {
               return -1;
            }
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static boolean m_6_boolean(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         String text = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath().toLowerCase(Locale.ROOT);
         String text2 = itemStack.getItem().getDescriptionId().toLowerCase(Locale.ROOT);
         return isArmor(text) || isArmor(text2);
      } else {
         return false;
      }
   }

   private static boolean isArmor(String text) {
      return text.contains("helmet") || text.contains("chestplate") || text.contains("leggings") || text.contains("boots");
   }

   public static boolean m_0_boolean(ItemStack itemStack) {
      return m_0(itemStack, "thorns");
   }

   public static boolean m_0(ItemStack itemStack, String text) {
      if (itemStack != null && !itemStack.isEmpty() && text != null && !text.isEmpty()) {
         String text2 = text.toLowerCase(Locale.ROOT);
         if (m_8(itemStack.getEnchantments(), text2)) {
            return true;
         }

         ItemEnchantments itemEnchantments = itemStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
         return m_8(itemEnchantments, text2);
      } else {
         return false;
      }
   }

   private static boolean m_8(ItemEnchantments itemEnchantments, String text) {
      if (itemEnchantments != null && !itemEnchantments.isEmpty()) {
         for (Holder holder : itemEnchantments.keySet()) {
            String text2 = (String)holder.unwrapKey().map(object -> ((net.minecraft.resources.ResourceKey)object).identifier().toString()).orElse(holder.getRegisteredName());
            if (text2 != null && text2.toLowerCase(Locale.ROOT).contains(text)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static long m_7_long(AbstractContainerMenu abstractContainerMenu, int value2) {
      if (abstractContainerMenu != null && abstractContainerMenu.slots != null) {
         long longValue = 1125899906842597L;
         int value3 = Math.min(value2, abstractContainerMenu.slots.size());

         for (int i = 0; i < value3; i++) {
            Slot slot = abstractContainerMenu.slots.get(i);
            ItemStack itemStack = slot == null ? ItemStack.EMPTY : slot.getItem();
            longValue = longValue * 31L + i;
            if (itemStack != null && !itemStack.isEmpty()) {
               longValue = longValue * 31L + System.identityHashCode(itemStack);
               longValue = longValue * 31L + System.identityHashCode(itemStack.getItem());
               longValue = longValue * 31L + itemStack.getCount();
            }
         }

         return longValue;
      } else {
         return Long.MIN_VALUE;
      }
   }

   public static void m_7(AbstractContainerMenu abstractContainerMenu, int value2, boolean flag) {
      if (abstractContainerMenu != null && abstractContainerMenu.slots != null) {
         int value3 = Math.min(value2, abstractContainerMenu.slots.size());

         for (int i = 0; i < value3; i++) {
            Slot slot = abstractContainerMenu.slots.get(i);
            if (slot != null && !slot.getItem().isEmpty()) {
               m_8(slot.getItem(), flag);
            }
         }
      }
   }

   private static void m_8(ItemStack itemStack, boolean flag) {
      long longValue = flag ? m_8_int(itemStack) / Math.max(1, itemStack.getCount()) : -1L;
      m_0(itemStack, flag, longValue);
   }

   private static void m_0(ItemStack itemStack, boolean flag, long longValue) {
      if (itemStack != null && !itemStack.isEmpty()) {
         ItemLore itemLore = itemStack.get(DataComponents.LORE);
         ArrayList<Component> componentList = new ArrayList<>();
         if (itemLore != null) {
            for (Component component : itemLore.lines()) {
               componentList.add(component);
            }
         }

         boolean flag2 = false;
         int value2 = -1;

         for (int i = componentList.size() - 1; i >= 0; i--) {
            String text = m_2_String(((Component)componentList.get(i)).getString());
            if (isPricePerItemLine(text)) {
               componentList.remove(i);
               flag2 = true;
            } else if (value2 == -1 && m_7_boolean(text)) {
               value2 = i + 1;
            }
         }

         if (flag && itemStack.getCount() > 1) {
            if (longValue <= 0L) {
               if (flag2) {
                  itemStack.set(DataComponents.LORE, new ItemLore(List.copyOf(componentList)));
               }
            } else {
               String text2 = "§a$ §fЦена за 1шт: §a$" + formatNumber(longValue);
               int value3 = value2 == -1 ? componentList.size() : Math.min(value2, componentList.size());
               componentList.add(value3, Component.literal(text2));
               itemStack.set(DataComponents.LORE, new ItemLore(List.copyOf(componentList)));
            }
         } else {
            if (flag2) {
               itemStack.set(DataComponents.LORE, new ItemLore(List.copyOf(componentList)));
            }
         }
      }
   }

   public static String formatNumber(long longValue) {
      String text = Long.toString(Math.max(0L, longValue));
      StringBuilder stringBuilder = new StringBuilder();
      int value2 = text.length();

      for (int i = 0; i < value2; i++) {
         if (i > 0 && (value2 - i) % 3 == 0) {
            stringBuilder.append(',');
         }

         stringBuilder.append(text.charAt(i));
      }

      return stringBuilder.toString();
   }

   public static boolean isPricePerItemLine(String text) {
      return m_0_String(text).contains("ценаза1шт");
   }

   private static boolean m_7_boolean(String text) {
      String text2 = m_2_String(text);
      return !text2.isEmpty() && !isPricePerItemLine(text2) ? text2.contains("Цена: ") || text2.contains("$") : false;
   }

   private static String m_0_String(String text) {
      return m_2_String(text)
         .replace(' ', ' ')
         .toLowerCase(Locale.ROOT)
         .replaceAll("\\s+", "")
         .replace("Цена за 1 шт".toLowerCase(Locale.ROOT).replace(" ", ""), "ценаза1шт");
   }

   private static PriceParser.StateHolder m_7_C3(String text) {
      String text2 = cleanText(text);
      if (text2.isEmpty()) {
         return PriceParser.StateHolder.C3;
      }

      if (m_0_boolean(text2)) {
         return PriceParser.StateHolder.C3;
      }

      Matcher matcher = PATTERN.matcher(text2);
      PriceParser.StateHolder c3 = PriceParser.StateHolder.C3;

      while (matcher.find()) {
         for (int i = 1; i <= matcher.groupCount(); i++) {
            long longValue = parseNumber(matcher.group(i));
            if (longValue > 0L) {
               c3 = c3.m_9(240, longValue);
            }
         }
      }

      if (c3.m_4_boolean()) {
         return c3;
      }

      boolean flag = PATTERN2.matcher(text2).find();
      Matcher matcher2 = PATTERN3.matcher(text2);

      while (matcher2.find()) {
         long longValue2 = parseNumber(matcher2.group());
         if (longValue2 > 0L) {
            int value2 = m_8(text2, matcher2.start(), matcher2.end(), flag);
            if (value2 > 0) {
               c3 = c3.m_9(value2, longValue2);
            }
         }
      }

      return c3;
   }

   private static long parseNumber(String text) {
      if (text == null) {
         return -1L;
      }

      String text2 = cleanText(text).replace(' ', ' ').replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
      if (text2.isEmpty()) {
         return -1L;
      }

      long longValue = 1L;
      if (text2.endsWith("kk") || text2.endsWith("кк")) {
         longValue = 1000000L;
         text2 = text2.substring(0, text2.length() - 2);
      } else if (text2.endsWith("k") || text2.endsWith("к")) {
         longValue = 1000L;
         text2 = text2.substring(0, text2.length() - 1);
      } else if (text2.endsWith("m") || text2.endsWith("м")) {
         longValue = 1000000L;
         text2 = text2.substring(0, text2.length() - 1);
      } else if (text2.endsWith("b") || text2.endsWith("б")) {
         longValue = 1000000000L;
         text2 = text2.substring(0, text2.length() - 1);
      }

      if (longValue > 1L) {
         String text3 = text2.replace(',', '.').replaceAll("[^0-9.]", "");
         if (text3.isEmpty()) {
            return -1L;
         }

         int value2 = text3.indexOf(46);
         if (value2 >= 0) {
            text3 = text3.substring(0, value2 + 1) + text3.substring(value2 + 1).replace(".", "");
         }

         try {
            return Math.round(Double.parseDouble(text3) * longValue);
         } catch (NumberFormatException numberFormatException) {
            return -1L;
         }
      } else {
         String text4 = text2.replaceAll("[^0-9]", "");
         if (text4.isEmpty()) {
            return -1L;
         }

         try {
            return Long.parseLong(text4);
         } catch (NumberFormatException numberFormatException2) {
            return -1L;
         }
      }
   }

   private static String cleanText(String text) {
      if (text == null) {
         return "";
      }

      String text2 = ChatFormatting.stripFormatting(text);
      return m_5_String(text2 == null ? text : text2).trim();
   }

   public static String m_5_String(String text) {
      if (text != null && !text.isEmpty()) {
         StringBuilder stringBuilder = null;

         for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            String text2 = null;
            if (ch == 'ꐂ') {
               text2 = " $";
            } else if (isSymbolChar(ch)) {
               text2 = " ";
            }

            if (text2 != null && stringBuilder == null) {
               stringBuilder = new StringBuilder(text.length()).append(text, 0, i);
            }

            if (stringBuilder != null) {
               if (text2 != null) {
                  stringBuilder.append(text2);
               } else {
                  stringBuilder.append(ch);
               }
            }
         }

         return stringBuilder == null ? text : stringBuilder.toString();
      } else {
         return "";
      }
   }

   private static boolean isSymbolChar(char ch) {
      return ch >= 'ꀀ' && ch <= '\ua4cf' || ch >= '\ue000' && ch <= '\uf8ff' || Character.isSurrogate(ch);
   }

   public static String m_2_String(String text) {
      return cleanText(text);
   }

   public static String m_6_String(String text) {
      if (text != null && !text.isEmpty()) {
         try {
            JsonElement jsonElement = JsonParser.parseString(text);
            StringBuilder stringBuilder = new StringBuilder();
            appendText(jsonElement, stringBuilder);
            String text2 = stringBuilder.toString();
            return text2.isEmpty() ? text : text2;
         } catch (Exception e) {
            return text;
         }
      } else {
         return "";
      }
   }

   private static void appendText(JsonElement jsonElement, StringBuilder stringBuilder) {
      if (jsonElement != null && stringBuilder != null) {
         if (jsonElement.isJsonPrimitive()) {
            stringBuilder.append(jsonElement.getAsString());
         } else if (jsonElement.isJsonArray()) {
            for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
               appendText(jsonElement2, stringBuilder);
            }
         } else if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("text") && jsonObject.get("text").isJsonPrimitive()) {
               stringBuilder.append(jsonObject.get("text").getAsString());
            }

            if (jsonObject.has("extra")) {
               appendText(jsonObject.get("extra"), stringBuilder);
            }
         }
      }
   }

   private static int m_8(String text, int value2, int value3, boolean flag) {
      short shortValue = 0;
      String text2 = text.substring(Math.max(0, value2 - 14), value2).toLowerCase(Locale.ROOT);
      String text3 = text.substring(value3, Math.min(text.length(), value3 + 14)).toLowerCase(Locale.ROOT);
      String text4 = text2 + " " + text3;
      if (text2.contains("$") || text3.contains("$")) {
         shortValue += 170;
      }

      if (PATTERN2.matcher(text4).find()) {
         shortValue += 120;
      }

      if (flag) {
         shortValue += 50;
      }

      return shortValue;
   }

   private static boolean m_0_boolean(String text) {
      String text2 = cleanText(text).replace(' ', ' ').toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
      return text2.contains("ценаза1шт");
   }

   static {
      m_9_void(0);
   }

   public static void m_9_void(int value2) {
      value = value2;
   }

   public static int m_8_int() {
      return value;
   }

   public static int m_1_int() {
      return 36;
   }

   private static final class StateHolder {
      private static final PriceParser.StateHolder C3 = new PriceParser.StateHolder(-1, -1L);
      private final int value;
      private final long longValue;

      private StateHolder(int value2, long longValue2) {
         this.value = value2;
         this.longValue = longValue2;
      }

      private boolean m_4_boolean() {
         return this.longValue > 0L;
      }

      private PriceParser.StateHolder m_9(int value2, long longValue2) {
         if (longValue2 <= 0L) {
            return this;
         } else {
            return this.m_4_boolean() && value2 <= this.value && (value2 != this.value || longValue2 <= this.longValue) ? this : new PriceParser.StateHolder(value2, longValue2);
         }
      }

      private boolean m_7(PriceParser.StateHolder c3) {
         return !this.m_4_boolean() ? false : !c3.m_4_boolean() || this.value > c3.value || this.value == c3.value && this.longValue > c3.longValue;
      }
   }
}
