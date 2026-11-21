package wtf.onetap.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.input.KeyEvent;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.config.ProxyConfig;
import wtf.onetap.config.ProxyEntry;
import wtf.onetap.config.ProxyState;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.util.KeyNames;

public class ServerEditScreen {
   private final Animation animation;
   private final Animation[] animationArray;
   private final Animation animation2;
   private final Animation animation3;
   private final Animation animation4;
   private final Animation animation5;
   private final ServerListEntry[] serverListEntryArray;
   private ServerEditScreen.ModeKind c1;
   private boolean flag;
   private int value;
   private int value2;
   private boolean flag2;
   private String text;
   private boolean flag3;
   private boolean flag4;
   private boolean flag5;
   private boolean flag6;
   private String text2;

   public ServerEditScreen() {
      this.animation = new Animation(280L, 0.0F, Easing.EASING8);
      this.animationArray = new Animation[]{new Animation(140L, 0.0F, Easing.EASING8), new Animation(140L, 0.0F, Easing.EASING8), new Animation(140L, 0.0F, Easing.EASING8)};
      this.animation2 = new Animation(140L, 0.0F, Easing.EASING8);
      this.animation3 = new Animation(140L, 0.0F, Easing.EASING8);
      this.animation4 = new Animation(140L, 0.0F, Easing.EASING8);
      this.animation5 = new Animation(140L, 0.0F, Easing.EASING8);
      this.serverListEntryArray = new ServerListEntry[]{new ServerListEntry(128, ""), new ServerListEntry(128, ""), new ServerListEntry(255, "")};
      this.c1 = ServerEditScreen.ModeKind.NONE;
      this.text = "";
      this.text2 = "";
   }

   public float m_9_float() {
      float floatValue = this.animation.update(this.flag ? 1.0F : 0.0F);
      if (!this.flag && this.animation.isFinished()) {
         this.c1 = ServerEditScreen.ModeKind.NONE;
      }

      return floatValue;
   }

   public void open(ServerEditScreen.ModeKind c12) {
      if (c12 != ServerEditScreen.ModeKind.NONE) {
         this.c1 = c12;
         this.flag = true;
         this.value = 0;
         this.text = "";
         this.flag3 = false;
         this.flag4 = false;
         this.flag5 = false;
         this.value2 = 2;
         this.flag2 = false;

         for (int i = 0; i < this.serverListEntryArray.length; i++) {
            this.serverListEntryArray[i].setFlag2(c12 == ServerEditScreen.ModeKind.PROXY && i == 2);
            this.serverListEntryArray[i].m_6_void("");
            this.serverListEntryArray[i].setFocused(i == 0);
         }

         this.m_2_void();
      }
   }

   public void setFields(String text3, String text4, String text5) {
      this.serverListEntryArray[0].m_6_void(text3);
      this.serverListEntryArray[1].m_6_void(text4);
      this.serverListEntryArray[2].m_6_void(text5);
      this.serverListEntryArray[0].setFocused(true);
      this.serverListEntryArray[1].setFocused(false);
      this.serverListEntryArray[2].setFocused(false);
      this.value = 0;
   }

   public void m_3_void(int value3) {
      this.value2 = Math.floorMod(value3, 3);
   }

   public void setFlag2(boolean flag7) {
      this.flag2 = flag7;
   }

   public void reset() {
      this.flag = false;
      this.flag4 = true;
   }

   public float m_6_float() {
      return this.animation.updateOrSnap();
   }

