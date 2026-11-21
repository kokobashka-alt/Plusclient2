package wtf.onetap.module.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.player.MultiPlayerGameModeAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "FastBreak", description = "Ускоряет ломание блоков", category = Category.PLAYER)
public class FastBreak extends Module {
   private final FloatSetting acceleration = new FloatSetting("Ускорение", 0.7F, 0.3F, 1.0F, 0.1F).build();
   private final BooleanSetting onlyTools = new BooleanSetting("Только инструменты", true).build();

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (MinecraftAccess.MINECRAFT.options.keyAttack.isDown()) {
            if (MinecraftAccess.MINECRAFT.hitResult instanceof BlockHitResult blockHitResult) {
               BlockPos blockPos = blockHitResult.getBlockPos();
               BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos);
               if (!blockState.isAir()) {
                  if (!(Boolean)this.onlyTools.getValue() || !(MinecraftAccess.MINECRAFT.player.getMainHandItem().getDestroySpeed(blockState) <= 1.0F)) {
                     ((MultiPlayerGameModeAccessor)MinecraftAccess.MINECRAFT.gameMode).onetap$setDestroyDelay(0);
                     Direction direction = blockHitResult.getDirection();
                     int value = Math.max(1, Math.round(this.acceleration.getFloat() / 0.35F));

                     for (int i = 0; i < value; i++) {
                        MinecraftAccess.MINECRAFT.gameMode.continueDestroyBlock(blockPos, direction);
                     }

                     MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
                  }
               }
            }
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (MinecraftAccess.MINECRAFT.gameMode != null) {
         ((MultiPlayerGameModeAccessor)MinecraftAccess.MINECRAFT.gameMode).onetap$setDestroyDelay(0);
      }
   }
}
