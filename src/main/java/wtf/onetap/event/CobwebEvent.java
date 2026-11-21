package wtf.onetap.event;

import net.minecraft.core.BlockPos;

public class CobwebEvent extends Event implements Cancellable {
   private final BlockPos blockPos;
   private boolean cancelled;

   public CobwebEvent(BlockPos blockPos2) {
      this.blockPos = blockPos2;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void cancel() {
      this.cancelled = true;
   }
}
