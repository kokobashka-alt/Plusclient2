package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.behavior.IPathingBehavior;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ForceCancelCommand extends Command {
   public ForceCancelCommand(IBaritone var1) {
      super(var1, "forcecancel");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandTooManyArgumentsException {
      var2.requireMax(0);
      IPathingBehavior var3 = this.baritone.getPathingBehavior();
      var3.cancelEverything();
      var3.forceCancel();
      this.logDirect("ok force canceled");
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Force cancel";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList("Like cancel, but more forceful.", "", "Usage:", "> forcecancel");
   }
}
