package wtf.onetap.module;

import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.module.render.NameProtect;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.PlayerFaceTexture;
import wtf.onetap.render.Render2D;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;

/**
 * Nursultan classic target HUD. Shows the KillAura target, or the entity under the crosshair, or the local player while
 * the chat is open. The panel has the head, name, HP and absorption text, a health bar with a lagging "ghost" section,
 * an absorption bar, particles that burst when the target loses health, and the target's armour above the panel.
 *
 * Font note: SF Medium from the source is replaced by Inter Medium.
 */
final class NursultanTargetPanel extends NursultanElement {
   private static final float WIDTH = 100.0F;
   private static final float HEIGHT = 36.0F;
   private static final int WHITE = ColorUtil.rgba(255, 255, 255, 255);
   private static final int GOLD = ColorUtil.rgba(255, 215, 0, 255);
   private static final int PANEL = ColorUtil.rgba(14, 10, 6, 255);
   private static final int HURT_RED = ColorUtil.rgba(222, 25, 25, 255);

   private final BooleanSetting themeBar;

   private final Animation fade = new Animation(300L, 0.0F, Easing.EASING8);
   private final Animation armorFade = new Animation(300L, 0.0F, Easing.EASING8);
   private final Animation hpBar = new Animation(300L, 0.0F, Easing.EASING8);
   private final Animation ghostBar = new Animation(600L, 0.0F, Easing.EASING8);
   private final Animation absorptionBar = new Animation(300L, 0.0F, Easing.EASING8);

   private final List<HitParticle> particles = new ArrayList<>();
   private LivingEntity lastTarget;
   private float lastHealth = -1.0F;
   private float lastHealthPercent = -1.0F;

   NursultanTargetPanel(BooleanSetting themeBar) {
      this.themeBar = themeBar;
   }

   @Override
   void draw(Render2DEvent event, float x, float y, float scale) {
      LivingEntity target = this.findTarget();
      boolean chat = chatOpen(MINECRAFT);
      if (target != null) {
         this.lastTarget = target;
         this.fade.update(1.0F);
         this.armorFade.update(1.0F);
      } else {
         this.fade.update(0.0F);
         this.armorFade.update(0.0F);
      }

      float anim = this.fade.get();
      if (anim <= 0.05F || this.lastTarget == null) {
         return;
      }

      LivingEntity entity = this.lastTarget;
      Player player = entity instanceof Player p ? p : null;
      int alpha = alpha(255.0F * anim);

      float width = WIDTH * scale;
      float height = HEIGHT * scale;
      float radius = 7.0F * scale;
      Draw2D.roundedRect(x, y, width, height, radius, withAlpha(PANEL, alpha));

      float head = 30.0F * scale;
      float headX = x + 3.0F * scale;
      float headY = y + (height - head) / 2.0F;
      float health = entity.getHealth();

      if (this.lastHealth == -1.0F || this.lastTarget != entity) {
         this.lastHealth = health;
         this.particles.clear();
      }

      if (health < this.lastHealth) {
         int count = Mth.clamp((int) ((this.lastHealth - health) * 4.0F), 5, 15);
         int accent = accentColor();
         for (int i = 0; i < count; i++) {
            this.particles.add(new HitParticle(headX + head / 2.0F, headY + head / 2.0F, accent));
         }

         this.lastHealth = health;
      } else if (health > this.lastHealth) {
         this.lastHealth = health;
      }

      this.drawParticles(alpha);

      float hurt = entity.hurtTime / 10.0F;
      int headTint = ColorUtil.rgba(255, (int) (255 * (1.0F - hurt)), (int) (255 * (1.0F - hurt)), alpha);
      if (player != null) {
         GpuTextureView face = PlayerFaceTexture.getFace(player);
         if (face != null) {
            Draw2D.m_4(headX, headY, head, head, 5.0F * scale, headTint, face);
         }
      } else {
         Draw2D.drawStyledText("N", Fonts.ICONS_NURIK, headX + scale, headY + 8.0F * scale, headTint, 26.0F * scale);
      }

      float textX = x + 35.0F * scale;
      String name = entity.getName().getString();
      NameProtect nameProtect = Onetap.MODULE_MANAGER.get(NameProtect.class);
      if (nameProtect != null && nameProtect.isEnabled()) {
         name = nameProtect.replaceInText(name);
      }

      Render2D.pushScissor(textX, y, width - 42.0F * scale, height);
      Draw2D.drawStyledText(name, Fonts.SUPPLIER14, textX + scale, y + 5.0F * scale,
            ColorUtil.rgba(255, 255, 255, alpha), 8.0F * scale);
      Render2D.popScissor();

      float currentHp = Float.isNaN(health) || health < 0.0F ? 0.0F : health;
      String hpText = String.format(Locale.US, "HP: %.1f", currentHp);
      Draw2D.drawStyledText(hpText, Fonts.SUPPLIER14, textX + scale, y + 15.5F * scale,
            ColorUtil.rgba(255, 255, 255, alpha), 6.75F * scale);

      float absorption = entity.getAbsorptionAmount();
      if (absorption > 0.0F) {
         String absText = String.format(Locale.US, "(+ %.1f)", absorption);
         float offset = Draw2D.getTextWidth(hpText, Fonts.SUPPLIER14, 6.5F * scale) + 3.0F * scale;
         Draw2D.drawStyledText(absText, Fonts.SUPPLIER14, textX + offset + 3.0F * scale, y + 15.5F * scale,
               ColorUtil.withAlpha(GOLD, alpha), 6.5F * scale);
      }

      this.drawHealthBar(entity, textX, y + 25.0F * scale, width - 42.0F * scale, 7.0F * scale, scale, anim, currentHp, alpha);

      if (this.armorFade.get() > 0.05F) {
         this.drawArmor(event, entity, x + width, y, scale, this.armorFade.get());
      }

      this.setSize(width, height);
   }

