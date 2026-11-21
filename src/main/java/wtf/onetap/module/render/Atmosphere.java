package wtf.onetap.module.render;

import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.AtmosphereRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Atmosphere", description = "Атмосферный туман", category = Category.RENDER)
public class Atmosphere extends Module {
   private final FloatSetting range = new FloatSetting("Дальность", 48.0F, 8.0F, 160.0F, 1.0F).build();
   private final FloatSetting density = new FloatSetting("Плотность", 1.0F, 0.15F, 4.0F, 0.05F).build();
   private final BooleanSetting themeColor = new BooleanSetting("Цвет от темы", true).build();
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(170, 195, 220, 255)).visibleWhen(() -> !(Boolean)this.themeColor.getValue()).build();

   public void m_1_void() {
      if (this.isEnabled() && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         int value = this.themeColor.getValue() ? ColorUtil.primaryColor() : (Integer)this.color.getValue();
         AtmosphereRenderer.m_6(value, this.range.getFloat(), this.density.getFloat());
      }
   }
}
