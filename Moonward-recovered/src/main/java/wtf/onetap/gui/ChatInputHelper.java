package wtf.onetap.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.module.Module;
import wtf.onetap.module.misc.ClientSounds;
import wtf.onetap.module.misc.UnHook;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.Setting;

public class ChatInputHelper {
   private static String text;

   public static boolean isTextInputOpen() {
      if (!ClickGUI.isActive() && !ClickGuiScreen.isEditing()) {
         Screen screen = Minecraft.getInstance().gui.screen();
         return screen == null
            ? false
            : screen instanceof ChatScreen
               || screen instanceof AbstractSignEditScreen
               || screen instanceof BookEditScreen
               || screen.getClass().getName().startsWith("wtf.onetap.")
               || m_7(screen);
      } else {
         return true;
      }
   }

   private static boolean m_7(ContainerEventHandler containerEventHandler) {
      GuiEventListener guiEventListener = containerEventHandler.getFocused();

      while (guiEventListener != null) {
         if (guiEventListener instanceof EditBox editBox) {
            return editBox.canConsumeInput();
         }

         if (guiEventListener instanceof AbstractTextAreaWidget) {
            return guiEventListener.isFocused();
         }

         if (!(guiEventListener instanceof ContainerEventHandler containerEventHandler2)) {
            return false;
         }

         guiEventListener = containerEventHandler2.getFocused();
      }

      return false;
   }

   @EventTarget
   public void onKeyPress(KeyPressEvent event) {
      if (event.getAction() == 1) {
         UnHook unHook = Onetap.MODULE_MANAGER.get(UnHook.class);
         if (unHook != null && unHook.isFlag()) {
            if (!isTextInputOpen() && event.getKey() == unHook.getReturnKey()) {
               unHook.setEnabled(false);
               event.cancel();
            }
         } else if (!ClickGuiScreen.isEditing()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null && mc.player != null && mc.gui.screen() == null && mc.gui.overlay() == null) {
               if (ClickGUI.isActive()) {
                  for (Module module : Onetap.MODULE_MANAGER.getModules()) {
                     if (module instanceof ClickGUI && module.getKey() == event.getKey()) {
                        module.toggle();
                     }
                  }
               } else if (unHook != null && unHook.getKey() != -1 && unHook.getKey() == event.getKey()) {
                  unHook.setEnabled(true);
                  event.cancel();
               } else {
                  for (Module module2 : Onetap.MODULE_MANAGER.getModules()) {
                     if (module2.getKey() == event.getKey()) {
                        module2.toggle();
                     }

                     for (Setting setting : module2.getSettings()) {
                        if (setting instanceof BooleanSetting booleanSetting && booleanSetting.getKey() == event.getKey()) {
                           booleanSetting.setValue(!(Boolean)booleanSetting.getValue());
                           Onetap.MODULE_MANAGER.get(ClientSounds.class).playToggle((Boolean)booleanSetting.getValue());
                        }
                     }
                  }

                  String text2 = Onetap.MACRO_MANAGER.getMacros().get(event.getKey());
                  if (text2 != null && mc.getConnection() != null) {
                     if (text2.startsWith("/")) {
                        mc.getConnection().sendCommand(text2.substring(1));
                     } else {
                        mc.getConnection().sendChat(text2);
                     }
                  }
               }
            }
         }
      }
   }

   public static void m_6_void(String text2) {
      text = text2;
   }

   public static String m_6_String() {
      return text;
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
}
