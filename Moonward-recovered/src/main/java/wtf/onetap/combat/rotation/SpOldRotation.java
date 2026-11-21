package wtf.onetap.combat.rotation;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.world.phys.AABB;
import wtf.onetap.annotation.RotationInfo;
import wtf.onetap.combat.AimUtil;
import wtf.onetap.util.C9_q;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

@RotationInfo(name = "SP (old)")
public class SpOldRotation implements MinecraftAccess, RotationMode {
   @Override
   public Rotations m_5(float floatValue, float floatValue2, Rotations rotations, AABB aABB, boolean flag, float floatValue3, float floatValue4) {
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      float floatValue5 = MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(threadLocalRandom.nextFloat() * 0.5F) >= 0.5F ? 0.0F : threadLocalRandom.nextFloat() * 1.5F;
      boolean flag2 = AimUtil.isLookingAt(new Rotations(floatValue, floatValue2), 64.0, aABB.inflate(floatValue5, -0.5F + floatValue5 * 1.5F, floatValue5));
      double[] doubleArray = new double[]{floatValue, floatValue2, rotations.getFloatValue(), rotations.getFloatValue2(), flag2 ? 1.0 : 0.0};
      return !C9_q.m_7(1, doubleArray) ? new Rotations(floatValue, floatValue2) : new Rotations((float)doubleArray[0], (float)doubleArray[1]);
   }

   @Override
   public void m_1_void() {
      C9_q.m_3_boolean(1);
   }
}
