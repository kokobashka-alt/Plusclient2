package wtf.onetap.combat.rotation.lopata;

/**
 * Feature layout shared by the recorder, the trainer (tools/lopata/train_lopata.py) and the runtime rotation.
 * Changing the order or the count invalidates every recorded file and every trained model.
 */
public final class LopataFeatures {
   public static final int COUNT = 13;
   public static final String[] NAMES = {
      "dist", "dyaw", "dpitch", "tSpeed", "tAccel", "tVy", "cooldown", "pGround", "pVy", "pSpeed", "tHurt", "prevDYaw", "prevDPitch"
   };
   /** Height of the aim point inside the target hitbox (0 = feet, 1 = top). A design choice, not a measured value. */
   public static final double AIM_HEIGHT = 0.8;
   /** Output labels per tick: degrees of yaw and pitch change. */
   public static final int OUTPUTS = 2;

   private LopataFeatures() {
   }

   public static float wrapDegrees(float degrees) {
      float wrapped = degrees % 360.0F;
      if (wrapped >= 180.0F) {
         wrapped -= 360.0F;
      }

      if (wrapped < -180.0F) {
         wrapped += 360.0F;
      }

      return wrapped;
   }

   /** Yaw towards a delta vector, in degrees, Minecraft convention (0 = +Z, positive towards -X). */
   public static float yawTo(double dx, double dz) {
      return wrapDegrees((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
   }

   /** Pitch towards a delta vector, in degrees, positive looks down. */
   public static float pitchTo(double dx, double dy, double dz) {
      return (float) Math.max(-90.0, Math.min(90.0, -Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz)))));
   }
}
