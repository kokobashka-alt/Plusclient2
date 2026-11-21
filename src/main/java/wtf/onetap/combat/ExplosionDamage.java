package wtf.onetap.combat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

public class ExplosionDamage implements MinecraftAccess {
   private ExplosionDamage() {
   }

   public static float calcDamage(Vec3 vec3, Player player) {
      return m_1(vec3, player, player.getBoundingBox());
   }

   public static float m_1(Vec3 vec3, Player player, AABB aABB) {
      if (MinecraftAccess.MINECRAFT.level.getDifficulty() != Difficulty.PEACEFUL && player != null) {
         if (!new AABB(
               Mth.floor(vec3.x - 11.0),
               Mth.floor(vec3.y - 11.0),
               Mth.floor(vec3.z - 11.0),
               Mth.floor(vec3.x + 13.0),
               Mth.floor(vec3.y + 13.0),
               Mth.floor(vec3.z + 13.0)
            )
            .intersects(aABB)) {
            return 0.0F;
         }

         if (!player.isInvulnerable()) {
            double doubleValue = getBottomCenter(aABB).distanceToSqr(vec3) / 144.0;
            if (doubleValue <= 1.0) {
               double doubleValue2 = m_7(vec3, aABB);
               double doubleValue3 = (1.0 - doubleValue) * doubleValue2;
               float floatValue = (float)Math.floor((doubleValue3 * doubleValue3 + doubleValue3) / 2.0 * 7.0 * 12.0 + 1.0);
               if (MinecraftAccess.MINECRAFT.level.getDifficulty() == Difficulty.EASY) {
                  floatValue = Math.min(floatValue / 2.0F + 1.0F, floatValue);
               } else if (MinecraftAccess.MINECRAFT.level.getDifficulty() == Difficulty.HARD) {
                  floatValue = floatValue * 3.0F / 2.0F;
               }

               DamageSource damageSource = MinecraftAccess.MINECRAFT.level.damageSources().explosion(null, null);
               floatValue = CombatRules.getDamageAfterAbsorb(player, floatValue, damageSource, player.getArmorValue(), (float)player.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
               if (player.hasEffect(MobEffects.RESISTANCE)) {
                  int value = 25 - (player.getEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
                  floatValue = Math.max(floatValue * value / 25.0F, 0.0F);
               }

               if (floatValue <= 0.0F) {
                  return 0.0F;
               }

               float floatValue2 = m_8_int(player);
               if (floatValue2 > 0.0F) {
                  floatValue = CombatRules.getDamageAfterMagicAbsorb(floatValue, floatValue2);
               }

               return floatValue;
            }
         }

         return 0.0F;
      } else {
         return 0.0F;
      }
   }

   public static float calcDamage(Vec3 vec3, Player player, BlockPos blockPos) {
      return m_3(vec3, player, player.getBoundingBox(), blockPos);
   }

   public static float calcDamage(Vec3 vec3, Player player, BlockPos blockPos, BlockPos blockPos2) {
      return m_4(vec3, player, player.getBoundingBox(), blockPos, blockPos2);
   }

   public static float m_3(Vec3 vec3, Player player, AABB aABB, BlockPos blockPos) {
      return m_4(vec3, player, aABB, blockPos, null);
   }

   public static float m_4(Vec3 vec3, Player player, AABB aABB, BlockPos blockPos, BlockPos blockPos2) {
      if (MinecraftAccess.MINECRAFT.level.getDifficulty() == Difficulty.PEACEFUL) {
         return 0.0F;
      }

      double doubleValue = 12.0;
      if (!new AABB(
            Mth.floor(vec3.x - doubleValue - 1.0),
            Mth.floor(vec3.y - doubleValue - 1.0),
            Mth.floor(vec3.z - doubleValue - 1.0),
            Mth.floor(vec3.x + doubleValue + 1.0),
            Mth.floor(vec3.y + doubleValue + 1.0),
            Mth.floor(vec3.z + doubleValue + 1.0)
         )
         .intersects(aABB)) {
         return 0.0F;
      }

      if (!player.isInvulnerable()) {
         double doubleValue2 = getBottomCenter(aABB).distanceToSqr(vec3) / 144.0;
         if (doubleValue2 <= 1.0) {
            double doubleValue3 = m_5(vec3, aABB, blockPos, blockPos2);
            double doubleValue4 = (1.0 - doubleValue2) * doubleValue3;
            float floatValue = (float)Math.floor((doubleValue4 * doubleValue4 + doubleValue4) / 2.0 * 7.0 * doubleValue + 1.0);
            if (MinecraftAccess.MINECRAFT.level.getDifficulty() == Difficulty.EASY) {
               floatValue = Math.min(floatValue / 2.0F + 1.0F, floatValue);
            } else if (MinecraftAccess.MINECRAFT.level.getDifficulty() == Difficulty.HARD) {
               floatValue = floatValue * 3.0F / 2.0F;
            }

            DamageSource damageSource = MinecraftAccess.MINECRAFT.level.damageSources().explosion(null, null);
            floatValue = CombatRules.getDamageAfterAbsorb(player, floatValue, damageSource, player.getArmorValue(), (float)player.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
            if (player.hasEffect(MobEffects.RESISTANCE)) {
               int value = 25 - (player.getEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
               floatValue = Math.max(floatValue * value / 25.0F, 0.0F);
            }

            if (floatValue <= 0.0F) {
               return 0.0F;
            }

            float floatValue2 = m_8_int(player);
            if (floatValue2 > 0.0F) {
               floatValue = CombatRules.getDamageAfterMagicAbsorb(floatValue, floatValue2);
            }

            return floatValue;
         }
      }

      return 0.0F;
   }

   public static boolean m_9(BlockPos blockPos, Entity entity) {
      AABB aABB = new AABB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1.0, blockPos.getY() + 2.0, blockPos.getZ() + 1.0);
      return entity.getBoundingBox().intersects(aABB);
   }

   public static float m_7(Vec3 vec3, AABB aABB) {
      return m_5(vec3, aABB, null);
   }

   private static float calcDamage(Vec3 vec3, AABB aABB, BlockPos blockPos) {
      return m_5(vec3, aABB, blockPos, null);
   }

   private static float m_5(Vec3 vec3, AABB aABB, BlockPos blockPos, BlockPos blockPos2) {
      return m_2(vec3, aABB, blockPos, blockPos2);
   }

   private static float m_5(Vec3 vec3, AABB aABB, BlockPos blockPos) {
      return m_2(vec3, aABB, blockPos, null);
   }

   private static float m_2(Vec3 vec3, AABB aABB, BlockPos blockPos, BlockPos blockPos2) {
      double doubleValue = 1.0 / ((aABB.maxX - aABB.minX) * 2.0 + 1.0);
      double doubleValue2 = 1.0 / ((aABB.maxY - aABB.minY) * 2.0 + 1.0);
      double doubleValue3 = 1.0 / ((aABB.maxZ - aABB.minZ) * 2.0 + 1.0);
      double doubleValue4 = (1.0 - Math.floor(1.0 / doubleValue) * doubleValue) / 2.0;
      double doubleValue5 = (1.0 - Math.floor(1.0 / doubleValue3) * doubleValue3) / 2.0;
      if (!(doubleValue < 0.0) && !(doubleValue2 < 0.0) && !(doubleValue3 < 0.0)) {
         int value = 0;
         int value2 = 0;

         for (double doubleValue6 = 0.0; doubleValue6 <= 1.0; doubleValue6 += doubleValue) {
            for (double doubleValue7 = 0.0; doubleValue7 <= 1.0; doubleValue7 += doubleValue2) {
               for (double doubleValue8 = 0.0; doubleValue8 <= 1.0; doubleValue8 += doubleValue3) {
                  double doubleValue9 = Mth.lerp(doubleValue6, aABB.minX, aABB.maxX);
                  double doubleValue10 = Mth.lerp(doubleValue7, aABB.minY, aABB.maxY);
                  double doubleValue11 = Mth.lerp(doubleValue8, aABB.minZ, aABB.maxZ);
                  Vec3 vec32 = new Vec3(doubleValue9 + doubleValue4, doubleValue10, doubleValue11 + doubleValue5);
                  BlockHitResult blockHitResult = BlockGetter.traverseBlocks(
                     vec32,
                     vec3,
                     null,
                     (object, blockPos3) -> {
                        BlockState blockState = blockPos3.equals(blockPos2)
                           ? Blocks.AIR.defaultBlockState()
                           : (blockPos3.equals(blockPos) ? Blocks.OBSIDIAN.defaultBlockState() : MinecraftAccess.MINECRAFT.level.getBlockState(blockPos3));
                        VoxelShape voxelShape = blockState.getCollisionShape(MinecraftAccess.MINECRAFT.level, blockPos3);
                        return voxelShape.clip(vec32, vec3, blockPos3);
                     },
                     object2 -> BlockHitResult.miss(
                        vec3, Direction.getApproximateNearest(vec32.x - vec3.x, vec32.y - vec3.y, vec32.z - vec3.z), BlockPos.containing(vec3)
                     )
                  );
                  if (blockHitResult.getType() == Type.MISS) {
                     value++;
                  }

                  value2++;
               }
            }
         }

         return (float)value / value2;
      } else {
         return 0.0F;
      }
   }

   private static Vec3 getBottomCenter(AABB aABB) {
      return new Vec3((aABB.minX + aABB.maxX) * 0.5, aABB.minY, (aABB.minZ + aABB.maxZ) * 0.5);
   }

   public static int m_8_int(Player player) {
      int value = 0;

      for (EquipmentSlot equipmentSlot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
         ItemStack itemStack = player.getItemBySlot(equipmentSlot);
         value += InventoryUtil.getEnchantmentLevel(itemStack, Enchantments.BLAST_PROTECTION) * 2 + InventoryUtil.getEnchantmentLevel(itemStack, Enchantments.PROTECTION);
      }

      return value;
   }
}
