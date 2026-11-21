package wtf.onetap.module.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Texture3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "TotemPop", description = "Иконка тотема и конфетти над игроком при слёте тотема", category = Category.RENDER)
public class TotemPop extends Module {
   private static final Identifier IDENTIFIER = Identifier.withDefaultNamespace("textures/item/totem_of_undying.png");
   private static final Identifier IDENTIFIER2 = Identifier.fromNamespaceAndPath("onetap", "images/particles/confetti.png");
   private static final Identifier IDENTIFIER3 = Identifier.fromNamespaceAndPath("onetap", "images/particles/star.png");
   private static final Identifier IDENTIFIER4 = Identifier.fromNamespaceAndPath("onetap", "images/particles/lightning.png");
   private static final int[] VALUE_ARRAY = new int[]{
      ColorUtil.rgb(255, 82, 82),
      ColorUtil.rgb(255, 152, 0),
      ColorUtil.rgb(255, 235, 59),
      ColorUtil.rgb(139, 195, 74),
      ColorUtil.rgb(0, 230, 118),
      ColorUtil.rgb(0, 229, 255),
      ColorUtil.rgb(64, 196, 255),
      ColorUtil.rgb(124, 77, 255),
      ColorUtil.rgb(255, 64, 129),
      ColorUtil.rgb(255, 215, 64)
   };
   private static final int[] VALUE_ARRAY2 = new int[]{ColorUtil.rgb(255, 255, 255), ColorUtil.rgb(255, 241, 118), ColorUtil.rgb(255, 215, 64), ColorUtil.rgb(64, 196, 255)};
   private final BooleanSetting icon = new BooleanSetting("Иконка", true).build();
   private final FloatSetting iconSize = new FloatSetting("Размер иконки", 0.6F, 0.3F, 1.2F, 0.05F).visibleWhen(() -> (Boolean)this.icon.getValue()).build();
   private final BooleanSetting confetti = new BooleanSetting("Конфетти", true).build();
   private final ModeSetting style = new ModeSetting("Стиль", "Конфетти", "Конфетти", "Звёздочки", "Молнии", "Вперемешку").visibleWhen(() -> (Boolean)this.confetti.getValue()).build();
   private final FloatSetting confettiCount = new FloatSetting("Кол-во конфетти", 60.0F, 10.0F, 150.0F, 5.0F).visibleWhen(() -> (Boolean)this.confetti.getValue()).build();
   private final FloatSetting particleSize = new FloatSetting("Размер партиклов", 1.0F, 0.4F, 2.0F, 0.05F).visibleWhen(() -> (Boolean)this.confetti.getValue()).build();
   private final BooleanSetting lieOnGround = new BooleanSetting("Ложиться на землю", true).visibleWhen(() -> (Boolean)this.confetti.getValue()).build();
   private final FloatSetting duration = new FloatSetting("Длительность", 3.0F, 1.5F, 6.0F, 0.5F).build();
   private final BooleanSetting onMobs = new BooleanSetting("На мобов", true).build();
   private final ArrayList<TotemPop.StateHolder2> c6List = new ArrayList<>();
   private final ArrayList<TotemPop.StateHolder> c2List = new ArrayList<>();
   private final MutableBlockPos mutableBlockPos = new MutableBlockPos();
   private final Matrix4f matrix4f = new Matrix4f();
   private final Vector4f[] vector4fArray = new Vector4f[]{new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f()};

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundEntityEventPacket clientboundEntityEventPacket) {
         if (clientboundEntityEventPacket.getEventId() == 35) {
            if (MinecraftAccess.MINECRAFT.level != null) {
               if (clientboundEntityEventPacket.getEntity(MinecraftAccess.MINECRAFT.level) instanceof LivingEntity livingEntity && (livingEntity instanceof Player || (Boolean)this.onMobs.getValue())) {
                  this.m_6_void(livingEntity);
               }
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         this.c6List.clear();
         this.c2List.clear();
      } else {
         this.c6List.removeIf(TotemPop.StateHolder2::m_4_boolean);

         for (int i = this.c2List.size() - 1; i >= 0; i--) {
            if (this.c2List.get(i).m_9_boolean((Boolean)this.lieOnGround.getValue())) {
               this.c2List.remove(i);
            }
         }

         int value = this.c2List.size() - 400;
         if (value > 0) {
            this.c2List.subList(0, value).clear();
         }
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null) {
         float floatValue = event.getFloatValue();
         if (!this.c6List.isEmpty()) {
            GpuTextureView gpuTextureView = this.getTextureView(IDENTIFIER);
            if (gpuTextureView != null) {
               Texture3DRenderer.m_5(event.getMatrix4f(), gpuTextureView, this.c6List.size(), false, true, Texture3DRenderer.m_6_GpuSampler(), bufferBuilder -> {
                  for (TotemPop.StateHolder2 c6 : this.c6List) {
                     this.m_3(bufferBuilder, event, c6, floatValue);
                  }
               });
            }
         }

         if (!this.c2List.isEmpty()) {
            int[] intArray = new int[3];

            for (TotemPop.StateHolder c2 : this.c2List) {
               intArray[c2.value3]++;
            }

            if (intArray[0] > 0) {
               this.m_2(event, floatValue, IDENTIFIER2, 0, intArray[0]);
            }

            if (intArray[1] > 0) {
               this.m_2(event, floatValue, IDENTIFIER3, 1, intArray[1]);
            }

            if (intArray[2] > 0) {
               this.m_2(event, floatValue, IDENTIFIER4, 2, intArray[2]);
            }
         }
      }
   }

   private void m_2(Render3DEvent event, float floatValue, Identifier identifier, int value, int value2) {
      GpuTextureView gpuTextureView = this.getTextureView(identifier);
      if (gpuTextureView != null) {
         Texture3DRenderer.m_3(event.getMatrix4f(), gpuTextureView, value2, true, bufferBuilder -> {
            for (TotemPop.StateHolder c2 : this.c2List) {
               if (c2.value3 == value) {
                  this.m_7(bufferBuilder, event, c2, floatValue);
               }
            }
         });
      }
   }

   private void m_6_void(LivingEntity livingEntity) {
      if ((Boolean)this.icon.getValue()) {
         this.c6List.add(new TotemPop.StateHolder2(livingEntity, (int)(this.duration.getFloat() * 20.0F)));
      }

      if ((Boolean)this.confetti.getValue()) {
         ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
         double doubleValue = livingEntity.getX();
         double doubleValue2 = livingEntity.getY() + livingEntity.getBbHeight() * 0.6;
         double doubleValue3 = livingEntity.getZ();
         int value = (int)(this.duration.getFloat() * 16.0F);
         int value2 = Math.min(this.confettiCount.getInt(), 400 - this.c2List.size());

         byte byteValue = switch ((String)this.style.getValue()) {
            case "Звёздочки" -> 1;
            case "Молнии" -> 2;
            case "Вперемешку" -> -1;
            default -> 0;
         };

         for (int i = 0; i < value2; i++) {
            double doubleValue4 = threadLocalRandom.nextDouble(Math.PI * 2);
            double doubleValue5 = threadLocalRandom.nextDouble(0.02, 0.22);
            float floatValue = (float)(Math.cos(doubleValue4) * doubleValue5);
            float floatValue2 = (float)(threadLocalRandom.nextFloat() < 0.3F ? threadLocalRandom.nextDouble(0.3, 0.45) : threadLocalRandom.nextDouble(0.12, 0.3));
            float floatValue3 = (float)(Math.sin(doubleValue4) * doubleValue5);
            int value3 = byteValue >= 0 ? byteValue : threadLocalRandom.nextInt(3);
            int[] intArray = value3 == 2 ? VALUE_ARRAY2 : VALUE_ARRAY;
            this.c2List
               .add(
                  new TotemPop.StateHolder(
                     doubleValue + threadLocalRandom.nextDouble(-0.25, 0.25),
                     doubleValue2 + threadLocalRandom.nextDouble(-0.2, 0.3),
                     doubleValue3 + threadLocalRandom.nextDouble(-0.25, 0.25),
                     floatValue,
                     floatValue2,
                     floatValue3,
                     intArray[threadLocalRandom.nextInt(intArray.length)],
                     (float)threadLocalRandom.nextDouble(0.7, 1.25),
                     threadLocalRandom.nextDouble(Math.PI * 2),
                     (float)threadLocalRandom.nextDouble(-0.35, 0.35),
                     threadLocalRandom.nextDouble(Math.PI * 2),
                     (float)threadLocalRandom.nextDouble(0.12, 0.3),
                     value + threadLocalRandom.nextInt(20),
                     value3
                  )
               );
         }
      }
   }

   private void m_3(BufferBuilder bufferBuilder, Render3DEvent event, TotemPop.StateHolder2 c6, float floatValue) {
      float floatValue2 = c6.value2 + floatValue;
      float floatValue3 = Math.clamp(floatValue2 / 10.0F, 0.0F, 1.0F);
      float floatValue4 = floatValue3 - 1.0F;
      float floatValue5 = 1.0F + 4.0F * floatValue4 * floatValue4 * floatValue4 + 3.0F * floatValue4 * floatValue4;
      float floatValue6 = Math.clamp(floatValue2 / 4.0F, 0.0F, 1.0F);
      float floatValue7 = (1.0F - floatValue3) * (1.0F - floatValue3) * -0.7F;
      float floatValue8 = Math.clamp((c6.value - floatValue2) / 12.0F, 0.0F, 1.0F);
      float floatValue9 = 1.0F - floatValue8;
      float floatValue10 = 1.0F - (3.2F * floatValue9 * floatValue9 * floatValue9 - 2.2F * floatValue9 * floatValue9);
      float floatValue11 = floatValue9 * floatValue9 * floatValue9 * 0.8F;
      float floatValue12 = floatValue9 * floatValue9 * 0.9F;
      int value = (int)(255.0F * floatValue6 * floatValue8 * floatValue8);
      if (value > 4) {
         float floatValue13 = 1.0F + 0.035F * (float)Math.sin(floatValue2 * 0.18F);
         float floatValue14 = this.iconSize.getFloat() * floatValue5 * floatValue10 * floatValue13;
         if (!(floatValue14 <= 0.001F)) {
            LivingEntity livingEntity = c6.livingEntity;
            double doubleValue;
            double doubleValue2;
            double doubleValue3;
            if (!livingEntity.isRemoved() && livingEntity.level() == MinecraftAccess.MINECRAFT.level) {
               doubleValue = Mth.lerp(floatValue, livingEntity.xo, livingEntity.getX());
               doubleValue2 = Mth.lerp(floatValue, livingEntity.yo, livingEntity.getY()) + livingEntity.getBbHeight();
               doubleValue3 = Mth.lerp(floatValue, livingEntity.zo, livingEntity.getZ());
            } else {
               doubleValue = c6.doubleValue;
               doubleValue2 = c6.doubleValue2 + c6.doubleValue4;
               doubleValue3 = c6.doubleValue3;
            }

            doubleValue2 += 0.45 + (1.0F - floatValue5) * 0.9 + Math.sin(floatValue2 * 0.12) * 0.045 + floatValue11 + 0.25;
            this.m_1(bufferBuilder, event, doubleValue, doubleValue2, doubleValue3, floatValue14, floatValue14, (float)Math.sin(floatValue2 * 0.09) * 0.1F + floatValue7 + floatValue12, ColorUtil.withAlpha(-1, value));
         }
      }
   }

   private void m_7(BufferBuilder bufferBuilder, Render3DEvent event, TotemPop.StateHolder c2, float floatValue) {
      float floatValue2 = c2.value4 + floatValue;
      float floatValue3 = Math.clamp((c2.value2 - floatValue2) / 12.0F, 0.0F, 1.0F);
      if (!(floatValue3 <= 0.01F)) {
         double doubleValue = c2.doubleValue + (c2.doubleValue4 - c2.doubleValue) * floatValue;
         double doubleValue2 = c2.doubleValue2 + (c2.doubleValue5 - c2.doubleValue2) * floatValue;
         double doubleValue3 = c2.doubleValue3 + (c2.doubleValue6 - c2.doubleValue3) * floatValue;
         float floatValue4 = 0.085F * c2.floatValue4 * this.particleSize.getFloat();
         float floatValue5 = c2.floatValue5 + (c2.floatValue6 - c2.floatValue5) * floatValue;
         if (c2.flag) {
            this.m_6(bufferBuilder, event, doubleValue, doubleValue2, doubleValue3, floatValue4 * (c2.value3 == 0 ? 1.0F : 1.5F), floatValue4 * 1.5F, floatValue5, ColorUtil.withAlpha(c2.value, (int)(240.0F * floatValue3)));
         } else {
            float floatValue6;
            float floatValue7;
            float floatValue8;
            if (c2.value3 == 0) {
               float floatValue9 = (float)Math.cos(floatValue2 * c2.floatValue8 + c2.doubleValue7);
               floatValue6 = floatValue4 * floatValue9;
               floatValue7 = floatValue4 * 1.5F;
               floatValue8 = 240.0F;
            } else if (c2.value3 == 1) {
               floatValue6 = floatValue7 = floatValue4 * 1.5F;
               floatValue5 *= 0.5F;
               floatValue8 = 255.0F * (0.7F + 0.3F * (float)Math.sin(floatValue2 * 0.35 + c2.doubleValue7));
            } else {
               floatValue6 = floatValue7 = floatValue4 * 1.5F;
               floatValue8 = 255.0F * (0.5F + 0.5F * (float)Math.sin(floatValue2 * 0.8 + c2.doubleValue7));
            }

            this.m_1(bufferBuilder, event, doubleValue, doubleValue2, doubleValue3, floatValue6, floatValue7, floatValue5, ColorUtil.withAlpha(c2.value, (int)(floatValue8 * floatValue3)));
         }
      }
   }

   private void m_1(BufferBuilder bufferBuilder, Render3DEvent event, double doubleValue, double doubleValue2, double doubleValue3, float floatValue, float floatValue2, float floatValue3, int value) {
      Vec3 vec3 = event.getVec3();
      this.matrix4f.set(event.getMatrix4f2());
      this.matrix4f.translate((float)(doubleValue - vec3.x), (float)(doubleValue2 - vec3.y), (float)(doubleValue3 - vec3.z));
      this.matrix4f.rotate(event.getQuaternionf());
      if (floatValue3 != 0.0F) {
         this.matrix4f.rotateZ(floatValue3);
      }

      this.matrix4f.scale(floatValue, floatValue2, 1.0F);
      transformPointXY(this.matrix4f, -0.5F, 0.5F, this.vector4fArray[0]);
      transformPointXY(this.matrix4f, 0.5F, 0.5F, this.vector4fArray[1]);
      transformPointXY(this.matrix4f, 0.5F, -0.5F, this.vector4fArray[2]);
      transformPointXY(this.matrix4f, -0.5F, -0.5F, this.vector4fArray[3]);
      this.m_8(bufferBuilder, value);
   }

   private void m_6(BufferBuilder bufferBuilder, Render3DEvent event, double doubleValue, double doubleValue2, double doubleValue3, float floatValue, float floatValue2, float floatValue3, int value) {
      Vec3 vec3 = event.getVec3();
      this.matrix4f.set(event.getMatrix4f2());
      this.matrix4f.translate((float)(doubleValue - vec3.x), (float)(doubleValue2 - vec3.y), (float)(doubleValue3 - vec3.z));
      this.matrix4f.rotateY(floatValue3);
      this.matrix4f.scale(floatValue, 1.0F, floatValue2);
      transformPointXZ(this.matrix4f, -0.5F, -0.5F, this.vector4fArray[0]);
      transformPointXZ(this.matrix4f, 0.5F, -0.5F, this.vector4fArray[1]);
      transformPointXZ(this.matrix4f, 0.5F, 0.5F, this.vector4fArray[2]);
      transformPointXZ(this.matrix4f, -0.5F, 0.5F, this.vector4fArray[3]);
      this.m_8(bufferBuilder, value);
   }

   private void m_8(BufferBuilder bufferBuilder, int value) {
      int value2 = value >> 24 & 0xFF;
      int value3 = value >> 16 & 0xFF;
      int value4 = value >> 8 & 0xFF;
      int value5 = value & 0xFF;
      bufferBuilder.addVertex(this.vector4fArray[0].x, this.vector4fArray[0].y, this.vector4fArray[0].z).setUv(0.0F, 0.0F).setColor(value3, value4, value5, value2);
      bufferBuilder.addVertex(this.vector4fArray[1].x, this.vector4fArray[1].y, this.vector4fArray[1].z).setUv(1.0F, 0.0F).setColor(value3, value4, value5, value2);
      bufferBuilder.addVertex(this.vector4fArray[2].x, this.vector4fArray[2].y, this.vector4fArray[2].z).setUv(1.0F, 1.0F).setColor(value3, value4, value5, value2);
      bufferBuilder.addVertex(this.vector4fArray[3].x, this.vector4fArray[3].y, this.vector4fArray[3].z).setUv(0.0F, 1.0F).setColor(value3, value4, value5, value2);
   }

   private static void transformPointXY(Matrix4f matrix4f2, float floatValue, float floatValue2, Vector4f vector4f) {
      vector4f.set(floatValue, floatValue2, 0.0F, 1.0F);
      matrix4f2.transform(vector4f);
   }

   private static void transformPointXZ(Matrix4f matrix4f2, float floatValue, float floatValue2, Vector4f vector4f) {
      vector4f.set(floatValue, 0.0F, floatValue2, 1.0F);
      matrix4f2.transform(vector4f);
   }

   private GpuTextureView getTextureView(Identifier identifier) {
      AbstractTexture abstractTexture = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(identifier);
      if (abstractTexture == null) {
         return null;
      }

      GpuTextureView gpuTextureView = abstractTexture.getTextureView();
      return gpuTextureView != null && !gpuTextureView.isClosed() ? gpuTextureView : null;
   }

   @Override
   public void onDisable() {
      this.c6List.clear();
      this.c2List.clear();
   }

   private final class StateHolder {
      private double doubleValue;
      private double doubleValue2;
      private double doubleValue3;
      private double doubleValue4;
      private double doubleValue5;
      private double doubleValue6;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private final int value;
      private final float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private final float floatValue7;
      private final double doubleValue7;
      private final float floatValue8;
      private final int value2;
      private final int value3;
      private int value4;
      private boolean flag;

      private StateHolder(
         double doubleValue8,
         double doubleValue9,
         double doubleValue10,
         float floatValue9,
         float floatValue10,
         float floatValue11,
         int value5,
         float floatValue12,
         double doubleValue11,
         float floatValue13,
         double doubleValue12,
         float floatValue14,
         int value6,
         int value7
      ) {
         this.doubleValue = doubleValue8;
         this.doubleValue2 = doubleValue9;
         this.doubleValue3 = doubleValue10;
         this.doubleValue4 = doubleValue8;
         this.doubleValue5 = doubleValue9;
         this.doubleValue6 = doubleValue10;
         this.floatValue = floatValue9;
         this.floatValue2 = floatValue10;
         this.floatValue3 = floatValue11;
         this.value = value5;
         this.floatValue4 = floatValue12;
         this.floatValue5 = (float)doubleValue11;
         this.floatValue6 = (float)doubleValue11;
         this.floatValue7 = floatValue13;
         this.doubleValue7 = doubleValue12;
         this.floatValue8 = floatValue14;
         this.value2 = value6;
         this.value3 = value7;
      }

      private boolean m_9_boolean(boolean flag2) {
         Notifications.m_5_C3_sArr();
         this.value4++;
         if (this.flag) {
            this.doubleValue = this.doubleValue4;
            this.doubleValue2 = this.doubleValue5;
            this.doubleValue3 = this.doubleValue6;
            this.floatValue5 = this.floatValue6;
            return this.value4 >= this.value2;
         }

         this.doubleValue = this.doubleValue4;
         this.doubleValue2 = this.doubleValue5;
         this.doubleValue3 = this.doubleValue6;
         this.floatValue5 = this.floatValue6;
         this.doubleValue4 = this.doubleValue4 + this.floatValue;
         this.doubleValue5 = this.doubleValue5 + this.floatValue2;
         this.doubleValue6 = this.doubleValue6 + this.floatValue3;
         this.floatValue6 = this.floatValue6 + this.floatValue7;
         this.floatValue2 -= 0.016F;
         if (this.floatValue2 < -0.1F) {
            this.floatValue2 = -0.1F;
         }

         double doubleValue8 = this.value4 * 0.22F + this.doubleValue7;
         this.floatValue = this.floatValue + (float)Math.sin(doubleValue8) * 0.0028F;
         this.floatValue3 = this.floatValue3 + (float)Math.cos(doubleValue8 * 0.83F) * 0.0028F;
         this.floatValue *= 0.965F;
         this.floatValue2 *= 0.985F;
         this.floatValue3 *= 0.965F;
         if (flag2 && this.floatValue2 <= 0.0F) {
            TotemPop.this.mutableBlockPos.set(this.doubleValue4, this.doubleValue5, this.doubleValue6);
            VoxelShape voxelShape = MinecraftAccess.MINECRAFT.level.getBlockState(TotemPop.this.mutableBlockPos).getCollisionShape(MinecraftAccess.MINECRAFT.level, TotemPop.this.mutableBlockPos);
            if (!voxelShape.isEmpty()) {
               this.doubleValue5 = TotemPop.this.mutableBlockPos.getY() + voxelShape.max(Axis.Y) + 0.01 + this.doubleValue7 * 0.0015;
               this.floatValue = this.floatValue2 = this.floatValue3 = 0.0F;
               this.doubleValue = this.doubleValue4;
               this.doubleValue2 = this.doubleValue5;
               this.doubleValue3 = this.doubleValue6;
               this.floatValue5 = this.floatValue6;
               this.flag = true;
            }
         }

         return this.value4 >= this.value2;
      }
   }

   private final class StateHolder2 {
      private final LivingEntity livingEntity;
      private final int value;
      private double doubleValue;
      private double doubleValue2;
      private double doubleValue3;
      private double doubleValue4;
      private int value2;

      private StateHolder2(LivingEntity livingEntity2, int value3) {
         this.livingEntity = livingEntity2;
         this.value = value3;
         this.m_4_void();
      }

      private void m_4_void() {
         this.doubleValue = this.livingEntity.getX();
         this.doubleValue2 = this.livingEntity.getY();
         this.doubleValue3 = this.livingEntity.getZ();
         this.doubleValue4 = this.livingEntity.getBbHeight();
      }

      private boolean m_4_boolean() {
         Notifications.m_5_C3_sArr();
         this.value2++;
         if (!this.livingEntity.isRemoved()) {
            this.m_4_void();
         }

         return this.value2 >= this.value;
      }
   }
}
