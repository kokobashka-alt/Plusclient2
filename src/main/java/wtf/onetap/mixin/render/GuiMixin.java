package wtf.onetap.mixin.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.bot.BotConnection;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.ScreenOpenEvent;
import wtf.onetap.gui.TitleScreenRenderer;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.util.ChatUtil;

@Mixin(Gui.class)
public class GuiMixin {
   @Shadow
   @Final
   private GuiRenderState guiRenderState;

   @Inject(
      method = "extractRenderState",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/Hud;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$render2D(DeltaTracker var1, boolean var2, boolean var3, CallbackInfo var4) {
      float var5 = var1.getGameTimeDeltaPartialTick(false);
      Minecraft var6 = Minecraft.getInstance();
      int var7 = (int)var6.mouseHandler.getScaledXPos(var6.getWindow());
      int var8 = (int)var6.mouseHandler.getScaledYPos(var6.getWindow());
      GuiGraphicsExtractor var9 = new GuiGraphicsExtractor(var6, this.guiRenderState, var7, var8);
      Onetap.EVENT_BUS.unregister(new Render2DEvent(var5, var9));
   }

   @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
   private Screen onetap$customServerList(Screen var1) {
      if (ChatUtil.isUnHooked() || !(var1 instanceof JoinMultiplayerScreen) && !(var1 instanceof SafetyScreen)) {
         return var1;
      }

      TitleScreenRenderer.TITLE_SCREEN_RENDERER.m_1_b_void();
      return new TitleScreen();
   }

   @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
   private void onetap$setScreen(Screen var1, CallbackInfo var2) {
      if (BotConnection.BOT_CONNECTION.m_9_boolean()) {
         var2.cancel();
      } else {
         if (var1 != null && ClickGUI.isActive()) {
            Minecraft var3 = Minecraft.getInstance();
            if (var3.level == null) {
               return;
            }

            var2.cancel();
         }
      }
   }

   @Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;releaseAll()V", shift = Shift.AFTER))
   private void onetap$screenOpen(Screen var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ScreenOpenEvent());
   }
}
