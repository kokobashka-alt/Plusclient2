package wtf.onetap.event;

public class KeyPressEvent extends Event implements Cancellable {
   private final int key;
   private final int action;
   private boolean cancelled;
   private static boolean flag;

   public KeyPressEvent(int value, int value2) {
      this.key = value;
      this.action = value2;
   }

   public int getKey() {
      return this.key;
   }

   public int getAction() {
      return this.action;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void cancel() {
      this.cancelled = true;
   }

   public static void m_8_void(boolean flag2) {
      flag = flag2;
   }

   public static boolean m_1_boolean() {
      return flag;
   }

   public static boolean m_4_boolean() {
      return true;
   }
}
