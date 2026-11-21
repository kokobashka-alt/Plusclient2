package wtf.onetap.mixin.screen;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.config.ChatPrivacy;
import wtf.onetap.module.misc.UnHook;
import wtf.onetap.module.player.NoCommands;

@Mixin(CommandSuggestions.class)
public class CommandSuggestionsMixin {
   @Shadow
   @Final
   private EditBox input;
   @Shadow
   @Final
   private Minecraft minecraft;
   @Shadow
   @Final
   private List<FormattedCharSequence> commandUsage;
   @Shadow
   private ParseResults<ClientSuggestionProvider> currentParse;
   @Shadow
   private CompletableFuture<Suggestions> pendingSuggestions;

   @Shadow
   private void showSuggestions(boolean var1) {
      throw new AssertionError();
   }

   @Invoker("getLastWordIndex")
   private static int invokeGetLastWordIndex(String var0) {
      throw new AssertionError();
   }

   @Shadow
   public void hide() {
      throw new AssertionError();
   }

   @Inject(method = "updateCommandInfo", at = @At("HEAD"), cancellable = true)
   private void onetap$hideSensitiveUpdate(CallbackInfo var1) {
      if (this.onetap$shouldHideSensitiveSuggestions()) {
         this.onetap$clearSensitiveSuggestions();
         var1.cancel();
      }
   }

   @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
   private void onetap$hideSensitiveRender(GuiGraphicsExtractor var1, int var2, int var3, CallbackInfo var4) {
      if (this.onetap$shouldHideSensitiveSuggestions()) {
         this.onetap$clearSensitiveSuggestions();
         var4.cancel();
      }
   }

   @Unique
   private boolean onetap$shouldHideSensitiveSuggestions() {
      return ChatPrivacy.m_2_boolean() && ChatPrivacy.m_7_int(this.input.getValue()) >= 0;
   }

   @Unique
   private void onetap$clearSensitiveSuggestions() {
      this.input.setSuggestion(null);
      this.hide();
      this.commandUsage.clear();
      this.currentParse = null;
      this.pendingSuggestions = null;
   }

   @Inject(method = "updateCommandInfo", at = @At("RETURN"))
   private void onetap$onUpdateCommandInfo(CallbackInfo var1) {
      UnHook var2 = Onetap.MODULE_MANAGER.get(UnHook.class);
      if (var2 == null || !var2.isFlag()) {
         if (!NoCommands.m_7_boolean()) {
            String var3 = this.input.getValue();
            if (var3.startsWith(".")) {
               String var4 = var3.substring(0, this.input.getCursorPosition());
               int var5 = invokeGetLastWordIndex(var4);
               String var6 = var4.substring(1);
               List var7 = Onetap.COMMAND_MANAGER.m_5_List(var6);
               if (!var7.isEmpty()) {
                  String var8 = var4.substring(var5);
                  if (var8.startsWith(".")) {
                     var7 = var7.stream().map(var0 -> "." + var0).toList();
                  }

                  this.pendingSuggestions = SharedSuggestionProvider.suggest(var7, new SuggestionsBuilder(var4, var5));
                  if (this.minecraft.options.autoSuggestions().get()) {
                     this.showSuggestions(false);
                  }
               }
            }
         }
      }
   }
}
