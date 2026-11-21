package wtf.onetap.command;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.List;
import wtf.onetap.Onetap;
import wtf.onetap.module.Module;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.KeyNames;

public class BindCommand implements Command {
   @Override
   public String getText2() {
      return "bind";
   }

   @Override
   public String getText3() {
      return "Забиндить модуль на определенную клавишу";
   }

   @Override
   public String getUsage() {
      return "list|add <модуль> <клавиша>|remove <модуль>|clear";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      if (commandHelp.getText2() == null) {
         ChatUtil.log("§cИспользование:");
         ChatUtil.log(" §7.bind list");
         ChatUtil.log(" §7.bind add <модуль> <клавиша>");
         ChatUtil.log(" §7.bind remove <модуль>");
         ChatUtil.log(" §7.bind clear");
         this.listBinds();
      } else {
         switch (commandHelp.getText2().toLowerCase()) {
            case "list":
               this.listBinds();
               break;
            case "add":
               if (commandHelp.getStringList().size() < 2) {
                  ChatUtil.log("§cИспользование: §7.bind add <модуль> <клавиша>");
                  return;
               }

               String text = String.join(" ", commandHelp.getStringList().subList(0, commandHelp.getStringList().size() - 1));
               String text2 = commandHelp.getStringList().get(commandHelp.getStringList().size() - 1);
               Module module = Onetap.MODULE_MANAGER.findByName(text).orElse(null);
               if (module == null) {
                  ChatUtil.log("§cНеизвестный модуль: §7" + text);
                  return;
               }

               Key key = CommandManager.getCommand(text2);
               if (key == null) {
                  ChatUtil.log("§cНеизвестная клавиша: §7" + text2);
                  return;
               }

               module.setKey(key.getValue());
               ChatUtil.log("§aПривязан §7" + module.getName() + " §aк §7" + KeyNames.getKeyName(key.getValue()));
               break;
            case "remove":
               if (commandHelp.getStringList().isEmpty()) {
                  ChatUtil.log("§cИспользование: §7.bind remove <модуль>");
                  return;
               }

               String text3 = String.join(" ", commandHelp.getStringList());
               Module module2 = Onetap.MODULE_MANAGER.findByName(text3).orElse(null);
               if (module2 == null) {
                  ChatUtil.log("§cНеизвестный модуль: §7" + text3);
                  return;
               }

               module2.setKey(-1);
               ChatUtil.log("§aСнят бинд с §7" + module2.getName());
               break;
            case "clear":
               Onetap.MODULE_MANAGER.getModules().forEach(module3 -> module3.setKey(-1));
               ChatUtil.log("§aОчищены все бинды");
               break;
            default:
               ChatUtil.log("§cИспользование:");
               ChatUtil.log(" §7.bind list");
               ChatUtil.log(" §7.bind add <модуль> <клавиша>");
               ChatUtil.log(" §7.bind remove <модуль>");
               ChatUtil.log(" §7.bind clear");
         }
      }
   }

   private void listBinds() {
      var moduleList = Onetap.MODULE_MANAGER.getModules().stream().filter(module -> module.getKey() > 0).toList();
      if (moduleList.isEmpty()) {
         ChatUtil.log("§eНет активных биндов");
      } else {
         moduleList.forEach(module2 -> ChatUtil.log(" §7" + module2.getName() + " §7= §f" + KeyNames.getKeyName(module2.getKey())));
      }
   }
}
