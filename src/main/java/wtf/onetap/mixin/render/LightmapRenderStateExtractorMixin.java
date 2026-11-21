package wtf.onetap.mixin.render;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.NoRender;

@Mixin(LightmapRenderStateExtractor.class)
public class LightmapRenderStateExtractorMixin {
   @Redirect(
      method = "extract",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getEffectBlendFactor(Lnet/minecraft/core/Holder;F)F")
   )
   private float onetap$noRenderDarkness(LocalPlayer var1, Holder<MobEffect> var2, float var3) {
      NoRender var4 = Onetap.MODULE_MANAGER.get(NoRender.class);
      return var4 != null && var4.isEnabled() && var4.modes.isSelected("Тьма вардена") ? 0.0F : var1.getEffectBlendFactor(var2, var3);
   }
}
