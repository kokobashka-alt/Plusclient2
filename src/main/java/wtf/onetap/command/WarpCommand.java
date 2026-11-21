package wtf.onetap.command;

import java.util.Collection;
import wtf.onetap.config.WarpManager;
import wtf.onetap.util.ChatUtil;

public class WarpCommand implements Command {
   @Override
   public String getText2() {
      return "warp";
   }

   @Override
   public String getText3() {
      return "Парс варпов и телепорт на ближайший к координатам";
   }

   @Override
   public String getUsage() {
      return "parse new|old|continue|stop|list|<x> <z>";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      if (commandHelp.getText2() == null) {
         this.printUsage();
      } else {
         switch (commandHelp.getText2().toLowerCase()) {
            case "parse":
               this.m_5(commandHelp);
               break;
            case "stop":
               WarpManager.m_2_void();
               break;
            case "list":
               this.listWarps();
               break;
            default:
               this.m_9(commandHelp);
         }
      }
   }

   private void m_5(CommandHelp commandHelp) {
      if (commandHelp.getStringList().isEmpty()) {
         ChatUtil.log("§7parse new|old");
      } else {
         switch (commandHelp.getStringList().get(0).toLowerCase()) {
            case "new":
               WarpManager.m_9_void(true);
               break;
            case "old":
            case "continue":
               WarpManager.m_9_void(false);
               break;
            default:
               ChatUtil.log("§7parse new|old");
         }
      }
   }

   private void listWarps() {
      var c0List = WarpManager.getWarpCollection();
      if (c0List.isEmpty()) {
         ChatUtil.log("§7empty");
      } else {
         ChatUtil.log("§7" + c0List.size());

         for (WarpManager.DataRecord c0 : c0List) {
            String text = c0.m_1p() != null && !c0.m_1p().isEmpty() ? c0.m_1p() : "?";
            String text2 = c0.m_1_p_String();
            long longValue = Math.round(c0.m_3_p());
            long longValue2 = Math.round(c0.m_3s());
            String text3 = text;
            long longValue3 = longValue2;
            long longValue4 = longValue;
            String text4 = text2;
            ChatUtil.log("§7" + text4 + " " + longValue4 + " " + longValue3 + " " + text3);
         }
      }
   }

   private void m_9(CommandHelp commandHelp) {
      if (commandHelp.getStringList().isEmpty()) {
         this.printUsage();
      } else {
         double doubleValue;
         double doubleValue2;
         try {
            doubleValue = Double.parseDouble(commandHelp.getText2());
            doubleValue2 = Double.parseDouble(commandHelp.getStringList().get(0));
         } catch (NumberFormatException numberFormatException) {
            this.printUsage();
            return;
         }

         WarpManager.m_6_boolean(doubleValue, doubleValue2);
      }
   }

   private void printUsage() {
      ChatUtil.log("§7.warp parse new|old");
      ChatUtil.log("§7.warp <x> <z>");
      ChatUtil.log("§7.warp list|stop");
   }
}
