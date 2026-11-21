package baritone.command.defaults;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.process.ICustomGoalProcess;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PathCommand extends Command {
   public PathCommand(IBaritone var1) {
      super(var1, "path");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandTooManyArgumentsException {
      ICustomGoalProcess var3 = this.baritone.getCustomGoalProcess();
      var2.requireMax(0);
      if (var3.getGoal() == null) {
         this.logDirect("No goal set");
      } else {
         BaritoneAPI.getProvider().getWorldScanner().repack(this.ctx);
         var3.path();
         this.logDirect("Now pathing");
      }
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Start heading towards the goal";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList("The path command tells Baritone to head towards the current goal.", "", "Usage:", "> path - Start the pathing.");
   }
}