   private void drawHealthBar(LivingEntity entity, float barX, float barY, float barWidth, float barHeight, float scale,
         float anim, float currentHp, int alpha) {
      float maxHealth = entity.getMaxHealth();
      float percent = Mth.clamp(currentHp / maxHealth, 0.0F, 1.0F);

      this.hpBar.update(barWidth * percent);
      if (this.lastHealthPercent >= 0.0F && percent < this.lastHealthPercent) {
         this.ghostBar.update(barWidth * percent);
      } else {
         this.ghostBar.reset(this.hpBar.get());
      }

      this.lastHealthPercent = percent;

      int fullLeft;
      int fullRight;
      int ghostLeft;
      int ghostRight;
      int background;
      if (this.themeBar.getValue()) {
         int c1 = accentColor();
         int c2 = accentColorTwo();
         fullRight = withAlpha(c1, alpha);
         fullLeft = withAlpha(c2, alpha);
         ghostLeft = withAlpha(c1, (int) (110 * anim));
         ghostRight = withAlpha(c2, (int) (110 * anim));
         background = ColorUtil.rgba(20, 20, 20, (int) (120 * anim));
      } else {
         int base = healthColor(percent);
         int r = (base >> 16) & 255;
         int g = (base >> 8) & 255;
         int b = base & 255;
         fullLeft = ColorUtil.rgba(clamp255((int) (r * 0.35F)), clamp255((int) (g * 0.35F)), clamp255((int) (b * 0.35F)), alpha);
         fullRight = ColorUtil.rgba(r, g, b, alpha);
         ghostLeft = ColorUtil.rgba(r, g, b, (int) (110 * anim));
         ghostRight = ColorUtil.rgba(clamp255((int) (r * 0.35F)), clamp255((int) (g * 0.35F)), clamp255((int) (b * 0.35F)), (int) (110 * anim));
         background = ColorUtil.rgba(clamp255((int) (r * 0.45F)), clamp255((int) (g * 0.45F)), clamp255((int) (b * 0.45F)), (int) (120 * anim));
      }

      Draw2D.roundedRect(barX, barY, barWidth, barHeight, 1.5F * scale, background);

      float ghost = this.ghostBar.get();
      if (ghost > 0.5F) {
         Draw2D.roundedRect(barX, barY, ghost, barHeight, 1.5F * scale, ghostLeft);
      }

      float now = this.hpBar.get();
      if (now > 0.5F) {
         Draw2D.roundedRect(barX, barY, now, barHeight, 1.5F * scale, fullLeft);
      }

      float absorptionPercent = Mth.clamp(entity.getAbsorptionAmount() / maxHealth, 0.0F, 1.0F);
      this.absorptionBar.update(barWidth * absorptionPercent);
      float absNow = this.absorptionBar.get();
      if (absNow > 0.5F) {
         int absColor = ColorUtil.rgba(140, 120, 0, (int) (200 * anim));
         Draw2D.roundedRect(barX - 0.25F * scale, barY, absNow, barHeight, 1.5F * scale, absColor);
      }
   }

   private void drawArmor(Render2DEvent event, LivingEntity entity, float rightX, float y, float scale, float armor) {
      List<ItemStack> items = new ArrayList<>();
      items.add(entity.getMainHandItem());
      for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
         items.add(entity.getItemBySlot(slot));
      }

      items.add(entity.getOffhandItem());
      java.util.Collections.reverse(items);

