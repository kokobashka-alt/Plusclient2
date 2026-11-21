package wtf.onetap.module.player;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "ClickThrough", description = "Позволяет взаимодействовать с контейнерами через стены", category = Category.PLAYER)
public class ClickThrough extends Module {
   private final Set<BlockPos> blockPosSet = new HashSet<>();

   @EventTarget
   public void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof AbstractContainerScreen)) {
            if (MinecraftAccess.MINECRAFT.options.keyUse.isDown() && !MinecraftAccess.MINECRAFT.player.isShiftKeyDown() && !MinecraftAccess.MINECRAFT.player.isUsingItem()) {
               this.blockPosSet.clear();
               float floatValue = MinecraftAccess.MINECRAFT.getDeltaTracker().getGameTimeDeltaPartialTick(true);
               Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition(floatValue);
               Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getViewVector(floatValue).scale(0.1);

               for (int i = 1; i <= 50; i++) {
                  Vec3 vec33 = vec3.add(vec32.x * i, vec32.y * i, vec32.z * i);
                  BlockPos blockPos = BlockPos.containing(vec33);
                  if (this.blockPosSet.add(blockPos)) {
                     BlockEntity blockEntity = MinecraftAccess.MINECRAFT.level.getBlockEntity(blockPos);
                     if (blockEntity != null) {
                        BlockHitResult blockHitResult = new BlockHitResult(
                           new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5), Direction.UP, blockPos, true
                        );
                        MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
                        return;
                     }
                  }
               }
            }
         }
      }
   }
}
