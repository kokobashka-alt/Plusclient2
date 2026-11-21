package baritone.process.elytra;

import baritone.Baritone;
import baritone.api.event.events.BlockChangeEvent;
import baritone.utils.accessor.IPalettedContainer;
import dev.babbaj.pathfinder.NetherPathfinder;
import dev.babbaj.pathfinder.Octree;
import dev.babbaj.pathfinder.PathSegment;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.BitStorage;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.phys.Vec3;
import sun.misc.Unsafe;

public class NetherPathfinderContext implements IElytraPathFinder {
   private static final Unsafe UNSAFE;
   private static final BlockState AIR_BLOCK_STATE;
   public final ReentrantReadWriteLock rwl;
   public final ReadLock readLock;
   public final WriteLock writeLock;
   private final int maxHeight;
   final long context;
   private final long seed;
   private final ExecutorService writeExecutor;
   private final ExecutorService readExecutor;
   private final ResourceKey<Level> dimension;
   final int minY;
   private final BlockStateOctreeInterface boi;

   public NetherPathfinderContext(long var1, Path var3, Level var4) {
      this.rwl = new ReentrantReadWriteLock();
      this.readLock = this.rwl.readLock();
      this.writeLock = this.rwl.writeLock();
      this.writeExecutor = Executors.newSingleThreadExecutor(Thread.ofPlatform().daemon().factory());
      this.readExecutor = Executors.newSingleThreadExecutor(Thread.ofPlatform().daemon().factory());
      this.dimension = var4.dimension();
      this.minY = var4.dimensionType().minY();
      int var5;
      if (this.dimension == Level.NETHER) {
         var5 = NetherPathfinder.DIMENSION_NETHER;
      } else if (this.dimension == Level.END) {
         var5 = NetherPathfinder.DIMENSION_END;
      } else {
         var5 = NetherPathfinder.DIMENSION_OVERWORLD;
      }

      int var6 = Math.min(var4.dimensionType().height(), 384);
      if (!Baritone.settings().elytraAllowAboveRoof.value && var5 == NetherPathfinder.DIMENSION_NETHER) {
         var6 = Math.min(var6, 128);
      }

      this.maxHeight = var6;
      this.context = NetherPathfinder.newContext(var1, var3 != null ? var3.toString() : null, var5, var6, Baritone.settings().elytraCustomAllocator.value);
      this.seed = var1;
      this.boi = new BlockStateOctreeInterface(this);
   }

   public boolean hasChunk(ChunkPos var1) {
      return NetherPathfinder.hasChunkFromJava(this.context, var1.x(), var1.z());
   }

   public void queueCacheCulling(int var1, int var2, int var3) {
      this.writeExecutor.execute(() -> {
         this.writeLock.lock();

         try {
            this.boi.chunkPtr = 0L;
            NetherPathfinder.cullFarChunks(this.context, var1, var2, var3);
         } finally {
            this.writeLock.unlock();
         }
      });
   }

   public void queueForPacking(LevelChunk var1) {
      SoftReference var2 = new SoftReference<>(var1);
      this.writeExecutor.execute(() -> {
         LevelChunk var2x = (LevelChunk)var2.get();
         if (var2x != null) {
            this.writeLock.lock();

            try {
               this.boi.chunkPtr = 0L;
               long var3 = NetherPathfinder.allocateAndInsertChunk(this.context, var2x.getPos().x(), var2x.getPos().z());
               writeChunkData(var2x, var3);
            } finally {
               this.writeLock.unlock();
            }
         }
      });
   }

