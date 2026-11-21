package baritone.process;

import baritone.Baritone;
import baritone.api.cache.ICachedWorld;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalComposite;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.pathing.goals.GoalYLevel;
import baritone.api.process.IExploreProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.MyChunkPos;
import baritone.cache.CachedWorld;
import baritone.utils.BaritoneProcessHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class ExploreProcess extends BaritoneProcessHelper implements IExploreProcess {
   private BlockPos explorationOrigin;
   private ExploreProcess.IChunkFilter filter;
   private int distanceCompleted;

   public ExploreProcess(Baritone var1) {
      super(var1);
   }

   @Override
   public boolean isActive() {
      return this.explorationOrigin != null;
   }

   @Override
   public void explore(int var1, int var2) {
      this.explorationOrigin = new BlockPos(var1, 0, var2);
      this.distanceCompleted = 0;
   }

   @Override
   public void applyJsonFilter(Path var1, boolean var2) throws Exception {
      this.filter = new ExploreProcess.JsonChunkFilter(var1, var2);
   }

   public ExploreProcess.IChunkFilter calcFilter() {
      ExploreProcess.IChunkFilter var1;
      if (this.filter != null) {
         var1 = new ExploreProcess.EitherChunk(this.filter, new ExploreProcess.BaritoneChunkCache());
      } else {
         var1 = new ExploreProcess.BaritoneChunkCache();
      }

      return var1;
   }

   @Override
   public PathingCommand onTick(boolean var1, boolean var2) {
      if (var1) {
         this.logDirect("Failed");
         if (Baritone.settings().notificationOnExploreFinished.value) {
            this.logNotification("Exploration failed", true);
         }

         this.onLostControl();
         return null;
      } else {
         ExploreProcess.IChunkFilter var3 = this.calcFilter();
         if (!Baritone.settings().disableCompletionCheck.value && var3.countRemain() == 0) {
            this.logDirect("Explored all chunks");
            if (Baritone.settings().notificationOnExploreFinished.value) {
               this.logNotification("Explored all chunks", false);
            }

            this.onLostControl();
            return null;
         } else {
            Goal[] var4 = this.closestUncachedChunks(this.explorationOrigin, var3);
            if (var4 == null) {
               this.logDebug("awaiting region load from disk");
               return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
            } else {
               return new PathingCommand(new GoalComposite(var4), PathingCommandType.FORCE_REVALIDATE_GOAL_AND_PATH);
            }
         }
      }
   }

   private Goal[] closestUncachedChunks(BlockPos var1, ExploreProcess.IChunkFilter var2) {
      int var3 = var1.getX() >> 4;
      int var4 = var1.getZ() >> 4;
      int var5 = Math.min(var2.countRemain(), Baritone.settings().exploreChunkSetMinimumSize.value);
      ArrayList<net.minecraft.core.BlockPos> var6 = new ArrayList<>();
      int var7 = Baritone.settings().worldExploringChunkOffset.value;
      int var8 = this.distanceCompleted;

      while (true) {
         for (int var9 = -var8; var9 <= var8; var9++) {
            int var10 = var8 - Math.abs(var9);
            int var11 = 0;

            while (var11 < 2) {
               int var12 = (var11 * 2 - 1) * var10;
               int var13 = Math.abs(var9) + Math.abs(var12);
               if (var13 != var8) {
                  throw new IllegalStateException(String.format("Offset %s %s has distance %s, expected %s", var9, var12, var13, var8));
               }

               switch (var2.isAlreadyExplored(var3 + var9, var4 + var12)) {
                  case NOT_EXPLORED:
                  default:
                     int var14 = (var3 + var9 << 4) + 8;
                     int var15 = (var4 + var12 << 4) + 8;
                     int var16 = var7 << 4;
                     if (var9 < 0) {
                        var14 -= var16;
                     } else {
                        var14 += var16;
                     }

                     if (var12 < 0) {
                        var15 -= var16;
                     } else {
                        var15 += var16;
                     }

                     var6.add(new BlockPos(var14, 0, var15));
                  case EXPLORED:
                     var11++;
                     break;
                  case UNKNOWN:
                     return null;
               }
            }
         }

         if (var8 % 10 == 0) {
            var5 = Math.min(var2.countRemain(), Baritone.settings().exploreChunkSetMinimumSize.value);
         }

         if (var6.size() >= var5) {
            return var6.stream().map(var0 -> createGoal(var0.getX(), var0.getZ())).toArray(Goal[]::new);
         }

         if (var6.isEmpty()) {
            this.distanceCompleted = var8 + 1;
         }

         var8++;
      }
   }

   private static Goal createGoal(int var0, int var1) {
      return Baritone.settings().exploreMaintainY.value == -1 ? new GoalXZ(var0, var1) : new GoalXZ(var0, var1) {
         @Override
         public double heuristic(int var1, int var2, int var3) {
            return super.heuristic(var1, var2, var3) + GoalYLevel.calculate(Baritone.settings().exploreMaintainY.value, var2);
         }
      };
   }

   @Override
   public void onLostControl() {
      this.explorationOrigin = null;
   }

   @Override
   public String displayName0() {
      return "Exploring around "
         + this.explorationOrigin
         + ", distance completed "
         + this.distanceCompleted
         + ", currently going to "
         + new GoalComposite(this.closestUncachedChunks(this.explorationOrigin, this.calcFilter()));
   }

   private class BaritoneChunkCache implements ExploreProcess.IChunkFilter {
      private final ICachedWorld cache = ExploreProcess.this.baritone.getWorldProvider().getCurrentWorld().getCachedWorld();

      @Override
      public ExploreProcess.Status isAlreadyExplored(int var1, int var2) {
         int var3 = var1 << 4;
         int var4 = var2 << 4;
         if (this.cache.isCached(var3, var4)) {
            return ExploreProcess.Status.EXPLORED;
         } else if (!((CachedWorld)this.cache).regionLoaded(var3, var4)) {
            Baritone.getExecutor().execute(() -> ((CachedWorld)this.cache).tryLoadFromDisk(var3 >> 9, var4 >> 9));
            return ExploreProcess.Status.UNKNOWN;
         } else {
            return ExploreProcess.Status.NOT_EXPLORED;
         }
      }

      @Override
      public int countRemain() {
         return Integer.MAX_VALUE;
      }
   }

   private class EitherChunk implements ExploreProcess.IChunkFilter {
      private final ExploreProcess.IChunkFilter a;
      private final ExploreProcess.IChunkFilter b;

      private EitherChunk(ExploreProcess.IChunkFilter var2, ExploreProcess.IChunkFilter var3) {
         this.a = var2;
         this.b = var3;
      }

      @Override
      public ExploreProcess.Status isAlreadyExplored(int var1, int var2) {
         return this.a.isAlreadyExplored(var1, var2) == ExploreProcess.Status.EXPLORED ? ExploreProcess.Status.EXPLORED : this.b.isAlreadyExplored(var1, var2);
      }

      @Override
      public int countRemain() {
         return Math.min(this.a.countRemain(), this.b.countRemain());
      }
   }

   private interface IChunkFilter {
      ExploreProcess.Status isAlreadyExplored(int var1, int var2);

      int countRemain();
   }

   private class JsonChunkFilter implements ExploreProcess.IChunkFilter {
      private final boolean invert;
      private final LongOpenHashSet inFilter;
      private final MyChunkPos[] positions;

      private JsonChunkFilter(Path var2, boolean var3) throws Exception {
         this.invert = var3;
         Gson var4 = new GsonBuilder().create();
         this.positions = (MyChunkPos[])var4.fromJson(new InputStreamReader(Files.newInputStream(var2)), MyChunkPos[].class);
         ExploreProcess.this.logDirect("Loaded " + this.positions.length + " positions");
         this.inFilter = new LongOpenHashSet();

         for (MyChunkPos var8 : this.positions) {
            this.inFilter.add(ChunkPos.pack(var8.x, var8.z));
         }
      }

      @Override
      public ExploreProcess.Status isAlreadyExplored(int var1, int var2) {
         return this.inFilter.contains(ChunkPos.pack(var1, var2)) ^ this.invert ? ExploreProcess.Status.EXPLORED : ExploreProcess.Status.UNKNOWN;
      }

      @Override
      public int countRemain() {
         if (!this.invert) {
            return Integer.MAX_VALUE;
         }

         int var1 = 0;
         ExploreProcess.BaritoneChunkCache var2 = ExploreProcess.this.new BaritoneChunkCache();

         for (MyChunkPos var6 : this.positions) {
            if (var2.isAlreadyExplored(var6.x, var6.z) != ExploreProcess.Status.EXPLORED) {
               if (++var1 >= Baritone.settings().exploreChunkSetMinimumSize.value) {
                  return var1;
               }
            }
         }

         return var1;
      }
   }

   private enum Status {
      EXPLORED,
      NOT_EXPLORED,
      UNKNOWN;
   }
}
