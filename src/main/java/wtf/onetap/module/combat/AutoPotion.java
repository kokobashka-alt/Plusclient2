package wtf.onetap.module.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket.Handler;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.AABB;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.MovementStateAccess;
import wtf.onetap.util.Rotations;

@ModuleInformation(name = "AutoPotion", description = "Автоматически использует зелья, бросая их под себя", category = Category.COMBAT)
public class AutoPotion extends Module {
   private final MultiModeSetting whatToThrow = new MultiModeSetting("Что кидать?", "Силка", "Силка", "Скорка", "Огнестойка").build();
   private final BooleanSetting onlyInPvp = new BooleanSetting("Только при пвп", true).build();
   private final BooleanSetting onlyOnGround = new BooleanSetting("Только на земле", false).build();
   private final BooleanSetting autoDisable = new BooleanSetting("Авто выкл", false).build();
   private int value = 20;
   private boolean flag;
   private boolean flag2;
   private UUID uUID;
   private boolean flag3;

   public AutoPotion() {
      ((Set)this.whatToThrow.getValue()).addAll(this.whatToThrow.getOptions());
   }

   @Override
   public void onEnable() {
      this.value = 20;
      this.flag = false;
      this.flag3 = false;
   }

   @Override
   public void onDisable() {
      this.restoreKeysIfNeeded();
      this.resetRotation();
      this.value = 20;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         this.value++;
         boolean flag4 = this.flag3;
         this.restoreKeysIfNeeded();
         ArrayList<java.lang.Integer> integerList = new ArrayList<>();
         this.m_9(integerList, this.whatToThrow.isSelected("Скорка"), MobEffects.SPEED);
         this.m_9(integerList, this.whatToThrow.isSelected("Силка"), MobEffects.STRENGTH);
         this.m_9(integerList, this.whatToThrow.isSelected("Огнестойка"), MobEffects.FIRE_RESISTANCE);
         if (integerList.isEmpty()) {
            this.resetRotation();
            if ((Boolean)this.autoDisable.getValue()) {
               this.setEnabled(false);
            }
         } else if ((!(Boolean)this.onlyInPvp.getValue() || this.flag2) && this.value >= 20 && this.canThrow()) {
            if (this.m_9_boolean() && this.isIdle()) {
               int value2 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
               boolean flag5 = integerList.stream().anyMatch(integer -> integer != value2);
               if (!flag5 || !InventoryUtil.isBusy()) {
                  if (flag5 && !flag4 && this.isSprinting()) {
                     InventoryUtil.releaseKeys();
                     MinecraftAccess.MINECRAFT.player.setSprinting(false);
                     this.flag3 = true;
                  } else {
                     if (flag5) {
                        InventoryUtil.m_6(() -> this.m_4(integerList, value2));
                        if (flag4) {
                           InventoryUtil.m_7_void();
                        }
                     } else {
                        this.m_4(integerList, value2);
                     }

                     this.value = 0;
                     this.resetRotation();
                     if ((Boolean)this.autoDisable.getValue()) {
                        this.setEnabled(false);
                     }
                  }
               }
            }
         } else {
            this.resetRotation();
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundBossEventPacket clientboundBossEventPacket) {
         clientboundBossEventPacket.dispatch(new Handler() {
            @Override
            public void add(UUID uUID2, Component component, float floatValue, BossBarColor bossBarColor, BossBarOverlay bossBarOverlay, boolean flag4, boolean flag5, boolean flag6) {
               String text = StringUtil.stripColor(component.getString()).toLowerCase(Locale.ROOT);
               if (text.contains("pvp") || text.contains("у вас осталось")) {
                  AutoPotion.this.flag2 = true;
                  AutoPotion.this.uUID = uUID2;
               }
            }

            @Override
            public void remove(UUID uUID3) {
               if (AutoPotion.this.uUID != null && AutoPotion.this.uUID.equals(uUID3)) {
                  AutoPotion.this.flag2 = false;
                  AutoPotion.this.uUID = null;
               }
            }
         });
      }
   }

