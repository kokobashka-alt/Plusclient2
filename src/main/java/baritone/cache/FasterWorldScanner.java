package baritone.cache;

import baritone.api.cache.ICachedWorld;
import baritone.api.cache.IWorldScanner;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.BlockOptionalMeta;
import baritone.api.utils.BlockOptionalMetaLookup;
import baritone.api.utils.IPlayerContext;
import baritone.utils.accessor.IPalettedContainer;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.BitStorage;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.GlobalPalette;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.SingleValuePalette;

public enum FasterWorldScanner implements IWorldScanner {
   INSTANCE;

   private static final BlockState[] PALETTE_REGISTRY_SENTINEL = new BlockState[0];

   @Override
   public List<BlockPos> scanChunkRadius(IPlayerContext var1, BlockOptionalMetaLookup var2, int var3, int var4, int var5) {
      assert var1.world() != null;
      if (var5 < 0) {
         throw new IllegalArgumentException("chunkRange must be >= 0");
      } else {
         return this.scanChunksInternal(var1, var2, getChunkRange(var1.playerFeet().x >> 4, var1.playerFeet().z >> 4, var5), var3);
      }
   }

   @Override
   public List<BlockPos> scanChunk(IPlayerContext var1, BlockOptionalMetaLookup var2, ChunkPos var3, int var4, int var5) {
      Stream var6 = this.scanChunkInternal(var1, var2, var3);
      if (var4 >= 0) {
         var6 = var6.limit(var4);
      }

      return (List<BlockPos>)var6.collect(Collectors.toList());
   }

   @Override
   public int repack(IPlayerContext var1) {
      return this.repack(var1, 40);
   }

   @Override
   public int repack(IPlayerContext var1, int var2) {
      ChunkSource var3 = var1.world().getChunkSource();
      ICachedWorld var4 = var1.worldData().getCachedWorld();
      BetterBlockPos var5 = var1.playerFeet();
      int var6 = var5.getX() >> 4;
      int var7 = var5.getZ() >> 4;
      int var8 = var6 - var2;
      int var9 = var7 - var2;
      int var10 = var6 + var2;
      int var11 = var7 + var2;
      int var12 = 0;

      for (int var13 = var8; var13 <= var10; var13++) {
         for (int var14 = var9; var14 <= var11; var14++) {
            LevelChunk var15 = var3.getChunk(var13, var14, false);
            if (var15 != null && !var15.isEmpty()) {
               var12++;
               var4.queueForPacking(var15);
            }
         }
      }

      return var12;
   }

   public static List<ChunkPos> getChunkRange(int var0, int var1, int var2) {
      ArrayList var3 = new ArrayList();
      var3.add(new ChunkPos(var0, var1));

      for (int var4 = 1; var4 < var2; var4++) {
         for (int var5 = 0; var5 <= var4; var5++) {
            var3.add(new ChunkPos(var0 - var5, var1 - var4));
            if (var5 != 0) {
               var3.add(new ChunkPos(var0 + var5, var1 - var4));
               var3.add(new ChunkPos(var0 - var5, var1 + var4));
            }

            var3.add(new ChunkPos(var0 + var5, var1 + var4));
            if (var5 != var4) {
               var3.add(new ChunkPos(var0 - var4, var1 - var5));
               var3.add(new ChunkPos(var0 + var4, var1 - var5));
               if (var5 != 0) {
                  var3.add(new ChunkPos(var0 - var4, var1 + var5));
                  var3.add(new ChunkPos(var0 + var4, var1 + var5));
               }
            }
         }
      }

      return var3;
   }

   private List<BlockPos> scanChunksInternal(IPlayerContext var1, BlockOptionalMetaLookup var2, List<ChunkPos> var3, int var4) {
      assert var1.world() != null;

      try {
         Stream var5 = var3.parallelStream().flatMap(var3x -> this.scanChunkInternal(var1, var2, var3x));
         if (var4 >= 0) {
            var5 = var5.limit(var4);
         }

         return (List<BlockPos>)var5.collect(Collectors.toList());
      } catch (Exception var6) {
         var6.printStackTrace();
         throw var6;
      }
   }

   private Stream<BlockPos> scanChunkInternal(IPlayerContext var1, BlockOptionalMetaLookup var2, ChunkPos var3) {
      ChunkSource var4 = var1.world().getChunkSource();
      if (!var4.hasChunk(var3.x(), var3.z())) {
         return Stream.empty();
      }

      long var5 = (long)var3.x() << 4;
      long var7 = (long)var3.z() << 4;
      int var9 = var1.playerFeet().y - var1.world().getMinY() >> 4;
      return this.collectChunkSections(var2, var4.getChunk(var3.x(), var3.z(), false), var5, var7, var9).stream();
   }

