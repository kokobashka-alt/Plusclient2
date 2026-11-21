package wtf.onetap.gui.hud;

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.gui.ColorPicker;
import wtf.onetap.gui.widget.TextField;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.Theme;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.ModeSetting;

public class HudStylePanel {
   private static final String[] TEXT_ARRAY = new String[]{"Свой", "От темы"};
   private static final Theme.ModeKind[] C8_ARRAY = new Theme.ModeKind[]{Theme.ModeKind.CUSTOM, Theme.ModeKind.THEME};
   private static final String[] TEXT_ARRAY2 = new String[]{"От хп", "От темы", "Свой"};
   private static final Theme.ModeKind[] C8_ARRAY2 = new Theme.ModeKind[]{Theme.ModeKind.C8, Theme.ModeKind.THEME, Theme.ModeKind.CUSTOM};
   private static final int VALUE = ColorUtil.rgba(225, 40, 40, 255);
   private static final int VALUE2 = ColorUtil.rgba(100, 244, 66, 255);
   private static Theme.DataRecord c6;
   private final TextField textField;
   private final Theme theme;
   private final String text;
   private final String[] textArray;
   private final int[] valueArray;
   private final int value;
   private final int value2;
   private final ModeSetting modeSetting;
   private final String[] textArray2;
   private final BooleanSetting booleanSetting;
   private final String text2;
   private final BooleanSupplier booleanSupplier;
   private final Animation animation;
   private final Animation animation2;
   private final Animation animation3;
   private final Animation[] animationArray;
   private final Animation[] animationArray2;
   private final Animation[] animationArray3;
   private final Animation animation4;
   private final Animation animation5;
   private final Animation animation6;
   private final Animation animation7;
   private final Animation animation8;
   private final float[] floatValueArray;
   private final int[] valueArray2;
   private final String[] textArray3;
   private boolean flag;
   private int value3;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private HudStylePanel.ModeKind c5;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private long longValue;
   private long longValue2;
   private long longValue3;
   private boolean flag2;
   private Theme.ModeKind c8;

   public HudStylePanel(TextField textField2, Theme theme2, String text3) {
      this(textField2, theme2, text3, new String[]{"Шапка", "Тело", "Текст", "Акцент"}, new int[]{0, 1, 2, 3}, null, null, null, null, null);
   }

   public static HudStylePanel m_7(TextField textField2, Theme theme2, String text3) {
      return new HudStylePanel(textField2, theme2, text3, new String[]{"Фон", "Текст", "Акцент"}, new int[]{0, 2, 3}, null, null, null, null, null);
   }

   public static HudStylePanel m_8(TextField textField2, Theme theme2, String text3, ModeSetting modeSetting2, BooleanSetting booleanSetting2, BooleanSupplier booleanSupplier2) {
      return new HudStylePanel(
         textField2, theme2, text3, new String[]{"Фон", "Текст", "Полоса"}, new int[]{0, 2, 3}, modeSetting2, new String[]{"Полоса", "Кружок"}, booleanSetting2, "Частицы урона", booleanSupplier2
      );
   }

   private HudStylePanel(TextField textField2, Theme theme2, String text3, String[] stringArray, int[] intArray, ModeSetting modeSetting2, String[] stringArray2, BooleanSetting booleanSetting2, String text4, BooleanSupplier booleanSupplier2) {
      this.animation = new Animation(280L, 0.0F, Easing.EASING8);
      this.animation2 = new Animation(250L, 0.0F, Easing.EASING8);
      this.animation3 = new Animation(200L, 0.0F, Easing.EASING8);
      this.animationArray2 = new Animation[]{new Animation(250L, 0.0F, Easing.EASING8), new Animation(250L, 0.0F, Easing.EASING8), new Animation(250L, 0.0F, Easing.EASING8)};
      this.animationArray3 = new Animation[]{new Animation(250L, 0.0F, Easing.EASING8), new Animation(250L, 0.0F, Easing.EASING8)};
      this.animation4 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation5 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation6 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation7 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation8 = new Animation(120L, 0.0F, Easing.EASING18);
      this.value3 = -1;
      this.c5 = HudStylePanel.ModeKind.NONE;
      this.floatValue7 = 1.0F;
      this.c8 = Theme.ModeKind.THEME;
      this.textField = textField2;
      this.theme = theme2;
      this.text = text3;
      this.textArray = stringArray;
      this.valueArray = intArray;
      this.value2 = stringArray.length;
      int value4 = 0;

      for (int i = 0; i < this.value2; i++) {
         if (intArray[i] == 3) {
            value4 = i;
         }
      }

      this.value = value4;
      this.modeSetting = modeSetting2;
      this.textArray2 = stringArray2;
      this.booleanSetting = booleanSetting2;
      this.text2 = text4;
      this.booleanSupplier = booleanSupplier2;
      this.animationArray = new Animation[this.value2];

      for (int i2 = 0; i2 < this.value2; i2++) {
         this.animationArray[i2] = new Animation(200L, 0.0F, Easing.EASING8);
      }

      this.floatValueArray = new float[this.value2];
      this.valueArray2 = new int[this.value2];
      this.textArray3 = new String[this.value2];
      Arrays.fill(this.valueArray2, -1);
   }

   public boolean m_2_boolean() {
      return this.flag || this.animation.getValue() > 0.01F;
   }

   public void toggle() {
      this.flag = !this.flag;
      if (!this.flag) {
         this.c5 = HudStylePanel.ModeKind.NONE;
      }
   }

   public void m_7_void() {
      this.flag = false;
      this.c5 = HudStylePanel.ModeKind.NONE;
   }

   public int getActiveColor() {
      return this.flag && this.value3 >= 0 ? this.valueArray[this.value3] : -1;
   }

   public void m_5(float floatValue10, float floatValue11, float floatValue12, float floatValue13, float floatValue14, float floatValue15) {
      int value4 = this.getActiveColor();
      if (value4 >= 0 && value4 <= 1) {
         int value5 = ColorUtil.rgba(255, 255, 255, 230);
         if (value4 == 0) {
            Theme.m_5(floatValue10 + 0.5F, floatValue11 + 0.5F, floatValue12 - 1.0F, floatValue13 - 1.0F, value5);
         } else if (floatValue15 > 0.01F) {
            Theme.m_5(floatValue10 + 0.5F, floatValue14 + 0.5F, floatValue12 - 1.0F, floatValue15 - 1.0F, value5);
         }
      }
   }

