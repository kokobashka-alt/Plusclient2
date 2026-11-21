package wtf.onetap.mixin.render;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.module.render.ClickGUI;

@Mixin(DebugScreenEntryList.class)
public class DebugScreenEntryListMixin {
   @Inject(method = "isCurrentlyEnabled", at = @At("HEAD"), cancellable = true)
   private void onetap$debugCrosshair(Identifier var1, CallbackInfoReturnable<Boolean> var2) {
      if (ClickGUI.isActive() && DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR.equals(var1)) {
         var2.setReturnValue(true);
      }
   }
}
