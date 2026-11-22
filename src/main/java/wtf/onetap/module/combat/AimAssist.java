package wtf.onetap.module.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.AimUtil;
import wtf.onetap.combat.CombatTargetHolder;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

@ModuleInformation(name = "AimAssist", description = "Доводит прицел к цели внутри конуса FOV и опционально бьёт, когда цель в прицеле", category = Category.COMBAT)
public class AimAssist extends Module {
   private final MultiModeSetting targets = new MultiModeSetting("Цели", "Игроки", "Игроки", "Мобы").build();
   private final FloatSetting reach = new FloatSetting("Дистанция", 4.5F, 2.0F, 6.0F, 0.1F).build();
   private final FloatSetting fov = new FloatSetting("Угол FOV", 90.0F, 10.0F, 180.0F, 1.0F).build();
   private final FloatSetting strength = new FloatSetting("Сила доводки", 0.5F, 0.0F, 1.0F, 0.05F).build();
   private final FloatSetting speed = new FloatSetting("Скорость (град/с)", 60.0F, 5.0F, 300.0F, 5.0F).build();
   private final FloatSetting verticalScale = new FloatSetting("Вертикаль", 0.6F, 0.0F, 1.0F, 0.05F).build();
   private final BooleanSetting onlyWhileAttackKey = new BooleanSetting("Только при зажатой ЛКМ", false).build();
   private final BooleanSetting triggerBot = new BooleanSetting("Триггербот", false).build();
   private final FloatSetting delayMin = new FloatSetting("Задержка мин (мс)", 80.0F, 0.0F, 400.0F, 10.0F)
      .visibleWhen(this.triggerBot::getValue).build();
   private final FloatSetting delayMax = new FloatSetting("Задержка макс (мс)", 140.0F, 0.0F, 600.0F, 10.0F)
      .visibleWhen(this.triggerBot::getValue).build();
   private volatile LivingEntity target;
   private long lastFrameNanos;
   private long attackAtMs;

   public AimAssist() {
   }

   @Override
   public void onDisable() {
      this.target = null;
      this.lastFrameNanos = 0L;
      this.attackAtMs = 0L;
      CombatTargetHolder.setAimAssistTarget(null);
   }

   /** Picks the target and runs the triggerbot once per game tick. */
   @EventTarget
   private void onTick(TickEvent event) {
      Player player = MinecraftAccess.MINECRAFT.player;
      if (!this.isEnabled() || player == null || MinecraftAccess.MINECRAFT.level == null || MinecraftAccess.MINECRAFT.gameMode == null) {
         this.target = null;
         this.attackAtMs = 0L;
         CombatTargetHolder.setAimAssistTarget(null);
         return;
      }

      boolean attacking = !(Boolean)this.onlyWhileAttackKey.getValue() || MinecraftAccess.MINECRAFT.options.keyAttack.isDown();
      Vec3 eye = player.getEyePosition();
      this.target = attacking ? this.findTarget(player, eye) : null;
      CombatTargetHolder.setAimAssistTarget(this.target);

      if ((Boolean)this.triggerBot.getValue()) {
         this.handleTriggerBot(player, eye);
      }
   }

   /** Moves the view toward the target once per rendered frame, using the real frame time. */
   @EventTarget
   private void onRender3D(Render3DEvent event) {
      Player player = MinecraftAccess.MINECRAFT.player;
      long now = System.nanoTime();
      float dtSeconds = this.lastFrameNanos == 0L ? 0.0F : (float) ((now - this.lastFrameNanos) / 1.0E9);
      this.lastFrameNanos = now;

      LivingEntity current = this.target;
      if (!this.isEnabled() || player == null || current == null || !current.isAlive()) {
         return;
      }

      Rotations toTarget = new Rotations(player.getEyePosition(), current.getBoundingBox().getCenter());
      float yaw = player.getYRot();
      float pitch = player.getXRot();
      float targetYaw = toTarget.getFloatValue();
      float targetPitch = toTarget.getFloatValue2();
      if (AimAssistMath.fovDistance(yaw, pitch, targetYaw, targetPitch) > this.fov.getFloat() / 2.0F) {
         return;
      }

      float[] step = AimAssistMath.smoothStep(
         yaw, pitch, targetYaw, targetPitch,
         this.strength.getFloat(), this.speed.getFloat(), this.verticalScale.getFloat(), dtSeconds
      );
      player.setYRot(yaw + step[0]);
      player.setXRot(AimAssistMath.clampPitch(pitch + step[1]));
   }

   private void handleTriggerBot(Player player, Vec3 eye) {
      LivingEntity current = this.target;
      boolean inCrosshair = current != null && AimUtil.isLookingAtEntity(current, null, this.reach.getFloat());
      boolean ready = player.getAttackStrengthScale(0.0F) >= 1.0F;
      if (!inCrosshair || !ready) {
         this.attackAtMs = 0L;
         return;
      }

      long nowMs = System.nanoTime() / 1_000_000L;
      if (this.attackAtMs == 0L) {
         this.attackAtMs = nowMs + AimAssistMath.humanDelayMs(
            ThreadLocalRandom.current().nextDouble(),
            Math.round(this.delayMin.getFloat()),
            Math.round(this.delayMax.getFloat())
         );
         return;
      }

      if (nowMs >= this.attackAtMs) {
         MinecraftAccess.MINECRAFT.gameMode.attack(player, current);
         player.swing(InteractionHand.MAIN_HAND);
         this.attackAtMs = 0L;
      }
   }

   private LivingEntity findTarget(Player player, Vec3 eye) {
      double range = this.reach.getFloat();
      double rangeSq = range * range;
      var box = player.getBoundingBox().inflate(range + 2.0);
      LivingEntity best = null;
      float bestFov = Float.MAX_VALUE;
      for (Entity entity : MinecraftAccess.MINECRAFT.level.getEntities(player, box, e -> e.isPickable() && !e.isSpectator())) {
         if (!(entity instanceof LivingEntity living) || !living.isAlive() || living.getId() == player.getId()) {
            continue;
         }
         boolean isPlayer = living instanceof Player;
         if (isPlayer && !this.targets.isSelected("Игроки") || !isPlayer && !this.targets.isSelected("Мобы")) {
            continue;
         }
         if (living.getBoundingBox().distanceToSqr(eye) > rangeSq) {
            continue;
         }
         Rotations toEntity = new Rotations(eye, living.getBoundingBox().getCenter());
         float fovValue = AimAssistMath.fovDistance(player.getYRot(), player.getXRot(), toEntity.getFloatValue(), toEntity.getFloatValue2());
         if (fovValue < bestFov) {
            bestFov = fovValue;
            best = living;
         }
      }
      return best;
   }
}
