package wtf.onetap.mixin.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.Dro4ka;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin {
   @Unique
   private static Dro4ka onetap$dro4kaModule;

   @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("TAIL"))
   private void onetap$dro4ka(AvatarRenderState var1, CallbackInfo var2) {
      Dro4ka var3 = onetap$dro4kaModule;
      if (var3 == null) {
         var3 = Onetap.MODULE_MANAGER.get(Dro4ka.class);
         onetap$dro4kaModule = var3;
      }

      if (var3 != null && var3.isEnabled()) {
         Minecraft var4 = Minecraft.getInstance();
         if (var4.level != null && var4.player != null) {
            if (var1.id == var4.player.getId() || var3.m_5_boolean()) {
               if (var4.level.getEntity(var1.id) instanceof Player var6) {
                  if (var3.m_2_boolean(var6)) {
                     PlayerModel var7 = (PlayerModel)(Object)this;
                     var7.rightArm.xRot = 0.0F;
                     var7.rightArm.zRot = 0.0F;
                     var7.rightArm.yRot = 0.0F;
                     var7.leftArm.xRot = deg(-32.0F);
                     var7.leftArm.zRot = deg(32.2F);
                     var7.leftArm.yRot = 0.0F;
                     float var8 = var1.ageInTicks;
                     float var9 = deg(10.0F);
                     float var10 = deg(8.0F);
                     float var11 = deg(4.0F);
                     var7.leftArm.xRot = var7.leftArm.xRot + Mth.sin(var8 * 1.8F) * var9;
                     var7.leftArm.zRot = var7.leftArm.zRot + Mth.sin(var8 * 2.1F) * var10;
                     var7.leftArm.yRot = var7.leftArm.yRot + Mth.cos(var8 * 1.2F) * var11;
                     var7.rightArm.xRot = var7.rightArm.xRot + Mth.sin(var8 * 1.8F + (float) Math.PI) * (var9 * 0.25F);
                     var7.rightArm.zRot = var7.rightArm.zRot + Mth.sin(var8 * 2.1F + (float) (Math.PI / 2)) * (var10 * 0.15F);
                  }
               }
            }
         }
      }
   }

   private static float deg(float var0) {
      return var0 * (float) (Math.PI / 180.0);
   }
}
