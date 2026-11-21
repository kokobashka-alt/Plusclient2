package wtf.onetap.mixin.render;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.combat.rotation.RotationModes;
import wtf.onetap.module.misc.FreeCam;
import wtf.onetap.module.movement.AntiLevitation;
import wtf.onetap.module.render.AspectRatio;
import wtf.onetap.module.render.NoRender;
import wtf.onetap.module.render.SmoothCamera;
import wtf.onetap.module.render.Zoom;

@Mixin(Camera.class)
public abstract class CameraMixin {
   @Shadow
   private float eyeHeight;
   @Shadow
   private float eyeHeightOld;
   @Shadow
   private Entity entity;

   @Shadow
   protected abstract void setPosition(Vec3 var1);

   @Shadow
   protected abstract void setRotation(float var1, float var2);

   @Inject(method = "tick", at = @At("TAIL"))
   private void onetap$antiLevitationEye(CallbackInfo var1) {
      if (this.entity == Minecraft.getInstance().player) {
         AntiLevitation var2 = Onetap.MODULE_MANAGER.get(AntiLevitation.class);
         if (var2 != null && var2.m_0_boolean()) {
            float var3 = var2.m_4_float();
            this.eyeHeight = var3;
            this.eyeHeightOld = var3;
         }
      }
   }

   @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;calculateFov(F)F"))
   private void onetap$freeCamPosition(DeltaTracker var1, CallbackInfo var2) {
      FreeCam var3 = Onetap.MODULE_MANAGER.get(FreeCam.class);
      if (var3 != null && var3.isEnabled()) {
         this.setPosition(var3.getInterpolatedPos(((Camera)(Object)this).getCameraEntityPartialTicks(var1)));
      }
   }

   @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;calculateFov(F)F"))
   private void onetap$smoothCamera(DeltaTracker var1, CallbackInfo var2) {
      SmoothCamera var3 = Onetap.MODULE_MANAGER.get(SmoothCamera.class);
      if (var3 != null && var3.isEnabled()) {
         Camera var4 = (Camera)(Object)this;
         var3.m_5(var4.yRot(), var4.xRot(), var4.position());
         this.setRotation(var3.getFloatValue(), var3.getFloatValue2());
         this.setPosition(var3.getVec3());
      }
   }

   @Inject(method = "extractRenderState", at = @At("TAIL"))
   private void onetap$freeCamRenderState(CameraRenderState var1, float var2, CallbackInfo var3) {
      FreeCam var4 = Onetap.MODULE_MANAGER.get(FreeCam.class);
      if (var4 != null && var4.isEnabled()) {
         var1.pos = var4.getInterpolatedPos(var2);
         var1.blockPos = BlockPos.containing(var1.pos);
      }
   }

   @Redirect(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewYRot(F)F"))
   private float onetap$cameraYaw(Entity var1, float var2) {
      if (var1 != Minecraft.getInstance().player) {
         return var1.getViewYRot(var2);
      }

      RotationModes var3 = Onetap.ROTATION_MODES;
      return !var3.isFlag5() && !var3.isFlag() ? var1.getViewYRot(var2) : var3.getYawPartial(var2);
   }

   @Redirect(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewXRot(F)F"))
   private float onetap$cameraPitch(Entity var1, float var2) {
      if (var1 != Minecraft.getInstance().player) {
         return var1.getViewXRot(var2);
      }

      RotationModes var3 = Onetap.ROTATION_MODES;
      return !var3.isFlag5() && !var3.isFlag() ? var1.getViewXRot(var2) : var3.getPitchPartial(var2);
   }

   @Inject(method = "getMaxZoom", at = @At("HEAD"), cancellable = true)
   private void onetap$noRenderCameraClip(float var1, CallbackInfoReturnable<Float> var2) {
      NoRender var3 = Onetap.MODULE_MANAGER.get(NoRender.class);
      if (var3 != null && var3.isEnabled() && var3.modes.isSelected("Камераклип")) {
         var2.setReturnValue(var1);
      }
   }

   @Redirect(
      method = "extractRenderState",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 1)
   )
   private boolean onetap$noRenderDarknessSky(LivingEntity var1, Holder<MobEffect> var2) {
      NoRender var3 = Onetap.MODULE_MANAGER.get(NoRender.class);
      return var3 != null && var3.isEnabled() && var3.modes.isSelected("Тьма вардена") ? false : var1.hasEffect(var2);
   }

   @Inject(method = {"calculateFov", "calculateHudFov"}, at = @At("RETURN"), cancellable = true)
   private void onetap$zoomFov(float var1, CallbackInfoReturnable<Float> var2) {
      Zoom var3 = Onetap.MODULE_MANAGER.get(Zoom.class);
      if (var3 != null && var3.isEnabled()) {
         var2.setReturnValue(var2.getReturnValueF() / var3.m_1_float(var1));
      }
   }

   @Redirect(method = "setupPerspective", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Projection;setupPerspective(FFFFF)V"))
   private void onetap$setupPerspective(Projection var1, float var2, float var3, float var4, float var5, float var6) {
      AspectRatio var7 = Onetap.MODULE_MANAGER.get(AspectRatio.class);
      if (var7 != null && var7.isEnabled()) {
         float var8 = var7.m_5_float();
         if (var8 > 0.0F) {
            var5 = var6 * var8;
         }
      }

      var1.setupPerspective(var2, var3, var4, var5, var6);
   }
}
