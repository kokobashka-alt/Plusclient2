package wtf.onetap.module.player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.client.MinecraftAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "AutoEat", description = "Ест, когда проголодался, и возвращает слот", category = Category.PLAYER)
public class AutoEat extends Module {
   private static final Set<Item> ITEM_SET = Set.of(
      Items.GOLDEN_APPLE,
      Items.ENCHANTED_GOLDEN_APPLE,
      Items.CHORUS_FRUIT,
      Items.ROTTEN_FLESH,
      Items.SPIDER_EYE,
      Items.POISONOUS_POTATO,
      Items.PUFFERFISH,
      Items.SUSPICIOUS_STEW
   );
   private final FloatSetting eatBelowHunger = new FloatSetting("Есть ниже голода", 18.0F, 1.0F, 20.0F, 1.0F).build();
   private final BooleanSetting fromInventory = new BooleanSetting("Из инвентаря", true).build();
   private final Deque<int[]> deque = new ArrayDeque<>();
   private boolean flag;
   private boolean flag2;
   private int value = -1;

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (MinecraftAccess.MINECRAFT.player.getFoodData().getFoodLevel() >= this.eatBelowHunger.getInt()) {
            this.flag2 = false;
            if (this.flag) {
               this.m_4_void();
            }
         } else if (MinecraftAccess.MINECRAFT.gui.screen() == null && (!MinecraftAccess.MINECRAFT.player.isUsingItem() || isFood(MinecraftAccess.MINECRAFT.player.getUseItem()))) {
            if (!isFood(MinecraftAccess.MINECRAFT.player.getMainHandItem())) {
               int value2 = InventoryUtil.findSlotInRange(AutoEat::isFood, 0, 9);
               if (value2 == -1 && (Boolean)this.fromInventory.getValue()) {
                  int value3 = InventoryUtil.findSlotInRange(AutoEat::isFood, 9, 36);
                  if (value3 != -1) {
                     if (this.canSwap()) {
                        int value4 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
                        this.swapSlots(value3, value4);
                        this.deque.push(new int[]{value3, value4});
                        this.flag = true;
                     }

                     return;
                  }
               }

               if (value2 == -1) {
                  if (!this.flag2) {
                     ChatUtil.log("AutoEat: нет еды");
                  }

                  this.flag2 = true;
                  if (this.flag) {
                     this.m_4_void();
                  }

                  return;
               }

               if (this.value == -1) {
                  this.value = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
               }

               InventoryUtil.selectHotbarSlot(value2);
            }

            this.flag = true;
            MinecraftAccess.MINECRAFT.options.keyUse.setDown(true);
            if (!MinecraftAccess.MINECRAFT.player.isUsingItem()) {
               ((MinecraftAccessor)MinecraftAccess.MINECRAFT).onetap$startUseItem();
            }
         }
      }
   }

   private void m_4_void() {
      this.flag = false;
      MinecraftAccess.MINECRAFT.options.keyUse.setDown(InventoryUtil.m_9_boolean());
      if (this.value != -1) {
         InventoryUtil.selectHotbarSlot(this.value);
      }

      this.value = -1;
      if (this.canSwap()) {
         while (!this.deque.isEmpty()) {
            int[] intArray = this.deque.pop();
            this.swapSlots(intArray[0], intArray[1]);
         }
      }

      this.deque.clear();
   }

   private void swapSlots(int value2, int value3) {
      InventoryUtil.m_6(() -> InventoryUtil.clickSlot(MinecraftAccess.MINECRAFT.player.containerMenu.containerId, value2, value3, ContainerInput.SWAP));
   }

   private boolean canSwap() {
      return MinecraftAccess.MINECRAFT.player.containerMenu == MinecraftAccess.MINECRAFT.player.inventoryMenu && !InventoryUtil.isBusy();
   }

   private static boolean isFood(ItemStack itemStack) {
      return itemStack.has(DataComponents.FOOD) && !ITEM_SET.contains(itemStack.getItem());
   }

   @Override
   public void onDisable() {
      if (this.flag && MinecraftAccess.MINECRAFT.player != null) {
         this.m_4_void();
      }

      this.flag = false;
      this.flag2 = false;
      this.value = -1;
      this.deque.clear();
   }
}
