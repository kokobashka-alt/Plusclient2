package wtf.onetap.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;

/**
 * Nursultan active potions list. A row appears when an effect becomes active and fades out when it ends. Rows are
 * sorted by name, show the amplifier when it is above zero, the remaining time, and the effect icon from the gui atlas.
 *
 * The effect list is read on every rendered frame, so no tick hook is needed. The Moonward source updated it on the
 * tick instead, which gives the same result one frame later at most.
 *
 * Font note: SF Regular and SF Medium from the source are replaced by Inter Medium.
 */
final class NursultanPotionsPanel extends NursultanElement {
   private static final Identifier GUI_ATLAS = Identifier.withDefaultNamespace("gui");
   private static final int PANEL = ColorUtil.rgba(14, 10, 6, 255);
   private static final int SEPARATOR = ColorUtil.rgba(88, 88, 88, 255);
   private static final int WHITE = ColorUtil.rgba(255, 255, 255, 255);
   private static final int AMPLIFIER = ColorUtil.rgba(211, 211, 211, 255);

   private final List<Row> rows = new ArrayList<>();
   private final Animation fade = new Animation(233L, 0.0F, Easing.EASING8);
   private final Animation widthAnim = new Animation(200L, 0.0F, Easing.EASING8);
   private final Animation separatorShift = new Animation(170L, 0.0F, Easing.EASING8);

   @Override
   void draw(Render2DEvent event, float x, float y, float scale) {
      this.refresh();
      this.rows.sort(Comparator.comparing(row -> row.name));

      float headerIcon = Draw2D.getTextWidth("E", Fonts.ICONS_NURIK, 8.0F * scale);
      float headerText = Draw2D.getTextWidth("Active Potions", Fonts.SUPPLIER14, 7.5F * scale);
      float targetWidth = headerIcon + headerText + 30.0F * scale;

      boolean anyVisible = false;
      for (Row row : this.rows) {
         row.progress = row.animation.update(row.active ? 1.0F : 0.0F);
         if (row.progress > 0.001F) {
            anyVisible = true;
            float name = Draw2D.getTextWidth(row.name, Fonts.SUPPLIER14, 6.5F * scale);
            float amplifier = row.amplifier >= 1 ? Draw2D.getTextWidth(" " + (row.amplifier + 1), Fonts.SUPPLIER14, 6.5F * scale) : 0.0F;
            float time = Draw2D.getTextWidth(duration(row.durationTicks), Fonts.SUPPLIER14, 6.5F * scale);
            targetWidth = Math.max(targetWidth, name + amplifier + time + 45.0F * scale);
         }
      }

      boolean chat = chatOpen(MINECRAFT);
      float global = this.fade.update(anyVisible || chat ? 1.0F : 0.0F);
      if (global <= 0.05F) {
         return;
      }

      float header = 14.5F * scale;
      float panelWidth = Math.max(20.0F * scale, this.widthAnim.update(targetWidth)) - 3.0F * scale;
      int headerAlpha = alpha(255.0F * global);

      Draw2D.roundedRect(x, y, panelWidth, header, 3.0F * scale, withAlpha(PANEL, headerAlpha));
      Draw2D.roundedRect(x + 13.75F * scale, y + 2.0F * scale, 0.5F * scale, 10.5F * scale, 0.0F, withAlpha(SEPARATOR, headerAlpha));
      Draw2D.drawStyledText("E", Fonts.ICONS_NURIK, x + 4.0F * scale,
            Draw2D.getTextBaseline(Fonts.ICONS_NURIK, 8.0F * scale, y, header), withAlpha(WHITE, headerAlpha), 8.0F * scale);
      Draw2D.drawStyledText("Active Potions", Fonts.SUPPLIER14, x + 18.0F * scale,
            Draw2D.getTextBaseline(Fonts.SUPPLIER14, 7.5F * scale, y, header), withAlpha(WHITE, headerAlpha), 7.5F * scale);

      float separatorX = x + panelWidth + 3.0F * scale - 6.5F * scale - this.separatorShift.update(12.0F * scale);
      float totalHeight = header;
      float rowY = y + header;

      for (Row row : this.rows) {
         if (row.progress <= 0.001F) {
            continue;
         }

         float rowHeight = 12.0F * scale * Math.min(1.0F, row.progress);
         totalHeight += rowHeight;
         int rowAlpha = alpha(255.0F * Math.min(1.0F, Math.max(0.0F, row.progress)) * global);
         if (rowAlpha < 5) {
            rowY += rowHeight;
            continue;
         }

         Draw2D.roundedRect(x, rowY, panelWidth, rowHeight, 3.0F * scale, withAlpha(PANEL, rowAlpha));
         Draw2D.roundedRect(separatorX, rowY + 2.0F * scale, 0.5F * scale, rowHeight - 4.0F * scale, 0.0F, withAlpha(SEPARATOR, rowAlpha));

         float textY = Draw2D.getTextBaseline(Fonts.SUPPLIER14, 6.5F * scale, rowY, rowHeight);
         Draw2D.drawStyledText(row.name, Fonts.SUPPLIER14, x + 4.0F * scale, textY, withAlpha(WHITE, rowAlpha), 6.5F * scale);

         if (row.amplifier >= 1) {
            float amplifierX = x + 6.0F * scale + Draw2D.getTextWidth(row.name, Fonts.SUPPLIER14, 6.75F * scale);
            Draw2D.drawStyledText(String.valueOf(row.amplifier + 1), Fonts.SUPPLIER14, amplifierX, textY, withAlpha(AMPLIFIER, rowAlpha), 6.5F * scale);
         }

         String time = duration(row.durationTicks);
         float timeX = separatorX - Draw2D.getTextWidth(time, Fonts.SUPPLIER14, 6.75F * scale) - 3.0F * scale;
         Draw2D.drawStyledText(time, Fonts.SUPPLIER14, timeX, textY, withAlpha(WHITE, rowAlpha), 6.5F * scale);

         TextureAtlasSprite sprite = effectSprite(row.effect);
         if (sprite != null) {
            float iconSize = 9.0F * scale;
            float iconX = separatorX + 3.5F * scale;
            float iconY = rowY + (rowHeight - iconSize) / 2.0F;
            TextureAtlas atlas = MINECRAFT.getAtlasManager().getAtlasOrThrow(GUI_ATLAS);
            Draw2D.m_0(iconX, iconY, iconSize, iconSize, withAlpha(WHITE, rowAlpha), atlas.getTextureView(),
                  sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1());
         }

         rowY += rowHeight;
      }

      this.setSize(panelWidth + 3.0F * scale, totalHeight);
   }

