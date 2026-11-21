package wtf.onetap.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import org.figuramc.figura.lua.api.keybind.FiguraKeybind;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.ScreenOpenEvent;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.util.KeyNames;

public class FiguraPanel {
   private final Minecraft minecraft;
   private final Animation animation;
   private final List<Animation> animationList;
   private List<FiguraKeybind> figuraKeybindList;
   private boolean flag;
   private boolean flag2;
   private static final FiguraPanel.DataRecord C1 = new FiguraPanel.DataRecord("\uf1b0", ColorUtil.rgba(255, 190, 90, 255), "Хвостик");
   private static final FiguraPanel.DataRecord C12 = new FiguraPanel.DataRecord("\uf5f0", ColorUtil.rgba(255, 210, 140, 255), "Ушки");
   private static final FiguraPanel.DataRecord C13 = new FiguraPanel.DataRecord("\uf5c8", ColorUtil.rgba(160, 175, 255, 255), "Усталость");
   private static final FiguraPanel.DataRecord C14 = new FiguraPanel.DataRecord("\uf5b8", ColorUtil.rgba(255, 210, 90, 255), "Довольная");
   private static final FiguraPanel.DataRecord C15 = new FiguraPanel.DataRecord("\uf5c2", ColorUtil.rgba(120, 220, 255, 255), "Удивление");
   private static final FiguraPanel.DataRecord C16 = new FiguraPanel.DataRecord("\uf585", ColorUtil.rgba(255, 140, 170, 255), "Смущение");
   private static final Map<String, FiguraPanel.DataRecord> STRING_MAP = Map.ofEntries(
      Map.entry("Bubble: Good", new FiguraPanel.DataRecord("\uf164", ColorUtil.rgba(255, 200, 70, 255), "Отлично")),
      Map.entry("Bubble: Heart", new FiguraPanel.DataRecord("\uf004", ColorUtil.rgba(255, 90, 115, 255), "Сердечко")),
      Map.entry("Bubble: Note", new FiguraPanel.DataRecord("\uf001", ColorUtil.rgba(125, 170, 255, 255), "Нотка")),
      Map.entry("Bubble: Question", new FiguraPanel.DataRecord("\uf128", ColorUtil.rgba(255, 165, 65, 255), "Вопрос")),
      Map.entry("Bubble: Sweat", new FiguraPanel.DataRecord("\uf043", ColorUtil.rgba(110, 205, 255, 255), "Капелька")),
      Map.entry("Wag tail", C1),
      Map.entry("尻尾ふりふり", C1),
      Map.entry("Jerk ears", C12),
      Map.entry("お耳ぴくぴく", C12),
      Map.entry("Emote (up)", C13),
      Map.entry("えもーと（上）", C13),
      Map.entry("Emote (right)", C14),
      Map.entry("えもーと（右）", C14),
      Map.entry("Emote (down)", C15),
      Map.entry("えもーと（下）", C15),
      Map.entry("Emote (left)", C16),
      Map.entry("えもーと（左）", C16)
   );

