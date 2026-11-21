package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class CommandAlias extends Command {
   private final String shortDesc;
   public final String target;

   public CommandAlias(IBaritone var1, List<String> var2, String var3, String var4) {
      super(var1, var2.toArray(new String[0]));
      this.shortDesc = var3;
      this.target = var4;
   }

   public CommandAlias(IBaritone var1, String var2, String var3, String var4) {
      super(var1, var2);
      this.shortDesc = var3;
      this.target = var4;
   }

   @Override
   public void execute(String var1, IArgConsumer var2) {
      this.baritone.getCommandManager().execute(String.format("%s %s", this.target, var2.rawRest()));
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return this.baritone.getCommandManager().tabComplete(String.format("%s %s", this.target, var2.rawRest()));
   }

   @Override
   public String getShortDesc() {
      return this.shortDesc;
   }

   @Override
   public List<String> getLongDesc() {
      return Collections.singletonList(String.format("This command is an alias, for: %s ...", this.target));
   }
}
