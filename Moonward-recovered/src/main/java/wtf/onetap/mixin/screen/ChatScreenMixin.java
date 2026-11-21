package wtf.onetap.mixin.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.config.ChatPrivacy;
import wtf.onetap.module.misc.ChatCorrector;
import wtf.onetap.module.misc.UnHook;
import wtf.onetap.module.player.NoCommands;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {
   @Shadow
   protected EditBox input;

   protected ChatScreenMixin(Component var1) {
      super(var1);
   }

   @Inject(method = "init", at = @At("TAIL"))
   private void onetap$initPrivacyButton(CallbackInfo var1) {
      if (ChatPrivacy.isNotUnHooked()) {
         this.addRenderableWidget(ChatPrivacy.m_3(this.width, this.input));
      }
   }

   @Inject(method = "extractRenderState", at = @At("RETURN"))
   private void onetap$renderPrivacyBlur(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      ChatPrivacy.m_2(this.input);
   }

   @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
   private void onetap$command(String var1, boolean var2, CallbackInfo var3) {
      UnHook var4 = Onetap.MODULE_MANAGER.get(UnHook.class);
      if (var4 == null || !var4.isFlag()) {
         if (var1 != null) {
            if (NoCommands.isAllowedCommand(var1)) {
               if (var1.startsWith(".") || NoCommands.m_7_boolean() && var1.startsWith("/.")) {
                  if (var1.startsWith("/")) {
                     var1 = var1.substring(1);
                  }

                  addRecent(var1, var2);
                  ChatCorrector.DataRecord var6 = ChatCorrector.m_9_C0(var1);
                  if (var6.m_66() == ChatCorrector.ModeKind.SERVER) {
                     Minecraft.getInstance().getConnection().sendCommand(var6.m_6_b());
                  } else {
                     Onetap.COMMAND_MANAGER.m_9_boolean("." + var6.m_6_b());
                  }

                  var3.cancel();
               } else {
                  if (var1.startsWith("/")) {
                     String var5 = ChatCorrector.correctCommand(var1);
                     if (!var5.equals(var1)) {
                        addRecent(var1, var2);
                        Minecraft.getInstance().getConnection().sendCommand(var5.substring(1));
                        var3.cancel();
                     }
                  }
               }
            }
         }
      }
   }

   private static void addRecent(String var0, boolean var1) {
      if (var1 && Minecraft.getInstance().gui != null && Minecraft.getInstance().gui.hud != null && Minecraft.getInstance().gui.hud.getChat() != null) {
         Minecraft.getInstance().gui.hud.getChat().addRecentChat(var0);
      }
   }
}
