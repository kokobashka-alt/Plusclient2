package wtf.onetap.mixin.entity.projectile;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.player.NoPush;

@Mixin(FishingHook.class)
public class FishingHookMixin {
   @Inject(method = "pullEntity", at = @At("HEAD"), cancellable = true)
   private void onetap$noPushFishingHook(Entity var1, CallbackInfo var2) {
      NoPush var3 = Onetap.MODULE_MANAGER.get(NoPush.class);
      if (var3 != null && var3.isEnabled() && var3.modes.isSelected("Удочка")) {
         if (var1 == Minecraft.getInstance().player) {
            var2.cancel();
         }
      }
   }
}
