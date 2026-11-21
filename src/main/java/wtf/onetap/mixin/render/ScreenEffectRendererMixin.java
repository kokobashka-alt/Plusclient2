package wtf.onetap.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.NoRender;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
   @Inject(method = "submitFire", at = @At("HEAD"), cancellable = true)
   private static void onetap$noRenderFire(PoseStack var0, SubmitNodeCollector var1, TextureAtlasSprite var2, CallbackInfo var3) {
      if (isNoRenderEnabled("Огонь")) {
         var3.cancel();
      }
   }

   @Inject(method = "submitWater", at = @At("HEAD"), cancellable = true)
   private static void onetap$noRenderWater(Minecraft var0, PoseStack var1, SubmitNodeCollector var2, CallbackInfo var3) {
      if (isNoRenderEnabled("Размытие в воде")) {
         var3.cancel();
      }
   }

   @Inject(method = "getViewBlockingState", at = @At("HEAD"), cancellable = true)
   private static void onetap$noRenderBlocks(Player var0, CallbackInfoReturnable<BlockState> var1) {
      if (isNoRenderEnabled("Блоки")) {
         var1.setReturnValue(null);
      }
   }

   private static boolean isNoRenderEnabled(String var0) {
      NoRender var1 = Onetap.MODULE_MANAGER.get(NoRender.class);
      return var1 != null && var1.isEnabled() && var1.modes.isSelected(var0);
   }
}
