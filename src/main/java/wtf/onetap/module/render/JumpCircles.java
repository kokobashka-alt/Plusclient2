package wtf.onetap.module.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.ArrayList;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "JumpCircles", description = "Рисует расходящееся кольцо на месте прыжка", category = Category.RENDER)
public class JumpCircles extends Module {
   private final ModeSetting color = new ModeSetting("Цвет", "Тема", "Тема", "Свой", "Радуга").build();
   private final ColorSetting customColor = new ColorSetting("Свой цвет", ColorUtil.rgba(140, 180, 255, 255)).visibleWhen(() -> ((String)this.color.getValue()).equals("Свой")).build();
   private final FloatSetting lifetime = new FloatSetting("Жизнь", 25.0F, 5.0F, 80.0F, 1.0F).build();
   private final FloatSetting size = new FloatSetting("Размер", 1.5F, 0.5F, 4.0F, 0.1F).build();
   private final FloatSetting glow = new FloatSetting("Свечение", 0.2F, 0.05F, 1.0F, 0.05F).build();
   private final ArrayList<JumpCircles.StateHolder> c7List = new ArrayList<>();
   private final Matrix4f matrix4f = new Matrix4f();
   private final MutableBlockPos mutableBlockPos = new MutableBlockPos();
   private float[] floatValueArray = new float[64];
   private float[] floatValueArray2 = new float[8];
   private float[] floatValueArray3 = new float[8];
   private int value;
   private int value2;
   private int value3;
   private double doubleValue;
   private double doubleValue2;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private boolean flag;
   private double doubleValue3;
   private Object object;

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if (this.object != MinecraftAccess.MINECRAFT.level) {
            this.c7List.clear();
            this.object = MinecraftAccess.MINECRAFT.level;
         }

         boolean flag2 = MinecraftAccess.MINECRAFT.player.onGround();
         if (flag2) {
            this.doubleValue3 = MinecraftAccess.MINECRAFT.player.getY();
         }

         if (this.flag && !flag2 && MinecraftAccess.MINECRAFT.player.getDeltaMovement().y > 0.0) {
            this.c7List.add(new JumpCircles.StateHolder(MinecraftAccess.MINECRAFT.player.getX(), this.doubleValue3, MinecraftAccess.MINECRAFT.player.getZ(), this.lifetime.getInt()));
         }

         this.flag = flag2;

