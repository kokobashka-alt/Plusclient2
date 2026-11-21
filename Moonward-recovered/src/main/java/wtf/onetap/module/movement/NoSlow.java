package wtf.onetap.module.movement;

import java.util.ArrayDeque;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ServerboundChunkBatchReceivedPacket;
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.NoSlowEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.entity.LivingEntityInvoker;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PacketUtil;

@ModuleInformation(name = "NoSlow", description = "Убирает замедление от использования предмета", category = Category.MOVEMENT)
public class NoSlow extends Module {
   public static boolean flag;
   private final ModeSetting mode = new ModeSetting("Мод", "GroundedGrim", "Vanilla", "Grim", "GroundedGrim").build();
   private final BooleanSetting ignoreGapplesAndEnchantedApples = new BooleanSetting("Не работать на геплы и чарки", false).visibleWhen(this::isGrimMode).build();
   private final BooleanSetting sprint = new BooleanSetting("Спринт", false).visibleWhen(() -> ((String)this.mode.getValue()).equals("GroundedGrim")).build();
   private final FloatSetting slowdownTicks = new FloatSetting("Тиков замедления", 1.0F, 1.0F, Float.intBitsToFloat((int)-5165318769366532096L), 1.0F)
      .visibleWhen(() -> ((String)this.mode.getValue()).equals("Grim"))
      .build();
   private int value;
   private final ArrayDeque<Packet<?>> arrayDeque = new ArrayDeque<>();
   private boolean flag2;
   private long longValue;
   private Vec3 vec3;

   @Override
   public void onDisable() {
      super.onDisable();
      this.value = 0;
      this.flushPackets();
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         this.flag2 = false;
         this.arrayDeque.clear();
         this.vec3 = null;
      }
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (this.flag2) {
         if (!(event.getPacket() instanceof ServerboundPongPacket) && !(event.getPacket() instanceof ServerboundChunkBatchReceivedPacket)) {
            if (event.getPacket() instanceof ServerboundClientTickEndPacket serverboundClientTickEndPacket && this.m_8_boolean()) {
               event.cancel();
               PacketUtil.sendPacket(serverboundClientTickEndPacket);
               this.flushPackets();
            }
         } else if (this.m_8_boolean()) {
            this.flushPackets();
         } else {
            event.cancel();
            this.arrayDeque.add(event.getPacket());
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (this.flag2 && event.getPacket() instanceof ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket && clientboundSetEntityMotionPacket.id() == MinecraftAccess.MINECRAFT.player.getId()) {
            event.cancel();
            this.vec3 = clientboundSetEntityMotionPacket.movement();
         } else if (this.flag2
            && (
               event.getPacket() instanceof ClientboundPlayerPositionPacket
                  || event.getPacket() instanceof ClientboundExplodePacket clientboundExplodePacket && clientboundExplodePacket.playerKnockback().isPresent()
            )) {
            this.flushPackets();
         } else if (event.getPacket() instanceof ClientboundSetEntityDataPacket clientboundSetEntityDataPacket && clientboundSetEntityDataPacket.id() == MinecraftAccess.MINECRAFT.player.getId()) {
            int value2 = LivingEntityInvoker.onetap$getLivingEntityFlags().id();

            for (DataValue dataValue : clientboundSetEntityDataPacket.packedItems()) {
               if (dataValue.id() == value2 && dataValue.value() instanceof Byte byte2) {
                  if (this.flag2) {
                     this.flushPackets();
                  }

                  ItemStack itemStack = MinecraftAccess.MINECRAFT.player.isUsingItem()
                     ? MinecraftAccess.MINECRAFT.player.getUseItem()
                     : MinecraftAccess.MINECRAFT.player.getItemInHand((byte2 & 2) != 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                  if ((byte2 & 1) != 0 && ((String)this.mode.getValue()).equals("GroundedGrim") && !itemStack.isEmpty() && !this.isIgnoredItem(itemStack)) {
                     this.flag2 = true;
                     this.longValue = System.currentTimeMillis();
                  }

                  return;
               }
            }
         }
      }
   }

   @EventTarget
   private void onNoSlow(NoSlowEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.isUsingItem()) {
         if (((String)this.mode.getValue()).equals("Vanilla")) {
            event.cancel();
         } else if (!this.isUsingIgnoredItem()) {
            switch ((String)this.mode.getValue()) {
               case "Grim":
                  int value2 = ((Float)this.slowdownTicks.getValue()).intValue() + 1;
                  if (this.value % value2 != 0) {
                     event.cancel();
                  }

                  this.value++;
                  break;
               case "GroundedGrim":
                  if (this.flag2 && !this.m_8_boolean()) {
                     event.cancel();
                  }
            }
         }
      }
   }

   private boolean isGrimMode() {
      return ((String)this.mode.getValue()).equals("Grim") || ((String)this.mode.getValue()).equals("GroundedGrim");
   }

   public static boolean m_9_boolean() {
      NoSlow noSlow = Onetap.MODULE_MANAGER.get(NoSlow.class);
      return noSlow != null
         && noSlow.isEnabled()
         && (Boolean)noSlow.sprint.getValue()
         && ((String)noSlow.mode.getValue()).equals("GroundedGrim")
         && noSlow.flag2
         && !noSlow.m_8_boolean()
         && !noSlow.isUsingIgnoredItem();
   }

   public static void m_3(LocalPlayer localPlayer) {
      if (localPlayer == MinecraftAccess.MINECRAFT.player && !localPlayer.isSprinting() && m_9_boolean()) {
         if (localPlayer.input.hasForwardImpulse() && !localPlayer.input.keyPresses.shift() && localPlayer.getFoodData().getFoodLevel() > 6) {
            if (!localPlayer.horizontalCollision || localPlayer.minorHorizontalCollision) {
               if (!localPlayer.isPassenger() && !localPlayer.isFallFlying() && !localPlayer.isInWater() && !localPlayer.isMobilityRestricted()) {
                  Sprint sprint2 = Onetap.MODULE_MANAGER.get(Sprint.class);
                  if (MinecraftAccess.MINECRAFT.options.keySprint.isDown() || sprint2 != null && sprint2.isEnabled()) {
                     localPlayer.setSprinting(true);
                  }
               }
            }
         }
      }
   }

   private boolean m_8_boolean() {
      return MinecraftAccess.MINECRAFT.player == null
         || !MinecraftAccess.MINECRAFT.player.isUsingItem()
         || !((String)this.mode.getValue()).equals("GroundedGrim")
         || System.currentTimeMillis() - this.longValue > 2500L;
   }

   private void flushPackets() {
      this.flag2 = false;

      while (!this.arrayDeque.isEmpty()) {
         PacketUtil.sendPacket(this.arrayDeque.poll());
      }

      if (this.vec3 != null && MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.lerpMotion(this.vec3);
      }

      this.vec3 = null;
   }

   private boolean isUsingIgnoredItem() {
      return this.isIgnoredItem(MinecraftAccess.MINECRAFT.player.getUseItem());
   }

   private boolean isIgnoredItem(ItemStack itemStack) {
      return (Boolean)this.ignoreGapplesAndEnchantedApples.getValue() && (itemStack.is(Items.GOLDEN_APPLE) || itemStack.is(Items.ENCHANTED_GOLDEN_APPLE));
   }
}
