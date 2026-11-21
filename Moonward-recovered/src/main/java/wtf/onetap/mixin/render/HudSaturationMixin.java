package wtf.onetap.mixin.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.BetterMinecraft;

@Mixin(Hud.class)
public class HudSaturationMixin {
   @Shadow
   private Player getCameraPlayer() {
      return null;
   }

   @Inject(method = "extractPlayerHealth", at = @At("RETURN"))
   private void onetap$saturationBar(GuiGraphicsExtractor var1, CallbackInfo var2) {
      BetterMinecraft var3 = Onetap.MODULE_MANAGER.get(BetterMinecraft.class);
      if (var3 != null && var3.isEnabled() && var3.saturationOverHunger.getValue()) {
         Player var4 = this.getCameraPlayer();
         if (var4 != null) {
            float var5 = var4.getFoodData().getSaturationLevel();
            int var6 = Mth.ceil(var5);
            int var7 = var1.guiWidth() / 2 + 91;
            int var8 = var1.guiHeight() - 39;
            int var9 = var8 - 10;
            if (var4.isEyeInFluid(FluidTags.WATER) || var4.getAirSupply() < var4.getMaxAirSupply()) {
               var9 -= 10;
            }

            int var10 = ARGB.color(180, 255, 220, 80);
            int var11 = ARGB.color(200, 255, 220, 80);

            for (int var12 = 0; var12 < 10; var12++) {
               int var13 = var7 - var12 * 8 - 9;
               if (var5 > 0.0F) {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("hud/food_empty"), var13, var9, 9, 9, var11);
               }

               if (var12 * 2 + 1 < var6) {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("hud/food_full"), var13, var9, 9, 9, var10);
               } else if (var12 * 2 + 1 == var6) {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("hud/food_half"), var13, var9, 9, 9, var10);
               }
            }
         }
      }
   }
}
