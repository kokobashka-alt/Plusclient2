package baritone.utils;

import baritone.api.BaritoneAPI;
import baritone.api.utils.IPlayerContext;
import baritone.utils.accessor.IPlayerControllerMP;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.HitResult;

public class BlockBreakHelper {
   private final IPlayerContext ctx;
   private boolean wasHitting;
   private int breakDelayTimer = 0;

   BlockBreakHelper(IPlayerContext var1) {
      this.ctx = var1;
   }

   public void stopBreakingBlock() {
      if (this.ctx.player() != null && this.wasHitting) {
         this.ctx.playerController().setHittingBlock(false);
         this.ctx.playerController().resetBlockRemoving();
         this.wasHitting = false;
      }
   }

   public void tick(boolean var1) {
      if (this.breakDelayTimer > 0) {
         this.breakDelayTimer--;
      } else {
         HitResult var2 = this.ctx.objectMouseOver();
         boolean var3 = var2 != null && var2.getType() == Type.BLOCK;
         if (var1 && var3) {
            this.ctx.playerController().setHittingBlock(this.wasHitting);
            if (this.ctx.playerController().hasBrokenBlock()) {
               this.ctx.playerController().syncHeldItem();
               this.ctx.playerController().clickBlock(((BlockHitResult)var2).getBlockPos(), ((BlockHitResult)var2).getDirection());
               this.ctx.player().swing(InteractionHand.MAIN_HAND);
            } else {
               if (this.ctx.playerController().onPlayerDamageBlock(((BlockHitResult)var2).getBlockPos(), ((BlockHitResult)var2).getDirection())) {
                  this.ctx.player().swing(InteractionHand.MAIN_HAND);
               }

               if (this.ctx.playerController().hasBrokenBlock()) {
                  this.breakDelayTimer = BaritoneAPI.getSettings().blockBreakSpeed.value - 1;
                  ((IPlayerControllerMP)this.ctx.minecraft().gameMode).setDestroyDelay(0);
               }
            }

            this.wasHitting = !this.ctx.playerController().hasBrokenBlock();
            this.ctx.playerController().setHittingBlock(false);
         } else {
            this.wasHitting = false;
         }
      }
   }
}
