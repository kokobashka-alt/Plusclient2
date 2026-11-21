package wtf.onetap.command;

import wtf.onetap.Onetap;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.SoundPlayer;

public class FriendCommand implements Command {
   @Override
   public String getText2() {
      return "friend";
   }

   @Override
   public String getText3() {
      return "Управление списком друзей";
   }

   @Override
   public String getUsage() {
      return "list|add <ник>|remove <ник>|clear";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      if (commandHelp.getText2() == null) {
         ChatUtil.log("§cИспользование:");
         ChatUtil.log(" §7.friend list");
         ChatUtil.log(" §7.friend add <ник>");
         ChatUtil.log(" §7.friend remove <ник>");
         ChatUtil.log(" §7.friend clear");
      } else {
         switch (commandHelp.getText2().toLowerCase()) {
            case "list":
               if (Onetap.FRIEND_MANAGER.isEmpty()) {
                  ChatUtil.log("§7Список друзей пуст");
                  return;
               }

               ChatUtil.log("§7Список друзей:");

               for (String text : Onetap.FRIEND_MANAGER.getFriends()) {
                  ChatUtil.log(" §7- §a" + text);
               }
               break;
            case "add":
               if (commandHelp.getStringList().isEmpty()) {
                  ChatUtil.log("§cИспользование: §7.friend add <ник>");
                  return;
               }

               String text2 = commandHelp.getStringList().get(0);
               Onetap.FRIEND_MANAGER.add(text2);
               SoundPlayer.play("friendadd");
               ChatUtil.log("§aДруг добавлен: §7" + text2);
               break;
            case "remove":
               if (commandHelp.getStringList().isEmpty()) {
                  ChatUtil.log("§cИспользование: §7.friend remove <ник>");
                  return;
               }

               String text3 = commandHelp.getStringList().get(0);
               if (Onetap.FRIEND_MANAGER.remove(text3)) {
                  SoundPlayer.play("friendremove");
                  ChatUtil.log("§aДруг удален: §7" + text3);
               } else {
                  ChatUtil.log("§cДруг не найден: §7" + text3);
               }
               break;
            case "clear":
               Onetap.FRIEND_MANAGER.clear();
               ChatUtil.log("§aСписок друзей очищен");
               break;
            default:
               ChatUtil.log("§cИспользование:");
               ChatUtil.log(" §7.friend list");
               ChatUtil.log(" §7.friend add <ник>");
               ChatUtil.log(" §7.friend remove <ник>");
               ChatUtil.log(" §7.friend clear");
         }
      }
   }
}
