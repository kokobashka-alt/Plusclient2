package wtf.onetap.mixin.network;

import net.minecraft.network.PacketProcessor.ListenerAndPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.combat.BackTrack;

@Mixin(ListenerAndPacket.class)
public class ListenerAndPacketMixin {
   @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
   private void onetap$holdForBackTrack(CallbackInfo var1) {
      BackTrack var2 = Onetap.MODULE_MANAGER.get(BackTrack.class);
      if (var2 != null && var2.m_6((ListenerAndPacket<?>)(Object)this)) {
         var1.cancel();
      }
   }
}
