package wtf.onetap.mixin.entity;

import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
import wtf.onetap.combat.rotation.RotationModes;
import wtf.onetap.event.CloseContainerEvent;
import wtf.onetap.event.DropItemEvent;
import wtf.onetap.event.MoveEvent;
import wtf.onetap.event.NoSlowEvent;
import wtf.onetap.event.PostMotionEvent;
import wtf.onetap.event.PreMotionEvent;
import wtf.onetap.event.RayTraceEvent;
import wtf.onetap.module.combat.NoEntityTrace;
import wtf.onetap.module.movement.NoClip;
import wtf.onetap.module.movement.NoSlow;
import wtf.onetap.module.movement.Timer;
import wtf.onetap.module.movement.WaterSpeed;
import wtf.onetap.module.player.NoPush;
import wtf.onetap.obf.C6_z;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MovementStateAccess;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin implements MovementStateAccess {
   @Shadow
   private boolean wasSprinting;
   @Shadow
   private double xLast;
   @Shadow
   private double yLast;
   @Shadow
   private double zLast;
   @Shadow
   private boolean lastOnGround;
   @Shadow
   private int positionReminder;

   @Override
   public boolean onetap$wasSprinting() {
      return this.wasSprinting;
   }

   @Override
   public void onetap$setWasSprinting(boolean var1) {
      this.wasSprinting = var1;
   }

   @Override
   public void onetap$setLastSent(double var1, double var3, double var5, boolean var7) {
      this.xLast = var1;
      this.yLast = var3;
      this.zLast = var5;
      this.lastOnGround = var7;
      this.positionReminder = 0;
   }

   @Redirect(
      method = "pick",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"
      )
   )
   private static EntityHitResult onetap$noEntityTrace(Entity var0, Vec3 var1, Vec3 var2, AABB var3, Predicate<Entity> var4, double var5) {
      NoEntityTrace var7 = Onetap.MODULE_MANAGER.get(NoEntityTrace.class);
      return var7 != null && var7.isEnabled() ? null : ProjectileUtil.getEntityHitResult(var0, var1, var2, var3, var4, var5);
   }

   @Redirect(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"))
   private static HitResult onetap$rayTrace(Entity var0, double var1, float var3, boolean var4) {
      Vec3 var5 = var0.getEyePosition(var3);
      Vec3 var6 = var0.getViewVector(var3);
      Vec3 var7 = var5.add(var6.scale(var1));
      RayTraceEvent var8 = new RayTraceEvent(var0, var5, var7, Block.OUTLINE, var4 ? Fluid.ANY : Fluid.NONE);
      Onetap.EVENT_BUS.unregister(var8);
      return var8.getResult() != null
         ? var8.getResult()
         : var0.level().clip(new ClipContext(var8.getStart(), var8.getEnd(), var8.getBlock(), var8.getFluid(), var0));
   }

   @Inject(method = "tick", at = @At("HEAD"))
   private void onetap$playerTick(CallbackInfo var1) {
      BotConnection.BOT_CONNECTION.m_7_void();
      C6_z.m_1_void();
   }

   @Redirect(method = "applyInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getYRot()F"))
   private float onetap$cameraBobYaw(LocalPlayer var1) {
      RotationModes var2 = Onetap.ROTATION_MODES;
      return var2.isFlag() && BotManager.BOT_MANAGER.m_1_boolean() ? var2.getRenderYaw() : var1.getYRot();
   }

   @Redirect(method = "applyInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getXRot()F"))
   private float onetap$cameraBobPitch(LocalPlayer var1) {
      RotationModes var2 = Onetap.ROTATION_MODES;
      return var2.isFlag() && BotManager.BOT_MANAGER.m_1_boolean() ? var2.getRenderPitch() : var1.getXRot();
   }

   @Inject(method = "moveTowardsClosestSpace", at = @At("HEAD"), cancellable = true)
   private void onetap$noPushBlocks(double var1, double var3, CallbackInfo var5) {
      NoPush var6 = Onetap.MODULE_MANAGER.get(NoPush.class);
      if (var6 != null && var6.isEnabled() && var6.modes.isSelected("Блоки")) {
         var5.cancel();
      }
   }

   @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
   private Vec3 onetap$move(Vec3 var1) {
      if (!BotManager.BOT_MANAGER.m_1_boolean()) {
         return var1;
      }

      MoveEvent var2 = new MoveEvent(var1);
      Onetap.EVENT_BUS.unregister(var2);
      return var2.getVec3();
   }

   @Inject(method = "closeContainer", at = @At("HEAD"), cancellable = true)
   private void onetap$closeInventory(CallbackInfo var1) {
      CloseContainerEvent var2 = new CloseContainerEvent();
      Onetap.EVENT_BUS.unregister(var2);
      if (var2.isCancelled()) {
         ((LocalPlayer)(Object)this).clientSideCloseContainer();
         var1.cancel();
      }
   }

   @Inject(method = "modifyInput(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/world/phys/Vec2;", at = @At("HEAD"))
   private void onetap$noSlowEvent(Vec2 var1, CallbackInfoReturnable<Vec2> var2) {
      LocalPlayer var3 = Minecraft.getInstance().player;
      NoSlow var4 = Onetap.MODULE_MANAGER.get(NoSlow.class);
      if (var4 != null && var4.isEnabled() && var3 != null && var3.isUsingItem()) {
         NoSlowEvent var5 = new NoSlowEvent();
         Onetap.EVENT_BUS.unregister(var5);
         NoSlow.flag = var5.isCancelled();
      } else {
         NoSlow.flag = false;
      }
   }

   @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;aiStep()V"))
   private void onetap$noSlowForceSprint(CallbackInfo var1) {
      NoSlow.m_3((LocalPlayer)(Object)this);
   }

   @Inject(method = "isSlowDueToUsingItem", at = @At("HEAD"), cancellable = true)
   private void onetap$noSlowSprint(CallbackInfoReturnable<Boolean> var1) {
      if (NoSlow.m_9_boolean()) {
         var1.setReturnValue(false);
      }
   }

   @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;aiStep()V"))
   private void onetap$waterSpeedSprint(CallbackInfo var1) {
      WaterSpeed.m_6((LocalPlayer)(Object)this);
   }

   @ModifyArg(
      method = "modifyInput(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/world/phys/Vec2;",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec2;scale(F)Lnet/minecraft/world/phys/Vec2;", ordinal = 1),
      index = 0
   )
   private float onetap$noSlowMultiplier(float var1) {
      NoSlow var2 = Onetap.MODULE_MANAGER.get(NoSlow.class);
      return var2 != null && var2.isEnabled() && NoSlow.flag ? 1.0F : var1;
   }

   @Inject(method = "drop(Z)Z", at = @At("HEAD"), cancellable = true)
   private void onetap$onDropItem(boolean var1, CallbackInfoReturnable<Boolean> var2) {
      DropItemEvent var3 = new DropItemEvent(((LocalPlayer)(Object)this).getInventory().getSelectedSlot());
      Onetap.EVENT_BUS.unregister(var3);
      if (var3.isCancelled()) {
         var2.setReturnValue(false);
      }
   }

   @Inject(method = "sendIsSprintingIfNeeded", at = @At("HEAD"), cancellable = true)
   private void onetap$sendIsSprintingIfNeeded(CallbackInfo var1) {
      if (InventoryUtil.m_8_boolean()) {
         var1.cancel();
      }
   }

   @Inject(method = "sendPosition", at = @At("HEAD"), cancellable = true)
   private void onetap$timerSkipPosition(CallbackInfo var1) {
      if (Timer.m_4_boolean()) {
         var1.cancel();
      }
   }

   @Inject(method = "aiStep", at = @At("HEAD"))
   private void onetap$noClipLivingUpdate(CallbackInfo var1) {
      if (BotManager.BOT_MANAGER.m_1_boolean()) {
         NoClip var2 = Onetap.MODULE_MANAGER.get(NoClip.class);
         if (var2 != null) {
            var2.m_7e_void();
         }
      }
   }

   @Redirect(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getYRot()F"))
   private float onetap$noClipSendYaw(LocalPlayer var1) {
      NoClip var2 = Onetap.MODULE_MANAGER.get(NoClip.class);
      return BotManager.BOT_MANAGER.m_1_boolean() && var2 != null && var2.m_6_boolean() ? var2.vector2f.x : var1.getYRot();
   }

   @Redirect(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getXRot()F"))
   private float onetap$noClipSendPitch(LocalPlayer var1) {
      NoClip var2 = Onetap.MODULE_MANAGER.get(NoClip.class);
      return BotManager.BOT_MANAGER.m_1_boolean() && var2 != null && var2.m_6_boolean() ? var2.vector2f.y : var1.getXRot();
   }

   @Inject(method = "sendPosition", at = @At("HEAD"), cancellable = true)
   private void onetap$preMotion(CallbackInfo var1) {
      if (BotManager.BOT_MANAGER.m_1_boolean()) {
         InventoryUtil.m_2_void();
         Onetap.EVENT_BUS.unregister(new PreMotionEvent());
         NoClip var2 = Onetap.MODULE_MANAGER.get(NoClip.class);
         if (var2 != null && var2.isFlying()) {
            var1.cancel();
         }
      }
   }

   @Inject(method = "sendPosition", at = @At("RETURN"))
   private void onetap$onPostMotion(CallbackInfo var1) {
      if (BotManager.BOT_MANAGER.m_1_boolean()) {
         Onetap.EVENT_BUS.unregister(new PostMotionEvent());
      }
   }
}
