package wtf.onetap.combat.rotation;

import net.minecraft.world.phys.AABB;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.RotationInfo;
import wtf.onetap.module.combat.AimAssistMath;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

/**
 * KillAura rotation "Legit": the same aiming as the AimAssist module.
 * Per frame it measures the angle from the current view to the target, skips the frame when the
 * target is outside the FOV cone, otherwise moves the view by AimAssistMath.smoothStep with the
 * four AimAssist settings (FOV, strength, speed, vertical), which KillAura exposes under the same names.
 * Silent: only the server-side rotation is changed, the camera stays under the player's control (no client look).
 * KillAura attacks in this mode only once the current rotation actually hits the target's hitbox.
 */
@RotationInfo(name = "Legit", fps = 240)
public class LegitRotation implements MinecraftAccess, RotationMode {
   /** The target leaves the aim cone only when it is this much wider than the cone that engaged it (no edge flicker). */
   private static final float RELEASE_FACTOR = 1.2F;
   /** Minimum speed (deg/s) used to drift the silent rotation back to the camera when nothing is aimed at. */
   private static final float RETURN_SPEED = 180.0F;

   private boolean engaged;

   @Override
   public Rotations m_5(float lastYaw, float lastPitch, Rotations target, AABB box, boolean flag, float attackReadiness, float dtSeconds) {
      KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
      if (MinecraftAccess.MINECRAFT.player == null || killAura == null || target == null) {
         return new Rotations(lastYaw, lastPitch);
      }

      if (dtSeconds <= 0.0F) {
         return new Rotations(lastYaw, lastPitch);
      }

      // The real camera (the player's own look). In silent mode it is NOT the rotation sent to the server.
      float camYaw = Onetap.ROTATION_MODES.getRenderYaw();
      float camPitch = Onetap.ROTATION_MODES.getRenderPitch();

      float targetYaw = target.getFloatValue();
      float targetPitch = target.getFloatValue2();

      // The FOV gate is measured from the camera, with hysteresis.
      float half = killAura.getLegitFov() / 2.0F;
      float distance = AimAssistMath.fovDistance(camYaw, camPitch, targetYaw, targetPitch);
      this.engaged = distance <= (this.engaged ? half * RELEASE_FACTOR : half);

      if (!this.engaged) {
         // Not aiming: the silent rotation drifts back to the camera instead of freezing where it was.
         float[] back = AimAssistMath.smoothStep(
            lastYaw, lastPitch, camYaw, camPitch,
            1.0F, Math.max(killAura.getLegitSpeed(), RETURN_SPEED), 1.0F, dtSeconds
         );
         return new Rotations(lastYaw + back[0], AimAssistMath.clampPitch(lastPitch + back[1]));
      }

      float[] step = AimAssistMath.smoothStep(
         lastYaw, lastPitch, targetYaw, targetPitch,
         killAura.getLegitStrength(), killAura.getLegitSpeed(), killAura.getLegitVertical(), dtSeconds
      );
      return new Rotations(lastYaw + step[0], AimAssistMath.clampPitch(lastPitch + step[1]));
   }

   @Override
   public void m_1_void() {
      this.engaged = false;
   }

   @Override
   public boolean m_2_boolean() {
      return false;
   }

   /** The result is used as is, without snapping to the mouse-sensitivity grid. */
   @Override
   public boolean m_6_boolean() {
      return true;
   }
}
