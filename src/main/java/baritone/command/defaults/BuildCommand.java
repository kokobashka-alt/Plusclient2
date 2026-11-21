package baritone.command.defaults;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.RelativeBlockPos;
import baritone.api.command.datatypes.RelativeFile;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.utils.BetterBlockPos;
import baritone.utils.schematic.SchematicSystem;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;

public class BuildCommand extends Command {
   private final File schematicsDir;

   public BuildCommand(IBaritone var1) {
      super(var1, "build");
      this.schematicsDir = new File(var1.getPlayerContext().minecraft().gameDirectory, "schematics");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidStateException, baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      File var3 = var2.getDatatypePost(RelativeFile.INSTANCE, this.schematicsDir).getAbsoluteFile();
      File var4 = var3;
      if (FilenameUtils.getExtension(var4.getAbsolutePath()).isEmpty()) {
         var4 = new File(var4.getAbsolutePath() + "." + Baritone.settings().schematicFallbackExtension.value);
      }

      if (!var4.exists()) {
         if (var3.exists()) {
            throw new CommandInvalidStateException(
               String.format(
                  "Cannot load %s because I do not know which schematic format that is. Please rename the file to include the correct file extension.", var4
               )
            );
         } else {
            throw new CommandInvalidStateException("Cannot find " + var4);
         }
      } else {
         if (!SchematicSystem.INSTANCE.getByFile(var4).isPresent()) {
            StringJoiner var8 = new StringJoiner(", ");
            SchematicSystem.INSTANCE.getFileExtensions().forEach(var8::add);
            throw new CommandInvalidStateException(String.format("Unsupported schematic format. Reckognized file extensions are: %s", var8));
         }

         BetterBlockPos var5 = this.ctx.playerFeet();
         BetterBlockPos var6;
         if (var2.hasAny()) {
            var2.requireMax(3);
            var6 = var2.getDatatypePost(RelativeBlockPos.INSTANCE, var5);
         } else {
            var2.requireMax(0);
            var6 = var5;
         }

         boolean var7 = this.baritone.getBuilderProcess().build(var4.getName(), var4, var6);
         if (!var7) {
            throw new CommandInvalidStateException("Couldn't load the schematic. Either your schematic is corrupt or this is a bug.");
         }

         this.logDirect(String.format("Successfully loaded schematic for building\nOrigin: %s", var6));
      }
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      if (var2.hasExactlyOne()) {
         return RelativeFile.tabComplete(var2, this.schematicsDir);
      } else if (var2.has(2)) {
         var2.get();
         return var2.tabCompleteDatatype(RelativeBlockPos.INSTANCE);
      } else {
         return Stream.empty();
      }
   }

   @Override
   public String getShortDesc() {
      return "Build a schematic";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "Build a schematic from a file.",
         "",
         "Usage:",
         "> build <filename> - Loads and builds '<filename>.schematic'",
         "> build <filename> <x> <y> <z> - Custom position"
      );
   }
}
