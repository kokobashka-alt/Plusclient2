package wtf.onetap.module;

import java.util.Locale;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;

/** Horizontal speed of the local player in blocks per second, from the change in position over one tick. */
@ModuleInformation(name = "SpeedHud", description = "Скорость игрока в блоках в секунду", category = Category.RENDER)
public final class SpeedHud extends HudElement {
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
      double dx = MINECRAFT.player.getX() - MINECRAFT.player.xo;
      double dy = MINECRAFT.player.getY() - MINECRAFT.player.yo;
      double dz = MINECRAFT.player.getZ() - MINECRAFT.player.zo;
      double bps = Math.sqrt(dx * dx + dy * dy + dz * dz) * 20.0;
      String text = String.format(Locale.US, "%.2f", bps);

      float fontSize = 11.0F * scale;
      float width = Draw2D.getTextWidth(text, Fonts.SUPPLIER4, fontSize) + 10.0F * scale;
      float height = 12.0F * scale;

      float textY = Draw2D.getTextBaseline(Fonts.SUPPLIER4, fontSize, y, height);
      Draw2D.drawStyledText(text, Fonts.SUPPLIER4, x + 5.0F * scale, textY, ColorUtil.rgba(255, 255, 255, 255), fontSize);

      this.setSize(width, height);
   }
}
