package wtf.onetap.event;

public class TickEvent extends Event {
   private static boolean flag;

   public static void m_1(boolean flag2) {
      flag = flag2;
   }

   public static boolean m_8_boolean() {
      return flag;
   }

   public static boolean m_1_boolean() {
      return true;
   }
}
