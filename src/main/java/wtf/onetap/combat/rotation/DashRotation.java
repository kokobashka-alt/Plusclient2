package wtf.onetap.combat.rotation;

import net.minecraft.world.phys.AABB;
import wtf.onetap.annotation.RotationInfo;
import wtf.onetap.combat.AimUtil;
import wtf.onetap.util.C9_q;
import wtf.onetap.util.Rotations;

@RotationInfo(name = "Рывки", fps = 240)
public class DashRotation implements RotationMode {
   @Override
   public Rotations m_5(float floatValue, float floatValue2, Rotations rotations, AABB aABB, boolean flag, float floatValue3, float floatValue4) {
      boolean flag2 = AimUtil.isLookingAt(new Rotations(floatValue, floatValue2), 64.0, aABB.inflate(-0.15F, -0.25, -0.1F));
      double[] doubleArray = new double[]{floatValue, floatValue2, rotations.getFloatValue(), rotations.getFloatValue2(), flag2 ? 1.0 : 0.0};
      return !C9_q.m_7(0, doubleArray) ? new Rotations(floatValue, floatValue2) : new Rotations((float)doubleArray[0], (float)doubleArray[1]);
   }

   @Override
   public void m_1_void() {
      C9_q.m_3_boolean(0);
   }
}
