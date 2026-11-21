package wtf.onetap.mixin.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import wtf.onetap.Onetap;
import wtf.onetap.module.misc.UnHook;
import wtf.onetap.obf.C6_m;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu> {
   @Unique
   private boolean onetap$openAnimStarted;
   @Unique
   private Button onetap$dropAllButton;

   protected InventoryScreenMixin(InventoryMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
   }

   @Inject(method = "init", at = @At("HEAD"))
   private void onetap$startOpenAnim(CallbackInfo var1) {
      if (!this.onetap$openAnimStarted) {
         this.onetap$openAnimStarted = true;
         C6_m.m_1_void();
      }
   }

   @Inject(
      method = "extractBackground",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/AbstractRecipeBookScreen;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$scaleBackground(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      this.onetap$pushOpenScale(var1);
   }

   @Inject(method = "extractBackground", at = @At("RETURN"))
   private void onetap$scaleBackgroundEnd(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      this.onetap$popOpenScale(var1);
   }

   @ModifyArgs(
      method = "extractBackground",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;extractEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V"
      )
   )
   private void onetap$scaleEntity(Args var1) {
      float var2 = C6_m.m_9_float();
      if (!C6_m.m_8_boolean()) {
         float var3 = this.leftPos + this.imageWidth / 2.0F;
         float var4 = this.topPos + this.imageHeight / 2.0F;
         int var5 = (Integer)var1.get(1);
         int var6 = (Integer)var1.get(2);
         int var7 = (Integer)var1.get(3);
         int var8 = (Integer)var1.get(4);
         int var9 = (Integer)var1.get(5);
         int var10 = (int)(var3 + (var5 - var3) * var2);
         int var11 = (int)(var4 + (var6 - var4) * var2);
         int var12 = (int)(var3 + (var7 - var3) * var2);
         int var13 = (int)(var4 + (var8 - var4) * var2);
         if (var12 <= var10) {
            var12 = var10 + 1;
         }

         if (var13 <= var11) {
            var13 = var11 + 1;
         }

         var1.set(1, var10);
         var1.set(2, var11);
         var1.set(3, var12);
         var1.set(4, var13);
         var1.set(5, Math.max(1, (int)(var9 * var2)));
      }
   }

   @Inject(method = "extractRenderState", at = @At("HEAD"))
   private void onetap$scaleContents(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      this.onetap$updateDropAllButton();
      this.onetap$pushOpenScale(var1);
   }

   @Inject(method = "extractRenderState", at = @At("RETURN"))
   private void onetap$scaleContentsEnd(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      this.onetap$popOpenScale(var1);
   }

   @Unique
   private void onetap$pushOpenScale(GuiGraphicsExtractor var1) {
      float var2 = C6_m.m_9_float();
      if (!C6_m.m_8_boolean()) {
         float var3 = this.leftPos + this.imageWidth / 2.0F;
         float var4 = this.topPos + this.imageHeight / 2.0F;
         Matrix3x2fStack var5 = var1.pose();
         var5.pushMatrix();
         var5.translate(var3, var4);
         var5.scale(var2, var2);
         var5.translate(-var3, -var4);
      }
   }

   @Unique
   private void onetap$popOpenScale(GuiGraphicsExtractor var1) {
      if (!C6_m.m_8_boolean()) {
         var1.pose().popMatrix();
      }
   }

   @Inject(method = "init", at = @At("TAIL"))
   private void onetap$initDropAllButton(CallbackInfo var1) {
      byte var2 = 96;
      byte var3 = 20;
      int var4 = Math.max(4, Math.min(this.width - var2 - 4, this.leftPos + (this.imageWidth - var2) / 2));
      int var5 = Math.max(4, this.topPos - var3 - 4);
      this.onetap$dropAllButton = this.addRenderableWidget(
         Button.builder(Component.literal("Выкинуть все"), var1x -> this.onetap$dropAllInventory()).bounds(var4, var5, var2, var3).build()
      );
      this.onetap$updateDropAllButton();
   }

   @Unique
   private boolean onetap$isSuspended() {
      UnHook var1 = Onetap.MODULE_MANAGER.get(UnHook.class);
      return var1 != null && var1.isFlag();
   }

   @Unique
   private void onetap$updateDropAllButton() {
      if (this.onetap$dropAllButton != null) {
         this.onetap$dropAllButton.visible = this.onetap$dropAllButton.active = !this.onetap$isSuspended();
      }
   }

   @Unique
   private void onetap$dropAllInventory() {
      if (!this.onetap$isSuspended() && this.minecraft != null && this.minecraft.player != null && this.minecraft.gameMode != null) {
         LocalPlayer var1 = this.minecraft.player;
         AbstractContainerMenu var2 = var1.containerMenu;
         int var3 = var2.containerId;

         for (Slot var5 : var2.slots) {
            if (this.onetap$isDroppablePlayerSlot(var5, var1.getInventory()) && var5.hasItem()) {
               this.minecraft.gameMode.handleContainerInput(var3, var5.index, 1, ContainerInput.THROW, var1);
            }
         }
      }
   }

   @Unique
   private boolean onetap$isDroppablePlayerSlot(Slot var1, Inventory var2) {
      if (var1.container != var2) {
         return false;
      }

      int var3 = var1.getContainerSlot();
      return var3 >= 0 && var3 <= 40;
   }
}
