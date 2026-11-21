package baritone.pathing.calc;

import baritone.Baritone;
import baritone.api.pathing.calc.IPath;
import baritone.api.pathing.goals.Goal;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.SettingsUtil;
import baritone.pathing.calc.openset.BinaryHeapOpenSet;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.Moves;
import baritone.utils.pathing.BetterWorldBorder;
import baritone.utils.pathing.Favoring;
import baritone.utils.pathing.MutableMoveResult;
import java.util.Optional;

public class AStarPathFinder extends AbstractNodeCostSearch {
   private final Favoring favoring;
   private final CalculationContext calcContext;

   public AStarPathFinder(BetterBlockPos var1, int var2, int var3, int var4, Goal var5, Favoring var6, CalculationContext var7) {
      super(var1, var2, var3, var4, var5, var7);
      this.favoring = var6;
      this.calcContext = var7;
   }

   @Override
   protected Optional<IPath> calculate0(long var1, long var3) {
      int var5 = this.calcContext.world.dimensionType().minY();
      int var6 = this.calcContext.world.dimensionType().height();
      this.startNode = this.getNodeAtPosition(this.startX, this.startY, this.startZ, BetterBlockPos.longHash(this.startX, this.startY, this.startZ));
      this.startNode.cost = 0.0;
      this.startNode.combinedCost = this.startNode.estimatedCostToGoal;
      BinaryHeapOpenSet var7 = new BinaryHeapOpenSet();
      var7.insert(this.startNode);
      double[] var8 = new double[COEFFICIENTS.length];

      for (int var9 = 0; var9 < var8.length; var9++) {
         var8[var9] = this.startNode.estimatedCostToGoal;
         this.bestSoFar[var9] = this.startNode;
      }

      MutableMoveResult var46 = new MutableMoveResult();
      BetterWorldBorder var10 = new BetterWorldBorder(this.calcContext.world.getWorldBorder());
      long var11 = System.currentTimeMillis();
      boolean var13 = Baritone.settings().slowPath.value;
      if (var13) {
         this.logDebug("slowPath is on, path timeout will be " + Baritone.settings().slowPathTimeoutMS.value + "ms instead of " + var1 + "ms");
      }

      long var14 = var11 + (var13 ? Baritone.settings().slowPathTimeoutMS.value : var1);
      long var16 = var11 + (var13 ? Baritone.settings().slowPathTimeoutMS.value : var3);
      boolean var18 = true;
      int var19 = 0;
      int var20 = 0;
      int var21 = 0;
      boolean var22 = !this.favoring.isEmpty();
      byte var23 = 64;
      int var24 = Baritone.settings().pathingMaxChunkBorderFetch.value;
      double var25 = Baritone.settings().minimumImprovementRepropagation.value ? 0.01 : 0.0;
      Moves[] var27 = Moves.values();

      while (!var7.isEmpty() && var21 < var24 && !this.cancelRequested) {
         if ((var19 & var23 - 1) == 0) {
            long var28 = System.currentTimeMillis();
            if (var28 - var16 >= 0L || !var18 && var28 - var14 >= 0L) {
               break;
            }
         }

         if (var13) {
            try {
               Thread.sleep(Baritone.settings().slowPathTimeDelayMS.value);
            } catch (InterruptedException var45) {
            }
         }

         PathNode var47 = var7.removeLowest();
         this.mostRecentConsidered = var47;
         var19++;
         if (this.goal.isInGoal(var47.x, var47.y, var47.z)) {
            this.logDebug("Took " + (System.currentTimeMillis() - var11) + "ms, " + var20 + " movements considered");
            return Optional.of(new Path(this.realStart, this.startNode, var47, var19, this.goal, this.calcContext));
         }

         for (Moves var32 : var27) {
            int var33 = var47.x + var32.xOffset;
            int var34 = var47.z + var32.zOffset;
            if ((var33 >> 4 != var47.x >> 4 || var34 >> 4 != var47.z >> 4) && !this.calcContext.isLoaded(var33, var34)) {
               if (!var32.dynamicXZ) {
                  var21++;
               }
            } else if ((var32.dynamicXZ || var10.entirelyContains(var33, var34)) && var47.y + var32.yOffset <= var6 && var47.y + var32.yOffset >= var5) {
               var46.reset();
               var32.apply(this.calcContext, var47.x, var47.y, var47.z, var46);
               var20++;
               double var35 = var46.cost;
               if (!(var35 >= 1000000.0)) {
                  if (var35 <= 0.0 || Double.isNaN(var35)) {
                     throw new IllegalStateException(
                        String.format(
                           "%s from %s %s %s calculated implausible cost %s",
                           var32,
                           SettingsUtil.maybeCensor(var47.x),
                           SettingsUtil.maybeCensor(var47.y),
                           SettingsUtil.maybeCensor(var47.z),
                           var35
                        )
                     );
                  }

                  if (!var32.dynamicXZ || var10.entirelyContains(var46.x, var46.z)) {
                     if (!var32.dynamicXZ && (var46.x != var33 || var46.z != var34)) {
                        throw new IllegalStateException(
                           String.format(
                              "%s from %s %s %s ended at x z %s %s instead of %s %s",
                              var32,
                              SettingsUtil.maybeCensor(var47.x),
                              SettingsUtil.maybeCensor(var47.y),
                              SettingsUtil.maybeCensor(var47.z),
                              SettingsUtil.maybeCensor(var46.x),
                              SettingsUtil.maybeCensor(var46.z),
                              SettingsUtil.maybeCensor(var33),
                              SettingsUtil.maybeCensor(var34)
                           )
                        );
                     }

                     if (!var32.dynamicY && var46.y != var47.y + var32.yOffset) {
                        throw new IllegalStateException(
                           String.format(
                              "%s from %s %s %s ended at y %s instead of %s",
                              var32,
                              SettingsUtil.maybeCensor(var47.x),
                              SettingsUtil.maybeCensor(var47.y),
                              SettingsUtil.maybeCensor(var47.z),
                              SettingsUtil.maybeCensor(var46.y),
                              SettingsUtil.maybeCensor(var47.y + var32.yOffset)
                           )
                        );
                     }

                     long var37 = BetterBlockPos.longHash(var46.x, var46.y, var46.z);
                     if (var22) {
                        var35 *= this.favoring.calculate(var37);
                     }

                     PathNode var39 = this.getNodeAtPosition(var46.x, var46.y, var46.z, var37);
                     double var40 = var47.cost + var35;
                     if (var39.cost - var40 > var25) {
                        var39.previous = var47;
                        var39.cost = var40;
                        var39.combinedCost = var40 + var39.estimatedCostToGoal;
                        if (var39.isOpen()) {
                           var7.update(var39);
                        } else {
                           var7.insert(var39);
                        }

                        for (int var42 = 0; var42 < COEFFICIENTS.length; var42++) {
                           double var43 = var39.estimatedCostToGoal + var39.cost / COEFFICIENTS[var42];
                           if (var8[var42] - var43 > var25) {
                              var8[var42] = var43;
                              this.bestSoFar[var42] = var39;
                              if (var18 && this.getDistFromStartSq(var39) > 25.0) {
                                 var18 = false;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      if (this.cancelRequested) {
         return Optional.empty();
      }

      System.out.println(var20 + " movements considered");
      System.out.println("Open set size: " + var7.size());
      System.out.println("PathNode map size: " + this.mapSize());
      System.out.println((int)(var19 * 1.0 / ((float)(System.currentTimeMillis() - var11) / 1000.0F)) + " nodes per second");
      var var48 = this.bestSoFar(true, var19);
      if (var48.isPresent()) {
         this.logDebug("Took " + (System.currentTimeMillis() - var11) + "ms, " + var20 + " movements considered");
      }

      return var48;
   }
}
