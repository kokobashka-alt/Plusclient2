package wtf.onetap.autobuy;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import wtf.onetap.util.MinecraftAccess;

public class ItemEntry implements MinecraftAccess {
   private static final ItemEntry ITEM_ENTRY = new ItemEntry();
   private static final Pattern PATTERN = Pattern.compile("(?i)(?:продавец|seller)\\s*:?\\s*([\\w\\d_]+)");
   private final List<ItemFormatter> itemFormatterList;
   private final List<ItemFormatter> itemFormatterList2;
   private String text;
   private long longValue;

   private ItemEntry() {
      this.itemFormatterList = new ArrayList<>();
      this.itemFormatterList2 = Collections.unmodifiableList(this.itemFormatterList);
      this.text = "";
   }

   public static ItemEntry m_9_C7x() {
      return ITEM_ENTRY;
   }

   public void m_4(ItemStack itemStack, String text2, int value) {
      if (itemStack != null && !itemStack.isEmpty()) {
         String text3 = cleanName(text2);
         if (text3.isEmpty()) {
            text3 = ItemUtil.stripFormatting(itemStack.getHoverName().getString());
         }

         if (!this.m_7(text3, value)) {
            int value2 = Math.max(1, itemStack.getCount());
            this.itemFormatterList.add(0, new ItemFormatter(itemStack, text3, value2, value, m_9_String(itemStack)));
            this.m_3_void(text3, value);
            this.m_3_void();
         }
      } else {
         this.m_9_void(text2, value);
      }
   }

   public void m_9_void(String text2, int value) {
      if (text2 != null && !text2.isBlank() && value > 0) {
         String text3 = cleanName(text2);
         if (!this.m_7(text3, value)) {
            ItemStack itemStack = ItemStack.EMPTY;
            int value2 = 1;

            for (ItemUtil itemUtil : FunTimeItems.getItems()) {
               if (itemUtil != null) {
                  String text4 = itemUtil.getText().toLowerCase(Locale.ROOT);
                  String text5 = itemUtil.getText2() == null ? text4 : itemUtil.getText2().toLowerCase(Locale.ROOT);
                  String text6 = text3.toLowerCase(Locale.ROOT);
                  if (text6.contains(text4) || text4.contains(text6) || text6.contains(text5) || text5.contains(text6)) {
                     try {
                        itemStack = itemUtil.m_9_ItemStack();
                        if (itemStack != null && !itemStack.isEmpty()) {
                           value2 = Math.max(1, itemStack.getCount());
                        }
                     } catch (Exception e) {
                        itemStack = ItemStack.EMPTY;
                     }
                     break;
                  }
               }
            }

            String text7 = this.m_0_String(text3);
            this.itemFormatterList.add(0, new ItemFormatter(itemStack, text3, value2, value, text7));
            this.m_3_void(text3, value);
            this.m_3_void();
         }
      }
   }

   private boolean m_7(String text2, int value) {
      if (text2 != null && !text2.isBlank() && value > 0) {
         String text3 = text2.toLowerCase(Locale.ROOT) + "|" + value;
         long longValue2 = System.currentTimeMillis();
         if (text3.equals(this.text) && longValue2 - this.longValue < 3000L) {
            return true;
         }

         if (!this.itemFormatterList.isEmpty()) {
            ItemFormatter itemFormatter = this.itemFormatterList.get(0);
            if (itemFormatter.getValue2() == value && itemFormatter.getText().equalsIgnoreCase(text2) && longValue2 - itemFormatter.getLongValue() < 3000L) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void m_3_void(String text2, int value) {
      this.text = text2.toLowerCase(Locale.ROOT) + "|" + value;
      this.longValue = System.currentTimeMillis();
   }

   public void m_4_void(JsonObject jsonObject) {
      int value = jsonObject.has("quantity") ? jsonObject.get("quantity").getAsInt() : 1;
      ItemStack itemStack = ItemStack.EMPTY;
      if (jsonObject.has("item")) {
         itemStack = BuiltInRegistries.ITEM
            .getOptional(Identifier.parse(jsonObject.get("item").getAsString()))
            .map(item -> new ItemStack(item, value))
            .orElse(ItemStack.EMPTY);
      }

      this.itemFormatterList
         .add(
            0, new ItemFormatter(itemStack, jsonObject.get("name").getAsString(), value, jsonObject.get("price").getAsInt(), jsonObject.has("seller") ? jsonObject.get("seller").getAsString() : null)
         );
      this.m_3_void();
   }

   public List<ItemFormatter> getItemFormatterList2() {
      return this.itemFormatterList2;
   }

   public void clear() {
      this.itemFormatterList.clear();
   }

   private void m_3_void() {
      while (this.itemFormatterList.size() > 100) {
         this.itemFormatterList.remove(this.itemFormatterList.size() - 1);
      }
   }

   private static String cleanName(String text2) {
      return text2 == null ? "" : ItemUtil.stripFormatting(text2).replaceAll("^\\s*-\\s*", "").replaceAll("\\s*-\\s*$", "").trim();
   }

   private String m_0_String(String text2) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return "Неизвестно";
      }

      String text3 = text2.toLowerCase(Locale.ROOT);

      for (int i = 0; i <= 35; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty()) {
            String text4 = ItemUtil.stripFormatting(itemStack.getHoverName().getString()).toLowerCase(Locale.ROOT);
            if (text4.contains(text3) || text3.contains(text4)) {
               String text5 = m_9_String(itemStack);
               if (!"Неизвестно".equals(text5)) {
                  return text5;
               }
            }
         }
      }

      return "Неизвестно";
   }

   private static String m_9_String(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         ItemLore itemLore = itemStack.get(DataComponents.LORE);
         if (itemLore == null) {
            return "Неизвестно";
         }

         for (Component component : itemLore.lines()) {
            String text2 = component.getString();
            Matcher matcher = PATTERN.matcher(text2);
            if (matcher.find()) {
               return ItemUtil.stripFormatting(matcher.group(1));
            }

            String text3 = text2.toLowerCase(Locale.ROOT);
            int value = text3.indexOf("продавец");
            if (value < 0) {
               value = text3.indexOf("seller");
            }

            if (value >= 0) {
               String text4 = ItemUtil.stripFormatting(text2.substring(value).replaceAll("(?i)(продавец|seller)\\s*:?\\s*", ""));
               if (!text4.isEmpty()) {
                  String[] stringArray = text4.split("\\s+");
                  if (stringArray.length > 0 && !stringArray[0].isEmpty()) {
                     return stringArray[0];
                  }
               }
            }
         }

         return "Неизвестно";
      } else {
         return "Неизвестно";
      }
   }
}
