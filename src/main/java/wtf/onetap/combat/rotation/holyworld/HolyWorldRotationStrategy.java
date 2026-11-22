package dev.leg1tka.rotation.holyworld;

import dev.leg1tka.modules.combat.AttackAura;
import dev.leg1tka.rotation.Rotation;
import dev.leg1tka.rotation.RotationContext;
import dev.leg1tka.rotation.RotationStorage;
import dev.leg1tka.rotation.RotationStrategy;
import java.util.Random;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

/**
 * Aura rotation driven by a GRU that was trained inside the game on the player's own mouse tracking.
 *
 * The network runs once per client tick (the rate it was recorded at) and answers with the view change for that
 * tick; between ticks the rotation is interpolated linearly so the picture stays smooth at the game's call rate.
 * Hidden state is reset on a new target. Noise with the level measured on the training data is added to each
 * output. Without a trained model a plain proportional controller is used, so the mode still works before training.
 */
public class HolyWorldRotationStrategy extends RotationStrategy {
    private static final long TICK_NANOS = 50_000_000L;
    private static final long STALE_NANOS = 250_000_000L;
    private static final double NOISE_FACTOR = 0.8;
    private static final float MAX_YAW_STEP = 40.0F;
    private static final float MAX_PITCH_STEP = 20.0F;
    private static final float FALLBACK_YAW_GAIN = 0.30F;
    private static final float FALLBACK_PITCH_GAIN = 0.25F;
    private static final float APPLY_SPEED = 90.0F;

    private final Random random = new Random();
    private final double[] features = new double[Features.N_IN];
    private final double[] output = new double[Features.N_OUT];

    private LivingEntity current;
    private GruNet runnerModel;
    private GruNet.Runner runner;
    private long lastCallNanos;
    private int lastAge = Integer.MIN_VALUE;
    private float appliedYaw;
    private float appliedPitch;
    private float startYaw;
    private float startPitch;
    private float endYaw;
    private float endPitch;
    private long startNanos;
    private double prevYawDelta;
    private double prevPitchDelta;
    private float lastAimYaw;
    private float lastAimPitch;
    private boolean haveAim;

    public void reset() {
        current = null;
        lastAge = Integer.MIN_VALUE;
        haveAim = false;
        prevYawDelta = 0;
        prevPitchDelta = 0;
        if (runner != null) {
            runner.reset();
        }
    }

    @Override
    public void a(LivingEntity target) {
        ClientPlayerEntity player = minecraftClient.player;
        if (player == null || target == null) {
            return;
        }
        long now = System.nanoTime();
        if (target != current || now - lastCallNanos > STALE_NANOS) {
            begin(player, target);
        }
        lastCallNanos = now;

        GruNet model = HolyWorldBrain.INSTANCE.model();
        if (model != runnerModel) {
            runnerModel = model;
            runner = model == null ? null : model.runner();
        }

        int age = player.age;
        if (age != lastAge) {
            lastAge = age;
            stepTick(player, target, model, now);
        }
        apply(now, target);
    }

    private void begin(ClientPlayerEntity player, LivingEntity target) {
        current = target;
        appliedYaw = player.getYaw();
        appliedPitch = player.getPitch();
        startYaw = endYaw = appliedYaw;
        startPitch = endPitch = appliedPitch;
        startNanos = System.nanoTime();
        lastAge = Integer.MIN_VALUE;
        haveAim = false;
        prevYawDelta = 0;
        prevPitchDelta = 0;
        if (runner != null) {
            runner.reset();
        }
    }

    private void stepTick(ClientPlayerEntity player, LivingEntity target, GruNet model, long now) {
        HolyWorldAim.Angles aim = HolyWorldAim.angles(player, target);
        float yawError = MathHelper.wrapDegrees(aim.yaw - appliedYaw);
        float pitchError = aim.pitch - appliedPitch;

        double yawDelta;
        double pitchDelta;
        if (model != null && runner != null) {
            double yawVelocity = haveAim ? MathHelper.wrapDegrees(aim.yaw - lastAimYaw) : 0.0;
            double pitchVelocity = haveAim ? aim.pitch - lastAimPitch : 0.0;
            Features.build(yawError, pitchError, prevYawDelta, prevPitchDelta, yawVelocity, pitchVelocity,
                    aim.distance, aim.sizeDegrees, player.getAttackCooldownProgress(0.5F), features);
            runner.step(features, output);
            yawDelta = Features.outputToDelta(output[0] + random.nextGaussian() * model.noiseStd[0] * NOISE_FACTOR);
            pitchDelta = Features.outputToDelta(output[1] + random.nextGaussian() * model.noiseStd[1] * NOISE_FACTOR);
        } else {
            yawDelta = yawError * FALLBACK_YAW_GAIN;
            pitchDelta = pitchError * FALLBACK_PITCH_GAIN;
        }
        yawDelta = MathHelper.clamp(yawDelta, -MAX_YAW_STEP, MAX_YAW_STEP);
        pitchDelta = MathHelper.clamp(pitchDelta, -MAX_PITCH_STEP, MAX_PITCH_STEP);

        startYaw = appliedYaw;
        startPitch = appliedPitch;
        endYaw = startYaw + (float) yawDelta;
        endPitch = MathHelper.clamp(startPitch + (float) pitchDelta, -89.0F, 89.0F);
        startNanos = now;
        prevYawDelta = yawDelta;
        prevPitchDelta = endPitch - startPitch;
        lastAimYaw = aim.yaw;
        lastAimPitch = aim.pitch;
        haveAim = true;
    }

    private void apply(long now, LivingEntity target) {
        float alpha = MathHelper.clamp((now - startNanos) / (float) TICK_NANOS, 0.0F, 1.0F);
        float yaw = startYaw + (endYaw - startYaw) * alpha;
        float pitch = startPitch + (endPitch - startPitch) * alpha;

        // Snap to the mouse sensitivity grid relative to what was applied last, like real mouse counts.
        float step = RotationContext.b();
        if (step > 0.0F) {
            yaw = appliedYaw + Math.round((yaw - appliedYaw) / step) * step;
            pitch = appliedPitch + Math.round((pitch - appliedPitch) / step) * step;
        }
        pitch = MathHelper.clamp(pitch, -89.0F, 89.0F);

        Rotation rotation = new Rotation(MathHelper.wrapDegrees(yaw), pitch);
        RotationStorage.a(rotation, APPLY_SPEED, APPLY_SPEED, APPLY_SPEED, APPLY_SPEED, 0, 1, AttackAura.I.getValue());
        this.e = new Vec2f(rotation.getYaw(), rotation.getPitch());
        appliedYaw = yaw;
        appliedPitch = pitch;
    }
}
