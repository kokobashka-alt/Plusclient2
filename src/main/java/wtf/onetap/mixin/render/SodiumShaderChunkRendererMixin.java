package wtf.onetap.mixin.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.List;
import java.util.Map;
import net.caffeinemc.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.obf.C7_p;

@Mixin(ShaderChunkRenderer.class)
public class SodiumShaderChunkRendererMixin {
   @Shadow
   private static Map<TerrainRenderPass, RenderPipeline> programs;
   @Unique
   private static final Identifier ONETAP$SHADER = Identifier.fromNamespaceAndPath("onetap", "world/sodium_terrain");
   @Unique
   private static int onetap$builtSignature = -2;

   @Inject(method = "compileProgram", at = @At("HEAD"))
   private void onetap$checkStale(TerrainRenderPass var1, CallbackInfoReturnable<RenderPipeline> var2) {
      int var3 = C7_p.m_1_boolean() ? C7_p.m_5_int() : -1;
      if (var3 != onetap$builtSignature) {
         programs.clear();
         onetap$builtSignature = var3;
      }
   }

   @Inject(method = "createShaderConstants", at = @At("RETURN"))
   private static void onetap$defines(TerrainRenderPass var0, CallbackInfoReturnable<List<String>> var1) {
      if (C7_p.m_1_boolean()) {
         List var2 = (List)var1.getReturnValue();
         int var3 = C7_p.m_5_int();
         int var4 = var3 >> 26;
         int var5 = var3 >> 24 & 3;
         if (var4 == 1 || var4 == 3) {
            var2.add("ONETAP_PARTICLES");
         }

         if (var4 == 2 || var4 == 3) {
            var2.add("ONETAP_LIFT");
         }

         if (var5 == 0) {
            var2.add("ONETAP_COLOR_BLOCK");
         }

         if (var5 == 2) {
            int var6 = var3 & 16777215;
            var2.add("ONETAP_COLOR_CUSTOM");
            var2.add("ONETAP_CR " + (var6 >> 16 & 0xFF));
            var2.add("ONETAP_CG " + (var6 >> 8 & 0xFF));
            var2.add("ONETAP_CB " + (var6 & 0xFF));
         }
      }
   }

   @ModifyArg(
      method = "createShader",
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/pipeline/RenderPipeline$Builder;withVertexShader(Lnet/minecraft/resources/Identifier;)Lcom/mojang/blaze3d/pipeline/RenderPipeline$Builder;"
      )
   )
   private Identifier onetap$vertexShader(Identifier var1) {
      return onetap$shader(var1);
   }

   @ModifyArg(
      method = "createShader",
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/pipeline/RenderPipeline$Builder;withFragmentShader(Lnet/minecraft/resources/Identifier;)Lcom/mojang/blaze3d/pipeline/RenderPipeline$Builder;"
      )
   )
   private Identifier onetap$fragmentShader(Identifier var1) {
      return onetap$shader(var1);
   }

   @Unique
   private static Identifier onetap$shader(Identifier var0) {
      return C7_p.m_1_boolean() ? ONETAP$SHADER : var0;
   }
}
