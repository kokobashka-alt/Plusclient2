package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.RelativeGoalXZ;
import baritone.api.pathing.goals.GoalXZ;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ExploreCommand extends Command {
   public ExploreCommand(IBaritone var1) {
      super(var1, "explore");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      if (var2.hasAny()) {
         var2.requireExactly(2);
      } else {
         var2.requireMax(0);
      }

      GoalXZ var3 = var2.hasAny() ? var2.getDatatypePost(RelativeGoalXZ.INSTANCE, this.ctx.playerFeet()) : new GoalXZ(this.ctx.playerFeet());
      this.baritone.getExploreProcess().explore(var3.getX(), var3.getZ());
      this.logDirect(String.format("Exploring from %s", var3.toString()));
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return var2.hasAtMost(2) ? var2.tabCompleteDatatype(RelativeGoalXZ.INSTANCE) : Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Explore things";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "Tell Baritone to explore randomly. If you used explorefilter before this, it will be applied.",
         "",
         "Usage:",
         "> explore - Explore from your current position.",
         "> explore <x> <z> - Explore from the specified X and Z position."
      );
   }
}
