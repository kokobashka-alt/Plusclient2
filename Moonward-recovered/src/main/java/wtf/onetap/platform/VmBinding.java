package wtf.onetap.platform;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.onetap.natives.NativeFunctions;

public class VmBinding {
   private static final Logger LOGGER = LoggerFactory.getLogger("onetap");
   private static final boolean FLAG = m_9_boolean();
   private static int[] valueArray;

   private VmBinding() {
   }

   public static boolean m_7_boolean() {
      return FLAG;
   }

   public static boolean neuro2(int value, float[] floatArray, double[] doubleArray) {
      return NativeFunctions.neuro2(value, floatArray, doubleArray);
   }

   public static void m_6_void() {
   }

   public static VmBinding.DataRecord m_3_C6() {
      if (!FLAG) {
         return null;
      }

      double[] doubleArray = new double[12];
      return !neuro2(9, null, doubleArray) ? null : new VmBinding.DataRecord(doubleArray[0] != 0.0, (long)doubleArray[1], (long)doubleArray[2], doubleArray[3] != 0.0, doubleArray[4] != 0.0);
   }

   public static String hashShort(String text) {
      if (text != null && !text.isEmpty()) {
         try {
            byte[] byteArray = MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(byteArray, 0, 4);
         } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            return "?";
         }
      } else {
         return "none";
      }
   }

   public static boolean m_1_boolean(byte[] byteArray) {
      if (FLAG && byteArray != null && byteArray.length == 16) {
         double[] doubleArray = new double[16];

         for (int i = 0; i < 16; i++) {
            doubleArray[i] = byteArray[i] & 0xFF;
         }

         return neuro2(11, null, doubleArray);
      } else {
         return false;
      }
   }

   public static VmBinding.DataRecord2 m_5_C7() {
      if (!FLAG) {
         return null;
      }

      double[] doubleArray = new double[64];
      if (neuro2(7, null, doubleArray) && doubleArray[0] != 0.0) {
         int value = (int)doubleArray[1];
         long longValue = (long)doubleArray[2];
         int value2 = 3;
         int value3 = (int)((long)doubleArray[value2++]);
         int[] intArray = new int[value3];

         for (int i = 0; i < value3; i++) {
            intArray[i] = (int)((long)doubleArray[value2++]);
         }

         int value4 = (int)((long)doubleArray[value2++]);
         int[] intArray2 = new int[value4];

         for (int i2 = 0; i2 < value4; i2++) {
            intArray2[i2] = (int)((long)doubleArray[value2++]);
         }

         byte[] byteArray = new byte[16];

         for (int i3 = 0; i3 < 16; i3++) {
            byteArray[i3] = (byte)((long)doubleArray[value2 + i3]);
         }

         value2 += 16;
         byte[] byteArray2 = new byte[16];

         for (int i4 = 0; i4 < 16; i4++) {
            byteArray2[i4] = (byte)((long)doubleArray[value2 + i4]);
         }

         return new VmBinding.DataRecord2(value, longValue, intArray, intArray2, byteArray, byteArray2);
      } else {
         return null;
      }
   }

   public static boolean m_8_boolean(byte[] byteArray) {
      if (FLAG && byteArray != null && byteArray.length == 16) {
         double[] doubleArray = new double[16];

         for (int i = 0; i < 16; i++) {
            doubleArray[i] = byteArray[i] & 0xFF;
         }

         return neuro2(10, null, doubleArray);
      } else {
         return false;
      }
   }

   public static byte[] m_1_BArr() {
      if (!FLAG) {
         return null;
      }

      double[] doubleArray = new double[32];
      if (!neuro2(12, null, doubleArray)) {
         return null;
      }

      byte[] byteArray = new byte[32];

      for (int i = 0; i < 32; i++) {
         byteArray[i] = (byte)((long)doubleArray[i]);
      }

      return byteArray;
   }

   public static boolean m_7(long longValue, byte[] byteArray) {
      if (FLAG && byteArray != null && byteArray.length == 64) {
         double[] doubleArray = new double[65];
         doubleArray[0] = longValue;

         for (int i = 0; i < 64; i++) {
            doubleArray[1 + i] = byteArray[i] & 0xFF;
         }

         return neuro2(6, null, doubleArray);
      } else {
         return false;
      }
   }

   private static boolean m_9_boolean() {
      try {
         Class.forName("jdk.internal.jaff.VmLogic").getMethod("bindMoonward", Class.class).invoke(null, NativeFunctions.class);
         return true;
      } catch (Throwable e) {
         return false;
      }
   }

   static {
      m_2(new int[2]);
   }

   public static void m_2(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_5_IArr() {
      return valueArray;
   }

   public record DataRecord(boolean f_3_a, long f_45, long f_4u, boolean f_34, boolean f_32) {
      public boolean m_3_a() {
         return this.f_3_a;
      }

      public long m_45() {
         return this.f_45;
      }

      public long m_4u() {
         return this.f_4u;
      }

      public boolean m_34() {
         return this.f_34;
      }

      public boolean m_32() {
         return this.f_32;
      }
   }

   public record DataRecord2(int f_4y, long f_7_a, int[] f_4m, int[] f_4_m, byte[] f_3_h, byte[] f_3y) {
      public int m_4y() {
         return this.f_4y;
      }

      public long m_7_a_long() {
         return this.f_7_a;
      }

      public int[] m_4m_IArr() {
         return this.f_4m;
      }

      public int[] m_4_m() {
         return this.f_4_m;
      }

      public byte[] m_3_h() {
         return this.f_3_h;
      }

      public byte[] m_3y() {
         return this.f_3y;
      }
   }
}
