package wtf.onetap.bot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BotManager {
   public static final BotManager BOT_MANAGER = new BotManager();
   private final Map<String, BotClient> stringMap;
   private BotClient botClient;
   private BotClient botClient2;
   private BotClient botClient3;
   private BotManager.CallbackType c7;
   private boolean flag;
   private static int[] valueArray;

   public BotManager() {
      this.stringMap = new LinkedHashMap<>();
      this.c7 = new BotManager.CallbackType() {};
   }

   public void m_2(BotManager.CallbackType c72) {
      this.c7 = c72 == null ? new BotManager.CallbackType() {} : c72;
   }

   public boolean m_6_boolean(String text, String text2) {
      return this.connect(text, text2, null);
   }

   public boolean connect(String text, String text2, ProxyInfo proxyInfo) {
      if (text == null || text.isBlank() || text2 == null || text2.isBlank()) {
         return false;
      }

      if (this.getBot(text) != null) {
         return false;
      }

      this.m_4_void();
      BotClient botClient4 = new BotClient(text, text2, proxyInfo);
      this.stringMap.put(normalize(text), botClient4);
      this.botClient3 = botClient4;
      this.c7.m_8(botClient4);
      return true;
   }

   public boolean disconnect(String text) {
      if (text != null && !text.isBlank()) {
         BotClient botClient4 = this.getBot(text);
         if (botClient4 == null) {
            return false;
         }

         if (botClient4 == this.botClient2) {
            return true;
         }

         this.m_4_void();
         this.m_2_void();
         this.botClient2 = botClient4;
         this.c7.m_9(botClient4);
         return true;
      } else {
         return false;
      }
   }

   public boolean m_9_boolean() {
      if (this.botClient != null && this.botClient2 != this.botClient) {
         this.m_2_void();
         this.botClient2 = this.botClient;
         this.c7.m_9(this.botClient);
         return true;
      } else {
         return true;
      }
   }

   public List<String> getNames() {
      ArrayList list = new ArrayList(this.stringMap.size());

      for (BotClient botClient4 : this.stringMap.values()) {
         list.add(botClient4.getText());
      }

      return list;
   }

   public String m_1_String() {
      return this.botClient2 != null && !this.botClient2.m_7_boolean() ? this.botClient2.getText() : null;
   }

   public boolean m_6_boolean() {
      return this.botClient2 == null || this.botClient2.m_7_boolean();
   }

   public BotClient findBot(String text) {
      return this.getBot(text);
   }

   public BotClient getBotClient() {
      return this.botClient;
   }

   public BotClient getBotClient2() {
      return this.botClient2;
   }

   public boolean isCurrent(BotClient botClient4) {
      return botClient4 != null && botClient4 == this.botClient2;
   }

   public boolean m_1_boolean() {
      return !BotConnection.BOT_CONNECTION.m_9_boolean();
   }

   public void m_9_void() {
      if (this.botClient != null && !this.flag) {
         this.flag = true;

         try {
            if (this.botClient != this.botClient2) {
               this.botClient.m_2_void();
               this.c7.m_0(this.botClient);
            }

            for (BotClient botClient4 : this.stringMap.values()) {
               if (botClient4 != this.botClient2) {
                  botClient4.m_2_void();
                  this.c7.m_0(botClient4);
               }
            }

            if (this.botClient2 != null && !this.botClient2.m_7_boolean()) {
               this.c7.m_4(this.botClient2);
            }
         } finally {
            this.flag = false;
         }
      }
   }

   public Boolean toggleBot(String text) {
      BotClient botClient4 = this.getBot(text);
      return botClient4 == null ? null : botClient4.toggle();
   }

   public boolean m_9_boolean(String text) {
      BotClient botClient4 = this.getBot(text);
      if (botClient4 == null) {
         return false;
      }

      if (botClient4 == this.botClient2) {
         this.m_9_boolean();
      }

      if (this.botClient3 == botClient4) {
         this.botClient3 = null;
      }

      this.stringMap.remove(normalize(text));
      this.c7.m_7(botClient4);
      return true;
   }

   public void m_7_void() {
      if (this.botClient != null && this.botClient2 != this.botClient) {
         this.m_9_boolean();
      }

      this.botClient3 = null;
      ArrayList list = new ArrayList<>(this.stringMap.values());
      this.stringMap.clear();

      for (BotClient botClient4 : (Iterable<BotClient>)list) {
         this.c7.m_7(botClient4);
      }
   }

   public boolean sendMessage(String text, String text2) {
      if (text == null || text.isBlank() || text2 == null || text2.isBlank()) {
         return false;
      }

      if ("all".equalsIgnoreCase(text)) {
         boolean flag2 = false;

         for (BotClient botClient4 : this.stringMap.values()) {
            flag2 |= this.c7.m_8(botClient4, text2);
         }

         return flag2;
      } else {
         BotClient botClient5 = this.getBot(text);
         return botClient5 != null && this.c7.m_8(botClient5, text2);
      }
   }

   public void m_5(BotClient botClient4) {
      if (this.botClient3 == botClient4 && botClient4 != null && botClient4 != this.botClient2) {
         this.botClient3 = null;
         this.disconnect(botClient4.getText());
      }
   }

   public void clear() {
      this.stringMap.clear();
      this.botClient = null;
      this.botClient2 = null;
      this.botClient3 = null;
      this.flag = false;
   }

   private void m_4_void() {
      if (this.botClient == null) {
         this.botClient = new BotClient(null, null);
         this.botClient2 = this.botClient;
         this.c7.m_3(this.botClient);
      }
   }

   private void m_2_void() {
      if (this.botClient2 != null) {
         this.c7.m_3(this.botClient2);
         this.c7.resetInput();
      }
   }

   private BotClient getBot(String text) {
      return this.stringMap.get(normalize(text));
   }

   private static String normalize(String text) {
      return text.toLowerCase(Locale.ROOT);
   }

   public static void m_9(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_1_IArr() {
      return valueArray;
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

   public interface CallbackType {
      default void m_3(BotClient botClient4) {
      }

      default void m_9(BotClient botClient4) {
      }

      default void m_8(BotClient botClient4) {
      }

      default void m_0(BotClient botClient4) {
      }

      default void m_7(BotClient botClient4) {
      }

      default void resetInput() {
      }

      default boolean m_8(BotClient botClient4, String text) {
         return true;
      }

      default void m_4(BotClient botClient4) {
      }
   }
}
