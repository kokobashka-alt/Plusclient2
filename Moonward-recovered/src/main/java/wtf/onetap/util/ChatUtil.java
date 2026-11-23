package wtf.onetap.util;

import baritone.api.utils.Helper;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import wtf.onetap.Onetap;
import wtf.onetap.mixin.render.ChatComponentAccessor;
import wtf.onetap.module.misc.UnHook;

public class ChatUtil {
   private static int[] valueArray;

   public static void log(String text) {
      Minecraft mc = Minecraft.getInstance();
      if (mc != null) {
         Runnable runnable = () -> {
            if (mc.gui != null && mc.gui.hud != null && mc.gui.hud.getChat() != null) {
               if (!isUnHooked()) {
                  mc.gui.hud.getChat().addClientSystemMessage(Component.literal("§fPlusClient.log §7» §7" + text));
               }
            }
         };
         if (mc.isSameThread()) {
            runnable.run();
         } else {
            mc.execute(runnable);
         }
      }
   }

   public static void m_8_void(Component component) {
      Minecraft mc = Minecraft.getInstance();
      if (mc != null) {
         Runnable runnable = () -> {
            if (mc.gui != null && mc.gui.hud != null && mc.gui.hud.getChat() != null) {
               if (!isUnHooked()) {
                  mc.gui.hud.getChat().addClientSystemMessage(Component.literal("§fPlusClient.log §7» §7").append(component));
               }
            }
         };
         if (mc.isSameThread()) {
            runnable.run();
         } else {
            mc.execute(runnable);
         }
      }
   }

   public static boolean containsWord(String text, String text2) {
      return Pattern.compile("(?<!\\w)" + Pattern.quote(text2) + "(?!\\w)").matcher(text).find();
   }

   public static boolean isUnHooked() {
      UnHook unHook = Onetap.MODULE_MANAGER.get(UnHook.class);
      return unHook != null && unHook.isFlag();
   }

   public static void m_3_void() {
      Minecraft mc = Minecraft.getInstance();
      if (mc != null && mc.gui != null && mc.gui.hud != null) {
         ChatComponent chatComponent = mc.gui.hud.getChat();
         ((ChatComponentAccessor)chatComponent)
            .onetap$getAllMessages()
            .removeIf(guiMessage -> guiMessage.source() == GuiMessageSource.SYSTEM_CLIENT && guiMessage.content().getString().startsWith("§fPlusClient.log §7» §7") || m_6(guiMessage));
         chatComponent.rescaleChat();
      }
   }

   private static boolean m_6(GuiMessage guiMessage) {
      if (guiMessage.signature() != null) {
         return false;
      }

      if (Helper.MESSAGE_TAG.equals(guiMessage.tag())) {
         return true;
      }

      String text = guiMessage.content().getString();
      String text2 = null;

      for (String text3 : List.of("[Baritone]", "[B]", "[Baritoe]")) {
         if (text.startsWith(text3 + " ")) {
            text2 = text3;
            break;
         }
      }

      if (text2 == null) {
         return false;
      }

      int value = text2.length();
      int[] intArray = new int[]{0};
      boolean[] flagArray = new boolean[]{true};
      guiMessage.content().visit((style, text4) -> {
         for (int i = 0; i < text4.length() && intArray[0] < value; i++) {
            int value2 = intArray[0] != 0 && intArray[0] != value - 1 ? 16733695 : 11141290;
            if (style.getColor() != null && style.getColor().getValue() != value2) {
            }
         }

         return intArray[0] >= value ? Optional.of(true) : Optional.empty();
      }, Style.EMPTY);
      return flagArray[0] && intArray[0] == value;
   }

   private ChatUtil() {
   }

   public static void m_3(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_3_IArr() {
      return valueArray;
   }

   static {
      m_3(null);
   }
}
