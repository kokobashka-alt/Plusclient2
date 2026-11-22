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
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "TriggerBot", description = "Бьёт цель, когда она уже в прицеле и атака готова. Прицел не двигает", category = Category.COMBAT)
public class TriggerBot extends Module {
   private final MultiModeSetting targets = new MultiModeSetting("Цели", "Игроки", "Игроки", "Мобы").build();
   private final FloatSetting reach = new FloatSetting("Дистанция", 3.0F, 2.0F, 6.0F, 0.1F).build();
   private final FloatSetting delayMin = new FloatSetting("Задержка мин (мс)", 80.0F, 0.0F, 400.0F, 10.0F).build();
   private final FloatSetting delayMax = new FloatSetting("Задержка макс (мс)", 140.0F, 0.0F, 600.0F, 10.0F).build();
   private final BooleanSetting onlyWhileAttackKey = new BooleanSetting("Только при зажатой ЛКМ", false).build();
   private final BooleanSetting swing = new BooleanSetting("Взмах рукой", true).build();
   private long attackAtMs;

   public TriggerBot() {
   }

   @Override
   public void onDisable() {
      this.attackAtMs = 0L;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      Player player = MinecraftAccess.MINECRAFT.player;
      if (!this.isEnabled() || player == null || MinecraftAccess.MINECRAFT.level == null || MinecraftAccess.MINECRAFT.gameMode == null) {
         this.attackAtMs = 0L;
         return;
      }

      if ((Boolean)this.onlyWhileAttackKey.getValue() && !MinecraftAccess.MINECRAFT.options.keyAttack.isDown()) {
         this.attackAtMs = 0L;
         return;
      }

      LivingEntity target = this.findEntityInCrosshair(player);
      boolean ready = player.getAttackStrengthScale(0.0F) >= 1.0F;
      if (target == null || !ready) {
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
         MinecraftAccess.MINECRAFT.gameMode.attack(player, target);
         if ((Boolean)this.swing.getValue()) {
            player.swing(InteractionHand.MAIN_HAND);
         }
         this.attackAtMs = 0L;
      }
   }

   private LivingEntity findEntityInCrosshair(Player player) {
      double range = this.reach.getFloat();
      Vec3 eye = player.getEyePosition();
      var box = player.getBoundingBox().inflate(range + 1.0);
      for (Entity entity : MinecraftAccess.MINECRAFT.level.getEntities(player, box, e -> e.isPickable() && !e.isSpectator())) {
         if (!(entity instanceof LivingEntity living) || !living.isAlive() || living.getId() == player.getId()) {
            continue;
         }
         boolean isPlayer = living instanceof Player;
         if (isPlayer && !this.targets.isSelected("Игроки") || !isPlayer && !this.targets.isSelected("Мобы")) {
            continue;
         }
         if (living.getBoundingBox().distanceToSqr(eye) > range * range) {
            continue;
         }
         if (AimUtil.isLookingAtEntity(living, null, range)) {
            return living;
         }
      }
      return null;
   }
}
