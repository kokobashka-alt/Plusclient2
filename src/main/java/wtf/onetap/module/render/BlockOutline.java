package wtf.onetap.module.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.GlowEspRenderer;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "BlockOutline", description = "Кастомная обводка и заливка выделяемого блока", category = Category.RENDER)
public class BlockOutline extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "Обычный", "Обычный", "Шейдерный").build();
   private final ModeSetting color = new ModeSetting("Цвет", "Тема", "Тема", "Свой", "Радуга").build();
   private final ColorSetting customColor = new ColorSetting("Свой цвет", ColorUtil.rgba(140, 180, 255, 255)).visibleWhen(() -> ((String)this.color.getValue()).equals("Свой")).build();
   private final BooleanSetting fill = new BooleanSetting("Заливка", true).build();
   private final FloatSetting fillOpacity = new FloatSetting("Прозрачность заливки", 60.0F, 0.0F, 255.0F, 1.0F).visibleWhen(() -> (Boolean)this.fill.getValue()).build();
   private final BooleanSetting animation = new BooleanSetting("Анимация", true).build();
   private final FloatSetting speed = new FloatSetting("Скорость", 1.0F, 0.1F, 3.0F, 0.05F).visibleWhen(() -> (Boolean)this.animation.getValue()).build();
   private final FloatSetting thickness = new FloatSetting("Толщина", 1.5F, 0.5F, 6.0F, 0.1F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Обычный")).build();
   private final FloatSetting glow = new FloatSetting("Глоу", 2.0F, 0.0F, 9.0F, 0.05F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Шейдерный")).build();
   private final FloatSetting strength = new FloatSetting("Сила", 1.0F, 0.05F, 1.0F, 0.05F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Шейдерный")).build();
   private final Matrix4f matrix4f = new Matrix4f();
   private final List<Vec3> vec3List = new ArrayList<>(24);
   private final List<AABB> aABBList = new ArrayList<>();
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private boolean flag;
   private RenderTarget renderTarget;
   private float floatValue;
   private long longValue = System.nanoTime();

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         BlockPos blockPos = null;
         List list = null;
         if (MinecraftAccess.MINECRAFT.hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK) {
            blockPos = blockHitResult.getBlockPos();
            BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos);
            if (!blockState.isAir()) {
               list = blockState.getShape(MinecraftAccess.MINECRAFT.level, blockPos).toAabbs();
            }
         }

         boolean flag2 = list != null && !list.isEmpty();
         if (flag2) {
            if (!this.flag) {
               this.doubleValue = blockPos.getX();
               this.doubleValue2 = blockPos.getY();
               this.doubleValue3 = blockPos.getZ();
            }

            this.aABBList.clear();
            this.aABBList.addAll(list);
            this.flag = true;
         }

         long longValue2 = System.nanoTime();
         float floatValue2 = Math.min((float)(longValue2 - this.longValue) * 1.0E-9F, 0.1F);
         this.longValue = longValue2;
         float floatValue3 = flag2 ? 1.0F : 0.0F;
         float floatValue4 = this.animation.getValue() ? 1.0F - (float)Math.exp(-floatValue2 * this.speed.getFloat() * 6.0F) : 1.0F;
         this.floatValue = this.floatValue + (floatValue3 - this.floatValue) * floatValue4;
         if (flag2) {
            this.doubleValue = this.doubleValue + (blockPos.getX() - this.doubleValue) * floatValue4;
            this.doubleValue2 = this.doubleValue2 + (blockPos.getY() - this.doubleValue2) * floatValue4;
            this.doubleValue3 = this.doubleValue3 + (blockPos.getZ() - this.doubleValue3) * floatValue4;
         }

         if (this.floatValue <= 0.001F && !flag2) {
            this.aABBList.clear();
            this.flag = false;
         } else if (this.flag && !this.aABBList.isEmpty()) {
            Vec3 vec3 = event.getVec3();
            this.matrix4f.set(event.getMatrix4f()).mul(event.getMatrix4f2());
            int value = this.m_1(event);
            int value2 = (int)(this.floatValue * 255.0F);
            int value3 = ColorUtil.withAlpha(value, (int)(this.fillOpacity.getFloat() * this.floatValue));
            int value4 = this.aABBList.size();
            if ((Boolean)this.fill.getValue() && value3 >>> 24 > 0) {
               int value5 = value3;
               Box3DRenderer.m_8(this.matrix4f, value4 * 24, bufferBuilder -> {
                  for (AABB aABB : this.aABBList) {
                     this.m_0(bufferBuilder, aABB, vec3, value5);
                  }
               });
            }

            if (((String)this.mode.getValue()).equals("Шейдерный")) {
               this.m_7(value, vec3);
            } else {
               this.vec3List.clear();

               for (AABB aABB2 : this.aABBList) {
                  this.m_9_void(aABB2.move(this.doubleValue, this.doubleValue2, this.doubleValue3));
               }

               Line3DRenderer.drawLines(this.matrix4f, this.vec3List, vec3, this.thickness.getFloat(), ColorUtil.withAlpha(value, value2));
            }
         }
      }
   }

   private void m_0(BufferBuilder bufferBuilder, AABB aABB, Vec3 vec3, int value) {
      int value2 = value >> 16 & 0xFF;
      int value3 = value >> 8 & 0xFF;
      int value4 = value & 0xFF;
      int value5 = value >> 24 & 0xFF;
      float floatValue2 = (float)(aABB.minX + this.doubleValue - vec3.x);
      float floatValue3 = (float)(aABB.minY + this.doubleValue2 - vec3.y);
      float floatValue4 = (float)(aABB.minZ + this.doubleValue3 - vec3.z);
      float floatValue5 = (float)(aABB.maxX + this.doubleValue - vec3.x);
      float floatValue6 = (float)(aABB.maxY + this.doubleValue2 - vec3.y);
      float floatValue7 = (float)(aABB.maxZ + this.doubleValue3 - vec3.z);
      bufferBuilder.addVertex(floatValue2, floatValue3, floatValue7).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue5, floatValue3, floatValue7).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue5, floatValue3, floatValue4).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue2, floatValue3, floatValue4).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue2, floatValue6, floatValue4).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue5, floatValue6, floatValue4).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue5, floatValue6, floatValue7).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue2, floatValue6, floatValue7).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue2, floatValue3, floatValue4).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue5, floatValue3, floatValue4).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue5, floatValue6, floatValue4).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue2, floatValue6, floatValue4).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue2, floatValue6, floatValue7).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue5, floatValue6, floatValue7).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue5, floatValue3, floatValue7).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue2, floatValue3, floatValue7).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue2, floatValue3, floatValue4).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue2, floatValue6, floatValue4).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue2, floatValue6, floatValue7).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue2, floatValue3, floatValue7).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue5, floatValue3, floatValue7).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue5, floatValue6, floatValue7).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue5, floatValue6, floatValue4).setColor(value2, value3, value4, value5);
      bufferBuilder.addVertex(floatValue5, floatValue3, floatValue4).setColor(value2, value3, value4, value5);
   }

   private int m_1(Render3DEvent event) {
      return switch ((String)this.color.getValue()) {
         case "Свой" -> this.customColor.getValue();
         case "Радуга" -> ColorUtil.m_3_int(((float)(MinecraftAccess.MINECRAFT.level.getGameTime() % 4096L) + event.getFloatValue()) * 0.02F);
         default -> ColorUtil.primaryColor();
      };
   }

   private void m_7(int value, Vec3 vec3) {
      Window window = MinecraftAccess.MINECRAFT.getWindow();
      if (this.renderTarget == null) {
         this.renderTarget = new TextureTarget("BlockOutline Mask", window.getWidth(), window.getHeight(), false, GpuFormat.RGBA8_UNORM);
      }

      if (this.renderTarget.width != window.getWidth() || this.renderTarget.height != window.getHeight()) {
         this.renderTarget.resize(window.getWidth(), window.getHeight());
      }

      int value2 = value | 0xFF000000;
      Box3DRenderer.m_7(this.renderTarget.getColorTextureView(), true, this.matrix4f, this.aABBList.size() * 24, bufferBuilder -> {
         for (AABB aABB : this.aABBList) {
            this.m_0(bufferBuilder, aABB, vec3, value2);
         }
      });
      GlowEspRenderer.m_2(this.renderTarget, true, this.glow.getFloat(), this.strength.getFloat() * this.floatValue, 0, 0.0F, true);
   }

   private void m_9_void(AABB aABB) {
      double doubleValue4 = aABB.minX;
      double doubleValue5 = aABB.minY;
      double doubleValue6 = aABB.minZ;
      double doubleValue7 = aABB.maxX;
      double doubleValue8 = aABB.maxY;
      double doubleValue9 = aABB.maxZ;
      this.addLine(doubleValue4, doubleValue5, doubleValue6, doubleValue7, doubleValue5, doubleValue6);
      this.addLine(doubleValue4, doubleValue5, doubleValue9, doubleValue7, doubleValue5, doubleValue9);
      this.addLine(doubleValue4, doubleValue8, doubleValue6, doubleValue7, doubleValue8, doubleValue6);
      this.addLine(doubleValue4, doubleValue8, doubleValue9, doubleValue7, doubleValue8, doubleValue9);
      this.addLine(doubleValue4, doubleValue5, doubleValue6, doubleValue4, doubleValue5, doubleValue9);
      this.addLine(doubleValue7, doubleValue5, doubleValue6, doubleValue7, doubleValue5, doubleValue9);
      this.addLine(doubleValue4, doubleValue8, doubleValue6, doubleValue4, doubleValue8, doubleValue9);
      this.addLine(doubleValue7, doubleValue8, doubleValue6, doubleValue7, doubleValue8, doubleValue9);
      this.addLine(doubleValue4, doubleValue5, doubleValue6, doubleValue4, doubleValue8, doubleValue6);
      this.addLine(doubleValue7, doubleValue5, doubleValue6, doubleValue7, doubleValue8, doubleValue6);
      this.addLine(doubleValue4, doubleValue5, doubleValue9, doubleValue4, doubleValue8, doubleValue9);
      this.addLine(doubleValue7, doubleValue5, doubleValue9, doubleValue7, doubleValue8, doubleValue9);
   }

   private void addLine(double doubleValue4, double doubleValue5, double doubleValue6, double doubleValue7, double doubleValue8, double doubleValue9) {
      this.vec3List.add(new Vec3(doubleValue4, doubleValue5, doubleValue6));
      this.vec3List.add(new Vec3(doubleValue7, doubleValue8, doubleValue9));
   }

   @Override
   public void onDisable() {
      if (this.renderTarget != null) {
         this.renderTarget.destroyBuffers();
         this.renderTarget = null;
      }

      this.floatValue = 0.0F;
      this.flag = false;
      this.aABBList.clear();
   }
}
