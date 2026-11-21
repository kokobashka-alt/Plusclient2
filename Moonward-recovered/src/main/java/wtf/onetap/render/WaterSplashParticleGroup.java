package wtf.onetap.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Arrays;
import java.util.Set;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle.Layer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.world.phys.Vec3;

public class WaterSplashParticleGroup extends ParticleGroup<WaterSplashParticle> {
   public static final ParticleRenderType PARTICLE_RENDER_TYPE = new ParticleRenderType("onetap:water_splash", "WS");
   private final WaterSplashParticleGroup.StateHolder c7 = new WaterSplashParticleGroup.StateHolder();
   private static int value;

   public WaterSplashParticleGroup(ParticleEngine particleEngine) {
      super(particleEngine);
   }

   @Override
   public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float floatValue) {
      Vec3 vec3 = camera.position();

      for (WaterSplashParticle waterSplashParticle : super.particles) {
         waterSplashParticle.m_0(this.c7, vec3, floatValue);
      }

      return this.c7;
   }

   public static void m_8_void(int value2) {
      value = value2;
   }

   public static int m_3_int() {
      return value;
   }

   public static int m_0_int() {
      return 110;
   }

   private static String a(byte[] byteArray) {
      int value2 = 0;
      int value3;
      char[] charArray = new char[value3 = byteArray.length];

      for (int i = 0; i < value3; i++) {
         int value4;
         if ((value4 = 255 & byteArray[i]) < 192) {
            charArray[value2++] = (char)value4;
         } else if (value4 < 224) {
            char ch = (char)((char)(value4 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value2++] = ch;
         } else if (i < value3 - 2) {
            char ch2 = (char)((char)(value4 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value2++] = ch2;
         }
      }

      return new String(charArray, 0, value2);
   }

   public static final class StateHolder extends QuadParticleRenderState {
      private static final Layer LAYER = Layer.TRANSLUCENT;
      private static final Set<Layer> LAYER_SET = Set.of(LAYER);
      private float[] floatValueArray = new float[5120];
      private int[] valueArray = new int[2048];
      private int value;

      public void m_3(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, int value2, int value3) {
         if (this.value * 2 == this.valueArray.length) {
            this.floatValueArray = Arrays.copyOf(this.floatValueArray, this.floatValueArray.length * 2);
            this.valueArray = Arrays.copyOf(this.valueArray, this.valueArray.length * 2);
         }

         int value4 = this.value * 5;
         this.floatValueArray[value4] = floatValue;
         this.floatValueArray[value4 + 1] = floatValue2;
         this.floatValueArray[value4 + 2] = floatValue3;
         this.floatValueArray[value4 + 3] = floatValue4;
         this.floatValueArray[value4 + 4] = floatValue5;
         this.valueArray[this.value * 2] = value2;
         this.valueArray[this.value * 2 + 1] = value3;
         this.value++;
      }

      @Override
      public void buildLayer(Layer layer, VertexConsumer vertexConsumer) {
         if (layer == LAYER) {
            for (int i = 0; i < this.value; i++) {
               int value2 = i * 5;
               vertexConsumer.addVertex(this.floatValueArray[value2], this.floatValueArray[value2 + 1], this.floatValueArray[value2 + 2])
                  .setUv(this.floatValueArray[value2 + 3], this.floatValueArray[value2 + 4])
                  .setColor(this.valueArray[i * 2])
                  .setLight(this.valueArray[i * 2 + 1]);
            }
         }
      }

      @Override
      public Set<Layer> layers() {
         return this.value == 0 ? Set.of() : LAYER_SET;
      }

      @Override
      public boolean isEmpty() {
         return this.value == 0;
      }

      @Override
      public void clear() {
         this.value = 0;
      }

      @Override
      public void submit(SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
         if (this.value > 0) {
            submitNodeCollector.submitQuadParticleGroup(this);
         }
      }
   }
}
