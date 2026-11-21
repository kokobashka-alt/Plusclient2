package wtf.onetap.mixin.client;

import net.minecraft.client.ClientClockManager;
import net.minecraft.core.Holder;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.TimeChanger;

@Mixin(ClientClockManager.class)
public class ClientClockManagerMixin {
   @Inject(method = "getTotalTicks", at = @At("RETURN"), cancellable = true)
   private void onetap$timeChanger(Holder<WorldClock> var1, CallbackInfoReturnable<Long> var2) {
      TimeChanger var3 = Onetap.MODULE_MANAGER.get(TimeChanger.class);
      if (var3 != null && var3.isEnabled() && var1.is(WorldClocks.OVERWORLD)) {
         var2.setReturnValue(var3.adjustTime((Long)var2.getReturnValue()));
      }
   }
}
