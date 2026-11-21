package wtf.onetap.event;

public class CloseContainerEvent extends Event implements Cancellable {
   private boolean cancelled;

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void cancel() {
      this.cancelled = true;
   }
}
