package wtf.onetap.module.render;

import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.SaturationRenderer;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Saturation", description = "Насыщенность и контраст", category = Category.RENDER)
public class Saturation extends Module {
   private final FloatSetting saturation = new FloatSetting("Насыщенность", 1.0F, 0.0F, 2.0F, 0.01F).build();
   private final FloatSetting contrast = new FloatSetting("Контраст", 1.0F, 0.0F, 2.0F, 0.01F).build();

   public void m_4_void() {
      if (this.isEnabled() && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         SaturationRenderer.m_7_void(this.saturation.getFloat(), this.contrast.getFloat());
      }
   }
}