   public ServerEditScreen.ModeKind getC1() {
      return this.c1;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public boolean isVisible() {
      return this.c1 != ServerEditScreen.ModeKind.NONE && (this.flag || this.animation.getValue() > 0.02F);
   }

   public boolean isOpen() {
      return this.flag && this.animation.getValue() > 0.45F;
   }

   public String getEntryText(int value3) {
      return value3 >= 0 && value3 < this.serverListEntryArray.length ? this.serverListEntryArray[value3].getText2() : "";
   }

   public String getSelectedText() {
      return this.value >= 0 && this.value < this.m_6_int() ? this.serverListEntryArray[this.value].getSelection() : "";
   }

   public void pasteIntoField(String text3) {
      if (this.value >= 0 && this.value < this.m_6_int()) {
         this.serverListEntryArray[this.value].paste(text3, this::isPrintable);
         this.text = "";
      }
   }

   public void m_9_void() {
      if (this.value >= 0 && this.value < this.m_6_int()) {
         this.serverListEntryArray[this.value].clear();
      }
   }

   public void selectAllInField() {
      if (this.value >= 0 && this.value < this.m_6_int()) {
         this.serverListEntryArray[this.value].selectAll();
      }
   }

   public void deleteSelected() {
      if (this.value >= 0 && this.value < this.m_6_int()) {
         this.serverListEntryArray[this.value].deleteOrClear();
      }
   }

   public int getValue2() {
      return this.value2;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public boolean m_9_boolean() {
      boolean flag7 = this.flag3;
      this.flag3 = false;
      return flag7;
   }

   public boolean m_1_boolean() {
      boolean flag7 = this.flag4;
      this.flag4 = false;
      return flag7;
   }

   public boolean m_0_f_boolean() {
      boolean flag7 = this.flag5;
      this.flag5 = false;
      return flag7;
   }

   public void setFlag6(boolean flag7) {
      this.flag6 = flag7;
   }

   public void m_5_void(String text3) {
      this.text2 = text3 == null ? "" : text3;
   }

   public boolean isEditing() {
      return this.flag6 && (this.c1 == ServerEditScreen.ModeKind.ADD || this.c1 == ServerEditScreen.ModeKind.EDIT || this.c1 == ServerEditScreen.ModeKind.DIRECT);
   }

   public int m_6_int() {
      return switch (this.c1.ordinal()) {
         case 0 -> 0;
         case 1 -> 3;
         case 2, 3 -> 2;
         case 4 -> 1;
         default -> throw new MatchException(null, null);
      };
   }

   public boolean isProxyMode() {
      return this.c1 == ServerEditScreen.ModeKind.PROXY;
   }

   public boolean isAddOrEdit() {
      return this.c1 == ServerEditScreen.ModeKind.ADD || this.c1 == ServerEditScreen.ModeKind.EDIT;
   }

   public String getTitle() {
      return switch (this.c1.ordinal()) {
         case 0 -> "";
         case 1 -> "Прокси";
         case 2 -> "Добавить сервер";
         case 3 -> "Изменить сервер";
         case 4 -> "Прямое подключение";
         default -> throw new MatchException(null, null);
      };
   }

   public String getConfirmText() {
      return this.c1 == ServerEditScreen.ModeKind.DIRECT ? "Подключиться" : "Применить";
   }

   public ServerEditScreen.DataRecord m_3_C5(float floatValue, float floatValue2) {
      int value3 = Math.max(1, this.m_6_int());
      float floatValue3 = 0.0F;
      if (this.isProxyMode()) {
         floatValue3 += 32.0F;
      }

      if (this.isAddOrEdit()) {
         floatValue3 += 38.0F;
      }

      if (this.isEditing()) {
         floatValue3 += 38.0F;
      }

      float floatValue4 = 16.0F + value3 * 28.0F + Math.max(0, value3 - 1) * 8 + floatValue3 + 16.0F;
      float floatValue5 = 46.0F + floatValue4 + 10.0F + 32.0F;
      float floatValue6 = (floatValue - 360.0F) / 2.0F;
      float floatValue7 = (floatValue2 - floatValue5) / 2.0F;
      float floatValue8 = floatValue7 + 36.0F + 10.0F;
      return new ServerEditScreen.DataRecord(floatValue6, floatValue7, floatValue8, floatValue4, floatValue8 + floatValue4 + 10.0F);
   }

   public ServerEditScreen.ModeKind2 m_7_C8(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      ServerEditScreen.DataRecord c5 = this.m_3_C5(floatValue3, floatValue4);
      int value3 = this.m_6_int();

      for (int i = 0; i < value3; i++) {
         if (isHovered(floatValue, floatValue2, c5.f_43 + 16.0F, c5.m_8_float(i), 328.0F, 28.0F)) {
            return ServerEditScreen.ModeKind2.FIELD;
         }
      }

      float floatValue5 = c5.m_4_float(value3);
      if (this.isProxyMode()) {
         if (isHovered(floatValue, floatValue2, c5.f_43 + 16.0F, floatValue5, 328.0F, 22.0F)) {
            return ServerEditScreen.ModeKind2.CHECK;
         }

         floatValue5 += 32.0F;
      }

      if (this.isAddOrEdit()) {
         if (isHovered(floatValue, floatValue2, c5.f_43 + 16.0F, floatValue5, 328.0F, 28.0F)) {
            return ServerEditScreen.ModeKind2.PACK;
         }

         floatValue5 += 38.0F;
      }

      if (this.isEditing() && isHovered(floatValue, floatValue2, c5.f_43 + 16.0F, floatValue5, 328.0F, 28.0F)) {
         return ServerEditScreen.ModeKind2.VIA;
      } else {
         float floatValue6 = c5.f_4_h + 4.0F;
         if (isHovered(floatValue, floatValue2, c5.f_43 + 4.0F, floatValue6, 174.0F, 24.0F)) {
            return ServerEditScreen.ModeKind2.CANCEL;
         } else {
            return isHovered(floatValue, floatValue2, c5.f_43 + 8.0F + 174.0F, floatValue6, 174.0F, 24.0F) ? ServerEditScreen.ModeKind2.APPLY : ServerEditScreen.ModeKind2.NONE;
         }
      }
   }

   public int m_0(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      ServerEditScreen.DataRecord c5 = this.m_3_C5(floatValue3, floatValue4);
      int value3 = this.m_6_int();

      for (int i = 0; i < value3; i++) {
         if (isHovered(floatValue, floatValue2, c5.f_43 + 16.0F, c5.m_8_float(i), 328.0F, 28.0F)) {
            return i;
         }
      }

      return -1;
   }

   public ServerEditScreen.ModeKind2 m_5(float floatValue, float floatValue2, float floatValue3, float floatValue4, boolean flag7) {
      ServerEditScreen.ModeKind2 c8 = this.m_7_C8(floatValue, floatValue2, floatValue3, floatValue4);
      switch (c8.ordinal()) {
         case 0:
            this.value = -1;

            for (ServerListEntry serverListEntry : this.serverListEntryArray) {
               serverListEntry.setFocused(false);
            }
            break;
         case 1:
            this.value = this.m_0(floatValue, floatValue2, floatValue3, floatValue4);

            for (int i = 0; i < this.serverListEntryArray.length; i++) {
               this.serverListEntryArray[i].setFocused(i == this.value);
            }

            this.serverListEntryArray[this.value].m_5_void(floatValue, flag7);
            break;
         case 2:
            this.flag2 = !this.flag2;
            break;
         case 3:
            this.value2 = (this.value2 + 1) % 3;
            break;
         case 4:
            this.flag5 = true;
            break;
         case 5:
            this.validate();
            break;
         case 6:
            this.flag = false;
            this.flag4 = true;
      }

      return c8;
   }

   public boolean m_7_boolean(int value3) {
      if (this.value >= 0 && this.value < this.m_6_int()) {
         this.serverListEntryArray[this.value].insert(String.valueOf((char)value3), this::isPrintable);
         this.text = "";
         return true;
      } else {
         return false;
      }
   }

   public void m_5_void() {
      int value3 = this.m_6_int();
      if (value3 > 0) {
         this.value = this.value < 0 ? 0 : (this.value + 1) % value3;

         for (int i = 0; i < this.serverListEntryArray.length; i++) {
            this.serverListEntryArray[i].setFocused(i == this.value);
         }
      }
   }

   public boolean m_7(KeyEvent event, Consumer<String> consumer, Supplier<String> supplier) {
      if (event.key() == 256) {
         this.reset();
         return true;
      }

      if (event.key() == 258) {
         this.m_5_void();
         return true;
      }

      if (event.isSelectAll()) {
         this.selectAllInField();
         return true;
      }

      if (event.isCopy()) {
         consumer.accept(this.getSelectedText());
         return true;
      }

      if (event.isPaste()) {
         this.pasteIntoField((String)supplier.get());
         return true;
      }

      if (event.isCut()) {
         consumer.accept(this.getSelectedText());
         this.deleteSelected();
         return true;
      }

      if (event.key() != 257 && event.key() != 335) {
         if (this.value >= 0 && this.value < this.m_6_int() && this.serverListEntryArray[this.value].onKey(event)) {
            this.text = "";
         }

         return true;
      } else {
         this.validate();
         return true;
      }
   }

   public void applyProxy() {
      ProxyState.proxyEntry = new ProxyEntry(ProxyEntry.ModeKind.SOCKS5, this.getEntryText(0), this.getEntryText(1), this.getEntryText(2));
      ProxyState.flag = this.isFlag2();
      ProxyConfig.m_6(ProxyState.proxyEntry);
      ProxyConfig.m_0_void();
   }

   public boolean validate() {
      if (this.c1 == ServerEditScreen.ModeKind.PROXY) {
         if (!isValidHostPort(this.serverListEntryArray[0].getText2())) {
            this.text = "Ошибка в написании IP адреса или порта";
            this.value = 0;
            return false;
         }
      } else {
         int value3 = this.c1 == ServerEditScreen.ModeKind.DIRECT ? 0 : 1;
         if (this.serverListEntryArray[value3].getText2().isBlank() || this.serverListEntryArray[value3].getText2().length() > 128) {
            this.text = "Укажи адрес сервера";
            this.value = value3;
            return false;
         }
      }

      this.flag3 = true;
      this.flag = false;
      return true;
   }

   public void m_3(float floatValue, float floatValue2, int value3, int value4, float floatValue3) {
      ServerEditScreen.DataRecord c5 = this.m_3_C5(floatValue, floatValue2);
      int value5 = this.m_6_int();
      ServerListEntry[] serverListEntryArray2 = this.serverListEntryArray;
      int value6 = serverListEntryArray2.length;
      ClickGUI.m_3_String();

      for (int i = 0; i < value6; i++) {
         ServerListEntry serverListEntry = serverListEntryArray2[i];
         if (serverListEntry.isFlag3()) {
            if (KeyNames.isLeftMouseDown()) {
               serverListEntry.m_6_void(value3);
            } else {
               serverListEntry.m_5_void();
            }
         }
      }

      Draw2D.drawPanel(c5.f_43, c5.f_4_i, 360.0F, 36.0F, floatValue3);
      this.m_1_void(c5.f_43, c5.f_4_i, floatValue3);
      Draw2D.drawPanel(c5.f_43, c5.f_4_e, 360.0F, c5.f_42, floatValue3);

      for (int i2 = 0; i2 < value5; i2++) {
         boolean flag7 = isHovered(value3, value4, c5.f_43 + 16.0F, c5.m_8_float(i2), 328.0F, 28.0F) || this.value == i2;
         this.m_9(c5.f_43, c5.m_8_float(i2), i2, this.animationArray[i2].update(flag7 ? 1.0F : 0.0F), floatValue3);
      }

      float floatValue4 = c5.m_4_float(value5);
      if (this.isProxyMode()) {
         boolean flag8 = isHovered(value3, value4, c5.f_43 + 16.0F, floatValue4, 328.0F, 22.0F);
         this.m_1_void(c5.f_43 + 16.0F, floatValue4, this.animation4.update(flag8 ? 1.0F : 0.0F), floatValue3);
         floatValue4 += 32.0F;
      }

      if (this.isAddOrEdit()) {
         boolean flag9 = isHovered(value3, value4, c5.f_43 + 16.0F, floatValue4, 328.0F, 28.0F);
         this.m_5(c5.f_43 + 16.0F, floatValue4, this.animation4.update(flag9 ? 1.0F : 0.0F), floatValue3);
         floatValue4 += 38.0F;
      }

      if (this.isEditing()) {
         boolean flag10 = isHovered(value3, value4, c5.f_43 + 16.0F, floatValue4, 328.0F, 28.0F);
         this.m_2(c5.f_43 + 16.0F, floatValue4, this.animation5.update(flag10 ? 1.0F : 0.0F), floatValue3);
      }

      float floatValue5 = c5.f_4_h + 4.0F;
      float floatValue6 = c5.f_43 + 4.0F;
      float floatValue7 = floatValue6 + 174.0F + 4.0F;
      Draw2D.drawPanel(c5.f_43, c5.f_4_h, 360.0F, 32.0F, floatValue3);
      Draw2D.m_3(floatValue6, floatValue5, 174.0F, 24.0F, "\uf060", "Отменить", this.animation3.update(isHovered(value3, value4, floatValue6, floatValue5, 174.0F, 24.0F) ? 1.0F : 0.0F), floatValue3);
      Draw2D.m_3(floatValue7, floatValue5, 174.0F, 24.0F, "\uf00c", this.getConfirmText(), this.animation2.update(isHovered(value3, value4, floatValue7, floatValue5, 174.0F, 24.0F) ? 1.0F : 0.0F), floatValue3);
      if (!this.text.isEmpty()) {
         float floatValue8 = Draw2D.getTextWidth(this.text, Fonts.SUPPLIER13, 11.0F);
         Draw2D.drawStyledText(this.text, Fonts.SUPPLIER13, (floatValue - floatValue8) / 2.0F, c5.f_4_h + 32.0F + 10.0F, ColorUtil.rgba(255, 110, 110, (int)(230.0F * floatValue3)), 11.0F);
      }
   }

   public static boolean isValidHostPort(String text3) {
      if (text3 != null && !text3.isEmpty()) {
         String[] stringArray = text3.split(":");
         if (stringArray.length < 2) {
            return false;
         }

         try {
            int value3 = Integer.parseInt(stringArray[1]);
            return value3 >= 0 && value3 <= 65535;
         } catch (NumberFormatException numberFormatException) {
            return false;
         }
      } else {
         return false;
      }
   }

   private void m_2_void() {
      String[] stringArray = switch (this.c1.ordinal()) {
         case 0 -> new String[]{"", "", ""};
         case 1 -> new String[]{"IP:Порт", "Логин (необязательно)", "Пароль (необязательно)"};
         case 2, 3 -> new String[]{"Название", "Адрес", ""};
         case 4 -> new String[]{"Адрес", "", ""};
         default -> throw new MatchException(null, null);
      };

      for (int i = 0; i < this.serverListEntryArray.length; i++) {
         this.serverListEntryArray[i].setText(stringArray[i]);
         this.serverListEntryArray[i].setFlag2(this.c1 == ServerEditScreen.ModeKind.PROXY && i == 2);
      }

      switch (this.c1.ordinal()) {
         case 0:
         default:
            break;
         case 1:
            this.serverListEntryArray[0].setMaxLength(128);
            this.serverListEntryArray[1].setMaxLength(255);
            this.serverListEntryArray[2].setMaxLength(255);
            break;
         case 2:
         case 3:
            this.serverListEntryArray[0].setMaxLength(32);
            this.serverListEntryArray[1].setMaxLength(128);
            break;
         case 4:
            this.serverListEntryArray[0].setMaxLength(128);
      }
   }

   private boolean isPrintable(int value3) {
      return value3 >= 32 && value3 != 127;
   }

   private String getResourcePackText() {
      String text3 = switch (this.value2) {
         case 0 -> "вкл";
         case 1 -> "выкл";
         default -> "спросить";
      };
      return "Ресурспак: " + text3;
   }

   private void m_1_void(float floatValue, float floatValue2, float floatValue3) {
      float floatValue4 = 26.0F;
      float floatValue5 = floatValue + 8.0F;
      float floatValue6 = floatValue2 + (36.0F - floatValue4) / 2.0F;
      Draw2D.m_8(
         floatValue5 + 0.5F,
         floatValue6 + 1.0F,
         floatValue4 - 1.0F,
         floatValue4 - 2.0F,
         6.0F,
         0.5F,
         ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(255.0F * floatValue3)),
         ColorUtil.withAlpha(ColorUtil.secondaryColor(), (int)(255.0F * floatValue3))
      );
      float floatValue7 = 30.0F;
      Draw2D.drawTextWithShadow(
         "\ue900",
         Fonts.SUPPLIER6,
         Draw2D.getCenteredX("\ue900", Fonts.SUPPLIER6, floatValue7, floatValue5, floatValue4),
         Draw2D.getCenteredY("\ue900", Fonts.SUPPLIER6, floatValue7, floatValue6, floatValue4),
         ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue3)),
         floatValue7
      );
      float floatValue8 = (float)(System.currentTimeMillis() % 4000L) / 4000.0F;
      Draw2D.m_9(
         this.getTitle(),
         Fonts.SUPPLIER18,
         floatValue5 + floatValue4 + 10.0F,
         Draw2D.getTextBaseline(Fonts.SUPPLIER18, 16.0F, floatValue2, 36.0F),
         ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(255.0F * floatValue3)),
         ColorUtil.withAlpha(ColorUtil.secondaryColor(), (int)(255.0F * floatValue3)),
         16.0F,
         floatValue8
      );
      Draw2D.drawStyledText(
         "\uf233", Fonts.SUPPLIER8, floatValue + 360.0F - 28.0F, Draw2D.getCenteredY("\uf233", Fonts.SUPPLIER8, 14.0F, floatValue2, 36.0F), ColorUtil.rgba(235, 235, 235, (int)(200.0F * floatValue3)), 14.0F
      );
   }

   private void m_9(float floatValue, float floatValue2, int value3, float floatValue3, float floatValue4) {
      float floatValue5 = floatValue + 16.0F;
      float floatValue6 = 328.0F;
      if (floatValue3 > 0.01F) {
         Draw2D.roundedRect(floatValue5, floatValue2, floatValue6, 28.0F, 5.0F, ColorUtil.rgba(255, 255, 255, (int)(14.0F * floatValue3 * floatValue4)));
      }

      Draw2D.roundedRect(floatValue5, floatValue2 + 28.0F - 1.0F, floatValue6, 1.0F, 0.0F, ColorUtil.rgba(255, 255, 255, (int)((this.value == value3 ? 180 : 70) * floatValue4)));
      this.serverListEntryArray[value3].m_3_void(floatValue5 + 6.0F, floatValue2, floatValue6 - 12.0F, 28.0F, floatValue4);
   }

   private void m_1_void(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      float floatValue5 = 16.0F;
      Draw2D.roundedRect(floatValue, floatValue2 + 3.0F, floatValue5, floatValue5, 4.0F, ColorUtil.rgba(255, 255, 255, (int)((18.0F + 20.0F * floatValue3) * floatValue4)));
      if (this.flag2) {
         Draw2D.drawStyledText("\uf00c", Fonts.SUPPLIER8, floatValue + 2.5F, Draw2D.getCenteredY("\uf00c", Fonts.SUPPLIER8, 11.0F, floatValue2 + 3.0F, floatValue5), ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(255.0F * floatValue4)), 11.0F);
      }

      Draw2D.drawStyledText("Включить прокси", Fonts.SUPPLIER14, floatValue + floatValue5 + 8.0F, Draw2D.getTextBaseline(Fonts.SUPPLIER14, 12.0F, floatValue2, 22.0F), ColorUtil.rgba(230, 230, 230, (int)(210.0F * floatValue4)), 12.0F);
   }

   private void m_5(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      float floatValue5 = 328.0F;
      if (floatValue3 > 0.01F) {
         Draw2D.roundedRect(floatValue, floatValue2, floatValue5, 28.0F, 5.0F, ColorUtil.rgba(255, 255, 255, (int)(14.0F * floatValue3 * floatValue4)));
      }

      Draw2D.drawStyledText(this.getResourcePackText(), Fonts.SUPPLIER14, floatValue + 8.0F, Draw2D.getTextBaseline(Fonts.SUPPLIER14, 12.0F, floatValue2, 28.0F), ColorUtil.rgba(230, 230, 230, (int)(210.0F * floatValue4)), 12.0F);
   }

   private void m_2(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      float floatValue5 = 328.0F;
      if (floatValue3 > 0.01F) {
         Draw2D.roundedRect(floatValue, floatValue2, floatValue5, 28.0F, 5.0F, ColorUtil.rgba(255, 255, 255, (int)(14.0F * floatValue3 * floatValue4)));
      }

      Draw2D.drawStyledText(this.getVersionText(), Fonts.SUPPLIER14, floatValue + 8.0F, Draw2D.getTextBaseline(Fonts.SUPPLIER14, 12.0F, floatValue2, 28.0F), ColorUtil.rgba(230, 230, 230, (int)(210.0F * floatValue4)), 12.0F);
   }

   private String getVersionText() {
      if (this.text2.isEmpty()) {
         return this.c1 == ServerEditScreen.ModeKind.DIRECT ? "ViaFabricPlus" : "Версия: не задана";
      } else {
         return "Версия: " + this.text2;
      }
   }

   private static boolean isHovered(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      return floatValue >= floatValue3 && floatValue <= floatValue3 + floatValue5 && floatValue2 >= floatValue4 && floatValue2 <= floatValue4 + floatValue6;
   }

   public enum ModeKind {
      NONE,
      PROXY,
      ADD,
      EDIT,
      DIRECT;

      private static String a(byte[] byteArray) {
         int value3 = 0;
         int value4;
         char[] charArray = new char[value4 = byteArray.length];

         for (int i = 0; i < value4; i++) {
            int value5;
            if ((value5 = 255 & byteArray[i]) < 192) {
               charArray[value3++] = (char)value5;
            } else if (value5 < 224) {
               char ch = (char)((char)(value5 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value3++] = ch;
            } else if (i < value4 - 2) {
               char ch2 = (char)((char)(value5 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value3++] = ch2;
            }
         }

         return new String(charArray, 0, value3);
      }
   }

   public record DataRecord(float f_43, float f_4_i, float f_4_e, float f_42, float f_4_h) {
      public float m_8_float(int value3) {
         return this.f_4_e + 16.0F + value3 * 36.0F;
      }

      public float m_4_float(int value3) {
         return this.m_8_float(Math.max(0, value3 - 1)) + 28.0F + 8.0F;
      }

      public float m_43() {
         return this.f_43;
      }

      public float m_4_i() {
         return this.f_4_i;
      }

      public float m_4_e() {
         return this.f_4_e;
      }

      public float m_42() {
         return this.f_42;
      }

      public float m_4_h() {
         return this.f_4_h;
      }
   }

   public enum ModeKind2 {
      NONE,
      FIELD,
      CHECK,
      PACK,
      VIA,
      APPLY,
      CANCEL;

      private static String a(byte[] byteArray) {
         int value3 = 0;
         int value4;
         char[] charArray = new char[value4 = byteArray.length];

         for (int i = 0; i < value4; i++) {
            int value5;
            if ((value5 = 255 & byteArray[i]) < 192) {
               charArray[value3++] = (char)value5;
            } else if (value5 < 224) {
               char ch = (char)((char)(value5 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value3++] = ch;
            } else if (i < value4 - 2) {
               char ch2 = (char)((char)(value5 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value3++] = ch2;
            }
         }

         return new String(charArray, 0, value3);
      }
   }
}
