package wtf.onetap.event;

public interface Cancellable {
   boolean isCancelled();

   void cancel();
}
