package wtf.onetap.combat.rotation;

import net.minecraft.world.phys.AABB;
import wtf.onetap.annotation.RotationInfo;
import wtf.onetap.util.Rotations;

@RotationInfo(name = "Snap")
public class SnapRotation implements RotationMode {
   @Override
   public Rotations m_5(float floatValue, float floatValue2, Rotations rotations, AABB aABB, boolean flag, float floatValue3, float floatValue4) {
      return rotations;
   }

   @Override
   public Rotations m_8(float floatValue, float floatValue2, Rotations rotations, float floatValue3) {
      return rotations;
   }
}
