package baritone.launch.mixins;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.PlayerUpdateEvent;
import baritone.api.event.events.SprintStateEvent;
import baritone.api.event.events.type.EventState;
import baritone.behavior.LookBehavior;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.obf.w;

@Mixin(LocalPlayer.class)
public class MixinClientPlayerEntity {
   @Unique
   private static final MethodHandle MAY_FLY = baritone$resolveMayFly();

   @Unique
   private static MethodHandle baritone$resolveMayFly() {
      try {
         Lookup var0 = MethodHandles.publicLookup();
         MethodType var10005 = MethodType.methodType(boolean.class);
         return var0.findVirtual(LocalPlayer.class, w.b("mayFly", LocalPlayer.class, var10005.parameterArray()), var10005);
      } catch (NoSuchMethodException var1) {
         return null;
      } catch (IllegalAccessException var2) {
         throw new RuntimeException(var2);
      }
   }

   @Inject(method = "tick", at = @At(value = "INVOKE", target = "net/minecraft/client/player/AbstractClientPlayer.tick()V", shift = Shift.AFTER))
   private void onPreUpdate(CallbackInfo var1) {
      IBaritone var2 = BaritoneAPI.getProvider().getBaritoneForPlayer((LocalPlayer)(Object)this);
      if (var2 != null) {
         var2.getGameEventHandler().onPlayerUpdate(new PlayerUpdateEvent(EventState.PRE));
      }
   }

   @Redirect(method = "aiStep", at = @At(value = "FIELD", target = "net/minecraft/world/entity/player/Abilities.mayfly:Z"))
   @Group(name = "mayFly", min = 1, max = 1)
   private boolean isAllowFlying(Abilities var1) {
      IBaritone var2 = BaritoneAPI.getProvider().getBaritoneForPlayer((LocalPlayer)(Object)this);
      return var2 == null ? var1.mayfly : !var2.getPathingBehavior().isPathing() && var1.mayfly;
   }

   @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;mayFly()Z"))
   @Group(name = "mayFly", min = 1, max = 1)
   private boolean onMayFlyNeoforge(LocalPlayer var1) throws java.lang.Throwable {
      IBaritone var2 = BaritoneAPI.getProvider().getBaritoneForPlayer((LocalPlayer)(Object)this);
      return var2 == null
         ? (boolean)MAY_FLY.invokeExact((LocalPlayer)var1)
         : !var2.getPathingBehavior().isPathing() && (boolean)MAY_FLY.invokeExact((LocalPlayer)var1);
   }

   @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Input;sprint()Z"))
   private boolean redirectSprintInput(Input var1) {
      IBaritone var2 = BaritoneAPI.getProvider().getBaritoneForPlayer((LocalPlayer)(Object)this);
      if (var2 == null) {
         return var1.sprint();
      } else {
         SprintStateEvent var3 = new SprintStateEvent();
         var2.getGameEventHandler().onPlayerSprintState(var3);
         if (var3.getState() != null) {
            return var3.getState();
         } else {
            return var2 != BaritoneAPI.getProvider().getPrimaryBaritone() ? false : var1.sprint();
         }
      }
   }

   @Inject(method = "rideTick", at = @At("HEAD"))
   private void updateRidden(CallbackInfo var1) {
      IBaritone var2 = BaritoneAPI.getProvider().getBaritoneForPlayer((LocalPlayer)(Object)this);
      if (var2 != null) {
         ((LookBehavior)var2.getLookBehavior()).pig();
      }
   }

   @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;tryToStartFallFlying()Z"))
   private boolean tryToStartFallFlying(LocalPlayer var1) {
      IBaritone var2 = BaritoneAPI.getProvider().getBaritoneForPlayer(var1);
      return var2 != null && var2.getPathingBehavior().isPathing() ? false : var1.tryToStartFallFlying();
   }
}
