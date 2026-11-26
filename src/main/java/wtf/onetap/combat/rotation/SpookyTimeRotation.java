package wtf.onetap.combat.rotation;

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
 * KillAura rotation "SpookyTime": point-based aim with five profiles, 1 aggressive to 5 calm, chosen in the
 * KillAura settings (see SpookyTimeProfile for the numbers). A set of points over the hitbox is drawn anew every
 * few ticks, the profile decides how many and how often. The point to aim at is picked by a softmax over scores
 * that depend on three things of the enemy: how closely its camera mirrors ours, the arithmetic mean of its last
 * yaw deltas, and its approximate pitch error. Speeds are scaled by a warped copy of the offset to the point
 * (SpookyTimeMath.warp); the aim direction itself always points at the real point. The final angles are snapped
 * to the mouse-sensitivity grid with the same formula the other rotations of this project use.
 */
@RotationInfo(name = "SpookyTime", fps = 60)
public class SpookyTimeRotation implements MinecraftAccess, RotationMode {
   private static final double EDGE = 0.88;
   private static final int HISTORY = 8;
   private static final float MIN_STEP = 0.12F;
   private static final float OFF_YAW = -0.1F;
   private static final float OFF_PITCH = -0.5F;

   private final double[] warpPhase = new double[3];
   private final double[] yawHistory = new double[HISTORY];
   private int historyCount;
   private int historyHead;
   private int lastTick = Integer.MIN_VALUE;

   private LivingEntity last;
   private double[][] points = new double[0][];
   private int pointIndex = -1;
   private double holdLeft;
   private double regenLeft;
   private float prevPitch;
   private float overshootYaw;
   private float overshootPitch;
   private double attention;
   private double skew;
   private double drift;
   private double tempo;
   private boolean metricsReady;

   public SpookyTimeRotation() {
      ThreadLocalRandom random = ThreadLocalRandom.current();
      for (int i = 0; i < this.warpPhase.length; i++) {
         this.warpPhase[i] = random.nextDouble(0.0, Math.PI * 2.0);
      }
   }

   @Override
   public Rotations m_6(
      float lastYaw, float lastPitch, Rotations rotations, AABB aABB, boolean flag, float attackReadiness, float dtSeconds, LivingEntity livingEntity
   ) {
      KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.level == null || killAura == null || livingEntity == null || dtSeconds <= 0.0F) {
         this.reset();
         return new Rotations(lastYaw, lastPitch);
      }

      SpookyTimeProfile profile = SpookyTimeProfile.of(killAura.getSpookyProfile());
      ThreadLocalRandom random = ThreadLocalRandom.current();
      double dtTicks = Math.min(dtSeconds * 20.0, 3.0);
      Vec3 eye = MinecraftAccess.MINECRAFT.player.getEyePosition();
      double range = killAura.getHitDistance() + killAura.getRotationDistance();

      if (livingEntity != this.last) {
         this.reset();
         this.last = livingEntity;
      }

      this.recordYaw(livingEntity);
      this.updateMetrics(livingEntity, eye, profile, dtTicks);

      Vec3 velocity = new Vec3(livingEntity.getX() - livingEntity.xo, 0.0, livingEntity.getZ() - livingEntity.zo).scale(profile.lead());

      this.regenLeft -= dtTicks;
      if (this.points.length == 0 || this.regenLeft <= 0.0) {
         this.regenerate(profile, random);
      }

      if (this.pointIndex >= 0 && this.pointIndex < this.points.length && !this.visible(eye, this.pointAt(livingEntity, this.points[this.pointIndex], velocity), range)) {
         this.pointIndex = -1;
      }

      this.holdLeft -= dtTicks;
      boolean switched = this.pointIndex < 0 || this.holdLeft <= 0.0;
      if (switched) {
         this.pointIndex = this.choosePoint(livingEntity, eye, velocity, range, profile, random);
         this.holdLeft = profile.holdMin() + random.nextDouble() * (profile.holdMax() - profile.holdMin());
      }

