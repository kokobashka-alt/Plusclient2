package wtf.onetap.util;

public enum C5_w {
   INSTANT,
   LINEAR,
   EASE_IN_SINE,
   EASE_OUT_SINE,
   EASE_IN_OUT_SINE,
   EASE_IN_QUAD,
   EASE_OUT_QUAD,
   EASE_IN_OUT_QUAD,
   EASE_IN_CUBIC,
   EASE_OUT_CUBIC,
   EASE_IN_OUT_CUBIC,
   EASE_IN_EXP,
   EASE_OUT_EXP,
   EASE_IN_OUT_EXP;

   private static final double DOUBLE_VALUE = Math.log(Double.longBitsToDouble(4652209618980700160L)) / Math.log(Double.longBitsToDouble(4611686018427387904L));
   private static final double DOUBLE_VALUE2 = Double.longBitsToDouble(4621819117588971520L) * Math.log(Double.longBitsToDouble(4611686018427387904L));
   private static final double DOUBLE_VALUE3 = Math.log(Double.longBitsToDouble(4652209618980700160L));

   public boolean m_2_boolean() {
      return switch (this.ordinal()) {
         case 0, 1, 2, 3, 5, 6, 8, 9, 11, 12 -> true;
         default -> false;
      };
   }

   public C5_w m_8_C5_w() {
      return switch (this.ordinal()) {
         case 0 -> INSTANT;
         case 1 -> LINEAR;
         case 2 -> EASE_OUT_SINE;
         case 3 -> EASE_IN_SINE;
         case 4 -> EASE_IN_OUT_SINE;
         case 5 -> EASE_OUT_QUAD;
         case 6 -> EASE_IN_QUAD;
         case 7 -> EASE_IN_OUT_QUAD;
         case 8 -> EASE_OUT_CUBIC;
         case 9 -> EASE_IN_CUBIC;
         case 10 -> EASE_IN_OUT_CUBIC;
         case 11 -> EASE_OUT_EXP;
         case 12 -> EASE_IN_EXP;
         case 13 -> EASE_IN_OUT_EXP;
         default -> throw new MatchException(null, null);
      };
   }

   public double m_1_double(double doubleValue) {
      return switch (this.ordinal()) {
         case 0, 1 -> doubleValue;
         case 2 -> 1.0 - Math.cos(doubleValue * Math.PI / 2.0);
         case 3 -> Math.sin(doubleValue * Math.PI / 2.0);
         case 4 -> -(Math.cos(Math.PI * doubleValue) - 1.0) / 2.0;
         case 5 -> doubleValue * doubleValue;
         case 6 -> 1.0 - (1.0 - doubleValue) * (1.0 - doubleValue);
         case 7 -> doubleValue < 0.5 ? 2.0 * doubleValue * doubleValue : 1.0 - Math.pow(-2.0 * doubleValue + 2.0, 2.0) / 2.0;
         case 8 -> doubleValue * doubleValue * doubleValue;
         case 9 -> 1.0 - Math.pow(1.0 - doubleValue, 3.0);
         case 10 -> doubleValue < 0.5 ? 4.0 * doubleValue * doubleValue * doubleValue : 1.0 - Math.pow(-2.0 * doubleValue + 2.0, 3.0) / 2.0;
         case 11 -> m_5_double(doubleValue);
         case 12 -> m_9_double(doubleValue);
         case 13 -> m_4_double(doubleValue);
         default -> throw new MatchException(null, null);
      };
   }

   public double m_0_double(double doubleValue) {
      return switch (this.ordinal()) {
         case 0, 1 -> doubleValue;
         case 2 -> Math.acos(-(doubleValue - 1.0)) * 2.0 / Math.PI;
         case 3 -> Math.asin(doubleValue) * 2.0 / Math.PI;
         default -> throw new UnsupportedOperationException();
         case 5 -> Math.sqrt(doubleValue);
         case 6 -> -(Math.sqrt(-(doubleValue - 1.0)) - 1.0);
         case 8 -> Math.pow(doubleValue, 0.3333333333333333);
         case 9 -> -Math.pow(-doubleValue + 1.0, 0.3333333333333333) + 1.0;
         case 11 -> m_7_double(doubleValue);
         case 12 -> m_3_double(doubleValue);
      };
   }

   private static double m_5_double(double doubleValue) {
      if (doubleValue == 0.0) {
         return 0.0;
      } else {
         return doubleValue == 1.0 ? 1.0 : Math.pow(2.0, 10.0 * doubleValue - DOUBLE_VALUE) - 9.775171065493646E-4;
      }
   }

   private static double m_9_double(double doubleValue) {
      if (doubleValue == 0.0) {
         return 0.0;
      } else {
         return doubleValue == 1.0 ? 1.0 : 1.0 - Math.pow(2.0, 10.0 - DOUBLE_VALUE - 10.0 * doubleValue) + 9.775171065493646E-4;
      }
   }

   private static double m_4_double(double doubleValue) {
      if (doubleValue == 0.0) {
         return 0.0;
      } else if (doubleValue == 1.0) {
         return 1.0;
      } else {
         return doubleValue < 0.5 ? Math.pow(2.0, 20.0 * doubleValue - DOUBLE_VALUE) - 9.775171065493646E-4 : 1.0 - Math.pow(2.0, 10.0 - DOUBLE_VALUE - 10.0 * doubleValue) + 9.775171065493646E-4;
      }
   }

   private static double m_7_double(double doubleValue) {
      if (doubleValue == 0.0) {
         return 0.0;
      } else {
         return doubleValue == 1.0 ? 1.0 : Math.log(1023.0 * doubleValue + 1.0) / DOUBLE_VALUE2;
      }
   }

   private static double m_3_double(double doubleValue) {
      if (doubleValue == 0.0) {
         return 0.0;
      } else {
         return doubleValue == 1.0 ? 1.0 : -((Math.log(-((1023.0 * doubleValue - 1024.0) / 1023.0)) - DOUBLE_VALUE2 + DOUBLE_VALUE3) / DOUBLE_VALUE2);
      }
   }

   private static String a(byte[] byteArray) {
      int value = 0;
      int value2;
      char[] charArray = new char[value2 = byteArray.length];

      for (int i = 0; i < value2; i++) {
         int value3;
         if ((value3 = 255 & byteArray[i]) < 192) {
            charArray[value++] = (char)value3;
         } else if (value3 < 224) {
            char ch = (char)((char)(value3 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value++] = ch;
         } else if (i < value2 - 2) {
            char ch2 = (char)((char)(value3 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value++] = ch2;
         }
      }

      return new String(charArray, 0, value);
   }
}
