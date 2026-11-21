package baritone.launch.mixins;

import baritone.utils.accessor.IFireworkRocketEntity;
import java.util.OptionalInt;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FireworkRocketEntity.class)
public abstract class MixinFireworkRocketEntity extends Entity implements IFireworkRocketEntity {
   @Shadow
   @Final
   private static EntityDataAccessor<OptionalInt> DATA_ATTACHED_TO_TARGET;
   @Shadow
   private LivingEntity attachedToEntity;

   @Shadow
   public abstract boolean isAttachedToEntity();

   private MixinFireworkRocketEntity(Level var1) {
      super(EntityTypes.FIREWORK_ROCKET, var1);
   }

   @Override
   public LivingEntity getBoostedEntity() {
      if (this.isAttachedToEntity() && this.attachedToEntity == null) {
         Entity var1 = this.level().getEntity(this.entityData.get(DATA_ATTACHED_TO_TARGET).getAsInt());
         if (var1 instanceof LivingEntity) {
            this.attachedToEntity = (LivingEntity)var1;
         }
      }

      return this.attachedToEntity;
   }
}
