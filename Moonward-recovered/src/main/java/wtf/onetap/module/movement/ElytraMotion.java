package wtf.onetap.module.movement;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.PostTravelEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "ElytraMotion", description = "Замораживает у планирующей цели", category = Category.MOVEMENT)
public class ElytraMotion extends Module {
   public boolean flag;
   private boolean flag2;
   private Vec3 vec3;

   @Override
   public void onEnable() {
      super.onEnable();
      this.flag = false;
      this.flag2 = false;
      this.vec3 = null;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.setNoGravity(false);
      }

      this.m_3_void();
      this.flag2 = false;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         LivingEntity livingEntity = KillAura.livingEntity;
         if (livingEntity == null) {
            this.m_3_void();
            this.flag2 = true;
         } else {
            this.flag2 = false;
            double doubleValue = MinecraftAccess.MINECRAFT.player.getEyePosition().distanceTo(livingEntity.getBoundingBox().getCenter());
            boolean flag3 = livingEntity.isFallFlying() && livingEntity.getDeltaMovement().length() * 20.0 >= 10.0;
            float floatValue = livingEntity.isFallFlying() ? 1.5F : 3.0F;
            boolean flag4 = MinecraftAccess.MINECRAFT.player.isFallFlying() && doubleValue < floatValue && !flag3;
            if (flag4) {
               Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getDeltaMovement();
               if (!this.flag) {
                  this.vec3 = vec32;
                  this.flag = true;
               } else if (vec32.lengthSqr() > 0.05) {
                  this.vec3 = vec32;
               }

               MinecraftAccess.MINECRAFT.player.setDeltaMovement(Vec3.ZERO);
               MinecraftAccess.MINECRAFT.player.setNoGravity(true);
            } else {
               this.m_3_void();
            }
         }
      }
   }

   @EventTarget
   private void onPostTravel(PostTravelEvent event) {
      if (this.flag && MinecraftAccess.MINECRAFT.player != null) {
         Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getDeltaMovement();
         if (vec32.lengthSqr() > 0.05) {
            this.vec3 = vec32;
         }

         MinecraftAccess.MINECRAFT.player.setDeltaMovement(Vec3.ZERO);
      }
   }

   private void m_3_void() {
      if (this.flag) {
         this.flag = false;
         if (MinecraftAccess.MINECRAFT.player != null) {
            MinecraftAccess.MINECRAFT.player.setNoGravity(false);
            if (this.vec3 != null) {
               MinecraftAccess.MINECRAFT.player.setDeltaMovement(this.vec3);
            }
         }

         this.vec3 = null;
      }
   }
}
