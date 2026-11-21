package wtf.onetap.net;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SseClient {
   private static final Logger LOGGER = LogManager.getLogger(SseClient.class);
   private static final SecureRandom SECURE_RANDOM = new SecureRandom();
   private final String text;
   private final String text2;
   private final int value;
   private final String[] textArray;
   private final SecretKeySpec secretKeySpec;
   private final Set<String> stringSet;
   private final Map<String, Integer> stringMap;
   private volatile String text3;
   private volatile String text4;
   private volatile String text5;
   private volatile long longValue;

   private boolean m_3(String text6, Consumer<JsonObject> consumer) throws java.net.MalformedURLException, java.net.ProtocolException, java.io.IOException {
      String text7 = this.text3 != null && text6.equals(this.text4) ? this.text3 : String.valueOf(System.currentTimeMillis() / 1000L);
      HttpURLConnection httpURLConnection = HttpHeaders.openConnection(new URL(text6 + "/" + this.text2 + "/sse?since=" + text7));
      HttpHeaders.setTimeouts(httpURLConnection, 12000, (int)9025628461191966664L);
      httpURLConnection.setRequestMethod("GET");
      httpURLConnection.setRequestProperty("Accept", "text/event-stream");
      httpURLConnection.setRequestProperty("Connection", "keep-alive");
      int value2 = HttpHeaders.getResponseCode(httpURLConnection);
      if (value2 != 200) {
         httpURLConnection.disconnect();
         return false;
      }

      this.text5 = text6;
      this.reward(text6);

      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getStream(httpURLConnection), StandardCharsets.UTF_8))) {
         String text8;
         while ((text8 = bufferedReader.readLine()) != null) {
            if (!text8.isEmpty() && !text8.startsWith(":") && text8.startsWith("data:")) {
               this.m_2(text8.substring(5).trim(), consumer);
            }
         }

         return true;
      } catch (Exception e) {
         return false;
      } finally {
         httpURLConnection.disconnect();
      }
   }

   private void m_2(String text6, Consumer<JsonObject> consumer) {
      if (!text6.isEmpty()) {
         try {
            JsonObject jsonObject = JsonParser.parseString(text6).getAsJsonObject();
            String text7 = jsonObject.has("event") ? jsonObject.get("event").getAsString() : "";
            if (!"message".equals(text7) || !jsonObject.has("message")) {
               return;
            }

            String text8 = jsonObject.has("id") ? jsonObject.get("id").getAsString() : null;
            if (text8 != null) {
               this.text3 = text8;
               this.text4 = this.text5;
               if (!this.m_2_boolean(text8)) {
                  return;
               }
            }

            JsonObject jsonObject2 = this.m_4_JsonObject(jsonObject.get("message").getAsString());
            if (jsonObject2 == null) {
               return;
            }

            if (jsonObject2.has("n") && jsonObject2.get("n").isJsonPrimitive() && !this.m_2_boolean("n:" + jsonObject2.get("n").getAsString())) {
               return;
            }

            this.longValue = System.currentTimeMillis();
            consumer.accept(jsonObject2);
         } catch (Exception e) {
            LOGGER.debug("{} rejected relay event", this.text, e);
         }
      }
   }

   private boolean tryConnect(String text6, String text7) {
      HttpURLConnection httpURLConnection = null;

      boolean flag;
      try {
         httpURLConnection = HttpHeaders.openConnection(new URL(text6 + "/" + this.text2));
         HttpHeaders.setTimeouts(httpURLConnection, 12000, 12000);
         httpURLConnection.setRequestMethod("POST");
         httpURLConnection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
         httpURLConnection.setRequestProperty("Priority", "1");
         httpURLConnection.setDoOutput(true);
         byte[] byteArray = text7.getBytes(StandardCharsets.UTF_8);
         httpURLConnection.setFixedLengthStreamingMode(byteArray.length);

         try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
            outputStream.write(byteArray);
         }

         int value2 = HttpHeaders.getResponseCode(httpURLConnection);
         if (value2 < 200 || value2 >= 300) {
            this.penalize(text6);
            return false;
         }

         this.reward(text6);
         flag = true;
      } catch (Exception e) {
         this.penalize(text6);
         LOGGER.debug("{} publish failed on {}", this.text, text6, e);
         return false;
      } finally {
         if (httpURLConnection != null) {
            httpURLConnection.disconnect();
         }
      }

      return flag;
   }

   public boolean m_4(JsonObject jsonObject, boolean flag) {
      return this.m_5(jsonObject, flag, 3);
   }

   public boolean m_5(JsonObject jsonObject, boolean flag, int value2) {
      if (jsonObject == null) {
         return false;
      }

      if (!jsonObject.has("n")) {
         jsonObject.addProperty("n", Long.toHexString(SECURE_RANDOM.nextLong()) + Long.toHexString(SECURE_RANDOM.nextLong()));
      }

      String text6 = this.m_8_String(jsonObject.toString());
      if (text6 == null) {
         return false;
      }

      int value3 = Math.max(1, value2);

      for (int i = 0; i < value3; i++) {
         if (this.m_1_boolean(text6, flag)) {
            return true;
         }

         if (i + 1 < value3) {
            sleep(250 * (i + 1));
         }
      }

      return false;
   }

   private boolean m_8_boolean(String text6) {
      HttpURLConnection httpURLConnection = null;

      boolean flag;
      try {
         httpURLConnection = HttpHeaders.openConnection(new URL(text6 + "/" + this.text2 + "/json?poll=1&since=1s"));
         HttpHeaders.setTimeouts(httpURLConnection, 12000, 6000);
         httpURLConnection.setRequestMethod("GET");
         httpURLConnection.setRequestProperty("Accept", "application/x-ndjson");
         int value2 = HttpHeaders.getResponseCode(httpURLConnection);
         if (value2 < 200 || value2 >= 300) {
            this.penalize(text6);
            return false;
         }

         this.reward(text6);
         flag = true;
      } catch (Exception e) {
         this.penalize(text6);
         LOGGER.debug("{} warmup failed on {}", this.text, text6, e);
         return false;
      } finally {
         if (httpURLConnection != null) {
            httpURLConnection.disconnect();
         }
      }

      return flag;
   }

   private void m_7(Consumer<JsonObject> consumer) {
      int value2 = 0;
      int value3 = 1500;

      while (!Thread.currentThread().isInterrupted()) {
         var stringList = this.m_4_List();
         String text6 = (String)stringList.get(value2 % stringList.size());

         try {
            if (this.m_3(text6, consumer)) {
               value3 = 1500;
               sleep(500);
               continue;
            }
         } catch (Exception e) {
            LOGGER.debug("{} subscribe failed on {}", this.text, text6, e);
         }

         this.penalize(text6);
         value2++;
         sleep(value3);
         value3 = Math.min(value3 * 2, 15000);
      }
   }

   private void m_9(Consumer<JsonObject> consumer) {
      int value2 = 0;

      while (!Thread.currentThread().isInterrupted()) {
         var stringList = this.m_4_List();
         String text6 = (String)stringList.get(value2 % stringList.size());

         try {
            if (this.m_5(text6, consumer)) {
               this.text5 = text6;
               value2 = 0;
               sleep(this.isStale() ? 600 : 1200);
               continue;
            }
         } catch (Exception e) {
            LOGGER.debug("{} poll failed on {}", this.text, text6, e);
         }

         this.penalize(text6);
         value2++;
         sleep(this.isStale() ? 800 : 1500);
      }
   }

   private synchronized boolean m_2_boolean(String text6) {
      if (!this.stringSet.add(text6)) {
         return false;
      }

      while (this.stringSet.size() > this.value) {
         String text7 = this.stringSet.iterator().next();
         this.stringSet.remove(text7);
      }

      return true;
   }

   private void reward(String text6) {
      this.stringMap.merge(text6, 2, Integer::sum);
      int value2 = this.stringMap.getOrDefault(text6, 0);
      if (value2 > 20) {
         this.stringMap.put(text6, 20);
      }
   }

   private void penalize(String text6) {
      this.stringMap.merge(text6, -3, Integer::sum);
      int value2 = this.stringMap.getOrDefault(text6, 0);
      if (value2 < -10) {
         this.stringMap.put(text6, -10);
      }
   }

   private static void sleep(int value2) {
      try {
         Thread.sleep(value2);
      } catch (InterruptedException interruptedException) {
         Thread.currentThread().interrupt();
      }
   }

   private static SecretKeySpec m_8_SecretKeySpec(String text6) {
      try {
         byte[] byteArray = MessageDigest.getInstance("SHA-256").digest(text6.getBytes(StandardCharsets.UTF_8));

         try {
            return new SecretKeySpec(byteArray, "AES");
         } catch (Exception e) {
            return new SecretKeySpec(Arrays.copyOf(byteArray, 16), "AES");
         }
      } catch (Exception e2) {
         throw new IllegalStateException("relay crypto init failed", e2);
      }
   }

   private boolean m_1_boolean(String text6, boolean flag) {
      if (!flag) {
         if (this.tryConnect(this.text5, text6)) {
            return true;
         }

         for (String text7 : this.m_4_List()) {
            if (!text7.equals(this.text5) && this.tryConnect(text7, text6)) {
               this.text5 = text7;
               return true;
            }
         }

         return false;
      } else {
         boolean flag2 = false;

         for (String text8 : this.m_4_List()) {
            if (this.tryConnect(text8, text6)) {
               this.text5 = text8;
               flag2 = true;
            }
         }

         return flag2;
      }
   }

   private void m_6_void() {
      for (String text6 : this.m_4_List()) {
         if (this.m_8_boolean(text6)) {
            this.text5 = text6;
            return;
         }

         sleep(250);
      }
   }

   public void m_7(String text6, Consumer<JsonObject> consumer) {
      Thread thread = new Thread(() -> {
         for (String text7 : this.m_4_List()) {
            try {
               if (this.m_8(text7, text6, consumer)) {
                  this.text5 = text7;
                  this.reward(text7);
                  return;
               }
            } catch (Exception e) {
               LOGGER.debug("{} history fetch failed on {}", this.text, text7, e);
            }

            this.penalize(text7);
         }
      }, this.text + "-history");
      thread.setDaemon(true);
      thread.start();
   }

   private List<String> m_4_List() {
      ArrayList list = new ArrayList<>(Arrays.asList(this.textArray));
      list.sort(Comparator.<String>comparingInt(text6 -> this.stringMap.getOrDefault(text6, 0)).reversed().thenComparing(text7 -> text7.equals(this.text5) ? 0 : 1));
      return list;
   }

   private boolean m_8(String text6, String text7, Consumer<JsonObject> consumer) throws java.io.IOException, java.net.MalformedURLException, java.net.ProtocolException {
      String text8 = text7 != null && !text7.isBlank() ? text7 : "1h";
      HttpURLConnection httpURLConnection = HttpHeaders.openConnection(new URL(text6 + "/" + this.text2 + "/json?since=" + text8));
      HttpHeaders.setTimeouts(httpURLConnection, 12000, 15000);
      httpURLConnection.setRequestMethod("GET");
      httpURLConnection.setRequestProperty("Accept", "application/x-ndjson");
      int value2 = HttpHeaders.getResponseCode(httpURLConnection);
      if (value2 != 200) {
         httpURLConnection.disconnect();
         return false;
      }

      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getStream(httpURLConnection), StandardCharsets.UTF_8))) {
         String text9;
         while ((text9 = bufferedReader.readLine()) != null) {
            if (!text9.isEmpty()) {
               this.m_2(text9.trim(), consumer);
            }
         }

         return true;
      } finally {
         httpURLConnection.disconnect();
      }
   }

   private boolean isStale() {
      return System.currentTimeMillis() - this.longValue > 45000L;
   }

   private static byte[] base64Decode(String text6) {
      try {
         return Base64.getUrlDecoder().decode(text6);
      } catch (IllegalArgumentException illegalArgumentException) {
         try {
            return Base64.getDecoder().decode(text6);
         } catch (IllegalArgumentException illegalArgumentException2) {
            return null;
         }
      }
   }

   public SseClient(String text6, String text7, String text8, int value2, String[] stringArray) {
      this.stringSet = new LinkedHashSet<>();
      this.stringMap = new ConcurrentHashMap<>();
      this.longValue = System.currentTimeMillis();
      this.text = text6;
      this.text2 = text7;
      this.value = Math.max(32, value2);
      this.textArray = (String[])stringArray.clone();
      this.secretKeySpec = m_8_SecretKeySpec(text8);
      this.text5 = this.textArray[0];

      for (String text9 : this.textArray) {
         this.stringMap.put(text9, 0);
      }
   }

   private static InputStream getStream(HttpURLConnection httpURLConnection) throws java.io.IOException {
      try {
         return httpURLConnection.getInputStream();
      } catch (IOException e) {
         InputStream inputStream = httpURLConnection.getErrorStream();
         if (inputStream != null) {
            return inputStream;
         } else {
            throw e;
         }
      }
   }

   public void m_6(Consumer<JsonObject> consumer) {
      Thread thread = new Thread(this::m_6_void, this.text + "-warmup");
      thread.setDaemon(true);
      thread.start();
      Thread thread2 = new Thread(() -> this.m_7(consumer), this.text);
      thread2.setDaemon(true);
      thread2.start();
      Thread thread3 = new Thread(() -> this.m_9(consumer), this.text + "-poll");
      thread3.setDaemon(true);
      thread3.start();
   }

   private boolean m_5(String text6, Consumer<JsonObject> consumer) throws java.io.IOException, java.net.MalformedURLException, java.net.ProtocolException {
      String text7 = this.text3 != null ? this.text3 : "2m";
      HttpURLConnection httpURLConnection = HttpHeaders.openConnection(new URL(text6 + "/" + this.text2 + "/json?poll=1&since=" + text7));
      HttpHeaders.setTimeouts(httpURLConnection, 12000, 12000);
      httpURLConnection.setRequestMethod("GET");
      httpURLConnection.setRequestProperty("Accept", "application/x-ndjson");
      int value2 = HttpHeaders.getResponseCode(httpURLConnection);
      if (value2 != 200) {
         httpURLConnection.disconnect();
         return false;
      }

      this.reward(text6);

      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getStream(httpURLConnection), StandardCharsets.UTF_8))) {
         String text8;
         while ((text8 = bufferedReader.readLine()) != null) {
            if (!text8.isEmpty()) {
               this.m_2(text8.trim(), consumer);
            }
         }

         return true;
      } finally {
         httpURLConnection.disconnect();
      }
   }

   private JsonObject m_4_JsonObject(String text6) {
      try {
         byte[] byteArray = base64Decode(text6);
         if (byteArray != null && byteArray.length > 12) {
            byte[] byteArray2 = Arrays.copyOfRange(byteArray, 0, 12);
            byte[] byteArray3 = Arrays.copyOfRange(byteArray, 12, byteArray.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(2, this.secretKeySpec, new GCMParameterSpec(128, byteArray2));
            String text7 = new String(cipher.doFinal(byteArray3), StandardCharsets.UTF_8);
            return JsonParser.parseString(text7).getAsJsonObject();
         } else {
            return null;
         }
      } catch (Exception e) {
         return null;
      }
   }

   private String m_8_String(String text6) {
      try {
         byte[] byteArray = new byte[12];
         SECURE_RANDOM.nextBytes(byteArray);
         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
         cipher.init(1, this.secretKeySpec, new GCMParameterSpec(128, byteArray));
         byte[] byteArray2 = cipher.doFinal(text6.getBytes(StandardCharsets.UTF_8));
         byte[] byteArray3 = new byte[byteArray.length + byteArray2.length];
         System.arraycopy(byteArray, 0, byteArray3, 0, byteArray.length);
         System.arraycopy(byteArray2, 0, byteArray3, byteArray.length, byteArray2.length);
         return Base64.getUrlEncoder().withoutPadding().encodeToString(byteArray3);
      } catch (Exception e) {
         LOGGER.error("{} encrypt failed", this.text, e);
         return null;
      }
   }
}
