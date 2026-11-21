package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.pathing.goals.GoalStrictDirection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class TunnelCommand extends Command {
   public TunnelCommand(IBaritone var1) {
      super(var1, "tunnel");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandTooManyArgumentsException {
      var2.requireMax(3);
      if (var2.hasExactly(3)) {
         boolean var3 = true;
         int var4 = Integer.parseInt(var2.getArgs().get(0).getValue());
         int var5 = Integer.parseInt(var2.getArgs().get(1).getValue());
         int var6 = Integer.parseInt(var2.getArgs().get(2).getValue());
         if (var5 < 1 || var4 < 2 || var6 < 1 || var4 > this.ctx.world().getMaxY()) {
            this.logDirect("Width and depth must at least be 1 block; Height must at least be 2 blocks, and cannot be greater than the build limit.");
            var3 = false;
         }

         if (var3) {
            var4--;
            var5--;
            Direction var9 = this.ctx.player().getDirection();
            int var10 = var5 % 2 == 0 ? 0 : 1;
            BlockPos var7;
            BlockPos var8;
            switch (var9) {
               case EAST:
                  var7 = new BlockPos(this.ctx.playerFeet().x, this.ctx.playerFeet().y, this.ctx.playerFeet().z - var5 / 2);
                  var8 = new BlockPos(this.ctx.playerFeet().x + var6, this.ctx.playerFeet().y + var4, this.ctx.playerFeet().z + var5 / 2 + var10);
                  break;
               case WEST:
                  var7 = new BlockPos(this.ctx.playerFeet().x, this.ctx.playerFeet().y, this.ctx.playerFeet().z + var5 / 2 + var10);
                  var8 = new BlockPos(this.ctx.playerFeet().x - var6, this.ctx.playerFeet().y + var4, this.ctx.playerFeet().z - var5 / 2);
                  break;
               case NORTH:
                  var7 = new BlockPos(this.ctx.playerFeet().x - var5 / 2, this.ctx.playerFeet().y, this.ctx.playerFeet().z);
                  var8 = new BlockPos(this.ctx.playerFeet().x + var5 / 2 + var10, this.ctx.playerFeet().y + var4, this.ctx.playerFeet().z - var6);
                  break;
               case SOUTH:
                  var7 = new BlockPos(this.ctx.playerFeet().x + var5 / 2 + var10, this.ctx.playerFeet().y, this.ctx.playerFeet().z);
                  var8 = new BlockPos(this.ctx.playerFeet().x - var5 / 2, this.ctx.playerFeet().y + var4, this.ctx.playerFeet().z + var6);
                  break;
               default:
                  throw new IllegalStateException("Unexpected value: " + var9);
            }

            this.logDirect(String.format("Creating a tunnel %s block(s) high, %s block(s) wide, and %s block(s) deep", var4 + 1, var5 + 1, var6));
            this.baritone.getBuilderProcess().clearArea(var7, var8);
         }
      } else {
         GoalStrictDirection var11 = new GoalStrictDirection(this.ctx.playerFeet(), this.ctx.player().getDirection());
         this.baritone.getCustomGoalProcess().setGoalAndPath(var11);
         this.logDirect(String.format("Goal: %s", var11.toString()));
      }
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Set a goal to tunnel in your current direction";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "The tunnel command sets a goal that tells Baritone to mine completely straight in the direction that you're facing.",
         "",
         "Usage:",
         "> tunnel - No arguments, mines in a 1x2 radius.",
         "> tunnel <height> <width> <depth> - Tunnels in a user defined height, width and depth."
      );
   }
}
