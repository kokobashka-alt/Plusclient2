package wtf.onetap.combat.crystal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;
import wtf.onetap.util.C9_q;
import wtf.onetap.util.LatencyStats;
import wtf.onetap.util.MinecraftAccess;

public class CrystalPlacer implements MinecraftAccess {
   private static final LatencyStats.StateHolder C0 = LatencyStats.getOrCreate("CrystalAura findPlace");
   private static final LatencyStats.StateHolder C02 = LatencyStats.getOrCreate("CrystalAura findBreak");
   private static final LatencyStats.StateHolder C03 = LatencyStats.getOrCreate("CrystalAura candidates");
   private static final LatencyStats.StateHolder C04 = LatencyStats.getOrCreate("CrystalAura rank");
   private static final LatencyStats.StateHolder C05 = LatencyStats.getOrCreate("CrystalAura possible");
   private static final LatencyStats.StateHolder C06 = LatencyStats.getOrCreate("CrystalAura evaluate");
   private static final LatencyStats.StateHolder C07 = LatencyStats.getOrCreate("CrystalAura native");
   private static double[] doubleValueArray = new double[0];
   private static double[] doubleValueArray2 = new double[0];
   private static double[] doubleValueArray3 = new double[0];
   private static boolean[] flagArray = new boolean[0];
   private static final CrystalPlacer.DataRecord5[] C7_ARRAY = new CrystalPlacer.DataRecord5[256];
   private static int value = -1;
   private static final List<EndCrystal> END_CRYSTAL_LIST = new ArrayList<>();
   private static long longValue;
   private static final Comparator<CrystalPlacer.DataRecord7> COMPARATOR = Comparator.<CrystalPlacer.DataRecord7>comparingInt(c9 -> c9.m_6z_C8().m_6a_int())
      .thenComparing(c92 -> !c92.m_6z_C8().m_2v())
      .thenComparingDouble(c93 -> c93.m_6z_C8().m_6f())
      .thenComparingDouble(c94 -> -c94.m_6z_C8().m_62_float())
      .thenComparingLong(c95 -> c95.m_6d_BlockPos().asLong());
   private static final Map<BlockState, Boolean> BLOCK_STATE_MAP = new HashMap<>();
   private static final Map<Float, Boolean> FLOAT_MAP = new HashMap<>();
   private static int value2 = -1;
   private static int value3;

   private CrystalPlacer() {
   }

   public static void setDeadline(long longValue2) {
      longValue = longValue2 <= 0L ? 0L : System.nanoTime() + longValue2;
   }

   private static boolean isDelayExpired() {
      return longValue != 0L && System.nanoTime() > longValue;
   }

   private static double[] ensureCapacity(double[] doubleArray, int value4) {
      return doubleArray.length >= value4 ? doubleArray : new double[value4 + 63 & -64];
   }

   private static boolean nativeCheckBoolean(double[] doubleArray) {
      long longValue2 = System.nanoTime();

      try {
         return C9_q.m_0(6, doubleArray);
      } finally {
         C07.recordSince(longValue2);
      }
   }

   @Nullable
   public static EndCrystal m_4(
      List<Player> playerList, Vec3 vec3, float floatValue, float floatValue2, float floatValue3, float floatValue4, List<Player> playerList2, Set<Integer> integerSet, Predicate<Vec3> predicate
   ) {
      if (MINECRAFT.level != null && MINECRAFT.player != null && !playerList.isEmpty()) {
         long longValue2 = System.nanoTime();

         try {
            EndCrystal endCrystal = null;
            float floatValue5 = -Float.MAX_VALUE;
            var endCrystalList = getCrystalsNear(vec3, floatValue, predicate);

            for (int i = 0; i < endCrystalList.size() && !isDelayExpired(); i++) {
               EndCrystal endCrystal2 = (EndCrystal)endCrystalList.get(i);
               if (!integerSet.contains(endCrystal2.getId())) {
                  float floatValue6 = m_5(endCrystal2.position(), playerList, floatValue3);
                  if (!(floatValue6 < 0.0F)) {
                     float floatValue7 = CrystalMining.m_3(endCrystal2.position(), MINECRAFT.player);
                     if (m_2_boolean(floatValue7, floatValue4, floatValue2)) {
                        float floatValue8 = m_4(endCrystal2.position(), playerList2, null, List.of(), floatValue2);
                        if (!(floatValue8 < 0.0F)) {
                           float floatValue9 = m_2_float(floatValue6, floatValue7, floatValue8);
                           if (floatValue9 > floatValue5) {
                              floatValue5 = floatValue9;
                              endCrystal = endCrystal2;
                           }
                        }
                     }
                  }
               }
            }

            return endCrystal;
         } finally {
            C02.recordSince(longValue2);
         }
      } else {
         return null;
      }
   }

   private static List<EndCrystal> getCrystalsNear(Vec3 vec3, float floatValue, Predicate<Vec3> predicate) {
      if (value == MINECRAFT.player.tickCount) {
         return END_CRYSTAL_LIST;
      }

      value = MINECRAFT.player.tickCount;
      END_CRYSTAL_LIST.clear();

      for (Entity entity : MINECRAFT.level.entitiesForRendering()) {
         if (entity instanceof EndCrystal endCrystal && !predicate.test(endCrystal.position()) && m_4(endCrystal, vec3, floatValue) != null) {
            END_CRYSTAL_LIST.add(endCrystal);
         }
      }

      return END_CRYSTAL_LIST;
   }

   @Nullable
   public static CrystalPlacer.DataRecord2 m_7(
      List<CrystalPlacer.DataRecord4> c5List,
      Vec3 vec3,
      float floatValue,
      float floatValue2,
      float floatValue3,
      float floatValue4,
      boolean flag,
      boolean flag2,
      float floatValue5,
      List<Player> playerList,
      @Nullable BlockPos blockPos,
      Collection<BlockPos> blockPosList,
      Set<Integer> integerSet,
      Predicate<Vec3> predicate
   ) {
      if (MINECRAFT.level != null && MINECRAFT.player != null && !c5List.isEmpty()) {
         long longValue2 = System.nanoTime();
         Collection list = blockPosList == null ? List.of() : blockPosList;
         CrystalMining.onTickStart();

         try {
            List list2 = flag2 ? m_7(c5List, vec3, floatValue, list) : List.of();
            return m_1(m_2(c5List, vec3, floatValue, floatValue2, flag, flag2, blockPos, list, integerSet, predicate), c5List, list2, floatValue3, floatValue4, floatValue5, playerList);
         } finally {
            CrystalMining.tickCooldown();
            C0.recordSince(longValue2);
         }
      } else {
         return null;
      }
   }

   public static CrystalPlacer.DataRecord2 m_7(
      List<CrystalPlacer.DataRecord4> c5List,
      Vec3 vec3,
      float floatValue,
      float floatValue2,
      float floatValue3,
      float floatValue4,
      float floatValue5,
      boolean flag,
      boolean flag2,
      float floatValue6,
      List<Player> playerList,
      @Nullable BlockPos blockPos,
      Collection<BlockPos> blockPosList,
      Set<Integer> integerSet,
      Predicate<Vec3> predicate
   ) {
      if (MINECRAFT.level != null && MINECRAFT.player != null && !c5List.isEmpty()) {
         long longValue2 = System.nanoTime();
         Collection list = blockPosList == null ? List.of() : blockPosList;
         CrystalMining.onTickStart();

         try {
            List list2 = flag2 ? m_7(c5List, vec3, floatValue, list) : List.of();
            var list3 = m_2(c5List, vec3, floatValue, floatValue2, flag, flag2, blockPos, list, integerSet, predicate);
            CrystalPlacer.DataRecord2 c2 = m_1(list3, c5List, list2, floatValue3, floatValue4, floatValue6, playerList);
            return c2 != null ? c2 : m_1(list3, c5List, list2, floatValue3, floatValue5, floatValue6, playerList);
         } finally {
            CrystalMining.tickCooldown();
            C0.recordSince(longValue2);
         }
      } else {
         return null;
      }
   }

