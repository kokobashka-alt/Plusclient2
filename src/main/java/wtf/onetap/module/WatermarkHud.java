package wtf.onetap.module;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;

@ModuleInformation(name = "Watermark", description = "Ватермарка в углу экрана", category = Category.RENDER)
public final class WatermarkHud extends HudElement {
   private static final String TITLE = "PLUSCLIENT";
   private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

   private final BooleanSetting showTime = new BooleanSetting("Время", true).build();

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      this.hudRender(event);
   }

   @EventTarget
   private void onMouseClick(MouseClickEvent event) {
      this.hudMouse(event);
   }

   @Override
   protected void drawElement(Render2DEvent event, float x, float y, float scale) {
      String text = this.showTime.getValue() ? TITLE + " | " + LocalTime.now().format(TIME) : TITLE;
      float fontSize = 7.5F * scale;
      float height = 14.0F * scale;
      float width = Draw2D.getTextWidth(text, Fonts.SUPPLIER4, fontSize) + 12.0F * scale;
      float radius = 4.0F * scale;

      int accent = accentColor();
      Draw2D.roundedRect(x - 0.5F, y - 0.5F, width + 1.0F, height + 1.0F, radius, accent);
      Draw2D.roundedRect(x, y, width, height, radius, ColorUtil.rgba(14, 10, 6, 255));

      float textY = Draw2D.getTextBaseline(Fonts.SUPPLIER4, fontSize, y, height);
      Draw2D.drawStyledText(text, Fonts.SUPPLIER4, x + 6.0F * scale, textY, ColorUtil.rgba(255, 255, 255, 255), fontSize);

      this.setSize(width, height);
   }

   private static int accentColor() {
      ColorSetting theme = Onetap.THEME_MANAGER.getColorSetting();
      return theme == null ? ColorUtil.rgba(120, 120, 255, 255) : theme.getValue();
   }
}
