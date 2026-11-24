package wtf.onetap.combat.rotation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.RotationInfo;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

/**
 * KillAura rotation "Wander": humanized point-based aim.
 * Every hold cycle it draws, without repeats within a lap, from a fixed set of 100 points spread over
 * the target's hitbox (relative to the target's body yaw), skipping any point that is out of reach or
 * has no line of sight (a clip-context raycast, like AimUtil uses for the same check), predicts the
 * target's next position from its last tick of movement, and turns toward the chosen point with an
 * eased step, small jitter, an occasional overshoot-and-correct, and a pitch stabilizer (deadzone,
 * blend, per-tick speed cap), then snaps the result to the mouse-sensitivity grid, the same formula
 * HolyWorldRotation and ShakalRotation use in this project. The point table and every constant below
 * are carried over unchanged from the source module.
 */
@RotationInfo(name = "Wander", fps = 240)
public class WanderRotation implements MinecraftAccess, RotationMode {
   // {yFrac, right, forward} offsets inside the target's hitbox, relative to its body yaw.
   private static final double[][] POINTS = {
      {0.983, -0.647, 0.758}, {0.552, -0.112, 0.379}, {0.548, 0.203, 0.379}, {0.541, 0.029, 0.379}, {0.582, 0.004, 0.379},
      {0.604, 0.105, 0.379}, {0.679, -0.357, 0.379}, {0.699, 0.624, 0.379}, {0.469, -0.282, 0.379}, {0.44, 0.835, 0.379},
      {0.916, 0.239, 0.758}, {0.862, -0.243, 0.758}, {0.833, -0.561, 0.758}, {0.787, -0.431, 0.758}, {0.826, 0.182, 0.758},
      {0.889, -0.09, 0.758}, {0.613, 0.578, 0.379}, {0.527, 0.696, 0.379}, {0.517, 0.46, 0.379}, {0.548, -0.322, 0.379},
      {0.559, -0.804, 0.379}, {0.633, -1.0, 0.379}, {0.66, -0.871, 0.379}, {0.708, -0.394, 0.379}, {0.707, 0.151, 0.379},
      {0.733, 0.777, 0.379}, {0.95, -0.42, 0.758}, {0.931, 0.444, 0.758}, {0.927, 0.639, 0.758}, {0.75, 0.462, 0.578},
      {0.748, 0.64, 0.379}, {0.823, 0.422, 0.758}, {0.796, 0.538, 0.758}, {0.361, 0.071, 0.379}, {0.219, 0.069, 0.379},
      {0.159, 0.093, 0.379}, {0.196, 0.046, 0.379}, {0.276, -0.147, 0.379}, {0.289, -0.312, 0.379}, {0.271, -0.261, 0.379},
      {0.278, 0.409, 0.379}, {0.295, 0.281, 0.379}, {0.368, -0.079, 0.379}, {0.428, -0.455, 0.379}, {0.433, -0.18, 0.379},
      {0.469, 0.192, 0.379}, {0.486, 0.539, 0.379}, {0.452, 0.738, 0.379}, {0.433, -0.755, 0.379}, {0.398, -1.0, 0.379},
      {0.492, -0.646, 0.379}, {0.895, -0.758, -0.115}, {0.873, -0.758, 0.163}, {0.863, -0.758, 0.437}, {0.902, -0.758, -0.252},
      {0.835, -0.758, -0.404}, {0.773, -0.758, 0.62}, {0.964, -0.758, 0.054}, {0.949, -0.758, 0.188}, {0.962, -0.758, 0.466},
      {0.945, -0.758, 0.594}, {0.89, -0.066, -0.758}, {0.823, -0.149, -0.758}, {0.769, -0.27, -0.758}, {0.904, -0.27, -0.758},
      {0.812, -0.366, -0.758}, {0.852, 0.477, -0.758}, {0.967, -0.112, -0.758}, {0.978, -0.415, -0.758}, {0.886, 0.758, -0.207},
      {0.968, 0.758, 0.023}, {0.814, 0.649, -0.758}, {0.817, 0.758, 0.234}, {0.823, 0.758, 0.063}, {0.593, 0.349, -0.379},
      {0.457, -0.592, -0.379}, {0.508, 0.361, -0.379}, {0.576, 1.0, -0.236}, {0.696, 0.806, -0.379}, {0.59, -0.684, -0.379},
      {0.461, 0.386, -0.379}, {0.447, 1.0, 0.015}, {0.6, 0.817, -0.379}, {0.422, 0.701, -0.379}, {0.201, 0.297, -0.379},
      {0.144, 0.105, -0.379}, {0.303, -0.559, -0.379}, {0.297, 0.568, -0.365}, {0.176, 0.568, -0.249}, {0.411, 0.893, -0.379},
      {0.651, 1.0, 0.056}, {0.603, 1.0, 0.047}, {0.734, 1.0, -0.027}, {0.303, 0.568, 0.202}, {0.386, 1.0, 0.025},
      {0.698, 1.0, -0.169}, {0.519, 1.0, -0.01}, {0.492, 1.0, -0.114}, {0.12, 0.568, 0.207}, {0.091, 0.568, 0.238}
   };