   public void queueBlockUpdate(BlockChangeEvent var1) {
      this.writeExecutor.execute(() -> {
         ChunkPos var2 = var1.getChunkPos();
         this.writeLock.lock();

         try {
            long var3 = NetherPathfinder.getChunk(this.context, var2.x(), var2.z());
            if (var3 != 0L) {
               var1.getBlocks().forEach(var3x -> {
                  BlockPos var4 = var3x.first().below(this.minY);
                  if (var4.getY() >= 0 && var4.getY() < 384) {
                     boolean var5 = var3x.second() != AIR_BLOCK_STATE;
                     Octree.setBlock(var3, var4.getX() & 15, var4.getY(), var4.getZ() & 15, var5);
                  }
               });
               return;
            }
         } finally {
            this.writeLock.unlock();
         }
      });
   }

   @Override
   public CompletableFuture<UnpackedSegment> pathFindAsync(BlockPos var1, BlockPos var2) {
      BlockPos var3 = var1.below(this.minY);
      BlockPos var4 = var2.below(this.minY);
      boolean var5 = Baritone.settings().elytraPredictTerrain.value && this.dimension == Level.NETHER;
      Lock var6 = var5 ? this.writeLock : this.readLock;
      ExecutorService var7 = var5 ? this.writeExecutor : this.readExecutor;
      return CompletableFuture.supplyAsync(
         () -> {
            var6.lock();

            try {
               PathSegment var5x = NetherPathfinder.pathFind(
                  this.context,
                  var3.getX(),
                  var3.getY(),
                  var3.getZ(),
                  var4.getX(),
                  var4.getY(),
                  var4.getZ(),
                  !Baritone.settings().elytraAllowTightSpaces.value,
                  false,
                  10000,
                  !var5,
                  8.0
               );
               if (var5x == null) {
                  throw new PathCalculationException("Path calculation failed");
               } else {
                  return new UnpackedSegment(UnpackedSegment.from(var5x).collect().stream().map(var1xx -> var1xx.above(this.minY)), var5x.finished);
               }
            } finally {
               var6.unlock();
            }
         },
         var7
      );
   }

   public boolean raytrace(double var1, double var3, double var5, double var7, double var9, double var11) {
      double var13 = var3 - this.minY;
      double var15 = var9 - this.minY;
      return NetherPathfinder.isVisible(this.context, NetherPathfinder.CACHE_MISS_SOLID, var1, var13, var5, var7, var15, var11);
   }

   public boolean raytrace(Vec3 var1, Vec3 var2) {
      Vec3 var3 = var1.subtract(0.0, this.minY, 0.0);
      Vec3 var4 = var2.subtract(0.0, this.minY, 0.0);
      return NetherPathfinder.isVisible(this.context, NetherPathfinder.CACHE_MISS_SOLID, var3.x, var3.y, var3.z, var4.x, var4.y, var4.z);
   }

   public boolean raytrace(int var1, double[] var2, double[] var3, int var4) {
      if (var2.length == var1 * 3 && var3.length == var1 * 3) {
         byte var5 = 1;

         while (var5 < var2.length) {
            var5 += 3;
         }

         switch (var4) {
            case 0:
               return NetherPathfinder.isVisibleMulti(this.context, NetherPathfinder.CACHE_MISS_SOLID, var1, var2, var3, false) == -1;
            case 1:
               return NetherPathfinder.isVisibleMulti(this.context, NetherPathfinder.CACHE_MISS_SOLID, var1, var2, var3, true) == -1;
            case 2:
               return NetherPathfinder.isVisibleMulti(this.context, NetherPathfinder.CACHE_MISS_SOLID, var1, var2, var3, true) != -1;
            default:
               throw new IllegalArgumentException("lol");
         }
      } else {
         throw new IllegalArgumentException("Bad array lengths");
      }
   }

   public void raytrace(int var1, double[] var2, double[] var3, boolean[] var4, double[] var5) {
      if (var2.length == var1 * 3 && var3.length == var1 * 3) {
         byte var6 = 1;

         while (var6 < var2.length) {
            var6 += 3;
         }

         NetherPathfinder.raytrace(this.context, NetherPathfinder.CACHE_MISS_SOLID, var1, var2, var3, var4, var5);
      } else {
         throw new IllegalArgumentException("Bad array lengths");
      }
   }

