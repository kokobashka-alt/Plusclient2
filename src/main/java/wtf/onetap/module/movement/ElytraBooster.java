package wtf.onetap.module.movement;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.ElytraMode;
import wtf.onetap.mixin.entity.LivingEntityInvoker;
import wtf.onetap.mixin.entity.projectile.FireworkRocketEntityAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.combat.ElytraTarget;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "ElytraBooster", description = "Увеличивает буст фейерверков", category = Category.MOVEMENT)
public class ElytraBooster extends Module {
   private static final int[] VALUE_ARRAY = new int[]{-45, 45, 135, -135};
   private static final int[] VALUE_ARRAY2 = new int[]{-45, 45};
   private static final float[] FLOAT_VALUE_ARRAY = new float[]{
      Float.intBitsToFloat(1070386381),
      Float.intBitsToFloat(1070638039),
      Float.intBitsToFloat(1070889697),
      Float.intBitsToFloat(1071309128),
      Float.intBitsToFloat(1071476900),
      Float.intBitsToFloat(1072147988),
      Float.intBitsToFloat(1072147988),
      Float.intBitsToFloat(1072147988),
      Float.intBitsToFloat(1072147988)
   };
   private static final float[] FLOAT_VALUE_ARRAY2 = new float[]{
      Float.intBitsToFloat(1070386381),
      Float.intBitsToFloat(1070554153),
      Float.intBitsToFloat(1070386381),
      Float.intBitsToFloat(1070554153),
      Float.intBitsToFloat(1070721925),
      Float.intBitsToFloat(1071057469),
      Float.intBitsToFloat(1071644672),
      Float.intBitsToFloat(1072902963),
      Float.intBitsToFloat(1074412913)
   };
   private final ModeSetting mode = new ModeSetting("Mode", "Rage", "Rage", "ReallyWorld", "Custom", "CustomPitch", "Bravo", "Grim", "GroundedGrim").build();
   private final FloatSetting grimMargin = new FloatSetting("Grim Margin", 0.002F, 0.0F, 0.05F, 5.0E-4F).visibleWhen(this::isGrimMode).build();
   private final FloatSetting residualCap = new FloatSetting("Residual Cap", 0.05F, 0.003F, 0.1F, 0.001F).visibleWhen(() -> ((String)this.mode.getValue()).equals("GroundedGrim")).build();
   private final BooleanSetting booleanSetting;
   private final ModeSetting modeSetting;
   private final FloatSetting floatSetting;
   private final FloatSetting floatSetting2;
   private final FloatSetting floatSetting3;
   private final FloatSetting floatSetting4;
   private final FloatSetting floatSetting5;
   private final FloatSetting floatSetting6;
   private final FloatSetting floatSetting7;
   private final FloatSetting floatSetting8;
   private final FloatSetting floatSetting9;
   private final FloatSetting floatSetting10;
   private final FloatSetting floatSetting11;
   private final FloatSetting floatSetting12;
   private final FloatSetting floatSetting13;
   private final FloatSetting floatSetting14;
   private final FloatSetting floatSetting15;
   private final FloatSetting floatSetting16;
   private final FloatSetting floatSetting17;
   private final FloatSetting floatSetting18;
   private final FloatSetting floatSetting19;
   private final FloatSetting floatSetting20;
   private final FloatSetting floatSetting21;
   private final FloatSetting floatSetting22;
   private final FloatSetting floatSetting23;
   private final FloatSetting floatSetting24;
   private final FloatSetting floatSetting25;
   private final FloatSetting floatSetting26;
   private final FloatSetting floatSetting27;
   private final FloatSetting floatSetting28;
   private final FloatSetting floatSetting29;
   private final FloatSetting floatSetting30;
   private final FloatSetting floatSetting31;
   private Vec3 vec3;
   private Vec3 vec32;
   private Vec3 vec33;
   private int value;
   private float floatValue;
   private double doubleValue;
   private double doubleValue2;
   private float floatValue2;
   private float floatValue3;
   private int value2;

