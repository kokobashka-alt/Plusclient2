package wtf.onetap.module.render;

import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.HandGlowRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "HandGlow", description = "Шейдерный glow/neon/огонь/волны вокруг рук", category = Category.RENDER)
public class HandGlow extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "Огонь", "Софт", "Неон", "Огонь", "Волны").build();
   private final BooleanSetting themeColor = new BooleanSetting("Цвет от темы", true).build();
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(255, 120, 40, 255)).visibleWhen(() -> !(Boolean)this.themeColor.getValue()).build();
   private final FloatSetting glow = new FloatSetting("Глоу", 0.2F, 0.2F, 9.0F, 0.05F).build();
   private final FloatSetting strength = new FloatSetting("Сила", 0.4F, 0.05F, 1.0F, 0.05F).build();
   private final FloatSetting speed = new FloatSetting("Скорость", 1.0F, 0.2F, 3.0F, 0.05F).visibleWhen(() -> !((String)this.mode.getValue()).equals("Софт")).build();

   @Override
   public void onDisable() {
      HandGlowRenderer.reset();
   }

   public void m_3_void() {
      if (this.isEnabled() && MinecraftAccess.MINECRAFT.player != null) {
         if (!MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson()) {
            HandGlowRenderer.reset();
         } else {
            int value = this.themeColor.getValue() ? ColorUtil.primaryColor() : (Integer)this.color.getValue();
            float floatValue = (float)System.nanoTime() * 1.0E-9F * this.speed.getFloat();
            HandGlowRenderer.m_5(value, this.glow.getFloat(), this.strength.getFloat(), this.m_3_int(), floatValue);
         }
      }
   }

   private int m_3_int() {
      String text = (String)this.mode.getValue();
      Notifications.m_5_C3_sArr();

      return switch (text) {
         case "Неон" -> 1;
         case "Огонь" -> 2;
         case "Волны" -> 3;
         default -> 0;
      };
   }
}
