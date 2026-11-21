package wtf.onetap.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;

public class WaterSplashEmitter extends NoRenderParticle {
   private final MutableBlockPos mutableBlockPos = new MutableBlockPos();
   private final float floatValue;
   private final float floatValue2;
   private final float floatValue3;

   public WaterSplashEmitter(ClientLevel clientLevel, double doubleValue, double doubleValue2, double doubleValue3, float floatValue4, float floatValue5) {
      super(clientLevel, doubleValue, doubleValue2, doubleValue3);
      super.lifetime = 24;
      super.gravity = 0.0F;
      this.floatValue = Math.min(2.0F, floatValue5);
      this.floatValue2 = floatValue4;
      this.floatValue3 = this.floatValue / 2.0F + floatValue4 / 3.0F;
      this.m_6_void(floatValue4, this.floatValue3);
      if (this.floatValue > 0.5F) {
         this.m_6_void(floatValue4, 0.1875F + this.floatValue / 8.0F + floatValue4 / 6.0F, 0.15F);
      } else {
         this.remove();
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (super.age == 8) {
         float floatValue4 = this.floatValue2 * 0.66F;
         this.m_6_void(floatValue4, this.floatValue3 * 2.0F);
         this.m_6_void(floatValue4, 0.375F + this.floatValue / 8.0F + this.floatValue2 / 6.0F, 0.05F);
      }

      if (!super.level.getFluidState(this.mutableBlockPos.set(super.x, super.y, super.z)).is(FluidTags.WATER)) {
         this.remove();
      }
   }

   private void m_6_void(float floatValue4, float floatValue5) {
      ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
      particleEngine.add(new WaterSplashParticle(super.level, super.x, super.y, super.z, floatValue4, floatValue5, false));
      particleEngine.add(new WaterSplashParticle(super.level, super.x, super.y, super.z, floatValue4, floatValue5, true));
      particleEngine.add(new WaterSplashRingParticle(super.level, super.x, super.y, super.z, floatValue4));
   }

   private void m_6_void(float floatValue4, float floatValue5, float floatValue6) {
      for (int i = 0; i < floatValue4 * 20.0F; i++) {
         double doubleValue = (super.random.nextDouble() - super.random.nextDouble()) * floatValue6;
         double doubleValue2 = floatValue5 * (1.0 + (super.random.nextDouble() - super.random.nextDouble()) * 0.25);
         double doubleValue3 = (super.random.nextDouble() - super.random.nextDouble()) * floatValue6;
         super.level
            .addParticle(ParticleTypes.FALLING_WATER, super.x + doubleValue / floatValue6 * floatValue4, super.y + 0.0625, super.z + doubleValue3 / floatValue6 * floatValue4, doubleValue, doubleValue2, doubleValue3);
      }
   }
}
