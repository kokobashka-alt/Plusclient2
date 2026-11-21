package baritone.pathing.calc;

import baritone.Baritone;
import baritone.api.pathing.calc.IPath;
import baritone.api.pathing.calc.IPathFinder;
import baritone.api.pathing.goals.Goal;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.Helper;
import baritone.api.utils.PathCalculationResult;
import baritone.pathing.movement.CalculationContext;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Optional;

public abstract class AbstractNodeCostSearch implements IPathFinder, Helper {
   protected final BetterBlockPos realStart;
   protected final int startX;
   protected final int startY;
   protected final int startZ;
   protected final Goal goal;
   private final CalculationContext context;
   private final Long2ObjectOpenHashMap<PathNode> map;
   protected PathNode startNode;
   protected PathNode mostRecentConsidered;
   protected final PathNode[] bestSoFar;
   private volatile boolean isFinished;
   protected boolean cancelRequested;
   protected static final double[] COEFFICIENTS = new double[]{
      Double.longBitsToDouble(4609434218613702656L),
      Double.longBitsToDouble(4611686018427387904L),
      Double.longBitsToDouble(4612811918334230528L),
      Double.longBitsToDouble(4613937818241073152L),
      Double.longBitsToDouble(4616189618054758400L),
      Double.longBitsToDouble(4617315517961601024L),
      Double.longBitsToDouble(4621819117588971520L)
   };

   AbstractNodeCostSearch(BetterBlockPos var1, int var2, int var3, int var4, Goal var5, CalculationContext var6) {
      this.bestSoFar = new PathNode[COEFFICIENTS.length];
      this.realStart = var1;
      this.startX = var2;
      this.startY = var3;
      this.startZ = var4;
      this.goal = var5;
      this.context = var6;
      this.map = new Long2ObjectOpenHashMap(Baritone.settings().pathingMapDefaultSize.value, Baritone.settings().pathingMapLoadFactor.value);
   }

   public void cancel() {
      this.cancelRequested = true;
   }

   @Override
   public synchronized PathCalculationResult calculate(long var1, long var3) {
      if (this.isFinished) {
         throw new IllegalStateException("Path finder cannot be reused!");
      }

      this.cancelRequested = false;

      try {
         IPath var5 = this.calculate0(var1, var3).map(IPath::postProcess).orElse(null);
         if (this.cancelRequested) {
            return new PathCalculationResult(PathCalculationResult.Type.CANCELLATION);
         }

         if (var5 == null) {
            return new PathCalculationResult(PathCalculationResult.Type.FAILURE);
         }

         int var6 = var5.length();
         var5 = var5.cutoffAtLoadedChunks(this.context.bsi);
         if (var5.length() < var6) {
            Helper.HELPER.logDebug("Cutting off path at edge of loaded chunks");
            Helper.HELPER.logDebug("Length decreased by " + (var6 - var5.length()));
         } else {
            Helper.HELPER.logDebug("Path ends within loaded chunks");
         }

         var6 = var5.length();
         var5 = var5.staticCutoff(this.goal);
         if (var5.length() < var6) {
            Helper.HELPER.logDebug("Static cutoff " + var6 + " to " + var5.length());
         }

         return this.goal.isInGoal(var5.getDest())
            ? new PathCalculationResult(PathCalculationResult.Type.SUCCESS_TO_GOAL, var5)
            : new PathCalculationResult(PathCalculationResult.Type.SUCCESS_SEGMENT, var5);
      } catch (Exception var11) {
         Helper.HELPER.logDirect("Pathing exception: " + var11);
         var11.printStackTrace();
         return new PathCalculationResult(PathCalculationResult.Type.EXCEPTION);
      } finally {
         this.isFinished = true;
      }
   }

   protected abstract Optional<IPath> calculate0(long var1, long var3);

   protected double getDistFromStartSq(PathNode var1) {
      int var2 = var1.x - this.startX;
      int var3 = var1.y - this.startY;
      int var4 = var1.z - this.startZ;
      return var2 * var2 + var3 * var3 + var4 * var4;
   }

   protected PathNode getNodeAtPosition(int var1, int var2, int var3, long var4) {
      PathNode var6 = (PathNode)this.map.get(var4);
      if (var6 == null) {
         var6 = new PathNode(var1, var2, var3, this.goal);
         this.map.put(var4, var6);
      }

      return var6;
   }

   @Override
   public Optional<IPath> pathToMostRecentNodeConsidered() {
      return Optional.ofNullable(this.mostRecentConsidered).map(var1 -> new Path(this.realStart, this.startNode, var1, 0, this.goal, this.context));
   }

   @Override
   public Optional<IPath> bestPathSoFar() {
      return this.bestSoFar(false, 0);
   }

   protected Optional<IPath> bestSoFar(boolean var1, int var2) {
      if (this.startNode == null) {
         return Optional.empty();
      }

      double var3 = 0.0;

      for (int var5 = 0; var5 < COEFFICIENTS.length; var5++) {
         if (this.bestSoFar[var5] != null) {
            double var6 = this.getDistFromStartSq(this.bestSoFar[var5]);
            if (var6 > var3) {
               var3 = var6;
            }

            if (var6 > 25.0) {
               if (var1) {
                  if (COEFFICIENTS[var5] >= 3.0) {
                     System.out.println("Warning: cost coefficient is greater than three! Probably means that");
                     System.out.println("the path I found is pretty terrible (like sneak-bridging for dozens of blocks)");
                     System.out.println("But I'm going to do it anyway, because yolo");
                  }

                  System.out.println("Path goes for " + Math.sqrt(var6) + " blocks");
                  this.logDebug("A* cost coefficient " + COEFFICIENTS[var5]);
               }

               return Optional.of(new Path(this.realStart, this.startNode, this.bestSoFar[var5], var2, this.goal, this.context));
            }
         }
      }

      if (var1) {
         this.logDebug("Even with a cost coefficient of " + COEFFICIENTS[COEFFICIENTS.length - 1] + ", I couldn't get more than " + Math.sqrt(var3) + " blocks");
         this.logDebug("No path found =(");
         this.logNotification("No path found =(", true);
      }

      return Optional.empty();
   }

   @Override
   public final boolean isFinished() {
      return this.isFinished;
   }

   @Override
   public final Goal getGoal() {
      return this.goal;
   }

   public BetterBlockPos getStart() {
      return new BetterBlockPos(this.startX, this.startY, this.startZ);
   }

   protected int mapSize() {
      return this.map.size();
   }
}
