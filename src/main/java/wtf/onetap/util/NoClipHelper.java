package wtf.onetap.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import wtf.onetap.Onetap;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.module.player.AutoTool;

public class NoClipHelper implements MinecraftAccess {
   private static Object object;
   private static int value = -1;

   private NoClipHelper() {
   }

   public static Vec3 getEyePosition() {
      return MinecraftAccess.MINECRAFT.player == null ? Vec3.ZERO : MinecraftAccess.MINECRAFT.player.getEyePosition();
   }

   public static boolean m_3_boolean(BlockPos blockPos) {
      if (MinecraftAccess.MINECRAFT.player != null && blockPos != null) {
         Vec3 vec3 = getEyePosition();
         Vec3 vec32 = new Vec3(
            Mth.clamp(vec3.x, blockPos.getX(), blockPos.getX() + 1.0),
            Mth.clamp(vec3.y, blockPos.getY(), blockPos.getY() + 1.0),
            Mth.clamp(vec3.z, blockPos.getZ(), blockPos.getZ() + 1.0)
         );
         double doubleValue = MinecraftAccess.MINECRAFT.player.blockInteractionRange();
         return vec3.distanceToSqr(vec32) <= doubleValue * doubleValue + 1.0E-4;
      } else {
         return false;
      }
   }

   public static Vector2f m_9_Vector2f(Vec3 vec3) {
      return m_4_Vector2f(vec3, null);
   }

   public static Vector2f m_4_Vector2f(Vec3 vec3, BlockPos blockPos) {
      if (MinecraftAccess.MINECRAFT.player != null && vec3 != null) {
         Vec3 vec32 = vec3.subtract(getEyePosition());
         float floatValue = MinecraftAccess.MINECRAFT.player.getYRot();
         float floatValue2 = MinecraftAccess.MINECRAFT.player.getXRot();
         float floatValue3 = (float)Math.toDegrees(Math.atan2(vec32.z, vec32.x)) - 90.0F;
         float floatValue4 = floatValue + Mth.wrapDegrees(floatValue3 - floatValue);
         float floatValue5 = Mth.clamp((float)(-Math.toDegrees(Math.atan2(vec32.y, Math.sqrt(vec32.x * vec32.x + vec32.z * vec32.z)))), -90.0F, 90.0F);
         double doubleValue = MinecraftAccess.MINECRAFT.options.sensitivity().get() * 0.6 + 0.2;
         double doubleValue2 = Math.pow(doubleValue, 3.0) * 8.0 * 0.15;
         if (doubleValue2 <= 0.0) {
            return blockPos != null && !m_2(floatValue4, floatValue5, blockPos) ? null : new Vector2f(floatValue4, floatValue5);
         }

         float floatValue6 = floatValue4 - (float)((floatValue4 - floatValue) % doubleValue2);
         float floatValue7 = Mth.clamp(floatValue5 - (float)((floatValue5 - floatValue2) % doubleValue2), -90.0F, 90.0F);

         for (int i = -3; i <= 3; i++) {
            for (int i2 = -3; i2 <= 3; i2++) {
               float floatValue8 = floatValue6 + i * (float)doubleValue2;
               float floatValue9 = Mth.clamp(floatValue7 + i2 * (float)doubleValue2, -90.0F, 90.0F);
               if (blockPos == null || m_2(floatValue8, floatValue9, blockPos)) {
                  return new Vector2f(floatValue8, floatValue9);
               }
            }
         }

         return blockPos == null ? new Vector2f(floatValue6, floatValue7) : null;
      } else {
         return null;
      }
   }

   private static boolean m_2(float floatValue, float floatValue2, BlockPos blockPos) {
      if (!m_3_boolean(blockPos)) {
         return false;
      }

      Vec3 vec3 = getEyePosition();
      AABB aABB = new AABB(blockPos);
      return aABB.contains(vec3) || aABB.clip(vec3, vec3.add(Vec3.directionFromRotation(floatValue2, floatValue).scale(MinecraftAccess.MINECRAFT.player.blockInteractionRange()))).isPresent();
   }

   public static void m_4(MovementInputEvent event, float floatValue) {
      float floatValue2 = event.getForward();
      float floatValue3 = event.getStrafe();
      if (floatValue2 != 0.0F || floatValue3 != 0.0F) {
         double[] doubleArray = m_5_DArr(floatValue2, floatValue3, MinecraftAccess.MINECRAFT.player.getYRot());
         double doubleValue = Double.MAX_VALUE;
         float floatValue4 = 0.0F;
         float floatValue5 = 0.0F;

         for (float floatValue6 = -1.0F; floatValue6 <= 1.0F; floatValue6++) {
            for (float floatValue7 = -1.0F; floatValue7 <= 1.0F; floatValue7++) {
               if (floatValue6 != 0.0F || floatValue7 != 0.0F) {
                  double[] doubleArray2 = m_5_DArr(floatValue6, floatValue7, floatValue);
                  double doubleValue2 = doubleArray2[0] - doubleArray[0];
                  double doubleValue3 = doubleArray2[1] - doubleArray[1];
                  double doubleValue4 = doubleValue2 * doubleValue2 + doubleValue3 * doubleValue3;
                  if (doubleValue4 < doubleValue) {
                     doubleValue = doubleValue4;
                     floatValue4 = floatValue6;
                     floatValue5 = floatValue7;
                  }
               }
            }
         }

         event.setForward(floatValue4);
         event.setStrafe(floatValue5);
      }
   }

