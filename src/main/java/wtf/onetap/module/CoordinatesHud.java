package wtf.onetap.module;

import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.setting.ColorSetting;

/**
 * Note: in the Moonward source, renderCoordsInfo is an empty method, so there was no element to port. This version
 * is new code that reads the same position data as the rest of the HUD.
 */
@ModuleInformation(name = "Coordinates", description = "Координаты игрока на экране", category = Category.RENDER)
public final class CoordinatesHud extends HudElement {
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
      double px = MINECRAFT.player.getX();
      double py = MINECRAFT.player.getY();
      double pz = MINECRAFT.player.getZ();
      String text = "X: " + (int) Math.floor(px) + "  Y: " + (int) Math.floor(py) + "  Z: " + (int) Math.floor(pz);

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
