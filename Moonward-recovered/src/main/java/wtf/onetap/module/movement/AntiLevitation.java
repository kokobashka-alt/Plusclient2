package wtf.onetap.module.movement;

import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ConnectedEvent;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.player.ElytraSwap;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PacketUtil;

@ModuleInformation(name = "AntiLevitation", description = "Опускает вниз при действии левитации через элитру", category = Category.MOVEMENT)
public class AntiLevitation extends Module {
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;

   public boolean m_0_boolean() {
      return this.flag3 && MinecraftAccess.MINECRAFT.player != null && !MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);
   }

   public float m_4_float() {
      return MinecraftAccess.MINECRAFT.player == null
         ? Float.intBitsToFloat((int)-8446666348929524695L)
         : MinecraftAccess.MINECRAFT.player.getEyeHeight(MinecraftAccess.MINECRAFT.player.isShiftKeyDown() ? Pose.CROUCHING : Pose.STANDING);
   }

   @Override
   public void onEnable() {
      if (!this.m_7_boolean()) {
         ChatUtil.log("Нет элитры в инвентаре");
         this.setEnabled(false);
      }
   }

   @Override
   public void onDisable() {
      this.m_4_void();
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundPlayerPositionPacket) {
         this.m_4_void();
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.hasEffect(MobEffects.LEVITATION)) {
         event.setJumping(this.flag2);
         this.flag2 = false;
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (!this.m_1_boolean()) {
         this.m_4_void();
      } else if (this.flag) {
         this.flag = false;
      } else if (!InventoryUtil.m_7_boolean()) {
         if (MinecraftAccess.MINECRAFT.gui.screen() == null && MinecraftAccess.MINECRAFT.gui.overlay() == null) {
            if (this.flag4) {
               if (MinecraftAccess.MINECRAFT.player.isFallFlying()) {
                  return;
               }

               this.flag4 = false;
            }

            this.m_7_void();
         }
      }
   }

   @EventTarget
   private void onConnected(ConnectedEvent event) {
      if (!this.m_1_boolean()) {
         this.flag3 = false;
         this.flag4 = false;
         this.m_2_void();
      } else {
         if (this.flag4 && !MinecraftAccess.MINECRAFT.player.isFallFlying()) {
            this.flag4 = false;
         }

         if (this.flag3) {
            MinecraftAccess.MINECRAFT.player.startFallFlying();
         }
      }
   }

   private void m_7_void() {
      ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST);
      if (itemStack.is(Items.ELYTRA)) {
         this.m_3_void();
      } else if (!itemStack.isEmpty()) {
         ElytraSwap elytraSwap = Onetap.MODULE_MANAGER.get(ElytraSwap.class);
         if (elytraSwap != null) {
            int value = InventoryUtil.m_1_int();
            if (value != -1) {
               InventoryUtil.m_6(() -> {
                  elytraSwap.swapNow(value);
                  this.m_3_void();
                  int value2 = InventoryUtil.findBestChestplateSlot();
                  elytraSwap.swapNow(value2 != -1 ? value2 : value);
               });
            }
         }
      }
   }

   private boolean m_7_boolean() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return false;
      } else {
         return MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA) ? true : InventoryUtil.m_1_int() != -1;
      }
   }

   private boolean m_1_boolean() {
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.level == null) {
         return false;
      } else if (!MinecraftAccess.MINECRAFT.player.hasEffect(MobEffects.LEVITATION)) {
         return false;
      } else if (MinecraftAccess.MINECRAFT.player.onGround() || MinecraftAccess.MINECRAFT.player.verticalCollisionBelow) {
         return false;
      } else {
         return !MinecraftAccess.MINECRAFT.player.isPassenger() && !MinecraftAccess.MINECRAFT.player.isInWater() && !MinecraftAccess.MINECRAFT.player.isInLava() && !MinecraftAccess.MINECRAFT.player.isSpectator()
            ? !this.isOverGround(0.1)
            : false;
      }
   }

   private boolean isOverGround(double doubleValue) {
      return !MinecraftAccess.MINECRAFT.level.noCollision(MinecraftAccess.MINECRAFT.player, MinecraftAccess.MINECRAFT.player.getBoundingBox().move(0.0, -doubleValue, 0.0));
   }

   private void reset() {
      this.flag2 = true;
      this.flag = true;
      this.flag3 = true;
      this.flag4 = true;
   }

   private void m_3_void() {
      PacketUtil.sendPacket(new ServerboundPlayerCommandPacket(MinecraftAccess.MINECRAFT.player, Action.START_FALL_FLYING));
      MinecraftAccess.MINECRAFT.player.startFallFlying();
      this.reset();
   }

   private void m_4_void() {
      this.flag = false;
      this.flag2 = false;
      this.flag3 = false;
      this.flag4 = false;
      this.m_2_void();
   }

   private void m_2_void() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (MinecraftAccess.MINECRAFT.player.isFallFlying() && !MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            MinecraftAccess.MINECRAFT.player.stopFallFlying();
         }
      }
   }

   private static String b(byte[] byteArray) {
      int value = 0;
      int value2;
      char[] charArray = new char[value2 = byteArray.length];

      for (int i = 0; i < value2; i++) {
         int value3;
         if ((value3 = 255 & byteArray[i]) < 192) {
            charArray[value++] = (char)value3;
         } else if (value3 < 224) {
            char ch = (char)((char)(value3 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value++] = ch;
         } else if (i < value2 - 2) {
            char ch2 = (char)((char)(value3 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value++] = ch2;
         }
      }

      return new String(charArray, 0, value);
   }
}