   public void m_0_void() {
      Minecraft mc = Minecraft.getInstance();
      if (!(mc.gui.screen() instanceof ChatScreen)) {
         this.m_7_void();
      }

      float floatValue10 = this.animation.update(this.flag ? 1.0F : 0.0F);
      if (!(floatValue10 < 0.01F)) {
         Theme.ModeKind c82 = this.theme.m_6_C8();
         if (c82 != Theme.ModeKind.CUSTOM) {
            this.c8 = c82;
         }

         float floatValue11 = this.animation2.update(c82 == Theme.ModeKind.CUSTOM ? 0.0F : 1.0F);

         for (int i = 0; i < this.value2; i++) {
            this.floatValueArray[i] = this.animationArray[i].update(i == this.value3 ? 1.0F : 0.0F);
         }

         this.floatValue4 = this.textField.m_5_float() + this.textField.m_6_float() + 6.0F;
         this.floatValue5 = this.textField.m_8_float();
         this.floatValue6 = this.m_1_float(floatValue11);
         if (this.floatValue4 + 136.0F > mc.getWindow().getGuiScaledWidth()) {
            this.floatValue4 = this.textField.m_5_float() - 136.0F - 6.0F;
         }

         this.floatValue5 = Math.max(2.0F, Math.min(this.floatValue5, mc.getWindow().getGuiScaledHeight() - this.floatValue6 - 2.0F));
         this.floatValue7 = 0.92F + 0.08F * floatValue10;
         this.floatValue8 = this.floatValue4 + 68.0F;
         this.floatValue9 = this.floatValue5 + this.floatValue6 / 2.0F;
         float floatValue12 = Render2D.getScale();
         float floatValue13 = Render2D.getOffsetX();
         float floatValue14 = Render2D.getOffsetY();
         Render2D.setTransform(floatValue12 * this.floatValue7, floatValue13 + floatValue12 * (this.floatValue8 - this.floatValue8 * this.floatValue7), floatValue14 + floatValue12 * (this.floatValue9 - this.floatValue9 * this.floatValue7));

         try {
            this.m_4_void();
            int value4 = (int)(255.0F * floatValue10);
            Draw2D.m_1(this.floatValue4 + 2.0F, this.floatValue5 + 1.0F, 132.0F, this.floatValue6 - 2.0F, 4.0F, 4.0F, ColorUtil.rgba(0, 0, 0, (int)(125.0F * floatValue10)));
            Draw2D.m_7(this.floatValue4, this.floatValue5, 136.0F, this.floatValue6, 4.0F, 3.0F * floatValue10, ColorUtil.rgba(255, 255, 255, 255));
            Draw2D.roundedRect(this.floatValue4, this.floatValue5, 136.0F, this.floatValue6, 4.0F, ColorUtil.rgba(14, 14, 20, (int)(128.0F * floatValue10)));
            if (this.modeSetting != null) {
               this.m_4_void(value4);
               Draw2D.rect(this.floatValue4 + 4.0F + 3.0F, this.floatValue5 + 4.0F + 25.0F + 3.0F, 122.0F, 1.0F, ColorUtil.rgba(255, 255, 255, (int)(20.0F * floatValue10)));
            }

            for (int i2 = 0; i2 < this.value2; i2++) {
               this.m_8_void(i2, floatValue11, value4);
            }

            Draw2D.rect(this.floatValue4 + 4.0F + 3.0F, this.m_8_float(floatValue11) + 3.0F, 122.0F, 1.0F, ColorUtil.rgba(255, 255, 255, (int)(20.0F * floatValue10)));
            if (this.isVisible()) {
               this.m_4(this.m_7_float(floatValue11), value4);
            }

            this.m_3_void(this.m_2_float(floatValue11), value4);
            this.m_9(this.m_5_float(floatValue11), value4);
            this.m_0(this.m_9(floatValue11), value4);
         } finally {
            Render2D.setTransform(floatValue12, floatValue13, floatValue14);
         }
      }
   }

