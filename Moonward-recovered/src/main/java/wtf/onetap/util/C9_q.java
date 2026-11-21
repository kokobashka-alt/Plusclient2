package wtf.onetap.util;

import java.util.Arrays;
import wtf.onetap.natives.NativeFunctions;
import wtf.onetap.platform.VmBinding;

public class C9_q {
   private static boolean flag;

   private C9_q() {
   }

   public static boolean m_4_boolean() {
      return VmBinding.m_7_boolean();
   }

   public static boolean m_7(int value, double[] doubleArray) {
      return m_4_boolean() && NativeFunctions.core(value, 1, doubleArray);
   }

   public static double[] m_3(int value, double... doubleArray) {
      double[] doubleArray2 = Arrays.copyOf(doubleArray, 32);
      m_7(value, doubleArray2);
      return doubleArray2;
   }

   public static boolean m_0(int value, double[] doubleArray) {
      return m_4_boolean() && NativeFunctions.core(value, 5, doubleArray);
   }

   public static boolean m_3_boolean(int value) {
      return m_4_boolean() && NativeFunctions.core(value, 0, new double[1]);
   }

   public static void m_8_void(int value) {
      if (m_4_boolean()) {
         double[] doubleArray = new double[]{System.currentTimeMillis()};
         NativeFunctions.core(value, 3, doubleArray);
      }
   }

   public static double[] m_9_DArr(int value) {
      if (!m_4_boolean()) {
         return null;
      }

      double[] doubleArray = new double[3];
      return NativeFunctions.core(value, 4, doubleArray) ? doubleArray : null;
   }

   public static boolean m_7_boolean() {
      return m_4_boolean() && NativeFunctions.core(0, 2, new double[1]);
   }

   public static boolean m_5_boolean(byte[] byteArray) {
      if (m_4_boolean() && byteArray != null && byteArray.length == 16) {
         double[] doubleArray = new double[16];

         for (int i = 0; i < 16; i++) {
            doubleArray[i] = byteArray[i] & 0xFF;
         }

         return NativeFunctions.core(0, 10, doubleArray);
      } else {
         return false;
      }
   }

   public static C9_q.DataRecord2 m_1_C8() {
      if (!m_4_boolean()) {
         return null;
      }

      double[] doubleArray = new double[96];
      if (NativeFunctions.core(0, 7, doubleArray) && doubleArray[0] != 0.0) {
         int value = (int)doubleArray[1];
         int value2 = (int)doubleArray[2];
         long longValue = (long)doubleArray[3];
         int value3 = 4;
         int value4 = (int)((long)doubleArray[value3++]);
         int[] intArray = new int[value4];

         for (int i = 0; i < value4; i++) {
            intArray[i] = (int)((long)doubleArray[value3++]);
         }

         int value5 = (int)((long)doubleArray[value3++]);
         int[] intArray2 = new int[value5];

         for (int i2 = 0; i2 < value5; i2++) {
            intArray2[i2] = (int)((long)doubleArray[value3++]);
         }

         byte[] byteArray = new byte[16];

         for (int i3 = 0; i3 < 16; i3++) {
            byteArray[i3] = (byte)((long)doubleArray[value3 + i3]);
         }

         return new C9_q.DataRecord2(value, value2, longValue, intArray, intArray2, byteArray);
      } else {
         return null;
      }
   }

   public static byte[] m_1_BArr() {
      if (!m_4_boolean()) {
         return null;
      }

      double[] doubleArray = new double[32];
      if (!NativeFunctions.core(0, 11, doubleArray)) {
         return null;
      }

      byte[] byteArray = new byte[32];

      for (int i = 0; i < 32; i++) {
         byteArray[i] = (byte)((long)doubleArray[i]);
      }

      return byteArray;
   }

   public static boolean m_1(int value, long longValue, byte[] byteArray) {
      if (m_4_boolean() && byteArray != null && byteArray.length == 64) {
         double[] doubleArray = new double[66];
         doubleArray[0] = value;
         doubleArray[1] = longValue;

         for (int i = 0; i < 64; i++) {
            doubleArray[2 + i] = byteArray[i] & 0xFF;
         }

         return NativeFunctions.core(0, 6, doubleArray);
      } else {
         return false;
      }
   }

   public static C9_q.DataRecord m_4_C3() {
      if (!m_4_boolean()) {
         return null;
      }

      double[] doubleArray = new double[8];
      return !NativeFunctions.core(0, 9, doubleArray) ? null : new C9_q.DataRecord(doubleArray[0] != 0.0, (long)doubleArray[1], (long)doubleArray[2], doubleArray[3] != 0.0, doubleArray[4] != 0.0);
   }

   public static void m_8_void(boolean flag2) {
      flag = flag2;
   }

   public static boolean m_3_boolean() {
      return flag;
   }

   public static boolean m_6_boolean() {
      return true;
   }

   public record DataRecord(boolean f_3j, long f_8b, long f_8_p, boolean f_3_z, boolean f_3h) {
      public boolean m_3j() {
         return this.f_3j;
      }

      public long m_8b() {
         return this.f_8b;
      }

      public long m_8_p_long() {
         return this.f_8_p;
      }

      public boolean m_3_z() {
         return this.f_3_z;
      }

      public boolean m_3h() {
         return this.f_3h;
      }
   }

   public record DataRecord2(int f_3_y, int f_0_o, long f_8_s, int[] f_3k, int[] f_3x, byte[] f_2_n) {
      public int m_3_y() {
         return this.f_3_y;
      }

      public int m_0_o() {
         return this.f_0_o;
      }

      public long m_8_s() {
         return this.f_8_s;
      }

      public int[] m_3k() {
         return this.f_3k;
      }

      public int[] m_3x() {
         return this.f_3x;
      }

      public byte[] m_2_n_BArr() {
         return this.f_2_n;
      }
   }
}
