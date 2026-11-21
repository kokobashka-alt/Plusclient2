package wtf.onetap.autobuy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

public class SellerParser {
   private static final Pattern PATTERN = Pattern.compile("Продавец:\\s*(.+)");
   private static final Pattern PATTERN2 = Pattern.compile("\\$(?:[^\\d]*?Цена)?[^\\d]*?([0-9][\\d,]*)");

   public static String m_6(Slot slot) {
      if (!slot.hasItem()) {
         return null;
      }

      ItemStack itemStack = slot.getItem();
      ItemLore itemLore = itemStack.get(DataComponents.LORE);
      if (itemLore != null) {
         for (Component component : itemLore.lines()) {
            String text = component.getString().replaceAll("(?i)§[0-9A-FK-OR]", "");
            Matcher matcher = PATTERN.matcher(text);
            if (matcher.find()) {
               return matcher.group(1).trim();
            }
         }
      }

      return null;
   }

   public static int m_2(Slot slot) {
      if (!slot.hasItem()) {
         return 0;
      }

      ItemStack itemStack = slot.getItem();
      ItemLore itemLore = itemStack.get(DataComponents.LORE);
      if (itemLore != null) {
         for (Component component : itemLore.lines()) {
            String text = component.getString();
            if (text.contains("$") || text.contains("Цена")) {
               String text2 = text.replaceAll("[^0-9]", "");
               if (!text2.isEmpty()) {
                  try {
                     return Math.toIntExact(Long.parseLong(text2));
                  } catch (NumberFormatException numberFormatException) {
                  }
               }
            }
         }
      }

      return 0;
   }
}
