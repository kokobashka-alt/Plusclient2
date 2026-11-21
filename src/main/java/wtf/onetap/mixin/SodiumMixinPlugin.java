package wtf.onetap.mixin;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class SodiumMixinPlugin implements IMixinConfigPlugin {
   private static final boolean SODIUM_PRESENT = FabricLoader.getInstance().isModLoaded("sodium");
   private static final boolean IAS_PRESENT = FabricLoader.getInstance().isModLoaded("ias");

   public static boolean isSodiumPresent() {
      return SODIUM_PRESENT;
   }

   public void onLoad(String var1) {
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String var1, String var2) {
      if (var2.endsWith("IasTitleOverlayMixin")) {
         return IAS_PRESENT;
      } else {
         return SODIUM_PRESENT ? true : !var2.endsWith("SodiumShaderChunkRendererMixin") && !var2.endsWith("SodiumUniformBufferManagerMixin");
      }
   }

   public void acceptTargets(Set<String> var1, Set<String> var2) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String var1, ClassNode var2, String var3, IMixinInfo var4) {
   }

   public void postApply(String var1, ClassNode var2, String var3, IMixinInfo var4) {
   }
}
