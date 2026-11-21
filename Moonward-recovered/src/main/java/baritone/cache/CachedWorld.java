package baritone.cache;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.cache.ICachedWorld;
import baritone.api.cache.IWorldData;
import baritone.api.utils.Helper;
import com.google.common.cache.CacheBuilder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;

public class CachedWorld implements ICachedWorld, Helper {
   private Long2ObjectMap<CachedRegion> cachedRegions;
   private final String directory;
   private final LinkedBlockingQueue<ChunkPos> toPackQueue;
   private final Map<ChunkPos, LevelChunk> toPackMap;
   private final DimensionType dimension;
   private final ResourceKey<Level> dimensionId;

   CachedWorld(Path var1, DimensionType var2, ResourceKey<Level> var3) {
      this.cachedRegions = new Long2ObjectOpenHashMap();
      this.toPackQueue = new LinkedBlockingQueue<>();
      this.toPackMap = (Map)CacheBuilder.newBuilder().softValues().build().asMap();
      if (!Files.exists(var1)) {
         try {
            Files.createDirectories(var1);
         } catch (IOException var4) {
         }
      }

      this.directory = var1.toString();
      this.dimension = var2;
      this.dimensionId = var3;
      System.out.println("Cached world directory: " + var1);
      Baritone.getExecutor().execute(new CachedWorld.PackerThread());
      Baritone.getExecutor().execute(() -> {
         try {
            Thread.sleep(30000L);

            while (true) {
               this.save();
               Thread.sleep(600000L);
            }
         } catch (InterruptedException var2x) {
            var2x.printStackTrace();
         }
      });
   }

   @Override
   public final void queueForPacking(LevelChunk var1) {
      if (this.toPackMap.put(var1.getPos(), var1) == null) {
         this.toPackQueue.add(var1.getPos());
      }
   }

   @Override
   public final boolean isCached(int var1, int var2) {
      CachedRegion var3 = this.getRegion(var1 >> 9, var2 >> 9);
      return var3 == null ? false : var3.isCached(var1 & 511, var2 & 511);
   }

   public final boolean regionLoaded(int var1, int var2) {
      return this.getRegion(var1 >> 9, var2 >> 9) != null;
   }

   @Override
   public final ArrayList<BlockPos> getLocationsOf(String var1, int var2, int var3, int var4, int var5) {
      ArrayList var6 = new ArrayList();
      int var7 = var3 >> 9;
      int var8 = var4 >> 9;

      for (int var9 = 0; var9 <= var5; var9++) {
         for (int var10 = -var9; var10 <= var9; var10++) {
            for (int var11 = -var9; var11 <= var9; var11++) {
               int var12 = var10 * var10 + var11 * var11;
               if (var12 == var9) {
                  int var13 = var10 + var7;
                  int var14 = var11 + var8;
                  CachedRegion var15 = this.getOrCreateRegion(var13, var14);
                  if (var15 != null) {
                     var6.addAll(var15.getLocationsOf(var1));
                  }
               }
            }
         }

         if (var6.size() >= var2) {
            return var6;
         }
      }

      return var6;
   }

   private void updateCachedChunk(CachedChunk var1) {
      CachedRegion var2 = this.getOrCreateRegion(var1.x >> 5, var1.z >> 5);
      var2.updateCachedChunk(var1.x & 31, var1.z & 31, var1);
   }

   @Override
   public final void save() {
      if (!Baritone.settings().chunkCaching.value) {
         System.out.println("Not saving to disk; chunk caching is disabled.");
         this.allRegions().forEach(var0 -> {
            if (var0 != null) {
               var0.removeExpired();
            }
         });
         this.prune();
      } else {
         long var1 = System.nanoTime() / 1000000L;
         this.allRegions().parallelStream().forEach(var1x -> {
            if (var1x != null) {
               var1x.save(this.directory);
            }
         });
         long var3 = System.nanoTime() / 1000000L;
         System.out.println("World save took " + (var3 - var1) + "ms");
         this.prune();
      }
   }

