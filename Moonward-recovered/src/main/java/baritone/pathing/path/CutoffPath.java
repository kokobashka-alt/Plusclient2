package baritone.pathing.path;

import baritone.api.pathing.calc.IPath;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.movement.IMovement;
import baritone.api.utils.BetterBlockPos;
import baritone.utils.pathing.PathBase;
import java.util.Collections;
import java.util.List;

public class CutoffPath extends PathBase {
   private final List<BetterBlockPos> path;
   private final List<IMovement> movements;
   private final int numNodes;
   private final Goal goal;

   public CutoffPath(IPath var1, int var2, int var3) {
      this.path = var1.positions().subList(var2, var3 + 1);
      this.movements = var1.movements().subList(var2, var3);
      this.numNodes = var1.getNumNodesConsidered();
      this.goal = var1.getGoal();
      this.sanityCheck();
   }

   public CutoffPath(IPath var1, int var2) {
      this(var1, 0, var2);
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
}