   private static final double EDGE = 0.88;
   private static final double PREDICT = 0.6;

   private static final float MAX_YAW = 14.0F;
   private static final float MAX_PITCH = 3.0F;
   private static final float MIN_STEP = 0.35F;
   private static final double F_MIN = 0.95;
   private static final double F_MAX = 0.75;
   private static final float CAP_RAND = 0.08F;

   private static final float JIT_YAW = 0.12F;
   private static final float JIT_PITCH = 0.08F;
   private static final float OFF_YAW = -0.1F;
   private static final float OFF_PITCH = -0.5F;

   private static final int HOLD_MIN = 1;
   private static final int HOLD_MAX = 4;
   private static final double OV_CHANCE = 0.1;
   private static final float OV_DEG = 1.5F;

   private LivingEntity last;
   private int pointIndex = -1;
   private int hold;
   private final ArrayDeque<Integer> bag = new ArrayDeque<>();
   private float prevPitch;
   private float overshootYaw;
   private float overshootPitch;

   @Override
   public Rotations m_6(
      float lastYaw, float lastPitch, Rotations rotations, AABB aABB, boolean flag, float attackReadiness, float dtSeconds, LivingEntity livingEntity
   ) {
      KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.level == null || killAura == null || livingEntity == null || dtSeconds <= 0.0F) {
         this.reset();
         return new Rotations(lastYaw, lastPitch);
      }

      if (livingEntity != this.last) {
         this.reset();
         this.last = livingEntity;
      }

      ThreadLocalRandom random = ThreadLocalRandom.current();
      Vec3 eye = MinecraftAccess.MINECRAFT.player.getEyePosition();
      double range = killAura.getHitDistance() + killAura.getRotationDistance();
      Vec3 velocity = new Vec3(livingEntity.getX() - livingEntity.xo, 0.0, livingEntity.getZ() - livingEntity.zo).scale(PREDICT);

      if (this.pointIndex >= 0 && !this.visible(eye, this.pointAt(livingEntity, this.pointIndex, velocity), range)) {
         this.pointIndex = -1;
      }

      boolean switched = this.pointIndex < 0 || this.hold <= 0;
      if (switched) {
         this.pointIndex = this.nextPoint(livingEntity, eye, velocity, range);
         this.hold = random.nextInt(HOLD_MIN, HOLD_MAX + 1);
      }

      if (this.pointIndex < 0) {
         return new Rotations(lastYaw, lastPitch);
      }

      this.hold--;
      Rotations aim = new Rotations(eye, this.pointAt(livingEntity, this.pointIndex, velocity));
      float dy = Mth.wrapDegrees(aim.getFloatValue() + OFF_YAW - lastYaw);
      float dp = aim.getFloatValue2() + OFF_PITCH - lastPitch;

      if (switched && random.nextDouble() < OV_CHANCE) {
         this.overshootYaw = Math.signum(dy) * OV_DEG * (float) random.nextDouble(0.5, 1.0);
         this.overshootPitch = Math.signum(dp) * OV_DEG * (float) random.nextDouble(0.25, 0.5);
      }

      dy += this.overshootYaw;
      dp += this.overshootPitch;
      this.overshootYaw *= 0.5F;
      this.overshootPitch *= 0.5F;

      double distance = Math.hypot(dy, dp);
      double factor = F_MIN + (F_MAX - F_MIN) * Math.min(distance / 90.0, 1.0);

      float stepYaw = this.step(dy, factor, MAX_YAW, random);
      float stepPitch = this.step(dp, factor, MAX_PITCH, random);
      stepYaw += jitter(random, JIT_YAW);
      stepPitch += jitter(random, JIT_PITCH);