   public boolean m_5_boolean(float floatValue10, float floatValue11) {
      if (!this.m_2_boolean()) {
         return false;
      }

      float floatValue12 = this.floatValue8 + (floatValue10 - this.floatValue8) / this.floatValue7;
      float floatValue13 = this.floatValue9 + (floatValue11 - this.floatValue9) / this.floatValue7;
      if (!(floatValue12 < this.floatValue4) && !(floatValue12 > this.floatValue4 + 136.0F) && !(floatValue13 < this.floatValue5) && !(floatValue13 > this.floatValue5 + this.floatValue6)) {
         float floatValue14 = this.animation2.getValue();
         float floatValue15 = this.floatValue4 + 4.0F + 3.0F;
         float floatValue16 = 122.0F;
         if (this.modeSetting != null) {
            float floatValue17 = this.floatValue5 + 4.0F + 12.0F;
            float floatValue18 = (floatValue16 - 3.0F) / 2.0F;

            for (int i = 0; i < 2; i++) {
               if (isHovered(floatValue12, floatValue13, floatValue15 + i * (floatValue18 + 3.0F), floatValue17, floatValue18, 13.0F)) {
                  this.modeSetting.setValue(this.textArray2[i]);
                  return true;
               }
            }
         }

         for (int i2 = 0; i2 < this.value2; i2++) {
            float floatValue19 = this.m_8_float(i2, floatValue14);
            if (isHovered(floatValue12, floatValue13, this.floatValue4 + 4.0F, floatValue19, 128.0F, 15.0F)) {
               this.m_0_void(i2 == this.value3 ? -1 : i2);
               return true;
            }

            if (i2 == this.value3 && isHovered(floatValue12, floatValue13, this.floatValue4 + 4.0F, floatValue19 + 15.0F, 128.0F, this.m_0(i2, floatValue14) * this.floatValueArray[i2])) {
               float floatValue20 = floatValue19 + 15.0F + 1.0F;
               if (i2 == this.value) {
                  String[] stringArray = this.getLabels();
                  int value4 = stringArray.length;
                  float floatValue21 = (floatValue16 - 3 * (value4 - 1)) / value4;

                  for (int i3 = 0; i3 < value4; i3++) {
                     if (isHovered(floatValue12, floatValue13, floatValue15 + i3 * (floatValue21 + 3.0F), floatValue20, floatValue21, 13.0F)) {
                        this.theme.m_4(this.getModes()[i3]);
                        return true;
                     }
                  }

                  floatValue20 += 16.0F;
                  if (this.theme.m_6_C8() != Theme.ModeKind.CUSTOM) {
                     return true;
                  }
               }

               float floatValue22 = floatValue20 + 5.0F + 3.0F;
               float floatValue23 = floatValue22 + 36.0F + 3.0F;
               float floatValue24 = floatValue23 + 5.0F + 3.0F;
               if (isHovered(floatValue12, floatValue13, floatValue15, floatValue20 - 2.0F, floatValue16, 9.0F)) {
                  this.c5 = HudStylePanel.ModeKind.HUE;
               } else if (isHovered(floatValue12, floatValue13, floatValue15, floatValue22, floatValue16, 36.0F)) {
                  this.c5 = HudStylePanel.ModeKind.C5;
               } else {
                  if (!isHovered(floatValue12, floatValue13, floatValue15, floatValue23 - 2.0F, floatValue16, 9.0F)) {
                     float floatValue25 = floatValue15 + floatValue16 - 13.0F;
                     float floatValue26 = floatValue25 - 13.0F - 3.0F;
                     Minecraft mc = Minecraft.getInstance();
                     if (isHovered(floatValue12, floatValue13, floatValue26, floatValue24, 13.0F, 13.0F)) {
                        mc.keyboardHandler.setClipboard(this.m_3_String(i2));
                        this.m_6_void(false);
                     } else if (isHovered(floatValue12, floatValue13, floatValue25, floatValue24, 13.0F, 13.0F) && this.parseHex(i2, mc.keyboardHandler.getClipboard())) {
                        this.m_6_void(true);
                     }

                     return true;
                  }

                  this.c5 = HudStylePanel.ModeKind.ALPHA;
               }

               this.m_4_void();
               return true;
            }
         }

         if (this.isVisible() && isHovered(floatValue12, floatValue13, this.floatValue4 + 4.0F, this.m_7_float(floatValue14), 128.0F, 13.0F)) {
            this.booleanSetting.setValue(!(Boolean)this.booleanSetting.getValue());
            return true;
         }

         float floatValue27 = this.m_2_float(floatValue14);
         if (isHovered(floatValue12, floatValue13, floatValue15, floatValue27 - 2.0F, floatValue16, 17.0F)) {
            this.c5 = HudStylePanel.ModeKind.BLUR;
            this.m_4_void();
            return true;
         }

         float floatValue28 = this.m_5_float(floatValue14) + 3.0F + 11.0F + 3.0F;
         float floatValue29 = m_1_float();
         if (isHovered(floatValue12, floatValue13, this.floatValue4 + 4.0F + 3.0F, floatValue28, floatValue29, 13.0F)) {
            c6 = this.theme.m_9_C6(this.text);
            this.m_1(false);
            return true;
         }

         if (isHovered(floatValue12, floatValue13, this.floatValue4 + 4.0F + 3.0F + floatValue29 + 3.0F, floatValue28, floatValue29, 13.0F)) {
            if (c6 != null) {
               this.theme.m_9(c6);
               if (this.value3 >= 0) {
                  this.m_9_void(this.value3);
               }

               this.m_1(true);
            }

            return true;
         } else {
            if (isHovered(floatValue12, floatValue13, this.floatValue4 + 4.0F, this.m_9(floatValue14), 128.0F, 13.0F)) {
               this.theme.m_6_void();
               if (this.value3 >= 0) {
                  this.m_9_void(this.value3);
               }
            }

            return true;
         }
      } else {
         if (!this.textField.m_2_boolean(floatValue10, floatValue11)) {
            this.m_7_void();
         }

         return false;
      }
   }

   public void m_1_void() {
      this.c5 = HudStylePanel.ModeKind.NONE;
   }

   private void m_4_void(int value4) {
      Supplier supplier = Fonts.SUPPLIER18;
      float floatValue10 = this.floatValue4 + 4.0F + 3.0F;
      float floatValue11 = 122.0F;
      float floatValue12 = this.floatValue5 + 4.0F;
      Draw2D.drawStyledText("Форма", supplier, floatValue10, Draw2D.getTextBaseline(supplier, 8.5F, floatValue12, 10.0F), ColorUtil.rgba(222, 222, 222, (int)(value4 * 0.78F)), 8.5F);
      float floatValue13 = (floatValue11 - 3.0F) / 2.0F;

      for (int i = 0; i < 2; i++) {
         float floatValue14 = this.animationArray3[i].update(((String)this.modeSetting.getValue()).equals(this.textArray2[i]) ? 1.0F : 0.0F);
         this.m_0(this.textArray2[i], floatValue10 + i * (floatValue13 + 3.0F), floatValue12 + 12.0F, floatValue13, floatValue14, value4);
      }
   }

   private void m_8_void(int value4, float floatValue10, int value5) {
      float floatValue11 = value5 / 255.0F;
      float floatValue12 = this.m_8_float(value4, floatValue10);
      float floatValue13 = this.floatValueArray[value4];
      float floatValue14 = this.m_0(value4, floatValue10) * floatValue13;
      float floatValue15 = this.floatValue4 + 4.0F + 3.0F;
      float floatValue16 = 122.0F;
      boolean flag3 = value4 == this.value;
      Theme.ModeKind c82 = this.theme.m_6_C8();
      boolean flag4 = flag3 && c82 != Theme.ModeKind.CUSTOM;
      ColorSetting colorSetting = this.getColorSetting(value4);
      if (floatValue13 > 0.01F) {
         Draw2D.roundedRect(this.floatValue4 + 4.0F, floatValue12, 128.0F, 15.0F + floatValue14, 3.0F, ColorUtil.rgba(255, 255, 255, (int)(18.0F * floatValue11 * floatValue13)));
      }

      float floatValue17 = floatValue12 + 3.0F;
      if (flag4) {
         this.m_6(c82, floatValue15, floatValue17, 9.0F, 9.0F, 2.5F, value5);
      } else {
         Draw2D.roundedRect(floatValue15, floatValue17, 9.0F, 9.0F, 2.5F, ColorUtil.withAlpha((Integer)colorSetting.getValue(), value5));
      }

      Draw2D.m_4(floatValue15, floatValue17, 9.0F, 9.0F, 0.5F, 2.5F, ColorUtil.rgba(255, 255, 255, (int)(75.0F * floatValue11)));
      Supplier supplier = Fonts.SUPPLIER18;
      Draw2D.drawStyledText(this.textArray[value4], supplier, floatValue15 + 13.0F, Draw2D.getTextBaseline(supplier, 8.5F, floatValue12, 15.0F), ColorUtil.rgba(234, 234, 234, value5), 8.5F);
      String text3 = flag4 ? (c82 == Theme.ModeKind.C8 ? "От хп" : "От темы") : this.m_3_String(value4) + getAlphaSuffix(colorSetting);
      float floatValue18 = 6.0F;
      float floatValue19 = this.floatValue4 + 136.0F - 4.0F - 3.0F - floatValue18;
      this.m_8("\uf054", floatValue19, floatValue12, floatValue18, (int)(180.0F * floatValue11 * (1.0F - floatValue13)));
      this.m_8("\uf078", floatValue19, floatValue12, floatValue18, (int)(180.0F * floatValue11 * floatValue13));
      float floatValue20 = Draw2D.getTextWidth(text3, supplier, 8.0F);
      float floatValue21 = floatValue15 + 13.0F + Draw2D.getTextWidth(this.textArray[value4], supplier, 8.5F);
      if (floatValue19 - 4.0F - floatValue20 < floatValue21 + 6.0F && !flag4) {
         text3 = this.m_3_String(value4);
         floatValue20 = Draw2D.getTextWidth(text3, supplier, 8.0F);
      }

      Draw2D.drawStyledText(text3, supplier, floatValue19 - 4.0F - floatValue20, Draw2D.getTextBaseline(supplier, 8.0F, floatValue12, 15.0F), ColorUtil.rgba(222, 222, 222, (int)(140.0F * floatValue11)), 8.0F);
      if (!(floatValue13 < 0.01F)) {
         float floatValue22 = floatValue12 + 15.0F;
         Render2D.pushScissor(this.floatValue4 + 4.0F, floatValue22, 128.0F, floatValue14);
         int value6 = (int)(value5 * floatValue13);
         float floatValue23 = floatValue22 + 1.0F;
         if (flag3) {
            String[] stringArray = this.getLabels();
            Theme.ModeKind[] c8Array = this.getModes();
            int value7 = stringArray.length;
            float floatValue24 = (floatValue16 - 3 * (value7 - 1)) / value7;

            for (int i = 0; i < value7; i++) {
               float floatValue25 = this.animationArray2[i].update(c8Array[i] == c82 ? 1.0F : 0.0F);
               this.m_0(stringArray[i], floatValue15 + i * (floatValue24 + 3.0F), floatValue23, floatValue24, floatValue25, value6);
            }

            floatValue23 += 16.0F;
            if (floatValue10 > 0.01F) {
               this.m_8(floatValue15, floatValue23, floatValue16, (int)(value6 * floatValue10));
            }
         }

         if (!flag3 || floatValue10 < 0.99F) {
            int value8 = flag3 ? (int)(value6 * (1.0F - floatValue10)) : value6;
            this.m_4(value4, floatValue15, floatValue23, floatValue16, value8);
         }

         Render2D.popScissor();
      }
   }

