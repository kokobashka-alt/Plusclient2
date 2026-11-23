package wtf.onetap.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Objects;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EraseAfterRun;
import wtf.onetap.render.AvatarTexture;

public class DiscordPresence {
   private static final Object OBJECT = new Object();
   private static Thread thread;
   private static volatile boolean flag;
   private static volatile boolean flag2;
   private static long longValue;
   private static String text;
   private static String text2;
   private static String text3;
   private static JsonObject jsonObject;
   private static int value;
   private static volatile String text4;
   private static volatile boolean flag3 = true;
   private static volatile String text5;
   private static volatile String text6;
   private static volatile String text7;
   private static String text8;

   @EraseAfterRun
   public static void startUserLookup() {
      Thread.ofVirtual().name("Discord user").start(() -> {
         try (DiscordIpc discordIpc = DiscordIpc.m_6("1519311702057947226", 3000L)) {
            m_7_void(discordIpc.getJsonObject());
         } catch (DiscordIpc.StateHolder2 c4) {
            Onetap.LOGGER.info("[Discord] account not read: {}", c4.getMessage());
         } catch (Throwable e) {
            Onetap.LOGGER.warn("[Discord] account not read", e);
         }
      });
      AvatarTexture.m_9_void();
   }

   public static void start() {
      synchronized (OBJECT) {
         if (thread == null) {
            flag = true;
            longValue = System.currentTimeMillis() / 1000L;
            text4 = null;
            flag3 = true;
            thread = new Thread(DiscordPresence::m_2_void, "Discord IPC");
            thread.setDaemon(true);
            thread.start();
         }
      }
   }

