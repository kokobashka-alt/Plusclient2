package baritone.api.cache;

import baritone.api.utils.BlockOptionalMetaLookup;
import baritone.api.utils.IPlayerContext;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;

public interface IWorldScanner {
   List<BlockPos> scanChunkRadius(IPlayerContext var1, BlockOptionalMetaLookup var2, int var3, int var4, int var5);

   default List<BlockPos> scanChunkRadius(IPlayerContext var1, List<Block> var2, int var3, int var4, int var5) {
      return this.scanChunkRadius(var1, new BlockOptionalMetaLookup(var2.toArray(new Block[0])), var3, var4, var5);
   }

   List<BlockPos> scanChunk(IPlayerContext var1, BlockOptionalMetaLookup var2, ChunkPos var3, int var4, int var5);

   default List<BlockPos> scanChunk(IPlayerContext var1, List<Block> var2, ChunkPos var3, int var4, int var5) {
      return this.scanChunk(var1, new BlockOptionalMetaLookup(var2), var3, var4, var5);
   }

   int repack(IPlayerContext var1);

   int repack(IPlayerContext var1, int var2);
}
