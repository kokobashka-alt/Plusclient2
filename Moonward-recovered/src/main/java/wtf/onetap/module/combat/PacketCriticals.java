package wtf.onetap.module.combat;

import java.util.ArrayDeque;
import java.util.Set;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ConnectedEvent;
import wtf.onetap.event.PostMotionEvent;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.input.ClientInputAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.movement.NoWeb;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.C6_f;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.MovementStateAccess;
import wtf.onetap.util.PacketUtil;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "PacketCriticals", description = "Критует под плавным падением/когда в паутине", category = Category.COMBAT)
public class PacketCriticals extends Module {
   private final ArrayDeque<Packet<?>> arrayDeque = new ArrayDeque<>();
   private boolean flag;
   private boolean flag2;
   private int value;
   private int value2 = Integer.MIN_VALUE;
   private boolean flag3;
   private final TimeHelper timeHelper = new TimeHelper();
   private final MultiModeSetting modes = new MultiModeSetting("Режимы", "Плавное падение", "Плавное падение", "Паутина").build();

   public PacketCriticals() {
      ((Set)this.modes.getValue()).add("Паутина");
   }

   @Override
   public void onDisable() {
      if (this.flag3 && MinecraftAccess.MINECRAFT.options != null) {
         MinecraftAccess.MINECRAFT.options.keyJump.setDown(InventoryUtil.isKeyDown(MinecraftAccess.MINECRAFT.options.keyJump));
      }

      this.flag3 = false;
      this.flushPackets(false);
      this.flag2 = false;
   }

   public boolean m_9_boolean() {
      return this.canCrit() && this.modes.isSelected("Плавное падение") && this.m_8_boolean() && !this.m_7_boolean();
   }

   public boolean m_3_boolean() {
      return this.canCrit() && this.modes.isSelected("Паутина") && this.m_7_boolean();
   }

   public boolean m_0_boolean() {
      return this.m_9_boolean() || this.m_3_boolean();
   }

   public boolean m_5_boolean() {
      return !this.m_0_boolean() || this.timeHelper.hasElapsed(560L);
   }

   private boolean canCrit() {
      return this.isEnabled() && MinecraftAccess.MINECRAFT.player != null && !MinecraftAccess.MINECRAFT.player.hasEffect(MobEffects.BLINDNESS);
   }

   private boolean m_8_boolean() {
      return MinecraftAccess.MINECRAFT.player.hasEffect(MobEffects.SLOW_FALLING) && !MinecraftAccess.MINECRAFT.player.onGround();
   }

