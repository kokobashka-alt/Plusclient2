package wtf.onetap.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

public class UseItemOnEvent extends Event implements Cancellable {
   private final InteractionHand interactionHand;
   private final BlockHitResult blockHitResult;
   private boolean cancelled;

   public UseItemOnEvent(InteractionHand interactionHand2, BlockHitResult blockHitResult2) {
      this.interactionHand = interactionHand2;
      this.blockHitResult = blockHitResult2;
   }

   public InteractionHand getInteractionHand() {
      return this.interactionHand;
   }

   public BlockHitResult getBlockHitResult() {
      return this.blockHitResult;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void cancel() {
      this.cancelled = true;
   }
}
