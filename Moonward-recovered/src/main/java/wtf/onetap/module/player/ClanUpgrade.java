package wtf.onetap.module.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

@ModuleInformation(name = "ClanUpgrade", description = "Автоматическое улучшение клана", category = Category.PLAYER)
public class ClanUpgrade extends Module {
   private int value = -1;

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         int value2 = InventoryUtil.findHotbarSlot(Items.REDSTONE);
         if (value2 != -1) {
            if (this.value == -1) {
               this.value = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
            }

            if (MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() != value2) {
               MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value2);
               MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value2));
            }

            Rotations rotations = Onetap.ROTATION_MODES.toRotations();
            Onetap.ROTATION_MODES.rotate(new Rotations(rotations.getFloatValue(), Float.intBitsToFloat((int)6368181112211243008L)), false, 90);
            BlockPos blockPos = MinecraftAccess.MINECRAFT.player.blockPosition();
            BlockPos blockPos2 = blockPos.below();
            BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2);
            if (blockState.isSolid()) {
               Vec3 vec3 = new Vec3(blockPos2.getX() + 0.5, blockPos2.getY() + 1.0, blockPos2.getZ() + 0.5);
               BlockHitResult blockHitResult = new BlockHitResult(vec3, Direction.UP, blockPos2, false);

               for (int i = 0; i < 4; i++) {
                  MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
                  MinecraftAccess.MINECRAFT.gameMode.startDestroyBlock(blockPos, Direction.UP);
               }

               MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
            }
         }
      }
   }

   @Override
   public void onDisable() {
      if (this.value != -1 && MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(this.value);
         MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(this.value));
         this.value = -1;
      }

      Onetap.ROTATION_MODES.reset();
   }
}
