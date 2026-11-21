package wtf.onetap.event;

public class MouseClickEvent extends Event implements Cancellable {
   private final double x;
   private final double y;
   private final int button;
   private final int action;
   private boolean cancelled;
   private static int value;

   public MouseClickEvent(double doubleValue, double doubleValue2, int value2, int value3) {
      this.x = doubleValue;
      this.y = doubleValue2;
      this.button = value2;
      this.action = value3;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public int getButton() {
      return this.button;
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

   public static void m_9_void(int value2) {
      value = value2;
   }

   public static int m_9_int() {
      return value;
   }

   public static int m_4_int() {
      return 0;
   }
}
