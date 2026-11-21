package wtf.onetap.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.HandGlow;
import wtf.onetap.module.render.NoRender;
import wtf.onetap.module.render.Zoom;
import wtf.onetap.obf.C5_d;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
   @Inject(method = "extract", at = @At("HEAD"))
   private void onetap$beginVanillaRenderCaches(DeltaTracker var1, boolean var2, CallbackInfo var3) {
      C5_d.m_8_void();
   }

   @Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
   private void onetap$noRenderShake(CameraRenderState var1, PoseStack var2, CallbackInfo var3) {
      NoRender var4 = Onetap.MODULE_MANAGER.get(NoRender.class);
      if (var4 != null && var4.isEnabled() && var4.modes.isSelected("Тряска")) {
         var3.cancel();
      }
   }

   @Redirect(
      method = "bobView",
      at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/level/CameraEntityRenderState;backwardsInterpolatedWalkDistance:F")
   )
   private float onetap$zoomBob(CameraEntityRenderState var1) {
      Zoom var2 = Onetap.MODULE_MANAGER.get(Zoom.class);
      float var3 = var1.backwardsInterpolatedWalkDistance;
      return var2 != null && var2.isEnabled() ? (float)(var3 / Mth.lerp(0.2, 1.0, var2.getDoubleValue())) : var3;
   }

   @Inject(method = "displayItemActivation", at = @At("HEAD"), cancellable = true)
   private void onetap$noRenderTotem(ItemStack var1, CallbackInfo var2) {
      if (var1 != null && var1.is(Items.TOTEM_OF_UNDYING)) {
         NoRender var3 = Onetap.MODULE_MANAGER.get(NoRender.class);
         if (var3 != null && var3.isEnabled() && var3.modes.isSelected("Тотем-анимация")) {
            var2.cancel();
         }
      }
   }

   @Inject(method = "renderItemInHand", at = @At("RETURN"))
   private void onetap$handGlow(CameraRenderState var1, float var2, Matrix4fc var3, CallbackInfo var4) {
      HandGlow var5 = Onetap.MODULE_MANAGER.get(HandGlow.class);
      if (var5 != null && var5.isEnabled()) {
         var5.m_3_void();
      }
   }
}
