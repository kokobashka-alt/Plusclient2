package wtf.onetap.mixin.render;

import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.onetap.Onetap;
import wtf.onetap.module.player.AutoTool;

@Mixin(ArmedEntityRenderState.class)
public class ArmedEntityRenderStateMixin {
   @Redirect(
      method = "extractArmedEntityRenderState",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getItemHeldByArm(Lnet/minecraft/world/entity/HumanoidArm;)Lnet/minecraft/world/item/ItemStack;"
      )
   )
   private static ItemStack onetap$autoToolArmStack(LivingEntity var0, HumanoidArm var1) {
      AutoTool var2 = Onetap.MODULE_MANAGER.get(AutoTool.class);
      ItemStack var3 = var0.getItemHeldByArm(var1);
      return var2 != null ? var2.m_1(var0, var1, var3) : var3;
   }
}
