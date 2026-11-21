package wtf.onetap.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateChangeEvent extends Event {
   private final BlockPos pos;
   private final BlockState oldState;
   private final BlockState newState;

   public BlockStateChangeEvent(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
      this.pos = blockPos;
      this.oldState = blockState;
      this.newState = blockState2;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getOldState() {
      return this.oldState;
   }

   public BlockState getNewState() {
      return this.newState;
   }
}
