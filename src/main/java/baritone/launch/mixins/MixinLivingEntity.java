package baritone.launch.mixins;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.RotationMoveEvent;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.module.movement.NoClip;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
   @Unique
   private RotationMoveEvent jumpRotationEvent;
   @Unique
   private RotationMoveEvent elytraRotationEvent;

   private MixinLivingEntity(EntityType<?> var1, Level var2) {
      super(var1, var2);
   }

   @Inject(method = "jumpFromGround", at = @At("HEAD"))
   private void preMoveRelative(CallbackInfo var1) {
      this.getBaritone().ifPresent(var1x -> {
         this.jumpRotationEvent = new RotationMoveEvent(RotationMoveEvent.Type.JUMP, this.getYRot(), this.getXRot());
         var1x.getGameEventHandler().onPlayerRotationMove(this.jumpRotationEvent);
      });
   }

   @Redirect(method = "jumpFromGround", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/LivingEntity.getYRot()F"))
   private float overrideYaw(LivingEntity var1) {
      if (var1 == Minecraft.getInstance().player) {
         NoClip var2 = Onetap.MODULE_MANAGER.get(NoClip.class);
         if (var2 != null && var2.m_6_boolean()) {
            return var2.vector2f.x;
         }
      }

      return var1 instanceof LocalPlayer && BaritoneAPI.getProvider().getBaritoneForPlayer((LocalPlayer)(Object)this) != null
         ? this.jumpRotationEvent.getYaw()
         : var1.getYRot();
   }

   @Inject(
      method = "updateFallFlyingMovement",
      at = @At(value = "INVOKE", target = "net/minecraft/world/entity/LivingEntity.getLookAngle()Lnet/minecraft/world/phys/Vec3;")
   )
   private void onPreElytraMove(Vec3 var1, CallbackInfoReturnable<Vec3> var2) {
      this.getBaritone().ifPresent(var1x -> {
         this.elytraRotationEvent = new RotationMoveEvent(RotationMoveEvent.Type.MOTION_UPDATE, this.getYRot(), this.getXRot());
         var1x.getGameEventHandler().onPlayerRotationMove(this.elytraRotationEvent);
         this.setYRot(this.elytraRotationEvent.getYaw());
         this.setXRot(this.elytraRotationEvent.getPitch());
      });
   }

   @Inject(
      method = "travelFallFlying",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V",
         shift = Shift.AFTER
      )
   )
   private void onPostElytraMove(CallbackInfo var1) {
      if (this.elytraRotationEvent != null) {
         this.setYRot(this.elytraRotationEvent.getOriginal().getYaw());
         this.setXRot(this.elytraRotationEvent.getOriginal().getPitch());
         this.elytraRotationEvent = null;
      }
   }

   @Unique
   private Optional<IBaritone> getBaritone() {
      return LocalPlayer.class.isInstance(this) ? Optional.ofNullable(BaritoneAPI.getProvider().getBaritoneForPlayer((LocalPlayer)(Object)this)) : Optional.empty();
   }
}
