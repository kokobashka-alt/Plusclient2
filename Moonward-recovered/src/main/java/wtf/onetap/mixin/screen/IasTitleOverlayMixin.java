package wtf.onetap.mixin.screen;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "ru.vidtu.ias.IASMinecraft")
public class IasTitleOverlayMixin {
   @Inject(method = "onInit", at = @At("HEAD"), cancellable = true)
   private static void onetap$hideTitleButton(Minecraft var0, Screen var1, Consumer<Button> var2, CallbackInfo var3) {
      if (var1 instanceof TitleScreen) {
         var3.cancel();
      }
   }

   @Inject(method = "onDraw", at = @At("HEAD"), cancellable = true)
   private static void onetap$hideTitleText(Screen var0, Font var1, GuiGraphicsExtractor var2, CallbackInfo var3) {
      if (var0 instanceof TitleScreen) {
         var3.cancel();
      }
   }
}
