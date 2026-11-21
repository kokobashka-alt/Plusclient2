package wtf.onetap.gui;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.util.KeyNames;

public class ChangelogPanel {
   private static final Pattern PATTERN = Pattern.compile("^\\[.]\\s*");
   private final Animation animation;
   private final Animation animation2;
   private final Animation animation3;
   private final Animation animation4;
   private final Animation animation5;
   private final Animation animation6;
   private final Animation animation7;
   private final Animation animation8;
   private final Animation animation9;
   private final Animation animation10;
   private final List<Animation> animationList;
   private List<ChangelogPanel.DataRecord2> c6List;
   private final List<Integer> integerList;
   private List<ChangelogPanel.DataRecord> c2List;
   private List<ChangelogPanel.DataRecord> c2List2;
   private float floatValue;
   private boolean flag;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private float floatValue10;
   private int value;
   private float floatValue11;
   private float floatValue12;
   private float floatValue13;
   private float floatValue14;
   private boolean flag2;

   public ChangelogPanel() {
      this.animation = new Animation(220L, 0.0F, Easing.EASING8);
      this.animation2 = new Animation(140L, 0.0F, Easing.EASING8);
      this.animation3 = new Animation(140L, 0.0F, Easing.EASING8);
      this.animation4 = new Animation(180L, 0.0F, Easing.EASING8);
      this.animation5 = new Animation(220L, 0.0F, Easing.EASING8);
      this.animation6 = new Animation(220L, 1.0F, Easing.EASING8);
      this.animation7 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation8 = new Animation(180L, 0.0F, Easing.C02);
      this.animation9 = new Animation(180L, 0.0F, Easing.EASING8);
      this.animation10 = new Animation(200L, 0.0F, Easing.EASING8);
      this.animationList = new ArrayList<>();
      this.integerList = new ArrayList<>();
      this.c2List = List.of();
      this.c2List2 = List.of();
      this.floatValue10 = -1.0F;
   }

