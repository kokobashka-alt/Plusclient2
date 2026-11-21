package wtf.onetap.event;

public class ContinueAttackEvent extends Event implements Cancellable {
   private final boolean flag;
   private boolean cancelled;

   public ContinueAttackEvent(boolean flag2) {
      this.flag = flag2;
   }

   public boolean isFlag() {
      return this.flag;
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
