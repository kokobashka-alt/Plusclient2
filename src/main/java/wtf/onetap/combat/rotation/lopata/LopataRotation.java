package wtf.onetap.combat.rotation.lopata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import wtf.onetap.annotation.RotationInfo;
import wtf.onetap.combat.rotation.RotationMode;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

/**
 * KillAura rotation "lopata": a GRU trained on recorded human fights (see LopataRecorder and tools/lopata/train_lopata.py)
 * predicts a Gaussian over the yaw and pitch change of the next tick. One sample is drawn per tick, so the movement keeps the
 * natural spread the model learned instead of a smoothed mean. The engine then snaps the step to the mouse-sensitivity grid.
 *
 * The model file is <gameDir>/onetap/lopata/lopata.bin. Without a valid file the mode leaves the rotation untouched.
 */
@RotationInfo(name = "lopata")
public class LopataRotation implements MinecraftAccess, RotationMode {
   /** Scales the predicted standard deviation: 1 samples as trained, 0 always takes the mean. */
   private static final double TEMPERATURE = 1.0;
   private static final float MAX_YAW_STEP = 60.0F;
   private static final float MAX_PITCH_STEP = 30.0F;

   private final LopataObservation observation = new LopataObservation();
   private final float[] step = new float[2];
   private LopataNet net;
   private long modelStamp = Long.MIN_VALUE;
   private String reportedProblem;

   private static Path modelPath() {
      return FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("lopata").resolve("lopata.bin");
   }

   /** Loads the model on first use and again whenever the file changes, so a retrained model needs no restart. */
   private boolean ensureModel() {
      Path path = modelPath();
      long stamp;
      try {
         stamp = Files.exists(path) ? Files.getLastModifiedTime(path).toMillis() : -1L;
      } catch (IOException e) {
         stamp = -2L;
      }

      if (stamp == this.modelStamp) {
         return this.net != null;
      }

      this.modelStamp = stamp;
      this.net = null;
      if (stamp < 0L) {
         this.report("нет модели: положите lopata.bin в " + path.getParent());
         return false;
      }

      try (InputStream in = Files.newInputStream(path)) {
         this.net = LopataNet.load(in);
         this.reportedProblem = null;
         ChatUtil.log("lopata: модель загружена (скрытый слой " + this.net.hidden() + ")");
         return true;
      } catch (IOException e) {
         this.report("модель не загрузилась: " + e.getMessage());
         return false;
      }
   }

   private void report(String problem) {
      if (!problem.equals(this.reportedProblem)) {
         this.reportedProblem = problem;
         ChatUtil.log("lopata: " + problem);
      }
   }

   private void reset() {
      this.observation.reset();
      if (this.net != null) {
         this.net.resetState();
      }
   }

   @Override
   public Rotations m_6(
      float lastYaw, float lastPitch, Rotations rotations, AABB aABB, boolean flag, float attackReadiness, float dtSeconds, LivingEntity livingEntity
   ) {
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.level == null || livingEntity == null) {
         this.reset();
         return new Rotations(lastYaw, lastPitch);
      }

      if (!this.ensureModel()) {
         return new Rotations(lastYaw, lastPitch);
      }

      if (!this.observation.observe(MinecraftAccess.MINECRAFT.player, livingEntity, lastYaw, lastPitch)) {
         this.net.resetState();
         return new Rotations(lastYaw, lastPitch);
      }

      if (this.observation.repeatedTick()) {
         return new Rotations(lastYaw, lastPitch);
      }

      if (!this.observation.continuesSequence()) {
         this.net.resetState();
      }

      this.net.step(this.observation.features);
      this.net.sample(ThreadLocalRandom.current(), TEMPERATURE, this.step);
      float yawStep = this.step[0];
      float pitchStep = this.step[1];
      if (!Float.isFinite(yawStep) || !Float.isFinite(pitchStep)) {
         this.net.resetState();
         return new Rotations(lastYaw, lastPitch);
      }

      yawStep = Mth.clamp(yawStep, -MAX_YAW_STEP, MAX_YAW_STEP);
      pitchStep = Mth.clamp(pitchStep, -MAX_PITCH_STEP, MAX_PITCH_STEP);
      return new Rotations(lastYaw + yawStep, Mth.clamp(lastPitch + pitchStep, -90.0F, 90.0F));
   }

   @Override
   public Rotations m_5(float lastYaw, float lastPitch, Rotations rotations, AABB aABB, boolean flag, float attackReadiness, float dtSeconds) {
      return new Rotations(lastYaw, lastPitch);
   }

   @Override
   public void m_1_void() {
      this.reset();
   }

   @Override
   public void m_8_void() {
      this.reset();
   }
}
