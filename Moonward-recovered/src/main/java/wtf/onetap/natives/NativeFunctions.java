package wtf.onetap.natives;

public class NativeFunctions {
   private static int[] valueArray;

   private NativeFunctions() {
   }

   public static native boolean neuro2(int value, float[] floatArray, double[] doubleArray);

   public static native boolean core(int value, int value2, double[] doubleArray);

   public static void m_3(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_7_IArr() {
      return valueArray;
   }
}
