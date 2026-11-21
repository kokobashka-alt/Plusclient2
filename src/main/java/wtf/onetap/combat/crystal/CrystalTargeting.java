package wtf.onetap.combat.crystal;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.util.MinecraftAccess;

public class CrystalTargeting implements MinecraftAccess {
   private static final Map<UUID, CrystalTargeting.DataRecord2> U_U_I_D_MAP = new ConcurrentHashMap<>();
   private static final Map<Integer, CrystalTargeting.DataRecord> INTEGER_MAP = new ConcurrentHashMap<>();
   private static ClientLevel clientLevel;

   private CrystalTargeting() {
   }

   public static boolean isInsideBlocks(Player player) {
      return player != null && isInsideBlocks(player.getBoundingBox());
   }

   public static boolean isInsideBlocks(AABB aABB) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return false;
      }

      double doubleValue = 1.0E-5;

      for (BlockPos blockPos : BlockPos.betweenClosed(
         Mth.floor(aABB.minX + doubleValue),
         Mth.floor(aABB.minY + doubleValue),
         Mth.floor(aABB.minZ + doubleValue),
         Mth.floor(aABB.maxX - doubleValue),
         Mth.floor(aABB.maxY - doubleValue),
         Mth.floor(aABB.maxZ - doubleValue)
      )) {
         if (MinecraftAccess.MINECRAFT.level.getBlockState(blockPos).is(Blocks.COBWEB)) {
            return true;
         }
      }

      return false;
   }

   public static AABB m_3(Player player, int value) {
      if (player == null) {
         return null;
      }

      if (MinecraftAccess.MINECRAFT.level == null) {
         return player.getBoundingBox();
      }

      AABB aABB = player.getBoundingBox();
      Vec3 vec3 = m_4_Vec3(player);
      boolean flag = player.onGround();
      double doubleValue = player.isNoGravity() ? 0.0 : player.getGravity();
      int value2 = Math.clamp(value, 0, 12);
      if (value2 <= 0) {
         return aABB;
      }

      Vec3 vec32 = scaleMotion(vec3, flag, isInsideBlocks(aABB));

      for (int i = 1; i <= value2; i++) {
         Vec3 vec33 = Entity.collideBoundingBox(player, vec32, aABB, MinecraftAccess.MINECRAFT.level, List.of());
         aABB = aABB.move(vec33);
         double doubleValue2 = vec33.x - vec32.x;
         double doubleValue3 = vec33.y - vec32.y;
         double doubleValue4 = vec33.z - vec32.z;
         boolean flag2 = vec32.y < 0.0 && Math.abs(doubleValue3) > 1.0E-7;
         double doubleValue5 = flag2 ? 1.0 : 0.98;
         double doubleValue6 = (Math.abs(doubleValue2) > 1.0E-7 ? 0.0 : vec32.x) * doubleValue5;
         double doubleValue7 = flag2 ? 0.0 : ((Math.abs(doubleValue3) > 1.0E-7 ? 0.0 : vec32.y) - doubleValue) * 0.98;
         double doubleValue8 = (Math.abs(doubleValue4) > 1.0E-7 ? 0.0 : vec32.z) * doubleValue5;
         vec32 = scaleMotion(new Vec3(doubleValue6, doubleValue7, doubleValue8), flag2, isInsideBlocks(aABB));
      }

      return aABB;
   }

   private static Vec3 scaleMotion(Vec3 vec3, boolean flag, boolean flag2) {
      double doubleValue = flag && vec3.y <= 0.0 ? -0.08 : vec3.y;
      return flag2 ? new Vec3(vec3.x * 0.25, doubleValue * 0.05, vec3.z * 0.25) : new Vec3(vec3.x, doubleValue, vec3.z);
   }

   public static void m_6(int value, Vec3 vec3) {
      if (MinecraftAccess.MINECRAFT.player != null && !(vec3.y <= 0.0)) {
         if (INTEGER_MAP.size() > 128) {
            INTEGER_MAP.clear();
         }

         INTEGER_MAP.put(value, new CrystalTargeting.DataRecord(MinecraftAccess.MINECRAFT.player.tickCount, vec3));
      }
   }

   public static boolean isRecentlyTracked(Player player) {
      CrystalTargeting.DataRecord c5 = INTEGER_MAP.get(player.getId());
      return c5 != null && MinecraftAccess.MINECRAFT.player != null && Math.abs(MinecraftAccess.MINECRAFT.player.tickCount - c5.m_3_g()) <= 2;
   }

   private static Vec3 m_4_Vec3(Player player) {
      if (clientLevel != MinecraftAccess.MINECRAFT.level) {
         U_U_I_D_MAP.clear();
         INTEGER_MAP.clear();
         clientLevel = MinecraftAccess.MINECRAFT.level;
      }

      if (U_U_I_D_MAP.size() > 128) {
         U_U_I_D_MAP.clear();
      }

      int value = MinecraftAccess.MINECRAFT.player == null ? player.tickCount : MinecraftAccess.MINECRAFT.player.tickCount;
      Vec3 vec3 = player.getPositionCodec().getBase();
      CrystalTargeting.DataRecord2 c6 = U_U_I_D_MAP.get(player.getUUID());
      if (c6 != null && c6.f_6t == value) {
         return c6.f_7_c;
      }

      Vec3 vec32 = new Vec3(player.getX() - player.xo, player.getY() - player.yo, player.getZ() - player.zo);
      Vec3 vec33 = player.getDeltaMovement();
      Vec3 vec34 = c6 == null ? Vec3.ZERO : vec3.subtract(c6.f_7_r);
      Vec3 vec35 = c6 == null ? Vec3.ZERO : c6.f_7_c;
      CrystalTargeting.DataRecord c5 = INTEGER_MAP.get(player.getId());
      Vec3 vec36 = c5 == null ? Vec3.ZERO : c5.m_1_a();
      int value2 = c6 == null ? 0 : value - c6.f_6t;
      int value3 = c6 == null ? 0 : value - c6.f_6v;
      int value4 = c5 == null ? 0 : value - c5.m_3_g();
      boolean flag = false;
      Vec3 vec37 = lengthSq(vec33) > 1.0E-5 ? vec33 : vec32;
      if (c6 != null && value2 > 0 && value2 <= 5) {
         if (lengthSq(vec34) > 1.0E-6) {
            double doubleValue = Math.max(value3, 1);
            Vec3 vec38 = new Vec3(vec34.x / doubleValue, vec34.y / doubleValue, vec34.z / doubleValue);
            if (lengthSq(vec38) <= 16.0) {
               vec37 = new Vec3(vec38.x * 0.75 + vec35.x * 0.25, vec38.y, vec38.z * 0.75 + vec35.z * 0.25);
            }
         } else {
            flag = true;
            if (value3 <= 2) {
               vec37 = vec35;
            }
         }
      }

      if (c5 != null && value4 >= 0 && value4 <= 1) {
         vec37 = vec36;
      }

      double doubleValue2 = lengthSq(vec37);
      if (!Double.isFinite(doubleValue2) || doubleValue2 > 16.0) {
         vec37 = Vec3.ZERO;
      }

      int value5 = flag ? c6.f_6v : value;
      U_U_I_D_MAP.put(player.getUUID(), new CrystalTargeting.DataRecord2(value, value5, vec3, vec37));
      return vec37;
   }

   private static double lengthSq(Vec3 vec3) {
      return vec3.x * vec3.x + vec3.y * vec3.y + vec3.z * vec3.z;
   }

   private record DataRecord(int f_3_g, Vec3 f_1_a) {
      public int m_3_g() {
         return this.f_3_g;
      }

      public Vec3 m_1_a() {
         return this.f_1_a;
      }
   }

   private record DataRecord2(int f_6t, int f_6v, Vec3 f_7_r, Vec3 f_7_c) {
      public int m_6t() {
         return this.f_6t;
      }

      public int m_6v() {
         return this.f_6v;
      }

      public Vec3 m_7_r_Vec3() {
         return this.f_7_r;
      }

      public Vec3 m_7_c_Vec3() {
         return this.f_7_c;
      }
   }
}
