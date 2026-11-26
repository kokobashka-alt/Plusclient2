package wtf.onetap.combat.rotation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

public final class ShardbetaRotation {
    public record Rotation(float yaw, float pitch) {}

    private static final double EDGE = 0.78;
    private static final double EDGE_RAND = 0.05;
    private static final double PREDICT = 0.6;
    private static final double PREDICT_RAND = 0.1;
    private static final double SPEED_RAND = 0.16;

    private static final float MAX_YAW = 15.0f;
    private static final float MAX_PITCH = 9.0f;
    private static final float MIN_STEP = 0.2f;
    private static final double F_MIN = 0.15;
    private static final double F_MAX = 0.5;
    private static final float CAP_RAND = 0.15f;
    private static final float EASE = 0.65f;
    private static final float ACCEL = 7.0f;
    private static final double SKIP_CHANCE = 0.05;

    private static final float JIT_YAW = 0.26f;
    private static final float JIT_PITCH = 0.16f;
    private static final float JIT_FADE = 9.0f;
    private static final float BEZIER = 1.0f;
    private static final float OFF_PITCH = -1.0f;
    private static final float OFF_YAW_RAND = 1.2f;
    private static final float OFF_PITCH_RAND = 0.8f;
    private static final int OFF_EVERY = 69;

    private static final int HOLD_MIN = 3;
    private static final int HOLD_MAX = 6;
    private static final float HOLD_ANGLE = 8.0f;
    private static final double MIN_Y = 0.45;
    private static final double MAX_Y = 0.94;
    private static final double OV_CHANCE = 0.24;
    private static final float OV_DEG = 1.2f;

    // {yFrac, right, forward}
    private static final double[][] P = {
        {0.784, -0.375, 0.192},
        {0.866, -0.201, -0.029},
        {0.802, -0.473, -0.106},
        {0.747, -0.07, 0.221},
        {0.785, 0.539, -0.024},
        {0.528, -0.3, 0.19},
        {0.694, -0.123, 0.256},
        {0.835, -0.407, 0.127},
        {0.873, -0.084, 0.082},
        {0.683, 0.081, 0.241},
        {0.561, -0.035, 0.14},
        {0.754, 0.257, -0.229},
        {0.711, -0.006, -0.155},
        {0.61, -0.176, -0.035},
        {0.702, 0.148, -0.191},
        {0.705, 0.343, -0.099},
        {0.899, -0.044, 0.009},
        {0.57, 0.185, 0.054},
        {0.602, 0.113, 0.114},
        {0.767, -0.254, -0.201},
        {0.742, 0.326, 0.007},
        {0.752, 0.498, -0.229},
        {0.588, -0.006, 0.07},
        {0.832, -0.289, 0.008},
        {0.778, 0.182, -0.108},
        {0.765, 0.087, -0.25}
    };

    private LivingEntity last;
    private int id = -1;
    private int hold;
    private final ArrayDeque<Integer> bag = new ArrayDeque<>();
    private double aggression = 1.0;
    private double edge = EDGE;
    private double predict = PREDICT;
    private double speed = 1.0;
    private int age;
    private float prevYaw;
    private float prevPitch;
    private float ovYaw;
    private float ovPitch;
    private float bz;
    private float driftYaw;
    private float driftPitch;
    private float driftYawTo;
    private float driftPitchTo;
    private int driftIn;

    public void setAggression(double a) {
        aggression = Math.max(0.05, a);
    }

    public void reset() {
        last = null;
        id = -1;
        hold = 0;
        bag.clear();
        age = 0;
        prevYaw = 0f;
        prevPitch = 0f;
        ovYaw = 0f;
        ovPitch = 0f;
        bz = 0f;
        driftYaw = 0f;
        driftPitch = 0f;
        driftYawTo = 0f;
        driftPitchTo = 0f;
        driftIn = 0;
    }

    public Rotation update(LivingEntity target, float yaw, float pitch, double range) {
        return update(target, null, yaw, pitch, range);
    }

    public Rotation update(LivingEntity target, AABB customBox, float yaw, float pitch, double range) {
        Minecraft mc = MinecraftAccess.MINECRAFT;
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || target == null) {
            reset();
            return new Rotation(yaw, pitch);
        }

        ThreadLocalRandom r = ThreadLocalRandom.current();
        if (target != last) {
            reset();
            last = target;
            edge = Mth.clamp(EDGE + rand(r, EDGE_RAND), 0.3, 1.0);
            predict = Math.max(0.0, PREDICT + rand(r, PREDICT_RAND));
            speed = 1.0 + rand(r, SPEED_RAND);
        }

        Vec3 eye = player.getEyePosition();
        Vec3 vel = new Vec3(target.getX() - target.xo, 0.0, target.getZ() - target.zo).scale(predict);
        AABB box = customBox != null ? customBox : target.getBoundingBox();

        if (id >= 0 && !visible(player, eye, at(target, box, id, vel), range)) {
            id = -1;
        }
        if (id >= 0 && hold <= 0 && error(eye, target, box, vel, yaw, pitch) > HOLD_ANGLE) {
            hold = 1;
        }

        boolean switched = id < 0 || hold <= 0;
        if (switched) {
            id = next(player, target, box, eye, vel, range);
            hold = r.nextInt(HOLD_MIN, HOLD_MAX + 1);
            age = 0;
        }
        if (id < 0) {
            return new Rotation(yaw, pitch);
        }

        hold--;
        age++;
        drift(r);

        float[] aim = ang(eye, at(target, box, id, vel));
        float dy = Mth.wrapDegrees(aim[0] + driftYaw - yaw);
        float dp = aim[1] + OFF_PITCH + driftPitch - pitch;

