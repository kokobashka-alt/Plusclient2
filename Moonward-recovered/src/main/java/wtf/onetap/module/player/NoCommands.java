package wtf.onetap.module.player;

import java.util.Set;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.MultiModeSetting;

@ModuleInformation(name = "NoCommands", description = "Отключает команды клиента через точку и команды Baritone", category = Category.PLAYER)
public class NoCommands extends Module {
   public final MultiModeSetting disableCommands = new MultiModeSetting("Отключить команды", "Клиент", "Клиент", "Баритон").build();

   public NoCommands() {
      ((Set)this.disableCommands.getValue()).add("Баритон");
   }

   public static boolean m_7_boolean() {
      NoCommands noCommands = Onetap.MODULE_MANAGER.get(NoCommands.class);
      return noCommands != null && noCommands.isEnabled() && noCommands.disableCommands.isSelected("Клиент");
   }

   public static boolean m_3_boolean() {
      NoCommands noCommands = Onetap.MODULE_MANAGER.get(NoCommands.class);
      return noCommands != null && noCommands.isEnabled() && noCommands.disableCommands.isSelected("Баритон");
   }

   public static boolean isAllowedCommand(String text) {
      if (!m_7_boolean()) {
         return true;
      }

      String text2 = text.startsWith("/") ? text.substring(1) : text;
      return text2.equals(".panic") || text2.startsWith(".panic ");
   }
}
