package wtf.onetap.util.rotation;

import java.util.ArrayDeque;
import java.util.Random;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import wtf.onetap.Onetap;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

/**
 * UltraShard — человеко-подобная ротация под Shard AC / ML античиты.
 *
 * <p>В отличие от классических «повернуться к цели» ротаций, эта модель
 * воспроизводит поведение живого игрока:
 *
 * <ul>
 *   <li><b>Reaction latency (80 мс):</b> при появлении новой цели или
 *       смещении цели прицел не «телепортируется», а ждёт ~80 мс,
 *       как настоящая реакция человека.</li>
 *   <li><b>Knockback lag (150 мс):</b> если цель резко сдвинулась (удар,
 *       отброс, отпрыжок), прицел НЕ следует мгновенно за ней — он
 *       остаётся на старой позиции указанное время. Это самый частый
 *       паттерн, который ML AC классифицирует как «не человек».</li>
 *   <li><b>Constant drift:</b> даже когда прицел точно на цели, он
 *       продолжает плавать по трём синусоидам + случайному блужданию
 *       (Brownian). Никогда не замирает.</li>
 *   <li><b>GCD quantization:</b> каждый шаг кратен GCD мыши
 *       (sensitivity-based). Плюс ±1–2 счётчика шума из Brownian.</li>
 *   <li><b>Focus variation:</b> каждые 15–30 тиков точка прицеливания
 *       внутри хитбокса переезжает — центр → край → лид → лаг. Как
 *       будто игрок «перефокусируется».</li>
 *   <li><b>Deceleration:</b> при сближении с целью шаг замедляется
 *       пропорционально ошибке (до 0.15×), имитируя ручное доведение.</li>
 *   <li><b>Acquire vs Track:</b> при новой цели gain = 0.70 (быстрый
 *       поворот), в установившемся режиме gain = 0.55 (плавно).</li>
 * </ul>
 *
 * <p>Не «замирает» никогда: если квантованный шаг вышел 0, но ошибка
 * есть — принудительно выдаётся 1 счётчик в сторону ошибки. Если ошибки
 * нет — Brownian через силу выдаст ±1 счётчик в случайную сторону.
 *
 * <p>Класс не зависит от конкретного модуля — принимает {@link LivingEntity}
 * и возвращает {@link Rotations}. Вызывающий код сам решает, отдавать ли
 * её в {@code Onetap.ROTATION_MODES.rotate(...)} или применить локально.
 */
public final class UltraShardRotation {

    // =================================================================
    //  Настройки (публичные — можно менять извне)
    // =================================================================

    /** Задержка реакции на новую цель и на смещение цели, мс. */
    public double reactionDelayMs = 80.0;

    /** Задержка игнорирования резкого движения цели (knockback), мс. */
    public double knockbackDelayMs = 150.0;

    /** Порог скорости цели, выше которого срабатывает knockback, бл/тик. */
    public double knockbackVelocityThreshold = 0.25;

    /** Gain при первой реакции на цель (быстрый поворот). */
    public double acquireGain = 0.70;

    /** Gain в установившемся режиме. */
    public double trackGain = 0.55;

    /** Максимальный шаг по yaw за один вызов, градусы. */
    public double maxYawPerTick = 22.0;

    /** Максимальный шаг по pitch за один вызов, градусы. */
    public double maxPitchPerTick = 12.0;

    /** Ошибка, ниже которой начинается замедление, градусы. */
    public double decelStartError = 8.0;

    /** Минимальный коэффициент замедления (никогда не 0). */
    public double decelMinFactor = 0.15;

    /** Базовая амплитуда дрейфа, градусы. */
    public double driftAmplitude = 0.22;

    /** Минимальная амплитуда дрейфа в GCD-счётчиках (2 = 2 мышиных count). */
    public double driftGcdMin = 2.0;

    /** Период между сменами focus point, тиков. */
    public int focusIntervalMin = 15;
    public int focusIntervalMax = 30;

    /** Шанс, что следующий focus point будет «лид» по скорости цели. */
    public double leadChance = 0.15;

    // =================================================================
    //  Константы сессии (детерминированные, но уникальные на запуск)
    // =================================================================

    private static final double TWO_PI = Math.PI * 2;

    private final long sessionStartNanos = System.nanoTime();
    private final Random rng;
    private final double freqX, freqY, freqZ;
    private final double phaseX, phaseY, phaseZ;

    // =================================================================
    //  Runtime state
    // =================================================================

