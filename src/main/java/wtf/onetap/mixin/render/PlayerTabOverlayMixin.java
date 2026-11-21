package wtf.onetap.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.BetterMinecraft;
import wtf.onetap.module.render.NameProtect;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
   @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
   private void onetap$protectName(PlayerInfo var1, CallbackInfoReturnable<Component> var2) {
      NameProtect var3 = Onetap.MODULE_MANAGER.get(NameProtect.class);
      if (var3 != null && var3.isEnabled()) {
         if (var1 != null && var1.getProfile() != null) {
            var2.setReturnValue(var3.replaceInComponent((Component)var2.getReturnValue()));
         }
      }
   }

   @ModifyArg(
      method = "extractRenderState",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V", ordinal = 2),
      index = 4
   )
   private int onetap$highlightRow(int var1, @Local(name = "i") int var2, @Local(name = "playerInfos") List<PlayerInfo> var3) {
      BetterMinecraft var4 = Onetap.MODULE_MANAGER.get(BetterMinecraft.class);
      return var4 != null && var2 < var3.size() ? var4.m_5((PlayerInfo)var3.get(var2), var1) : var1;
   }
}
