package baritone.process.elytra;

import baritone.Baritone;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.Tuple;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public class BuildLimitPathFinder implements IElytraPathFinder {
   final int flightLevel;
   final IPlayerContext playerCtx;
   final NetherPathfinderContext netherCtx;

   public BuildLimitPathFinder(IPlayerContext var1, NetherPathfinderContext var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("IPlayerContext cannot be null");
      }

      this.playerCtx = var1;
      if (var2 == null) {
         throw new IllegalArgumentException("NetherPathfinderContext cannot be null");
      }

      this.flightLevel = var1.world().getMaxY() + 16;
      this.netherCtx = var2;
      if (var2.getMaxHeight() + var1.world().getMinY() < var1.world().getMaxY()) {
         throw new IllegalStateException("Nether pathfinder max height is below world build limit, cannot proceed");
      }
   }

   public Tuple<List<BetterBlockPos>, Boolean> generateDirectPath(BetterBlockPos var1, BetterBlockPos var2, int var3, int var4) {
      LinkedList var5 = new LinkedList();
      BetterBlockPos var7 = var1.y == this.flightLevel ? var1 : new BetterBlockPos(var1.getX(), this.flightLevel, var1.getZ());
      BetterBlockPos var8 = var2.y == this.flightLevel ? var2 : new BetterBlockPos(var2.getX(), this.flightLevel, var2.getZ());
      BetterBlockPos var9 = var7;
      var5.add(var9);

      while (var5.size() < var4) {
         double var10 = var8.getX() - var9.getX();
         double var12 = var8.getZ() - var9.getZ();
         double var14 = Math.sqrt(var10 * var10 + var12 * var12);
         double var16 = var10 * var10 + var12 * var12;
         if (var16 <= var3 * var3) {
            return new Tuple<>(var5, true);
         }

         if (var14 <= 32.0) {
            var5.add(var8);
            return new Tuple<>(var5, true);
         }

         double var18 = 32.0 / var14;
         int var20 = (int)Math.round(var9.getX() + var10 * var18);
         int var21 = (int)Math.round(var9.getZ() + var12 * var18);
         var9 = new BetterBlockPos(var20, this.flightLevel, var21);
         var5.add(var9);
      }

      return new Tuple<>(var5, false);
   }

   public Tuple<List<BetterBlockPos>, Boolean> generateTransitionUp(BetterBlockPos var1, BetterBlockPos var2) {
      double var3 = var2.getX() - var1.getX();
      double var5 = var2.getZ() - var1.getZ();
      double var7 = Math.sqrt(var3 * var3 + var5 * var5);
      double var9 = 8.0 / var7;
      double var11 = var3 * var9;
      double var13 = var5 * var9;
      int var15 = this.netherCtx.getMaxHeight() + this.playerCtx.world().getMinY() - 1;
      ChunkPos var16 = new ChunkPos(var1.x >> 4, var1.z >> 4);
      if (!this.isSkyClear(var16, var1.y)) {
         return new Tuple<>(new LinkedList<>(), false);
      }

      LinkedList var17 = new LinkedList();
      BlockPos var18 = var16.getMiddleBlockPosition(var15 + 4);

      for (int var19 = 2; var19 <= 2; var19++) {
         BetterBlockPos var20 = new BetterBlockPos((int)(var18.getX() + var11 * var19), var15 + var19 * 8, (int)(var18.getZ() + var13 * var19));
         var17.add(var20);
      }

      return new Tuple<>(var17, true);
   }

   public Tuple<List<BetterBlockPos>, Boolean> generateTransitionDown(BetterBlockPos var1) {
      int var2 = this.netherCtx.getMaxHeight() + this.playerCtx.world().getMinY() - 1;
      ChunkPos var3 = new ChunkPos(var1.x >> 4, var1.z >> 4);
      LinkedList var4 = new LinkedList();
      if (!this.isSkyClear(new ChunkPos(var1.x >> 4, var1.z >> 4), var2 - 16)) {
         return new Tuple<>(new LinkedList<>(), false);
      }

      var4.add(new BetterBlockPos(var3.getMiddleBlockPosition(var2 - 8)));
      return new Tuple<>(var4, true);
   }

   public boolean isSkyClear(ChunkPos var1, int var2) {
      if (!this.playerCtx.world().getChunkSource().hasChunk(var1.x(), var1.z())) {
         return false;
      }

      for (int var3 = 0; var3 < 16; var3++) {
         for (int var4 = 0; var4 < 16; var4++) {
            BlockPos var5 = var1.getBlockAt(var3, var2, var4);
            int var6 = this.playerCtx.world().getHeight(Types.MOTION_BLOCKING, var5.getX(), var5.getZ());
            if (var6 > var2) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public CompletableFuture<UnpackedSegment> pathFindAsync(BlockPos var1, BlockPos var2) {
      int var3 = this.netherCtx.getMaxHeight() + this.playerCtx.world().getMinY() - 1;
      double var5 = Baritone.settings().elytraLongDistanceThreshold.value >= 32
         ? Baritone.settings().elytraLongDistanceThreshold.value.intValue()
         : Double.POSITIVE_INFINITY;
      double var7 = var1.distSqr(new Vec3i(var2.getX(), var1.getY(), var2.getZ()));
      boolean var9 = var7 > var5 * var5;
      boolean var10 = var1.getY() >= var3;
      boolean var11 = var2.getY() >= var3;
      if (var10 && var11) {
         var var27 = this.generateDirectPath(new BetterBlockPos(var1), new BetterBlockPos(var2), 0, 500);
         return CompletableFuture.completedFuture(new UnpackedSegment(((List)var27.getA()).stream(), (Boolean)var27.getB()));
      }

      if (var9) {
         if (var10) {
            var var26 = this.generateDirectPath(new BetterBlockPos(var1), new BetterBlockPos(var2), (int)var5, 500);
            return CompletableFuture.completedFuture(new UnpackedSegment(((List)var26.getA()).stream(), var11 ? (Boolean)var26.getB() : false));
         } else {
            var var24 = this.generateTransitionUp(new BetterBlockPos(var1), new BetterBlockPos(var2));
            List var29 = (List)var24.getA();
            Boolean var31 = (Boolean)var24.getB();
            if (var31) {
               var var34 = this.generateDirectPath((BetterBlockPos)var29.get(var29.size() - 1), new BetterBlockPos(var2), (int)var5, 500);
               var29.addAll((Collection)var34.getA());
               return CompletableFuture.completedFuture(new UnpackedSegment(var29.stream(), var11 ? (Boolean)var34.getB() : false));
            } else {
               double var25 = var2.getX() - var1.getX();
               double var32 = var2.getZ() - var1.getZ();
               double var35 = var5 / 2.0 / Math.sqrt(var25 * var25 + var32 * var32);
               double var18 = var25 * var35;
               double var20 = var32 * var35;
               BlockPos var22 = new BlockPos((int)(var1.getX() + var18), var3, (int)(var1.getZ() + var20));
               return this.incompletePathfind(var1, var22);
            }
         }
      } else if (var10) {
         var var23 = this.generateTransitionDown(new BetterBlockPos(var1));
         List var28 = (List)var23.getA();
         boolean var30 = (Boolean)var23.getB();
         if (!var30) {
            BetterBlockPos var33 = var7 > 32.0 ? new BetterBlockPos(var2) : new BetterBlockPos(var2.getX(), this.playerCtx.world().getMaxY(), var2.getZ());
            var var16 = this.generateDirectPath(new BetterBlockPos(var1), var33, 0, 2);
            return CompletableFuture.completedFuture(new UnpackedSegment(((List)var16.getA()).stream(), (Boolean)var16.getB()));
         } else {
            return CompletableFuture.supplyAsync(() -> {
               UnpackedSegment var3x = this.blockingPathFind((BlockPos)var28.get(var28.size() - 1), var2);
               var28.addAll(var3x.collect());
               return new UnpackedSegment(var28.stream(), var3x.isFinished());
            });
         }
      } else if (var11) {
         var var12 = this.generateTransitionUp(new BetterBlockPos(var1), new BetterBlockPos(var2));
         List var13 = (List)var12.getA();
         Boolean var14 = (Boolean)var12.getB();
         if (var14) {
            var var15 = this.generateDirectPath((BetterBlockPos)var13.get(var13.size() - 1), new BetterBlockPos(var2), 0, 500);
            var13.addAll((Collection)var15.getA());
            return CompletableFuture.completedFuture(new UnpackedSegment(var13.stream(), (Boolean)var15.getB()));
         } else {
            return this.netherCtx.pathFindAsync(var1, new BetterBlockPos(var2.getX(), var3, var2.getZ()));
         }
      } else {
         return this.netherCtx.pathFindAsync(var1, var2);
      }
   }

   private CompletableFuture<UnpackedSegment> incompletePathfind(BlockPos var1, BlockPos var2) {
      return CompletableFuture.supplyAsync(() -> {
         UnpackedSegment var3 = this.blockingPathFind(var1, var2);
         return new UnpackedSegment(var3.collect().stream(), false);
      });
   }

   private UnpackedSegment blockingPathFind(BlockPos var1, BlockPos var2) {
      try {
         return this.netherCtx.pathFindAsync(var1, var2).get();
      } catch (InterruptedException var5) {
         Thread.currentThread().interrupt();
         throw new RuntimeException(var5);
      } catch (ExecutionException var6) {
         Throwable var4 = var6.getCause();
         if (var4 instanceof PathCalculationException) {
            throw (PathCalculationException)var4;
         } else {
            throw new RuntimeException(var6);
         }
      }
   }
}
