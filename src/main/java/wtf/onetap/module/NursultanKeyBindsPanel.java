package wtf.onetap.module;

import java.util.ArrayList;
import java.util.List;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.util.KeyNames;

/**
 * Nursultan hotkeys list: a header with the "C" icon and "Hotkeys" title, then one row per enabled module that has a
 * bind. The list fades in while any module is bound or the chat is open, and each row grows in when its module is
 * enabled. The width follows the widest row.
 *
 * Font note: the Moonward source used SF Regular and SF Medium, which this client does not have, so Inter Medium is
 * used for both.
 */
final class NursultanKeyBindsPanel extends NursultanElement {
   private static final int PANEL = ColorUtil.rgba(14, 10, 6, 255);
   private static final int SEPARATOR = ColorUtil.rgba(125, 125, 125, 255);
   private static final int WHITE = ColorUtil.rgba(255, 255, 255, 255);

   private final Animation fade = new Animation(233L, 0.0F, Easing.EASING8);
   private final Animation widthAnim = new Animation(200L, 0.0F, Easing.EASING8);
   private final Animation separatorShift = new Animation(170L, 0.0F, Easing.EASING8);

   @Override
   void draw(Render2DEvent event, float x, float y, float scale) {
      List<Module> rows = new ArrayList<>();
      boolean anyBound = false;
      for (Module module : Onetap.MODULE_MANAGER.getModules()) {
         if (module.isEnabled() && module.getKey() != -1) {
            anyBound = true;
         }

         if (module.getKey() != -1 && module.getAnimationProgress() > 0.001F) {
            rows.add(module);
         }
      }

      boolean chat = chatOpen(MINECRAFT);
      float global = this.fade.update(anyBound || chat ? 1.0F : 0.0F);
      if (global <= 0.05F) {
         return;
      }

      float header = 14.5F * scale;
      float bindWidth = 0.0F;
      for (Module module : rows) {
         bindWidth = Math.max(bindWidth, textWidth(KeyNames.getKeyName(module.getKey()), 6.75F * scale));
      }

      this.separatorShift.update(bindWidth);

      float targetWidth = 55.0F * scale;
      for (Module module : rows) {
         float elements = textWidth(module.getName(), 6.75F * scale)
               + textWidth(KeyNames.getKeyName(module.getKey()), 6.75F * scale)
               + 30.0F * scale;
         targetWidth = Math.max(targetWidth, elements);
      }

      float w = this.widthAnim.update(targetWidth);
      int headerAlpha = alpha(255.0F * global);

      Draw2D.roundedRect(x, y, w, header, 3.0F * scale, withAlpha(PANEL, headerAlpha));
      Draw2D.roundedRect(x + 15.25F * scale, y + 2.0F * scale, 0.5F * scale, 10.5F * scale, 0.0F, withAlpha(SEPARATOR, headerAlpha));
      Draw2D.drawStyledText("C", Fonts.ICONS_NURIK, x + 4.0F * scale,
            Draw2D.getTextBaseline(Fonts.ICONS_NURIK, 8.0F * scale, y, header), withAlpha(WHITE, headerAlpha), 8.0F * scale);
      Draw2D.drawStyledText("Hotkeys", Fonts.SUPPLIER14, x + 19.5F * scale,
            Draw2D.getTextBaseline(Fonts.SUPPLIER14, 7.5F * scale, y, header), withAlpha(WHITE, headerAlpha), 7.5F * scale);

      float totalHeight = header;
      float rowY = y + header;
      for (Module module : rows) {
         float progress = (float) module.getAnimationProgress();
         float rowHeight = 12.0F * scale * Math.min(1.0F, progress);
         totalHeight += rowHeight;

         int rowAlpha = alpha(255.0F * Math.min(1.0F, Math.max(0.0F, progress)) * global);
         if (rowAlpha < 5) {
            rowY += rowHeight;
            continue;
         }

         String bind = KeyNames.getKeyName(module.getKey());
         Draw2D.roundedRect(x, rowY, w, rowHeight, 3.0F * scale, withAlpha(PANEL, rowAlpha));

         float separatorX = x + w - 6.5F * scale - this.separatorShift.get();
         Draw2D.roundedRect(separatorX, rowY + 2.0F * scale, 0.5F * scale, rowHeight - 4.0F * scale, 0.0F, withAlpha(SEPARATOR, rowAlpha));

         float nameY = Draw2D.getTextBaseline(Fonts.SUPPLIER14, 6.5F * scale, rowY, rowHeight);
         Draw2D.drawStyledText(module.getName(), Fonts.SUPPLIER14, x + 4.25F * scale, nameY, withAlpha(WHITE, rowAlpha), 6.5F * scale);

         float bindX = x + w - 2.5F * scale - this.separatorShift.get()
               - textWidth(bind, 6.75F * scale) / 2.0F + this.separatorShift.get() / 2.0F - 0.25F * scale;
         Draw2D.drawStyledText(bind, Fonts.SUPPLIER14, bindX, nameY, withAlpha(WHITE, rowAlpha), 6.5F * scale);

         rowY += rowHeight;
      }

      this.setSize(w, totalHeight);
   }

   private static float textWidth(String text, float size) {
      return Draw2D.getTextWidth(text, Fonts.SUPPLIER14, size);
   }

   private static int alpha(float value) {
      return (int) Math.max(0.0F, Math.min(255.0F, value));
   }
}