   private void m_4(List<Integer> integerList, int value2) {
      for (int value3 : integerList) {
         if (value3 == value2) {
            MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
         } else {
            int value4 = value3 < 9 ? value3 + 36 : value3;
            InventoryUtil.clickSlot(0, value4, value2, ContainerInput.SWAP);
            MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
            InventoryUtil.clickSlot(0, value4, value2, ContainerInput.SWAP);
         }
      }
   }

   private void restoreKeysIfNeeded() {
      if (this.flag3) {
         this.flag3 = false;
         InventoryUtil.restoreKeys();
      }
   }

   private boolean isSprinting() {
      return MinecraftAccess.MINECRAFT.player.isSprinting() || ((MovementStateAccess)MinecraftAccess.MINECRAFT.player).onetap$wasSprinting();
   }

   private boolean canThrow() {
      return !this.m_8_boolean() && (!(Boolean)this.onlyOnGround.getValue() || MinecraftAccess.MINECRAFT.player.onGround());
   }

   private boolean isIdle() {
      return !MinecraftAccess.MINECRAFT.player.isUsingItem() && !MinecraftAccess.MINECRAFT.gameMode.isDestroying();
   }

   private void m_9(List<Integer> integerList, boolean flag4, Holder<MobEffect> holder) {
      if (flag4 && !MinecraftAccess.MINECRAFT.player.hasEffect(holder)) {
         int value2 = this.findPotionSlot(true, holder);
         if (value2 == -1) {
            value2 = this.findPotionSlot(false, holder);
         }

         if (value2 != -1 && !integerList.contains(value2)) {
            integerList.add(value2);
         }
      }
   }

   private boolean m_9_boolean() {
      Rotations rotations = Onetap.ROTATION_MODES.isFlag() ? Onetap.ROTATION_MODES.toRotations() : new Rotations(MinecraftAccess.MINECRAFT.player.getYRot(), MinecraftAccess.MINECRAFT.player.getXRot());
      float floatValue = Mth.wrapDegrees(Onetap.ROTATION_MODES.getRenderYaw());
      float floatValue2 = rotations.getFloatValue() + Mth.clamp(Mth.wrapDegrees(floatValue - rotations.getFloatValue()), -75.0F, 75.0F);
      float floatValue3 = Mth.clamp(rotations.getFloatValue2() + Mth.clamp(90.0F - rotations.getFloatValue2(), -45.0F, 45.0F), -90.0F, 90.0F);
      Onetap.ROTATION_MODES.rotateWithSensitivity(new Rotations(floatValue2, floatValue3), false, 5);
      if (Math.abs(Onetap.ROTATION_MODES.getFloatValue2() - floatValue3) > 0.5F) {
         return false;
      }

      this.flag = true;
      return Math.abs(90.0F - MinecraftAccess.MINECRAFT.player.getXRot()) <= 1.0F;
   }

   private void resetRotation() {
      if (this.flag) {
         Onetap.ROTATION_MODES.m_0_void();
         this.flag = false;
      }
   }

   private boolean m_8_boolean() {
      if (MinecraftAccess.MINECRAFT.player.getY() < 0.0) {
         return false;
      }

      AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox().move(0.0, -0.5, 0.0);
      return !MinecraftAccess.MINECRAFT.level.getBlockCollisions(MinecraftAccess.MINECRAFT.player, aABB).iterator().hasNext();
   }

   private int findPotionSlot(boolean flag4, Holder<MobEffect> holder) {
      int value2 = flag4 ? 9 : 36;

      for (int i = 0; i < value2; i++) {
         if (this.isSplashWithEffect(MinecraftAccess.MINECRAFT.player.getInventory().getItem(i), holder)) {
            return i;
         }
      }

      return -1;
   }

   private boolean isSplashWithEffect(ItemStack itemStack, Holder<MobEffect> holder) {
      if (itemStack != null && !itemStack.isEmpty() && itemStack.is(Items.SPLASH_POTION)) {
         PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
         if (potionContents == null) {
            return false;
         }

         for (MobEffectInstance mobEffectInstance : potionContents.getAllEffects()) {
            if (mobEffectInstance.getEffect().equals(holder)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
