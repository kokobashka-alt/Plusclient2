package wtf.onetap.mixin.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.config.ChatPrivacy;

@Mixin(
   targets = {
         "net.minecraft.client.gui.components.ChatComponent$DrawingBackgroundGraphicsAccess",
         "net.minecraft.client.gui.components.ChatComponent$DrawingFocusedGraphicsAccess"
   }
)
public class ChatGraphicsAccessMixin {
   @Shadow(remap = false)
   @Final
   private GuiGraphicsExtractor graphics;

   @Inject(method = "handleMessage", at = @At("RETURN"))
   private void onetap$blurSentPassword(int var1, float var2, FormattedCharSequence var3, CallbackInfoReturnable<Boolean> var4) {
      ChatPrivacy.m_7(this.graphics.pose(), var1, var2, var3);
   }
}
