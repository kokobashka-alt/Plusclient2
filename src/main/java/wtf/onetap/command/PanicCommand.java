package wtf.onetap.command;

import java.util.ArrayList;
import wtf.onetap.Onetap;
import wtf.onetap.module.Module;
import wtf.onetap.util.ChatUtil;

public class PanicCommand implements Command {
   @Override
   public String getText2() {
      return "panic";
   }

   @Override
   public String getText3() {
      return "Выключает все модули клиента";
   }

   @Override
   public String getUsage() {
      return "";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      for (Module module : new ArrayList<>(Onetap.MODULE_MANAGER.getModules())) {
         if (module.isEnabled()) {
            module.setEnabled(false);
         }
      }

      ChatUtil.log("Все модули выключены");
   }
}
