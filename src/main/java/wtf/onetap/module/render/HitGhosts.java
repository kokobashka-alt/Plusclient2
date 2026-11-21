package wtf.onetap.module.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.AttackEntityEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.C6h;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "HitGhosts", description = "Призрачный след ударенной сущности, поднимающийся со свечением", category = Category.RENDER)
public class HitGhosts extends Module {
   private final FloatSetting duration = new FloatSetting("Длительность", 1.0F, 0.5F, 2.5F, 0.25F).build();
   private final FloatSetting liftHeight = new FloatSetting("Высота подъёма", 1.0F, 0.2F, 3.0F, 0.1F).build();
   private final BooleanSetting onMobs = new BooleanSetting("На мобов", true).build();
   private final BooleanSetting themeColor = new BooleanSetting("Цвет темы", true).build();
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(140, 180, 255, 255)).visibleWhen(() -> !(Boolean)this.themeColor.getValue()).build();
   private final List<HitGhosts.DataRecord> c6List = new ArrayList<>();
   private Object object;

   @EventTarget
   private void onAttackEntity(AttackEntityEvent event) {
      Notifications.m_5_C3_sArr();
      if (event.getEntity() instanceof LivingEntity livingEntity) {
         if (livingEntity != MinecraftAccess.MINECRAFT.player && livingEntity.isAlive()) {
            if (livingEntity instanceof Player || (Boolean)this.onMobs.getValue()) {
               if (MinecraftAccess.MINECRAFT.level != null) {
                  C6h.DataRecord c6 = C6h.m_2_C6(livingEntity, MinecraftAccess.MINECRAFT.getDeltaTracker().getGameTimeDeltaPartialTick(false));
                  if (c6 != null) {
                     this.c6List.add(new HitGhosts.DataRecord(c6, System.nanoTime()));
                     if (this.c6List.size() > 8) {
                        this.c6List.removeFirst();
                     }
                  }
               }
            }
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
      } else {
         this.clear();
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (!this.c6List.isEmpty() && MinecraftAccess.MINECRAFT.level != null) {
         long longValue = System.nanoTime();
         float floatValue = this.duration.getFloat() * 1.0E9F;
         this.c6List.removeIf(c6 -> (float)(longValue - c6.m_1r()) >= floatValue);
         if (!this.c6List.isEmpty()) {
            Vec3 vec3 = event.getVec3();
            Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
            int value = this.themeColor.getValue() ? ColorUtil.primaryColor() : (Integer)this.color.getValue();
            float floatValue2 = this.liftHeight.getFloat();

            for (HitGhosts.DataRecord c62 : this.c6List) {
               float floatValue3 = (float)((double)(longValue - c62.m_1r()) / floatValue);
               float floatValue4 = 1.0F - floatValue3;
               float floatValue5 = (1.0F - floatValue4 * floatValue4 * floatValue4) * floatValue2;
               EntityRenderState entityRenderState = c62.m_1_k_C6().m_8c();
               double doubleValue = entityRenderState.x - vec3.x;
               double doubleValue2 = entityRenderState.y + floatValue5 - vec3.y;
               double doubleValue3 = entityRenderState.z - vec3.z;
               C6h.m_9(c62.m_1_k_C6(), event, matrix4f, doubleValue, doubleValue2, doubleValue3, -1, (int)(0.55F * floatValue4 * 255.0F), false);
               C6h.m_9(c62.m_1_k_C6(), event, matrix4f, doubleValue, doubleValue2, doubleValue3, value, (int)(0.45F * floatValue4 * 255.0F), true);
            }
         }
      }
   }

   private void clear() {
      this.c6List.clear();
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

   private record DataRecord(C6h.DataRecord f_1_k, long f_1r) {
      public C6h.DataRecord m_1_k_C6() {
         return this.f_1_k;
      }

      public long m_1r() {
         return this.f_1r;
      }
   }
}
