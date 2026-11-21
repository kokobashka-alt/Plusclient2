package wtf.onetap.module.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.autobuy.FunTimeItemPresets;
import wtf.onetap.autobuy.ItemKey;
import wtf.onetap.autobuy.ItemMatcher;
import wtf.onetap.autobuy.PriceParser;
import wtf.onetap.config.CollectorConfig;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.gui.ClickGuiScreen;
import wtf.onetap.gui.FunTimeCollectorScreen;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Collector", description = "Автоматически собирает нужный инвентарь на FunTime", category = Category.MISC)
public class Collector extends Module {
   private static final Pattern PATTERN = Pattern.compile("(\\d+)/(\\d+)");
   private final KeybindSetting openEditor = new KeybindSetting("Открыть редактор", -1).build();
   private final List<Collector.DataRecord> c0List = new ArrayList<>();
   private final Collector.StateHolder c7 = new Collector.StateHolder();
   private final Collector.StateHolder c72 = new Collector.StateHolder();
   private ItemKey itemKey;
   private Collector.DataRecord c0;
   private int value;

   @Override
   public void onEnable() {
      CollectorConfig.m_8_void();
      ChatUtil.log("Модуль ищет самые дешевые лоты среди тех которые есть, имейте это ввиду, и будьте осторожны!");
   }

   @Override
   public void onDisable() {
      this.itemKey = null;
      CollectorConfig.m_5_void();
   }

   public void m_4_void() {
      if (!this.isEnabled()) {
         this.toggle();
      }

      this.cycleItem();
   }

   private void cycleItem() {
      var itemKeyList = FunTimeItemPresets.getKeys();
      int value2 = this.itemKey == null ? 0 : itemKeyList.indexOf(this.itemKey) + 1;
      this.c0 = null;
      this.c0List.clear();
      this.itemKey = null;

      for (int i = value2; i < itemKeyList.size(); i++) {
         ItemKey itemKey2 = (ItemKey)itemKeyList.get(i);
         if (itemKey2.isFlag() && this.m_0(itemKey2) < m_8(itemKey2, false)) {
            this.itemKey = itemKey2;
            break;
         }
      }

      if (this.itemKey != null) {
         ChatUtil.log("Переходим к сбору предмета: " + this.itemKey.getText());
      } else {
         ChatUtil.log("Все предметы собраны, работа завершена");
      }
   }

   public int getOpenEditor() {
      return (Integer)this.openEditor.getValue();
   }

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (event.getAction() == 1
         && !ChatInputHelper.isTextInputOpen()
         && (Integer)this.openEditor.getValue() != -1
         && event.getKey() == (Integer)this.openEditor.getValue()
         && MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.gui.setScreen(FunTimeCollectorScreen.m_7_C74());
         event.cancel();
      } else if (!ClickGuiScreen.isEditing()) {
         if (this.itemKey != null && MinecraftAccess.MINECRAFT.gui.screen() instanceof AbstractContainerScreen) {
            event.cancel();
         }

         if (event.getKey() == 256 && this.itemKey != null) {
            this.itemKey = null;
            ChatUtil.log("Работа модуля была принудительно завершена");
         }
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (this.itemKey != null && MinecraftAccess.MINECRAFT.gui.screen() instanceof AbstractContainerScreen) {
         event.setForward(0.0F);
         event.setStrafe(0.0F);
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.getConnection() != null) {
         if (this.itemKey != null) {
            if (MinecraftAccess.MINECRAFT.player.getInventory().getFreeSlot() == -1) {
               ChatUtil.log("Автоматическое отключение: нет свободных слотов в инвентаре, освободите место");
               this.closeContainer();
               this.toggle();
               return;
            }

            this.value++;
            if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen) && MinecraftAccess.MINECRAFT.player.tickCount >= 200 && this.c7.m_6(1000L, 300L)) {
               MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah search " + this.itemKey.getText());
               this.c7.m_6_void();
               this.value = 0;
            }
         }

