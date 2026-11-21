package wtf.onetap.command;

import java.awt.Desktop.Action;
import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import wtf.onetap.Onetap;
import wtf.onetap.config.ConfigException;
import wtf.onetap.util.ChatUtil;

public class ConfigCommand implements Command {
   @Override
   public String getText2() {
      return "cfg";
   }

   @Override
   public String getText3() {
      return "Управление конфигами";
   }

   @Override
   public String getUsage() {
      return "list|save [name]|load [name]|delete <name>|dir";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      if (commandHelp.getText2() == null) {
         ChatUtil.log("§cИспользование:");
         ChatUtil.log(" §7.cfg list");
         ChatUtil.log(" §7.cfg save [name]");
         ChatUtil.log(" §7.cfg load [name]");
         ChatUtil.log(" §7.cfg delete <name>");
         ChatUtil.log(" §7.cfg dir");
      } else {
         try {
            switch (commandHelp.getText2().toLowerCase()) {
               case "dir":
                  this.openFolder();
                  break;
               case "list":
                  this.listConfigs();
                  break;
               case "save":
                  this.saveConfig(this.m_3_String(commandHelp));
                  break;
               case "load":
                  this.loadConfig(this.m_3_String(commandHelp));
                  break;
               case "delete":
               case "del":
               case "remove":
                  this.deleteConfig(this.m_5(commandHelp, true));
                  break;
               default:
                  ChatUtil.log("§cИспользование:");
                  ChatUtil.log(" §7.cfg list");
                  ChatUtil.log(" §7.cfg save [name]");
                  ChatUtil.log(" §7.cfg load [name]");
                  ChatUtil.log(" §7.cfg delete <name>");
                  ChatUtil.log(" §7.cfg dir");
            }
         } catch (ConfigException configException) {
            ChatUtil.log("§cОшибка: §f" + configException.getMessage());
         }
      }
   }

   private void listConfigs() {
      var stringList = Onetap.CONFIG_MANAGER.listConfigs();
      if (stringList.isEmpty()) {
         ChatUtil.log("§eКонфигов пока нет");
      } else {
         ChatUtil.log("§fКонфиги:");
         stringList.forEach(text -> ChatUtil.log(" §7- §7" + text));
      }
   }

   private void saveConfig(String text) {
      Onetap.CONFIG_MANAGER.saveConfig(text);
      ChatUtil.log("§aКонфиг §7" + text + " §aсохранен");
   }

   private void loadConfig(String text) {
      Onetap.CONFIG_MANAGER.loadConfig(text);
      ChatUtil.log("§aКонфиг §7" + text + " §aзагружен");
   }

   private void deleteConfig(String text) {
      if (Onetap.CONFIG_MANAGER.deleteConfig(text)) {
         ChatUtil.log("§aКонфиг §7" + text + " §aудален");
      } else {
         ChatUtil.log("§cКонфиг не найден: §7" + text);
      }
   }

   private String m_3_String(CommandHelp commandHelp) {
      return this.m_5(commandHelp, false);
   }

   private String m_5(CommandHelp commandHelp, boolean flag) {
      if (commandHelp.getStringList().isEmpty()) {
         if (flag) {
            throw new ConfigException("Укажи имя конфига");
         } else {
            return "default";
         }
      } else {
         return commandHelp.getStringList().get(0);
      }
   }

   private void openFolder() {
      Path path = Onetap.CONFIG_MANAGER.getPath();

      try {
         Files.createDirectories(path);
      } catch (IOException e) {
         ChatUtil.log("§cНе удалось создать папку: §f" + e.getMessage());
         return;
      }
      boolean flag = switch (Util.getPlatform()) {
         case WINDOWS -> this.startProcess("explorer.exe", path.toString());
         case OSX -> this.startProcess("open", path.toString());
         case LINUX -> this.startProcess("xdg-open", path.toString());
         default -> false;
      };
      if (flag) {
         ChatUtil.log("§aОткрываю папку с конфигами...");
      } else {
         if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.OPEN)) {
            try {
               Desktop.getDesktop().open(path.toFile());
               ChatUtil.log("§aОткрываю папку с конфигами...");
               return;
            } catch (IOException e2) {
            }
         }

         Minecraft.getInstance().keyboardHandler.setClipboard(path.toString());
         ChatUtil.log("§eПуть скопирован в буфер обмена: §f" + path);
      }
   }

   private boolean startProcess(String... stringArray) {
      try {
         new ProcessBuilder(stringArray).inheritIO().start();
         return true;
      } catch (IOException e) {
         return false;
      }
   }
}
