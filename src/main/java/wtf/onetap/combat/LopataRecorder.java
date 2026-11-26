package wtf.onetap.module.combat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.rotation.lopata.LopataFeatures;
import wtf.onetap.combat.rotation.lopata.LopataObservation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

/**
 * Records the training set for the lopata rotation: for every tick in which the player aims at another player, the feature
 * vector of that tick and the yaw / pitch change that the player's own mouse produced during the next tick.
 * Output: <gameDir>/onetap/lopata/rec_<time>.csv, one file per enable. Train with tools/lopata/train_lopata.py.
 *
 * Recording pauses (and the sequence ends) while a screen is open, while KillAura or another module controls the rotation,
 * and when no player stands in the configured range and view angle.
 */
@ModuleInformation(name = "LopataRecorder", description = "Пишет датасет для lopata: ваши движения прицела при бое с игроками", category = Category.COMBAT)
public class LopataRecorder extends Module {
   private static final float MAX_YAW_LABEL = 90.0F;
   private static final float MAX_PITCH_LABEL = 60.0F;

   private final FloatSetting range = new FloatSetting("Дистанция", 6.0F, 3.0F, 10.0F, 0.5F).build();
   private final FloatSetting fov = new FloatSetting("Угол обзора", 70.0F, 20.0F, 120.0F, 5.0F).build();

   private final LopataObservation observation = new LopataObservation();
   private final float[] pending = new float[LopataFeatures.COUNT];
   private boolean hasPending;
   private float pendingYaw;
   private float pendingPitch;
   private Player target;
   private BufferedWriter out;
   private Path file;
   private int sequence;
   private long rows;

   public LopataRecorder() {
   }

   @Override
   public void onEnable() {
      this.closeFile();
      try {
         Path dir = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("lopata");
         Files.createDirectories(dir);
         String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
         this.file = dir.resolve("rec_" + stamp + ".csv");
         this.out = Files.newBufferedWriter(this.file, StandardCharsets.UTF_8);
         this.out.write("seq," + String.join(",", LopataFeatures.NAMES) + ",yawStep,pitchStep\n");
         this.sequence = 0;
         this.rows = 0L;
         this.endSequence();
         ChatUtil.log("lopata: запись в " + this.file.getFileName());
      } catch (IOException e) {
         this.out = null;
         ChatUtil.log("lopata: не удалось открыть файл записи: " + e.getMessage());
      }
   }

   @Override
   public void onDisable() {
      this.closeFile();
   }

   private void closeFile() {
      this.endSequence();
      if (this.out != null) {
         try {
            this.out.close();
         } catch (IOException ignored) {
         }

         ChatUtil.log(String.format(Locale.ROOT, "lopata: записано %d тиков (около %.1f мин) в %s", this.rows, this.rows / 1200.0, this.file.getFileName()));
         this.out = null;
      }
   }

   private void endSequence() {
      if (this.hasPending) {
         this.sequence++;
      }

      this.hasPending = false;
      this.target = null;
      this.observation.reset();
   }

   private boolean controlledByClient() {
      return Onetap.ROTATION_MODES.isFlag() || Onetap.ROTATION_MODES.isFlag5();
   }

   private Player pickTarget(Player self, float yaw, float pitch) {
      double maxRange = this.range.getValue();
      double maxAngle = this.fov.getValue();
      Player keep = this.target;
      if (keep != null && keep.isAlive() && !keep.isRemoved() && self.distanceTo(keep) <= maxRange * 1.2 && this.angleTo(self, keep, yaw, pitch) <= maxAngle * 1.5) {
         return keep;
      }

      Player best = null;
      double bestDist = Double.MAX_VALUE;
      for (Player other : MinecraftAccess.MINECRAFT.level.players()) {
         if (other == self || !other.isAlive() || other.isSpectator()) {
            continue;
         }

         double dist = self.distanceTo(other);
         if (dist <= maxRange && dist < bestDist && this.angleTo(self, other, yaw, pitch) <= maxAngle) {
            best = other;
            bestDist = dist;
         }
      }

      return best;
   }

   private double angleTo(Player self, Player other, float yaw, float pitch) {
      double dx = other.getX() - self.getX();
      double dz = other.getZ() - self.getZ();
      double dy = other.getY() + other.getBbHeight() * LopataFeatures.AIM_HEIGHT - self.getEyeY();
      float dyaw = LopataFeatures.wrapDegrees(LopataFeatures.yawTo(dx, dz) - yaw);
      float dpitch = LopataFeatures.pitchTo(dx, dy, dz) - pitch;
      return Math.hypot(dyaw, dpitch);
   }

   @EventTarget
   private void onTick(TickEvent event) {
      Player player = MinecraftAccess.MINECRAFT.player;
      if (!this.isEnabled() || this.out == null || player == null || MinecraftAccess.MINECRAFT.level == null) {
         this.endSequence();
         return;
      }

      if (MinecraftAccess.MINECRAFT.gui.screen() != null || this.controlledByClient()) {
         this.endSequence();
         return;
      }

      float yaw = player.getYRot();
      float pitch = player.getXRot();
      Player picked = this.pickTarget(player, yaw, pitch);
      if (picked == null) {
         this.endSequence();
         return;
      }

      if (picked != this.target) {
         this.endSequence();
         this.target = picked;
      }

      if (!this.observation.observe(player, picked, yaw, pitch)) {
         this.endSequence();
         return;
      }

      if (this.observation.repeatedTick()) {
         return;
      }

      if (this.hasPending && !this.observation.continuesSequence()) {
         this.endSequence();
         this.target = picked;
         if (!this.observation.observe(player, picked, yaw, pitch)) {
            return;
         }
      }

      if (this.hasPending) {
         float yawStep = LopataFeatures.wrapDegrees(yaw - this.pendingYaw);
         float pitchStep = pitch - this.pendingPitch;
         if (Math.abs(yawStep) > MAX_YAW_LABEL || Math.abs(pitchStep) > MAX_PITCH_LABEL) {
            this.endSequence();
            this.target = picked;
            if (!this.observation.observe(player, picked, yaw, pitch)) {
               return;
            }
         } else {
            this.writeRow(yawStep, pitchStep);
         }
      }

      System.arraycopy(this.observation.features, 0, this.pending, 0, LopataFeatures.COUNT);
      this.pendingYaw = yaw;
      this.pendingPitch = pitch;
      this.hasPending = true;
   }

   private void writeRow(float yawStep, float pitchStep) {
      StringBuilder sb = new StringBuilder(160);
      sb.append(this.sequence);
      for (float v : this.pending) {
         sb.append(',').append(v);
      }

      sb.append(',').append(yawStep).append(',').append(pitchStep).append('\n');
      try {
         this.out.write(sb.toString());
         this.rows++;
         if ((this.rows & 255L) == 0L) {
            this.out.flush();
         }
      } catch (IOException e) {
         ChatUtil.log("lopata: ошибка записи, запись остановлена: " + e.getMessage());
         this.out = null;
      }
   }
}
