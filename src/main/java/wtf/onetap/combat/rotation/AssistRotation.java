package wtf.onetap.combat.rotation;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.RotationInfo;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

@RotationInfo(name = "Assist", fps = 240)
public class AssistRotation implements MinecraftAccess, RotationMode {
   private float floatValue;

   @Override
   public Rotations m_5(float floatValue2, float floatValue3, Rotations rotations, AABB aABB, boolean flag, float floatValue4, float floatValue5) {
      KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
      if (MinecraftAccess.MINECRAFT.player != null && killAura != null) {
         float floatValue6 = floatValue5 <= 0.0F ? 0.05F : Math.min(floatValue5, 0.1F);
         float floatValue7 = MinecraftAccess.MINECRAFT.player.getYRot();
         float floatValue8 = MinecraftAccess.MINECRAFT.player.getXRot();
         float floatValue9 = Mth.wrapDegrees(floatValue7 - floatValue2);
         float floatValue10 = floatValue8 - floatValue3;
         float floatValue11 = Mth.wrapDegrees(rotations.getFloatValue() - floatValue7);
         float floatValue12 = rotations.getFloatValue2() - floatValue8;
         float floatValue13 = (float)Math.hypot(floatValue11, floatValue12);
         float floatValue14 = (float)Math.hypot(floatValue9, floatValue10);
         float floatValue15 = killAura.getHitAssist();
         float floatValue16 = Mth.clamp((floatValue4 - 0.6F) / 0.4F, 0.0F, 1.0F);
         floatValue16 = floatValue16 * floatValue16 * (3.0F - 2.0F * floatValue16);
         float floatValue17 = Math.max(0.0F, floatValue13 - m_7_float(aABB) * (1.0F - floatValue15 * floatValue16));
         float floatValue18 = 0.0F;
         if (floatValue14 > 0.0F && floatValue17 > 0.0F) {
            float floatValue19 = 1.0F - (float)Math.exp(-floatValue17 / killAura.getTurnSpeed());
            float floatValue20 = floatValue14 / floatValue6;
            float floatValue21 = killAura.getRotationDuration();
            float floatValue22 = floatValue20 * floatValue20 / (floatValue20 * floatValue20 + floatValue21 * floatValue21);
            float floatValue23 = Mth.clamp(0.5F + 0.5F * (floatValue9 * floatValue11 + floatValue10 * floatValue12) / (floatValue14 * floatValue13), 0.0F, 1.0F);
            floatValue18 = floatValue19 * floatValue22 * floatValue23 * (1.0F - floatValue15 + floatValue15 * floatValue16);
         }

         float floatValue24 = killAura.getResponseSeconds();
         this.floatValue = floatValue24 <= 0.0F ? floatValue18 : this.floatValue + (floatValue18 - this.floatValue) * (1.0F - (float)Math.exp(-floatValue6 / floatValue24));
         float floatValue25 = Math.min(floatValue17, floatValue14 * killAura.getAssistStrength() * this.floatValue);
         return floatValue25 <= 0.0F
            ? new Rotations(floatValue7, floatValue8)
            : new Rotations(floatValue7 + floatValue11 / floatValue13 * floatValue25, Mth.clamp(floatValue8 + floatValue12 / floatValue13 * floatValue25 * killAura.getVerticalAssist(), -90.0F, 90.0F));
      } else {
         return new Rotations(floatValue2, floatValue3);
      }
   }

   @Override
   public boolean m_2_boolean() {
      return Onetap.ROTATION_MODES.isFlag4();
   }

   @Override
   public void m_1_void() {
      this.floatValue = 0.0F;
   }

   private static float m_7_float(AABB aABB) {
      if (aABB == null) {
         return 0.0F;
      }

      double doubleValue = MinecraftAccess.MINECRAFT.player.getEyePosition().distanceTo(aABB.getCenter());
      return (float)Math.toDegrees(Math.atan2(aABB.getXsize() * 0.5, Math.max(doubleValue, 0.1)));
   }
}
