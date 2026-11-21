package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.ICommand;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.exception.CommandNotFoundException;
import baritone.api.command.helpers.Paginator;
import baritone.api.command.helpers.TabCompleteHelper;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent.RunCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.MutableComponent;

public class HelpCommand extends Command {
   public HelpCommand(IBaritone var1) {
      super(var1, "help", "?");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandNotFoundException, baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      var2.requireMax(1);
      if (var2.hasAny() && !var2.is(Integer.class)) {
         String var3 = var2.getString().toLowerCase();
         ICommand var4 = this.baritone.getCommandManager().getCommand(var3);
         if (var4 == null) {
            throw new CommandNotFoundException(var3);
         }

         this.logDirect(String.format("%s - %s", String.join(" / ", var4.getNames()), var4.getShortDesc()));
         this.logDirect("");
         var4.getLongDesc().forEach(this::logDirect);
         this.logDirect("");
         MutableComponent var5 = Component.literal("Click to return to the help menu");
         var5.setStyle(var5.getStyle().withClickEvent(new RunCommand(FORCE_COMMAND_PREFIX + var1)));
         this.logDirect(var5);
      } else {
         Paginator.paginate(
            var2,
            new Paginator<>(
               this.baritone.getCommandManager().getRegistry().descendingStream().filter(var0 -> !var0.hiddenFromHelp()).collect(Collectors.toList())
            ),
            () -> this.logDirect("All Baritone commands (clickable):"),
            var1x -> {
               String var2x = String.join("/", var1x.getNames());
               String var3x = var1x.getNames().get(0);
               MutableComponent var4x = Component.literal(" - " + var1x.getShortDesc());
               var4x.setStyle(var4x.getStyle().withColor(ChatFormatting.DARK_GRAY));
               MutableComponent var5x = Component.literal(var2x);
               var5x.setStyle(var5x.getStyle().withColor(ChatFormatting.WHITE));
               MutableComponent var6 = Component.literal("");
               var6.setStyle(var6.getStyle().withColor(ChatFormatting.GRAY));
               var6.append(var5x);
               var6.append("\n" + var1x.getShortDesc());
               var6.append("\n\nClick to view full help");
               String var7 = FORCE_COMMAND_PREFIX + String.format("%s %s", var1, var1x.getNames().get(0));
               MutableComponent var8 = Component.literal(var3x);
               var8.setStyle(var8.getStyle().withColor(ChatFormatting.GRAY));
               var8.append(var4x);
               var8.setStyle(var8.getStyle().withHoverEvent(new ShowText(var6)).withClickEvent(new RunCommand(var7)));
               return var8;
            },
            FORCE_COMMAND_PREFIX + var1
         );
      }
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return var2.hasExactlyOne()
         ? new TabCompleteHelper().addCommands(this.baritone.getCommandManager()).filterPrefix(var2.getString()).stream()
         : Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "View all commands or help on specific ones";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "Using this command, you can view detailed help information on how to use certain commands of Baritone.",
         "",
         "Usage:",
         "> help - Lists all commands and their short descriptions.",
         "> help <command> - Displays help information on a specific command."
      );
   }
}
