package wtf.onetap.module.render;

import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.gui.ClickGuiScreen;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Render2D;
import wtf.onetap.setting.FloatSetting;

@ModuleInformation(name = "ClickGUI", description = "Графический интерфейс", category = Category.RENDER, key = 344)
public class ClickGUI extends Module {
   private static ClickGUI clickGUI;
   private final FloatSetting glow = new FloatSetting("Свечение", 1.0F, 0.0F, 1.5F, 0.05F).build();
   private final FloatSetting screenDim = new FloatSetting("Затемнение экрана", 0.35F, 0.0F, 1.0F, 0.05F).build();
   private static String text;

   public ClickGUI() {
      clickGUI = this;
   }

   public static boolean isActive() {
      return clickGUI != null && clickGUI.isEnabled();
   }

   public static float getGlow() {
      return clickGUI != null ? clickGUI.glow.getFloat() : 0.0F;
   }

   public static void disable() {
      if (clickGUI != null) {
         clickGUI.setEnabled(false);
      }
   }

   public static void m_8(float floatValue, float floatValue2, float floatValue3) {
      if (clickGUI != null) {
         clickGUI.m_1_void(floatValue, floatValue2, floatValue3);
      }
   }

   private void m_1_void(float floatValue, float floatValue2, float floatValue3) {
      m_3_String();
      float floatValue4 = Render2D.getScale();
      float floatValue5 = Render2D.getOffsetX();
      float floatValue6 = Render2D.getOffsetY();
      Render2D.setTransform(1.0F, 0.0F, 0.0F);

      try {
         int value = (int)(100.0F * this.screenDim.getFloat() * floatValue);
         if (value > 0) {
            Draw2D.rect(0.0F, 0.0F, floatValue2, floatValue3, ColorUtil.rgba(0, 0, 0, value));
         }
      } finally {
         Render2D.setTransform(floatValue4, floatValue5, floatValue6);
      }
   }

   @Override
   public void onEnable() {
      ClickGuiScreen.open();
   }

   @Override
   public void onDisable() {
      ClickGuiScreen.close();
   }

   public static void m_9_void(String text2) {
      text = text2;
   }

   public static String m_3_String() {
      return text;
   }

   static {
      m_9_void("OkPKB");
   }
}