   public ElytraBooster() {
      NoClip.m_4_C3_sArr();
      this.booleanSetting = new BooleanSetting("Boost Up", false).visibleWhen(() -> ((String)this.mode.getValue()).equals("GroundedGrim")).build();
      this.modeSetting = new ModeSetting("ReallyWorld Mode", "Rage", "Rage", "Legit").visibleWhen(() -> ((String)this.mode.getValue()).equals("ReallyWorld")).build();
      this.floatSetting = new FloatSetting("Custom Speed", 1.63F, 1.0F, 2.3F, 0.01F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Custom")).build();
      this.floatSetting2 = new FloatSetting("Bravo Axis", 1.63F, 1.5F, 1.8F, 0.005F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Bravo")).build();
      this.floatSetting3 = new FloatSetting("Bravo Limit", 45.0F, 30.0F, 60.0F, 0.5F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Bravo")).build();
      this.floatSetting4 = new FloatSetting("Bravo Margin", 0.3F, 0.0F, 3.0F, 0.05F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Bravo")).build();
      this.floatSetting5 = this.createPitchSetting("XZ 0-5");
      this.floatSetting6 = this.createPitchSetting("XZ 5-10");
      this.floatSetting7 = this.createPitchSetting("XZ 10-15");
      this.floatSetting8 = this.createPitchSetting("XZ 15-20");
      this.floatSetting9 = this.createPitchSetting("XZ 20-25");
      this.floatSetting10 = this.createPitchSetting("XZ 25-30");
      this.floatSetting11 = this.createPitchSetting("XZ 30-35");
      this.floatSetting12 = this.createPitchSetting("XZ 35-40");
      this.floatSetting13 = this.createPitchSetting("XZ 40-45");
      this.floatSetting14 = this.createPitchSetting("Pitch Up 0-5");
      this.floatSetting15 = this.createPitchSetting("Pitch Up 5-10");
      this.floatSetting16 = this.createPitchSetting("Pitch Up 10-15");
      this.floatSetting17 = this.createPitchSetting("Pitch Up 15-20");
      this.floatSetting18 = this.createPitchSetting("Pitch Up 20-25");
      this.floatSetting19 = this.createPitchSetting("Pitch Up 25-30");
      this.floatSetting20 = this.createPitchSetting("Pitch Up 30-35");
      this.floatSetting21 = this.createPitchSetting("Pitch Up 35-40");
      this.floatSetting22 = this.createPitchSetting("Pitch Up 40-45");
      this.floatSetting23 = this.createPitchSetting("Pitch Down 0-5");
      this.floatSetting24 = this.createPitchSetting("Pitch Down 5-10");
      this.floatSetting25 = this.createPitchSetting("Pitch Down 10-15");
      this.floatSetting26 = this.createPitchSetting("Pitch Down 15-20");
      this.floatSetting27 = this.createPitchSetting("Pitch Down 20-25");
      this.floatSetting28 = this.createPitchSetting("Pitch Down 25-30");
      this.floatSetting29 = this.createPitchSetting("Pitch Down 30-35");
      this.floatSetting30 = this.createPitchSetting("Pitch Down 35-40");
      this.floatSetting31 = this.createPitchSetting("Pitch Down 40-45");
      this.value = Integer.MIN_VALUE;
      this.floatValue = Float.NaN;
      this.doubleValue = Double.NaN;
      this.value2 = Integer.MIN_VALUE;
   }

   public static ElytraBooster m_3_C0_l() {
      ElytraBooster elytraBooster = Onetap.MODULE_MANAGER.get(ElytraBooster.class);
      return elytraBooster != null && elytraBooster.isEnabled() && elytraBooster.isGrimMode() ? elytraBooster : null;
   }

   private boolean isGrimMode() {
      return ((String)this.mode.getValue()).equals("Grim") || ((String)this.mode.getValue()).equals("GroundedGrim");
   }

   public Vec3 m_0(LocalPlayer localPlayer, Vec3 vec34, Vec3 vec35) {
      float floatValue4 = localPlayer.getYRot();
      NoClip.m_4_C3_sArr();
      float floatValue5 = localPlayer.getXRot();
      Vec3 vec36 = localPlayer.calculateViewVector(floatValue5, floatValue4);
      Vec3 vec37 = this.value2 == localPlayer.tickCount - 1 ? localPlayer.calculateViewVector(this.floatValue3, this.floatValue2) : vec36;
      this.floatValue2 = floatValue4;
      this.floatValue3 = floatValue5;
      this.value2 = localPlayer.tickCount;
      int value3 = m_4(localPlayer);
      if (value3 == 0) {
         return vec35;
      }

      if (((String)this.mode.getValue()).equals("GroundedGrim")) {
         return this.m_3(localPlayer, vec34, vec35, vec36, vec37, value3);
      }

      double doubleValue3 = this.grimMargin.getFloat();
      double[] doubleArray = new double[3];
      double[] doubleArray2 = new double[3];

      for (Axis axis : Axis.VALUES) {
         int value4 = axis.ordinal();
         double doubleValue4 = vec36.get(axis);
         double doubleValue5 = vec37.get(axis);
         double doubleValue6 = Math.min(1.7, 1.7 * (Math.max(0.05, doubleValue4) + Math.max(0.05, doubleValue5)));
         double doubleValue7 = Math.max(-1.7, 1.7 * (Math.min(-0.05, doubleValue4) + Math.min(-0.05, doubleValue5)));
         double doubleValue8 = vec35.get(axis);
         double doubleValue9 = vec34.get(axis);
         doubleArray2[value4] = Math.max(doubleValue8, doubleValue8 + Math.max(0.0, doubleValue6 - doubleValue9) - doubleValue3);
         doubleArray[value4] = Math.min(doubleValue8, doubleValue8 + Math.min(0.0, doubleValue7 - doubleValue9) + doubleValue3);
      }

      Vec3 vec38 = localPlayer.position();
      Vec3 vec39 = m_3(localPlayer, vec38, vec34);
      if (vec39 != null) {
         Vec3 vec310 = vec35;

         for (Axis axis2 : Axis.VALUES) {
            int value5 = axis2.ordinal();
            vec310 = vec310.with(axis2, Mth.clamp(vec39.get(axis2) - vec38.get(axis2), doubleArray[value5], doubleArray2[value5]));
         }

         return vec310;
      } else {
         double doubleValue10 = 100.0;

         for (Axis axis3 : Axis.VALUES) {
            double doubleValue11 = vec36.get(axis3);
            if (!(Math.abs(doubleValue11) < 1.0E-6)) {
               doubleValue10 = Math.min(doubleValue10, (doubleValue11 > 0.0 ? doubleArray2[axis3.ordinal()] : doubleArray[axis3.ordinal()]) / doubleValue11);
            }
         }

         doubleValue10 = Math.max(doubleValue10, 0.0);
         Vec3 vec311 = vec35;

         for (Axis axis4 : Axis.VALUES) {
            int value6 = axis4.ordinal();
            vec311 = vec311.with(axis4, Mth.clamp(vec36.get(axis4) * doubleValue10, doubleArray[value6], doubleArray2[value6]));
         }

         return vec311;
      }
   }

   private Vec3 m_3(LocalPlayer localPlayer, Vec3 vec34, Vec3 vec35, Vec3 vec36, Vec3 vec37, int value3) {
      ElytraTarget elytraTarget = ElytraTarget.getActive();
      Vec3 vec38 = elytraTarget != null ? elytraTarget.m_6_Vec3(localPlayer) : null;
      if (vec38 != null && localPlayer.distanceToSqr(vec38) < 1.75) {
         return vec35;
      }

      Vec3 vec39 = m_3(localPlayer, localPlayer.position(), vec34);
      return m_6(localPlayer, vec34, vec35, vec36, vec37, value3, vec39, (Boolean)this.booleanSetting.getValue(), this.residualCap.getFloat(), this.grimMargin.getFloat());
   }

   public static Vec3 m_6(LocalPlayer localPlayer, Vec3 vec34, Vec3 vec35, Vec3 vec36, Vec3 vec37, int value3, Vec3 vec38, boolean flag, double doubleValue3, double doubleValue4) {
      float floatValue4 = localPlayer.getXRot() * (float) (Math.PI / 180.0);
      double doubleValue5 = ((LivingEntityInvoker)localPlayer).onetap$getEffectiveGravity();
      Vec3 vec39 = localPlayer.position();
      Vec3 vec310 = vec35;
      double doubleValue6 = m_0(vec35, vec36, vec39, vec38);

      for (Vec3 vec311 : new Vec3[]{vec36, vec37}) {
         Vec3 vec312 = vec34;

         for (int i = 0; i < value3; i++) {
            vec312 = vec312.scale(0.5).add(vec311.scale(0.85));
            Vec3 vec313 = m_7(vec312, vec36, floatValue4, doubleValue5);
            double doubleValue7 = m_0(vec313, vec36, vec39, vec38);
            if (doubleValue7 > doubleValue6) {
               vec310 = vec313;
               doubleValue6 = doubleValue7;
            }
         }
      }

      int value4 = Math.min(value3, 4);
      double doubleValue8 = Math.max(0.003, doubleValue3);

      for (Axis axis : Axis.VALUES) {
         double doubleValue9 = vec36.get(axis);
         double doubleValue10 = vec38 != null ? vec38.get(axis) - vec39.get(axis) - vec310.get(axis) : 0.0;
         double doubleValue11 = vec38 != null ? Math.signum(doubleValue10) : (flag && axis == Axis.Y ? 1.0 : Math.signum(doubleValue9));
         if (doubleValue11 != 0.0) {
            double doubleValue12 = Mth.clamp(0.5 * Math.abs(doubleValue9 - vec37.get(axis)) * 0.85 * value4, 0.003, doubleValue8) - doubleValue4;
            if (vec38 != null) {
               doubleValue12 = Math.min(doubleValue12, Math.abs(doubleValue10));
            }

            if (!(doubleValue12 <= 0.0)) {
               vec310 = vec310.with(axis, vec310.get(axis) + doubleValue11 * doubleValue12);
            }
         }
      }

      return vec310;
   }

   private static double m_0(Vec3 vec34, Vec3 vec35, Vec3 vec36, Vec3 vec37) {
      return vec37 != null ? -vec36.add(vec34).distanceToSqr(vec37) : vec34.dot(vec35);
   }

   private static Vec3 m_3(LocalPlayer localPlayer, Vec3 vec34, Vec3 vec35) {
      ElytraTarget elytraTarget = ElytraTarget.getActive();
      if (elytraTarget == null) {
         return null;
      }

      Vec3 vec36 = elytraTarget.m_6_Vec3(localPlayer);
      if (vec36 == null) {
         return null;
      }

      double doubleValue3 = vec35.length() * 4.0 + Math.sqrt(1.75);
      return vec34.distanceToSqr(vec36) <= doubleValue3 * doubleValue3 ? vec36 : null;
   }

   public static Vec3 m_7(Vec3 vec34, Vec3 vec35, float floatValue4, double doubleValue3) {
      double doubleValue4 = Math.sqrt(vec35.x * vec35.x + vec35.z * vec35.z);
      double doubleValue5 = vec34.horizontalDistance();
      double doubleValue6 = Mth.square(Math.cos(floatValue4));
      vec34 = vec34.add(0.0, doubleValue3 * (-1.0 + doubleValue6 * 0.75), 0.0);
      if (vec34.y < 0.0 && doubleValue4 > 0.0) {
         double doubleValue7 = vec34.y * -0.1 * doubleValue6;
         vec34 = vec34.add(vec35.x * doubleValue7 / doubleValue4, doubleValue7, vec35.z * doubleValue7 / doubleValue4);
      }

      if (floatValue4 < 0.0F && doubleValue4 > 0.0) {
         double doubleValue8 = doubleValue5 * -Mth.sin(floatValue4) * 0.04;
         vec34 = vec34.add(-vec35.x * doubleValue8 / doubleValue4, doubleValue8 * 3.2, -vec35.z * doubleValue8 / doubleValue4);
      }

      if (doubleValue4 > 0.0) {
         vec34 = vec34.add((vec35.x / doubleValue4 * doubleValue5 - vec34.x) * 0.1, 0.0, (vec35.z / doubleValue4 * doubleValue5 - vec34.z) * 0.1);
      }

      return vec34.multiply(0.99F, 0.98F, 0.99F);
   }

   public static int m_4(LocalPlayer localPlayer) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return 0;
      }

      int value3 = 0;

      for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
         if (entity instanceof FireworkRocketEntity fireworkRocketEntity
            && fireworkRocketEntity.getEntityData().get(FireworkRocketEntityAccessor.onetap$getAttachedTargetData()).orElse(-1) == localPlayer.getId()) {
            value3++;
         }
      }

      return value3;
   }

   public static float m_7_float(LivingEntity livingEntity) {
      return m_4(livingEntity, null, null);
   }

   public static float m_4(LivingEntity livingEntity, Vec3 vec34, Vec3 vec35) {
      ElytraBooster elytraBooster = Onetap.MODULE_MANAGER.get(ElytraBooster.class);
      return elytraBooster != null && elytraBooster.isEnabled() ? elytraBooster.m_7(1.5F, livingEntity, vec34, vec35) : 1.5F;
   }

   public float m_7(float floatValue4, LivingEntity livingEntity, Vec3 vec34, Vec3 vec35) {
      if (MinecraftAccess.MINECRAFT.player != null && livingEntity == MinecraftAccess.MINECRAFT.player && !this.isGrimMode()) {
         float floatValue5 = this.getPitch();
         float floatValue6 = this.getYaw();

         float floatValue7 = switch ((String)this.mode.getValue()) {
            case "Custom" -> Math.max(this.floatSetting.getFloat(), 0.0F);
            case "ReallyWorld" -> ((String)this.modeSetting.getValue()).equals("Legit") ? this.m_3_float() : this.m_0_float(floatValue5, floatValue6);
            case "CustomPitch" -> this.m_5_float();
            case "Bravo" -> this.m_5(livingEntity, floatValue4, vec34 != null ? vec34 : livingEntity.getDeltaMovement(), vec35 != null ? vec35 : livingEntity.getLookAngle());
            default -> this.m_4_float();
         };
         ElytraTarget elytraTarget = ElytraTarget.getActive();
         if (elytraTarget != null) {
            floatValue7 = elytraTarget.applySlowdown(floatValue7, livingEntity);
         }

         return floatValue7;
      } else {
         return floatValue4;
      }
   }

   private float m_5(LivingEntity livingEntity, float floatValue4, Vec3 vec34, Vec3 vec35) {
      this.m_6(livingEntity, vec34, vec35);
      Vec3 vec36 = this.vec32;
      Vec3 vec37 = this.vec33;
      float floatValue5 = (float)Math.asin(Mth.clamp(-vec35.y, -1.0, 1.0));
      double doubleValue3 = ((LivingEntityInvoker)livingEntity).onetap$getEffectiveGravity();
      double doubleValue4 = this.m_9_double(doubleValue3);
      double doubleValue5 = Math.max(0.0F, this.floatSetting3.getFloat() - this.floatSetting4.getFloat()) / 20.0;
      Vec3 vec38 = m_7(vec37, vec35, floatValue5, doubleValue3);
      double[] doubleArray = new double[3];
      double[] doubleArray2 = new double[3];

      for (Axis axis : Axis.VALUES) {
         int value3 = axis.ordinal();
         double doubleValue6 = vec35.get(axis);
         double doubleValue7 = vec36.get(axis);
         double doubleValue8 = Math.min(doubleValue4, doubleValue4 * (Math.max(0.05, doubleValue6) + Math.max(0.05, doubleValue7)));
         double doubleValue9 = Math.max(-doubleValue4, doubleValue4 * (Math.min(-0.05, doubleValue6) + Math.min(-0.05, doubleValue7)));
         double doubleValue10 = vec37.get(axis);
         doubleArray2[value3] = vec38.get(axis) + Math.max(0.0, doubleValue8 - doubleValue10);
         doubleArray[value3] = vec38.get(axis) + Math.min(0.0, doubleValue9 - doubleValue10);
      }

      if (m_0(vec34, vec35, floatValue5, doubleValue3, 4.0, doubleArray, doubleArray2, doubleValue5)) {
         return 4.0F;
      }

      double doubleValue11 = 4.0;

      for (double doubleValue12 = 3.95; doubleValue12 > -0.05; doubleValue12 -= 0.05) {
         double doubleValue13 = Math.max(0.0, doubleValue12);
         if (m_0(vec34, vec35, floatValue5, doubleValue3, doubleValue13, doubleArray, doubleArray2, doubleValue5)) {
            for (int i = 0; i < 16; i++) {
               double doubleValue14 = (doubleValue13 + doubleValue11) * 0.5;
               if (m_0(vec34, vec35, floatValue5, doubleValue3, doubleValue14, doubleArray, doubleArray2, doubleValue5)) {
                  doubleValue13 = doubleValue14;
               } else {
                  doubleValue11 = doubleValue14;
               }
            }

            return (float)doubleValue13;
         }

         doubleValue11 = doubleValue13;
      }

      return floatValue4;
   }

   private static boolean m_0(Vec3 vec34, Vec3 vec35, float floatValue4, double doubleValue3, double doubleValue4, double[] doubleArray, double[] doubleArray2, double doubleValue5) {
      Vec3 vec36 = m_7(ElytraMode.m_3(vec34, vec35, doubleValue4), vec35, floatValue4, doubleValue3);

      for (Axis axis : Axis.VALUES) {
         double doubleValue6 = vec36.get(axis);
         if (doubleValue6 > doubleArray2[axis.ordinal()] || doubleValue6 < doubleArray[axis.ordinal()]) {
            return false;
         }
      }

      return vec36.length() <= doubleValue5;
   }

   private void m_6(LivingEntity livingEntity, Vec3 vec34, Vec3 vec35) {
      if (livingEntity.tickCount != this.value) {
         this.vec32 = this.value == livingEntity.tickCount - 1 ? this.vec3 : vec35;
         this.vec3 = vec35;
         this.vec33 = vec34;
         this.value = livingEntity.tickCount;
      }
   }

   private double m_9_double(double doubleValue3) {
      float floatValue4 = this.floatSetting2.getFloat();
      if (floatValue4 == this.floatValue && doubleValue3 == this.doubleValue) {
         return this.doubleValue2;
      }

      Vec3 vec34 = new Vec3(0.0, 0.0, 1.0);
      Vec3 vec35 = Vec3.ZERO;

      for (int i = 0; i < 400; i++) {
         vec35 = m_7(ElytraMode.m_3(vec35, vec34, floatValue4), vec34, 0.0F, doubleValue3);
      }

      this.floatValue = floatValue4;
      this.doubleValue = doubleValue3;
      this.doubleValue2 = 2.0 * vec35.z - m_7(vec35, vec34, 0.0F, doubleValue3).z;
      return this.doubleValue2;
   }

   private FloatSetting createPitchSetting(String text) {
      return new FloatSetting(text, 1.61F, 1.5F, 2.5F, 0.01F).visibleWhen(() -> ((String)this.mode.getValue()).equals("CustomPitch")).build();
   }

   private float getPitch() {
      return Mth.wrapDegrees(MinecraftAccess.MINECRAFT.player.getXRot());
   }

   private float getYaw() {
      return Mth.wrapDegrees(MinecraftAccess.MINECRAFT.player.getYRot());
   }

   private float m_4_float() {
      float floatValue4 = Mth.wrapDegrees(MinecraftAccess.MINECRAFT.player.getYRot());
      float[] floatArray = new float[]{45.0F, 135.0F, 225.0F, 315.0F};
      float floatValue5 = 180.0F;

      for (float floatValue6 : floatArray) {
         floatValue5 = Math.min(floatValue5, Math.abs(Mth.wrapDegrees(floatValue4 - floatValue6)));
      }

      float floatValue7 = floatValue5 / 45.0F;
      float floatValue8 = 1.0F - floatValue7;
      float floatValue9 = (float)Math.pow(Math.max(0.0F, floatValue8), 2.8F);
      float floatValue10 = MinecraftAccess.MINECRAFT.player.getXRot();
      float floatValue11 = 0.13F;
      if (floatValue10 <= -90.0F) {
         floatValue11 = 0.126F;
      }

      if (floatValue10 <= -75.0F) {
         floatValue11 = 0.123F;
      }

      float floatValue12 = 0.89F - Math.abs(floatValue10) / 90.0F;
      float floatValue13 = floatValue11 + (floatValue12 - floatValue11) * floatValue9;
      float floatValue14 = Math.abs(Mth.wrapDegrees(MinecraftAccess.MINECRAFT.player.getYRot() - MinecraftAccess.MINECRAFT.player.yHeadRotO));
      if (floatValue14 > 45.0F) {
         floatValue13 = 0.0F;
      }

      float floatValue15 = floatValue13;
      float floatValue16;
      if (floatValue10 >= -75.0F) {
         float floatValue17 = Math.abs(floatValue10 - -45.0F);
         float floatValue18 = Math.max(0.0F, Math.min(1.0F, 1.0F - Math.min(floatValue17 / 45.0F, 1.0F)));
         float floatValue19 = (float)Math.pow(floatValue18, 1.4F);
         if (floatValue10 <= -50.0F) {
            floatValue19 = (float)Math.pow(floatValue18, 1.5);
         }

         if (floatValue10 <= -61.0F) {
            floatValue19 = (float)Math.pow(floatValue18, 1.9F);
         }

         if (floatValue10 <= -50.0F) {
            floatValue19 = (float)Math.pow(floatValue18, 1.9F);
         }

         floatValue16 = 0.79F * floatValue19;
      } else {
         floatValue16 = 0.0F;
      }

      return 1.5F + Math.max(floatValue15, floatValue16);
   }

   private float m_0_float(float floatValue4, float floatValue5) {
      return Math.max(this.m_0_float(floatValue5), this.m_8_float(floatValue4));
   }

   private float m_3_float() {
      return this.m_6(this.getPitch(), this.getYaw(), false, true);
   }

   private float m_5_float() {
      return Math.max(this.m_9(this.getYaw()), this.m_7_float(this.getPitch()));
   }

   private float m_9(float floatValue4) {
      float floatValue5 = m_6_float(floatValue4);
      if (floatValue5 <= 5.0F) {
         return this.floatSetting5.getFloat();
      } else if (floatValue5 <= 10.0F) {
         return this.floatSetting6.getFloat();
      } else if (floatValue5 <= 15.0F) {
         return this.floatSetting7.getFloat();
      } else if (floatValue5 <= 20.0F) {
         return this.floatSetting8.getFloat();
      } else if (floatValue5 <= 25.0F) {
         return this.floatSetting9.getFloat();
      } else if (floatValue5 <= 30.0F) {
         return this.floatSetting10.getFloat();
      } else if (floatValue5 <= 35.0F) {
         return this.floatSetting11.getFloat();
      } else {
         return floatValue5 <= 40.0F ? this.floatSetting12.getFloat() : this.floatSetting13.getFloat();
      }
   }

   private float m_7_float(float floatValue4) {
      float floatValue5 = m_6_float(floatValue4);
      boolean flag = floatValue4 < 0.0F;
      if (floatValue5 <= 5.0F) {
         return flag ? this.floatSetting14.getFloat() : this.floatSetting23.getFloat();
      } else if (floatValue5 <= 10.0F) {
         return flag ? this.floatSetting15.getFloat() : this.floatSetting24.getFloat();
      } else if (floatValue5 <= 15.0F) {
         return flag ? this.floatSetting16.getFloat() : this.floatSetting25.getFloat();
      } else if (floatValue5 <= 20.0F) {
         return flag ? this.floatSetting17.getFloat() : this.floatSetting26.getFloat();
      } else if (floatValue5 <= 25.0F) {
         return flag ? this.floatSetting18.getFloat() : this.floatSetting27.getFloat();
      } else if (floatValue5 <= 30.0F) {
         return flag ? this.floatSetting19.getFloat() : this.floatSetting28.getFloat();
      } else if (floatValue5 <= 35.0F) {
         return flag ? this.floatSetting20.getFloat() : this.floatSetting29.getFloat();
      } else if (floatValue5 <= 40.0F) {
         return flag ? this.floatSetting21.getFloat() : this.floatSetting30.getFloat();
      } else {
         return flag ? this.floatSetting22.getFloat() : this.floatSetting31.getFloat();
      }
   }

   private static float m_2(float[] floatArray, float floatValue4) {
      int value3 = (int)(m_6_float(floatValue4) / 5.0F);
      if (value3 >= floatArray.length) {
         value3 = floatArray.length - 1;
      }

      if (value3 < 0) {
         value3 = 0;
      }

      return floatArray[value3];
   }

   private float m_0_float(float floatValue4) {
      return m_2(FLOAT_VALUE_ARRAY, floatValue4);
   }

   private float m_8_float(float floatValue4) {
      return m_2(FLOAT_VALUE_ARRAY2, floatValue4);
   }

   private float m_6(float floatValue4, float floatValue5, boolean flag, boolean flag2) {
      if (Math.abs(floatValue4) > 55.0F) {
         return 1.55F;
      }

      float floatValue6 = this.m_8(floatValue5, flag2);
      floatValue6 = this.m_9_float(floatValue4, floatValue6);
      floatValue6 = Math.max(flag ? 1.65F : 1.6F, floatValue6);
      return Math.min(floatValue6, flag ? 1.9F : 2.2F);
   }

   private float m_8(float floatValue4, boolean flag) {
      int value3 = m_3(floatValue4, VALUE_ARRAY);
      if (value3 == -1) {
         return 1.6F;
      }

      float floatValue5 = Math.abs(Mth.wrapDegrees(floatValue4) - VALUE_ARRAY[value3]);
      float floatValue6 = 2.2F;
      float floatValue7 = 1.6F;
      float floatValue8 = 12.0F;
      float floatValue9 = 0.0F;
      if (floatValue5 <= floatValue8) {
         float floatValue10 = floatValue5 / floatValue8;
         floatValue9 = floatValue6 - (floatValue6 - floatValue7) * floatValue10;
      }

      float floatValue11 = m_4_float(floatValue5);
      float floatValue12 = Math.max(floatValue9, floatValue11);
      return flag ? Math.min(floatValue12, 1.8F) : floatValue12;
   }

   private float m_9_float(float floatValue4, float floatValue5) {
      int value3 = m_3(floatValue4, VALUE_ARRAY2);
      if (value3 == -1) {
         return floatValue5;
      }

      float floatValue6 = Math.abs(Math.abs(floatValue4) - Math.abs(VALUE_ARRAY2[value3]));
      if (floatValue6 < 30.0F) {
         floatValue5 += 0.4F * (1.0F - floatValue6 / 30.0F);
      }

      return floatValue5;
   }

   private static float m_4_float(float floatValue4) {
      float[] floatArray = new float[]{4.0F, 8.0F, 11.0F, 15.0F, 21.0F, 28.0F};
      float[] floatArray2 = new float[]{2.2F, 2.1F, 2.0F, 1.9F, 1.8F, 1.7F, 1.6F};
      int value3 = 0;

      while (value3 < floatArray.length && floatValue4 >= floatArray[value3]) {
         value3++;
      }

      return floatArray2[value3];
   }

   private static int m_3(float floatValue4, int[] intArray) {
      int value3 = -1;
      float floatValue5 = Float.MAX_VALUE;

      for (int i = 0; i < intArray.length; i++) {
         float floatValue6 = Math.abs(Mth.wrapDegrees(floatValue4) - intArray[i]);
         if (floatValue6 < floatValue5) {
            floatValue5 = floatValue6;
            value3 = i;
         }
      }

      return value3;
   }

   private static float m_6_float(float floatValue4) {
      float floatValue5 = Math.abs(floatValue4);
      if (floatValue5 > 90.0F) {
         floatValue5 = 180.0F - floatValue5;
      }

      if (floatValue5 > 45.0F) {
         floatValue5 = 90.0F - floatValue5;
      }

      return floatValue5;
   }
}
