package wtf.onetap.module.movement;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.entity.LivingEntityInvoker;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "WaterSpeed", description = "Ускорение в воде и мини-прыжки с критами", category = Category.MOVEMENT)
public class WaterSpeed extends Module {
   private final BooleanSetting miniJumps = new BooleanSetting("Мини прыжки", true).build();
   private final BooleanSetting acceleration = new BooleanSetting("Ускорение", true).build();
   private double doubleValue;
   private boolean flag;

   public static void m_6(LocalPlayer localPlayer) {
      WaterSpeed waterSpeed = Onetap.MODULE_MANAGER.get(WaterSpeed.class);
      if (waterSpeed != null && waterSpeed.isEnabled() && (Boolean)waterSpeed.acceleration.getValue()) {
         if (localPlayer.isInWater() && !localPlayer.isPassenger() && isMovingForward(localPlayer)) {
            localPlayer.setSprinting(true);
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if ((Boolean)this.acceleration.getValue() && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (MinecraftAccess.MINECRAFT.player.isInWater() && !MinecraftAccess.MINECRAFT.player.isPassenger() && isMovingForward(MinecraftAccess.MINECRAFT.player)) {
            Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getDeltaMovement();
            if (!(vec3.x * vec3.x + vec3.z * vec3.z <= 1.0E-7)) {
               MinecraftAccess.MINECRAFT.player.setDeltaMovement(vec3.x * 1.00057, vec3.y, vec3.z * 1.00057);
            }
         }
      }
   }

   private static boolean isMovingForward(LocalPlayer localPlayer) {
      return localPlayer.input.keyPresses.forward() && localPlayer.input.getMoveVector().y > 0.001F;
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if ((Boolean)this.miniJumps.getValue() && event.isJumping() && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.isInWater() && !MinecraftAccess.MINECRAFT.player.onGround()) {
         if (!MinecraftAccess.MINECRAFT.player.isSwimming() && !MinecraftAccess.MINECRAFT.player.isPassenger() && !MinecraftAccess.MINECRAFT.player.getAbilities().flying) {
            double doubleValue2 = MinecraftAccess.MINECRAFT.player.getFluidHeight(FluidTags.WATER);
            if (!(doubleValue2 <= 0.0)) {
               this.doubleValue = ((LivingEntityInvoker)MinecraftAccess.MINECRAFT.player).onetap$getEffectiveGravity();
               this.flag = MinecraftAccess.MINECRAFT.player.isSprinting();
               double doubleValue3 = -doubleValue2 - 0.001;
               double doubleValue4 = MinecraftAccess.MINECRAFT.player.getDeltaMovement().y;
               if (Math.abs(doubleValue4) < 0.003) {
                  doubleValue4 = 0.0;
               }

               if (this.isKillAuraTargeting()) {
                  float floatValue = MinecraftAccess.MINECRAFT.player.getCurrentItemAttackStrengthDelay();
                  int value = Math.max(0, (int)Math.ceil((0.92F - MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(0.5F)) * floatValue));
                  if (value <= 10) {
                     WaterSpeed.StateHolder c8 = new WaterSpeed.StateHolder(20000);
                     this.m_8(c8, doubleValue3, doubleValue4, 0, true, value, false, doubleValue3, (int)Math.ceil(0.92F * floatValue));
                     if (c8.value < 24) {
                        event.setJumping(c8.flag);
                        return;
                     }
                  }
               }

               event.setJumping(this.m_0(doubleValue3, doubleValue4));
            }
         }
      }
   }

   private boolean isKillAuraTargeting() {
      KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
      return killAura != null && killAura.isEnabled() && KillAura.livingEntity != null;
   }

   private void m_8(WaterSpeed.StateHolder c8, double doubleValue2, double doubleValue3, int value, boolean flag2, int value2, boolean flag3, double doubleValue4, int value3) {
      if (value < c8.value && c8.value3-- > 0) {
         if (Math.abs(doubleValue3) < 0.003) {
            doubleValue3 = 0.0;
         }

         boolean flag4 = doubleValue2 < -0.001;

         for (int i = flag4 ? 1 : 0; i >= 0; i--) {
            double doubleValue5 = i == 1 ? doubleValue3 + 0.04F : doubleValue3;
            double doubleValue6 = doubleValue2 + doubleValue5;
            double doubleValue7 = this.m_7(doubleValue5, flag4);
            boolean flag5 = value == 0 ? i == 1 : flag3;
            double doubleValue8 = Math.min(doubleValue4, doubleValue6);
            if (flag2 && doubleValue6 < doubleValue2 && doubleValue6 >= -0.001 && value + 1 >= value2) {
               int value4 = 0;
               if (value3 > 1) {
                  WaterSpeed.StateHolder c82 = new WaterSpeed.StateHolder(1500);
                  this.m_8(c82, doubleValue6, doubleValue7, 0, false, value3, false, doubleValue6, 0);
                  value4 = c82.value;
               }

               if (value + 1 < c8.value || value4 < c8.value2 || value4 == c8.value2 && doubleValue8 > c8.doubleValue) {
                  c8.value = value + 1;
                  c8.value2 = value4;
                  c8.flag = flag5;
                  c8.doubleValue = doubleValue8;
               }
            } else {
               this.m_8(c8, doubleValue6, doubleValue7, value + 1, flag2 || flag4, value2, flag5, doubleValue8, value3);
            }
         }
      }
   }

   private boolean m_0(double doubleValue2, double doubleValue3) {
      if (doubleValue3 < 0.0) {
         return true;
      }

      int value = -1;
      double doubleValue4 = Double.NEGATIVE_INFINITY;

      for (int i = 0; i <= 20; i++) {
         double doubleValue5 = this.m_8(doubleValue2, doubleValue3, i);
         if (doubleValue5 > doubleValue4 + 1.0E-9) {
            doubleValue4 = doubleValue5;
            value = i;
         }
      }

      return value != 0;
   }

   private double m_8(double doubleValue2, double doubleValue3, int value) {
      boolean flag2 = false;

      for (int i = 0; i < 60; i++) {
         if (Math.abs(doubleValue3) < 0.003) {
            doubleValue3 = 0.0;
         }

         boolean flag3 = doubleValue2 < -0.001;
         if (flag3 && flag2) {
            return doubleValue3;
         }

         if (flag3 && (i < value || doubleValue3 < 0.0)) {
            doubleValue3 += 0.04F;
         }

         doubleValue2 += doubleValue3;
         doubleValue3 = this.m_7(doubleValue3, flag3);
         if (!flag3) {
            flag2 = true;
         }
      }

      return Double.NaN;
   }

   private double m_7(double doubleValue2, boolean flag2) {
      if (!flag2) {
         return (doubleValue2 - this.doubleValue) * 0.98F;
      } else {
         double doubleValue3 = doubleValue2 * 0.8;
         if (this.flag) {
            return doubleValue3;
         } else {
            return doubleValue2 <= 0.0 && Math.abs(doubleValue3 - 0.005) >= 0.003 && Math.abs(doubleValue3 - this.doubleValue / 16.0) < 0.003 ? -0.003 : doubleValue3 - this.doubleValue / 16.0;
         }
      }
   }

   private static final class StateHolder {
      public int value;
      public int value2;
      public int value3;
      public boolean flag;
      public double doubleValue;

      public StateHolder(int value4) {
         this.value = 24;
         this.value2 = Integer.MAX_VALUE;
         this.doubleValue = Double.NEGATIVE_INFINITY;
         this.value3 = value4;
      }
   }
}
