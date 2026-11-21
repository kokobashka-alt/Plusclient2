package wtf.onetap.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState.TraverseRange;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.render.BlurRenderer;
import wtf.onetap.render.Render2D;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
   @Shadow
   @Final
   private List<?> draws;

   @Inject(method = "addElementsToMeshes", at = @At("HEAD"))
   private void onetap$afterVanillaBlur(TraverseRange var1, CallbackInfo var2) {
      if (var1 == TraverseRange.AFTER_BLUR) {
         BlurRenderer.m_5_void();
      }
   }

   @Inject(method = "addElementToMesh", at = @At("TAIL"))
   private void onetap$trackBlur(GuiElementRenderState var1, CallbackInfo var2) {
      BlurRenderer.m_7(var1, this.draws.size() - 1);
   }

   @Inject(method = "draw", at = @At("HEAD"))
   private void onetap$backdrop(CallbackInfo var1) {
      BlurRenderer.m_6_void();
   }

   @WrapOperation(
      method = "draw",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/render/GuiRenderer;executeDrawRange(Ljava/util/function/Supplier;Lcom/mojang/blaze3d/pipeline/RenderTarget;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;II)V"
      )
   )
   private void onetap$blur(GuiRenderer var1, Supplier<String> var2, RenderTarget var3, GpuBufferSlice var4, int var5, int var6, Operation<Void> var7) {
      IntList var8 = BlurRenderer.m_0_IntList();
      int var9 = var5;

      for (int var10 = 0; var10 < var8.size(); var10++) {
         int var11 = var8.getInt(var10);
         if (var11 >= var5 && var11 < var6) {
            if (var11 > var9) {
               var7.call(var1, var2, var3, var4, var9, var11);
            }

            BlurRenderer.m_5_void(var10);
            var9 = var11;
         }
      }

      var7.call(var1, var2, var3, var4, var9, var6);
   }

   @ModifyArg(
      method = "submitBlitFromItemAtlas",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/state/gui/BlitRenderState;<init>(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/gui/render/TextureSetup;Lorg/joml/Matrix3x2fc;IIIIFFFFILnet/minecraft/client/gui/navigation/ScreenRectangle;Lnet/minecraft/client/gui/navigation/ScreenRectangle;)V"
      ),
      index = 11
   )
   private int onetap$itemColor(int var1, @Local(argsOnly = true) GuiItemRenderState var2) {
      return Render2D.getItemStateId(var2);
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void onetap$endFrame(CallbackInfo var1) {
      Render2D.clearItemStates();
      BlurRenderer.m_7_void();
   }
}
