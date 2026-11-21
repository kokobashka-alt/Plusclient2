package wtf.onetap.mixin.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain.TargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher.PreparedFrame;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.level.dimension.DimensionType.Skybox;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.module.combat.ElytraTarget;
import wtf.onetap.module.render.Atmosphere;
import wtf.onetap.module.render.BlockOutline;
import wtf.onetap.module.render.GlowESP;
import wtf.onetap.module.render.NoRender;
import wtf.onetap.module.render.Saturation;
import wtf.onetap.module.render.ShaderSky;
import wtf.onetap.module.render.Weather;
import wtf.onetap.render.GlowEspRenderer;
import wtf.onetap.util.WorldToScreen;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
   @Shadow
   @Final
   private RenderTarget entityOutlineTarget;
   @Shadow
   @Final
   private LevelTargetBundle targets;
   @Shadow
   @Final
   private LevelRenderState levelRenderState;
   @Unique
   private boolean onetap$outline;
   @Unique
   private boolean onetap$skippedOutlinePost;

   @Inject(method = "render", at = @At("HEAD"))
   private void onetap$begin(CallbackInfo var1) {
      this.onetap$outline = false;
      this.onetap$skippedOutlinePost = false;
   }

   @Redirect(
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;hasAnyOutline()Z")
   )
   private boolean onetap$hasOutline(PreparedFrame var1) {
      boolean var2 = var1.hasAnyOutline();
      this.onetap$outline |= var2;
      return var2;
   }

   @Redirect(
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/PostChain;addToFrame(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;IILnet/minecraft/client/renderer/PostChain$TargetBundle;)V"
      )
   )
   private void onetap$post(PostChain var1, FrameGraphBuilder var2, int var3, int var4, TargetBundle var5) {
      GlowESP var6 = Onetap.MODULE_MANAGER.get(GlowESP.class);
      if (var6 != null && var6.isEnabled() && this.onetap$outline && !this.onetap$skippedOutlinePost) {
         this.onetap$skippedOutlinePost = true;
      } else {
         var1.addToFrame(var2, var3, var4, var5);
      }
   }

   @Inject(method = "doEntityOutline", at = @At("HEAD"), cancellable = true)
   private void onetap$glow(CallbackInfo var1) {
      GlowESP var2 = Onetap.MODULE_MANAGER.get(GlowESP.class);
      if (var2 != null && var2.isEnabled()) {
         var1.cancel();
         if (this.onetap$outline) {
            GlowEspRenderer.m_5(this.entityOutlineTarget, true, var2.glow.getFloat(), var2.strength.getFloat(), var2.m_7_int(), var2.getTime());
         }
      }
   }

   @Inject(method = "submitBlockOutline", at = @At("HEAD"), cancellable = true)
   private void onetap$blockOutline(PoseStack var1, SubmitNodeCollector var2, LevelRenderState var3, CallbackInfo var4) {
      BlockOutline var5 = Onetap.MODULE_MANAGER.get(BlockOutline.class);
      if (var5 != null && var5.isEnabled()) {
         var4.cancel();
      }
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void onetap$render3D(
      GraphicsResourceAllocator var1,
      DeltaTracker var2,
      boolean var3,
      CameraRenderState var4,
      Matrix4fc var5,
      GpuBufferSlice var6,
      Vector4f var7,
      boolean var8,
      CallbackInfo var9
   ) {
      ElytraTarget var10 = Onetap.MODULE_MANAGER.get(ElytraTarget.class);
      if (var10 != null && var10.isEnabled()) {
         var10.m_0_void(var2.getGameTimeDeltaPartialTick(true));
      }

      Onetap.ROTATION_MODES.updateModeRotation();
      Atmosphere var11 = Onetap.MODULE_MANAGER.get(Atmosphere.class);
      if (var11 != null && var11.isEnabled()) {
         var11.m_1_void();
      }

      Weather var12 = Onetap.MODULE_MANAGER.get(Weather.class);
      if (var12 != null && var12.isEnabled()) {
         var12.m_2(var4.projectionMatrix, var5, var4.pos, var2.getGameTimeDeltaPartialTick(true), var7);
      }

      Saturation var13 = Onetap.MODULE_MANAGER.get(Saturation.class);
      if (var13 != null && var13.isEnabled()) {
         var13.m_4_void();
      }

      Matrix4f var14 = new Matrix4f(var4.projectionMatrix);
      Matrix4f var15 = new Matrix4f(var5);
      WorldToScreen.setMatrices(var14, var15);
      Onetap.EVENT_BUS.unregister(new Render3DEvent(var2.getGameTimeDeltaPartialTick(true), var14, var15, var4.pos, new Quaternionf(var4.orientation), var4));
   }

   @Inject(method = "addCloudsPass", at = @At("HEAD"), cancellable = true)
   private void onetap$noRenderClouds(
      FrameGraphBuilder var1, CloudStatus var2, Vec3 var3, long var4, float var6, int var7, float var8, int var9, CallbackInfo var10
   ) {
      if (isNoRenderEnabled("Облака")) {
         var10.cancel();
      }
   }

   @Inject(method = "addWeatherPass", at = @At("HEAD"), cancellable = true)
   private void onetap$noRenderWeather(FrameGraphBuilder var1, GpuBufferSlice var2, CallbackInfo var3) {
      if (isNoRenderEnabled("Погода")) {
         var3.cancel();
      }
   }

   @Inject(method = "addSkyPass", at = @At("HEAD"), cancellable = true)
   private void onetap$shaderSky(FrameGraphBuilder var1, CameraRenderState var2, GpuBufferSlice var3, CallbackInfo var4) {
      ShaderSky var5 = Onetap.MODULE_MANAGER.get(ShaderSky.class);
      if (var5 != null && var5.isEnabled()) {
         FogType var6 = var2.fogType;
         if (var6 != FogType.POWDER_SNOW && var6 != FogType.LAVA) {
            if (!var2.entityRenderState.doesMobEffectBlockSky) {
               if (this.levelRenderState.skyRenderState.skybox != Skybox.NONE) {
                  var4.cancel();
                  FramePass var7 = var1.addPass("shadersky");
                  this.targets.main = var7.readsAndWrites(this.targets.main);
                  var7.executes(() -> var5.m_3(var2));
               }
            }
         }
      }
   }

   private static boolean isNoRenderEnabled(String var0) {
      NoRender var1 = Onetap.MODULE_MANAGER.get(NoRender.class);
      return var1 != null && var1.isEnabled() && var1.modes.isSelected(var0);
   }
}
