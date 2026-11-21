package baritone.cache;

import baritone.api.cache.ICachedWorld;
import baritone.api.cache.IWorldScanner;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.BlockOptionalMetaLookup;
import baritone.api.utils.IPlayerContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;

public enum WorldScanner implements IWorldScanner {
   INSTANCE;

   @Override
   public List<BlockPos> scanChunkRadius(IPlayerContext var1, BlockOptionalMetaLookup var2, int var3, int var4, int var5) {
      ArrayList var6 = new ArrayList();
      if (var2.blocks().isEmpty()) {
         return var6;
      }

      ClientChunkCache var7 = (ClientChunkCache)var1.world().getChunkSource();
      int var8 = var5 * var5;
      int var9 = var1.playerFeet().getX() >> 4;
      int var10 = var1.playerFeet().getZ() >> 4;
      int var11 = var1.playerFeet().getY() - var1.world().dimensionType().minY();
      int var12 = var11 >> 4;
      int[] var13 = IntStream.range(0, var1.world().dimensionType().height() / 16)
         .boxed()
         .sorted(Comparator.comparingInt(var1x -> Math.abs(var1x - var12)))
         .mapToInt(var0 -> var0)
         .toArray();
      int var14 = 0;
      boolean var15 = false;

      while (true) {
         boolean var16 = true;
         boolean var17 = false;

         for (int var18 = -var14; var18 <= var14; var18++) {
            for (int var19 = -var14; var19 <= var14; var19++) {
               int var20 = var18 * var18 + var19 * var19;
               if (var20 == var14) {
                  var17 = true;
                  int var21 = var18 + var9;
                  int var22 = var19 + var10;
                  LevelChunk var23 = var7.getChunk(var21, var22, null, false);
                  if (var23 != null) {
                     var16 = false;
                     if (this.scanChunkInto(var21 << 4, var22 << 4, var1.world().dimensionType().minY(), var23, var2, var6, var3, var4, var11, var13)) {
                        var15 = true;
                     }
                  }
               }
            }
         }

         if (var16 && var17 || var6.size() >= var3 && (var14 > var8 || var14 > 1 && var15)) {
            return var6;
         }

         var14++;
      }
   }

   @Override
   public List<BlockPos> scanChunk(IPlayerContext var1, BlockOptionalMetaLookup var2, ChunkPos var3, int var4, int var5) {
      if (var2.blocks().isEmpty()) {
         return Collections.emptyList();
      } else {
         ClientChunkCache var6 = (ClientChunkCache)var1.world().getChunkSource();
         LevelChunk var7 = var6.getChunk(var3.x(), var3.z(), null, false);
         int var8 = var1.playerFeet().getY();
         if (var7 != null && !var7.isEmpty()) {
            ArrayList var9 = new ArrayList();
            this.scanChunkInto(
               var3.x() << 4,
               var3.z() << 4,
               var1.world().dimensionType().minY(),
               var7,
               var2,
               var9,
               var4,
               var5,
               var8,
               IntStream.range(0, var1.world().dimensionType().height() / 16).toArray()
            );
            return var9;
         } else {
            return Collections.emptyList();
         }
      }
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

   private boolean scanChunkInto(
      int var1, int var2, int var3, LevelChunk var4, BlockOptionalMetaLookup var5, Collection<BlockPos> var6, int var7, int var8, int var9, int[] var10
   ) {
      LevelChunkSection[] var11 = var4.getSections();
      boolean var12 = false;

      for (int var16 : var10) {
         LevelChunkSection var17 = var11[var16];
         if (var17 != null && !var17.hasOnlyAir()) {
            int var18 = var16 << 4;
            PalettedContainer var19 = var17.getStates();

            for (int var20 = 0; var20 < 16; var20++) {
               for (int var21 = 0; var21 < 16; var21++) {
                  for (int var22 = 0; var22 < 16; var22++) {
                     BlockState var23 = (BlockState)var19.get(var22, var20, var21);
                     if (var5.has(var23)) {
                        int var24 = var18 | var20;
                        if (var6.size() >= var7) {
                           if (Math.abs(var24 - var9) < var8) {
                              var12 = true;
                           } else if (var12) {
                              return true;
                           }
                        }

                        var6.add(new BlockPos(var1 | var22, var24 + var3, var2 | var21));
                     }
                  }
               }
            }
         }
      }

      return var12;
   }
}