   public boolean passable(int var1, int var2, int var3) {
      return !this.boi.get0(var1, var2, var3);
   }

   public void cancel() {
      NetherPathfinder.cancel(this.context);
   }

   public void destroy() {
      this.cancel();
      this.readExecutor.shutdownNow();
      this.writeExecutor.shutdownNow();

      try {
         while (!this.readExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
         }

         while (!this.writeExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
         }
      } catch (InterruptedException var2) {
         var2.printStackTrace();
      }

      NetherPathfinder.freeContext(this.context);
   }

   public long getSeed() {
      return this.seed;
   }

   public void acquireReadLock() {
      this.readLock.lock();
   }

   public boolean tryAcquireReadLock() {
      return this.readLock.tryLock();
   }

   public void releaseReadLock() {
      this.readLock.unlock();
   }

   public int getMaxHeight() {
      return this.maxHeight;
   }

   private static void writeChunkData(LevelChunk var0, long var1) {
      try {
         LevelChunkSection[] var3 = var0.getSections();
         int var4 = Math.min(var3.length, 24);

         for (int var5 = 0; var5 < var4; var5++) {
            LevelChunkSection var6 = var3[var5];
            if (var6 != null && !var6.hasOnlyAir()) {
               PalettedContainer var7 = var6.getStates();
               IPalettedContainer var8 = (IPalettedContainer)var7;
               Palette var9 = var8.getPalette();
               int var10 = -1;
               int var11 = -1;
               int var12 = -1;
               int var13 = -1;

               for (int var14 = 0; var14 < var9.getSize(); var14++) {
                  BlockState var15 = (BlockState)var9.valueFor(var14);
                  if (var15 == Blocks.AIR.defaultBlockState()) {
                     var10 = var14;
                  } else if (var15 == Blocks.CAVE_AIR.defaultBlockState()) {
                     var11 = var14;
                  } else if (var15 == Blocks.RED_MUSHROOM.defaultBlockState()) {
                     var12 = var14;
                  } else if (var15 == Blocks.BROWN_MUSHROOM.defaultBlockState()) {
                     var13 = var14;
                  }
               }

               if (var10 == -1 & var11 == -1) {
                  UNSAFE.setMemory(var1 + var5 * 512L, 512L, (byte)-1);
               } else {
                  BitStorage var31 = var8.getStorage();
                  if (var31 != null) {
                     long[] var32 = var31.getRaw();
                     int var16 = var31.getSize();
                     int var17 = var31.getBits();
                     long var18 = (1L << var17) - 1L;
                     int var20 = var5 << 4;
                     int var21 = 0;

                     for (int var22 = 0; var21 < var32.length && var22 < var16; var21++) {
                        long var23 = var32[var21];

                        for (int var25 = 0; var25 <= 64 - var17 && var22 < var16; var22++) {
                           int var26 = (int)(var23 >> var25 & var18);
                           int var27 = var22 & 15;
                           int var28 = var20 + (var22 >> 8);
                           int var29 = var22 >> 4 & 15;
                           if (!(var26 == var10 | var26 == var11) & var26 != var12 & var26 != var13) {
                              Octree.setBlock(var1, var27, var28, var29, true);
                           }

                           var25 += var17;
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var30) {
         var30.printStackTrace();
         throw new RuntimeException(var30);
      }
   }

   public static boolean isSupported() {
      return NetherPathfinder.isThisSystemSupported();
   }

   static {
      try {
         Field var0 = Unsafe.class.getDeclaredField("theUnsafe");
         var0.setAccessible(true);
         UNSAFE = (Unsafe)var0.get(null);
      } catch (Exception var1) {
         throw new RuntimeException(var1);
      }

      AIR_BLOCK_STATE = Blocks.AIR.defaultBlockState();
   }

   public static final class Visibility {
      private Visibility() {
      }
   }
}
