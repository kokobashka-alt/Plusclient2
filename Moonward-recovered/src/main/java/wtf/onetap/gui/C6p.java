package wtf.onetap.gui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.setting.MultiModeSetting;

public class C6p {
   private final MultiModeSetting multiModeSetting;
   private final Animation animation;
   private Set<String> stringSet;

   public C6p(MultiModeSetting multiModeSetting2) {
      this.animation = new Animation(250L, 1.0F, Easing.EASING3);
      this.multiModeSetting = multiModeSetting2;
      this.stringSet = Set.copyOf((Collection<? extends String>)multiModeSetting2.getValue());
   }

   public float m_2_float(float floatValue) {
      String[] stringArray = this.multiModeSetting.getOptions().toArray(String[]::new);
      if (stringArray.length == 0) {
         return 42.0F;
      }

      float floatValue2 = 6.0F;
      int value = 1;
      float floatValue3 = 4.0F;

      for (String text : stringArray) {
         float floatValue4 = Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 11.0F) + 6.0F;
         if (floatValue2 + floatValue4 > floatValue + 6.0F && floatValue2 > 6.0F) {
            value++;
            floatValue2 = 6.0F;
         }

         floatValue2 += floatValue4 + floatValue3;
      }

      return 23 + value * 20;
   }

   public void m_4_void(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      ColorPicker.m_0_StringArr();
      int value = (int)(200.0F * floatValue4);
      int value2 = (int)(255.0F * floatValue4);
      int value3 = ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), value);
      Draw2D.drawStyledText(this.multiModeSetting.getName(), Fonts.SUPPLIER16, floatValue + 6.0F, floatValue2 + 5.0F, value3, 12.0F);
      float floatValue5 = floatValue2 + 23.0F;
      String[] stringArray = this.multiModeSetting.getOptions().toArray(String[]::new);
      float floatValue6 = floatValue + 6.0F;
      float floatValue7 = 4.0F;
      float floatValue8 = floatValue + 6.0F + floatValue3;
      float floatValue9 = this.animation.get();
      int value4 = ColorUtil.rgba(70, 70, 70, 180);
      int value5 = ColorUtil.rgba(25, 25, 25, 180);
      Set set = (Set)this.multiModeSetting.getValue();

      for (String text : stringArray) {
         float floatValue10 = Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 11.0F) + 6.0F;
         if (floatValue6 + floatValue10 > floatValue8 && floatValue6 > floatValue + 6.0F) {
            floatValue6 = floatValue + 6.0F;
            floatValue5 += 20.0F;
         }

         boolean flag = set.contains(text);
         boolean flag2 = this.stringSet.contains(text);
         float floatValue11 = flag && flag2 ? 1.0F : (flag ? floatValue9 : (flag2 ? 1.0F - floatValue9 : 0.0F));
         int value6 = ColorUtil.lerpColor(value4, ColorUtil.primaryColor(), floatValue11);
         int value7 = ColorUtil.lerpColor(value5, ColorUtil.secondaryColor(), floatValue11);
         Draw2D.m_8(floatValue6, floatValue5, floatValue10, 16.0F, 4.0F, 0.0F, ColorUtil.withAlpha(value6, (int)(230.0F * floatValue4)), ColorUtil.withAlpha(value7, (int)(230.0F * floatValue4)));
         float floatValue12 = Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 11.0F);
         int value8 = ColorUtil.lerpColor(value3, ColorUtil.withAlpha(ColorUtil.rgba(245, 245, 245, 245), value2), floatValue11);
         Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER16, floatValue6 + (floatValue10 - floatValue12) / 2.0F, floatValue5 + 2.0F, value8, 11.0F);
         floatValue6 += floatValue10 + floatValue7;
      }
   }

   public boolean m_3_boolean(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      float floatValue6 = floatValue4 + 23.0F;
      if (floatValue2 < floatValue6) {
         return false;
      }

      String[] stringArray = this.multiModeSetting.getOptions().toArray(String[]::new);
      float floatValue7 = floatValue3 + 6.0F;
      float floatValue8 = 4.0F;
      float floatValue9 = floatValue3 + 6.0F + floatValue5;

      for (String text : stringArray) {
         float floatValue10 = Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 11.0F) + 6.0F;
         if (floatValue7 + floatValue10 > floatValue9 && floatValue7 > floatValue3 + 6.0F) {
            floatValue7 = floatValue3 + 6.0F;
            floatValue6 += 20.0F;
         }

         if (floatValue2 >= floatValue6 && floatValue2 <= floatValue6 + 16.0F && floatValue >= floatValue7 && floatValue <= floatValue7 + floatValue10) {
            float floatValue11 = this.animation.get();
            this.stringSet = Set.copyOf((Collection<? extends String>)this.multiModeSetting.getValue());
            HashSet set = new HashSet((Collection)this.multiModeSetting.getValue());
            if (set.contains(text)) {
               set.remove(text);
            } else {
               set.add(text);
            }

            this.multiModeSetting.setValue(Set.copyOf(set));
            this.animation.setDuration(1.0F - floatValue11);
            this.animation.setTarget(1.0F);
            return true;
         }

         floatValue7 += floatValue10 + floatValue8;
      }

      return false;
   }

   public void m_3_void() {
   }
}
