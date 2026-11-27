package wtf.onetap.gui;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;

/**
 * Divine-style title screen layout: background textures, a 'PlusClient' title, five buttons on the right
 * and a copyright line. Drawn on top of the existing TitleScreenRenderer (see its m_9 / m_8 hooks).
 * ENABLED = false restores the original Plusclient menu.
 */
public final class DivineMainMenu {
   public static final boolean ENABLED = true;

   public static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath("onetap", "textures/gui/background.png");
   public static final Identifier BLACK_BACKGROUND = Identifier.fromNamespaceAndPath("onetap", "textures/gui/black_background.png");
   public static final Identifier BLUE_BACKGROUND = Identifier.fromNamespaceAndPath("onetap", "textures/gui/blue_background.png");

   public static final int BUTTON_SINGLEPLAYER = 0;
   public static final int BUTTON_MULTIPLAYER = 1;
   public static final int BUTTON_ACCOUNTS = 2;
   public static final int BUTTON_SETTINGS = 3;
   public static final int BUTTON_QUIT = 4;

   private static final String TITLE = "PlusClient";
   private static final String[] LABELS = {"Одиночная игра", "Сетевая игра", "Аккаунты", "Настройки", "Выйти"};
   private static final String COPYRIGHT = "Авторское право © PlusClient 2026";

   private static final float PANEL_W = 160.0F;
   private static final float BTN_W = 140.0F;
   private static final float BTN_H = 22.0F;
   private static final float BTN_GAP = 8.0F;
   private static final float TITLE_SIZE = 20.0F;
   private static final float BTN_TEXT_SIZE = 16.0F;

   private DivineMainMenu() {
   }

   /** Full-window background, same stacking order as the Divine screen. */
   public static void drawBackground(GpuTextureView background, GpuTextureView black, GpuTextureView blue, int windowWidth, int windowHeight) {
      drawTexture(background, 0.0F, 0.0F, windowWidth, windowHeight, ColorUtil.rgba(255, 255, 255, 255));
      drawTexture(black, 0.0F, 0.0F, windowWidth, windowHeight, ColorUtil.rgba(255, 255, 255, 200));
      drawTexture(blue, 0.0F, 0.0F, windowWidth, windowHeight, ColorUtil.rgba(255, 255, 255, 255));
   }

   public static void draw(int windowWidth, int windowHeight, int mouseX, int mouseY) {
      float iconSize = windowHeight * 0.55F;
      float iconX = windowWidth * 0.08F;
      float iconY = (windowHeight - iconSize) / 2.0F;
      drawPlus(iconX + iconSize / 2.0F, iconY + iconSize / 2.0F, iconSize);

      float panelX = windowWidth - PANEL_W - 30.0F;
      float titleY = windowHeight / 2.0F - 95.0F;
      float titleX = panelX + (PANEL_W - Draw2D.getTextWidth(TITLE, Fonts.SUPPLIER4, TITLE_SIZE)) / 2.0F;
      Draw2D.drawStyledText(TITLE, Fonts.SUPPLIER4, titleX, titleY, ColorUtil.rgba(255, 255, 255, 255), TITLE_SIZE);

      float btnX = panelX + (PANEL_W - BTN_W) / 2.0F;
      float btnStartY = titleY + Draw2D.getFontHeight(Fonts.SUPPLIER4, TITLE_SIZE) + 16.0F;
      for (int i = 0; i < LABELS.length; i++) {
         float by = buttonY(btnStartY, i);
         boolean hovered = isInside(mouseX, mouseY, btnX, by);
         Draw2D.roundedRect(btnX, by, BTN_W, BTN_H, 6.0F, hovered ? ColorUtil.rgba(31, 31, 31, 255) : ColorUtil.rgba(9, 9, 9, 255));
         int textColor = ColorUtil.rgba(255, 255, 255, hovered ? 255 : 140);
         String label = LABELS[i];
         float tx = Draw2D.getCenteredX(label, Fonts.SUPPLIER, BTN_TEXT_SIZE, btnX, BTN_W);
         float ty = Draw2D.getCenteredY(label, Fonts.SUPPLIER, BTN_TEXT_SIZE, by, BTN_H);
         Draw2D.drawStyledText(label, Fonts.SUPPLIER, tx, ty, textColor, BTN_TEXT_SIZE);
      }

      float cw = Draw2D.getTextWidth(COPYRIGHT, Fonts.SUPPLIER, 12.0F);
      Draw2D.drawStyledText(COPYRIGHT, Fonts.SUPPLIER, windowWidth / 2.0F - cw / 2.0F, windowHeight - 10.0F, ColorUtil.rgba(255, 255, 255, 80), 12.0F);
   }

   /** Big "+" in place of the Divine logo: two rounded bars centred on the given point. */
   private static void drawPlus(float centerX, float centerY, float size) {
      float length = size * 0.8F;
      float thickness = size * 0.12F;
      int white = ColorUtil.rgba(255, 255, 255, 255);
      Draw2D.roundedRect(centerX - length / 2.0F, centerY - thickness / 2.0F, length, thickness, thickness / 2.0F, white);
      Draw2D.roundedRect(centerX - thickness / 2.0F, centerY - length / 2.0F, thickness, length, thickness / 2.0F, white);
   }

   /** Returns the button index under the cursor, or -1. */
   public static int hitButton(double mouseX, double mouseY, int windowWidth, int windowHeight) {
      float panelX = windowWidth - PANEL_W - 30.0F;
      float titleY = windowHeight / 2.0F - 95.0F;
      float btnX = panelX + (PANEL_W - BTN_W) / 2.0F;
      float btnStartY = titleY + Draw2D.getFontHeight(Fonts.SUPPLIER4, TITLE_SIZE) + 16.0F;
      for (int i = 0; i < LABELS.length; i++) {
         if (isInside(mouseX, mouseY, btnX, buttonY(btnStartY, i))) {
            return i;
         }
      }
      return -1;
   }

   private static float buttonY(float btnStartY, int index) {
      float by = btnStartY + index * (BTN_H + BTN_GAP);
      return index == BUTTON_QUIT ? by + BTN_GAP * 2.0F : by;
   }

   private static boolean isInside(double mouseX, double mouseY, float x, float y) {
      return mouseX >= x && mouseX <= x + BTN_W && mouseY >= y && mouseY <= y + BTN_H;
   }

   public static GpuTextureView view(Identifier identifier) {
      return Minecraft.getInstance().getTextureManager().getTexture(identifier).getTextureView();
   }

   private static void drawTexture(GpuTextureView view, float x, float y, float w, float h, int color) {
      Render2D.m_4(x, y, w, h, 0.0F, color, view);
   }
}
