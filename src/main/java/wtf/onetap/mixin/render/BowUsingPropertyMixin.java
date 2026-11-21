package wtf.onetap.mixin.render;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.IsUsingItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.render.ViewModelRenderer;

@Mixin(IsUsingItem.class)
public class BowUsingPropertyMixin {
   @Inject(method = "get", at = @At("RETURN"), cancellable = true)
   private void onetap$holdMyItemsBowUsing(
      ItemStack var1, ClientLevel var2, LivingEntity var3, int var4, ItemDisplayContext var5, CallbackInfoReturnable<Boolean> var6
   ) {
      if (var3 != null) {
         var6.setReturnValue(ViewModelRenderer.m_9(var3, var1, var6.getReturnValueZ()));
      }
   }
}
