package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.BetterBlockPos;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.level.block.AirBlock;

public class SurfaceCommand extends Command {
   protected SurfaceCommand(IBaritone var1) {
      super(var1, "surface", "top");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) {
      BetterBlockPos var3 = this.ctx.playerFeet();
      int var4 = this.ctx.world().getSeaLevel();
      int var5 = this.ctx.world().getHeight();
      if (var3.getY() > var4 && this.ctx.world().getBlockState(var3.above()).getBlock() instanceof AirBlock) {
         this.logDirect("Already at surface");
      } else {
         int var6 = Math.max(var3.getY(), var4);

         for (int var7 = var6; var7 < var5; var7++) {
            BetterBlockPos var8 = new BetterBlockPos(var3.getX(), var7, var3.getZ());
            if (!(this.ctx.world().getBlockState(var8).getBlock() instanceof AirBlock) && var8.getY() > var3.getY()) {
               GoalBlock var9 = new GoalBlock(var8.above());
               this.logDirect(String.format("Going to: %s", var9.toString()));
               this.baritone.getCustomGoalProcess().setGoalAndPath(var9);
               return;
            }
         }

         this.logDirect("No higher location found");
      }
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Used to get out of caves, mines, ...";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "The surface/top command tells Baritone to head towards the closest surface-like area.",
         "",
         "This can be the surface or the highest available air space, depending on circumstances.",
         "",
         "Usage:",
         "> surface - Used to get out of caves, mines, ...",
         "> top - Used to get out of caves, mines, ..."
      );
   }
}
