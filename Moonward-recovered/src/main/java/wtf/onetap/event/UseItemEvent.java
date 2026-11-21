package wtf.onetap.event;

import net.minecraft.world.InteractionHand;

public class UseItemEvent extends Event implements Cancellable {
   private final InteractionHand hand;
   private boolean cancelled;

   public UseItemEvent(InteractionHand interactionHand) {
      this.hand = interactionHand;
   }

   public InteractionHand getHand() {
      return this.hand;
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
