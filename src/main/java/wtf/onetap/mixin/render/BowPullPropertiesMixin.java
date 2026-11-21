package wtf.onetap.mixin.render;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.render.ViewModelRenderer;

@Mixin(UseDuration.class)
public class BowPullPropertiesMixin {
   @Inject(method = "get", at = @At("RETURN"), cancellable = true)
   private void onetap$holdMyItemsBowPull(ItemStack var1, ClientLevel var2, ItemOwner var3, int var4, CallbackInfoReturnable<Float> var5) {
      if (var3 != null && var3.asLivingEntity() != null) {
         var5.setReturnValue(ViewModelRenderer.m_0(var3.asLivingEntity(), var1, var5.getReturnValueF()));
      }
   }
}
