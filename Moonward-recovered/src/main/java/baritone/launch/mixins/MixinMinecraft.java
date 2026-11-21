package baritone.launch.mixins;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.PlayerUpdateEvent;
import baritone.api.event.events.TickEvent;
import baritone.api.event.events.WorldEvent;
import baritone.api.event.events.type.EventState;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
   @Shadow
   public LocalPlayer player;
   @Shadow
   public ClientLevel level;
   @Unique
   private BiFunction<EventState, TickEvent.Type, TickEvent> tickProvider;

   @Inject(method = "<init>", at = @At("RETURN"))
   private void postInit(CallbackInfo var1) {
      BaritoneAPI.getProvider().getPrimaryBaritone();
   }

   @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;tick()V", shift = Shift.AFTER))
   private void runTick(CallbackInfo var1) {
      this.tickProvider = TickEvent.createNextProvider();

      for (IBaritone var3 : BaritoneAPI.getProvider().getAllBaritones()) {
         TickEvent.Type var4 = var3.getPlayerContext().player() != null && var3.getPlayerContext().world() != null ? TickEvent.Type.IN : TickEvent.Type.OUT;
         var3.getGameEventHandler().onTick(this.tickProvider.apply(EventState.PRE, var4));
      }
   }

   @Inject(method = "tick", at = @At("RETURN"))
   private void postRunTick(CallbackInfo var1) {
      if (this.tickProvider != null) {
         for (IBaritone var3 : BaritoneAPI.getProvider().getAllBaritones()) {
            TickEvent.Type var4 = var3.getPlayerContext().player() != null && var3.getPlayerContext().world() != null ? TickEvent.Type.IN : TickEvent.Type.OUT;
            var3.getGameEventHandler().onPostTick(this.tickProvider.apply(EventState.POST, var4));
         }

         this.tickProvider = null;
      }
   }

   @Inject(method = "tick", at = @At(value = "INVOKE", target = "net/minecraft/client/multiplayer/ClientLevel.tickEntities()V", shift = Shift.AFTER))
   private void postUpdateEntities(CallbackInfo var1) {
      IBaritone var2 = BaritoneAPI.getProvider().getBaritoneForPlayer(this.player);
      if (var2 != null) {
         var2.getGameEventHandler().onPlayerUpdate(new PlayerUpdateEvent(EventState.POST));
      }
   }

   @Inject(method = "setLevel", at = @At("HEAD"))
   private void preLoadWorld(ClientLevel var1, CallbackInfo var2) {
      if (this.level != null || var1 != null) {
         BaritoneAPI.getProvider().getPrimaryBaritone().getGameEventHandler().onWorldEvent(new WorldEvent(var1, EventState.PRE));
      }
   }

   @Inject(method = "setLevel", at = @At("RETURN"))
   private void postLoadWorld(ClientLevel var1, CallbackInfo var2) {
      BaritoneAPI.getProvider().getPrimaryBaritone().getGameEventHandler().onWorldEvent(new WorldEvent(var1, EventState.POST));
   }

   @Redirect(
      method = "tick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;screen()Lnet/minecraft/client/gui/screens/Screen;", ordinal = 1)
   )
   private Screen passEvents(Gui var1) {
      return BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing() && this.player != null ? null : var1.screen();
   }
}
