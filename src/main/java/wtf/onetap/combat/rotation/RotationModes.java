package wtf.onetap.combat.rotation;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.pve.CombatPlanner;
import wtf.onetap.util.LatencyStats;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

public class RotationModes implements MinecraftAccess {
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private float floatValue7;
   private RotationMode rotationMode;
   private int value;
   private long longValue;
   private boolean flag5;
   private float floatValue8;
   private float floatValue9;
   private boolean flag6;
   private final List<RotationMode> rotationModeList;
   private boolean flag7;
   private boolean flag8;
   private long longValue2;
   private int value2;
   private final Map<RotationMode, LatencyStats.StateHolder> rotationModeMap;
   private final Map<RotationMode, LatencyStats.StateHolder> rotationModeMap2;
   private static final List<String> BASE_MODES = List.of("Vanilla", "SP (old)", "Snap", "Snap2", "Рывки", "Shakal", "LegitSnap", "Assist", "Legit", "Wander", "HolyWorld", "NoRot");
   private static final List<String> SHARD_MODES = List.of("Shard", "Shard AI");
   private static final List<String> STRING_LIST = java.util.stream.Stream.concat(BASE_MODES.stream(), SHARD_MODES.stream()).toList();
   private static final Set<String> STRING_SET = Set.copyOf(STRING_LIST);
   private static final Set<String> STRING_SET2 = Set.of();
   private Vec3 vec3;
   private AABB aABB;
   private LivingEntity livingEntity;
   private String text;
   private String text2;
   private boolean flag9;
   private float floatValue10;
   private long longValue3;
   private static int value3;

   public RotationModes() {
      this.value = Integer.MIN_VALUE;
      this.rotationModeList = List.of(new SpOldRotation(), new SnapRotation(), new Snap2Rotation(), new DashRotation(), new ShakalRotation(), new LegitSnapRotation(), new AssistRotation(), new LegitRotation(), new WanderRotation(), new HolyWorldRotation());
      this.rotationModeMap = new IdentityHashMap<>();
      this.rotationModeMap2 = new IdentityHashMap<>();

      for (RotationMode rotationMode2 : this.rotationModeList) {
         this.rotationModeMap.put(rotationMode2, LatencyStats.getOrCreate("rotation " + rotationMode2.getInfo().name()));
         this.rotationModeMap2.put(rotationMode2, LatencyStats.getOrCreate("rotation " + rotationMode2.getInfo().name() + " return"));
      }
   }

   public void setSpoofedRotation(float floatValue11, float floatValue12) {
      this.flag5 = true;
      this.floatValue5 = floatValue11;
      this.floatValue6 = floatValue12;
   }

   public void setSpoofedRotationSilent(float floatValue11, float floatValue12) {
      this.floatValue5 = floatValue11;
      this.floatValue6 = floatValue12;
   }

   public void syncWithPlayer() {
      this.flag5 = false;
      if (MinecraftAccess.MINECRAFT.player != null) {
         this.floatValue5 = MinecraftAccess.MINECRAFT.player.getYRot();
         this.floatValue6 = MinecraftAccess.MINECRAFT.player.getXRot();
         this.floatValue = this.floatValue5;
         this.floatValue2 = this.floatValue6;
         this.floatValue3 = this.floatValue5;
         this.floatValue4 = this.floatValue6;
      }
   }

   public boolean isFlag5() {
      return this.flag5;
   }

   public void beginTick() {
      this.value = Integer.MIN_VALUE;
      this.flag3 = false;
      if (MinecraftAccess.MINECRAFT.player == null) {
         this.flag = false;
         this.flag2 = false;
         this.longValue = 0L;
      } else {
         if (!this.flag && !this.flag5) {
            this.floatValue5 = MinecraftAccess.MINECRAFT.player.getYRot();
            this.floatValue6 = MinecraftAccess.MINECRAFT.player.getXRot();
            this.floatValue = this.floatValue5;
            this.floatValue2 = this.floatValue6;
            this.floatValue3 = this.floatValue;
            this.floatValue4 = this.floatValue2;
         } else {
            this.floatValue3 = this.floatValue;
            this.floatValue4 = this.floatValue2;
            Rotations rotations = this.rotationMode == null ? null : this.rotationMode.m_8_C3b();
            if (rotations != null) {
               this.applyServerRotation(this.applyMouseSensitivity(rotations));
            }
         }
      }
   }

