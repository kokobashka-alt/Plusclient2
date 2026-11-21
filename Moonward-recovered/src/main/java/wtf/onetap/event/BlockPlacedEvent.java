package wtf.onetap.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public class BlockPlacedEvent extends Event {
   private final ItemStack itemStack;
   private final BlockPos blockPos;

   public BlockPlacedEvent(ItemStack itemStack2, BlockPos blockPos2) {
      this.itemStack = itemStack2;
      this.blockPos = blockPos2;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }
}
