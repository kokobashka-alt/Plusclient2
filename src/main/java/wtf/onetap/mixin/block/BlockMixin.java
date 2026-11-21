package wtf.onetap.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.event.BlockPlacedEvent;

@Mixin(Block.class)
public class BlockMixin {
   @Inject(method = "setPlacedBy", at = @At("HEAD"))
   private void onetap$setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5, CallbackInfo var6) {
      Onetap.EVENT_BUS.unregister(new BlockPlacedEvent(var5, var2));
   }
}
