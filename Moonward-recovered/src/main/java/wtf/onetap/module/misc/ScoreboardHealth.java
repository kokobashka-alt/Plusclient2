package wtf.onetap.module.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "ScoreboardHealth", description = "Берет реальное хп из скорборда", category = Category.MISC)
public class ScoreboardHealth extends Module {
   public ScoreboardHealth() {
      this.setEnabled(true);
   }

   public float m_0_float(LivingEntity livingEntity) {
      return this.m_8(livingEntity, false);
   }

   public float m_8(LivingEntity livingEntity, boolean flag) {
      float floatValue = this.m_3_float(livingEntity);
      if (flag) {
         floatValue += Math.max(0.0F, livingEntity.getAbsorptionAmount());
      }

      return Math.max(0.0F, floatValue);
   }

   private float m_3_float(LivingEntity livingEntity) {
      if (!this.isEnabled()) {
         return livingEntity.getHealth();
      }

      if (livingEntity instanceof Player player) {
         if (MinecraftAccess.MINECRAFT.level == null) {
            return livingEntity.getHealth();
         }

         Scoreboard scoreboard = MinecraftAccess.MINECRAFT.level.getScoreboard();
         Objective objective = scoreboard.getDisplayObjective(DisplaySlot.BELOW_NAME);
         if (objective == null) {
            return livingEntity.getHealth();
         }

         ReadOnlyScoreInfo readOnlyScoreInfo = scoreboard.getPlayerScoreInfo(player, objective);
         return readOnlyScoreInfo == null ? livingEntity.getHealth() : readOnlyScoreInfo.value();
      } else {
         return livingEntity.getHealth();
      }
   }
}
