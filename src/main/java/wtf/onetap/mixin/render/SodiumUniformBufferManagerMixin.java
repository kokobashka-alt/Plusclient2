package wtf.onetap.mixin.render;

import net.caffeinemc.mods.sodium.client.render.chunk.UniformBufferManager;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.onetap.obf.C7_p;

@Mixin(UniformBufferManager.class)
public class SodiumUniformBufferManagerMixin {
   @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 0))
   private Object onetap$fadePeriod(OptionInstance<Double> var1) {
      return C7_p.m_1_boolean() ? C7_p.m_1_Double() : var1.get();
   }
}
