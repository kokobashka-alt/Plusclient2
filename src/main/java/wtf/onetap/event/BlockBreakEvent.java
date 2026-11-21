package wtf.onetap.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class BlockBreakEvent extends Event implements Cancellable {
   private final BlockPos pos;
   private final Direction direction;
   private boolean cancelled;

   public BlockBreakEvent(BlockPos blockPos, Direction direction2) {
      this.pos = blockPos;
      this.direction = direction2;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Direction getDirection() {
      return this.direction;
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
