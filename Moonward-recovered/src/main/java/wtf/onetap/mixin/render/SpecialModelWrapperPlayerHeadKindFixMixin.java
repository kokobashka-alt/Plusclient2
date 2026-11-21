package wtf.onetap.mixin.render;

import net.minecraft.client.renderer.item.SpecialModelWrapper.Unbaked;
import net.minecraft.client.renderer.special.SpecialModelRenderer.BakingContext;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.level.block.SkullBlock.Types;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Unbaked.class)
public abstract class SpecialModelWrapperPlayerHeadKindFixMixin {
   @Redirect(
      method = "bake",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/special/SpecialModelRenderer$Unbaked;bake(Lnet/minecraft/client/renderer/special/SpecialModelRenderer$BakingContext;)Lnet/minecraft/client/renderer/special/SpecialModelRenderer;"
      )
   )
   private SpecialModelRenderer<?> onetap$forcePlayerHeadRenderer(
      net.minecraft.client.renderer.special.SpecialModelRenderer.Unbaked<?> var1, BakingContext var2
   ) {
      return var1 instanceof net.minecraft.client.renderer.special.SkullSpecialRenderer.Unbaked var3 && var3.kind() == Types.PLAYER
         ? new net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer.Unbaked().bake(var2)
         : var1.bake(var2);
   }
}
