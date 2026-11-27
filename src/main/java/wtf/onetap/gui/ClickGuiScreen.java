package wtf.onetap.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.annotation.EraseAfterRun;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.CharTypedEvent;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.event.MouseScrollEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.setting.KeybindWidget;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.ThemeManager;

public class ClickGuiScreen {
   private static ClickGuiScreen clickGuiScreen;
   private boolean flag;
   private final Animation animation;
   private final SidebarPanel sidebarPanel;
   private final SearchBox searchBox;
   private int value;
   private int value2;
   private int value3;
   private final Animation animation2;
   private final Animation animation3;
   private SidebarPanel.ModeKind c7;
   private int value4;
   private final Animation animation4;
   private final Animation animation5;
   private final Animation animation6;
   private final Animation animation7;
   private final Map<Object, Animation> objectMap;
   private String text;
   private List<Module> moduleList;
   private List<Module> moduleList2;
   private Map<Module, Integer> moduleMap;
   private Map<Module, Integer> moduleMap2;
   private List<ClickGuiScreen.DataRecord> c7List;
   private List<ClickGuiScreen.DataRecord> c7List2;
   private final Map<Module, ModuleButton> moduleMap3;
   private final ModulePanel modulePanel;
   private final CustomThemePanel customThemePanel;
   private ModuleButton moduleButton;
   private KeybindWidget keybindWidget;
   private float floatValue;
   private float floatValue2;
   private final float[] floatValueArray;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private boolean flag2;
   private float floatValue10;
   private final Animation animation8;
   private final Animation animation9;
   private final Animation animation10;
   private final Animation animation11;
   private final Animation animation12;
   private Module module;
   private String text2;
   private String text3;
   private float floatValue11;
   private float floatValue12;
   private float floatValue13;
   private float floatValue14;
   private float floatValue15;
   private float floatValue16;
   private float floatValue17;
   private float floatValue18;
   private float floatValue19;
   private float floatValue20;
   private static String[] textArray;

   public ClickGuiScreen() {
      this.animation = new Animation(300L, 0.0F, Easing.EASING8);
      this.sidebarPanel = new SidebarPanel();
      this.searchBox = new SearchBox();
      this.animation2 = new Animation(400L, 1.0F, Easing.EASING8);
      this.animation3 = new Animation(150L, 0.0F, Easing.EASING8);
      this.c7 = SidebarPanel.ModeKind.MODULES;
      this.animation4 = new Animation(150L, 1.0F, Easing.EASING8);
      this.animation5 = new Animation(160L, 1.0F, Easing.EASING8);
      this.animation6 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation7 = new Animation(150L, 0.0F, Easing.EASING8);
      this.objectMap = new HashMap<>();
      this.text = "";
      this.moduleList = List.of();
      this.moduleList2 = List.of();
      this.moduleMap = Map.of();
      this.moduleMap2 = Map.of();
      this.c7List = List.of();
      this.c7List2 = List.of();
      this.moduleMap3 = new HashMap<>();
      this.modulePanel = new ModulePanel();
      this.customThemePanel = new CustomThemePanel();
      this.floatValueArray = new float[Category.values().length + 2];
      this.animation8 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation9 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation10 = new Animation(180L, 0.0F, Easing.C02);
      this.animation11 = new Animation(200L, 0.0F, Easing.EASING8);
      this.animation12 = new Animation(120L, 1.0F, Easing.EASING18);
      this.floatValue11 = 1.0F;
      this.floatValue12 = 0.0F;
      this.floatValue13 = 0.0F;
   }

   @EraseAfterRun
   public static void createInstance() {
      clickGuiScreen = new ClickGuiScreen();
      Onetap.EVENT_BUS.register(clickGuiScreen);
   }

   public static void open() {
      if (clickGuiScreen != null) {
         clickGuiScreen.flag = true;
         clickGuiScreen.animation.setTarget(1.0F);
         clickGuiScreen.modulePanel.m_2_void(true);
         clickGuiScreen.customThemePanel.m_1(true);
         Minecraft.getInstance().mouseHandler.releaseMouse();
      }
   }

   public static void close() {
      if (clickGuiScreen != null && clickGuiScreen.flag) {
         clickGuiScreen.flag = false;
         clickGuiScreen.animation.setTarget(0.0F);
         clickGuiScreen.modulePanel.m_2_void(false);
         clickGuiScreen.customThemePanel.close();
         clickGuiScreen.moduleButton = null;
         if (clickGuiScreen.keybindWidget != null) {
            clickGuiScreen.keybindWidget.deactivate();
         }

         clickGuiScreen.flag2 = false;
         clickGuiScreen.searchBox.m_1_void();
         Minecraft.getInstance().mouseHandler.grabMouse();
      }
   }

   public static boolean isOpen() {
      return clickGuiScreen != null && clickGuiScreen.flag;
   }

   public static boolean isEditing() {
      return clickGuiScreen != null && (clickGuiScreen.modulePanel.isEditing() || clickGuiScreen.moduleButton != null || clickGuiScreen.keybindWidget != null && clickGuiScreen.keybindWidget.isFlag() || clickGuiScreen.searchBox.isFlag());
   }

   @EventTarget
   public void onTick(TickEvent event) {
      if (this.flag) {
         MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;
         if (mouseHandler.isMouseGrabbed()) {
            mouseHandler.releaseMouse();
         }
      }
   }

   @EventTarget(priority = EventPriority.HIGHEST)
   public void onKeyPress(KeyPressEvent event) {
      if (this.flag && this.moduleButton != null) {
         if (event.getAction() == 1) {
            event.cancel();
            Module module2 = this.moduleButton.getModule();
            if (event.getKey() == 256) {
               this.moduleButton.m_2_void();
               this.moduleButton = null;
            } else if (event.getKey() != 259 && event.getKey() != 261) {
               module2.setKey(event.getKey());
               this.moduleButton.m_2_void();
               this.moduleButton = null;
            } else {
               module2.setKey(-1);
               this.moduleButton.m_2_void();
               this.moduleButton = null;
            }
         }
      } else if (this.flag && this.keybindWidget != null && this.keybindWidget.isFlag()) {
         if (event.getAction() == 1) {
            event.cancel();
            if (event.getKey() == 256) {
               this.keybindWidget.deactivate();
            } else {
               this.keybindWidget.onKeyPress(event.getKey() != 259 && event.getKey() != 261 ? event.getKey() : -1);
               Onetap.MASCOT_MANAGER.m_7_void();
            }
         }
      } else if (this.flag && this.modulePanel.isEditing()) {
         if (event.getAction() != 0) {
            event.cancel();
            this.modulePanel.onKey(event.getKey());
         }
      } else if (!this.flag || !this.searchBox.isFlag()) {
         if (event.getAction() == 1) {
            if (event.getKey() == 256) {
               ClickGUI.disable();
            }
         }
      } else if (event.getAction() != 0) {
         event.cancel();
         this.searchBox.m_1_void(event.getKey());
      }
   }

