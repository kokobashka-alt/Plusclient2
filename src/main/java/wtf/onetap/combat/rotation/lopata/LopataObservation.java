package wtf.onetap.combat.rotation.lopata;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Builds the feature vector of {@link LopataFeatures} once per client tick. The recorder and the runtime rotation use
 * this same class, so the model sees at runtime exactly what it was trained on.
 *
 * The vector for tick t describes the state before the rotation step of tick t is applied: angles are measured from the
 * rotation passed in, and prevDYaw / prevDPitch are the change between the previous call and this one.
 */
public final class LopataObservation {
   public final float[] features = new float[LopataFeatures.COUNT];

   private int lastTick = Integer.MIN_VALUE;
   private LivingEntity lastTarget;
   private float lastYaw;
   private float lastPitch;
   private double lastTargetSpeed;
   private boolean contiguous;
   private boolean repeated;

   /** True if the previous call was the immediately preceding tick against the same target. */
   public boolean continuesSequence() {
      return this.contiguous;
   }

   /** True if this call happened in the same tick as the previous one; features were left untouched. */
   public boolean repeatedTick() {
      return this.repeated;
   }

   public void reset() {
      this.lastTick = Integer.MIN_VALUE;
      this.lastTarget = null;
      this.contiguous = false;
      this.repeated = false;
   }

   /**
    * @return false if the state is unusable (non-finite values); features are then undefined.
    */
   public boolean observe(Player player, LivingEntity target, float yaw, float pitch) {
      int tick = player.tickCount;
      if (target == this.lastTarget && tick == this.lastTick) {
         this.repeated = true;
         return true;
      }

      this.repeated = false;
      this.contiguous = target == this.lastTarget && tick == this.lastTick + 1;

      Vec3 eye = player.getEyePosition();
      AABB box = target.getBoundingBox();
      double ax = (box.minX + box.maxX) * 0.5;
      double az = (box.minZ + box.maxZ) * 0.5;
      double ay = box.minY + box.getYsize() * LopataFeatures.AIM_HEIGHT;
      double dx = ax - eye.x;
      double dy = ay - eye.y;
      double dz = az - eye.z;

      double vx = target.getX() - target.xo;
      double vy = target.getY() - target.yo;
      double vz = target.getZ() - target.zo;
      double speed = Math.hypot(vx, vz);
      double accel = this.contiguous ? speed - this.lastTargetSpeed : 0.0;

      float prevDYaw = 0.0F;
      float prevDPitch = 0.0F;
      if (this.contiguous) {
         prevDYaw = LopataFeatures.wrapDegrees(yaw - this.lastYaw);
         prevDPitch = pitch - this.lastPitch;
      }

      Vec3 motion = player.getDeltaMovement();
      float[] f = this.features;
      f[0] = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
      f[1] = LopataFeatures.wrapDegrees(LopataFeatures.yawTo(dx, dz) - yaw);
      f[2] = LopataFeatures.pitchTo(dx, dy, dz) - pitch;
      f[3] = (float) speed;
      f[4] = (float) accel;
      f[5] = (float) vy;
      f[6] = player.getAttackStrengthScale(0.0F);
      f[7] = player.onGround() ? 1.0F : 0.0F;
      f[8] = (float) motion.y;
      f[9] = (float) motion.horizontalDistance();
      f[10] = target.hurtTime;
      f[11] = prevDYaw;
      f[12] = prevDPitch;

      this.lastTick = tick;
      this.lastTarget = target;
      this.lastYaw = yaw;
      this.lastPitch = pitch;
      this.lastTargetSpeed = speed;

      for (float v : f) {
         if (!Float.isFinite(v)) {
            this.reset();
            return false;
         }
      }

      return true;
   }
}
