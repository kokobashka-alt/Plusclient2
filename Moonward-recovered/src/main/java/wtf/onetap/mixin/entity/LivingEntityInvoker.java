package wtf.onetap.mixin.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityInvoker {
   @Invoker("updateFallFlyingMovement")
   Vec3 onetap$updateFallFlyingMovement(Vec3 var1);

   @Invoker("getEffectiveGravity")
   double onetap$getEffectiveGravity();

   @Accessor("noJumpDelay")
   int onetap$getNoJumpDelay();

   @Accessor("DATA_LIVING_ENTITY_FLAGS")
   static EntityDataAccessor<Byte> onetap$getLivingEntityFlags() {
      throw new AssertionError();
   }
}
