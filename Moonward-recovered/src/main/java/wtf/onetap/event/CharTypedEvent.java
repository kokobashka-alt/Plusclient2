package wtf.onetap.event;

public class CharTypedEvent extends Event implements Cancellable {
   private final int codePoint;
   private boolean cancelled;

   public CharTypedEvent(int value) {
      this.codePoint = value;
   }

   public int getCodePoint() {
      return this.codePoint;
   }

   public String m_5_String() {
      return Character.toString(this.codePoint);
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