   private List<BlockPos> collectChunkSections(BlockOptionalMetaLookup var1, LevelChunk var2, long var3, long var5, int var7) {
      ArrayList var8 = new ArrayList();
      int var9 = var2.getMinY();
      LevelChunkSection[] var10 = var2.getSections();
      int var11 = var10.length;
      int var12 = var7 - 1;

      for (int var13 = var7; var12 >= 0 || var13 < var11; var12--) {
         if (var13 < var11) {
            this.visitSection(var1, var10[var13], var8, var3, var9 + var13 * 16, var5);
         }

         if (var12 >= 0) {
            this.visitSection(var1, var10[var12], var8, var3, var9 + var12 * 16, var5);
         }

         var13++;
      }

      return var8;
   }

   private void visitSection(BlockOptionalMetaLookup var1, LevelChunkSection var2, List<BlockPos> var3, long var4, int var6, long var7) {
      if (var2 != null && !var2.hasOnlyAir()) {
         PalettedContainer var9 = var2.getStates();
         if (((IPalettedContainer)var9).getStorage() != null) {
            Palette var10 = ((IPalettedContainer)var9).getPalette();
            if (var10 instanceof SingleValuePalette) {
               if (var1.has((BlockState)var10.valueFor(0))) {
                  for (int var24 = 0; var24 < 16; var24++) {
                     for (int var25 = 0; var25 < 16; var25++) {
                        for (int var26 = 0; var26 < 16; var26++) {
                           var3.add(new BlockPos((int)var4 + var24, var6 + var25, (int)var7 + var26));
                        }
                     }
                  }
               }
            } else {
               boolean[] var11 = this.getIncludedFilterIndices(var1, var10);
               if (var11.length != 0) {
                  BitStorage var12 = ((IPalettedContainer)var2.getStates()).getStorage();
                  long[] var13 = var12.getRaw();
                  int var14 = var12.getSize();
                  int var15 = var12.getBits();
                  long var16 = (1L << var15) - 1L;
                  int var18 = 0;

                  for (int var19 = 0; var18 < var13.length && var19 < var14; var18++) {
                     long var20 = var13[var18];

                     for (int var22 = 0; var22 <= 64 - var15 && var19 < var14; var19++) {
                        int var23 = (int)(var20 >> var22 & var16);
                        if (var11[var23]) {
                           var3.add(new BlockPos((int)var4 + (var19 & 0xFF & 15), var6 + (var19 >> 8), (int)var7 + ((var19 & 0xFF) >> 4)));
                        }

                        var22 += var15;
                     }
                  }
               }
            }
         }
      }
   }

   private boolean[] getIncludedFilterIndices(BlockOptionalMetaLookup var1, Palette<BlockState> var2) {
      boolean var3 = false;
      BlockState[] var4 = getPalette(var2);
      if (var4 == PALETTE_REGISTRY_SENTINEL) {
         return this.getIncludedFilterIndicesFromRegistry(var1);
      }

      int var5 = var4.length;
      boolean[] var6 = new boolean[var5];

      for (int var7 = 0; var7 < var5; var7++) {
         BlockState var8 = var4[var7];
         if (var1.has(var8)) {
            var6[var7] = true;
            var3 = true;
         } else {
            var6[var7] = false;
         }
      }

      return !var3 ? new boolean[0] : var6;
   }

   private boolean[] getIncludedFilterIndicesFromRegistry(BlockOptionalMetaLookup var1) {
      boolean[] var2 = new boolean[Block.BLOCK_STATE_REGISTRY.size()];

      for (BlockOptionalMeta var4 : var1.blocks()) {
         for (BlockState var6 : var4.getAllBlockStates()) {
            var2[Block.BLOCK_STATE_REGISTRY.getId(var6)] = true;
         }
      }

      return var2;
   }

   private static BlockState[] getPalette(Palette<BlockState> var0) {
      if (var0 instanceof GlobalPalette) {
         return PALETTE_REGISTRY_SENTINEL;
      }

      FriendlyByteBuf var1 = new FriendlyByteBuf(Unpooled.buffer());
      var0.write(var1, Block.BLOCK_STATE_REGISTRY);
      int var2 = var1.readVarInt();
      BlockState[] var3 = new BlockState[var2];

      for (int var4 = 0; var4 < var2; var4++) {
         BlockState var5 = Block.BLOCK_STATE_REGISTRY.byId(var1.readVarInt());
         assert var5 != null;
         var3[var4] = var5;
      }

      return var3;
   }
}