   @EventTarget(priority = EventPriority.HIGHEST)
   public void onCharTyped(CharTypedEvent event) {
      if (this.flag && this.modulePanel.isEditing()) {
         event.cancel();
         this.modulePanel.typeChar(event.getCodePoint());
      } else {
         if (this.flag && this.searchBox.isFlag()) {
            event.cancel();
            this.searchBox.m_3_void(event.getCodePoint());
         }
      }
   }

   @EventTarget
   public void onMouseClick(MouseClickEvent event) {
      if (this.flag) {
         float floatValue21 = ((float)event.getX() - this.floatValue12) / this.floatValue11;
         float floatValue22 = ((float)event.getY() - this.floatValue13) / this.floatValue11;
         if (this.moduleButton != null) {
            if (event.getAction() == 1 && event.getButton() >= 2) {
               this.moduleButton.getModule().setKey(-event.getButton() - 1);
               this.moduleButton.m_2_void();
               this.moduleButton = null;
            }
         } else if (this.keybindWidget != null && this.keybindWidget.isFlag()) {
            if (event.getAction() == 1 && event.getButton() >= 2) {
               this.keybindWidget.bindKey(event.getButton());
               Onetap.MASCOT_MANAGER.m_7_void();
            }
         } else if (this.modulePanel.isFlag()) {
            if (this.modulePanel.hasActiveWidget()) {
               if (event.getAction() == 1) {
                  if (event.getButton() >= 2) {
                     this.modulePanel.m_7_void(event.getButton());
                  } else if (!this.modulePanel.m_9_boolean(floatValue21, floatValue22)) {
                     this.modulePanel.closePanel();
                  }
               }
            } else {
               if (event.getAction() == 1) {
                  if (this.modulePanel.m_9_boolean(floatValue21, floatValue22)) {
                     this.modulePanel.m_2(floatValue21, floatValue22, event.getButton());
                  } else {
                     this.modulePanel.closePanel();
                  }
               } else if (event.getAction() == 0) {
                  this.modulePanel.refresh();
               }
            }
         } else if (this.customThemePanel.isFlag()) {
            if (event.getAction() == 1) {
               if (this.customThemePanel.m_7_boolean(floatValue21, floatValue22)) {
                  this.customThemePanel.m_2(floatValue21, floatValue22, event.getButton());
               } else {
                  this.customThemePanel.close();
               }
            } else if (event.getAction() == 0) {
               this.customThemePanel.resetPickers();
            }
         } else if (event.getAction() == 0) {
            if (event.getButton() == 0) {
               this.flag2 = false;
            }
         } else if (event.getAction() == 1) {
            if (event.getButton() == 0
               && this.floatValue4 > 0.0F
               && this.floatValue9 > 0.0F
               && floatValue21 >= this.floatValue6
               && floatValue21 <= this.floatValue6 + 3.0F
               && floatValue22 >= this.floatValue7
               && floatValue22 <= this.floatValue7 + this.floatValue8) {
               this.flag2 = true;
               this.floatValue10 = floatValue22 - this.floatValue7;
            } else {
               if (event.getButton() == 0) {
                  if (this.searchBox.m_6_boolean(floatValue21, floatValue22)) {
                     return;
                  }
               } else {
                  this.searchBox.m_1_void();
               }

               int value5 = this.sidebarPanel.getValue();
               SidebarPanel.ModeKind c72 = this.sidebarPanel.getC7();
               if (event.getButton() == 0 && this.m_6_boolean(floatValue21, floatValue22)) {
                  this.sidebarPanel.m_3_void();
                  this.m_2_void(c72, value5);
               } else if (this.sidebarPanel.m_7(floatValue21, floatValue22, this.floatValue14, this.floatValue15, this.floatValue18 + this.floatValue20 - 5.0F - 48.0F)) {
                  if (this.sidebarPanel.getC7() != c72 || this.sidebarPanel.getValue() != value5) {
                     this.m_2_void(c72, value5);
                  }
               } else {
                  float floatValue23 = this.floatValue14 + 136.0F;
                  float floatValue24 = 6.0F;
                  float floatValue25 = (this.floatValue16 - floatValue24) / 2.0F;
                  float floatValue26 = this.floatValue15 - this.floatValue;
                  if (this.sidebarPanel.getC7() == SidebarPanel.ModeKind.MODELS) {
                     if (event.getButton() != 0) {
                        return;
                     }

                     for (ClickGuiScreen.DataRecord c73 : this.c7List) {
                        float floatValue27 = floatValue23 + c73.m_4b();
                        float floatValue28 = floatValue26 + c73.m_4_b();
                        if (c73.m_9_z_String() == null
                           && floatValue21 >= floatValue27
                           && floatValue21 <= floatValue27 + this.m_6(c73)
                           && floatValue22 >= floatValue28
                           && floatValue22 <= floatValue28 + this.m_0(c73)
                           && floatValue22 >= this.floatValue15
                           && floatValue22 <= this.floatValue18 + this.floatValue20 - 8.0F) {
                           if (c73.m_4_o_boolean()) {
                              this.getKeybindWidget().m_6_void();
                           } else {
                              Onetap.MASCOT_MANAGER.m_7(c73.m_5_d());
                           }

                           return;
                        }
                     }
                  } else if (this.sidebarPanel.getC7() == SidebarPanel.ModeKind.THEMES) {
                     float floatValue29 = 50.0F;
                     ThemeManager themeManager = Onetap.THEME_MANAGER;

                     for (int i = 0; i < themeManager.m_0_int(); i++) {
                        int value6 = i % 2;
                        int value7 = i / 2;
                        float floatValue30 = floatValue23 + value6 * (floatValue25 + floatValue24);
                        float floatValue31 = floatValue26 + value7 * (floatValue29 + floatValue24);
                        if (floatValue21 >= floatValue30
                           && floatValue21 <= floatValue30 + floatValue25
                           && floatValue22 >= floatValue31
                           && floatValue22 <= floatValue31 + floatValue29
                           && floatValue22 >= this.floatValue15
                           && floatValue22 <= this.floatValue18 + this.floatValue20 - 8.0F) {
                           if (!themeManager.m_9_boolean(i)
                              || event.getButton() != 1
                                 && i != themeManager.getValue()
                                 && (
                                    !(floatValue21 >= floatValue30 + floatValue25 - 24.0F)
                                       || !(floatValue21 <= floatValue30 + floatValue25 - 10.0F)
                                       || !(floatValue22 >= floatValue31 + 12.0F)
                                       || !(floatValue22 <= floatValue31 + 26.0F)
                                 )) {
                              if (i != themeManager.getValue()) {
                                 this.value3 = themeManager.getValue();
                                 this.value = ColorUtil.primaryColor();
                                 this.value2 = ColorUtil.secondaryColor();
                                 themeManager.m_5_boolean(i);
                                 this.animation2.setDuration(0.0F);
                                 this.animation2.setTarget(1.0F);
                              }

                              return;
                           }

                           this.customThemePanel.openAt(floatValue21 + 1.0F, floatValue22 + 1.0F);
                           return;
                        }
                     }
                  } else {
                     var moduleList3 = this.getModules();

                     for (int i2 = 0; i2 < moduleList3.size(); i2++) {
                        int value8 = i2 % 2;
                        int value9 = i2 / 2;
                        float floatValue32 = floatValue23 + value8 * (floatValue25 + floatValue24);
                        float floatValue33 = floatValue26 + value9 * 30;
                        ModuleButton moduleButton2 = this.moduleMap3.get(moduleList3.get(i2));
                        if (moduleButton2 != null && moduleButton2.isHovered(floatValue21, floatValue22, floatValue32, floatValue33, floatValue25) && floatValue22 >= this.floatValue15 && floatValue22 <= this.floatValue18 + this.floatValue20 - 8.0F) {
                           Module module2 = (Module)moduleList3.get(i2);
                           if (event.getButton() == 2) {
                              this.moduleButton = moduleButton2;
                              moduleButton2.m_7_void();
                           } else if (event.getButton() == 1 && !module2.getSettings().isEmpty()) {
                              this.m_0(module2, floatValue21 + 9.0F, floatValue22 - 9.0F);
                           } else if (event.getButton() == 0) {
                              if (moduleButton2.m_2_boolean(floatValue21, floatValue22, floatValue33)) {
                                 this.m_0(module2, floatValue32 + floatValue25 + 6.0F, floatValue33);
                              } else {
                                 module2.toggle();
                              }
                           }

                           return;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void m_0(Module module2, float floatValue21, float floatValue22) {
      float floatValue23 = this.floatValue18;
      float floatValue24 = this.floatValue18 + this.floatValue20;
      if (this.floatValue11 != 0.0F) {
         Window window = Minecraft.getInstance().getWindow();
         float floatValue25 = window.getGuiScaledWidth();
         float floatValue26 = window.getGuiScaledHeight();
         float floatValue27 = (floatValue25 - this.floatValue12) / this.floatValue11;
         float floatValue28 = (0.0F - this.floatValue12) / this.floatValue11;
         if (floatValue21 + 200.0F > floatValue27) {
            floatValue21 = floatValue27 - 200.0F - 4.0F;
         }

         if (floatValue21 < floatValue28) {
            floatValue21 = floatValue28 + 4.0F;
         }

         floatValue23 = (0.0F - this.floatValue13) / this.floatValue11 + 4.0F;
         floatValue24 = (floatValue26 - this.floatValue13) / this.floatValue11 - 4.0F;
      }

      this.modulePanel.m_4(module2, floatValue21, floatValue22);
      this.modulePanel.m_3_void(floatValue23, floatValue24);
   }

   private void m_2_void(SidebarPanel.ModeKind c72, int value5) {
      this.searchBox.clear();
      this.text = "";
      this.floatValueArray[m_2_int(c72, value5)] = this.floatValue2;
      this.c7 = c72;
      this.value4 = value5;
      this.floatValue = this.floatValue2 = this.floatValueArray[m_2_int(this.sidebarPanel.getC7(), this.sidebarPanel.getValue())];
      this.animation4.setDuration(0.0F);
      this.animation4.setTarget(1.0F);
   }

   private float m_3_float() {
      float floatValue21 = 144.0F;
      float floatValue22 = (this.floatValue19 - 8.0F - floatValue21 - 6.0F) / 2.0F;
      return floatValue21 + floatValue22 - 22.0F;
   }

   private boolean m_6_boolean(float floatValue21, float floatValue22) {
      float floatValue23 = this.m_3_float();
      float floatValue24 = 8.0F;
      return floatValue21 >= floatValue23 && floatValue21 <= floatValue23 + 22.0F && floatValue22 >= floatValue24 && floatValue22 <= floatValue24 + 22.0F;
   }

   private static int m_2_int(SidebarPanel.ModeKind c72, int value5) {
      return c72 == SidebarPanel.ModeKind.MODULES ? value5 : Category.values().length + c72.ordinal() - 1;
   }

   private float getColumnWidth() {
      return (this.floatValue16 - 12.0F) / 3.0F;
   }

   private float m_5(SidebarPanel.ModeKind c72, int value5) {
      if (c72 == SidebarPanel.ModeKind.THEMES) {
         int value6 = (Onetap.THEME_MANAGER.m_0_int() + 1) / 2;
         return value6 * 56 - 6;
      }

      if (c72 == SidebarPanel.ModeKind.MODELS) {
         if (this.c7List.isEmpty()) {
            return 0.0F;
         }

         ClickGuiScreen.DataRecord c73 = this.c7List.getLast();
         return c73.m_4_b() + this.m_0(c73);
      } else {
         int value7 = (this.getModules().size() + 1) / 2;
         return value7 > 0 ? value7 * 30 - 4 : 0.0F;
      }
   }

   private float m_6(ClickGuiScreen.DataRecord c72) {
      return c72.m_4_o_boolean() ? this.floatValue16 : this.getColumnWidth();
   }

   private float m_0(ClickGuiScreen.DataRecord c72) {
      return c72.m_9_z_String() != null ? 18.0F : (c72.m_4_o_boolean() ? 26.0F : this.getColumnWidth() + 14.0F);
   }

   private List<ClickGuiScreen.DataRecord> getSearchResults() {
      String text4 = this.searchBox.getText().toLowerCase(Locale.ROOT);
      float floatValue21 = this.getColumnWidth();
      float floatValue22 = floatValue21 + 14.0F + 6.0F;
      ArrayList list = new ArrayList();
      float floatValue23 = 0.0F;
      int value5 = 0;
      String text5 = null;
      if (text4.isEmpty()) {
         if (!Onetap.MASCOT_MANAGER.m_0(null)) {
            list.add(new ClickGuiScreen.DataRecord(null, null, true, 0.0F, floatValue23));
            floatValue23 += 32.0F;
         }

         list.add(new ClickGuiScreen.DataRecord(null, null, false, 0.0F, floatValue23));
         value5 = 1;
      }

      for (MascotManager.StateHolder c8 : Onetap.MASCOT_MANAGER.getC8List()) {
         if (c8.getText2().toLowerCase(Locale.ROOT).contains(text4)) {
            if (!Objects.equals(c8.getText3(), text5)) {
               text5 = c8.getText3();
               if (value5 > 0) {
                  floatValue23 += floatValue22;
                  value5 = 0;
               }

               list.add(new ClickGuiScreen.DataRecord(null, text5, false, 0.0F, floatValue23));
               floatValue23 += 18.0F;
            }

            if (value5 == 3) {
               floatValue23 += floatValue22;
               value5 = 0;
            }

            list.add(new ClickGuiScreen.DataRecord(c8, null, false, value5 * (floatValue21 + 6.0F), floatValue23));
            value5++;
         }
      }

      return list;
   }

   private List<Module> getModules() {
      String text4 = this.searchBox.getText();
      if (text4.isEmpty()) {
         return Onetap.MODULE_MANAGER.getByCategory(Category.values()[this.sidebarPanel.getValue()]);
      }

      String text5 = text4.toLowerCase(Locale.ROOT);
      return Onetap.MODULE_MANAGER.getModules().stream().filter(module2 -> module2.getName().toLowerCase(Locale.ROOT).contains(text5)).toList();
   }

   @EventTarget
   public void onMouseScroll(MouseScrollEvent event) {
      if (this.flag) {
         if (this.modulePanel.isFlag()) {
            float floatValue21 = ((float)event.getDoubleValue() - this.floatValue12) / this.floatValue11;
            float floatValue22 = ((float)event.getDoubleValue2() - this.floatValue13) / this.floatValue11;
            this.modulePanel.m_2(floatValue21, floatValue22, event.getDoubleValue3());
         } else if (!this.customThemePanel.isFlag()) {
            float floatValue23 = this.floatValue18 + this.floatValue20 - 4.0F - this.floatValue15;
            float floatValue24 = this.m_5(this.sidebarPanel.getC7(), this.sidebarPanel.getValue());
            float floatValue25 = Math.max(0.0F, floatValue24 - floatValue23 + 4.0F);
            if (!(floatValue25 <= 0.0F)) {
               this.floatValue2 = this.floatValue2 - (float)event.getDoubleValue3() * 25.0F;
               this.floatValue2 = Math.max(0.0F, Math.min(this.floatValue2, floatValue25));
               this.floatValueArray[m_2_int(this.sidebarPanel.getC7(), this.sidebarPanel.getValue())] = this.floatValue2;
               this.animation8.setTarget(1.0F);
            }
         }
      }
   }

   @EventTarget(priority = EventPriority.LOWEST)
   public void onRender2D(Render2DEvent event) {
      ClickGUI.m_3_String();
      float floatValue21 = this.animation.get();
      if (!(floatValue21 < 0.01F)) {
         Minecraft mc = Minecraft.getInstance();
         int value5 = mc.getWindow().getGuiScaledWidth();
         int value6 = mc.getWindow().getGuiScaledHeight();
         float floatValue22 = 420.0F;
         float floatValue23 = 260.0F;
         float floatValue24 = 48.0F + floatValue23;
         float floatValue25 = value5 * 0.85F / floatValue22;
         float floatValue26 = value6 * 0.85F / floatValue24;
         float floatValue27 = Math.min(1.0F, Math.min(floatValue25, floatValue26));
         float floatValue28 = 0.9F + 0.1F * floatValue21;
         this.floatValue11 = floatValue27 * floatValue28;
         float floatValue29 = floatValue22 * this.floatValue11;
         float floatValue30 = floatValue24 * this.floatValue11;
         this.floatValue12 = (value5 - floatValue29) / 2.0F;
         this.floatValue13 = (value6 - floatValue30) / 2.0F;
         this.floatValue19 = floatValue22;
         this.floatValue20 = floatValue23;
         this.floatValue17 = 0.0F;
         this.floatValue18 = 48.0F;
         ClickGUI.m_8(floatValue21, value5, value6);
         Render2D.setTransform(this.floatValue11, this.floatValue12, this.floatValue13);

         try {
            this.m_2_void(floatValue21);
            Draw2D.m_1(0.0F, this.floatValue18, this.floatValue19, this.floatValue20, 8.0F, 4.0F * floatValue21, ColorUtil.withAlpha(DivineStyle.MAIN, (int)(225.0F * floatValue21)));
            Draw2D.m_5(0.0F, this.floatValue18, this.floatValue19, this.floatValue20, 8.0F, 3.0F * floatValue21, ColorUtil.rgba(35, 35, 35, (int)(105.0F * floatValue21)), ClickGUI.getGlow() * floatValue21);
            this.floatValue14 = 8.0F;
            this.floatValue15 = this.floatValue18 + 8.0F;
            this.floatValue16 = this.floatValue19 - 8.0F - (this.floatValue14 + 136.0F);
            float floatValue31 = this.animation2.get();
            float floatValue32 = this.animation2.isFinished() ? 1.0F : floatValue31;
            float floatValue33 = this.floatValue18 + this.floatValue20 - 5.0F - 48.0F;
            this.sidebarPanel.m_5(this.floatValue14, this.floatValue15, floatValue33, floatValue21, floatValue32, this.value, this.value2);
            float floatValue34 = this.animation4.get();
            boolean flag3 = !this.animation4.isFinished();
            boolean flag4 = this.animation2.isFinished();
            SidebarPanel.ModeKind c72 = this.sidebarPanel.getC7();
            if (c72 == SidebarPanel.ModeKind.MODELS) {
               var c7List3 = this.getSearchResults();
               if (!c7List3.equals(this.c7List) && !flag3) {
                  this.c7List2 = this.c7List;
                  this.animation5.setDuration(0.0F);
                  this.animation5.setTarget(1.0F);
               }

               this.text = this.searchBox.getText();
               this.c7List = c7List3;
            }

            float floatValue35 = this.m_5(c72, this.sidebarPanel.getValue());
            float floatValue36 = this.floatValue15;
            float floatValue37 = this.floatValue18 + this.floatValue20 - 4.0F - floatValue36;
            float floatValue38 = Math.max(0.0F, floatValue35 - floatValue37 + 4.0F);
            if (this.floatValue2 > floatValue38) {
               this.floatValue2 = floatValue38;
               this.floatValueArray[m_2_int(c72, this.sidebarPanel.getValue())] = this.floatValue2;
            }

            if (floatValue38 > 0.0F) {
               this.floatValue3 = floatValue35;
               this.floatValue4 = floatValue38;
               this.floatValue8 = Math.min(floatValue37, Math.max(15.0F, floatValue37 * floatValue37 / this.floatValue3));
               this.floatValue9 = Math.max(0.0F, floatValue37 - this.floatValue8);
               if (this.flag2) {
                  float floatValue39 = ((float)(mc.mouseHandler.ypos() / mc.getWindow().getGuiScale()) - this.floatValue13) / this.floatValue11;
                  this.floatValue = this.floatValue2 = Math.max(0.0F, Math.min((floatValue39 - floatValue36 - this.floatValue10) / this.floatValue9 * this.floatValue4, this.floatValue4));
                  this.floatValueArray[m_2_int(c72, this.sidebarPanel.getValue())] = this.floatValue2;
               }
            } else {
               this.floatValue4 = 0.0F;
               this.flag2 = false;
            }

            this.floatValue = this.floatValue + (this.floatValue2 - this.floatValue) * 0.1F;
            if (Math.abs(this.floatValue - this.floatValue2) < 0.5F) {
               this.floatValue = this.floatValue2;
            }

            float floatValue40 = this.floatValue14 + 136.0F;
            float floatValue41 = this.floatValue15 - this.floatValue;
            float floatValue42 = 6.0F;
            float floatValue43 = (this.floatValue16 - floatValue42) / 2.0F;
            Render2D.pushScissor(floatValue40 - 1.0F, floatValue36, this.floatValue17 + this.floatValue19 - 8.0F - floatValue40 + 3.0F, floatValue37);
            List list = null;
            boolean flag5 = false;
            float floatValue44 = 1.0F;
            if (c72 == SidebarPanel.ModeKind.MODULES) {
               String text4 = this.searchBox.getText();
               list = this.getModules();
               if (!text4.equals(this.text)) {
                  this.moduleMap = indexModules(this.moduleList);
                  this.moduleMap2 = indexModules(list);
                  this.moduleList2 = this.moduleList;
                  this.text = text4;
                  this.animation5.setDuration(0.0F);
                  this.animation5.setTarget(1.0F);
               }

               this.moduleList = list;
               floatValue44 = this.animation5.get();
               flag5 = !flag3 && !this.animation5.isFinished();
            } else if (c72 == SidebarPanel.ModeKind.MODELS) {
               floatValue44 = this.animation5.get();
               flag5 = !flag3 && !this.animation5.isFinished();
            }

            float floatValue45 = flag3 ? floatValue34 : 1.0F;
            int value7 = (int)(255.0F * floatValue45 * floatValue21);
            if (value7 > 0) {
               if (c72 == SidebarPanel.ModeKind.THEMES) {
                  this.m_3(floatValue41, floatValue40, floatValue43, floatValue42, value7, flag4, floatValue32);
               } else if (c72 == SidebarPanel.ModeKind.MODELS) {
                  this.m_6(floatValue41, floatValue40, value7, flag5 ? floatValue44 : 1.0F);
               } else if (flag5) {
                  this.m_4(list, floatValue41, floatValue40, floatValue43, floatValue42, value7, floatValue44);
               } else if (value7 > 0) {
                  this.m_9(list, floatValue41, floatValue40, floatValue43, floatValue42, value7);
               }
            }

            if (flag5) {
               int value8 = (int)(255.0F * (1.0F - floatValue44) * floatValue21);
               if (value8 > 0) {
                  if (c72 == SidebarPanel.ModeKind.MODELS) {
                     this.m_8(floatValue41, floatValue40, value8, floatValue44);
                  } else {
                     this.m_2(this.moduleList2, floatValue41, floatValue40, floatValue43, floatValue42, value8, floatValue44);
                  }
               }
            }

            if (flag3) {
               int value9 = (int)(255.0F * (1.0F - floatValue34) * floatValue21);
               if (value9 > 0) {
                  float floatValue46 = this.floatValue15 - this.floatValueArray[m_2_int(this.c7, this.value4)];
                  if (this.c7 == SidebarPanel.ModeKind.THEMES) {
                     this.m_3(floatValue46, floatValue40, floatValue43, floatValue42, value9, flag4, floatValue32);
                  } else if (this.c7 == SidebarPanel.ModeKind.MODELS) {
                     this.m_6(floatValue46, floatValue40, value9, 1.0F);
                  } else {
                     this.m_9(Onetap.MODULE_MANAGER.getByCategory(Category.values()[this.value4]), floatValue46, floatValue40, floatValue43, floatValue42, value9);
                  }
               }
            }

            Render2D.popScissor();
            if (floatValue38 > 0.0F) {
               this.floatValue5 = this.floatValue;
            }

            float floatValue47 = this.animation8.update(floatValue38 > 0.0F ? 1.0F : 0.0F);
            if (floatValue47 > 0.01F && this.floatValue4 > 0.0F) {
               this.floatValue6 = this.floatValue17 + this.floatValue19 - 2.0F - 3.0F;
               this.floatValue7 = floatValue36 + (this.floatValue9 > 0.0F ? this.floatValue5 / this.floatValue4 * this.floatValue9 : 0.0F);
               int value10 = (int)(180.0F * floatValue47 * floatValue21);
               float floatValue48 = ((float)(mc.mouseHandler.xpos() / mc.getWindow().getGuiScale()) - this.floatValue12) / this.floatValue11;
               float floatValue49 = ((float)(mc.mouseHandler.ypos() / mc.getWindow().getGuiScale()) - this.floatValue13) / this.floatValue11;
               boolean flag6 = floatValue48 >= this.floatValue6 && floatValue48 <= this.floatValue6 + 3.0F && floatValue49 >= this.floatValue7 && floatValue49 <= this.floatValue7 + this.floatValue8;
               float floatValue50 = this.animation9.update(!flag6 && !this.flag2 ? 0.0F : 1.0F);
               float floatValue51 = this.animation10.update(this.flag2 ? 1.0F : 0.0F);
               float floatValue52 = Math.clamp(floatValue51, 0.0F, 1.0F);
               float floatValue53 = Math.max(floatValue50 * 0.65F, floatValue52);
               int value11 = Math.min(255, (int)(120.0F + 130.0F * floatValue53));
               int value12 = Math.min(222, (int)(value10 + 15.0F * floatValue52));
               if (value10 > 2) {
                  Draw2D.roundedRect(this.floatValue6, floatValue36, 3.0F, floatValue37, 1.5F, ColorUtil.rgba(0, 0, 0, 30 * value10 / 125));
                  Draw2D.roundedRect(this.floatValue6, this.floatValue7 + floatValue51 * 1.5F, 3.0F, this.floatValue8 - floatValue51 * 3.0F, 1.5F, ColorUtil.rgba(value11, value11, value11, value12));
               }
            }

            if (!this.modulePanel.isFlag()) {
               this.module = null;
               if (c72 == SidebarPanel.ModeKind.MODULES) {
                  float floatValue54 = ((float)(mc.mouseHandler.xpos() / mc.getWindow().getGuiScale()) - this.floatValue12) / this.floatValue11;
                  float floatValue55 = ((float)(mc.mouseHandler.ypos() / mc.getWindow().getGuiScale()) - this.floatValue13) / this.floatValue11;
                  if (floatValue55 >= this.floatValue15 && floatValue55 <= this.floatValue18 + this.floatValue20 - 8.0F) {
                     var moduleList3 = this.getModules();

                     for (int i = 0; i < moduleList3.size(); i++) {
                        float floatValue56 = floatValue40 + i % 2 * (floatValue43 + floatValue42);
                        float floatValue57 = floatValue41 + i / 2 * 30;
                        ModuleButton moduleButton2 = this.moduleMap3.get(moduleList3.get(i));
                        if (moduleButton2 != null && moduleButton2.isHovered(floatValue54, floatValue55, floatValue56, floatValue57, floatValue43)) {
                           this.module = (Module)moduleList3.get(i);
                           break;
                        }
                     }
                  }
               }
            }

            String text5 = this.module != null ? this.module.getDescription() : null;
            boolean flag7 = text5 != null && !text5.isEmpty();
            if (flag7 && !text5.equals(this.text2)) {
               if (this.animation11.getValue() > 0.01F) {
                  this.text3 = this.text2;
                  this.animation12.setDuration(0.0F);
                  this.animation12.setTarget(1.0F);
               }

               this.text2 = text5;
            }

            float floatValue58 = this.animation11.update(flag7 ? 1.0F : 0.0F);
            if (floatValue58 > 0.01F && this.text2 != null) {
               float floatValue59 = floatValue58 * floatValue21;
               float floatValue60 = this.animation12.get();
               float floatValue61 = Math.max(-20.0F, 2.0F - this.floatValue13 / this.floatValue11) + (1.0F - floatValue58) * 5.0F;
               if (this.text3 != null && floatValue60 < 1.0F) {
                  this.m_5(this.text3, floatValue61 - floatValue60 * 3.0F, (1.0F - floatValue60) * floatValue59);
               }

               this.m_5(this.text2, floatValue61 + (1.0F - floatValue60) * 3.0F, floatValue60 * floatValue59);
            }

            this.modulePanel.m_3_void();
            this.customThemePanel.m_2_void();
         } finally {
            Render2D.setTransform(1.0F, 0.0F, 0.0F);
         }
      }
   }

   private void m_2_void(float floatValue21) {
      Draw2D.m_1(0.0F, 0.0F, this.floatValue19, 38.0F, 8.0F, 4.0F * floatValue21, ColorUtil.rgba(12, 12, 12, (int)(95.0F * floatValue21)));
      Draw2D.m_5(0.0F, 0.0F, this.floatValue19, 38.0F, 8.0F, 3.0F * floatValue21, ColorUtil.rgba(35, 35, 35, (int)(105.0F * floatValue21)), ClickGUI.getGlow() * floatValue21);
      int value5 = ColorUtil.primaryColor();
      int value6 = ColorUtil.secondaryColor();
      int value7 = (int)(255.0F * floatValue21);
      float floatValue22 = 26.0F;
      float floatValue23 = 8.0F;
      float floatValue24 = (38.0F - floatValue22) / 2.0F;
      Draw2D.m_8(floatValue23 + 0.5F, floatValue24 + 1.0F, floatValue22 - 1.0F, floatValue22 - 2.0F, 6.0F, 0.5F, ColorUtil.withAlpha(value5, value7), ColorUtil.withAlpha(value6, value7));
      float floatValue25 = 30.0F;
      float floatValue26 = Draw2D.getCenteredY("\ue900", Fonts.SUPPLIER6, floatValue25, floatValue24, floatValue22);
      float floatValue27 = Draw2D.getCenteredX("\ue900", Fonts.SUPPLIER6, floatValue25, floatValue23, floatValue22);
      Draw2D.drawTextWithShadow("\ue900", Fonts.SUPPLIER6, floatValue27, floatValue26, ColorUtil.rgba(255, 255, 255, value7), floatValue25);
      float floatValue28 = floatValue23 + floatValue22 + 8.0F;
      float floatValue29 = Draw2D.getTextBaseline(Fonts.SUPPLIER18, 16.0F, 0.0F, 38.0F);
      float floatValue30 = (float)(System.currentTimeMillis() % 4000L) / 4000.0F;
      Draw2D.m_9("Moonward", Fonts.SUPPLIER18, floatValue28, floatValue29, ColorUtil.withAlpha(value5, value7), ColorUtil.withAlpha(value6, value7), 16.0F, floatValue30);
      float floatValue31 = 144.0F;
      float floatValue32 = (this.floatValue19 - 8.0F - floatValue31 - 6.0F) / 2.0F;
      this.searchBox.m_5(floatValue31 + floatValue32 + 6.0F, floatValue32, 38.0F, floatValue21);
      this.m_0(value5, value6, floatValue21);
   }

   private void m_0(int value5, int value6, float floatValue21) {
      Minecraft mc = Minecraft.getInstance();
      float floatValue22 = ((float)(mc.mouseHandler.xpos() / mc.getWindow().getGuiScale()) - this.floatValue12) / this.floatValue11;
      float floatValue23 = ((float)(mc.mouseHandler.ypos() / mc.getWindow().getGuiScale()) - this.floatValue13) / this.floatValue11;
      float floatValue24 = this.m_3_float();
      float floatValue25 = 8.0F;
      float floatValue26 = this.animation7.update(this.sidebarPanel.getC7() == SidebarPanel.ModeKind.MODELS ? 1.0F : 0.0F);
      float floatValue27 = this.animation6.update(this.m_6_boolean(floatValue22, floatValue23) ? 1.0F : 0.0F);
      Draw2D.roundedRect(floatValue24, floatValue25, 22.0F, 22.0F, 6.0F, ColorUtil.rgba(0, 0, 0, (int)(45.0F * floatValue21)));
      if (floatValue26 > 0.01F) {
         Draw2D.m_0(floatValue24, floatValue25, 22.0F, 22.0F, 5.0F, 0.0F, ColorUtil.withAlpha(value5, (int)(175.0F * floatValue26 * floatValue21)), ColorUtil.withAlpha(value6, (int)(175.0F * floatValue26 * floatValue21)));
      }

      float floatValue28 = 19.0F;
      float floatValue29 = Math.max(floatValue26, floatValue27 * 0.5F);
      int value7 = (int)(140.0F + 115.0F * floatValue29);
      int value8 = (int)(175.0F + 55.0F * floatValue29);
      float floatValue30 = Draw2D.getCenteredX("\ue90a", Fonts.SUPPLIER7, floatValue28, floatValue24, 22.0F);
      float floatValue31 = Draw2D.getCenteredY("\ue90a", Fonts.SUPPLIER7, floatValue28, floatValue25, 22.0F);
      Draw2D.drawTextWithShadow("\ue90a", Fonts.SUPPLIER7, floatValue30, floatValue31, ColorUtil.rgba(value7, value7, value7, (int)(value8 * floatValue21)), floatValue28);
   }

   private void m_5(String text4, float floatValue21, float floatValue22) {
      float floatValue23 = 14.0F;
      float floatValue24 = this.floatValue19 - 16.0F;
      float floatValue25 = Draw2D.getTextWidth(text4, Fonts.SUPPLIER16, floatValue23);
      if (floatValue25 > floatValue24) {
         floatValue23 *= floatValue24 / floatValue25;
         floatValue25 = floatValue24;
      }

      Draw2D.drawTextWithShadow(text4, Fonts.SUPPLIER16, (this.floatValue19 - floatValue25) / 2.0F, floatValue21, ColorUtil.rgba(222, 222, 222, (int)(235.0F * floatValue22)), floatValue23);
   }

   private void m_9(List<Module> moduleList3, float floatValue21, float floatValue22, float floatValue23, float floatValue24, int value5) {
      float floatValue25 = value5 / 255.0F;

      for (int i = 0; i < moduleList3.size(); i++) {
         int value6 = i % 2;
         int value7 = i / 2;
         float floatValue26 = floatValue22 + value6 * (floatValue23 + floatValue24);
         float floatValue27 = floatValue21 + value7 * 30;
         if (!(floatValue27 + 26.5F <= this.floatValue15) && !(floatValue27 - 0.5F >= this.floatValue18 + this.floatValue20 - 4.0F)) {
            this.moduleMap3.computeIfAbsent((Module)moduleList3.get(i), ModuleButton::new).m_7_void(floatValue26, floatValue27, floatValue23, floatValue25);
         }
      }
   }

   private void m_4(List<Module> moduleList3, float floatValue21, float floatValue22, float floatValue23, float floatValue24, int value5, float floatValue25) {
      float floatValue26 = value5 / 255.0F;

      for (int i = 0; i < moduleList3.size(); i++) {
         Module module2 = (Module)moduleList3.get(i);
         float floatValue27 = floatValue22 + i % 2 * (floatValue23 + floatValue24);
         float floatValue28 = floatValue21 + i / 2 * 30;
         float floatValue29 = floatValue27;
         float floatValue30 = floatValue28;
         float floatValue31 = floatValue26;
         Integer integer = this.moduleMap.get(module2);
         if (integer != null) {
            if (integer != i) {
               float floatValue32 = floatValue22 + integer % 2 * (floatValue23 + floatValue24);
               float floatValue33 = floatValue21 + integer / 2 * 30;
               floatValue29 = floatValue32 + (floatValue27 - floatValue32) * floatValue25;
               floatValue30 = floatValue33 + (floatValue28 - floatValue33) * floatValue25;
            }
         } else {
            floatValue30 = floatValue28 - 10.0F * (1.0F - floatValue25);
            floatValue31 = floatValue26 * floatValue25;
         }

         if (!(floatValue31 <= 0.01F) && !(floatValue30 + 26.5F <= this.floatValue15) && !(floatValue30 - 0.5F >= this.floatValue18 + this.floatValue20 - 4.0F)) {
            this.moduleMap3.computeIfAbsent(module2, ModuleButton::new).m_7_void(floatValue29, floatValue30, floatValue23, floatValue31);
         }
      }
   }

   private void m_2(List<Module> moduleList3, float floatValue21, float floatValue22, float floatValue23, float floatValue24, int value5, float floatValue25) {
      float floatValue26 = value5 / 255.0F;

      for (int i = 0; i < moduleList3.size(); i++) {
         Module module2 = (Module)moduleList3.get(i);
         if (!this.moduleMap2.containsKey(module2)) {
            float floatValue27 = floatValue22 + i % 2 * (floatValue23 + floatValue24);
            float floatValue28 = floatValue21 + i / 2 * 30 + 10.0F * floatValue25;
            if (!(floatValue28 + 26.5F <= this.floatValue15) && !(floatValue28 - 0.5F >= this.floatValue18 + this.floatValue20 - 4.0F)) {
               this.moduleMap3.computeIfAbsent(module2, ModuleButton::new).m_7_void(floatValue27, floatValue28, floatValue23, floatValue26);
            }
         }
      }
   }

   private static Map<Module, Integer> indexModules(List<Module> moduleList3) {
      HashMap map = new HashMap();

      for (int i = 0; i < moduleList3.size(); i++) {
         map.put((Module)moduleList3.get(i), i);
      }

      return map;
   }

   private void m_6(float floatValue21, float floatValue22, int value5, float floatValue23) {
      float floatValue24 = value5 / 255.0F;
      HashMap map = new HashMap();
      if (floatValue23 < 1.0F) {
         for (ClickGuiScreen.DataRecord c72 : this.c7List2) {
            map.put(c72.m_8_Object(), c72);
         }
      }

      for (ClickGuiScreen.DataRecord c73 : this.c7List) {
         float floatValue25 = floatValue22 + c73.m_4b();
         float floatValue26 = floatValue21 + c73.m_4_b();
         float floatValue27 = floatValue24;
         if (floatValue23 < 1.0F) {
            ClickGuiScreen.DataRecord c74 = (ClickGuiScreen.DataRecord)map.get(c73.m_8_Object());
            if (c74 != null) {
               floatValue25 = floatValue22 + c74.m_4b() + (c73.m_4b() - c74.m_4b()) * floatValue23;
               floatValue26 = floatValue21 + c74.m_4_b() + (c73.m_4_b() - c74.m_4_b()) * floatValue23;
            } else {
               floatValue26 -= 10.0F * (1.0F - floatValue23);
               floatValue27 = floatValue24 * floatValue23;
            }
         }

         if (floatValue27 > 0.01F) {
            this.m_9(c73, floatValue25, floatValue26, floatValue27);
         }
      }
   }

   private void m_8(float floatValue21, float floatValue22, int value5, float floatValue23) {
      float floatValue24 = value5 / 255.0F;
      HashSet set = new HashSet();

      for (ClickGuiScreen.DataRecord c72 : this.c7List) {
         set.add(c72.m_8_Object());
      }

      for (ClickGuiScreen.DataRecord c73 : this.c7List2) {
         if (!set.contains(c73.m_8_Object())) {
            this.m_9(c73, floatValue22 + c73.m_4b(), floatValue21 + c73.m_4_b() + 10.0F * floatValue23, floatValue24);
         }
      }
   }

   private void m_9(ClickGuiScreen.DataRecord c72, float floatValue21, float floatValue22, float floatValue23) {
      if (!(floatValue22 + this.m_0(c72) + 0.5F <= this.floatValue15) && !(floatValue22 - 0.5F >= this.floatValue18 + this.floatValue20 - 4.0F)) {
         if (c72.m_9_z_String() != null) {
            float floatValue24 = floatValue22 + 3.5F - 1.0F;
            Draw2D.drawStyledText(c72.m_9_z_String(), Fonts.SUPPLIER16, floatValue21 + 2.0F, floatValue24, ColorUtil.rgba(160, 160, 160, (int)(222.0F * floatValue23)), 11.0F);
         } else if (c72.m_4_o_boolean()) {
            this.m_8_void(floatValue21, floatValue22, this.m_6(c72), floatValue23);
         } else {
            float floatValue25 = this.getColumnWidth();
            float floatValue26 = floatValue25 + 14.0F;
            float floatValue27 = floatValue25 - 8.0F;
            MascotManager.StateHolder c8 = c72.m_5_d();
            Draw2D.roundedRect(floatValue21, floatValue22, floatValue25, floatValue26, 6.0F, ColorUtil.rgba(0, 0, 0, (int)(35.0F * floatValue23)));
            GpuTextureView gpuTextureView = c8 == null ? null : c8.m_9_GpuTextureView();
            if (gpuTextureView != null) {
               Draw2D.m_4(floatValue21 + 4.0F, floatValue22 + 4.0F, floatValue27, floatValue27, 5.0F, ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue23)), gpuTextureView);
            } else {
               Draw2D.roundedRect(floatValue21 + 4.0F, floatValue22 + 4.0F, floatValue27, floatValue27, 5.0F, ColorUtil.rgba(0, 0, 0, (int)(30.0F * floatValue23)));
               if (c8 == null) {
                  float floatValue28 = 22.0F;
                  float floatValue29 = Draw2D.getCenteredX("\ue916", Fonts.SUPPLIER7, floatValue28, floatValue21 + 4.0F, floatValue27);
                  float floatValue30 = Draw2D.getCenteredY("\ue916", Fonts.SUPPLIER7, floatValue28, floatValue22 + 4.0F, floatValue27);
                  Draw2D.drawStyledText("\ue916", Fonts.SUPPLIER7, floatValue29, floatValue30, ColorUtil.rgba(140, 140, 140, (int)(200.0F * floatValue23)), floatValue28);
               }
            }

            String text4 = c8 == null ? "Без модели" : c8.getText2();
            float floatValue31 = 10.0F;
            float floatValue32 = floatValue25 - 8.0F;
            float floatValue33 = Draw2D.getTextWidth(text4, Fonts.SUPPLIER16, floatValue31);
            float floatValue34 = floatValue22 + 4.0F + floatValue27 + (14.0F - floatValue31) / 2.0F - 0.5F;
            int value5 = ColorUtil.rgba(222, 222, 222, (int)(222.0F * floatValue23));
            if (floatValue33 > floatValue32) {
               Draw2D.drawColoredTextRaw(text4, Fonts.SUPPLIER16, floatValue21 + 4.0F, floatValue34, value5, 0.8F, 1.0F, floatValue32, floatValue31);
            } else {
               Draw2D.drawStyledText(text4, Fonts.SUPPLIER16, floatValue21 + (floatValue25 - floatValue33) / 2.0F, floatValue34, value5, floatValue31);
            }

            Animation animation13 = this.objectMap.computeIfAbsent(c72.m_8_Object(), object -> new Animation(180L, Onetap.MASCOT_MANAGER.m_0(c8) ? 1.0F : 0.0F, Easing.EASING8));
            float floatValue35 = animation13.update(Onetap.MASCOT_MANAGER.m_0(c8) ? 1.0F : 0.0F);
            if (floatValue35 > 0.01F) {
               Draw2D.m_4(floatValue21, floatValue22, floatValue25, floatValue26, 0.5F, 6.0F, ColorUtil.rgba(255, 255, 255, (int)(180.0F * floatValue35 * floatValue23)));
            }
         }
      }
   }

   private KeybindWidget getKeybindWidget() {
      if (this.keybindWidget == null) {
         this.keybindWidget = new KeybindWidget(Onetap.MASCOT_MANAGER.getKeybindSetting());
      }

      return this.keybindWidget;
   }

   private void m_8_void(float floatValue21, float floatValue22, float floatValue23, float floatValue24) {
      Draw2D.roundedRect(floatValue21, floatValue22, floatValue23, 26.0F, 8.0F, ColorUtil.rgba(0, 0, 0, (int)(35.0F * floatValue24)));
      this.getKeybindWidget().m_2(floatValue21, floatValue22 + 1.0F, floatValue23, floatValue24);
   }

   private void m_3(float floatValue21, float floatValue22, float floatValue23, float floatValue24, int value5, boolean flag3, float floatValue25) {
      ThemeManager themeManager = Onetap.THEME_MANAGER;
      float floatValue26 = value5 / 255.0F;
      float floatValue27 = 50.0F;
      byte byteValue = 26;
      int value6 = themeManager.m_0_int();

      for (int i = 0; i < value6; i++) {
         int value7 = i % 2;
         int value8 = i / 2;
         float floatValue28 = floatValue22 + value7 * (floatValue23 + floatValue24);
         float floatValue29 = floatValue21 + value8 * (floatValue27 + floatValue24);
         if (!(floatValue29 + floatValue27 + 0.5F <= this.floatValue15) && !(floatValue29 - 0.5F >= this.floatValue18 + this.floatValue20 - 4.0F)) {
            int value9 = themeManager.m_8_C54(i).m_1m_int();
            int value10 = themeManager.m_8_C54(i).m_1c();
            Draw2D.roundedRect(floatValue28, floatValue29, floatValue23, floatValue27, 6.0F, ColorUtil.rgba(0, 0, 0, (int)(35.0F * floatValue26)));
            float floatValue30 = floatValue29 + 6.0F;
            Draw2D.m_0(floatValue28 + 6.0F, floatValue30, floatValue23 - 12.0F, byteValue, 6.0F, 0.0F, ColorUtil.withAlpha(value9, (int)(255.0F * floatValue26)), ColorUtil.withAlpha(value10, (int)(255.0F * floatValue26)));
            float floatValue31 = floatValue30 + byteValue + 1.0F;
            float floatValue32 = Draw2D.getTextWidth(themeManager.m_8_C54(i).m_4_l(), Fonts.SUPPLIER16, 12.0F);
            Draw2D.drawStyledText(themeManager.m_8_C54(i).m_4_l(), Fonts.SUPPLIER16, floatValue28 + 10.0F, floatValue31, ColorUtil.rgba(222, 222, 222, (int)(222.0F * floatValue26)), 12.0F);
            float floatValue33 = floatValue31 + 5.0F;
            float floatValue34 = floatValue28 + 10.0F + floatValue32 + 6.0F;
            Draw2D.roundedRect(floatValue34, floatValue33, 6.0F, 6.0F, 3.0F, ColorUtil.withAlpha(value9, (int)(222.0F * floatValue26)));
            Draw2D.roundedRect(floatValue34 + 9.0F, floatValue33, 6.0F, 6.0F, 3.0F, ColorUtil.withAlpha(value10, (int)(222.0F * floatValue26)));
            if (themeManager.m_9_boolean(i)) {
               Minecraft mc = Minecraft.getInstance();
               double doubleValue = mc.getWindow().getGuiScale();
               float floatValue35 = (float)(mc.mouseHandler.xpos() / doubleValue - Render2D.getOffsetX()) / Render2D.getScale();
               float floatValue36 = (float)(mc.mouseHandler.ypos() / doubleValue - Render2D.getOffsetY()) / Render2D.getScale();
               boolean flag4 = floatValue35 >= floatValue28 + floatValue23 - 24.0F && floatValue35 <= floatValue28 + floatValue23 - 10.0F && floatValue36 >= floatValue29 + 12.0F && floatValue36 <= floatValue29 + 26.0F;
               float floatValue37 = this.animation3.update(flag4 ? 1.0F : 0.0F);
               float floatValue38 = 11.0F + floatValue37 * 1.5F;
               float floatValue39 = Draw2D.getTextWidth("\uf040", Fonts.SUPPLIER8, floatValue38);
               Draw2D.drawTextWithShadow(
                  "\uf040",
                  Fonts.SUPPLIER8,
                  floatValue28 + floatValue23 - 17.0F - floatValue39 / 2.0F,
                  floatValue29 + 19.0F - floatValue38 / 2.0F,
                  ColorUtil.rgba(255, 255, 255, (int)((140.0F + 115.0F * floatValue37) * floatValue26)),
                  floatValue38
               );
            }

            boolean flag5 = i == themeManager.getValue();
            boolean flag6 = !flag3 && i == this.value3;
            float floatValue40 = 0.0F;
            if (flag5) {
               floatValue40 = flag3 ? 1.0F : floatValue25;
            }

            if (flag6) {
               floatValue40 = 1.0F - floatValue25;
            }

            if (floatValue40 > 0.01F) {
               Draw2D.m_4(floatValue28, floatValue29, floatValue23, floatValue27, 0.5F, 6.0F, ColorUtil.rgba(255, 255, 255, (int)(180.0F * floatValue40 * floatValue26)));
            }
         }
      }
   }

   public static void m_8(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_8_StringArr() {
      return textArray;
   }

   static {
      m_8((String[])null);
   }

   private record DataRecord(MascotManager.StateHolder f_5_d, String f_9_z, boolean f_4_o, float f_4b, float f_4_b) {
      public Object m_8_Object() {
         return this.f_5_d != null ? this.f_5_d : (this.f_9_z != null ? this.f_9_z : (this.f_4_o ? FiguraPanel.class : ClickGuiScreen.DataRecord.class));
      }

      public MascotManager.StateHolder m_5_d() {
         return this.f_5_d;
      }

      public String m_9_z_String() {
         return this.f_9_z;
      }

      public boolean m_4_o_boolean() {
         return this.f_4_o;
      }

      public float m_4b() {
         return this.f_4b;
      }

      public float m_4_b() {
         return this.f_4_b;
      }
   }
}
