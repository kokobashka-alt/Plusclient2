package wtf.onetap.command;

import wtf.onetap.render.TargetArrow;
import wtf.onetap.util.ChatUtil;

public class GpsCommand implements Command {
   @Override
   public String getText2() {
      return "gps";
   }

   @Override
   public String getText3() {
      return "Ставит GPS-метку со стрелкой на указанные координаты";
   }

   @Override
   public String getUsage() {
      return "<x> <z>|off";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      if (commandHelp.getText2() == null) {
         this.printUsage();
      } else if (commandHelp.getText2().equalsIgnoreCase("off")) {
         TargetArrow.m_9_void();
         ChatUtil.log("§aGPS-метка убрана");
      } else if (commandHelp.getStringList().isEmpty()) {
         this.printUsage();
      } else {
         double doubleValue;
         double doubleValue2;
         try {
            doubleValue = Double.parseDouble(commandHelp.getText2());
            doubleValue2 = Double.parseDouble(commandHelp.getStringList().get(0));
         } catch (NumberFormatException numberFormatException) {
            ChatUtil.log("§cКоординаты должны быть числами");
            return;
         }

         TargetArrow.m_5_void(doubleValue, doubleValue2);
         long longValue = (long)doubleValue;
         long longValue2 = (long)doubleValue2;
         long longValue3 = longValue;
         ChatUtil.log("§aGPS-метка поставлена: §7" + longValue3 + " " + longValue2);
      }
   }

   private void printUsage() {
      ChatUtil.log("§cИспользование:");
      ChatUtil.log(" §7.gps <x> <z>");
      ChatUtil.log(" §7.gps off");
   }
}
