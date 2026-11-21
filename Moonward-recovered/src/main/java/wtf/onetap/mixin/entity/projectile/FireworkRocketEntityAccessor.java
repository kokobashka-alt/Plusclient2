package wtf.onetap.mixin.entity.projectile;

import java.util.OptionalInt;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FireworkRocketEntity.class)
public interface FireworkRocketEntityAccessor {
   @Accessor("attachedToEntity")
   LivingEntity onetap$getAttachedToEntity();

   @Accessor("DATA_ATTACHED_TO_TARGET")
   static EntityDataAccessor<OptionalInt> onetap$getAttachedTargetData() {
      throw new AssertionError();
   }
}
