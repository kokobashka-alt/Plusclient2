package wtf.onetap.bot;

import wtf.onetap.module.misc.TapeMouse;

public class BotClient {
   private final String text;
   private final String text2;
   private final ProxyInfo proxyInfo;
   private int value;
   private boolean flag;
   private boolean flag2;
   private long longValue;

   public BotClient(String text3, String text4) {
      this(text3, text4, null);
   }

   public BotClient(String text3, String text4, ProxyInfo proxyInfo2) {
      this.flag = true;
      this.text = text3;
      this.text2 = text4;
      this.proxyInfo = proxyInfo2;
   }

   public String getText() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public ProxyInfo getProxyInfo() {
      return this.proxyInfo;
   }

   public int getValue() {
      return this.value;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void m_2_void() {
      this.value++;
   }

   public void setFlag(boolean flag3) {
      this.flag = flag3;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public boolean toggle() {
      this.flag2 = !this.flag2;
      if (this.flag2) {
         this.longValue = 0L;
      }

      return this.flag2;
   }

   public boolean m_5(float floatValue, float floatValue2, long longValue2) {
      if (!this.flag2) {
         return false;
      }

      if (!TapeMouse.m_3(floatValue, floatValue2, this.longValue, longValue2)) {
         return false;
      }

      this.longValue = longValue2;
      return true;
   }

   public boolean m_7_boolean() {
      return this.text == null;
   }
}
