package wtf.onetap.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.bot.BotManager;
import wtf.onetap.event.BlockStateChangeEvent;
import wtf.onetap.event.EntitySpawnEvent;
import wtf.onetap.module.movement.NoClip;
import wtf.onetap.module.render.Weather;
import wtf.onetap.pve.CombatPlanner;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
   @Inject(method = "addEntity", at = @At("RETURN"))
   private void onetap$addEntity(Entity var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new EntitySpawnEvent(var1));
   }

   @Inject(method = "tickEntities", at = @At("RETURN"))
   private void onetap$entitiesTicked(CallbackInfo var1) {
      if ((Object)this == Minecraft.getInstance().level) {
         if (BotManager.BOT_MANAGER.m_1_boolean()) {
            CombatPlanner.COMBAT_PLANNER.m_3_void();
         }
      }
   }

   @Inject(method = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", at = @At("HEAD"), cancellable = true)
   private void onetap$noVanillaRain(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12, CallbackInfo var14) {
      if (var1 == ParticleTypes.RAIN) {
         Weather var15 = Onetap.MODULE_MANAGER.get(Weather.class);
         if (var15 != null && var15.m_3_boolean()) {
            var14.cancel();
         }
      }
   }

   @Inject(method = "setServerVerifiedBlockState", at = @At("HEAD"), cancellable = true)
   private void onetap$setServerVerifiedBlockState(BlockPos var1, BlockState var2, int var3, CallbackInfo var4) {
      ClientLevel var5 = (ClientLevel)(Object)this;
      NoClip var6 = Onetap.MODULE_MANAGER.get(NoClip.class);
      if (var6 != null && var6.onBlockUpdate(var5, var1, var2, var3)) {
         var4.cancel();
      } else {
         BlockState var7 = var5.getBlockState(var1);
         if (var7 != var2) {
            Onetap.EVENT_BUS.unregister(new BlockStateChangeEvent(var1.immutable(), var7, var2));
         }
      }
   }

   @Inject(method = "syncBlockState", at = @At("HEAD"), cancellable = true)
   private void onetap$syncBlockState(BlockPos var1, BlockState var2, Vec3 var3, CallbackInfo var4) {
      NoClip var5 = Onetap.MODULE_MANAGER.get(NoClip.class);
      if (var5 != null && var5.isGrimGroundMode() && var5.onBlockUpdate((ClientLevel)(Object)this, var1, var2, 19)) {
         var4.cancel();
      }
   }
}
