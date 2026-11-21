package wtf.onetap.module.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.AttackEntityEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Texture3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Particles", description = "Частицы в мире", category = Category.RENDER)
public class Particles extends Module {
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "images/glow.png");
   private static final Identifier IDENTIFIER2 = Identifier.fromNamespaceAndPath("onetap", "images/particles/spark_1.png");
   private static final Identifier IDENTIFIER3 = Identifier.fromNamespaceAndPath("onetap", "images/particles/star.png");
   private static final Identifier IDENTIFIER4 = Identifier.fromNamespaceAndPath("onetap", "images/particles/snow.png");
   private static final Identifier IDENTIFIER5 = Identifier.fromNamespaceAndPath("onetap", "images/particles/orb.png");
   private final ModeSetting style = new ModeSetting("Стиль", "Обычный", "Обычный", "Огоньки").build();
   private final BooleanSetting inWorld = new BooleanSetting("В мире", true).build();
   private final BooleanSetting onHit = new BooleanSetting("При ударе", false).build();
   private final ModeSetting hitStyle = new ModeSetting("Стиль (удар)", "Обычный", "Обычный", "Орбизы").visibleWhen(() -> (Boolean)this.onHit.getValue()).build();
   private final ModeSetting texture = new ModeSetting("Текстура", "Свечение", "Свечение", "Искра", "Звезда", "Снег").build();
   private final ModeSetting physics = new ModeSetting("Физика", "Парение", "Парение", "Падение").visibleWhen(() -> !this.isSparkStyle()).build();
   private final FloatSetting worldCount = new FloatSetting("Кол-во (мир)", 80.0F, 10.0F, 250.0F, 1.0F).visibleWhen(() -> (Boolean)this.inWorld.getValue()).build();
   private final FloatSetting worldDistance = new FloatSetting("Дистанция (мир)", 24.0F, 8.0F, 48.0F, 1.0F).visibleWhen(() -> (Boolean)this.inWorld.getValue()).build();
   private final FloatSetting worldSize = new FloatSetting("Размер (мир)", 0.35F, 0.1F, 1.2F, 0.05F).visibleWhen(() -> (Boolean)this.inWorld.getValue()).build();
   private final FloatSetting worldLifetime = new FloatSetting("Жизнь (мир)", 90.0F, 20.0F, 200.0F, 5.0F).visibleWhen(() -> (Boolean)this.inWorld.getValue()).build();
   private final FloatSetting hitCount = new FloatSetting("Кол-во (удар)", 40.0F, 5.0F, 120.0F, 1.0F).visibleWhen(() -> (Boolean)this.onHit.getValue()).build();
   private final FloatSetting hitSize = new FloatSetting("Размер (удар)", 0.4F, 0.1F, 1.2F, 0.05F).visibleWhen(() -> (Boolean)this.onHit.getValue()).build();
   private final FloatSetting hitLifetime = new FloatSetting("Жизнь (удар)", 50.0F, 15.0F, 200.0F, 5.0F).visibleWhen(() -> (Boolean)this.onHit.getValue()).build();
   private final BooleanSetting themeColor = new BooleanSetting("Цвет темы", true).build();
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(140, 180, 255, 255)).visibleWhen(() -> !(Boolean)this.themeColor.getValue()).build();
   private final ArrayList<Particles.StateHolder> c9List = new ArrayList<>();
   private final MutableBlockPos mutableBlockPos = new MutableBlockPos();
   private final Matrix4f matrix4f = new Matrix4f();
   private final Vector4f[] vector4fArray = new Vector4f[]{new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f()};
   private Object object;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private boolean flag;
   private double doubleValue4;
   private double doubleValue5;
   private double doubleValue6;
   private double doubleValue7;

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if (this.object != MinecraftAccess.MINECRAFT.level) {
            this.clear();
            this.object = MinecraftAccess.MINECRAFT.level;
         }

         double doubleValue8 = MinecraftAccess.MINECRAFT.player.getX();
         double doubleValue9 = MinecraftAccess.MINECRAFT.player.getY();
         double doubleValue10 = MinecraftAccess.MINECRAFT.player.getZ();
         if (this.flag) {
            this.doubleValue4 = doubleValue8 - this.doubleValue;
            this.doubleValue5 = doubleValue9 - this.doubleValue2;
            this.doubleValue6 = doubleValue10 - this.doubleValue3;
            this.doubleValue7 = Math.sqrt(this.doubleValue4 * this.doubleValue4 + this.doubleValue5 * this.doubleValue5 + this.doubleValue6 * this.doubleValue6);
         } else {
            this.doubleValue4 = this.doubleValue5 = this.doubleValue6 = this.doubleValue7 = 0.0;
         }

         this.doubleValue = doubleValue8;
         this.doubleValue2 = doubleValue9;
         this.doubleValue3 = doubleValue10;
         this.flag = true;
         boolean flag2 = this.isSparkStyle();
         boolean flag3 = !flag2 && ((String)this.physics.getValue()).equals("Падение");
         float floatValue = this.worldDistance.getFloat() * 1.4F;
         float floatValue2 = floatValue * floatValue;

         for (int i = this.c9List.size() - 1; i >= 0; i--) {
            Particles.StateHolder c9 = this.c9List.get(i);
            if (c9.m_2(flag3, flag2, doubleValue8, doubleValue9, doubleValue10, c9.flag ? Float.MAX_VALUE : floatValue2)) {
               this.c9List.remove(i);
            }
         }

         this.m_7_void();
         if ((Boolean)this.inWorld.getValue()) {
            this.m_2(this.worldLifetime.getInt(), flag3, flag2, doubleValue8, doubleValue9, doubleValue10);
         }
      } else {
         this.clear();
      }
   }

   @EventTarget
   private void onAttackEntity(AttackEntityEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null && (Boolean)this.onHit.getValue()) {
         this.m_4(event.getEntity());
      }
   }

   private boolean isOrbHitStyle() {
      return ((String)this.hitStyle.getValue()).equals("Орбизы");
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null && !this.c9List.isEmpty()) {
         GpuTextureView gpuTextureView = this.m_8_GpuTextureView();
         GpuTextureView gpuTextureView2 = this.getTextureView(IDENTIFIER5);
         if (gpuTextureView != null || gpuTextureView2 != null) {
            int value = this.themeColor.getValue() ? ColorUtil.primaryColor() : (Integer)this.color.getValue();
            int value2 = ColorUtil.lerpColor(value, -1, 0.52F);
            Vec3 vec3 = event.getVec3();
            float floatValue = event.getFloatValue();
            float floatValue2 = this.worldSize.getFloat();
            float floatValue3 = this.hitSize.getFloat();
            float floatValue4 = this.worldDistance.getFloat() * 1.1F;
            float floatValue5 = this.worldDistance.getFloat() * 1.4F;
            float floatValue6 = floatValue4 * floatValue4;
            double doubleValue8 = MinecraftAccess.MINECRAFT.player.getX();
            double doubleValue9 = MinecraftAccess.MINECRAFT.player.getY();
            double doubleValue10 = MinecraftAccess.MINECRAFT.player.getZ();
            int value3 = 0;
            int value4 = 0;

            for (Particles.StateHolder c9 : this.c9List) {
               if (c9.flag2) {
                  value4++;
               } else {
                  value3++;
               }
            }

            if (value3 > 0 && gpuTextureView != null) {
               Texture3DRenderer.m_3(event.getMatrix4f(), gpuTextureView, value3, true, bufferBuilder -> {
                  for (Particles.StateHolder c92 : this.c9List) {
                     if (!c92.flag2) {
                        this.m_8(bufferBuilder, c92, event, vec3, floatValue, c92.flag ? floatValue3 : floatValue2, c92.value, floatValue4, floatValue5, floatValue6, doubleValue8, doubleValue9, doubleValue10, value2);
                     }
                  }
               });
            }

            if (value4 > 0 && gpuTextureView2 != null) {
               int value5 = ColorUtil.lerpColor(value, -1, 0.18F);
               Texture3DRenderer.m_8(event.getMatrix4f(), gpuTextureView2, value4, true, true, bufferBuilder2 -> {
                  for (Particles.StateHolder c93 : this.c9List) {
                     if (c93.flag2) {
                        this.m_8(bufferBuilder2, c93, event, vec3, floatValue, floatValue3 * 0.62F, c93.value, floatValue4, floatValue5, floatValue6, doubleValue8, doubleValue9, doubleValue10, value5);
                     }
                  }
               });
            }
         }
      }
   }

   private void m_8(
      BufferBuilder bufferBuilder,
      Particles.StateHolder c9,
      Render3DEvent event,
      Vec3 vec3,
      float floatValue,
      float floatValue2,
      float floatValue3,
      float floatValue4,
      float floatValue5,
      float floatValue6,
      double doubleValue8,
      double doubleValue9,
      double doubleValue10,
      int value
   ) {
      float floatValue7 = this.m_2(c9, floatValue, floatValue3, floatValue4, floatValue5, floatValue6, doubleValue8, doubleValue9, doubleValue10);
      if (!(floatValue7 <= 0.01F)) {
         double doubleValue11 = c9.doubleValue + (c9.doubleValue4 - c9.doubleValue) * floatValue;
         double doubleValue12 = c9.doubleValue2 + (c9.doubleValue5 - c9.doubleValue2) * floatValue;
         double doubleValue13 = c9.doubleValue3 + (c9.doubleValue6 - c9.doubleValue3) * floatValue;
         float floatValue8 = floatValue2 * (0.55F + 0.45F * c9.floatValue4) * floatValue7;
         if (!(floatValue8 <= 0.001F)) {
            int value2 = floatValue8 < floatValue2 * 0.35F ? Math.clamp((int)(floatValue8 / (floatValue2 * 0.35F) * 255.0F), 0, 255) : 255;
            this.m_5(bufferBuilder, event, vec3, doubleValue11, doubleValue12, doubleValue13, floatValue8, ColorUtil.withAlpha(value, value2));
         }
      }
   }

   private float m_2(Particles.StateHolder c9, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, double doubleValue8, double doubleValue9, double doubleValue10) {
      double doubleValue11 = c9.doubleValue + (c9.doubleValue4 - c9.doubleValue) * floatValue;
      double doubleValue12 = c9.doubleValue2 + (c9.doubleValue5 - c9.doubleValue2) * floatValue;
      double doubleValue13 = c9.doubleValue3 + (c9.doubleValue6 - c9.doubleValue3) * floatValue;
      float floatValue6 = c9.value2 + floatValue;
      float floatValue7 = Math.clamp(floatValue6 / Math.max(1.0F, floatValue2), 0.0F, 1.0F);
      float floatValue8 = 1.0F - floatValue7 * floatValue7 * floatValue7;
      float floatValue9 = 1.0F;
      if (!c9.flag) {
         double doubleValue14 = doubleValue11 - doubleValue8;
         double doubleValue15 = doubleValue12 - doubleValue9;
         double doubleValue16 = doubleValue13 - doubleValue10;
         double doubleValue17 = doubleValue14 * doubleValue14 + doubleValue15 * doubleValue15 + doubleValue16 * doubleValue16;
         if (doubleValue17 > floatValue5) {
            double doubleValue18 = Math.sqrt(doubleValue17);
            float floatValue10 = (float)Math.clamp((doubleValue18 - floatValue3) / (floatValue4 - floatValue3), 0.0, 1.0);
            floatValue9 = 1.0F - floatValue10 * floatValue10 * floatValue10;
         }
      }

      float floatValue11 = Math.clamp(floatValue6 / 8.0F, 0.0F, 1.0F);
      float floatValue12 = floatValue11 - 1.0F;
      float floatValue13 = 1.0F + 2.70158F * floatValue12 * floatValue12 * floatValue12 + 1.70158F * floatValue12 * floatValue12;
      return Math.min(floatValue8, floatValue9) * floatValue13;
   }

   private void m_5(BufferBuilder bufferBuilder, Render3DEvent event, Vec3 vec3, double doubleValue8, double doubleValue9, double doubleValue10, float floatValue, int value) {
      this.matrix4f.set(event.getMatrix4f2());
      this.matrix4f.translate((float)(doubleValue8 - vec3.x), (float)(doubleValue9 - vec3.y), (float)(doubleValue10 - vec3.z));
      this.matrix4f.rotate(event.getQuaternionf());
      this.matrix4f.scale(floatValue);
      if (this.m_9(this.matrix4f)) {
         int value2 = value >> 24 & 0xFF;
         int value3 = value >> 16 & 0xFF;
         int value4 = value >> 8 & 0xFF;
         int value5 = value & 0xFF;
         bufferBuilder.addVertex(this.vector4fArray[0].x, this.vector4fArray[0].y, this.vector4fArray[0].z).setUv(0.0F, 1.0F).setColor(value3, value4, value5, value2);
         bufferBuilder.addVertex(this.vector4fArray[1].x, this.vector4fArray[1].y, this.vector4fArray[1].z).setUv(1.0F, 1.0F).setColor(value3, value4, value5, value2);
         bufferBuilder.addVertex(this.vector4fArray[2].x, this.vector4fArray[2].y, this.vector4fArray[2].z).setUv(1.0F, 0.0F).setColor(value3, value4, value5, value2);
         bufferBuilder.addVertex(this.vector4fArray[3].x, this.vector4fArray[3].y, this.vector4fArray[3].z).setUv(0.0F, 0.0F).setColor(value3, value4, value5, value2);
      }
   }

   private boolean m_9(Matrix4f matrix4f2) {
      transformPoint(matrix4f2, -0.5F, 0.5F, this.vector4fArray[0]);
      transformPoint(matrix4f2, 0.5F, 0.5F, this.vector4fArray[1]);
      transformPoint(matrix4f2, 0.5F, -0.5F, this.vector4fArray[2]);
      transformPoint(matrix4f2, -0.5F, -0.5F, this.vector4fArray[3]);
      return this.vector4fArray[0].w > 0.001F || this.vector4fArray[1].w > 0.001F || this.vector4fArray[2].w > 0.001F || this.vector4fArray[3].w > 0.001F;
   }

   private static void transformPoint(Matrix4f matrix4f2, float floatValue, float floatValue2, Vector4f vector4f) {
      vector4f.set(floatValue, floatValue2, 0.0F, 1.0F);
      matrix4f2.transform(vector4f);
   }

   private void m_7_void() {
      int value = this.c9List.size() - 370;
      if (value > 0) {
         int value2 = 0;

         for (int i = 0; i < 2 && value2 < value; i++) {
            boolean flag2 = i == 0;
            int value3 = 0;

            while (value3 < this.c9List.size() && value2 < value) {
               if (this.c9List.get(value3).flag == flag2) {
                  this.c9List.remove(value3);
                  value2++;
               } else {
                  value3++;
               }
            }
         }
      }
   }

   private int countAlive() {
      int value = 0;

      for (Particles.StateHolder c9 : this.c9List) {
         if (!c9.flag) {
            value++;
         }
      }

      return value;
   }

   private void m_2(int value, boolean flag2, boolean flag3, double doubleValue8, double doubleValue9, double doubleValue10) {
      int value2 = Math.min(this.worldCount.getInt(), 250);
      int value3 = value2 - this.countAlive();
      if (value3 > 0) {
         if (this.c9List.size() < 370) {
            int value4 = 370 - this.c9List.size();
            value3 = Math.min(value3, value4);
            int value5 = Math.min(value3, Math.max(6, (int)(value3 * 0.4 + this.doubleValue7 * 40.0)));
            value5 = Math.min(value5, 48);
            double doubleValue11;
            double doubleValue12;
            double doubleValue13;
            if (this.doubleValue7 > 0.02) {
               doubleValue11 = this.doubleValue4 / this.doubleValue7;
               doubleValue12 = this.doubleValue5 / this.doubleValue7;
               doubleValue13 = this.doubleValue6 / this.doubleValue7;
            } else {
               Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getLookAngle();
               doubleValue11 = vec3.x;
               doubleValue12 = vec3.y;
               doubleValue13 = vec3.z;
            }

            int value6 = 0;
            int value7 = 0;

            for (int i = value5 * 4; value6 < value5 && value7 < i && this.c9List.size() < 370; value7++) {
               if (this.m_2(value, flag2, flag3, doubleValue8, doubleValue9, doubleValue10, doubleValue11, doubleValue12, doubleValue13)) {
                  value6++;
               }
            }
         }
      }
   }

   private boolean m_2(int value, boolean flag2, boolean flag3, double doubleValue8, double doubleValue9, double doubleValue10, double doubleValue11, double doubleValue12, double doubleValue13) {
      Notifications.m_5_C3_sArr();
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      float floatValue = this.worldDistance.getFloat();
      int value2 = MinecraftAccess.MINECRAFT.level.getMinY() + 5;
      int value3 = 0;

      double doubleValue14;
      double doubleValue15;
      double doubleValue16;
      while (true) {
         if (value3 >= 8) {
            return false;
         }

         if (threadLocalRandom.nextFloat() < 0.7F) {
            double doubleValue17 = threadLocalRandom.nextDouble(0.5, floatValue);
            double doubleValue18 = threadLocalRandom.nextDouble(-floatValue * 0.55, floatValue * 0.55);
            double doubleValue19 = threadLocalRandom.nextDouble(-floatValue * 0.4, floatValue * 0.55);
            double doubleValue20 = doubleValue13;
            double doubleValue21 = -doubleValue11;
            double doubleValue22 = Math.sqrt(doubleValue20 * doubleValue20 + doubleValue21 * doubleValue21);
            if (doubleValue22 < 1.0E-4) {
               doubleValue20 = 1.0;
               doubleValue21 = 0.0;
            } else {
               doubleValue20 /= doubleValue22;
               doubleValue21 /= doubleValue22;
            }

            double doubleValue23 = this.doubleValue7 > 0.02 ? threadLocalRandom.nextDouble(0.0, this.doubleValue7) : 0.0;
            doubleValue14 = doubleValue8 + doubleValue11 * (doubleValue17 + doubleValue23) + doubleValue20 * doubleValue18;
            doubleValue15 = doubleValue9 + doubleValue12 * (doubleValue17 * 0.35) + doubleValue19 + 1.0;
            doubleValue16 = doubleValue10 + doubleValue13 * (doubleValue17 + doubleValue23) + doubleValue21 * doubleValue18;
         } else {
            double doubleValue24;
            double doubleValue25;
            double doubleValue26;
            do {
               doubleValue24 = threadLocalRandom.nextDouble(-floatValue, floatValue);
               doubleValue25 = threadLocalRandom.nextDouble(-floatValue * 0.6, floatValue * 0.6);
               doubleValue26 = threadLocalRandom.nextDouble(-floatValue, floatValue);
            } while (doubleValue24 * doubleValue24 + doubleValue25 * doubleValue25 + doubleValue26 * doubleValue26 > floatValue * floatValue);

            doubleValue14 = doubleValue8 + doubleValue24 + this.doubleValue4 * threadLocalRandom.nextDouble();
            doubleValue15 = doubleValue9 + doubleValue25 + 1.0;
            doubleValue16 = doubleValue10 + doubleValue26 + this.doubleValue6 * threadLocalRandom.nextDouble();
         }

         label74: {
            if (flag3) {
               double doubleValue27 = this.m_3_double(doubleValue14, doubleValue9, doubleValue16);
               if (Double.isNaN(doubleValue27)) {
                  break label74;
               }

               doubleValue15 = doubleValue27 + threadLocalRandom.nextDouble(0.0, 0.08);
            }

            if (!(doubleValue15 < value2)) {
               this.mutableBlockPos.set(doubleValue14, doubleValue15, doubleValue16);
               if (MinecraftAccess.MINECRAFT.level.hasChunkAt(this.mutableBlockPos)) {
                  if (flag3) {
                     break;
                  }

                  BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(this.mutableBlockPos);
                  if (blockState.getCollisionShape(MinecraftAccess.MINECRAFT.level, this.mutableBlockPos).isEmpty()
                     && (!MinecraftAccess.MINECRAFT.level.dimensionType().hasSkyLight() || MinecraftAccess.MINECRAFT.level.getBrightness(LightLayer.SKY, this.mutableBlockPos) > 0)) {
                     break;
                  }
               }
            }
         }

         value3++;
      }

      double doubleValue28 = threadLocalRandom.nextDouble(Math.PI * 2);
      float floatValue2;
      float floatValue3;
      float floatValue4;
      if (flag3) {
         float floatValue5 = (float)threadLocalRandom.nextDouble(0.006, 0.024);
         floatValue2 = (float)(Math.cos(doubleValue28) * floatValue5);
         floatValue3 = (float)threadLocalRandom.nextDouble(0.05, 0.09);
         floatValue4 = (float)(Math.sin(doubleValue28) * floatValue5);
      } else {
         float floatValue6 = flag2 ? (float)threadLocalRandom.nextDouble(0.04, 0.14) : (float)threadLocalRandom.nextDouble(0.08, 0.36);
         floatValue2 = (float)(Math.cos(doubleValue28) * floatValue6);
         floatValue3 = flag2 ? (float)threadLocalRandom.nextDouble(-0.08, 0.02) : (float)threadLocalRandom.nextDouble(-0.002, 0.01);
         floatValue4 = (float)(Math.sin(doubleValue28) * floatValue6);
      }

      float floatValue7 = (float)threadLocalRandom.nextDouble(0.72, 1.18);
      int value4 = Math.max(12, value + threadLocalRandom.nextInt(-12, 13));
      this.c9List.add(new Particles.StateHolder(doubleValue14, doubleValue15, doubleValue16, floatValue2, floatValue3, floatValue4, floatValue7, value4, false));
      return true;
   }

   private double m_3_double(double doubleValue8, double doubleValue9, double doubleValue10) {
      int value = Mth.floor(doubleValue8);
      int value2 = Mth.floor(doubleValue10);
      if (!MinecraftAccess.MINECRAFT.level.hasChunkAt(value, value2)) {
         return Double.NaN;
      }

      int value3 = Mth.floor(doubleValue9) + 2;
      int value4 = Math.max(MinecraftAccess.MINECRAFT.level.getMinY() + 1, Mth.floor(doubleValue9) - (int)Math.min(16.0F, this.worldDistance.getFloat()));
      boolean flag2 = false;

      for (int i = value3; i >= value4; i--) {
         this.mutableBlockPos.set(value, i, value2);
         VoxelShape voxelShape = MinecraftAccess.MINECRAFT.level.getBlockState(this.mutableBlockPos).getCollisionShape(MinecraftAccess.MINECRAFT.level, this.mutableBlockPos);
         if (voxelShape.isEmpty()) {
            flag2 = true;
         } else if (flag2) {
            return i + voxelShape.max(Axis.Y) + 0.04;
         }
      }

      return Double.NaN;
   }

   private boolean isSparkStyle() {
      return ((String)this.style.getValue()).equals("Огоньки");
   }

   private void m_4(Entity entity) {
      int value = Math.min(this.hitCount.getInt(), 120);
      if (value > 0) {
         ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
         int value2 = this.hitLifetime.getInt();
         double doubleValue8 = entity.getX();
         double doubleValue9 = entity.getY() + entity.getBbHeight() * 0.5;
         double doubleValue10 = entity.getZ();
         boolean flag2 = this.isOrbHitStyle();

         for (int i = 0; i < value; i++) {
            double doubleValue11 = threadLocalRandom.nextDouble(Math.PI * 2);
            double doubleValue12 = threadLocalRandom.nextDouble(-1.0995574287564276, 1.0995574287564276);
            double doubleValue13 = Math.cos(doubleValue12);
            double doubleValue14 = doubleValue8;
            double doubleValue15 = doubleValue9;
            double doubleValue16 = doubleValue10;
            float floatValue;
            float floatValue2;
            float floatValue3;
            if (flag2) {
               double doubleValue17 = threadLocalRandom.nextDouble(0.22, 0.5);
               floatValue = (float)(Math.cos(doubleValue11) * doubleValue13 * doubleValue17);
               floatValue2 = (float)(threadLocalRandom.nextDouble(-0.02, 0.06) + Math.sin(doubleValue12) * doubleValue17 * 0.5);
               floatValue3 = (float)(Math.sin(doubleValue11) * doubleValue13 * doubleValue17);
               double doubleValue18 = threadLocalRandom.nextDouble(0.0, 0.5);
               doubleValue14 += Math.cos(doubleValue11) * doubleValue18;
               doubleValue16 += Math.sin(doubleValue11) * doubleValue18;
               doubleValue15 += threadLocalRandom.nextDouble(-0.2, 0.2);
            } else {
               double doubleValue19 = threadLocalRandom.nextDouble(0.14, 0.38);
               floatValue = (float)(Math.cos(doubleValue11) * doubleValue13 * doubleValue19);
               floatValue2 = (float)(Math.sin(doubleValue12) * doubleValue19 + threadLocalRandom.nextDouble(0.02, 0.08));
               floatValue3 = (float)(Math.sin(doubleValue11) * doubleValue13 * doubleValue19);
               double doubleValue20 = threadLocalRandom.nextDouble(0.0, 0.25);
               doubleValue14 += Math.cos(doubleValue11) * doubleValue20;
               doubleValue15 += threadLocalRandom.nextDouble(-0.2, 0.2);
               doubleValue16 += Math.sin(doubleValue11) * doubleValue20;
            }

            float floatValue4 = (float)threadLocalRandom.nextDouble(0.72, 1.3);
            int value3 = Math.max(12, value2 + threadLocalRandom.nextInt(-12, 13));
            this.c9List.add(new Particles.StateHolder(doubleValue14, doubleValue15, doubleValue16, floatValue, floatValue2, floatValue3, floatValue4, value3, true, flag2));
         }
      }
   }

   private GpuTextureView m_8_GpuTextureView() {
      Identifier identifier = switch ((String)this.texture.getValue()) {
         case "Искра" -> IDENTIFIER2;
         case "Звезда" -> IDENTIFIER3;
         case "Снег" -> IDENTIFIER4;
         default -> IDENTIFIER;
      };
      return this.getTextureView(identifier);
   }

   private GpuTextureView getTextureView(Identifier identifier) {
      AbstractTexture abstractTexture = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(identifier);
      if (abstractTexture == null) {
         return null;
      }

      GpuTextureView gpuTextureView = abstractTexture.getTextureView();
      return gpuTextureView != null && !gpuTextureView.isClosed() ? gpuTextureView : null;
   }

   private void clear() {
      this.c9List.clear();
      this.flag = false;
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

   private final class StateHolder {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public final float floatValue4;
      public final int value;
      public final boolean flag;
      public final boolean flag2;
      public int value2;

      public StateHolder(double doubleValue8, double doubleValue9, double doubleValue10, float floatValue5, float floatValue6, float floatValue7, float floatValue8, int value3, boolean flag3) {
         this(doubleValue8, doubleValue9, doubleValue10, floatValue5, floatValue6, floatValue7, floatValue8, value3, flag3, false);
      }

      public StateHolder(double doubleValue8, double doubleValue9, double doubleValue10, float floatValue5, float floatValue6, float floatValue7, float floatValue8, int value3, boolean flag3, boolean flag4) {
         this.doubleValue = doubleValue8;
         this.doubleValue2 = doubleValue9;
         this.doubleValue3 = doubleValue10;
         this.doubleValue4 = doubleValue8;
         this.doubleValue5 = doubleValue9;
         this.doubleValue6 = doubleValue10;
         this.floatValue = floatValue5;
         this.floatValue2 = floatValue6;
         this.floatValue3 = floatValue7;
         this.floatValue4 = floatValue8;
         this.value = value3;
         this.flag = flag3;
         this.flag2 = flag4;
      }

      public boolean m_2(boolean flag3, boolean flag4, double doubleValue8, double doubleValue9, double doubleValue10, float floatValue5) {
         Notifications.m_5_C3_sArr();
         this.value2++;
         this.doubleValue = this.doubleValue4;
         this.doubleValue2 = this.doubleValue5;
         this.doubleValue3 = this.doubleValue6;
         this.doubleValue4 = this.doubleValue4 + this.floatValue;
         this.doubleValue5 = this.doubleValue5 + this.floatValue2;
         this.doubleValue6 = this.doubleValue6 + this.floatValue3;
         if (this.flag2) {
            this.floatValue2 -= 0.05F;
            this.floatValue *= 0.998F;
            this.floatValue3 *= 0.998F;
            Particles.this.mutableBlockPos.set(this.doubleValue4, this.doubleValue5 - 0.05, this.doubleValue6);
            if (!MinecraftAccess.MINECRAFT.level.getBlockState(Particles.this.mutableBlockPos).getCollisionShape(MinecraftAccess.MINECRAFT.level, Particles.this.mutableBlockPos).isEmpty()) {
               this.floatValue2 = -this.floatValue2 * 0.78F;
               if (Math.abs(this.floatValue2) < 0.03F) {
                  this.floatValue2 = 0.0F;
               }

               this.doubleValue5 = this.doubleValue5 + this.floatValue2;
            }

            if (this.floatValue != 0.0F) {
               Particles.this.mutableBlockPos.set(this.doubleValue4 + (this.floatValue > 0.0F ? 0.1 : -0.1), this.doubleValue5, this.doubleValue6);
               if (!MinecraftAccess.MINECRAFT.level.getBlockState(Particles.this.mutableBlockPos).getCollisionShape(MinecraftAccess.MINECRAFT.level, Particles.this.mutableBlockPos).isEmpty()) {
                  this.floatValue = -this.floatValue * 0.7F;
               }
            }

            if (this.floatValue3 != 0.0F) {
               Particles.this.mutableBlockPos.set(this.doubleValue4, this.doubleValue5, this.doubleValue6 + (this.floatValue3 > 0.0F ? 0.1 : -0.1));
               if (!MinecraftAccess.MINECRAFT.level.getBlockState(Particles.this.mutableBlockPos).getCollisionShape(MinecraftAccess.MINECRAFT.level, Particles.this.mutableBlockPos).isEmpty()) {
                  this.floatValue3 = -this.floatValue3 * 0.7F;
               }
            }
         } else if (flag4) {
            float floatValue6 = this.value2 * 0.14F + this.floatValue4 * 9.3F;
            this.floatValue = this.floatValue + (float)Math.sin(floatValue6) * 0.0035F;
            this.floatValue3 = this.floatValue3 + (float)Math.cos(floatValue6 * 0.87F) * 0.0035F;
            this.floatValue2 += 0.0015F;
            if (this.floatValue2 > 0.11F) {
               this.floatValue2 = 0.11F;
            }

            if (this.floatValue2 < 0.035F) {
               this.floatValue2 = 0.035F;
            }

            this.floatValue *= 0.96F;
            this.floatValue3 *= 0.96F;
         } else if (flag3) {
            this.floatValue2 -= 0.018F;
            Particles.this.mutableBlockPos.set(this.doubleValue4, this.doubleValue5, this.doubleValue6);
            if (!MinecraftAccess.MINECRAFT.level.getBlockState(Particles.this.mutableBlockPos).isAir()) {
               this.floatValue *= -0.7F;
               this.floatValue3 *= -0.7F;
               this.floatValue2 *= -0.5F;
            }

            this.floatValue *= 0.92F;
            this.floatValue2 *= 0.92F;
            this.floatValue3 *= 0.92F;
         } else {
            this.floatValue *= 0.9F;
            this.floatValue2 *= 0.9F;
            this.floatValue3 *= 0.9F;
            this.floatValue2 -= 0.001F;
         }

         if (this.value2 >= this.value) {
            return true;
         }

         if (this.doubleValue5 < MinecraftAccess.MINECRAFT.level.getMinY() + 4) {
            return true;
         }

         if (this.flag) {
            return false;
         }

         double doubleValue11 = this.doubleValue4 - doubleValue8;
         double doubleValue12 = this.doubleValue5 - doubleValue9;
         double doubleValue13 = this.doubleValue6 - doubleValue10;
         return doubleValue11 * doubleValue11 + doubleValue12 * doubleValue12 + doubleValue13 * doubleValue13 > floatValue5;
      }
   }
}
