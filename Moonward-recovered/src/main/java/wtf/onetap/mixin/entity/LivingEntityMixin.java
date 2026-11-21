package wtf.onetap.mixin.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.combat.ElytraMode;
import wtf.onetap.event.PostTravelEvent;
import wtf.onetap.mixin.entity.projectile.FireworkRocketEntityAccessor;
import wtf.onetap.module.combat.ElytraTarget;
import wtf.onetap.module.movement.ElytraBooster;
import wtf.onetap.module.movement.ElytraMotion;
import wtf.onetap.module.movement.GrimGlide;
import wtf.onetap.module.movement.NoClip;
import wtf.onetap.module.player.NoDelay;
import wtf.onetap.module.render.SwingAnimation;
import wtf.onetap.util.InventoryUtil;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
   @Shadow
   private int noJumpDelay;

   @Redirect(method = "updateFallFlyingMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getXRot()F"))
   private float onetap$noClipFallFlyingPitch(LivingEntity var1) {
      NoClip var2 = Onetap.MODULE_MANAGER.get(NoClip.class);
      return var1 == Minecraft.getInstance().player && var2 != null && var2.m_6_boolean() ? var2.vector2f.y : var1.getXRot();
   }

   @Redirect(
      method = "updateFallFlyingMovement",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getLookAngle()Lnet/minecraft/world/phys/Vec3;")
   )
   private Vec3 onetap$noClipFallFlyingLook(LivingEntity var1) {
      NoClip var2 = Onetap.MODULE_MANAGER.get(NoClip.class);
      return var1 == Minecraft.getInstance().player && var2 != null && var2.m_6_boolean()
         ? var1.calculateViewVector(var2.vector2f.y, var2.vector2f.x)
         : var1.getLookAngle();
   }

   @Inject(method = "aiStep", at = @At("HEAD"))
   private void onetap$noJumpDelay(CallbackInfo var1) {
      NoDelay var2 = Onetap.MODULE_MANAGER.get(NoDelay.class);
      if (var2 != null && var2.isEnabled() && var2.multiModeSetting.isSelected("Прыжок")) {
         this.noJumpDelay = 0;
      }
   }

   @Inject(method = "travel", at = @At("RETURN"))
   private void onetap$onTravelPost(Vec3 var1, CallbackInfo var2) {
      if ((Object)this == Minecraft.getInstance().player) {
         Onetap.EVENT_BUS.unregister(new PostTravelEvent());
      }
   }

   @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
   private void onetap$setSprinting(boolean var1, CallbackInfo var2) {
      LocalPlayer var3 = Minecraft.getInstance().player;
      if (var3 != null && (Object)this == var3 && var1 && InventoryUtil.m_8_boolean()) {
         var2.cancel();
      }
   }

   @Inject(method = "getCurrentSwingDuration", at = @At("HEAD"), cancellable = true)
   private void onetap$swingAnimationsDuration(CallbackInfoReturnable<Integer> var1) {
      LocalPlayer var2 = Minecraft.getInstance().player;
      if (var2 != null && (Object)this == var2) {
         SwingAnimation var3 = Onetap.MODULE_MANAGER.get(SwingAnimation.class);
         if (var3 != null) {
            if (var3.m_7_boolean()) {
               var1.setReturnValue(var3.m_7(var2));
            } else if (var3.m_1_boolean()) {
               var1.setReturnValue(25 - (int)var3.speed.getFloat() * 2);
            }
         }
      }
   }

   @ModifyConstant(method = "updateFallFlyingMovement", constant = @Constant(doubleValue = 0.1))
   private double onetap$fastElytraTurning(double var1) {
      if ((Object)this != Minecraft.getInstance().player) {
         return var1;
      } else {
         ElytraTarget var3 = ElytraTarget.getActive();
         if (var3 == null) {
            return var1;
         } else {
            return ElytraMode.isFast(var3.m_9_String()) ? 1.0 : var1;
         }
      }
   }

   @ModifyVariable(method = "updateFallFlyingMovement", at = @At("HEAD"), argsOnly = true)
   private Vec3 onetap$grimElytraFirework(Vec3 var1) {
      LocalPlayer var2 = Minecraft.getInstance().player;
      return var2 != null && (Object)this == var2 ? grimFireworkBoost(var2, var1) : var1;
   }

   @Inject(method = "travelInWater", at = @At("HEAD"))
   private void onetap$grimWaterFirework(CallbackInfo var1) {
      LocalPlayer var2 = Minecraft.getInstance().player;
      if (var2 != null && (Object)this == var2) {
         Vec3 var3 = var2.getDeltaMovement();
         Vec3 var4 = grimFireworkBoost(var2, var3);
         if (var4 != var3) {
            var2.setDeltaMovement(var4);
         }
      }
   }

   @Unique
   private static Vec3 grimFireworkBoost(LocalPlayer var0, Vec3 var1) {
      if (ElytraBooster.m_3_C0_l() != null) {
         return var1;
      }

      ElytraTarget var2 = ElytraTarget.getActive();
      if (var2 != null && ElytraMode.isGroundGrim(var2.m_9_String())) {
         Vec3 var3 = var0.calculateViewVector(Onetap.ROTATION_MODES.getFloatValue2(), Onetap.ROTATION_MODES.getFloatValue());
         int var4 = attachedRockets(var0);
         Vec3 var5 = var1;

         for (int var6 = 0; var6 < var4; var6++) {
            var5 = ElytraMode.m_3(var5, var3, ElytraBooster.m_4(var0, var5, var3));
         }

         return var5;
      } else {
         return var1;
      }
   }

   @Unique
   private static int attachedRockets(LocalPlayer var0) {
      ClientLevel var1 = Minecraft.getInstance().level;
      if (var1 == null) {
         return 0;
      }

      int var2 = 0;

      for (Entity var4 : var1.entitiesForRendering()) {
         if (var4 instanceof FireworkRocketEntity var5 && var5.tickCount > 0 && ((FireworkRocketEntityAccessor)var5).onetap$getAttachedToEntity() == var0) {
            var2++;
         }
      }

      return var2;
   }

   @Inject(method = "updateFallFlyingMovement", at = @At("RETURN"), cancellable = true)
   private void onetap$grimElytraBooster(Vec3 var1, CallbackInfoReturnable<Vec3> var2) {
      LocalPlayer var3 = Minecraft.getInstance().player;
      if (var3 != null && (LivingEntity)(Object)this == var3) {
         ElytraBooster var4 = ElytraBooster.m_3_C0_l();
         if (var4 != null) {
            var2.setReturnValue(var4.m_0(var3, var3.getDeltaMovement(), (Vec3)var2.getReturnValue()));
         }
      }
   }

   @Inject(method = "updateFallFlyingMovement", at = @At("RETURN"), cancellable = true)
   private void onetap$elytraMotionFreeze(Vec3 var1, CallbackInfoReturnable<Vec3> var2) {
      LocalPlayer var3 = Minecraft.getInstance().player;
      if (var3 != null && (LivingEntity)(Object)this == var3) {
         ElytraMotion var4 = Onetap.MODULE_MANAGER.get(ElytraMotion.class);
         if (var4 != null && var4.isEnabled() && var4.flag) {
            var2.setReturnValue(Vec3.ZERO);
         }
      }
   }

   @Inject(method = "updateFallFlyingMovement", at = @At("RETURN"), cancellable = true)
   private void onetap$grimGlide(Vec3 var1, CallbackInfoReturnable<Vec3> var2) {
      LocalPlayer var3 = Minecraft.getInstance().player;
      if (var3 != null && (LivingEntity)(Object)this == var3) {
         GrimGlide var4 = Onetap.MODULE_MANAGER.get(GrimGlide.class);
         if (var4 != null && var4.isSlimeHvhActive()) {
            var2.setReturnValue(var4.m_1(var1, (Vec3)var2.getReturnValue()));
         }
      }
   }
}