         for (int i = this.c7List.size() - 1; i >= 0; i--) {
            if (this.c7List.get(i).value2++ >= this.c7List.get(i).value) {
               this.c7List.remove(i);
            }
         }
      } else {
         this.c7List.clear();
         this.flag = false;
         this.object = null;
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null && !this.c7List.isEmpty()) {
         this.floatValue2 = (float)event.getVec3().y;
         float floatValue5 = this.size.getFloat();
         float floatValue6 = (float)(MinecraftAccess.MINECRAFT.level.getGameTime() % 4096L) + event.getFloatValue();
         this.matrix4f.set(event.getMatrix4f()).mul(event.getMatrix4f2());
         Box3DRenderer.m_4(this.matrix4f, this.c7List.size() * 4096, bufferBuilder -> {
            for (JumpCircles.StateHolder c7 : this.c7List) {
               float floatValue7 = Math.clamp((c7.value2 + event.getFloatValue()) / c7.value, 0.0F, 1.0F);
               float floatValue8 = 1.0F - (1.0F - floatValue7) * (1.0F - floatValue7);
               float floatValue9 = floatValue5 * floatValue8 * 0.5F;
               float floatValue10 = (1.0F - floatValue7) * (1.0F - floatValue7);
               this.floatValue = floatValue6 + c7.value2 * 0.05F;
               this.floatValue3 = (float)(c7.doubleValue - event.getVec3().x);
               this.floatValue4 = (float)(c7.doubleValue3 - event.getVec3().z);
               int value4 = Math.clamp((int)((this.m_3_int(0.0F, this.floatValue) >>> 24 & 0xFF) * floatValue10), 0, 255);
               float floatValue11 = floatValue9 * this.glow.getFloat();
               float floatValue12 = floatValue9 - floatValue11;
               float floatValue13 = floatValue9 + floatValue11 * 0.125F;
               float floatValue14 = floatValue9 + floatValue11;
               this.m_2(c7, floatValue14);
               int value5 = Math.clamp((int)Math.ceil((Math.PI * 2) * floatValue14 / 0.07F), 24, 256);
               this.m_8(bufferBuilder, value5, floatValue12, floatValue9, 0, value4);
               this.m_8(bufferBuilder, value5, floatValue9, floatValue14, value4, 0);
               this.m_8(bufferBuilder, value5, floatValue13, floatValue14, value4 / 2, 0);
            }
         });
      }
   }

   private void m_2(JumpCircles.StateHolder c7, float floatValue5) {
      this.value = Mth.floor(c7.doubleValue - floatValue5) - 1;
      this.value2 = Mth.floor(c7.doubleValue3 - floatValue5) - 1;
      this.value3 = Math.max(Mth.floor(c7.doubleValue + floatValue5) + 2 - this.value, Mth.floor(c7.doubleValue3 + floatValue5) + 2 - this.value2);
      if (this.floatValueArray.length < this.value3 * this.value3) {
         this.floatValueArray = new float[this.value3 * this.value3];
      }

      for (int i = 0; i < this.value3; i++) {
         for (int i2 = 0; i2 < this.value3; i2++) {
            this.floatValueArray[i * this.value3 + i2] = (float)(m_6(MinecraftAccess.MINECRAFT.level, this.mutableBlockPos, this.value + i, this.value2 + i2, c7.doubleValue2) - this.floatValue2);
         }
      }

      this.doubleValue = c7.doubleValue;
      this.doubleValue2 = c7.doubleValue3;
   }

   private float m_3_float(int value4, int value5) {
      int value6 = Math.clamp(value4 - this.value, 0, this.value3 - 1);
      int value7 = Math.clamp(value5 - this.value2, 0, this.value3 - 1);
      return this.floatValueArray[value6 * this.value3 + value7];
   }

   private float m_4_float(float floatValue5, float floatValue6) {
      double doubleValue4 = this.doubleValue + floatValue5;
      double doubleValue5 = this.doubleValue2 + floatValue6;
      int value4 = Mth.floor(doubleValue4);
      int value5 = Mth.floor(doubleValue5);
      float floatValue7 = (float)(doubleValue4 - value4);
      float floatValue8 = (float)(doubleValue5 - value5);
      float floatValue9 = this.m_3_float(value4, value5);
      float floatValue10 = floatValue9;

      for (int i = -1; i <= 1; i++) {
         for (int i2 = -1; i2 <= 1; i2++) {
            if (i != 0 || i2 != 0) {
               float floatValue11 = this.m_3_float(value4 + i, value5 + i2) - floatValue9;
               if (!(floatValue11 <= 0.01F) && !(floatValue11 > 1.3F)) {
                  float floatValue12 = i > 0 ? 1.0F - floatValue7 : (i < 0 ? floatValue7 : 0.0F);
                  float floatValue13 = i2 > 0 ? 1.0F - floatValue8 : (i2 < 0 ? floatValue8 : 0.0F);
                  float floatValue14 = Mth.sqrt(floatValue12 * floatValue12 + floatValue13 * floatValue13);
                  if (!(floatValue14 >= 0.4F)) {
                     float floatValue15 = 1.0F - floatValue14 / 0.4F;
                     floatValue10 = Math.max(floatValue10, floatValue9 + floatValue11 * floatValue15 * floatValue15 * (3.0F - 2.0F * floatValue15));
                  }
               }
            }
         }
      }

      return floatValue10 + 0.02F;
   }

   private void m_8(BufferBuilder bufferBuilder, int value4, float floatValue5, float floatValue6, int value5, int value6) {
      if (!(floatValue6 <= floatValue5)) {
         int value7 = Math.max(1, (int)Math.ceil((floatValue6 - floatValue5) / 0.07F));
         if (this.floatValueArray2.length < value7 + 1) {
            this.floatValueArray2 = new float[value7 + 1];
            this.floatValueArray3 = new float[value7 + 1];
         }

         float floatValue7 = 1.0F;
         float floatValue8 = 0.0F;
         int value8 = this.m_3_int(0.0F, this.floatValue);

         for (int i = 0; i <= value7; i++) {
            float floatValue9 = floatValue5 + (floatValue6 - floatValue5) * i / value7;
            this.floatValueArray2[i] = this.m_4_float(floatValue9, 0.0F);
         }

         for (int i2 = 1; i2 <= value4; i2++) {
            float floatValue10 = (float)(i2 * Math.PI * 2.0 / value4);
            float floatValue11 = Mth.cos(floatValue10);
            float floatValue12 = Mth.sin(floatValue10);
            int value9 = this.m_3_int(floatValue10, this.floatValue);

            for (int i3 = 0; i3 <= value7; i3++) {
               float floatValue13 = floatValue5 + (floatValue6 - floatValue5) * i3 / value7;
               this.floatValueArray3[i3] = this.m_4_float(floatValue11 * floatValue13, floatValue12 * floatValue13);
            }

            for (int i4 = 0; i4 < value7; i4++) {
               float floatValue14 = this.floatValueArray2[i4];
               float floatValue15 = this.floatValueArray2[i4 + 1];
               float floatValue16 = this.floatValueArray3[i4 + 1];
               float floatValue17 = this.floatValueArray3[i4];
               float floatValue18 = Math.min(Math.min(floatValue14, floatValue15), Math.min(floatValue17, floatValue16));
               float floatValue19 = Math.max(Math.max(floatValue14, floatValue15), Math.max(floatValue17, floatValue16));
               if (!(floatValue19 - floatValue18 > 0.6F)) {
                  float floatValue20 = floatValue5 + (floatValue6 - floatValue5) * i4 / value7;
                  float floatValue21 = floatValue5 + (floatValue6 - floatValue5) * (i4 + 1) / value7;
                  int value10 = value5 + (value6 - value5) * i4 / value7;
                  int value11 = value5 + (value6 - value5) * (i4 + 1) / value7;
                  this.m_3(bufferBuilder, floatValue7 * floatValue20, floatValue14, floatValue8 * floatValue20, value8, value10);
                  this.m_3(bufferBuilder, floatValue7 * floatValue21, floatValue15, floatValue8 * floatValue21, value8, value11);
                  this.m_3(bufferBuilder, floatValue11 * floatValue21, floatValue16, floatValue12 * floatValue21, value9, value11);
                  this.m_3(bufferBuilder, floatValue11 * floatValue20, floatValue17, floatValue12 * floatValue20, value9, value10);
               }
            }

            float[] floatArray = this.floatValueArray2;
            this.floatValueArray2 = this.floatValueArray3;
            this.floatValueArray3 = floatArray;
            floatValue7 = floatValue11;
            floatValue8 = floatValue12;
            value8 = value9;
         }
      }
   }

   private void m_3(BufferBuilder bufferBuilder, float floatValue5, float floatValue6, float floatValue7, int value4, int value5) {
      bufferBuilder.addVertex(this.floatValue3 + floatValue5, floatValue6, this.floatValue4 + floatValue7).setColor(value4 >> 16 & 0xFF, value4 >> 8 & 0xFF, value4 & 0xFF, value5);
   }

   private static double m_6(Level level, MutableBlockPos mutableBlockPos2, int value4, int value5, double doubleValue4) {
      int value6 = Mth.floor(doubleValue4);
      double doubleValue5 = doubleValue4;

      for (int i = value6; i < value6 + 3; i++) {
         VoxelShape voxelShape = level.getBlockState(mutableBlockPos2.set(value4, i, value5)).getCollisionShape(level, mutableBlockPos2);
         if (voxelShape.isEmpty()) {
            break;
         }

         double doubleValue6 = i + voxelShape.bounds().maxY;
         if (doubleValue6 <= doubleValue4 + 0.03) {
            break;
         }

         doubleValue5 = doubleValue6;
      }

      if (doubleValue5 > doubleValue4) {
         return doubleValue5;
      }

      if (!m_1(level, mutableBlockPos2, value4, value6, value5, doubleValue4) && !m_1(level, mutableBlockPos2, value4, value6 - 1, value5, doubleValue4)) {
         for (int i2 = value6 - 1; i2 > value6 - 4; i2--) {
            VoxelShape voxelShape2 = level.getBlockState(mutableBlockPos2.set(value4, i2, value5)).getCollisionShape(level, mutableBlockPos2);
            if (!voxelShape2.isEmpty()) {
               return i2 + voxelShape2.bounds().maxY;
            }
         }

         return doubleValue4;
      } else {
         return doubleValue4;
      }
   }

   private static boolean m_1(Level level, MutableBlockPos mutableBlockPos2, int value4, int value5, int value6, double doubleValue4) {
      VoxelShape voxelShape = level.getBlockState(mutableBlockPos2.set(value4, value5, value6)).getCollisionShape(level, mutableBlockPos2);
      return !voxelShape.isEmpty() && Math.abs(value5 + voxelShape.bounds().maxY - doubleValue4) < 0.03;
   }

   private int m_3_int(float floatValue5, float floatValue6) {
      return switch ((String)this.color.getValue()) {
         case "Свой" -> this.customColor.getValue();
         case "Радуга" -> ColorUtil.m_3_int(floatValue6);
         default -> ColorUtil.lerpColor(ColorUtil.primaryColor(), ColorUtil.secondaryColor(), 0.5F + 0.5F * (float)Math.cos(floatValue5 - floatValue6 * 0.08F));
      };
   }

   @Override
   public void onDisable() {
      this.c7List.clear();
      this.flag = false;
      this.object = null;
   }

   private static final class StateHolder {
      public final double doubleValue;
      public final double doubleValue2;
      public final double doubleValue3;
      public final int value;
      public int value2;

      public StateHolder(double doubleValue4, double doubleValue5, double doubleValue6, int value4) {
         this.doubleValue = doubleValue4;
         this.doubleValue2 = doubleValue5;
         this.doubleValue3 = doubleValue6;
         this.value = value4;
      }
   }
}
