package wtf.onetap.mixin.entity.projectile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import wtf.onetap.combat.AttackCooldownTracker;
import wtf.onetap.combat.ElytraMode;
import wtf.onetap.module.combat.ElytraTarget;
import wtf.onetap.module.movement.ElytraBooster;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {
   @Shadow
   private LivingEntity attachedToEntity;

   @Inject(method = "tick", at = @At("HEAD"))
   private void onetap$trackBoost(CallbackInfo var1) {
      if (this.attachedToEntity != null) {
         if (((FireworkRocketEntity)(Object)this).level().isClientSide()) {
            AttackCooldownTracker.markAttacked(this.attachedToEntity.getId());
         }
      }
   }

   @ModifyArgs(
      method = "tick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V")
   )
   private void onetap$grimFireworkVelocity(Args var1) {
      LocalPlayer var2 = Minecraft.getInstance().player;
      if (var2 != null && this.attachedToEntity == var2) {
         if (ElytraBooster.m_3_C0_l() != null) {
            var1.set(0, this.attachedToEntity.getDeltaMovement());
         } else {
            ElytraTarget var3 = ElytraTarget.getActive();
            if (var3 != null && ElytraMode.isGroundGrim(var3.m_9_String())) {
               var1.set(0, this.attachedToEntity.getDeltaMovement());
            }
         }
      }
   }

   @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 1.5))
   private double onetap$fireworkBoost(double var1) {
      return ElytraBooster.m_7_float(this.attachedToEntity);
   }
}
