package wtf.onetap.module.combat;

import java.util.ArrayDeque;
import net.minecraft.network.PacketProcessor.ListenerAndPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.AttackEntityEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "BackTrack", description = "Откладывает пакеты сервера и бьёт цель на старой позиции (Grim)", category = Category.COMBAT)
public class BackTrack extends Module {
   private final FloatSetting delayMs = new FloatSetting("Задержка (мс)", 120.0F, 50.0F, 400.0F, 10.0F).build();
   private final FloatSetting pauseAfterResetMs = new FloatSetting("Пауза после сброса (мс)", 250.0F, 0.0F, 1000.0F, 50.0F).build();
   private final BooleanSetting showPositions = new BooleanSetting("Показывать позиции", true).build();
   private final ArrayDeque<BackTrack.DataRecord> arrayDeque = new ArrayDeque<>();
   private final VecDeltaCodec vecDeltaCodec = new VecDeltaCodec();
   private LivingEntity livingEntity;
   private long longValue;
   private long longValue2;
   private Vec3 vec3;
   private int value = -1;
   private boolean flag;

   public boolean m_6(ListenerAndPacket<?> listenerAndPacket) {
      if (!this.flag && this.isEnabled() && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && listenerAndPacket.listener() == MinecraftAccess.MINECRAFT.getConnection()) {
         Packet packet = listenerAndPacket.packet();
         long longValue3 = System.currentTimeMillis();
         boolean flag2 = !this.arrayDeque.isEmpty();
         if (flag2 && this.m_5(packet)) {
            this.flush();
            return false;
         }

         LivingEntity livingEntity2 = this.m_4_LivingEntity();
         if (flag2 || livingEntity2 != null && longValue3 >= this.longValue2) {
            Vec3 vec32 = livingEntity2 == null ? null : this.m_4(packet, livingEntity2);
            if (!flag2) {
               if (vec32 == null || !this.m_0(livingEntity2, vec32)) {
                  return false;
               }

               this.value = livingEntity2.getId();
            }

            if (vec32 != null && livingEntity2.getId() == this.value) {
               this.vec3 = vec32;
            }

            this.arrayDeque.add(new BackTrack.DataRecord(listenerAndPacket, longValue3));
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (!this.arrayDeque.isEmpty()) {
         if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
            LivingEntity livingEntity2 = this.m_4_LivingEntity();
            if (livingEntity2 != null && livingEntity2.getId() == this.value && this.m_0(livingEntity2, this.vec3)) {
               long longValue3 = System.currentTimeMillis() - this.delayMs.getInt();
               this.flag = true;

               try {
                  while (!this.arrayDeque.isEmpty() && this.arrayDeque.peek().m_7y() <= longValue3) {
                     this.arrayDeque.poll().m_0_g().handle();
                  }
               } finally {
                  this.flag = false;
               }
            } else {
               this.flush();
               this.longValue2 = System.currentTimeMillis() + this.pauseAfterResetMs.getInt();
            }
         } else {
            this.reset();
         }
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if ((Boolean)this.showPositions.getValue() && !this.arrayDeque.isEmpty() && this.vec3 != null) {
         LivingEntity livingEntity2 = this.m_4_LivingEntity();
         if (livingEntity2 != null && livingEntity2.getId() == this.value) {
            Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
            AABB aABB = livingEntity2.getBoundingBox();
            Vec3 vec32 = livingEntity2.position();
            m_6(matrix4f, event.getVec3(), aABB.move(this.vec3.subtract(vec32)), ColorUtil.rgba(255, 70, 70, 255));
            m_6(matrix4f, event.getVec3(), aABB.move(livingEntity2.getPosition(event.getFloatValue()).subtract(vec32)), ColorUtil.rgba(70, 255, 120, 255));
         }
      }
   }

   @EventTarget
   private void onAttackEntity(AttackEntityEvent event) {
      if (event.getEntity() instanceof LivingEntity livingEntity2) {
         this.livingEntity = livingEntity2;
         this.longValue = System.currentTimeMillis();
      }
   }

   @Override
   public void onDisable() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         this.flush();
      } else {
         this.reset();
      }

      this.longValue2 = 0L;
      this.livingEntity = null;
   }

   private void flush() {
      this.flag = true;

      BackTrack.DataRecord c0;
      try {
         while ((c0 = this.arrayDeque.poll()) != null) {
            c0.m_0_g().handle();
         }
      } finally {
         this.flag = false;
         this.vec3 = null;
         this.value = -1;
      }
   }

   private void reset() {
      this.arrayDeque.clear();
      this.vec3 = null;
      this.value = -1;
   }

   private static void m_6(Matrix4f matrix4f, Vec3 vec32, AABB aABB, int value2) {
      Box3DRenderer.drawBox(
         matrix4f,
         (float)(aABB.minX - vec32.x),
         (float)(aABB.minY - vec32.y),
         (float)(aABB.minZ - vec32.z),
         (float)(aABB.maxX - vec32.x),
         (float)(aABB.maxY - vec32.y),
         (float)(aABB.maxZ - vec32.z),
         ColorUtil.withAlpha(value2, 40)
      );
      Line3DRenderer.drawLines(matrix4f, ElytraTarget.boxToLines(aABB), vec32, 1.5F, ColorUtil.withAlpha(value2, 220));
   }

