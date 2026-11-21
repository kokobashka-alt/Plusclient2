package wtf.onetap.module.render;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.entity.projectile.FireworkRocketEntityAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.render.Render2D;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.WorldToScreen;

@ModuleInformation(name = "FireworkESP", description = "Рисует тег на фейрверк", category = Category.RENDER)
public class FireworkESP extends Module {
   private ItemStack itemStack;
   private final ModeSetting mode = new ModeSetting("Мод", "Nursultan", "Nursultan", "Линия").build();
   private final FloatSetting lineTime = new FloatSetting("Время линии", 1.5F, 0.3F, 4.0F, 0.1F).visibleWhen(this::isLineMode).build();
   private final FloatSetting thickness = new FloatSetting("Толщина", 3.5F, 1.5F, 8.0F, 0.5F).visibleWhen(this::isLineMode).build();
   private final BooleanSetting themeColor = new BooleanSetting("Цвет от темы", true).visibleWhen(this::isLineMode).build();
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(255, 170, 60, 230)).visibleWhen(() -> this.isLineMode() && !(Boolean)this.themeColor.getValue()).build();
   private final Set<Integer> integerSet = new HashSet<>();
   private final List<FireworkESP.DataRecord3> c5List = new ArrayList<>();
   private final Map<Integer, FireworkESP.StateHolder> integerMap = new HashMap<>();

   @Override
   public void onDisable() {
      this.integerSet.clear();
      this.c5List.clear();
      this.integerMap.clear();
   }

   private boolean isLineMode() {
      return ((String)this.mode.getValue()).equals("Линия");
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if (this.isLineMode()) {
            this.integerSet.clear();
            this.c5List.clear();
         } else {
            this.integerMap.clear();
            long longValue = System.currentTimeMillis();
            this.c5List.removeIf(c5 -> longValue - c5.m_8g() > 4000L);
            HashSet set = new HashSet();

            for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
               if (entity instanceof FireworkRocketEntity fireworkRocketEntity) {
                  int value = fireworkRocketEntity.getId();
                  set.add(value);
                  if (!this.integerSet.contains(value)) {
                     this.integerSet.add(value);
                     LivingEntity livingEntity = ((FireworkRocketEntityAccessor)fireworkRocketEntity).onetap$getAttachedToEntity();
                     if (livingEntity != MinecraftAccess.MINECRAFT.player && (livingEntity == null || livingEntity instanceof Player)) {
                        this.c5List.add(new FireworkESP.DataRecord3(fireworkRocketEntity.position(), longValue));
                     }
                  }
               }
            }

            this.integerSet.retainAll(set);
            this.trimList();
         }
      } else {
         this.integerSet.clear();
         this.c5List.clear();
         this.integerMap.clear();
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null && this.isLineMode()) {
         long longValue = System.currentTimeMillis();
         float floatValue = event.getFloatValue();
         this.m_4(longValue, floatValue);
         if (!this.integerMap.isEmpty()) {
            float floatValue2 = this.lineTime.getFloat() * 1000.0F;
            int value = this.themeColor.getValue() ? ColorUtil.primaryColor() : (Integer)this.color.getValue();
            int value2 = value >>> 24 & 0xFF;
            Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
            Vec3 vec3 = event.getVec3();
            float floatValue3 = this.thickness.getFloat();

            for (FireworkESP.StateHolder c2 : this.integerMap.values()) {
               ArrayList list = new ArrayList();
               ArrayList list2 = new ArrayList();

               for (FireworkESP.DataRecord2 c3 : c2.arrayDeque) {
                  if (c3.m_69_boolean()) {
                     this.m_2(matrix4f, vec3, list, list2, value, value2, floatValue3);
                     list.clear();
                     list2.clear();
                  }

                  list.add(c3.m_9_j());
                  list2.add((float)(longValue - c3.m_6_e_long()) / floatValue2);
               }

               if (c2.vec3 != null) {
                  list.add(c2.vec3);
                  list2.add(0.0F);
               }

               this.m_2(matrix4f, vec3, list, list2, value, value2, floatValue3);
            }
         }
      }
   }

   private void m_4(long longValue, float floatValue) {
      long longValue2 = (long)(this.lineTime.getFloat() * 1000.0F);
      HashSet set = new HashSet();

      for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
         if (entity instanceof FireworkRocketEntity fireworkRocketEntity && fireworkRocketEntity.isAlive()) {
            LivingEntity livingEntity = ((FireworkRocketEntityAccessor)fireworkRocketEntity).onetap$getAttachedToEntity();
            if (livingEntity != MinecraftAccess.MINECRAFT.player && (livingEntity == null || livingEntity instanceof Player)) {
               int value = livingEntity != null ? livingEntity.getId() : -fireworkRocketEntity.getId() - 1;
               if (set.add(value)) {
                  Vec3 vec3 = livingEntity != null ? livingEntity.getPosition(floatValue).add(fireworkRocketEntity.position().subtract(livingEntity.position())) : fireworkRocketEntity.getPosition(floatValue);
                  FireworkESP.StateHolder c2 = this.integerMap.computeIfAbsent(value, integer -> new FireworkESP.StateHolder());
                  if (livingEntity != null) {
                     c2.vec32 = fireworkRocketEntity.position().subtract(livingEntity.position());
                  }

                  c2.longValue = longValue;
                  this.m_0(c2, vec3, longValue);
               }
            }
         }
      }

      this.integerMap
         .entrySet()
         .removeIf(
            entry -> {
               FireworkESP.StateHolder c22 = entry.getValue();
               if (!set.contains(entry.getKey())) {
                  if (entry.getKey() >= 0
                     && c22.flag
                     && longValue - c22.longValue <= 700L
                     && MinecraftAccess.MINECRAFT.level.getEntity(entry.getKey()) instanceof Player player
                     && player.isAlive()) {
                     this.m_0(c22, player.getPosition(floatValue).add(c22.vec32), longValue);
                  } else {
                     c22.flag = false;
                     c22.vec3 = null;
                  }
               }

               while (!c22.arrayDeque.isEmpty() && longValue - c22.arrayDeque.peekFirst().m_6_e_long() > longValue2) {
                  c22.arrayDeque.pollFirst();
               }

               return c22.arrayDeque.isEmpty() && !c22.flag;
            }
         );
   }

   private void m_0(FireworkESP.StateHolder c2, Vec3 vec3, long longValue) {
      c2.vec3 = vec3;
      FireworkESP.DataRecord2 c3 = c2.arrayDeque.peekLast();
      if (c3 == null || !c2.flag || c3.m_9_j().distanceToSqr(vec3) >= 0.010000000000000002) {
         c2.arrayDeque.addLast(new FireworkESP.DataRecord2(vec3, longValue, c3 != null && !c2.flag));
         if (c2.arrayDeque.size() > 1500) {
            c2.arrayDeque.pollFirst();
         }
      }

      c2.flag = true;
   }

   private void m_2(Matrix4f matrix4f, Vec3 vec3, List<Vec3> vec3List, List<Float> floatList, int value, int value2, float floatValue) {
      if (vec3List.size() >= 2) {
         float floatValue2 = (Float)floatList.get(0);
         float floatValue3 = (Float)floatList.get(1);
         if (floatValue2 > 1.0F && floatValue2 > floatValue3) {
            float floatValue4 = Mth.clamp((floatValue2 - 1.0F) / (floatValue2 - floatValue3), 0.0F, 1.0F);
            vec3List.set(0, ((Vec3)vec3List.get(0)).lerp((Vec3)vec3List.get(1), floatValue4));
            floatList.set(0, 1.0F);
         }

         int[] intArray = new int[vec3List.size()];

         for (int i = 0; i < intArray.length; i++) {
            float floatValue5 = 1.0F - Mth.clamp((Float)floatList.get(i), 0.0F, 1.0F);
            float floatValue6 = floatValue5 * floatValue5 * (3.0F - 2.0F * floatValue5);
            intArray[i] = ColorUtil.withAlpha(value, (int)(value2 * floatValue6));
         }

         Line3DRenderer.m_6(matrix4f, vec3List, intArray, vec3, floatValue);
      }
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null && !this.isLineMode() && !this.c5List.isEmpty()) {
         CameraRenderState cameraRenderState = MinecraftAccess.MINECRAFT.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
         WorldToScreen.setMatrices(new Matrix4f(cameraRenderState.projectionMatrix), new Matrix4f(cameraRenderState.viewRotationMatrix));
         long longValue = System.currentTimeMillis();
         ArrayList<FireworkESP.DataRecord> c0List = new ArrayList<>(this.c5List.size());
         var iterator = this.c5List.iterator();

         while (iterator.hasNext()) {
            FireworkESP.DataRecord3 c5 = (FireworkESP.DataRecord3)iterator.next();
            long longValue2 = longValue - c5.m_8g();
            if (longValue2 > 4000L) {
               iterator.remove();
            } else {
               WorldToScreen.DataRecord c9 = WorldToScreen.project(c5.m_3_n_Vec3().x, c5.m_3_n_Vec3().y + 0.2, c5.m_3_n_Vec3().z);
               if (c9 != null) {
                  float floatValue = (float)longValue2 / 1000.0F;
                  float floatValue2 = this.m_0_float(longValue2);
                  if (!(floatValue2 <= 0.01F)) {
                     int value = (int)(255.0F * floatValue2);
                     String text = String.format(Locale.US, "%.1f сек. ", floatValue);
                     float floatValue3 = 8.0F;
                     float floatValue4 = 8.0F;
                     float floatValue5 = 2.0F;
                     float floatValue6 = 2.0F;
                     float floatValue7 = 13.0F;
                     float floatValue8 = Draw2D.getTextWidth(text, Fonts.SUPPLIER15, floatValue3);
                     float floatValue9 = floatValue5 + floatValue4 + floatValue6 + floatValue8 + floatValue5;
                     float floatValue10 = c9.m_2r_float() - floatValue9 / 2.0F;
                     float floatValue11 = c9.m_2___float() - 10.0F - (1.0F - floatValue2) * 2.0F;
                     int value2 = ColorUtil.rgba(0, 0, 0, (int)(125.0F * floatValue2));
                     float floatValue12 = floatValue10 + floatValue5;
                     float floatValue13 = floatValue11 + (floatValue7 - floatValue4) / 2.0F;
                     float floatValue14 = Mth.clamp(floatValue2, 0.0F, 1.0F);
                     float floatValue15 = 0.5F * floatValue14;
                     float floatValue16 = 16.0F * floatValue15;
                     c0List.add(
                        new FireworkESP.DataRecord(
                           floatValue10,
                           floatValue11,
                           floatValue9,
                           floatValue7,
                           value2,
                           text,
                           floatValue10 + floatValue5 + floatValue4 + floatValue6,
                           floatValue11 + 1.8F,
                           value,
                           floatValue12 + (floatValue4 - floatValue16) * 0.5F,
                           floatValue13 + (floatValue4 - floatValue16) * 0.5F,
                           floatValue16,
                           (int)(255.0F * floatValue14)
                        )
                     );
                  }
               }
            }
         }

         for (FireworkESP.DataRecord c0 : c0List) {
            Draw2D.roundedRect(c0.f_8f, c0.f_8_r, c0.f_8d, c0.f_8s, 0.0F, c0.f_7_t);
         }

         if (!c0List.isEmpty()) {
            ItemStack itemStack2 = this.getFireworkStack();

            for (FireworkESP.DataRecord c02 : c0List) {
               if (!(c02.f_8_c <= 0.001F)) {
                  TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
                  MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, itemStack2, ItemDisplayContext.GUI, MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, itemStack2.hashCode());
                  event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
                  Render2D.drawItem(trackingItemStackRenderState, c02.f_8k, c02.f_8_w, c02.f_8_c, ColorUtil.rgba(255, 255, 255, c02.f_7o));
               }
            }
         }

         for (FireworkESP.DataRecord c03 : c0List) {
            Draw2D.drawStyledText(c03.f_9_k, Fonts.SUPPLIER15, c03.f_8u, c03.f_8_t, ColorUtil.rgba(255, 255, 255, c03.f_7_p), 8.0F);
         }
      }
   }

   private float m_0_float(long longValue) {
      long longValue2 = 2700L;
      if (longValue <= longValue2) {
         return 1.0F;
      }

      float floatValue = Mth.clamp((float)(longValue - longValue2) / 1300.0F, 0.0F, 1.0F);
      float floatValue2 = floatValue * floatValue * (3.0F - 2.0F * floatValue);
      return 1.0F - floatValue2;
   }

   private void trimList() {
      if (this.c5List.size() > 128) {
         int value = this.c5List.size() - 128;
         this.c5List.subList(0, value).clear();
      }
   }

   private ItemStack getFireworkStack() {
      if (this.itemStack == null) {
         this.itemStack = new ItemStack(Items.FIREWORK_ROCKET);
      }

      return this.itemStack;
   }

   private record DataRecord(
      float f_8f,
      float f_8_r,
      float f_8d,
      float f_8s,
      int f_7_t,
      String f_9_k,
      float f_8u,
      float f_8_t,
      int f_7_p,
      float f_8k,
      float f_8_w,
      float f_8_c,
      int f_7o
   ) {
      public float m_8f() {
         return this.f_8f;
      }

      public float m_8_r_float() {
         return this.f_8_r;
      }

      public float m_8d_float() {
         return this.f_8d;
      }

      public float m_8s() {
         return this.f_8s;
      }

      public int m_7_t_int() {
         return this.f_7_t;
      }

      public String m_9_k() {
         return this.f_9_k;
      }

      public float m_8u() {
         return this.f_8u;
      }

      public float m_8_t() {
         return this.f_8_t;
      }

      public int m_7_p() {
         return this.f_7_p;
      }

      public float m_8k() {
         return this.f_8k;
      }

      public float m_8_w_float() {
         return this.f_8_w;
      }

      public float m_8_c() {
         return this.f_8_c;
      }

      public int m_7o_int() {
         return this.f_7o;
      }
   }

   private static final class StateHolder {
      public final ArrayDeque<FireworkESP.DataRecord2> arrayDeque;
      public Vec3 vec3;
      public Vec3 vec32;
      public long longValue;
      public boolean flag;

      private StateHolder() {
         this.arrayDeque = new ArrayDeque<>();
         this.vec32 = Vec3.ZERO;
      }
   }

   private record DataRecord2(Vec3 f_9_j, long f_6_e, boolean f_69) {
      public Vec3 m_9_j() {
         return this.f_9_j;
      }

      public long m_6_e_long() {
         return this.f_6_e;
      }

      public boolean m_69_boolean() {
         return this.f_69;
      }
   }

   private record DataRecord3(Vec3 f_3_n, long f_8g) {
      public Vec3 m_3_n_Vec3() {
         return this.f_3_n;
      }

      public long m_8g() {
         return this.f_8g;
      }
   }
}
