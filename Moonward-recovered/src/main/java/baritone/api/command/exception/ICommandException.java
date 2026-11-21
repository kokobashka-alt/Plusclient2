package baritone.api.command.exception;

import baritone.api.command.ICommand;
import baritone.api.command.argument.ICommandArgument;
import baritone.api.utils.Helper;
import java.util.List;
import net.minecraft.ChatFormatting;

public interface ICommandException {
   String getMessage();

   default void handle(ICommand var1, List<ICommandArgument> var2) {
      Helper.HELPER.logDirect(this.getMessage(), ChatFormatting.RED);
   }
}
