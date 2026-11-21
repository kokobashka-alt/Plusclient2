package wtf.onetap.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AimPoints {
   private static final int[][] VALUE_ARRAY = m_7_IArrArr();

   private AimPoints() {
   }

   public static Vec3 m_6_Vec3(BlockPos blockPos) {
      return Vec3.atBottomCenterOf(blockPos.above());
   }

   public static Vec3 getBottomCenter(AABB aABB) {
      return new Vec3((aABB.minX + aABB.maxX) * 0.5, aABB.minY, (aABB.minZ + aABB.maxZ) * 0.5);
   }

   public static double distanceSqToBottomCenter(Vec3 vec3, AABB aABB) {
      return getBottomCenter(aABB).distanceToSqr(vec3);
   }

   public static float m_3(CollisionUtil collisionUtil, Vec3 vec3, AABB aABB) {
      double doubleValue = Math.sqrt(distanceSqToBottomCenter(vec3, aABB)) / 12.0;
      return doubleValue > 1.0 ? 0.0F : calcExplosionDamage(doubleValue, m_0(collisionUtil, vec3, aABB));
   }

   public static float m_3_float(Vec3 vec3, AABB aABB) {
      double doubleValue = Math.sqrt(distanceSqToBottomCenter(vec3, aABB)) / 12.0;
      return doubleValue > 1.0 ? 0.0F : calcExplosionDamage(doubleValue, 1.0);
   }

   private static float calcExplosionDamage(double doubleValue, double doubleValue2) {
      double doubleValue3 = (1.0 - doubleValue) * doubleValue2;
      return (float)((doubleValue3 * doubleValue3 + doubleValue3) / 2.0 * 7.0 * 12.0 + 1.0);
   }

   public static float m_0(CollisionUtil collisionUtil, Vec3 vec3, AABB aABB) {
      double doubleValue = 1.0 / ((aABB.maxX - aABB.minX) * 2.0 + 1.0);
      double doubleValue2 = 1.0 / ((aABB.maxY - aABB.minY) * 2.0 + 1.0);
      double doubleValue3 = 1.0 / ((aABB.maxZ - aABB.minZ) * 2.0 + 1.0);
      if (!(doubleValue < 0.0) && !(doubleValue2 < 0.0) && !(doubleValue3 < 0.0)) {
         double doubleValue4 = (1.0 - Math.floor(1.0 / doubleValue) * doubleValue) / 2.0;
         double doubleValue5 = (1.0 - Math.floor(1.0 / doubleValue3) * doubleValue3) / 2.0;
         int value = 0;
         int value2 = 0;

         for (double doubleValue6 = 0.0; doubleValue6 <= 1.0; doubleValue6 += doubleValue) {
            for (double doubleValue7 = 0.0; doubleValue7 <= 1.0; doubleValue7 += doubleValue2) {
               for (double doubleValue8 = 0.0; doubleValue8 <= 1.0; doubleValue8 += doubleValue3) {
                  Vec3 vec32 = new Vec3(
                     Mth.lerp(doubleValue6, aABB.minX, aABB.maxX) + doubleValue4, Mth.lerp(doubleValue7, aABB.minY, aABB.maxY), Mth.lerp(doubleValue8, aABB.minZ, aABB.maxZ) + doubleValue5
                  );
                  if (BlockGetter.traverseBlocks(vec32, vec3, collisionUtil, (collisionUtil2, blockPos) -> collisionUtil2.m_9_VoxelShape(blockPos).clip(vec32, vec3, blockPos), collisionUtil3 -> null) == null
                     )
                   {
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

   public static Set<BlockPos> m_8(CollisionUtil collisionUtil, BlockPos blockPos) {
      BlockPos blockPos2 = blockPos.above();
      byte byteValue = 13;
      float[] floatArray = new float[byteValue * byteValue * byteValue];
      Arrays.fill(floatArray, Float.NaN);
      HashSet set = new HashSet();

      for (int[] intArray : VALUE_ARRAY) {
         float floatValue = 4.2F;

         for (byte byteValue2 = 0; byteValue2 < intArray.length && floatValue > 0.0F; byteValue2 += 3) {
            int value = ((intArray[byteValue2] + 6) * byteValue + intArray[byteValue2 + 1] + 6) * byteValue + intArray[byteValue2 + 2] + 6;
            BlockPos blockPos3 = blockPos2.offset(intArray[byteValue2], intArray[byteValue2 + 1], intArray[byteValue2 + 2]);
            if (Float.isNaN(floatArray[value])) {
               float floatValue2 = collisionUtil.m_1_float(blockPos3);
               floatArray[value] = floatValue2 < 0.0F ? -1.0F : (floatValue2 + 0.3F) * 0.3F;
            }

            if (floatArray[value] >= 0.0F) {
               floatValue -= floatArray[value];
               if (floatValue > 0.0F) {
                  set.add(blockPos3);
               }
            }

            floatValue -= 0.22500001F;
         }
      }

      return set;
   }

   private static int[][] m_7_IArrArr() {
      int[][] arrayArray = new int[1352][];
      int value = (int)Math.ceil(18.666666F);
      int value2 = 0;

      for (int i = 0; i < 16; i++) {
         for (int i2 = 0; i2 < 16; i2++) {
            for (int i3 = 0; i3 < 16; i3++) {
               if (i == 0 || i == 15 || i2 == 0 || i2 == 15 || i3 == 0 || i3 == 15) {
                  double doubleValue = i / 15.0F * 2.0F - 1.0F;
                  double doubleValue2 = i2 / 15.0F * 2.0F - 1.0F;
                  double doubleValue3 = i3 / 15.0F * 2.0F - 1.0F;
                  double doubleValue4 = Math.sqrt(doubleValue * doubleValue + doubleValue2 * doubleValue2 + doubleValue3 * doubleValue3);
                  doubleValue /= doubleValue4;
                  doubleValue2 /= doubleValue4;
                  doubleValue3 /= doubleValue4;
                  int[] intArray = new int[value * 3];
                  double doubleValue5 = 0.5;
                  double doubleValue6 = 0.0;
                  double doubleValue7 = 0.5;

                  for (int i4 = 0; i4 < value; i4++) {
                     intArray[i4 * 3] = Mth.floor(doubleValue5);
                     intArray[i4 * 3 + 1] = Mth.floor(doubleValue6);
                     intArray[i4 * 3 + 2] = Mth.floor(doubleValue7);
                     doubleValue5 += doubleValue * 0.3F;
                     doubleValue6 += doubleValue2 * 0.3F;
                     doubleValue7 += doubleValue3 * 0.3F;
                  }

                  arrayArray[value2++] = intArray;
               }
            }
         }
      }

      return arrayArray;
   }
}
