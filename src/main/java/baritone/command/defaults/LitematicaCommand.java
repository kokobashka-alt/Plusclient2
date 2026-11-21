package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class LitematicaCommand extends Command {
   public LitematicaCommand(IBaritone var1) {
      super(var1, "litematica");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandInvalidTypeException {
      var2.requireMax(1);
      int var3 = var2.hasAny() ? var2.getAs(Integer.class) - 1 : 0;
      this.baritone.getBuilderProcess().buildOpenLitematic(var3);
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Builds the loaded schematic";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList("Build a schematic currently open in Litematica.", "", "Usage:", "> litematica", "> litematica <#>");
   }
}
