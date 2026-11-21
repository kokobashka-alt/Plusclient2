package wtf.onetap.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Render2D;

public abstract class BaseScreen extends Screen {
   private final Animation animation = new Animation(300L, 0.0F, Easing.EASING8);
   private final float floatValue;
   private final float floatValue2;
   private float floatValue3 = 1.0F;
   private float floatValue4;
   private float floatValue5;
   private int value;
   private int value2;
   private boolean flag;
   private static int[] valueArray;

   public BaseScreen(String text, float floatValue6, float floatValue7) {
      super(Component.literal(text));
      this.floatValue = floatValue6;
      this.floatValue2 = floatValue7;
      Onetap.EVENT_BUS.register(new BaseScreen.StateHolder(this));
   }

   public void m_0_void() {
      this.flag = false;
      this.animation.setDuration(0.0F);
      this.animation.setTarget(1.0F);
   }

   public abstract void m_0(GuiGraphicsExtractor guiGraphicsExtractor, float floatValue6, float floatValue7, float floatValue8);

   @Override
   public void extractBackground(GuiGraphicsExtractor guiGraphicsExtractor, int value3, int value4, float floatValue6) {
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int value3, int value4, float floatValue6) {
      super.extractRenderState(guiGraphicsExtractor, value3, value4, floatValue6);
      this.value = value3;
      this.value2 = value4;
   }

   public abstract int m_1_int();

   public boolean m_7_boolean(int value3) {
      int value4 = this.m_1_int();
      if (value4 != -1 && value3 == value4) {
         this.onClose();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      return this.m_7_boolean(event.key()) || super.keyPressed(event);
   }

   @Override
   public void onClose() {
      this.flag = true;
      this.animation.setTarget(0.0F);
      super.onClose();
   }

   private void m_8(GuiGraphicsExtractor guiGraphicsExtractor) {
      boolean flag2 = super.minecraft != null && super.minecraft.gui.screen() == this;
      if (!flag2 && !this.flag) {
         this.animation.setDuration(0.0F);
      }

      float floatValue6 = this.animation.get();
      if (!(floatValue6 < 0.01F)) {
         float floatValue7 = flag2 ? this.value : -10000.0F;
         float floatValue8 = flag2 ? this.value2 : -10000.0F;
         Render2D.setTransform(1.0F, 0.0F, 0.0F);
         Draw2D.roundedRect(0.0F, 0.0F, super.width, super.height, 0.0F, ColorUtil.rgba(0, 0, 0, (int)(110.0F * floatValue6)));
         float floatValue9 = Math.min(1.0F, Math.min((super.width - 16) / this.floatValue, (super.height - 50) / this.floatValue2));
         this.floatValue3 = floatValue9 * (0.9F + 0.1F * floatValue6);
         this.floatValue4 = (super.width - this.floatValue * this.floatValue3) / 2.0F;
         this.floatValue5 = (super.height - 42 - this.floatValue2 * this.floatValue3) / 2.0F;
         Render2D.setTransform(this.floatValue3, this.floatValue4, this.floatValue5);

         try {
            this.m_0(guiGraphicsExtractor, this.m_4_float(floatValue7), this.m_7_float(floatValue8), floatValue6);
         } finally {
            Render2D.setTransform(1.0F, 0.0F, 0.0F);
         }
      }
   }

   public float m_4_float(double doubleValue) {
      return (float)((doubleValue - this.floatValue4) / this.floatValue3);
   }

   public float m_7_float(double doubleValue) {
      return (float)((doubleValue - this.floatValue5) / this.floatValue3);
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   @Override
   public boolean isInGameUi() {
      return true;
   }

   public static void m_1(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_6_IArr() {
      return valueArray;
   }

   static {
      m_1(new int[4]);
   }

   private static final class StateHolder {
      private final BaseScreen baseScreen;

      private StateHolder(BaseScreen baseScreen2) {
         this.baseScreen = baseScreen2;
      }

      @EventTarget(priority = EventPriority.LOWEST)
      private void onRender2D(Render2DEvent event) {
         this.baseScreen.m_8(event.getGuiGraphicsExtractor());
      }
   }
}
