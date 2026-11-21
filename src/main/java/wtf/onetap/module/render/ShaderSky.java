package wtf.onetap.module.render;

import net.minecraft.client.renderer.state.level.CameraRenderState;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.SkyRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "ShaderSky", description = "Шейдерное небо", category = Category.RENDER)
public class ShaderSky extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "Скопление", "Скопление", "Северное сияние", "Гроза", "Космос", "Чёрная дыра", "Токио").build();
   private final ModeSetting modeSetting;
   private final FloatSetting floatSetting;
   private final FloatSetting floatSetting2;
   private final FloatSetting floatSetting3;
   private final BooleanSetting booleanSetting;
   private final ColorSetting colorSetting;
   private final ColorSetting colorSetting2;
   private final ColorSetting colorSetting3;

   public ShaderSky() {
      Notifications.m_5_C3_sArr();
      this.modeSetting = new ModeSetting("Время", "Ночь", "Утро", "День", "Ночь").visibleWhen(() -> ((String)this.mode.getValue()).equals("Токио")).build();
      this.floatSetting = new FloatSetting("Масштаб", 1.2F, 0.25F, 4.0F, 0.05F).build();
      this.floatSetting2 = new FloatSetting("Скорость", 1.0F, 0.1F, 4.0F, 0.05F).build();
      this.floatSetting3 = new FloatSetting("Яркость", 1.0F, 0.1F, 3.0F, 0.05F).build();
      this.booleanSetting = new BooleanSetting("Цвет от темы", true).build();
      this.colorSetting = new ColorSetting("Основной", ColorUtil.rgb(4, 12, 28)).visibleWhen(() -> !(Boolean)this.booleanSetting.getValue()).build();
      this.colorSetting2 = new ColorSetting("Вторичный", ColorUtil.rgb(22, 118, 170)).visibleWhen(() -> !(Boolean)this.booleanSetting.getValue()).build();
      this.colorSetting3 = new ColorSetting("Акцент", ColorUtil.rgb(124, 244, 255)).visibleWhen(() -> !(Boolean)this.booleanSetting.getValue()).build();
   }

   private int m_4_int() {
      return switch ((String)this.mode.getValue()) {
         case "Гроза" -> 1;
         case "Северное сияние" -> 2;
         case "Космос" -> 3;
         case "Чёрная дыра" -> 4;
         case "Токио" -> 5;
         default -> 0;
      };
   }

   public void m_3(CameraRenderState cameraRenderState) {
      if (this.isEnabled() && MinecraftAccess.MINECRAFT.level != null) {
         float floatValue = (float)(System.nanoTime() * 1.0E-9) * this.floatSetting2.getFloat();
         int value;
         int value2;
         int value3;
         if ((Boolean)this.booleanSetting.getValue()) {
            value = ColorUtil.primaryColor();
            value2 = ColorUtil.secondaryColor();
            value3 = ColorUtil.lerpColor(value, value2, 0.55F);
         } else {
            value = (Integer)this.colorSetting.getValue();
            value2 = (Integer)this.colorSetting2.getValue();
            value3 = (Integer)this.colorSetting3.getValue();
         }
         float floatValue2 = switch ((String)this.modeSetting.getValue()) {
            case "Утро" -> 0.0F;
            case "День" -> 1.0F;
            default -> 2.0F;
         };
         SkyRenderer.m_1(cameraRenderState, floatValue, this.floatSetting.getFloat(), this.floatSetting3.getFloat(), value, value2, value3, this.m_4_int(), floatValue2);
      }
   }
}
