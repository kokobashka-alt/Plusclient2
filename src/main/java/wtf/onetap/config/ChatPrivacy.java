package wtf.onetap.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button.Plain;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;
import wtf.onetap.Onetap;
import wtf.onetap.module.misc.UnHook;
import wtf.onetap.render.Render2D;
import wtf.onetap.util.ChatUtil;

public class ChatPrivacy {
   private static final Path PATH = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("chat_privacy.json");
   private static final Set<String> STRING_SET = Set.of("/reg", "/register", "/login", "/l", "/r");
   private static volatile boolean flag = m_7_boolean();
   private static volatile ChatPrivacy.DataRecord c9;
   private static int value;

   private ChatPrivacy() {
   }

   public static boolean isNotUnHooked() {
      UnHook unHook = Onetap.MODULE_MANAGER.get(UnHook.class);
      return unHook == null || !unHook.isFlag();
   }

   public static boolean m_2_boolean() {
      return flag && isNotUnHooked();
   }

   public static Button m_3(int value2, EditBox editBox) {
      short shortValue = 132;
      byte byteValue = 16;
      int value3 = Math.max(4, value2 - shortValue - 6);
      int value4 = Math.max(4, editBox.getY() - byteValue - 6);
      return new Plain(value3, value4, shortValue, byteValue, m_7_Component(), button -> {
         flag = !flag;
         m_4_void();
         button.setMessage(m_7_Component());
      }, Supplier::get) {
         @Override
         public boolean shouldTakeFocusAfterInteraction() {
            return false;
         }

         @Override
         public ComponentPath nextFocusPath(FocusNavigationEvent event) {
            return null;
         }
      };
   }

   private static Component m_7_Component() {
      return Component.literal("Скрывать пароль: " + flag);
   }

   public static void m_2(EditBox editBox) {
      if (m_2_boolean()) {
         int value2 = m_7_int(editBox.getValue());
         if (value2 >= 0) {
            int value3 = editBox.getX() + editBox.getInnerWidth();
            int value4 = Mth.clamp(editBox.getScreenX(value2) - 2, editBox.getX(), value3);
            int value5 = Math.min(editBox.getScreenX(editBox.getValue().length()) + 2, value3);
            if (value5 > value4) {
               Render2D.m_4(value4, editBox.getY() - 1.0F, value5 - value4, editBox.getHeight() + 2.0F, 3.0F, 0.0F, 3.0F, -1);
            }
         }
      }
   }

   public static void m_0_void(Packet<?> packet) {
      String text = switch (packet) {
         case ServerboundChatCommandPacket serverboundChatCommandPacket -> serverboundChatCommandPacket.command();
         case ServerboundChatCommandSignedPacket serverboundChatCommandSignedPacket -> serverboundChatCommandSignedPacket.command();
         default -> null;
      };
      if (text != null) {
         String text2 = "/" + text;
         int value2 = m_7_int(text2);
         if (value2 >= 0) {
            var stringList = List.of(text2.substring(value2).trim().split("\\s+"));
            c9 = new ChatPrivacy.DataRecord(stringList, System.currentTimeMillis());
         }
      }
   }

   public static void m_7(Matrix3x2f matrix3x2f, int value2, float floatValue, FormattedCharSequence formattedCharSequence) {
      ChatUtil.m_3_IArr();
      ChatPrivacy.DataRecord c92 = c9;
      if (c92 != null && !(floatValue <= 0.0F) && m_2_boolean()) {
         if (System.currentTimeMillis() - c92.m_8_q() > 10000L) {
            c9 = null;
         } else {
            StringBuilder stringBuilder = new StringBuilder();
            formattedCharSequence.accept((value3, style, value4) -> {
               stringBuilder.appendCodePoint(value4);
               return true;
            });
            String text = stringBuilder.toString();
            Font font = Minecraft.getInstance().font;

            for (String text2 : c92.m_2_f_List()) {
               for (int i = text.indexOf(text2); i >= 0; i = text.indexOf(text2, i + text2.length())) {
                  Vector2f vector2f = matrix3x2f.transformPosition(m_5(font, formattedCharSequence, i) - 1.0F, value2 - 1.0F, new Vector2f());
                  Vector2f vector2f2 = matrix3x2f.transformPosition(m_5(font, formattedCharSequence, i + text2.length()) + 1.0F, value2 + 9.0F, new Vector2f());
                  Render2D.m_4(vector2f.x, vector2f.y, vector2f2.x - vector2f.x, vector2f2.y - vector2f.y, 3.0F, 0.0F, 3.0F, -1);
               }
            }
         }
      }
   }

   private static int m_5(Font font, FormattedCharSequence formattedCharSequence, int value2) {
      int[] intArray = new int[]{0};
      return font.width(formattedCharSink -> formattedCharSequence.accept((value3, style, value4) -> {
         if (intArray[0] >= value2) {
            return false;
         }

         Character.charCount(value4);
         return formattedCharSink.accept(value3, style, value4);
      }));
   }

   public static int m_7_int(String text) {
      if (text != null && !text.isBlank()) {
         int value2 = 0;

         while (value2 < text.length() && Character.isWhitespace(text.charAt(value2))) {
            value2++;
         }

         if (value2 < text.length() && text.charAt(value2) == '/') {
            int value3 = value2;

            while (value3 < text.length() && !Character.isWhitespace(text.charAt(value3))) {
               value3++;
            }

            String text2 = text.substring(value2, value3).toLowerCase(Locale.ROOT);
            if (!STRING_SET.contains(text2)) {
               return -1;
            }

            int value4 = value3;

            while (value4 < text.length() && Character.isWhitespace(text.charAt(value4))) {
               value4++;
            }

            return value4 < text.length() ? value4 : -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private static boolean m_7_boolean() {
      if (!Files.exists(PATH)) {
         return false;
      }

      try (BufferedReader bufferedReader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
         JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();
         return jsonObject.has("hideSensitiveInfo") && jsonObject.get("hideSensitiveInfo").getAsBoolean();
      } catch (IOException | RuntimeException e) {
         Onetap.LOGGER.error("Failed to load chat privacy state", e);
         return false;
      }
   }

   private static void m_4_void() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("hideSensitiveInfo", flag);

      try {
         Files.createDirectories(PATH.getParent());
         Files.writeString(PATH, jsonObject.toString(), StandardCharsets.UTF_8);
      } catch (IOException e) {
         Onetap.LOGGER.error("Failed to save chat privacy state", e);
      }
   }

   static {
      m_1_void(0);
   }

   public static void m_1_void(int value2) {
      value = value2;
   }

   public static int m_1_int() {
      return value;
   }

   public static int m_2_int() {
      return 97;
   }

   private record DataRecord(List<String> f_2_f, long f_8_q) {
      public List<String> m_2_f_List() {
         return this.f_2_f;
      }

      public long m_8_q() {
         return this.f_8_q;
      }
   }
}
