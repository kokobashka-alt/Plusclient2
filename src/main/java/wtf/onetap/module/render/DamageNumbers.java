package wtf.onetap.module.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.AttackEntityEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.WorldToScreen;

@ModuleInformation(name = "DamageNumbers", description = "Всплывающие цифры урона в мире", category = Category.RENDER)
public class DamageNumbers extends Module {
   private static final int VALUE = ColorUtil.rgba(120, 255, 140, 255);
   private static final int VALUE2 = ColorUtil.rgba(255, 80, 90, 255);
   private final FloatSetting size = new FloatSetting("Размер", 1.0F, 0.5F, 2.5F, 0.05F).build();
   private final BooleanSetting onMobs = new BooleanSetting("На мобов", false).build();
   private final Map<Integer, Float> integerMap = new HashMap<>();
   private final Map<Integer, Long> integerMap2 = new HashMap<>();
   private final List<DamageNumbers.StateHolder> c5List = new ArrayList<>();
   private Object object;

   @EventTarget
   private void onAttackEntity(AttackEntityEvent event) {
      if (event.getEntity() instanceof LivingEntity livingEntity && MinecraftAccess.MINECRAFT.player != null) {
         if (this.isCrit()) {
            this.integerMap2.put(livingEntity.getId(), System.currentTimeMillis() + 800L);
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if (this.object != MinecraftAccess.MINECRAFT.level) {
            this.clear();
            this.object = MinecraftAccess.MINECRAFT.level;
         }

         this.m_4_void();
         long longValue = System.currentTimeMillis();
         this.c5List.removeIf(c5 -> longValue - c5.longValue >= 800L);
      } else {
         this.clear();
      }
   }

   private boolean isCrit() {
      return MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(0.5F) > 0.9F
         && MinecraftAccess.MINECRAFT.player.fallDistance > 0.0
         && !MinecraftAccess.MINECRAFT.player.onGround()
         && !MinecraftAccess.MINECRAFT.player.onClimbable()
         && !MinecraftAccess.MINECRAFT.player.isInWater()
         && !MinecraftAccess.MINECRAFT.player.isMobilityRestricted()
         && !MinecraftAccess.MINECRAFT.player.isPassenger()
         && !MinecraftAccess.MINECRAFT.player.isSprinting();
   }

   private void m_4_void() {
      long longValue = System.currentTimeMillis();
      this.integerMap2.values().removeIf(longObj -> longObj < longValue);
      HashMap map = new HashMap();

      for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
         if (entity instanceof LivingEntity livingEntity && livingEntity != MinecraftAccess.MINECRAFT.player && (livingEntity instanceof Player || (Boolean)this.onMobs.getValue())) {
            float floatValue = livingEntity.getHealth();
            Float floatObj = this.integerMap.get(livingEntity.getId());
            if (floatObj != null && floatObj - floatValue > 0.01F) {
               this.spawnNumber(livingEntity, floatObj - floatValue);
            }

            map.put(livingEntity.getId(), floatValue);
         }
      }

      this.integerMap.clear();
      this.integerMap.putAll(map);
   }

   private void spawnNumber(LivingEntity livingEntity, float floatValue) {
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      boolean flag = this.integerMap2.remove(livingEntity.getId()) != null;
      int value = flag ? VALUE2 : VALUE;
      this.c5List
         .add(
            new DamageNumbers.StateHolder(
               livingEntity.getX() + threadLocalRandom.nextDouble(-0.35, 0.35),
               livingEntity.getY() + livingEntity.getBbHeight() * 0.55 + threadLocalRandom.nextDouble(0.1, 0.45),
               livingEntity.getZ() + threadLocalRandom.nextDouble(-0.35, 0.35),
               (float)threadLocalRandom.nextDouble(-0.35, 0.35),
               (float)threadLocalRandom.nextDouble(-0.35, 0.35),
               formatDamage(floatValue),
               value
            )
         );
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && !this.c5List.isEmpty()) {
         CameraRenderState cameraRenderState = MinecraftAccess.MINECRAFT.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
         WorldToScreen.setMatrices(new Matrix4f(cameraRenderState.projectionMatrix), new Matrix4f(cameraRenderState.viewRotationMatrix));
         Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
         long longValue = System.currentTimeMillis();

         for (DamageNumbers.StateHolder c5 : this.c5List) {
            float floatValue = (float)(longValue - c5.longValue) / 800.0F;
            if (!(floatValue >= 1.0F)) {
               float floatValue2 = (float)(longValue - c5.longValue) / 1000.0F;
               double doubleValue = c5.doubleValue + c5.floatValue * floatValue2;
               double doubleValue2 = c5.doubleValue2 + 0.75F * (1.0F - (1.0F - floatValue) * (1.0F - floatValue));
               double doubleValue3 = c5.doubleValue3 + c5.floatValue2 * floatValue2;
               WorldToScreen.DataRecord c9 = WorldToScreen.project(doubleValue, doubleValue2, doubleValue3);
               if (c9 != null) {
                  float floatValue3 = Math.min(1.0F, floatValue2 / 0.12F) * (1.0F - floatValue * floatValue * floatValue);
                  if (!(floatValue3 <= 0.01F)) {
                     float floatValue4 = (float)vec3.distanceTo(new Vec3(doubleValue, doubleValue2, doubleValue3));
                     float floatValue5 = this.size.getFloat() * 16.0F * getScale(floatValue4);
                     float floatValue6 = Draw2D.getTextWidth(c5.text, Fonts.SUPPLIER4, floatValue5);
                     int value = (int)(255.0F * floatValue3);
                     Draw2D.drawTextOutlined(c5.text, Fonts.SUPPLIER4, c9.m_2r_float() - floatValue6 / 2.0F, c9.m_2___float() - floatValue5 / 2.0F, ColorUtil.withAlpha(c5.value, value), floatValue5);
                  }
               }
            }
         }
      }
   }

   private static float getScale(float floatValue) {
      return 1.0F + -0.19999999F * (floatValue / (floatValue + 30.0F));
   }

   private static String formatDamage(float floatValue) {
      int value = Math.round(floatValue);
      return Math.abs(floatValue - value) < 0.05F ? String.valueOf(value) : String.format(Locale.ROOT, "%.1f", floatValue);
   }

   private void clear() {
      this.c5List.clear();
      this.integerMap.clear();
      this.integerMap2.clear();
      this.object = null;
   }

   @Override
   public void onEnable() {
      this.clear();
   }

   @Override
   public void onDisable() {
      this.clear();
   }

   private static final class StateHolder {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public float floatValue;
      public float floatValue2;
      public String text;
      public int value;
      public long longValue;

      public StateHolder(double doubleValue4, double doubleValue5, double doubleValue6, float floatValue3, float floatValue4, String text2, int value2) {
         this.doubleValue = doubleValue4;
         this.doubleValue2 = doubleValue5;
         this.doubleValue3 = doubleValue6;
         this.floatValue = floatValue3;
         this.floatValue2 = floatValue4;
         this.text = text2;
         this.value = value2;
         this.longValue = System.currentTimeMillis();
      }
   }
}
