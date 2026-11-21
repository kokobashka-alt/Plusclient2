package wtf.onetap.module.player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.client.MinecraftAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "PotionSwap", description = "Пьёт зелья подряд, пока зажата кнопка, и возвращает слот", category = Category.PLAYER)
public class PotionSwap extends Module {
   private final BooleanSetting fromInventory = new BooleanSetting("Из инвентаря", true).build();
   private final Set<Holder<MobEffect>> holderSet = new HashSet<>();
   private final Deque<int[]> deque = new ArrayDeque<>();
   private boolean flag;
   private boolean flag2;
   private int value = -1;
   private int value2 = -1;
   private int value3;

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (this.flag && MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() != this.value2) {
            this.m_2_void(false);
         }

         boolean flag3 = MinecraftAccess.MINECRAFT.options.keyUse.isDown();
         if (this.m_9_boolean()) {
            if (!this.flag) {
               this.flag = true;
               this.value = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
               this.value2 = this.value;
               this.holderSet.clear();
               this.deque.clear();
            }

            this.holderSet.addAll(this.getEffects(MinecraftAccess.MINECRAFT.player.getUseItem()));
            this.flag2 = true;
            this.value3 = 0;
         } else if (this.flag) {
            if (this.flag2) {
               this.flag2 = false;
               if (!flag3 || !this.m_6_boolean()) {
                  this.m_2_void(true);
               }
            } else {
               if (!flag3 || ++this.value3 > 10) {
                  this.m_2_void(true);
               }
            }
         }
      } else {
         this.m_2_void();
      }
   }

   private boolean m_9_boolean() {
      return MinecraftAccess.MINECRAFT.player.isUsingItem() && MinecraftAccess.MINECRAFT.player.getUsedItemHand() == InteractionHand.MAIN_HAND && MinecraftAccess.MINECRAFT.player.getUseItem().is(Items.POTION);
   }

   private boolean m_6_boolean() {
      int value4 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();

      for (int i = 0; i < 9; i++) {
         if (i != value4 && this.m_5_boolean(MinecraftAccess.MINECRAFT.player.getInventory().getItem(i))) {
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(i);
            this.value2 = i;
            return true;
         }
      }

      if ((Boolean)this.fromInventory.getValue() && this.canSwap()) {
         for (int i2 = 9; i2 < 36; i2++) {
            if (this.m_5_boolean(MinecraftAccess.MINECRAFT.player.getInventory().getItem(i2))) {
               this.swapSlots(i2, value4);
               this.deque.push(new int[]{i2, value4});
               if (InventoryUtil.m_6_boolean()) {
                  ((MinecraftAccessor)MinecraftAccess.MINECRAFT).setRightClickDelay(2);
               }

               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void m_2_void(boolean flag3) {
      if (flag3 && MinecraftAccess.MINECRAFT.player != null && this.value != -1) {
         InventoryUtil.selectHotbarSlot(this.value);
      }

      if (MinecraftAccess.MINECRAFT.player != null && this.canSwap()) {
         while (!this.deque.isEmpty()) {
            int[] intArray = this.deque.pop();
            this.swapSlots(intArray[0], intArray[1]);
         }
      }

      this.m_2_void();
   }

   private void m_2_void() {
      this.flag = false;
      this.flag2 = false;
      this.value = -1;
      this.value2 = -1;
      this.value3 = 0;
      this.holderSet.clear();
      this.deque.clear();
   }

   private void swapSlots(int value4, int value5) {
      InventoryUtil.m_6(() -> InventoryUtil.clickSlot(MinecraftAccess.MINECRAFT.player.containerMenu.containerId, value4, value5, ContainerInput.SWAP));
   }

   private boolean canSwap() {
      return MinecraftAccess.MINECRAFT.player.containerMenu == MinecraftAccess.MINECRAFT.player.inventoryMenu && !InventoryUtil.isBusy();
   }

   private boolean m_5_boolean(ItemStack itemStack) {
      if (!itemStack.isEmpty() && itemStack.is(Items.POTION)) {
         var holderSet2 = this.getEffects(itemStack);
         return holderSet2.isEmpty() ? false : !this.holderSet.containsAll(holderSet2);
      } else {
         return false;
      }
   }

   private Set<Holder<MobEffect>> getEffects(ItemStack itemStack) {
      HashSet set = new HashSet();
      PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
      if (potionContents == null) {
         return set;
      }

      for (MobEffectInstance mobEffectInstance : potionContents.getAllEffects()) {
         set.add(mobEffectInstance.getEffect());
      }

      return set;
   }

   @Override
   public void onDisable() {
      if (this.flag) {
         this.m_2_void(true);
      }

      this.m_2_void();
   }

   private static String b(byte[] byteArray) {
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
