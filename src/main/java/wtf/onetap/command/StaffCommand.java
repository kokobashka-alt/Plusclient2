package wtf.onetap.command;

import wtf.onetap.Onetap;
import wtf.onetap.util.ChatUtil;

public class StaffCommand implements Command {
   @Override
   public String getText2() {
      return "staff";
   }

   @Override
   public String getText3() {
      return "Управление списком стаффа";
   }

   @Override
   public String getUsage() {
      return "list|add <ник>|remove <ник>|clear";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      if (commandHelp.getText2() == null) {
         ChatUtil.log("§cИспользование:");
         ChatUtil.log(" §7.staff list");
         ChatUtil.log(" §7.staff add <ник>");
         ChatUtil.log(" §7.staff remove <ник>");
         ChatUtil.log(" §7.staff clear");
      } else {
         switch (commandHelp.getText2().toLowerCase()) {
            case "list":
               if (Onetap.STAFF_MANAGER.isEmpty()) {
                  ChatUtil.log("§7Список стаффа пуст");
                  return;
               }

               ChatUtil.log("§7Список стаффа:");

               for (String text : Onetap.STAFF_MANAGER.getStaff()) {
                  ChatUtil.log(" §7- §a" + text);
               }
               break;
            case "add":
               if (commandHelp.getStringList().isEmpty()) {
                  ChatUtil.log("§cИспользование: §7.staff add <ник>");
                  return;
               }

               String text2 = commandHelp.getStringList().get(0);
               Onetap.STAFF_MANAGER.add(text2);
               ChatUtil.log("§aСтаффер добавлен: §7" + text2);
               break;
            case "remove":
               if (commandHelp.getStringList().isEmpty()) {
                  ChatUtil.log("§cИспользование: §7.staff remove <ник>");
                  return;
               }

               String text3 = commandHelp.getStringList().get(0);
               if (Onetap.STAFF_MANAGER.remove(text3)) {
                  ChatUtil.log("§aСтаффер удален: §7" + text3);
               } else {
                  ChatUtil.log("§cСтаффер не найден: §7" + text3);
               }
               break;
            case "clear":
               Onetap.STAFF_MANAGER.clear();
               ChatUtil.log("§aСписок стаффа очищен");
               break;
            default:
               ChatUtil.log("§cИспользование:");
               ChatUtil.log(" §7.staff list");
               ChatUtil.log(" §7.staff add <ник>");
               ChatUtil.log(" §7.staff remove <ник>");
               ChatUtil.log(" §7.staff clear");
         }
      }
   }
}
