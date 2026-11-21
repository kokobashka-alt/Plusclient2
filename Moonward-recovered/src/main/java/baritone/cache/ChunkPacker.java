package baritone.cache;

import baritone.api.utils.BlockUtils;
import baritone.pathing.movement.MovementHelper;
import baritone.utils.BlockStateInterface;
import baritone.utils.pathing.PathingBlockType;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;

public class ChunkPacker {
   private ChunkPacker() {
   }

   public static CachedChunk pack(LevelChunk var0) {
      HashMap var1 = new HashMap();
      int var2 = var0.getLevel().dimensionType().height();
      BitSet var3 = new BitSet(CachedChunk.size(var2));

      try {
         LevelChunkSection[] var4 = var0.getSections();

         for (int var5 = 0; var5 < var2 / 16; var5++) {
            LevelChunkSection var6 = var4[var5];
            if (var6 != null) {
               PalettedContainer var7 = var6.getStates();
               int var8 = var5 << 4;

               for (int var9 = 0; var9 < 16; var9++) {
                  int var10 = var9 | var8;

                  for (int var11 = 0; var11 < 16; var11++) {
                     for (int var12 = 0; var12 < 16; var12++) {
                        int var13 = CachedChunk.getPositionIndex(var12, var10, var11);
                        BlockState var14 = (BlockState)var7.get(var12, var9, var11);
                        boolean[] var15 = getPathingBlockType(var14, var0, var12, var10 + var0.getMinY(), var11).getBits();
                        var3.set(var13, var15[0]);
                        var3.set(var13 + 1, var15[1]);
                        Block var16 = var14.getBlock();
                        if (CachedChunk.BLOCKS_TO_KEEP_TRACK_OF.contains(var16)) {
                           String var17 = BlockUtils.blockToString(var16);
                           ((java.util.List)var1.computeIfAbsent(var17, var0x -> new ArrayList<>())).add(new BlockPos(var12, var10 + var0.getMinY(), var11));
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var18) {
         var18.printStackTrace();
      }

      BlockState[] var19 = new BlockState[256];

      for (int var20 = 0; var20 < 16; var20++) {
         label58:
         for (int var21 = 0; var21 < 16; var21++) {
            for (int var22 = var2 - 1; var22 >= 0; var22--) {
               int var23 = CachedChunk.getPositionIndex(var21, var22, var20);
               if (var3.get(var23) || var3.get(var23 + 1)) {
                  var19[var20 << 4 | var21] = BlockStateInterface.getFromChunk(var0, var21, var22, var20);
                  continue label58;
               }
            }

            var19[var20 << 4 | var21] = Blocks.AIR.defaultBlockState();
         }
      }

      return new CachedChunk(var0.getPos().x(), var0.getPos().z(), var2, var3, var19, var1, System.currentTimeMillis());
   }

   private static PathingBlockType getPathingBlockType(BlockState var0, LevelChunk var1, int var2, int var3, int var4) {
      Block var5 = var0.getBlock();
      if (MovementHelper.isWater(var0)) {
         if (MovementHelper.possiblyFlowing(var0)) {
            return PathingBlockType.AVOID;
         }

         int var6 = var3 - var1.getLevel().dimensionType().minY();
         if ((var2 == 15 || !MovementHelper.possiblyFlowing(BlockStateInterface.getFromChunk(var1, var2 + 1, var6, var4)))
            && (var2 == 0 || !MovementHelper.possiblyFlowing(BlockStateInterface.getFromChunk(var1, var2 - 1, var6, var4)))
            && (var4 == 15 || !MovementHelper.possiblyFlowing(BlockStateInterface.getFromChunk(var1, var2, var6, var4 + 1)))
            && (var4 == 0 || !MovementHelper.possiblyFlowing(BlockStateInterface.getFromChunk(var1, var2, var6, var4 - 1)))) {
            if (var2 != 0 && var2 != 15 && var4 != 0 && var4 != 15) {
               return PathingBlockType.WATER;
            }

            Vec3 var7 = var0.getFluidState().getFlow(var1.getLevel(), new BlockPos(var2 + (var1.getPos().x() << 4), var3, var4 + (var1.getPos().z() << 4)));
            return var7.x == 0.0 && var7.z == 0.0 ? PathingBlockType.AVOID : PathingBlockType.WATER;
         } else {
            return PathingBlockType.AVOID;
         }
      } else if (MovementHelper.avoidWalkingInto(var0) || MovementHelper.isBottomSlab(var0)) {
         return PathingBlockType.AVOID;
      } else {
         return !(var5 instanceof AirBlock) && !(var5 instanceof TallGrassBlock) && !(var5 instanceof DoublePlantBlock) && !(var5 instanceof FlowerBlock)
            ? PathingBlockType.SOLID
            : PathingBlockType.AIR;
      }
   }

   public static BlockState pathingTypeToBlock(PathingBlockType var0, DimensionType var1, ResourceKey<Level> var2) {
      switch (var0) {
         case AIR:
            return Blocks.AIR.defaultBlockState();
         case WATER:
            return Blocks.WATER.defaultBlockState();
         case AVOID:
            return Blocks.LAVA.defaultBlockState();
         case SOLID:
            if (var2 == Level.NETHER) {
               return Blocks.NETHERRACK.defaultBlockState();
            } else {
               if (var2 == Level.END) {
                  return Blocks.END_STONE.defaultBlockState();
               }

               return Blocks.STONE.defaultBlockState();
            }
         default:
            return null;
      }
   }
}