   public static void m_7_void() {
      Thread thread2;
      synchronized (OBJECT) {
         flag = false;
         longValue = 0L;
         text3 = null;
         text2 = null;
         text = null;
         jsonObject = null;
         value++;
         thread2 = thread;
         thread = null;
         OBJECT.notifyAll();
      }

      if (thread2 != null) {
         try {
            thread2.join(2000L);
         } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
         }
      }
   }

   public static void m_1_void() {
      m_7_void();
   }

   public static boolean isRunning() {
      return flag;
   }

   public static boolean m_1_boolean() {
      return flag2;
   }

   public static String m_8_String() {
      if (flag3) {
         return null;
      }

      flag3 = true;
      return text4;
   }

   public static void m_2(String text9, String text10, String text11) {
      if (flag) {
         if (!Objects.equals(text9, text) || !Objects.equals(text10, text2) || !Objects.equals(text11, text3)) {
            text = text9;
            text2 = text10;
            text3 = text11;
            if (longValue == 0L) {
               longValue = System.currentTimeMillis() / 1000L;
            }

            String text12 = text11 != null ? text11 : "Одиночная игра";
            JsonObject jsonObject2 = new JsonObject();
            if (text9 != null) {
               jsonObject2.addProperty("details", text9);
            }

            if (text10 != null) {
               jsonObject2.addProperty("state", text10);
            }

            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("start", longValue);
            jsonObject2.add("timestamps", jsonObject3);
            JsonObject jsonObject4 = new JsonObject();
            jsonObject4.addProperty("large_image", "https://cdn.discordapp.com/app-icons/1519311702057947226/3ad7939f0c3738f4b39472568f7a5539.png");
            jsonObject4.addProperty("large_text", text6 != null ? text6 : "PlusClient 26.2");
            String text13 = text11 != null ? m_6_String(text11) : null;
            if (text13 != null) {
               jsonObject4.addProperty("small_image", text13);
            }

            jsonObject4.addProperty("small_text", text12);
            jsonObject2.add("assets", jsonObject4);
            JsonArray jsonArray = new JsonArray();
            JsonObject jsonObject5 = new JsonObject();
            jsonObject5.addProperty("label", "Discord");
            jsonObject5.addProperty("url", "https://discord.gg/7ZEBnxNSVv");
            jsonArray.add(jsonObject5);
            jsonObject2.add("buttons", jsonArray);
            synchronized (OBJECT) {
               jsonObject = jsonObject2;
               value++;
               OBJECT.notifyAll();
            }
         }
      }
   }

   private static void m_2_void() {
      long longValue2 = ProcessHandle.current().pid();
      m_1_String();
      long longValue3 = 5000L;
      DiscordIpc discordIpc = null;
      int value2 = -1;

      try {
         while (isCurrentThread()) {
            if (discordIpc == null) {
               try {
                  discordIpc = DiscordIpc.m_6("1519311702057947226", 5000L);
               } catch (DiscordIpc.StateHolder2 c4) {
                  setError(c4.getMessage());
                  m_2_void(longValue3);
                  longValue3 = Math.min(longValue3 * 2L, 60000L);
                  continue;
               } catch (Throwable e) {
                  setError("Discord IPC недоступен: " + e);
                  Onetap.LOGGER.warn("[Discord] IPC unavailable", e);
                  return;
               }

               m_7_void(discordIpc.getJsonObject());
               Onetap.LOGGER.info("[Discord] connected through {} as {}", discordIpc.m_0_String(), text6);
               flag2 = true;
               longValue3 = 5000L;
               value2 = -1;
               setError(null);
            }

            try {
               JsonObject jsonObject2 = null;
               int value3;
               synchronized (OBJECT) {
                  value3 = value;
                  if (value3 != value2) {
                     jsonObject2 = jsonObject;
                  }
               }

               if (value3 != value2) {
                  discordIpc.buildActivity(longValue2, jsonObject2);
                  value2 = value3;
               }

               JsonObject jsonObject3;
               while ((jsonObject3 = discordIpc.readMessage()) != null) {
                  if ("ERROR".equals(DiscordIpc.getStringOrNull(jsonObject3, "evt"))) {
                     JsonObject jsonObject4 = jsonObject3.has("data") && jsonObject3.get("data").isJsonObject() ? jsonObject3.getAsJsonObject("data") : null;
                     setError("Discord отклонил активность: " + DiscordIpc.getStringOrNull(jsonObject4, "code") + " " + DiscordIpc.getStringOrNull(jsonObject4, "message"));
                  }
               }
            } catch (IOException e2) {
               discordIpc.close();
               discordIpc = null;
               flag2 = false;
               setError("соединение с Discord потеряно: " + e2.getMessage());
               continue;
            }

            m_2_void(500L);
         }
      } finally {
         if (isCurrentThread()) {
            flag2 = false;
         }

         if (discordIpc != null) {
            discordIpc.close();
         }
      }
   }

   private static boolean isCurrentThread() {
      synchronized (OBJECT) {
         return thread == Thread.currentThread();
      }
   }

   private static void m_2_void(long longValue2) {
      synchronized (OBJECT) {
         if (thread == Thread.currentThread()) {
            try {
               OBJECT.wait(longValue2);
            } catch (InterruptedException interruptedException) {
               Thread.currentThread().interrupt();
            }
         }
      }
   }

   private static void setError(String text9) {
      if (!Objects.equals(text9, text4)) {
         text4 = text9;
         if (text9 != null) {
            flag3 = false;
            Onetap.LOGGER.warn("[Discord] RPC: {}", text9);
         }
      }
   }

   private static void m_7_void(JsonObject jsonObject2) {
      if (jsonObject2 != null && DiscordIpc.getStringOrNull(jsonObject2, "id") != null) {
         text5 = DiscordIpc.getStringOrNull(jsonObject2, "id");
         text6 = DiscordIpc.getStringOrNull(jsonObject2, "username");
         String text9 = DiscordIpc.getStringOrNull(jsonObject2, "avatar");
         text7 = text9 != null && !text9.isBlank() ? text9 : null;
      }
   }

   private static String m_6_String(String text9) {
      int value2 = text9.indexOf(58);
      String text10 = value2 == -1 ? text9 : text9.substring(0, value2);
      return text10.isEmpty() ? null : "https://api.minetools.eu/favicon/" + text10;
   }

   public static String getUserName() {
      return text6;
   }

   public static boolean hasUser() {
      return text6 != null;
   }

   public static String m_9_String() {
      if (text7 == null) {
         return null;
      }

      String text9 = text5;
      String text10 = text7;
      String text11 = text7.startsWith("a_") ? ".gif" : ".png";
      String text12 = text10;
      String text13 = text9;
      return "https://cdn.discordapp.com/avatars/" + text13 + "/" + text12 + text11;
   }

   private DiscordPresence() {
   }

   static {
      setUserName("Mn5Sic");
   }

   public static void setUserName(String text9) {
      text8 = text9;
   }

   public static String m_1_String() {
      return text8;
   }
}