   private static List<CrystalPlacer.DataRecord7> m_2(
      List<CrystalPlacer.DataRecord4> c5List,
      Vec3 vec3,
      float floatValue,
      float floatValue2,
      boolean flag,
      boolean flag2,
      @Nullable BlockPos blockPos,
      Collection<BlockPos> blockPosList,
      Set<Integer> integerSet,
      Predicate<Vec3> predicate
   ) {
      HashMap map = new HashMap();
      long longValue2 = System.nanoTime();

      label53:
      for (CrystalPlacer.DataRecord4 c5 : c5List) {
         BlockPos blockPos2 = BlockPos.containing(c5.m_5_l());

         for (int i = -3; i <= 3; i++) {
            for (int i2 = -3; i2 <= 3; i2++) {
               if (isDelayExpired()) {
                  break label53;
               }

               if (!m_7(vec3, blockPos2.getX() + i, blockPos2.getZ() + i2, floatValue)) {
                  for (int i3 = -4; i3 <= 1; i3++) {
                     BlockPos blockPos3 = blockPos2.offset(i, i3, i2);
                     if (!map.containsKey(blockPos3) && !blockPosList.contains(blockPos3)) {
                        CrystalPlacer.DataRecord7 c9 = m_7(blockPos3, blockPos2, vec3, floatValue, floatValue2, flag, flag2, blockPos, blockPosList, integerSet, c5List, predicate);
                        if (c9 != null) {
                           map.put(blockPos3, c9);
                        }
                     }
                  }
               }
            }
         }
      }

      C03.recordSince(longValue2);
      ArrayList list = new ArrayList(map.values());
      long longValue3 = System.nanoTime();
      list.sort(COMPARATOR);
      C04.recordSince(longValue3);
      return list;
   }

   @Nullable
   private static CrystalPlacer.DataRecord2 m_1(List<CrystalPlacer.DataRecord7> c9List, List<CrystalPlacer.DataRecord4> c5List, List<List<BlockPos>> listList, float floatValue, float floatValue2, float floatValue3, List<Player> playerList) {
      boolean[] flagArray2 = m_8(c9List, c5List, floatValue2);
      CrystalPlacer.DataRecord2 c2 = null;
      CrystalPlacer.DataRecord6 c8 = null;
      int value4 = 0;
      int value5 = 0;

      while (value5 < c9List.size() && !isDelayExpired()) {
         int value6 = value5;

         int value7;
         for (value7 = 0; value6 < c9List.size() && value6 - value5 < 256 && value4 + value7 < 100; value6++) {
            CrystalPlacer.DataRecord7 c9 = (CrystalPlacer.DataRecord7)c9List.get(value6);
            if (c8 != null && c8.m_7(c9.m_6z_C8())) {
               break;
            }

            if (flagArray2[value6]) {
               value7++;
            }
         }

         if (value6 == value5) {
            break;
         }

         value4 += value7;
         m_5(c9List, value5, value6, flagArray2, c5List, listList, floatValue, floatValue2, floatValue3, playerList);

         for (int i = value5; i < value6; i++) {
            CrystalPlacer.DataRecord5 c7 = C7_ARRAY[i - value5];
            if (c7 != null && (c8 == null || c7.m_5_p().compareTo(c8) < 0)) {
               c8 = c7.m_5_p();
               c2 = c7.m_7d();
            }
         }

         value5 = value6;
      }

      return c2;
   }

   @Nullable
   public static CrystalPlacer.DataRecord2 m_1(
      BlockPos blockPos,
      List<CrystalPlacer.DataRecord4> c5List,
      Vec3 vec3,
      float floatValue,
      float floatValue2,
      float floatValue3,
      float floatValue4,
      boolean flag,
      boolean flag2,
      float floatValue5,
      List<Player> playerList,
      Collection<BlockPos> blockPosList,
      Set<Integer> integerSet,
      Predicate<Vec3> predicate
   ) {
      if (MINECRAFT.level != null && MINECRAFT.player != null && !c5List.isEmpty() && !blockPosList.contains(blockPos)) {
         CrystalMining.onTickStart();

         try {
            for (CrystalPlacer.DataRecord4 c5 : c5List) {
               CrystalPlacer.DataRecord7 c9 = m_7(blockPos, BlockPos.containing(c5.m_5_l()), vec3, floatValue, floatValue2, flag, flag2, blockPos, blockPosList, integerSet, c5List, predicate);
               if (c9 != null) {
                  if (!m_1(topCenter(blockPos), c5List, floatValue4)) {
                     return null;
                  }

                  CrystalPlacer.DataRecord5 c7 = m_1(c9, c5List, flag2 ? m_7(c5List, vec3, floatValue, blockPosList) : List.of(), floatValue3, floatValue4, floatValue5, playerList);
                  return c7 == null ? null : c7.m_7d();
               }
            }

            return null;
         } finally {
            CrystalMining.tickCooldown();
         }
      } else {
         return null;
      }
   }

   private static boolean[] m_8(List<CrystalPlacer.DataRecord7> c9List, List<CrystalPlacer.DataRecord4> c5List, float floatValue) {
      int value4 = c9List.size();
      if (flagArray.length < value4) {
         flagArray = new boolean[Math.max(value4, flagArray.length * 2)];
      }

      long longValue2 = System.nanoTime();

      try {
         for (short shortValue = 0; shortValue < value4; shortValue += 256) {
            int value5 = Math.min(256, value4 - shortValue);
            boolean flag = m_8(c9List, shortValue, value5, c5List, floatValue);
            if (!flag) {
               for (int i = shortValue; i < shortValue + value5; i++) {
                  flagArray[i] = m_1(topCenter(((CrystalPlacer.DataRecord7)c9List.get(i)).m_6d_BlockPos()), c5List, floatValue);
               }
            }
         }

         return flagArray;
      } finally {
         C05.recordSince(longValue2);
      }
   }

