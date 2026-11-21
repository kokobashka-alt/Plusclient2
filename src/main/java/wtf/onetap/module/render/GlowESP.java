package wtf.onetap.module.render;

import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.GlowEspRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "GlowESP", description = "Шейдерный glow-контур игроков через стены", category = Category.RENDER)
public class GlowESP extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "Неон", "Софт", "Неон", "Огонь", "Волны").build();
   private final BooleanSetting themeColor = new BooleanSetting("Цвет от темы", true).build();
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(140, 180, 255, 255)).visibleWhen(() -> !(Boolean)this.themeColor.getValue()).build();
   private final ColorSetting friendColor = new ColorSetting("Цвет друзей", ColorUtil.rgba(50, 205, 50, 255)).build();
   public final FloatSetting glow = new FloatSetting("Глоу", 1.5F, 0.0F, 9.0F, 0.05F).build();
   public final FloatSetting strength = new FloatSetting("Сила", 1.0F, 0.05F, 1.0F, 0.05F).build();
   private final FloatSetting speed = new FloatSetting("Скорость", 1.0F, 0.2F, 3.0F, 0.05F).visibleWhen(() -> !((String)this.mode.getValue()).equals("Софт")).build();

   @Override
   public void onDisable() {
      GlowEspRenderer.m_6_void();
   }

   public int m_7_int() {
      Notifications.m_5_C3_sArr();

      return switch ((String)this.mode.getValue()) {
         case "Неон" -> 1;
         case "Огонь" -> 2;
         case "Волны" -> 3;
         default -> 0;
      };
   }

   public float getTime() {
      return (float)(System.nanoTime() * 1.0E-9 * this.speed.getFloat());
   }

   public boolean shouldGlow(Entity entity) {
      return this.isEnabled() && entity instanceof Player player && player.isAlive()
         ? player != MinecraftAccess.MINECRAFT.player || !MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson()
         : false;
   }

   public int getGlowColor(Player player) {
      if (!this.shouldGlow(player)) {
         return 0;
      } else {
         return Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())
            ? ARGB.opaque((Integer)this.friendColor.getValue())
            : ARGB.opaque(this.themeColor.getValue() ? ColorUtil.primaryColor() : (Integer)this.color.getValue());
      }
   }
}
