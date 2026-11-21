package wtf.onetap.mixin.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.module.movement.NoClip;
import wtf.onetap.module.movement.Speed;
import wtf.onetap.module.player.MoreDistance;
import wtf.onetap.module.player.NoPush;

@Mixin(Player.class)
public class PlayerMixin {
   @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getLookAngle()Lnet/minecraft/world/phys/Vec3;"))
   private Vec3 onetap$noClipTravelLook(Player var1) {
      NoClip var2 = Onetap.MODULE_MANAGER.get(NoClip.class);
      return var1 == Minecraft.getInstance().player && var2 != null && var2.m_6_boolean()
         ? var1.calculateViewVector(var2.vector2f.y, var2.vector2f.x)
         : var1.getLookAngle();
   }

   @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
   private void onetap$noClipDestroySpeed(BlockState var1, CallbackInfoReturnable<Float> var2) {
      LocalPlayer var3 = Minecraft.getInstance().player;
      if (var3 != null && (Object)this == var3) {
         NoClip var4 = Onetap.MODULE_MANAGER.get(NoClip.class);
         if (var4 != null && var4.m_8_boolean()) {
            var2.setReturnValue((Float)var2.getReturnValue() * (var3.onGround() ? 1.5F : 1.75F));
         }
      }
   }

   @Inject(method = "isPushedByFluid", at = @At("RETURN"), cancellable = true)
   private void onetap$noPushWater(CallbackInfoReturnable<Boolean> var1) {
      NoPush var2 = Onetap.MODULE_MANAGER.get(NoPush.class);
      if (var2 != null && var2.isEnabled() && var2.modes.isSelected("Вода")) {
         var1.setReturnValue(false);
      }
   }

   @Inject(method = "blockInteractionRange", at = @At("RETURN"), cancellable = true)
   private void onetap$moreDistance(CallbackInfoReturnable<Double> var1) {
      Player var2 = (Player)(Object)this;
      if (var2 == Minecraft.getInstance().player && !var2.isCreative()) {
         MoreDistance var3 = Onetap.MODULE_MANAGER.get(MoreDistance.class);
         if (var3 != null && var3.isEnabled()) {
            var1.setReturnValue(5.35);
         }
      }
   }

   @Inject(method = "maybeBackOffFromEdge", at = @At("RETURN"), cancellable = true)
   private void onetap$speedEdge(Vec3 var1, MoverType var2, CallbackInfoReturnable<Vec3> var3) {
      if ((Object)this == Minecraft.getInstance().player) {
         Speed var4 = Speed.m_3_C0u();
         if (var4 != null) {
            if (var4.isFlag3()) {
               var3.setReturnValue(var1);
            } else {
               var4.m_2_void(!((Vec3)var3.getReturnValue()).equals(var1));
            }
         }
      }
   }
}
