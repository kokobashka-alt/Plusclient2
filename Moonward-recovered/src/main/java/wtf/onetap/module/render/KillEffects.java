package wtf.onetap.module.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.AttackEntityEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.C6h;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.render.Texture3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "KillEffects", description = "Эффект на месте убитого: созвездие, чертёж или геометрия", category = Category.RENDER)
public class KillEffects extends Module {
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "images/glow.png");
   private static final float[][] FLOAT_VALUE_ARRAY = new float[][]{
      {
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(1058642330)
      },
      {
            1.0F,
            0.0F,
            0.0F,
            Float.intBitsToFloat(-1082130432),
            0.0F,
            0.0F,
            0.0F,
            1.0F,
            0.0F,
            0.0F,
            Float.intBitsToFloat(-1082130432),
            0.0F,
            0.0F,
            0.0F,
            1.0F,
            0.0F,
            0.0F,
            Float.intBitsToFloat(-1082130432)
      },
      {
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(-1088841318),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(1058642330),
            Float.intBitsToFloat(1058642330)
      }
   };
   private static final int[][] VALUE_ARRAY = new int[][]{
      {0, 1, 0, 2, 0, 3, 1, 2, 1, 3, 2, 3},
      {0, 2, 0, 3, 0, 4, 0, 5, 1, 2, 1, 3, 1, 4, 1, 5, 2, 4, 2, 5, 3, 4, 3, 5},
      {0, 1, 2, 3, 4, 5, 6, 7, 0, 2, 1, 3, 4, 6, 5, 7, 0, 4, 1, 5, 2, 6, 3, 7}
   };
   private final ModeSetting effect = new ModeSetting("Эффект", "Созвездие", "Созвездие", "Чертёж", "Геометрия").build();
   private final FloatSetting duration = new FloatSetting("Длительность", 2.0F, 1.0F, 4.0F, 0.1F).build();
   private final FloatSetting size = new FloatSetting("Размер", 1.0F, 0.5F, 2.0F, 0.05F).build();
   private final BooleanSetting onlyOwn = new BooleanSetting("Только свои", true).build();
   private final BooleanSetting onMobs = new BooleanSetting("На мобов", true).build();
   private final BooleanSetting themeColor = new BooleanSetting("Цвет темы", true).build();
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(140, 180, 255, 255)).visibleWhen(() -> !(Boolean)this.themeColor.getValue()).build();
   private final Map<Integer, Long> integerMap = new HashMap<>();
   private final Map<Integer, Long> integerMap2 = new HashMap<>();
   private final ArrayList<KillEffects.StateHolder2> c1List = new ArrayList<>();
   private final Matrix4f matrix4f = new Matrix4f();
   private final Vector4f[] vector4fArray = new Vector4f[]{new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f()};
   private Object object;
   private static String[] textArray;

   public boolean hasEffect(int value) {
      return this.isEnabled() && this.integerMap2.containsKey(value);
   }

   @EventTarget
   private void onAttackEntity(AttackEntityEvent event) {
      this.integerMap.put(event.getEntity().getId(), System.currentTimeMillis());
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundEntityEventPacket clientboundEntityEventPacket) {
         if (clientboundEntityEventPacket.getEventId() == 3) {
            if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
               if (clientboundEntityEventPacket.getEntity(MinecraftAccess.MINECRAFT.level) instanceof LivingEntity livingEntity && livingEntity != MinecraftAccess.MINECRAFT.player) {
                  if (livingEntity instanceof Player || (Boolean)this.onMobs.getValue()) {
                     long longValue = System.currentTimeMillis();
                     Long longObj = this.integerMap.remove(livingEntity.getId());
                     if (!(Boolean)this.onlyOwn.getValue() || longObj != null && longValue - longObj <= 10000L) {
                        int value = this.themeColor.getValue() ? ColorUtil.primaryColor() : (Integer)this.color.getValue();

                        KillEffects.StateHolder2 c1 = switch ((String)this.effect.getValue()) {
                           case "Чертёж" -> new KillEffects.StateHolder(livingEntity, value);
                           case "Геометрия" -> new KillEffects.StateHolder4(livingEntity, value);
                           default -> new KillEffects.StateHolder3(livingEntity, value);
                        };
                        if (c1.c6 != null) {
                           this.integerMap2.put(livingEntity.getId(), longValue + (long)(this.duration.getFloat() * 1000.0F));
                        }

                        this.c1List.add(c1);
                        if (this.c1List.size() > 4) {
                           this.c1List.removeFirst();
                        }
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

         long longValue = System.currentTimeMillis();
         this.integerMap.entrySet().removeIf(entry -> longValue - entry.getValue() > 10000L);
         this.integerMap2.values().removeIf(longObj -> longValue >= longObj);
      } else {
         this.clear();
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (!this.c1List.isEmpty() && MinecraftAccess.MINECRAFT.level != null) {
         long longValue = System.nanoTime();

         for (KillEffects.StateHolder2 c1 : this.c1List) {
            c1.floatValue5 = (float)(longValue - c1.longValue) / c1.floatValue4;
         }

         this.c1List.removeIf(c12 -> c12.floatValue5 >= 1.0F);
         if (!this.c1List.isEmpty()) {
            Matrix4f matrix4f2 = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
            int value = 0;

            for (KillEffects.StateHolder2 c13 : this.c1List) {
               c13.m_6(event, matrix4f2);
               value += c13.m_7_int();
            }

            AbstractTexture abstractTexture = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(IDENTIFIER);
            if (abstractTexture != null) {
               GpuTextureView gpuTextureView = abstractTexture.getTextureView();
               if (gpuTextureView != null && !gpuTextureView.isClosed()) {
                  Texture3DRenderer.m_8(event.getMatrix4f(), gpuTextureView, value, true, true, bufferBuilder -> {
                     for (KillEffects.StateHolder2 c14 : this.c1List) {
                        c14.m_2(bufferBuilder, event);
                     }
                  });
               }
            }
         }
      }
   }

   private void m_9(BufferBuilder bufferBuilder, Render3DEvent event, double doubleValue, double doubleValue2, double doubleValue3, float floatValue, int value) {
      Notifications.m_5_C3_sArr();
      int value2 = value >>> 24 & 0xFF;
      if (!(floatValue <= 0.001F) && value2 > 3) {
         Vec3 vec3 = event.getVec3();
         this.matrix4f.set(event.getMatrix4f2());
         this.matrix4f.translate((float)(doubleValue - vec3.x), (float)(doubleValue2 - vec3.y), (float)(doubleValue3 - vec3.z));
         this.matrix4f.rotate(event.getQuaternionf());
         this.matrix4f.scale(floatValue, floatValue, 1.0F);
         transformPoint(this.matrix4f, -0.5F, 0.5F, this.vector4fArray[0]);
         transformPoint(this.matrix4f, 0.5F, 0.5F, this.vector4fArray[1]);
         transformPoint(this.matrix4f, 0.5F, -0.5F, this.vector4fArray[2]);
         transformPoint(this.matrix4f, -0.5F, -0.5F, this.vector4fArray[3]);
         if (!(this.vector4fArray[0].w <= 0.001F) || !(this.vector4fArray[1].w <= 0.001F) || !(this.vector4fArray[2].w <= 0.001F) || !(this.vector4fArray[3].w <= 0.001F)) {
            int value3 = value >> 16 & 0xFF;
            int value4 = value >> 8 & 0xFF;
            int value5 = value & 0xFF;
            bufferBuilder.addVertex(this.vector4fArray[0].x, this.vector4fArray[0].y, this.vector4fArray[0].z).setUv(0.0F, 1.0F).setColor(value3, value4, value5, value2);
            bufferBuilder.addVertex(this.vector4fArray[1].x, this.vector4fArray[1].y, this.vector4fArray[1].z).setUv(1.0F, 1.0F).setColor(value3, value4, value5, value2);
            bufferBuilder.addVertex(this.vector4fArray[2].x, this.vector4fArray[2].y, this.vector4fArray[2].z).setUv(1.0F, 0.0F).setColor(value3, value4, value5, value2);
            bufferBuilder.addVertex(this.vector4fArray[3].x, this.vector4fArray[3].y, this.vector4fArray[3].z).setUv(0.0F, 0.0F).setColor(value3, value4, value5, value2);
         }
      }
   }

   private static void m_4(Matrix4f matrix4f2, Render3DEvent event, List<Vec3> vec3List, int value, float floatValue, float floatValue2) {
      if (vec3List.size() >= 2) {
         Vec3 vec3 = event.getVec3();
         Line3DRenderer.drawLines(matrix4f2, vec3List, vec3, floatValue2 * 4.5F, fadeColor(value, 0.12F * floatValue));
         Line3DRenderer.drawLines(matrix4f2, vec3List, vec3, floatValue2 * 2.2F, fadeColor(value, 0.3F * floatValue));
         Line3DRenderer.drawLines(matrix4f2, vec3List, vec3, floatValue2, fadeColor(ColorUtil.lerpColor(value, -1, 0.6F), floatValue));
      }
   }

   private static void transformPoint(Matrix4f matrix4f2, float floatValue, float floatValue2, Vector4f vector4f) {
      vector4f.set(floatValue, floatValue2, 0.0F, 1.0F);
      matrix4f2.transform(vector4f);
   }

   private static float clamp01(float floatValue) {
      return Math.max(0.0F, Math.min(1.0F, floatValue));
   }

   private static float smoothStep(float floatValue) {
      floatValue = clamp01(floatValue);
      return floatValue * floatValue * (3.0F - 2.0F * floatValue);
   }

   private static float easeOutCubic(float floatValue) {
      float floatValue2 = 1.0F - clamp01(floatValue);
      return 1.0F - floatValue2 * floatValue2 * floatValue2;
   }

   private static int fadeColor(int value, float floatValue) {
      return ColorUtil.withAlpha(value, (int)(255.0F * clamp01(floatValue)));
   }

   private static float easeOutExp(float floatValue) {
      return (1.0F - (float)Math.exp(-4.0F * clamp01(floatValue))) / (1.0F - (float)Math.exp(-4.0));
   }

   private static Vec3 lerp(Vec3 vec3, Vec3 vec32, float floatValue) {
      return vec3.add((vec32.x - vec3.x) * floatValue, (vec32.y - vec3.y) * floatValue, (vec32.z - vec3.z) * floatValue);
   }

   private static Vector3f m_9(ThreadLocalRandom threadLocalRandom) {
      Vector3f vector3f = new Vector3f(threadLocalRandom.nextFloat(-1.0F, 1.0F), threadLocalRandom.nextFloat(-1.0F, 1.0F), threadLocalRandom.nextFloat(-1.0F, 1.0F));
      if (vector3f.lengthSquared() < 1.0E-4F) {
         vector3f.set(0.0F, 1.0F, 0.0F);
      }

      return vector3f.normalize();
   }

   private void clear() {
      this.integerMap.clear();
      this.integerMap2.clear();
      this.c1List.clear();
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

   static {
      m_1(null);
   }

   public static void m_1(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_3_StringArr() {
      return textArray;
   }

   private final class StateHolder extends KillEffects.StateHolder2 {
      public final Quaternionf quaternionf = new Quaternionf();
      public final Vector3f vector3f = new Vector3f();
      public final Vec3[] vec3Array = new Vec3[4];
      public float[] floatValueArray;
      public float[] floatValueArray2;
      public float[] floatValueArray3;
      public float[] floatValueArray4;

      public StateHolder(LivingEntity livingEntity, int value2) {
         super(livingEntity, value2, true);
      }

      public void m_0(Render3DEvent event) {
         KillEffects.StateHolder5 c9 = new KillEffects.StateHolder5();
         C6h.m_5(super.c6, event.getCameraRenderState(), 0.0, 0.0, 0.0, c9, -1, 255);
         int value2 = c9.floatArrayList.size() / 12;
         this.floatValueArray = Arrays.copyOf(c9.floatArrayList.elements(), value2 * 12);
         this.floatValueArray2 = new float[value2 * 3];
         this.floatValueArray3 = new float[value2 * 4];
         this.floatValueArray4 = new float[value2];
         Notifications.m_5_C3_sArr();
         float floatValue6 = Float.MAX_VALUE;
         float floatValue7 = -Float.MAX_VALUE;

         for (byte byteValue = 1; byteValue < value2 * 12; byteValue += 3) {
            floatValue6 = Math.min(floatValue6, this.floatValueArray[byteValue]);
            floatValue7 = Math.max(floatValue7, this.floatValueArray[byteValue]);
         }

         float floatValue8 = (floatValue6 + floatValue7) * 0.5F;
         ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
         Vector3f vector3f2 = new Vector3f();
         Vector3f vector3f3 = new Vector3f();

         for (int i = 0; i < value2; i++) {
            int value3 = i * 12;
            Vector3f vector3f4 = new Vector3f(
                  this.floatValueArray[value3 + 3] - this.floatValueArray[value3], this.floatValueArray[value3 + 4] - this.floatValueArray[value3 + 1], this.floatValueArray[value3 + 5] - this.floatValueArray[value3 + 2]
               )
               .cross(
                  vector3f2.set(this.floatValueArray[value3 + 9] - this.floatValueArray[value3], this.floatValueArray[value3 + 10] - this.floatValueArray[value3 + 1], this.floatValueArray[value3 + 11] - this.floatValueArray[value3 + 2])
               );
            if (vector3f4.lengthSquared() > 1.0E-8F) {
               vector3f4.normalize();
            }

            vector3f3.set(this.m_5_float(i), this.m_9_float(i) - floatValue8, this.m_1_float(i));
            if (vector3f3.lengthSquared() > 1.0E-6F) {
               vector3f3.normalize();
            }

            if (vector3f4.dot(vector3f3) < 0.0F) {
               vector3f4.negate();
            }

            Vector3f vector3f5 = vector3f4.mul(0.6F).add(vector3f3.mul(0.8F)).add(0.0F, 0.2F, 0.0F);
            if (vector3f5.lengthSquared() < 1.0E-6F) {
               vector3f5.set(0.0F, 1.0F, 0.0F);
            }

            vector3f5.normalize().mul(threadLocalRandom.nextFloat(0.6F, 1.1F));
            this.floatValueArray2[i * 3] = vector3f5.x;
            this.floatValueArray2[i * 3 + 1] = vector3f5.y;
            this.floatValueArray2[i * 3 + 2] = vector3f5.z;
            Vector3f vector3f6 = KillEffects.m_9(threadLocalRandom);
            this.floatValueArray3[i * 4] = vector3f6.x;
            this.floatValueArray3[i * 4 + 1] = vector3f6.y;
            this.floatValueArray3[i * 4 + 2] = vector3f6.z;
            this.floatValueArray3[i * 4 + 3] = threadLocalRandom.nextFloat(-1.2F, 1.2F);
            this.floatValueArray4[i] = threadLocalRandom.nextFloat() * 0.05F;
         }
      }

      public float m_5_float(int value2) {
         int value3 = value2 * 12;
         return (this.floatValueArray[value3] + this.floatValueArray[value3 + 3] + this.floatValueArray[value3 + 6] + this.floatValueArray[value3 + 9]) * 0.25F;
      }

      public float m_9_float(int value2) {
         int value3 = value2 * 12;
         return (this.floatValueArray[value3 + 1] + this.floatValueArray[value3 + 4] + this.floatValueArray[value3 + 7] + this.floatValueArray[value3 + 10]) * 0.25F;
      }

      public float m_1_float(int value2) {
         int value3 = value2 * 12;
         return (this.floatValueArray[value3 + 2] + this.floatValueArray[value3 + 5] + this.floatValueArray[value3 + 8] + this.floatValueArray[value3 + 11]) * 0.25F;
      }

      public float m_8_float(int value2) {
         return KillEffects.easeOutCubic((super.floatValue5 - this.floatValueArray4[value2]) / 0.6F) * 1.4F * super.floatValue3;
      }

      public float m_4_float() {
         return KillEffects.clamp01(super.floatValue5 / 0.03F) * (1.0F - KillEffects.smoothStep((super.floatValue5 - 0.55F) / 0.45F));
      }

      public int m_8_int() {
         return ColorUtil.lerpColor(-1, super.value, KillEffects.smoothStep(super.floatValue5 / 0.4F));
      }

      public void m_9_void(int value2) {
         float floatValue6 = this.m_5_float(value2);
         float floatValue7 = this.m_9_float(value2);
         float floatValue8 = this.m_1_float(value2);
         float floatValue9 = this.m_8_float(value2);
         float floatValue10 = floatValue9 / (1.4F * super.floatValue3);
         if (floatValue10 > 0.0F) {
            this.quaternionf.fromAxisAngleRad(this.floatValueArray3[value2 * 4], this.floatValueArray3[value2 * 4 + 1], this.floatValueArray3[value2 * 4 + 2], this.floatValueArray3[value2 * 4 + 3] * floatValue10);
         }

         for (int i = 0; i < 4; i++) {
            int value3 = value2 * 12 + i * 3;
            this.vector3f.set(this.floatValueArray[value3] - floatValue6, this.floatValueArray[value3 + 1] - floatValue7, this.floatValueArray[value3 + 2] - floatValue8);
            if (floatValue10 > 0.0F) {
               this.quaternionf.transform(this.vector3f);
            }

            this.vec3Array[i] = new Vec3(
               super.doubleValue + floatValue6 + this.vector3f.x + this.floatValueArray2[value2 * 3] * floatValue9,
               super.doubleValue2 + floatValue7 + this.vector3f.y + this.floatValueArray2[value2 * 3 + 1] * floatValue9,
               super.doubleValue3 + floatValue8 + this.vector3f.z + this.floatValueArray2[value2 * 3 + 2] * floatValue9
            );
         }
      }

      public @Override
      int m_7_int() {
         return this.floatValueArray4 == null ? 0 : this.floatValueArray4.length;
      }

      public @Override
      void m_2(BufferBuilder bufferBuilder, Render3DEvent event) {
         if (this.floatValueArray != null) {
            int value2 = KillEffects.fadeColor(this.m_8_int(), 0.2F * this.m_4_float());

            for (int i = 0; i < this.floatValueArray4.length; i++) {
               float floatValue6 = this.m_8_float(i);
               KillEffects.this.m_9(
                  bufferBuilder,
                  event,
                  super.doubleValue + this.m_5_float(i) + this.floatValueArray2[i * 3] * floatValue6,
                  super.doubleValue2 + this.m_9_float(i) + this.floatValueArray2[i * 3 + 1] * floatValue6,
                  super.doubleValue3 + this.m_1_float(i) + this.floatValueArray2[i * 3 + 2] * floatValue6,
                  0.12F * super.floatValue3,
                  value2
               );
            }
         }
      }

      public @Override
      void m_6(Render3DEvent event, Matrix4f matrix4f2) {
         if (super.c6 != null) {
            if (this.floatValueArray == null) {
               this.m_0(event);
            }

            float floatValue6 = this.m_4_float();
            if (!(floatValue6 <= 0.01F)) {
               ArrayList list = new ArrayList(this.floatValueArray4.length * 8);

               for (int i = 0; i < this.floatValueArray4.length; i++) {
                  this.m_9_void(i);

                  for (int i2 = 0; i2 < 4; i2++) {
                     list.add(this.vec3Array[i2]);
                     list.add(this.vec3Array[i2 + 1 & 3]);
                  }
               }

               KillEffects.m_4(matrix4f2, event, list, this.m_8_int(), floatValue6, 1.3F);
            }
         }
      }
   }

   private abstract class StateHolder2 {
      public final double doubleValue;
      public final double doubleValue2;
      public final double doubleValue3;
      public final float floatValue;
      public final float floatValue2;
      public final float floatValue3;
      public final int value;
      public final C6h.DataRecord c6;
      public final long longValue;
      public final float floatValue4;
      public float floatValue5;

      public StateHolder2(LivingEntity livingEntity, int value2, boolean flag) {
         Notifications.m_5_C3_sArr();
         this.longValue = System.nanoTime();
         this.floatValue4 = KillEffects.this.duration.getFloat() * 1.0E9F;
         this.doubleValue = livingEntity.getX();
         this.doubleValue2 = livingEntity.getY();
         this.doubleValue3 = livingEntity.getZ();
         this.floatValue = livingEntity.getBbWidth();
         this.floatValue2 = livingEntity.getBbHeight();
         this.floatValue3 = KillEffects.this.size.getFloat();
         this.value = value2;
         this.c6 = flag ? C6h.m_2_C6(livingEntity, MinecraftAccess.MINECRAFT.getDeltaTracker().getGameTimeDeltaPartialTick(false)) : null;
      }

      public abstract int m_7_int();

      public float m_3_float() {
         return KillEffects.clamp01(this.floatValue5 / 0.03F) * (1.0F - KillEffects.smoothStep((this.floatValue5 - 0.5F) / 0.5F));
      }

      public int m_1_int() {
         return ColorUtil.lerpColor(-1, this.value, KillEffects.smoothStep(this.floatValue5 / 0.3F));
      }

      public abstract void m_2(BufferBuilder bufferBuilder, Render3DEvent event);

      public void m_6(Render3DEvent event, Matrix4f matrix4f2) {
      }
   }

   private final class StateHolder3 extends KillEffects.StateHolder2 {
      public final float[] floatValueArray = new float[14];
      public final float[] floatValueArray2 = new float[14];
      public final float[] floatValueArray3;
      public final float[] floatValueArray4;
      public final float[] floatValueArray5;
      public final float[] floatValueArray6;
      public final float[] floatValueArray7;
      public final float[] floatValueArray8;
      public final float[] floatValueArray9;
      public final int[] valueArray;

      public StateHolder3(LivingEntity livingEntity, int value2) {
         super(livingEntity, value2, false);
         Notifications.m_5_C3_sArr();
         this.floatValueArray3 = new float[14];
         this.floatValueArray4 = new float[14];
         this.floatValueArray5 = new float[14];
         this.floatValueArray6 = new float[14];
         this.floatValueArray7 = new float[14];
         this.floatValueArray8 = new float[14];
         this.floatValueArray9 = new float[14];
         ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
         float floatValue6 = super.floatValue * 0.7F * super.floatValue3;

         for (int i = 0; i < 14; i++) {
            this.floatValueArray2[i] = threadLocalRandom.nextFloat() * super.floatValue2;
            float floatValue7 = 1.0F - 0.45F * Math.abs(this.floatValueArray2[i] / super.floatValue2 - 0.55F);
            this.floatValueArray[i] = threadLocalRandom.nextFloat(-1.0F, 1.0F) * floatValue6 * floatValue7;
            this.floatValueArray3[i] = threadLocalRandom.nextFloat(-1.0F, 1.0F) * floatValue6 * floatValue7;
            this.floatValueArray4[i] = 0.07F * this.floatValueArray2[i] / super.floatValue2 + threadLocalRandom.nextFloat() * 0.02F;
            this.floatValueArray5[i] = threadLocalRandom.nextFloat() * (float) (Math.PI * 2);
            this.floatValueArray6[i] = threadLocalRandom.nextFloat(0.55F, 0.8F);
            this.floatValueArray7[i] = threadLocalRandom.nextFloat(0.15F, 0.45F) * super.floatValue3;
            this.floatValueArray8[i] = threadLocalRandom.nextFloat(0.04F, 0.1F) * super.floatValue3;
            this.floatValueArray9[i] = threadLocalRandom.nextFloat() * (float) (Math.PI * 2);
         }

         boolean[][] arrayArray = new boolean[14][14];
         int[] intArray = new int[56];
         int value3 = 0;

         for (int i2 = 0; i2 < 14; i2++) {
            int value4 = -1;
            int value5 = -1;
            float floatValue8 = Float.MAX_VALUE;
            float floatValue9 = Float.MAX_VALUE;

            for (int i3 = 0; i3 < 14; i3++) {
               if (i3 != i2) {
                  float floatValue10 = this.floatValueArray[i2] - this.floatValueArray[i3];
                  float floatValue11 = this.floatValueArray2[i2] - this.floatValueArray2[i3];
                  float floatValue12 = this.floatValueArray3[i2] - this.floatValueArray3[i3];
                  float floatValue13 = floatValue10 * floatValue10 + floatValue11 * floatValue11 + floatValue12 * floatValue12;
                  if (floatValue13 < floatValue8) {
                     value5 = value4;
                     floatValue9 = floatValue8;
                     value4 = i3;
                     floatValue8 = floatValue13;
                  } else if (floatValue13 < floatValue9) {
                     value5 = i3;
                     floatValue9 = floatValue13;
                  }
               }
            }

            for (int value6 : new int[]{value4, value5}) {
               if (value6 >= 0 && !arrayArray[i2][value6]) {
                  arrayArray[i2][value6] = arrayArray[value6][i2] = true;
                  intArray[value3++] = i2;
                  intArray[value3++] = value6;
               }
            }
         }

         this.valueArray = Arrays.copyOf(intArray, value3);
      }

      public Vec3 m_4_Vec3(int value2) {
         float floatValue6 = 0.5F * super.floatValue5;
         float floatValue7 = (float)Math.cos(floatValue6);
         float floatValue8 = (float)Math.sin(floatValue6);
         float floatValue9 = this.floatValueArray9[value2] + super.floatValue5 * 6.0F;
         float floatValue10 = this.floatValueArray[value2] * floatValue7 - this.floatValueArray3[value2] * floatValue8 + (float)Math.sin(floatValue9) * this.floatValueArray8[value2];
         float floatValue11 = this.floatValueArray[value2] * floatValue8 + this.floatValueArray3[value2] * floatValue7 + (float)Math.cos(floatValue9 * 1.3F) * this.floatValueArray8[value2];
         float floatValue12 = 1.0F - (1.0F - super.floatValue5) * (1.0F - super.floatValue5);
         float floatValue13 = this.floatValueArray2[value2] + this.floatValueArray7[value2] * floatValue12 + (float)Math.sin(this.floatValueArray9[value2] * 2.0F + super.floatValue5 * 9.0F) * 0.03F * super.floatValue3;
         return new Vec3(super.doubleValue + floatValue10, super.doubleValue2 + floatValue13, super.doubleValue3 + floatValue11);
      }

      public float m_7_float(int value2) {
         return KillEffects.clamp01((super.floatValue5 - this.floatValueArray4[value2]) / 0.04F) * (1.0F - KillEffects.smoothStep((super.floatValue5 - this.floatValueArray6[value2]) / 0.2F));
      }

      public @Override
      int m_7_int() {
         return 28;
      }

      public @Override
      void m_2(BufferBuilder bufferBuilder, Render3DEvent event) {
         int value2 = ColorUtil.lerpColor(super.value, -1, 0.65F);

         for (int i = 0; i < 14; i++) {
            float floatValue6 = this.m_7_float(i);
            if (!(floatValue6 <= 0.0F)) {
               Vec3 vec3 = this.m_4_Vec3(i);
               float floatValue7 = 0.75F + 0.25F * (float)Math.sin(this.floatValueArray5[i] + super.floatValue5 * 30.0F);
               KillEffects.this.m_9(bufferBuilder, event, vec3.x, vec3.y, vec3.z, 0.42F * super.floatValue3 * floatValue6, KillEffects.fadeColor(super.value, 0.25F * floatValue6));
               KillEffects.this.m_9(bufferBuilder, event, vec3.x, vec3.y, vec3.z, 0.13F * super.floatValue3 * floatValue7, KillEffects.fadeColor(value2, floatValue6));
            }
         }
      }

      public @Override
      void m_6(Render3DEvent event, Matrix4f matrix4f2) {
         ArrayList list = new ArrayList(this.valueArray.length);
         int[] intArray = new int[this.valueArray.length / 2];
         int value2 = 0;

         for (byte byteValue = 0; byteValue < this.valueArray.length; byteValue += 2) {
            int value3 = this.valueArray[byteValue];
            int value4 = this.valueArray[byteValue + 1];
            float floatValue6 = KillEffects.clamp01((super.floatValue5 - Math.max(this.floatValueArray4[value3], this.floatValueArray4[value4])) / 0.06F);
            float floatValue7 = 0.65F * floatValue6 * Math.min(this.m_7_float(value3), this.m_7_float(value4));
            if (!(floatValue7 <= 0.01F)) {
               Vec3 vec3 = this.m_4_Vec3(value3);
               list.add(vec3);
               list.add(KillEffects.lerp(vec3, this.m_4_Vec3(value4), floatValue6));
               intArray[value2++] = KillEffects.fadeColor(super.value, floatValue7);
            }
         }

         if (value2 > 0) {
            Line3DRenderer.m_7(matrix4f2, list, Arrays.copyOf(intArray, value2), event.getVec3(), 1.2F);
         }
      }
   }

   private final class StateHolder4 extends KillEffects.StateHolder2 {
      public final int[] valueArray = new int[30];
      public final float[] floatValueArray = new float[90];
      public final float[] floatValueArray2 = new float[90];
      public final float[] floatValueArray3 = new float[120];
      public final float[] floatValueArray4 = new float[30];
      public final Quaternionf quaternionf = new Quaternionf();
      public final Vector3f vector3f = new Vector3f();
      public final Vec3[] vec3Array = new Vec3[8];

      public StateHolder4(LivingEntity livingEntity, int value2) {
         super(livingEntity, value2, true);
         ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
         float floatValue6 = super.floatValue * 0.45F;

         for (int i = 0; i < 30; i++) {
            this.valueArray[i] = threadLocalRandom.nextInt(KillEffects.FLOAT_VALUE_ARRAY.length);
            float floatValue7 = threadLocalRandom.nextFloat() * (float) (Math.PI * 2);
            float floatValue8 = threadLocalRandom.nextFloat() * floatValue6;
            this.floatValueArray[i * 3] = (float)Math.cos(floatValue7) * floatValue8;
            this.floatValueArray[i * 3 + 1] = threadLocalRandom.nextFloat(0.1F, 0.95F) * super.floatValue2;
            this.floatValueArray[i * 3 + 2] = (float)Math.sin(floatValue7) * floatValue8;
            Vector3f vector3f2 = new Vector3f((float)Math.cos(floatValue7), threadLocalRandom.nextFloat(-0.35F, 0.9F), (float)Math.sin(floatValue7))
               .normalize()
               .mul(threadLocalRandom.nextFloat(1.0F, 2.4F) * super.floatValue3);
            this.floatValueArray2[i * 3] = vector3f2.x;
            this.floatValueArray2[i * 3 + 1] = vector3f2.y;
            this.floatValueArray2[i * 3 + 2] = vector3f2.z;
            Vector3f vector3f3 = KillEffects.m_9(threadLocalRandom);
            this.floatValueArray3[i * 4] = vector3f3.x;
            this.floatValueArray3[i * 4 + 1] = vector3f3.y;
            this.floatValueArray3[i * 4 + 2] = vector3f3.z;
            this.floatValueArray3[i * 4 + 3] = threadLocalRandom.nextFloat(3.0F, 9.0F);
            this.floatValueArray4[i] = threadLocalRandom.nextFloat(0.06F, 0.14F) * super.floatValue3;
         }
      }

      public Vec3 m_7(int value2, float floatValue6) {
         float floatValue7 = KillEffects.easeOutExp(floatValue6);
         return new Vec3(
            super.doubleValue + this.floatValueArray[value2 * 3] + this.floatValueArray2[value2 * 3] * floatValue7,
            super.doubleValue2 + this.floatValueArray[value2 * 3 + 1] + this.floatValueArray2[value2 * 3 + 1] * floatValue7 + 0.3F * floatValue6 * floatValue6 * super.floatValue3,
            super.doubleValue3 + this.floatValueArray[value2 * 3 + 2] + this.floatValueArray2[value2 * 3 + 2] * floatValue7
         );
      }

      public @Override
      int m_7_int() {
         return 31;
      }

      public @Override
      void m_2(BufferBuilder bufferBuilder, Render3DEvent event) {
         float floatValue6 = this.m_3_float();
         int value2 = KillEffects.fadeColor(this.m_1_int(), 0.3F * floatValue6);

         for (int i = 0; i < 30; i++) {
            Vec3 vec3 = this.m_7(i, super.floatValue5);
            KillEffects.this.m_9(bufferBuilder, event, vec3.x, vec3.y, vec3.z, this.floatValueArray4[i] * 3.5F, value2);
         }

         float floatValue7 = 1.0F - KillEffects.clamp01(super.floatValue5 / 0.15F);
         if (floatValue7 > 0.0F) {
            KillEffects.this.m_9(
               bufferBuilder,
               event,
               super.doubleValue,
               super.doubleValue2 + super.floatValue2 * 0.5F,
               super.doubleValue3,
               (0.8F + 0.8F * (1.0F - floatValue7)) * super.floatValue3,
               KillEffects.fadeColor(ColorUtil.lerpColor(super.value, -1, 0.5F), 0.8F * floatValue7 * floatValue7)
            );
         }
      }

      public @Override
      void m_6(Render3DEvent event, Matrix4f matrix4f2) {
         Notifications.m_5_C3_sArr();
         float floatValue6 = this.m_3_float();
         if (!(floatValue6 <= 0.01F)) {
            ArrayList list = new ArrayList(720);
            ArrayList list2 = new ArrayList(360);
            int[] intArray = new int[180];
            int value2 = this.m_1_int();
            int value3 = 0;

            for (int i = 0; i < 30; i++) {
               Vec3 vec3 = this.m_7(i, super.floatValue5);
               this.quaternionf.fromAxisAngleRad(this.floatValueArray3[i * 4], this.floatValueArray3[i * 4 + 1], this.floatValueArray3[i * 4 + 2], this.floatValueArray3[i * 4 + 3] * super.floatValue5);
               float[] floatArray = KillEffects.FLOAT_VALUE_ARRAY[this.valueArray[i]];

               for (int i2 = 0; i2 < floatArray.length / 3; i2++) {
                  this.quaternionf.transform(this.vector3f.set(floatArray[i2 * 3], floatArray[i2 * 3 + 1], floatArray[i2 * 3 + 2]).mul(this.floatValueArray4[i]));
                  this.vec3Array[i2] = vec3.add(this.vector3f.x, this.vector3f.y, this.vector3f.z);
               }

               int[] intArray2 = KillEffects.VALUE_ARRAY[this.valueArray[i]];

               for (byte byteValue = 0; byteValue < intArray2.length; byteValue += 2) {
                  list.add(this.vec3Array[intArray2[byteValue]]);
                  list.add(this.vec3Array[intArray2[byteValue + 1]]);
               }

               Vec3 vec32 = vec3;

               for (int i3 = 1; i3 <= 6; i3++) {
                  Vec3 vec33 = this.m_7(i, Math.max(0.0F, super.floatValue5 - i3 * 0.025F));
                  list2.add(vec32);
                  list2.add(vec33);
                  intArray[value3++] = KillEffects.fadeColor(value2, 0.5F * floatValue6 * (1.0F - i3 / 7.0F));
                  vec32 = vec33;
               }
            }

            Line3DRenderer.m_7(matrix4f2, list2, intArray, event.getVec3(), 1.6F);
            KillEffects.m_4(matrix4f2, event, list, value2, floatValue6, 1.3F);
         }
      }
   }

   private static final class StateHolder5 implements VertexConsumer {
      public final FloatArrayList floatArrayList;

      private StateHolder5() {
         this.floatArrayList = new FloatArrayList();
      }

      @Override
      public void addVertex(float floatValue, float floatValue2, float floatValue3, int value, float floatValue4, float floatValue5, int value2, int value3, float floatValue6, float floatValue7, float floatValue8) {
         this.floatArrayList.add(floatValue);
         this.floatArrayList.add(floatValue2);
         this.floatArrayList.add(floatValue3);
      }

      @Override
      public VertexConsumer addVertex(float floatValue, float floatValue2, float floatValue3) {
         return this;
      }

      @Override
      public VertexConsumer setColor(int value, int value2, int value3, int value4) {
         return this;
      }

      @Override
      public VertexConsumer setColor(int value) {
         return this;
      }

      @Override
      public VertexConsumer setUv(float floatValue, float floatValue2) {
         return this;
      }

      @Override
      public VertexConsumer setUv1(int value, int value2) {
         return this;
      }

      @Override
      public VertexConsumer setUv2(int value, int value2) {
         return this;
      }

      @Override
      public VertexConsumer setNormal(float floatValue, float floatValue2, float floatValue3) {
         return this;
      }

      @Override
      public VertexConsumer setLineWidth(float floatValue) {
         return this;
      }
   }
}
