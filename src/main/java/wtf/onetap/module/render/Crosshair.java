package wtf.onetap.module.render;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.EntityHitResult;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.combat.AutoSwap;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Crosshair", description = "Кастомный прицел с индикацией кд", category = Category.RENDER)
public class Crosshair extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "Крест", "Крест", "Круг").build();
   private final FloatSetting circleRadius = new FloatSetting("Радиус круга", 4.0F, 0.0F, 10.0F, 0.5F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Круг")).build();
   private final FloatSetting circleThickness = new FloatSetting("Толщина круга", 0.5F, 0.5F, 2.0F, 0.1F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Круг")).build();
   private final FloatSetting radius = new FloatSetting("Радиус", 4.0F, 2.0F, 10.0F, 0.5F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Крест")).build();
   private final FloatSetting length = new FloatSetting("Длина", 3.0F, 1.0F, 10.0F, 0.5F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Крест")).build();
   private final FloatSetting thickness = new FloatSetting("Толщина", 0.5F, 0.5F, 3.0F, 0.5F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Крест")).build();
   private final BooleanSetting outline = new BooleanSetting("Обводка", false).visibleWhen(() -> ((String)this.mode.getValue()).equals("Крест")).build();
   private final BooleanSetting colorizeAim = new BooleanSetting("Окрашивать наводку", true).build();
   private final BooleanSetting centerDot = new BooleanSetting("Точка в центре", true).visibleWhen(() -> ((String)this.mode.getValue()).equals("Крест")).build();
   private final BooleanSetting tShaped = new BooleanSetting("T-Образный", false).visibleWhen(() -> ((String)this.mode.getValue()).equals("Крест")).build();
   private final BooleanSetting cooldownAnimation = new BooleanSetting("Анимация кулдауна", false).build();
   private final FloatSetting animationStrength = new FloatSetting("Сила анимации", 1.0F, 0.1F, 2.0F, 0.1F).visibleWhen(this.cooldownAnimation::getValue).build();
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(255, 255, 255, 255)).build();
   private float floatValue;
   private long longValue;

   @Override
   public void onEnable() {
      this.floatValue = 0.0F;
      this.longValue = 0L;
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      if (this.m_0_boolean()) {
         long longValue2 = System.currentTimeMillis();
         if (this.longValue == 0L) {
            this.longValue = longValue2;
         }

         long longValue3 = longValue2 - this.longValue;
         this.longValue = longValue2;
         float floatValue2 = 1.0F - (float)Math.pow(0.8, (float)longValue3 / 16.6667F);
         float floatValue3 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth() * 0.5F;
         float floatValue4 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() * 0.5F;
         boolean flag = MinecraftAccess.MINECRAFT.hitResult instanceof EntityHitResult entityHitResult
            && entityHitResult.getEntity() != null
            && !entityHitResult.getEntity().isRemoved()
            && entityHitResult.getEntity() != MinecraftAccess.MINECRAFT.player;
         int value = this.colorizeAim.getValue() && flag ? ColorUtil.rgba(255, 60, 60, 255) : (Integer)this.color.getValue();
         boolean flag2 = ((String)this.mode.getValue()).equals("Круг");
         float floatValue5 = flag2 ? this.circleRadius.getFloat() : this.radius.getFloat();
         float floatValue6 = 1.0F - Mth.clamp(MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(0.0F), 0.0F, 1.0F);
         float floatValue7 = (float)(-(Math.cos(Math.PI * floatValue6) - 1.0) / 2.0);
         float floatValue8 = this.cooldownAnimation.getValue() ? floatValue7 * 10.0F * this.animationStrength.getFloat() : 0.0F;
         float floatValue9 = floatValue5 + floatValue8;
         if (this.floatValue == 0.0F) {
            this.floatValue = floatValue9;
         } else {
            this.floatValue = this.floatValue + (floatValue9 - this.floatValue) * floatValue2;
         }

         if (flag2) {
            float floatValue10 = this.floatValue * 2.0F;
            Draw2D.m_7(floatValue3 - this.floatValue, floatValue4 - this.floatValue, floatValue10, floatValue10, this.circleThickness.getFloat(), this.floatValue, 0.0F, value);
         } else {
            float floatValue11 = this.thickness.getFloat();
            float floatValue12 = this.length.getFloat();
            if ((Boolean)this.centerDot.getValue()) {
               this.m_6(floatValue3 - floatValue11 / 2.0F, floatValue4 - floatValue11 / 2.0F, floatValue11, floatValue11, value);
            }

            this.m_6(floatValue3 - this.floatValue - floatValue12, floatValue4 - floatValue11 / 2.0F, floatValue12, floatValue11, value);
            this.m_6(floatValue3 + this.floatValue, floatValue4 - floatValue11 / 2.0F, floatValue12, floatValue11, value);
            this.m_6(floatValue3 - floatValue11 / 2.0F, floatValue4 + this.floatValue, floatValue11, floatValue12, value);
            if (!(Boolean)this.tShaped.getValue()) {
               this.m_6(floatValue3 - floatValue11 / 2.0F, floatValue4 - this.floatValue - floatValue12, floatValue11, floatValue12, value);
            }
         }
      }
   }

   private void m_6(float floatValue2, float floatValue3, float floatValue4, float floatValue5, int value) {
      if ((Boolean)this.outline.getValue()) {
         Draw2D.rect(floatValue2 - 0.5F, floatValue3 - 0.5F, floatValue4 + 1.0F, floatValue5 + 1.0F, ColorUtil.rgba(0, 0, 0, 255));
      }

      Draw2D.rect(floatValue2, floatValue3, floatValue4, floatValue5, value);
   }

   private boolean m_0_boolean() {
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.level == null) {
         return false;
      }

      if (MinecraftAccess.MINECRAFT.gui.hud.isHidden()) {
         return false;
      }

      if (!MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson()) {
         return false;
      }

      AutoSwap autoSwap = Onetap.MODULE_MANAGER.get(AutoSwap.class);
      return autoSwap != null && autoSwap.m_3_boolean() ? false : MinecraftAccess.MINECRAFT.gui.screen() == null || MinecraftAccess.MINECRAFT.gui.screen() instanceof ChatScreen;
   }
}
