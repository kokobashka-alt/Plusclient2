package wtf.onetap.combat.crystal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CrystalDamage {
   private static final Direction[] DIRECTION_ARRAY = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP};
   private static boolean flag;

   private CrystalDamage() {
   }

   public static CrystalDamage.DataRecord2 m_1(
      CrystalDamage.CallbackType c4, Vec3 vec3, Vec3 vec32, CrystalDamage.DataRecord8 c9, List<CrystalDamage.DataRecord8> c9List, List<CrystalDamage.DataRecord8> c9List2, List<CrystalDamage.DataRecord6> c7lList, CrystalDamage.DataRecord3 c6, CrystalDamage.DataRecord2 c5
   ) {
      return m_0(c4, vec3, vec32, c9, c9List, c9List2, c7lList, c6, c5, null);
   }

   public static CrystalDamage.DataRecord2 m_0(
      CrystalDamage.CallbackType c4, Vec3 vec3, Vec3 vec32, CrystalDamage.DataRecord8 c9, List<CrystalDamage.DataRecord8> c9List, List<CrystalDamage.DataRecord8> c9List2, List<CrystalDamage.DataRecord6> c7lList, CrystalDamage.DataRecord3 c6, CrystalDamage.DataRecord2 c5, BlockPos blockPos
   ) {
      if (c9List.isEmpty()) {
         return null;
      }

      float floatValue = Float.NEGATIVE_INFINITY;
      CrystalDamage.DataRecord5 c7_c = blockPos == null ? null : m_3(c4, blockPos, c9, c6, true);
      if (c7_c != null && c7_c.m_6_j() != CrystalDamage.ModeKind2.PLACE_CRYSTAL) {
         CrystalDamage.DataRecord4 c72 = m_5(c4, topCenter(blockPos), c7_c.m_1t_C3(), c9, c9List, c9List2, c6, 0.0F);
         if (c72 != null) {
            floatValue = c72.m_3_u();
         }
      }

      CrystalDamage.DataRecord2 c52 = null;
      float floatValue2 = Float.NEGATIVE_INFINITY;
      float floatValue3 = Float.NEGATIVE_INFINITY;
      CrystalDamage.DataRecord4 c722 = null;
      Vec3 vec33 = null;

      for (CrystalDamage.DataRecord6 c7l : c7lList) {
         if (!(vec3.distanceTo(c7l.m_5n()) > c6.m_5p() + 2.5) && !c4.m_6_boolean(BlockPos.containing(c7l.m_5n()))) {
            CrystalDamage.DataRecord4 c723 = m_5(c4, c7l.m_5n(), CrystalDamage.DataRecord.f_0, c9, c9List, c9List2, c6, 0.0F);
            if (c723 != null) {
               floatValue3 = Math.max(floatValue3, c723.m_3_u());
               if (c7l.m_2_t_int() < 0 && (c722 == null || c723.m_3_u() > c722.m_3_u())) {
                  c722 = c723;
                  vec33 = c7l.m_5n();
               }

               if (c7l.m_2_t_int() >= 0 && !(c723.m_3_u() <= floatValue2) && (c723.m_1h_boolean() || !isSignificantlyBetter(floatValue, c723.m_3_u()))) {
                  Vec3 vec34 = m_4(c4, vec3, vec32, getCrystalBox(c7l.m_5n()), c6.m_5p(), c7l.m_2_t_int(), CrystalDamage.DataRecord.f_0, c6.m_5r());
                  if (vec34 != null) {
                     floatValue2 = c723.m_3_u();
                     c52 = new CrystalDamage.DataRecord2(
                        CrystalDamage.ModeKind2.BREAK, BlockPos.containing(c7l.m_5n()).below(), null, null, vec34, c7l.m_2_t_int(), c723.m_3p(), c723.m_3i()
                     );
                  }
               }
            }
         }
      }

      if (c52 == null && c6.m_51()) {
         var c7_cList = m_5(c4, vec3, c9, c9List, c6, blockPos);
         c7_cList.sort(Comparator.<CrystalDamage.DataRecord5>comparingDouble(c7_c2 -> c7_c2.m_2_x() + m_0(c5, blockPos, c7_c2)).reversed());
         CrystalDamage.StateHolder c1 = new CrystalDamage.StateHolder();
         CrystalDamage.StateHolder c12 = new CrystalDamage.StateHolder();
         CrystalDamage.StateHolder c13 = new CrystalDamage.StateHolder();
         int value = 0;

         for (CrystalDamage.DataRecord5 c7_c3 : c7_cList) {
            if (value >= 256) {
               break;
            }

            CrystalDamage.StateHolder c14 = c7_c3.m_6_j() == CrystalDamage.ModeKind2.PLACE_CRYSTAL ? c1 : (c7_c3.m_16_boolean() ? c13 : c12);
            float floatValue4 = m_0(c5, blockPos, c7_c3);
            if (c7_c3.m_2_x() + floatValue4 <= Math.min(c1.floatValue, Math.min(c12.floatValue, c13.floatValue))) {
               break;
            }

            if (!(c7_c3.m_2_x() + floatValue4 <= c14.floatValue)) {
               value++;
               boolean flag2 = c7_c3.m_7_e().equals(blockPos);
               Vec3 vec35 = topCenter(c7_c3.m_7_e());
               CrystalDamage.DataRecord4 c724 = m_5(c4, vec35, c7_c3.m_1t_C3(), c9, c9List, c9List2, c6, flag2 ? 0.0F : m_3(c7_c3, c6));
               if (c724 != null && (flag2 || m_6(c7_c3, c724, c6))) {
                  float floatValue5 = c724.m_3_u() - c7_c3.m_2u() + floatValue4;
                  if (!(floatValue5 <= c14.floatValue) && m_4(c4, vec3, vec32, getCrystalBox(vec35), c6.m_5p(), -1, c7_c3.m_1t_C3(), c6.m_5r()) != null) {
                     CrystalDamage.DataRecord2 c53 = m_2(c4, vec3, vec32, c7_c3, c6, c724);
                     if (c53 != null) {
                        c14.c5 = c53;
                        c14.floatValue = floatValue5;
                        c14.c72 = c724;
                     }
                  }
               }
            }
         }

         boolean flag3 = m_8(c13, floatValue3) && (c13.c72.m_1h_boolean() || !m_5(c4, vec3, vec32, c9, c9List, c9List2, c6));
         boolean flag4 = m_8(c12, floatValue3);
         CrystalDamage.StateHolder c15 = flag3 ? c13 : (flag4 ? c12 : null);
         CrystalDamage.StateHolder c16 = flag3 && c13.c72.m_1h_boolean() ? c13 : (flag4 && c12.c72.m_1h_boolean() ? c12 : null);
         CrystalDamage.DataRecord2 c54;
         if (c1.c5 == null) {
            c54 = c15 == null ? null : c15.c5;
         } else {
            c54 = c16 != null && !c1.c72.m_1h_boolean() ? c16.c5 : c1.c5;
         }

         if (c54 == null && c722 == null) {
            c54 = m_8(c4, vec3, vec32, c9, c9List, c9List2, c6);
         }

         c54 = m_0(c4, vec3, vec32, c9, c9List, c9List2, c6, c7lList, c5, blockPos, c54);
         return c722 != null
               && (
                  c54 == null
                     || c54.m_5l() == CrystalDamage.ModeKind2.PLACE_CRYSTAL
                        && !c54.m_1_s_BlockPos().equals(blockPos)
                        && !c1.c72.m_1h_boolean()
                        && !isSignificantlyBetter(c1.c72.m_3_u(), c722.m_3_u())
               )
            ? new CrystalDamage.DataRecord2(CrystalDamage.ModeKind2.HOLD, BlockPos.containing(vec33).below(), null, null, vec33, -1, c722.m_3p(), c722.m_3i())
            : c54;
      } else {
         return c52;
      }
   }

   private static CrystalDamage.DataRecord2 m_0(
      CrystalDamage.CallbackType c4,
      Vec3 vec3,
      Vec3 vec32,
      CrystalDamage.DataRecord8 c9,
      List<CrystalDamage.DataRecord8> c9List,
      List<CrystalDamage.DataRecord8> c9List2,
      CrystalDamage.DataRecord3 c6,
      List<CrystalDamage.DataRecord6> c7lList,
      CrystalDamage.DataRecord2 c5,
      BlockPos blockPos,
      CrystalDamage.DataRecord2 c52
   ) {
      if (c5 != null && (c52 == null || !c5.m_1_s_BlockPos().equals(c52.m_1_s_BlockPos()) && !c52.m_1_s_BlockPos().equals(blockPos))) {
         BlockPos blockPos2 = c5.m_1_s_BlockPos();
         Vec3 vec33 = topCenter(blockPos2);

         for (CrystalDamage.DataRecord6 c7l : c7lList) {
            if (c7l.m_2_t_int() < 0 && !(c7l.m_5n().distanceToSqr(vec33) > 0.25)) {
               CrystalDamage.DataRecord4 c72 = m_5(c4, vec33, CrystalDamage.DataRecord.f_0, c9, c9List, c9List2, c6, 0.0F);
               if (c72 != null && (c52 == null || !(c72.m_3p() < c52.m_50() * 0.84F))) {
                  return new CrystalDamage.DataRecord2(CrystalDamage.ModeKind2.HOLD, blockPos2, null, null, vec33, -1, c72.m_3p(), c72.m_3i());
               }

               return c52;
            }
         }

         CrystalDamage.DataRecord5 c7_c = m_3(c4, blockPos2, c9, c6, blockPos2.equals(blockPos));
         if (c7_c == null) {
            return c52;
         }

         CrystalDamage.DataRecord4 c722 = m_5(c4, vec33, c7_c.m_1t_C3(), c9, c9List, c9List2, c6, 0.0F);
         if (c722 != null && (c52 == null || !(c722.m_3p() < c52.m_50() * 0.84F))) {
            if (m_4(c4, vec3, vec32, getCrystalBox(vec33), c6.m_5p(), -1, c7_c.m_1t_C3(), c6.m_5r()) == null) {
               return c52;
            }

            CrystalDamage.DataRecord2 c53 = m_2(c4, vec3, vec32, c7_c, c6, c722);
            return c53 != null ? c53 : c52;
         } else {
            return c52;
         }
      } else {
         return c52;
      }
   }

   private static boolean m_8(CrystalDamage.StateHolder c1, float floatValue) {
      return c1.c5 != null && isSignificantlyBetter(c1.c72.m_3_u(), floatValue);
   }

   private static boolean m_5(CrystalDamage.CallbackType c4, Vec3 vec3, Vec3 vec32, CrystalDamage.DataRecord8 c9, List<CrystalDamage.DataRecord8> c9List, List<CrystalDamage.DataRecord8> c9List2, CrystalDamage.DataRecord3 c6) {
      int value = Mth.ceil(c6.m_5_s()) + 1;
      BlockPos blockPos = BlockPos.containing(vec3);
      double doubleValue = (c6.m_5_s() + 1.0) * (c6.m_5_s() + 1.0);

      for (int i = -value; i <= value; i++) {
         for (int i2 = -value; i2 <= value; i2++) {
            for (int i3 = -value; i3 <= value; i3++) {
               BlockPos blockPos2 = blockPos.offset(i, i2, i3);
               if (!(vec3.distanceToSqr(blockPos2.getX() + 0.5, blockPos2.getY() + 0.5, blockPos2.getZ() + 0.5) > doubleValue)
                  && c4.classify(blockPos2) == CrystalDamage.ModeKind.BASE
                  && c4.classify(blockPos2.above()) == CrystalDamage.ModeKind.AIR
                  && !m_4(c4, blockPos2.above())) {
                  Vec3 vec33 = topCenter(blockPos2);
                  if (!(distanceSqToBox(vec3, getCrystalBox(vec33)) > c6.m_5p())
                     && m_1(vec33, c9List, c6)
                     && !c4.m_9_boolean(getBodyBox(blockPos2))
                     && m_3(c4, vec3, vec32, blockPos2, null, c6.m_5_s(), CrystalDamage.DataRecord.f_0, c6.m_5r()) != null) {
                     CrystalDamage.DataRecord4 c72 = m_5(c4, vec33, CrystalDamage.DataRecord.f_0, c9, c9List, c9List2, c6, 0.0F);
                     if (c72 != null && !(c72.m_3o() < 0.16F) && m_4(c4, vec3, vec32, getCrystalBox(vec33), c6.m_5p(), -1, CrystalDamage.DataRecord.f_0, c6.m_5r()) != null) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   private static boolean m_1(Vec3 vec3, List<CrystalDamage.DataRecord8> c9List, CrystalDamage.DataRecord3 c6) {
      for (CrystalDamage.DataRecord8 c9 : c9List) {
         double doubleValue = Math.min(c9.m_1_h().distanceTo(vec3), c9.m_17_Vec3().distanceTo(vec3));
         if (doubleValue <= 12.0 && m_3(calcExplosionDamage(doubleValue / 12.0, 1.0F), c9, c6.m_5_u()) >= c9.m_2d_float() * 0.16F) {
            return true;
         }
      }

      return false;
   }

   private static boolean isSignificantlyBetter(float floatValue, float floatValue2) {
      return floatValue2 == Float.NEGATIVE_INFINITY || floatValue > floatValue2 + Math.max(3.0F, floatValue2 * 0.35F);
   }

   private static float m_0(CrystalDamage.DataRecord2 c5, BlockPos blockPos, CrystalDamage.DataRecord5 c7_c) {
      if (c7_c.m_7_e().equals(blockPos)) {
         return 8.0F;
      } else if (c5 != null && c7_c.m_7_e().equals(c5.m_1_s_BlockPos())) {
         return c5.m_5l() == CrystalDamage.ModeKind2.MINE ? 6.0F : 1.5F;
      } else {
         return 0.0F;
      }
   }

   private static boolean m_6(CrystalDamage.DataRecord5 c7_c, CrystalDamage.DataRecord4 c72, CrystalDamage.DataRecord3 c6) {
      return c7_c.m_6_j() != CrystalDamage.ModeKind2.MINE || c72.m_1h_boolean() || c72.m_3p() >= c6.m_11() + 2.0F;
   }

   private static float m_3(CrystalDamage.DataRecord5 c7_c, CrystalDamage.DataRecord3 c6) {
      return c7_c.m_6_j() != CrystalDamage.ModeKind2.MINE && c7_c.m_6_j() != CrystalDamage.ModeKind2.PLACE_OBSIDIAN ? 0.0F : Math.max(1.0F, c6.m_1_f() * 0.1F);
   }

   private static List<CrystalDamage.DataRecord5> m_5(CrystalDamage.CallbackType c4, Vec3 vec3, CrystalDamage.DataRecord8 c9, List<CrystalDamage.DataRecord8> c9List, CrystalDamage.DataRecord3 c6, BlockPos blockPos) {
      ArrayList list = new ArrayList();
      int value = Mth.ceil(c6.m_5_s()) + 1;
      BlockPos blockPos2 = BlockPos.containing(vec3);
      MutableBlockPos mutableBlockPos = new MutableBlockPos();
      double doubleValue = (c6.m_5_s() + 1.0) * (c6.m_5_s() + 1.0);

      for (int i = -value; i <= value; i++) {
         for (int i2 = -value; i2 <= value; i2++) {
            for (int i3 = -value; i3 <= value; i3++) {
               mutableBlockPos.set(blockPos2.getX() + i, blockPos2.getY() + i2, blockPos2.getZ() + i3);
               if (!(vec3.distanceToSqr(mutableBlockPos.getX() + 0.5, mutableBlockPos.getY() + 0.5, mutableBlockPos.getZ() + 0.5) > doubleValue)) {
                  Vec3 vec32 = topCenter(mutableBlockPos);
                  if (!(distanceSqToBox(vec3, getCrystalBox(vec32)) > c6.m_5p())) {
                     float floatValue = m_8(vec32, c9List, c6);
                     if (!(floatValue <= 0.0F)) {
                        BlockPos blockPos3 = mutableBlockPos.immutable();
                        CrystalDamage.DataRecord5 c7_c = m_3(c4, blockPos3, c9, c6, blockPos3.equals(blockPos));
                        if (c7_c != null) {
                           float floatValue2 = floatValue - c7_c.m_2u();
                           list.add(new CrystalDamage.DataRecord5(c7_c.m_6_j(), blockPos3, c7_c.m_2_q(), c7_c.m_1t_C3(), c7_c.m_2u(), c7_c.m_16_boolean(), floatValue2));
                        }
                     }
                  }
               }
            }
         }
      }

      return list;
   }

   private static CrystalDamage.DataRecord5 m_3(CrystalDamage.CallbackType c4, BlockPos blockPos, CrystalDamage.DataRecord8 c9, CrystalDamage.DataRecord3 c6, boolean flag2) {
      if (c4.m_3_boolean(blockPos)) {
         return null;
      }

      BlockPos blockPos2 = blockPos.above();
      CrystalDamage.ModeKind c0 = c4.classify(blockPos);
      if (!m_4(c4, blockPos2) && (c0 == CrystalDamage.ModeKind.BASE || !m_4(c4, blockPos))) {
         CrystalDamage.ModeKind2 c2 = null;
         BlockPos blockPos3 = blockPos;
         BlockPos blockPos4 = null;
         BlockPos blockPos5 = null;
         float floatValue = 0.0F;
         if (c4.classify(blockPos2) != CrystalDamage.ModeKind.AIR) {
            int value = m_4(c4, blockPos2, c9, c6);
            if (value < 0) {
               return null;
            }

            floatValue += 2.0F + value * 0.3F;
            c2 = CrystalDamage.ModeKind2.MINE;
            blockPos3 = blockPos2;
            blockPos4 = blockPos2;
         }

         boolean flag3 = c0 != CrystalDamage.ModeKind.BASE;
         if (flag3) {
            if (!c6.m_5_z()) {
               return null;
            }

            if (c0 == CrystalDamage.ModeKind.SOLID) {
               int value2 = m_4(c4, blockPos, c9, c6);
               if (value2 < 0) {
                  return null;
               }

               floatValue += 2.0F + value2 * 0.3F;
               if (c2 == null) {
                  c2 = CrystalDamage.ModeKind2.MINE;
               }
            } else {
               if (c4.m_4(new AABB(blockPos), false)) {
                  return null;
               }

               if (c2 == null) {
                  c2 = CrystalDamage.ModeKind2.PLACE_OBSIDIAN;
               }

               blockPos5 = blockPos;
            }

            floatValue++;
         }

         AABB aABB = getBodyBox(blockPos);
         if (c4.m_4(aABB, false)) {
            return null;
         }

         if ((c2 == null || c2 == CrystalDamage.ModeKind2.PLACE_OBSIDIAN) && c4.m_4(aABB, true)) {
            if (!flag2) {
               return null;
            }

            c2 = CrystalDamage.ModeKind2.WAIT;
         }

         CrystalDamage.DataRecord c3 = blockPos4 == null && blockPos5 == null ? CrystalDamage.DataRecord.f_0 : new CrystalDamage.DataRecord(blockPos4, blockPos5);
         if (c4.classify(blockPos.above(2)) == CrystalDamage.ModeKind.SOLID || c4.classify(blockPos.above(2)) == CrystalDamage.ModeKind.BASE) {
            floatValue -= 1.5F;
         }

         return new CrystalDamage.DataRecord5(c2 == null ? CrystalDamage.ModeKind2.PLACE_CRYSTAL : c2, blockPos, blockPos3, c3, floatValue, flag3, 0.0F);
      } else {
         return null;
      }
   }

   private static boolean m_4(CrystalDamage.CallbackType c4, BlockPos blockPos) {
      if (!c4.m_6_boolean(blockPos) && !c4.m_6_boolean(blockPos.above())) {
         for (Direction direction : Plane.HORIZONTAL) {
            if (c4.m_6_boolean(blockPos.relative(direction))) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private static int m_4(CrystalDamage.CallbackType c4, BlockPos blockPos, CrystalDamage.DataRecord8 c9, CrystalDamage.DataRecord3 c6) {
      return c6.m_53() && !c4.m_3_boolean(blockPos) && !m_4(blockPos, c9) ? m_3(c4, blockPos, c6) : -1;
   }

   private static int m_3(CrystalDamage.CallbackType c4, BlockPos blockPos, CrystalDamage.DataRecord3 c6) {
      int value = c4.m_5_int(blockPos);
      return value > c6.m_5c() ? -1 : value;
   }

   private static boolean m_4(BlockPos blockPos, CrystalDamage.DataRecord8 c9) {
      AABB aABB = new AABB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, c9.m_6a_AABB().maxY, blockPos.getZ() + 1);
      return blockPos.getY() < c9.m_6a_AABB().minY && aABB.intersects(c9.m_6a_AABB().inflate(0.3, 0.0, 0.3));
   }

   private static CrystalDamage.DataRecord2 m_2(CrystalDamage.CallbackType c4, Vec3 vec3, Vec3 vec32, CrystalDamage.DataRecord5 c7_c, CrystalDamage.DataRecord3 c6, CrystalDamage.DataRecord4 c72) {
      double doubleValue = c6.m_5_s();
      CrystalDamage.DataRecord2 c5 = c7_c.m_16_boolean() ? m_7(c4, vec3, c7_c.m_7_e(), c7_c.m_1t_C3().m_1a_BlockPos(), doubleValue, c6.m_5r(), c72) : null;
      if (c7_c.m_16_boolean() && c5 == null) {
         return null;
      }

      return switch (c7_c.m_6_j().ordinal()) {
         case 0, 5 -> null;
         case 1, 4 -> {
            CrystalDamage.DataRecord7 c8 = m_3(c4, vec3, vec32, c7_c.m_7_e(), null, doubleValue, c7_c.m_1t_C3(), c6.m_5r());
            yield c8 == null ? null : new CrystalDamage.DataRecord2(c7_c.m_6_j(), c7_c.m_7_e(), c7_c.m_7_e(), null, c8.m_3b(), -1, c72.m_3p(), c72.m_3i());
         }
         case 2 -> c5;
         case 3 -> {
            CrystalDamage.DataRecord7 c82 = m_3(c4, vec3, vec32, c7_c.m_2_q(), null, doubleValue, CrystalDamage.DataRecord.f_0, c6.m_5r());
            yield c82 == null ? null : new CrystalDamage.DataRecord2(CrystalDamage.ModeKind2.MINE, c7_c.m_7_e(), c7_c.m_2_q(), null, c82.m_3b(), -1, c72.m_3p(), c72.m_3i());
         }
         default -> throw new MatchException(null, null);
      };
   }

   private static CrystalDamage.DataRecord2 m_8(CrystalDamage.CallbackType c4, Vec3 vec3, Vec3 vec32, CrystalDamage.DataRecord8 c9, List<CrystalDamage.DataRecord8> c9List, List<CrystalDamage.DataRecord8> c9List2, CrystalDamage.DataRecord3 c6) {
      if (!c6.m_53()) {
         return null;
      }

      CrystalDamage.DataRecord2 c5 = null;
      float floatValue = Float.NEGATIVE_INFINITY;

      for (CrystalDamage.DataRecord8 c92 : c9List) {
         BlockPos blockPos = BlockPos.containing(c92.m_1_h());

         for (Direction direction : Plane.HORIZONTAL) {
            for (int i = 0; i <= 1; i++) {
               BlockPos blockPos2 = blockPos.relative(direction).above(i);
               CrystalDamage.ModeKind c0 = c4.classify(blockPos2);
               if ((c0 == CrystalDamage.ModeKind.SOLID || c0 == CrystalDamage.ModeKind.BASE) && !c4.m_3_boolean(blockPos2) && !m_4(blockPos2, c9)) {
                  int value = c4.m_5_int(blockPos2);
                  if (value >= 0 && value <= 200) {
                     BlockPos blockPos3 = blockPos2.relative(direction).below();
                     CrystalDamage.ModeKind c02 = c4.classify(blockPos3);
                     boolean flag2 = c02 != CrystalDamage.ModeKind.BASE;
                     if (c4.classify(blockPos3.above()) == CrystalDamage.ModeKind.AIR
                        && !c4.m_3_boolean(blockPos3)
                        && !m_4(c4, blockPos3.above())
                        && (!flag2 || c02 != CrystalDamage.ModeKind.SOLID && c6.m_5_z() && !c4.m_4(new AABB(blockPos3), false))) {
                        Vec3 vec33 = topCenter(blockPos3);
                        if (!(distanceSqToBox(vec3, getCrystalBox(vec33)) > c6.m_5p())) {
                           CrystalDamage.DataRecord c3 = new CrystalDamage.DataRecord(blockPos2, flag2 ? blockPos3 : null);
                           CrystalDamage.DataRecord4 c72 = m_5(c4, vec33, c3, c9, c9List, c9List2, c6, 0.0F);
                           if (c72 != null) {
                              float floatValue2 = c72.m_3_u() - value * 0.3F;
                              if (!(floatValue2 <= floatValue)
                                 && (flag2 ? m_7(c4, vec3, blockPos3, blockPos2, c6.m_5_s(), c6.m_5r(), c72) != null : !c4.m_4(getBodyBox(blockPos3), true))
                                 && m_4(c4, vec3, vec32, getCrystalBox(vec33), c6.m_5p(), -1, c3, c6.m_5r()) != null) {
                                 CrystalDamage.DataRecord7 c8 = m_3(c4, vec3, vec32, blockPos2, null, c6.m_5_s(), CrystalDamage.DataRecord.f_0, c6.m_5r());
                                 if (c8 != null) {
                                    floatValue = floatValue2;
                                    c5 = new CrystalDamage.DataRecord2(CrystalDamage.ModeKind2.MINE, blockPos3, blockPos2, null, c8.m_3b(), -1, c72.m_3p(), c72.m_3i());
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return c5;
   }

   private static CrystalDamage.DataRecord2 m_7(CrystalDamage.CallbackType c4, Vec3 vec3, BlockPos blockPos, BlockPos blockPos2, double doubleValue, boolean flag2, CrystalDamage.DataRecord4 c72) {
      CrystalDamage.DataRecord c3 = blockPos2 == null ? CrystalDamage.DataRecord.f_0 : new CrystalDamage.DataRecord(blockPos2, null);
      CrystalDamage.DataRecord2 c5 = null;
      double doubleValue2 = Double.MAX_VALUE;

      for (Direction direction : DIRECTION_ARRAY) {
         BlockPos blockPos3 = blockPos.relative(direction);
         if (!blockPos3.equals(blockPos2) && c4.m_9_boolean(blockPos3)) {
            Direction direction2 = direction.getOpposite();
            AABB aABB = new AABB(blockPos3);
            if (isBehindFace(vec3, aABB, direction2)) {
               Vec3 vec32 = m_6(aABB, direction2, 0.5, 0.5);
               double doubleValue3 = vec3.distanceTo(vec32);
               if (!(doubleValue3 > doubleValue - 0.05) && (flag2 || m_1(c4, vec3, vec32, blockPos, c3))) {
                  CrystalDamage.DataRecord2 c52 = new CrystalDamage.DataRecord2(CrystalDamage.ModeKind2.PLACE_OBSIDIAN, blockPos, blockPos3, direction2, vec32, -1, c72.m_3p(), c72.m_3i());
                  if (direction == Direction.DOWN) {
                     return c52;
                  }

                  if (!(doubleValue3 >= doubleValue2)) {
                     doubleValue2 = doubleValue3;
                     c5 = c52;
                  }
               }
            }
         }
      }

      return c5;
   }

   private static CrystalDamage.DataRecord4 m_5(CrystalDamage.CallbackType c4, Vec3 vec3, CrystalDamage.DataRecord c3, CrystalDamage.DataRecord8 c9, List<CrystalDamage.DataRecord8> c9List, List<CrystalDamage.DataRecord8> c9List2, CrystalDamage.DataRecord3 c6, float floatValue) {
      float floatValue2 = 0.0F;
      float floatValue3 = 0.0F;
      boolean flag2 = false;

      for (CrystalDamage.DataRecord8 c92 : c9List) {
         float floatValue4 = c6.m_5_c() ? Math.min(c6.m_11(), c92.m_2d_float()) : c6.m_11();
         float floatValue5 = m_3(c4, vec3, c3, c92, c6.m_5_u(), floatValue4);
         floatValue2 = Math.max(floatValue2, floatValue5);
         floatValue3 = Math.max(floatValue3, floatValue5 / c92.m_2d_float());
         if (floatValue5 >= c92.m_2d_float()) {
            flag2 = true;
         }
      }

      if (!(floatValue2 < c6.m_11()) || c6.m_5_c() && flag2) {
         float floatValue6 = c6.m_1_f() - floatValue;
         float floatValue7 = m_6(c4, vec3, c3, c9, c6.m_5_u(), floatValue6, 2.0F + floatValue);
         if (floatValue7 < 0.0F) {
            return null;
         }

         for (CrystalDamage.DataRecord8 c93 : c9List2) {
            if (m_6(c4, vec3, c3, c93, c6.m_5_u(), floatValue6, floatValue) < 0.0F) {
               return null;
            }
         }

         return new CrystalDamage.DataRecord4(floatValue2, floatValue7, floatValue2 + (flag2 ? 20.0F : 0.0F) - floatValue7 * 0.35F, flag2, floatValue3);
      } else {
         return null;
      }
   }

   private static float m_3(CrystalDamage.CallbackType c4, Vec3 vec3, CrystalDamage.DataRecord c3, CrystalDamage.DataRecord8 c9, int value, float floatValue) {
      float floatValue2 = m_6(c4, vec3, c3, c9.m_17_Vec3(), c9.m_6k_AABB(), c9, value, floatValue, Float.MAX_VALUE);
      if (floatValue2 < floatValue) {
         return 0.0F;
      } else {
         return c9.m_17_Vec3().equals(c9.m_1_h())
            ? floatValue2
            : Math.min(floatValue2, m_6(c4, vec3, c3, c9.m_1_h(), c9.m_6a_AABB(), c9, value, floatValue, Float.MAX_VALUE));
      }
   }

   private static float m_6(CrystalDamage.CallbackType c4, Vec3 vec3, CrystalDamage.DataRecord c3, CrystalDamage.DataRecord8 c9, int value, float floatValue, float floatValue2) {
      double doubleValue = Math.min(c9.m_1_h().distanceTo(vec3), c9.m_17_Vec3().distanceTo(vec3));
      float floatValue3 = doubleValue > 12.0 ? 0.0F : m_3(calcExplosionDamage(doubleValue / 12.0, 1.0F), c9, value);
      if (floatValue3 <= floatValue && floatValue3 + floatValue2 < c9.m_2d_float()) {
         return floatValue3;
      }

      float floatValue4 = Math.min(floatValue, c9.m_2d_float() - floatValue2);
      float floatValue5 = m_6(c4, vec3, c3, c9.m_1_h(), c9.m_6a_AABB(), c9, value, 0.0F, floatValue4);
      if (!c9.m_17_Vec3().equals(c9.m_1_h())) {
         floatValue5 = Math.max(floatValue5, m_6(c4, vec3, c3, c9.m_17_Vec3(), c9.m_6k_AABB(), c9, value, 0.0F, floatValue4));
      }

      return !(floatValue5 > floatValue) && !(floatValue5 + floatValue2 >= c9.m_2d_float()) ? floatValue5 : -1.0F;
   }

   private static float m_8(Vec3 vec3, List<CrystalDamage.DataRecord8> c9List, CrystalDamage.DataRecord3 c6) {
      float floatValue = 0.0F;
      boolean flag2 = false;

      for (CrystalDamage.DataRecord8 c9 : c9List) {
         double doubleValue = Math.max(c9.m_1_h().distanceTo(vec3), c9.m_17_Vec3().distanceTo(vec3));
         float floatValue2 = m_3(calcExplosionDamage(doubleValue / 12.0, 1.0F), c9, c6.m_5_u());
         boolean flag3 = c6.m_5_c() && floatValue2 >= c9.m_2d_float();
         if (floatValue2 >= c6.m_11() || flag3) {
            flag2 = true;
         }

         floatValue = Math.max(floatValue, flag3 ? floatValue2 + 20.0F : floatValue2);
      }

      return flag2 ? floatValue : 0.0F;
   }

   public static float m_6(CrystalDamage.CallbackType c4, Vec3 vec3, CrystalDamage.DataRecord c3, CrystalDamage.DataRecord8 c9, int value, boolean flag2) {
      float floatValue = m_6(c4, vec3, c3, c9.m_1_h(), c9.m_6a_AABB(), c9, value, 0.0F, Float.MAX_VALUE);
      if (c9.m_17_Vec3().equals(c9.m_1_h())) {
         return floatValue;
      }

      float floatValue2 = m_6(c4, vec3, c3, c9.m_17_Vec3(), c9.m_6k_AABB(), c9, value, 0.0F, Float.MAX_VALUE);
      return flag2 ? Math.max(floatValue, floatValue2) : Math.min(floatValue, floatValue2);
   }

   private static float m_6(CrystalDamage.CallbackType c4, Vec3 vec3, CrystalDamage.DataRecord c3, Vec3 vec32, AABB aABB, CrystalDamage.DataRecord8 c9, int value, float floatValue, float floatValue2) {
      double doubleValue = vec32.distanceTo(vec3) / 12.0;
      if (doubleValue > 1.0) {
         return 0.0F;
      }

      float floatValue3 = floatValue <= 0.0F ? 0.0F : m_3(doubleValue, floatValue, c9, value);
      if (floatValue3 > 1.0F) {
         return 0.0F;
      }

      float floatValue4 = floatValue2 == Float.MAX_VALUE ? 1.0F : m_3(doubleValue, floatValue2, c9, value);
      return m_3(calcExplosionDamage(doubleValue, m_3(c4, vec3, aABB, c3, floatValue3, floatValue4)), c9, value);
   }

   private static float m_3(double doubleValue, float floatValue, CrystalDamage.DataRecord8 c9, int value) {
      if (m_3(calcExplosionDamage(doubleValue, 1.0F), c9, value) < floatValue) {
         return 2.0F;
      }

      float floatValue2 = 0.0F;
      float floatValue3 = 1.0F;

      for (int i = 0; i < 16; i++) {
         float floatValue4 = (floatValue2 + floatValue3) / 2.0F;
         if (m_3(calcExplosionDamage(doubleValue, floatValue4), c9, value) >= floatValue) {
            floatValue3 = floatValue4;
         } else {
            floatValue2 = floatValue4;
         }
      }

      return floatValue3;
   }

   public static float calcExplosionDamage(double doubleValue, float floatValue) {
      double doubleValue2 = (1.0 - doubleValue) * floatValue;
      return (float)((doubleValue2 * doubleValue2 + doubleValue2) / 2.0 * 7.0 * 12.0 + 1.0);
   }

   public static float m_3(float floatValue, CrystalDamage.DataRecord8 c9, int value) {
      floatValue = switch (value) {
         case 0 -> 0.0F;
         case 1 -> Math.min(floatValue / 2.0F + 1.0F, floatValue);
         default -> floatValue;
         case 3 -> floatValue * 3.0F / 2.0F;
      };
      float floatValue2 = 2.0F + c9.m_2_v() / 4.0F;
      float floatValue3 = Mth.clamp(c9.m_2t() - floatValue / floatValue2, c9.m_2t() * 0.2F, 20.0F);
      floatValue *= 1.0F - floatValue3 / 25.0F;
      if (c9.m_3_m() >= 0) {
         floatValue = Math.max(floatValue * (25 - (c9.m_3_m() + 1) * 5) / 25.0F, 0.0F);
      }

      return floatValue <= 0.0F ? 0.0F : CombatRules.getDamageAfterMagicAbsorb(floatValue, c9.m_24());
   }

   public static float m_7(CrystalDamage.CallbackType c4, Vec3 vec3, AABB aABB, CrystalDamage.DataRecord c3) {
      return m_3(c4, vec3, aABB, c3, 0.0F, 1.0F);
   }

   public static float m_3(CrystalDamage.CallbackType c4, Vec3 vec3, AABB aABB, CrystalDamage.DataRecord c3, float floatValue, float floatValue2) {
      double doubleValue = 1.0 / ((aABB.maxX - aABB.minX) * 2.0 + 1.0);
      double doubleValue2 = 1.0 / ((aABB.maxY - aABB.minY) * 2.0 + 1.0);
      double doubleValue3 = 1.0 / ((aABB.maxZ - aABB.minZ) * 2.0 + 1.0);
      double doubleValue4 = (1.0 - Math.floor(1.0 / doubleValue) * doubleValue) / 2.0;
      double doubleValue5 = (1.0 - Math.floor(1.0 / doubleValue3) * doubleValue3) / 2.0;
      ArrayList<Vec3> vec3List = new ArrayList<>();

      for (double doubleValue6 = 0.0; doubleValue6 <= 1.0; doubleValue6 += doubleValue) {
         for (double doubleValue7 = 0.0; doubleValue7 <= 1.0; doubleValue7 += doubleValue2) {
            for (double doubleValue8 = 0.0; doubleValue8 <= 1.0; doubleValue8 += doubleValue3) {
               vec3List.add(
                  new Vec3(Mth.lerp(doubleValue6, aABB.minX, aABB.maxX) + doubleValue4, Mth.lerp(doubleValue7, aABB.minY, aABB.maxY), Mth.lerp(doubleValue8, aABB.minZ, aABB.maxZ) + doubleValue5)
               );
            }
         }
      }

      int value = vec3List.size();
      if (value == 0) {
         return 0.0F;
      }

      int value2 = 0;

      for (int i = 0; i < value; i++) {
         if (!c4.m_6((Vec3)vec3List.get(i), vec3, c3)) {
            value2++;
         }

         if (value2 + value - i - 1 < floatValue * value) {
            return 0.0F;
         }

         if (value2 > floatValue2 * value) {
            return 1.0F;
         }
      }

      return (float)value2 / value;
   }

   public static Vec3 m_4(CrystalDamage.CallbackType c4, Vec3 vec3, Vec3 vec32, AABB aABB, double doubleValue, int value, CrystalDamage.DataRecord c3, boolean flag2) {
      if (aABB.contains(vec3)) {
         return aABB.getCenter();
      }

      AABB aABB2 = aABB.deflate(0.02);
      ArrayList<Vec3> vec3List = new ArrayList<>();

      for (Direction direction : Direction.values()) {
         if (isBehindFace(vec3, aABB, direction)) {
            int value2 = vec3List.size();

            for (int i = 0; i < 5; i++) {
               for (int i2 = 0; i2 < 5; i2++) {
                  Vec3 vec33 = m_6(aABB2, direction, 0.1 + i * 0.2, 0.1 + i2 * 0.2);
                  if (vec33.distanceTo(vec3) <= doubleValue - 0.05) {
                     vec3List.add(vec33);
                  }
               }
            }

            if (vec3List.size() != value2) {
               Vec3 vec34 = m_4(vec3, vec32, aABB2, direction, doubleValue - 0.05);
               if (vec34 != null) {
                  vec3List.add(vec34);
               }
            }
         }
      }

      vec3List.sort(Comparator.comparingDouble(vec35 -> -vec32.dot(vec35.subtract(vec3).normalize())));
      if (flag2) {
         return vec3List.isEmpty() ? null : (Vec3)vec3List.getFirst();
      }

      for (Vec3 vec36 : vec3List) {
         if (!c4.m_1(vec3, vec36, c3) && !c4.m_3(vec3, vec36, value)) {
            return vec36;
         }
      }

      return null;
   }

   private static Vec3 m_4(Vec3 vec3, Vec3 vec32, AABB aABB, Direction direction, double doubleValue) {
      Vec3 vec33 = closestPointOnFace(vec3, aABB, direction);
      if (vec33.distanceTo(vec3) > doubleValue) {
         return null;
      }

      Vec3 vec34 = new Vec3(Math.abs(direction.getStepX()), Math.abs(direction.getStepY()), Math.abs(direction.getStepZ()));
      double doubleValue2 = vec32.dot(vec34);
      if (Math.abs(doubleValue2) < 1.0E-6) {
         return vec33;
      }

      double doubleValue3 = (vec33.dot(vec34) - vec3.dot(vec34)) / doubleValue2;
      if (doubleValue3 <= 0.0) {
         return vec33;
      }

      Vec3 vec35 = closestPointOnFace(vec3.add(vec32.scale(doubleValue3)), aABB, direction);
      if (vec35.distanceTo(vec3) <= doubleValue) {
         return vec35;
      }

      double doubleValue4 = 0.0;
      double doubleValue5 = 1.0;

      for (int i = 0; i < 12; i++) {
         double doubleValue6 = (doubleValue4 + doubleValue5) * 0.5;
         if (vec33.lerp(vec35, doubleValue6).distanceTo(vec3) <= doubleValue) {
            doubleValue4 = doubleValue6;
         } else {
            doubleValue5 = doubleValue6;
         }
      }

      return vec33.lerp(vec35, doubleValue4);
   }

   private static Vec3 closestPointOnFace(Vec3 vec3, AABB aABB, Direction direction) {
      double doubleValue = Mth.clamp(vec3.x, aABB.minX, aABB.maxX);
      double doubleValue2 = Mth.clamp(vec3.y, aABB.minY, aABB.maxY);
      double doubleValue3 = Mth.clamp(vec3.z, aABB.minZ, aABB.maxZ);

      return switch (direction) {
         case EAST -> new Vec3(aABB.maxX, doubleValue2, doubleValue3);
         case WEST -> new Vec3(aABB.minX, doubleValue2, doubleValue3);
         case UP -> new Vec3(doubleValue, aABB.maxY, doubleValue3);
         case DOWN -> new Vec3(doubleValue, aABB.minY, doubleValue3);
         case SOUTH -> new Vec3(doubleValue, doubleValue2, aABB.maxZ);
         case NORTH -> new Vec3(doubleValue, doubleValue2, aABB.minZ);
      };
   }

   public static CrystalDamage.DataRecord7 m_2(Vec3 vec3, Vec3 vec32, BlockPos blockPos, Direction direction, double doubleValue) {
      return m_7(vec3, vec32, blockPos, direction, doubleValue, (vec33, direction2) -> true);
   }

   public static Vec3 offsetTowards(Vec3 vec3, Direction direction) {
      return vec3.add(direction.getStepX() * 0.001, direction.getStepY() * 0.001, direction.getStepZ() * 0.001);
   }

   private static CrystalDamage.DataRecord7 m_3(CrystalDamage.CallbackType c4, Vec3 vec3, Vec3 vec32, BlockPos blockPos, Direction direction, double doubleValue, CrystalDamage.DataRecord c3, boolean flag2) {
      return m_7(vec3, vec32, blockPos, direction, doubleValue, (vec33, direction2) -> flag2 || !c4.m_1(vec3, offsetTowards(vec33, direction2), c3));
   }

   private static boolean m_1(CrystalDamage.CallbackType c4, Vec3 vec3, Vec3 vec32, BlockPos blockPos, CrystalDamage.DataRecord c3) {
      Vec3 vec33 = new AABB(blockPos).clip(vec3, vec32).orElse(null);
      if (vec33 == null) {
         return true;
      }

      Vec3 vec34 = vec33.subtract(vec32.subtract(vec3).normalize().scale(0.001));
      return !c4.m_1(vec3, vec34, c3);
   }

   public static CrystalDamage.DataRecord7 m_7(Vec3 vec3, Vec3 vec32, BlockPos blockPos, Direction direction, double doubleValue, BiPredicate<Vec3, Direction> biPredicate) {
      AABB aABB = new AABB(blockPos);
      CrystalDamage.DataRecord7 c8 = null;
      double doubleValue2 = Double.NEGATIVE_INFINITY;

      for (Direction direction2 : Direction.values()) {
         if ((direction == null || direction2 == direction) && isBehindFace(vec3, aABB, direction2)) {
            for (int i = 0; i < 3; i++) {
               for (int i2 = 0; i2 < 3; i2++) {
                  Vec3 vec33 = m_6(aABB, direction2, 0.25 + i * 0.25, 0.25 + i2 * 0.25);
                  Vec3 vec34 = vec33.subtract(vec3);
                  double doubleValue3 = vec34.length();
                  if (!(doubleValue3 > doubleValue - 0.05)) {
                     double doubleValue4 = vec32.dot(vec34) / doubleValue3;
                     if (!(doubleValue4 <= doubleValue2) && biPredicate.test(vec33, direction2)) {
                        doubleValue2 = doubleValue4;
                        c8 = new CrystalDamage.DataRecord7(vec33, direction2);
                     }
                  }
               }
            }
         }
      }

      return c8;
   }

   public static boolean isBehindFace(Vec3 vec3, AABB aABB, Direction direction) {
      return switch (direction) {
         case EAST -> vec3.x > aABB.maxX;
         case WEST -> vec3.x < aABB.minX;
         case UP -> vec3.y > aABB.maxY;
         case DOWN -> vec3.y < aABB.minY;
         case SOUTH -> vec3.z > aABB.maxZ;
         case NORTH -> vec3.z < aABB.minZ;
      };
   }

   private static Vec3 m_6(AABB aABB, Direction direction, double doubleValue, double doubleValue2) {
      return switch (direction.getAxis()) {
         case X -> new Vec3(direction == Direction.EAST ? aABB.maxX : aABB.minX, Mth.lerp(doubleValue, aABB.minY, aABB.maxY), Mth.lerp(doubleValue2, aABB.minZ, aABB.maxZ));
         case Y -> new Vec3(Mth.lerp(doubleValue, aABB.minX, aABB.maxX), direction == Direction.UP ? aABB.maxY : aABB.minY, Mth.lerp(doubleValue2, aABB.minZ, aABB.maxZ));
         case Z -> new Vec3(Mth.lerp(doubleValue, aABB.minX, aABB.maxX), Mth.lerp(doubleValue2, aABB.minY, aABB.maxY), direction == Direction.SOUTH ? aABB.maxZ : aABB.minZ);
      };
   }

   private static double distanceSqToBox(Vec3 vec3, AABB aABB) {
      double doubleValue = Math.max(Math.max(aABB.minX - vec3.x, vec3.x - aABB.maxX), 0.0);
      double doubleValue2 = Math.max(Math.max(aABB.minY - vec3.y, vec3.y - aABB.maxY), 0.0);
      double doubleValue3 = Math.max(Math.max(aABB.minZ - vec3.z, vec3.z - aABB.maxZ), 0.0);
      return Math.sqrt(doubleValue * doubleValue + doubleValue2 * doubleValue2 + doubleValue3 * doubleValue3);
   }

   public static Vec3 topCenter(BlockPos blockPos) {
      return new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5);
   }

   public static AABB getCrystalBox(Vec3 vec3) {
      return new AABB(vec3.x - 1.0, vec3.y, vec3.z - 1.0, vec3.x + 1.0, vec3.y + 2.0, vec3.z + 1.0);
   }

   public static AABB getBodyBox(BlockPos blockPos) {
      return new AABB(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 3, blockPos.getZ() + 1);
   }

   static {
      m_7_void(true);
   }

   public static void m_7_void(boolean flag2) {
      flag = flag2;
   }

   public static boolean m_6_boolean() {
      return flag;
   }

   public static boolean m_8_boolean() {
      return false;
   }

   public enum ModeKind {
      AIR,
      REPLACEABLE,
      BASE,
      SOLID;

      private static String a(byte[] byteArray) {
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

   private static final class StateHolder {
      private CrystalDamage.DataRecord2 c5;
      private CrystalDamage.DataRecord4 c72;
      private float floatValue;

      private StateHolder() {
         this.floatValue = Float.intBitsToFloat((int)4606176206738423808L);
      }
   }

   public enum ModeKind2 {
      BREAK,
      PLACE_CRYSTAL,
      PLACE_OBSIDIAN,
      MINE,
      WAIT,
      HOLD;

      private static String a(byte[] byteArray) {
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

   public record DataRecord(BlockPos f_1a, BlockPos f_1f) {
      public static final CrystalDamage.DataRecord f_0 = new CrystalDamage.DataRecord(null, null);
      private static String[] f_3;

      public BlockPos m_1a_BlockPos() {
         return this.f_1a;
      }

      public BlockPos m_1f_BlockPos() {
         return this.f_1f;
      }

      static {
         String[] stringArray = new String[3];
         m_4(stringArray);
      }

      public static void m_4(String[] stringArray) {
         f_3 = stringArray;
      }

      public static String[] m_3_StringArr() {
         return f_3;
      }
   }

   public interface CallbackType {
      CrystalDamage.ModeKind classify(BlockPos blockPos);

      boolean m_9_boolean(BlockPos blockPos);

      boolean m_6(Vec3 vec3, Vec3 vec32, CrystalDamage.DataRecord c3);

      boolean m_1(Vec3 vec3, Vec3 vec32, CrystalDamage.DataRecord c3);

      boolean m_4(AABB aABB, boolean flag2);

      boolean m_3(Vec3 vec3, Vec3 vec32, int value);

      int m_5_int(BlockPos blockPos);

      boolean m_3_boolean(BlockPos blockPos);

      boolean m_6_boolean(BlockPos blockPos);

      boolean m_9_boolean(AABB aABB);
   }

   public record DataRecord2(CrystalDamage.ModeKind2 f_5l, BlockPos f_1_s, BlockPos f_1_r, Direction f_25, Vec3 f_38, int f_3r, float f_50, float f_5q) {
      public CrystalDamage.ModeKind2 m_5l() {
         return this.f_5l;
      }

      public BlockPos m_1_s_BlockPos() {
         return this.f_1_s;
      }

      public BlockPos m_1_r_BlockPos() {
         return this.f_1_r;
      }

      public Direction m_25() {
         return this.f_25;
      }

      public Vec3 m_38() {
         return this.f_38;
      }

      public int m_3r() {
         return this.f_3r;
      }

      public float m_50() {
         return this.f_50;
      }

      public float m_5q() {
         return this.f_5q;
      }
   }

   public record DataRecord3(
      double f_5_s, double f_5p, float f_11, float f_1_f, boolean f_5_c, boolean f_51, boolean f_5_z, boolean f_53, int f_5c, int f_5_u, boolean f_5r
   ) {
      public double m_5_s() {
         return this.f_5_s;
      }

      public double m_5p() {
         return this.f_5p;
      }

      public float m_11() {
         return this.f_11;
      }

      public float m_1_f() {
         return this.f_1_f;
      }

      public boolean m_5_c() {
         return this.f_5_c;
      }

      public boolean m_51() {
         return this.f_51;
      }

      public boolean m_5_z() {
         return this.f_5_z;
      }

      public boolean m_53() {
         return this.f_53;
      }

      public int m_5c() {
         return this.f_5c;
      }

      public int m_5_u() {
         return this.f_5_u;
      }

      public boolean m_5r() {
         return this.f_5r;
      }
   }

   private record DataRecord4(float f_3p, float f_3i, float f_3_u, boolean f_1h, float f_3o) {
      public float m_3p() {
         return this.f_3p;
      }

      public float m_3i() {
         return this.f_3i;
      }

      public float m_3_u() {
         return this.f_3_u;
      }

      public boolean m_1h_boolean() {
         return this.f_1h;
      }

      public float m_3o() {
         return this.f_3o;
      }
   }

   private record DataRecord5(CrystalDamage.ModeKind2 f_6_j, BlockPos f_7_e, BlockPos f_2_q, CrystalDamage.DataRecord f_1t, float f_2u, boolean f_16, float f_2_x) {
      public CrystalDamage.ModeKind2 m_6_j() {
         return this.f_6_j;
      }

      public BlockPos m_7_e() {
         return this.f_7_e;
      }

      public BlockPos m_2_q() {
         return this.f_2_q;
      }

      public CrystalDamage.DataRecord m_1t_C3() {
         return this.f_1t;
      }

      public float m_2u() {
         return this.f_2u;
      }

      public boolean m_16_boolean() {
         return this.f_16;
      }

      public float m_2_x() {
         return this.f_2_x;
      }
   }

   public record DataRecord6(int f_2_t, Vec3 f_5n) {
      public int m_2_t_int() {
         return this.f_2_t;
      }

      public Vec3 m_5n() {
         return this.f_5n;
      }
   }

   public record DataRecord7(Vec3 f_3b, Direction f_3_c) {
      public Vec3 m_3b() {
         return this.f_3b;
      }

      public Direction m_3_c() {
         return this.f_3_c;
      }
   }

   public record DataRecord8(Vec3 f_1_h, AABB f_6a, Vec3 f_17, AABB f_6k, float f_2d, float f_2t, float f_2_v, float f_24, int f_3_m) {
      public Vec3 m_1_h() {
         return this.f_1_h;
      }

      public AABB m_6a_AABB() {
         return this.f_6a;
      }

      public Vec3 m_17_Vec3() {
         return this.f_17;
      }

      public AABB m_6k_AABB() {
         return this.f_6k;
      }

      public float m_2d_float() {
         return this.f_2d;
      }

      public float m_2t() {
         return this.f_2t;
      }

      public float m_2_v() {
         return this.f_2_v;
      }

      public float m_24() {
         return this.f_24;
      }

      public int m_3_m() {
         return this.f_3_m;
      }
   }
}
