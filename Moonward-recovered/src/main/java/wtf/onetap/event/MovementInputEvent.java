package wtf.onetap.event;

public class MovementInputEvent extends Event implements Cancellable {
   private float forward;
   private float strafe;
   private boolean jumping;
   private boolean sneaking;
   private final double doubleValue;
   private boolean cancelled;
   private boolean modified;
   private static String[] textArray;

   public MovementInputEvent(float floatValue, float floatValue2, boolean flag, boolean flag2, double doubleValue2) {
      this.forward = floatValue;
      this.strafe = floatValue2;
      this.jumping = flag;
      this.sneaking = flag2;
      this.doubleValue = doubleValue2;
   }

   public float getForward() {
      return this.forward;
   }

   public float getStrafe() {
      return this.strafe;
   }

   public boolean isJumping() {
      return this.jumping;
   }

   public boolean isSneaking() {
      return this.sneaking;
   }

   public double getDoubleValue() {
      return this.doubleValue;
   }

   public void setForward(float floatValue) {
      this.forward = floatValue;
      this.modified = true;
   }

   public void setStrafe(float floatValue) {
      this.strafe = floatValue;
      this.modified = true;
   }

   public void setJumping(boolean flag) {
      this.jumping = flag;
      this.modified = true;
   }

   public void setSneaking(boolean flag) {
      this.sneaking = flag;
      this.modified = true;
   }

   public boolean isModified() {
      return this.modified;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void cancel() {
      this.cancelled = true;
   }

   public static void m_9(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_6_StringArr() {
      return textArray;
   }
}
