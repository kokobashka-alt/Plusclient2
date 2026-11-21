package wtf.onetap.module.player;

import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "ItemFix", description = "Защищает перенос предметов с курсора", category = Category.PLAYER)
public class ItemFix extends Module {
   private final MultiModeSetting components = new MultiModeSetting("Компоненты", "Слоты", "Слоты", "Элитры", "Нагрудник", "Шар").build();

   public ItemFix() {
      this.components.setValue(new LinkedHashSet<>(List.of("Слоты", "Элитры", "Нагрудник", "Шар")));
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen)
            && !(MinecraftAccess.MINECRAFT.gui.screen() instanceof CreativeModeInventoryScreen)
            && !(MinecraftAccess.MINECRAFT.gui.screen() instanceof ShulkerBoxScreen)) {
            ItemStack itemStack = MinecraftAccess.MINECRAFT.player.containerMenu.getCarried();
            if (!itemStack.isEmpty()) {
               int value = this.findEmptySlot();
               boolean flag = value != -1;
               if (this.components.isSelected("Элитры") && this.findItemSlot(Items.ELYTRA) == -1 && flag && itemStack.is(Items.ELYTRA)) {
                  InventoryUtil.clickSlot(MinecraftAccess.MINECRAFT.player.containerMenu.containerId, value, 0, ContainerInput.PICKUP);
               } else if (this.components.isSelected("Нагрудник") && this.m_8_int() == -1 && flag && this.isChestplate(itemStack.getItem())) {
                  InventoryUtil.clickSlot(MinecraftAccess.MINECRAFT.player.containerMenu.containerId, value, 0, ContainerInput.PICKUP);
               } else {
                  if (this.components.isSelected("Шар") && this.findItemSlot(Items.PLAYER_HEAD) == -1 && flag && itemStack.is(Items.PLAYER_HEAD)) {
                     InventoryUtil.clickSlot(MinecraftAccess.MINECRAFT.player.containerMenu.containerId, value, 0, ContainerInput.PICKUP);
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.getConnection() != null && this.components.isSelected("Слоты")) {
         if (event.getPacket() instanceof ClientboundSetHeldSlotPacket) {
            MinecraftAccess.MINECRAFT.getConnection().send(new ServerboundSetCarriedItemPacket(MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot()));
            event.cancel();
         }
      }
   }

   private int findItemSlot(Item item) {
      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty() && itemStack.is(item)) {
            return i;
         }
      }

      return -1;
   }

   private int m_8_int() {
      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty() && this.isChestplate(itemStack.getItem())) {
            return i;
         }
      }

      return -1;
   }

   private boolean isChestplate(Item item) {
      return item == Items.NETHERITE_CHESTPLATE
         || item == Items.DIAMOND_CHESTPLATE
         || item == Items.IRON_CHESTPLATE
         || item == Items.CHAINMAIL_CHESTPLATE
         || item == Items.GOLDEN_CHESTPLATE
         || item == Items.LEATHER_CHESTPLATE;
   }

   private int findEmptySlot() {
      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).isEmpty()) {
            return i < 9 ? i + 36 : i;
         }
      }

      return -1;
   }
}
