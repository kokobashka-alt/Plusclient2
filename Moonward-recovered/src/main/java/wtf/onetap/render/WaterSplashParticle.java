package wtf.onetap.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class WaterSplashParticle extends Particle {
   private static final Identifier[] IDENTIFIER_ARRAY = m_1_IdentifierArr("water_splash");
   private static final Identifier[] IDENTIFIER_ARRAY2 = m_1_IdentifierArr("water_splash_foam");
   private final MutableBlockPos mutableBlockPos = new MutableBlockPos();
   private final Identifier[] identifierArray;
   public final float floatValue;
   private final float floatValue2;
   public TextureAtlasSprite textureAtlasSprite;
   public int value = -1;

   public WaterSplashParticle(ClientLevel clientLevel, double doubleValue, double doubleValue2, double doubleValue3, float floatValue3, float floatValue4, boolean flag) {
      this(clientLevel, doubleValue, doubleValue2, doubleValue3, floatValue3, floatValue4, flag ? IDENTIFIER_ARRAY2 : IDENTIFIER_ARRAY);
      if (!flag) {
         this.value = ARGB.opaque(BiomeColors.getAverageWaterColor(clientLevel, this.mutableBlockPos.set(doubleValue, doubleValue2, doubleValue3)));
      }
   }

   public WaterSplashParticle(ClientLevel clientLevel, double doubleValue, double doubleValue2, double doubleValue3, float floatValue3, float floatValue4, Identifier[] identifierArray2) {
      super(clientLevel, doubleValue, doubleValue2, doubleValue3);
      this.identifierArray = identifierArray2;
      this.floatValue = floatValue3;
      this.floatValue2 = floatValue4;
      super.lifetime = 18;
      super.gravity = 0.0F;
      this.m_6_void();
   }

   public static Identifier[] m_1_IdentifierArr(String text) {
      Identifier[] identifierArray2 = new Identifier[9];

      for (int i = 0; i < identifierArray2.length; i++) {
         identifierArray2[i] = Identifier.fromNamespaceAndPath("onetap", text + "_" + (i + 1));
      }

      return identifierArray2;
   }

   @Override
   public void tick() {
      super.tick();
      this.m_6_void();
      if (!super.level.getFluidState(this.mutableBlockPos.set(super.x, super.y, super.z)).is(FluidTags.WATER)) {
         this.remove();
      }
   }

   private void m_6_void() {
      if (!super.removed) {
         this.textureAtlasSprite = Minecraft.getInstance()
            .getAtlasManager()
            .getAtlasOrThrow(AtlasIds.PARTICLES)
            .getSprite(this.identifierArray[super.age * (this.identifierArray.length - 1) / super.lifetime]);
      }
   }

   @Override
   public ParticleRenderType getGroup() {
      return WaterSplashParticleGroup.PARTICLE_RENDER_TYPE;
   }

   public float m_5_float(float floatValue3) {
      return this.floatValue * (0.8F + 0.2F * Mth.lerp(floatValue3, super.age - 1.0F, super.age) / super.lifetime);
   }

   public void m_0(WaterSplashParticleGroup.StateHolder c7, Vec3 vec3, float floatValue3) {
      float floatValue4 = (float)(Mth.lerp(floatValue3, super.xo, super.x) - vec3.x);
      float floatValue5 = (float)(Mth.lerp(floatValue3, super.yo, super.y) - vec3.y);
      float floatValue6 = (float)(Mth.lerp(floatValue3, super.zo, super.z) - vec3.z);
      float floatValue7 = this.m_5_float(floatValue3);
      float floatValue8 = floatValue4 - floatValue7;
      float floatValue9 = floatValue4 + floatValue7;
      float floatValue10 = floatValue6 - floatValue7;
      float floatValue11 = floatValue6 + floatValue7;
      float floatValue12 = 2.0F / this.textureAtlasSprite.contents().width();
      float floatValue13 = this.textureAtlasSprite.getU(floatValue12);
      float floatValue14 = this.textureAtlasSprite.getU(1.0F - floatValue12);
      float floatValue15 = this.textureAtlasSprite.getV0();
      float floatValue16 = this.textureAtlasSprite.getV1();
      int value2 = this.getLightCoords(floatValue3);
      this.m_8(c7, floatValue8, floatValue10, floatValue8, floatValue11, floatValue5, floatValue13, floatValue14, floatValue15, floatValue16, value2);
      this.m_8(c7, floatValue8, floatValue11, floatValue9, floatValue11, floatValue5, floatValue13, floatValue14, floatValue15, floatValue16, value2);
      this.m_8(c7, floatValue9, floatValue11, floatValue9, floatValue10, floatValue5, floatValue13, floatValue14, floatValue15, floatValue16, value2);
      this.m_8(c7, floatValue9, floatValue10, floatValue8, floatValue10, floatValue5, floatValue13, floatValue14, floatValue15, floatValue16, value2);
   }

   private void m_8(WaterSplashParticleGroup.StateHolder c7, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, float floatValue10, float floatValue11, int value2) {
      float floatValue12 = floatValue7 + this.floatValue2;
      c7.m_3(floatValue3, floatValue7, floatValue4, floatValue8, floatValue11, this.value, value2);
      c7.m_3(floatValue5, floatValue7, floatValue6, floatValue9, floatValue11, this.value, value2);
      c7.m_3(floatValue5, floatValue12, floatValue6, floatValue9, floatValue10, this.value, value2);
      c7.m_3(floatValue3, floatValue12, floatValue4, floatValue8, floatValue10, this.value, value2);
      c7.m_3(floatValue5, floatValue7, floatValue6, floatValue9, floatValue11, this.value, value2);
      c7.m_3(floatValue3, floatValue7, floatValue4, floatValue8, floatValue11, this.value, value2);
      c7.m_3(floatValue3, floatValue12, floatValue4, floatValue8, floatValue10, this.value, value2);
      c7.m_3(floatValue5, floatValue12, floatValue6, floatValue9, floatValue10, this.value, value2);
   }
}
