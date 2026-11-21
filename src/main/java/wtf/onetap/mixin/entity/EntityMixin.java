package wtf.onetap.mixin.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.combat.rotation.RotationModes;
import wtf.onetap.module.movement.NoClip;
import wtf.onetap.module.movement.Speed;
import wtf.onetap.module.player.NoPush;
import wtf.onetap.module.render.FreeLook;
import wtf.onetap.module.render.WaterSplash;

@Mixin(Entity.class)
public class EntityMixin {
   @Redirect(method = "moveRelative", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getYRot()F"))
   private float onetap$noClipMovementYaw(Entity var1) {
      NoClip var2 = Onetap.MODULE_MANAGER.get(NoClip.class);
      return var1 == Minecraft.getInstance().player && var2 != null && var2.m_6_boolean() ? var2.vector2f.x : var1.getYRot();
   }

   @Inject(method = "getViewVector", at = @At("HEAD"), cancellable = true)
   private void onetap$noClipViewVector(float var1, CallbackInfoReturnable<Vec3> var2) {
      if ((Object)this == Minecraft.getInstance().player) {
         NoClip var3 = Onetap.MODULE_MANAGER.get(NoClip.class);
         if (var3 != null && var3.m_6_boolean()) {
            var2.setReturnValue(((Entity)(Object)this).calculateViewVector(var3.vector2f.y, var3.vector2f.x));
         }
      }
   }

   @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
   private void onetap$noPushEntity(Entity var1, CallbackInfo var2) {
      NoPush var3 = Onetap.MODULE_MANAGER.get(NoPush.class);
      boolean var4 = var3 != null && var3.isEnabled() && var3.modes.isSelected("Игроки");
      if (var4 || Speed.m_7_boolean()) {
         LocalPlayer var5 = Minecraft.getInstance().player;
         if (var5 != null) {
            if ((Object)this == var5 || var1 == var5) {
               var2.cancel();
            }
         }
      }
   }

   @Inject(method = "doWaterSplashEffect", at = @At("TAIL"))
   private void onetap$waterSplash(CallbackInfo var1) {
      WaterSplash var2 = Onetap.MODULE_MANAGER.get(WaterSplash.class);
      if (var2 != null && var2.isEnabled()) {
         var2.m_3_void((Entity)(Object)this);
      }
   }

   @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
   private void onetap$turn(double var1, double var3, CallbackInfo var5) {
      if ((Object)this == Minecraft.getInstance().player) {
         FreeLook var6 = Onetap.MODULE_MANAGER.get(FreeLook.class);
         if (var6 != null && var6.isActive()) {
            var6.onMouseDelta(var1, var3);
            var5.cancel();
         } else {
            RotationModes var7 = Onetap.ROTATION_MODES;
            if (var7.isFlag5() || var7.isFlag()) {
               if (!var7.m_4_boolean()) {
                  var7.applyMouseDelta(var1, var3);
                  var5.cancel();
               }
            }
         }
      }
   }
}
