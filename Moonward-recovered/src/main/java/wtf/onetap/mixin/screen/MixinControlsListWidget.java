package wtf.onetap.mixin.screen;

import java.util.Arrays;
import java.util.Set;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.onetap.module.player.VoiceChat;

@Mixin(KeyBindsList.class)
public abstract class MixinControlsListWidget {
   @Unique
   private static final Set<String> VOICECHAT_KEYS = Set.of(
      "key.push_to_talk",
      "key.whisper",
      "key.mute_microphone",
      "key.disable_voice_chat",
      "key.hide_icons",
      "key.voice_chat",
      "key.voice_chat_settings",
      "key.voice_chat_group",
      "key.voice_chat_toggle_recording",
      "key.voice_chat_adjust_volumes"
   );

   @Redirect(
      method = "<init>",
      at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;keyMappings:[Lnet/minecraft/client/KeyMapping;", opcode = 180)
   )
   private KeyMapping[] onetap$filterVoiceChatKeys(Options var1) {
      KeyMapping[] var2 = var1.keyMappings;
      return var2 != null && !VoiceChat.m_4_boolean()
         ? Arrays.stream(var2).filter(var0 -> var0 != null && !onetap$isVoiceChatKey(var0)).toArray(KeyMapping[]::new)
         : var2;
   }

   @Unique
   private static boolean onetap$isVoiceChatKey(KeyMapping var0) {
      return "voicechat".equals(var0.getCategory().id().getNamespace()) || VOICECHAT_KEYS.contains(var0.getName());
   }
}
