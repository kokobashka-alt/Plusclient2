package wtf.onetap.animation;

public interface Easing {
   Easing EASING = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5 = floatValue3 * floatValue / floatValue4 + floatValue2;
      return (float)(-2.0 * Math.pow(floatValue5, 3.0) + 3.0 * Math.pow(floatValue5, 2.0));
   };
   Easing EASING2 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5 = floatValue3 * floatValue / floatValue4 + floatValue2;
      return floatValue5 < 0.5 ? 4.0F * floatValue5 * floatValue5 * floatValue5 : (float)(1.0 - Math.pow(-2.0F * floatValue5 + 2.0F, 3.0) / 2.0);
   };
   Easing EASING3 = (floatValue, floatValue2, floatValue3, floatValue4) -> floatValue3 * floatValue / floatValue4 + floatValue2;
   Easing EASING4 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      return floatValue3 * (floatValue5 = floatValue / floatValue4) * floatValue5 + floatValue2;
   };
   Easing EASING5 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      return -floatValue3 * (floatValue5 = floatValue / floatValue4) * (floatValue5 - 2.0F) + floatValue2;
   };
   Easing EASING6 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      return (floatValue5 = floatValue / (floatValue4 / 2.0F)) < 1.0F ? floatValue3 / 2.0F * floatValue5 * floatValue5 + floatValue2 : -floatValue3 / 2.0F * (--floatValue5 * (floatValue5 - 2.0F) - 1.0F) + floatValue2;
   };
   Easing EASING7 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      return floatValue3 * (floatValue5 = floatValue / floatValue4) * floatValue5 * floatValue5 + floatValue2;
   };
   Easing EASING8 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      return floatValue3 * ((floatValue5 = floatValue / floatValue4 - 1.0F) * floatValue5 * floatValue5 + 1.0F) + floatValue2;
   };
   Easing EASING9 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      float floatValue6;
      return (floatValue5 = floatValue / (floatValue4 / 2.0F)) < 1.0F ? floatValue3 / 2.0F * floatValue5 * floatValue5 * floatValue5 + floatValue2 : floatValue3 / 2.0F * ((floatValue6 = floatValue5 - 2.0F) * floatValue6 * floatValue6 + 2.0F) + floatValue2;
   };
   Easing EASING10 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      return floatValue3 * (floatValue5 = floatValue / floatValue4) * floatValue5 * floatValue5 * floatValue5 + floatValue2;
   };
   Easing EASING11 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      return -floatValue3 * ((floatValue5 = floatValue / floatValue4 - 1.0F) * floatValue5 * floatValue5 * floatValue5 - 1.0F) + floatValue2;
   };
   Easing EASING12 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      float floatValue6;
      return (floatValue5 = floatValue / (floatValue4 / 2.0F)) < 1.0F
         ? floatValue3 / 2.0F * floatValue5 * floatValue5 * floatValue5 * floatValue5 + floatValue2
         : -floatValue3 / 2.0F * ((floatValue6 = floatValue5 - 2.0F) * floatValue6 * floatValue6 * floatValue6 - 2.0F) + floatValue2;
   };
   Easing EASING13 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      return floatValue3 * (floatValue5 = floatValue / floatValue4) * floatValue5 * floatValue5 * floatValue5 * floatValue5 + floatValue2;
   };
   Easing EASING14 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      return floatValue3 * ((floatValue5 = floatValue / floatValue4 - 1.0F) * floatValue5 * floatValue5 * floatValue5 * floatValue5 + 1.0F) + floatValue2;
   };
   Easing EASING15 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      float floatValue6;
      return (floatValue5 = floatValue / (floatValue4 / 2.0F)) < 1.0F
         ? floatValue3 / 2.0F * floatValue5 * floatValue5 * floatValue5 * floatValue5 * floatValue5 + floatValue2
         : floatValue3 / 2.0F * ((floatValue6 = floatValue5 - 2.0F) * floatValue6 * floatValue6 * floatValue6 * floatValue6 + 2.0F) + floatValue2;
   };
   Easing EASING16 = (floatValue, floatValue2, floatValue3, floatValue4) -> -floatValue3 * (float)Math.cos(floatValue / floatValue4 * (Math.PI / 2)) + floatValue3 + floatValue2;
   Easing EASING17 = (floatValue, floatValue2, floatValue3, floatValue4) -> floatValue3 * (float)Math.sin(floatValue / floatValue4 * (Math.PI / 2)) + floatValue2;
   Easing EASING18 = (floatValue, floatValue2, floatValue3, floatValue4) -> -floatValue3 / 2.0F * ((float)Math.cos(Math.PI * floatValue / floatValue4) - 1.0F) + floatValue2;
   Easing EASING19 = (floatValue, floatValue2, floatValue3, floatValue4) -> floatValue == 0.0F ? floatValue2 : floatValue3 * (float)Math.pow(2.0, 10.0F * (floatValue / floatValue4 - 1.0F)) + floatValue2;
   Easing EASING20 = (floatValue, floatValue2, floatValue3, floatValue4) -> floatValue == floatValue4 ? floatValue2 + floatValue3 : floatValue3 * (-((float)Math.pow(2.0, -10.0F * floatValue / floatValue4)) + 1.0F) + floatValue2;
   Easing EASING21 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      if (floatValue == 0.0F) {
         return floatValue2;
      }

      if (floatValue == floatValue4) {
         return floatValue2 + floatValue3;
      }

      if ((floatValue = floatValue / (floatValue4 / 2.0F)) < 1.0F) {
         return floatValue3 / 2.0F * (float)Math.pow(2.0, 10.0F * (floatValue - 1.0F)) + floatValue2;
      }

      float floatValue5 = floatValue3 / 2.0F;
      float floatValue6 = floatValue - 1.0F;
      return floatValue5 * (-((float)Math.pow(2.0, -10.0F * (floatValue - 1.0F))) + 2.0F) + floatValue2;
   };
   Easing EASING22 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      return -floatValue3 * ((float)Math.sqrt(1.0F - (floatValue5 = floatValue / floatValue4) * floatValue5) - 1.0F) + floatValue2;
   };
   Easing EASING23 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      return floatValue3 * (float)Math.sqrt(1.0F - (floatValue5 = floatValue / floatValue4 - 1.0F) * floatValue5) + floatValue2;
   };
   Easing EASING24 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      float floatValue5;
      float floatValue6;
      return (floatValue5 = floatValue / (floatValue4 / 2.0F)) < 1.0F
         ? -floatValue3 / 2.0F * ((float)Math.sqrt(1.0F - floatValue5 * floatValue5) - 1.0F) + floatValue2
         : floatValue3 / 2.0F * ((float)Math.sqrt(1.0F - (floatValue6 = floatValue5 - 2.0F) * floatValue6) + 1.0F) + floatValue2;
   };
   Easing.StateHolder4 C3 = new Easing.StateHolder7();
   Easing.StateHolder4 C32 = new Easing.StateHolder6();
   Easing.StateHolder4 C33 = new Easing.StateHolder2();
   Easing.StateHolder C0 = new Easing.StateHolder3();
   Easing.StateHolder C02 = new Easing.StateHolder5();
   Easing.StateHolder C03 = new Easing.StateHolder5(Float.intBitsToFloat(1085698904));
   Easing.StateHolder C04 = new Easing.StateHolder8();
   Easing EASING25 = (floatValue, floatValue2, floatValue3, floatValue4) -> {
      if ((floatValue = floatValue / floatValue4) < 0.36363637F) {
         return floatValue3 * (7.5625F * floatValue * floatValue) + floatValue2;
      } else if (floatValue < 0.72727275F) {
         float floatValue5;
         return floatValue3 * (7.5625F * (floatValue5 = floatValue - 0.54545456F) * floatValue5 + 0.75F) + floatValue2;
      } else {
         float floatValue6;
         float floatValue7;
         return floatValue < 0.90909094F
            ? floatValue3 * (7.5625F * (floatValue6 = floatValue - 0.8181818F) * floatValue6 + 0.9375F) + floatValue2
            : floatValue3 * (7.5625F * (floatValue7 = floatValue - 0.95454544F) * floatValue7 + 0.984375F) + floatValue2;
      }
   };
   Easing EASING26 = (floatValue, floatValue2, floatValue3, floatValue4) -> floatValue3 - EASING25.ease(floatValue4 - floatValue, 0.0F, floatValue3, floatValue4) + floatValue2;
   Easing EASING27 = (floatValue, floatValue2, floatValue3, floatValue4) -> floatValue < floatValue4 / 2.0F
      ? EASING26.ease(floatValue * 2.0F, 0.0F, floatValue3, floatValue4) * 0.5F + floatValue2
      : EASING25.ease(floatValue * 2.0F - floatValue4, 0.0F, floatValue3, floatValue4) * 0.5F + floatValue3 * 0.5F + floatValue2;

   float ease(float floatValue, float floatValue2, float floatValue3, float floatValue4);

   abstract class StateHolder implements Easing {
      private float floatValue;
      private static int value;

      public StateHolder() {
         this(Float.intBitsToFloat((int)6234090506605415776L));
      }

      public StateHolder(float floatValue2) {
         this.floatValue = floatValue2;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public void setFloatValue(float floatValue2) {
         this.floatValue = floatValue2;
      }

      public static void m_1_void(int value2) {
         value = value2;
      }

      public static int m_7_int() {
         return value;
      }

      public static int m_5_int() {
         return 16;
      }
   }

   class StateHolder2 extends Easing.StateHolder4 {
      public StateHolder2(float floatValue, float floatValue2) {
         super(floatValue, floatValue2);
      }

      public StateHolder2() {
      }

      @Override
      public float ease(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
         Animation.m_8_C3_sArr();
         float floatValue5 = this.getFloatValue();
         float floatValue6 = this.getFloatValue2();
         if (floatValue == 0.0F) {
            return floatValue2;
         }

         if ((floatValue = floatValue / (floatValue4 / 2.0F)) == 2.0F) {
            return floatValue2 + floatValue3;
         }

         if (floatValue6 == 0.0F) {
            floatValue6 = floatValue4 * 0.45000002F;
         }

         float floatValue7;
         if (floatValue5 < Math.abs(floatValue3)) {
            floatValue5 = floatValue3;
            floatValue7 = floatValue6 / 4.0F;
         } else {
            floatValue7 = floatValue6 / (float) (Math.PI * 2) * (float)Math.asin(floatValue3 / floatValue5);
         }

         return floatValue < 1.0F
            ? -0.5F * (floatValue5 * (float)Math.pow(2.0, 10.0F * --floatValue) * (float)Math.sin((floatValue * floatValue4 - floatValue7) * (Math.PI * 2) / floatValue6)) + floatValue2
            : floatValue5 * (float)Math.pow(2.0, -10.0F * --floatValue) * (float)Math.sin((floatValue * floatValue4 - floatValue7) * (Math.PI * 2) / floatValue6) * 0.5F + floatValue3 + floatValue2;
      }
   }

   class StateHolder3 extends Easing.StateHolder {
      public StateHolder3() {
      }

      public StateHolder3(float floatValue) {
         super(floatValue);
      }

      @Override
      public float ease(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
         float floatValue5 = this.getFloatValue();
         float floatValue6;
         return floatValue3 * (floatValue6 = floatValue / floatValue4) * floatValue6 * ((floatValue5 + 1.0F) * floatValue6 - floatValue5) + floatValue2;
      }
   }

   abstract class StateHolder4 implements Easing {
      private float floatValue;
      private float floatValue2;
      private static String text;

      public StateHolder4(float floatValue3, float floatValue4) {
         this.floatValue = floatValue3;
         this.floatValue2 = floatValue4;
      }

      public StateHolder4() {
         this(Float.intBitsToFloat((int)-3580834464776847360L), 0.0F);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public void setFloatValue(float floatValue3) {
         this.floatValue = floatValue3;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public void setFloatValue2(float floatValue3) {
         this.floatValue2 = floatValue3;
      }

      public static void m_8_void(String text2) {
         text = text2;
      }

      public static String m_1_String() {
         return text;
      }
   }

   class StateHolder5 extends Easing.StateHolder {
      public StateHolder5() {
      }

      public StateHolder5(float floatValue) {
         super(floatValue);
      }

      @Override
      public float ease(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
         float floatValue5 = this.getFloatValue();
         float floatValue6;
         return floatValue3 * ((floatValue6 = floatValue / floatValue4 - 1.0F) * floatValue6 * ((floatValue5 + 1.0F) * floatValue6 + floatValue5) + 1.0F) + floatValue2;
      }
   }

   class StateHolder6 extends Easing.StateHolder4 {
      public StateHolder6(float floatValue, float floatValue2) {
         super(floatValue, floatValue2);
      }

      public StateHolder6() {
      }

      @Override
      public float ease(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
         Animation.m_8_C3_sArr();
         float floatValue5 = this.getFloatValue();
         float floatValue6 = this.getFloatValue2();
         if (floatValue == 0.0F) {
            return floatValue2;
         }

         if ((floatValue = floatValue / floatValue4) == 1.0F) {
            return floatValue2 + floatValue3;
         }

         if (floatValue6 == 0.0F) {
            floatValue6 = floatValue4 * 0.3F;
         }

         float floatValue7;
         if (floatValue5 < Math.abs(floatValue3)) {
            floatValue5 = floatValue3;
            floatValue7 = floatValue6 / 4.0F;
         } else {
            floatValue7 = floatValue6 / (float) (Math.PI * 2) * (float)Math.asin(floatValue3 / floatValue5);
         }

         return floatValue5 * (float)Math.pow(2.0, -10.0F * floatValue) * (float)Math.sin((floatValue * floatValue4 - floatValue7) * (Math.PI * 2) / floatValue6) + floatValue3 + floatValue2;
      }
   }

   class StateHolder7 extends Easing.StateHolder4 {
      public StateHolder7(float floatValue, float floatValue2) {
         super(floatValue, floatValue2);
      }

      public StateHolder7() {
      }

      @Override
      public float ease(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
         float floatValue5 = this.getFloatValue();
         Animation.m_8_C3_sArr();
         float floatValue6 = this.getFloatValue2();
         if (floatValue == 0.0F) {
            return floatValue2;
         }

         if ((floatValue = floatValue / floatValue4) == 1.0F) {
            return floatValue2 + floatValue3;
         }

         if (floatValue6 == 0.0F) {
            floatValue6 = floatValue4 * 0.3F;
         }

         float floatValue7;
         if (floatValue5 < Math.abs(floatValue3)) {
            floatValue5 = floatValue3;
            floatValue7 = floatValue6 / 4.0F;
         } else {
            floatValue7 = floatValue6 / (float) (Math.PI * 2) * (float)Math.asin(floatValue3 / floatValue5);
         }

         return -(floatValue5 * (float)Math.pow(2.0, 10.0F * --floatValue) * (float)Math.sin((floatValue * floatValue4 - floatValue7) * (Math.PI * 2) / floatValue6)) + floatValue2;
      }
   }

   class StateHolder8 extends Easing.StateHolder {
      public StateHolder8() {
      }

      public StateHolder8(float floatValue) {
         super(floatValue);
      }

      @Override
      public float ease(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
         Animation.m_8_C3_sArr();
         float floatValue5 = this.getFloatValue();
         float floatValue6;
         float floatValue7;
         float floatValue8;
         float floatValue9;
         return (floatValue6 = floatValue / (floatValue4 / 2.0F)) < 1.0F
            ? floatValue3 / 2.0F * (floatValue6 * floatValue6 * (((floatValue8 = floatValue5 * 1.525F) + 1.0F) * floatValue6 - floatValue8)) + floatValue2
            : floatValue3 / 2.0F * ((floatValue7 = floatValue6 - 2.0F) * floatValue7 * (((floatValue9 = floatValue5 * 1.525F) + 1.0F) * floatValue7 + floatValue9) + 2.0F) + floatValue2;
      }
   }
}
