package wtf.onetap.command;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

public class TpCommand implements Command, MinecraftAccess {
   @Override
   public String getText2() {
      return "tp";
   }

   @Override
   public String getText3() {
      return "Телепорт к игроку или на координаты";
   }

   @Override
   public String getUsage() {
      return "<name>|<x> <y> <z>";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (commandHelp.getText2() == null) {
            this.printUsage();
         } else {
            if (commandHelp.getStringList().size() >= 2) {
               this.m_0(commandHelp);
            } else {
               this.teleportToPlayer(commandHelp.getText2());
            }
         }
      }
   }

   private void m_0(CommandHelp commandHelp) {
      double doubleValue;
      double doubleValue2;
      double doubleValue3;
      try {
         doubleValue = Double.parseDouble(commandHelp.getText2());
         doubleValue2 = Double.parseDouble(commandHelp.getStringList().get(0));
         doubleValue3 = Double.parseDouble(commandHelp.getStringList().get(1));
      } catch (NumberFormatException numberFormatException) {
         this.printUsage();
         return;
      }

      if (Double.isFinite(doubleValue) && Double.isFinite(doubleValue2) && Double.isFinite(doubleValue3) && !(Math.abs(doubleValue) > 2.9999984E7) && !(Math.abs(doubleValue3) > 2.9999984E7)) {
         MinecraftAccess.MINECRAFT.player.setPos(doubleValue, doubleValue2, doubleValue3);
      } else {
         ChatUtil.log("§cНекорректные координаты");
      }
   }

   private void teleportToPlayer(String text) {
      Player player = null;

      for (Player player2 : MinecraftAccess.MINECRAFT.level.players()) {
         if (player2.getGameProfile().name().equalsIgnoreCase(text)) {
            player = player2;
         }
      }

      if (player == null) {
         ChatUtil.log("§cИгрок не найден рядом");
      } else {
         LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;

         for (int i = 0; i < 10; i++) {
            localPlayer.connection.send(new Pos(localPlayer.getX(), localPlayer.getY(), localPlayer.getZ(), localPlayer.onGround(), localPlayer.horizontalCollision));
         }

         Vec3 vec3 = player.position();

         for (int i2 = 0; i2 < 10; i2++) {
            localPlayer.connection.send(new Pos(vec3.x, vec3.y, vec3.z, localPlayer.onGround(), localPlayer.horizontalCollision));
            localPlayer.setPos(vec3.x, vec3.y, vec3.z);
         }
      }
   }

   private void printUsage() {
      ChatUtil.log("§7.tp <name>");
      ChatUtil.log("§7.tp <x> <y> <z>");
   }
}