    private final ArrayDeque<AimSnapshot> history = new ArrayDeque<>();
    /** Накопленная дробная часть шага после квантования [yaw, pitch]. */
    private final double[] residual = new double[2];
    /** Знаковый случайный процесс для «минимального движения» [yaw, pitch]. */
    private final double[] brownian = new double[2];

    private Vec3 focusTarget = Vec3.ZERO;
    private Vec3 focusCurrent = Vec3.ZERO;
    private int focusTimer = 20;

    private Vec3 lastTargetCenter = Vec3.ZERO;
    private long lastTargetNanos;

    private int lastTargetId = -1;
    private boolean hasTarget;
    private boolean acquiring;
    private long acquireStartNanos;

    private boolean knockbackActive;
    private long knockbackStartNanos;
    private Vec3 knockbackHoldPos = Vec3.ZERO;

    private int tickCounter;

    // =================================================================
    //  Constructor
    // =================================================================

    public UltraShardRotation() {
        this.rng = new Random(System.nanoTime());
        // Частоты дрейфа 0.22–0.70 Hz — разные в каждой сессии
        this.freqX = 0.28 + rng.nextDouble() * 0.30;
        this.freqY = 0.35 + rng.nextDouble() * 0.35;
        this.freqZ = 0.22 + rng.nextDouble() * 0.28;
        // Случайные фазы, чтобы разные оси дрейфовали вразнобой
        this.phaseX = rng.nextDouble() * TWO_PI;
        this.phaseY = rng.nextDouble() * TWO_PI;
        this.phaseZ = rng.nextDouble() * TWO_PI;
    }

    // =================================================================
    //  Public API
    // =================================================================

    /** Полный сброс состояния. Вызывать при выключении модуля / дисконнекте. */
    public void reset() {
        history.clear();
        java.util.Arrays.fill(residual, 0.0);
        java.util.Arrays.fill(brownian, 0.0);
        focusTarget = Vec3.ZERO;
        focusCurrent = Vec3.ZERO;
        focusTimer = 20;
        lastTargetCenter = Vec3.ZERO;
        lastTargetNanos = 0L;
        lastTargetId = -1;
        hasTarget = false;
        acquiring = false;
        acquireStartNanos = 0L;
        knockbackActive = false;
        knockbackStartNanos = 0L;
        knockbackHoldPos = Vec3.ZERO;
        tickCounter = 0;
    }

    /**
     * Вычислить следующий шаг ротации.
     *
     * @param player текущий игрок
     * @param target цель (живая сущность)
     * @param nanos  {@code System.nanoTime()} текущего тика
     * @return готовый {@link Rotations}, который нужно отдать в
     *         {@code Onetap.ROTATION_MODES.rotate(rot, false, 15)}
     */
    public Rotations tick(LocalPlayer player, LivingEntity target, long nanos) {
        tickCounter++;

        AABB box = target.getBoundingBox();
        Vec3 center = box.getCenter();
        Vec3 eye = player.getEyePosition();

        // --- Новая цель: сброс + acquisition phase ---
        if (!hasTarget || target.getId() != lastTargetId) {
            resetForNewTarget(target.getId(), center, nanos);
            return new Rotations(player.getYRot(), player.getXRot());
        }

        // --- Детект knockback ---
        Vec3 delta = center.subtract(lastTargetCenter);
        double speed = delta.length();
        if (!knockbackActive && speed > knockbackVelocityThreshold) {
            knockbackActive = true;
            knockbackStartNanos = nanos;
            knockbackHoldPos = lastTargetCenter;
        }
        if (knockbackActive && (nanos - knockbackStartNanos) >= msToNanos(knockbackDelayMs)) {
            knockbackActive = false;
        }

        // --- Обновление истории ---
        lastTargetCenter = center;
        lastTargetNanos = nanos;
        history.addLast(new AimSnapshot(center, nanos));
        trimHistory(nanos);

        // --- Acquisition: ждём реакционный лаг, потом резко поворачиваем ---
        if (acquiring) {
            if ((nanos - acquireStartNanos) < msToNanos(reactionDelayMs)) {
                return new Rotations(player.getYRot(), player.getXRot());
            }
            acquiring = false;
        }

        // --- Базовая позиция цели (с учётом lag / knockback) ---
        Vec3 aimBase;
        if (knockbackActive) {
            aimBase = knockbackHoldPos;
        } else {
            aimBase = getDelayedPosition(nanos, reactionDelayMs);
        }

        // --- Focus point ---
        updateFocus(box, speed, delta, center);

        // --- Синусоидальный дрейф ---
        Vec3 drift = computeDrift(nanos);

        // --- Итоговая точка прицеливания ---
        Vec3 aimPoint = aimBase.add(focusCurrent).add(drift);
        aimPoint = clampLooseToBox(aimPoint, box, 1.2);

        // --- Желаемая ротация ---
        Rotations desired = new Rotations(eye, aimPoint);
        Rotations current = Onetap.ROTATION_MODES.toRotations();

        // --- Применение с GCD-квантовкой, gain, decel, Brownian ---
        double gain = acquiring ? acquireGain : trackGain;
        return applyStep(current, desired, gain);
    }

