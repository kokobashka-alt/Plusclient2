package wtf.onetap.mixin.render;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.NameProtect;

@Mixin(PlayerTeam.class)
public class PlayerTeamMixin {
   @Inject(method = "formatNameForTeam", at = @At("RETURN"), cancellable = true)
   private static void onetap$protectName(@Nullable Team var0, Component var1, CallbackInfoReturnable<MutableComponent> var2) {
      NameProtect var3 = Onetap.MODULE_MANAGER.get(NameProtect.class);
      if (var3 != null && var3.isEnabled()) {
         var2.setReturnValue((MutableComponent)var3.replaceInComponent((Component)var2.getReturnValue()));
      }
   }
}
