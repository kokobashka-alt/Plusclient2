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
 * Works on the camera (client look), like AimAssist.
 */
@RotationInfo(name = "Legit", fps = 240)
public class LegitRotation implements MinecraftAccess, RotationMode {
   @Override
   public Rotations m_5(float lastYaw, float lastPitch, Rotations target, AABB box, boolean flag, float attackReadiness, float dtSeconds) {
      KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
      if (MinecraftAccess.MINECRAFT.player == null || killAura == null || target == null) {
         return new Rotations(lastYaw, lastPitch);
      }

      float yaw = MinecraftAccess.MINECRAFT.player.getYRot();
      float pitch = MinecraftAccess.MINECRAFT.player.getXRot();
      if (dtSeconds <= 0.0F) {
         return new Rotations(yaw, pitch);
      }

      float targetYaw = target.getFloatValue();
      float targetPitch = target.getFloatValue2();
      if (AimAssistMath.fovDistance(yaw, pitch, targetYaw, targetPitch) > killAura.getLegitFov() / 2.0F) {
         return new Rotations(yaw, pitch);
      }

      float[] step = AimAssistMath.smoothStep(
         yaw, pitch, targetYaw, targetPitch,
         killAura.getLegitStrength(), killAura.getLegitSpeed(), killAura.getLegitVertical(), dtSeconds
      );
      return new Rotations(yaw + step[0], AimAssistMath.clampPitch(pitch + step[1]));
   }

   @Override
   public boolean m_2_boolean() {
      return Onetap.ROTATION_MODES.isFlag4();
   }

   /** The result is used as is, without snapping to the mouse-sensitivity grid, exactly like AimAssist. */
   @Override
   public boolean m_6_boolean() {
      return true;
   }
}
