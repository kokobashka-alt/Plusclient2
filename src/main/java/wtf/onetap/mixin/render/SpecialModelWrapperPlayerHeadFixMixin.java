package wtf.onetap.mixin.render;

import com.mojang.math.Transformation;
import java.util.Optional;
import net.minecraft.client.renderer.item.SpecialModelWrapper.Unbaked;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Unbaked.class)
public abstract class SpecialModelWrapperPlayerHeadFixMixin {
   @Shadow
   public abstract net.minecraft.client.renderer.special.SpecialModelRenderer.Unbaked<?> specialModel();

   @Redirect(
      method = "bake",
      at = @At(value = "INVOKE", target = "Lcom/mojang/math/Transformation;compose(Lorg/joml/Matrix4fc;Ljava/util/Optional;)Lorg/joml/Matrix4fc;")
   )
   private Matrix4fc onetap$fixSkullTransform(Matrix4fc var1, Optional<Transformation> var2) {
      if (var2.isEmpty()
         && (
            this.specialModel() instanceof net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer.Unbaked
               || this.specialModel() instanceof net.minecraft.client.renderer.special.SkullSpecialRenderer.Unbaked
         )) {
         var2 = Optional.of(new Transformation(new Vector3f(0.5F, 0.0F, 0.5F), new Quaternionf().rotationX((float) Math.PI), null, null));
      }

      return Transformation.compose(var1, var2);
   }
}
