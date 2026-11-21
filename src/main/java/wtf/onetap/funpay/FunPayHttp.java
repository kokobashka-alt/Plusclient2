package wtf.onetap.funpay;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.function.Supplier;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.onetap.util.ChatUtil;

public class FunPayHttp {
   private static final Logger LOGGER = LoggerFactory.getLogger("FunDeliver");
   private final FunPayHttp.StateHolder c1;
   private final FunPayChat funPayChat;
   private final FunPayClient funPayClient;
   private final Supplier<String> supplier;
   private final Supplier<Float> supplier2;
   private final Supplier<Float> supplier3;
   private final Supplier<Boolean> supplier4;
   private final Supplier<Long> supplier5;
   private final Supplier<Boolean> supplier6;
   private static boolean flag;

   public FunPayHttp(Supplier<String> supplier7, Supplier<Float> supplier8, Supplier<Float> supplier9, Supplier<Boolean> supplier10, Supplier<Long> supplier11, Supplier<Boolean> supplier12) {
      this.c1 = new FunPayHttp.StateHolder();
      this.funPayChat = new FunPayChat(this);
      this.funPayClient = new FunPayClient(this);
      this.supplier = supplier7;
      this.supplier2 = supplier8;
      this.supplier3 = supplier9;
      this.supplier4 = supplier10;
      this.supplier5 = supplier11;
      this.supplier6 = supplier12;
   }

   public long m_5_long() {
      Long longObj = this.supplier5.get();
      return longObj == null ? -1L : longObj;
   }

   public boolean m_8_boolean() {
      return this.supplier4.get();
   }

   public boolean m_6_boolean() {
      return this.supplier6.get();
   }

   public FunPayHttp.StateHolder getC1() {
      return this.c1;
   }

   public FunPayChat getFunPayChat() {
      return this.funPayChat;
   }

   public FunPayClient getFunPayClient() {
      return this.funPayClient;
   }

   public float m_1_float() {
      return this.supplier2.get();
   }

   public float m_6_float() {
      return this.supplier3.get();
   }

   public long m_1_long() {
      return Long.parseLong(this.c1.text);
   }