         this.m_7_void();
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (this.itemKey != null) {
         if (event.getPacket() instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
            String text = clientboundSystemChatPacket.content().getString();
            if (text.toLowerCase().contains("[✘] ошибка! этот товар уже купили!")) {
               this.c0 = null;
               this.c0List.clear();
               return;
            }

            if (text.contains("[✘] Ошибка! У Вас не хватает Монет!")) {
               ChatUtil.log("Автоматическое отключение из-за нехватки баланса на аккаунте");
               this.toggle();
               return;
            }

            if (text.contains("Данная команда недоступна в режиме AFK")) {
               ChatUtil.log("Выйдите из режима AFK, чтобы Collector продолжил работу");
            }
         }

         if (event.getPacket() instanceof ClientboundOpenScreenPacket && !(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen)) {
            if (this.value >= 8) {
               ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
               int value2 = threadLocalRandom.nextBoolean() ? threadLocalRandom.nextInt(205, 231) : threadLocalRandom.nextInt(305, 325);
               MinecraftAccess.MINECRAFT.getConnection().sendChat("/an" + value2);
               ChatUtil.log("Обнаружили замедление аукциона, переходим на " + value2 + " анархию");
            }

            this.c72.m_6_void();
         }
      }
   }

   private void m_7_void() {
      if (this.itemKey != null && MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen) {
         String text = containerScreen.getTitle().getString().replaceAll("§.", "").toLowerCase().trim();
         int value2 = ((ChestMenu)containerScreen.getMenu()).containerId;
         NonNullList nonNullList = ((ChestMenu)containerScreen.getMenu()).slots;
         if (text.contains(this.itemKey.getText().toLowerCase())) {
            if (this.c7.m_6(300L, 80L) && this.m_0(this.itemKey) >= m_8(this.itemKey, false)) {
               ChatUtil.log("Предмет " + this.itemKey.getText() + " приобретен, перехожу к следующему");
               this.cycleItem();
               this.closeContainer();
            } else if (this.c72.m_6(1000L, 150L)) {
               if (this.c7.m_6(this.itemKey.isFlag2() ? 400L : 550L, 120L)) {
                  if (this.itemKey.isFlag2()) {
                     this.m_6(text, value2, nonNullList);
                  } else {
                     this.m_7(text, value2, nonNullList);
                  }

                  this.c7.m_6_void();
               }
            }
         } else if (!text.contains("подтверждение покупки") && !text.contains("подозрительная цена!") && !text.contains("подозрительная цена: ")) {
            this.closeContainer();
         } else {
            if (this.c7.m_6(200L, 90L)) {
               this.m_6(value2, nonNullList, "[Кyпить]");
               this.c7.m_6_void();
            }
         }
      }
   }

   private void m_6(String text, int value2, List<Slot> slotList) {
      Matcher matcher = PATTERN.matcher(text);
      if (matcher.find()) {
         int value3 = Integer.parseInt(matcher.group(1));
         int value4 = Math.min(4, Integer.parseInt(matcher.group(2)));
         if (this.c0 == null) {
            if (this.c0List.stream().noneMatch(c02 -> c02.m_82_int() == value3)) {
               slotList.stream()
                  .filter(slot -> this.m_3_boolean(slot.getItem()))
                  .forEach(slot2 -> this.c0List.add(new Collector.DataRecord(value3, slot2.index, PriceParser.getPriceInt(slot2.getItem()))));
            }

            if (value3 < value4) {
               this.m_6(value2, slotList, "следующая страница");
            } else {
               this.c0 = this.c0List.stream().min(Comparator.comparingInt(Collector.DataRecord::m_8_n_int)).orElse(null);
               if (this.c0 == null) {
                  this.closeContainer();
               }
            }
         } else if (value3 == this.c0.m_82_int()) {
            Slot slot3 = slotList.stream()
               .filter(slot4 -> this.m_3_boolean(slot4.getItem()) && PriceParser.getPriceInt(slot4.getItem()) == this.c0.m_8_n_int())
               .findFirst()
               .orElse(null);
            if (slot3 == null) {
               slot3 = slotList.stream()
                  .filter(slot5 -> this.m_3_boolean(slot5.getItem()))
                  .min(Comparator.comparingInt(slot6 -> PriceParser.getPriceInt(slot6.getItem())))
                  .orElse(null);
            }

            if (slot3 != null) {
               InventoryUtil.clickSlot(value2, slot3.index, 0, ContainerInput.QUICK_MOVE);
               this.c7.m_6_void();
            } else {
               ChatUtil.log("Оффер пропал, пересканирую");
               this.c0 = null;
               this.c0List.clear();
               this.closeContainer();
            }
         } else {
            this.m_6(value2, slotList, value3 < this.c0.m_82_int() ? "следующая страница" : "предыдущая страница");
         }
      }
   }

   private void m_7(String text, int value2, List<Slot> slotList) {
      var slotList2 = slotList.stream().filter(slot -> this.m_3_boolean(slot.getItem())).toList();
      int value3 = slotList2.stream().mapToInt(slot2 -> PriceParser.getPriceInt(slot2.getItem())).min().orElse(0);
      List list = slotList2.stream()
         .filter(slot3 -> PriceParser.getPriceInt(slot3.getItem()) <= Math.round(value3 * 2))
         .filter(slot4 -> slot4.getItem().getCount() <= m_8(this.itemKey, true) - this.m_0(this.itemKey))
         .toList();
      Slot slot5 = (Slot)list.stream()
         .filter(object -> ((net.minecraft.world.inventory.Slot)object).getItem().getCount() >= m_8(this.itemKey, false) - this.m_0(this.itemKey))
         .min(Comparator.comparingInt(object2 -> PriceParser.getPriceInt(((net.minecraft.world.inventory.Slot)object2).getItem())))
         .orElse(null);
      if (slot5 == null) {
         slot5 = (Slot)list.stream().min(Comparator.comparingInt(object3 -> PriceParser.getPriceInt(((net.minecraft.world.inventory.Slot)object3).getItem()))).orElse(null);
      }

      if (slot5 != null) {
         InventoryUtil.clickSlot(value2, slot5.index, 0, ContainerInput.QUICK_MOVE);
         this.c7.m_6_void();
      } else {
         Matcher matcher = PATTERN.matcher(text);
         if (matcher.find()) {
            int value4 = Integer.parseInt(matcher.group(1));
            int value5 = Integer.parseInt(matcher.group(2));
            if (value5 == 1) {
               ChatUtil.log("Пропускаем предмет " + this.itemKey.getText() + ", ибо нету подходящего");
               this.cycleItem();
               this.closeContainer();
            } else {
               this.m_6(value2, slotList, value4 == 1 ? "следующая страница" : "предыдущая страница");
            }
         }
      }
   }

   private void m_6(int value2, List<Slot> slotList, String text) {
      String text2 = text.toLowerCase();
      slotList.stream()
         .filter(slot -> slot.getItem().getHoverName().getString().toLowerCase().contains(text2))
         .findFirst()
         .ifPresent(slot2 -> InventoryUtil.clickSlot(value2, slot2.index, 0, ContainerInput.QUICK_MOVE));
   }

   private void closeContainer() {
      if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
      }
   }

   private boolean m_3_boolean(ItemStack itemStack) {
      if (!itemStack.isEmpty() && this.itemKey.m_4_boolean(itemStack)) {
         Inventory inventory = MinecraftAccess.MINECRAFT.player.getInventory();
         ItemStack itemStack2 = ItemStack.EMPTY;

         for (int i = 0; i < 40; i++) {
            ItemStack itemStack3 = inventory.getItem(i);
            if (!itemStack3.isEmpty() && this.itemKey.m_4_boolean(itemStack3)) {
               itemStack2 = itemStack3;
               break;
            }
         }

         if (itemStack2.isEmpty()
            || (
                  !(this.itemKey.getItem() instanceof PotionItem)
                     || Objects.equals(itemStack.get(DataComponents.POTION_CONTENTS), itemStack2.get(DataComponents.POTION_CONTENTS))
               )
               && itemStack.getHoverName().getString().trim().equalsIgnoreCase(itemStack2.getHoverName().getString().trim())) {
            if (this.m_8(itemStack, "➥ Нажмите, чтобы забрать") || PriceParser.getPriceInt(itemStack) <= 0) {
               return false;
            } else if (this.itemKey.getItem() == Items.TOTEM_OF_UNDYING && itemStack.hasFoil()) {
               return false;
            } else if (this.itemKey.getItem() == Items.ELYTRA && itemStack.get(DataComponents.DAMAGE) != null) {
               return (432 - itemStack.get(DataComponents.DAMAGE)) / 432.0F >= 0.5F;
            } else if (PriceParser.m_6_boolean(itemStack) && itemStack.get(DataComponents.DAMAGE) != null && itemStack.getMaxDamage() > 0) {
               boolean flag = this.itemKey
                  .getItemMatcherList()
                  .stream()
                  .anyMatch(itemMatcher -> itemMatcher.isActive() && !itemMatcher.isDeny() && itemMatcher.getText().equals(Enchantments.MENDING.identifier().toString()));
               return flag || (float)(itemStack.getMaxDamage() - itemStack.get(DataComponents.DAMAGE)) / itemStack.getMaxDamage() >= 0.7F;
            } else {
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean m_8(ItemStack itemStack, String text) {
      return MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null
         ? itemStack.getTooltipLines(TooltipContext.of(MinecraftAccess.MINECRAFT.level), MinecraftAccess.MINECRAFT.player, TooltipFlag.NORMAL)
            .stream()
            .anyMatch(component -> component.getString().contains(text))
         : false;
   }

   private static int m_8(ItemKey itemKey2, boolean flag) {
      return Math.max(1, flag ? itemKey2.getValue() + Math.round(itemKey2.getValue() * 0.2F) : itemKey2.getValue());
   }

   private int m_0(ItemKey itemKey2) {
      int value2 = 0;

      for (int i = 0; i < 40; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty() && itemKey2.m_4_boolean(itemStack)) {
            value2 += itemStack.getCount();
         }
      }

      return value2;
   }

   private record DataRecord(int f_82, int f_81, int f_8_n) {
      public int m_82_int() {
         return this.f_82;
      }

      public int m_81() {
         return this.f_81;
      }

      public int m_8_n_int() {
         return this.f_8_n;
      }
   }

   private static final class StateHolder {
      private long longValue;
      private long longValue2;

      private StateHolder() {
         this.longValue = System.currentTimeMillis();
         this.longValue2 = m_8_long();
      }

      public boolean m_6(long longValue3, long longValue4) {
         return System.currentTimeMillis() - (longValue3 + this.longValue2 % (longValue4 + 1L)) >= this.longValue;
      }

      public void m_6_void() {
         this.longValue = System.currentTimeMillis();
         this.longValue2 = m_8_long();
      }

      private static long m_8_long() {
         return (long)(Math.random() * 9.223372E18F);
      }
   }
}
