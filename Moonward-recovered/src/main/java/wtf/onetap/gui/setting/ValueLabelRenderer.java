package wtf.onetap.gui.setting;

import net.minecraft.client.Minecraft;
import wtf.onetap.gui.ColorPicker;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;

public class ValueLabelRenderer {
   private float floatValue;
   private long longValue;
   private long longValue2;
   private long longValue3;

   public ValueLabelRenderer() {
   }

   public void m_7(String text, float floatValue2, float floatValue3, int value, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, float floatValue10) {
      ColorPicker.m_0_StringArr();
      long longValue4 = System.currentTimeMillis();
      float floatValue11 = this.longValue3 == 0L ? 0.0F : Math.min(0.05F, (float)(longValue4 - this.longValue3) / 1000.0F);
      this.longValue3 = longValue4;
      float floatValue12 = Draw2D.getTextWidth(text, Fonts.SUPPLIER16, floatValue5) - floatValue4;
      boolean flag = floatValue12 > 0.0F && m_8_boolean(floatValue7, floatValue8, floatValue9, floatValue10);
      float floatValue13;
      float floatValue14;
      if (flag) {
         if (this.longValue == 0L) {
            this.longValue = longValue4;
         }

         this.longValue2 = 0L;
         boolean flag2 = longValue4 - this.longValue >= 150L;
         floatValue13 = flag2 ? floatValue12 + 3.0F : this.floatValue;
         floatValue14 = 45.0F;
      } else {
         this.longValue = 0L;
         if (this.longValue2 == 0L) {
            this.longValue2 = longValue4;
         }

         boolean flag3 = longValue4 - this.longValue2 >= 150L;
         floatValue13 = flag3 ? 0.0F : this.floatValue;
         floatValue14 = 110.0F;
      }

      float floatValue15 = floatValue14 * floatValue11;
      this.floatValue = this.floatValue + Math.max(-floatValue15, Math.min(floatValue15, floatValue13 - this.floatValue));
      float floatValue16 = floatValue4 * (1.0F - floatValue6);
      float floatValue17 = floatValue16 > 0.0F ? Math.clamp(1.0F - (floatValue12 + 3.0F - this.floatValue) / floatValue16, 0.0F, 1.0F) : 1.0F;
      float floatValue18 = floatValue6 + (1.0F - floatValue6) * floatValue17;
      Render2D.m_6(Fonts.SUPPLIER16.get(), text, floatValue2 - this.floatValue, floatValue3, floatValue5, value, floatValue18, 1.0F, floatValue4, floatValue2);
   }

   private static boolean m_8_boolean(float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      Minecraft mc = Minecraft.getInstance();
      double doubleValue = mc.getWindow().getGuiScale();
      float floatValue6 = (float)(mc.mouseHandler.xpos() / doubleValue - Render2D.getOffsetX()) / Render2D.getScale();
      float floatValue7 = (float)(mc.mouseHandler.ypos() / doubleValue - Render2D.getOffsetY()) / Render2D.getScale();
      return floatValue6 >= floatValue2 && floatValue6 <= floatValue2 + floatValue4 && floatValue7 >= floatValue3 && floatValue7 <= floatValue3 + floatValue5;
   }
}
