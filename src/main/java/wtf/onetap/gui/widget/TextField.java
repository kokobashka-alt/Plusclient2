package wtf.onetap.gui.widget;

import net.minecraft.client.Minecraft;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.util.MouseUtil;

public class TextField {
   private final String text;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private boolean flag;
   private int value;
   private int value2;
   private boolean flag2;

   public TextField(String text2, float floatValue9, float floatValue10) {
      this.floatValue7 = 1.0F;
      this.value = -1;
      this.value2 = -1;
      this.text = text2;
      this.floatValue = floatValue9;
      this.floatValue2 = floatValue10;
   }

   public String getText() {
      return this.text;
   }

   public float m_5_float() {
      this.m_6_void();
      return this.floatValue = this.m_2_float(this.floatValue);
   }

   public float m_8_float() {
      this.m_6_void();
      return this.floatValue2 = this.m_0_float(this.floatValue2);
   }

   public float m_6_float() {
      return this.floatValue3 * this.floatValue7;
   }

   public float m_9_float() {
      return this.floatValue4 * this.floatValue7;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public void setFlag2(boolean flag3) {
      this.flag2 = flag3;
   }

   public void m_2_void(float floatValue9, float floatValue10) {
      this.m_6_void();
      this.floatValue = floatValue9;
      this.floatValue2 = floatValue10;
      this.m_8_void();
   }

   public void m_8_void(float floatValue9, float floatValue10) {
      this.floatValue3 = floatValue9;
      this.floatValue4 = floatValue10;
   }

   public void setFloatValue7(float floatValue9) {
      this.floatValue7 = floatValue9;
   }

   public float m_4_float() {
      return this.floatValue8 * this.floatValue7;
   }

   public void setFloatValue8(float floatValue9) {
      this.floatValue8 = floatValue9;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void m_1_void() {
      this.m_9(null);
   }

   public void m_9(MouseUtil mouseUtil) {
      if (this.flag) {
         this.flag2 = true;
         Minecraft mc = Minecraft.getInstance();
         double doubleValue = mc.getWindow().getGuiScale();
         float floatValue9 = (float)(mc.mouseHandler.xpos() / doubleValue - this.floatValue5);
         float floatValue10 = (float)(mc.mouseHandler.ypos() / doubleValue - this.floatValue6);
         if (mouseUtil != null) {
            float[] floatArray = mouseUtil.m_6(this, floatValue9, floatValue10);
            floatValue9 = floatArray[0];
            floatValue10 = floatArray[1];
         }

         this.m_2_void(floatValue9, floatValue10);
      }
   }

   public boolean m_0(float floatValue9, float floatValue10, int value3) {
      if (value3 == 0 && this.m_3_boolean(floatValue9, floatValue10)) {
         this.flag = true;
         this.floatValue5 = floatValue9 - this.m_5_float();
         this.floatValue6 = floatValue10 - this.m_8_float();
         return true;
      } else {
         return false;
      }
   }

   public void m_7_void() {
      this.flag = false;
   }

   private boolean m_3_boolean(float floatValue9, float floatValue10) {
      return floatValue9 >= this.m_5_float() && floatValue9 <= this.m_5_float() + this.m_6_float() && floatValue10 >= this.m_8_float() && floatValue10 <= this.m_8_float() + this.m_9_float();
   }

   public boolean m_2_boolean(float floatValue9, float floatValue10) {
      return this.m_3_boolean(floatValue9, floatValue10);
   }

   private void m_6_void() {
      ClickGUI.m_3_String();
      Minecraft mc = Minecraft.getInstance();
      if (mc != null && mc.getWindow() != null) {
         int value3 = mc.getWindow().getGuiScaledWidth();
         int value4 = mc.getWindow().getGuiScaledHeight();
         if (this.value < 0) {
            this.value = value3;
            this.value2 = value4;
         } else if (value3 != this.value || value4 != this.value2) {
            this.floatValue = this.floatValue * value3 / this.value;
            this.floatValue2 = this.floatValue2 * value4 / this.value2;
            this.value = value3;
            this.value2 = value4;
         }
      }
   }

   private float m_2_float(float floatValue9) {
      Minecraft mc = Minecraft.getInstance();
      return mc != null && mc.getWindow() != null ? Math.max(0.0F, Math.min(floatValue9, mc.getWindow().getGuiScaledWidth() - this.m_6_float())) : floatValue9;
   }

   private float m_0_float(float floatValue9) {
      Minecraft mc = Minecraft.getInstance();
      return mc != null && mc.getWindow() != null ? Math.max(0.0F, Math.min(floatValue9, mc.getWindow().getGuiScaledHeight() - this.m_9_float())) : floatValue9;
   }

   private void m_8_void() {
      this.floatValue = this.m_2_float(this.floatValue);
      this.floatValue2 = this.m_0_float(this.floatValue2);
   }
}
