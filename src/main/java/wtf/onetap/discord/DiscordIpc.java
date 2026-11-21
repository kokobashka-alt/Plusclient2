package wtf.onetap.discord;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.UUID;

public class DiscordIpc implements AutoCloseable {
   private final DiscordIpc.CallbackType c3;
   private JsonObject jsonObject;
   private byte[] byteValueArray;

   private DiscordIpc(DiscordIpc.CallbackType c32) {
      this.byteValueArray = new byte[0];
      this.c3 = c32;
   }

   public static DiscordIpc m_6(String text, long longValue) throws wtf.onetap.discord.DiscordIpc.StateHolder2 {
      DiscordIpc.CallbackType c32 = isWindows() ? WindowsPipe.m_1_C6u() : m_0_C3();
      DiscordIpc discordIpc = new DiscordIpc(c32);

      try {
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("v", 1);
         jsonObject2.addProperty("client_id", text);
         discordIpc.sendFrame(0, jsonObject2);
         long longValue2 = System.currentTimeMillis() + longValue;

         while (System.currentTimeMillis() < longValue2) {
            JsonObject jsonObject3 = discordIpc.readMessage();
            if (jsonObject3 == null) {
               Thread.sleep(20L);
            } else if ("DISPATCH".equals(getStringOrNull(jsonObject3, "cmd")) && "READY".equals(getStringOrNull(jsonObject3, "evt"))) {
               JsonObject jsonObject4 = jsonObject3.has("data") && jsonObject3.get("data").isJsonObject() ? jsonObject3.getAsJsonObject("data") : new JsonObject();
               discordIpc.jsonObject = jsonObject4.has("user") && jsonObject4.get("user").isJsonObject() ? jsonObject4.getAsJsonObject("user") : new JsonObject();
               return discordIpc;
            }
         }

         c32.close();
         long longValue3 = longValue / 1000L;
         String text2 = c32.getText();
         long longValue4 = longValue3;
         throw new DiscordIpc.StateHolder2("Discord не ответил на подключение за " + longValue4 + " с (" + text2 + ")");
      } catch (DiscordIpc.StateHolder3 c8) {
         c32.close();
         throw new DiscordIpc.StateHolder2("Discord закрыл подключение: " + c8.getMessage());
      } catch (IOException e) {
         c32.close();
         throw new DiscordIpc.StateHolder2("обрыв при подключении к Discord (" + c32.getText() + "): " + e.getMessage());
      } catch (InterruptedException interruptedException) {
         c32.close();
         Thread.currentThread().interrupt();
         throw new DiscordIpc.StateHolder2("подключение прервано");
      }
   }

   public JsonObject getJsonObject() {
      return this.jsonObject;
   }

   public String m_0_String() {
      return this.c3.getText();
   }

   public String buildActivity(long longValue, JsonObject jsonObject2) throws java.io.IOException {
      JsonObject jsonObject3 = new JsonObject();
      jsonObject3.addProperty("pid", longValue);
      if (jsonObject2 != null) {
         jsonObject3.add("activity", jsonObject2);
      }

      JsonObject jsonObject4 = new JsonObject();
      jsonObject4.addProperty("cmd", "SET_ACTIVITY");
      jsonObject4.add("args", jsonObject3);
      String text = UUID.randomUUID().toString();
      jsonObject4.addProperty("nonce", text);
      this.sendFrame(1, jsonObject4);
      return text;
   }

