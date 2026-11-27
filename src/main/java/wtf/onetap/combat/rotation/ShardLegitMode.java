package wtf.onetap.combat.rotation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.RotationInfo;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

/**
 * KillAura rotation "ShardLegit": adapter that lets the engine drive {@link ShardbetaRotation}. The delegate
 * keeps its own state per target and already snaps its step to the mouse-sensitivity grid, so the engine must not
 * snap again. It is called at 20 Hz because its hold, drift and acceleration counters count calls and were written
 * for one call per game tick, like ShardbetaRotation.
 */
@RotationInfo(name = "ShardLegit", fps = 20)
public class ShardLegitMode implements MinecraftAccess, RotationMode {
   private final ShardbetaRotation delegate = new ShardbetaRotation();

   @Override
   public Rotations m_6(
      float lastYaw, float lastPitch, Rotations rotations, AABB aABB, boolean flag, float attackReadiness, float dtSeconds, LivingEntity livingEntity
   ) {
      KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.level == null || killAura == null || livingEntity == null) {
         this.delegate.reset();
         return new Rotations(lastYaw, lastPitch);
      }

      double range = killAura.getHitDistance() + killAura.getRotationDistance();
      return this.delegate.updateAsRotations(livingEntity, aABB, lastYaw, lastPitch, range);
   }

   @Override
   public Rotations m_5(float lastYaw, float lastPitch, Rotations rotations, AABB aABB, boolean flag, float attackReadiness, float dtSeconds) {
      return new Rotations(lastYaw, lastPitch);
   }

   @Override
   public boolean m_2_boolean() {
      return false;
   }

   /** The delegate already quantizes to the sensitivity grid. */
   @Override
   public boolean m_6_boolean() {
      return true;
   }

   @Override
   public void m_1_void() {
      this.delegate.reset();
   }
}