   private boolean m_7_boolean() {
      return C6_f.m_4n_boolean();
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         this.arrayDeque.clear();
         this.flag = false;
      } else {
         Packet packet = event.getPacket();
         if (packet instanceof ServerboundPlayerCommandPacket serverboundPlayerCommandPacket && serverboundPlayerCommandPacket.getAction() == Action.STOP_SPRINTING) {
            this.value2 = MinecraftAccess.MINECRAFT.player.tickCount;
         }

         if (packet instanceof ServerboundAcceptTeleportationPacket) {
            this.flushPackets(false);
            this.flag2 = true;
         } else if (packet instanceof ServerboundMovePlayerPacket) {
            if (this.flag) {
               this.flushPackets(false);
            }

            if (this.flag2) {
               this.flag2 = false;
            } else if (this.m_3_boolean()) {
               event.cancel();
               this.arrayDeque.add(packet);
               this.flag = true;
               this.value = MinecraftAccess.MINECRAFT.player.tickCount;
            }
         } else {
            if (this.flag) {
               event.cancel();
               this.arrayDeque.add(packet);
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.options != null && this.m_3_boolean() && MinecraftAccess.MINECRAFT.options.keyJump.isDown() && this.m_4_boolean()) {
         MinecraftAccess.MINECRAFT.options.keyJump.setDown(false);
         this.flag3 = true;
      }
   }

   @EventTarget
   private void onPostMotion(PostMotionEvent event) {
      if (this.flag3) {
         this.flag3 = false;
         MinecraftAccess.MINECRAFT.options.keyJump.setDown(InventoryUtil.isKeyDown(MinecraftAccess.MINECRAFT.options.keyJump));
      }
   }

   private boolean m_4_boolean() {
      if (MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(2.5F) < 0.92F) {
         return false;
      }

      KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
      return killAura != null && killAura.isEnabled() && KillAura.livingEntity != null;
   }

   @EventTarget
   private void onConnected(ConnectedEvent event) {
      if (this.flag && (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.player.tickCount - this.value > 1)) {
         this.flushPackets(false);
      }
   }

   public void m_1(Runnable runnable) {
      if (runnable != null) {
         if (this.m_0_boolean()) {
            this.timeHelper.reset();
         }

         if (this.m_3_boolean() && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.input != null && MinecraftAccess.MINECRAFT.options != null) {
            Vec2 vec2 = MinecraftAccess.MINECRAFT.player.input.getMoveVector();
            boolean flag4 = MinecraftAccess.MINECRAFT.options.keyUp.isDown();
            boolean flag5 = MinecraftAccess.MINECRAFT.options.keyDown.isDown();
            boolean flag6 = MinecraftAccess.MINECRAFT.options.keyLeft.isDown();
            boolean flag7 = MinecraftAccess.MINECRAFT.options.keyRight.isDown();
            boolean flag8 = MinecraftAccess.MINECRAFT.options.keyJump.isDown();
            boolean flag9 = MinecraftAccess.MINECRAFT.options.keySprint.isDown();
            Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getDeltaMovement();
            boolean flag10 = this.isNoWebEnabled();
            InventoryUtil.m_3_void();
            if (flag10) {
               this.applyTinyMotion();
            }

            this.flushPackets(true);
            this.m_4_void();

            try {
               if (flag10) {
                  this.applyTinyMotion();
               }

               runnable.run();
            } finally {
               ((ClientInputAccessor)MinecraftAccess.MINECRAFT.player.input).onetap$setMoveVector(vec2);
               MinecraftAccess.MINECRAFT.options.keyUp.setDown(flag4);
               MinecraftAccess.MINECRAFT.options.keyDown.setDown(flag5);
               MinecraftAccess.MINECRAFT.options.keyLeft.setDown(flag6);
               MinecraftAccess.MINECRAFT.options.keyRight.setDown(flag7);
               MinecraftAccess.MINECRAFT.options.keyJump.setDown(flag8);
               MinecraftAccess.MINECRAFT.options.keySprint.setDown(flag9);
               if (flag10) {
                  MinecraftAccess.MINECRAFT.player.setDeltaMovement(vec3);
               }
            }
         } else {
            this.flushPackets(false);
            runnable.run();
         }
      }
   }

   private void m_4_void() {
      MovementStateAccess movementStateAccess = (MovementStateAccess)MinecraftAccess.MINECRAFT.player;
      if (movementStateAccess.onetap$wasSprinting() && this.value2 != MinecraftAccess.MINECRAFT.player.tickCount) {
         PacketUtil.sendPacket(new ServerboundPlayerCommandPacket(MinecraftAccess.MINECRAFT.player, Action.STOP_SPRINTING));
         MinecraftAccess.MINECRAFT.player.setSprinting(false);
         movementStateAccess.onetap$setWasSprinting(false);
         InventoryUtil.m_7_void();
      }
   }

   private void flushPackets(boolean flag4) {
      this.flag = false;
      boolean flag5 = !flag4;

      while (!this.arrayDeque.isEmpty()) {
         Packet packet = this.arrayDeque.poll();
         if (!flag5 && packet instanceof ServerboundMovePlayerPacket serverboundMovePlayerPacket) {
            packet = this.m_6(serverboundMovePlayerPacket);
            flag5 = true;
         }

         if (MinecraftAccess.MINECRAFT.player != null) {
            PacketUtil.sendPacket(packet);
         }
      }
   }

   private ServerboundMovePlayerPacket m_6(ServerboundMovePlayerPacket serverboundMovePlayerPacket) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         double doubleValue;
         double doubleValue2;
         double doubleValue3;
         if (serverboundMovePlayerPacket.hasPosition()) {
            doubleValue = serverboundMovePlayerPacket.getX(0.0);
            doubleValue2 = serverboundMovePlayerPacket.getY(0.0) - 1.0E-9;
            doubleValue3 = serverboundMovePlayerPacket.getZ(0.0);
            Vec3 vec3 = PacketUtil.m_1_Vec3();
            if (vec3 != null && doubleValue2 >= vec3.y && doubleValue2 - vec3.y < 5.0E-4) {
               doubleValue2 = vec3.y - 1.0E-9;
            }
         } else {
            Vec3 vec32 = PacketUtil.m_1_Vec3();
            Vec3 vec33 = this.m_6_Vec3();
            if (vec32 == null || vec33 == null) {
               return serverboundMovePlayerPacket;
            }

            doubleValue = vec32.x + vec33.x;
            doubleValue2 = vec32.y - 1.0E-9;
            doubleValue3 = vec32.z + vec33.z;
            MinecraftAccess.MINECRAFT.player.setPos(MinecraftAccess.MINECRAFT.player.getX() + vec33.x, MinecraftAccess.MINECRAFT.player.getY(), MinecraftAccess.MINECRAFT.player.getZ() + vec33.z);
         }

         ((MovementStateAccess)MinecraftAccess.MINECRAFT.player).onetap$setLastSent(doubleValue, doubleValue2, doubleValue3, false);
         boolean flag4 = serverboundMovePlayerPacket.horizontalCollision();
         return serverboundMovePlayerPacket.hasRotation()
            ? new PosRot(doubleValue, doubleValue2, doubleValue3, serverboundMovePlayerPacket.getYRot(0.0F), serverboundMovePlayerPacket.getXRot(0.0F), false, flag4)
            : new Pos(doubleValue, doubleValue2, doubleValue3, false, flag4);
      } else {
         return serverboundMovePlayerPacket;
      }
   }

   private Vec3 m_6_Vec3() {
      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getViewVector(1.0F).horizontal();
      if (vec3.lengthSqr() < 1.0E-8) {
         vec3 = new Vec3(0.0, 0.0, 1.0);
      }

      vec3 = vec3.normalize().scale(2.5E-4);
      Vec3[] vec3Array = new Vec3[]{vec3, vec3.reverse(), new Vec3(-vec3.z, 0.0, vec3.x), new Vec3(vec3.z, 0.0, -vec3.x)};

      for (Vec3 vec32 : vec3Array) {
         if (MinecraftAccess.MINECRAFT.level.noCollision(MinecraftAccess.MINECRAFT.player, MinecraftAccess.MINECRAFT.player.getBoundingBox().move(vec32))) {
            return vec32;
         }
      }

      return null;
   }

   private boolean isNoWebEnabled() {
      NoWeb noWeb = Onetap.MODULE_MANAGER.get(NoWeb.class);
      return noWeb != null && noWeb.isEnabled();
   }

   private void applyTinyMotion() {
      MinecraftAccess.MINECRAFT.player.setDeltaMovement(1.0E-5, 1.0E-5, 1.0E-5);
   }
}