   public JsonObject readMessage() throws java.io.IOException, wtf.onetap.discord.DiscordIpc.StateHolder3 {
      while (true) {
         byte[] byteArray = new byte[8192];
         int value = this.c3.read(byteArray);
         if (value > 0) {
            byte[] byteArray2 = new byte[this.byteValueArray.length + value];
            System.arraycopy(this.byteValueArray, 0, byteArray2, 0, this.byteValueArray.length);
            System.arraycopy(byteArray, 0, byteArray2, this.byteValueArray.length, value);
            this.byteValueArray = byteArray2;
            if (value >= byteArray.length) {
               continue;
            }
         }

         while (this.byteValueArray.length >= 8) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(this.byteValueArray, 0, 8).order(ByteOrder.LITTLE_ENDIAN);
            value = byteBuffer.getInt();
            int value2 = byteBuffer.getInt();
            if (value2 < 0 || value2 > (int)-7560664908081135616L) {
               throw new IOException("кадр неверной длины " + value2);
            }

            if (this.byteValueArray.length < 8 + value2) {
               return null;
            }

            String text = new String(this.byteValueArray, 8, value2, StandardCharsets.UTF_8);
            byte[] byteArray3 = new byte[this.byteValueArray.length - 8 - value2];
            System.arraycopy(this.byteValueArray, 8 + value2, byteArray3, 0, byteArray3.length);
            this.byteValueArray = byteArray3;
            switch (value) {
               case 1:
                  return JsonParser.parseString(text).getAsJsonObject();
               case 2:
                  JsonObject jsonObject2 = JsonParser.parseString(text).getAsJsonObject();
                  throw new DiscordIpc.StateHolder3(getStringOrNull(jsonObject2, "code") + " " + getStringOrNull(jsonObject2, "message"));
               case 3:
                  this.sendFrame(4, JsonParser.parseString(text).getAsJsonObject());
            }
         }

         return null;
      }
   }

   private void sendFrame(int value, JsonObject jsonObject2) throws java.io.IOException {
      byte[] byteArray = jsonObject2.toString().getBytes(StandardCharsets.UTF_8);
      ByteBuffer byteBuffer = ByteBuffer.allocate(8 + byteArray.length).order(ByteOrder.LITTLE_ENDIAN);
      byteBuffer.putInt(value).putInt(byteArray.length).put(byteArray);
      this.c3.write(byteBuffer.array());
   }

   @Override
   public void close() {
      try {
         this.sendFrame(2, new JsonObject());
      } catch (IOException e) {
      }

      this.c3.close();
   }

   public static String getStringOrNull(JsonObject jsonObject2, String text) {
      return jsonObject2 != null && jsonObject2.has(text) && !jsonObject2.get(text).isJsonNull() ? jsonObject2.get(text).getAsString() : null;
   }

   public static boolean isWindows() {
      return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("windows");
   }

   private static DiscordIpc.CallbackType m_0_C3() throws wtf.onetap.discord.DiscordIpc.StateHolder2 {
      DiscordPresence.m_1_String();
      LinkedHashSet<String> stringSet = new LinkedHashSet<>();

      for (String text : new String[]{"XDG_RUNTIME_DIR", "TMPDIR", "TMP", "TEMP"}) {
         String text2 = System.getenv(text);
         if (text2 != null && !text2.isBlank()) {
            stringSet.add(text2);
         }
      }

      stringSet.add("/tmp");
      String[] stringArray = new String[]{
         "",
         "app/com.discordapp.Discord/",
         "app/com.discordapp.DiscordCanary/",
         "snap.discord/",
         "snap.discord-canary/",
         ".flatpak/dev.vencord.Vesktop/xdg-run/"
      };
      ArrayList list = new ArrayList();

      for (String text3 : stringSet) {
         for (String text4 : stringArray) {
            for (int i = 0; i < 10; i++) {
               Path path = Path.of(text3, text4 + "discord-ipc-" + i);
               if (Files.exists(path)) {
                  try {
                     SocketChannel socketChannel = SocketChannel.open(StandardProtocolFamily.UNIX);

                     try {
                        socketChannel.connect(UnixDomainSocketAddress.of(path));
                        socketChannel.configureBlocking(false);
                     } catch (IOException e) {
                        socketChannel.close();
                        throw e;
                     }

                     return new DiscordIpc.StateHolder(socketChannel, path.toString());
                  } catch (IOException e2) {
                     list.add(path + ": " + e2.getMessage());
                  }
               }
            }
         }
      }

      if (list.isEmpty()) {
         throw new DiscordIpc.StateHolder2("Discord не запущен: нет сокета discord-ipc-N в " + String.join(", ", stringSet));
      } else {
         throw new DiscordIpc.StateHolder2("не удалось подключиться к Discord: " + String.join("; ", list));
      }
   }

   private static final class StateHolder implements DiscordIpc.CallbackType {
      private final SocketChannel socketChannel;
      private final String text;

      public StateHolder(SocketChannel socketChannel2, String text2) {
         this.socketChannel = socketChannel2;
         this.text = text2;
      }

      @Override
      public int read(byte[] byteArray) throws java.io.IOException {
         DiscordPresence.m_1_String();
         int value = this.socketChannel.read(ByteBuffer.wrap(byteArray));
         if (value < 0) {
            throw new IOException("Discord закрыл сокет");
         } else {
            return value;
         }
      }

      @Override
      public void write(byte[] byteArray) throws java.io.IOException {
         ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
         DiscordPresence.m_1_String();
         long longValue = System.currentTimeMillis() + 2000L;

         while (byteBuffer.hasRemaining()) {
            if (this.socketChannel.write(byteBuffer) == 0) {
               if (System.currentTimeMillis() > longValue) {
                  throw new IOException("Discord не принимает данные");
               }

               Thread.onSpinWait();
            }
         }
      }

      @Override
      public String getText() {
         return this.text;
      }

      @Override
      public void close() {
         try {
            this.socketChannel.close();
         } catch (IOException e) {
         }
      }
   }

   public interface CallbackType extends AutoCloseable {
      int read(byte[] byteArray) throws java.io.IOException;

      void write(byte[] byteArray) throws java.io.IOException;

      String getText();

      @Override
      void close();
   }

   public static final class StateHolder2 extends Exception {
      public StateHolder2(String text) {
         super(text);
      }
   }

   private static final class StateHolder3 extends IOException {
      public StateHolder3(String text) {
         super(text);
      }
   }
}
