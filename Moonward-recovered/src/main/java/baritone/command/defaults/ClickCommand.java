package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ClickCommand extends Command {
   public ClickCommand(IBaritone var1) {
      super(var1, "click");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandTooManyArgumentsException {
      var2.requireMax(0);
      this.baritone.openClick();
      this.logDirect("aight dude");
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Open click";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList("Opens click dude", "", "Usage:", "> click");
   }
}
