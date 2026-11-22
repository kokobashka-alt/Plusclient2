package wtf.onetap.combat.rotation;

import java.util.Random;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.RotationInfo;
import wtf.onetap.combat.rotation.holyworld.Features;
import wtf.onetap.combat.rotation.holyworld.GruNet;
import wtf.onetap.combat.rotation.holyworld.HolyWorldAim;
import wtf.onetap.combat.rotation.holyworld.HolyWorldBrain;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

/**
 * KillAura rotation driven by a GRU that was trained inside the game on the player's own mouse tracking
 * (see {@code .holyworld}). It is called at 20 Hz, the rate the data was recorded at, and answers with the view
 * change for one tick; if a call arrives later than 50 ms the change is scaled by the elapsed time. Hidden state is
 * reset on a new target. Noise with the level measured on the training data is added to each output, and the result
 * is snapped to whole mouse counts with the remainder carried over. Without a trained model a plain proportional
 * controller is used, so the mode works before training too.
 */
@RotationInfo(name = "HolyWorld", fps = 20)
public class HolyWorldRotation implements RotationMode, MinecraftAccess {
    private static final float TICK_SECONDS = 0.05F;
    private static final float MIN_SCALE = 0.6F;
    private static final float MAX_SCALE = 2.5F;
    private static final double NOISE_FACTOR = 0.8;
    private static final float MAX_YAW_STEP = 40.0F;
    private static final float MAX_PITCH_STEP = 20.0F;
    private static final float FALLBACK_YAW_GAIN = 0.30F;
    private static final float FALLBACK_PITCH_GAIN = 0.25F;
    private static final long STALE_NANOS = 400_000_000L;
    private static final double UNKNOWN_HALF_WIDTH = 0.3;

    private final Random random = new Random();
    private final double[] features = new double[Features.N_IN];
    private final double[] output = new double[Features.N_OUT];
    private final float[] residual = new float[2];

    private LivingEntity tracked;
    private boolean trackedAny;
    private GruNet runnerModel;
    private GruNet.Runner runner;
    private long lastCallNanos;
    private double prevYawDelta;
    private double prevPitchDelta;
    private float lastAimYaw;
    private float lastAimPitch;
    private boolean haveAim;

    @Override
    public Rotations m_5(float yaw, float pitch, Rotations toTarget, AABB box, boolean flag, float extra, float dt) {
        return step(yaw, pitch, toTarget, null, null, dt);
    }

    @Override
    public Rotations m_9(float yaw, float pitch, Rotations toTarget, AABB box, boolean flag, float extra, float dt,
                         LivingEntity entity, Vec3 point, boolean flag2) {
        return step(yaw, pitch, toTarget, entity, point, dt);
    }

    private Rotations step(float yaw, float pitch, Rotations toTarget, LivingEntity entity, Vec3 point, float dt) {
        LocalPlayer player = MINECRAFT.player;
        if (player == null) {
            return new Rotations(yaw, pitch);
        }
        long now = System.nanoTime();
        if (!trackedAny || entity != tracked || now - lastCallNanos > STALE_NANOS) {
            begin(entity);
        }
        lastCallNanos = now;

        GruNet model = HolyWorldBrain.INSTANCE.model();
        if (model != runnerModel) {
            runnerModel = model;
            runner = model == null ? null : model.runner();
        }

        HolyWorldAim.Angles aim;
        if (entity != null) {
            aim = HolyWorldAim.angles(player, entity);
        } else if (point != null) {
            aim = HolyWorldAim.angles(player, point, UNKNOWN_HALF_WIDTH);
        } else {
            aim = null;
        }
        float aimYaw = aim != null ? aim.yaw : toTarget.getFloatValue();
        float aimPitch = aim != null ? aim.pitch : toTarget.getFloatValue2();
        double distance = aim != null ? aim.distance : 3.0;
        double size = aim != null ? aim.sizeDegrees : Math.toDegrees(Math.atan2(UNKNOWN_HALF_WIDTH, distance));

        float yawError = Mth.wrapDegrees(aimYaw - yaw);
        float pitchError = aimPitch - pitch;
        float scale = Mth.clamp(dt / TICK_SECONDS, MIN_SCALE, MAX_SCALE);

        double yawDelta;
        double pitchDelta;
        if (model != null && runner != null) {
            double yawVelocity = haveAim ? Mth.wrapDegrees(aimYaw - lastAimYaw) : 0.0;
            double pitchVelocity = haveAim ? aimPitch - lastAimPitch : 0.0;
            Features.build(yawError, pitchError, prevYawDelta, prevPitchDelta, yawVelocity, pitchVelocity,
                    distance, size, player.getAttackStrengthScale(0.5F), features);
            runner.step(features, output);
            yawDelta = Features.outputToDelta(output[0] + random.nextGaussian() * model.noiseStd[0] * NOISE_FACTOR);
            pitchDelta = Features.outputToDelta(output[1] + random.nextGaussian() * model.noiseStd[1] * NOISE_FACTOR);
        } else {
            yawDelta = yawError * FALLBACK_YAW_GAIN;
            pitchDelta = pitchError * FALLBACK_PITCH_GAIN;
        }
        yawDelta = Mth.clamp(yawDelta, -MAX_YAW_STEP, MAX_YAW_STEP) * scale;
        pitchDelta = Mth.clamp(pitchDelta, -MAX_PITCH_STEP, MAX_PITCH_STEP) * scale;

        float newYaw = yaw + quantize(0, (float) yawDelta);
        float newPitch = Mth.clamp(pitch + quantize(1, (float) pitchDelta), -89.0F, 89.0F);

        // Feed the change that was actually applied back as the next "previous delta", in per-tick units.
        prevYawDelta = Mth.wrapDegrees(newYaw - yaw) / scale;
        prevPitchDelta = (newPitch - pitch) / scale;
        lastAimYaw = aimYaw;
        lastAimPitch = aimPitch;
        haveAim = true;
        return new Rotations(Mth.wrapDegrees(newYaw), newPitch);
    }

    /** Snaps a movement to whole mouse counts, carrying the remainder to the next call. */
    private float quantize(int axis, float desired) {
        float sensitivity = MINECRAFT.options.sensitivity().get().floatValue() * 0.6F + 0.2F;
        float gridStep = sensitivity * sensitivity * sensitivity * 1.2F;
        if (!(gridStep > 1.0E-4F)) {
            return desired;
        }
        float pending = desired + residual[axis];
        long counts = Math.round(pending / gridStep);
        float applied = counts * gridStep;
        residual[axis] = pending - applied;
        return applied;
    }

    private void begin(LivingEntity entity) {
        tracked = entity;
        trackedAny = true;
        haveAim = false;
        prevYawDelta = 0;
        prevPitchDelta = 0;
        residual[0] = 0;
        residual[1] = 0;
        if (runner != null) {
            runner.reset();
        }
    }

    @Override
    public void m_1_void() {
        trackedAny = false;
        tracked = null;
        haveAim = false;
        prevYawDelta = 0;
        prevPitchDelta = 0;
        residual[0] = 0;
        residual[1] = 0;
        if (runner != null) {
            runner.reset();
        }
    }

    /** Sensitivity snapping is done in {@link #quantize}; the manager must not round the result again. */
    @Override
    public boolean m_6_boolean() {
        return true;
    }
}
