package wtf.onetap.util;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.combat.crystal.CrystalDamage;

public class RaycastUtil {
   private RaycastUtil() {
   }

   public static RaycastUtil.DataRecord2 m_4(RaycastUtil.CallbackType c9, Vec3 vec3, Vec3 vec32, AABB aABB, List<RaycastUtil.DataRecord> c1List, List<AABB> aABBList, double doubleValue, boolean flag, boolean flag2) {
      for (RaycastUtil.DataRecord c1 : c1List) {
         RaycastUtil.DataRecord2 c5 = m_3(c9, vec3, vec32, aABB, c1, aABBList, doubleValue, flag, flag2);
         if (c5 != null) {
            return c5;
         }
      }

      return null;
   }

   private static RaycastUtil.DataRecord2 m_3(RaycastUtil.CallbackType c9, Vec3 vec3, Vec3 vec32, AABB aABB, RaycastUtil.DataRecord c1, List<AABB> aABBList, double doubleValue, boolean flag, boolean flag2) {
      AABB aABB2 = c1.m_6cc().deflate(1.0E-5);
      int value = Mth.floor(aABB2.minY);
      if (!m_2(c9, aABB2, value, value)) {
         AABB aABB3 = aABB2.minmax(c1.m_6c_l().deflate(1.0E-5));
         return m_1(c9, vec3, vec32, aABB, aABBList, doubleValue, flag2, aABB3, c1.m_6c_l(), value, value);
      } else {
         int value2 = Mth.floor(aABB2.maxY);
         return flag && value2 > value && !m_2(c9, aABB2, value + 1, value2)
            ? m_1(c9, vec3, vec32, aABB, aABBList, doubleValue, flag2, aABB2, aABB2, value + 1, value2)
            : null;
      }
   }

   private static boolean m_2(RaycastUtil.CallbackType c9, AABB aABB, int value, int value2) {
      for (BlockPos blockPos : m_2(aABB, value, value2)) {
         if (c9.m_9_boolean(blockPos)) {
            return true;
         }
      }

      return false;
   }

   private static RaycastUtil.DataRecord2 m_1(
      RaycastUtil.CallbackType c9, Vec3 vec3, Vec3 vec32, AABB aABB, List<AABB> aABBList, double doubleValue, boolean flag, AABB aABB2, AABB aABB3, int value, int value2
   ) {
      InventoryUtil.m_8_StringArr();
      AABB aABB4 = aABB.inflate(0.05, 0.0, 0.05);
      RaycastUtil.DataRecord2 c5 = null;
      double doubleValue2 = Double.NEGATIVE_INFINITY;

      for (BlockPos blockPos : m_2(aABB2, value, value2)) {
         AABB aABB5 = new AABB(blockPos);
         if (c9.m_3_boolean(blockPos) && !aABB5.intersects(aABB4) && !aABBList.stream().anyMatch(aABB5::intersects)) {
            RaycastUtil.DataRecord2 c52 = m_6(c9, vec3, vec32, blockPos, doubleValue, flag);
            if (c52 != null) {
               double doubleValue3 = m_4(aABB5, aABB3) * 10.0 + vec32.dot(c52.m_2s_Vec3().subtract(vec3).normalize());
               if (!(doubleValue3 <= doubleValue2)) {
                  doubleValue2 = doubleValue3;
                  c5 = c52;
               }
            }
         }
      }

      return c5;
   }

   private static RaycastUtil.DataRecord2 m_6(RaycastUtil.CallbackType c9, Vec3 vec3, Vec3 vec32, BlockPos blockPos, double doubleValue, boolean flag) {
      RaycastUtil.DataRecord2 c5 = null;
      double doubleValue2 = Double.NEGATIVE_INFINITY;

      for (Direction direction : Direction.values()) {
         BlockPos blockPos2 = blockPos.relative(direction);
         if (c9.isCobweb(blockPos2)) {
            Direction direction2 = direction.getOpposite();
            CrystalDamage.DataRecord7 c8 = CrystalDamage.m_7(vec3, vec32, blockPos2, direction2, doubleValue, (vec33, direction3) -> flag || !c9.m_3_boolean(vec3, CrystalDamage.offsetTowards(vec33, direction3)));
            if (c8 != null) {
               double doubleValue3 = vec32.dot(c8.m_3b().subtract(vec3).normalize());
               if (!(doubleValue3 <= doubleValue2)) {
                  doubleValue2 = doubleValue3;
                  c5 = new RaycastUtil.DataRecord2(blockPos, blockPos2, direction2, c8.m_3b());
               }
            }
         }
      }

      return c5;
   }

   private static List<BlockPos> m_2(AABB aABB, int value, int value2) {
      return BlockPos.betweenClosedStream(Mth.floor(aABB.minX), value, Mth.floor(aABB.minZ), Mth.floor(aABB.maxX), value2, Mth.floor(aABB.maxZ))
         .map(BlockPos::immutable)
         .toList();
   }

   private static double m_4(AABB aABB, AABB aABB2) {
      double doubleValue = Math.min(aABB.maxX, aABB2.maxX) - Math.max(aABB.minX, aABB2.minX);
      double doubleValue2 = Math.min(aABB.maxZ, aABB2.maxZ) - Math.max(aABB.minZ, aABB2.minZ);
      return !(doubleValue <= 0.0) && !(doubleValue2 <= 0.0) ? doubleValue * doubleValue2 : 0.0;
   }

   public record DataRecord(AABB f_6cc, AABB f_6c_l) {
      public AABB m_6cc() {
         return this.f_6cc;
      }

      public AABB m_6c_l() {
         return this.f_6c_l;
      }
   }

   public record DataRecord2(BlockPos f_4_n_n, BlockPos f_4_n__, Direction f_2k, Vec3 f_2s) {
      public BlockPos m_4_n_n() {
         return this.f_4_n_n;
      }

      public BlockPos m_4_n__() {
         return this.f_4_n__;
      }

      public Direction m_2k_Direction() {
         return this.f_2k;
      }

      public Vec3 m_2s_Vec3() {
         return this.f_2s;
      }
   }

   public interface CallbackType {
      boolean m_3_boolean(BlockPos blockPos);

      boolean m_9_boolean(BlockPos blockPos);

      boolean isCobweb(BlockPos blockPos);

      boolean m_3_boolean(Vec3 vec3, Vec3 vec32);
   }
}
