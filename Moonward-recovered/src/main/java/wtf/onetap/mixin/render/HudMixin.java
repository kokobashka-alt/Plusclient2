package wtf.onetap.mixin.render;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.NoRender;

@Mixin(targets = "net.minecraft.client.gui.Hud$HeartType")
public class HudMixin {
   @Redirect(
      method = "forPlayer",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 1)
   )
   private static boolean onetap$noRenderWither(Player var0, Holder<MobEffect> var1) {
      if (var1 == MobEffects.WITHER) {
         NoRender var2 = Onetap.MODULE_MANAGER.get(NoRender.class);
         if (var2 != null && var2.isEnabled() && var2.modes.isSelected("Иссушение")) {
            return false;
         }
      }

      return var0.hasEffect(var1);
   }
}
