package baritone.api.process;

import baritone.api.utils.BlockOptionalMeta;
import net.minecraft.world.level.block.Block;

public interface IGetToBlockProcess extends IBaritoneProcess {
   void getToBlock(BlockOptionalMeta var1);

   default void getToBlock(Block var1) {
      this.getToBlock(new BlockOptionalMeta(var1));
   }

   boolean blacklistClosest();
}
