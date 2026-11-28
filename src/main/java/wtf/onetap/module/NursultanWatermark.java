package wtf.onetap.module;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ClientPacketListener;
import wtf.onetap.Onetap;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.network.ServerTpsTracker;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.setting.ColorSetting;

/**
 * Nursultan watermark: a first row with the client name and the nickname, fps and time; a second row with
 * coordinates, ping, tps and speed. Icons come from the icons_nurik atlas, copied from the Moonward assets.
 *
 * Differences from the Moonward source: the nickname is shown instead of the hard-coded insulting line that the
 * source used, and the SF Medium font is replaced by Inter Medium, which is available in this client.
 */
final class NursultanWatermark extends NursultanElement {
   private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
   private static final String BRAND = "PLUSCLIENT";
   private static final int WHITE = ColorUtil.rgba(255, 255, 255, 255);
   private static final int SEPARATOR = ColorUtil.rgba(255, 255, 255, 100);
   private static final int PANEL = ColorUtil.rgba(14, 10, 6, 255);
   private static final int FALLBACK_A = ColorUtil.rgba(120, 120, 255, 255);
   private static final int FALLBACK_B = ColorUtil.rgba(180, 90, 255, 255);

   @Override
   void draw(Render2DEvent event, float x, float y, float scale) {
      float font = 7.0F * scale;
      float icon = 8.0F * scale;
      float height = 15.0F * scale;
      float gap = 3.0F * scale;
      float radius = 4.0F * scale;
      long time = System.currentTimeMillis();

      int themeA = themeColor(false);
      int themeB = themeColor(true);
      float pulse = (float) (Math.sin(time / 200.0) * 0.3 + 0.7);
      int pulsing = withAlpha(themeA, (int) (255 * pulse));

      // First row, box 1: logo and client name with a gradient across the letters.
      float logoBox = 17.0F * scale + 10.0F * scale + Draw2D.getTextWidth(BRAND, Fonts.SUPPLIER14, font);
      float startX = x;
      Draw2D.roundedRect(x, y, logoBox, height, radius, PANEL);
      float logoY = Draw2D.getTextBaseline(Fonts.SUPPLIER6, 15.0F * scale, y, height);
      Draw2D.drawStyledText("\ue900", Fonts.SUPPLIER6, x + 4.2F * scale, logoY, pulsing, 15.0F * scale);
      Draw2D.roundedRect(x + 18.0F * scale, y + 3.5F * scale, 0.5F * scale, height - 7.0F * scale, 0.2F * scale, SEPARATOR);
      float cx = x + 22.0F * scale;
      float textY = Draw2D.getTextBaseline(Fonts.SUPPLIER14, font, y, height);
      for (int i = 0; i < BRAND.length(); i++) {
         String ch = String.valueOf(BRAND.charAt(i));
         int color = lerpColor(themeA, themeB, time, i * 0.35F);
         Draw2D.drawStyledText(ch, Fonts.SUPPLIER14, cx, textY, color, font);
         cx += Draw2D.getTextWidth(ch, Fonts.SUPPLIER14, font);
      }

      // First row, box 2: nickname, fps, time.
      String nick = MINECRAFT.player.getName().getString();
      String fps = MINECRAFT.getFps() + " Fps";
      String clock = LocalTime.now().format(TIME);
      float nickW = Draw2D.getTextWidth(nick, Fonts.SUPPLIER14, font);
      float fpsW = Draw2D.getTextWidth(fps, Fonts.SUPPLIER14, font);
      float clockW = Draw2D.getTextWidth(clock, Fonts.SUPPLIER14, font);
      float firstRowX = x + logoBox + gap;
      float rowOneBox = 62.0F * scale + nickW + fpsW + clockW;
      Draw2D.roundedRect(firstRowX, y, rowOneBox, height, radius, PANEL);
      float px = firstRowX + 4.0F * scale;
      Draw2D.drawStyledText("W", Fonts.ICONS_NURIK, px, Draw2D.getTextBaseline(Fonts.ICONS_NURIK, icon, y, height), themeA, icon);
      Draw2D.drawStyledText(nick, Fonts.SUPPLIER14, px + 10.0F * scale, textY, WHITE, font);
      px += 11.0F * scale + nickW + 5.0F * scale;
      Draw2D.roundedRect(px, y + 3.5F * scale, 0.5F * scale, height - 7.0F * scale, 0.2F * scale, SEPARATOR);
      px += 6.0F * scale;
      Draw2D.drawStyledText("X", Fonts.ICONS_NURIK, px, Draw2D.getTextBaseline(Fonts.ICONS_NURIK, icon, y, height), themeA, icon);
      Draw2D.drawStyledText(fps, Fonts.SUPPLIER14, px + 11.0F * scale, textY, WHITE, font);
      px += 11.0F * scale + fpsW + 5.0F * scale;
      Draw2D.roundedRect(px, y + 3.5F * scale, 0.5F * scale, height - 7.0F * scale, 0.2F * scale, SEPARATOR);
      px += 6.0F * scale;
      Draw2D.drawStyledText("V", Fonts.ICONS_NURIK, px, Draw2D.getTextBaseline(Fonts.ICONS_NURIK, icon, y, height), themeA, icon);
      Draw2D.drawStyledText(clock, Fonts.SUPPLIER14, px + 11.0F * scale, textY, WHITE, font);
      float rowOneWidth = (firstRowX + rowOneBox) - startX;

      // Second row.
      float y2 = y + height + gap;
      float pulse2 = (float) (Math.sin((time + 150) / 250.0) * 0.3 + 0.7);
      int pulsing2 = withAlpha(themeA, (int) (255 * pulse2));
      float bx = x;
      Draw2D.roundedRect(bx, y2, 17.0F * scale, height, radius, PANEL);
      Draw2D.drawStyledText("U", Fonts.ICONS_NURIK, bx + 4.5F * scale, Draw2D.getTextBaseline(Fonts.ICONS_NURIK, icon, y2, height), pulsing2, icon);
      bx += 17.0F * scale + gap;

      String coords = (int) Math.floor(MINECRAFT.player.getX()) + " " + (int) Math.floor(MINECRAFT.player.getY()) + " " + (int) Math.floor(MINECRAFT.player.getZ());
      bx = this.infoBox(bx, y2, height, radius, gap, scale, "F", coords, 13.0F, 17.0F, themeA, font, icon);

      String ping = this.ping() + " Ping";
      bx = this.infoBox(bx, y2, height, radius, gap, scale, "Q", ping, 13.5F, 17.0F, themeA, font, icon);

      String tps = String.format(Locale.US, "%.1f Ticks", ServerTpsTracker.SERVER_TPS_TRACKER.getTps());
      bx = this.infoBox(bx, y2, height, radius, gap, scale, "T", tps, 13.0F, 17.0F, themeA, font, icon);

      double dx = MINECRAFT.player.getX() - MINECRAFT.player.xo;
      double dz = MINECRAFT.player.getZ() - MINECRAFT.player.zo;
      String speed = String.format(Locale.US, "%.1f Bps", Math.hypot(dx, dz) * 20.0);
      float speedBox = (20.0F + 4.0F) * scale + Draw2D.getTextWidth(speed, Fonts.SUPPLIER14, font);
      Draw2D.roundedRect(bx, y2, speedBox, height, radius, PANEL);
      Draw2D.drawStyledText("@", Fonts.ICONS_NURIK, bx + 4.0F * scale, Draw2D.getTextBaseline(Fonts.ICONS_NURIK, icon, y2, height), themeA, icon);
      Draw2D.roundedRect(bx + 15.0F * scale, y2 + 3.5F * scale, 0.5F * scale, height - 7.0F * scale, 0.2F * scale, SEPARATOR);
      Draw2D.drawStyledText(speed, Fonts.SUPPLIER14, bx + 20.0F * scale, Draw2D.getTextBaseline(Fonts.SUPPLIER14, font, y2, height), WHITE, font);
      float rowTwoWidth = (bx + speedBox) - startX;

      this.setSize(Math.max(rowOneWidth, rowTwoWidth), height * 2.0F + gap);
   }

