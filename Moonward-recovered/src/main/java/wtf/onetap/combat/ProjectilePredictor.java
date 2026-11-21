package wtf.onetap.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

public class ProjectilePredictor implements MinecraftAccess {
   private static String[] textArray;

   private ProjectilePredictor() {
   }

   private static ProjectilePredictor.CallbackType predictEntity(Entity entity) {
      return m_2(entity, null, Integer.MAX_VALUE);
   }

   public static ProjectilePredictor.CallbackType m_2(final Entity entity, Entity entity2, int value) {
      Entity entity3 = entity instanceof Projectile projectile ? projectile.getOwner() : entity;
      final double doubleValue = entity instanceof Projectile ? 0.0 : 0.5;
      final ArrayList<ProjectilePredictor.DataRecord3> c5List = new ArrayList<>();

      for (Entity entity4 : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
         if (entity4 != entity && entity4 != entity3 && entity4.isAlive() && entity4.canBeHitByProjectile()) {
            Vec3 vec3 = entity4.position().subtract(entity4.xo, entity4.yo, entity4.zo);
            c5List.add(new ProjectilePredictor.DataRecord3(entity4, entity4.getBoundingBox(), vec3.lengthSqr() > 4.0 ? Vec3.ZERO : vec3, entity4 == entity2 ? value : Integer.MAX_VALUE));
         }
      }

      return new ProjectilePredictor.CallbackType() {
         @Override
         public boolean m_3_boolean(Vec3 vec32) {
            return MinecraftAccess.MINECRAFT.level.hasChunkAt(Mth.floor(vec32.x), Mth.floor(vec32.z));
         }

         @Override
         public boolean m_5_boolean(Vec3 vec33) {
            return MinecraftAccess.MINECRAFT.level.getFluidState(BlockPos.containing(vec33)).is(FluidTags.WATER);
         }

         @Override
         public HitResult m_6(Vec3 vec34, Vec3 vec35, int value2, int value3) {
            InventoryUtil.m_8_StringArr();
            BlockHitResult blockHitResult = MinecraftAccess.MINECRAFT.level.clipIncludingBorder(new ClipContext(vec34, vec35, Block.COLLIDER, Fluid.NONE, entity));
            Vec3 vec36 = blockHitResult.getType() == Type.MISS ? vec35 : blockHitResult.getLocation();
            double doubleValue2 = Mth.clamp((value3 - 2) / 20.0, 0.0, 0.3F) + doubleValue;
            EntityHitResult entityHitResult = null;
            double doubleValue3 = Double.MAX_VALUE;

            for (ProjectilePredictor.DataRecord3 c5 : c5List) {
               if (value2 <= c5.f_2q) {
                  var optional = c5.f_3__.move(c5.f_3_w.scale(value2)).inflate(doubleValue2).clip(vec34, vec36);
                  if (!optional.isEmpty() && !(vec34.distanceToSqr((Vec3)optional.get()) >= doubleValue3)) {
                     doubleValue3 = vec34.distanceToSqr((Vec3)optional.get());
                     entityHitResult = new EntityHitResult(c5.f_3_r, (Vec3)optional.get());
                  }
               }
            }

            return entityHitResult != null ? entityHitResult : (blockHitResult.getType() == Type.MISS ? null : blockHitResult);
         }

         @Override
         public double m_5_double(Vec3 vec37) {
            BlockHitResult blockHitResult2 = MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec37, vec37.subtract(0.0, 64.0, 0.0), Block.COLLIDER, Fluid.NONE, entity));
            return blockHitResult2.getType() == Type.MISS ? vec37.y - 64.0 : blockHitResult2.getLocation().y;
         }
      };
   }

   public static ProjectilePredictor.DataRecord predictEntityPath(Vec3 vec3, Vec3 vec32, Entity entity) {
      return simulatePath(vec3, vec32, predictEntity(entity), entity.tickCount, 160);
   }

   public static ProjectilePredictor.DataRecord simulatePath(Vec3 vec3, Vec3 vec32, ProjectilePredictor.CallbackType c1, int value, int value2) {
      ArrayList list = new ArrayList();
      list.add(vec3);

      for (int i = 1; i <= value2; i++) {
         if (!c1.m_3_boolean(vec3)) {
            return new ProjectilePredictor.DataRecord(list, vec3, i, vec3, false, null);
         }

         vec32 = vec32.add(0.0, -0.03, 0.0).scale(c1.m_5_boolean(vec3) ? 0.8F : 0.99F);
         Vec3 vec33 = vec3.add(vec32);
         if (!c1.m_3_boolean(vec33)) {
            return new ProjectilePredictor.DataRecord(list, vec3, i, vec3, false, null);
         }

         HitResult hitResult = c1.m_6(vec3, vec33, i, value + i - 1);
         if (hitResult != null) {
            list.add(hitResult.getLocation());
            return new ProjectilePredictor.DataRecord(list, hitResult.getLocation(), i, vec3, true, hitResult instanceof BlockHitResult blockHitResult ? blockHitResult.getDirection() : null);
         }

         list.add(vec33);
         vec3 = vec33;
      }

      return new ProjectilePredictor.DataRecord(list, vec3, value2, vec3, false, null);
   }

   public static Vec3 flatten(Vec3 vec3, boolean flag) {
      return new Vec3(vec3.x, flag ? 0.0 : vec3.y, vec3.z);
   }

   public static Vec3 lookVector(float floatValue, float floatValue2, Vec3 vec3) {
      double doubleValue = Math.toRadians(floatValue);
      double doubleValue2 = Math.toRadians(floatValue2);
      return new Vec3(-Math.sin(doubleValue) * Math.cos(doubleValue2), -Math.sin(doubleValue2), Math.cos(doubleValue) * Math.cos(doubleValue2)).scale(1.5).add(vec3);
   }

   public static ProjectilePredictor.DataRecord2 m_3(Vec3 vec3, Vec3 vec32, ProjectilePredictor.DataRecord c0, double doubleValue, int value, ProjectilePredictor.CallbackType c1) {
      ProjectilePredictor.DataRecord4 c6 = m_2(c0, c1, value);
      ProjectilePredictor.DataRecord2 c3 = null;
      ProjectilePredictor.DataRecord2 c32 = null;

      for (Rotations rotations : m_9(vec3, vec32, c6)) {
         ProjectilePredictor.DataRecord2 c33 = m_1(vec3, vec32, rotations, c6, c1);
         c3 = m_5(c33, c3, doubleValue);
         if (c3.f_37 <= 0.05) {
            return c3;
         }

         if (c32 == null || c33.f_37 < c32.f_37) {
            c32 = c33;
         }
      }

      if (c32 != null && c32.f_37 < 4.0) {
         c3 = m_5(m_0(vec3, vec32, c6, c1, c32), c3, doubleValue);
      }

      if (c3 == null || !c3.m_9_boolean()) {
         c3 = m_5(m_0(vec3, vec32, c6, doubleValue, c1), c3, doubleValue);
      }

      return c3 != null ? c3 : m_1(vec3, vec32, toRotations(c6.m_6_Vec3().subtract(vec3)), c6, c1);
   }

   private static ProjectilePredictor.DataRecord4 m_2(ProjectilePredictor.DataRecord c0, ProjectilePredictor.CallbackType c1, int value) {
      Vec3 vec3 = c0.f_2a != null && c0.f_2a != Direction.UP ? c0.f_28 : null;
      return new ProjectilePredictor.DataRecord4(c0.f_39, m_5(c0, c1), vec3, Math.min(value, 160));
   }

   private static double m_5(ProjectilePredictor.DataRecord c0, ProjectilePredictor.CallbackType c1) {
      return c0.f_2a == Direction.UP ? c0.f_28.y : c1.m_5_double(c0.f_39);
   }

   private static ProjectilePredictor.DataRecord2 m_5(ProjectilePredictor.DataRecord2 c3, ProjectilePredictor.DataRecord2 c32, double doubleValue) {
      if (c3 == null) {
         return c32;
      }

      if (c32 == null) {
         return c3;
      }

      double doubleValue2 = c3.m_5_double(doubleValue);
      double doubleValue3 = c32.m_5_double(doubleValue);
      return !(doubleValue2 < doubleValue3) && (doubleValue2 != doubleValue3 || !(c3.f_37 < c32.f_37)) ? c32 : c3;
   }

   private static ProjectilePredictor.DataRecord2 m_0(Vec3 vec3, Vec3 vec32, ProjectilePredictor.DataRecord4 c6, double doubleValue, ProjectilePredictor.CallbackType c1) {
      double doubleValue2 = c6.m_6_Vec3().x - vec3.x;
      double doubleValue3 = c6.m_6_Vec3().z - vec3.z;
      double doubleValue4 = Math.hypot(doubleValue2, doubleValue3);
      if (doubleValue4 < 0.001) {
         return null;
      }

      ProjectilePredictor.DataRecord2 c3 = null;
      float floatValue = 0.0F;

      for (float floatValue2 = 70.0F; floatValue2 >= -50.0F; floatValue2 -= 2.0F) {
         ProjectilePredictor.DataRecord2 c32 = m_4(vec3, vec32, c6, c1, doubleValue2 / doubleValue4, doubleValue3 / doubleValue4, floatValue2);
         if (c32 != null && m_5(c32, c3, doubleValue) == c32) {
            c3 = c32;
            floatValue = floatValue2;
         }
      }

      if (c3 == null) {
         return null;
      }

      for (float floatValue3 = floatValue - 2.0F; floatValue3 <= floatValue + 2.0F; floatValue3 += 0.2F) {
         c3 = m_5(m_4(vec3, vec32, c6, c1, doubleValue2 / doubleValue4, doubleValue3 / doubleValue4, floatValue3), c3, doubleValue);
      }

      return c3.f_37 < 4.0 ? m_5(m_0(vec3, vec32, c6, c1, c3), c3, doubleValue) : c3;
   }

   private static ProjectilePredictor.DataRecord2 m_4(Vec3 vec3, Vec3 vec32, ProjectilePredictor.DataRecord4 c6, ProjectilePredictor.CallbackType c1, double doubleValue, double doubleValue2, float floatValue) {
      double doubleValue3 = Math.toRadians(floatValue);
      Vec3 vec33 = m_0(doubleValue, doubleValue2, vec32, Math.cos(doubleValue3) * 1.5, -Math.sin(doubleValue3) * 1.5);
      return vec33 == null ? null : m_1(vec3, vec32, toRotations(vec33), c6, c1);
   }

   private static List<Rotations> m_9(Vec3 vec3, Vec3 vec32, ProjectilePredictor.DataRecord4 c6) {
      ArrayList list = new ArrayList();
      Vec3 vec33 = c6.m_6_Vec3();
      InventoryUtil.m_8_StringArr();
      double doubleValue = vec33.x - vec3.x;
      double doubleValue2 = vec33.z - vec3.z;
      double doubleValue3 = Math.hypot(doubleValue, doubleValue2);

      for (int i = 1; i < 160 && i <= c6.f_2l; i++) {
         double doubleValue4 = m_9_double(i);
         double doubleValue5 = doubleValue / doubleValue4 - vec32.x;
         double doubleValue6 = doubleValue2 / doubleValue4 - vec32.z;
         double doubleValue7 = 2.25 - doubleValue5 * doubleValue5 - doubleValue6 * doubleValue6;
         if (doubleValue7 >= 0.0) {
            for (double doubleValue8 : new double[]{-Math.sqrt(doubleValue7), Math.sqrt(doubleValue7)}) {
               double doubleValue9 = doubleValue8 + vec32.y;
               double doubleValue10 = vec3.y + doubleValue4 * doubleValue9 - m_2_double(i);
               double doubleValue11 = vec3.y + m_9_double(i + 1) * doubleValue9 - m_2_double(i + 1);
               boolean flag = doubleValue10 >= c6.f_2j && doubleValue11 < c6.f_2j + 0.1;
               if (flag || Math.abs(doubleValue10 - vec33.y) < 1.0) {
                  containsRotation(list, toRotations(new Vec3(doubleValue5, doubleValue8, doubleValue6)));
               }
            }
         }

         for (int i2 = i; i2 <= i + 1; i2++) {
            double doubleValue12 = (c6.f_2j + (i2 == i ? 0.001 : -0.001) - vec3.y + m_2_double(i2)) / m_9_double(i2) - vec32.y;
            if (!(Math.abs(doubleValue12) >= 1.5)) {
               Vec3 vec34 = m_0(doubleValue / doubleValue3, doubleValue2 / doubleValue3, vec32, Math.sqrt(2.25 - doubleValue12 * doubleValue12), doubleValue12);
               if (vec34 != null && Math.abs(doubleValue4 * Math.hypot(vec34.x + vec32.x, vec34.z + vec32.z) - doubleValue3) < 2.0) {
                  containsRotation(list, toRotations(vec34));
               }
            }
         }
      }

      return list;
   }

   private static void containsRotation(List<Rotations> rotationsList, Rotations rotations) {
      for (Rotations rotations2 : rotationsList) {
         if (Math.abs(Mth.wrapDegrees(rotations2.getFloatValue() - rotations.getFloatValue())) < 0.1F && Math.abs(rotations2.getFloatValue2() - rotations.getFloatValue2()) < 0.1F) {
            return;
         }
      }

      rotationsList.add(rotations);
   }

   private static Vec3 m_0(double doubleValue, double doubleValue2, Vec3 vec3, double doubleValue3, double doubleValue4) {
      double doubleValue5 = (vec3.x * doubleValue2 - vec3.z * doubleValue) / doubleValue3;
      if (Math.abs(doubleValue5) >= 1.0) {
         return null;
      }

      double doubleValue6 = Math.sqrt(1.0 - doubleValue5 * doubleValue5);
      double doubleValue7 = (doubleValue6 * doubleValue - doubleValue5 * doubleValue2) * doubleValue3;
      double doubleValue8 = (doubleValue6 * doubleValue2 + doubleValue5 * doubleValue) * doubleValue3;
      return doubleValue7 * doubleValue + doubleValue8 * doubleValue2 + vec3.x * doubleValue + vec3.z * doubleValue2 > 0.0 ? new Vec3(doubleValue7, doubleValue4, doubleValue8) : null;
   }

   private static double m_9_double(int value) {
      return 0.99F * (1.0 - Math.pow(0.99F, value)) / 0.00999999F;
   }

   private static double m_2_double(int value) {
      return 2.970002861025678 * (value - m_9_double(value));
   }

   private static Rotations toRotations(Vec3 vec3) {
      return new Rotations((float)(Math.toDegrees(Math.atan2(vec3.z, vec3.x)) - 90.0), (float)(-Math.toDegrees(Math.atan2(vec3.y, Math.hypot(vec3.x, vec3.z)))));
   }

   public static ProjectilePredictor.DataRecord2 m_4(Vec3 vec3, Vec3 vec32, Rotations rotations, ProjectilePredictor.DataRecord c0, ProjectilePredictor.CallbackType c1) {
      return m_1(vec3, vec32, rotations, m_2(c0, c1, 160), c1);
   }

   private static ProjectilePredictor.DataRecord2 m_1(Vec3 vec3, Vec3 vec32, Rotations rotations, ProjectilePredictor.DataRecord4 c6, ProjectilePredictor.CallbackType c1) {
      ProjectilePredictor.DataRecord c0 = simulatePath(vec3, lookVector(rotations.getFloatValue(), rotations.getFloatValue2(), vec32), c1, 0, c6.f_2l);
      if (!c0.f_3v || c0.f_2a == null) {
         return new ProjectilePredictor.DataRecord2(rotations, c0.f_2g, c0.f_39, Double.MAX_VALUE);
      }

      if (c6.f_2_y != null) {
         return new ProjectilePredictor.DataRecord2(rotations, c0.f_2g, c0.f_28, c0.f_28.distanceTo(c6.f_2_y));
      }

      Vec3 vec33 = c0.f_39;
      double doubleValue = Math.hypot(vec33.x - c6.f_2_o.x, vec33.z - c6.f_2_o.z) + Math.abs(m_5(c0, c1) - c6.f_2j) * 0.5;
      return new ProjectilePredictor.DataRecord2(rotations, c0.f_2g, vec33, doubleValue);
   }

   private static ProjectilePredictor.DataRecord2 m_0(Vec3 vec3, Vec3 vec32, ProjectilePredictor.DataRecord4 c6, ProjectilePredictor.CallbackType c1, ProjectilePredictor.DataRecord2 c3) {
      float floatValue = 0.05F;

      for (int i = 0; i < 8 && c3.m_7_boolean() && c3.f_37 > 0.02; i++) {
         float floatValue2 = c3.f_3_f.getFloatValue();
         float floatValue3 = c3.f_3_f.getFloatValue2();
         ProjectilePredictor.DataRecord2 c32 = m_1(vec3, vec32, new Rotations(floatValue2 + floatValue, floatValue3), c6, c1);
         ProjectilePredictor.DataRecord2 c33 = m_1(vec3, vec32, new Rotations(floatValue2, floatValue3 + floatValue), c6, c1);
         if (!c32.m_7_boolean() || !c33.m_7_boolean()) {
            break;
         }

         Vec3 vec33 = c3.f_1_n;
         Vec3 vec34 = c6.m_8_Vec3(c32.f_1_n.subtract(vec33)).scale(1.0 / floatValue);
         Vec3 vec35 = c6.m_8_Vec3(c33.f_1_n.subtract(vec33)).scale(1.0 / floatValue);
         Vec3 vec36 = c6.m_8_Vec3(c6.m_6_Vec3().subtract(vec33));
         double doubleValue = vec34.dot(vec34);
         double doubleValue2 = vec34.dot(vec35);
         double doubleValue3 = vec35.dot(vec35);
         double doubleValue4 = vec34.dot(vec36);
         double doubleValue5 = vec35.dot(vec36);
         double doubleValue6 = doubleValue * doubleValue3 - doubleValue2 * doubleValue2;
         if (Math.abs(doubleValue6) < 1.0E-9) {
            break;
         }

         double doubleValue7 = Mth.clamp((doubleValue4 * doubleValue3 - doubleValue2 * doubleValue5) / doubleValue6, -4.0, 4.0);
         double doubleValue8 = Mth.clamp((doubleValue * doubleValue5 - doubleValue2 * doubleValue4) / doubleValue6, -4.0, 4.0);
         ProjectilePredictor.DataRecord2 c34 = null;

         for (double doubleValue9 : new double[]{1.0, 0.5, 0.25}) {
            ProjectilePredictor.DataRecord2 c35 = m_1(vec3, vec32, new Rotations(floatValue2 + (float)(doubleValue7 * doubleValue9), Mth.clamp(floatValue3 + (float)(doubleValue8 * doubleValue9), -89.9F, 89.9F)), c6, c1);
            if (c35.f_37 < c3.f_37) {
               c34 = c35;
               break;
            }
         }

         if (c34 == null) {
            break;
         }

         c3 = c34;
      }

      return c3;
   }

   public static void m_9(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_2_StringArr() {
      return textArray;
   }

   static {
      m_9(new String[2]);
   }

   public record DataRecord(ArrayList<Vec3> f_27, Vec3 f_28, int f_2g, Vec3 f_39, boolean f_3v, Direction f_2a) {
      public ArrayList<Vec3> m_27_ArrayList() {
         return this.f_27;
      }

      public Vec3 m_28() {
         return this.f_28;
      }

      public int m_2g_int() {
         return this.f_2g;
      }

      public Vec3 m_39_Vec3() {
         return this.f_39;
      }

      public boolean m_3v() {
         return this.f_3v;
      }

      public Direction m_2a_Direction() {
         return this.f_2a;
      }
   }

   public interface CallbackType {
      boolean m_3_boolean(Vec3 vec3);

      boolean m_5_boolean(Vec3 vec3);

      HitResult m_6(Vec3 vec3, Vec3 vec32, int value, int value2);

      double m_5_double(Vec3 vec3);
   }

   public record DataRecord2(Rotations f_3_f, int f_2_i, Vec3 f_1_n, double f_37) {
      public boolean m_7_boolean() {
         return this.f_37 != Double.MAX_VALUE;
      }

      public boolean m_9_boolean() {
         return this.f_37 <= 0.3;
      }

      public double m_5_double(double doubleValue) {
         return this.f_37 <= doubleValue ? this.f_2_i + this.f_37 * 10.0 : Double.MAX_VALUE;
      }

      public Rotations m_3_f_C3b() {
         return this.f_3_f;
      }

      public int m_2_i() {
         return this.f_2_i;
      }

      public Vec3 m_1_n_Vec3() {
         return this.f_1_n;
      }

      public double m_37() {
         return this.f_37;
      }
   }

   private record DataRecord3(Entity f_3_r, AABB f_3__, Vec3 f_3_w, int f_2q) {
      public Entity m_3_r() {
         return this.f_3_r;
      }

      public AABB m_3__() {
         return this.f_3__;
      }

      public Vec3 m_3_w() {
         return this.f_3_w;
      }

      public int m_2q() {
         return this.f_2q;
      }
   }

   private record DataRecord4(Vec3 f_2_o, double f_2j, Vec3 f_2_y, int f_2l) {
      public Vec3 m_6_Vec3() {
         return this.f_2_y != null ? this.f_2_y : this.f_2_o;
      }

      public Vec3 m_8_Vec3(Vec3 vec3) {
         return this.f_2_y != null ? vec3 : new Vec3(vec3.x, 0.0, vec3.z);
      }

      public Vec3 m_2_o_Vec3() {
         return this.f_2_o;
      }

      public double m_2j_double() {
         return this.f_2j;
      }

      public Vec3 m_2_y_Vec3() {
         return this.f_2_y;
      }

      public int m_2l() {
         return this.f_2l;
      }
   }
}
