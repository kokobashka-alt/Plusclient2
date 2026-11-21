package wtf.onetap.util;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.gui.widget.TextField;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;

public class MouseUtil {
   private final Supplier<List<TextField>> supplier;
   private final Predicate<TextField> predicate;
   private final Animation animation;
   private final MouseUtil.StateHolder c3;
   private final MouseUtil.StateHolder c32;

   public MouseUtil(Supplier<List<TextField>> supplier2, Predicate<TextField> predicate2) {
      this.animation = new Animation(200L, 0.0F, Easing.EASING8);
      this.c3 = new MouseUtil.StateHolder();
      this.c32 = new MouseUtil.StateHolder();
      this.supplier = supplier2;
      this.predicate = predicate2;
   }

   public float[] m_6(TextField textField, float floatValue, float floatValue2) {
      if (isAltDown()) {
         this.m_4_void();
         return new float[]{floatValue, floatValue2};
      }

      Minecraft mc = Minecraft.getInstance();
      float floatValue3 = mc.getWindow().getGuiScaledWidth();
      float floatValue4 = mc.getWindow().getGuiScaledHeight();
      float floatValue5 = 5.001F;
      float floatValue6 = floatValue5;
      float floatValue7 = 0.0F;
      float floatValue8 = 0.0F;
      this.c3.flag = false;
      this.c32.flag = false;
      float floatValue9 = floatValue3 / 2.0F - (floatValue + textField.m_6_float() / 2.0F);
      if (Math.abs(floatValue9) < floatValue5) {
         floatValue5 = Math.abs(floatValue9);
         floatValue7 = floatValue9;
         this.c3.flag = true;
         this.c3.floatValue = floatValue3 / 2.0F;
      }

      float floatValue10 = floatValue4 / 2.0F - (floatValue2 + textField.m_9_float() / 2.0F);
      if (Math.abs(floatValue10) < floatValue6) {
         floatValue6 = Math.abs(floatValue10);
         floatValue8 = floatValue10;
         this.c32.flag = true;
         this.c32.floatValue = floatValue4 / 2.0F;
      }

      for (TextField textField2 : this.supplier.get()) {
         if (textField2 != textField && this.predicate.test(textField2) && !(textField2.m_6_float() <= 0.0F) && !(textField2.m_9_float() <= 0.0F)) {
            float[] floatArray = new float[]{textField2.m_5_float(), textField2.m_5_float() + textField2.m_6_float()};
            float[] floatArray2 = new float[]{textField2.m_8_float(), textField2.m_8_float() + textField2.m_9_float()};
            float[] floatArray3 = new float[]{floatValue, floatValue + textField.m_6_float()};
            float[] floatArray4 = new float[]{floatValue2, floatValue2 + textField.m_9_float()};

            for (float floatValue11 : floatArray) {
               for (float floatValue12 : floatArray3) {
                  float floatValue13 = floatValue11 - floatValue12;
                  if (Math.abs(floatValue13) < floatValue5) {
                     floatValue5 = Math.abs(floatValue13);
                     floatValue7 = floatValue13;
                     this.c3.flag = true;
                     this.c3.floatValue = floatValue11;
                  }
               }
            }

            for (float floatValue14 : floatArray2) {
               for (float floatValue15 : floatArray4) {
                  float floatValue16 = floatValue14 - floatValue15;
                  if (Math.abs(floatValue16) < floatValue6) {
                     floatValue6 = Math.abs(floatValue16);
                     floatValue8 = floatValue16;
                     this.c32.flag = true;
                     this.c32.floatValue = floatValue14;
                  }
               }
            }
         }
      }

      return new float[]{floatValue + floatValue7, floatValue2 + floatValue8};
   }

   public void m_4(boolean flag) {
      float floatValue = this.animation.update(flag ? 1.0F : 0.0F);
      if (!(floatValue < 0.01F)) {
         Minecraft mc = Minecraft.getInstance();
         float floatValue2 = mc.getWindow().getGuiScaledWidth();
         float floatValue3 = mc.getWindow().getGuiScaledHeight();
         int value = ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(120.0F * floatValue));
         Draw2D.rect(floatValue2 / 2.0F - 0.5F, 0.0F, 1.0F, floatValue3, value);
         Draw2D.rect(0.0F, floatValue3 / 2.0F - 0.5F, floatValue2, 1.0F, value);
      }
   }

   public void m_0_void() {
      this.m_3(this.c3, true);
      this.m_3(this.c32, false);
   }

   public void m_4_void() {
      this.c3.flag = false;
      this.c32.flag = false;
   }

   public void m_6_void() {
      this.m_4_void();
      this.animation.setDuration(0.0F);
      this.c3.animation.setDuration(0.0F);
      this.c32.animation.setDuration(0.0F);
   }

   private void m_3(MouseUtil.StateHolder c33, boolean flag) {
      float floatValue = c33.animation.update(c33.flag ? 1.0F : 0.0F);
      if (!(floatValue < 0.01F)) {
         Minecraft mc = Minecraft.getInstance();
         int value = ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(220.0F * floatValue));
         float floatValue2 = 1.0F;
         if (flag) {
            Draw2D.rect(c33.floatValue - floatValue2 / 2.0F, 0.0F, floatValue2, mc.getWindow().getGuiScaledHeight(), value);
         } else {
            Draw2D.rect(0.0F, c33.floatValue - floatValue2 / 2.0F, mc.getWindow().getGuiScaledWidth(), floatValue2, value);
         }
      }
   }

   private static boolean isAltDown() {
      Window window = Minecraft.getInstance().getWindow();
      return InputConstants.isKeyDown(window, 342) || InputConstants.isKeyDown(window, 346);
   }

   private static final class StateHolder {
      public float floatValue;
      public boolean flag;
      public final Animation animation;

      private StateHolder() {
         this.animation = new Animation(130L, 0.0F, Easing.EASING8);
      }
   }
}
