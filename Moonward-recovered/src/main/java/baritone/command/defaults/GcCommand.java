package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class GcCommand extends Command {
   public GcCommand(IBaritone var1) {
      super(var1, "gc");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandTooManyArgumentsException {
      var2.requireMax(0);
      System.gc();
      this.logDirect("ok called System.gc()");
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Call System.gc()";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList("Calls System.gc().", "", "Usage:", "> gc");
   }
}
