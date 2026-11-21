package wtf.onetap.mixin.player;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.event.AttackEntityEvent;
import wtf.onetap.event.BlockBreakEvent;
import wtf.onetap.event.ContainerClickEvent;
import wtf.onetap.event.UseItemEvent;
import wtf.onetap.event.UseItemOnEvent;
import wtf.onetap.obf.C6_z;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
   @Inject(method = "handleContainerInput", at = @At("HEAD"), cancellable = true)
   private void onetap$containerClick(int var1, int var2, int var3, ContainerInput var4, Player var5, CallbackInfo var6) {
      ContainerClickEvent var7 = new ContainerClickEvent(var1, var2, var3, var4, var5);
      Onetap.EVENT_BUS.unregister(var7);
      if (var7.isCancelled()) {
         var6.cancel();
      }
   }

   @Inject(method = "continueDestroyBlock", at = @At("HEAD"))
   private void onetap$blockBreaking(BlockPos var1, Direction var2, CallbackInfoReturnable<Boolean> var3) {
      Onetap.EVENT_BUS.unregister(new BlockBreakEvent(var1, var2));
   }

   @Inject(method = "startDestroyBlock", at = @At("HEAD"))
   private void onetap$startDestroy(BlockPos var1, Direction var2, CallbackInfoReturnable<Boolean> var3) {
      Onetap.EVENT_BUS.unregister(new BlockBreakEvent(var1, var2));
   }

   @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
   private void onetap$useItemOn(LocalPlayer var1, InteractionHand var2, BlockHitResult var3, CallbackInfoReturnable<InteractionResult> var4) {
      UseItemOnEvent var5 = new UseItemOnEvent(var2, var3);
      Onetap.EVENT_BUS.unregister(var5);
      if (var5.isCancelled()) {
         var4.setReturnValue(InteractionResult.FAIL);
      }
   }

   @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
   private void onetap$useItem(Player var1, InteractionHand var2, CallbackInfoReturnable<InteractionResult> var3) {
      UseItemEvent var4 = new UseItemEvent(var2);
      Onetap.EVENT_BUS.unregister(var4);
      if (var4.isCancelled()) {
         var3.setReturnValue(InteractionResult.FAIL);
      }
   }

   @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
   private void onetap$attack(Player var1, Entity var2, CallbackInfo var3) {
      AttackEntityEvent var4 = new AttackEntityEvent(var2);
      Onetap.EVENT_BUS.unregister(var4);
      if (var4.isCancelled()) {
         var3.cancel();
      } else {
         C6_z.m_7_void();
      }
   }
}