    /** Текущее значение focus-point (для отладки / визуализации). */
    public Vec3 focusPoint() {
        return focusCurrent;
    }

    /** Позиция, на которую бы указывал прицел в буфере задержки. */
    public Vec3 delayedTargetPoint() {
        return history.isEmpty() ? lastTargetCenter : history.peekFirst().pos;
    }

    // =================================================================
    //  Внутренняя логика
    // =================================================================

    private void resetForNewTarget(int targetId, Vec3 center, long nanos) {
        history.clear();
        java.util.Arrays.fill(residual, 0.0);
        java.util.Arrays.fill(brownian, 0.0);
        focusTarget = Vec3.ZERO;
        focusCurrent = Vec3.ZERO;
        focusTimer = 20;

        lastTargetId = targetId;
        hasTarget = true;
        acquiring = true;
        acquireStartNanos = nanos;

        lastTargetCenter = center;
        lastTargetNanos = nanos;
        knockbackActive = false;

        history.addLast(new AimSnapshot(center, nanos));
    }

    private void updateFocus(AABB box, double speed, Vec3 delta, Vec3 center) {
        if (--focusTimer > 0) {
            focusCurrent = focusCurrent.lerp(focusTarget, 0.08);
            return;
        }

        focusTimer = focusIntervalMin + rng.nextInt(focusIntervalMax - focusIntervalMin + 1);

        // Лид-стратегия: цель движется быстро и не в knockback
        if (!knockbackActive && speed > 0.08 && rng.nextDouble() < leadChance) {
            focusTarget = delta.scale(2.0);
        } else {
            // Случайная точка внутри хитбокса со смещением к краям
            double x = rng.nextDouble() * 2 - 1;
            double y = rng.nextDouble() * 2 - 1;
            double z = rng.nextDouble() * 2 - 1;
            focusTarget = new Vec3(
                    x * box.getXsize() * 0.35,
                    y * box.getYsize() * 0.30,
                    z * box.getZsize() * 0.35);
        }

        focusCurrent = focusCurrent.lerp(focusTarget, 0.08);
    }

    private Vec3 computeDrift(long nanos) {
        double gcd = sensitivityGcd();
        double amp = Math.max(driftAmplitude, gcd * driftGcdMin);
        double t = (nanos - sessionStartNanos) * 1e-9;

        double dx = Math.sin(t * freqX * TWO_PI + phaseX) * amp;
        double dy = Math.cos(t * freqY * TWO_PI + phaseY) * amp * 0.55;
        double dz = Math.sin(t * freqZ * TWO_PI + phaseZ) * amp;

        return new Vec3(dx, dy, dz);
    }

    private Vec3 getDelayedPosition(long nowNanos, double delayMs) {
        long target = nowNanos - msToNanos(delayMs);
        Vec3 best = lastTargetCenter;
        for (AimSnapshot snap : history) {
            if (snap.time <= target) {
                best = snap.pos;
            } else {
                break;
            }
        }
        return best;
    }

    private void trimHistory(long nowNanos) {
        long cutoff = nowNanos - msToNanos(reactionDelayMs + knockbackDelayMs + 400);
        while (!history.isEmpty() && history.peekFirst().time < cutoff) {
            history.removeFirst();
        }
    }

    // =================================================================
    //  GCD-квантованный шаг
    // =================================================================

