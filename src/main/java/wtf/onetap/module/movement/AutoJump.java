package wtf.onetap.module.movement;

import net.minecraft.world.effect.MobEffects;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Auto Jump", description = "Автоматически прыгает при выбранных условиях", category = Category.MOVEMENT)
public class AutoJump extends Module {
   private final MultiModeSetting jumpIf = new MultiModeSetting("Прыгать если", "Активно зелье замедления", "Активна KillAura", "Активно зелье замедления").build();

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         boolean flag = false;
         if (this.jumpIf.isSelected("Активна KillAura")) {
            KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
            if (killAura != null && killAura.isEnabled() && killAura.findTargetInRange() != null) {
               flag = true;
            }
         }

         if (this.jumpIf.isSelected("Активно зелье замедления") && MinecraftAccess.MINECRAFT.player.hasEffect(MobEffects.SLOWNESS)) {
            flag = true;
         }

         if (flag) {
            event.setJumping(true);
         }
      }
   }
}
