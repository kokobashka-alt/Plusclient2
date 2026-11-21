package baritone.utils;

public class BaritoneMath {
   private BaritoneMath() {
   }

   public static int fastFloor(double var0) {
      return (int)(var0 + 1.0737418E9F) - 1073741824;
   }

   public static int fastCeil(double var0) {
      return 1073741824 - (int)(1.0737418E9F - var0);
   }
}
