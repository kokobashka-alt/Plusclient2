package wtf.onetap.util;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.ConnectedEvent;
import wtf.onetap.event.EventPriority;
import wtf.onetap.module.combat.PacketCriticals;

public class C6_f implements MinecraftAccess {
   private static float floatValue;
   private static double doubleValue;
   private static boolean flag;
   private static int value;

   @EventTarget(priority = EventPriority.HIGHEST)
   private void onConnected(ConnectedEvent event) {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      if (localPlayer == null) {
         floatValue = 0.0F;
         flag = false;
         value = 0;
      } else {
         if (localPlayer.onGround()) {
            value++;
         } else {
            value = 0;
         }

         if (flag) {
            double doubleValue2 = doubleValue - localPlayer.getY();
            if (Math.abs(doubleValue2) > 8.0) {
               floatValue = (float)Math.max(0.0, localPlayer.fallDistance);
            } else if (localPlayer.onGround() || m_7_boolean()) {
               floatValue = 0.0F;
            } else if (doubleValue2 > 0.0) {
               floatValue += (float)doubleValue2;
            }
         }

         doubleValue = localPlayer.getY();
         flag = true;
      }
   }

   public static float m_5_float() {
      return floatValue;
   }

   public static boolean m_7(boolean flag2, boolean flag3) {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      if (localPlayer == null) {
         return false;
      } else {
         PacketCriticals packetCriticals = Onetap.MODULE_MANAGER.get(PacketCriticals.class);
         if (localPlayer.hasEffect(MobEffects.SLOW_FALLING) && packetCriticals != null && packetCriticals.m_0_boolean()) {
            return m_4n_boolean() || localPlayer.getDeltaMovement().y < 0.0;
         } else if (!flag2) {
            return true;
         } else if (m_6_boolean()) {
            return false;
         } else if (m_1_boolean()) {
            return true;
         } else if (localPlayer.onGround()) {
            return m_8_boolean() && m_4v_boolean() ? false : flag3 && value > 1;
         } else {
            return m_5_boolean() ? m_9_boolean() : !m_4_d_boolean();
         }
      }
   }

   public static boolean m_2_boolean() {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      return localPlayer != null && !localPlayer.isSprinting() && m_5_boolean() && !m_1_boolean() && m_9_boolean();
   }

   public static boolean m_5_boolean() {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      return localPlayer != null && !localPlayer.onGround() && !localPlayer.isInWater() ? localPlayer.fallDistance > 0.0 || floatValue > 0.0F : false;
   }

   public static boolean m_6_boolean() {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      if (localPlayer == null || MinecraftAccess.MINECRAFT.level == null) {
         return false;
      } else {
         return m_8_boolean() && !localPlayer.isUnderWater() && localPlayer.isInWater() ? m_6_boolean(0, 1, 0) && m_6_boolean(0, 2, 0) : false;
      }
   }

   public static boolean m_4_boolean() {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      return localPlayer != null && MinecraftAccess.MINECRAFT.level != null ? localPlayer.isInWater() || m_7_boolean() || m_7_boolean(-0.8) : false;
   }

   private static boolean m_8_boolean() {
      if (MinecraftAccess.MINECRAFT.options != null && MinecraftAccess.MINECRAFT.options.keyJump.isDown()) {
         return true;
      }

      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      return localPlayer != null && localPlayer.input != null && localPlayer.input.keyPresses.jump();
   }

   public static boolean m_1_boolean() {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      if (localPlayer == null) {
         return true;
      } else if (localPlayer.isMobilityRestricted()
         || localPlayer.getAbilities().flying
         || localPlayer.isFallFlying()
         || localPlayer.isPassenger()
         || localPlayer.onClimbable()
         || localPlayer.isInLava()
         || localPlayer.hasEffect(MobEffects.LEVITATION)
         || m_4n_boolean()) {
         return true;
      } else {
         return !m_7_boolean() ? false : !m_8_boolean() || m_7_boolean(1.0) || !m_4v_boolean();
      }
   }

   public static boolean m_4_d_boolean() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         float floatValue2 = m_6_boolean(0, 2, 0) ? 0.6F : 0.08F;
         return m_0_boolean() || floatValue < floatValue2 || floatValue > 1.2F;
      } else {
         return false;
      }
   }

   private static boolean m_9_boolean() {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      if (localPlayer.isInWater()) {
         return false;
      } else {
         return m_4_boolean() ? localPlayer.fallDistance > 0.0 : m_0_float() < 0.08F || !m_3_boolean();
      }
   }

   private static boolean m_4v_boolean() {
      PlayerUtil playerUtil = PlayerUtil.predictLocal(0);

      for (int i = 0; i < 30; i++) {
         playerUtil.tick();
         if (!playerUtil.flag4 && !playerUtil.flag7 && playerUtil.vec32.y < 0.0) {
            return true;
         }
      }

      return false;
   }

   private static boolean m_7_boolean() {
      return m_7_boolean(0.0) || m_7_boolean(0.4);
   }

   private static boolean m_7_boolean(double doubleValue2) {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      return localPlayer != null && MinecraftAccess.MINECRAFT.level != null
         ? MinecraftAccess.MINECRAFT.level.getFluidState(BlockPos.containing(localPlayer.getX(), localPlayer.getY() + doubleValue2, localPlayer.getZ())).is(FluidTags.WATER)
         : false;
   }

   private static float m_0_float() {
      return (float)Math.max(MinecraftAccess.MINECRAFT.player.fallDistance, floatValue);
   }

   private static boolean m_3_boolean() {
      if (PlayerUtil.predictLocal(1).flag4) {
         return true;
      }

      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      double doubleValue2 = Math.min(localPlayer.getDeltaMovement().y, 0.0) - 0.08;
      return doubleValue2 >= 0.0 ? false : !localPlayer.level().noCollision(localPlayer, localPlayer.getBoundingBox().move(0.0, doubleValue2, 0.0));
   }

   private static boolean m_0_boolean() {
      return MinecraftAccess.MINECRAFT.level != null && m_6_boolean(0, 1, 0) && m_6_boolean(0, 2, 0) && m_6_boolean(0, 3, 0);
   }

   public static boolean m_2_boolean(LivingEntity livingEntity) {
      return livingEntity != null && m_1_boolean() && livingEntity.hurtTime > 0;
   }

   public static boolean m_4n_boolean() {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox();

         for (BlockPos blockPos : BlockPos.betweenClosed(
            (int)Math.floor(aABB.minX),
            (int)Math.floor(aABB.minY),
            (int)Math.floor(aABB.minZ),
            (int)Math.floor(aABB.maxX),
            (int)Math.floor(aABB.maxY),
            (int)Math.floor(aABB.maxZ)
         )) {
            BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos);
            if (blockState.is(Blocks.COBWEB)) {
               VoxelShape voxelShape = blockState.getShape(MinecraftAccess.MINECRAFT.level, blockPos);
               if (!voxelShape.isEmpty()) {
                  for (AABB aABB2 : voxelShape.toAabbs()) {
                     if (aABB2.move(blockPos).intersects(aABB)) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static boolean m_6_boolean(int value2, int value3, int value4) {
      return MinecraftAccess.MINECRAFT.level.getBlockState(MinecraftAccess.MINECRAFT.player.blockPosition().offset(value2, value3, value4)).isAir();
   }
}
