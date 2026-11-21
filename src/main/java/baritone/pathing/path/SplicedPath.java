package baritone.pathing.path;

import baritone.api.pathing.calc.IPath;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.movement.IMovement;
import baritone.api.utils.BetterBlockPos;
import baritone.utils.pathing.PathBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class SplicedPath extends PathBase {
   private final List<BetterBlockPos> path;
   private final List<IMovement> movements;
   private final int numNodes;
   private final Goal goal;

   private SplicedPath(List<BetterBlockPos> var1, List<IMovement> var2, int var3, Goal var4) {
      this.path = var1;
      this.movements = var2;
      this.numNodes = var3;
      this.goal = var4;
      this.sanityCheck();
   }

   @Override
   public Goal getGoal() {
      return this.goal;
   }

   @Override
   public List<IMovement> movements() {
      return Collections.unmodifiableList(this.movements);
   }

   @Override
   public List<BetterBlockPos> positions() {
      return Collections.unmodifiableList(this.path);
   }

   @Override
   public int getNumNodesConsidered() {
      return this.numNodes;
   }

   @Override
   public int length() {
      return this.path.size();
   }

   public static Optional<SplicedPath> trySplice(IPath var0, IPath var1, boolean var2) {
      if (var1 != null && var0 != null) {
         if (!var0.getDest().equals(var1.getSrc())) {
            return Optional.empty();
         }

         HashSet var3 = new HashSet<>(var1.positions());
         int var4 = -1;

         for (int var5 = 0; var5 < var0.length() - 1; var5++) {
            if (var3.contains(var0.positions().get(var5))) {
               var4 = var5;
               break;
            }
         }

         if (var4 != -1) {
            if (!var2) {
               return Optional.empty();
            }
         } else {
            var4 = var0.length() - 1;
         }

         int var8 = var1.positions().indexOf(var0.positions().get(var4));
         if (!var2 && var8 != 0) {
            throw new IllegalStateException("Paths to be spliced are overlapping incorrectly");
         }

         ArrayList var6 = new ArrayList();
         ArrayList var7 = new ArrayList();
         var6.addAll(var0.positions().subList(0, var4 + 1));
         var7.addAll(var0.movements().subList(0, var4));
         var6.addAll(var1.positions().subList(var8 + 1, var1.length()));
         var7.addAll(var1.movements().subList(var8, var1.length() - 1));
         return Optional.of(new SplicedPath(var6, var7, var0.getNumNodesConsidered() + var1.getNumNodesConsidered(), var0.getGoal()));
      } else {
         return Optional.empty();
      }
   }
}
