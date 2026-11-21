package wtf.onetap.mixin.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.event.CobwebEvent;

@Mixin(WebBlock.class)
public class CobwebMixin {
   @Inject(
      method = "entityInside",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;makeStuckInBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)V"
      ),
      cancellable = true
   )
   private void onetap$onEntityCollision(
      BlockState var1, Level var2, BlockPos var3, Entity var4, InsideBlockEffectApplier var5, boolean var6, CallbackInfo var7
   ) {
      if (var4 == Minecraft.getInstance().player) {
         CobwebEvent var8 = new CobwebEvent(var3);
         Onetap.EVENT_BUS.unregister(var8);
         if (var8.isCancelled()) {
            var7.cancel();
         }
      }
   }
}
