package wtf.onetap.mixin.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.autobuy.AutoBuyUtil;

@Mixin(AbstractContainerScreen.class)
public abstract class AutoBuyContainerMixin extends Screen {
   @Shadow
   protected int leftPos;
   @Shadow
   protected int topPos;
   @Shadow
   protected int imageWidth;
   @Unique
   private Button onetap$autoBuyToggle;
   @Unique
   private Button onetap$parserToggle;
   @Unique
   private AbstractSliderButton onetap$discountSlider;

   protected AutoBuyContainerMixin(Component var1) {
      super(var1);
   }

   @Inject(method = "init", at = @At("TAIL"))
   private void onetap$initAutoBuyButtons(CallbackInfo var1) {
      if (this.minecraft != null && this.minecraft.gui.screen() instanceof ContainerScreen var2) {
         if (AutoBuyUtil.m_1(var2)) {
            this.onetap$ensureButtons();
         }
      }
   }

   @Inject(method = "extractRenderState", at = @At("HEAD"))
   private void onetap$updateAutoBuyButtons(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (this.minecraft != null && this.minecraft.gui.screen() instanceof ContainerScreen var6) {
         boolean var15 = AutoBuyUtil.m_1(var6);
         if (var15 && this.onetap$autoBuyToggle == null) {
            this.onetap$ensureButtons();
         }

         this.onetap$setButtonsVisible(var15);
         if (var15) {
            byte var8 = 90;
            byte var9 = 20;
            byte var10 = 4;
            int var11 = Math.max(4, this.topPos - var9 - 4);
            int var12 = this.leftPos + (this.imageWidth - var8) / 2;
            if (this.onetap$autoBuyToggle != null) {
               this.onetap$autoBuyToggle.setX(var12);
               this.onetap$autoBuyToggle.setY(var11);
               this.onetap$autoBuyToggle.setMessage(Component.literal(onetap$buyLabel()));
            }

            int var13 = this.leftPos + this.imageWidth + 6;
            if (var13 + 120 > this.width - 4) {
               var13 = Math.max(4, this.leftPos - 126);
            }

            int var14 = Math.max(4, this.topPos + 6);
            if (this.onetap$parserToggle != null) {
               this.onetap$parserToggle.setX(var13);
               this.onetap$parserToggle.setY(var14);
               this.onetap$parserToggle.setMessage(Component.literal(onetap$parserLabel()));
            }

            if (this.onetap$discountSlider != null) {
               this.onetap$discountSlider.setX(var13);
               this.onetap$discountSlider.setY(var14 + var9 + var10);
            }
         }
      } else {
         this.onetap$setButtonsVisible(false);
      }
   }

   @Unique
   private void onetap$ensureButtons() {
      if (this.onetap$autoBuyToggle == null) {
         byte var1 = 90;
         byte var2 = 20;
         byte var3 = 4;
         int var4 = Math.max(4, this.topPos - var2 - 4);
         int var5 = this.leftPos + (this.imageWidth - var1) / 2;
         this.onetap$autoBuyToggle = this.addRenderableWidget(
            Button.builder(Component.literal(onetap$buyLabel()), var0 -> AutoBuyUtil.toggleBuying()).bounds(var5, var4, var1, var2).build()
         );
         int var6 = this.leftPos + this.imageWidth + 6;
         if (var6 + 120 > this.width - 4) {
            var6 = Math.max(4, this.leftPos - 126);
         }

         int var7 = Math.max(4, this.topPos + 6);
         this.onetap$parserToggle = this.addRenderableWidget(
            Button.builder(Component.literal(onetap$parserLabel()), var0 -> AutoBuyUtil.toggleParser()).bounds(var6, var7, 120, var2).build()
         );
         double var8 = AutoBuyUtil.getDiscount() / 100.0;
         this.onetap$discountSlider = this.addRenderableWidget(
            new AbstractSliderButton(var6, var7 + var2 + var3, 120, var2, Component.literal("Скидка: " + AutoBuyUtil.getDiscount() + "%"), var8) {
               @Override
               protected void updateMessage() {
                  int var1x = (int)Math.round(this.value * 100.0);
                  this.setMessage(Component.literal("Скидка: " + var1x + "%"));
               }

               @Override
               protected void applyValue() {
                  int var1x = (int)Math.round(this.value * 100.0);
                  AutoBuyUtil.setDiscount(var1x);
                  this.updateMessage();
               }
            }
         );
      }
   }

   @Unique
   private void onetap$setButtonsVisible(boolean var1) {
      if (this.onetap$autoBuyToggle != null) {
         this.onetap$autoBuyToggle.visible = var1;
      }

      if (this.onetap$parserToggle != null) {
         this.onetap$parserToggle.visible = var1;
      }

      if (this.onetap$discountSlider != null) {
         this.onetap$discountSlider.visible = var1;
      }
   }

   @Unique
   private static String onetap$buyLabel() {
      return "AutoBuy: " + (AutoBuyUtil.isBuying() ? "§aON" : "§cOFF");
   }

   @Unique
   private static String onetap$parserLabel() {
      return "Авто парсер: " + (AutoBuyUtil.isParsing() ? "§aON" : "§cOFF");
   }
}
