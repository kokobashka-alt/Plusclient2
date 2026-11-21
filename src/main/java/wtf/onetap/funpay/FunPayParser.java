package wtf.onetap.funpay;

import wtf.onetap.util.TimeHelper;

public class FunPayParser {
   private final String text;
   private final long longValue;
   private final String text2;
   private final String text3;
   private TimeHelper timeHelper;
   private FunPayParser.ModeKind c2;
   private String text4;
   private boolean flag;

   public FunPayParser(String text5, long longValue2, String text6, String text7) {
      this.timeHelper = new TimeHelper();
      this.text = text5;
      this.longValue = longValue2;
      this.text2 = text6;
      this.text3 = text7;
   }

   public void setC2(FunPayParser.ModeKind c22) {
      this.c2 = c22;
   }

   public FunPayParser.ModeKind getC2() {
      return this.c2;
   }

   public void setText4(String text5) {
      this.text4 = text5;
   }

   public String getText4() {
      return this.text4;
   }

   public void setTimeHelper(TimeHelper timeHelper2) {
      this.timeHelper = timeHelper2;
   }

   public TimeHelper getTimeHelper() {
      return this.timeHelper;
   }

   public String getText() {
      return this.text;
   }

   public long getLongValue() {
      return this.longValue;
   }

   public String getText2() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public void setFlag(boolean flag2) {
      this.flag = flag2;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public enum ModeKind {
      NICKNAME,
      CONFIRM_NICKNAME,
      DELIVERY;

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
}
