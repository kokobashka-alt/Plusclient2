package wtf.onetap.command;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

public class VClipCommand implements Command, MinecraftAccess {
   @Override
   public String getText2() {
      return "vclip";
   }

   @Override
   public String getText3() {
      return "Телепорт по вертикали сквозь блоки";
   }

   @Override
   public String getUsage() {
      return "up|down|<value>";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (commandHelp.getText2() == null) {
            this.printUsage();
         } else {
            BlockPos blockPos = MinecraftAccess.MINECRAFT.player.blockPosition();
            float floatValue;
            switch (commandHelp.getText2().toLowerCase()) {
               case "up":
                  floatValue = this.clip(blockPos, true);
                  break;
               case "down":
                  floatValue = this.clip(blockPos, false);
                  break;
               default:
                  try {
                     floatValue = Integer.parseInt(commandHelp.getText2());
                  } catch (NumberFormatException numberFormatException) {
                     this.printUsage();
                     return;
                  }
            }

            if (floatValue == 0.0F) {
               ChatUtil.log("§cНет свободного места");
            } else {
               this.m_8_void(floatValue);
            }
         }
      }
   }

   private float clip(BlockPos blockPos, boolean flag) {
      int value = flag ? 1 : -1;
      int value2 = flag ? MinecraftAccess.MINECRAFT.level.getMaxY() - blockPos.getY() : MinecraftAccess.MINECRAFT.level.getMinY() - blockPos.getY();
      boolean flag2 = false;

      for (int i = value; flag ? i < value2 : i > value2; i += value) {
         BlockPos blockPos2 = blockPos.offset(0, i, 0);
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2);
         BlockState blockState2 = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2.above());
         if (!flag && blockState.is(Blocks.BEDROCK)) {
            return 0.0F;
         }

         if (blockState.isAir() && blockState2.isAir()) {
            if (flag2) {
               return i;
            }
         } else {
            flag2 = true;
         }
      }

      return 0.0F;
   }

   private void m_8_void(float floatValue) {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;

      for (int i = 0; i < 10; i++) {
         localPlayer.connection.send(new Pos(localPlayer.getX(), localPlayer.getY(), localPlayer.getZ(), localPlayer.onGround(), localPlayer.horizontalCollision));
      }

      double doubleValue = localPlayer.getY() + floatValue;

      for (int i2 = 0; i2 < 10; i2++) {
         localPlayer.setDeltaMovement(Vec3.ZERO);
         localPlayer.connection.send(new Pos(localPlayer.getX(), doubleValue, localPlayer.getZ(), localPlayer.onGround(), localPlayer.horizontalCollision));
         localPlayer.setPos(localPlayer.getX(), doubleValue, localPlayer.getZ());
      }
   }

   private void printUsage() {
      ChatUtil.log("§7.vclip up|down");
      ChatUtil.log("§7.vclip <value>");
   }
}