   private LivingEntity m_4_LivingEntity() {
      LivingEntity livingEntity2 = KillAura.livingEntity;
      if (livingEntity2 == null && this.livingEntity != null && System.currentTimeMillis() - this.longValue < 2000L) {
         livingEntity2 = this.livingEntity;
      }

      return livingEntity2 != null && livingEntity2 != MinecraftAccess.MINECRAFT.player && livingEntity2.isAlive() ? livingEntity2 : null;
   }

   private boolean m_0(LivingEntity livingEntity2, Vec3 vec32) {
      double doubleValue = MinecraftAccess.MINECRAFT.player.entityInteractionRange();
      Vec3 vec33 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      AABB aABB = livingEntity2.getBoundingBox();
      double doubleValue2 = Math.sqrt(aABB.distanceToSqr(vec33));
      double doubleValue3 = Math.sqrt(aABB.move(vec32.subtract(livingEntity2.position())).distanceToSqr(vec33));
      return doubleValue2 <= doubleValue && doubleValue3 > doubleValue && doubleValue3 <= doubleValue + 3.0 - 0.5;
   }

   private Vec3 m_4(Packet<?> packet, LivingEntity livingEntity2) {
      Vec3 vec32 = this.vec3 != null && this.value == livingEntity2.getId() ? this.vec3 : livingEntity2.getPositionCodec().getBase();

      return switch (packet) {
         case ClientboundMoveEntityPacket clientboundMoveEntityPacket when clientboundMoveEntityPacket.hasPosition() && clientboundMoveEntityPacket.getEntity(MinecraftAccess.MINECRAFT.level) == livingEntity2 -> {
            this.vecDeltaCodec.setBase(vec32);
            yield this.vecDeltaCodec.decode(clientboundMoveEntityPacket.getXa(), clientboundMoveEntityPacket.getYa(), clientboundMoveEntityPacket.getZa());
         }
         case ClientboundTeleportEntityPacket clientboundTeleportEntityPacket when clientboundTeleportEntityPacket.id() == livingEntity2.getId() && clientboundTeleportEntityPacket.relatives().isEmpty() -> clientboundTeleportEntityPacket.change().position();
         case ClientboundEntityPositionSyncPacket clientboundEntityPositionSyncPacket when clientboundEntityPositionSyncPacket.id() == livingEntity2.getId() -> clientboundEntityPositionSyncPacket.values().position();
         default -> null;
      };
   }

   private boolean m_5(Packet<?> packet) {
      int value2 = MinecraftAccess.MINECRAFT.player.getId();
      return packet instanceof ClientboundPlayerPositionPacket
         || packet instanceof ClientboundPlayerRotationPacket
         || packet instanceof ClientboundExplodePacket
         || packet instanceof ClientboundRespawnPacket
         || packet instanceof ClientboundLoginPacket
         || packet instanceof ClientboundStartConfigurationPacket
         || packet instanceof ClientboundSetHealthPacket
         || packet instanceof ClientboundGameEventPacket
         || packet instanceof ClientboundPlayerAbilitiesPacket
         || packet instanceof ClientboundPlayerCombatKillPacket
         || packet instanceof ClientboundOpenScreenPacket
         || packet instanceof ClientboundContainerClosePacket
         || packet instanceof ClientboundContainerSetContentPacket
         || packet instanceof ClientboundContainerSetSlotPacket
         || packet instanceof ClientboundSetCursorItemPacket
         || packet instanceof ClientboundSetPlayerInventoryPacket
         || packet instanceof ClientboundSetHeldSlotPacket
         || packet instanceof ClientboundSetPassengersPacket
         || packet instanceof ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket && clientboundSetEntityMotionPacket.id() == value2
         || packet instanceof ClientboundHurtAnimationPacket clientboundHurtAnimationPacket && clientboundHurtAnimationPacket.id() == value2
         || packet instanceof ClientboundDamageEventPacket clientboundDamageEventPacket && clientboundDamageEventPacket.entityId() == value2
         || packet instanceof ClientboundEntityEventPacket clientboundEntityEventPacket && clientboundEntityEventPacket.getEntity(MinecraftAccess.MINECRAFT.level) == MinecraftAccess.MINECRAFT.player
         || packet instanceof ClientboundTeleportEntityPacket clientboundTeleportEntityPacket && clientboundTeleportEntityPacket.id() == this.value && !clientboundTeleportEntityPacket.relatives().isEmpty();
   }

   private record DataRecord(ListenerAndPacket<?> f_0_g, long f_7y) {
      public ListenerAndPacket<?> m_0_g() {
         return this.f_0_g;
      }

      public long m_7y() {
         return this.f_7y;
      }
   }
}