   public void endTick() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         this.flag = false;
         this.flag2 = false;
         this.longValue = 0L;
      } else {
         if (!this.flag3) {
            this.text = null;
            if (this.flag2) {
               this.m_3_void();
               return;
            }

            if (this.flag5) {
               this.flag = false;
               return;
            }

            this.flag = false;
            if (this.flag4) {
               this.floatValue5 = this.floatValue;
               this.floatValue6 = this.floatValue2;
               this.resetModes();
               this.rotationMode = null;
            } else {
               this.returnToCamera();
            }
         }
      }
   }

   public void reset() {
      this.flag = false;
      this.flag2 = false;
      this.flag3 = false;
      this.longValue = 0L;
      this.resetModes();
      this.rotationMode = null;
      this.text = null;
      this.resetSmoothing();
      if (MinecraftAccess.MINECRAFT.player != null) {
         this.floatValue = MinecraftAccess.MINECRAFT.player.getYRot();
         this.floatValue2 = MinecraftAccess.MINECRAFT.player.getXRot();
         this.floatValue5 = this.floatValue;
         this.floatValue6 = this.floatValue2;
         this.floatValue3 = this.floatValue;
         this.floatValue4 = this.floatValue2;
      }
   }

   public void restorePlayerRotation() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         this.flag = false;
         this.flag2 = false;
         this.longValue = 0L;
         this.flag3 = true;
         this.resetModes();
         this.rotationMode = null;
         this.text = null;
         MinecraftAccess.MINECRAFT.player.setYRot(this.floatValue5);
         MinecraftAccess.MINECRAFT.player.yRotO = this.floatValue5;
         MinecraftAccess.MINECRAFT.player.setXRot(this.floatValue6);
         MinecraftAccess.MINECRAFT.player.xRotO = this.floatValue6;
      }
   }

   public void applyAndFinish() {
      if (MinecraftAccess.MINECRAFT.player != null && this.flag) {
         this.floatValue5 = this.floatValue;
         this.floatValue6 = this.floatValue2;
         MinecraftAccess.MINECRAFT.player.setYRot(this.floatValue);
         MinecraftAccess.MINECRAFT.player.yRotO = this.floatValue;
         MinecraftAccess.MINECRAFT.player.setXRot(this.floatValue2);
         MinecraftAccess.MINECRAFT.player.xRotO = this.floatValue2;
         this.floatValue3 = this.floatValue;
         this.floatValue4 = this.floatValue2;
         this.flag = false;
         this.flag2 = false;
         this.longValue = 0L;
         this.resetModes();
         this.rotationMode = null;
         this.text = null;
         this.flag3 = true;
      }
   }

   public void rotateByModeNoBox(Vec3 vec32, String text3, boolean flag10, int value4) {
      this.rotateByModeFlagged(vec32, null, text3, flag10, value4, false);
   }

   public void rotateWithSensitivity(Rotations rotations, boolean flag10, int value4) {
      this.rotateInternal(rotations, flag10, value4, true);
   }

   public void rotate(Rotations rotations, boolean flag10, int value4) {
      this.rotateInternal(rotations, flag10, value4, false);
   }

   private void rotateInternal(Rotations rotations, boolean flag10, int value4, boolean flag11) {
      if (value4 >= this.value) {
         this.text = null;
         this.text2 = null;
         if (this.rotationMode != null) {
            this.resetModes();
            this.rotationMode = null;
         }

         this.setTargetRotation(flag11 ? this.applyMouseSensitivity(rotations) : rotations, flag10, value4);
      }
   }

   public void rotateByMode(Vec3 vec32, AABB aABB2, String text3, boolean flag10, int value4) {
      this.rotateByModeFlagged(vec32, aABB2, text3, flag10, value4, false);
   }

   public void rotateByModeFlagged(Vec3 vec32, AABB aABB2, String text3, boolean flag10, int value4, boolean flag11) {
      this.rotateByModeFull(vec32, aABB2, text3, flag10, value4, flag11, false, 0.0F, null);
   }

   public void rotateByModeFull(Vec3 vec32, AABB aABB2, String text3, boolean flag10, int value4, boolean flag11, boolean flag12, float floatValue11, LivingEntity livingEntity2) {
      if (value4 >= this.value) {
         this.text2 = text3;
         int value5 = this.getModePriority(text3);
         if (value5 > 0) {
            if (this.text == null) {
               this.longValue3 = System.nanoTime();
            }

            this.text = text3;
            this.vec3 = vec32;
            this.aABB = aABB2;
            this.livingEntity = livingEntity2;
            this.flag9 = flag11;
            this.floatValue10 = floatValue11;
            this.value = value4;
            this.flag4 = flag10;
            this.flag2 = false;
            this.longValue = 0L;
            this.flag3 = true;
            this.flag = true;
         } else {
            this.text = null;
            Rotations rotations = this.computeModeRotation(text3, vec32, aABB2, flag11, floatValue11, 0.05F, livingEntity2, flag10);
            if (rotations != null) {
               this.applyModeRotation(rotations, flag10, value4);
            }
         }
      }
   }

   public void updateModeRotation() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (!this.flag2) {
            if (this.text != null) {
               int value4 = this.getModePriority(this.text);
               if (value4 > 0) {
                  long longValue4 = System.nanoTime();
                  float floatValue11 = Math.min((float)(longValue4 - this.longValue3) / 1.0E9F, 0.25F);
                  if (!(floatValue11 < 1.0F / value4)) {
                     this.longValue3 = longValue4;
                     Rotations rotations = this.computeModeRotation(this.text, this.vec3, this.aABB, this.flag9, this.floatValue10, floatValue11, this.livingEntity, this.flag4);
                     if (rotations != null) {
                        this.applyModeRotation(rotations, this.flag4, this.value);
                     }
                  }
               }
            }
         }
      }
   }

   public void updateTarget(Vec3 vec32, AABB aABB2) {
      if (this.text != null) {
         this.vec3 = vec32;
         this.aABB = aABB2;
      }
   }

   private Rotations computeModeRotation(String text3, Vec3 vec32, AABB aABB2, boolean flag10, float floatValue11, float floatValue12, LivingEntity livingEntity2, boolean flag11) {
      Rotations rotations = this.getRotationTo(vec32);
      RotationMode rotationMode2 = this.findMode(text3);
      this.rotationMode = rotationMode2;
      if (rotationMode2 == null) {
         return rotations;
      }

      long longValue4 = System.nanoTime();
      Rotations rotations2 = rotationMode2.m_9(this.floatValue, this.floatValue2, rotations, aABB2, flag10, floatValue11, floatValue12, livingEntity2, vec32, flag11);
      this.rotationModeMap.get(rotationMode2).recordSince(longValue4);
      return rotations2;
   }

   private void applyModeRotation(Rotations rotations, boolean flag10, int value4) {
      this.setTargetRotation(this.rotationMode != null && this.rotationMode.m_6_boolean() ? rotations : this.applyMouseSensitivity(rotations), flag10, value4);
   }

   private RotationMode findMode(String text3) {
      for (RotationMode rotationMode2 : this.rotationModeList) {
         if (rotationMode2.getInfo().name().equalsIgnoreCase(text3)) {
            return rotationMode2;
         }
      }

      return null;
   }

   private int getModePriority(String text3) {
      RotationMode rotationMode2 = this.findMode(text3);
      return rotationMode2 == null ? 0 : rotationMode2.m_6_int();
   }

   public String[] getModeNames() {
      return STRING_LIST.toArray(String[]::new);
   }

   public static boolean isCustomMode(String text3) {
      return STRING_LIST.contains(text3) && !STRING_SET.contains(text3);
   }

   public static boolean isShardMode(String text3) {
      return SHARD_MODES.contains(text3);
   }

   public static ShardbetaAuraMode parseShardMode(String text3) {
      return "Shard AI".equals(text3) ? ShardbetaAuraMode.SHARD_AI : ShardbetaAuraMode.SHARD;
   }

   public Vec3 getModeAimOffset(String text3) {
      RotationMode rotationMode2 = this.findMode(text3);
      return rotationMode2 == null ? Vec3.ZERO : rotationMode2.m_1_Vec3();
   }

   public void m_1_void() {
      if (this.rotationMode != null) {
         this.rotationMode.m_8_void();
      }
   }

   private void setTargetRotation(Rotations rotations, boolean flag10, int value4) {
      this.floatValue = rotations.getFloatValue();
      this.floatValue2 = Mth.clamp(rotations.getFloatValue2(), -90.0F, 90.0F);
      this.value = value4;
      this.flag4 = flag10;
      this.flag2 = false;
      this.longValue = 0L;
      this.flag3 = true;
      this.flag = true;
      MinecraftAccess.MINECRAFT.player.setYRot(this.floatValue);
      MinecraftAccess.MINECRAFT.player.setXRot(this.floatValue2);
   }

   private Rotations applyMouseSensitivity(Rotations rotations) {
      float floatValue11 = MinecraftAccess.MINECRAFT.options.sensitivity().get().floatValue() * 0.6F + 0.2F;
      float floatValue12 = floatValue11 * floatValue11 * floatValue11 * 1.2F;
      float floatValue13 = this.floatValue + Math.round(Mth.wrapDegrees(rotations.getFloatValue() - this.floatValue) / floatValue12) * floatValue12;
      float floatValue14 = this.floatValue2 + Math.round((rotations.getFloatValue2() - this.floatValue2) / floatValue12) * floatValue12;
      return new Rotations(floatValue13, Mth.clamp(floatValue14, -90.0F, 90.0F));
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public Rotations getRotationTo(Vec3 vec32) {
      Vec3 vec33 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      double doubleValue = vec32.x - vec33.x;
      double doubleValue2 = vec32.y - vec33.y;
      double doubleValue3 = vec32.z - vec33.z;
      double doubleValue4 = Math.sqrt(doubleValue * doubleValue + doubleValue3 * doubleValue3);
      float floatValue11 = Mth.wrapDegrees((float)(Math.toDegrees(Math.atan2(doubleValue3, doubleValue)) - 90.0));
      float floatValue12 = Mth.clamp((float)(-Math.toDegrees(Math.atan2(doubleValue2, doubleValue4))), -90.0F, 90.0F);
      return new Rotations(floatValue11, floatValue12);
   }

   public Rotations getRotationTo(BlockPos blockPos) {
      return this.getRotationTo(new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5));
   }

   public void lookAtHumanized(BlockPos blockPos, int value4) {
      this.lookAtHumanized(new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5), value4);
   }

   public void lookAtHumanized(Vec3 vec32, int value4) {
      if (MinecraftAccess.MINECRAFT.player != null && value4 >= this.value) {
         Rotations rotations = this.getRotationTo(vec32);
         Rotations rotations2 = this.humanize(rotations);
         this.setTargetRotation(this.applyMouseSensitivity(rotations2), true, value4);
      }
   }

   public void lookAt(BlockPos blockPos, int value4) {
      this.lookAtClamped(new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5), value4);
   }

   public void lookAtClamped(Vec3 vec32, int value4) {
      if (MinecraftAccess.MINECRAFT.player != null && value4 >= this.value) {
         Rotations rotations = this.getRotationTo(vec32);
         float floatValue11 = Mth.wrapDegrees(rotations.getFloatValue());
         float floatValue12 = Mth.clamp(rotations.getFloatValue2(), -89.8F, 89.8F);
         this.floatValue8 = floatValue11;
         this.floatValue9 = floatValue12;
         this.flag6 = true;
         this.setTargetRotation(this.applyMouseSensitivity(new Rotations(floatValue11, floatValue12)), true, value4);
      }
   }

   public void lookAtAngles(float floatValue11, float floatValue12, int value4) {
      if (MinecraftAccess.MINECRAFT.player != null && value4 >= this.value) {
         float floatValue13 = Mth.wrapDegrees(floatValue11);
         float floatValue14 = Mth.clamp(floatValue12, -89.8F, 89.8F);
         this.floatValue8 = floatValue13;
         this.floatValue9 = floatValue14;
         this.flag6 = true;
         this.setTargetRotation(this.applyMouseSensitivity(new Rotations(floatValue13, floatValue14)), true, value4);
      }
   }

   public void resetSmoothing() {
      this.flag6 = false;
      this.floatValue8 = 0.0F;
      this.floatValue9 = 0.0F;
   }

   private Rotations humanize(Rotations rotations) {
      double doubleValue = System.nanoTime() / 1.0E8;
      float floatValue11 = Mth.wrapDegrees(rotations.getFloatValue());
      float floatValue12 = Mth.clamp(rotations.getFloatValue2(), -89.8F, 89.8F);
      float floatValue13 = (float)Math.sin(doubleValue * 0.8 + (Math.PI / 2)) * 4.5F;
      float floatValue14 = (float)Math.sin(doubleValue * 1.2 + (Math.PI * 6.0 / 5.0)) * 3.2F;
      float floatValue15 = (float)Math.sin(doubleValue * 0.5 + (Math.PI * 4.0 / 5.0)) * 2.1F;
      float floatValue16 = (float)Math.cos(doubleValue * 1.0 + (Math.PI * 3.0 / 10.0)) * 5.7F;
      float floatValue17 = (float)Math.cos(doubleValue * 0.7 + (Math.PI * 3.0 / 2.0)) * 3.8F;
      float floatValue18 = (float)Math.sin(doubleValue * 0.2) * 8.0F;
      float floatValue19 = (float)Math.cos(doubleValue * 0.15) * 6.0F;
      float floatValue20 = Mth.wrapDegrees(floatValue11 + floatValue13 + floatValue14 + floatValue18);
      float floatValue21 = Mth.clamp(floatValue12 + floatValue15 + floatValue16 + floatValue17 + floatValue19, -89.8F, 89.8F);
      float floatValue22 = this.flag6 ? this.floatValue8 : Mth.wrapDegrees(MinecraftAccess.MINECRAFT.player.getYRot());
      float floatValue23 = this.flag6 ? this.floatValue9 : Mth.wrapDegrees(MinecraftAccess.MINECRAFT.player.getXRot());
      float floatValue24 = 0.23F + (float)Math.sin(doubleValue * 0.1) * 0.07F;
      float floatValue25 = 0.15F + (float)Math.cos(doubleValue * 0.08) * 0.05F;
      float floatValue26 = Mth.wrapDegrees(floatValue20 - floatValue22);
      float floatValue27 = Mth.wrapDegrees(floatValue22 + floatValue26 * floatValue24);
      float floatValue28 = floatValue21 - floatValue23;
      float floatValue29 = Mth.clamp(floatValue23 + floatValue28 * floatValue25, -89.8F, 89.8F);
      this.floatValue8 = floatValue27;
      this.floatValue9 = floatValue29;
      this.flag6 = true;
      return new Rotations(floatValue27, floatValue29);
   }

   public Rotations toRotations() {
      return new Rotations(this.floatValue, this.floatValue2);
   }

   public Rotations getTargetRotation() {
      return new Rotations(this.floatValue3, this.floatValue4);
   }

   public void m_0_void() {
      this.startReturn(0L);
   }

   public void startReturn(long longValue4) {
      if (MinecraftAccess.MINECRAFT.player != null && !this.flag2 && this.flag) {
         if (this.flag4) {
            this.applyAndFinish();
         } else {
            this.flag2 = true;
            this.flag3 = false;
            this.floatValue7 = 0.0F;
            this.text = null;
            this.longValue = longValue4 > 0L ? System.currentTimeMillis() + longValue4 : 0L;
            this.flag7 = this.text2 != null && STRING_SET2.contains(this.text2);
            this.longValue2 = CombatPlanner.COMBAT_PLANNER.getLongValue2() + (longValue4 + 49L) / 50L;
            this.value2 = 0;
         }
      }
   }

   public boolean isFlag() {
      return this.flag;
   }

   public float getRenderYaw() {
      if (this.flag5) {
         return this.floatValue5;
      } else {
         return this.flag4 ? this.floatValue : this.floatValue5;
      }
   }

   public float getRenderPitch() {
      if (this.flag5) {
         return this.floatValue6;
      } else {
         return this.flag4 ? this.floatValue2 : this.floatValue6;
      }
   }

   public float getYawPartial(float floatValue11) {
      if (this.flag5) {
         return this.floatValue5;
      } else if (!this.flag4) {
         return this.floatValue5;
      } else {
         return this.m_4_boolean() ? MinecraftAccess.MINECRAFT.player.getYRot() : Mth.rotLerp(floatValue11, this.floatValue3, this.floatValue);
      }
   }

   public float getPitchPartial(float floatValue11) {
      if (this.flag5) {
         return this.floatValue6;
      } else if (!this.flag4) {
         return this.floatValue6;
      } else {
         return this.m_4_boolean() ? MinecraftAccess.MINECRAFT.player.getXRot() : Mth.lerp(floatValue11, this.floatValue4, this.floatValue2);
      }
   }

   public float getLerpedPitch(float floatValue11) {
      return this.flag4 ? this.floatValue2 : Mth.lerp(floatValue11, this.floatValue4, this.floatValue2);
   }

   public void applyMouseDelta(double doubleValue, double doubleValue2) {
      this.floatValue5 += (float)doubleValue * 0.15F;
      this.floatValue6 = Mth.clamp(this.floatValue6 + (float)doubleValue2 * 0.15F, -90.0F, 90.0F);
   }

   public boolean isFlag4() {
      return this.flag4;
   }

   public boolean m_4_boolean() {
      return this.flag && this.rotationMode != null && this.rotationMode.m_2_boolean();
   }

   public boolean m_2_boolean(String text3) {
      if (this.flag2 && this.flag7 && STRING_SET2.contains(text3) && CombatPlanner.COMBAT_PLANNER.isOnline()) {
         this.flag8 = true;
         return true;
      } else {
         return false;
      }
   }

   public long getLongValue2() {
      return this.longValue2;
   }

   private void resetModes() {
      this.rotationModeList.forEach(RotationMode::m_1_void);
   }

   private void m_3_void() {
      if (this.flag4) {
         this.applyAndFinish();
      } else {
         boolean flag10 = this.flag7 && this.flag8;
         this.flag8 = false;
         if (this.longValue > 0L && System.currentTimeMillis() < this.longValue) {
            MinecraftAccess.MINECRAFT.player.setYRot(this.floatValue);
            MinecraftAccess.MINECRAFT.player.setXRot(this.floatValue2);
         } else {
            float floatValue11 = Mth.wrapDegrees(this.floatValue5 - this.floatValue);
            float floatValue12 = this.floatValue6 - this.floatValue2;
            if (flag10) {
               if (Math.hypot(floatValue11, floatValue12) < 3.0) {
                  this.flag2 = false;
                  this.flag = false;
                  this.longValue = 0L;
                  this.returnToCamera();
                  return;
               }

               Rotations rotations = CombatPlanner.COMBAT_PLANNER.getPathStep().m_0_C3b();
               if (rotations != null) {
                  this.value2 = 0;
                  this.applyServerRotation(this.applyMouseSensitivity(rotations));
                  return;
               }

               if (++this.value2 <= 10) {
                  MinecraftAccess.MINECRAFT.player.setYRot(this.floatValue);
                  MinecraftAccess.MINECRAFT.player.setXRot(this.floatValue2);
                  return;
               }
            }

            this.flag7 = false;
            Rotations rotations2 = null;
            if (this.rotationMode != null) {
               long longValue4 = System.nanoTime();
               rotations2 = this.rotationMode.m_8(this.floatValue, this.floatValue2, new Rotations(this.floatValue5, this.floatValue6), 0.05F);
               this.rotationModeMap2.get(this.rotationMode).recordSince(longValue4);
            }

            if (rotations2 != null) {
               if (this.rotationMode.isCloseTo(this.floatValue, this.floatValue2, new Rotations(this.floatValue5, this.floatValue6))) {
                  this.flag2 = false;
                  this.flag = false;
                  this.longValue = 0L;
                  this.returnToCamera();
               } else {
                  this.applyServerRotation(this.rotationMode.m_6_boolean() ? rotations2 : this.applyMouseSensitivity(rotations2));
               }
            } else if (Math.abs(floatValue11) <= 1.0F && Math.abs(floatValue12) <= 1.0F) {
               this.flag2 = false;
               this.flag = false;
               this.longValue = 0L;
               this.floatValue7 = 0.0F;
               this.returnToCamera();
            } else {
               this.floatValue7 = Math.min(this.floatValue7 + 12.0F, 9999.0F);
               float floatValue13 = Mth.clamp(floatValue11, -this.floatValue7, this.floatValue7);
               float floatValue14 = Mth.clamp(floatValue12, -this.floatValue7, this.floatValue7);
               this.applyServerRotation(this.applyMouseSensitivity(new Rotations(this.floatValue + floatValue13, this.floatValue2 + floatValue14)));
            }
         }
      }
   }

   private void applyServerRotation(Rotations rotations) {
      this.floatValue = rotations.getFloatValue();
      this.floatValue2 = Mth.clamp(rotations.getFloatValue2(), -90.0F, 90.0F);
      MinecraftAccess.MINECRAFT.player.setYRot(this.floatValue);
      MinecraftAccess.MINECRAFT.player.setXRot(this.floatValue2);
   }

   private void returnToCamera() {
      this.resetModes();
      this.rotationMode = null;
      Rotations rotations = this.applyMouseSensitivity(new Rotations(this.floatValue5, this.floatValue6));
      float floatValue11 = rotations.getFloatValue() - this.floatValue5;
      if (floatValue11 != 0.0F) {
         this.floatValue5 = rotations.getFloatValue();
         MinecraftAccess.MINECRAFT.player.yBob += floatValue11;
         MinecraftAccess.MINECRAFT.player.yBobO += floatValue11;
      }

      this.floatValue6 = rotations.getFloatValue2();
      MinecraftAccess.MINECRAFT.player.setYRot(this.floatValue5);
      MinecraftAccess.MINECRAFT.player.yRotO = this.floatValue5;
      MinecraftAccess.MINECRAFT.player.setXRot(this.floatValue6);
      MinecraftAccess.MINECRAFT.player.xRotO = this.floatValue6;
      this.floatValue = this.floatValue5;
      this.floatValue2 = this.floatValue6;
      this.floatValue3 = this.floatValue5;
      this.floatValue4 = this.floatValue6;
   }

   static {
      m_2_void(37);
   }

   public static void m_2_void(int value4) {
      value3 = value4;
   }

   public static int m_9_int() {
      return value3;
   }

   public static int m_7_int() {
      return 0;
   }
}
