package wtf.onetap.module.player;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.config.ClientConfig;
import de.maxhenkel.voicechat.net.ClientServerNetManager;
import de.maxhenkel.voicechat.net.RequestSecretPacket;
import de.maxhenkel.voicechat.voice.client.ClientManager;
import de.maxhenkel.voicechat.voice.client.ClientPlayerStateManager;
import de.maxhenkel.voicechat.voice.client.ClientVoicechat;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "VoiceChat", description = "Настройки голосового чата", category = Category.PLAYER)
public class VoiceChat extends Module {
   private static VoiceChat voiceChat;
   private boolean flag;
   private boolean flag2;
   private static String[] textArray;

   public VoiceChat() {
      voiceChat = this;
      ClientTickEvents.END_CLIENT_TICK.register(mc -> this.m_4_void());
   }

   public static boolean isActive() {
      return voiceChat != null && voiceChat.isEnabled();
   }

   public static boolean m_4_boolean() {
      return isActive();
   }

   @Override
   public void onEnable() {
      this.flag = false;
      this.m_4_void();
   }

   @Override
   public void onDisable() {
      this.flag = false;
      this.m_4_void();
   }

   private void m_4_void() {
      m_9_StringArr();
      ClientConfig clientConfig = VoicechatClient.CLIENT_CONFIG;
      if (clientConfig != null) {
         boolean flag3 = this.isEnabled();
         if (this.flag && this.flag2 == flag3) {
            if (!flag3) {
               this.m_2_void();
            }
         } else {
            ClientPlayerStateManager clientPlayerStateManager = ClientManager.getPlayerStateManager();
            ClientVoicechat clientVoicechat = ClientManager.getClient();
            if (flag3) {
               if (!(Boolean)clientConfig.onboardingFinished.get()) {
                  clientConfig.onboardingFinished.set(true).save();
               }

               clientConfig.hideIcons.set(false).save();
               clientPlayerStateManager.setDisabled(false);
               if (clientVoicechat != null && clientVoicechat.getConnection() == null) {
                  ClientServerNetManager.sendToServer(new RequestSecretPacket(Voicechat.COMPATIBILITY_VERSION));
               }
            } else {
               clientConfig.hideIcons.set(true).save();
               clientPlayerStateManager.setDisabled(true);
               if (clientVoicechat != null) {
                  clientVoicechat.closeMicThread();
                  clientVoicechat.onVoiceChatDisconnected();
               }

               this.m_2_void();
            }

            this.flag = true;
            this.flag2 = flag3;
         }
      }
   }

   private void m_2_void() {
      if (MinecraftAccess.MINECRAFT.gui.screen() != null && MinecraftAccess.MINECRAFT.gui.screen().getClass().getName().startsWith("de.maxhenkel.voicechat")) {
         MinecraftAccess.MINECRAFT.gui.setScreen(null);
      }
   }

   public static void m_8(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_9_StringArr() {
      return textArray;
   }

   private static String b(byte[] byteArray) {
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
