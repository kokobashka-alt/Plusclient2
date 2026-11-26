package wtf.onetap.combat.rotation;

/**
 * Pure math of the SpookyTime rotation. Nothing here touches Minecraft, so every formula can be checked on its own.
 * Angles in degrees unless a name says radians. The functions never compare two angles directly: each raw
 * difference first goes through a smooth transform (cosine lobe, tanh, saturating exponent).
 */
public final class SpookyTimeMath {
    private static final double G3 = 1.2207440846057596;
    private static final double A1 = 1.0 / G3;
    private static final double A2 = 1.0 / (G3 * G3);
    private static final double A3 = 1.0 / (G3 * G3 * G3);

    private SpookyTimeMath() {
    }

    /**
     * How closely the enemy camera mirrors ours. The enemy looks straight at us when its yaw equals ours plus 180,
     * so the raw difference is measured from that mirror and mapped through a cosine lobe: 1 at the mirror, 0 opposite.
     */
    public static double attention(double enemyYaw, double myYaw) {
        double err = wrap(enemyYaw - (myYaw + 180.0));
        return 0.5 * (1.0 + Math.cos(Math.toRadians(err)));
    }

    /** Approximate pitch error of the enemy as a value in -1 to 1. neededPitch is the pitch that would aim the enemy at us. */
    public static double pitchSkew(double enemyPitch, double neededPitch) {
        return Math.tanh((enemyPitch - neededPitch) / 18.0);
    }

    /** Arithmetic mean of the recent yaw deltas of the enemy, as a signed value in -1 to 1. */
    public static double yawDrift(double[] deltas, int count) {
        if (count <= 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (int i = 0; i < count; i++) {
            sum += deltas[i];
        }
        return Math.tanh(sum / count / 9.0);
    }

    /** Tempo of the enemy camera in 0 to 1: grows when the enemy looks away, turns quickly or has a big pitch error. */
    public static double tempo(double attention, double drift, double skew) {
        return clamp01(0.45 * (1.0 - attention) + 0.35 * Math.abs(drift) + 0.20 * Math.abs(skew));
    }

    /** Height fraction around which points are preferred. The enemy pitch error moves it between 0.62 and 0.90. */
    public static double heightCenter(double skew) {
        return 0.76 + 0.14 * skew;
    }

    /**
     * Space warp. A direct formula, with no random part: each axis of the offset gets its own smooth factor that
     * depends on the offset itself and on fixed phases. An offset such as x 2, y 0.3, z 1 turns into numbers of the
     * same kind but different size. The result is used only to scale speeds, never as the aim direction.
     */
    public static double[] warp(double x, double y, double z, double strength, double[] phase) {
        double wx = x * (1.0 - strength * Math.sin(0.83 * x + 0.37 * z + phase[0]));
        double wy = y * (1.0 - strength * 1.6 * Math.sin(1.31 * y + 0.52 * x + phase[1]));
        double wz = z * (1.0 - strength * Math.cos(0.67 * z - 0.44 * y + phase[2]));
        return new double[] {wx, wy, wz};
    }

    public static double length(double[] v) {
        return Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    }

    /** Share of the warped offset that lies in the horizontal plane, 0 to 1. Scales the yaw speed. */
    public static double horizontalShare(double[] w) {
        double len = Math.max(length(w), 1.0E-4);
        return Math.hypot(w[0], w[2]) / len;
    }

    /** Share of the warped offset that is vertical, 0 to 1. Scales the pitch speed. */
    public static double verticalShare(double[] w) {
        double len = Math.max(length(w), 1.0E-4);
        return Math.min(Math.abs(w[1]) / len, 1.0);
    }

    /** Speed gain from the warped distance. Closer means faster, between 0.8 and 1.3. */
    public static double distanceGain(double warpedLength) {
        return 0.80 + 0.50 * Math.exp(-warpedLength / 6.0);
    }

    /** Share of the remaining angle that is covered in dtTicks ticks. Independent of the frame rate. */
    public static double follow(double smooth, double dtTicks) {
        double s = Math.min(Math.max(smooth, 0.0), 0.999);
        return 1.0 - Math.pow(s, dtTicks);
    }

    /**
     * Low-discrepancy points over the hitbox. Each row is {height fraction, right, forward}, right and forward in
     * -0.9 to 0.9 relative to the body yaw of the enemy. The seed shifts the whole set, so every redraw differs.
     */
    public static double[][] points(int count, double yLow, double yHigh, double seed) {
        double[][] out = new double[count][3];
        for (int i = 0; i < count; i++) {
            double n = i + seed;
            double u = frac(A1 * n);
            double v = frac(A2 * n);
            double w = frac(A3 * n);
            out[i][0] = yLow + (yHigh - yLow) * u;
            out[i][1] = (v * 2.0 - 1.0) * 0.9;
            out[i][2] = (w * 2.0 - 1.0) * 0.9;
        }
        return out;
    }

    /** Score of one point. Higher is better. */
    public static double score(double[] point, double heightCenter, double sigma, double drift, double attention) {
        double dy = point[0] - heightCenter;
        double vertical = -(dy * dy) / (2.0 * sigma * sigma);
        double lateral = 0.9 * drift * point[1];
        double depth = (2.0 * attention - 1.0) * 0.4 * point[2];
        return vertical + lateral + depth;
    }

    /** Softmax pick. Scores of negative infinity are never chosen. Returns -1 when nothing can be chosen. rand is in 0 to 1. */
    public static int pick(double[] scores, double temperature, double rand) {
        double max = Double.NEGATIVE_INFINITY;
        for (double s : scores) {
            max = Math.max(max, s);
        }
        if (max == Double.NEGATIVE_INFINITY) {
            return -1;
        }
        double t = Math.max(temperature, 1.0E-3);
        double total = 0.0;
        double[] weights = new double[scores.length];
        for (int i = 0; i < scores.length; i++) {
            weights[i] = scores[i] == Double.NEGATIVE_INFINITY ? 0.0 : Math.exp((scores[i] - max) / t);
            total += weights[i];
        }
        double cut = rand * total;
        int last = -1;
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] <= 0.0) {
                continue;
            }
            last = i;
            cut -= weights[i];
            if (cut <= 0.0) {
                return i;
            }
        }
        return last;
    }

    public static double wrap(double degrees) {
        double d = degrees % 360.0;
        if (d >= 180.0) {
            d -= 360.0;
        } else if (d < -180.0) {
            d += 360.0;
        }
        return d;
    }

    public static double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    private static double frac(double v) {
        return v - Math.floor(v);
    }
}