    private Rotations applyStep(Rotations current, Rotations desired, double gain) {
        float gcd = sensitivityGcd();

        double errorYaw = wrapDegrees(desired.getFloatValue() - current.getFloatValue());
        double errorPitch = clamp(desired.getFloatValue2() - current.getFloatValue2(), -90.0, 90.0);
        double errorMag = Math.hypot(errorYaw, errorPitch);

        // Замедление при сближении
        double factor = 1.0;
        if (errorMag < decelStartError) {
            factor = Math.max(decelMinFactor, errorMag / decelStartError);
        }

        // Желаемый шаг (до квантовки)
        double desiredYawStep = clamp(errorYaw * gain * factor, -maxYawPerTick, maxYawPerTick);
        double desiredPitchStep = clamp(errorPitch * gain * factor, -maxPitchPerTick, maxPitchPerTick);

        // Добавляем неслитый остаток
        desiredYawStep += residual[0];
        desiredPitchStep += residual[1];

        // Квантовка по GCD
        long yawCounts = Math.round(desiredYawStep / gcd);
        long pitchCounts = Math.round(desiredPitchStep / gcd);

        // Brownian walk — «живой» шум в счётчиках мыши
        updateBrownian();
        long yawBrownian = (long) Math.floor(brownian[0]);
        long pitchBrownian = (long) Math.floor(brownian[1]);
        brownian[0] -= yawBrownian;
        brownian[1] -= pitchBrownian;
        yawCounts += yawBrownian;
        pitchCounts += pitchBrownian;

        // Никогда не останавливаемся: если ошибка есть, а счётчики нулевые — 1 count
        if (yawCounts == 0 && Math.abs(errorYaw) > 0.02) {
            yawCounts = errorYaw > 0 ? 1 : -1;
        }
        if (pitchCounts == 0 && Math.abs(errorPitch) > 0.02) {
            pitchCounts = errorPitch > 0 ? 1 : -1;
        }

        // Финальный шаг и остаток
        double stepYaw = yawCounts * gcd;
        double stepPitch = pitchCounts * gcd;
        residual[0] = desiredYawStep - stepYaw;
        residual[1] = desiredPitchStep - stepPitch;

        // Не перескакиваем цель больше чем на 40% — но допускаем лёгкий over-aim
        if (Math.abs(stepYaw) > Math.abs(errorYaw) + 0.4 * gcd) {
            stepYaw = Math.copySign(Math.abs(errorYaw) + 0.4 * gcd, errorYaw);
        }
        if (Math.abs(stepPitch) > Math.abs(errorPitch) + 0.4 * gcd) {
            stepPitch = Math.copySign(Math.abs(errorPitch) + 0.4 * gcd, errorPitch);
        }

        float newYaw = (float) (current.getFloatValue() + stepYaw);
        float newPitch = (float) clamp(current.getFloatValue2() + stepPitch, -90.0, 90.0);
        return new Rotations(newYaw, newPitch);
    }

    private void updateBrownian() {
        // Random walk с возвратом к нулю
        brownian[0] += rng.nextGaussian() * 0.35;
        brownian[1] += rng.nextGaussian() * 0.20;

        // Мягкие границы
        brownian[0] = clamp(brownian[0], -2.5, 2.5);
        brownian[1] = clamp(brownian[1], -1.5, 1.5);

        // Mean reversion — тянем к 0, чтобы не сваливался в одну сторону
        brownian[0] *= 0.93;
        brownian[1] *= 0.93;
    }

    // =================================================================
    //  Helpers
    // =================================================================

    private static long msToNanos(double ms) {
        return (long) (ms * 1_000_000.0);
    }

    private static double wrapDegrees(double deg) {
        return deg - Math.floor((deg + 180.0) / 360.0) * 360.0;
    }

    private static double clamp(double v, double min, double max) {
        return v < min ? min : Math.min(v, max);
    }

    private static Vec3 clampLooseToBox(Vec3 point, AABB box, double factor) {
        double cx = (box.minX + box.maxX) * 0.5;
        double cy = (box.minY + box.maxY) * 0.5;
        double cz = (box.minZ + box.maxZ) * 0.5;
        double hx = box.getXsize() * 0.5 * factor;
        double hy = box.getYsize() * 0.5 * factor;
        double hz = box.getZsize() * 0.5 * factor;
        return new Vec3(
                clamp(point.x, cx - hx, cx + hx),
                clamp(point.y, cy - hy, cy + hy),
                clamp(point.z, cz - hz, cz + hz));
    }

    /**
     * GCD (mouse sensitivity step) в градусах. Формула совпадает с
     * {@code LegacyCrystalAura.getSensitivityFactor()}.
     */
    private static float sensitivityGcd() {
        float s = MinecraftAccess.MINECRAFT.options.sensitivity().get().floatValue() * 0.6F + 0.2F;
        return s * s * s * 1.2F;
    }

    // =================================================================
    //  Snapshot для буфера задержки
    // =================================================================

    private static final class AimSnapshot {
        final Vec3 pos;
        final long time;

        AimSnapshot(Vec3 pos, long time) {
            this.pos = pos;
            this.time = time;
        }
    }
}
