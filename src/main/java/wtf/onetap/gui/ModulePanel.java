package wtf.onetap.gui;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.gui.setting.BooleanSettingWidget;
import wtf.onetap.gui.setting.ButtonSettingWidget;
import wtf.onetap.gui.setting.KeybindWidget;
import wtf.onetap.gui.setting.LabelWidget;
import wtf.onetap.gui.setting.ModeSettingWidget;
import wtf.onetap.gui.setting.SettingWidget;
import wtf.onetap.gui.setting.SliderWidget;
import wtf.onetap.module.Module;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ButtonSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.setting.LabelSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.setting.Setting;
import wtf.onetap.setting.TextSetting;
import wtf.onetap.util.KeyboardUtil;

public class ModulePanel {
   private Module module;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private boolean flag;
   private boolean flag2;
   private float floatValue5;
   private float floatValue6;
   private boolean flag3;
   private float floatValue7;
   private final Animation animation;
   private final Animation animation2;
   private final Animation animation3;
   private final Animation animation4;
   private final Map<Setting<?>, ButtonSettingWidget> settingMap;
   private final Map<Setting<?>, BooleanSettingWidget> settingMap2;
   private final Map<Setting<?>, SliderWidget> settingMap3;
   private final Map<Setting<?>, ModeSettingWidget> settingMap4;
   private final Map<Setting<?>, C6p> settingMap5;
   private final Map<Setting<?>, KeybindWidget> settingMap6;
   private final Map<Setting<?>, ColorPicker> settingMap7;
   private final Map<Setting<?>, KeyboardUtil> settingMap8;
   private final Map<Setting<?>, LabelWidget> settingMap9;
   private final Map<Setting<?>, Animation> settingMap10;
   private SettingWidget settingWidget;
   private KeyboardUtil keyboardUtil;

   public ModulePanel() {
      this.animation = new Animation(300L, 0.0F, Easing.EASING8);
      this.animation2 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation3 = new Animation(180L, 0.0F, Easing.EASING8);
      this.animation4 = new Animation(180L, 0.0F, Easing.EASING8);
      this.settingMap = new HashMap<>();
      this.settingMap2 = new HashMap<>();
      this.settingMap3 = new HashMap<>();
      this.settingMap4 = new HashMap<>();
      this.settingMap5 = new HashMap<>();
      this.settingMap6 = new HashMap<>();
      this.settingMap7 = new HashMap<>();
      this.settingMap8 = new HashMap<>();
      this.settingMap9 = new HashMap<>();
      this.settingMap10 = new HashMap<>();
   }

   public void m_4(Module module2, float floatValue8, float floatValue9) {
      if (!this.flag || this.module != module2) {
         this.closeWidgets();
         this.module = module2;
         this.floatValue = floatValue8;
         this.floatValue2 = floatValue9;
         this.floatValue5 = 0.0F;
         this.floatValue6 = 0.0F;
         this.animation3.setDuration(0.0F);
         this.animation4.setDuration(0.0F);
         this.flag3 = false;
         this.flag = true;
         this.animation.setDuration(0.0F);
         this.m_2_void(true);
      }
   }

   public void m_3_void(float floatValue8, float floatValue9) {
      float floatValue10 = this.getPanelHeight();
      if (this.floatValue2 + floatValue10 > floatValue9) {
         this.floatValue2 = floatValue9 - floatValue10;
      }

      if (this.floatValue2 < floatValue8) {
         this.floatValue2 = floatValue8;
      }
   }

   public void closePanel() {
      this.flag = false;
      this.m_2_void(false);
   }

   public void m_2_void(boolean flag4) {
      this.animation.setTarget(this.flag && flag4 ? 1.0F : 0.0F);
      if (!flag4) {
         this.closeWidgets();
         this.resetWidgets();
      }
   }

   private void closeWidgets() {
      if (this.settingWidget != null) {
         this.settingWidget.deactivate();
         this.settingWidget = null;
      }

      if (this.keyboardUtil != null) {
         this.keyboardUtil.clear();
         this.keyboardUtil = null;
      }
   }

   private void resetWidgets() {
      this.flag2 = false;
      this.flag3 = false;

      for (SliderWidget sliderWidget : this.settingMap3.values()) {
         sliderWidget.m_8_void();
      }

      for (ColorPicker colorPicker : this.settingMap7.values()) {
         colorPicker.reset();
      }
   }