   public void m_1(float floatValue15, float floatValue16, float floatValue17, float floatValue18, float floatValue19, boolean flag3) {
      if (!(floatValue19 < 0.01F) && this.m_0_boolean(floatValue17)) {
         float floatValue20 = this.m_2_float(floatValue17);
         float floatValue21 = this.animation2.update(flag3 && isHovered(floatValue15, floatValue16, floatValue20, floatValue18, this.floatValue12, 24.0F) ? 1.0F : 0.0F);
         Draw2D.drawPanel(floatValue20, floatValue18, this.floatValue12, 24.0F, floatValue19);
         if (floatValue21 > 0.01F) {
            Draw2D.roundedRect(floatValue20 + 3.0F, floatValue18 + 3.0F, this.floatValue12 - 6.0F, 18.0F, 5.0F, ColorUtil.rgba(255, 255, 255, (int)(22.0F * floatValue21 * floatValue19)));
         }

         int value2 = ColorUtil.rgba(230, 230, 230, (int)(195.0F * floatValue19));
         float floatValue22 = floatValue20 + 9.0F;
         Draw2D.drawTextWithShadow(
            "\uf1ea",
            Fonts.SUPPLIER8,
            floatValue22,
            Draw2D.getCenteredY("\uf1ea", Fonts.SUPPLIER8, 12.0F, floatValue18, 24.0F),
            ColorUtil.lerpColor(value2, ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(255.0F * floatValue19)), floatValue21),
            12.0F
         );
         floatValue22 += Draw2D.getTextWidth("\uf1ea", Fonts.SUPPLIER8, 12.0F) + 6.0F;
         Draw2D.drawTextWithShadow(
            "Что нового",
            Fonts.SUPPLIER16,
            floatValue22,
            Draw2D.getTextBaseline(Fonts.SUPPLIER16, 11.0F, floatValue18, 24.0F),
            ColorUtil.lerpColor(value2, ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue19)), floatValue21),
            11.0F
         );
      }
   }

   public void m_2(float floatValue15, float floatValue16, float floatValue17, float floatValue18) {
      float floatValue19 = this.animation.update(this.flag2 ? 1.0F : 0.0F);
      if (!(floatValue19 < 0.01F) && this.m_0_boolean(floatValue17)) {
         Draw2D.roundedRect(0.0F, 0.0F, floatValue17, floatValue18, 0.0F, ColorUtil.rgba(0, 0, 0, (int)(100.0F * floatValue19)));
         float floatValue20 = 0.92F + 0.08F * floatValue19;
         float floatValue21 = floatValue17 / 2.0F;
         float floatValue22 = floatValue18 / 2.0F;
         Render2D.setTransform(floatValue20, floatValue21 - floatValue21 * floatValue20, floatValue22 - floatValue22 * floatValue20);

         try {
            float floatValue23 = this.floatValue10;
            float floatValue24 = this.animation5.update(this.m_1_float(floatValue18));
            float floatValue25 = this.m_3_float(floatValue17);
            float floatValue26 = 20.0F;
            this.m_3_void(floatValue15, floatValue16, floatValue25, floatValue26, floatValue19);
            Draw2D.drawPanel(floatValue25, floatValue26, floatValue23, floatValue24, floatValue19);
            m_5("\uf1ea", "Список изменений", floatValue25, floatValue26, floatValue19);
            this.m_9_void(floatValue15, floatValue16, floatValue25 + floatValue23 - 34.0F, floatValue26 + 6.0F, floatValue19);
            this.m_5(floatValue15, floatValue16, floatValue25, floatValue26 + 40.0F, floatValue23, floatValue24 - 40.0F - 14.0F, floatValue19);
         } finally {
            Render2D.setTransform(1.0F, 0.0F, 0.0F);
         }
      }
   }

   public boolean isVisible() {
      return this.flag2 || this.animation.getValue() > 0.01F;
   }

   public boolean m_9_boolean(float floatValue15, float floatValue16, float floatValue17, float floatValue18, float floatValue19) {
      if (!this.m_0_boolean(floatValue17)) {
         return false;
      }

      if (!this.flag2) {
         if (this.isVisible()) {
            return true;
         }

         if (!isHovered(floatValue15, floatValue16, this.m_2_float(floatValue17), floatValue19, this.floatValue12, 24.0F)) {
            return false;
         }

         this.flag2 = true;
         this.animation5.setDuration(this.m_1_float(floatValue18));
         return true;
      } else {
         float floatValue20 = this.animation5.getValue();
         float floatValue21 = this.m_3_float(floatValue17);
         float floatValue22 = 20.0F;
         int value2 = this.m_0_int(floatValue15, floatValue16);
         if (value2 >= 0) {
            this.m_0_void(value2);
            return true;
         }

         if (this.m_3_boolean(floatValue15, floatValue16)) {
            return true;
         }

         if (!isHovered(floatValue15, floatValue16, floatValue21, floatValue22, this.floatValue10, floatValue20) || isHovered(floatValue15, floatValue16, floatValue21 + this.floatValue10 - 34.0F, floatValue22 + 6.0F, 26.0F, 26.0F)) {
            this.flag2 = false;
         }

         return true;
      }
   }

   private void m_0_void(int value2) {
      if (value2 != this.value) {
         this.c2List2 = this.c2List;
         this.floatValue = this.animation4.getValue();
         this.value = value2;
         this.floatValue14 = 0.0F;
         this.animation4.setDuration(0.0F);
         this.flag = false;
         this.animation6.reset(0.0F);
         this.m_6_void(this.floatValue10);
      }
   }

   private boolean m_3_boolean(float floatValue15, float floatValue16) {
      if (!(this.animation9.updateOrSnap() < 1.0F) && isHovered(floatValue15, floatValue16, this.floatValue3 - 3.0F, this.floatValue4, 9.0F, this.floatValue5)) {
         this.floatValue2 = floatValue16 >= this.floatValue6 && floatValue16 <= this.floatValue6 + this.floatValue7 ? floatValue16 - this.floatValue6 : this.floatValue7 / 2.0F;
         return this.flag = true;
      } else {
         return false;
      }
   }

   private static void m_5(String text, String text2, float floatValue15, float floatValue16, float floatValue17) {
      Draw2D.drawStyledText(text, Fonts.SUPPLIER8, floatValue15 + 16.0F, Draw2D.getCenteredY(text, Fonts.SUPPLIER8, 13.0F, floatValue16 + 6.0F, 26.0F), ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(255.0F * floatValue17)), 13.0F);
      Draw2D.drawStyledText(
         text2,
         Fonts.SUPPLIER15,
         floatValue15 + 16.0F + Draw2D.getTextWidth(text, Fonts.SUPPLIER8, 13.0F) + 7.0F,
         Draw2D.getTextBaseline(Fonts.SUPPLIER15, 13.0F, floatValue16 + 6.0F, 26.0F),
         ColorUtil.rgba(245, 245, 245, (int)(245.0F * floatValue17)),
         13.0F
      );
   }

   private void m_3_void(float floatValue15, float floatValue16, float floatValue17, float floatValue18, float floatValue19) {
      if (this.integerList.size() >= 2) {
         this.floatValue8 = floatValue17 - 8.0F - this.floatValue13;
         this.floatValue9 = floatValue18 + 40.0F;
         Draw2D.drawPanel(this.floatValue8, floatValue18, this.floatValue13, 40.0F + this.integerList.size() * 30.0F + 4.0F, floatValue19);
         m_5("\uf133", "Обновления", this.floatValue8, floatValue18, floatValue19);
         int value2 = ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(255.0F * floatValue19));
         float floatValue20 = this.floatValue9 + this.animation10.update(this.value * 30.0F);
         Draw2D.roundedRect(this.floatValue8 + 6.0F, floatValue20, this.floatValue13 - 12.0F, 26.0F, 5.0F, ColorUtil.withAlpha(value2, (int)(40.0F * floatValue19)));
         int value3 = this.flag2 ? this.m_0_int(floatValue15, floatValue16) : -1;

         for (int i = 0; i < this.integerList.size(); i++) {
            float floatValue21 = this.floatValue9 + i * 30.0F;
            float floatValue22 = this.animationList.get(i).update(i == value3 ? 1.0F : 0.0F);
            if (floatValue22 > 0.01F) {
               Draw2D.roundedRect(this.floatValue8 + 6.0F, floatValue21, this.floatValue13 - 12.0F, 26.0F, 5.0F, ColorUtil.rgba(255, 255, 255, (int)(22.0F * floatValue22 * floatValue19)));
            }

            float floatValue23 = Math.clamp(1.0F - Math.abs(floatValue20 - floatValue21) / 30.0F, 0.0F, 1.0F);
            int value4 = ColorUtil.lerpColor(ColorUtil.rgba(230, 230, 230, (int)(195.0F * floatValue19)), ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue19)), floatValue22);
            Draw2D.drawTextWithShadow(
               this.c6List.get(this.integerList.get(i)).m_7j(),
               Fonts.SUPPLIER16,
               this.floatValue8 + 16.0F,
               Draw2D.getTextBaseline(Fonts.SUPPLIER16, 12.5F, floatValue21, 26.0F),
               ColorUtil.lerpColor(value4, value2, floatValue23),
               12.5F
            );
         }

         float floatValue24 = Draw2D.getTextWidth("NEW", Fonts.SUPPLIER16, 9.0F) + 12.0F;
         float floatValue25 = this.floatValue8 + this.floatValue13 - 16.0F - floatValue24;
         float floatValue26 = this.floatValue9 + 6.0F;
         Draw2D.roundedRect(floatValue25, floatValue26, floatValue24, 14.0F, 4.0F, ColorUtil.withAlpha(value2, (int)(60.0F * floatValue19)));
         Draw2D.drawStyledText("NEW", Fonts.SUPPLIER16, floatValue25 + 6.0F, Draw2D.getTextBaseline(Fonts.SUPPLIER16, 9.0F, floatValue26, 14.0F), value2, 9.0F);
      }
   }

   private int m_0_int(float floatValue15, float floatValue16) {
      if (this.integerList.size() >= 2 && !(floatValue15 < this.floatValue8 + 6.0F) && !(floatValue15 > this.floatValue8 + this.floatValue13 - 6.0F) && !(floatValue16 < this.floatValue9)) {
         int value2 = (int)((floatValue16 - this.floatValue9) / 30.0F);
         return value2 < this.integerList.size() && floatValue16 - this.floatValue9 - value2 * 30.0F <= 26.0F ? value2 : -1;
      } else {
         return -1;
      }
   }

   public boolean consumeFlag() {
      if (!this.flag2) {
         return false;
      }

      this.flag2 = false;
      return true;
   }

   public boolean m_5_boolean(double doubleValue, float floatValue15) {
      if (!this.flag2) {
         return false;
      }

      this.floatValue14 = Math.clamp(this.floatValue14 - (float)doubleValue * 14.5F * 3.0F, 0.0F, this.m_7_float(floatValue15));
      return true;
   }

   private void m_9_void(float floatValue15, float floatValue16, float floatValue17, float floatValue18, float floatValue19) {
      float floatValue20 = this.animation3.update(this.flag2 && isHovered(floatValue15, floatValue16, floatValue17, floatValue18, 26.0F, 26.0F) ? 1.0F : 0.0F);
      float floatValue21 = 16.0F + floatValue20 * 3.0F;
      int value2 = ColorUtil.lerpColor(ColorUtil.rgba(220, 220, 220, (int)(180.0F * floatValue19)), ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue19)), floatValue20);
      Draw2D.drawStyledText("\uf00d", Fonts.SUPPLIER8, floatValue17 + (26.0F - Draw2D.getTextWidth("\uf00d", Fonts.SUPPLIER8, floatValue21)) / 2.0F, Draw2D.getCenteredY("\uf00d", Fonts.SUPPLIER8, floatValue21, floatValue18, 26.0F), value2, floatValue21);
   }

   private void m_5(float floatValue15, float floatValue16, float floatValue17, float floatValue18, float floatValue19, float floatValue20, float floatValue21) {
      float floatValue22 = Math.max(0.0F, this.floatValue11 - floatValue20);
      this.flag = this.flag & (floatValue22 > 0.0F && KeyNames.isLeftMouseDown());
      if (this.flag) {
         this.animation4.setDuration(this.floatValue14 = Math.clamp((floatValue16 - this.floatValue2 - floatValue18) / Math.max(1.0F, floatValue20 - this.floatValue7) * floatValue22, 0.0F, floatValue22));
      }

      this.floatValue14 = Math.clamp(this.floatValue14, 0.0F, floatValue22);
      float floatValue23 = this.animation4.update(this.floatValue14);
      float floatValue24 = this.animation6.update(1.0F);
      Render2D.pushScissor(floatValue17, floatValue18, floatValue19, floatValue20);

      try {
         if (floatValue24 < 1.0F) {
            m_6(this.c2List2, floatValue17, floatValue18 - this.floatValue, floatValue19, floatValue18, floatValue20, floatValue21 * (1.0F - floatValue24));
         }

         m_6(this.c2List, floatValue17, floatValue18 - floatValue23, floatValue19, floatValue18, floatValue20, floatValue21 * floatValue24);
      } finally {
         Render2D.popScissor();
      }

      this.floatValue3 = floatValue17 + floatValue19 - 6.0F;
      this.floatValue4 = floatValue18;
      this.floatValue5 = floatValue20;
      if (floatValue22 > 0.0F) {
         this.floatValue7 = Math.max(18.0F, floatValue20 * floatValue20 / this.floatValue11);
         this.floatValue6 = floatValue18 + (floatValue20 - this.floatValue7) * floatValue23 / floatValue22;
      }

      float floatValue25 = this.animation9.update(floatValue22 > 0.0F ? 1.0F : 0.0F);
      if (!(floatValue25 < 0.01F)) {
         boolean flag3 = this.flag2 && isHovered(floatValue15, floatValue16, this.floatValue3 - 3.0F, this.floatValue6, 9.0F, this.floatValue7);
         Draw2D.m_7(
            this.floatValue3,
            this.floatValue4,
            3.0F,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7,
            this.animation7.update(!flag3 && !this.flag ? 0.0F : 1.0F),
            this.animation8.update(this.flag ? 1.0F : 0.0F),
            floatValue21 * floatValue25
         );
      }
   }

   private static void m_6(List<ChangelogPanel.DataRecord> c2List3, float floatValue15, float floatValue16, float floatValue17, float floatValue18, float floatValue19, float floatValue20) {
      if (!(floatValue20 < 0.01F)) {
         float floatValue21 = floatValue15 + 16.0F;
         int value2 = ColorUtil.rgba(255, 255, 255, (int)(14.0F * floatValue20));
         int value3 = ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue20));
         int value4 = ColorUtil.rgba(228, 228, 228, (int)(240.0F * floatValue20));
         float floatValue22 = (float)(System.currentTimeMillis() % 4000L) / 4000.0F;

         for (ChangelogPanel.DataRecord c2 : c2List3) {
            float floatValue23 = floatValue16 + c2.m_9_e();
            if (floatValue23 > floatValue18 + floatValue19) {
               break;
            }

            if (!(floatValue23 + c2.m_9_c_float() < floatValue18)) {
               ChangelogPanel.DataRecord2 c6 = c2.m_00();
               switch (c6.m_02_C4().ordinal()) {
                  case 0:
                     Draw2D.roundedRect(floatValue21 - 8.0F, floatValue23 - 8.0F, floatValue17 - 32.0F + 16.0F - 4.0F, c2.m_9_c_float() + 16.0F, 7.0F, value2);
                     Draw2D.m_9(
                        c6.m_7j(),
                        Fonts.SUPPLIER16,
                        floatValue21,
                        Draw2D.getTextBaseline(Fonts.SUPPLIER16, 12.5F, floatValue23, 20.0F),
                        ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(255.0F * floatValue20)),
                        ColorUtil.withAlpha(ColorUtil.secondaryColor(), (int)(255.0F * floatValue20)),
                        12.5F,
                        floatValue22
                     );
                     break;
                  case 1:
                     float floatValue24 = Draw2D.getTextWidth(c6.m_7j(), Fonts.SUPPLIER16, 10.0F) + 12.0F;
                     float floatValue25 = floatValue23 + 3.0F;
                     Draw2D.roundedRect(floatValue21, floatValue25, floatValue24, 14.0F, 4.0F, ColorUtil.withAlpha(c6.m_10().value, (int)(45.0F * floatValue20)));
                     Draw2D.drawStyledText(
                        c6.m_7j(), Fonts.SUPPLIER16, floatValue21 + 6.0F, Draw2D.getTextBaseline(Fonts.SUPPLIER16, 10.0F, floatValue25, 14.0F), ColorUtil.withAlpha(c6.m_10().value, (int)(255.0F * floatValue20)), 10.0F
                     );
                     break;
                  case 2:
                     m_0(c2, floatValue21, floatValue23, value3, value4, floatValue20);
               }
            }
         }
      }
   }

   private static void m_0(ChangelogPanel.DataRecord c2, float floatValue15, float floatValue16, int value2, int value3, float floatValue17) {
      ChangelogPanel.DataRecord2 c6 = c2.m_00();
      Draw2D.roundedRect(floatValue15 + 1.0F, floatValue16 + 5.25F, 4.0F, 4.0F, 2.0F, ColorUtil.withAlpha(c6.m_10().value, (int)(255.0F * floatValue17)));
      float floatValue18 = floatValue15 + 10.0F;
      float floatValue19 = Draw2D.getTextBaseline(Fonts.SUPPLIER16, 11.0F, floatValue16, 14.5F);
      float floatValue20 = floatValue18;
      if (!c6.m_7x().isEmpty()) {
         Draw2D.drawTextWithShadow(c6.m_7x(), Fonts.SUPPLIER16, floatValue18, floatValue19, value2, 11.0F);
         floatValue20 += Draw2D.getTextWidth(c6.m_7x() + " ", Fonts.SUPPLIER16, 11.0F);
      }

      for (int i = 0; i < c2.m_58().size(); i++) {
         Draw2D.drawTextWithShadow(c2.m_58().get(i), Fonts.SUPPLIER16, i == 0 ? floatValue20 : floatValue18, floatValue19 + i * 14.5F, value3, 11.0F);
      }
   }

   private boolean m_0_boolean(float floatValue15) {
      if (this.c6List == null) {
         this.c6List = loadChangelog();
         this.floatValue12 = 9.0F + Draw2D.getTextWidth("\uf1ea", Fonts.SUPPLIER8, 12.0F) + 6.0F + Draw2D.getTextWidth("Что нового", Fonts.SUPPLIER16, 11.0F) + 9.0F;
         this.floatValue13 = 16.0F + Draw2D.getTextWidth("\uf133", Fonts.SUPPLIER8, 13.0F) + 7.0F + Draw2D.getTextWidth("Обновления", Fonts.SUPPLIER15, 13.0F) + 16.0F;
         float floatValue16 = Draw2D.getTextWidth("NEW", Fonts.SUPPLIER16, 9.0F) + 12.0F;

         for (int i = 0; i < this.c6List.size(); i++) {
            if (this.c6List.get(i).m_02_C4() == ChangelogPanel.ModeKind2.RELEASE) {
               this.integerList.add(i);
               this.floatValue13 = Math.max(this.floatValue13, 16.0F + Draw2D.getTextWidth(this.c6List.get(i).m_7j(), Fonts.SUPPLIER16, 12.5F) + 10.0F + floatValue16 + 16.0F);
               this.animationList.add(new Animation(140L, 0.0F, Easing.EASING8));
            }
         }
      }

      if (this.c6List.isEmpty()) {
         return false;
      }

      float floatValue17 = this.integerList.size() < 2 ? 0.0F : this.floatValue13 + 8.0F;
      float floatValue18 = Math.clamp(floatValue15 - (20.0F + floatValue17) * 2.0F, 240.0F, 400.0F);
      if (floatValue18 != this.floatValue10) {
         this.m_6_void(floatValue18);
      }

      return true;
   }

   private float m_3_float(float floatValue15) {
      return (floatValue15 - this.floatValue10) / 2.0F;
   }

   private void m_6_void(float floatValue15) {
      this.floatValue10 = floatValue15;
      float floatValue16 = floatValue15 - 32.0F - 10.0F - 6.0F;
      int value2 = this.integerList.get(this.value);
      int value3 = this.value + 1 < this.integerList.size() ? this.integerList.get(this.value + 1) : this.c6List.size();
      ArrayList<ChangelogPanel.DataRecord> c2List3 = new ArrayList<>(value3 - value2);
      float floatValue17 = 8.0F;

      for (ChangelogPanel.DataRecord2 c6 : this.c6List.subList(value2, value3)) {
         switch (c6.m_02_C4().ordinal()) {
            case 0:
               c2List3.add(new ChangelogPanel.DataRecord(c6, floatValue17, 20.0F, List.of()));
               floatValue17 += 20.0F;
               break;
            case 1:
               c2List3.add(new ChangelogPanel.DataRecord(c6, floatValue17, 20.0F, List.of()));
               floatValue17 += 20.0F;
               break;
            case 2:
               var stringList = m_5(c6, floatValue16);
               c2List3.add(new ChangelogPanel.DataRecord(c6, floatValue17, stringList.size() * 14.5F, stringList));
               floatValue17 += stringList.size() * 14.5F + 2.0F;
         }
      }

      ChangelogPanel.DataRecord c2 = (ChangelogPanel.DataRecord)c2List3.getFirst();
      c2List3.set(0, new ChangelogPanel.DataRecord(c2.m_00(), c2.m_9_e(), floatValue17 - 2.0F - c2.m_9_e(), c2.m_58()));
      this.c2List = c2List3;
      this.floatValue11 = floatValue17 + 8.0F;
   }

   private static List<String> m_5(ChangelogPanel.DataRecord2 c6, float floatValue15) {
      ArrayList list = new ArrayList();
      float floatValue16 = floatValue15 - (c6.m_7x().isEmpty() ? 0.0F : Draw2D.getTextWidth(c6.m_7x() + " ", Fonts.SUPPLIER16, 11.0F));
      StringBuilder stringBuilder = new StringBuilder();

      for (String text : c6.m_7j().split(" ")) {
         if (!stringBuilder.isEmpty() && Draw2D.getTextWidth(stringBuilder + " " + text, Fonts.SUPPLIER16, 11.0F) > floatValue16) {
            list.add(stringBuilder.toString());
            stringBuilder.setLength(0);
            floatValue16 = floatValue15;
         }

         if (!stringBuilder.isEmpty()) {
            stringBuilder.append(' ');
         }

         stringBuilder.append(text);
      }

      list.add(stringBuilder.toString());
      return list;
   }

   private float m_2_float(float floatValue15) {
      return (floatValue15 - this.floatValue12) / 2.0F;
   }

   private float m_1_float(float floatValue15) {
      return Math.min(40.0F + this.floatValue11 + 14.0F, floatValue15 - 40.0F);
   }

   private float m_4_float(float floatValue15) {
      return this.m_1_float(floatValue15) - 40.0F - 14.0F;
   }

   private float m_7_float(float floatValue15) {
      return Math.max(0.0F, this.floatValue11 - this.m_4_float(floatValue15));
   }

   private static List<ChangelogPanel.DataRecord2> loadChangelog() {
      try (InputStream inputStream = ChangelogPanel.class.getResourceAsStream("/assets/onetap/CHANGELOG.md")) {
         if (inputStream == null) {
            return List.of();
         }

         ArrayList list = new ArrayList();
         ChangelogPanel.ModeKind c1 = ChangelogPanel.ModeKind.OTHER;
         boolean flag3 = false;

         for (String text : new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).split("\\R")) {
            String text2 = text.strip().replace('—', '-').replace('–', '-');
            if (text2.startsWith("## ")) {
               flag3 = true;
               c1 = ChangelogPanel.ModeKind.OTHER;
               list.add(new ChangelogPanel.DataRecord2(ChangelogPanel.ModeKind2.RELEASE, c1, "", text2.substring(3).strip()));
            } else if (flag3 && !text2.isEmpty()) {
               if (text2.startsWith("* ")) {
                  list.add(m_4(c1, PATTERN.matcher(text2.substring(2)).replaceFirst("").strip()));
               } else if (text2.startsWith("[")) {
                  String text3 = PATTERN.matcher(text2).replaceFirst("").strip();
                  if (text3.endsWith(":")) {
                     text3 = text3.substring(0, text3.length() - 1);
                  }

                  c1 = ChangelogPanel.ModeKind.m_5_C1(text3);
                  list.add(new ChangelogPanel.DataRecord2(ChangelogPanel.ModeKind2.SECTION, c1, "", text3));
               }
            }
         }

         return list;
      } catch (Exception e) {
         return List.of();
      }
   }

   private static ChangelogPanel.DataRecord2 m_4(ChangelogPanel.ModeKind c1, String text) {
      int value2 = text.indexOf(": ");
      return value2 > 0 && value2 <= 30
         ? new ChangelogPanel.DataRecord2(ChangelogPanel.ModeKind2.ITEM, c1, text.substring(0, value2 + 1), text.substring(value2 + 2))
         : new ChangelogPanel.DataRecord2(ChangelogPanel.ModeKind2.ITEM, c1, "", text);
   }

   private static boolean isHovered(float floatValue15, float floatValue16, float floatValue17, float floatValue18, float floatValue19, float floatValue20) {
      return floatValue15 >= floatValue17 && floatValue15 <= floatValue17 + floatValue19 && floatValue16 >= floatValue18 && floatValue16 <= floatValue18 + floatValue20;
   }

   private enum ModeKind {
      ADDED(ColorUtil.rgba(120, 225, 140, 255)),
      CHANGED(ColorUtil.rgba(115, 190, 255, 255)),
      FIXED(ColorUtil.rgba(255, 196, 92, 255)),
      REMOVED(ColorUtil.rgba(255, 115, 115, 255)),
      OTHER(ColorUtil.rgba(200, 200, 200, 255));

      private final int value;

      ModeKind(int value2) {
         this.value = value2;
      }

      private static ChangelogPanel.ModeKind m_5_C1(String text) {
         ClickGUI.m_3_String();
         String text2 = text.toLowerCase(Locale.ROOT);
         if (text2.startsWith("добав")) {
            return ADDED;
         } else if (text2.startsWith("измен")) {
            return CHANGED;
         } else if (text2.startsWith("исправ")) {
            return FIXED;
         } else {
            return text2.startsWith("удал") ? REMOVED : OTHER;
         }
      }
   }

   private record DataRecord(ChangelogPanel.DataRecord2 f_00, float f_9_e, float f_9_c, List<String> f_58) {
      public ChangelogPanel.DataRecord2 m_00() {
         return this.f_00;
      }

      public float m_9_e() {
         return this.f_9_e;
      }

      public float m_9_c_float() {
         return this.f_9_c;
      }

      public List<String> m_58() {
         return this.f_58;
      }
   }

   private enum ModeKind2 {
      RELEASE,
      SECTION,
      ITEM;

      private static String a(byte[] byteArray) {
         int value2 = 0;
         int value3;
         char[] charArray = new char[value3 = byteArray.length];

         for (int i = 0; i < value3; i++) {
            int value4;
            if ((value4 = 255 & byteArray[i]) < 192) {
               charArray[value2++] = (char)value4;
            } else if (value4 < 224) {
               char ch = (char)((char)(value4 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value2++] = ch;
            } else if (i < value3 - 2) {
               char ch2 = (char)((char)(value4 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value2++] = ch2;
            }
         }

         return new String(charArray, 0, value2);
      }
   }

   private record DataRecord2(ChangelogPanel.ModeKind2 f_02, ChangelogPanel.ModeKind f_10, String f_7x, String f_7j) {
      public ChangelogPanel.ModeKind2 m_02_C4() {
         return this.f_02;
      }

      public ChangelogPanel.ModeKind m_10() {
         return this.f_10;
      }

      public String m_7x() {
         return this.f_7x;
      }

      public String m_7j() {
         return this.f_7j;
      }
   }
}
