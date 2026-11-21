package wtf.onetap.autobuy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import wtf.onetap.config.AutoBuyBans;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.InventoryUtil;

public class AuctionParser {
   private static final Pattern PATTERN = Pattern.compile("(?:\\$\\s*([\\d,.\\s]+)|([\\d,.\\s]+)\\s*\\$|(?:Цена|Price)[:\\s]*([\\d,.\\s]+))", 66);
   private static final Pattern PATTERN2 = Pattern.compile("(.+?)\\s*\\[(\\d+)\\s*[уУ]р\\.?\\]");
   private static volatile boolean flag;
   private static volatile int value;
   private static AuctionParser.ModeKind c3 = AuctionParser.ModeKind.IDLE;
   private static long longValue;
   private static int value2;
   private static List<ItemUtil> itemUtilList = new ArrayList<>();
   private static ItemUtil itemUtil;
   private static int value3;
   private static int value4 = -1;
   private static int value5;
   private static boolean flag2;
   private static long longValue2;

   private AuctionParser() {
   }

   public static boolean isRunning() {
      return flag;
   }

   public static int getDiscount() {
      return value;
   }

   public static void setDiscount(int value6) {
      value = Math.max(0, Math.min(100, value6));
   }

   public static String getCurrentName() {
      return itemUtil != null ? itemUtil.getText() : null;
   }

   public static void m_9_void(boolean flag3) {
      if (flag != flag3) {
         Minecraft mc = Minecraft.getInstance();
         if (flag3) {
            var itemUtilList2 = getEnabledItems();
            if (itemUtilList2 == null || itemUtilList2.isEmpty()) {
               ChatUtil.log("§c[Авто-парсер] §7Нет предметов. ПКМ по предмету в GUI AutoBuy");
               flag = false;
               m_5_void();
               return;
            }

            flag = true;
            c3 = AuctionParser.ModeKind.SEND_SEARCH;
            longValue = 0L;
            value2 = 0;
            itemUtil = null;
            itemUtilList = itemUtilList2;
            value3 = 0;
            reset();
            AuctionConfig.getConfig().setFlag(false);
            if (!(mc.gui.screen() instanceof ContainerScreen) && mc.player != null && mc.getConnection() != null) {
               mc.getConnection().sendCommand("ah");
            }

            ChatUtil.log("§a[Авто-парсер] §7Старт, предметов: §f" + itemUtilList.size() + " §7| скидка §f" + value + "% §8| все страницы");
         } else {
            flag = false;
            m_5_void();
            ChatUtil.log("§c[Авто-парсер] §7Выключен");
         }
      }
   }

   private static void m_5_void() {
      c3 = AuctionParser.ModeKind.IDLE;
      longValue = 0L;
      value2 = 0;
      itemUtil = null;
      itemUtilList = new ArrayList<>();
      value3 = 0;
      reset();
   }

   private static void reset() {
      value4 = -1;
      value5 = 0;
      flag2 = false;
      longValue2 = 0L;
   }

   public static void m_5(Minecraft mc) {
      if (flag && mc != null && mc.player != null) {
         if (c3 != AuctionParser.ModeKind.IDLE) {
            long longValue3 = System.currentTimeMillis();
            if (c3 == AuctionParser.ModeKind.FINISH_WAIT) {
               if (longValue3 >= longValue) {
                  stopAndClose(mc);
               }
            } else if (longValue3 >= longValue) {
               if (c3 != AuctionParser.ModeKind.SEND_SEARCH) {
                  if (itemUtil != null) {
                     value3++;
                     if (value3 < 2) {
                        reset();
                        startSearch(mc, itemUtil);
                        c3 = AuctionParser.ModeKind.WAIT_RESULTS;
                        longValue = longValue3 + 500L;
                     } else {
                        m_6_void(longValue3);
                     }
                  }
               } else {
                  if (itemUtilList == null || itemUtilList.isEmpty()) {
                     itemUtilList = getEnabledItems();
                     value2 = 0;
                  }

                  if (itemUtilList != null && !itemUtilList.isEmpty()) {
                     if (value2 >= itemUtilList.size()) {
                        c3 = AuctionParser.ModeKind.FINISH_WAIT;
                        longValue = longValue3 + 500L;
                     } else {
                        itemUtil = itemUtilList.get(value2);
                        value3 = 0;
                        reset();
                        startSearch(mc, itemUtil);
                        c3 = AuctionParser.ModeKind.WAIT_RESULTS;
                        longValue = longValue3 + 500L;
                     }
                  } else {
                     m_9_void(false);
                  }
               }
            }
         }
      }
   }

