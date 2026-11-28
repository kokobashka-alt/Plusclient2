package wtf.onetap.module;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.GameType;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.PlayerFaceTexture;
import wtf.onetap.render.TextRenderer;

/**
 * Nursultan "Staff Online" list. A player is listed when the tab prefix or the nickname looks like a staff role, or
 * when the player is on the client's staff list. A player who is not in the connection's player info is considered
 * vanished. Rows show the skin head, the tab prefix cut after the nickname, and a dot: green when online, red when
 * vanished or in spectator mode.
 *
 * Omitted from the Moonward source: a built-in list of shop bot names that was applied only on two specific server
 * addresses. It was server data, not part of the HUD, so it is not carried over.
 *
 * Font note: SF Medium is replaced by Inter Medium. The role pattern is copied unchanged from the source and may need
 * editing for other servers.
 */
final class NursultanStaffPanel extends NursultanElement {
   private static final Pattern NAME = Pattern.compile("^\\w{3,16}$");
   private static final Pattern ROLE = Pattern.compile(
         ".*(ꔷ|ꔳ|ꔩ|ꔥ|ꔡ|ꔗ|ꔓ|\\bmod\\b|\\badm\\b|\\bhelp\\b|\\bwne\\b|модер|хелп|помощ|админ|владел|отриц|\\btaf\\b|\\bcurat\\b|куратор|\\bdev\\b|разраб|\\bsupp\\b|саппорт|\\byt\\b|\\[yt\\]|ютуб|стажер|сотрудник).*");
   private static final int PANEL = ColorUtil.rgba(14, 10, 6, 255);
   private static final int SEPARATOR = ColorUtil.rgba(88, 88, 88, 255);
   private static final int WHITE = ColorUtil.rgba(255, 255, 255, 255);
   private static final int ONLINE = ColorUtil.rgba(32, 255, 32, 255);
   private static final int VANISHED = ColorUtil.rgba(255, 32, 32, 255);

   private final List<Entry> staff = new ArrayList<>();
   private final Animation fade = new Animation(233L, 0.0F, Easing.EASING8);
   private final Animation widthAnim = new Animation(200L, 0.0F, Easing.EASING8);

   @Override
   void draw(Render2DEvent event, float x, float y, float scale) {
      this.refresh();

      boolean chat = chatOpen(MINECRAFT);
      float global = this.fade.update(!this.staff.isEmpty() || chat ? 1.0F : 0.0F);
      if (global <= 0.05F) {
         return;
      }

      float header = 14.5F * scale;
      float targetWidth = 64.0F * scale;
      for (Entry entry : this.staff) {
         float prefix = entry.prefixRenderer().getWidth(6.75F * scale);
         targetWidth = Math.max(targetWidth, 12.0F * scale + prefix + 15.0F * scale);
      }

      float w = this.widthAnim.update(targetWidth);
      int headerAlpha = alpha(255.0F * global);

      Draw2D.roundedRect(x, y, w, header, 3.0F * scale, withAlpha(PANEL, headerAlpha));
      Draw2D.roundedRect(x + 15.25F * scale, y + 2.0F * scale, 0.5F * scale, 10.5F * scale, 0.0F, withAlpha(SEPARATOR, headerAlpha));
      Draw2D.drawStyledText("O", Fonts.ICONS_NURIK, x + 4.25F * scale,
            Draw2D.getTextBaseline(Fonts.ICONS_NURIK, 8.5F * scale, y, header), withAlpha(WHITE, headerAlpha), 8.5F * scale);
      Draw2D.drawStyledText("Staff Online", Fonts.SUPPLIER14, x + 19.5F * scale,
            Draw2D.getTextBaseline(Fonts.SUPPLIER14, 7.5F * scale, y, header), withAlpha(WHITE, headerAlpha), 7.5F * scale);

      float totalHeight = header;
      float rowY = y + header;
      for (Entry entry : this.staff) {
         float progress = entry.animation.update(entry.onServer ? 1.0F : 0.0F);
         if (progress <= 0.001F) {
            continue;
         }

         float rowHeight = 11.0F * scale * Math.min(1.0F, progress);
         totalHeight += rowHeight;
         int rowAlpha = alpha(255.0F * Math.min(1.0F, progress) * global);
         if (rowAlpha < 5) {
            rowY += rowHeight;
            continue;
         }

         Draw2D.roundedRect(x, rowY, w, rowHeight, 3.0F * scale, withAlpha(PANEL, rowAlpha));
         Draw2D.roundedRect(x + w - 11.25F * scale, rowY + 2.0F * scale, 0.5F * scale, rowHeight - 4.0F * scale, 0.0F, withAlpha(SEPARATOR, rowAlpha));

         float headSize = 8.0F * scale;
         float headX = x + 3.0F * scale;
         float headY = rowY + (rowHeight - headSize) / 2.0F;
         if (entry.profile != null) {
            GpuTextureView face = PlayerFaceTexture.getFace(entry.profile);
            if (face != null) {
               Draw2D.m_4(headX, headY, headSize, headSize, 2.0F * scale, withAlpha(WHITE, rowAlpha), face);
            }
         }

         float prefixY = Draw2D.getTextBaseline(Fonts.SUPPLIER14, 6.5F * scale, rowY, rowHeight);
         entry.prefixRenderer().draw(x + 2.0F * scale + 12.0F * scale, prefixY, 6.75F * scale, rowAlpha);

         int dot = entry.status == Status.NONE ? ONLINE : VANISHED;
         Draw2D.roundedRect(x + w - 8.0F * scale, rowY + (rowHeight - 5.0F * scale) / 2.0F, 5.0F * scale, 5.0F * scale, 2.0F * scale, withAlpha(dot, rowAlpha));

         rowY += rowHeight;
      }

      this.setSize(w, totalHeight);
   }

