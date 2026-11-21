package wtf.onetap.event;

public class RemoveEntitiesEvent extends Event {
   private final int value;

   public RemoveEntitiesEvent(int value2) {
      this.value = value2;
   }

   public int getValue() {
      return this.value;
   }
}