      // Pitch stabilizer: deadzone, blend toward the wanted value, and a per-call speed cap.
      float pitchDeadzone = 0.85F;
      float pitchBlend = 0.3F;
      float pitchLimit = 4.0F;
      float wantedPitch = Math.abs(dp) <= pitchDeadzone ? 0.0F : stepPitch;
      stepPitch = Mth.clamp(this.prevPitch + (wantedPitch - this.prevPitch) * pitchBlend, -pitchLimit, pitchLimit);
      if (Math.abs(dp) <= pitchDeadzone && Math.abs(stepPitch) < pitchDeadzone) {
         stepPitch = 0.0F;
      }

      double sensitivity = MinecraftAccess.MINECRAFT.options.sensitivity().get() * 0.6 + 0.2;
      double grid = sensitivity * sensitivity * sensitivity * 1.2;
      stepYaw = (float) (Math.round(stepYaw / grid) * grid);
      stepPitch = (float) (Math.round(stepPitch / grid) * grid);
      this.prevPitch = stepPitch;

      return new Rotations(lastYaw + stepYaw, Mth.clamp(lastPitch + stepPitch, -90.0F, 90.0F));
   }

   @Override
   public Rotations m_5(float lastYaw, float lastPitch, Rotations rotations, AABB aABB, boolean flag, float attackReadiness, float dtSeconds) {
      return new Rotations(lastYaw, lastPitch);
   }

   @Override
   public boolean m_2_boolean() {
      return false;
   }

   /** The step above already snaps to the mouse-sensitivity grid, so the engine must not redo it. */
   @Override
   public boolean m_6_boolean() {
      return true;
   }

   @Override
   public void m_1_void() {
      this.reset();
   }

   private void reset() {
      this.last = null;
      this.pointIndex = -1;
      this.hold = 0;
      this.bag.clear();
      this.prevPitch = 0.0F;
      this.overshootYaw = 0.0F;
      this.overshootPitch = 0.0F;
   }

   // Next point: random order within a lap, each point used once per lap, skipping unreachable ones.
   private int nextPoint(LivingEntity target, Vec3 eye, Vec3 velocity, double range) {
      for (int pass = 0; pass < 2; pass++) {
         while (!this.bag.isEmpty()) {
            int i = this.bag.poll();
            if (i != this.pointIndex && this.visible(eye, this.pointAt(target, i, velocity), range)) {
               return i;
            }
         }

         List<Integer> all = new ArrayList<>();
         for (int i = 0; i < POINTS.length; i++) {
            if (this.visible(eye, this.pointAt(target, i, velocity), range)) {
               all.add(i);
            }
         }

         Collections.shuffle(all, ThreadLocalRandom.current());
         this.bag.addAll(all);
      }

      return this.pointIndex >= 0 && this.visible(eye, this.pointAt(target, this.pointIndex, velocity), range) ? this.pointIndex : -1;
   }

   private Vec3 pointAt(LivingEntity target, int index, Vec3 velocity) {
      AABB box = target.getBoundingBox();
      double half = box.getXsize() * 0.5 * EDGE;
      double yaw = Math.toRadians(target.yBodyRot);
      double x = box.getCenter().x + (-Math.cos(yaw) * POINTS[index][1] - Math.sin(yaw) * POINTS[index][2]) * half;
      double z = box.getCenter().z + (-Math.sin(yaw) * POINTS[index][1] + Math.cos(yaw) * POINTS[index][2]) * half;
      return new Vec3(x, box.minY + box.getYsize() * POINTS[index][0], z).add(velocity);
   }

   private boolean visible(Vec3 from, Vec3 to, double range) {
      if (from.distanceTo(to) > range) {
         return false;
      }

      ClipContext context = new ClipContext(from, to, Block.OUTLINE, Fluid.NONE, MinecraftAccess.MINECRAFT.player);
      return MinecraftAccess.MINECRAFT.level.clip(context).getType() == Type.MISS;
   }

   private static float jitter(ThreadLocalRandom random, double range) {
      return (float) ((random.nextDouble() * 2 - 1) * range);
   }

   private float step(float delta, double factor, float cap, ThreadLocalRandom random) {
      float limit = cap * (1.0F + CAP_RAND * jitter(random, 1.0));
      float s = Mth.clamp((float) (delta * factor), -limit, limit);
      if (Math.abs(delta) <= MIN_STEP) {
         return delta;
      }

      return Math.abs(s) < MIN_STEP ? Math.copySign(MIN_STEP, delta) : s;
   }
}