   private void m_6(Theme.ModeKind c82, float floatValue10, float floatValue11, float floatValue12, float floatValue13, float floatValue14, int value4) {
      if (c82 == Theme.ModeKind.C8) {
         Draw2D.m_0(floatValue10, floatValue11, floatValue12, floatValue13, floatValue14, 0.0F, ColorUtil.withAlpha(VALUE, value4), ColorUtil.withAlpha(VALUE2, value4));
      } else {
         Draw2D.m_0(floatValue10, floatValue11, floatValue12, floatValue13, floatValue14, 0.0F, ColorUtil.withAlpha(ColorUtil.primaryColor(), value4), ColorUtil.withAlpha(ColorUtil.secondaryColor(), value4));
      }
   }

   private void m_4(int value4, float floatValue10, float floatValue11, float floatValue12, int value5) {
      if (value5 > 0) {
         ColorSetting colorSetting = this.getColorSetting(value4);
         int value6 = (Integer)colorSetting.getValue();
         float[] floatArray = value4 == this.value3 ? new float[]{this.floatValue, this.floatValue2, this.floatValue3} : ColorUtil.m_2(colorSetting.getRed(), colorSetting.getGreen(), colorSetting.getBlue());
         float floatValue13 = floatArray[0];
         float floatValue14 = floatArray[1];
         float floatValue15 = floatArray[2];
         Draw2D.m_3(floatValue10, floatValue11, floatValue12, 5.0F, 2.0F, ColorUtil.rgba(255, 255, 255, value5));
         this.m_6(clamp(floatValue10 + floatValue12 * floatValue13, floatValue10 + 2.0F, floatValue10 + floatValue12 - 2.0F), floatValue11, 5.0F, value5);
         float floatValue16 = floatValue11 + 5.0F + 3.0F;
         ColorPicker.m_4(floatValue10, floatValue16, floatValue12, 36.0F, floatValue13, value5);
         float floatValue17 = clamp(floatValue10 + floatValue12 * floatValue14, floatValue10 + 3.5F, floatValue10 + floatValue12 - 3.5F);
         float floatValue18 = clamp(floatValue16 + 36.0F * (1.0F - floatValue15), floatValue16 + 3.5F, floatValue16 + 36.0F - 3.5F);
         Draw2D.roundedRect(floatValue17 - 3.5F, floatValue18 - 3.5F, 7.0F, 7.0F, 3.5F, ColorUtil.withAlpha(ColorUtil.withAlpha(value6, 255), value5));
         Draw2D.m_4(floatValue17 - 3.5F, floatValue18 - 3.5F, 7.0F, 7.0F, 1.0F, 3.5F, ColorUtil.rgba(255, 255, 255, value5));
         float floatValue19 = floatValue16 + 36.0F + 3.0F;
         Draw2D.roundedRect(floatValue10, floatValue19, floatValue12, 5.0F, 2.0F, ColorUtil.rgba(70, 70, 74, value5));
         Draw2D.m_0(floatValue10, floatValue19, floatValue12, 5.0F, 2.0F, 0.0F, ColorUtil.withAlpha(value6, 0), ColorUtil.withAlpha(value6, value5));
         this.m_6(clamp(floatValue10 + floatValue12 * (colorSetting.getAlpha() / 255.0F), floatValue10 + 2.0F, floatValue10 + floatValue12 - 2.0F), floatValue19, 5.0F, value5);
         float floatValue20 = floatValue19 + 5.0F + 3.0F;
         Supplier supplier = Fonts.SUPPLIER18;
         Draw2D.drawStyledText(this.m_3_String(value4), supplier, floatValue10, Draw2D.getTextBaseline(supplier, 8.5F, floatValue20, 13.0F), ColorUtil.rgba(222, 222, 222, (int)(value5 * 0.85F)), 8.5F);
         float floatValue21 = floatValue10 + floatValue12 - 13.0F;
         float floatValue22 = floatValue21 - 13.0F - 3.0F;
         float[] floatArray2 = m_0_FArr();
         float floatValue23 = this.animation6.update(isHovered(floatArray2[0], floatArray2[1], floatValue22, floatValue20, 13.0F, 13.0F) ? 1.0F : 0.0F);
         float floatValue24 = this.animation7.update(isHovered(floatArray2[0], floatArray2[1], floatValue21, floatValue20, 13.0F, 13.0F) ? 1.0F : 0.0F);
         this.m_3(floatValue22, floatValue20, "\uf0c5", value5, floatValue23);
         this.m_3(floatValue21, floatValue20, "\uf0ea", value5, floatValue24);
         long longValue4 = System.currentTimeMillis();
         if (value4 == this.value3 && longValue4 < this.longValue2) {
            float floatValue25 = this.m_7(longValue4);
            float floatValue26 = this.animation8.get();
            float floatValue27 = (1.0F - floatValue25) * 12.0F;
            float floatValue28 = floatValue16 + 18.0F - 5.0F;
            if (floatValue26 < 0.99F) {
               this.m_0("Цвет скопирован!", floatValue10, floatValue12, floatValue28 - floatValue26 * 3.0F, floatValue27, value5 * floatValue25 * (1.0F - floatValue26));
            }

            if (floatValue26 > 0.01F) {
               this.m_0("Цвет вставлен!", floatValue10, floatValue12, floatValue28 + (1.0F - floatValue26) * 3.0F, floatValue27, value5 * floatValue25 * floatValue26);
            }
         }
      }
   }

