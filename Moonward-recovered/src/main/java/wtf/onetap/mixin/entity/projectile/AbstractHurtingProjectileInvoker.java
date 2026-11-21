package wtf.onetap.mixin.entity.projectile;

import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractHurtingProjectile.class)
public interface AbstractHurtingProjectileInvoker {
   @Invoker("getInertia")
   float onetap$getInertia();

   @Invoker("getLiquidInertia")
   float onetap$getLiquidInertia();
}