   private synchronized void prune() {
      if (Baritone.settings().pruneRegionsFromRAM.value) {
         BlockPos var1 = this.guessPosition();

         for (CachedRegion var3 : this.allRegions()) {
            if (var3 != null) {
               int var4 = (var3.getX() << 9) + 256 - var1.getX();
               int var5 = (var3.getZ() << 9) + 256 - var1.getZ();
               double var6 = Math.sqrt(var4 * var4 + var5 * var5);
               if (var6 > 1024.0) {
                  this.logDebug("Deleting cached region from ram");
                  this.cachedRegions.remove(this.getRegionID(var3.getX(), var3.getZ()));
               }
            }
         }
      }
   }

   private BlockPos guessPosition() {
      for (IBaritone var2 : BaritoneAPI.getProvider().getAllBaritones()) {
         IWorldData var3 = var2.getWorldProvider().getCurrentWorld();
         if (var3 != null && var3.getCachedWorld() == this && var2.getPlayerContext().player() != null) {
            return var2.getPlayerContext().playerFeet();
         }
      }

      CachedChunk var5 = null;

      for (CachedRegion var7 : this.allRegions()) {
         if (var7 != null) {
            CachedChunk var4 = var7.mostRecentlyModified();
            if (var4 != null && (var5 == null || var5.cacheTimestamp < var4.cacheTimestamp)) {
               var5 = var4;
            }
         }
      }

      return var5 == null ? new BlockPos(0, 0, 0) : new BlockPos((var5.x << 4) + 8, 0, (var5.z << 4) + 8);
   }

   private synchronized List<CachedRegion> allRegions() {
      return new ArrayList<>(this.cachedRegions.values());
   }

   @Override
   public final void reloadAllFromDisk() {
      long var1 = System.nanoTime() / 1000000L;
      this.allRegions().forEach(var1x -> {
         if (var1x != null) {
            var1x.load(this.directory);
         }
      });
      long var3 = System.nanoTime() / 1000000L;
      System.out.println("World load took " + (var3 - var1) + "ms");
   }

   public final synchronized CachedRegion getRegion(int var1, int var2) {
      return (CachedRegion)this.cachedRegions.get(this.getRegionID(var1, var2));
   }

   private synchronized CachedRegion getOrCreateRegion(int var1, int var2) {
      return (CachedRegion)this.cachedRegions.computeIfAbsent(this.getRegionID(var1, var2), var3 -> {
         CachedRegion var5 = new CachedRegion(var1, var2, this.dimension, this.dimensionId);
         var5.load(this.directory);
         return var5;
      });
   }

   public void tryLoadFromDisk(int var1, int var2) {
      this.getOrCreateRegion(var1, var2);
   }

   private long getRegionID(int var1, int var2) {
      return !this.isRegionInWorld(var1, var2) ? 0L : var1 & 4294967295L | (var2 & 4294967295L) << 32;
   }

   private boolean isRegionInWorld(int var1, int var2) {
      return var1 <= 58594 && var1 >= -58594 && var2 <= 58594 && var2 >= -58594;
   }

   private class PackerThread implements Runnable {
      @Override
      public void run() {
         while (true) {
            try {
               ChunkPos var1 = CachedWorld.this.toPackQueue.take();
               LevelChunk var2 = CachedWorld.this.toPackMap.remove(var1);
               if (CachedWorld.this.toPackQueue.size() <= Baritone.settings().chunkPackerQueueMaxSize.value) {
                  CachedChunk var3 = ChunkPacker.pack(var2);
                  CachedWorld.this.updateCachedChunk(var3);
               }
            } catch (InterruptedException var4) {
               var4.printStackTrace();
               return;
            } catch (Throwable var5) {
               var5.printStackTrace();
            }
         }
      }
   }
}
