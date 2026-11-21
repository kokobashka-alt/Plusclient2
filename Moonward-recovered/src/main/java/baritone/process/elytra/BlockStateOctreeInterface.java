package baritone.process.elytra;

import dev.babbaj.pathfinder.NetherPathfinder;
import dev.babbaj.pathfinder.Octree;

public class BlockStateOctreeInterface {
   private final NetherPathfinderContext context;
   private final long contextPtr;
   private final int minY;
   transient long chunkPtr;
   private int prevChunkX = Integer.MAX_VALUE;
   private int prevChunkZ = Integer.MAX_VALUE;

   public BlockStateOctreeInterface(NetherPathfinderContext var1) {
      this.context = var1;
      this.contextPtr = var1.context;
      this.minY = var1.minY;
   }

   public boolean get0(int var1, int var2, int var3) {
      int var4 = var2 - this.minY;
      if (var4 >= 0 && var4 <= 383) {
         int var5 = var1 >> 4;
         int var6 = var3 >> 4;
         if (this.chunkPtr == 0L | (var5 ^ this.prevChunkX | var6 ^ this.prevChunkZ) != 0) {
            this.prevChunkX = var5;
            this.prevChunkZ = var6;
            this.chunkPtr = NetherPathfinder.getChunkOrDefault(this.contextPtr, var5, var6, true);
         }

         return Octree.getBlock(this.chunkPtr, var1 & 15, var4, var3 & 15);
      } else {
         return false;
      }
   }
}
