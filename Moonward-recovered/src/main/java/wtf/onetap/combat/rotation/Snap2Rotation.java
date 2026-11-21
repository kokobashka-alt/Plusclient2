package wtf.onetap.combat.rotation;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.RotationInfo;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.util.C9_q;
import wtf.onetap.util.Rotations;

@RotationInfo(name = "Snap2", fps = 30)
public class Snap2Rotation implements RotationMode {
   @Override
   public Rotations m_5(float floatValue, float floatValue2, Rotations rotations, AABB aABB, boolean flag, float floatValue3, float floatValue4) {
      KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
      double[] doubleArray = new double[]{
         floatValue,
         floatValue2,
         rotations.getFloatValue(),
         rotations.getFloatValue2(),
         flag ? 1.0 : 0.0,
         floatValue3,
         floatValue4,
         Onetap.ROTATION_MODES.getRenderYaw(),
         Onetap.ROTATION_MODES.getRenderPitch(),
         System.currentTimeMillis(),
         killAura != null ? killAura.getAimBackDelay() : 100.0
      };
      return !C9_q.m_7(3, doubleArray) ? new Rotations(floatValue, floatValue2) : new Rotations((float)doubleArray[0], (float)doubleArray[1]);
   }

   @Override
   public Vec3 m_1_Vec3() {
      double[] doubleArray = C9_q.m_9_DArr(3);
      return doubleArray == null ? Vec3.ZERO : new Vec3(doubleArray[0], doubleArray[1], doubleArray[2]);
   }

   @Override
   public int m_6_int() {
      KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
      return killAura != null && killAura.isHighRefreshRate() ? 60 : this.getInfo().fps();
   }

   @Override
   public void m_8_void() {
      C9_q.m_8_void(3);
   }

   @Override
   public void m_1_void() {
      C9_q.m_3_boolean(3);
   }
}