   public static void m_0(Minecraft mc, List<Slot> slotList) {
      if (flag && mc != null && mc.player != null) {
         if (itemUtilList == null || itemUtilList.isEmpty()) {
            itemUtilList = getEnabledItems();
            value2 = 0;
         }

         if (itemUtilList != null && !itemUtilList.isEmpty()) {
            long longValue3 = System.currentTimeMillis();
            if (value2 >= itemUtilList.size()) {
               stopAndClose(mc);
            } else {
               switch (c3.ordinal()) {
                  case 1:
                     if (longValue3 < longValue) {
                        return;
                     }

                     if (value2 >= itemUtilList.size()) {
                        stopAndClose(mc);
                        return;
                     }

                     itemUtil = itemUtilList.get(value2);
                     value3 = 0;
                     reset();
                     startSearch(mc, itemUtil);
                     c3 = AuctionParser.ModeKind.WAIT_RESULTS;
                     longValue = longValue3 + 500L;
                     break;
                  case 2:
                  case 4:
                     if (longValue3 < longValue) {
                        return;
                     }

                     if (!m_1(mc, slotList)) {
                        if (longValue3 - longValue2 > 2200L) {
                           value3++;
                           if (value3 < 2 && itemUtil != null) {
                              reset();
                              startSearch(mc, itemUtil);
                              c3 = AuctionParser.ModeKind.WAIT_RESULTS;
                              longValue = longValue3 + 500L;
                           } else {
                              m_2_void(longValue3);
                           }
                        }

                        return;
                     }

                     flag2 = true;
                     m_7_void(slotList);
                     int value6 = findFirstSlot(slotList);
                     if (value6 >= 0 && value5 < 40) {
                        c3 = AuctionParser.ModeKind.CLICK_NEXT;
                        longValue = longValue3 + 80L;
                     } else {
                        m_2_void(longValue3);
                     }
                     break;
                  case 3:
                     if (longValue3 < longValue) {
                        return;
                     }

                     if (!m_1(mc, slotList)) {
                        c3 = AuctionParser.ModeKind.WAIT_RESULTS;
                        longValue = longValue3 + 300L;
                        return;
                     }

                     int value7 = findFirstSlot(slotList);
                     if (value7 < 0 || value5 >= 40) {
                        m_2_void(longValue3);
                        return;
                     }

                     int value8 = mc.player.containerMenu != null ? mc.player.containerMenu.containerId : 0;
                     InventoryUtil.clickSlot(value8, value7, 0, ContainerInput.PICKUP);
                     value5++;
                     c3 = AuctionParser.ModeKind.WAIT_NEXT_PAGE;
                     longValue = longValue3 + 550L;
                     break;
                  case 5:
                     if (longValue3 < longValue) {
                        return;
                     }

                     if (value2 >= itemUtilList.size()) {
                        stopAndClose(mc);
                     } else {
                        c3 = AuctionParser.ModeKind.SEND_SEARCH;
                        longValue = 0L;
                     }
                     break;
                  case 6:
                     if (longValue3 < longValue) {
                        return;
                     }

                     stopAndClose(mc);
               }
            }
         } else {
            m_9_void(false);
         }
      }
   }

   private static boolean m_1(Minecraft mc, List<Slot> slotList) {
      if (!(mc != null && mc.gui.screen() instanceof ContainerScreen containerScreen)) {
         return false;
      }

      if (!flag2 && longValue2 <= 0L) {
         return false;
      }

      String text = ItemUtil.stripFormatting(containerScreen.getTitle().getString()).toLowerCase(Locale.ROOT).replace('ё', 'е');
      if (text.contains("поиск") || text.contains("search")) {
         return true;
      }

      if (m_1_boolean(text)) {
         if (value5 > 0 && flag2) {
            return true;
         } else {
            return itemUtil != null && m_4(slotList, itemUtil) > 0 && longValue2 > 0L && System.currentTimeMillis() - longValue2 > 400L ? false : false;
         }
      } else {
         if (itemUtil != null) {
            String text2 = ItemUtil.stripFormatting(itemUtil.getText2()).toLowerCase(Locale.ROOT).replace('ё', 'е');
            String text3 = ItemUtil.stripFormatting(itemUtil.getText()).toLowerCase(Locale.ROOT).replace('ё', 'е');
            if (!text2.isEmpty() && (text.contains(text2) || text2.contains(text)) || !text3.isEmpty() && (text.contains(text3) || text3.contains(text))) {
               return true;
            }
         }

         return longValue2 > 0L && !m_1_boolean(text);
      }
   }

   private static boolean m_1_boolean(String text) {
      if (text != null && !text.isBlank()) {
         String text2 = text.toLowerCase(Locale.ROOT).replace('ё', 'е');
         return !text2.contains("поиск") && !text2.contains("search")
            ? text2.contains("аукцион")
               || text2.contains("auction")
               || text2.contains("торг")
               || text2.contains("маркет")
               || text2.contains("market")
               || text2.contains("базар")
               || text2.equals("ah")
               || text2.startsWith("ah ")
            : false;
      } else {
         return false;
      }
   }

