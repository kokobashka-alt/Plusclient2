package baritone.utils.pathing;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.pathing.calc.IPath;
import baritone.api.pathing.goals.Goal;
import baritone.pathing.path.CutoffPath;
import baritone.utils.BlockStateInterface;
import net.minecraft.core.BlockPos;

public abstract class PathBase implements IPath {
   public PathBase cutoffAtLoadedChunks(Object var1) {
      if (!Baritone.settings().cutoffAtLoadBoundary.value) {
         return this;
      }

      BlockStateInterface var2 = (BlockStateInterface)var1;

      for (int var3 = 0; var3 < this.positions().size(); var3++) {
         BlockPos var4 = this.positions().get(var3);
         if (!var2.worldContainsLoadedChunk(var4.getX(), var4.getZ())) {
            return new CutoffPath(this, var3);
         }
      }

      return this;
   }

   public PathBase staticCutoff(Goal var1) {
      int var2 = BaritoneAPI.getSettings().pathCutoffMinimumLength.value;
      if (this.length() < var2) {
         return this;
      } else if (var1 != null && !var1.isInGoal(this.getDest())) {
         double var3 = BaritoneAPI.getSettings().pathCutoffFactor.value;
         int var5 = (int)((this.length() - var2) * var3) + var2 - 1;
         return new CutoffPath(this, var5);
      } else {
         return this;
      }
   }
}
