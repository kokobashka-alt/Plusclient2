package wtf.onetap.combat.rotation;

/**
 * The five SpookyTime aim profiles, number 1 (aggressive) to number 5 (calm). Every value is a starting point
 * that has not been tuned on a live server. Units: smooth is the share of the remaining angle that is kept per
 * tick (higher means smoother), maxYaw and maxPitch are degree caps per tick, intervals are in ticks.
 */
public record SpookyTimeProfile(
        String name,
        double smooth,
        double maxYaw,
        double maxPitch,
        int points,
        int regenMin,
        int regenMax,
        double holdMin,
        double holdMax,
        double lead,
        double overshootChance,
        double overshootDeg,
        double pitchDeadzone,
        double pitchBlend,
        double lag,
        double sigma,
        double temperature,
        double warp) {

    public static final SpookyTimeProfile[] ALL = {
        // 1: aggressive. Low smoothing, high speed, 27 points that are redrawn often, short holds, frequent overshoot.
        new SpookyTimeProfile("Агрессивная", 0.18, 46.0, 12.0, 27, 3, 6, 1.0, 2.0, 0.70, 0.16, 2.2, 0.25, 0.75, 0.0, 0.30, 0.55, 0.16),
        // 2: burst. Fast and wide, the speed rises noticeably when the enemy camera is agitated.
        new SpookyTimeProfile("Рывок", 0.36, 30.0, 8.0, 21, 4, 8, 2.0, 3.0, 0.62, 0.11, 1.8, 0.45, 0.60, 1.0, 0.26, 0.45, 0.14),
        // 3: balance. Middle of every range.
        new SpookyTimeProfile("Баланс", 0.55, 19.0, 5.0, 16, 6, 11, 3.0, 5.0, 0.52, 0.07, 1.4, 0.65, 0.45, 2.0, 0.22, 0.35, 0.12),
        // 4: shadow. Reads the enemy with a lag, holds one point for a long time, rarely overshoots.
        new SpookyTimeProfile("Тень", 0.72, 11.0, 3.4, 12, 8, 14, 4.0, 7.0, 0.42, 0.04, 1.0, 0.80, 0.35, 3.0, 0.18, 0.27, 0.10),
        // 5: calm. High smoothing, low speed, 8 points redrawn every 10 to 17 ticks, no overshoot.
        new SpookyTimeProfile("Спокойная", 0.86, 6.0, 2.2, 8, 10, 17, 6.0, 10.0, 0.32, 0.00, 0.0, 0.95, 0.25, 4.0, 0.14, 0.20, 0.08)
    };

    /** Profile by its number, 1 to 5. Anything else gives the middle profile. */
    public static SpookyTimeProfile of(int number) {
        return number >= 1 && number <= ALL.length ? ALL[number - 1] : ALL[2];
    }

    /** 0 for the calmest profile, 1 for the most aggressive one. */
    public double aggression() {
        return 1.0 - smooth;
    }
}
