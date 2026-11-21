package wtf.onetap.mixin.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.onetap.obf.C5_d;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
   @Redirect(
      method = "extractShadow",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getChunk(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/chunk/ChunkAccess;"
      )
   )
   private ChunkAccess onetap$cachedShadowChunk(Level var1, BlockPos var2) {
      return C5_d.m_9(var1, var2);
   }

   @Redirect(
      method = "extractShadowPiece",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;)I")
   )
   private int onetap$cachedShadowBrightness(Level var1, BlockPos var2) {
      return C5_d.m_8(var1, var2);
   }
}
