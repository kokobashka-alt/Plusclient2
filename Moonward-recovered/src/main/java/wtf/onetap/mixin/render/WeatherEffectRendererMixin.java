package wtf.onetap.mixin.render;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.Weather;

@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRendererMixin {
   @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
   private void onetap$skipExtract(ClientLevel var1, float var2, Vec3 var3, WeatherRenderState var4, CallbackInfo var5) {
      Weather var6 = Onetap.MODULE_MANAGER.get(Weather.class);
      if (var6 != null && var6.m_3_boolean()) {
         var5.cancel();
      }
   }

   @Inject(method = "render", at = @At("HEAD"), cancellable = true)
   private void onetap$customWeather(Vec3 var1, WeatherRenderState var2, CallbackInfo var3) {
      Weather var4 = Onetap.MODULE_MANAGER.get(Weather.class);
      if (var4 != null && var4.m_3_boolean()) {
         var3.cancel();
      }
   }
}
