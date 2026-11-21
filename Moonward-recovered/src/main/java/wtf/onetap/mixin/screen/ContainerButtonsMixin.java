package wtf.onetap.mixin.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.autobuy.AutoBuyUtil;
import wtf.onetap.module.misc.UnHook;
import wtf.onetap.util.ServerDetector;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerButtonsMixin extends Screen {
   @Shadow
   protected int leftPos;
   @Shadow
   protected int topPos;
   @Shadow
   protected int imageWidth;
   @Shadow
   protected int imageHeight;
   @Unique
   private Button onetap$dropAllButton;
   @Unique
   private Button onetap$putAllButton;
   @Unique
   private Button onetap$takeAllButton;

   protected ContainerButtonsMixin(Component var1) {
      super(var1);
   }

   @Inject(method = "init", at = @At("TAIL"))
   private void onetap$initContainerButtons(CallbackInfo var1) {
      this.onetap$dropAllButton = null;
      this.onetap$putAllButton = null;
      this.onetap$takeAllButton = null;
      if (this.onetap$isSupportedContainerScreen() && !this.onetap$isAuctionContainer()) {
         byte var2 = 104;
         byte var3 = 96;
         byte var4 = 20;
         byte var5 = 4;
         int var6 = Math.max(4, Math.min(this.width - var2 - 4, this.leftPos + (this.imageWidth - var2) / 2));
         int var7 = Math.max(4, this.topPos - var4 - 4);
         int var8 = this.leftPos + this.imageWidth + 6;
         int var9 = this.leftPos - var3 - 6;
         int var10 = var8 + var3 <= this.width - 4 ? var8 : Math.max(4, var9);
         int var11 = Math.max(4, this.topPos + 6);
         this.onetap$dropAllButton = this.addRenderableWidget(
            Button.builder(Component.literal("Выкинуть все"), var1x -> this.onetap$dropAllFromContainer()).bounds(var6, var7, var2, var4).build()
         );
         this.onetap$putAllButton = this.addRenderableWidget(
            Button.builder(Component.literal("Сложить все"), var1x -> this.onetap$moveAllFromPlayerToContainer()).bounds(var10, var11, var3, var4).build()
         );
         this.onetap$takeAllButton = this.addRenderableWidget(
            Button.builder(Component.literal("Взять все"), var1x -> this.onetap$takeAllFromContainer()).bounds(var10, var11 + var4 + var5, var3, var4).build()
         );
         this.onetap$setStorageButtonsVisible(!this.onetap$isAutoJoinerLocked() && !this.onetap$isSuspended());
      }
   }

   @Inject(method = "extractRenderState", at = @At("HEAD"))
   private void onetap$updateStorageButtons(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (this.onetap$dropAllButton != null) {
         this.onetap$setStorageButtonsVisible(!this.onetap$isAutoJoinerLocked() && !this.onetap$isSuspended());
      }
   }

   @Inject(method = "onClose", at = @At("HEAD"))
   private void onetap$stopGriefJoiner(CallbackInfo var1) {
      if (this.onetap$isAutoJoinerLocked()) {
         ServerDetector.SERVER_DETECTOR.stop();
      }
   }

   @Unique
   private boolean onetap$isSuspended() {
      UnHook var1 = Onetap.MODULE_MANAGER.get(UnHook.class);
      return var1 != null && var1.isFlag();
   }

   @Unique
   private boolean onetap$isAutoJoinerLocked() {
      return !this.onetap$isSuspended() && ServerDetector.SERVER_DETECTOR.m_1((AbstractContainerScreen<?>)(Object)this);
   }

   @Unique
   private void onetap$setStorageButtonsVisible(boolean var1) {
      if (this.onetap$dropAllButton != null) {
         this.onetap$dropAllButton.visible = this.onetap$dropAllButton.active = var1;
      }

      if (this.onetap$putAllButton != null) {
         this.onetap$putAllButton.visible = this.onetap$putAllButton.active = var1;
      }

      if (this.onetap$takeAllButton != null) {
         this.onetap$takeAllButton.visible = this.onetap$takeAllButton.active = var1;
      }
   }

   @Unique
   private boolean onetap$isSupportedContainerScreen() {
      return this.minecraft != null && (this.minecraft.gui.screen() instanceof ContainerScreen || this.minecraft.gui.screen() instanceof ShulkerBoxScreen);
   }

   @Unique
   private boolean onetap$isAuctionContainer() {
      return this.minecraft != null && this.minecraft.gui.screen() instanceof ContainerScreen var1 ? AutoBuyUtil.m_0_boolean(var1) : false;
   }

   @Unique
   private void onetap$moveAllFromPlayerToContainer() {
      if (!this.onetap$isSuspended()
         && !this.onetap$isAutoJoinerLocked()
         && this.minecraft != null
         && this.minecraft.player != null
         && this.minecraft.gameMode != null) {
         LocalPlayer var1 = this.minecraft.player;
         AbstractContainerMenu var2 = var1.containerMenu;
         int var3 = var2.containerId;

         for (int var4 = var2.slots.size() - 1; var4 >= 0; var4--) {
            Slot var5 = var2.slots.get(var4);
            if (this.onetap$isPlayerInventorySlot(var5, var1.getInventory()) && var5.hasItem()) {
               this.minecraft.gameMode.handleContainerInput(var3, var5.index, 0, ContainerInput.QUICK_MOVE, var1);
            }
         }
      }
   }

   @Unique
   private void onetap$takeAllFromContainer() {
      if (!this.onetap$isSuspended()
         && !this.onetap$isAutoJoinerLocked()
         && this.minecraft != null
         && this.minecraft.player != null
         && this.minecraft.gameMode != null) {
         LocalPlayer var1 = this.minecraft.player;
         AbstractContainerMenu var2 = var1.containerMenu;
         int var3 = var2.containerId;

         for (int var4 = 0; var4 < var2.slots.size(); var4++) {
            Slot var5 = var2.slots.get(var4);
            if (var5.container != var1.getInventory() && var5.hasItem()) {
               this.minecraft.gameMode.handleContainerInput(var3, var5.index, 0, ContainerInput.QUICK_MOVE, var1);
            }
         }
      }
   }

   @Unique
   private void onetap$dropAllFromContainer() {
      if (!this.onetap$isSuspended()
         && !this.onetap$isAutoJoinerLocked()
         && this.minecraft != null
         && this.minecraft.player != null
         && this.minecraft.gameMode != null) {
         LocalPlayer var1 = this.minecraft.player;
         AbstractContainerMenu var2 = var1.containerMenu;
         int var3 = var2.containerId;

         for (int var4 = 0; var4 < var2.slots.size(); var4++) {
            Slot var5 = var2.slots.get(var4);
            if (var5.container != var1.getInventory() && var5.hasItem()) {
               this.minecraft.gameMode.handleContainerInput(var3, var5.index, 1, ContainerInput.THROW, var1);
            }
         }
      }
   }

   @Unique
   private boolean onetap$isPlayerInventorySlot(Slot var1, Inventory var2) {
      if (var1.container != var2) {
         return false;
      }

      int var3 = var1.getContainerSlot();
      return var3 >= 0 && var3 <= 35;
   }
}
