package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SchematicaCommand extends Command {
   public SchematicaCommand(IBaritone var1) {
      super(var1, "schematica");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandTooManyArgumentsException {
      var2.requireMax(0);
      this.baritone.getBuilderProcess().buildOpenSchematic();
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
      return Arrays.asList("Builds the schematic currently open in Schematica.", "", "Usage:", "> schematica");
   }
}
