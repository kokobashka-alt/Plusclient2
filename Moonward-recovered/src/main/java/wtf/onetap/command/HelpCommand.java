package wtf.onetap.command;

import wtf.onetap.Onetap;
import wtf.onetap.util.ChatUtil;

public class HelpCommand implements Command {
   @Override
   public String getText2() {
      return "help";
   }

   @Override
   public String getText3() {
      return "Список команд";
   }

   @Override
   public String getUsage() {
      return "";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      ChatUtil.log("§7Список команд:");
      Onetap.COMMAND_MANAGER.getStringMap().values().forEach(command -> ChatUtil.log(" §7." + command.getText2() + " §7- §7" + command.getText3()));
   }
}
