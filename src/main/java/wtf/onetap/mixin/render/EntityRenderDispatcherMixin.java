package wtf.onetap.mixin.render;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.KillEffects;
import wtf.onetap.render.EntityRenderHelper;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
   @Unique
   private final EntityRenderHelper onetap$playerRenderStates = new EntityRenderHelper();

   @Redirect(
      method = "extractEntity",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;"
      )
   )
   private EntityRenderState onetap$reusePlayerRenderState(EntityRenderer<?, ?> var1, Entity var2, float var3) {
      return this.onetap$playerRenderStates.m_9(var1, var2, var3);
   }

   @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
   private <E extends Entity> void onetap$hideDissolving(E var1, Frustum var2, double var3, double var5, double var7, CallbackInfoReturnable<Boolean> var9) {
      KillEffects var10 = Onetap.MODULE_MANAGER.get(KillEffects.class);
      if (var10 != null && var10.hasEffect(var1.getId())) {
         var9.setReturnValue(false);
      }
   }

   @Inject(method = "onResourceManagerReload", at = @At("RETURN"))
   private void onetap$clearPlayerRenderStates(CallbackInfo var1) {
      this.onetap$playerRenderStates.clear();
   }
}