   /** Rebuilds the staff list from the tab list. Entries that have left stay until their fade-out finishes. */
   private void refresh() {
      ClientPacketListener connection = MINECRAFT.getConnection();
      if (connection == null || MINECRAFT.player == null) {
         return;
      }

      for (Entry entry : this.staff) {
         entry.onServer = false;
      }

      String self = MINECRAFT.player.getName().getString();
      for (PlayerInfo info : connection.getOnlinePlayers()) {
         String name = info.getProfile().name().replaceAll("[\\[\\]]", "");
         PlayerInfo lookup = connection.getPlayerInfo(name);
         boolean vanish = lookup == null;
         boolean spectator = lookup != null && lookup.getGameMode() == GameType.SPECTATOR;

         Component tab = info.getTabListDisplayName();
         boolean rolePrefix = ROLE.matcher(tab == null ? "" : tab.getString().toLowerCase(Locale.ROOT)).matches();
         boolean validName = NAME.matcher(name).matches();
         boolean notSelf = !name.equals(self);
         boolean manual = Onetap.STAFF_MANAGER.isStaff(name);

         if (!((validName && notSelf && rolePrefix) || (validName && notSelf && vanish) || manual)) {
            continue;
         }

         Status status = vanish || spectator ? Status.VANISHED : Status.NONE;
         Optional<Entry> existing = this.staff.stream().filter(entry -> entry.name.equals(name)).findFirst();
         if (existing.isPresent()) {
            existing.get().onServer = true;
            existing.get().status = status;
            existing.get().profile = info.getProfile();
         } else {
            Component prefix = tab == null ? Component.literal(name) : cutAfterNick(tab, name);
            Entry entry = new Entry(prefix, name, status, info.getProfile());
            entry.onServer = true;
            this.staff.add(entry);
         }
      }

      this.staff.removeIf(entry -> !entry.onServer && entry.animation.get() <= 0.0F);
   }

   /** Keeps the tab prefix up to and including the nickname, with the styles of the parts that are kept. */
   private static Component cutAfterNick(Component prefix, String name) {
      String full = prefix.getString();
      int index = full.indexOf(name);
      if (index == -1 || index + name.length() >= full.length()) {
         return prefix;
      }

      int end = index + name.length();
      MutableComponent out = Component.empty();
      int[] length = new int[1];
      prefix.visit((style, text) -> {
         if (length[0] >= end) {
            return Optional.empty();
         }

         int take = Math.min(text.length(), end - length[0]);
         if (take > 0) {
            out.append(Component.literal(text.substring(0, take)).setStyle(style));
            length[0] += take;
         }

         return Optional.empty();
      }, Style.EMPTY);
      return out;
   }

   private static int alpha(float value) {
      return (int) Math.max(0.0F, Math.min(255.0F, value));
   }

   private enum Status {
      NONE,
      VANISHED
   }

   private static final class Entry {
      private final Component prefix;
      private final String name;
      private final Animation animation = new Animation(233L, 0.0F, Easing.EASING8);
      private Status status;
      private GameProfile profile;
      private boolean onServer;
      private TextRenderer renderer;

      private Entry(Component prefix, String name, Status status, GameProfile profile) {
         this.prefix = prefix;
         this.name = name;
         this.status = status;
         this.profile = profile;
      }

      private TextRenderer prefixRenderer() {
         if (this.renderer == null) {
            this.renderer = TextRenderer.m_6(this.prefix, Fonts.SUPPLIER14, Fonts.SUPPLIER17);
         }

         return this.renderer;
      }
   }
}
