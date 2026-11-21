package wtf.onetap.command;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.Map.Entry;
import wtf.onetap.Onetap;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.KeyNames;

public class MacroCommand implements Command {
   @Override
   public String getText2() {
      return "macro";
   }

   @Override
   public String getText3() {
      return "Отправка сообщения по нажатию клавиши";
   }

   @Override
   public String getUsage() {
      return "list|add <клавиша> <команда>|remove <клавиша>|clear";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      if (commandHelp.getText2() == null) {
         ChatUtil.log("§cИспользование:");
         ChatUtil.log(" §7.macro list");
         ChatUtil.log(" §7.macro add <клавиша> <команда>");
         ChatUtil.log(" §7.macro remove <клавиша>");
         ChatUtil.log(" §7.macro clear");
      } else {
         switch (commandHelp.getText2().toLowerCase()) {
            case "list":
               if (Onetap.MACRO_MANAGER.isEmpty()) {
                  ChatUtil.log("§eНет активных макросов");
                  return;
               }

               ChatUtil.log("§7Список макросов:");

               for (Entry entry : Onetap.MACRO_MANAGER.getMacros().entrySet()) {
                  ChatUtil.log(" §7" + KeyNames.getKeyName((Integer)entry.getKey()) + " §7= §f" + (String)entry.getValue());
               }
               break;
            case "add":
               if (commandHelp.getStringList().size() < 2) {
                  ChatUtil.log("§cИспользование: §7.macro add <клавиша> <команда>");
                  return;
               }

               Key key = CommandManager.getCommand(commandHelp.getStringList().get(0));
               if (key == null) {
                  ChatUtil.log("§cНеизвестная клавиша: §7" + commandHelp.getStringList().get(0));
                  return;
               }

               String text = String.join(" ", commandHelp.getStringList().subList(1, commandHelp.getStringList().size()));
               Onetap.MACRO_MANAGER.add(key.getValue(), text);
               ChatUtil.log("§aПривязан макрос §7" + text + " §aк клавише §7" + KeyNames.getKeyName(key.getValue()));
               break;
            case "remove":
               if (commandHelp.getStringList().isEmpty()) {
                  ChatUtil.log("§cИспользование: §7.macro remove <клавиша>");
                  return;
               }

               Key key2 = CommandManager.getCommand(commandHelp.getStringList().get(0));
               if (key2 == null) {
                  ChatUtil.log("§cНеизвестная клавиша: §7" + commandHelp.getStringList().get(0));
                  return;
               }

               if (Onetap.MACRO_MANAGER.remove(key2.getValue())) {
                  ChatUtil.log("§aМакрос снят с клавиши §7" + KeyNames.getKeyName(key2.getValue()));
               } else {
                  ChatUtil.log("§cНа этой клавише нет макроса: §7" + KeyNames.getKeyName(key2.getValue()));
               }
               break;
            case "clear":
               Onetap.MACRO_MANAGER.clear();
               ChatUtil.log("§aВсе макросы очищены");
               break;
            default:
               ChatUtil.log("§cИспользование:");
               ChatUtil.log(" §7.macro list");
               ChatUtil.log(" §7.macro add <клавиша> <команда>");
               ChatUtil.log(" §7.macro remove <клавиша>");
               ChatUtil.log(" §7.macro clear");
         }
      }
   }
}