   /** One icon panel of the second row: icon, separator, text. Returns the x where the next panel starts. */
   private float infoBox(float x, float y, float height, float radius, float gap, float scale, String iconGlyph, String text,
         float separatorAt, float textAt, int themeColor, float font, float icon) {
      float width = (textAt + 4.0F) * scale + Draw2D.getTextWidth(text, Fonts.SUPPLIER14, font);
      Draw2D.roundedRect(x, y, width, height, radius, PANEL);
      Draw2D.drawStyledText(iconGlyph, Fonts.ICONS_NURIK, x + 4.0F * scale, Draw2D.getTextBaseline(Fonts.ICONS_NURIK, icon, y, height), themeColor, icon);
      Draw2D.roundedRect(x + separatorAt * scale, y + 3.5F * scale, 0.5F * scale, height - 7.0F * scale, 0.2F * scale, SEPARATOR);
      Draw2D.drawStyledText(text, Fonts.SUPPLIER14, x + textAt * scale, Draw2D.getTextBaseline(Fonts.SUPPLIER14, font, y, height), WHITE, font);
      return x + width + gap;
   }

   private int ping() {
      ClientPacketListener connection = MINECRAFT.getConnection();
      if (connection == null || MINECRAFT.player == null) {
         return 0;
      }

      PlayerInfo info = connection.getPlayerInfo(MINECRAFT.player.getUUID());
      return info == null ? 0 : Math.max(0, info.getLatency());
   }

   private static int themeColor(boolean second) {
      ColorSetting setting = second ? Onetap.THEME_MANAGER.getColorSetting2() : Onetap.THEME_MANAGER.getColorSetting();
      if (setting == null) {
         return second ? FALLBACK_B : FALLBACK_A;
      }

      return setting.getValue();
   }

   /** Colour that moves between the two theme colours over time, with a per-letter offset. */
   private static int lerpColor(int from, int to, long time, float offset) {
      float t = (float) (Math.sin(time / 800.0 + offset) + 1.0) / 2.0F;
      int r = (int) (((from >> 16) & 255) * (1 - t) + ((to >> 16) & 255) * t);
      int g = (int) (((from >> 8) & 255) * (1 - t) + ((to >> 8) & 255) * t);
      int b = (int) ((from & 255) * (1 - t) + (to & 255) * t);
      return ColorUtil.rgba(r, g, b, 255);
   }
}
