package wtf.onetap.module.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.render.BoltRenderer;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Texture3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "TargetESP", description = "Подсвечивает текущую цель KillAura", category = Category.RENDER)
public class TargetESP extends Module {
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "images/marker.png");
   private static final Identifier IDENTIFIER2 = Identifier.fromNamespaceAndPath("onetap", "images/glow.png");
   private static final Identifier IDENTIFIER3 = Identifier.fromNamespaceAndPath("onetap", "images/target.png");
   private static final double DOUBLE_VALUE = Math.toRadians(Double.longBitsToDouble(4633641066610819072L));
   private final ModeSetting mode = new ModeSetting("Режим", "Маркер", "Маркер", "Призраки", "Кружок", "Celestial", "Кубики", "Магический круг", "Молния").build();
   private final ModeSetting texture = new ModeSetting("Текстура", "Маркер", "Маркер", "Ромб").visibleWhen(() -> ((String)this.mode.getValue()).equals("Маркер")).build();
   private final BooleanSetting glow = new BooleanSetting("Свечение", true).visibleWhen(() -> ((String)this.mode.getValue()).equals("Маркер")).build();
   private final ModeSetting displayMode = new ModeSetting("Отображение", "Обычный", "Обычный", "Призрачный")
      .visibleWhen(() -> ((String)this.mode.getValue()).equals("Призраки"))
      .build();
   private final ModeSetting displayMode2 = new ModeSetting("Отображение", "Новый", "Старый", "Новый").visibleWhen(() -> ((String)this.mode.getValue()).equals("Кружок")).build();
   private final BooleanSetting trail = new BooleanSetting("След", true)
      .visibleWhen(
         () -> ((String)this.mode.getValue()).equals("Призраки") && ((String)this.displayMode.getValue()).equals("Обычный")
            || ((String)this.mode.getValue()).equals("Кружок") && ((String)this.displayMode2.getValue()).equals("Новый")
      )
      .build();
   private final BooleanSetting redOnDamage = new BooleanSetting("Краснеть при уроне", true).build();
   private final BooleanSetting custom = new BooleanSetting("Кастом", false)
      .visibleWhen(() -> ((String)this.mode.getValue()).equals("Призраки") && ((String)this.displayMode.getValue()).equals("Обычный"))
      .build();
   private final FloatSetting radius = new FloatSetting("Радиус", 0.62F, 0.1F, 2.5F, 0.01F)
      .visibleWhen(() -> ((String)this.mode.getValue()).equals("Призраки") && ((String)this.displayMode.getValue()).equals("Обычный") && (Boolean)this.custom.getValue())
      .build();
   private final FloatSetting density = new FloatSetting("Плотность", 1.0F, 0.1F, 5.0F, 0.1F)
      .visibleWhen(() -> ((String)this.mode.getValue()).equals("Призраки") && ((String)this.displayMode.getValue()).equals("Обычный") && (Boolean)this.custom.getValue())
      .build();
   private final FloatSetting size = new FloatSetting("Размер", 0.28F, 0.05F, 1.0F, 0.01F)
      .visibleWhen(() -> ((String)this.mode.getValue()).equals("Призраки") && ((String)this.displayMode.getValue()).equals("Обычный") && (Boolean)this.custom.getValue())
      .build();
   private final FloatSetting count = new FloatSetting("Количество", 74.0F, 1.0F, 150.0F, 1.0F)
      .visibleWhen(() -> ((String)this.mode.getValue()).equals("Призраки") && ((String)this.displayMode.getValue()).equals("Обычный") && (Boolean)this.custom.getValue())
      .build();
   private final FloatSetting speed = new FloatSetting("Скорость", 180.0F, 10.0F, 720.0F, 10.0F)
      .visibleWhen(() -> ((String)this.mode.getValue()).equals("Призраки") && ((String)this.displayMode.getValue()).equals("Обычный") && (Boolean)this.custom.getValue())
      .build();
   private final FloatSetting speed2 = new FloatSetting("Скорость", 1.0F, 0.2F, 3.0F, 0.1F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Кубики")).build();
   private final FloatSetting size2 = new FloatSetting("Размер", 1.0F, 0.5F, 2.0F, 0.05F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Кубики")).build();
   private final FloatSetting count2 = new FloatSetting("Количество", 10.0F, 1.0F, 24.0F, 1.0F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Кубики")).build();
   private final FloatSetting radius2 = new FloatSetting("Радиус", 1.25F, 0.5F, 3.0F, 0.05F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Магический круг")).build();
   private final FloatSetting speed3 = new FloatSetting("Скорость", 1.0F, 0.2F, 3.0F, 0.1F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Магический круг")).build();
   private final BooleanSetting lightPillar = new BooleanSetting("Столб света", true).visibleWhen(() -> ((String)this.mode.getValue()).equals("Магический круг")).build();
   private final BooleanSetting sparks = new BooleanSetting("Искры", true).visibleWhen(() -> ((String)this.mode.getValue()).equals("Магический круг")).build();
   private final FloatSetting count3 = new FloatSetting("Количество", 3.0F, 1.0F, 8.0F, 1.0F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Молния")).build();
   private final FloatSetting frequency = new FloatSetting("Частота", 1.0F, 0.3F, 3.0F, 0.1F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Молния")).build();
   private final BooleanSetting branching = new BooleanSetting("Ветвление", true).visibleWhen(() -> ((String)this.mode.getValue()).equals("Молния")).build();
   private final BooleanSetting groundBolts = new BooleanSetting("Разряды по земле", true).visibleWhen(() -> ((String)this.mode.getValue()).equals("Молния")).build();
   private final BooleanSetting themeColor = new BooleanSetting("Цвет от темы", true).build();
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(140, 180, 255, 255)).visibleWhen(() -> !(Boolean)this.themeColor.getValue()).build();
   private final ColorSetting color2 = new ColorSetting("Цвет 2", ColorUtil.rgba(255, 80, 180, 255))
      .visibleWhen(
         () -> !(Boolean)this.themeColor.getValue()
            && (
               ((String)this.mode.getValue()).equals("Призраки")
                  || ((String)this.mode.getValue()).equals("Молния")
                  || ((String)this.mode.getValue()).equals("Кружок")
                  || ((String)this.mode.getValue()).equals("Маркер")
            )
      )
      .build();
   private final Animation animation = new Animation(300L, 0.0F, Easing.EASING8);
   private final Animation animation2 = new Animation(300L, 0.0F, Easing.EASING8);
   private final ArrayList<TargetESP.StateHolder3> c6List = new ArrayList<>(512);
   private double doubleValue;
   private final TargetESP.StateHolder4[] c7Array = new TargetESP.StateHolder4[]{new TargetESP.StateHolder4(), new TargetESP.StateHolder4(), new TargetESP.StateHolder4()};
   private float floatValue;
   private float floatValue2;
   private final Matrix4f matrix4f = new Matrix4f();
   private final Vector3f vector3f = new Vector3f();
   private final Vector3f vector3f2 = new Vector3f();
   private final Quaternionf quaternionf = new Quaternionf();
   private float floatValue3;
   private LivingEntity livingEntity;
   private LivingEntity livingEntity2;
   private long longValue = System.currentTimeMillis();
   private float floatValue4 = 0.016F;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7 = 1.0F;
   private boolean flag;
   private double doubleValue2 = Double.NaN;
   private double doubleValue3 = Double.NaN;
   private double doubleValue4 = Double.NaN;
   private double doubleValue5;
   private double doubleValue6;
   private double doubleValue7;
   private float floatValue8;
   private final ArrayList<TargetESP.StateHolder5> c9List = new ArrayList<>(48);
   private final ArrayList<BoltRenderer.DataRecord> c7List = new ArrayList<>(64);
   private LivingEntity livingEntity3;
   private float floatValue9;
   private float floatValue10;
   private float floatValue11;
   private int value;
   private float floatValue12;
   private float floatValue13 = 1.0F;
   private BufferBuilder bufferBuilder;
   private static Module[] moduleArray;

   @EventTarget(priority = EventPriority.HIGH)
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         LivingEntity livingEntity4 = KillAura.livingEntity;
         if (livingEntity4 != null && livingEntity4.isAlive()) {
            if (this.livingEntity == null) {
               this.livingEntity = livingEntity4;
               this.animation.setDuration(0.0F);
               this.doubleValue2 = this.doubleValue3 = this.doubleValue4 = Double.NaN;
            } else if (livingEntity4 != this.livingEntity) {
               this.livingEntity2 = this.livingEntity;
               this.animation2.setDuration(this.animation.getValue());
               this.livingEntity = livingEntity4;
               this.animation.setDuration(0.0F);
               this.doubleValue2 = this.doubleValue3 = this.doubleValue4 = Double.NaN;
            }
         } else if (this.livingEntity != null) {
            this.livingEntity2 = this.livingEntity;
            this.animation2.setDuration(this.animation.getValue());
            this.livingEntity = null;
            this.doubleValue2 = this.doubleValue3 = this.doubleValue4 = Double.NaN;
         }

         long longValue2 = System.currentTimeMillis();
         this.floatValue4 = Math.max(0.001F, (float)(longValue2 - this.longValue) / 1000.0F);
         this.longValue = longValue2;
         float floatValue14 = this.livingEntity2 == null ? 0.0F : this.animation2.update(0.0F);
         if (this.livingEntity2 != null && floatValue14 <= 0.01F) {
            this.livingEntity2 = null;
         }

         float floatValue15 = this.livingEntity == null ? 0.0F : this.animation.update(1.0F);
         if (((String)this.mode.getValue()).equals("Молния")) {
            this.updateTrail();
            if (this.livingEntity != null && floatValue15 > 0.01F) {
               this.m_1_void(this.livingEntity);
            } else {
               this.livingEntity3 = null;
            }
         } else if (!this.c9List.isEmpty() || this.livingEntity3 != null) {
            this.clear();
         }

         if (this.livingEntity2 != null) {
            this.m_0(event, this.livingEntity2, floatValue14, false);
         }

         if (this.livingEntity != null && floatValue15 > 0.01F) {
            this.m_0(event, this.livingEntity, floatValue15, true);
         }

         if (this.c7Array[0].flag
            && (
               this.livingEntity == null && this.livingEntity2 == null
                  || !((String)this.mode.getValue()).equals("Призраки")
                  || !((String)this.displayMode.getValue()).equals("Призрачный")
            )) {
            this.reset();
         }

         if (((String)this.mode.getValue()).equals("Кружок") && ((String)this.displayMode2.getValue()).equals("Новый") && (Boolean)this.trail.getValue()) {
            this.m_1_void();
            if (this.livingEntity != null && floatValue15 > 0.01F) {
               this.m_8_void(this.livingEntity);
            }

            this.m_9(event);
            if (this.livingEntity == null && this.c6List.isEmpty()) {
               this.doubleValue2 = this.doubleValue3 = this.doubleValue4 = Double.NaN;
            }
         } else {
            this.c6List.clear();
            this.doubleValue2 = this.doubleValue3 = this.doubleValue4 = Double.NaN;
         }
      } else {
         this.livingEntity = null;
         this.livingEntity2 = null;
         this.animation.setDuration(0.0F);
         this.animation2.setDuration(0.0F);
         this.c6List.clear();
         this.reset();
         this.clear();
         this.doubleValue2 = this.doubleValue3 = this.doubleValue4 = Double.NaN;
      }
   }

   private void m_0(Render3DEvent event, LivingEntity livingEntity4, float floatValue14, boolean flag2) {
      float floatValue15 = event.getFloatValue();
      double doubleValue8 = livingEntity4.xo + (livingEntity4.getX() - livingEntity4.xo) * floatValue15;
      double doubleValue9 = livingEntity4.yo + (livingEntity4.getY() - livingEntity4.yo) * floatValue15;
      double doubleValue10 = livingEntity4.zo + (livingEntity4.getZ() - livingEntity4.zo) * floatValue15;
      if (flag2) {
         this.doubleValue5 = doubleValue8;
         this.doubleValue6 = doubleValue9;
         this.doubleValue7 = doubleValue10;
      }

      if (flag2) {
         this.m_2_void(livingEntity4);
      }

      float floatValue16 = Math.min(1.0F, livingEntity4.hurtTime / 10.0F);
      int value2 = this.themeColor.getValue() ? ColorUtil.primaryColor() : (Integer)this.color.getValue();
      int value3 = this.themeColor.getValue() ? ColorUtil.secondaryColor() : (Integer)this.color2.getValue();
      if ((Boolean)this.redOnDamage.getValue()) {
         int value4 = ColorUtil.rgba(255, 45, 45, 255);
         value2 = ColorUtil.lerpColor(value2, value4, floatValue16);
         value3 = ColorUtil.lerpColor(value3, value4, floatValue16);
      }

      int value5 = ColorUtil.withAlpha(value2, (int)((value2 >>> 24 & 0xFF) * floatValue14));
      int value6 = ColorUtil.withAlpha(value3, (int)((value3 >>> 24 & 0xFF) * floatValue14));
      if (((String)this.mode.getValue()).equals("Призраки")) {
         if (((String)this.displayMode.getValue()).equals("Призрачный")) {
            if (flag2 || this.livingEntity == null) {
               this.m_7(event, livingEntity4, doubleValue8, doubleValue9, doubleValue10, value5, value6, floatValue14);
            }
         } else {
            this.m_0(event, livingEntity4, doubleValue8, doubleValue9, doubleValue10, value5, value6, floatValue14);
         }
      } else if (((String)this.mode.getValue()).equals("Кружок")) {
         if (((String)this.displayMode2.getValue()).equals("Старый")) {
            this.m_6(event, livingEntity4, doubleValue8, doubleValue9, doubleValue10, value5, value6, floatValue14);
         } else {
            this.m_2(event, livingEntity4, doubleValue8, doubleValue9, doubleValue10, value5, value6, floatValue14);
         }
      } else if (((String)this.mode.getValue()).equals("Celestial")) {
         this.m_2(event, livingEntity4, doubleValue8, doubleValue9, doubleValue10, value2, floatValue14);
      } else if (((String)this.mode.getValue()).equals("Кубики")) {
         this.m_6(event, livingEntity4, doubleValue8, doubleValue9, doubleValue10, value5, floatValue14);
      } else if (((String)this.mode.getValue()).equals("Магический круг")) {
         this.m_3(event, livingEntity4, doubleValue8, doubleValue9, doubleValue10, value5, floatValue14);
      } else if (!((String)this.mode.getValue()).equals("Молния")) {
         boolean flag3 = ((String)this.texture.getValue()).equals("Маркер");
         GpuTextureView gpuTextureView = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(flag3 ? IDENTIFIER3 : IDENTIFIER).getTextureView();
         this.matrix4f.set(event.getMatrix4f2());
         this.matrix4f
            .translate((float)(doubleValue8 - event.getVec3().x), (float)(doubleValue9 + livingEntity4.getBbHeight() / 2.0 - event.getVec3().y), (float)(doubleValue10 - event.getVec3().z));
         this.matrix4f.rotate(event.getQuaternionf());
         this.matrix4f.rotate((float)Math.toRadians(this.floatValue5), 0.0F, 0.0F, 1.0F);
         this.matrix4f.scale(0.105F * (1.0F + (1.0F - floatValue14) * 0.65F) * this.floatValue7 * (flag3 ? 0.6F : 1.0F));
         int value7 = ColorUtil.lerpColor(value5, value6, 0.5F);
         Texture3DRenderer.m_8(new Matrix4f(event.getMatrix4f()).mul(this.matrix4f), gpuTextureView, 1, false, (Boolean)this.glow.getValue(), bufferBuilder2 -> {
            m_4(bufferBuilder2, -10.4F, 10.4F, 0.0F, 1.0F, value5);
            m_4(bufferBuilder2, 10.4F, 10.4F, 1.0F, 1.0F, value7);
            m_4(bufferBuilder2, 10.4F, -10.4F, 1.0F, 0.0F, value6);
            m_4(bufferBuilder2, -10.4F, -10.4F, 0.0F, 0.0F, value7);
         });
      } else {
         if (flag2 || livingEntity4 != this.livingEntity) {
            this.m_3(event, livingEntity4, doubleValue8, doubleValue9, doubleValue10, value5, value6);
         }
      }
   }

   private static void m_4(BufferBuilder bufferBuilder2, float floatValue14, float floatValue15, float floatValue16, float floatValue17, int value2) {
      bufferBuilder2.addVertex(floatValue14, floatValue15, 0.0F).setUv(floatValue16, floatValue17).setColor(value2 >> 16 & 0xFF, value2 >> 8 & 0xFF, value2 & 0xFF, value2 >>> 24 & 0xFF);
   }

   private void m_0(Render3DEvent event, LivingEntity livingEntity4, double doubleValue8, double doubleValue9, double doubleValue10, int value2, int value3, float floatValue14) {
      GpuTextureView gpuTextureView = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(IDENTIFIER2).getTextureView();
      double doubleValue11 = doubleValue9 + livingEntity4.getBbHeight() * 0.58;
      double doubleValue12 = this.custom.getValue() ? this.radius.getFloat() : 0.62 + livingEntity4.getBbWidth() * 0.22;
      float floatValue15 = this.custom.getValue() ? this.density.getFloat() * 0.01F : 0.01F;
      float floatValue16 = this.custom.getValue() ? this.size.getFloat() : 0.28F;
      int value4 = this.custom.getValue() ? this.count.getInt() : 74;
      double doubleValue13 = System.currentTimeMillis() / 600.0;
      int value5 = 3 * ((this.trail.getValue() ? value4 : 0) + 3);
      this.m_3(
         event,
         gpuTextureView,
         value5,
         () -> {
            for (int i = 0; i < 3; i++) {
               float floatValue17 = i * 120.0F;
               double doubleValue14 = Math.toRadians(this.floatValue5 * 0.72F + floatValue17);
               double doubleValue15 = doubleValue13 + i * 2.094;
               if ((Boolean)this.trail.getValue()) {
                  for (int i2 = value4; i2 > 0; i2--) {
                     float floatValue18 = 1.0F - i2 / (value4 + 1.0F);
                     double doubleValue16 = doubleValue14 - i2 * floatValue15;
                     int value6 = ColorUtil.lerpColor(value2, value3, (float)(Math.sin(doubleValue15 - i2 * 0.08) * 0.5 + 0.5));
                     int value7 = (int)((value6 >>> 24 & 0xFF) * floatValue14 * floatValue18 * floatValue18 * 0.7F);
                     this.m_8(
                        event,
                        doubleValue8 + Math.cos(doubleValue16) * doubleValue12,
                        doubleValue11 + Math.sin(doubleValue16 * 1.35 + i) * 0.68,
                        doubleValue10 + Math.sin(doubleValue16) * doubleValue12,
                        this.floatValue5 + floatValue17 - i2 * 5,
                        1.15F * floatValue18,
                        ColorUtil.withAlpha(value6, value7),
                        false,
                        floatValue16
                     );
                  }
               }

               double doubleValue17 = doubleValue8 + Math.cos(doubleValue14) * doubleValue12;
               double doubleValue18 = doubleValue11 + Math.sin(doubleValue14 * 1.35 + i) * 0.68;
               double doubleValue19 = doubleValue10 + Math.sin(doubleValue14) * doubleValue12;
               int value8 = ColorUtil.lerpColor(value2, value3, (float)(Math.sin(doubleValue15) * 0.5 + 0.5));
               this.m_8(event, doubleValue17, doubleValue18, doubleValue19, this.floatValue5 + floatValue17, this.floatValue7 * 1.28F, value8, true, floatValue16);
            }
         }
      );
   }

   private void m_7(Render3DEvent event, LivingEntity livingEntity4, double doubleValue8, double doubleValue9, double doubleValue10, int value2, int value3, float floatValue14) {
      GpuTextureView gpuTextureView = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(IDENTIFIER2).getTextureView();
      if (gpuTextureView != null) {
         float floatValue15 = 500.0F * Math.min(this.floatValue4, 0.2F);
         this.floatValue = this.floatValue + 0.36363637F * Math.max(1.0F, this.floatValue4 * 1000.0F);
         if (this.floatValue > 180000.0F) {
            this.floatValue -= 180000.0F;
         }

         this.floatValue2 = this.floatValue2 + ((livingEntity4.hurtTime > 7 ? 1.0F : 0.0F) - this.floatValue2) * (1.0F - (float)Math.exp(-12.0F * this.floatValue4));
         float floatValue16 = 0.6F * (1.0F - this.floatValue2);
         float floatValue17 = 0.05F * floatValue15;
         float floatValue18 = 0.004F * floatValue15;
         float floatValue19 = 0.3F * floatValue15;
         double doubleValue11 = livingEntity4.getBbHeight() * 0.5;
         double doubleValue12 = System.currentTimeMillis() / 600.0;
         int value4 = 0;

         for (int i = 0; i < 3; i++) {
            TargetESP.StateHolder4 c7 = this.c7Array[i];
            double doubleValue13 = (this.floatValue + i * 120.0F) * (float) (Math.PI / 180.0);
            if (!c7.flag) {
               c7.doubleValue = doubleValue8;
               c7.doubleValue2 = doubleValue9;
               c7.doubleValue3 = doubleValue10;
               c7.flag = true;
            } else {
               c7.doubleValue = c7.doubleValue + (doubleValue8 + Math.sin(doubleValue13) * floatValue16 - c7.doubleValue) * floatValue17;
               c7.doubleValue2 = c7.doubleValue2 + (doubleValue9 + 0.2 + doubleValue11 * Math.sin(this.floatValue / (i + 1.0F) * (float) (Math.PI / 180.0)) - c7.doubleValue2) * floatValue17;
               c7.doubleValue3 = c7.doubleValue3 + (doubleValue10 + Math.cos(doubleValue13) * floatValue16 - c7.doubleValue3) * floatValue17;
            }

            c7.m_5_void((float)c7.doubleValue, (float)(c7.doubleValue2 + 0.7), (float)c7.doubleValue3);
            c7.m_4_void(floatValue18, floatValue19);
            value4 += c7.value2;
         }

         if (value4 != 0) {
            this.m_3(event, gpuTextureView, value4, () -> {
               for (int i2 = 0; i2 < 3; i2++) {
                  TargetESP.StateHolder4 c72 = this.c7Array[i2];
                  double doubleValue14 = doubleValue12 + i2 * 2.094;

                  for (int i3 = 0; i3 < c72.value2; i3++) {
                     TargetESP.StateHolder2 c5 = c72.m_6_C5(i3);
                     float floatValue20 = c5.floatValue4 / 50.0F;
                     int value5 = ColorUtil.lerpColor(value2, value3, (float)(Math.sin(doubleValue14 - i3 * 0.08) * 0.5 + 0.5));
                     this.m_8(event, c5.floatValue, c5.floatValue2, c5.floatValue3, 0.0F, 1.0F, ColorUtil.withAlpha(value5, (int)(255.0F * floatValue20 * floatValue14)), false, 0.6F * floatValue20);
                  }
               }
            });
         }
      }
   }

   private void reset() {
      for (TargetESP.StateHolder4 c7 : this.c7Array) {
         c7.reset();
      }

      this.floatValue2 = 0.0F;
   }

   private static float pulse(double doubleValue8) {
      return (float)(0.5 + 0.5 * Math.sin(doubleValue8 - System.currentTimeMillis() / 700.0));
   }

   private void m_2(Render3DEvent event, LivingEntity livingEntity4, double doubleValue8, double doubleValue9, double doubleValue10, int value2, int value3, float floatValue14) {
      float floatValue15 = livingEntity4.getBbHeight();
      double doubleValue11 = livingEntity4.getBbWidth() / 2.0 + 0.18;
      float floatValue16 = (float)(0.5 - 0.5 * Math.cos(System.currentTimeMillis() / 420.0));
      double doubleValue12 = doubleValue9 + 0.08 + (floatValue15 - 0.16) * floatValue16;
      GpuTextureView gpuTextureView = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(IDENTIFIER2).getTextureView();
      float floatValue17 = 0.14F * this.floatValue7;
      this.m_3(event, gpuTextureView, 244, () -> {
         for (int i = 0; i < 122; i++) {
            double doubleValue13 = i * Math.PI * 2.0 / 122.0;
            double doubleValue14 = doubleValue8 + Math.cos(doubleValue13) * doubleValue11;
            double doubleValue15 = doubleValue10 + Math.sin(doubleValue13) * doubleValue11;
            int value4 = ColorUtil.lerpColor(value2, value3, pulse(doubleValue13));
            int value5 = value4 >>> 24 & 0xFF;
            int value6 = ColorUtil.withAlpha(value4, value5 / 5);
            int value7 = ColorUtil.withAlpha(value4, (int)(value5 * 0.9F));
            this.m_8(event, doubleValue14, doubleValue12, doubleValue15, 0.0F, 1.0F, value6, false, floatValue17 * 2.2F);
            this.m_8(event, doubleValue14, doubleValue12, doubleValue15, 0.0F, 1.0F, value7, false, floatValue17);
         }
      });
   }

   private void m_6(Render3DEvent event, LivingEntity livingEntity4, double doubleValue8, double doubleValue9, double doubleValue10, int value2, int value3, float floatValue14) {
      float floatValue15 = livingEntity4.getBbHeight();
      float floatValue16 = livingEntity4.getBbWidth() * 0.9F;
      double doubleValue11 = System.currentTimeMillis() / 1000.0 * 1.25;
      double doubleValue12 = doubleValue9 + (0.1 + 0.45 * (1.0 - Math.cos(2.0 * doubleValue11))) * floatValue15;
      double doubleValue13 = doubleValue9 + (0.1 + 0.45 * (1.0 - Math.cos(2.0 * (doubleValue11 - 0.3)))) * floatValue15;
      Matrix4f matrix4f2 = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
      Vec3 vec3 = event.getVec3();
      Box3DRenderer.m_4(matrix4f2, 360, bufferBuilder2 -> {
         for (int i = 0; i < 90; i++) {
            double doubleValue14 = i * Math.PI * 2.0 / 90.0;
            double doubleValue15 = (i + 1) * Math.PI * 2.0 / 90.0;
            int value4 = ColorUtil.lerpColor(value2, value3, pulse(doubleValue14));
            int value5 = ColorUtil.lerpColor(value2, value3, pulse(doubleValue15));
            int value6 = value4 >> 16 & 0xFF;
            int value7 = value4 >> 8 & 0xFF;
            int value8 = value4 & 0xFF;
            int value9 = (int)((value4 >>> 24 & 0xFF) * floatValue14);
            int value10 = value5 >> 16 & 0xFF;
            int value11 = value5 >> 8 & 0xFF;
            int value12 = value5 & 0xFF;
            int value13 = (int)((value5 >>> 24 & 0xFF) * floatValue14);
            float floatValue17 = (float)(doubleValue8 - vec3.x + Math.cos(doubleValue14) * floatValue16);
            float floatValue18 = (float)(doubleValue10 - vec3.z + Math.sin(doubleValue14) * floatValue16);
            float floatValue19 = (float)(doubleValue8 - vec3.x + Math.cos(doubleValue15) * floatValue16);
            float floatValue20 = (float)(doubleValue10 - vec3.z + Math.sin(doubleValue15) * floatValue16);
            float floatValue21 = (float)(doubleValue12 - vec3.y);
            float floatValue22 = (float)(doubleValue13 - vec3.y);
            bufferBuilder2.addVertex(floatValue17, floatValue21, floatValue18).setColor(value6, value7, value8, value9);
            bufferBuilder2.addVertex(floatValue19, floatValue21, floatValue20).setColor(value10, value11, value12, value13);
            bufferBuilder2.addVertex(floatValue19, floatValue22, floatValue20).setColor(value10, value11, value12, 0);
            bufferBuilder2.addVertex(floatValue17, floatValue22, floatValue18).setColor(value6, value7, value8, 0);
         }
      });
   }

   private void m_2(Render3DEvent event, LivingEntity livingEntity4, double doubleValue8, double doubleValue9, double doubleValue10, int value2, float floatValue14) {
      GpuTextureView gpuTextureView = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(IDENTIFIER2).getTextureView();
      if (gpuTextureView != null) {
         int value3 = ColorUtil.rgba(75, 75, 75, 255);
         int value4 = value2 >>> 24 & 0xFF;
         double doubleValue11 = (MinecraftAccess.MINECRAFT.player.tickCount + event.getFloatValue()) / 10.0;
         double doubleValue12 = doubleValue11 * 2.0 % (Math.PI * 2);
         double doubleValue13 = livingEntity4.getBbWidth() * 1.2;
         double doubleValue14 = livingEntity4.getBbHeight() / 2.0 + 0.2;
         this.m_3(event, gpuTextureView, 60, () -> {
            for (int i = 0; i < 4; i++) {
               double doubleValue15 = doubleValue11 + i * 15.0;
               double doubleValue16 = i * (Math.PI / 2);

               for (int i2 = 0; i2 <= 14; i2++) {
                  double doubleValue17 = i2 * DOUBLE_VALUE / 14.0;
                  double doubleValue18 = doubleValue17 + doubleValue12 + doubleValue16;
                  double doubleValue19 = doubleValue8 + Math.cos(doubleValue18) * doubleValue13;
                  double doubleValue20 = doubleValue9 + doubleValue14 + Math.sin(doubleValue15 + doubleValue17 + i) * 0.7;
                  double doubleValue21 = doubleValue10 + Math.sin(doubleValue18) * doubleValue13;
                  float floatValue15 = i2 / 14.0F;
                  float floatValue16 = 0.4F * (0.5F + floatValue15) * 1.3F;
                  float floatValue17 = (float)(0.5 + 0.5 * Math.sin(doubleValue11 * 1.5 + i2 * 14 * 0.035));
                  int value5 = ColorUtil.lerpColor(value2, value3, floatValue17);
                  this.m_8(event, doubleValue19, doubleValue20, doubleValue21, 0.0F, 1.0F, ColorUtil.withAlpha(value5, (int)(value4 * floatValue14)), false, floatValue16);
               }
            }
         });
      }
   }

   private void m_6(Render3DEvent event, LivingEntity livingEntity4, double doubleValue8, double doubleValue9, double doubleValue10, int value2, float floatValue14) {
      Vec3 vec3 = event.getVec3();
      float floatValue15 = (float)(doubleValue8 - vec3.x);
      float floatValue16 = (float)(doubleValue9 - vec3.y);
      float floatValue17 = (float)(doubleValue10 - vec3.z);
      float floatValue18 = 1.2F - 0.5F * floatValue14;
      float floatValue19 = livingEntity4.getBbWidth() / 0.6F;
      float floatValue20 = livingEntity4.getBbHeight();
      float floatValue21 = 0.12F * this.size2.getFloat() * (0.35F + 0.65F * floatValue14);
      float floatValue22 = (float)Math.toRadians(this.floatValue3);
      float floatValue23 = (float)Math.toRadians(this.floatValue3 * 3.0F);
      Matrix4f matrix4f2 = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
      int value3 = ColorUtil.withAlpha(ColorUtil.lerpColor(value2, ColorUtil.rgba(255, 255, 255, 255), 0.5F), (int)(100.0F * floatValue14));
      int value4 = ColorUtil.withAlpha(value2, (int)(70.0F * floatValue14));
      int value5 = ColorUtil.withAlpha(value2, (int)((value2 >>> 24 & 0xFF) * 0.5F * floatValue14));
      int value6 = Math.max(1, this.count2.getInt());
      Box3DRenderer.m_4(matrix4f2, value6 * 48, bufferBuilder2 -> {
         for (int i = 0; i < value6; i++) {
            this.m_5(i, value6, floatValue15, floatValue16, floatValue17, floatValue19, floatValue20, floatValue18, floatValue22, floatValue23);
            this.matrix4f.rotateY(floatValue23 * 0.45F);
            this.m_1(bufferBuilder2, floatValue21 * 0.55F, value3);
            this.m_5(i, value6, floatValue15, floatValue16, floatValue17, floatValue19, floatValue20, floatValue18, floatValue22, floatValue23);
            this.m_1(bufferBuilder2, floatValue21, value4);
         }
      });
      GpuTextureView gpuTextureView = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(IDENTIFIER2).getTextureView();
      this.m_3(event, gpuTextureView, value6 * 3, () -> {
         for (int i2 = 0; i2 < value6; i2++) {
            this.m_4(i2, value6, (float)doubleValue8, (float)doubleValue9, (float)doubleValue10, floatValue19, floatValue20, floatValue18, floatValue22, floatValue23);
            this.m_8(event, this.vector3f2.x, this.vector3f2.y, this.vector3f2.z, 0.0F, 1.0F, value5, true, 0.38F * this.size2.getFloat() * floatValue14);
         }
      });
   }

   private void m_4(int value2, int value3, float floatValue14, float floatValue15, float floatValue16, float floatValue17, float floatValue18, float floatValue19, float floatValue20, float floatValue21) {
      float floatValue22 = value3 == 1 ? 0.5F : (float)value2 / (value3 - 1);
      float floatValue23 = value2 * 2.3999631F + floatValue20;
      float floatValue24 = 0.06F + floatValue22 * 1.14F;
      float floatValue25 = 1.0F - (2.0F * floatValue22 - 1.0F) * (2.0F * floatValue22 - 1.0F);
      float floatValue26 = (0.42F + 0.55F * floatValue25) * floatValue17 * floatValue19 * 1.25F;
      this.vector3f2
         .set(floatValue14 + (float)Math.cos(floatValue23) * floatValue26, floatValue15 + floatValue24 * floatValue18 + (float)Math.sin(floatValue21 * 0.35F + value2) * 0.04F, floatValue16 + (float)Math.sin(floatValue23) * floatValue26);
   }

   private void m_5(int value2, int value3, float floatValue14, float floatValue15, float floatValue16, float floatValue17, float floatValue18, float floatValue19, float floatValue20, float floatValue21) {
      this.m_4(value2, value3, floatValue14, floatValue15, floatValue16, floatValue17, floatValue18, floatValue19, floatValue20, floatValue21);
      this.matrix4f.identity();
      this.matrix4f.translate(this.vector3f2.x, this.vector3f2.y, this.vector3f2.z);
      this.matrix4f.rotateY(floatValue21);
      this.matrix4f.rotate(this.quaternionf.rotationAxis((float) (Math.PI / 3), 0.70710677F, 0.0F, 0.70710677F));
   }

   private void m_1(BufferBuilder bufferBuilder2, float floatValue14, int value2) {
      int value3 = value2 >> 16 & 0xFF;
      int value4 = value2 >> 8 & 0xFF;
      int value5 = value2 & 0xFF;
      int value6 = value2 >>> 24 & 0xFF;
      this.m_4(bufferBuilder2, -floatValue14, -floatValue14, floatValue14, floatValue14, -floatValue14, floatValue14, floatValue14, -floatValue14, -floatValue14, -floatValue14, -floatValue14, -floatValue14, 0.0F, -1.0F, 0.0F, value3, value4, value5, value6);
      this.m_4(bufferBuilder2, -floatValue14, floatValue14, -floatValue14, floatValue14, floatValue14, -floatValue14, floatValue14, floatValue14, floatValue14, -floatValue14, floatValue14, floatValue14, 0.0F, 1.0F, 0.0F, value3, value4, value5, value6);
      this.m_4(bufferBuilder2, -floatValue14, -floatValue14, -floatValue14, floatValue14, -floatValue14, -floatValue14, floatValue14, floatValue14, -floatValue14, -floatValue14, floatValue14, -floatValue14, 0.0F, 0.0F, -1.0F, value3, value4, value5, value6);
      this.m_4(bufferBuilder2, -floatValue14, floatValue14, floatValue14, floatValue14, floatValue14, floatValue14, floatValue14, -floatValue14, floatValue14, -floatValue14, -floatValue14, floatValue14, 0.0F, 0.0F, 1.0F, value3, value4, value5, value6);
      this.m_4(bufferBuilder2, -floatValue14, -floatValue14, -floatValue14, -floatValue14, floatValue14, -floatValue14, -floatValue14, floatValue14, floatValue14, -floatValue14, -floatValue14, floatValue14, -1.0F, 0.0F, 0.0F, value3, value4, value5, value6);
      this.m_4(bufferBuilder2, floatValue14, -floatValue14, floatValue14, floatValue14, floatValue14, floatValue14, floatValue14, floatValue14, -floatValue14, floatValue14, -floatValue14, -floatValue14, 1.0F, 0.0F, 0.0F, value3, value4, value5, value6);
   }

   private void m_4(
      BufferBuilder bufferBuilder2,
      float floatValue14,
      float floatValue15,
      float floatValue16,
      float floatValue17,
      float floatValue18,
      float floatValue19,
      float floatValue20,
      float floatValue21,
      float floatValue22,
      float floatValue23,
      float floatValue24,
      float floatValue25,
      float floatValue26,
      float floatValue27,
      float floatValue28,
      int value2,
      int value3,
      int value4,
      int value5
   ) {
      float floatValue29 = 0.48F + 0.52F * Math.max(0.0F, floatValue26 * 0.28F + floatValue27 * 0.86F + floatValue28 * 0.42F);
      int value6 = Math.min(255, (int)(value2 * floatValue29));
      int value7 = Math.min(255, (int)(value3 * floatValue29));
      int value8 = Math.min(255, (int)(value4 * floatValue29));
      this.m_5(bufferBuilder2, floatValue14, floatValue15, floatValue16, value6, value7, value8, value5);
      this.m_5(bufferBuilder2, floatValue17, floatValue18, floatValue19, value6, value7, value8, value5);
      this.m_5(bufferBuilder2, floatValue20, floatValue21, floatValue22, value6, value7, value8, value5);
      this.m_5(bufferBuilder2, floatValue23, floatValue24, floatValue25, value6, value7, value8, value5);
   }

   private void m_5(BufferBuilder bufferBuilder2, float floatValue14, float floatValue15, float floatValue16, int value2, int value3, int value4, int value5) {
      this.matrix4f.transformPosition(floatValue14, floatValue15, floatValue16, this.vector3f);
      bufferBuilder2.addVertex(this.vector3f.x, this.vector3f.y, this.vector3f.z).setColor(value2, value3, value4, value5);
   }

   private void m_3(Render3DEvent event, LivingEntity livingEntity4, double doubleValue8, double doubleValue9, double doubleValue10, int value2, float floatValue14) {
      Vec3 vec3 = event.getVec3();
      float floatValue15 = (float)(doubleValue8 - vec3.x);
      float floatValue16 = (float)(doubleValue9 - vec3.y + 0.025);
      float floatValue17 = (float)(doubleValue10 - vec3.z);
      Matrix4f matrix4f2 = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
      float floatValue18 = (this.radius2.getFloat() + livingEntity4.getBbWidth() * 0.4F) * (0.9F + 0.1F * floatValue14);
      int value3 = (int)((value2 >>> 24 & 0xFF) * floatValue14);
      int value4 = value2 >> 16 & 0xFF;
      int value5 = value2 >> 8 & 0xFF;
      int value6 = value2 & 0xFF;
      byte byteValue = 48;
      byte byteValue2 = 32;
      byte byteValue3 = 6;
      int value7 = byteValue + byteValue3 * 2 + 32 + (this.lightPillar.getValue() ? byteValue2 : 0);
      Box3DRenderer.m_4(matrix4f2, value7 * 4, bufferBuilder2 -> {
         float floatValue19 = (float)Math.toRadians(this.floatValue8);
         Notifications.m_5_C3_sArr();
         float floatValue20 = -((float)Math.toRadians(this.floatValue8 * 0.75F));
         float floatValue21 = floatValue18;
         float floatValue22 = floatValue18 - 0.065F;

         for (int i = 0; i < byteValue; i++) {
            if (i % 6 < 4) {
               double doubleValue11 = floatValue19 + i * ((Math.PI * 2) / byteValue);
               double doubleValue12 = floatValue19 + (i + 1) * ((Math.PI * 2) / byteValue);
               float floatValue23 = (float)Math.cos(doubleValue11);
               float floatValue24 = (float)Math.sin(doubleValue11);
               float floatValue25 = (float)Math.cos(doubleValue12);
               float floatValue26 = (float)Math.sin(doubleValue12);
               bufferBuilder2.addVertex(floatValue15 + floatValue23 * floatValue22, floatValue16, floatValue17 + floatValue24 * floatValue22).setColor(value4, value5, value6, (int)(value3 * 0.85F));
               bufferBuilder2.addVertex(floatValue15 + floatValue23 * floatValue21, floatValue16, floatValue17 + floatValue24 * floatValue21).setColor(value4, value5, value6, (int)(value3 * 0.85F));
               bufferBuilder2.addVertex(floatValue15 + floatValue25 * floatValue21, floatValue16, floatValue17 + floatValue26 * floatValue21).setColor(value4, value5, value6, (int)(value3 * 0.85F));
               bufferBuilder2.addVertex(floatValue15 + floatValue25 * floatValue22, floatValue16, floatValue17 + floatValue26 * floatValue22).setColor(value4, value5, value6, (int)(value3 * 0.85F));
            }
         }

         float floatValue27 = floatValue18 * 0.72F;
         float floatValue28 = 0.025F;

         for (int i2 = 0; i2 < 2; i2++) {
            float floatValue29 = i2 == 0 ? 0.0F : (float) (Math.PI / 3);

            for (int i3 = 0; i3 < 3; i3++) {
               double doubleValue13 = floatValue20 + floatValue29 + i3 * (Math.PI * 2.0 / 3.0);
               double doubleValue14 = floatValue20 + floatValue29 + (i3 + 1) % 3 * (Math.PI * 2.0 / 3.0);
               float floatValue30 = floatValue15 + (float)Math.cos(doubleValue13) * floatValue27;
               float floatValue31 = floatValue17 + (float)Math.sin(doubleValue13) * floatValue27;
               float floatValue32 = floatValue15 + (float)Math.cos(doubleValue14) * floatValue27;
               float floatValue33 = floatValue17 + (float)Math.sin(doubleValue14) * floatValue27;
               float floatValue34 = floatValue32 - floatValue30;
               float floatValue35 = floatValue33 - floatValue31;
               float floatValue36 = (float)Math.sqrt(floatValue34 * floatValue34 + floatValue35 * floatValue35);
               if (!(floatValue36 < 1.0E-4F)) {
                  float floatValue37 = -floatValue35 / floatValue36 * floatValue28;
                  float floatValue38 = floatValue34 / floatValue36 * floatValue28;
                  int value8 = (int)(value3 * 0.9F);
                  bufferBuilder2.addVertex(floatValue30 - floatValue37, floatValue16, floatValue31 - floatValue38).setColor(255, 255, 255, value8);
                  bufferBuilder2.addVertex(floatValue30 + floatValue37, floatValue16, floatValue31 + floatValue38).setColor(value4, value5, value6, value8);
                  bufferBuilder2.addVertex(floatValue32 + floatValue37, floatValue16, floatValue33 + floatValue38).setColor(value4, value5, value6, value8);
                  bufferBuilder2.addVertex(floatValue32 - floatValue37, floatValue16, floatValue33 - floatValue38).setColor(255, 255, 255, value8);
               }
            }
         }

         float floatValue39 = floatValue18 * 0.38F;
         float floatValue40 = floatValue39 - 0.035F;

         for (int i4 = 0; i4 < 24; i4++) {
            double doubleValue15 = floatValue19 * 1.5F + i4 * (Math.PI / 12);
            double doubleValue16 = floatValue19 * 1.5F + (i4 + 1) * (Math.PI / 12);
            float floatValue41 = (float)Math.cos(doubleValue15);
            float floatValue42 = (float)Math.sin(doubleValue15);
            float floatValue43 = (float)Math.cos(doubleValue16);
            float floatValue44 = (float)Math.sin(doubleValue16);
            bufferBuilder2.addVertex(floatValue15 + floatValue41 * floatValue40, floatValue16, floatValue17 + floatValue42 * floatValue40).setColor(value4, value5, value6, (int)(value3 * 0.7F));
            bufferBuilder2.addVertex(floatValue15 + floatValue41 * floatValue39, floatValue16, floatValue17 + floatValue42 * floatValue39).setColor(value4, value5, value6, (int)(value3 * 0.7F));
            bufferBuilder2.addVertex(floatValue15 + floatValue43 * floatValue39, floatValue16, floatValue17 + floatValue44 * floatValue39).setColor(value4, value5, value6, (int)(value3 * 0.7F));
            bufferBuilder2.addVertex(floatValue15 + floatValue43 * floatValue40, floatValue16, floatValue17 + floatValue44 * floatValue40).setColor(value4, value5, value6, (int)(value3 * 0.7F));
         }

         if ((Boolean)this.lightPillar.getValue()) {
            float floatValue45 = livingEntity4.getBbHeight() * 1.15F;
            float floatValue46 = floatValue18 * 0.88F;
            int value9 = (int)(value3 * 0.28F);

            for (int i5 = 0; i5 < byteValue2; i5++) {
               double doubleValue17 = floatValue19 * 0.4F + i5 * ((Math.PI * 2) / byteValue2);
               double doubleValue18 = floatValue19 * 0.4F + (i5 + 1) * ((Math.PI * 2) / byteValue2);
               float floatValue47 = (float)Math.cos(doubleValue17);
               float floatValue48 = (float)Math.sin(doubleValue17);
               float floatValue49 = (float)Math.cos(doubleValue18);
               float floatValue50 = (float)Math.sin(doubleValue18);
               bufferBuilder2.addVertex(floatValue15 + floatValue47 * floatValue46, floatValue16, floatValue17 + floatValue48 * floatValue46).setColor(value4, value5, value6, value9);
               bufferBuilder2.addVertex(floatValue15 + floatValue49 * floatValue46, floatValue16, floatValue17 + floatValue50 * floatValue46).setColor(value4, value5, value6, value9);
               bufferBuilder2.addVertex(floatValue15 + floatValue49 * floatValue46, floatValue16 + floatValue45, floatValue17 + floatValue50 * floatValue46).setColor(value4, value5, value6, 0);
               bufferBuilder2.addVertex(floatValue15 + floatValue47 * floatValue46, floatValue16 + floatValue45, floatValue17 + floatValue48 * floatValue46).setColor(value4, value5, value6, 0);
            }
         }
      });
      if ((Boolean)this.sparks.getValue()) {
         GpuTextureView gpuTextureView = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(IDENTIFIER2).getTextureView();
         if (gpuTextureView != null) {
            byte byteValue4 = 16;
            float floatValue51 = livingEntity4.getBbHeight() * 1.25F;
            this.m_3(event, gpuTextureView, byteValue4 * 3, () -> {
               for (int i6 = 0; i6 < byteValue4; i6++) {
                  float floatValue52 = i6 * 137.5F;
                  float floatValue53 = (this.floatValue8 * 0.008F + i6 * (1.0F / byteValue4)) % 1.0F;
                  double doubleValue19 = Math.toRadians(floatValue52 + this.floatValue8 * 0.3F);
                  double doubleValue20 = floatValue18 * 0.75F * Math.sqrt((i6 % 5 + 1) / 5.0);
                  double doubleValue21 = doubleValue8 + Math.cos(doubleValue19) * doubleValue20;
                  double doubleValue22 = doubleValue9 + 0.05 + floatValue53 * floatValue51;
                  double doubleValue23 = doubleValue10 + Math.sin(doubleValue19) * doubleValue20;
                  float floatValue54 = (float)Math.sin(floatValue53 * Math.PI);
                  int value10 = (int)(value3 * floatValue54 * 0.75F);
                  this.m_8(event, doubleValue21, doubleValue22, doubleValue23, (float)Math.toDegrees(doubleValue19), 1.0F, ColorUtil.withAlpha(value2, value10), true, 0.13F * (0.8F + floatValue54 * 0.4F));
               }
            });
         }
      }
   }

   private void m_3(Render3DEvent event, LivingEntity livingEntity4, double doubleValue8, double doubleValue9, double doubleValue10, int value2, int value3) {
      Vec3 vec3 = event.getVec3();
      float floatValue14 = livingEntity4.getBbHeight();
      Matrix4f matrix4f2 = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
      double doubleValue11 = doubleValue8 - vec3.x;
      double doubleValue12 = doubleValue9 + floatValue14 * 0.5 - vec3.y;
      double doubleValue13 = doubleValue10 - vec3.z;
      float floatValue15 = (float)Math.sqrt(doubleValue11 * doubleValue11 + doubleValue12 * doubleValue12 + doubleValue13 * doubleValue13);
      float floatValue16 = Math.max(1.0F, MinecraftAccess.MINECRAFT.getWindow().getHeight() / 700.0F);
      float floatValue17 = Mth.clamp(5.0F / Math.max(floatValue15, 0.1F), 0.55F, 1.7F) * floatValue16;
      float floatValue18 = (value2 >>> 24 & 0xFF) / 255.0F;
      this.c7List.clear();

      for (TargetESP.StateHolder5 c9 : this.c9List) {
         if (c9.livingEntity == livingEntity4 && !(c9.floatValue4 < 0.0F)) {
            float floatValue19 = c9.m_2_float();
            if (!(floatValue19 <= 0.01F)) {
               float floatValue20 = c9.m_0_float();
               float floatValue21 = c9.m_3_float();

               for (TargetESP.StateHolder c2 : c9.c2List) {
                  BoltRenderer.DataRecord c7 = this.m_0(c9, c2, doubleValue8, doubleValue9, doubleValue10, floatValue20, floatValue21, floatValue19 * floatValue18, value2, value3, floatValue17);
                  if (c7 != null) {
                     this.c7List.add(c7);
                  }
               }
            }
         }
      }

      if (!this.c7List.isEmpty()) {
         BoltRenderer.m_8(matrix4f2, vec3, this.c7List, 0.9F * floatValue16);
      }
   }

   private BoltRenderer.DataRecord m_0(
      TargetESP.StateHolder5 c9, TargetESP.StateHolder c2, double doubleValue8, double doubleValue9, double doubleValue10, float floatValue14, float floatValue15, float floatValue16, int value2, int value3, float floatValue17
   ) {
      int value4 = c2.value - 1;
      float floatValue18 = c2.flag ? Mth.clamp((floatValue14 - c2.floatValue3) / 0.35F, 0.0F, 1.0F) : floatValue14;
      float floatValue19 = floatValue15 * value4;
      float floatValue20 = floatValue18 * value4;
      if (floatValue20 - floatValue19 <= 0.001F) {
         return null;
      }

      int value5 = (int)Math.floor(floatValue19) + 1;
      int value6 = (int)Math.ceil(floatValue20) - 1;
      int value7 = Math.max(0, value6 - value5 + 1) + 2;
      double[] doubleArray = new double[value7 * 3];
      float[] floatArray = new float[value7];
      int[] intArray = new int[value7];
      int value8 = Math.min(255, (int)(255.0F * floatValue16 * c2.floatValue2));

      for (int i = 0; i < value7; i++) {
         boolean flag2 = i == 0 || i == value7 - 1;
         float floatValue21 = i == 0 ? floatValue19 : (i == value7 - 1 ? floatValue20 : value5 + i - 1);
         int value9 = Math.min((int)floatValue21, value4 - 1);
         float floatValue22 = floatValue21 - value9;
         int value10 = value9 * 3;
         int value11 = value10 + 3;
         doubleArray[i * 3] = doubleValue8 + c2.floatValueArray[value10] + (c2.floatValueArray[value11] - c2.floatValueArray[value10]) * floatValue22;
         doubleArray[i * 3 + 1] = doubleValue9 + c2.floatValueArray[value10 + 1] + (c2.floatValueArray[value11 + 1] - c2.floatValueArray[value10 + 1]) * floatValue22;
         doubleArray[i * 3 + 2] = doubleValue10 + c2.floatValueArray[value10 + 2] + (c2.floatValueArray[value11 + 2] - c2.floatValueArray[value10 + 2]) * floatValue22;
         float floatValue23 = floatValue21 / value4;
         float floatValue24 = c2.flag ? c2.floatValue3 + floatValue23 * 0.35F : floatValue23;
         int value12 = ColorUtil.lerpColor(value2, value3, Mth.clamp(floatValue24, 0.0F, 1.0F));
         float floatValue25 = (1.0F - 0.4F * floatValue23) * (flag2 ? 0.55F : 1.0F);
         floatArray[i] = 5.0F * floatValue17 * c9.floatValue3 * c2.floatValue * floatValue25;
         intArray[i] = ColorUtil.withAlpha(value12, flag2 ? value8 * 3 / 5 : value8);
      }

      return new BoltRenderer.DataRecord(doubleArray, floatArray, intArray, value7);
   }

   private void updateTrail() {
      for (int i = this.c9List.size() - 1; i >= 0; i--) {
         TargetESP.StateHolder5 c9 = this.c9List.get(i);
         c9.floatValue4 = c9.floatValue4 + this.floatValue4 * this.frequency.getFloat();
         if (c9.floatValue4 >= c9.floatValue) {
            this.c9List.remove(i);
         }
      }
   }

   private void m_1_void(LivingEntity livingEntity4) {
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      float floatValue14 = livingEntity4.getHealth();
      if (this.livingEntity3 != livingEntity4) {
         this.livingEntity3 = livingEntity4;
         this.value = livingEntity4.hurtTime;
         this.floatValue11 = floatValue14;
         this.floatValue12 = 0.0F;
         this.floatValue13 = 1.0F;
         this.floatValue9 = 0.0F;
         this.floatValue10 = 0.0F;
      }

      if (floatValue14 < this.floatValue11 - 0.01F) {
         this.floatValue12 = this.floatValue11 - floatValue14;
         this.floatValue13 = 0.0F;
      } else {
         this.floatValue13 = this.floatValue13 + this.floatValue4;
      }

      this.floatValue11 = floatValue14;
      if (livingEntity4.hurtTime > this.value && livingEntity4.hurtTime >= 7) {
         float floatValue15 = this.floatValue13 < 0.3F ? this.floatValue12 : 3.0F;
         this.floatValue13 = 1.0F;
         int value2 = Mth.clamp(Math.round(2.0F + floatValue15 * 0.45F), 2, 7);
         float floatValue16 = Mth.clamp(1.0F + floatValue15 * 0.06F, 1.0F, 1.8F);

         for (int i = 0; i < value2; i++) {
            this.m_5(livingEntity4, floatValue16, i * 0.05F);
         }
      }

      this.value = livingEntity4.hurtTime;
      this.floatValue9 = this.floatValue9 + this.floatValue4 * this.frequency.getFloat();
      if (this.floatValue9 >= this.floatValue10) {
         this.floatValue9 = 0.0F;
         this.floatValue10 = threadLocalRandom.nextFloat(0.22F, 0.55F);
         int value3 = 0;

         for (TargetESP.StateHolder5 c9 : this.c9List) {
            if (c9.livingEntity == livingEntity4 && !c9.flag) {
               value3++;
            }
         }

         if (value3 < this.count3.getInt()) {
            this.m_5(livingEntity4, 1.0F, 0.0F);
         }
      }
   }

   private void m_5(LivingEntity livingEntity4, float floatValue14, float floatValue15) {
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      float floatValue16 = livingEntity4.getBbWidth();
      float floatValue17 = livingEntity4.getBbHeight();
      double doubleValue8 = threadLocalRandom.nextDouble() * Math.PI * 2.0;
      float floatValue18 = floatValue16 * 0.5F + threadLocalRandom.nextFloat(0.5F, 1.1F);
      float floatValue19 = (float)Math.cos(doubleValue8) * floatValue18;
      float floatValue20 = floatValue17 * threadLocalRandom.nextFloat(0.5F, 1.05F);
      float floatValue21 = (float)Math.sin(doubleValue8) * floatValue18;
      double doubleValue9 = threadLocalRandom.nextDouble() * Math.PI * 2.0;
      float floatValue22 = threadLocalRandom.nextFloat() * floatValue16 * 0.35F;
      float floatValue23 = (float)Math.cos(doubleValue9) * floatValue22;
      float floatValue24 = floatValue17 * threadLocalRandom.nextFloat(0.2F, 0.75F);
      float floatValue25 = (float)Math.sin(doubleValue9) * floatValue22;
      TargetESP.StateHolder5 c9 = new TargetESP.StateHolder5(livingEntity4, -floatValue15, threadLocalRandom.nextFloat(0.75F, 1.0F), 0.16F, floatValue14, false, threadLocalRandom.nextInt());
      float[] floatArray = m_6(floatValue19, floatValue20, floatValue21, floatValue23, floatValue24, floatValue25, 5, 0.2F);
      c9.c2List.add(new TargetESP.StateHolder(floatArray, 1.0F, 1.0F, 0.0F, false));
      this.addTrail(c9);
      int value2 = floatArray.length / 3;
      if ((Boolean)this.branching.getValue()) {
         float floatValue26 = (float)Math.sqrt((floatValue23 - floatValue19) * (floatValue23 - floatValue19) + (floatValue24 - floatValue20) * (floatValue24 - floatValue20) + (floatValue25 - floatValue21) * (floatValue25 - floatValue21));
         float floatValue27 = (floatValue23 - floatValue19) / floatValue26;
         float floatValue28 = (floatValue24 - floatValue20) / floatValue26;
         float floatValue29 = (floatValue25 - floatValue21) / floatValue26;
         int value3 = 1 + threadLocalRandom.nextInt(3);

         for (int i = 0; i < value3; i++) {
            int value4 = threadLocalRandom.nextInt(value2 / 5, value2 * 3 / 4);
            float floatValue30 = floatArray[value4 * 3];
            float floatValue31 = floatArray[value4 * 3 + 1];
            float floatValue32 = floatArray[value4 * 3 + 2];
            float floatValue33 = floatValue27 + (float)threadLocalRandom.nextGaussian() * 0.9F;
            float floatValue34 = floatValue28 + (float)threadLocalRandom.nextGaussian() * 0.45F;
            float floatValue35 = floatValue29 + (float)threadLocalRandom.nextGaussian() * 0.9F;
            float floatValue36 = (float)Math.sqrt(floatValue33 * floatValue33 + floatValue34 * floatValue34 + floatValue35 * floatValue35);
            if (!(floatValue36 < 1.0E-4F)) {
               float floatValue37 = floatValue26 * threadLocalRandom.nextFloat(0.12F, 0.26F) / floatValue36;
               float[] floatArray2 = m_6(floatValue30, floatValue31, floatValue32, floatValue30 + floatValue33 * floatValue37, floatValue31 + floatValue34 * floatValue37, floatValue32 + floatValue35 * floatValue37, 3, 0.25F);
               c9.c2List.add(new TargetESP.StateHolder(floatArray2, 0.55F, 0.7F, (float)value4 / (value2 - 1), true));
            }
         }
      }

      if ((Boolean)this.groundBolts.getValue()) {
         int value5 = 1 + threadLocalRandom.nextInt(2);

         for (int i2 = 0; i2 < value5; i2++) {
            if (!(threadLocalRandom.nextFloat() > 0.75F)) {
               double doubleValue10 = threadLocalRandom.nextDouble() * Math.PI * 2.0;
               float floatValue38 = floatValue16 * 0.5F + threadLocalRandom.nextFloat(0.3F, 0.9F);
               TargetESP.StateHolder5 c92 = new TargetESP.StateHolder5(livingEntity4, -(floatValue15 + c9.floatValue2 * 0.8F), threadLocalRandom.nextFloat(0.55F, 0.75F), 0.12F, floatValue14 * 0.85F, true, threadLocalRandom.nextInt());
               float[] floatArray3 = m_6(floatValue23, floatValue24, floatValue25, (float)Math.cos(doubleValue10) * floatValue38, threadLocalRandom.nextFloat(0.0F, 0.08F), (float)Math.sin(doubleValue10) * floatValue38, 4, 0.25F);
               c92.c2List.add(new TargetESP.StateHolder(floatArray3, 0.7F, 0.9F, 0.0F, false));
               this.addTrail(c92);
            }
         }
      }
   }

   private void addTrail(TargetESP.StateHolder5 c9) {
      if (this.c9List.size() >= 48) {
         this.c9List.remove(0);
      }

      this.c9List.add(c9);
   }

   private static float[] m_6(float floatValue14, float floatValue15, float floatValue16, float floatValue17, float floatValue18, float floatValue19, int value2, float floatValue20) {
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      float[] floatArray = new float[]{floatValue14, floatValue15, floatValue16, floatValue17, floatValue18, floatValue19};

      for (int i = 0; i < value2; i++) {
         int value3 = floatArray.length / 3;
         float[] floatArray2 = new float[(2 * value3 - 1) * 3];

         for (int i2 = 0; i2 < value3 - 1; i2++) {
            int value4 = i2 * 3;
            int value5 = i2 * 6;
            floatArray2[value5] = floatArray[value4];
            floatArray2[value5 + 1] = floatArray[value4 + 1];
            floatArray2[value5 + 2] = floatArray[value4 + 2];
            float floatValue21 = floatArray[value4 + 3] - floatArray[value4];
            float floatValue22 = floatArray[value4 + 4] - floatArray[value4 + 1];
            float floatValue23 = floatArray[value4 + 5] - floatArray[value4 + 2];
            float floatValue24 = (float)Math.sqrt(floatValue21 * floatValue21 + floatValue22 * floatValue22 + floatValue23 * floatValue23);
            float floatValue25 = floatArray[value4] + floatValue21 * 0.5F;
            float floatValue26 = floatArray[value4 + 1] + floatValue22 * 0.5F;
            float floatValue27 = floatArray[value4 + 2] + floatValue23 * 0.5F;
            if (floatValue24 > 1.0E-4F) {
               float floatValue28 = floatValue21 / floatValue24;
               float floatValue29 = floatValue22 / floatValue24;
               float floatValue30 = floatValue23 / floatValue24;
               float floatValue31 = (float)threadLocalRandom.nextGaussian();
               float floatValue32 = (float)threadLocalRandom.nextGaussian();
               float floatValue33 = (float)threadLocalRandom.nextGaussian();
               float floatValue34 = floatValue31 * floatValue28 + floatValue32 * floatValue29 + floatValue33 * floatValue30;
               floatValue31 -= floatValue34 * floatValue28;
               floatValue32 -= floatValue34 * floatValue29;
               floatValue33 -= floatValue34 * floatValue30;
               float floatValue35 = (float)Math.sqrt(floatValue31 * floatValue31 + floatValue32 * floatValue32 + floatValue33 * floatValue33);
               if (floatValue35 > 1.0E-4F) {
                  float floatValue36 = floatValue24 * floatValue20 * threadLocalRandom.nextFloat(0.4F, 1.0F) / floatValue35;
                  floatValue25 += floatValue31 * floatValue36;
                  floatValue26 += floatValue32 * floatValue36;
                  floatValue27 += floatValue33 * floatValue36;
               }
            }

            floatArray2[value5 + 3] = floatValue25;
            floatArray2[value5 + 4] = floatValue26;
            floatArray2[value5 + 5] = floatValue27;
         }

         int value6 = (value3 - 1) * 3;
         int value7 = (2 * value3 - 2) * 3;
         floatArray2[value7] = floatArray[value6];
         floatArray2[value7 + 1] = floatArray[value6 + 1];
         floatArray2[value7 + 2] = floatArray[value6 + 2];
         floatArray = floatArray2;
      }

      return floatArray;
   }

   private void clear() {
      this.c9List.clear();
      this.livingEntity3 = null;
      this.floatValue9 = 0.0F;
      this.floatValue10 = 0.0F;
   }

   private static float hash(int value2, int value3) {
      int value4 = value2 * -1640531535 ^ value3 * -2048144789;
      value4 ^= value4 >>> 15;
      value4 *= 739982445;
      value4 ^= value4 >>> 12;
      return (value4 & 65535) / 65535.0F;
   }

   private void m_1_void() {
      float floatValue14 = this.floatValue4 * 60.0F;
      float floatValue15 = (float)Math.pow(0.86, floatValue14);
      float floatValue16 = (float)Math.pow(0.9, floatValue14);

      for (int i = this.c6List.size() - 1; i >= 0; i--) {
         TargetESP.StateHolder3 c6 = this.c6List.get(i);
         c6.floatValue5 = c6.floatValue5 + this.floatValue4;
         if (c6.floatValue5 >= c6.floatValue6) {
            this.c6List.remove(i);
         } else {
            c6.doubleValue = c6.doubleValue + c6.floatValue * this.floatValue4;
            c6.doubleValue2 = c6.doubleValue2 + c6.floatValue2 * this.floatValue4;
            c6.doubleValue3 = c6.doubleValue3 + c6.floatValue3 * this.floatValue4;
            c6.floatValue *= floatValue15;
            c6.floatValue2 = (c6.floatValue2 - 0.015F * floatValue14) * floatValue16;
            c6.floatValue3 *= floatValue15;
         }
      }
   }

   private void m_8_void(LivingEntity livingEntity4) {
      float floatValue14 = livingEntity4.getBbHeight();
      double doubleValue8 = livingEntity4.getBbWidth() / 2.0 + 0.18;
      float floatValue15 = (float)(0.5 - 0.5 * Math.cos(System.currentTimeMillis() / 420.0));
      double doubleValue9 = this.doubleValue6 + 0.08 + (floatValue14 - 0.16) * floatValue15;
      double doubleValue10 = Double.isNaN(this.doubleValue2) ? 0.0 : this.doubleValue5 - this.doubleValue2;
      double doubleValue11 = Double.isNaN(this.doubleValue3) ? 0.0 : this.doubleValue6 - this.doubleValue3;
      double doubleValue12 = Double.isNaN(this.doubleValue4) ? 0.0 : this.doubleValue7 - this.doubleValue4;
      double doubleValue13 = Math.sqrt(doubleValue10 * doubleValue10 + doubleValue11 * doubleValue11 + doubleValue12 * doubleValue12);
      this.doubleValue2 = this.doubleValue5;
      this.doubleValue3 = this.doubleValue6;
      this.doubleValue4 = this.doubleValue7;
      float floatValue16 = Math.min(this.floatValue4, 0.1F);
      double doubleValue14 = doubleValue13 / this.floatValue4;
      if (doubleValue14 > 0.6) {
         this.doubleValue = this.doubleValue + Math.min(doubleValue14 * 60.0 + 60.0, 480.0) * floatValue16;
      } else {
         this.doubleValue = 0.0;
      }

      int value2 = Math.min((int)this.doubleValue, 1200 - this.c6List.size());
      this.doubleValue = this.doubleValue - (int)this.doubleValue;
      if (value2 > 0) {
         ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

         for (int i = 0; i < value2; i++) {
            double doubleValue15 = threadLocalRandom.nextDouble() * Math.PI * 2.0;
            double doubleValue16 = Math.cos(doubleValue15);
            double doubleValue17 = Math.sin(doubleValue15);
            double doubleValue18 = doubleValue8 * threadLocalRandom.nextDouble(0.88, 1.06);
            float floatValue17 = 0.04F + threadLocalRandom.nextFloat() * 0.1F;
            this.c6List
               .add(
                  new TargetESP.StateHolder3(
                     this.doubleValue5 + doubleValue16 * doubleValue18,
                     doubleValue9 + threadLocalRandom.nextDouble(-0.06, 0.06),
                     this.doubleValue7 + doubleValue17 * doubleValue18,
                     (float)doubleValue16 * floatValue17,
                     threadLocalRandom.nextFloat() * 0.06F,
                     (float)doubleValue17 * floatValue17,
                     0.07F,
                     2.2F + threadLocalRandom.nextFloat() * 1.4F
                  )
               );
         }
      }
   }

   private void m_9(Render3DEvent event) {
      if (!this.c6List.isEmpty()) {
         GpuTextureView gpuTextureView = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(IDENTIFIER2).getTextureView();
         if (gpuTextureView != null) {
            int value2 = this.themeColor.getValue() ? ColorUtil.primaryColor() : (Integer)this.color.getValue();
            if (this.livingEntity != null && (Boolean)this.redOnDamage.getValue()) {
               float floatValue14 = Math.min(1.0F, this.livingEntity.hurtTime / 10.0F);
               value2 = ColorUtil.lerpColor(value2, ColorUtil.rgba(255, 45, 45, 255), floatValue14);
            }

            int value3 = value2;
            int value4 = value3 >>> 24 & 0xFF;
            this.m_3(event, gpuTextureView, this.c6List.size() * 3, () -> {
               for (TargetESP.StateHolder3 c6 : this.c6List) {
                  float floatValue15 = 1.0F - c6.floatValue5 / c6.floatValue6;
                  if (!(floatValue15 <= 0.01F)) {
                     float floatValue16 = Math.min(1.0F, c6.floatValue5 / 0.25F);
                     float floatValue17 = floatValue15 * floatValue15 * floatValue15;
                     float floatValue18 = floatValue16 * floatValue17;
                     this.m_8(event, c6.doubleValue, c6.doubleValue2, c6.doubleValue3, 0.0F, 1.0F, ColorUtil.withAlpha(value3, (int)(value4 * 0.75F * floatValue18)), true, c6.floatValue4);
                  }
               }
            });
         }
      }
   }

   private void m_3(Render3DEvent event, GpuTextureView gpuTextureView, int value2, Runnable runnable) {
      if (gpuTextureView != null && value2 > 0) {
         Texture3DRenderer.m_8(new Matrix4f(event.getMatrix4f()), gpuTextureView, value2, bufferBuilder2 -> {
            this.bufferBuilder = bufferBuilder2;

            try {
               runnable.run();
            } finally {
               this.bufferBuilder = null;
            }
         });
      }
   }

   private void m_8(Render3DEvent event, double doubleValue8, double doubleValue9, double doubleValue10, float floatValue14, float floatValue15, int value2, boolean flag2, float floatValue16) {
      this.m_1(event, doubleValue8, doubleValue9, doubleValue10, floatValue14, floatValue15, value2, flag2, floatValue16, floatValue16);
   }

   private void m_1(Render3DEvent event, double doubleValue8, double doubleValue9, double doubleValue10, float floatValue14, float floatValue15, int value2, boolean flag2, float floatValue16, float floatValue17) {
      this.m_1(event, doubleValue8, doubleValue9, doubleValue10, floatValue14, floatValue16 * floatValue15, floatValue17 * floatValue15);
      this.m_1_void(value2);
      if (flag2) {
         this.m_1_void(ColorUtil.rgba(255, 255, 255, Math.min(180, (value2 >>> 24 & 0xFF) * 3 / 4)));
         this.matrix4f.scale(1.75F);
         this.m_1_void(ColorUtil.withAlpha(value2, (value2 >>> 24 & 0xFF) / 4));
      }
   }

   private void m_1(Render3DEvent event, double doubleValue8, double doubleValue9, double doubleValue10, float floatValue14, float floatValue15, float floatValue16) {
      this.matrix4f.set(event.getMatrix4f2());
      this.matrix4f.translate((float)(doubleValue8 - event.getVec3().x), (float)(doubleValue9 - event.getVec3().y), (float)(doubleValue10 - event.getVec3().z));
      this.matrix4f.rotate(event.getQuaternionf());
      this.matrix4f.rotateZ((float)Math.toRadians(floatValue14));
      this.matrix4f.scale(floatValue15, floatValue16, 1.0F);
   }

   private void m_1_void(int value2) {
      int value3 = value2 >>> 24 & 0xFF;
      int value4 = value2 >> 16 & 0xFF;
      int value5 = value2 >> 8 & 0xFF;
      int value6 = value2 & 0xFF;
      this.m_6(-0.5F, 0.5F, 0.0F, 1.0F, value4, value5, value6, value3);
      this.m_6(0.5F, 0.5F, 1.0F, 1.0F, value4, value5, value6, value3);
      this.m_6(0.5F, -0.5F, 1.0F, 0.0F, value4, value5, value6, value3);
      this.m_6(-0.5F, -0.5F, 0.0F, 0.0F, value4, value5, value6, value3);
   }

   private void m_6(float floatValue14, float floatValue15, float floatValue16, float floatValue17, int value2, int value3, int value4, int value5) {
      this.matrix4f.transformPosition(floatValue14, floatValue15, 0.0F, this.vector3f);
      this.bufferBuilder.addVertex(this.vector3f.x, this.vector3f.y, this.vector3f.z).setUv(floatValue16, floatValue17).setColor(value2, value3, value4, value5);
   }

   private void m_2_void(LivingEntity livingEntity4) {
      if (!((String)this.mode.getValue()).equals("Призраки") || !((String)this.displayMode.getValue()).equals("Призрачный")) {
         float floatValue14 = livingEntity4.hurtTime > 5 ? 0.7F : 1.0F;
         int value2 = livingEntity4.hurtTime > 5 ? 14 : 10;
         this.floatValue7 = this.floatValue7 + (floatValue14 - this.floatValue7) * (1.0F - (float)Math.exp(-value2 * this.floatValue4));
         if (((String)this.mode.getValue()).equals("Призраки")) {
            this.floatValue6 = this.custom.getValue() ? this.speed.getFloat() : 180.0F;
            this.floatValue5 = this.floatValue5 + this.floatValue6 * this.floatValue4;
            if (this.floatValue5 > 180000.0F) {
               this.floatValue5 -= 180000.0F;
            }
         } else if (!((String)this.mode.getValue()).equals("Кружок")) {
            if (!((String)this.mode.getValue()).equals("Celestial")) {
               if (((String)this.mode.getValue()).equals("Кубики")) {
                  this.floatValue3 = (this.floatValue3 + this.speed2.getFloat() * 90.0F * this.floatValue4) % 360000.0F;
               } else if (((String)this.mode.getValue()).equals("Магический круг")) {
                  this.floatValue8 = (this.floatValue8 + this.speed3.getFloat() * 55.0F * this.floatValue4) % 360.0F;
               } else if (!((String)this.mode.getValue()).equals("Молния")) {
                  float floatValue15 = 400.0F;
                  float floatValue16 = 464.0F;
                  if (!this.flag) {
                     this.floatValue6 = this.floatValue6 + floatValue15 * this.floatValue4;
                     if (this.floatValue6 > floatValue16) {
                        this.floatValue6 = floatValue16;
                        this.flag = true;
                     }
                  } else {
                     this.floatValue6 = this.floatValue6 - floatValue15 * this.floatValue4;
                     if (this.floatValue6 < -floatValue16) {
                        this.floatValue6 = -floatValue16;
                        this.flag = false;
                     }
                  }

                  this.floatValue5 = (this.floatValue5 + this.floatValue6 * this.floatValue4) % 360.0F;
               }
            }
         }
      }
   }

   @Override
   public void onEnable() {
      this.longValue = System.currentTimeMillis();
      this.floatValue7 = 1.0F;
      this.c6List.clear();
      this.reset();
      this.floatValue = 0.0F;
      this.doubleValue2 = this.doubleValue3 = this.doubleValue4 = Double.NaN;
      this.floatValue8 = 0.0F;
      this.floatValue3 = 0.0F;
      this.clear();
   }

   @Override
   public void onDisable() {
      this.livingEntity = null;
      this.livingEntity2 = null;
      this.animation.setDuration(0.0F);
      this.animation2.setDuration(0.0F);
      this.c6List.clear();
      this.reset();
      this.floatValue = 0.0F;
      this.doubleValue2 = this.doubleValue3 = this.doubleValue4 = Double.NaN;
      this.floatValue8 = 0.0F;
      this.floatValue3 = 0.0F;
      this.clear();
   }

   static {
      m_1(null);
   }

   public static void m_1(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_9_C3_sArr() {
      return moduleArray;
   }

   private static final class StateHolder {
      public final float[] floatValueArray;
      public final int value;
      public final float floatValue;
      public final float floatValue2;
      public final float floatValue3;
      public final boolean flag;

      public StateHolder(float[] floatArray, float floatValue14, float floatValue15, float floatValue16, boolean flag2) {
         this.floatValueArray = floatArray;
         this.value = floatArray.length / 3;
         this.floatValue = floatValue14;
         this.floatValue2 = floatValue15;
         this.floatValue3 = floatValue16;
         this.flag = flag2;
      }
   }

   private static final class StateHolder2 {
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public float floatValue4;
   }

   private static final class StateHolder3 {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public float floatValue4;
      public float floatValue5;
      public float floatValue6;

      public StateHolder3(double doubleValue8, double doubleValue9, double doubleValue10, float floatValue14, float floatValue15, float floatValue16, float floatValue17, float floatValue18) {
         this.doubleValue = doubleValue8;
         this.doubleValue2 = doubleValue9;
         this.doubleValue3 = doubleValue10;
         this.floatValue = floatValue14;
         this.floatValue2 = floatValue15;
         this.floatValue3 = floatValue16;
         this.floatValue4 = floatValue17;
         this.floatValue6 = floatValue18;
      }
   }

   private static final class StateHolder4 {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public boolean flag;
      public final TargetESP.StateHolder2[] c5Array;
      public int value;
      public int value2;

      public StateHolder4() {
         this.c5Array = new TargetESP.StateHolder2[64];

         for (int i = 0; i < 64; i++) {
            this.c5Array[i] = new TargetESP.StateHolder2();
         }
      }

      public void reset() {
         this.flag = false;
         this.value = 0;
         this.value2 = 0;
      }

      public void m_5_void(float floatValue14, float floatValue15, float floatValue16) {
         if (this.value2 == 64) {
            this.value = (this.value + 1) % 64;
            this.value2--;
         }

         TargetESP.StateHolder2 c5 = this.c5Array[(this.value + this.value2) % 64];
         c5.floatValue = floatValue14;
         c5.floatValue2 = floatValue15;
         c5.floatValue3 = floatValue16;
         c5.floatValue4 = Float.intBitsToFloat((int)2970133468715417600L);
         this.value2++;
      }

      public void m_4_void(float floatValue14, float floatValue15) {
         Notifications.m_5_C3_sArr();

         for (int i = 0; i < this.value2; i++) {
            TargetESP.StateHolder2 c5 = this.m_6_C5(i);
            c5.floatValue2 += floatValue14;
            c5.floatValue4 -= floatValue15;
         }

         while (this.value2 > 0 && this.c5Array[this.value].floatValue4 <= 0.0F) {
            this.value = (this.value + 1) % 64;
            this.value2--;
         }
      }

      public TargetESP.StateHolder2 m_6_C5(int value3) {
         return this.c5Array[(this.value + value3) % 64];
      }
   }

   private static final class StateHolder5 {
      public final LivingEntity livingEntity;
      public final ArrayList<TargetESP.StateHolder> c2List;
      public final float floatValue;
      public final float floatValue2;
      public final float floatValue3;
      public final boolean flag;
      public final int value;
      public float floatValue4;

      public StateHolder5(LivingEntity livingEntity4, float floatValue14, float floatValue15, float floatValue16, float floatValue17, boolean flag2, int value2) {
         this.c2List = new ArrayList<>(4);
         this.livingEntity = livingEntity4;
         this.floatValue4 = floatValue14;
         this.floatValue = floatValue15;
         this.floatValue2 = floatValue16;
         this.floatValue3 = floatValue17;
         this.flag = flag2;
         this.value = value2;
      }

      public float m_0_float() {
         float floatValue14 = 1.0F - Math.min(1.0F, this.floatValue4 / this.floatValue2);
         return 1.0F - floatValue14 * floatValue14 * floatValue14;
      }

      public float m_1_float() {
         Notifications.m_5_C3_sArr();
         float floatValue14 = this.floatValue * 0.5F;
         return this.floatValue4 > floatValue14 ? Math.min(1.0F, (this.floatValue4 - floatValue14) / (this.floatValue - floatValue14)) : 0.0F;
      }

      public float m_3_float() {
         return this.m_1_float() * 0.65F;
      }

      public float m_2_float() {
         Notifications.m_5_C3_sArr();
         float floatValue14 = 1.0F - this.m_1_float();
         float floatValue15 = this.floatValue4 >= this.floatValue2 ? 0.8F + 0.2F * TargetESP.hash(this.value, (int)(this.floatValue4 * 18.0F)) : 1.0F;
         return floatValue14 * (float)Math.sqrt(floatValue14) * floatValue15;
      }
   }
}