      if (this.pointIndex < 0) {
         return new Rotations(lastYaw, lastPitch);
      }

      Vec3 aimPoint = this.pointAt(livingEntity, this.points[this.pointIndex], velocity);
      Rotations aim = new Rotations(eye, aimPoint);
      float dy = Mth.wrapDegrees(aim.getFloatValue() + OFF_YAW - lastYaw);
      float dp = aim.getFloatValue2() + OFF_PITCH - lastPitch;

      if (switched && profile.overshootChance() > 0.0 && random.nextDouble() < profile.overshootChance()) {
         this.overshootYaw = (float) (Math.signum(dy) * profile.overshootDeg() * random.nextDouble(0.5, 1.0));
         this.overshootPitch = (float) (Math.signum(dp) * profile.overshootDeg() * random.nextDouble(0.25, 0.5));
      }

      dy += this.overshootYaw;
      dp += this.overshootPitch;
      this.overshootYaw *= 0.5F;
      this.overshootPitch *= 0.5F;

      // Speed gains come from the warped offset to the point, not from the real one.
      Vec3 offset = aimPoint.subtract(eye);
      double[] warped = SpookyTimeMath.warp(offset.x, offset.y, offset.z, profile.warp(), this.warpPhase);
      double warpedLength = SpookyTimeMath.length(warped);
      double yawGain = 0.55 + 0.90 * SpookyTimeMath.horizontalShare(warped);
      double pitchGain = 0.60 + 1.40 * SpookyTimeMath.verticalShare(warped);
      double speed = (0.75 + 0.50 * this.tempo) * SpookyTimeMath.distanceGain(warpedLength);
      double follow = SpookyTimeMath.follow(profile.smooth(), dtTicks) * speed;

      float yawLimit = (float) (profile.maxYaw() * yawGain * dtTicks);
      float pitchLimit = (float) (profile.maxPitch() * pitchGain * dtTicks);

      float stepYaw = this.step(dy, follow, yawLimit);
      float wantedPitch = Math.abs(dp) <= profile.pitchDeadzone() ? 0.0F : this.step(dp, follow, pitchLimit);
      float stepPitch = Mth.clamp(this.prevPitch + (wantedPitch - this.prevPitch) * (float) profile.pitchBlend(), -pitchLimit, pitchLimit);
      if (Math.abs(dp) <= profile.pitchDeadzone() && Math.abs(stepPitch) < profile.pitchDeadzone()) {
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
      this.points = new double[0][];
      this.pointIndex = -1;
      this.holdLeft = 0.0;
      this.regenLeft = 0.0;
      this.prevPitch = 0.0F;
      this.overshootYaw = 0.0F;
      this.overshootPitch = 0.0F;
      this.historyCount = 0;
      this.historyHead = 0;
      this.lastTick = Integer.MIN_VALUE;
      this.metricsReady = false;
   }

   /** Keeps the yaw delta of the enemy for each game tick, newest last, at most HISTORY entries. */
   private void recordYaw(LivingEntity target) {
      int tick = MinecraftAccess.MINECRAFT.player.tickCount;
      if (tick == this.lastTick) {
         return;
      }

      this.lastTick = tick;
      this.yawHistory[this.historyHead] = Mth.wrapDegrees(target.getYRot() - target.yRotO);
      this.historyHead = (this.historyHead + 1) % HISTORY;
      this.historyCount = Math.min(this.historyCount + 1, HISTORY);
   }