   private Connection m_9_Connection(String text) {
      Connection connection = Jsoup.connect(text)
         .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
         .ignoreContentType(true)
         .header("accept-language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
         .cookie("golden_key", this.supplier.get())
         .timeout(5000);
      if (this.c1.text3 != null) {
         connection.cookie("PHPSESSID", this.c1.text3);
      }

      if (this.c1.text4 != null) {
         connection.cookie("golden_seal", this.c1.text4);
      }

      return connection;
   }

   public boolean m_4_boolean() {
      return this.c1.text != null;
   }

   public boolean m_2_boolean() {
      if (this.c1.text != null) {
         return true;
      }

      try {
         Response response = this.m_9_Connection("https://funpay.com/").execute();
         Document document = response.parse();
         Element element = document.selectFirst(".user-link-name");
         if (element == null) {
            return false;
         }

         JsonObject jsonObject = JsonParser.parseString(document.body().attr("data-app-data")).getAsJsonObject();
         this.c1.text = jsonObject.get("userId").getAsString();
         this.c1.text5 = jsonObject.get("csrf-token").getAsString();
         this.c1.text3 = response.cookie("PHPSESSID");
         this.c1.text4 = response.cookie("golden_seal");
         this.c1.text2 = element.text();
         this.funPayChat.setFunPayParserList(null);
         LOGGER.info("login OK: user={} id={}", this.c1.text2, this.c1.text);
         return true;
      } catch (Exception e) {
         LOGGER.warn("login failed", e);
         return false;
      }
   }

   public void m_1_void() {
      try {
         JsonArray jsonArray = new JsonArray();
         jsonArray.add(this.funPayChat.m_7_JsonObject());
         jsonArray.add(this.funPayClient.m_2_JsonObject());
         JsonObject jsonObject = JsonParser.parseString(this.m_8_Response(jsonArray.toString(), String.valueOf(false)).body()).getAsJsonObject();
         JsonArray jsonArray2 = jsonObject.getAsJsonArray("objects");

         for (JsonElement jsonElement : jsonArray2) {
            JsonObject jsonObject2 = jsonElement.getAsJsonObject();
            if ("orders_counters".equalsIgnoreCase(jsonObject2.get("type").getAsString())) {
               this.funPayClient.m_1_void(jsonObject2);
            }
         }

         for (JsonElement jsonElement2 : jsonArray2) {
            JsonObject jsonObject3 = jsonElement2.getAsJsonObject();
            if ("chat_bookmarks".equalsIgnoreCase(jsonObject3.get("type").getAsString())) {
               this.funPayChat.m_1_void(jsonObject3);
            }
         }

         this.funPayChat.m_2_void();
      } catch (Exception e) {
         LOGGER.warn("poll error", e);
         if (this.supplier4.get()) {
            ChatUtil.log("§c[Fun Deliver] Ошибка опроса FunPay: §f" + e);
         }
      }
   }

   public Response m_8_Response(String text, String text2) throws java.io.IOException {
      return this.m_9_Connection("https://funpay.com/runner/")
         .header("accept", "*/*")
         .header("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
         .header("x-requested-with", "XMLHttpRequest")
         .method(Method.POST)
         .data("objects", text == null ? "" : text)
         .data("request", text2 == null ? "" : text2)
         .data("csrf_token", this.c1.text5 == null ? "" : this.c1.text5)
         .followRedirects(true)
         .execute();
   }

   public Response get(String text) throws java.io.IOException {
      return this.m_9_Connection(text).method(Method.GET).execute();
   }

   public void m_3_void(String text) {
      try {
         Document document = this.m_9_Connection("https://funpay.com/orders/" + (text.startsWith("#") ? text.substring(1) : text) + "/").get();
         Element element = document.selectFirst(".modal-refund form");
         if (element == null) {
            return;
         }

         String text2 = element.attr("action");
         Connection connection = this.m_9_Connection(text2.startsWith("http") ? text2 : "https://funpay.com" + text2).method(Method.POST).followRedirects(true);

         for (Element element2 : element.select("input[type=hidden]")) {
            connection.data(element2.attr("name"), element2.attr("value"));
         }

         connection.data("csrf_token", this.c1.text5).execute();
      } catch (Exception e) {
      }
   }

   public Response m_8_Response(String text) throws java.io.IOException {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("type", "chat_node");
      jsonObject.addProperty("id", text);
      jsonObject.addProperty("tag", "00000000");
      JsonObject jsonObject2 = new JsonObject();
      jsonObject2.addProperty("node", text);
      jsonObject2.addProperty("last_message", -1);
      jsonObject2.addProperty("content", "");
      jsonObject.add("data", jsonObject2);
      JsonArray jsonArray = new JsonArray();
      jsonArray.add(jsonObject);
      return this.m_8_Response(jsonArray.toString(), String.valueOf(false));
   }

   public void sendChatMessage(String text, String text2) {
      try {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("action", "chat_message");
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("node", text);
         jsonObject2.addProperty("last_message", -1);
         jsonObject2.addProperty("content", text2);
         jsonObject.add("data", jsonObject2);
         JsonObject jsonObject3 = new JsonObject();
         jsonObject3.addProperty("type", "chat_node");
         jsonObject3.addProperty("id", text);
         jsonObject3.addProperty("tag", "00000000");
         JsonObject jsonObject4 = new JsonObject();
         jsonObject4.addProperty("node", text);
         jsonObject4.addProperty("last_message", -1);
         jsonObject4.addProperty("content", "");
         jsonObject3.add("data", jsonObject4);
         JsonArray jsonArray = new JsonArray();
         jsonArray.add(jsonObject3);
         this.m_8_Response(jsonArray.toString(), jsonObject.toString());
      } catch (Exception e) {
         LOGGER.warn("send message to {} failed", text, e);
         if (this.supplier4.get()) {
            ChatUtil.log("§c[Fun Deliver] Не удалось отправить сообщение покупателю: §f" + e);
         }
      }
   }

   public void upload(String text, byte[] byteArray, String text2) {
      throw new SecurityException("Disabled by local privacy patch: screen capture or outbound upload");
   }

   private String upload(byte[] byteArray, String text) {
      throw new SecurityException("Disabled by local privacy patch: screen capture or outbound upload");
   }

   static {
      m_4(true);
   }

   public static void m_4(boolean flag2) {
      flag = flag2;
   }

   public static boolean m_0_boolean() {
      return flag;
   }

   public static boolean m_9_boolean() {
      return false;
   }

   public static final class StateHolder {
      public String text;
      public String text2;
      public String text3;
      public String text4;
      public String text5;

      public String getText2() {
         return this.text2;
      }
   }
}