        if (switched && r.nextDouble() < OV_CHANCE) {
            ovYaw = Math.signum(dy) * OV_DEG * (float) r.nextDouble(0.5, 1.0);
            ovPitch = Math.signum(dp) * OV_DEG * (float) r.nextDouble(0.25, 0.5);
        }
        dy += ovYaw;
        dp += ovPitch;
        ovYaw *= 0.5f;
        ovPitch *= 0.5f;

        double dist = Math.hypot(dy, dp);
        double f = F_MIN + (F_MAX - F_MIN) * Math.min(dist / 90.0, 1.0);
        f = Math.min(1.0, f * speed);

        float sy = step(dy, f, MAX_YAW * (float) aggression, r);
        float sp = step(dp, f, MAX_PITCH * (float) aggression, r);
        float ramp = 1.0f - EASE * Math.max(0.0f, 1.0f - age / 3.0f);
        sy *= ramp;
        sp *= ramp;

        float fade = (float) Math.min(1.0, dist / JIT_FADE);
        sy += rand(r, JIT_YAW * fade);
        sp += rand(r, JIT_PITCH * fade);

        if (switched) {
            bz = (r.nextBoolean() ? 1.0f : -1.0f) * BEZIER;
        }
        bz *= 0.85f;
        float bf = (float) Math.min(1.0, dist / 60.0);
        sy += bz * bf * 2.0f;
        sp -= bz * bf;

        sy = Mth.clamp(sy, prevYaw - ACCEL, prevYaw + ACCEL);
        sp = Mth.clamp(sp, prevPitch - ACCEL, prevPitch + ACCEL);

        if (r.nextDouble() < SKIP_CHANCE) {
            sy = 0.0f;
            sp = 0.0f;
        }

        double k = mc.options.sensitivity().get() * 0.6 + 0.2;
        double q = k * k * k * 8.0 * 0.15;
        sy = (float) (Math.round(sy / q) * q);
        sp = (float) (Math.round(sp / q) * q);

        prevYaw = sy;
        prevPitch = sp;

        return new Rotation(yaw + sy, Mth.clamp(pitch + sp, -90.0f, 90.0f));
    }

    public Rotations updateAsRotations(LivingEntity target, AABB customBox, float yaw, float pitch, double range) {
        Rotation r = update(target, customBox, yaw, pitch, range);
        return new Rotations(r.yaw(), r.pitch());
    }

    private int next(LocalPlayer player, LivingEntity t, AABB box, Vec3 eye, Vec3 vel, double range) {
        for (int pass = 0; pass < 2; pass++) {
            while (!bag.isEmpty()) {
                int i = bag.poll();
                if (i != id && visible(player, eye, at(t, box, i, vel), range)) {
                    return i;
                }
            }
            List<Integer> all = new ArrayList<>();
            for (int i = 0; i < P.length; i++) {
                if (P[i][0] < MIN_Y || P[i][0] > MAX_Y) continue;
                if (visible(player, eye, at(t, box, i, vel), range)) {
                    all.add(i);
                }
            }
            Collections.shuffle(all, ThreadLocalRandom.current());
            bag.addAll(all);
        }
        return id >= 0 && visible(player, eye, at(t, box, id, vel), range) ? id : -1;
    }

    private static float rand(ThreadLocalRandom r, double range) {
        return (float) ((r.nextDouble() * 2.0 - 1.0) * range);
    }

    private void drift(ThreadLocalRandom r) {
        if (--driftIn <= 0) {
            driftIn = (int) Math.round(OFF_EVERY * r.nextDouble(0.7, 1.3));
            driftYawTo = rand(r, OFF_YAW_RAND);
            driftPitchTo = rand(r, OFF_PITCH_RAND);
        }
        driftYaw += (driftYawTo - driftYaw) * 0.08f;
        driftPitch += (driftPitchTo - driftPitch) * 0.08f;
    }

    private double error(Vec3 eye, LivingEntity t, AABB box, Vec3 vel, float yaw, float pitch) {
        float[] aim = ang(eye, at(t, box, id, vel));
        return Math.hypot(Mth.wrapDegrees(aim[0] - yaw), aim[1] - pitch);
    }

    private static float step(float d, double f, float cap, ThreadLocalRandom r) {
        float limit = cap * (1.0f + CAP_RAND * rand(r, 1.0));
        float s = Mth.clamp((float) (d * f), -limit, limit);
        if (Math.abs(d) <= MIN_STEP) return d;
        return Math.abs(s) < MIN_STEP ? Math.copySign(MIN_STEP, d) : s;
    }

    private Vec3 at(LivingEntity t, AABB b, int i, Vec3 vel) {
        double half = b.getXsize() * 0.5 * edge;
        double yawRad = Math.toRadians(t.yBodyRot);
        Vec3 center = b.getCenter();
        double x = center.x + (-Math.cos(yawRad) * P[i][1] - Math.sin(yawRad) * P[i][2]) * half;
        double z = center.z + (-Math.sin(yawRad) * P[i][1] + Math.cos(yawRad) * P[i][2]) * half;
        return new Vec3(x, b.minY + b.getYsize() * P[i][0], z).add(vel);
    }

    private static boolean visible(LocalPlayer player, Vec3 from, Vec3 to, double range) {
        if (from.distanceTo(to) > range) return false;
        Minecraft mc = MinecraftAccess.MINECRAFT;
        if (mc.level == null) return false;
        ClipContext ctx = new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
        return mc.level.clip(ctx).getType() == HitResult.Type.MISS;
    }

    private static float[] ang(Vec3 from, Vec3 to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        float yaw = Mth.wrapDegrees((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
        float pitch = Mth.clamp((float) -Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz))), -90.0f, 90.0f);
        return new float[]{yaw, pitch};
    }
}
