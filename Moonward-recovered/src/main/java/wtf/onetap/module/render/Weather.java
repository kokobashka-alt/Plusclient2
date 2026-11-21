package wtf.onetap.module.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.WeatherRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(
   name = "Weather",
   description = "Шейдерная погода: объёмный дождь, снег и лепестки сакуры, мокрые поверхности, рябь, капли на экране",
   category = Category.RENDER
)
public class Weather extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "По погоде", "По погоде", "Всегда").visibleWhen(() -> !this.isPetals()).build();
   private final ModeSetting type = new ModeSetting("Тип", "Авто", "Авто", "Дождь", "Снег", "Лепестки").build();
   private final FloatSetting density = new FloatSetting("Плотность", 1.0F, 0.2F, 1.6F, 0.05F).build();
   private final FloatSetting range = new FloatSetting("Дальность", 44.0F, 20.0F, 56.0F, 1.0F).build();
   private final FloatSetting dropLength = new FloatSetting("Длина капель", 1.1F, 0.5F, 2.0F, 0.05F).visibleWhen(this::m_9_boolean).build();
   private final FloatSetting size = new FloatSetting("Размер", 1.0F, 0.5F, 2.5F, 0.05F).build();
   private final FloatSetting wind = new FloatSetting("Ветер", 0.35F, 0.0F, 1.0F, 0.05F).build();
   private final BooleanSetting wetSurfaces = new BooleanSetting("Мокрые поверхности", true).visibleWhen(this::m_9_boolean).build();
   private final BooleanSetting reflections = new BooleanSetting("Отражения", true).visibleWhen(() -> this.m_9_boolean() && (Boolean)this.wetSurfaces.getValue()).build();
   private final BooleanSetting screenDrops = new BooleanSetting("Капли на экране", true).visibleWhen(this::m_9_boolean).build();
   private final ModeSetting quality = new ModeSetting("Качество", "Среднее", "Низкое", "Среднее", "Высокое").build();
   private final BooleanSetting themeColor = new BooleanSetting("Цвет темы", false).visibleWhen(this::m_9_boolean).build();
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(185, 210, 255, 255)).visibleWhen(() -> this.m_9_boolean() && !(Boolean)this.themeColor.getValue()).build();
   private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(65536).order(ByteOrder.nativeOrder());
   private final float[] floatValueArray = new float[128];
   private final float[] floatValueArray2 = new float[56];
   private final MutableBlockPos mutableBlockPos = new MutableBlockPos();
   private final Matrix4f matrix4f = new Matrix4f();
   private final Matrix4f matrix4f2 = new Matrix4f();
   private Object object;
   private boolean flag;
   private boolean flag2 = true;
   private boolean flag3;
   private int value;
   private int value2;
   private float floatValue;
   private boolean flag4;
   private float floatValue2 = 0.55F;
   private double doubleValue = -1.0;
   private double doubleValue2;
   private double doubleValue3;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private long longValue;
   private double doubleValue4 = Double.NaN;
   private double doubleValue5 = Double.NaN;

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if (this.object != MinecraftAccess.MINECRAFT.level) {
            this.m_4_void();
            this.object = MinecraftAccess.MINECRAFT.level;
         }

         if (!MinecraftAccess.MINECRAFT.isPaused()) {
            this.longValue++;
         }

         long longValue2 = this.longValue;
         Precipitation precipitation = this.getPrecipitation();

         boolean flag5 = switch ((String)this.type.getValue()) {
            case "Дождь" -> false;
            case "Снег" -> true;
            case "Лепестки" -> false;
            default -> !this.m_5_boolean() && precipitation == Precipitation.SNOW;
         };
         float floatValue8 = !this.m_5_boolean() && !this.isPetals() ? (precipitation == Precipitation.NONE ? 0.0F : MinecraftAccess.MINECRAFT.level.getRainLevel(1.0F)) : 1.0F;
         this.flag = flag5;
         this.floatValue4 = this.floatValue4 + (floatValue8 - this.floatValue4) * 0.05F;
         if (this.floatValue4 < 0.004F && floatValue8 <= 0.0F) {
            this.floatValue4 = 0.0F;
         }

         this.flag4 = this.floatValue4 > 0.01F || floatValue8 > 0.01F;
         if (!this.flag && !this.isPetals() && this.floatValue4 > 0.1F) {
            float floatValue9 = Math.min(1.0F, 0.3F + this.floatValue4);
            this.floatValue5 = Math.min(floatValue9, this.floatValue5 + 0.0038461538F);
         } else {
            this.floatValue5 = Math.max(0.0F, this.floatValue5 - (this.flag ? 0.004166667F : 6.25E-4F));
         }
         this.floatValue2 = switch ((String)this.quality.getValue()) {
            case "Низкое" -> 0.72F;
            case "Высокое" -> 0.45F;
            default -> 0.55F;
         };
         float floatValue10 = 0.0F;
         if (!this.flag
            && !this.isPetals()
            && (Boolean)this.screenDrops.getValue()
            && MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson()
            && !MinecraftAccess.MINECRAFT.player.isUnderWater()
            && this.m_7_boolean()) {
            floatValue10 = this.floatValue4;
         }

         this.floatValue7 = this.isPetals() ? Math.min(1.0F, this.floatValue7 + 8.3333335E-4F) : Math.max(0.0F, this.floatValue7 - 0.005F);
         this.floatValue6 = this.floatValue6 + (floatValue10 - this.floatValue6) * (floatValue10 > this.floatValue6 ? 0.03F : 0.012F);
         if (this.floatValue6 < 0.003F && floatValue10 <= 0.0F) {
            this.floatValue6 = 0.0F;
         }

         this.m_9_void(longValue2);
      } else {
         this.m_4_void();
      }
   }

   public void m_2(Matrix4fc matrix4fc, Matrix4fc matrix4fc2, Vec3 vec3, float floatValue8, Vector4f vector4f) {
      if (this.isEnabled() && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null && this.object == MinecraftAccess.MINECRAFT.level) {
         float floatValue9 = this.floatValue4;
         float floatValue10 = this.wetSurfaces.getValue() ? this.floatValue5 : 0.0F;
         float floatValue11 = this.floatValue6 * 0.5F;
         if (!(floatValue9 < 0.01F) || !(floatValue10 < 0.01F) || !(floatValue11 < 0.01F)) {
            if (this.flag3 || !WeatherRenderer.hasTexture()) {
               WeatherRenderer.m_5(this.byteBuffer.clear());
               this.flag3 = false;
            }

            this.matrix4f.set(matrix4fc2);
            this.matrix4f2.set(matrix4fc2).invert();
            RenderTarget renderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
            float floatValue12 = matrix4fc.m11();
            float floatValue13 = 2.0F / floatValue12 / Math.max(1, renderTarget.height);
            float floatValue14 = floatValue12 / matrix4fc.m00();
            float floatValue15 = 0.2126F * vector4f.x + 0.7152F * vector4f.y + 0.0722F * vector4f.z;
            float floatValue16 = Mth.clamp(0.35F + floatValue15, 0.35F, 1.3F);
            int value3 = this.themeColor.getValue() ? ColorUtil.primaryColor() : (Integer)this.color.getValue();
            float floatValue17 = (value3 >> 16 & 0xFF) / 255.0F;
            float floatValue18 = (value3 >> 8 & 0xFF) / 255.0F;
            float floatValue19 = (value3 & 0xFF) / 255.0F;
            if (this.flag || this.isPetals()) {
               floatValue17 = 1.0F;
               floatValue18 = 1.0F;
               floatValue19 = 1.0F;
            }

            float floatValue20 = this.floatValue2;
            float floatValue21 = floatValue20 / 0.5F * (floatValue20 / 0.5F);
            float floatValue22 = Math.min(1.0F, 0.85F * this.density.getFloat() * (0.3F + 0.7F * floatValue9) * floatValue21);
            double doubleValue6 = ((double)this.longValue + floatValue8) / 20.0;
            float floatValue23 = (float)(((float)(this.longValue % 20480L) + floatValue8) / 20.0);
            double doubleValue7 = this.doubleValue < 0.0 ? 0.0 : Mth.clamp(doubleValue6 - this.doubleValue, 0.0, 0.1);
            this.doubleValue = doubleValue6;
            float floatValue24 = this.wind.getFloat() * 0.36F;
            m_9(doubleValue6, floatValue24, 0.0, this.floatValueArray2, 8);
            m_9(doubleValue6, floatValue24, 0.35, this.floatValueArray2, 32);
            m_9(doubleValue6, floatValue24, 0.8, this.floatValueArray2, 34);
            float floatValue25 = floatValue24 <= 0.0F ? 0.0F : m_7_float(doubleValue6);
            this.doubleValue2 = (this.doubleValue2 + this.floatValueArray2[8] * 70.0 * doubleValue7) % 8192.0;
            this.doubleValue3 = (this.doubleValue3 + this.floatValueArray2[9] * 70.0 * doubleValue7) % 8192.0;
            if (doubleValue7 == 0.0) {
               this.floatValue3 = (float)vec3.y;
            }

            this.floatValue3 = this.floatValue3 + ((float)vec3.y - this.floatValue3) * (float)Math.min(1.0, doubleValue7 * 1.5);
            boolean flag5 = RenderSystem.getDevice().getDeviceInfo().isZZeroToOne();
            float[] floatArray = this.floatValueArray2;
            floatArray[0] = matrix4fc.m22();
            floatArray[1] = matrix4fc.m32();
            floatArray[2] = matrix4fc.m00();
            floatArray[3] = floatValue12;
            this.doubleValue4 = m_3_double(this.doubleValue4, vec3.x);
            this.doubleValue5 = m_3_double(this.doubleValue5, vec3.z);
            floatArray[4] = (float)(vec3.x - this.doubleValue4);
            floatArray[5] = (float)vec3.y;
            floatArray[6] = (float)(vec3.z - this.doubleValue5);
            floatArray[7] = floatValue23;
            floatArray[10] = this.range.getFloat();
            floatArray[11] = flag5 ? 1.0F : 0.0F;
            floatArray[12] = floatValue9;
            floatArray[13] = floatValue10;
            floatArray[14] = this.isPetals() ? 2.0F : (this.flag ? 1.0F : 0.0F);
            floatArray[15] = floatValue22;
            floatArray[16] = this.dropLength.getFloat();
            floatArray[17] = this.size.getFloat();
            floatArray[18] = this.floatValue;
            floatArray[19] = floatValue13;
            floatArray[20] = floatValue11;
            floatArray[21] = this.reflections.getValue() && this.wetSurfaces.getValue() ? 1.0F : 0.0F;
            floatArray[22] = floatValue20;
            floatArray[23] = floatValue14;
            floatArray[24] = floatValue17 * floatValue16;
            floatArray[25] = floatValue18 * floatValue16;
            floatArray[26] = floatValue19 * floatValue16;
            floatArray[27] = this.isPetals() ? 0.95F : (this.flag ? 0.8F : 0.55F);
            floatArray[28] = vector4f.x;
            floatArray[29] = vector4f.y;
            floatArray[30] = vector4f.z;
            floatArray[31] = floatValue15;
            floatArray[36] = floatValue25;
            floatArray[37] = (float)this.doubleValue2;
            floatArray[38] = (float)this.doubleValue3;
            floatArray[39] = this.floatValue3;
            m_0(floatArray, floatValue20 * (this.flag ? 0.62F : (this.isPetals() ? 2.0F : 1.0F)), this.range.getFloat(), this.isPetals());
            floatArray[43] = this.floatValue7;
            floatArray[47] = this.isPetals() ? 41.0F : 0.0F;
            WeatherRenderer.m_7(this.matrix4f, this.matrix4f2, floatArray);
         }
      }
   }

   private static void m_0(float[] floatArray, float floatValue8, float floatValue9, boolean flag5) {
      float[] floatArray2 = flag5 ? new float[]{floatValue9, 0.0F, 0.0F} : new float[]{Math.min(9.0F, floatValue9), Math.min(24.0F, floatValue9), floatValue9};
      float[] floatArray3 = new float[]{0.0F, 9.0F, 24.0F};

      for (int i = 0; i < 3; i++) {
         float floatValue10 = floatValue8 * (1 << i);
         float floatValue11 = floatArray2[i] + (i < 2 && !flag5 ? 2.0F : 0.0F);
         if (!(floatArray2[i] <= floatArray3[i])) {
            Math.ceil(floatValue11 / floatValue10);
         }

         Math.ceil(2.0F * floatValue11 / 14.0F);
      }
   }

   private static double m_3_double(double doubleValue6, double doubleValue7) {
      return Math.abs(doubleValue7 - doubleValue6) <= 8192.0 ? doubleValue6 : Math.round(doubleValue7 / 2048.0) * 2048.0;
   }

   private static float m_7_float(double doubleValue6) {
      double doubleValue7 = 0.5 + 0.5 * (0.55 * Math.sin(doubleValue6 * 0.61 + 0.4) + 0.3 * Math.sin(doubleValue6 * 1.37 + 2.1) + 0.15 * Math.sin(doubleValue6 * 2.9 + 4.2));
      doubleValue7 = Mth.clamp(doubleValue7, 0.0, 1.0);
      doubleValue7 = doubleValue7 * doubleValue7 * (3.0 - 2.0 * doubleValue7);
      return (float)(doubleValue7 * doubleValue7);
   }

   private static void m_9(double doubleValue6, float floatValue8, double doubleValue7, float[] floatArray, int value3) {
      double doubleValue8 = doubleValue6 + doubleValue7;
      float floatValue9 = m_7_float(doubleValue8);
      double doubleValue9 = 0.6 + 1.1 * Math.sin(doubleValue8 * 0.031) + 0.5 * Math.sin(doubleValue8 * 0.083 + 1.7) + 0.45 * Math.sin(doubleValue8 * 1.13 + 0.7) * floatValue9;
      Math.cos(doubleValue9);
      Math.sin(doubleValue9);
   }

   public boolean m_3_boolean() {
      return this.flag4 && this.isEnabled();
   }

   private boolean m_9_boolean() {
      return !((String)this.type.getValue()).equals("Снег") && !this.isPetals();
   }

   private boolean isPetals() {
      return ((String)this.type.getValue()).equals("Лепестки");
   }

   private boolean m_5_boolean() {
      return ((String)this.mode.getValue()).equals("Всегда");
   }

   private Precipitation getPrecipitation() {
      return MinecraftAccess.MINECRAFT.level.getPrecipitationAt(MinecraftAccess.MINECRAFT.player.blockPosition());
   }

   private boolean m_7_boolean() {
      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      int value3 = MinecraftAccess.MINECRAFT.level.getHeight(Types.MOTION_BLOCKING, Mth.floor(vec3.x), Mth.floor(vec3.z));
      return value3 <= Mth.floor(vec3.y);
   }

   private void m_9_void(long longValue2) {
      int value3 = Mth.floor(MinecraftAccess.MINECRAFT.player.getX());
      int value4 = Mth.floor(MinecraftAccess.MINECRAFT.player.getZ());
      boolean flag5 = this.flag2 || Math.abs(value3 - this.value) > 24 || Math.abs(value4 - this.value2) > 24;

      for (int i = value4 - 56; i <= value4 + 56; i++) {
         if (flag5 || (i + longValue2 & 3L) == 0L) {
            this.m_1_void(i, value3);
         }
      }

      this.flag2 = false;
      this.value = value3;
      this.value2 = value4;
      this.flag3 = true;
      float floatValue8 = -512.0F;

      for (int i2 = value4 - 56; i2 <= value4 + 56; i2++) {
         floatValue8 = Math.max(floatValue8, this.floatValueArray[i2 & 127]);
      }

      this.floatValue = floatValue8;
   }

   private void m_1_void(int value3, int value4) {
      float floatValue8 = -512.0F;

      for (int i = value4 - 56; i <= value4 + 56; i++) {
         int value5 = MinecraftAccess.MINECRAFT.level.getHeight(Types.MOTION_BLOCKING, i, value3);
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(this.mutableBlockPos.set(i, value5 - 1, value3));
         VoxelShape voxelShape = blockState.getShape(MinecraftAccess.MINECRAFT.level, this.mutableBlockPos);
         float floatValue9;
         int value6;
         if (blockState.blocksMotion() && !voxelShape.isEmpty()) {
            double doubleValue6 = voxelShape.max(Axis.Y);
            floatValue9 = (float)(value5 - 1 + doubleValue6);
            value6 = 0;

            for (int i2 = 0; i2 < 4; i2++) {
               double doubleValue7 = voxelShape.max(Axis.Y, (i2 >> 1) * 0.5 + 0.25, (i2 & 1) * 0.5 + 0.25);
               int value7 = doubleValue7 == Double.NEGATIVE_INFINITY ? 15 : Mth.clamp((int)Math.round((doubleValue6 - doubleValue7) * 16.0), 0, 14);
               value6 |= value7 << i2 * 4;
            }

            if (value6 == 65535) {
               value6 = 0;
            }
         } else {
            FluidState fluidState = blockState.getFluidState();
            floatValue9 = value5 - 1 + (fluidState.isEmpty() ? 1.0F : fluidState.getHeight(MinecraftAccess.MINECRAFT.level, this.mutableBlockPos));
            value6 = fluidState.is(FluidTags.WATER) ? 65535 : 0;
         }

         int value8 = Mth.clamp(Math.round((floatValue9 + 512.0F) / 4096.0F * 65535.0F), 0, 65535);
         int value9 = ((value3 & 127) * 128 + (i & 127)) * 4;
         this.byteBuffer.putShort(value9, (short)value8);
         this.byteBuffer.putShort(value9 + 2, (short)value6);
         if (floatValue9 > floatValue8) {
            floatValue8 = floatValue9;
         }
      }

      this.floatValueArray[value3 & 127] = floatValue8;
   }

   private void m_4_void() {
      this.object = null;
      this.flag = false;
      this.flag2 = true;
      this.flag3 = false;
      this.floatValue = 0.0F;
      this.floatValue4 = 0.0F;
      this.floatValue5 = 0.0F;
      this.floatValue6 = 0.0F;
      this.floatValue7 = 0.0F;
      this.flag4 = false;
      this.doubleValue = -1.0;
      this.doubleValue4 = Double.NaN;
      this.doubleValue5 = Double.NaN;
      Arrays.fill(this.floatValueArray, -512.0F);
   }

   @Override
   public void onEnable() {
      this.m_4_void();
   }

   @Override
   public void onDisable() {
      this.m_4_void();
   }
}
