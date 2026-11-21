package wtf.onetap.module.misc;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.autobuy.PriceParser;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.gui.ClickGuiScreen;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Ah Helper", description = "Отображает самый выгодный предмет на аукционе", category = Category.MISC)
public class AhHelper extends Module {
   private final ColorSetting cheapItemColor = new ColorSetting("Дешевый предмет", -11796661).build();
   private final ColorSetting profitableItemColor = new ColorSetting("Выгодный предмет", -16776961).build();
   private final BooleanSetting ignoreThorns = new BooleanSetting("Игнорировать шипы", false).build();
   private final KeybindSetting ahSearch = new KeybindSetting("Поиск по /ah", -1).build();
   private final BooleanSetting pricePerItem = new BooleanSetting("Цена за 1шт", false).build();
   private int value = Integer.MIN_VALUE;
   private Set<Integer> integerSet = Set.of();
   private Set<Integer> integerSet2 = Set.of();
   private long longValue = Long.MIN_VALUE;
   private int value2 = Integer.MIN_VALUE;
   private boolean flag;
   private boolean flag2;

   @Override
   public void onEnable() {
      this.m_7_void();
   }

   @Override
   public void onDisable() {
      this.m_3_void();
      this.m_7_void();
   }

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (!ChatInputHelper.isTextInputOpen()) {
         if (event.getAction() == 1) {
            if (!ClickGuiScreen.isEditing()) {
               if ((Integer)this.ahSearch.getValue() != -1 && event.getKey() == (Integer)this.ahSearch.getValue()) {
                  if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.getConnection() != null) {
                     if (MinecraftAccess.MINECRAFT.gui.screen() == null) {
                        ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
                        if (!itemStack.isEmpty()) {
                           String text = m_2_String(itemStack);
                           if (text != null && !text.isBlank()) {
                              MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah search " + text);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof AbstractContainerScreen abstractContainerScreen)) {
         this.value = Integer.MIN_VALUE;
      } else {
         AbstractContainerMenu abstractContainerMenu = abstractContainerScreen.getMenu();
         if (abstractContainerMenu != null && abstractContainerMenu.slots != null) {
            int value3 = Math.min(54, abstractContainerMenu.slots.size());
            long longValue2 = PriceParser.m_7_long(abstractContainerMenu, value3);
            boolean flag3 = (Boolean)this.ignoreThorns.getValue();
            boolean flag4 = (Boolean)this.pricePerItem.getValue();
            if (this.value2 != abstractContainerMenu.containerId || this.longValue != longValue2 || this.flag != flag3 || this.flag2 != flag4) {
               if (isAuctionTitle(abstractContainerScreen.getTitle().getString())) {
                  this.value = abstractContainerMenu.containerId;
                  this.m_5(abstractContainerMenu.slots.subList(0, value3), flag3);
                  if (flag4 || this.flag2) {
                     PriceParser.m_7(abstractContainerMenu, value3, flag4);
                  }
               } else {
                  this.value = Integer.MIN_VALUE;
                  this.integerSet = Set.of();
                  this.integerSet2 = Set.of();
               }

               this.longValue = PriceParser.m_7_long(abstractContainerMenu, value3);
               this.value2 = abstractContainerMenu.containerId;
               this.flag = flag3;
               this.flag2 = flag4;
            }
         } else {
            this.value = Integer.MIN_VALUE;
         }
      }
   }

   public boolean isCurrentMenu(AbstractContainerMenu abstractContainerMenu) {
      return abstractContainerMenu != null && abstractContainerMenu.containerId == this.value;
   }

   public void m_9(GuiGraphicsExtractor guiGraphicsExtractor, AbstractContainerMenu abstractContainerMenu) {
      if (this.isCurrentMenu(abstractContainerMenu) && abstractContainerMenu.slots != null) {
         int value3 = pulseAlpha((Integer)this.cheapItemColor.getValue());
         int value4 = pulseAlpha((Integer)this.profitableItemColor.getValue());

         for (Slot slot : abstractContainerMenu.slots) {
            if (this.integerSet2.contains(slot.index)) {
               highlightSlot(guiGraphicsExtractor, slot, value4);
            } else if (this.integerSet.contains(slot.index)) {
               highlightSlot(guiGraphicsExtractor, slot, value3);
            }
         }
      }
   }

   private void m_5(List<Slot> slotList, boolean flag3) {
      this.integerSet = m_1(slotList.stream().filter(slot -> m_1(slot, flag3)), Comparator.comparingInt(AhHelper.DataRecord::m_4v_int), 1);
      this.integerSet2 = m_1(
         slotList.stream().filter(slot2 -> m_1(slot2, flag3)).filter(slot3 -> slot3.getItem().getCount() > 1), Comparator.comparingDouble(AhHelper.DataRecord::m_5_y), 4
      );
   }

   private static Set<Integer> m_1(Stream<Slot> stream, Comparator<AhHelper.DataRecord> comparator, int value3) {
      return stream.<AhHelper.DataRecord>map(slot -> {
         int value4 = PriceParser.m_8_int(slot.getItem());
         return new AhHelper.DataRecord(slot.index, value4, (double)value4 / Math.max(1, slot.getItem().getCount()));
      }).sorted(comparator).limit(value3).map(AhHelper.DataRecord::m_49_int).collect(Collectors.toUnmodifiableSet());
   }

   private static boolean m_1(Slot slot, boolean flag3) {
      if (slot != null && slot.hasItem()) {
         ItemStack itemStack = slot.getItem();
         if (m_4_boolean(itemStack) || PriceParser.m_8_int(itemStack) <= 0) {
            return false;
         } else {
            return flag3 && PriceParser.m_6_boolean(itemStack) ? PriceParser.m_0(itemStack, "mending") && !PriceParser.m_0_boolean(itemStack) : true;
         }
      } else {
         return false;
      }
   }

   private static boolean m_4_boolean(ItemStack itemStack) {
      ItemLore itemLore = itemStack.get(DataComponents.LORE);
      if (itemLore == null) {
         return false;
      }

      for (Component component : itemLore.lines()) {
         String text = normalize(component.getString());
         boolean flag3 = text.contains("причин") || text.contains("reason");
         boolean flag4 = text.contains("продано") || text.contains("sold");
         if (flag3 && flag4) {
            return true;
         }
      }

      return false;
   }

   private static void highlightSlot(GuiGraphicsExtractor guiGraphicsExtractor, Slot slot, int value3) {
      guiGraphicsExtractor.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, scaleAlpha(value3, 0.3F));
      guiGraphicsExtractor.outline(slot.x - 1, slot.y - 1, 18, 18, value3 | 0xFF000000);
   }

   private static int pulseAlpha(int value3) {
      float floatValue = (float)Math.abs(Math.sin(System.currentTimeMillis() / 3.0 * Math.PI / 180.0));
      return scaleAlpha(value3, floatValue);
   }

   private static int scaleAlpha(int value3, float floatValue) {
      int value4 = Math.round((value3 >>> 24 & 0xFF) * floatValue);
      return value4 << 24 | value3 & 16777215;
   }

   private void m_3_void() {
      if (MinecraftAccess.MINECRAFT.gui.screen() instanceof AbstractContainerScreen abstractContainerScreen && this.isCurrentMenu(abstractContainerScreen.getMenu())) {
         PriceParser.m_7(abstractContainerScreen.getMenu(), 54, false);
      }
   }

   private void m_7_void() {
      this.value = Integer.MIN_VALUE;
      this.integerSet = Set.of();
      this.integerSet2 = Set.of();
      this.longValue = Long.MIN_VALUE;
      this.value2 = Integer.MIN_VALUE;
      this.flag = false;
      this.flag2 = false;
   }

   public static String m_2_String(ItemStack itemStack) {
      if (itemStack.isEmpty()) {
         return null;
      }

      String text = PriceParser.m_5_String(cleanText(itemStack.getHoverName().getString()))
         .replaceAll("^\\[★]\\s*", "")
         .replaceAll("\\[★]", "")
         .replaceAll("xxx", "")
         .replaceAll("123", "")
         .replaceAll("^\\[.*?]\\s*", "")
         .trim()
         .replaceAll("\\s+", " ");
      return text.isEmpty() ? null : text;
   }

   public static String cleanText(String text) {
      if (text == null) {
         return "";
      }

      String text2 = ChatFormatting.stripFormatting(text);
      return text2 == null ? text.trim() : text2.trim();
   }

   public static String m_7_String(String text) {
      return cleanText(text).toLowerCase(Locale.ROOT);
   }

   public static boolean isAuctionTitle(String text) {
      String text2 = normalize(text);
      return text2.contains("аукцион")
         || text2.contains("auction")
         || text2.contains("поиск")
         || text2.contains("search")
         || text2.contains("п:")
         || text2.contains("0a2z");
   }

   private static String normalize(String text) {
      return text == null ? "" : text.toLowerCase(Locale.ROOT).replace(' ', ' ').trim();
   }

   private record DataRecord(int f_49, int f_4v, double f_5_y) {
      public int m_49_int() {
         return this.f_49;
      }

      public int m_4v_int() {
         return this.f_4v;
      }

      public double m_5_y() {
         return this.f_5_y;
      }
   }
}
