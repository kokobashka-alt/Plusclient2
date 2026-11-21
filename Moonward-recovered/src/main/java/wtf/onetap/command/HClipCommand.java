package wtf.onetap.command;

import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

public class HClipCommand implements Command, MinecraftAccess {
   @Override
   public String getText2() {
      return "hclip";
   }

   @Override
   public String getText3() {
      return "Телепорт вперёд по направлению взгляда";
   }

   @Override
   public String getUsage() {
      return "<distance>";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (commandHelp.getText2() == null) {
            this.printUsage();
         } else {
            double doubleValue;
            try {
               doubleValue = Double.parseDouble(commandHelp.getText2());
            } catch (NumberFormatException numberFormatException) {
               this.printUsage();
               return;
            }

            if (Double.isFinite(doubleValue) && !(doubleValue < 1.0) && !(doubleValue > 10000.0)) {
               double doubleValue2 = Math.toRadians(MinecraftAccess.MINECRAFT.player.getYRot());
               double doubleValue3 = MinecraftAccess.MINECRAFT.player.getX() - Math.sin(doubleValue2) * doubleValue;
               double doubleValue4 = MinecraftAccess.MINECRAFT.player.getZ() + Math.cos(doubleValue2) * doubleValue;
               MinecraftAccess.MINECRAFT.player.setPos(doubleValue3, MinecraftAccess.MINECRAFT.player.getY(), doubleValue4);
            } else {
               ChatUtil.log("§cДистанция от 1 до 10000");
            }
         }
      }
   }

   private void printUsage() {
      ChatUtil.log("§7.hclip <distance>");
   }
}
