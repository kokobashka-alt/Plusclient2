package wtf.onetap.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import wtf.onetap.bot.BotClient;
import wtf.onetap.bot.BotManager;
import wtf.onetap.bot.ProxyInfo;
import wtf.onetap.util.ChatUtil;

public class BotCommand implements Command {
   private static final List<String> STRING_LIST = List.of("connect", "control", "return", "list", "remove", "clear", "say", "tapemouse");

   @Override
   public String getText2() {
      return "bot";
   }

   @Override
   public String getText3() {
      return "Управление бот-сессиями";
   }

   @Override
   public String getUsage() {
      return "connect <ник> <айпи> [прокси]|control <ник>|return|list|remove <ник>|clear|say <ник|all> <текст>|tapemouse <ник>";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      if (commandHelp.getText2() == null) {
         m_5_void();
      } else {
         switch (commandHelp.getText2().toLowerCase(Locale.ROOT)) {
            case "connect":
               if (commandHelp.getStringList().size() < 2) {
                  ChatUtil.log("§cИспользование: §7.bot connect <ник> <айпи> [прокси]");
                  return;
               }

               String text = commandHelp.getStringList().get(0);
               String text2 = commandHelp.getStringList().get(1);
               if (BotManager.BOT_MANAGER.findBot(text) != null) {
                  ChatUtil.log("§cБот уже существует: §7" + text);
                  return;
               }

               ProxyInfo proxyInfo = null;
               if (commandHelp.getStringList().size() > 2) {
                  proxyInfo = ProxyInfo.m_7_C8_s(commandHelp.getStringList().subList(2, commandHelp.getStringList().size()));
                  if (proxyInfo == null) {
                     ChatUtil.log("§cНеверный прокси. §7.bot connect <ник> <айпи> [прокси]");
                     return;
                  }
               }

               if (!BotManager.BOT_MANAGER.connect(text, text2, proxyInfo)) {
                  ChatUtil.log("§cИспользование: §7.bot connect <ник> <айпи> [прокси]");
                  return;
               }

               ChatUtil.log("§aПодключение: §7" + text + " §7→ §7" + text2);
               break;
            case "control":
               if (commandHelp.getStringList().isEmpty()) {
                  ChatUtil.log("§cИспользование: §7.bot control <ник>");
                  return;
               }

               String text3 = commandHelp.getStringList().get(0);
               if (!BotManager.BOT_MANAGER.disconnect(text3)) {
                  ChatUtil.log("§cБот не найден: §7" + text3);
                  return;
               }

               ChatUtil.log("§aУправление: §7" + text3);
               break;
            case "return":
               BotManager.BOT_MANAGER.m_9_boolean();
               ChatUtil.log("§aВозврат на основную сессию");
               break;
            case "list":
               var stringList = BotManager.BOT_MANAGER.getNames();
               if (stringList.isEmpty()) {
                  ChatUtil.log("§7Список ботов пуст");
                  return;
               }

               ChatUtil.log("§7Список ботов:");

               for (String text4 : stringList) {
                  BotClient botClient = BotManager.BOT_MANAGER.findBot(text4);
                  String text5 = botClient != null && botClient.getText2() != null ? botClient.getText2() : "?";
                  ChatUtil.log(" §7- §a" + text4 + " §7" + text5);
               }
               break;
            case "remove":
               if (commandHelp.getStringList().isEmpty()) {
                  ChatUtil.log("§cИспользование: §7.bot remove <ник>");
                  return;
               }

               String text6 = commandHelp.getStringList().get(0);
               if (!BotManager.BOT_MANAGER.m_9_boolean(text6)) {
                  ChatUtil.log("§cБот не найден: §7" + text6);
                  return;
               }

               ChatUtil.log("§aБот удален: §7" + text6);
               break;
            case "clear":
               BotManager.BOT_MANAGER.m_7_void();
               ChatUtil.log("§aСписок ботов очищен");
               break;
            case "say":
               if (commandHelp.getStringList().size() < 2) {
                  ChatUtil.log("§cИспользование: §7.bot say <ник|all> <текст>");
                  return;
               }

               String text7 = commandHelp.getStringList().get(0);
               String text8 = String.join(" ", commandHelp.getStringList().subList(1, commandHelp.getStringList().size()));
               if ("all".equalsIgnoreCase(text7)) {
                  if (BotManager.BOT_MANAGER.getNames().isEmpty()) {
                     ChatUtil.log("§7Список ботов пуст");
                     return;
                  }

                  if (!BotManager.BOT_MANAGER.sendMessage("all", text8)) {
                     ChatUtil.log("§cНекому отправить");
                     return;
                  }

                  ChatUtil.log("§aОтправлено всем");
                  return;
               }

               if (BotManager.BOT_MANAGER.findBot(text7) == null) {
                  ChatUtil.log("§cБот не найден: §7" + text7);
                  return;
               }

               if (!BotManager.BOT_MANAGER.sendMessage(text7, text8)) {
                  ChatUtil.log("§cБот не в игре: §7" + text7);
                  return;
               }

               ChatUtil.log("§aОтправлено: §7" + text7);
               break;
            case "tapemouse":
               if (commandHelp.getStringList().isEmpty()) {
                  ChatUtil.log("§cИспользование: §7.bot tapemouse <ник>");
                  return;
               }

               String text9 = commandHelp.getStringList().get(0);
               Boolean bool = BotManager.BOT_MANAGER.toggleBot(text9);
               if (bool == null) {
                  ChatUtil.log("§cБот не найден: §7" + text9);
                  return;
               }

               ChatUtil.log(bool ? "§aTapeMouse: §7" + text9 : "§7TapeMouse выключен: §7" + text9);
               break;
            default:
               m_5_void();
         }
      }
   }