   /** Rebuilds the row list from the player's active effects. Rows keep their animation between frames. */
   private void refresh() {
      if (MINECRAFT.player == null) {
         return;
      }

      Map<String, MobEffectInstance> current = new LinkedHashMap<>();
      for (MobEffectInstance effect : MINECRAFT.player.getActiveEffects()) {
         current.putIfAbsent(name(effect) + ":" + effect.getAmplifier(), effect);
      }

      for (Row row : this.rows) {
         MobEffectInstance effect = current.remove(row.name + ":" + row.amplifier);
         if (effect != null) {
            if (!row.active) {
               row.animation.reset(1.0F);
            }

            row.durationTicks = effect.getDuration();
            row.active = true;
         } else {
            row.active = false;
         }
      }

      for (MobEffectInstance effect : current.values()) {
         this.rows.add(new Row(name(effect), effect.getAmplifier(), effect.getEffect(), effect.getDuration()));
      }

      this.rows.removeIf(row -> !row.active && row.animation.get() <= 0.0F);
   }

   private static TextureAtlasSprite effectSprite(Holder<MobEffect> effect) {
      return MINECRAFT.getAtlasManager().getAtlasOrThrow(GUI_ATLAS).getSprite(Hud.getMobEffectSprite(effect));
   }

   private static String name(MobEffectInstance effect) {
      return Component.translatable(effect.getDescriptionId()).getString();
   }

   private static String duration(int ticks) {
      int seconds = ticks / 20;
      return String.format("%d:%02d", seconds / 60, seconds % 60);
   }

   private static int alpha(float value) {
      return (int) Math.max(0.0F, Math.min(255.0F, value));
   }

   private static final class Row {
      private final String name;
      private final int amplifier;
      private final Holder<MobEffect> effect;
      private final Animation animation = new Animation(250L, 0.0F, Easing.EASING8);
      private int durationTicks;
      private boolean active = true;
      private float progress;

      private Row(String name, int amplifier, Holder<MobEffect> effect, int durationTicks) {
         this.name = name;
         this.amplifier = amplifier;
         this.effect = effect;
         this.durationTicks = durationTicks;
      }
   }
}