   private static void m_7_void(List<Slot> slotList) {
      int value6 = m_4(slotList, itemUtil);
      if (value6 > 0 && (value4 <= 0 || value6 < value4)) {
         value4 = value6;
      }
   }

   private static void m_2_void(long longValue3) {
      if (value4 > 0 && itemUtil != null) {
         int value6 = (int)Math.floor(value4 * (100.0 - value) / 100.0);
         if (value6 < 0) {
            value6 = 0;
         }

         itemUtil.setCount(value6);
         String text = itemUtil.getText();
         String text2 = formatPrice(value6);
         String text3 = formatPrice(value4);
         int value7 = value5 + 1;
         String text4 = text3;
         String text5 = text2;
         String text6 = text;
         ChatUtil.log("§a[Авто-парсер] §f" + text6 + " §7→ §a$" + text5 + " §8(мин $" + text4 + ", стр. " + value7 + ")");
         AutoBuyBans.m_0_void();
      } else if (itemUtil != null) {
         value3++;
         if (value3 < 2) {
            reset();
            Minecraft mc = Minecraft.getInstance();
            startSearch(mc, itemUtil);
            c3 = AuctionParser.ModeKind.WAIT_RESULTS;
            longValue = longValue3 + 500L;
            return;
         }

         ChatUtil.log("§e[Авто-парсер] §7Не найдено: §f" + itemUtil.getText());
      }

      m_6_void(longValue3);
   }

   private static void m_6_void(long longValue3) {
      value2++;
      value3 = 0;
      reset();
      c3 = AuctionParser.ModeKind.WAIT_BETWEEN;
      longValue = longValue3 + 400L;
   }

   private static void stopAndClose(Minecraft mc) {
      if (mc.player != null) {
         mc.player.closeContainer();
         if (mc.getConnection() != null) {
            mc.getConnection().sendCommand("ah");
         }
      }

      AuctionConfig.getConfig().setFlag(true);
      m_9_void(false);
      ChatUtil.log("§a[Авто-парсер] §7Готово. AutoBuy §aON");
   }

   private static void startSearch(Minecraft mc, ItemUtil itemUtil2) {
      if (mc != null && mc.getConnection() != null && itemUtil2 != null) {
         flag2 = false;
         value5 = 0;
         value4 = -1;
         longValue2 = System.currentTimeMillis();
         mc.getConnection().sendCommand("ah search " + getItemName(itemUtil2));
      }
   }

   private static String getItemName(ItemUtil itemUtil2) {
      String text = itemUtil2.getText2();
      if (text == null || text.isEmpty()) {
         text = itemUtil2.getText();
      }

      return cleanName(text);
   }

   public static String cleanName(String text) {
      if (text == null) {
         return "";
      }

      String text2 = text.replace("[★] ", "").replace("[★]", "").replace("xxx", "").trim();
      Matcher matcher = PATTERN2.matcher(text2);
      if (matcher.find()) {
         String text3 = matcher.group(1).trim().replace(" опыта", "").replace(" Опыта", "").trim();
         text2 = text3 + " с уровнем " + matcher.group(2);
      }

      return text2.trim();
   }

   private static int findFirstSlot(List<Slot> slotList) {
      if (slotList == null) {
         return -1;
      }

      int value6 = Math.min(slotList.size(), 54);

      for (int i = 0; i < value6; i++) {
         Slot slot = (Slot)slotList.get(i);
         if (slot != null && slot.hasItem()) {
            ItemStack itemStack = slot.getItem();
            if (itemStack.getItem() == Items.SPECTRAL_ARROW && m_8_boolean(itemStack)) {
               return slot.index;
            }
         }
      }

      for (int i2 = 0; i2 < value6; i2++) {
         Slot slot2 = (Slot)slotList.get(i2);
         if (slot2 != null && slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            Item item = itemStack2.getItem();
            if ((item == Items.SPECTRAL_ARROW || item == Items.ARROW || item == Items.TIPPED_ARROW) && m_8_boolean(itemStack2)) {
               return slot2.index;
            }
         }
      }

      return -1;
   }

