package baritone.api.cache;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockTypeAccess {
   BlockState getBlock(int var1, int var2, int var3);

   default BlockState getBlock(BlockPos var1) {
      return this.getBlock(var1.getX(), var1.getY(), var1.getZ());
   }
}