   private void m_6_void(boolean flag3) {
      long longValue4 = System.currentTimeMillis();
      if (longValue4 >= this.longValue2) {
         this.longValue = longValue4;
         this.animation8.setDuration(flag3 ? 1.0F : 0.0F);
      } else {
         float floatValue10 = this.m_7(longValue4);
         this.longValue = longValue4 - (long)(200.0 * (1.0 - Math.cbrt(1.0F - Math.max(0.0F, Math.min(1.0F, floatValue10)))));
         this.animation8.setTarget(flag3 ? 1.0F : 0.0F);
      }

      this.longValue2 = longValue4 + 1200L;
   }

   private float m_7(long longValue4) {
      float floatValue10 = (float)(longValue4 - this.longValue);
      float floatValue11 = (float)(this.longValue2 - longValue4);
      return floatValue10 < 200.0F ? Easing.EASING8.ease(floatValue10, 0.0F, 1.0F, 200.0F) : (floatValue11 < 200.0F ? Easing.EASING7.ease(floatValue11, 0.0F, 1.0F, 200.0F) : 1.0F);
   }

   private void m_0(String text3, float floatValue10, float floatValue11, float floatValue12, float floatValue13, float floatValue14) {
      Supplier supplier = Fonts.SUPPLIER18;
      float floatValue15 = 9.0F;
      float floatValue16 = Draw2D.getTextWidth(text3, supplier, floatValue15);
      Draw2D.drawTextWithShadow(text3, supplier, floatValue10 + (floatValue11 - floatValue16) / 2.0F + floatValue13, floatValue12, ColorUtil.rgba(255, 255, 255, (int)floatValue14), floatValue15);
   }

   private void m_8(String text3, float floatValue10, float floatValue11, float floatValue12, int value4) {
      if (value4 > 0) {
         Draw2D.drawStyledText(text3, Fonts.SUPPLIER8, floatValue10, Draw2D.getCenteredY(text3, Fonts.SUPPLIER8, floatValue12, floatValue11, 15.0F), ColorUtil.rgba(222, 222, 222, value4), floatValue12);
      }
   }

   private void m_6(float floatValue10, float floatValue11, float floatValue12, int value4) {
      Draw2D.roundedRect(floatValue10 - 2.0F, floatValue11 - 1.5F, 4.0F, floatValue12 + 3.0F, 1.5F, ColorUtil.rgba(255, 255, 255, value4));
      Draw2D.m_4(floatValue10 - 2.0F, floatValue11 - 1.5F, 4.0F, floatValue12 + 3.0F, 0.5F, 1.5F, ColorUtil.rgba(0, 0, 0, value4));
   }

   private void m_3(float floatValue10, float floatValue11, String text3, int value4, float floatValue12) {
      Draw2D.roundedRect(floatValue10, floatValue11, 13.0F, 13.0F, 3.0F, ColorUtil.rgba(255, 255, 255, (int)((20.0F + 30.0F * floatValue12) * value4 / 255.0F)));
      float floatValue13 = 8.0F + floatValue12;
      Draw2D.drawStyledText(
         text3,
         Fonts.SUPPLIER8,
         Draw2D.getCenteredX(text3, Fonts.SUPPLIER8, floatValue13, floatValue10, 13.0F),
         Draw2D.getCenteredY(text3, Fonts.SUPPLIER8, floatValue13, floatValue11, 13.0F),
         ColorUtil.rgba(222, 222, 222, (int)((170.0F + 85.0F * floatValue12) * value4 / 255.0F)),
         floatValue13
      );
   }

   private static float[] m_0_FArr() {
      Minecraft mc = Minecraft.getInstance();
      double doubleValue = mc.getWindow().getGuiScale();
      return new float[]{
         (float)((mc.mouseHandler.xpos() / doubleValue - Render2D.getOffsetX()) / Render2D.getScale()),
         (float)((mc.mouseHandler.ypos() / doubleValue - Render2D.getOffsetY()) / Render2D.getScale())
      };
   }

   private void m_0(String text3, float floatValue10, float floatValue11, float floatValue12, float floatValue13, int value4) {
      int value5 = ColorUtil.lerpColor(ColorUtil.rgba(255, 255, 255, 20), ColorUtil.primaryColor(), floatValue13);
      int value6 = ColorUtil.lerpColor(ColorUtil.rgba(255, 255, 255, 20), ColorUtil.secondaryColor(), floatValue13);
      Draw2D.m_0(floatValue10, floatValue11, floatValue12, 13.0F, 3.0F, 0.0F, Theme.scaleAlpha(value5, value4), Theme.scaleAlpha(value6, value4));
      Supplier supplier = Fonts.SUPPLIER18;
      int value7 = ColorUtil.lerpColor(ColorUtil.rgba(222, 222, 222, 190), ColorUtil.rgba(245, 245, 245, 255), floatValue13);
      Draw2D.drawTextWithShadow(text3, supplier, floatValue10 + (floatValue12 - Draw2D.getTextWidth(text3, supplier, 8.0F)) / 2.0F, Draw2D.getTextBaseline(supplier, 8.0F, floatValue11, 13.0F), Theme.scaleAlpha(value7, value4), 8.0F);
   }

