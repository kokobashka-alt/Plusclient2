package wtf.onetap.module.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.BlockBreakEvent;
import wtf.onetap.event.ConnectedEvent;
import wtf.onetap.mixin.player.MultiPlayerGameModeAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "AreaBreak", description = "Киркой и лопатой ломает 3x3 вокруг сломанного блока", category = Category.PLAYER)
public class AreaBreak extends Module {
   private BlockPos blockPos;
   private Direction direction;

   @EventTarget
   private void onBlockBreak(BlockBreakEvent event) {
      this.blockPos = event.getPos().immutable();
      this.direction = event.getDirection();
   }

   @EventTarget
   private void onConnected(ConnectedEvent event) {
      VoiceChat.m_9_StringArr();
      BlockPos blockPos2 = this.blockPos;
      Direction direction2 = this.direction;
      this.blockPos = null;
      if (blockPos2 != null && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2).isAir()) {
            ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
            if (itemStack.is(ItemTags.PICKAXES) || itemStack.is(ItemTags.SHOVELS)) {
               Axis[] axisArray = new Axis[2];
               int value = 0;

               for (Axis axis : Axis.values()) {
                  if (axis != direction2.getAxis()) {
                     axisArray[value++] = axis;
                  }
               }

               for (int i = -1; i <= 1; i++) {
                  for (int i2 = -1; i2 <= 1; i2++) {
                     if (i != 0 || i2 != 0) {
                        this.m_2(blockPos2.relative(axisArray[0], i).relative(axisArray[1], i2), direction2);
                     }
                  }
               }
            }
         }
      }
   }

   private void m_2(BlockPos blockPos2, Direction direction2) {
      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2);
      if (!blockState.isAir() && MinecraftAccess.MINECRAFT.player.isWithinBlockInteractionRange(blockPos2, 0.0)) {
         if (!(blockState.getDestroyProgress(MinecraftAccess.MINECRAFT.player, MinecraftAccess.MINECRAFT.level, blockPos2) < Float.intBitsToFloat((int)3727944512048201728L))) {
            MultiPlayerGameModeAccessor multiPlayerGameModeAccessor = (MultiPlayerGameModeAccessor)MinecraftAccess.MINECRAFT.gameMode;
            multiPlayerGameModeAccessor.onetap$startPrediction(MinecraftAccess.MINECRAFT.level, value -> new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, blockPos2, direction2, value));
            multiPlayerGameModeAccessor.onetap$startPrediction(MinecraftAccess.MINECRAFT.level, value2 -> {
               MinecraftAccess.MINECRAFT.gameMode.destroyBlock(blockPos2);
               return new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, blockPos2, direction2, value2);
            });
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.blockPos = null;
   }
}
