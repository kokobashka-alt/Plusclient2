package baritone.pathing.calc;

import baritone.api.pathing.calc.IPath;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.movement.IMovement;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.Helper;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.Movement;
import baritone.pathing.movement.Moves;
import baritone.pathing.path.CutoffPath;
import baritone.utils.pathing.PathBase;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Path extends PathBase {
   private final BetterBlockPos start;
   private final BetterBlockPos end;
   private final List<BetterBlockPos> path;
   private final List<Movement> movements;
   private final List<PathNode> nodes;
   private final Goal goal;
   private final int numNodes;
   private final CalculationContext context;
   private volatile boolean verified;

   Path(BetterBlockPos var1, PathNode var2, PathNode var3, int var4, Goal var5, CalculationContext var6) {
      this.end = new BetterBlockPos(var3.x, var3.y, var3.z);
      this.numNodes = var4;
      this.movements = new ArrayList<>();
      this.goal = var5;
      this.context = var6;
      PathNode var7 = var3;
      ArrayList var8 = new ArrayList();
      ArrayList var9 = new ArrayList();

      while (var7 != null) {
         var9.add(var7);
         var8.add(new BetterBlockPos(var7.x, var7.y, var7.z));
         var7 = var7.previous;
      }

      BetterBlockPos var10 = new BetterBlockPos(var2.x, var2.y, var2.z);
      if (!var1.equals(var10) && var2.equals(var3)) {
         this.start = var1;
         PathNode var11 = new PathNode(var1.x, var1.y, var1.z, var5);
         var11.cost = 0.0;
         var9.add(var11);
         var8.add(var1);
      } else {
         this.start = var10;
      }

      this.path = Lists.reverse(var8);
      this.nodes = Lists.reverse(var9);
   }

   @Override
   public Goal getGoal() {
      return this.goal;
   }

   private boolean assembleMovements() {
      if (!this.path.isEmpty() && this.movements.isEmpty()) {
         for (int var1 = 0; var1 < this.path.size() - 1; var1++) {
            double var2 = this.nodes.get(var1 + 1).cost - this.nodes.get(var1).cost;
            Movement var4 = this.runBackwards(this.path.get(var1), this.path.get(var1 + 1), var2);
            if (var4 == null) {
               return true;
            }

            this.movements.add(var4);
         }

         return false;
      } else {
         throw new IllegalStateException("Path must not be empty");
      }
   }

   private Movement runBackwards(BetterBlockPos var1, BetterBlockPos var2, double var3) {
      for (Moves var8 : Moves.values()) {
         Movement var9 = var8.apply0(this.context, var1);
         if (var9.getDest().equals(var2)) {
            var9.override(Math.min(var9.calculateCost(this.context), var3));
            return var9;
         }
      }

      Helper.HELPER.logDebug("Movement became impossible during calculation " + var1 + " " + var2 + " " + var2.subtract(var1));
      return null;
   }

   @Override
   public IPath postProcess() {
      if (this.verified) {
         throw new IllegalStateException("Path must not be verified twice");
      }

      this.verified = true;
      boolean var1 = this.assembleMovements();
      this.movements.forEach(var1x -> var1x.checkLoadedChunk(this.context));
      if (var1) {
         CutoffPath var2 = new CutoffPath(this, this.movements().size());
         if (var2.movements().size() != this.movements.size()) {
            throw new IllegalStateException("Path has wrong size after cutoff");
         } else {
            return var2;
         }
      } else {
         this.sanityCheck();
         return this;
      }
   }

   @Override
   public List<IMovement> movements() {
      if (!this.verified) {
         throw new IllegalStateException("Path not yet verified");
      } else {
         return Collections.unmodifiableList(this.movements);
      }
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
   public BetterBlockPos getSrc() {
      return this.start;
   }

   @Override
   public BetterBlockPos getDest() {
      return this.end;
   }
}