   private void m_8(float floatValue10, float floatValue11, float floatValue12, int value4) {
      if (value4 > 0) {
         Draw2D.roundedRect(floatValue10, floatValue11, floatValue12, 20.0F, 3.0F, ColorUtil.rgba(255, 255, 255, (int)(13 * value4 / 255.0F)));
         float floatValue13 = floatValue11 + 6.0F;
         boolean flag3 = this.c8 == Theme.ModeKind.C8;
         this.m_6(this.c8, floatValue10 + 3.0F, floatValue13, 22.0F, 8.0F, 2.0F, value4);
         Supplier supplier = Fonts.SUPPLIER18;
         String text3 = flag3 ? "Здоровье" : Onetap.THEME_MANAGER.m_0_C54().m_4_l();
         Draw2D.drawStyledText(text3, supplier, floatValue10 + 29.0F, Draw2D.getTextBaseline(supplier, 8.0F, floatValue11, 20.0F), ColorUtil.rgba(222, 222, 222, (int)(value4 * 0.85F)), 8.0F);
         String text4 = flag3 ? "по HP цели" : "меняется с темой";
         float floatValue14 = Draw2D.getTextWidth(text4, supplier, 7.5F);
         float floatValue15 = floatValue10 + 29.0F + Draw2D.getTextWidth(text3, supplier, 8.0F);
         if (floatValue10 + floatValue12 - 3.0F - floatValue14 > floatValue15 + 6.0F) {
            Draw2D.drawStyledText(text4, supplier, floatValue10 + floatValue12 - 3.0F - floatValue14, Draw2D.getTextBaseline(supplier, 7.5F, floatValue11, 20.0F), ColorUtil.rgba(222, 222, 222, (int)(value4 * 0.5F)), 7.5F);
         }
      }
   }

   private void m_4(float floatValue10, int value4) {
      Supplier supplier = Fonts.SUPPLIER18;
      float floatValue11 = this.floatValue4 + 4.0F + 3.0F;
      float floatValue12 = 122.0F;
      Draw2D.drawStyledText(this.text2, supplier, floatValue11, Draw2D.getTextBaseline(supplier, 8.5F, floatValue10, 13.0F), ColorUtil.rgba(222, 222, 222, (int)(value4 * 0.85F)), 8.5F);
      float floatValue13 = this.animation3.update(this.booleanSetting.getValue() ? 1.0F : 0.0F);
      float floatValue14 = 18.0F;
      float floatValue15 = 9.0F;
      float floatValue16 = floatValue11 + floatValue12 - floatValue14;
      float floatValue17 = floatValue10 + (13.0F - floatValue15) / 2.0F;
      int value5 = ColorUtil.rgba(255, 255, 255, 41);
      Draw2D.m_0(
         floatValue16, floatValue17, floatValue14, floatValue15, 4.5F, 0.0F, Theme.scaleAlpha(ColorUtil.lerpColor(value5, ColorUtil.primaryColor(), floatValue13), value4), Theme.scaleAlpha(ColorUtil.lerpColor(value5, ColorUtil.secondaryColor(), floatValue13), value4)
      );
      float floatValue18 = floatValue16 + 1.0F + (floatValue14 - floatValue15) * floatValue13;
      Draw2D.roundedRect(floatValue18, floatValue17 + 1.0F, floatValue15 - 2.0F, floatValue15 - 2.0F, (floatValue15 - 2.0F) / 2.0F, ColorUtil.rgba(255, 255, 255, value4));
   }

   private void m_3_void(float floatValue10, int value4) {
      Supplier supplier = Fonts.SUPPLIER18;
      float floatValue11 = this.floatValue4 + 4.0F + 3.0F;
      float floatValue12 = 122.0F;
      String text3 = "Блюр";
      String text4 = String.valueOf(Math.round((Float)this.theme.getFloatSetting().getValue()));
      float floatValue13 = Draw2D.getTextWidth(text3, supplier, 8.5F);
      float floatValue14 = Draw2D.getTextWidth(text4, supplier, 8.0F);
      Draw2D.drawStyledText(text3, supplier, floatValue11, Draw2D.getTextBaseline(supplier, 8.5F, floatValue10, 13.0F), ColorUtil.rgba(222, 222, 222, (int)(value4 * 0.78F)), 8.5F);
      Draw2D.drawStyledText(text4, supplier, floatValue11 + floatValue12 - floatValue14, Draw2D.getTextBaseline(supplier, 8.0F, floatValue10, 13.0F), ColorUtil.rgba(222, 222, 222, value4), 8.0F);
      float floatValue15 = floatValue11 + floatValue13 + 5.0F;
      float floatValue16 = floatValue11 + floatValue12 - floatValue14 - 5.0F - floatValue15;
      float floatValue17 = floatValue10 + 5.0F;
      float floatValue18 = (Float)this.theme.getFloatSetting().getValue() / this.theme.getFloatSetting().getMax();
      Draw2D.roundedRect(floatValue15, floatValue17, floatValue16, 3.0F, 1.5F, ColorUtil.rgba(255, 255, 255, (int)(36 * value4 / 255.0F)));
      if (floatValue18 > 0.01F) {
         Draw2D.m_0(floatValue15, floatValue17, floatValue16 * floatValue18, 3.0F, 1.5F, 0.0F, ColorUtil.withAlpha(ColorUtil.primaryColor(), value4), ColorUtil.withAlpha(ColorUtil.secondaryColor(), value4));
      }

      float floatValue19 = floatValue15 + floatValue16 * floatValue18;
      Draw2D.roundedRect(floatValue19 - 4.0F, floatValue10 + 2.5F, 8.0F, 8.0F, 4.0F, ColorUtil.rgba(255, 255, 255, value4));
   }

   private void m_0(float floatValue10, int value4) {
      Supplier supplier = Fonts.SUPPLIER18;
      float floatValue11 = this.floatValue4 + 4.0F;
      float floatValue12 = 128.0F;
      Draw2D.roundedRect(floatValue11, floatValue10, floatValue12, 13.0F, 3.0F, ColorUtil.rgba(255, 255, 255, (int)(15 * value4 / 255.0F)));
      String text3 = "Сбросить";
      float floatValue13 = 7.0F;
      float floatValue14 = Draw2D.getTextWidth(text3, supplier, 8.0F);
      float floatValue15 = floatValue13 + 3.0F + floatValue14;
      float floatValue16 = floatValue11 + (floatValue12 - floatValue15) / 2.0F;
      int value5 = ColorUtil.rgba(222, 222, 222, (int)(value4 * 0.7F));
      Draw2D.drawStyledText("\uf2ea", Fonts.SUPPLIER8, floatValue16, Draw2D.getCenteredY("\uf2ea", Fonts.SUPPLIER8, floatValue13, floatValue10, 13.0F), value5, floatValue13);
      Draw2D.drawStyledText(text3, supplier, floatValue16 + floatValue13 + 3.0F, Draw2D.getTextBaseline(supplier, 8.0F, floatValue10, 13.0F), value5, 8.0F);
   }

