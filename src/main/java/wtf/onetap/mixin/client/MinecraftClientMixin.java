package wtf.onetap.mixin.client;

import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.bot.BotConnection;
import wtf.onetap.bot.BotManager;
import wtf.onetap.combat.AttackCooldownTracker;
import wtf.onetap.event.ContinueAttackEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.TitleScreenRenderer;
import wtf.onetap.module.combat.CrystalAura;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.module.combat.ProjectHelper;
import wtf.onetap.module.misc.FreeCam;
import wtf.onetap.module.misc.PvPSafe;
import wtf.onetap.module.movement.NoClip;
import wtf.onetap.module.movement.NoWeb;
import wtf.onetap.module.movement.Sprint;
import wtf.onetap.module.player.NoDelay;
import wtf.onetap.module.render.GlowESP;
import wtf.onetap.obf.C6_z;
import wtf.onetap.pve.CombatPlanner;
import wtf.onetap.render.ViewModelRenderer;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.TickExecutor;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
   @Shadow
   private int rightClickDelay;
   @Unique
   private boolean onetap$wasUsingItemBeforeStartUse;

   @Inject(method = "handleKeybinds", at = @At("HEAD"), cancellable = true)
   private void onetap$handleKeybinds(CallbackInfo var1) {
      if (((Minecraft)(Object)this).player == null) {
         var1.cancel();
      }
   }

   @Inject(method = "handleGlobalKeyPress", at = @At("HEAD"), cancellable = true)
   private void onetap$friendsKeyWhileTyping(Key var1, boolean var2, CallbackInfoReturnable<Boolean> var3) {
      Minecraft var4 = (Minecraft)(Object)this;
      if (var4.gui.screen() instanceof TitleScreen && !ChatUtil.isUnHooked() && TitleScreenRenderer.TITLE_SCREEN_RENDERER.m_0_boolean() && var4.options.keyFriends.matches(var1)) {
         var3.setReturnValue(false);
      }
   }

   @ModifyVariable(method = "updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;Z)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private boolean onetap$keepSoundsWhenParked(boolean var1) {
      return var1 && !BotConnection.BOT_CONNECTION.m_9_boolean();
   }

   @Inject(method = "tick", at = @At("HEAD"))
   private void onetap$tick(CallbackInfo var1) {
      TickExecutor.m_0_boolean();
      InventoryUtil.m_9_void();
      InventoryUtil.m_8_void();
      AttackCooldownTracker.tick();
      C6_z.m_8_void();
      Onetap.ROTATION_MODES.beginTick();
      if (BotManager.BOT_MANAGER.m_1_boolean()) {
         Onetap.EVENT_BUS.unregister(new TickEvent());
      }
   }

   @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
   private void onetap$continueAttack(boolean var1, CallbackInfo var2) {
      ContinueAttackEvent var3 = new ContinueAttackEvent(var1);
      Onetap.EVENT_BUS.unregister(var3);
      NoClip var4 = Onetap.MODULE_MANAGER.get(NoClip.class);
      if (var3.isCancelled() || var4 != null && var4.m_0_boolean()) {
         var2.cancel();
      }
   }

   @Inject(method = "tick", at = @At("RETURN"))
   private void onetap$tickEnd(CallbackInfo var1) {
      CombatPlanner.COMBAT_PLANNER.m_4_void();
      Onetap.ROTATION_MODES.endTick();
      CrystalAura var2 = Onetap.MODULE_MANAGER.get(CrystalAura.class);
      if (var2 != null) {
         var2.scheduleAsync();
      }

      KillAura var3 = Onetap.MODULE_MANAGER.get(KillAura.class);
      if (var3 != null) {
         var3.m_2_void();
      }

      BotManager.BOT_MANAGER.m_9_void();
   }

   @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
   private void onetap$glowEsp(Entity var1, CallbackInfoReturnable<Boolean> var2) {
      GlowESP var3 = Onetap.MODULE_MANAGER.get(GlowESP.class);
      if (var3 != null && var3.shouldGlow(var1)) {
         var2.setReturnValue(true);
      }
   }

   @Inject(method = "pick", at = @At("RETURN"))
   private void onetap$freeCamPick(float var1, CallbackInfo var2) {
      Minecraft var3 = (Minecraft)(Object)this;
      if (var3.level != null && var3.player != null) {
         FreeCam var4 = Onetap.MODULE_MANAGER.get(FreeCam.class);
         if (var4 != null && var4.isEnabled()) {
            var4.m_7_void();
         }

         NoWeb var5 = Onetap.MODULE_MANAGER.get(NoWeb.class);
         if (var5 != null
            && var5.isEnabled()
            && var5.breakThroughWeb.getValue()
            && (var5.placeThroughWeb.getValue() || var3.options.keyAttack.isDown())
            && var3.hitResult != null
            && var3.hitResult.getType() == Type.BLOCK) {
            BlockHitResult var6 = (BlockHitResult)var3.hitResult;
            if (var3.level.getBlockState(var6.getBlockPos()).getBlock() == Blocks.COBWEB) {
               Vec3 var7 = var3.player.getEyePosition(var1);
               Vec3 var8 = Vec3.directionFromRotation(var3.player.getXRot(), var3.player.getYRot());
               Vec3 var9 = var7.add(var8.scale(4.5));
               BlockHitResult var10 = var3.level.clip(new ClipContext(var7, var9, Block.OUTLINE, Fluid.NONE, var3.player));

               while (var10.getType() != Type.MISS && var3.level.getBlockState(var10.getBlockPos()).getBlock() == Blocks.COBWEB) {
                  Vec3 var11 = var10.getLocation().add(var8.scale(0.1));
                  Vec3 var12 = var11.add(var8.scale(4.5));
                  var10 = var3.level.clip(new ClipContext(var11, var12, Block.OUTLINE, Fluid.NONE, var3.player));
               }

               if (var10.getType() != Type.MISS) {
                  var3.hitResult = var10;
               }
            }
         }
      }
   }

   @Inject(method = "stop", at = @At("HEAD"))
   private void onetap$stop(CallbackInfo var1) {
      Thread var2 = new Thread(() -> {
         try {
            Onetap.CONFIG_MANAGER.saveConfig("autocfg");
         } catch (Exception var1x) {
            Onetap.LOGGER.error("Failed to save autocfg on shutdown", var1x);
         }
      }, "OnetapShutdownSave");
      var2.start();

      try {
         var2.join(500L);
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
      }
   }

   @Inject(
      method = "startAttack",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V", shift = Shift.AFTER)
   )
   private void onetap$holdMyItemsAttack(CallbackInfoReturnable<Boolean> var1) {
      ViewModelRenderer.onSwing(InteractionHand.MAIN_HAND);
   }

   @ModifyArg(
      method = "startUseItem",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V")
   )
   private InteractionHand onetap$holdMyItemsUse(InteractionHand var1) {
      ViewModelRenderer.onSwing(var1);
      return var1;
   }

   @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
   private void onetap$deferUntilRotation(CallbackInfo var1) {
      if (onetap$holdProjectileShot() || C6_z.m_6_boolean()) {
         var1.cancel();
      }
   }

   @Redirect(
      method = "handleKeybinds",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;releaseUsingItem(Lnet/minecraft/world/entity/player/Player;)V"
      )
   )
   private void onetap$holdProjectileRelease(MultiPlayerGameMode var1, Player var2) {
      if (!onetap$holdProjectileShot()) {
         var1.releaseUsingItem(var2);
      }
   }

   @Unique
   private static boolean onetap$holdProjectileShot() {
      ProjectHelper var0 = Onetap.MODULE_MANAGER.get(ProjectHelper.class);
      return var0 != null && var0.m_9_boolean();
   }

   @Inject(method = "startUseItem", at = @At("HEAD"))
   private void onetap$captureUsingItemBeforeStartUse(CallbackInfo var1) {
      LocalPlayer var2 = ((Minecraft)(Object)this).player;
      this.onetap$wasUsingItemBeforeStartUse = var2 != null && var2.isUsingItem();
   }

   @Inject(method = "startUseItem", at = @At("RETURN"))
   private void onetap$sprintReset(CallbackInfo var1) {
      LocalPlayer var2 = ((Minecraft)(Object)this).player;
      if (var2 != null && !this.onetap$wasUsingItemBeforeStartUse && var2.isUsingItem()) {
         Sprint var3 = Onetap.MODULE_MANAGER.get(Sprint.class);
         if (var3 != null && var3.isEnabled()) {
            Sprint.m_8_void(var2);
         }
      }
   }

   @Inject(method = "startUseItem", at = @At("RETURN"))
   private void onetap$noItemDelay(CallbackInfo var1) {
      LocalPlayer var2 = ((Minecraft)(Object)this).player;
      if (var2 != null) {
         NoDelay var3 = Onetap.MODULE_MANAGER.get(NoDelay.class);
         if (var3 != null && var3.isEnabled()) {
            if (var3.multiModeSetting.isSelected("ПКМ")) {
               this.rightClickDelay = 0;
            }

            if (var2.getMainHandItem().is(Items.EXPERIENCE_BOTTLE) || var2.getOffhandItem().is(Items.EXPERIENCE_BOTTLE)) {
               if (var3.multiModeSetting.isSelected("Опыт")) {
                  this.rightClickDelay = var3.floatSetting.getInt() / 10;
               }
            }
         }
      }
   }

   @Inject(method = "setScreenAndShow", at = @At("HEAD"), cancellable = true)
   private void onetap$parkedSetScreenAndShow(Screen var1, CallbackInfo var2) {
      if (BotConnection.BOT_CONNECTION.m_9_boolean()) {
         var2.cancel();
      }
   }

   @Inject(method = "disconnectFromWorld", at = @At("HEAD"), cancellable = true)
   private void onetap$pvpSafeDisconnect(Component var1, CallbackInfo var2) {
      if (BotConnection.BOT_CONNECTION.m_9_boolean()) {
         var2.cancel();
      } else {
         PvPSafe var3 = Onetap.MODULE_MANAGER.get(PvPSafe.class);
         if (var3 != null && var3.m_9_boolean()) {
            var3.openWarningScreen();
            var2.cancel();
         }
      }
   }

   @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V", at = @At("HEAD"), cancellable = true)
   private void onetap$parkedDisconnect(Screen var1, boolean var2, boolean var3, CallbackInfo var4) {
      if (BotConnection.BOT_CONNECTION.m_9_boolean()) {
         var4.cancel();
      }
   }

   @Inject(method = "clearClientLevel", at = @At("HEAD"), cancellable = true)
   private void onetap$parkedClearClientLevel(Screen var1, CallbackInfo var2) {
      if (BotConnection.BOT_CONNECTION.m_9_boolean()) {
         var2.cancel();
      }
   }
}