   private static boolean m_8_boolean(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         String text = ItemUtil.stripFormatting(itemStack.getHoverName().getString()).toLowerCase(Locale.ROOT).replace('ё', 'е');
         return !text.contains("страниц") || !text.contains("след") && !text.contains("next") ? text.contains("следующ") || text.contains("следуйщ") : true;
      } else {
         return false;
      }
   }

   private static boolean m_7_boolean(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         Item item = itemStack.getItem();
         if (item == Items.SPECTRAL_ARROW || item == Items.ARROW || item == Items.TIPPED_ARROW) {
            String text = ItemUtil.stripFormatting(itemStack.getHoverName().getString()).toLowerCase(Locale.ROOT);
            if (text.contains("страниц")
               || text.contains("след")
               || text.contains("пред")
               || text.contains("page")
               || text.contains("назад")
               || text.contains("вперед")) {
               return true;
            }
         }

         return m_8_boolean(itemStack);
      } else {
         return false;
      }
   }

   private static int m_4(List<Slot> slotList, ItemUtil itemUtil2) {
      if (slotList != null && itemUtil2 != null) {
         int value6 = Integer.MAX_VALUE;

         for (int i = 0; i <= 44 && i < slotList.size(); i++) {
            Slot slot = (Slot)slotList.get(i);
            if (slot != null && !slot.getItem().isEmpty()) {
               ItemStack itemStack = slot.getItem();
               if (!m_7_boolean(itemStack) && (!PriceParser.m_6_boolean(itemStack) || !PriceParser.m_0_boolean(itemStack)) && itemUtil2.matches(itemStack)) {
                  int value7 = m_1_int(itemStack);
                  if (value7 > 0) {
                     int value8 = Math.max(1, itemStack.getCount());
                     int value9 = value7 / value8;
                     if (value9 > 0 && value9 < value6) {
                        value6 = value9;
                     }
                  }
               }
            }
         }

         return value6 == Integer.MAX_VALUE ? -1 : value6;
      } else {
         return -1;
      }
   }

   private static int m_1_int(ItemStack itemStack) {
      long longValue3 = PriceParser.getPriceLong(itemStack);
      if (longValue3 > 0L) {
         return (int)Math.min(2147483647L, longValue3);
      }

      ItemLore itemLore = itemStack.get(DataComponents.LORE);
      if (itemLore == null) {
         return -1;
      }

      for (Component component : itemLore.lines()) {
         String text = component.getString();
         if (text.contains("Цена:") || text.contains("Price:")) {
            int value6 = Math.max(text.indexOf("Цена:"), text.indexOf("Price:"));
            if (value6 >= 0) {
               String text2 = text.substring(value6).replaceAll("[^0-9]", "");
               if (!text2.isEmpty()) {
                  try {
                     int value7 = Integer.parseInt(text2);
                     if (value7 > 0) {
                        return value7;
                     }
                  } catch (NumberFormatException numberFormatException) {
                  }
               }
            }
         }

         Matcher matcher = PATTERN.matcher(text);
         if (matcher.find()) {
            for (int i = 1; i <= 3; i++) {
               if (matcher.group(i) != null) {
                  try {
                     int value8 = Integer.parseInt(matcher.group(i).replaceAll("[\\s,.]", ""));
                     if (value8 > 0) {
                        return value8;
                     }
                  } catch (NumberFormatException numberFormatException2) {
                  }
               }
            }
         }
      }

      return -1;
   }

   private static List<ItemUtil> getEnabledItems() {
      ArrayList list = new ArrayList();
      HashSet set = new HashSet();

      for (ItemUtil itemUtil2 : FunTimeItems.getItems()) {
         if (itemUtil2 != null && itemUtil2.isFlag3()) {
            String text = itemUtil2.getText().toLowerCase();
            if (set.add(text)) {
               list.add(itemUtil2);
            }
         }
      }

      return list;
   }

   private static String formatPrice(int value6) {
      String text = String.valueOf(value6);
      StringBuilder stringBuilder = new StringBuilder();
      int value7 = 0;

      for (int i = text.length() - 1; i >= 0; i--) {
         if (value7 > 0 && value7 % 3 == 0) {
            stringBuilder.insert(0, '.');
         }

         stringBuilder.insert(0, text.charAt(i));
         value7++;
      }

      return stringBuilder.toString();
   }

   private enum ModeKind {
      IDLE,
      SEND_SEARCH,
      WAIT_RESULTS,
      CLICK_NEXT,
      WAIT_NEXT_PAGE,
      WAIT_BETWEEN,
      FINISH_WAIT;

      private static String a(byte[] byteArray) {
         int value6 = 0;
         int value7;
         char[] charArray = new char[value7 = byteArray.length];

         for (int i = 0; i < value7; i++) {
            int value8;
            if ((value8 = 255 & byteArray[i]) < 192) {
               charArray[value6++] = (char)value8;
            } else if (value8 < 224) {
               char ch = (char)((char)(value8 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value6++] = ch;
            } else if (i < value7 - 2) {
               char ch2 = (char)((char)(value8 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value6++] = ch2;
            }
         }

         return new String(charArray, 0, value6);
      }
   }
}
