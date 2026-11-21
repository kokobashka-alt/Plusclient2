package wtf.onetap.module.render;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleGroupRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.WaterSplashEmitter;
import wtf.onetap.render.WaterSplashParticleGroup;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "WaterSplash", description = "Объёмные всплески воды при падении в воду", category = Category.RENDER)
public class WaterSplash extends Module {
   private final Int2ObjectOpenHashMap<WaterSplash.StateHolder> int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();
   private Object object;
   private long longValue;

   public WaterSplash() {
      ParticleGroupRegistry.register(WaterSplashParticleGroup.PARTICLE_RENDER_TYPE, WaterSplashParticleGroup::new);
   }

   @Override
   public void onDisable() {
      this.m_4_void();
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         this.m_4_void();
      } else {
         if (this.object != MinecraftAccess.MINECRAFT.level) {
            this.m_4_void();
            this.object = MinecraftAccess.MINECRAFT.level;
         }

         long longValue2 = ++this.longValue;

         for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
            if (entity != null && !(entity instanceof AbstractArrow)) {
               ((WaterSplash.StateHolder)this.int2ObjectOpenHashMap.computeIfAbsent(entity.getId(), value -> new WaterSplash.StateHolder())).m_3(Math.abs(entity.getDeltaMovement().y), longValue2);
            }
         }

         this.int2ObjectOpenHashMap.values().removeIf(c0 -> c0.longValue != longValue2);
      }
   }

   public void m_3_void(Entity entity) {
      if (entity.level() instanceof ClientLevel clientLevel && !(entity instanceof AbstractArrow)) {
         FluidState fluidState = Fluids.EMPTY.defaultFluidState();
         BlockPos blockPos = entity.blockPosition();

         for (int i = 0; i < 5; i++) {
            FluidState fluidState2 = clientLevel.getFluidState(blockPos.above(i));
            if (fluidState.getType() == Fluids.WATER && fluidState2.isEmpty()) {
               this.m_6(clientLevel, entity, Mth.floor(entity.getY()) + (i - 1) + fluidState.getOwnHeight());
               return;
            }

            fluidState = fluidState2;
         }
      }
   }

   private void m_6(ClientLevel clientLevel, Entity entity, float floatValue) {
      Notifications.m_5_C3_sArr();
      double doubleValue = Math.abs(entity.getDeltaMovement().y);
      WaterSplash.StateHolder c0 = (WaterSplash.StateHolder)this.int2ObjectOpenHashMap.get(entity.getId());
      if (c0 != null) {
         doubleValue = Math.max(doubleValue, c0.m_3_double());
      }

      ParticleStatus particleStatus = MinecraftAccess.MINECRAFT.options.particles().get();
      if (particleStatus == ParticleStatus.DECREASED && clientLevel.getRandom().nextInt(3) == 0) {
         particleStatus = ParticleStatus.MINIMAL;
      }

      if (!(MinecraftAccess.MINECRAFT.gameRenderer.mainCamera().position().distanceToSqr(entity.getX(), floatValue, entity.getZ()) > 1024.0) && particleStatus != ParticleStatus.MINIMAL) {
         MinecraftAccess.MINECRAFT.particleEngine.add(new WaterSplashEmitter(clientLevel, entity.getX(), floatValue, entity.getZ(), entity.getBbWidth(), (float)doubleValue));
      }
   }

   private void m_4_void() {
      this.int2ObjectOpenHashMap.clear();
      this.object = null;
      this.longValue = 0L;
   }

   private static final class StateHolder {
      private final double[] doubleValueArray;
      private int value;
      private int value2;
      private long longValue;

      private StateHolder() {
         this.doubleValueArray = new double[4];
      }

      public void m_3(double doubleValue, long longValue2) {
         this.doubleValueArray[this.value2] = doubleValue;
         Notifications.m_5_C3_sArr();
         this.value2 = this.value2 + 1 & 3;
         if (this.value < 4) {
            this.value++;
         }

         this.longValue = longValue2;
      }

      public double m_3_double() {
         Notifications.m_5_C3_sArr();
         double doubleValue = 0.0;

         for (int i = 0; i < this.value; i++) {
            doubleValue = Math.max(doubleValue, this.doubleValueArray[i]);
         }

         return doubleValue;
      }
   }
}