   private void m_4_void() {
      if (this.c5 != HudStylePanel.ModeKind.NONE) {
         float[] floatArray = m_0_FArr();
         float floatValue10 = floatArray[0];
         float floatValue11 = floatArray[1];
         float floatValue12 = this.floatValue4 + 4.0F + 3.0F;
         float floatValue13 = 122.0F;
         if (this.c5 == HudStylePanel.ModeKind.BLUR) {
            Supplier supplier = Fonts.SUPPLIER18;
            float floatValue14 = Draw2D.getTextWidth("Блюр", supplier, 8.5F);
            float floatValue15 = Draw2D.getTextWidth(String.valueOf(Math.round((Float)this.theme.getFloatSetting().getValue())), supplier, 8.0F);
            float floatValue16 = floatValue12 + floatValue14 + 5.0F;
            float floatValue17 = floatValue12 + floatValue13 - floatValue15 - 5.0F - floatValue16;
            this.theme.getFloatSetting().setValue((float)Math.round(clamp((floatValue10 - floatValue16) / floatValue17, 0.0F, 1.0F) * this.theme.getFloatSetting().getMax()));
         } else if (this.value3 < 0) {
            this.c5 = HudStylePanel.ModeKind.NONE;
         } else {
            float floatValue18 = this.m_8_float(this.value3, this.animation2.getValue()) + 15.0F + 1.0F;
            if (this.value3 == this.value) {
               floatValue18 += 16.0F;
            }

            float floatValue19 = floatValue18 + 5.0F + 3.0F;
            switch (this.c5.ordinal()) {
               case 1:
                  this.floatValue = clamp((floatValue10 - floatValue12) / floatValue13, 0.0F, 1.0F);
                  this.m_5_void();
                  break;
               case 2:
                  this.floatValue2 = clamp((floatValue10 - floatValue12) / floatValue13, 0.0F, 1.0F);
                  this.floatValue3 = clamp(1.0F - (floatValue11 - floatValue19) / 36.0F, 0.0F, 1.0F);
                  this.m_5_void();
                  break;
               case 3:
                  ColorSetting colorSetting = this.getColorSetting(this.value3);
                  int value4 = Math.round(clamp((floatValue10 - floatValue12) / floatValue13, 0.0F, 1.0F) * 255.0F);
                  colorSetting.setValue(ColorUtil.withAlpha((Integer)colorSetting.getValue(), value4));
            }
         }
      }
   }

   private void m_0_void(int value4) {
      this.value3 = value4;
      this.c5 = HudStylePanel.ModeKind.NONE;
      if (value4 >= 0) {
         this.m_9_void(value4);
      }
   }

   private void m_9_void(int value4) {
      ColorSetting colorSetting = this.getColorSetting(value4);
      float[] floatArray = ColorUtil.m_2(colorSetting.getRed(), colorSetting.getGreen(), colorSetting.getBlue());
      this.floatValue = floatArray[0];
      this.floatValue2 = floatArray[1];
      this.floatValue3 = floatArray[2];
   }

   private void m_5_void() {
      ColorSetting colorSetting = this.getColorSetting(this.value3);
      int value4 = ColorUtil.m_7_int(this.floatValue, this.floatValue2, this.floatValue3);
      int value5 = colorSetting.getAlpha() << 24 | value4 & 16777215;
      if ((Integer)colorSetting.getValue() != value5) {
         colorSetting.setValue(value5);
      }
   }

   private boolean parseHex(int value4, String text3) {
      String text4 = text3.trim();
      if (text4.startsWith("#")) {
         text4 = text4.substring(1);
      }

      if (text4.length() != 6) {
         return false;
      }

      try {
         int value5 = Integer.parseInt(text4, 16);
         ColorSetting colorSetting = this.getColorSetting(value4);
         colorSetting.setValue(colorSetting.getAlpha() << 24 | value5);
         this.m_9_void(value4);
         return true;
      } catch (NumberFormatException numberFormatException) {
         return false;
      }
   }

   private ColorSetting getColorSetting(int value4) {
      return switch (this.valueArray[value4]) {
         case 0 -> this.theme.getColorSetting();
         case 1 -> this.theme.getColorSetting2();
         case 2 -> this.theme.getColorSetting3();
         default -> this.theme.getColorSetting4();
      };
   }

   private String[] getLabels() {
      return this.theme.m_1_boolean() ? TEXT_ARRAY2 : TEXT_ARRAY;
   }

   private Theme.ModeKind[] getModes() {
      return this.theme.m_1_boolean() ? C8_ARRAY2 : C8_ARRAY;
   }

   private static String getAlphaSuffix(ColorSetting colorSetting) {
      return colorSetting.getAlpha() == 255 ? "" : " " + Math.round(colorSetting.getAlpha() / 2.55F) + "%";
   }

   private String m_3_String(int value4) {
      int value5 = (Integer)this.getColorSetting(value4).getValue() & 16777215;
      if (this.valueArray2[value4] != value5) {
         this.valueArray2[value4] = value5;
         this.textArray3[value4] = String.format("#%06X", value5);
      }

      return this.textArray3[value4];
   }

   private boolean isVisible() {
      return this.booleanSetting != null && this.booleanSupplier.getAsBoolean();
   }

   private float m_0(int value4, float floatValue10) {
      return value4 != this.value ? 73.0F : 85.0F + -48.0F * floatValue10 + 4.0F;
   }

   private float m_2_float() {
      return this.floatValue5 + 4.0F + (this.modeSetting != null ? 32.0F : 0.0F);
   }

   private float m_8_float(int value4, float floatValue10) {
      float floatValue11 = this.m_2_float();

      for (int i = 0; i < value4; i++) {
         floatValue11 += 15.0F + this.m_0(i, floatValue10) * this.floatValueArray[i] + 2.0F;
      }

      return floatValue11;
   }

   private float m_8_float(float floatValue10) {
      int value4 = this.value2 - 1;
      return this.m_8_float(value4, floatValue10) + 15.0F + this.m_0(value4, floatValue10) * this.floatValueArray[value4];
   }

   private float m_7_float(float floatValue10) {
      return this.m_8_float(floatValue10) + 3.0F + 1.0F + 3.0F;
   }

   private float m_2_float(float floatValue10) {
      return this.m_7_float(floatValue10) + (this.isVisible() ? 15.0F : 0.0F);
   }

   private float m_5_float(float floatValue10) {
      return this.m_2_float(floatValue10) + 13.0F + 3.0F;
   }

   private float m_9(float floatValue10) {
      return this.m_5_float(floatValue10) + 33.0F + 3.0F;
   }

   private float m_1_float(float floatValue10) {
      return this.m_9(floatValue10) + 13.0F + 4.0F - this.floatValue5;
   }

   private static float m_1_float() {
      return 59.5F;
   }

