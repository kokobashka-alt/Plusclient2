package wtf.onetap.gui;

import wtf.onetap.render.ColorUtil;

/**
 * Divine colour palette used by the restyled ClickGUI. Values come from Divine's default theme
 * ("Фиолетовая"), mapped by ThemeSettings order: MAIN, MODULE_VISUAL, TEXT, TEXT_INACTIVE, BUTTON,
 * BUTTON_INACTIVE, OUTLINE, TOOLTIP, TOOLTIP_BG.
 */
public final class DivineStyle {
   public static final int MAIN = hex("#130C19E1");
   public static final int ACCENT = hex("#AD73FFFF");
   public static final int TEXT = hex("#FFFFFFFF");
   public static final int TEXT_INACTIVE = hex("#FFFFFFAF");
   public static final int BUTTON = hex("#4E3366FF");
   public static final int BUTTON_INACTIVE = hex("#3B264C78");
   public static final int OUTLINE = hex("#67459932");
   public static final int TOOLTIP = hex("#FFFFFFFF");
   public static final int TOOLTIP_BG = hex("#130C19E1");

   private DivineStyle() {
   }

   /** Parses Divine's "#RRGGBBAA" notation into the packed colour used by ColorUtil. */
   private static int hex(String s) {
      int v = (int) Long.parseLong(s.substring(1), 16);
      return ColorUtil.rgba((v >> 24) & 0xFF, (v >> 16) & 0xFF, (v >> 8) & 0xFF, v & 0xFF);
   }
}
