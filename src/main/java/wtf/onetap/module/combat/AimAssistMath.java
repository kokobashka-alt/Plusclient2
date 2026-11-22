package wtf.onetap.module.combat;

/**
 * Pure math for AimAssist: angle wrapping, field-of-view measure and the smoothing step.
 * No Minecraft types, so it can be checked outside the game.
 *
 * Units: all angles are degrees. Yaw wraps in [-180, 180), pitch is clamped to [-90, 90].
 * Speed is in degrees per second and is uncalibrated; the default in AimAssist is a starting value only.
 */
public final class AimAssistMath {
   private AimAssistMath() {
   }

   public static float wrapDegrees(float degrees) {
      float wrapped = (degrees + 180.0F) % 360.0F;
      if (wrapped < 0.0F) {
         wrapped += 360.0F;
      }
      return wrapped - 180.0F;
   }

   /** Angular distance between the current view and a target direction, in degrees. */
   public static float fovDistance(float yaw, float pitch, float targetYaw, float targetPitch) {
      float dYaw = wrapDegrees(targetYaw - yaw);
      float dPitch = targetPitch - pitch;
      return (float) Math.hypot(dYaw, dPitch);
   }

   /**
    * One smoothing step toward the target.
    * The remaining error is closed by a fraction derived from strength (0..1) and speed (deg/s),
    * and the step never exceeds speed * dt in one update.
    *
    * @return {yawStep, pitchStep} to add to the current view
    */
   public static float[] smoothStep(float yaw, float pitch, float targetYaw, float targetPitch,
                                    float strength, float speedDegPerSec, float verticalScale, float dtSeconds) {
      float dYaw = wrapDegrees(targetYaw - yaw);
      float dPitch = targetPitch - pitch;
      float clampedStrength = Math.max(0.0F, Math.min(1.0F, strength));
      float clampedDt = Math.max(0.0F, Math.min(0.1F, dtSeconds));
      float maxStep = Math.max(0.0F, speedDegPerSec) * clampedDt;
      float fraction = clampedStrength * (1.0F - (float) Math.exp(-clampedDt * 10.0F));
      float yawStep = clamp(dYaw * fraction, maxStep);
      float pitchStep = clamp(dPitch * fraction * Math.max(0.0F, Math.min(1.0F, verticalScale)), maxStep);
      return new float[]{yawStep, pitchStep};
   }

   /** Applies the step to a pitch value and clamps the result to the legal range. */
   public static float clampPitch(float pitch) {
      return Math.max(-90.0F, Math.min(90.0F, pitch));
   }

   /** Random attack delay between min and max milliseconds, for the triggerbot. */
   public static long humanDelayMs(double unit, long minMs, long maxMs) {
      long low = Math.max(0L, Math.min(minMs, maxMs));
      long high = Math.max(0L, Math.max(minMs, maxMs));
      double clampedUnit = Math.max(0.0, Math.min(1.0, unit));
      return low + Math.round(clampedUnit * (high - low));
   }

   private static float clamp(float value, float limit) {
      return Math.max(-limit, Math.min(limit, value));
   }
}