   private void updateMetrics(LivingEntity target, Vec3 eye, SpookyTimeProfile profile, double dtTicks) {
      double rawAttention = SpookyTimeMath.attention(target.getYRot(), MinecraftAccess.MINECRAFT.player.getYRot());
      float neededPitch = new Rotations(target.getEyePosition(), eye).getFloatValue2();
      double rawSkew = SpookyTimeMath.pitchSkew(target.getXRot(), neededPitch);
      double rawDrift = SpookyTimeMath.yawDrift(this.yawHistory, this.historyCount);

      // A profile with lag reads the enemy slowly: every metric follows its raw value through a low-pass filter.
      double alpha = 1.0 - Math.exp(-dtTicks / (1.0 + profile.lag()));
      if (!this.metricsReady) {
         this.attention = rawAttention;
         this.skew = rawSkew;
         this.drift = rawDrift;
         this.metricsReady = true;
      } else {
         this.attention += (rawAttention - this.attention) * alpha;
         this.skew += (rawSkew - this.skew) * alpha;
         this.drift += (rawDrift - this.drift) * alpha;
      }

      this.tempo = SpookyTimeMath.tempo(this.attention, this.drift, this.skew);
   }

   private void regenerate(SpookyTimeProfile profile, ThreadLocalRandom random) {
      double aggression = profile.aggression();
      double yLow = 0.55 - 0.43 * aggression;
      double yHigh = 0.90 + 0.08 * aggression;
      this.points = SpookyTimeMath.points(profile.points(), yLow, yHigh, random.nextDouble() * 1000.0);
      this.pointIndex = -1;
      this.regenLeft = random.nextInt(profile.regenMin(), profile.regenMax() + 1);
   }

   private int choosePoint(LivingEntity target, Vec3 eye, Vec3 velocity, double range, SpookyTimeProfile profile, ThreadLocalRandom random) {
      double center = SpookyTimeMath.heightCenter(this.skew);
      double[] scores = new double[this.points.length];
      for (int i = 0; i < this.points.length; i++) {
         if (this.visible(eye, this.pointAt(target, this.points[i], velocity), range)) {
            scores[i] = SpookyTimeMath.score(this.points[i], center, profile.sigma(), this.drift, this.attention);
         } else {
            scores[i] = Double.NEGATIVE_INFINITY;
         }
      }

      int index = SpookyTimeMath.pick(scores, profile.temperature(), random.nextDouble());
      return index == this.pointIndex && this.points.length > 1 ? this.pickOther(scores, index, profile, random) : index;
   }

   /** The same point twice in a row is allowed only when no other point is visible. */
   private int pickOther(double[] scores, int same, SpookyTimeProfile profile, ThreadLocalRandom random) {
      double saved = scores[same];
      scores[same] = Double.NEGATIVE_INFINITY;
      int other = SpookyTimeMath.pick(scores, profile.temperature(), random.nextDouble());
      scores[same] = saved;
      return other < 0 ? same : other;
   }

   private Vec3 pointAt(LivingEntity target, double[] point, Vec3 velocity) {
      AABB box = target.getBoundingBox();
      double half = box.getXsize() * 0.5 * EDGE;
      double yaw = Math.toRadians(target.yBodyRot);
      double x = box.getCenter().x + (-Math.cos(yaw) * point[1] - Math.sin(yaw) * point[2]) * half;
      double z = box.getCenter().z + (-Math.sin(yaw) * point[1] + Math.cos(yaw) * point[2]) * half;
      return new Vec3(x, box.minY + box.getYsize() * point[0], z).add(velocity);
   }

   private boolean visible(Vec3 from, Vec3 to, double range) {
      if (from.distanceTo(to) > range) {
         return false;
      }

      ClipContext context = new ClipContext(from, to, Block.OUTLINE, Fluid.NONE, MinecraftAccess.MINECRAFT.player);
      return MinecraftAccess.MINECRAFT.level.clip(context).getType() == Type.MISS;
   }

   /** Moves a share of the remaining angle, capped, and never overshoots by the share itself for tiny angles. */
   private float step(float delta, double follow, float limit) {
      if (Math.abs(delta) <= MIN_STEP) {
         return delta;
      }

      float s = Mth.clamp((float) (delta * follow), -limit, limit);
      return Math.abs(s) < MIN_STEP ? Math.copySign(MIN_STEP, delta) : s;
   }
}