   public boolean isFlag() {
      return this.flag;
   }

   public boolean m_9_boolean(float floatValue8, float floatValue9) {
      if (!this.flag) {
         return false;
      }

      float floatValue10 = this.animation.get();
      float floatValue11 = 0.9F + 0.1F * floatValue10;
      float floatValue12 = this.getPanelHeight();
      float floatValue13 = this.floatValue + 100.0F;
      float floatValue14 = this.floatValue2 + floatValue12 / 2.0F;
      float floatValue15 = 200.0F * floatValue11 / 2.0F;
      float floatValue16 = floatValue12 * floatValue11 / 2.0F;
      return floatValue8 >= floatValue13 - floatValue15 && floatValue8 <= floatValue13 + floatValue15 && floatValue9 >= floatValue14 - floatValue16 && floatValue9 <= floatValue14 + floatValue16;
   }

   private float getContentHeight() {
      if (this.module == null) {
         return 0.0F;
      }

      float floatValue8 = 28.0F;
      float floatValue9 = 188.0F;

      for (Setting setting : this.module.getSettings()) {
         float floatValue10 = 24.0F;
         if (setting instanceof ModeSetting modeSetting) {
            floatValue10 = this.settingMap4.computeIfAbsent(modeSetting, setting2 -> new ModeSettingWidget((ModeSetting)setting2)).m_2_float(floatValue9);
         } else if (setting instanceof MultiModeSetting multiModeSetting) {
            floatValue10 = this.settingMap5.computeIfAbsent(multiModeSetting, setting3 -> new C6p((MultiModeSetting)setting3)).m_2_float(floatValue9);
         } else if (setting instanceof ColorSetting) {
            ColorPicker colorPicker = this.settingMap7.get(setting);
            if (colorPicker != null) {
               floatValue10 = colorPicker.getPanelHeight();
            }
         }

         Animation animation5 = this.settingMap10.get(setting);
         float floatValue11 = animation5 != null ? animation5.update(setting.isVisible() ? 1.0F : 0.0F) : (setting.isVisible() ? 1 : 0);
         floatValue8 += (floatValue10 - 3.0F) * floatValue11;
      }

      return Math.max(floatValue8 + 6.0F, 50.0F);
   }

   private float getPanelHeight() {
      return Math.min(this.getContentHeight(), 350.0F);
   }

   private float m_2_float() {
      return Math.max(0.0F, this.getContentHeight() - this.getPanelHeight());
   }

   private float m_5_float() {
      return this.floatValue2 + 28.0F + 2.0F;
   }

   private float m_4_float() {
      return Math.max(0.0F, this.getPanelHeight() - 28.0F - 6.0F - 2.0F);
   }

   private float m_1_float(float floatValue8, float floatValue9) {
      return !(floatValue9 <= 0.0F) && !(floatValue8 <= 0.0F) ? Math.min(floatValue8, Math.max(15.0F, floatValue8 * floatValue8 / (floatValue8 + floatValue9))) : floatValue8;
   }

   private float m_8_float(float floatValue8, float floatValue9, float floatValue10, float floatValue11) {
      return !(floatValue11 <= 0.0F) && !(floatValue9 <= floatValue10) ? floatValue8 + this.floatValue5 / floatValue11 * (floatValue9 - floatValue10) : floatValue8;
   }

   public boolean m_2(float floatValue8, float floatValue9, double doubleValue) {
      if (!this.flag || this.module == null) {
         return false;
      }

      if (!this.m_9_boolean(floatValue8, floatValue9)) {
         return false;
      }

      float floatValue10 = this.m_2_float();
      if (floatValue10 <= 0.0F) {
         return true;
      }

      this.floatValue6 = Math.max(0.0F, Math.min(floatValue10, this.floatValue6 - (float)doubleValue * 25.0F));
      return true;
   }

   public boolean hasActiveWidget() {
      return this.settingWidget != null;
   }

   public boolean isEditing() {
      return this.settingWidget != null || this.keyboardUtil != null;
   }

