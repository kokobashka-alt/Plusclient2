package baritone.utils;

import baritone.Baritone;
import baritone.api.utils.IPlayerContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.HitResult;

public class BlockPlaceHelper {
   private final IPlayerContext ctx;
   private int rightClickTimer;

   BlockPlaceHelper(IPlayerContext var1) {
      this.ctx = var1;
   }

   public void tick(boolean var1) {
      if (this.rightClickTimer > 0) {
         this.rightClickTimer--;
      } else {
         HitResult var2 = this.ctx.objectMouseOver();
         if (var1 && !this.ctx.player().isHandsBusy() && var2 != null && var2.getType() == Type.BLOCK) {
            this.rightClickTimer = Baritone.settings().rightClickSpeed.value - 1;

            for (InteractionHand var6 : InteractionHand.values()) {
               if (this.ctx.playerController().processRightClickBlock(this.ctx.player(), this.ctx.world(), var6, (BlockHitResult)var2)
                  == InteractionResult.SUCCESS) {
                  this.ctx.player().swing(var6);
                  return;
               }

               if (!this.ctx.player().getItemInHand(var6).isEmpty()
                  && this.ctx.playerController().processRightClick(this.ctx.player(), this.ctx.world(), var6) == InteractionResult.SUCCESS) {
                  return;
               }
            }
         }
      }
   }
}
