package wtf.onetap.net;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {
   public static final HtmlParser HTML_PARSER = new HtmlParser();
   private static final Pattern PATTERN = Pattern.compile("^/(?!/)[A-Za-z0-9/_\\-.%?=&#]*$");
   private static final Pattern PATTERN2 = Pattern.compile("(csrf-token&quot;:&quot;)([^&]*)(&quot;)");
   private static final Pattern PATTERN3 = Pattern.compile("(\"csrf-token\"\\s*:\\s*\")([^\"]*)(\")");
   private static final Pattern PATTERN4 = Pattern.compile("(name=\"csrf_token\"[^>]*value=\")([^\"]*)(\")");
   private final HttpClient httpClient;
   private String text;
   private volatile String text2;
   private volatile String text3;
   private volatile String text4;

   private HtmlParser() {
      this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5L)).followRedirects(Redirect.NORMAL).build();
      this.text = "";
   }

   public void upload(String text5, String text6, String text7, JsonObject jsonObject, BiConsumer<Integer, String> biConsumer) {
      throw new SecurityException("Disabled by local privacy patch: screen capture or outbound upload");
   }

   public void upload(String text5, byte[] byteArray, BiConsumer<Integer, String> biConsumer) {
      throw new SecurityException("Disabled by local privacy patch: screen capture or outbound upload");
   }

   private void upload(HttpRequest httpRequest, BiConsumer<Integer, String> biConsumer) {
      throw new SecurityException("Disabled by local privacy patch: screen capture or outbound upload");
   }

   private synchronized void m_1_void(String text5) {
      if (!text5.equals(this.text)) {
         this.text = text5;
         this.text2 = null;
         this.text3 = null;
         this.text4 = null;
      }
   }

   private String m_3_String(String text5) {
      StringBuilder stringBuilder = new StringBuilder("golden_key=").append(text5);
      if (this.text2 != null) {
         stringBuilder.append("; PHPSESSID=").append(this.text2);
      }

      if (this.text3 != null) {
         stringBuilder.append("; golden_seal=").append(this.text3);
      }

      return stringBuilder.toString();
   }

   private void m_8_void(List<String> stringList) {
      for (String text5 : stringList) {
         String text6 = text5.split(";", 2)[0];
         int value = text6.indexOf(61);
         if (value > 0) {
            String text7 = text6.substring(0, value).trim();
            String text8 = text6.substring(value + 1).trim();
            if (text7.equals("PHPSESSID")) {
               this.text2 = text8;
            } else if (text7.equals("golden_seal")) {
               this.text3 = text8;
            }
         }
      }
   }

   private String m_0_String(String text5) {
      for (Pattern pattern : new Pattern[]{PATTERN2, PATTERN3, PATTERN4}) {
         Matcher matcher = pattern.matcher(text5);
         StringBuilder stringBuilder = new StringBuilder();
         boolean flag = false;

         while (matcher.find()) {
            flag = true;
            if (!matcher.group(2).isEmpty()) {
               this.text4 = matcher.group(2);
            }

            matcher.appendReplacement(stringBuilder, Matcher.quoteReplacement(matcher.group(1) + matcher.group(3)));
         }

         if (flag) {
            matcher.appendTail(stringBuilder);
            text5 = stringBuilder.toString();
         }
      }

      String text6 = this.text;
      return text6.isEmpty() ? text5 : text5.replace(text6, "");
   }

   private static String m_2_String(String text5, String text6) {
      return URLEncoder.encode(text5, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(text6, StandardCharsets.UTF_8);
   }

   private static String m_9(JsonElement jsonElement) {
      return jsonElement != null && !jsonElement.isJsonNull() ? (jsonElement.isJsonPrimitive() ? jsonElement.getAsString() : jsonElement.toString()) : "";
   }

   private static void m_1(ByteArrayOutputStream byteArrayOutputStream, String text5) {
      byteArrayOutputStream.writeBytes(text5.getBytes(StandardCharsets.UTF_8));
   }
}
