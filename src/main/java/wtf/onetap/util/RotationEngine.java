package wtf.onetap.util;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class RotationEngine {
    public static final double STEP = 0.004166666666666667;
    private static final double YAW_SPEED_MIN = 36.0;
    private static final double YAW_SPEED_MAX = 52.0;
    private static final double PITCH_SPEED_MIN = 3.0;
    private static final double PITCH_SPEED_MAX = 12.0;
    private static final double CORE_SPEED_MIN = 1.0;
    private static final double CORE_SPEED_MAX = 4.0;
    private static final double YAW_RAGGED = 7.0;
    private static final double PITCH_RAGGED = 2.0;
    private static final double YAW_JITTER = 0.05;
    private static final double PITCH_JITTER = 0.02;
    private static final double JITTER_MOTION_REF = 3.0;
    private static final double MICRO_MIN = 0.024523435;
    private static final double MICRO_MAX = 0.0454645636;
    private static final double MICRO_LIMIT = 0.1;
    private static final double YAW_RAMP_STEPS = 29.0;
    private static final double FOV_RADIUS = 35.0;
    private static final double OFFSCREEN_YAW_SPEED = 50.0;
    private static final double OFFSCREEN_RESPONSE = 0.4;
    private static final double UNIQUE_EPS = 1.0E-4;

    private double yaw;
    private double pitch;
    private double yawSpeed;
    private double pitchSpeed;
    private double yawTargetSpeed;
    private double pitchTargetSpeed;
    private double lastYawRoll = Double.NaN;
    private double lastPitchRoll = Double.NaN;
    private double lastYawTickSpeed = Double.NaN;
    private double lastPitchTickSpeed = Double.NaN;
    private double yawJitter;
    private double yawJitterTarget;
    private double pitchJitter;
    private double jitterMotion;
    private double microYaw;
    private double microPitch;
    private double modeElapsed;
    private double yawRampElapsed;
    private double frameAccumulator;
    private long lastNanos;
    private long stepCounter;
    private boolean seeded;
    private boolean core;
    private boolean wasCore;
    private boolean locked;
    private boolean wasLocked;
    private double maxDistance = Double.POSITIVE_INFINITY;

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance > 0.0 ? maxDistance : Double.POSITIVE_INFINITY;
    }

    public void seed(float yaw, float pitch, long nanos) {
        this.reset();
        this.yaw = yaw;
        this.pitch = clamp(pitch, -90.0, 90.0);
        this.lastNanos = nanos;
        this.seeded = true;
    }

    public void reset() {
        this.yawSpeed = this.pitchSpeed = 0.0;
        this.yawTargetSpeed = this.pitchTargetSpeed = 0.0;
        this.lastYawRoll = this.lastPitchRoll = Double.NaN;
        this.lastYawTickSpeed = this.lastPitchTickSpeed = Double.NaN;
        this.yawJitter = this.yawJitterTarget = this.pitchJitter = this.jitterMotion = 0.0;
        this.microYaw = this.microPitch = 0.0;
        this.modeElapsed = 0.0;
        this.yawRampElapsed = 0.0;
        this.frameAccumulator = 0.0;
        this.stepCounter = 0L;
        this.seeded = false;
        this.core = this.wasCore = false;
        this.locked = this.wasLocked = false;
    }

    public void update(Vec3 eye, AABB box, long nanos) {
        if (!this.seeded) {
            this.seed((float) this.yaw, (float) this.pitch, nanos);
            return;
        }

        double dt = (nanos - this.lastNanos) * 1.0E-9;
        this.lastNanos = nanos;
        if (dt < 0.0 || dt > 0.25) {
            this.frameAccumulator = 0.0;
            return;
        }

        this.frameAccumulator += dt;
        while (this.frameAccumulator + 1.0E-9 >= STEP) {
            this.step(eye, box);
            this.frameAccumulator -= STEP;
        }
    }

    private void step(Vec3 eye, AABB box) {
        this.stepCounter++;
        this.updateVisibility(eye, box);
        if (this.core != this.wasCore || this.locked != this.wasLocked) {
            this.modeElapsed = 0.0;
            this.yawRampElapsed = 0.0;
            this.wasCore = this.core;
            this.wasLocked = this.locked;
        }
        this.modeElapsed++;
        this.yawRampElapsed++;

        Vec3 aim = box.getCenter();
        Vec3 delta = aim.subtract(eye);
        double horizontal = delta.horizontalDistance();
        double targetYaw = Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0;
        double targetPitch = clamp(-Math.toDegrees(Math.atan2(delta.y, horizontal)), -90.0, 90.0);
        double yawDiff = wrapDegrees(targetYaw - this.yaw);
        double pitchDiff = targetPitch - this.pitch;
        double error = Math.hypot(yawDiff, pitchDiff);
        double curve = Easings.EASE_IN_OUT_CUBIC.apply(Math.min(1.0, error / 45.0));
        boolean offFov = error > FOV_RADIUS;

        boolean newTick = this.stepCounter % 12L == 0L;
        if (newTick || Double.isNaN(this.yawTargetSpeed)) {
            this.reroll();
        }

        if (newTick) {
            this.yawSpeed += ragged(YAW_RAGGED);
            this.pitchSpeed += ragged(PITCH_RAGGED);
        }

        double rampFactor = smoothstep(this.modeElapsed / (this.core ? 8.0 : 42.0));
        double response = (this.core ? 0.06 : 0.012) + (this.core ? 0.34 : 0.11) * rampFactor;
        double speedScale = 0.55 + 0.75 * curve;
        double minYaw = this.core ? 0.5 : 2.0;
        double minPitch = 0.5;
        double yawRamp = this.core ? 0.15 + 0.85 * smoothstep(this.yawRampElapsed / YAW_RAMP_STEPS) : 1.0;
        double maxYaw = Math.min(this.yawAccelLimit(1.35) * speedScale, YAW_SPEED_MAX) * yawRamp;
        double maxPitch = this.pitchAccelLimit(1.35) * speedScale;
        double yawTarget = offFov
                ? OFFSCREEN_YAW_SPEED + (this.yawTargetSpeed - (YAW_SPEED_MIN + YAW_SPEED_MAX) * 0.5) * 0.3
                : this.yawTargetSpeed * speedScale * yawRamp;
        double yawLimit = offFov ? YAW_SPEED_MAX : maxYaw;
        this.yawSpeed = integrate(this.yawSpeed, yawTarget, minYaw, yawLimit, offFov ? OFFSCREEN_RESPONSE : response);
        this.pitchSpeed = integrate(this.pitchSpeed, this.pitchTargetSpeed * speedScale, minPitch, maxPitch, response);

        if (newTick) {
            this.yawSpeed = offFov ? this.yawSpeed : uniqueValue(this.yawSpeed, this.lastYawTickSpeed, minYaw, maxYaw);
            this.pitchSpeed = uniqueValue(this.pitchSpeed, this.lastPitchTickSpeed, minPitch, maxPitch);
            this.lastYawTickSpeed = this.yawSpeed;
            this.lastPitchTickSpeed = this.pitchSpeed;
        }

        double prevYaw = this.yaw;
        double prevPitch = this.pitch;
        double tickPart = STEP * 20.0;
        double stepScale = 0.6 + 0.4 * curve;
        double yawStep = this.locked ? 0.0 : this.yawSpeed * tickPart * stepScale;
        double pitchStep = this.locked ? 0.0 : this.pitchSpeed * tickPart * stepScale;
        this.yaw += Math.copySign(Math.min(Math.abs(yawDiff), yawStep), yawDiff);
        this.pitch = clamp(this.pitch + Math.copySign(Math.min(Math.abs(pitchDiff), pitchStep), pitchDiff), -90.0, 90.0);

        double movement = Math.hypot(this.yaw - prevYaw, this.pitch - prevPitch);
        double motionTarget = clamp(movement / JITTER_MOTION_REF, 0.0, 1.0);
        this.jitterMotion += (motionTarget - this.jitterMotion) * (motionTarget > this.jitterMotion ? 0.06 : 0.35);
        this.yawJitterTarget = random(-YAW_JITTER, YAW_JITTER);
        this.yawJitter += (this.yawJitterTarget - this.yawJitter) * 0.5;
        this.pitchJitter = random(-PITCH_JITTER, PITCH_JITTER);

        if (!this.locked && movement > 0.0) {
            this.microYaw = microStep(this.microYaw);
            this.microPitch = microStep(this.microPitch);
        }
    }

    private void reroll() {
        if (this.core) {
            this.yawTargetSpeed = uniqueRandom(YAW_SPEED_MIN, YAW_SPEED_MAX, this.lastYawRoll);
            this.pitchTargetSpeed = uniqueRandom(CORE_SPEED_MIN, CORE_SPEED_MAX, this.lastPitchRoll);
        } else {
            this.yawTargetSpeed = uniqueRandom(YAW_SPEED_MIN, YAW_SPEED_MAX, this.lastYawRoll);
            this.pitchTargetSpeed = uniqueRandom(PITCH_SPEED_MIN, PITCH_SPEED_MAX, this.lastPitchRoll);
        }
        this.lastYawRoll = this.yawTargetSpeed;
        this.lastPitchRoll = this.pitchTargetSpeed;
    }

    private void updateVisibility(Vec3 eye, AABB box) {
        double pitchRad = Math.toRadians(this.pitch);
        double yawRad = Math.toRadians(this.yaw);
        Vec3 look = new Vec3(
                -Math.sin(yawRad) * Math.cos(pitchRad),
                -Math.sin(pitchRad),
                Math.cos(yawRad) * Math.cos(pitchRad)
        );
        double reach = Math.min(this.maxDistance, eye.distanceTo(box.getCenter()) + 4.0);
        Vec3 far = eye.add(look.scale(reach));
        Vec3 center = box.getCenter();
        double halfWidth = box.getXsize() * 0.3;
        double halfDepth = box.getZsize() * 0.3;
        AABB coreBox = new AABB(
                center.x - halfWidth,
                box.minY,
                center.z - halfDepth,
                center.x + halfWidth,
                box.maxY,
                center.z + halfDepth
        );
        boolean onHitbox = box.contains(eye) || box.clip(eye, far).isPresent();
        this.locked = coreBox.contains(eye) || coreBox.clip(eye, far).isPresent();
        this.core = onHitbox && !this.locked;
    }

    private double yawAccelLimit(double factor) {
        return YAW_SPEED_MAX * factor;
    }

    private double pitchAccelLimit(double factor) {
        return (this.core ? CORE_SPEED_MAX : PITCH_SPEED_MAX) * factor;
    }

    private static double integrate(double current, double target, double min, double max, double response) {
        return clamp(current + (target - current) * response, min, max);
    }

    private static double uniqueRandom(double min, double max, double previous) {
        double value = random(min, max);
        int attempts = 0;
        while (!Double.isNaN(previous) && Math.abs(value - previous) < UNIQUE_EPS && attempts++ < 24) {
            value = random(min, max);
        }
        if (!Double.isNaN(previous) && Math.abs(value - previous) < UNIQUE_EPS) {
            value = clamp(previous + (previous < max ? UNIQUE_EPS * 10.0 : -UNIQUE_EPS * 10.0), min, max);
        }
        return value;
    }

    private static double uniqueValue(double value, double previous, double min, double max) {
        if (Double.isNaN(previous) || Math.abs(value - previous) >= UNIQUE_EPS) {
            return value;
        }
        return clamp(value + (value < max ? UNIQUE_EPS * 10.0 : -UNIQUE_EPS * 10.0), min, max);
    }

    private static double ragged(double limit) {
        double value = ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
        return Math.copySign(Math.pow(Math.abs(value), 2.2) * limit, value);
    }

    private static double microStep(double current) {
        double magnitude = random(MICRO_MIN, MICRO_MAX);
        double next = current + (ThreadLocalRandom.current().nextBoolean() ? magnitude : -magnitude);
        if (next > MICRO_LIMIT) {
            next = MICRO_LIMIT - (next - MICRO_LIMIT);
        } else if (next < -MICRO_LIMIT) {
            next = -MICRO_LIMIT - (next + MICRO_LIMIT);
        }
        return next;
    }

    private static double random(double min, double max) {
        return min + ThreadLocalRandom.current().nextDouble(max - min);
    }

    private static double smoothstep(double value) {
        value = clamp(value, 0.0, 1.0);
        return value * value * (3.0 - 2.0 * value);
    }

    private static double wrapDegrees(double value) {
        return value - Math.floor((value + 180.0) / 360.0) * 360.0;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public float getYaw() {
        return (float) (this.yaw + (this.yawJitter + this.microYaw) * this.jitterMotion);
    }

    public float getPitch() {
        return (float) clamp(this.pitch + (this.pitchJitter + this.microPitch) * this.jitterMotion, -90.0, 90.0);
    }
}
