package wtf.onetap.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import wtf.onetap.render.CrownModel;

public class RotationManager {
   public static final RotationManager ROTATION_MANAGER = new RotationManager();
   private final Map<Integer, RotationManager.StateHolder> integerMap;
   private int value;

   private RotationManager() {
      this.integerMap = new HashMap<>();
   }

   public void tick() {
      this.value++;
   }

   public void m_4(Player player, float floatValue) {
      RotationManager.StateHolder c7 = this.integerMap.computeIfAbsent(player.getId(), integer -> new RotationManager.StateHolder(player));
      CrownModel.m_2_String();
      c7.value = this.value;
      c7.m_2(player, floatValue);
   }

   public void purge() {
      this.integerMap.values().removeIf(c7 -> this.value - c7.value > 40);
   }

   public RotationManager.StateHolder m_4_C7(int value2) {
      return this.integerMap.get(value2);
   }

   public void m_9_void() {
      this.integerMap.clear();
   }

   public static final class StateHolder {
      private int value;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private float floatValue7;
      private float floatValue8;
      private float floatValue9;
      private float floatValue10;
      private float floatValue11;
      private float floatValue12;
      private float floatValue13;
      private float floatValue14;

      private StateHolder(Player player) {
         this.floatValue11 = player.yHeadRot;
         this.floatValue = this.floatValue2 = (float)(Math.random() * (float) (Math.PI * 2));
      }

      public float m_9(float floatValue15) {
         return this.floatValue2 + (this.floatValue - this.floatValue2) * floatValue15;
      }

      public float m_7_float(float floatValue15) {
         return this.floatValue6 + (this.floatValue5 - this.floatValue6) * floatValue15;
      }

      public float m_1_float(float floatValue15) {
         return this.floatValue9 + (this.floatValue8 - this.floatValue9) * floatValue15;
      }

      public float m_5_float() {
         return this.floatValue3 - this.floatValue4;
      }

      private void m_2(Player player, float floatValue15) {
         this.floatValue2 = this.floatValue;
         CrownModel.m_2_String();
         this.floatValue6 = this.floatValue5;
         this.floatValue9 = this.floatValue8;
         float floatValue16 = player.yHeadRot;
         float floatValue17 = (float)Math.toRadians(floatValue16);
         this.floatValue4 = (float)Math.toRadians(Mth.wrapDegrees(floatValue16 - this.floatValue11)) / 0.05F;
         this.floatValue11 = floatValue16;
         float floatValue18 = (float)(player.getX() - player.xo) / 0.05F;
         float floatValue19 = (float)(player.getY() - player.yo) / 0.05F;
         float floatValue20 = (float)(player.getZ() - player.zo) / 0.05F;
         if (floatValue18 * floatValue18 + floatValue19 * floatValue19 + floatValue20 * floatValue20 > 6400.0F) {
            floatValue18 = this.floatValue12;
            floatValue19 = this.floatValue13;
            floatValue20 = this.floatValue14;
         }

         float floatValue21 = Mth.clamp((floatValue18 - this.floatValue12) / 0.05F, -60.0F, 60.0F);
         float floatValue22 = Mth.clamp((floatValue20 - this.floatValue14) / 0.05F, -60.0F, 60.0F);
         this.floatValue12 = floatValue18;
         this.floatValue13 = floatValue19;
         this.floatValue14 = floatValue20;
         float floatValue23 = (float)Math.sqrt(this.floatValue12 * this.floatValue12 + this.floatValue14 * this.floatValue14);
         float floatValue24 = this.floatValue13 < 0.0F ? -this.floatValue13 : -0.25F * this.floatValue13;
         float floatValue25 = (floatValue24 + 0.35F * floatValue23) * floatValue15;
         float floatValue26 = Mth.clamp(floatValue25 * 0.75F / 0.49804688F, -25.0F, 25.0F);
         float floatValue27 = (float)Math.sin(floatValue17);
         float floatValue28 = (float)Math.cos(floatValue17);
         float floatValue29 = -floatValue21 * floatValue27 + floatValue22 * floatValue28;
         float floatValue30 = -floatValue21 * floatValue28 - floatValue22 * floatValue27;
         float floatValue31 = -this.floatValue12 * floatValue27 + this.floatValue14 * floatValue28;
         float floatValue32 = -this.floatValue12 * floatValue28 - this.floatValue14 * floatValue27;
         float floatValue33 = (float)Math.sqrt(floatValue31 * floatValue31 + floatValue32 * floatValue32);
         float floatValue34 = 1.0F - (float)Math.exp(-1.1F * Math.abs(floatValue25) * 0.005F);

         for (int i = 0; i < 10; i++) {
            this.floatValue3 = this.floatValue3 + (floatValue26 - this.floatValue3) * floatValue34;
            float floatValue35 = this.floatValue3 - this.floatValue4;
            float floatValue36 = -0.008F * this.floatValue3 * Math.abs(this.floatValue3) - 0.35F * floatValue35;
            if (Math.abs(floatValue35) <= 0.0069999998F && Math.abs(floatValue36) < 1.4F) {
               this.floatValue3 = this.floatValue4;
            } else {
               this.floatValue3 = this.floatValue3 + (floatValue36 - Math.signum(floatValue35) * 1.4F) * 0.005F;
            }

            this.floatValue3 = Mth.clamp(this.floatValue3, -25.0F, 25.0F);
            this.floatValue = this.floatValue + (this.floatValue3 - this.floatValue4) * 0.005F;
            float floatValue37 = 1.0F / (1.0F + Math.abs(this.floatValue3) / 25.0F);
            float floatValue38 = (floatValue29 * 0.045F + floatValue31 * floatValue33 * 0.02F) * floatValue37 * 15.0F * 15.0F / 9.8F;
            float floatValue39 = (floatValue30 * 0.045F + floatValue32 * floatValue33 * 0.02F) * floatValue37 * 15.0F * 15.0F / 9.8F;
            this.floatValue7 = this.floatValue7 + (floatValue38 - 225.0F * this.floatValue5 - 6.0F * this.floatValue7) * 0.005F;
            this.floatValue10 = this.floatValue10 + (floatValue39 - 225.0F * this.floatValue8 - 6.0F * this.floatValue10) * 0.005F;
            this.floatValue5 = Mth.clamp(this.floatValue5 + this.floatValue7 * 0.005F, -0.4F, 0.4F);
            this.floatValue8 = Mth.clamp(this.floatValue8 + this.floatValue10 * 0.005F, -0.4F, 0.4F);
         }

         if (Math.abs(this.floatValue) > 1608.4955F) {
            float floatValue40 = (float)(Math.floor(this.floatValue / (float) (Math.PI * 2)) * (float) (Math.PI * 2));
            this.floatValue -= floatValue40;
            this.floatValue2 -= floatValue40;
         }
      }
   }
}