   private static double[] m_5_DArr(float floatValue, float floatValue2, float floatValue3) {
      float floatValue4 = Mth.clamp(Math.round(floatValue2), -1.0F, 1.0F) * 0.98F;
      float floatValue5 = Mth.clamp(Math.round(floatValue), -1.0F, 1.0F) * 0.98F;
      double doubleValue = (double)floatValue4 * floatValue4 + (double)floatValue5 * floatValue5;
      if (doubleValue > 1.0) {
         double doubleValue2 = Math.sqrt(doubleValue);
         floatValue4 = (float)(floatValue4 / doubleValue2);
         floatValue5 = (float)(floatValue5 / doubleValue2);
      }

      float floatValue6 = Mth.sin(floatValue3 * (float) (Math.PI / 180.0));
      float floatValue7 = Mth.cos(floatValue3 * (float) (Math.PI / 180.0));
      return new double[]{(double)floatValue4 * floatValue7 - (double)floatValue5 * floatValue6, (double)floatValue5 * floatValue7 + (double)floatValue4 * floatValue6};
   }

   public static void m_2(Object object2, BlockState blockState, BlockPos blockPos, Runnable runnable) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         runnable.run();
      } else {
         int value2 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
         int value3 = -1;
         float floatValue = 1.0F;

         for (int i = 0; i < 9; i++) {
            ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
            if (!itemStack.isEmpty()
               && itemStack.has(DataComponents.TOOL)
               && !(itemStack.getDestroySpeed(blockState) <= 1.0F)
               && (!blockState.requiresCorrectToolForDrops() || itemStack.isCorrectToolForDrops(blockState))) {
               float floatValue2 = m_3(itemStack, blockState);
               if (floatValue2 > floatValue) {
                  floatValue = floatValue2;
                  value3 = i;
               }
            }
         }

         if (value3 == -1) {
            runnable.run();
         } else {
            AutoTool autoTool = Onetap.MODULE_MANAGER.get(AutoTool.class);
            boolean flag = autoTool != null && !autoTool.m_4_boolean();
            if (flag) {
               if (value == -1) {
                  value = value2;
               }

               MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value3);
               object = object2;
               runnable.run();
            } else if (value2 == value3) {
               runnable.run();
            } else {
               m_8_void(value3);

               try {
                  runnable.run();
               } finally {
                  m_8_void(value2);
               }
            }
         }
      }
   }

   public static void m_1(Object object2) {
      if (object == null || object == object2) {
         object = null;
         if (value != -1 && MinecraftAccess.MINECRAFT.player != null) {
            int value2 = value;
            value = -1;
            m_8_void(value2);
         }
      }
   }

   private static void m_8_void(int value2) {
      MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value2);
      if (MinecraftAccess.MINECRAFT.gameMode != null) {
         MinecraftAccess.MINECRAFT.gameMode.ensureHasSentCarriedItem();
      }
   }

   private static float m_3(ItemStack itemStack, BlockState blockState) {
      float floatValue = itemStack.getDestroySpeed(blockState);
      if (floatValue > 1.0F) {
         floatValue += (float)MinecraftAccess.MINECRAFT.player.getAttributeValue(Attributes.MINING_EFFICIENCY);
      }

      if (MobEffectUtil.hasDigSpeed(MinecraftAccess.MINECRAFT.player)) {
         floatValue *= 1.0F + (MobEffectUtil.getDigSpeedAmplification(MinecraftAccess.MINECRAFT.player) + 1) * 0.2F;
      }

      MobEffectInstance mobEffectInstance = MinecraftAccess.MINECRAFT.player.getEffect(MobEffects.MINING_FATIGUE);
      if (mobEffectInstance != null) {
         floatValue *= switch (mobEffectInstance.getAmplifier()) {
            case 0 -> 0.3F;
            case 1 -> 0.09F;
            case 2 -> 0.0027F;
            default -> 8.1E-4F;
         };
      }

      floatValue *= (float)MinecraftAccess.MINECRAFT.player.getAttributeValue(Attributes.BLOCK_BREAK_SPEED);
      if (MinecraftAccess.MINECRAFT.player.isEyeInFluid(FluidTags.WATER)) {
         floatValue *= (float)MinecraftAccess.MINECRAFT.player.getAttributeValue(Attributes.SUBMERGED_MINING_SPEED);
      }

      if (!MinecraftAccess.MINECRAFT.player.onGround()) {
         floatValue /= 5.0F;
      }

      return floatValue;
   }
}
