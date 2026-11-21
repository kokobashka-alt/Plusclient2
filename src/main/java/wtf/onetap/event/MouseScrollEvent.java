package wtf.onetap.event;

public class MouseScrollEvent extends Event {
   private final double doubleValue;
   private final double doubleValue2;
   private final double doubleValue3;

   public MouseScrollEvent(double doubleValue4, double doubleValue5, double doubleValue6) {
      this.doubleValue = doubleValue4;
      this.doubleValue2 = doubleValue5;
      this.doubleValue3 = doubleValue6;
   }

   public double getDoubleValue() {
      return this.doubleValue;
   }

   public double getDoubleValue2() {
      return this.doubleValue2;
   }

   public double getDoubleValue3() {
      return this.doubleValue3;
   }
}