   private void m_1(boolean flag3) {
      this.flag2 = flag3;
      this.longValue3 = System.currentTimeMillis() + 1400L;
   }

   private void m_9(float floatValue10, int value4) {
      float floatValue11 = value4 / 255.0F;
      Supplier supplier = Fonts.SUPPLIER18;
      float floatValue12 = this.floatValue4 + 4.0F;
      float floatValue13 = 128.0F;
      Draw2D.roundedRect(floatValue12, floatValue10, floatValue13, 33.0F, 3.0F, ColorUtil.rgba(255, 255, 255, (int)(13.0F * floatValue11)));
      float floatValue14 = floatValue12 + 3.0F;
      float floatValue15 = floatValue13 - 6.0F;
      float floatValue16 = floatValue10 + 3.0F;
      Draw2D.drawStyledText("Стиль", supplier, floatValue14, Draw2D.getTextBaseline(supplier, 8.5F, floatValue16, 11.0F), ColorUtil.rgba(222, 222, 222, (int)(value4 * 0.78F)), 8.5F);
      float floatValue17 = 30.0F;
      float floatValue18 = floatValue14 + floatValue15 - floatValue17;

      for (int i = 0; c6 != null && i < 4; i++) {
         float floatValue19 = floatValue18 + i * 8;
         float floatValue20 = floatValue16 + 2.5F;
         if (i == 3 && (c6.m_3_x() || c6.m_3_e_boolean())) {
            this.m_6(c6.m_3_e_boolean() ? Theme.ModeKind.C8 : Theme.ModeKind.THEME, floatValue19, floatValue20, 6.0F, 6.0F, 2.0F, value4);
         } else {
            int value5 = switch (i) {
               case 0 -> c6.m_8_z();
               case 1 -> c6.m_8l_int();
               case 2 -> c6.m_8_d();
               default -> c6.m_8_u();
            };
            Draw2D.roundedRect(floatValue19, floatValue20, 6.0F, 6.0F, 2.0F, ColorUtil.withAlpha(value5, value4));
         }

         Draw2D.m_4(floatValue19, floatValue20, 6.0F, 6.0F, 0.5F, 2.0F, ColorUtil.rgba(255, 255, 255, (int)(75.0F * floatValue11)));
      }

      boolean flag3 = System.currentTimeMillis() < this.longValue3;
      String text3 = flag3 ? (this.flag2 ? "применено" : "скопировано") : (c6 == null ? "буфер пуст" : c6.m_6g_String());
      int value6 = flag3 ? Theme.scaleAlpha(this.theme.m_3_int(), value4) : ColorUtil.rgba(222, 222, 222, (int)(value4 * 0.5F));
      float floatValue21 = Draw2D.getTextWidth(text3, supplier, 7.5F);
      Draw2D.drawStyledText(text3, supplier, (c6 == null ? floatValue14 + floatValue15 : floatValue18 - 4.0F) - floatValue21, Draw2D.getTextBaseline(supplier, 7.5F, floatValue16, 11.0F), value6, 7.5F);
      float floatValue22 = floatValue16 + 11.0F + 3.0F;
      float floatValue23 = m_1_float();
      float[] floatArray = m_0_FArr();
      float floatValue24 = this.animation4.update(isHovered(floatArray[0], floatArray[1], floatValue14, floatValue22, floatValue23, 13.0F) ? 1.0F : 0.0F);
      float floatValue25 = this.animation5.update(c6 != null && isHovered(floatArray[0], floatArray[1], floatValue14 + floatValue23 + 3.0F, floatValue22, floatValue23, 13.0F) ? 1.0F : 0.0F);
      this.m_7("Копировать", "\uf0c5", floatValue14, floatValue22, floatValue23, value4, floatValue24, true);
      this.m_7("Вставить", "\uf0ea", floatValue14 + floatValue23 + 3.0F, floatValue22, floatValue23, value4, floatValue25, c6 != null);
   }

   private void m_7(String text3, String text4, float floatValue10, float floatValue11, float floatValue12, int value4, float floatValue13, boolean flag3) {
      Supplier supplier = Fonts.SUPPLIER18;
      float floatValue14 = flag3 ? 1.0F : 0.4F;
      Draw2D.roundedRect(floatValue10, floatValue11, floatValue12, 13.0F, 3.0F, ColorUtil.rgba(255, 255, 255, (int)((22.0F + 30.0F * floatValue13) * value4 / 255.0F * floatValue14)));
      float floatValue15 = 7.0F;
      float floatValue16 = 7.5F;
      float floatValue17 = Draw2D.getTextWidth(text3, supplier, floatValue16);
      float floatValue18 = floatValue15 + 3.0F + floatValue17;
      float floatValue19 = floatValue10 + (floatValue12 - floatValue18) / 2.0F;
      int value5 = ColorUtil.rgba(222, 222, 222, (int)((170.0F + 85.0F * floatValue13) * value4 / 255.0F * floatValue14));
      Draw2D.drawStyledText(text4, Fonts.SUPPLIER8, Draw2D.getCenteredX(text4, Fonts.SUPPLIER8, floatValue15, floatValue19, floatValue15), Draw2D.getCenteredY(text4, Fonts.SUPPLIER8, floatValue15, floatValue11, 13.0F), value5, floatValue15);
      Draw2D.drawStyledText(text3, supplier, floatValue19 + floatValue15 + 3.0F, Draw2D.getTextBaseline(supplier, floatValue16, floatValue11, 13.0F), value5, floatValue16);
   }

   private static boolean isHovered(float floatValue10, float floatValue11, float floatValue12, float floatValue13, float floatValue14, float floatValue15) {
      return floatValue10 >= floatValue12 && floatValue10 <= floatValue12 + floatValue14 && floatValue11 >= floatValue13 && floatValue11 <= floatValue13 + floatValue15;
   }

   private static float clamp(float floatValue10, float floatValue11, float floatValue12) {
      return Math.max(floatValue11, Math.min(floatValue12, floatValue10));
   }

   private enum ModeKind {
      NONE,
      HUE,
      C5,
      ALPHA,
      BLUR;

      private static String a(byte[] byteArray) {
         int value4 = 0;
         int value5;
         char[] charArray = new char[value5 = byteArray.length];

         for (int i = 0; i < value5; i++) {
            int value6;
            if ((value6 = 255 & byteArray[i]) < 192) {
               charArray[value4++] = (char)value6;
            } else if (value6 < 224) {
               char ch = (char)((char)(value6 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value4++] = ch;
            } else if (i < value5 - 2) {
               char ch2 = (char)((char)(value6 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value4++] = ch2;
            }
         }

         return new String(charArray, 0, value4);
      }
   }
}
