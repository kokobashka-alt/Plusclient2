package baritone.pathing.movement.movements;

import baritone.api.IBaritone;
import baritone.api.pathing.movement.MovementStatus;
import baritone.api.utils.BetterBlockPos;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.Movement;
import baritone.pathing.movement.MovementHelper;
import baritone.pathing.movement.MovementState;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MovementDownward extends Movement {
   private int numTicks = 0;

   public MovementDownward(IBaritone var1, BetterBlockPos var2, BetterBlockPos var3) {
      super(var1, var2, var3, new BetterBlockPos[]{var3});
   }

   @Override
   public void reset() {
      super.reset();
      this.numTicks = 0;
   }

   @Override
   public double calculateCost(CalculationContext var1) {
      return cost(var1, this.src.x, this.src.y, this.src.z);
   }

   @Override
   protected Set<BetterBlockPos> calculateValidPositions() {
      return ImmutableSet.of(this.src, this.dest);
   }

   public static double cost(CalculationContext var0, int var1, int var2, int var3) {
      if (!var0.allowDownward) {
         return 1000000.0;
      }

      if (!MovementHelper.canWalkOn(var0, var1, var2 - 2, var3)) {
         return 1000000.0;
      }

      BlockState var4 = var0.get(var1, var2 - 1, var3);
      Block var5 = var4.getBlock();
      return var5 != Blocks.LADDER && var5 != Blocks.VINE
         ? FALL_N_BLOCKS_COST[1] + MovementHelper.getMiningDurationTicks(var0, var1, var2 - 1, var3, var4, false)
         : 6.666666666666667;
   }

   @Override
   public MovementState updateState(MovementState var1) {
      super.updateState(var1);
      if (var1.getStatus() != MovementStatus.RUNNING) {
         return var1;
      }

      if (this.ctx.playerFeet().equals(this.dest)) {
         return var1.setStatus(MovementStatus.SUCCESS);
      }

      if (!this.playerInValidPosition()) {
         return var1.setStatus(MovementStatus.UNREACHABLE);
      }

      double var2 = this.ctx.player().position().x - (this.dest.getX() + 0.5);
      double var4 = this.ctx.player().position().z - (this.dest.getZ() + 0.5);
      double var6 = Math.sqrt(var2 * var2 + var4 * var4);
      if (this.numTicks++ < 10 && var6 < 0.2) {
         return var1;
      }

      MovementHelper.moveTowards(this.ctx, var1, this.positionsToBreak[0]);
      return var1;
   }
}
