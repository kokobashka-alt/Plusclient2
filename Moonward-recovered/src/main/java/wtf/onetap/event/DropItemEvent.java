package wtf.onetap.event;

public class DropItemEvent extends Event implements Cancellable {
   private final int slot;
   private boolean cancelled;

   public DropItemEvent(int value) {
      this.slot = value;
   }

   public int getSlot() {
      return this.slot;
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
