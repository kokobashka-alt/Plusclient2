package wtf.onetap.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.module.render.ArmorDurability;
import wtf.onetap.module.render.GlowESP;
import wtf.onetap.module.render.SeeInvisibles;
import wtf.onetap.module.render.Tags;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
   @Inject(
      method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
      at = @At("RETURN")
   )
   private void onetap$extractRenderState(LivingEntity var1, LivingEntityRenderState var2, float var3, CallbackInfo var4) {
      if (var1 instanceof LocalPlayer && Onetap.ROTATION_MODES.isFlag()) {
         var2.xRot = Onetap.ROTATION_MODES.getLerpedPitch(var3);
      }

      if (var1 instanceof LocalPlayer) {
         KillAura var5 = Onetap.MODULE_MANAGER.get(KillAura.class);
         if (var5 != null && var5.isEnabled()) {
            var2.bodyRot = var2.bodyRot + var5.m_8_float();
         }
      }

      SeeInvisibles var10 = Onetap.MODULE_MANAGER.get(SeeInvisibles.class);
      if (var10 != null && var10.isEnabled() && var1 instanceof Player && var1.isInvisible()) {
         var2.isInvisibleToPlayer = false;
      }

      ArmorDurability var6 = Onetap.MODULE_MANAGER.get(ArmorDurability.class);
      if (var6 != null && var6.isEnabled()) {
         var6.m_0(var2, var1);
      }

      if (var1 instanceof Player var7) {
         GlowESP var8 = Onetap.MODULE_MANAGER.get(GlowESP.class);
         if (var8 != null && var8.isEnabled()) {
            int var9 = var8.getGlowColor(var7);
            if (var9 != 0) {
               var2.outlineColor = var9;
            }
         }
      }
   }

   @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z", at = @At("HEAD"), cancellable = true)
   private void onetap$hidePlayerNameTag(LivingEntity var1, double var2, CallbackInfoReturnable<Boolean> var4) {
      if (var1 instanceof Player) {
         Tags var5 = Onetap.MODULE_MANAGER.get(Tags.class);
         if (var5 != null && var5.isEnabled()) {
            var4.setReturnValue(false);
         }
      }
   }

   @Redirect(
      method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ARGB;multiply(II)I")
   )
   private int onetap$multiplyColor(int var1, int var2, LivingEntityRenderState var3, PoseStack var4, SubmitNodeCollector var5, CameraRenderState var6) {
      SeeInvisibles var7 = Onetap.MODULE_MANAGER.get(SeeInvisibles.class);
      if (var7 != null && var7.isEnabled() && var3.isInvisible && !var3.isInvisibleToPlayer) {
         var1 = ARGB.white(var7.opacity.getValue());
      }

      return ARGB.multiply(var1, var2);
   }
}
