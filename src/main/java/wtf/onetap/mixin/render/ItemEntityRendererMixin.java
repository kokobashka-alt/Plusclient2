package wtf.onetap.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.ItemPhysics;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {
   @Inject(
      method = "extractRenderState(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;F)V",
      at = @At("TAIL")
   )
   private void onetap$capturePhysicsState(ItemEntity var1, ItemEntityRenderState var2, float var3, CallbackInfo var4) {
      ItemPhysics.putState(var2, var1.onGround());
   }

   @Inject(
      method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void onetap$renderWithPhysics(ItemEntityRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4, CallbackInfo var5) {
      ItemPhysics var6 = Onetap.MODULE_MANAGER.get(ItemPhysics.class);
      if (var6 != null && var6.isEnabled() && !var1.item.isEmpty()) {
         var5.cancel();
         var2.pushPose();
         ItemPhysics.DataRecord var7 = ItemPhysics.getState(var1);
         boolean var8 = var7 != null && var7.m_5_k();
         float var9 = 1.0F;
         if (var8) {
            var2.translate(0.0F, 0.02F + 0.0625F * var9, 0.0F);
            var2.mulPose(Axis.XP.rotationDegrees(90.0F));
            var2.mulPose(Axis.ZP.rotationDegrees(var1.seed * 180.0F));
         } else {
            float var10 = (var1.ageInTicks + var1.seed) * 2.0F;
            float var11 = Mth.sin(var1.ageInTicks / 10.0F + var1.seed) * 0.06F + 0.1F;
            var2.translate(0.0F, var11 + 0.25F * var9, 0.0F);
            var2.mulPose(Axis.YP.rotationDegrees(var10 * 1.5F));
            var2.mulPose(Axis.XP.rotationDegrees(35.0F + var10));
            var2.mulPose(Axis.ZP.rotationDegrees(10.0F + var10 * 0.5F));
         }

         var1.item.submit(var2, var3, var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.outlineColor);
         var2.popPose();
      }
   }
}
