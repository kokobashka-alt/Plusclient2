package baritone.api.process;

import baritone.api.utils.BlockOptionalMeta;
import baritone.api.utils.BlockOptionalMetaLookup;
import java.util.stream.Stream;
import net.minecraft.world.level.block.Block;

public interface IMineProcess extends IBaritoneProcess {
   void mineByName(int var1, String... var2);

   void mine(int var1, BlockOptionalMetaLookup var2);

   default void mine(BlockOptionalMetaLookup var1) {
      this.mine(0, var1);
   }

   default void mineByName(String... var1) {
      this.mineByName(0, var1);
   }

   default void mine(int var1, BlockOptionalMeta... var2) {
      this.mine(var1, new BlockOptionalMetaLookup(var2));
   }

   default void mine(BlockOptionalMeta... var1) {
      this.mine(0, var1);
   }

   default void mine(int var1, Block... var2) {
      this.mine(var1, new BlockOptionalMetaLookup(Stream.of(var2).map(BlockOptionalMeta::new).toArray(BlockOptionalMeta[]::new)));
   }

   default void mine(Block... var1) {
      this.mine(0, var1);
   }

   default void cancel() {
      this.onLostControl();
   }
}