   public FiguraPanel() {
      this.minecraft = Minecraft.getInstance();
      this.animation = new Animation(130L, 0.0F, Easing.EASING8);
      this.animationList = new ArrayList<>();
      this.figuraKeybindList = List.of();
   }

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (!ChatInputHelper.isTextInputOpen()) {
         if (this.minecraft.player != null && this.minecraft.gui.screen() == null && this.minecraft.gui.overlay() == null) {
            int value = (Integer)Onetap.MASCOT_MANAGER.getKeybindSetting().getValue();
            if (event.getAction() == 1 && !this.flag && value >= 0 && event.getKey() == value) {
               this.m_8_void();
            }
         }
      }
   }

   @EventTarget
   private void onMouseClick(MouseClickEvent event) {
      if (this.flag) {
         event.cancel();
      } else {
         int value = (Integer)Onetap.MASCOT_MANAGER.getKeybindSetting().getValue();
         if (value < -1 && event.getAction() == 1 && event.getButton() == -value - 1) {
            if (!ChatInputHelper.isTextInputOpen() && this.minecraft.player != null && this.minecraft.gui.screen() == null && this.minecraft.gui.overlay() == null) {
               this.m_8_void();
            }
         }
      }
   }

   @EventTarget
   private void onScreenOpen(ScreenOpenEvent event) {
      this.flag = false;
      this.flag2 = false;
      this.animation.reset(0.0F);
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      if (this.minecraft.player == null) {
         this.flag = false;
         this.flag2 = false;
      } else if (this.flag2) {
         float floatValue = this.animation.update(0.0F);
         if (this.animation.isFinished()) {
            this.flag2 = false;
         } else {
            this.m_6_void(floatValue);
         }
      } else if (this.flag) {
         if (!KeyNames.m_0_boolean((Integer)Onetap.MASCOT_MANAGER.getKeybindSetting().getValue())) {
            int value = this.m_7_int(1.0F);
            if (value != -1) {
               Onetap.MASCOT_MANAGER.m_2(this.figuraKeybindList.get(value));
            }

            this.flag = false;
            this.flag2 = true;
            this.minecraft.mouseHandler.grabMouse();
         } else {
            this.m_6_void(this.animation.update(1.0F));
         }
      }
   }

   private void m_8_void() {
      this.figuraKeybindList = Onetap.MASCOT_MANAGER.m_4_List();
      if (!this.figuraKeybindList.isEmpty()) {
         while (this.animationList.size() < this.figuraKeybindList.size()) {
            this.animationList.add(new Animation(110L, 0.0F, Easing.EASING8));
         }

         for (Animation animation2 : this.animationList) {
            animation2.reset(0.0F);
         }

         this.flag = true;
         this.flag2 = false;
         this.minecraft.mouseHandler.releaseMouse();
      }
   }

   private void m_6_void(float floatValue) {
      float floatValue2 = this.minecraft.getWindow().getGuiScaledWidth() / 2.0F;
      MascotManager.m_2_String();
      float floatValue3 = this.minecraft.getWindow().getGuiScaledHeight() / 2.0F;
      int value = this.m_7_int(floatValue);
      int value2 = ColorUtil.primaryColor();
      int value3 = this.figuraKeybindList.size();
      float floatValue4 = (float)((Math.PI * 2) / value3);
      float floatValue5 = floatValue4 - 0.08F;
      float floatValue6 = 24.0F * floatValue;
      Draw2D.m_1(floatValue2 - floatValue6, floatValue3 - floatValue6, floatValue6 * 2.0F, floatValue6 * 2.0F, floatValue6, 4.0F, ColorUtil.rgba(0, 0, 0, 120));
      Draw2D.m_7(floatValue2 - floatValue6, floatValue3 - floatValue6, floatValue6 * 2.0F, floatValue6 * 2.0F, floatValue6, 3.0F, ColorUtil.rgba(165, 165, 165, 235));
      Draw2D.m_5(floatValue2, floatValue3, floatValue6, 1.2F * floatValue, 1.0F, 0.0F, 1.0F, ColorUtil.rgba(255, 255, 255, 22));

      for (int i = 0; i < value3; i++) {
         double doubleValue = (-Math.PI / 2) + i * floatValue4;
         float floatValue7 = this.animationList.get(i).update(i == value ? 1.0F : 0.0F);
         float floatValue8 = (58.0F + floatValue7 * 6.0F) * floatValue;
         float floatValue9 = (24.0F + floatValue7 * 3.0F) * floatValue;
         Draw2D.m_5(
            floatValue2, floatValue3, floatValue8, floatValue9, floatValue5 / (float) (Math.PI * 2), (float)(doubleValue - floatValue5 / 2.0F), 1.5F, ColorUtil.lerpColor(ColorUtil.rgba(40, 40, 40, 185), value2, floatValue7)
         );
         float floatValue10 = floatValue2 + (float)Math.cos(doubleValue) * floatValue8;
         float floatValue11 = floatValue3 + (float)Math.sin(doubleValue) * floatValue8;
         FiguraPanel.DataRecord c1 = m_5(this.figuraKeybindList.get(i));
         this.m_1(c1.m_3_j(), floatValue10, floatValue11, (12.0F + floatValue7 * 2.0F) * floatValue, ColorUtil.withAlpha(c1.m_0_x(), (int)(200.0F + 55.0F * floatValue7)));
      }

      if (value == -1) {
         this.m_1("\uf186", floatValue2, floatValue3, 14.0F * floatValue, ColorUtil.rgba(150, 150, 150, 170));
      } else {
         FiguraPanel.DataRecord c12 = m_5(this.figuraKeybindList.get(value));
         this.m_1(c12.m_3_j(), floatValue2, floatValue3, 15.0F * floatValue, c12.m_0_x());
         String text = c12.m_30();
         float floatValue12 = 9.0F * floatValue;
         float floatValue13 = floatValue3 + 79.0F * floatValue;
         float floatValue14 = Draw2D.getTextWidth(text, Fonts.SUPPLIER18, floatValue12);
         Draw2D.rect(floatValue2 - (floatValue14 + 6.0F) / 2.0F, floatValue13 - 2.0F, floatValue14 + 6.0F, floatValue12 + 4.0F, ColorUtil.rgba(0, 0, 0, 75));
         Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER18, floatValue2 - floatValue14 / 2.0F, floatValue13, ColorUtil.rgba(230, 230, 230, 255), floatValue12);
      }
   }

   public static boolean isKnownKeybind(FiguraKeybind figuraKeybind) {
      return STRING_MAP.containsKey(ChatFormatting.stripFormatting(figuraKeybind.getName()));
   }

   private static FiguraPanel.DataRecord m_5(FiguraKeybind figuraKeybind) {
      return STRING_MAP.get(ChatFormatting.stripFormatting(figuraKeybind.getName()));
   }

   private void m_1(String text, float floatValue, float floatValue2, float floatValue3, int value) {
      float floatValue4 = floatValue3 * 2.0F;
      float floatValue5 = Draw2D.getCenteredX(text, Fonts.SUPPLIER8, floatValue3, floatValue - floatValue4 / 2.0F, floatValue4);
      float floatValue6 = Draw2D.getCenteredY(text, Fonts.SUPPLIER8, floatValue3, floatValue2 - floatValue4 / 2.0F, floatValue4);
      Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER8, floatValue5, floatValue6, value, floatValue3);
   }

   private int m_7_int(float floatValue) {
      float floatValue2 = this.minecraft.getWindow().getGuiScaledWidth() / 2.0F;
      MascotManager.m_2_String();
      float floatValue3 = this.minecraft.getWindow().getGuiScaledHeight() / 2.0F;
      double doubleValue = this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow()) - floatValue2;
      double doubleValue2 = this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow()) - floatValue3;
      float floatValue4 = 27.0F * floatValue;
      if (doubleValue * doubleValue + doubleValue2 * doubleValue2 < floatValue4 * floatValue4) {
         return -1;
      }

      double doubleValue3 = (Math.PI * 2) / this.figuraKeybindList.size();
      double doubleValue4 = (Math.atan2(doubleValue2, doubleValue) + (Math.PI / 2) + doubleValue3 / 2.0) % (Math.PI * 2);
      if (doubleValue4 < 0.0) {
         doubleValue4 += Math.PI * 2;
      }

      return Math.min(this.figuraKeybindList.size() - 1, (int)(doubleValue4 / doubleValue3));
   }

   private static String a(byte[] byteArray) {
      int value = 0;
      int value2;
      char[] charArray = new char[value2 = byteArray.length];

      for (int i = 0; i < value2; i++) {
         int value3;
         if ((value3 = 255 & byteArray[i]) < 192) {
            charArray[value++] = (char)value3;
         } else if (value3 < 224) {
            char ch = (char)((char)(value3 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value++] = ch;
         } else if (i < value2 - 2) {
            char ch2 = (char)((char)(value3 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value++] = ch2;
         }
      }

      return new String(charArray, 0, value);
   }

   private record DataRecord(String f_3_j, int f_0_x, String f_30) {
      public String m_3_j() {
         return this.f_3_j;
      }

      public int m_0_x() {
         return this.f_0_x;
      }

      public String m_30() {
         return this.f_30;
      }
   }
}
