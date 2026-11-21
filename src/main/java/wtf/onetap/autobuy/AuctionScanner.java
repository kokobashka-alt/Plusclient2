package wtf.onetap.autobuy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.TimeHelper;

public class AuctionScanner implements MinecraftAccess {
   private static final Pattern PATTERN = Pattern.compile("(?:\\$\\s*([\\d,.\\s]+)|([\\d,.\\s]+)\\s*\\$|(?:Цена|Price)[:\\s]*([\\d,.\\s]+))", 66);
   private static final Pattern PATTERN2 = Pattern.compile("(?i)(?:Продавец|Seller)[:\\s]+(.+)");
   private static final Pattern PATTERN3 = Pattern.compile("(?i)монет[аыи]?\\s*:?\\s*\\$?\\s*([\\d\\s.,]+)");
   private final Random random;
   private final TimeHelper timeHelper;
   private final Set<String> stringSet;
   private final Map<String, Long> stringMap;
   private final Map<String, AuctionScanner.DataRecord> stringMap2;
   private long longValue;
   private long longValue2;
   private static boolean flag;

   public AuctionScanner() {
      this.random = new Random();
      this.timeHelper = new TimeHelper();
      this.stringSet = ConcurrentHashMap.newKeySet();
      this.stringMap = new ConcurrentHashMap<>();
      this.stringMap2 = new ConcurrentHashMap<>();
      this.longValue = System.currentTimeMillis();
      this.longValue2 = this.m_6_long();
   }

   public void m_0_void() {
      this.stringSet.clear();
      this.stringMap.clear();
      this.timeHelper.reset();
      this.longValue2 = this.m_6_long();
      this.longValue = System.currentTimeMillis();
   }

   public void m_6_void() {
      this.m_0_void();
      this.stringMap2.clear();
   }