   public static List<String> complete(String[] stringArray, int value, String text) {
      if (value == 1) {
         return filterByPrefix(STRING_LIST, text);
      }

      if (stringArray.length < 2) {
         return List.of();
      }

      String text2 = stringArray[1].toLowerCase(Locale.ROOT);
      if (value == 2) {
         if (text2.equals("connect")) {
            return text.isEmpty() ? List.of("ник") : List.of();
         }

         if (text2.equals("control") || text2.equals("remove") || text2.equals("say") || text2.equals("tapemouse")) {
            ArrayList list = new ArrayList();
            if (text2.equals("say") && "all".startsWith(text.toLowerCase(Locale.ROOT))) {
               list.add("all");
            }

            list.addAll(filterByPrefix(BotManager.BOT_MANAGER.getNames(), text));
            if (list.isEmpty() && text.isEmpty()) {
               list.add("ник");
            }

            return list;
         }
      }

      if (value == 3 && text.isEmpty()) {
         if (text2.equals("connect")) {
            return List.of("айпи");
         }

         if (text2.equals("say")) {
            return List.of("текст");
         }
      }

      return value == 4 && text.isEmpty() && text2.equals("connect") ? List.of("прокси") : List.of();
   }

   private static List<String> filterByPrefix(Iterable<String> stringList, String text) {
      String text2 = text.toLowerCase(Locale.ROOT);
      ArrayList list = new ArrayList();

      for (String text3 : stringList) {
         if (text3.toLowerCase(Locale.ROOT).startsWith(text2)) {
            list.add(text3);
         }
      }

      return list;
   }

   private static void m_5_void() {
      ChatUtil.log("§cИспользование:");
      ChatUtil.log(" §7.bot connect <ник> <айпи> [прокси]");
      ChatUtil.log(" §7.bot control <ник>");
      ChatUtil.log(" §7.bot return");
      ChatUtil.log(" §7.bot list");
      ChatUtil.log(" §7.bot remove <ник>");
      ChatUtil.log(" §7.bot clear");
      ChatUtil.log(" §7.bot say <ник|all> <текст>");
      ChatUtil.log(" §7.bot tapemouse <ник>");
   }
}