   private static boolean m_8(List<CrystalPlacer.DataRecord7> c9List, int value4, int value5, List<CrystalPlacer.DataRecord4> c5List, float floatValue) {
      int value6 = c5List.size();
      double[] doubleArray = doubleValueArray = ensureCapacity(doubleValueArray, 4 + value6 * 6 + value5 * value6);
      doubleArray[0] = 12.0;
      doubleArray[1] = value5;
      doubleArray[2] = value6;
      doubleArray[3] = floatValue;

      for (int i = 0; i < value6; i++) {
         CrystalMining.m_6(((CrystalPlacer.DataRecord4)c5List.get(i)).m_1n(), doubleArray, 4 + i * 6);
      }

      int value7 = 4 + value6 * 6;

      for (int i2 = value4; i2 < value4 + value5; i2++) {
         Vec3 vec3 = topCenter(((CrystalPlacer.DataRecord7)c9List.get(i2)).m_6d_BlockPos());

         for (int i3 = 0; i3 < value6; i3++) {
            Vec3 vec32 = ((CrystalPlacer.DataRecord4)c5List.get(i3)).m_5_l();
            doubleArray[value7++] = vec32.distanceTo(vec3);
         }
      }

      if (nativeCheckBoolean(doubleArray) && doubleArray[0] == 1.0) {
         for (int i4 = 0; i4 < value5; i4++) {
            flagArray[value4 + i4] = doubleArray[2 + i4] != 0.0;
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean m_1(Vec3 vec3, List<CrystalPlacer.DataRecord4> c5List, float floatValue) {
      for (CrystalPlacer.DataRecord4 c5 : c5List) {
         if (m_8(CrystalMining.m_9(vec3, c5.m_1n(), c5.m_3_i()), c5.m_1n(), floatValue)) {
            return true;
         }
      }

      return false;
   }

   private static int countOpponents(List<Player> playerList) {
      int value4 = 0;
      if (playerList != null) {
         for (Player player : playerList) {
            if (player != MINECRAFT.player && player.isAlive()) {
               value4++;
            }
         }
      }

      return value4;
   }

   private static void m_5(
      List<CrystalPlacer.DataRecord7> c9List,
      int value4,
      int value5,
      boolean[] flagArray2,
      List<CrystalPlacer.DataRecord4> c5List,
      List<List<BlockPos>> listList,
      float floatValue,
      float floatValue2,
      float floatValue3,
      List<Player> playerList
   ) {
      ArrayList<CrystalPlacer.DataRecord7> c9List2 = new ArrayList<>(value5 - value4);
      int[] intArray = new int[value5 - value4];

      for (int i = value4; i < value5; i++) {
         C7_ARRAY[i - value4] = null;
         if (flagArray2[i]) {
            intArray[c9List2.size()] = i - value4;
            c9List2.add((CrystalPlacer.DataRecord7)c9List.get(i));
         }
      }

      if (!c9List2.isEmpty()) {
         long longValue2 = System.nanoTime();

         try {
            int value6 = countOpponents(playerList);
            CrystalPlacer.DataRecord3[] c4Array = c9List2.size() > 1 && c5List.size() <= 3 && value6 <= 3 ? m_6(c9List2, c5List, value6, floatValue, floatValue2, floatValue3, playerList) : null;
            if (c4Array == null) {
               c4Array = new CrystalPlacer.DataRecord3[c9List2.size()];

               for (int i2 = 0; i2 < c9List2.size(); i2++) {
                  CrystalPlacer.DataRecord7 c9 = (CrystalPlacer.DataRecord7)c9List2.get(i2);
                  c4Array[i2] = m_9(c9, c5List, c9.m_4i_List(), -1, 0.0F, floatValue, floatValue2, floatValue3, playerList);
               }
            }

            CrystalPlacer.DataRecord5[] c7Array = m_1(c9List2, c4Array, c5List, listList, floatValue, floatValue2, floatValue3, playerList);

            for (int i3 = 0; i3 < c9List2.size(); i3++) {
               C7_ARRAY[intArray[i3]] = c7Array[i3];
            }
         } finally {
            C06.recordSince(longValue2);
         }
      }
   }

   @Nullable
   private static CrystalPlacer.DataRecord3[] m_6(List<CrystalPlacer.DataRecord7> c9List, List<CrystalPlacer.DataRecord4> c5List, int value4, float floatValue, float floatValue2, float floatValue3, List<Player> playerList) {
      int value5 = c5List.size();
      int value6 = 1 + value5 + value4;
      int value7 = value5 * 9 + 4 + value4 * 4;
      double[] doubleArray = doubleValueArray2 = ensureCapacity(doubleValueArray2, 9 + value6 * 6 + c9List.size() * value7);
      m_7(doubleArray, 13, c9List.size(), value5, value4, floatValue2, floatValue3, floatValue, -1, 0.0F);
      m_3(doubleArray, c5List, playerList);
      int value8 = 9 + value6 * 6;

      for (CrystalPlacer.DataRecord7 c9 : c9List) {
         value8 = m_8(c9, c5List, c9.m_4i_List(), playerList, doubleArray, value8);
      }

      if (nativeCheckBoolean(doubleArray) && doubleArray[0] == 1.0) {
         CrystalPlacer.DataRecord3[] c4Array = new CrystalPlacer.DataRecord3[c9List.size()];

         for (int i = 0; i < c9List.size(); i++) {
            c4Array[i] = m_6(doubleArray, 2 + i * 5);
         }

         return c4Array;
      } else {
         return null;
      }
   }

   private static CrystalPlacer.DataRecord3 m_6(double[] doubleArray, int value4) {
      return new CrystalPlacer.DataRecord3((int)doubleArray[value4], (float)doubleArray[value4 + 1], (float)doubleArray[value4 + 2], (float)doubleArray[value4 + 3], doubleArray[value4 + 4] != 0.0);
   }

   private static void m_7(double[] doubleArray, int value4, int value5, int value6, int value7, float floatValue, float floatValue2, float floatValue3, int value8, float floatValue4) {
   }

   private static void m_3(double[] doubleArray, List<CrystalPlacer.DataRecord4> c5List, List<Player> playerList) {
      CrystalMining.m_6(MINECRAFT.player, doubleArray, 9);

      for (int i = 0; i < c5List.size(); i++) {
         CrystalMining.m_6(((CrystalPlacer.DataRecord4)c5List.get(i)).m_1n(), doubleArray, 9 + (1 + i) * 6);
      }

      int value4 = 1 + c5List.size();
      if (playerList != null) {
         for (Player player : playerList) {
            if (player != MINECRAFT.player && player.isAlive()) {
               CrystalMining.m_6(player, doubleArray, 9 + value4++ * 6);
            }
         }
      }
   }

   private static int m_8(CrystalPlacer.DataRecord7 c9, List<CrystalPlacer.DataRecord4> c5List, List<BlockPos> blockPosList, List<Player> playerList, double[] doubleArray, int value4) {
      Vec3 vec3 = topCenter(c9.m_6d_BlockPos());
      AABB aABB = m_9(c9);

      for (CrystalPlacer.DataRecord4 c5 : c5List) {
         m_1(vec3, c5, aABB, blockPosList, doubleArray, value4);
         value4 += 9;
      }

      CrystalMining.m_7(vec3, MINECRAFT.player, MINECRAFT.player.getBoundingBox(), aABB, blockPosList, doubleArray, value4);
      value4 += 4;
      if (playerList != null) {
         for (Player player : playerList) {
            if (player != MINECRAFT.player && player.isAlive()) {
               CrystalMining.m_7(vec3, player, player.getBoundingBox(), aABB, blockPosList, doubleArray, value4);
               value4 += 4;
            }
         }
      }

      return value4;
   }

   private static void m_1(Vec3 vec3, CrystalPlacer.DataRecord4 c5, @Nullable AABB aABB, List<BlockPos> blockPosList, double[] doubleArray, int value4) {
      AABB aABB2 = c5.m_1n().getBoundingBox();
      boolean flag = c5.m_3_i() != aABB2 && !c5.m_5_h();
      CrystalMining.m_7(vec3, c5.m_1n(), c5.m_3_i(), aABB, blockPosList, doubleArray, value4);
      if (flag) {
      }

      if (flag) {
         CrystalMining.m_7(vec3, c5.m_1n(), aABB2, aABB, blockPosList, doubleArray, value4 + 4);
      }
   }

   @Nullable
   private static AABB m_9(CrystalPlacer.DataRecord7 c9) {
      return c9.m_4_w_boolean() ? new AABB(c9.m_6d_BlockPos()).deflate(1.0E-4) : null;
   }

   @Nullable
   private static CrystalPlacer.DataRecord5 m_1(CrystalPlacer.DataRecord7 c9, List<CrystalPlacer.DataRecord4> c5List, List<List<BlockPos>> listList, float floatValue, float floatValue2, float floatValue3, List<Player> playerList) {
      CrystalPlacer.DataRecord3 c4 = m_9(c9, c5List, c9.m_4i_List(), -1, 0.0F, floatValue, floatValue2, floatValue3, playerList);
      return m_1(List.of(c9), new CrystalPlacer.DataRecord3[]{c4}, c5List, listList, floatValue, floatValue2, floatValue3, playerList)[0];
   }

   private static CrystalPlacer.DataRecord5[] m_1(
      List<CrystalPlacer.DataRecord7> c9List, CrystalPlacer.DataRecord3[] c4Array, List<CrystalPlacer.DataRecord4> c5List, List<List<BlockPos>> listList, float floatValue, float floatValue2, float floatValue3, List<Player> playerList
   ) {
      int value4 = c9List.size();
      ArrayList list = new ArrayList<>(Collections.nCopies(value4, List.of()));
      int[] intArray = new int[value4];
      Arrays.fill(intArray, -1);
      if (!listList.isEmpty()) {
         m_7(c9List, c4Array, c5List, listList, floatValue2, list, intArray);
      }

      CrystalPlacer.DataRecord3[] c4Array2 = m_6(c9List, c4Array, list, intArray, c5List, floatValue, floatValue2, floatValue3, playerList);
      CrystalPlacer.DataRecord5[] c7Array = new CrystalPlacer.DataRecord5[value4];

      for (int i = 0; i < value4; i++) {
         CrystalPlacer.DataRecord7 c9 = (CrystalPlacer.DataRecord7)c9List.get(i);
         List list2 = (List)list.get(i);
         CrystalPlacer.DataRecord3 c4 = intArray[i] >= 0 ? c4Array2[i] : c4Array[i];
         if (c4.m_7m_int() == 1 && (!c9.m_4z() || !list2.isEmpty())) {
            float floatValue4 = c9.m_6z_C8().m_6f();

            for (BlockPos blockPos : (Iterable<BlockPos>)list2) {
               floatValue4 += CrystalMining.m_1_float(MINECRAFT.level.getBlockState(blockPos), blockPos);
            }

            if (m_5_boolean(floatValue4)) {
               List list3 = list2.isEmpty() ? c9.m_4i_List() : concat(c9.m_4i_List(), list2);
               CrystalPlacer.DataRecord6 c8 = new CrystalPlacer.DataRecord6(list3.size(), c9.m_4_w_boolean(), c4Array[i].m_7s_boolean(), floatValue4, c9.m_6z_C8().m_2v(), c4.m_57());
               c7Array[i] = new CrystalPlacer.DataRecord5(
                  new CrystalPlacer.DataRecord2(
                     c9.m_6d_BlockPos(),
                     c9.m_4_w_boolean(),
                     list3.isEmpty() ? null : (BlockPos)list3.getFirst(),
                     c4.m_54(),
                     c4.m_5f(),
                     c9.m_4i_List().isEmpty() && !list2.isEmpty()
                  ),
                  c8
               );
            }
         }
      }

      return c7Array;
   }

   private static void m_7(List<CrystalPlacer.DataRecord7> c9List, CrystalPlacer.DataRecord3[] c4Array, List<CrystalPlacer.DataRecord4> c5List, List<List<BlockPos>> listList, float floatValue, List<List<BlockPos>> listList2, int[] intArray) {
      ArrayList<CrystalPlacer.DataRecord> c1List = new ArrayList<>();

      for (int i = 0; i < c9List.size(); i++) {
         if (c4Array[i].m_7m_int() == -1) {
            if (isDelayExpired()) {
               break;
            }

            BlockPos blockPos = ((CrystalPlacer.DataRecord7)c9List.get(i)).m_6d_BlockPos();

            for (int i2 = 0; i2 < listList.size(); i2++) {
               List list = (List)listList.get(i2);
               if (list.contains(blockPos)) {
                  list = new ArrayList(list);
                  list.remove(blockPos);
               }

               if (!list.isEmpty()) {
                  c1List.add(new CrystalPlacer.DataRecord(i, i2, list));
               }
            }
         }
      }

      if (!c1List.isEmpty()) {
         boolean[] flagArray2 = m_9(c1List, c9List, c5List, floatValue);
         ArrayList<CrystalPlacer.DataRecord> c1List2 = new ArrayList<>();

         for (int i3 = 0; i3 < c1List.size(); i3++) {
            CrystalPlacer.DataRecord c1 = (CrystalPlacer.DataRecord)c1List.get(i3);
            if (flagArray2[i3] && intArray[c1.m_7_y()] < 0) {
               c1.m_7_y();
               c1.m_7v();
               listList2.set(c1.m_7_y(), c1.m_6_h_List());

               for (int i4 = 1; i4 < c1.m_6_h_List().size(); i4++) {
                  c1List2.add(new CrystalPlacer.DataRecord(c1.m_7_y(), c1.m_7v(), c1.m_6_h_List().subList(0, i4)));
               }
            }
         }

         if (!c1List2.isEmpty()) {
            boolean[] flagArray3 = m_9(c1List2, c9List, c5List, floatValue);
            boolean[] flagArray4 = new boolean[c9List.size()];

            for (int i5 = 0; i5 < c1List2.size(); i5++) {
               CrystalPlacer.DataRecord c12 = (CrystalPlacer.DataRecord)c1List2.get(i5);
               if (flagArray3[i5] && !flagArray4[c12.m_7_y()]) {
                  flagArray4[c12.m_7_y()] = true;
                  listList2.set(c12.m_7_y(), c12.m_6_h_List());
               }
            }
         }
      }
   }

   private static boolean[] m_9(List<CrystalPlacer.DataRecord> c1List, List<CrystalPlacer.DataRecord7> c9List, List<CrystalPlacer.DataRecord4> c5List, float floatValue) {
      boolean[] flagArray2 = new boolean[c1List.size()];
      int value4 = c5List.size();

      for (short shortValue = 0; shortValue < c1List.size(); shortValue += 256) {
         int value5 = Math.min(256, c1List.size() - shortValue);
         double[] doubleArray = doubleValueArray3 = ensureCapacity(doubleValueArray3, 4 + value4 * 6 + value5 * 10);
         doubleArray[0] = 15.0;
         doubleArray[1] = value5;
         doubleArray[2] = value4;
         doubleArray[3] = floatValue;

         for (int i = 0; i < value4; i++) {
            CrystalMining.m_6(((CrystalPlacer.DataRecord4)c5List.get(i)).m_1n(), doubleArray, 4 + i * 6);
         }

         int value6 = 4 + value4 * 6;

         for (int i2 = 0; i2 < value5; i2++) {
            CrystalPlacer.DataRecord c1 = (CrystalPlacer.DataRecord)c1List.get(shortValue + i2);
            CrystalPlacer.DataRecord7 c9 = (CrystalPlacer.DataRecord7)c9List.get(c1.m_7_y());
            doubleArray[value6] = c1.m_7v();
            m_1(topCenter(c9.m_6d_BlockPos()), (CrystalPlacer.DataRecord4)c5List.get(c1.m_7v()), m_9(c9), concat(c9.m_4i_List(), c1.m_6_h_List()), doubleArray, value6 + 1);
            value6 += 10;
         }

         if (nativeCheckBoolean(doubleArray) && doubleArray[0] == 1.0) {
            for (int i3 = 0; i3 < value5; i3++) {
               flagArray2[shortValue + i3] = doubleArray[2 + i3] != 0.0;
            }
         } else {
            for (int i4 = 0; i4 < value5; i4++) {
               CrystalPlacer.DataRecord c12 = (CrystalPlacer.DataRecord)c1List.get(shortValue + i4);
               CrystalPlacer.DataRecord7 c92 = (CrystalPlacer.DataRecord7)c9List.get(c12.m_7_y());
               CrystalPlacer.DataRecord4 c5 = (CrystalPlacer.DataRecord4)c5List.get(c12.m_7v());
               float floatValue2 = m_3(topCenter(c92.m_6d_BlockPos()), c5, m_9(c92), concat(c92.m_4i_List(), c12.m_6_h_List()));
               flagArray2[shortValue + i4] = m_8(floatValue2, c5.m_1n(), floatValue);
            }
         }
      }

      return flagArray2;
   }

   private static CrystalPlacer.DataRecord3[] m_6(
      List<CrystalPlacer.DataRecord7> c9List, CrystalPlacer.DataRecord3[] c4Array, List<List<BlockPos>> listList, int[] intArray, List<CrystalPlacer.DataRecord4> c5List, float floatValue, float floatValue2, float floatValue3, List<Player> playerList
   ) {
      CrystalPlacer.DataRecord3[] c4Array2 = new CrystalPlacer.DataRecord3[c9List.size()];
      ArrayList<Integer> integerList = new ArrayList<>();

      for (int i = 0; i < c9List.size(); i++) {
         if (intArray[i] >= 0) {
            integerList.add(i);
         }
      }

      if (integerList.isEmpty()) {
         return c4Array2;
      }

      int value4 = c5List.size();
      int value5 = countOpponents(playerList);
      if (value4 <= 3 && value5 <= 3) {
         int value6 = 1 + value4 + value5;
         int value7 = 2 + value4 * 9 + 4 + value5 * 4;

         for (short shortValue = 0; shortValue < integerList.size(); shortValue += 256) {
            int value8 = Math.min(256, integerList.size() - shortValue);
            double[] doubleArray = doubleValueArray2 = ensureCapacity(doubleValueArray2, 9 + value6 * 6 + value8 * value7);
            m_7(doubleArray, 16, value8, value4, value5, floatValue2, floatValue3, floatValue, -1, 0.0F);
            m_3(doubleArray, c5List, playerList);
            int value9 = 9 + value6 * 6;

            for (int i2 = 0; i2 < value8; i2++) {
               int value10 = (Integer)integerList.get(shortValue + i2);
               CrystalPlacer.DataRecord7 c9 = (CrystalPlacer.DataRecord7)c9List.get(value10);
               doubleArray[value9] = intArray[value10];
               doubleArray[value9 + 1] = c4Array[value10].m_54();
               value9 = m_8(c9, c5List, concat(c9.m_4i_List(), (List<BlockPos>)listList.get(value10)), playerList, doubleArray, value9 + 2);
            }

            if (!nativeCheckBoolean(doubleArray) || doubleArray[0] != 1.0) {
               break;
            }

            for (int i3 = 0; i3 < value8; i3++) {
               c4Array2[integerList.get(shortValue + i3)] = m_6(doubleArray, 2 + i3 * 5);
            }
         }
      }

      for (int value11 : integerList) {
         if (c4Array2[value11] == null) {
            CrystalPlacer.DataRecord7 c92 = (CrystalPlacer.DataRecord7)c9List.get(value11);
            c4Array2[value11] = m_9(c92, c5List, concat(c92.m_4i_List(), (List<BlockPos>)listList.get(value11)), intArray[value11], c4Array[value11].m_54(), floatValue, floatValue2, floatValue3, playerList);
         }
      }

      return c4Array2;
   }

   private static CrystalPlacer.DataRecord3 m_9(CrystalPlacer.DataRecord7 c9, List<CrystalPlacer.DataRecord4> c5List, List<BlockPos> blockPosList, int value4, float floatValue, float floatValue2, float floatValue3, float floatValue4, List<Player> playerList) {
      CrystalPlacer.DataRecord3 c4 = m_6(c9, c5List, blockPosList, value4, floatValue, floatValue2, floatValue3, floatValue4, playerList);
      return c4 != null ? c4 : m_0(c9, c5List, blockPosList, value4, floatValue, floatValue2, floatValue3, floatValue4, playerList);
   }

   @Nullable
   private static CrystalPlacer.DataRecord3 m_6(CrystalPlacer.DataRecord7 c9, List<CrystalPlacer.DataRecord4> c5List, List<BlockPos> blockPosList, int value4, float floatValue, float floatValue2, float floatValue3, float floatValue4, List<Player> playerList) {
      int value5 = c5List.size();
      int value6 = countOpponents(playerList);
      if (value5 <= 3 && value6 <= 3) {
         int value7 = 1 + value5 + value6;
         double[] doubleArray = doubleValueArray2 = ensureCapacity(doubleValueArray2, 9 + value7 * 6 + value5 * 9 + 4 + value6 * 4);
         m_7(doubleArray, 11, value7, value5, value6, floatValue3, floatValue4, floatValue2, value4, floatValue);
         m_3(doubleArray, c5List, playerList);
         m_8(c9, c5List, blockPosList, playerList, doubleArray, 9 + value7 * 6);
         return nativeCheckBoolean(doubleArray) && doubleArray[0] == 1.0 ? new CrystalPlacer.DataRecord3((int)doubleArray[1], (float)doubleArray[2], (float)doubleArray[3], (float)doubleArray[4], doubleArray[5] != 0.0) : null;
      } else {
         return null;
      }
   }

   private static CrystalPlacer.DataRecord3 m_0(CrystalPlacer.DataRecord7 c9, List<CrystalPlacer.DataRecord4> c5List, List<BlockPos> blockPosList, int value4, float floatValue, float floatValue2, float floatValue3, float floatValue4, List<Player> playerList) {
      Vec3 vec3 = topCenter(c9.m_6d_BlockPos());
      AABB aABB = m_9(c9);
      float floatValue5 = 0.0F;
      boolean flag = false;
      boolean flag2 = false;

      for (int i = 0; i < c5List.size(); i++) {
         CrystalPlacer.DataRecord4 c5 = (CrystalPlacer.DataRecord4)c5List.get(i);
         float floatValue6 = m_3(vec3, c5, aABB, blockPosList);
         if (value4 >= 0) {
            if (i == value4) {
               floatValue5 = Math.max(floatValue, floatValue6);
            }
         } else {
            floatValue5 = Math.max(floatValue5, floatValue6);
            flag |= m_8(floatValue6, c5.m_1n(), floatValue3);
            flag2 |= m_9(floatValue6, c5.m_1n());
         }
      }

      if (value4 < 0 && !flag) {
         return new CrystalPlacer.DataRecord3(-1, floatValue5, 0.0F, 0.0F, flag2);
      }

      float floatValue7 = CrystalMining.m_1(vec3, MINECRAFT.player, MINECRAFT.player.getBoundingBox(), aABB, blockPosList);
      if (!m_2_boolean(floatValue7, floatValue4, floatValue2)) {
         return new CrystalPlacer.DataRecord3(0, floatValue5, 0.0F, 0.0F, flag2);
      }

      float floatValue8 = m_4(vec3, playerList, aABB, blockPosList, floatValue2);
      return floatValue8 < 0.0F ? new CrystalPlacer.DataRecord3(0, floatValue5, 0.0F, 0.0F, flag2) : new CrystalPlacer.DataRecord3(1, floatValue5, floatValue7, m_2_float(floatValue5, floatValue7, floatValue8), flag2);
   }

   private static float m_3(Vec3 vec3, CrystalPlacer.DataRecord4 c5, @Nullable AABB aABB, List<BlockPos> blockPosList) {
      float floatValue = CrystalMining.m_1(vec3, c5.m_1n(), c5.m_3_i(), aABB, blockPosList);
      if (c5.m_3_i() != c5.m_1n().getBoundingBox() && !c5.m_5_h()) {
         floatValue = Math.min(floatValue, CrystalMining.m_1(vec3, c5.m_1n(), c5.m_1n().getBoundingBox(), aABB, blockPosList));
      }

      return floatValue;
   }

   private static List<BlockPos> concat(List<BlockPos> blockPosList, List<BlockPos> blockPosList2) {
      ArrayList list = new ArrayList(blockPosList.size() + blockPosList2.size());
      list.addAll(blockPosList);
      list.addAll(blockPosList2);
      return list;
   }

   private static List<List<BlockPos>> m_7(List<CrystalPlacer.DataRecord4> c5List, Vec3 vec3, float floatValue, Collection<BlockPos> blockPosList) {
      ArrayList list = new ArrayList(c5List.size());

      for (CrystalPlacer.DataRecord4 c5 : c5List) {
         AABB aABB = c5.m_3_i().deflate(0.05);
         ArrayList list2 = new ArrayList();

         for (BlockPos blockPos : getBlocksIn(aABB)) {
            BlockState blockState = MINECRAFT.level.getBlockState(blockPos);
            if (!blockPosList.contains(blockPos)
               && m_8(blockState, blockPos)
               && collidesWith(blockState, blockPos, aABB)
               && !(distanceSqToBlock(vec3, blockPos) > floatValue * floatValue)
               && CrystalInteraction.m_9(blockPos, vec3, floatValue) != null) {
               list2.add(blockPos.immutable());
            }
         }

         list2.sort(Comparator.<BlockPos>comparingDouble(blockPos2 -> CrystalMining.m_1_float(MINECRAFT.level.getBlockState(blockPos2), blockPos2)).thenComparingInt(Vec3i::getY));
         list.add(list2.size() > 4 ? List.copyOf(list2.subList(0, 4)) : list2);
      }

      return list;
   }

   public static boolean collidesWithBlocks(AABB aABB) {
      if (MINECRAFT.level == null) {
         return false;
      }

      AABB aABB2 = aABB.deflate(0.05);

      for (BlockPos blockPos : getBlocksIn(aABB2)) {
         BlockState blockState = MINECRAFT.level.getBlockState(blockPos);
         if (m_8(blockState, blockPos) && collidesWith(blockState, blockPos, aABB2)) {
            return true;
         }
      }

      return false;
   }

   private static Iterable<BlockPos> getBlocksIn(AABB aABB) {
      return BlockPos.betweenClosed(
         Mth.floor(aABB.minX), Mth.floor(aABB.minY), Mth.floor(aABB.minZ), Mth.floor(aABB.maxX), Mth.floor(aABB.maxY), Mth.floor(aABB.maxZ)
      );
   }

   private static boolean collidesWith(BlockState blockState, BlockPos blockPos, AABB aABB) {
      for (AABB aABB2 : blockState.getCollisionShape(MINECRAFT.level, blockPos).toAabbs()) {
         if (aABB2.move(blockPos).intersects(aABB)) {
            return true;
         }
      }

      return false;
   }

   private static float m_4(Vec3 vec3, List<Player> playerList, AABB aABB, List<BlockPos> blockPosList, float floatValue) {
      float floatValue2 = 0.0F;
      if (playerList == null) {
         return floatValue2;
      }

      for (Player player : playerList) {
         if (player != MINECRAFT.player && player.isAlive()) {
            float floatValue3 = CrystalMining.m_1(vec3, player, player.getBoundingBox(), aABB, blockPosList);
            if (nativeCheck(4.0, floatValue3, floatValue, player.getHealth() + player.getAbsorptionAmount())) {
               return -1.0F;
            }

            floatValue2 = Math.max(floatValue2, floatValue3);
         }
      }

      return floatValue2;
   }

   private static boolean m_0(BlockPos blockPos, @Nullable BlockPos blockPos2, @Nullable BlockPos blockPos3) {
      for (Direction direction : Direction.values()) {
         BlockPos blockPos4 = blockPos.relative(direction);
         BlockState blockState = MINECRAFT.level.getBlockState(blockPos4);
         if (!blockPos4.equals(blockPos2) && !blockPos4.equals(blockPos3) && !blockState.canBeReplaced() && !blockState.is(Blocks.COBWEB) && !blockState.getShape(MINECRAFT.level, blockPos4).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   public static boolean isObsidianOrBedrock(BlockPos blockPos) {
      return MINECRAFT.level != null && (MINECRAFT.level.getBlockState(blockPos).is(Blocks.OBSIDIAN) || MINECRAFT.level.getBlockState(blockPos).is(Blocks.BEDROCK));
   }

   private static CrystalPlacer.DataRecord7 m_7(
      BlockPos blockPos,
      BlockPos blockPos2,
      Vec3 vec3,
      float floatValue,
      float floatValue2,
      boolean flag,
      boolean flag2,
      BlockPos blockPos3,
      Collection<BlockPos> blockPosList,
      Set<Integer> integerSet,
      List<CrystalPlacer.DataRecord4> c5List,
      Predicate<Vec3> predicate
   ) {
      BlockPos blockPos4 = blockPos.above();
      double doubleValue = (double)floatValue * floatValue;
      if (distanceSqToBlock(vec3, blockPos) > doubleValue) {
         return null;
      }

      if (distanceSqToBox(vec3, blockPos.getX(), blockPos.getY() + 1.0, blockPos.getZ(), blockPos.getX() + 1.0, blockPos.getY() + 3.0, blockPos.getZ() + 1.0) > (double)floatValue2 * floatValue2) {
         return null;
      }

      Vec3 vec32 = topCenter(blockPos);
      if (predicate.test(vec32)) {
         return null;
      }

      BlockState blockState = MINECRAFT.level.getBlockState(blockPos);
      BlockState blockState2 = MINECRAFT.level.getBlockState(blockPos4);
      BlockPos blockPos5 = null;
      BlockPos blockPos6 = null;
      if (!blockState2.isAir()) {
         if (!flag2) {
            return null;
         }

         if (!m_8(blockState2, blockPos4)) {
            return null;
         }

         blockPos5 = blockPos4;
      }

      boolean flag3 = !isObsidianOrBedrock(blockPos);
      if (flag3) {
         if (!flag) {
            return null;
         }

         if (!blockState.canBeReplaced()) {
            if (!flag2) {
               return null;
            }

            if (!m_8(blockState, blockPos)) {
               return null;
            }

            if (blockPos5 == null) {
               blockPos5 = blockPos;
            } else {
               blockPos6 = blockPos;
            }
         }
      }

      AABB aABB = new AABB(blockPos4.getX(), blockPos4.getY(), blockPos4.getZ(), blockPos4.getX() + 1, blockPos4.getY() + 2, blockPos4.getZ() + 1);
      var entityList = getEntitiesIn(aABB, integerSet);
      boolean flag4 = flag2
         && !flag3
         && !entityList.isEmpty()
         && entityList.stream().allMatch(entity -> entity instanceof EndCrystal endCrystal && endCrystal.blockPosition().equals(blockPos4));
      if (!entityList.isEmpty() && !flag4) {
         return null;
      }

      AABB aABB2 = flag3 ? new AABB(blockPos) : null;

      for (CrystalPlacer.DataRecord4 c5 : c5List) {
         if (c5.m_3_i().intersects(aABB)) {
            return null;
         }

         if (aABB2 != null && c5.m_3_i().intersects(aABB2)) {
            return null;
         }
      }

      if (flag3 && !m_0(blockPos, blockPos5, blockPos6)) {
         return null;
      }

      if (m_0(blockPos5, blockPos2, vec3, doubleValue, blockPosList) && m_0(blockPos6, blockPos2, vec3, doubleValue, blockPosList)) {
         float floatValue3 = blockPos5 == null ? 0.0F : CrystalMining.m_1_float(MINECRAFT.level.getBlockState(blockPos5), blockPos5);
         if (blockPos6 != null) {
            floatValue3 += CrystalMining.m_1_float(MINECRAFT.level.getBlockState(blockPos6), blockPos6);
         }

         if (!m_5_boolean(floatValue3)) {
            return null;
         }

         BlockPos blockPos7 = null;
         BlockPos blockPos8 = null;
         if (blockPos5 == null) {
            if (flag3 && isBlocked(blockPos)) {
               return null;
            }

            if (flag3 && CrystalInteraction.m_6(blockPos, vec3, floatValue, false) == null) {
               return null;
            }

            if (!flag3 && !flag4 && CrystalInteraction.m_6(blockPos, vec3, floatValue) == null) {
               return null;
            }
         } else if (CrystalInteraction.m_9(blockPos5, vec3, floatValue) != null) {
            blockPos7 = blockPos5;
            blockPos8 = blockPos6;
         } else {
            if (blockPos6 == null || CrystalInteraction.m_9(blockPos6, vec3, floatValue) == null) {
               return null;
            }

            blockPos7 = blockPos6;
            blockPos8 = blockPos5;
         }

         double doubleValue2 = Double.MAX_VALUE;

         for (CrystalPlacer.DataRecord4 c52 : c5List) {
            doubleValue2 = Math.min(doubleValue2, c52.m_5_l().distanceToSqr(vec32));
         }

         List list = blockPos7 == null ? List.of() : (blockPos8 == null ? List.of(blockPos7) : List.of(blockPos7, blockPos8));
         return new CrystalPlacer.DataRecord7(blockPos.immutable(), flag3, flag4, list, new CrystalPlacer.DataRecord6(list.size(), flag3, true, floatValue3, blockPos.equals(blockPos3), -((float)doubleValue2)));
      } else {
         return null;
      }
   }

   private static boolean m_0(@Nullable BlockPos blockPos, BlockPos blockPos2, Vec3 vec3, double doubleValue, Collection<BlockPos> blockPosList) {
      if (blockPos == null) {
         return true;
      } else if (blockPosList.contains(blockPos)) {
         return false;
      } else {
         return blockPos.getY() <= blockPos2.getY() + 2 && blockPos.getY() >= blockPos2.getY() - 4 ? distanceSqToBlock(vec3, blockPos) <= doubleValue : false;
      }
   }

   public static boolean isMineable(BlockState blockState) {
      return blockState != null
         && MINECRAFT.level != null
         && !blockState.isAir()
         && !(blockState.getBlock() instanceof LiquidBlock)
         && blockState.getDestroySpeed(MINECRAFT.level, BlockPos.ZERO) >= 0.0F;
   }

   private static Vec3 topCenter(BlockPos blockPos) {
      return new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5);
   }

   public static boolean m_8_boolean(BlockState blockState) {
      return m_8(blockState, BlockPos.ZERO);
   }

   private static boolean m_8(BlockState blockState, BlockPos blockPos) {
      if (isMineable(blockState) && MINECRAFT.player != null && !blockState.is(Blocks.COBWEB) && !blockState.is(Blocks.SWEET_BERRY_BUSH)) {
         int value4 = CrystalMining.getTickSeed();
         if (value4 != value2) {
            BLOCK_STATE_MAP.clear();
            FLOAT_MAP.clear();
            value2 = value4;
         }

         Boolean bool = BLOCK_STATE_MAP.get(blockState);
         if (bool != null) {
            return bool;
         }

         float floatValue = CrystalMining.m_1_float(blockState, blockPos);
         boolean flag = blockState.is(Blocks.OBSIDIAN) || blockState.is(Blocks.CRYING_OBSIDIAN);
         boolean flag2 = !MINECRAFT.player.onGround();
         boolean flag3 = nativeCheck(6.0, floatValue, flag ? 1.0 : 0.0, flag2 ? 1.0 : 0.0, flag && flag2 && CrystalTargeting.isInsideBlocks(MINECRAFT.player) ? 1.0 : 0.0);
         BLOCK_STATE_MAP.put(blockState, flag3);
         return flag3;
      } else {
         return false;
      }
   }

   private static boolean m_5_boolean(float floatValue) {
      if (floatValue <= 0.0F) {
         return true;
      }

      int value4 = CrystalMining.getTickSeed();
      if (value4 != value2) {
         BLOCK_STATE_MAP.clear();
         FLOAT_MAP.clear();
         value2 = value4;
      }

      Boolean bool = FLOAT_MAP.get(floatValue);
      if (bool != null) {
         return bool;
      }

      boolean flag = nativeCheck(7.0, floatValue);
      FLOAT_MAP.put(floatValue, flag);
      return flag;
   }

   public static double distanceSqToBox(Vec3 vec3, AABB aABB) {
      return vec3.distanceToSqr(Mth.clamp(vec3.x, aABB.minX, aABB.maxX), Mth.clamp(vec3.y, aABB.minY, aABB.maxY), Mth.clamp(vec3.z, aABB.minZ, aABB.maxZ));
   }

   public static double distanceSqToBlock(Vec3 vec3, BlockPos blockPos) {
      return distanceSqToBox(vec3, blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1.0, blockPos.getY() + 1.0, blockPos.getZ() + 1.0);
   }

   public static double distanceSqToBox(Vec3 vec3, double doubleValue, double doubleValue2, double doubleValue3, double doubleValue4, double doubleValue5, double doubleValue6) {
      double doubleValue7 = Mth.clamp(vec3.x, doubleValue, doubleValue4) - vec3.x;
      double doubleValue8 = Mth.clamp(vec3.y, doubleValue2, doubleValue5) - vec3.y;
      double doubleValue9 = Mth.clamp(vec3.z, doubleValue3, doubleValue6) - vec3.z;
      return doubleValue7 * doubleValue7 + doubleValue8 * doubleValue8 + doubleValue9 * doubleValue9;
   }

   private static boolean m_7(Vec3 vec3, int value4, int value5, float floatValue) {
      double doubleValue = Math.max(Math.max(value4 - vec3.x, 0.0), vec3.x - (value4 + 1.0));
      double doubleValue2 = Math.max(Math.max(value5 - vec3.z, 0.0), vec3.z - (value5 + 1.0));
      return doubleValue * doubleValue + doubleValue2 * doubleValue2 > (double)floatValue * floatValue;
   }

   private static boolean hasEntities(BlockPos blockPos, Set<Integer> integerSet) {
      return !m_7_List(blockPos, integerSet).isEmpty();
   }

   private static List<Entity> m_7_List(BlockPos blockPos, Set<Integer> integerSet) {
      return getEntitiesIn(new AABB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 2, blockPos.getZ() + 1), integerSet);
   }

   private static List<Entity> getEntitiesIn(AABB aABB, Set<Integer> integerSet) {
      return MINECRAFT.level.getEntities((Entity)null, aABB, entity -> entity.isAlive() && !entity.isSpectator() && !integerSet.contains(entity.getId()));
   }

   public static boolean m_8(BlockPos blockPos, Set<Integer> integerSet) {
      if (MINECRAFT.level != null && isObsidianOrBedrock(blockPos) && MINECRAFT.level.getBlockState(blockPos.above()).isAir()) {
         var entityList = m_7_List(blockPos.above(), integerSet);
         return !entityList.isEmpty() && entityList.stream().noneMatch(entity -> entity.blocksBuilding);
      } else {
         return false;
      }
   }

   @Nullable
   public static CrystalPlacer.DataRecord2 m_8(
      BlockPos blockPos,
      List<CrystalPlacer.DataRecord4> c5List,
      Vec3 vec3,
      float floatValue,
      float floatValue2,
      float floatValue3,
      float floatValue4,
      List<Player> playerList,
      boolean flag,
      Collection<BlockPos> blockPosList,
      Set<Integer> integerSet,
      Predicate<Vec3> predicate
   ) {
      if (MINECRAFT.level != null && MINECRAFT.player != null) {
         Vec3 vec32 = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 1.1, blockPos.getZ() + 0.5);
         CrystalPlacer.DataRecord2 c2 = null;
         CrystalMining.onTickStart();

         try {
            for (BlockPos blockPos2 : BlockPos.betweenClosed(blockPos.offset(-3, -2, -3), blockPos.offset(3, 1, 3))) {
               BlockPos blockPos3 = blockPos2.immutable();
               if ((blockPos3.getX() != blockPos.getX() || blockPos3.getZ() != blockPos.getZ() || blockPos3.getY() < blockPos.getY() || blockPos3.getY() > blockPos.getY() + 2)
                  && !blockPosList.contains(blockPos3)) {
                  CrystalPlacer.DataRecord7 c9 = m_7(blockPos3, blockPos, vec3, floatValue, floatValue2, flag, false, null, blockPosList, integerSet, c5List, predicate);
                  if (c9 != null) {
                     Vec3 vec33 = topCenter(blockPos3);
                     if (!(vec33.distanceToSqr(vec32) > 36.0)
                        && MINECRAFT.level.clip(new ClipContext(vec32, vec33, Block.COLLIDER, Fluid.NONE, MINECRAFT.player)).getType() == Type.MISS) {
                        AABB aABB = c9.m_4_w_boolean() ? new AABB(blockPos3).deflate(1.0E-4) : null;
                        float floatValue5 = CrystalMining.m_1(vec33, MINECRAFT.player, MINECRAFT.player.getBoundingBox(), aABB, List.of());
                        if (m_2_boolean(floatValue5, floatValue4, floatValue3) && !(m_4(vec33, playerList, aABB, List.of(), floatValue3) < 0.0F)) {
                           float floatValue6 = 0.0F;

                           for (CrystalPlacer.DataRecord4 c5 : c5List) {
                              floatValue6 = Math.max(floatValue6, CrystalMining.m_1(vec33, c5.m_1n(), c5.m_3_i(), aABB, List.of()));
                           }

                           if (c2 == null
                              ? nativeCheck(5.0, 0.0)
                              : nativeCheck(5.0, 1.0, c2.m_60() ? 1.0 : 0.0, c9.m_4_w_boolean() ? 1.0 : 0.0, floatValue6, floatValue5, c2.m_1_u_float(), c2.m_1_c())) {
                              c2 = new CrystalPlacer.DataRecord2(blockPos3, c9.m_4_w_boolean(), null, floatValue6, floatValue5, false);
                           }
                        }
                     }
                  }
               }
            }

            return c2;
         } finally {
            CrystalMining.tickCooldown();
         }
      } else {
         return null;
      }
   }

   public static boolean isBlocked(BlockPos blockPos) {
      return MINECRAFT.player == null
         || MINECRAFT.level == null
         || MINECRAFT.player.getBoundingBox().intersects(new AABB(blockPos))
         || !MINECRAFT.level.isUnobstructed(Blocks.OBSIDIAN.defaultBlockState(), blockPos, CollisionContext.empty());
   }

   @Nullable
   public static Vec3 m_4(EndCrystal endCrystal, Vec3 vec3, double doubleValue) {
      if (endCrystal != null && endCrystal.isAlive()) {
         AABB aABB = endCrystal.getBoundingBox();
         Vec3 vec32 = new Vec3(
            Mth.clamp(vec3.x, aABB.minX + 0.02, aABB.maxX - 0.02),
            Mth.clamp(vec3.y, aABB.minY + 0.02, aABB.maxY - 0.02),
            Mth.clamp(vec3.z, aABB.minZ + 0.02, aABB.maxZ - 0.02)
         );
         return distanceSqToBox(vec3, aABB) <= doubleValue * doubleValue ? vec32 : null;
      } else {
         return null;
      }
   }

   public static boolean m_3(EndCrystal endCrystal, List<Player> playerList, float floatValue, float floatValue2, float floatValue3, List<Player> playerList2) {
      if (endCrystal != null && endCrystal.isAlive() && MINECRAFT.player != null && MINECRAFT.level != null) {
         if (m_5(endCrystal.position(), playerList, floatValue2) < 0.0F) {
            return false;
         }

         float floatValue4 = CrystalMining.m_3(endCrystal.position(), MINECRAFT.player);
         return m_2_boolean(floatValue4, floatValue3, floatValue) && m_4(endCrystal.position(), playerList2, null, List.of(), floatValue) >= 0.0F;
      } else {
         return false;
      }
   }

   private static float m_5(Vec3 vec3, List<Player> playerList, float floatValue) {
      float floatValue2 = -1.0F;

      for (Player player : playerList) {
         if (player.isAlive()) {
            float floatValue3 = CrystalMining.m_3(vec3, player);
            if (m_8(floatValue3, player, floatValue)) {
               floatValue2 = Math.max(floatValue2, floatValue3);
            }
         }
      }

      return floatValue2;
   }

   private static boolean m_8(float floatValue, Player player, float floatValue2) {
      return nativeCheck(2.0, floatValue, floatValue2, player.getHealth() + player.getAbsorptionAmount());
   }

   private static boolean m_9(float floatValue, Player player) {
      return nativeCheck(14.0, floatValue, player.getHealth() + player.getAbsorptionAmount());
   }

   private static boolean m_2_boolean(float floatValue, float floatValue2, float floatValue3) {
      return nativeCheck(3.0, floatValue, floatValue2, floatValue3);
   }

   private static float m_2_float(float floatValue, float floatValue2, float floatValue3) {
      return (float)nativeCompute(1.0, floatValue, floatValue2, floatValue3)[0];
   }

   public static float m_2_float(Player player) {
      return (float)nativeCompute(9.0, player.getHealth(), player.getAbsorptionAmount())[0];
   }

   public static float m_1_float(float floatValue) {
      return (float)nativeCompute(8.0, floatValue)[0];
   }

   public static float m_6_float(float floatValue) {
      return (float)nativeCompute(8.0, floatValue)[1];
   }

   private static boolean nativeCheck(double... doubleArray) {
      return nativeCompute(doubleArray)[0] != 0.0;
   }

   private static double[] nativeCompute(double... doubleArray) {
      long longValue2 = System.nanoTime();

      try {
         return C9_q.m_3(6, doubleArray);
      } finally {
         C07.recordSince(longValue2);
      }
   }

   public static boolean m_7_boolean(BlockPos blockPos, Set<Integer> integerSet) {
      return isObsidianOrBedrock(blockPos) && MINECRAFT.level.getBlockState(blockPos.above()).isAir() && !hasEntities(blockPos.above(), integerSet);
   }

   public static void m_9_void(int value4) {
      value3 = value4;
   }

   public static int m_5_int() {
      return value3;
   }

   public static int m_9_int() {
      return 17;
   }

   private static String a(byte[] byteArray) {
      int value4 = 0;
      int value5;
      char[] charArray = new char[value5 = byteArray.length];

      for (int i = 0; i < value5; i++) {
         int value6;
         if ((value6 = 255 & byteArray[i]) < 192) {
            charArray[value4++] = (char)value6;
         } else if (value6 < 224) {
            char ch = (char)((char)(value6 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value4++] = ch;
         } else if (i < value5 - 2) {
            char ch2 = (char)((char)(value6 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value4++] = ch2;
         }
      }

      return new String(charArray, 0, value4);
   }

   private record DataRecord(int f_7_y, int f_7v, List<BlockPos> f_6_h) {
      public int m_7_y() {
         return this.f_7_y;
      }

      public int m_7v() {
         return this.f_7v;
      }

      public List<BlockPos> m_6_h_List() {
         return this.f_6_h;
      }
   }

   public record DataRecord2(BlockPos f_7_i, boolean f_60, @Nullable BlockPos f_72, float f_1_u, float f_1_c, boolean f_6y) {
      public BlockPos m_7_i() {
         return this.f_7_i;
      }

      public boolean m_60() {
         return this.f_60;
      }

      @Nullable
      public BlockPos m_72_BlockPos() {
         return this.f_72;
      }

      public float m_1_u_float() {
         return this.f_1_u;
      }

      public float m_1_c() {
         return this.f_1_c;
      }

      public boolean m_6y_boolean() {
         return this.f_6y;
      }
   }

   private record DataRecord3(int f_7m, float f_54, float f_5f, float f_57, boolean f_7s) {
      public int m_7m_int() {
         return this.f_7m;
      }

      public float m_54() {
         return this.f_54;
      }

      public float m_5f() {
         return this.f_5f;
      }

      public float m_57() {
         return this.f_57;
      }

      public boolean m_7s_boolean() {
         return this.f_7s;
      }
   }

   public record DataRecord4(Player f_1n, AABB f_3_i, Vec3 f_5_l, boolean f_5_h) {
      public DataRecord4(Player player, AABB aABB, boolean flag) {
         this(player, aABB, new Vec3((aABB.minX + aABB.maxX) * 0.5, aABB.minY, (aABB.minZ + aABB.maxZ) * 0.5), flag);
      }

      public Player m_1n() {
         return this.f_1n;
      }

      public AABB m_3_i() {
         return this.f_3_i;
      }

      public Vec3 m_5_l() {
         return this.f_5_l;
      }

      public boolean m_5_h() {
         return this.f_5_h;
      }
   }

   private record DataRecord5(CrystalPlacer.DataRecord2 f_7d, CrystalPlacer.DataRecord6 f_5_p) {
      public CrystalPlacer.DataRecord2 m_7d() {
         return this.f_7d;
      }

      public CrystalPlacer.DataRecord6 m_5_p() {
         return this.f_5_p;
      }
   }

   private record DataRecord6(int f_6_t, boolean f_2_u, boolean f_20, float f_6f, boolean f_2v, float f_62) implements Comparable<CrystalPlacer.DataRecord6> {
      private int m_6a_int() {
         return this.f_6_t > 0 ? 4 : (this.f_20 ? 0 : 2) + (this.f_2_u ? 1 : 0);
      }

      private boolean m_7(CrystalPlacer.DataRecord6 c8) {
         return this.m_6a_int() != c8.m_6a_int() ? this.m_6a_int() < c8.m_6a_int() : this.f_6_t > 0 && !c8.f_2v && (this.f_2v || this.f_6f < c8.f_6f);
      }

      public int compareTo(CrystalPlacer.DataRecord6 c8) {
         return (int)CrystalPlacer.nativeCompute(
            0.0,
            this.f_6_t,
            this.f_2_u ? 1.0 : 0.0,
            this.f_20 ? 1.0 : 0.0,
            this.f_6f,
            this.f_2v ? 1.0 : 0.0,
            this.f_62,
            c8.f_6_t,
            c8.f_2_u ? 1.0 : 0.0,
            c8.f_20 ? 1.0 : 0.0,
            c8.f_6f,
            c8.f_2v ? 1.0 : 0.0,
            c8.f_62
         )[0];
      }

      public int m_6_t() {
         return this.f_6_t;
      }

      public boolean m_2_u() {
         return this.f_2_u;
      }

      public boolean m_20() {
         return this.f_20;
      }

      public float m_6f() {
         return this.f_6f;
      }

      public boolean m_2v() {
         return this.f_2v;
      }

      public float m_62_float() {
         return this.f_62;
      }
   }

   private record DataRecord7(BlockPos f_6d, boolean f_4_w, boolean f_4z, List<BlockPos> f_4i, CrystalPlacer.DataRecord6 f_6z) {
      public BlockPos m_6d_BlockPos() {
         return this.f_6d;
      }

      public boolean m_4_w_boolean() {
         return this.f_4_w;
      }

      public boolean m_4z() {
         return this.f_4z;
      }

      public List<BlockPos> m_4i_List() {
         return this.f_4i;
      }

      public CrystalPlacer.DataRecord6 m_6z_C8() {
         return this.f_6z;
      }
   }
}
