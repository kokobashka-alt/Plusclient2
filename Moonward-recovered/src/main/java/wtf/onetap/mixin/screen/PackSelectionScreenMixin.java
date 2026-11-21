package wtf.onetap.mixin.screen;

import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import wtf.onetap.obf.C8_f;
import wtf.onetap.util.ChatUtil;

@Mixin(PackSelectionScreen.class)
public abstract class PackSelectionScreenMixin {
   @ModifyArg(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util$OS;openPath(Ljava/nio/file/Path;)V"), index = 0)
   private Path onetap$launcherResourcePackDirectory(Path var1) {
      if (!ChatUtil.isUnHooked()) {
         return var1;
      }

      Path var2 = Minecraft.getInstance().getResourcePackDirectory().toAbsolutePath().normalize();
      return !var1.toAbsolutePath().normalize().equals(var2) ? var1 : C8_f.m_8_Path(var1);
   }
}
