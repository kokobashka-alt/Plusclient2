package wtf.onetap.mixin.entity.projectile;

import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface AbstractArrowInvoker {
   @Invoker("isInGround")
   boolean onetap$isInGround();

   @Invoker("getWaterInertia")
   float onetap$getWaterInertia();
}