      float itemScale = 0.65F * scale;
      float slot = 16.0F * itemScale;
      float itemX = rightX - slot * 6.0F - 5.0F * scale;
      float itemY = y - slot - 2.0F * scale;
      float size = slot * armor;
      for (ItemStack stack : items) {
         if (stack.isEmpty()) {
            itemX += slot;
            continue;
         }

         TrackingItemStackRenderState state = new TrackingItemStackRenderState();
         MINECRAFT.getItemModelResolver().updateForTopItem(state, stack, ItemDisplayContext.GUI, MINECRAFT.level, entity, 0);
         event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(state.getModelIdentity());
         Render2D.drawItem(state, itemX, itemY, size, WHITE);
         Render2D.drawItemDecorations(stack, itemX, itemY, size, alpha(255.0F * armor));
         itemX += slot;
      }
   }

   private void drawParticles(int alpha) {
      this.particles.removeIf(p -> p.alpha() <= 0.0F);
      for (HitParticle particle : this.particles) {
         particle.update();
         int pAlpha = (int) (particle.alpha() * alpha);
         if (pAlpha > 5) {
            Draw2D.roundedRect(particle.x - particle.size / 2.0F, particle.y - particle.size / 2.0F, particle.size, particle.size,
                  particle.size / 2.0F, withAlpha(particle.color, pAlpha));
         }
      }
   }

   /** The KillAura target if it is alive, otherwise the entity under the crosshair, otherwise the player while chatting. */
   private LivingEntity findTarget() {
      KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
      if (killAura != null && killAura.isEnabled() && killAura.getLivingEntity2() != null && killAura.getLivingEntity2().isAlive()) {
         return killAura.getLivingEntity2();
      }

      if (MINECRAFT.crosshairPickEntity instanceof LivingEntity living && living.isAlive()) {
         return living;
      }

      return chatOpen(MINECRAFT) ? MINECRAFT.player : null;
   }

   private static int healthColor(float ratio) {
      int max = ColorUtil.rgba(44, 246, 53, 255);
      int at56 = ColorUtil.rgba(160, 228, 69, 255);
      int at38 = ColorUtil.rgba(222, 191, 79, 255);
      int at32 = ColorUtil.rgba(233, 150, 87, 255);
      int at11 = ColorUtil.rgba(255, 125, 98, 255);
      if (ratio >= 0.56F) {
         return ColorUtil.lerpColor(max, at56, Mth.clamp((1.0F - ratio) / (1.0F - 0.56F), 0.0F, 1.0F));
      } else if (ratio >= 0.38F) {
         return ColorUtil.lerpColor(at56, at38, Mth.clamp((0.56F - ratio) / (0.56F - 0.38F), 0.0F, 1.0F));
      } else if (ratio >= 0.32F) {
         return ColorUtil.lerpColor(at38, at32, Mth.clamp((0.38F - ratio) / (0.38F - 0.32F), 0.0F, 1.0F));
      } else if (ratio >= 0.11F) {
         return ColorUtil.lerpColor(at32, at11, Mth.clamp((0.32F - ratio) / (0.32F - 0.11F), 0.0F, 1.0F));
      }

      return at11;
   }

   private static int accentColor() {
      ColorSetting setting = Onetap.THEME_MANAGER.getColorSetting();
      return setting == null ? ColorUtil.rgba(120, 120, 255, 255) : setting.getValue();
   }

   private static int accentColorTwo() {
      ColorSetting setting = Onetap.THEME_MANAGER.getColorSetting2();
      return setting == null ? ColorUtil.rgba(180, 90, 255, 255) : setting.getValue();
   }

   private static int alpha(float value) {
      return (int) Math.max(0.0F, Math.min(255.0F, value));
   }

   private static int clamp255(int value) {
      return Math.max(0, Math.min(255, value));
   }

   private static final class HitParticle {
      private float x;
      private float y;
      private final float vx;
      private final float vy;
      private final float size;
      private final long spawn;
      private final int color;

      private HitParticle(float x, float y, int color) {
         this.x = x;
         this.y = y;
         double angle = Math.random() * Math.PI * 2.0;
         double speed = Math.random() * 0.4 + 0.1;
         this.vx = (float) (Math.cos(angle) * speed);
         this.vy = (float) (Math.sin(angle) * speed);
         this.size = (float) (Math.random() * 8.0 + 2.0);
         this.spawn = System.currentTimeMillis();
         this.color = color;
      }

      private void update() {
         this.x += this.vx;
         this.y += this.vy;
      }

      private float alpha() {
         long elapsed = System.currentTimeMillis() - this.spawn;
         return elapsed >= 2000L ? 0.0F : 1.0F - (float) elapsed / 2000.0F;
      }
   }
}