   public void onKey(int value) {
      if (this.keyboardUtil != null) {
         this.keyboardUtil.onKeyPressed(value);
         if (!this.keyboardUtil.isFlag2()) {
            this.keyboardUtil = null;
         }
      } else if (this.settingWidget != null) {
         if (value == 256) {
            this.settingWidget.deactivate();
            this.settingWidget = null;
         } else if (value != 259 && value != 261) {
            this.settingWidget.onKeyPress(value);
            this.settingWidget = null;
         } else {
            this.settingWidget.onKeyPress(-1);
            this.settingWidget = null;
         }
      }
   }

   public void m_7_void(int value) {
      if (this.settingWidget != null) {
         if (value >= 2) {
            this.settingWidget.bindKey(value);
            this.settingWidget = null;
         }
      }
   }

   public void typeChar(int value) {
      if (this.keyboardUtil != null) {
         this.keyboardUtil.typeChar(value);
      }
   }

   public void refresh() {
      if (this.flag && this.module != null) {
         this.resetWidgets();
      }
   }

   public boolean m_2(float floatValue8, float floatValue9, int value) {
      if (this.flag && this.module != null) {
         float floatValue10 = this.animation.get();
         float floatValue11 = 0.9F + 0.1F * floatValue10;
         float floatValue12 = this.getPanelHeight();
         float floatValue13 = this.floatValue + 100.0F;
         float floatValue14 = this.floatValue2 + floatValue12 / 2.0F;
         float floatValue15 = floatValue13 + (floatValue8 - floatValue13) / floatValue11;
         float floatValue16 = floatValue14 + (floatValue9 - floatValue14) / floatValue11;
         if (this.keyboardUtil != null) {
            this.keyboardUtil.clear();
            this.keyboardUtil = null;
         }

         if (this.m_6_boolean(floatValue15, floatValue16)) {
            this.closePanel();
            return true;
         }

         float floatValue17 = this.m_2_float();
         if (value == 0 && floatValue17 > 0.0F) {
            float floatValue18 = this.m_5_float();
            float floatValue19 = this.m_4_float();
            float floatValue20 = this.m_1_float(floatValue19, floatValue17);
            float floatValue21 = this.m_8_float(floatValue18, floatValue19, floatValue20, floatValue17);
            float floatValue22 = this.floatValue + 200.0F - 6.0F;
            if (floatValue15 >= floatValue22 - 3.0F && floatValue15 <= floatValue22 + 4.0F && floatValue16 >= floatValue18 && floatValue16 <= floatValue18 + floatValue19) {
               this.flag3 = true;
               this.floatValue7 = floatValue16 - floatValue21;
               if (floatValue16 < floatValue21 || floatValue16 > floatValue21 + floatValue20) {
                  float floatValue23 = floatValue16 - floatValue18 - floatValue20 / 2.0F;
                  this.floatValue6 = Math.max(0.0F, Math.min(floatValue17, floatValue23 / Math.max(1.0F, floatValue19 - floatValue20) * floatValue17));
                  this.floatValue5 = this.floatValue6;
               }

               return true;
            }
         }

         if (value == 0 && this.m_7_boolean(floatValue15, floatValue16)) {
            this.flag2 = true;
            this.floatValue3 = floatValue15 - this.floatValue;
            this.floatValue4 = floatValue16 - this.floatValue2;
            return true;
         }

         if (value != 0 && value != 2) {
            return true;
         }

         float floatValue24 = this.floatValue2 + 28.0F;
         float floatValue25 = this.floatValue2 + floatValue12 - 6.0F;
         float floatValue26 = floatValue24 - this.floatValue5;
         float floatValue27 = 188.0F;

         for (Setting setting : this.module.getSettings()) {
            Animation animation5 = this.settingMap10.computeIfAbsent(setting, setting2 -> new Animation(200L, setting.isVisible() ? 1.0F : 0.0F, Easing.EASING8));
            float floatValue28 = animation5.update(setting.isVisible() ? 1.0F : 0.0F);
            if (!(floatValue28 < 0.01F)) {
               float floatValue29 = 24.0F;
               if (setting instanceof ModeSetting modeSetting) {
                  floatValue29 = this.settingMap4.computeIfAbsent(modeSetting, setting3 -> new ModeSettingWidget((ModeSetting)setting3)).m_2_float(floatValue27);
               } else if (setting instanceof MultiModeSetting multiModeSetting) {
                  floatValue29 = this.settingMap5.computeIfAbsent(multiModeSetting, setting4 -> new C6p((MultiModeSetting)setting4)).m_2_float(floatValue27);
               } else if (setting instanceof ColorSetting colorSetting) {
                  ColorPicker colorPicker = this.settingMap7.get(colorSetting);
                  if (colorPicker != null) {
                     floatValue29 = colorPicker.getPanelHeight();
                  }
               }

               float floatValue30 = floatValue26;
               float floatValue31 = floatValue26 + (floatValue29 - 3.0F) * floatValue28;
               boolean flag4 = floatValue31 < floatValue24 || floatValue30 > floatValue25;
               if (!flag4 && floatValue16 >= floatValue24 && floatValue16 <= floatValue25 && (value == 0 || setting instanceof BooleanSetting)) {
                  if (setting instanceof ButtonSetting buttonSetting) {
                     ButtonSettingWidget buttonSettingWidget = this.settingMap.computeIfAbsent(buttonSetting, setting5 -> new ButtonSettingWidget((ButtonSetting)setting5));
                     if (buttonSettingWidget.m_8(floatValue15, floatValue16, this.floatValue + 6.0F, floatValue26, floatValue27)) {
                        return true;
                     }
                  } else if (setting instanceof BooleanSetting booleanSetting) {
                     BooleanSettingWidget booleanSettingWidget = this.settingMap2.computeIfAbsent(booleanSetting, setting6 -> new BooleanSettingWidget((BooleanSetting)setting6));
                     if (booleanSettingWidget.m_5_boolean(floatValue15, floatValue16, this.floatValue + 6.0F, floatValue26, floatValue27, value)) {
                        if (booleanSettingWidget.isFlag()) {
                           this.settingWidget = booleanSettingWidget;
                        }

                        return true;
                     }
                  } else if (setting instanceof FloatSetting floatSetting) {
                     SliderWidget sliderWidget = this.settingMap3.computeIfAbsent(floatSetting, setting7 -> new SliderWidget((FloatSetting)setting7));
                     if (sliderWidget.m_1_boolean(floatValue15, floatValue16, this.floatValue + 6.0F, floatValue26, floatValue27)) {
                        return true;
                     }
                  } else if (setting instanceof TextSetting textSetting) {
                     KeyboardUtil keyboardUtil2 = this.settingMap8.computeIfAbsent(textSetting, setting8 -> new KeyboardUtil((TextSetting)setting8));
                     if (keyboardUtil2.m_6(floatValue15, floatValue16, this.floatValue + 6.0F, floatValue26, floatValue27)) {
                        this.keyboardUtil = keyboardUtil2;
                        return true;
                     }
                  } else if (setting instanceof ModeSetting modeSetting2) {
                     ModeSettingWidget modeSettingWidget = this.settingMap4.computeIfAbsent(modeSetting2, setting9 -> new ModeSettingWidget((ModeSetting)setting9));
                     if (modeSettingWidget.m_4_boolean(floatValue15, floatValue16, this.floatValue + 6.0F, floatValue26, floatValue27)) {
                        return true;
                     }
                  } else if (setting instanceof MultiModeSetting multiModeSetting2) {
                     C6p c6p = this.settingMap5.computeIfAbsent(multiModeSetting2, setting10 -> new C6p((MultiModeSetting)setting10));
                     if (c6p.m_3_boolean(floatValue15, floatValue16, this.floatValue + 6.0F, floatValue26, floatValue27)) {
                        return true;
                     }
                  } else if (setting instanceof KeybindSetting keybindSetting) {
                     KeybindWidget keybindWidget = this.settingMap6.computeIfAbsent(keybindSetting, setting11 -> new KeybindWidget((KeybindSetting)setting11));
                     if (keybindWidget.m_2(floatValue15, floatValue16, this.floatValue + 6.0F, floatValue26, floatValue27)) {
                        this.settingWidget = keybindWidget;
                        return true;
                     }
                  } else if (setting instanceof ColorSetting colorSetting2) {
                     ColorPicker colorPicker2 = this.settingMap7.computeIfAbsent(colorSetting2, setting12 -> new ColorPicker((ColorSetting)setting12));
                     if (colorPicker2.m_6(floatValue15, floatValue16, this.floatValue + 6.0F, floatValue26, floatValue27)) {
                        return true;
                     }
                  }
               }

               floatValue26 = floatValue31;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void m_3_void() {
      this.m_9_void();
      ClickGUI.m_3_String();
      float floatValue8 = this.animation.get();
      if (!(floatValue8 < 0.01F) && this.module != null) {
         float floatValue9 = this.m_2_float();
         if (this.floatValue6 > floatValue9) {
            this.floatValue6 = floatValue9;
         }

         if (this.floatValue6 < 0.0F) {
            this.floatValue6 = 0.0F;
         }

         this.floatValue5 = this.floatValue5 + (this.floatValue6 - this.floatValue5) * 0.2F;
         if (Math.abs(this.floatValue5 - this.floatValue6) < 0.1F) {
            this.floatValue5 = this.floatValue6;
         }

         floatValue9 = this.m_2_float();
         float floatValue10 = this.getPanelHeight();
         float floatValue11 = 0.9F + 0.1F * floatValue8;
         float floatValue12 = this.animation3.update(floatValue9 > 0.0F && this.floatValue5 > 0.5F ? 1.0F : 0.0F);
         float floatValue13 = this.animation4.update(floatValue9 > 0.0F && floatValue9 - this.floatValue5 > 0.5F ? 1.0F : 0.0F);
         float floatValue14 = Render2D.getScale();
         float floatValue15 = Render2D.getOffsetX();
         float floatValue16 = Render2D.getOffsetY();
         float floatValue17 = this.floatValue + 100.0F;
         float floatValue18 = this.floatValue2 + floatValue10 / 2.0F;
         Render2D.setTransform(floatValue14 * floatValue11, floatValue15 + floatValue14 * (floatValue17 - floatValue17 * floatValue11), floatValue16 + floatValue14 * (floatValue18 - floatValue18 * floatValue11));

         try {
            Draw2D.m_5(this.floatValue, this.floatValue2, 200.0F, floatValue10, 8.0F, 2.0F * floatValue8, ColorUtil.rgba(155, 155, 155, (int)(255.0F * floatValue8)), ClickGUI.getGlow() * floatValue8);
            Draw2D.m_1(this.floatValue, this.floatValue2, 200.0F, floatValue10, 8.0F, 4.0F, ColorUtil.rgba(0, 0, 0, (int)(45.0F * floatValue8)));
            Draw2D.roundedRect(this.floatValue, this.floatValue2, 200.0F, floatValue10, 8.0F, ColorUtil.rgba(12, 12, 22, (int)(45.0F * floatValue8)));
            float floatValue19 = this.floatValue2 + 28.0F;
            float floatValue20 = floatValue10 - 28.0F - 6.0F;
            float floatValue21 = floatValue19 + floatValue20;
            float floatValue22 = floatValue20 > 0.0F ? Math.min(28.0F, floatValue20 * 0.45F) : 0.0F;
            float floatValue23 = floatValue19 - this.floatValue5;
            float floatValue24 = 188.0F;
            Render2D.pushScissor(this.floatValue, this.floatValue2, 200.0F, floatValue10);

            for (Setting setting : this.module.getSettings()) {
               Animation animation5 = this.settingMap10.computeIfAbsent(setting, setting2 -> new Animation(200L, setting.isVisible() ? 1.0F : 0.0F, Easing.EASING8));
               float floatValue25 = animation5.update(setting.isVisible() ? 1.0F : 0.0F);
               if (!(floatValue25 < 0.01F)) {
                  float floatValue26 = 24.0F;
                  if (setting instanceof ModeSetting modeSetting) {
                     ModeSettingWidget modeSettingWidget = this.settingMap4.computeIfAbsent(modeSetting, setting3 -> new ModeSettingWidget((ModeSetting)setting3));
                     floatValue26 = modeSettingWidget.m_2_float(floatValue24);
                  } else if (setting instanceof MultiModeSetting multiModeSetting) {
                     C6p c6p = this.settingMap5.computeIfAbsent(multiModeSetting, setting4 -> new C6p((MultiModeSetting)setting4));
                     floatValue26 = c6p.m_2_float(floatValue24);
                  } else if (setting instanceof ColorSetting colorSetting) {
                     ColorPicker colorPicker = this.settingMap7.get(colorSetting);
                     if (colorPicker != null) {
                        floatValue26 = colorPicker.getPanelHeight();
                     }
                  }

                  float floatValue27 = floatValue23;
                  float floatValue28 = floatValue23 + (floatValue26 - 3.0F) * floatValue25;
                  boolean flag4 = floatValue28 < this.floatValue2 || floatValue27 > this.floatValue2 + floatValue10;
                  if (!flag4) {
                     if (setting instanceof ButtonSetting buttonSetting) {
                        this.settingMap.computeIfAbsent(buttonSetting, setting5 -> new ButtonSettingWidget((ButtonSetting)setting5)).m_4_void(this.floatValue + 6.0F, floatValue23, floatValue24, floatValue8 * floatValue25);
                     } else if (setting instanceof BooleanSetting booleanSetting) {
                        this.settingMap2.computeIfAbsent(booleanSetting, setting6 -> new BooleanSettingWidget((BooleanSetting)setting6)).m_1_void(this.floatValue + 6.0F, floatValue23, floatValue24, floatValue8 * floatValue25);
                     } else if (setting instanceof FloatSetting floatSetting) {
                        this.settingMap3.computeIfAbsent(floatSetting, setting7 -> new SliderWidget((FloatSetting)setting7)).m_5(this.floatValue + 6.0F, floatValue23, floatValue24, floatValue8 * floatValue25);
                     } else if (setting instanceof TextSetting textSetting) {
                        this.settingMap8.computeIfAbsent(textSetting, setting8 -> new KeyboardUtil((TextSetting)setting8)).m_7_void(this.floatValue + 6.0F, floatValue23, floatValue24, floatValue8 * floatValue25);
                     } else if (setting instanceof LabelSetting labelSetting) {
                        this.settingMap9.computeIfAbsent(labelSetting, setting9 -> new LabelWidget((LabelSetting)setting9)).m_1_void(this.floatValue + 6.0F, floatValue23, floatValue24, floatValue8 * floatValue25);
                     } else if (setting instanceof ModeSetting modeSetting2) {
                        ModeSettingWidget modeSettingWidget2 = this.settingMap4.computeIfAbsent(modeSetting2, setting10 -> new ModeSettingWidget((ModeSetting)setting10));
                        modeSettingWidget2.m_2(this.floatValue + 6.0F, floatValue23, floatValue24, floatValue8 * floatValue25);
                     } else if (setting instanceof MultiModeSetting multiModeSetting2) {
                        C6p c6p2 = this.settingMap5.computeIfAbsent(multiModeSetting2, setting11 -> new C6p((MultiModeSetting)setting11));
                        c6p2.m_4_void(this.floatValue + 6.0F, floatValue23, floatValue24, floatValue8 * floatValue25);
                     } else if (setting instanceof KeybindSetting keybindSetting) {
                        this.settingMap6.computeIfAbsent(keybindSetting, setting12 -> new KeybindWidget((KeybindSetting)setting12)).m_2(this.floatValue + 6.0F, floatValue23, floatValue24, floatValue8 * floatValue25);
                     } else if (setting instanceof ColorSetting colorSetting2) {
                        ColorPicker colorPicker2 = this.settingMap7.computeIfAbsent(colorSetting2, setting13 -> new ColorPicker((ColorSetting)setting13));
                        colorPicker2.m_4_void(this.floatValue + 6.0F, floatValue23, floatValue24, floatValue8 * floatValue25);
                     }
                  }

                  floatValue23 = floatValue28;
               }
            }

            if (floatValue22 > 0.0F) {
               int value = ColorUtil.rgba(12, 12, 22, 0);
               if (floatValue12 > 0.01F) {
                  int value2 = ColorUtil.rgba(12, 12, 22, (int)(175.0F * floatValue12 * floatValue8));
                  Render2D.m_1(this.floatValue, this.floatValue2, 200.0F, 28.0F + floatValue22, 8.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.0F, value2, value, value, value2);
               }

               if (floatValue13 > 0.01F) {
                  int value3 = ColorUtil.rgba(12, 12, 22, (int)(175.0F * floatValue13 * floatValue8));
                  Render2D.m_1(this.floatValue, floatValue21 - floatValue22, 200.0F, floatValue22 + 6.0F, 0.0F, 0.0F, 8.0F, 8.0F, 0.0F, 0.0F, value, value3, value3, value);
               }
            }

            Render2D.popScissor();
            Draw2D.drawStyledText(
               this.module.getName(), Fonts.SUPPLIER16, this.floatValue + 12.0F, this.floatValue2 + 8.0F, ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), (int)(222.0F * floatValue8)), 13.0F
            );
            Minecraft mc = Minecraft.getInstance();
            double doubleValue = mc.getWindow().getGuiScale();
            float floatValue29 = (float)(mc.mouseHandler.xpos() / doubleValue - Render2D.getOffsetX()) / Render2D.getScale();
            float floatValue30 = (float)(mc.mouseHandler.ypos() / doubleValue - Render2D.getOffsetY()) / Render2D.getScale();
            float floatValue31 = this.animation2.update(this.m_6_boolean(floatValue29, floatValue30) ? 1.0F : 0.0F);
            float floatValue32 = 14.0F + floatValue31 * 2.0F;
            float floatValue33 = Draw2D.getTextWidth("\uf00d", Fonts.SUPPLIER8, floatValue32);
            int value4 = (int)(190.0F + 45.0F * floatValue31);
            Draw2D.drawStyledText(
               "\uf00d",
               Fonts.SUPPLIER8,
               this.floatValue + 200.0F - 17.0F - floatValue33 / 2.0F,
               this.floatValue2 + (33.0F - floatValue32) / 2.0F,
               ColorUtil.rgba(value4, value4, value4, (int)(220.0F * floatValue8)),
               floatValue32
            );
            if (floatValue9 > 0.0F && floatValue20 > 0.0F) {
               float floatValue34 = this.m_5_float();
               float floatValue35 = this.m_4_float();
               float floatValue36 = this.m_1_float(floatValue35, floatValue9);
               float floatValue37 = this.m_8_float(floatValue34, floatValue35, floatValue36, floatValue9);
               float floatValue38 = this.floatValue + 200.0F - 6.0F;
               Draw2D.roundedRect(floatValue38, floatValue34, 3.0F, floatValue35, 1.5F, ColorUtil.rgba(0, 0, 0, (int)(30.0F * floatValue8)));
               Draw2D.roundedRect(floatValue38, floatValue37, 3.0F, floatValue36, 1.5F, ColorUtil.rgba(160, 160, 160, (int)(180.0F * floatValue8)));
            }
         } finally {
            Render2D.setTransform(floatValue14, floatValue15, floatValue16);
         }
      }
   }

   private boolean m_6_boolean(float floatValue8, float floatValue9) {
      return floatValue8 >= this.floatValue + 200.0F - 28.0F && floatValue8 <= this.floatValue + 200.0F - 6.0F && floatValue9 >= this.floatValue2 + 4.0F && floatValue9 <= this.floatValue2 + 26.0F;
   }

   private boolean m_7_boolean(float floatValue8, float floatValue9) {
      return floatValue8 >= this.floatValue && floatValue8 <= this.floatValue + 200.0F && floatValue9 >= this.floatValue2 && floatValue9 <= this.floatValue2 + 28.0F;
   }

   private void m_9_void() {
      Minecraft mc = Minecraft.getInstance();
      double doubleValue = mc.getWindow().getGuiScale();
      if (this.flag3) {
         float floatValue8 = this.m_2_float();
         if (floatValue8 > 0.0F) {
            float floatValue9 = (float)((mc.mouseHandler.ypos() / doubleValue - Render2D.getOffsetY()) / Render2D.getScale());
            float floatValue10 = this.m_5_float();
            float floatValue11 = this.m_4_float();
            float floatValue12 = this.m_1_float(floatValue11, floatValue8);
            float floatValue13 = Math.max(1.0F, floatValue11 - floatValue12);
            this.floatValue6 = Math.max(0.0F, Math.min(floatValue8, (floatValue9 - this.floatValue7 - floatValue10) / floatValue13 * floatValue8));
            this.floatValue5 = this.floatValue6;
         } else {
            this.flag3 = false;
         }
      }

      if (this.flag2) {
         this.floatValue = (float)((mc.mouseHandler.xpos() / doubleValue - Render2D.getOffsetX()) / Render2D.getScale() - this.floatValue3);
         this.floatValue2 = (float)((mc.mouseHandler.ypos() / doubleValue - Render2D.getOffsetY()) / Render2D.getScale() - this.floatValue4);
      }
   }
}
