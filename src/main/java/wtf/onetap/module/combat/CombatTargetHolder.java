package wtf.onetap.combat;

import net.minecraft.world.entity.LivingEntity;

/**
 * Shared target for render modules such as TargetESP.
 * KillAura keeps its own static target. AimAssist and TriggerBot each publish their target here,
 * so the highlight works when only one of them is active. Each owner writes only its own slot.
 */
public final class CombatTargetHolder {
   private static volatile LivingEntity aimAssistTarget;
   private static volatile LivingEntity triggerBotTarget;

   private CombatTargetHolder() {
   }

   public static void setAimAssistTarget(LivingEntity target) {
      aimAssistTarget = target;
   }

   public static void setTriggerBotTarget(LivingEntity target) {
      triggerBotTarget = target;
   }

   /** Returns the AimAssist target if set, otherwise the TriggerBot target, otherwise null. */
   public static LivingEntity resolve() {
      LivingEntity aim = aimAssistTarget;
      return aim != null ? aim : triggerBotTarget;
   }
}
