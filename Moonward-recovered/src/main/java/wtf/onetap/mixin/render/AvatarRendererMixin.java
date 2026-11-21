package wtf.onetap.mixin.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.render.CrownLayer;
import wtf.onetap.render.WingsLayer;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
   @Inject(method = "<init>", at = @At("RETURN"))
   private void onetap$addWings(Context var1, boolean var2, CallbackInfo var3) {
      AvatarRenderer var4 = (AvatarRenderer)(Object)this;
      ((LivingEntityRendererAccessor)this).onetap$addLayer(new WingsLayer(var4));
      ((LivingEntityRendererAccessor)this).onetap$addLayer(new CrownLayer(var4));
   }
}