   public void m_7(int value, List<Slot> slotList, List<ItemUtil> itemUtilList) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (itemUtilList != null && !itemUtilList.isEmpty()) {
            this.m_9_void();
            this.m_3_void();
            this.m_8(value, slotList, itemUtilList);
            if (this.timeHelper.hasElapsed(this.longValue2)) {
               this.m_6_void(value);
               this.longValue2 = this.m_6_long();
               this.timeHelper.reset();
            }
         }
      }
   }

   private long m_6_long() {
      return 551L + (long)(this.random.nextDouble() * 99.0);
   }

   public void m_6_void(int value) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         InventoryUtil.clickSlot(value, 49, 0, ContainerInput.QUICK_MOVE);
         this.stringSet.clear();
         this.stringMap.clear();
      }
   }

   private boolean m_8(int value, List<Slot> slotList, List<ItemUtil> itemUtilList) {
      HashMap map = new HashMap();
      ArrayList list = new ArrayList();

      for (ItemUtil itemUtil : itemUtilList) {
         if (itemUtil != null && itemUtil.isFlag2()) {
            ((java.util.List)map.computeIfAbsent(itemUtil.getItem(), object -> new ArrayList<>())).add(itemUtil);
            if (itemUtil.getItem() == Items.PLAYER_HEAD) {
               list.add(itemUtil);
            }
         }
      }

      long longValue3 = m_5_long();

      label145:
      for (int i = 40; i <= 44 && i < slotList.size(); i++) {
         Slot slot = (Slot)slotList.get(i);
         if (slot != null && !slot.getItem().isEmpty()) {
            ItemStack itemStack = slot.getItem();
            String text = ItemUtil.stripFormatting(itemStack.getHoverName().getString());
            if (!PriceParser.m_6_boolean(itemStack) || !PriceParser.m_0_boolean(itemStack)) {
               int value2 = this.m_1_int(itemStack);
               if (value2 > 0 && (longValue3 < 0L || value2 <= longValue3)) {
                  String text2 = text + "|" + value2 + "|" + i;
                  if (!this.stringSet.contains(text2)) {
                     Item item = itemStack.getItem();
                     List list2 = (List)map.get(item);
                     if ((list2 == null || list2.isEmpty()) && item == Items.PLAYER_HEAD) {
                        list2 = list;
                     }

                     if (list2 != null && !list2.isEmpty()) {
                        var iterator = list2.iterator();

                        while (true) {
                           if (!iterator.hasNext()) {
                              continue label145;
                           }

                           ItemUtil itemUtil2 = (ItemUtil)iterator.next();
                           if (itemUtil2 != null && itemUtil2.isFlag2() && itemUtil2.matches(itemStack)) {
                              int value3 = Math.max(1, itemStack.getCount());
                              int value4 = value2 / value3;
                              int value5 = itemUtil2.getValue();
                              if (value5 > 0 && value4 <= value5 && (!itemUtil2.isStackable() || value3 >= itemUtil2.getValue2()) && (longValue3 < 0L || value2 <= longValue3)) {
                                 String text3 = this.m_5_String(itemStack);
                                 if (text3 == null || text3.isEmpty()) {
                                    break;
                                 }

                                 String text4 = ItemUtil.stripFormatting(MinecraftAccess.MINECRAFT.player.getName().getString());
                                 if (!text4.equalsIgnoreCase(ItemUtil.stripFormatting(text3)) && !TextSanitizer.isBlacklisted(text3)) {
                                    break;
                                 }
                              }
                           }
                        }

                        this.stringMap2.put(text.toLowerCase().trim(), new AuctionScanner.DataRecord(itemStack.copy(), text, value2));
                        int value6 = slot.index;
                        if (value6 == 0 || value6 != i) {
                           value6 = i;
                        }

                        InventoryUtil.clickSlot(value, value6, 0, ContainerInput.QUICK_MOVE);
                        this.stringSet.add(text2);
                        this.stringMap.put(text2, System.currentTimeMillis());
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   public static long m_5_long() {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return -1L;
      }

      try {
         Scoreboard scoreboard = MinecraftAccess.MINECRAFT.level.getScoreboard();
         Objective objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
         if (objective == null) {
            return -1L;
         }

         long longValue3 = -1L;

         for (PlayerScoreEntry playerScoreEntry : scoreboard.listPlayerScores(objective)) {
            if (playerScoreEntry != null && !playerScoreEntry.isHidden()) {
               String text = m_3(scoreboard, playerScoreEntry);
               long longValue4 = m_3_long(text);
               if (longValue4 > longValue3) {
                  longValue3 = longValue4;
               }
            }
         }

         long longValue5 = m_3_long(objective.getDisplayName().getString());
         if (longValue5 > longValue3) {
            longValue3 = longValue5;
         }

         return longValue3;
      } catch (Throwable e) {
         return -1L;
      }
   }

   private static String m_3(Scoreboard scoreboard, PlayerScoreEntry playerScoreEntry) {
      StringBuilder stringBuilder = new StringBuilder();
      PlayerTeam playerTeam = scoreboard.getPlayersTeam(playerScoreEntry.owner());
      if (playerTeam != null) {
         if (playerTeam.getPlayerPrefix() != null) {
            stringBuilder.append(playerTeam.getPlayerPrefix().getString());
         }

         if (playerTeam.getPlayerSuffix() != null) {
            stringBuilder.append(playerTeam.getPlayerSuffix().getString());
         }
      }

      if (playerScoreEntry.display() != null) {
         stringBuilder.append(playerScoreEntry.display().getString());
      }

      stringBuilder.append(' ').append(playerScoreEntry.owner());
      stringBuilder.append(' ').append(playerScoreEntry.value());
      String text = stringBuilder.toString();
      String text2 = ChatFormatting.stripFormatting(text);
      return text2 != null ? text2 : text;
   }

   private static long m_3_long(String text) {
      if (text != null && !text.isBlank()) {
         String text2 = text.replace('ё', 'е');
         Matcher matcher = PATTERN3.matcher(text2);
         if (!matcher.find()) {
            String text3 = text2.toLowerCase(Locale.ROOT);
            int value = text3.indexOf("монет");
            if (value < 0) {
               return -1L;
            }

            String text4 = text2.substring(value);
            Matcher matcher2 = Pattern.compile("([\\d][\\d\\s.,]*)").matcher(text4);
            return !matcher2.find() ? -1L : m_4_long(matcher2.group(1));
         } else {
            return m_4_long(matcher.group(1));
         }
      } else {
         return -1L;
      }
   }

   private static long m_4_long(String text) {
      if (text == null) {
         return -1L;
      }

      String text2 = text.replaceAll("[\\s,.]", "");
      if (text2.isEmpty()) {
         return -1L;
      }

      try {
         return Long.parseLong(text2);
      } catch (NumberFormatException numberFormatException) {
         return -1L;
      }
   }

   private int m_1_int(ItemStack itemStack) {
      long longValue3 = PriceParser.getPriceLong(itemStack);
      if (longValue3 > 0L) {
         return (int)Math.min(2147483647L, longValue3);
      }

      ItemLore itemLore = itemStack.get(DataComponents.LORE);
      if (itemLore != null) {
         for (Component component : itemLore.lines()) {
            String text = component.getString();
            if (text.contains("Цена:") || text.contains("Price:")) {
               int value = Math.max(text.indexOf("Цена:"), text.indexOf("Price:"));
               if (value >= 0) {
                  String text2 = text.substring(value).replaceAll("[^0-9]", "");
                  if (!text2.isEmpty()) {
                     try {
                        int value2 = Integer.parseInt(text2);
                        if (value2 > 0) {
                           return value2;
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
                     String text3 = matcher.group(i).replaceAll("[\\s,.]", "");

                     try {
                        int value3 = Integer.parseInt(text3);
                        if (value3 > 0) {
                           return value3;
                        }
                     } catch (NumberFormatException numberFormatException2) {
                     }
                  }
               }
            }
         }
      }

      return -1;
   }

   private String m_5_String(ItemStack itemStack) {
      ItemLore itemLore = itemStack.get(DataComponents.LORE);
      if (itemLore == null) {
         return null;
      }

      for (Component component : itemLore.lines()) {
         Matcher matcher = PATTERN2.matcher(component.getString());
         if (matcher.find()) {
            String text = ItemUtil.stripFormatting(matcher.group(1).trim());
            if (!text.isEmpty()) {
               return text;
            }
         }
      }

      return null;
   }

   private void m_9_void() {
      long longValue3 = System.currentTimeMillis();
      if (longValue3 - this.longValue > 30000L) {
         this.stringSet.removeIf(text -> {
            Long longObj = this.stringMap.get(text);
            if (longObj != null && longValue3 - longObj > 30000L) {
               this.stringMap.remove(text);
               return true;
            } else {
               return false;
            }
         });
         this.longValue = longValue3;
      }
   }

   private void m_3_void() {
      long longValue3 = System.currentTimeMillis();
      this.stringMap2.entrySet().removeIf(entry -> longValue3 - entry.getValue().f_6m > 10000L);
   }

   public void m_9_void(String text, int value) {
      String text2 = ItemUtil.stripFormatting(text).toLowerCase().trim();
      int value2 = value;
      AuctionScanner.DataRecord c6 = null;
      String text3 = null;

      for (Entry entry : this.stringMap2.entrySet()) {
         AuctionScanner.DataRecord c62 = (AuctionScanner.DataRecord)entry.getValue();
         if (c62.m_3_d() == value) {
            c6 = c62;
            text3 = (String)entry.getKey();
            break;
         }
      }

      if (c6 == null && !text2.isEmpty()) {
         for (Entry entry2 : this.stringMap2.entrySet()) {
            String text4 = ((AuctionScanner.DataRecord)entry2.getValue()).m_2_j_String().toLowerCase();
            if (text4.contains(text2) || text2.contains(text4) || ((String)entry2.getKey()).contains(text2)) {
               c6 = (AuctionScanner.DataRecord)entry2.getValue();
               text3 = (String)entry2.getKey();
               break;
            }
         }
      }

      if (c6 == null) {
         long longValue3 = 0L;

         for (Entry entry3 : this.stringMap2.entrySet()) {
            if (((AuctionScanner.DataRecord)entry3.getValue()).m_6m_long() > longValue3 && System.currentTimeMillis() - ((AuctionScanner.DataRecord)entry3.getValue()).m_6m_long() < 15000L) {
               longValue3 = ((AuctionScanner.DataRecord)entry3.getValue()).m_6m_long();
               c6 = (AuctionScanner.DataRecord)entry3.getValue();
               text3 = (String)entry3.getKey();
            }
         }
      }

      if (c6 != null) {
         if (value2 <= 0) {
            value2 = c6.m_3_d();
         }

         ItemEntry.m_9_C7x().m_4(c6.m_1k_ItemStack(), c6.m_2_j_String(), value2);
         this.stringMap2.remove(text3);
      } else {
         if (value2 > 0) {
            ItemEntry.m_9_C7x().m_9_void(text, value2);
         }
      }
   }

   static {
      m_2_void(false);
   }

   public static void m_2_void(boolean flag2) {
      flag = flag2;
   }

   public static boolean m_2_boolean() {
      return flag;
   }

   public static boolean m_5_boolean() {
      return true;
   }

   public record DataRecord(ItemStack f_1k, String f_2_j, int f_3_d, long f_6m) {
      public DataRecord(ItemStack itemStack, String text, int value) {
         this(itemStack, text, value, System.currentTimeMillis());
      }

      public ItemStack m_1k_ItemStack() {
         return this.f_1k;
      }

      public String m_2_j_String() {
         return this.f_2_j;
      }

      public int m_3_d() {
         return this.f_3_d;
      }

      public long m_6m_long() {
         return this.f_6m;
      }
   }
}
