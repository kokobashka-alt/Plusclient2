package wtf.onetap.module.render;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "HitIndicator", description = "Показывает красную дугу со стороны атакующего врага", category = Category.RENDER)
public class HitIndicator extends Module {
   private final Map<Integer, HitIndicator.StateHolder> integerMap = new HashMap<>();
   private float floatValue = -1.0F;

   @Override
   public void onEnable() {
      this.floatValue = this.getPlayerHealth();
      this.integerMap.clear();
   }

   @Override
   public void onDisable() {
      this.integerMap.clear();
      this.floatValue = -1.0F;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         float floatValue2 = this.getPlayerHealth();
         if (this.floatValue < 0.0F) {
            this.floatValue = floatValue2;
         } else {
            if (floatValue2 + 0.01F < this.floatValue) {
               LivingEntity livingEntity = this.m_2_LivingEntity();
               if (livingEntity != null) {
                  HitIndicator.StateHolder c5 = this.integerMap.computeIfAbsent(livingEntity.getId(), integer -> new HitIndicator.StateHolder());
                  c5.livingEntity = livingEntity;
                  c5.longValue = System.currentTimeMillis();
               }
            }

            this.floatValue = floatValue2;
         }
      } else {
         this.floatValue = -1.0F;
         this.integerMap.clear();
      }
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && !this.integerMap.isEmpty()) {
         long longValue = System.currentTimeMillis();
         float floatValue2 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth();
         float floatValue3 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight();
         float floatValue4 = floatValue2 / 2.0F;
         float floatValue5 = floatValue3 / 2.0F;
         float floatValue6 = Math.min(72.0F, Math.max(24.0F, Math.min(floatValue4, floatValue5) - 8.0F));
         float floatValue7 = MinecraftAccess.MINECRAFT.gameRenderer.mainCamera().yRot();
         var iterator = this.integerMap.values().iterator();

         while (iterator.hasNext()) {
            HitIndicator.StateHolder c5 = (HitIndicator.StateHolder)iterator.next();
            long longValue2 = longValue - c5.longValue;
            if (longValue2 < 2000L && c5.livingEntity != null && c5.livingEntity.isAlive()) {
               double doubleValue = c5.livingEntity.getX() - MinecraftAccess.MINECRAFT.player.getX();
               double doubleValue2 = c5.livingEntity.getZ() - MinecraftAccess.MINECRAFT.player.getZ();
               if (!(doubleValue * doubleValue + doubleValue2 * doubleValue2 < 1.0E-4)) {
                  float floatValue8 = Mth.wrapDegrees((float)(Math.toDegrees(Math.atan2(doubleValue2, doubleValue)) - 90.0) - floatValue7);
                  float floatValue9 = 0.92F * Mth.clamp((float)longValue2 / 90.0F, 0.0F, 1.0F);
                  if (longValue2 > 1400L) {
                     floatValue9 *= (float)(2000L - longValue2) / 600.0F;
                  }

                  if (!(floatValue9 <= 0.01F)) {
                     this.m_5(floatValue4, floatValue5, floatValue6, floatValue8, 110.0F, 3.2F, floatValue9);
                  }
               }
            } else {
               iterator.remove();
            }
         }
      }
   }

   private void m_5(float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8) {
      float floatValue9 = (float)Math.toRadians(floatValue5 - floatValue6 / 2.0F);
      float floatValue10 = (float)Math.toRadians(floatValue5 + floatValue6 / 2.0F);
      float floatValue11 = floatValue4 * (float)Math.toRadians(floatValue6);
      int value = Math.max(40, (int)Math.ceil(floatValue11 / (floatValue7 * 0.5F)));

      for (int i = 0; i <= value; i++) {
         float floatValue12 = (float)i / value;
         float floatValue13 = 1.0F - (floatValue12 * 2.0F - 1.0F) * (floatValue12 * 2.0F - 1.0F);
         float floatValue14 = floatValue13 * floatValue13 * (3.0F - 2.0F * floatValue13);
         int value2 = Mth.clamp((int)(floatValue8 * 255.0F * floatValue14), 0, 255);
         if (value2 > 1) {
            float floatValue15 = floatValue9 + (floatValue10 - floatValue9) * floatValue12;
            float floatValue16 = floatValue2 + (float)Math.sin(floatValue15) * floatValue4;
            float floatValue17 = floatValue3 - (float)Math.cos(floatValue15) * floatValue4;
            float floatValue18 = floatValue7 * 2.4F;
            Draw2D.roundedRect(floatValue16 - floatValue18 * 0.5F, floatValue17 - floatValue18 * 0.5F, floatValue18, floatValue18, floatValue18 * 0.5F, ColorUtil.rgba(255, 20, 30, (int)(value2 * 0.25F)));
            Draw2D.roundedRect(floatValue16 - floatValue7 * 0.5F, floatValue17 - floatValue7 * 0.5F, floatValue7, floatValue7, floatValue7 * 0.5F, ColorUtil.rgba(255, 42, 52, value2));
         }
      }
   }

   private LivingEntity m_2_LivingEntity() {
      DamageSource damageSource = MinecraftAccess.MINECRAFT.player.getLastDamageSource();
      if (damageSource == null) {
         return null;
      } else if (damageSource.getEntity() instanceof LivingEntity livingEntity && livingEntity != MinecraftAccess.MINECRAFT.player) {
         return livingEntity;
      } else {
         return damageSource.getDirectEntity() instanceof LivingEntity livingEntity2 && livingEntity2 != MinecraftAccess.MINECRAFT.player ? livingEntity2 : null;
      }
   }

   private float getPlayerHealth() {
      return MinecraftAccess.MINECRAFT.player == null ? -1.0F : MinecraftAccess.MINECRAFT.player.getHealth() + MinecraftAccess.MINECRAFT.player.getAbsorptionAmount();
   }

   private static final class StateHolder {
      public LivingEntity livingEntity;
      public long longValue;
   }
}
