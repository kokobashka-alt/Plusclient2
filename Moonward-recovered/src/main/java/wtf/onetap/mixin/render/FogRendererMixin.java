package wtf.onetap.mixin.render;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.environment.DarknessFogEnvironment;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.NoRender;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
   @Inject(method = "setupFog", at = @At("RETURN"), cancellable = true)
   private void onetap$noRenderBadEffects(Camera var1, int var2, DeltaTracker var3, float var4, ClientLevel var5, CallbackInfoReturnable<FogData> var6) {
      NoRender var7 = Onetap.MODULE_MANAGER.get(NoRender.class);
      if (var7 != null && var7.isEnabled() && var7.modes.isSelected("Плохие эффекты")) {
         FogData var8 = (FogData)var6.getReturnValue();
         if (var8 != null) {
            var8.environmentalStart = Float.MAX_VALUE;
            var8.environmentalEnd = Float.MAX_VALUE;
            var8.renderDistanceStart = Float.MAX_VALUE;
            var8.renderDistanceEnd = Float.MAX_VALUE;
            var8.skyEnd = Float.MAX_VALUE;
            var8.cloudEnd = Float.MAX_VALUE;
            var8.color.w = 0.0F;
            var6.setReturnValue(var8);
         }
      }
   }

   @Redirect(
      method = {"setupFog", "computeFogColor"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/fog/environment/FogEnvironment;isApplicable(Lnet/minecraft/world/level/material/FogType;Lnet/minecraft/world/entity/Entity;)Z"
      )
   )
   private boolean onetap$noRenderDarknessFog(FogEnvironment var1, FogType var2, Entity var3) {
      if (var1 instanceof DarknessFogEnvironment) {
         NoRender var4 = Onetap.MODULE_MANAGER.get(NoRender.class);
         if (var4 != null && var4.isEnabled() && var4.modes.isSelected("Тьма вардена")) {
            return false;
         }
      }

      return var1.isApplicable(var2, var3);
   }
}
