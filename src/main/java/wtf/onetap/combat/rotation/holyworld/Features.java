package wtf.onetap.combat.rotation.holyworld;

/** Observation layout shared by recording, training and inference. All angles are in degrees, one step is one client tick. */
public final class Features {
    public static final int N_IN = 9;
    public static final int N_OUT = 2;

    private static final double ANGLE_SCALE = 45.0;
    private static final double DELTA_SCALE = 10.0;
    private static final double TARGET_VELOCITY_SCALE = 5.0;
    private static final double DISTANCE_SCALE = 10.0;
    private static final double SIZE_SCALE = 10.0;

    private Features() {
    }

    /**
     * @param yawError       wrapped yaw from the view to the aim point
     * @param pitchError     pitch from the view to the aim point
     * @param prevYawDelta   yaw change applied during the previous tick
     * @param prevPitchDelta pitch change applied during the previous tick
     * @param targetYawVel   change of the aim point's yaw angle since the previous tick
     * @param targetPitchVel change of the aim point's pitch angle since the previous tick
     * @param distance       distance to the target in blocks
     * @param sizeDegrees    angular half width of the target's hitbox as seen from the player
     * @param cooldown       attack cooldown progress, 0..1
     */
    public static void build(double yawError, double pitchError, double prevYawDelta, double prevPitchDelta,
                             double targetYawVel, double targetPitchVel, double distance, double sizeDegrees,
                             double cooldown, double[] out) {
        out[0] = clamp(yawError / ANGLE_SCALE, -4, 4);
        out[1] = clamp(pitchError / ANGLE_SCALE, -4, 4);
        out[2] = clamp(prevYawDelta / DELTA_SCALE, -6, 6);
        out[3] = clamp(prevPitchDelta / DELTA_SCALE, -6, 6);
        out[4] = clamp(targetYawVel / TARGET_VELOCITY_SCALE, -6, 6);
        out[5] = clamp(targetPitchVel / TARGET_VELOCITY_SCALE, -6, 6);
        out[6] = clamp(distance / DISTANCE_SCALE, 0, 3);
        out[7] = clamp(sizeDegrees / SIZE_SCALE, 0, 3);
        out[8] = clamp(cooldown, 0, 1);
    }

    public static double deltaToOutput(double degrees) {
        return clamp(degrees / DELTA_SCALE, -6, 6);
    }

    public static double outputToDelta(double output) {
        return output * DELTA_SCALE;
    }

    private static double clamp(double v, double lo, double hi) {
        if (Double.isNaN(v)) {
            return 0;
        }
        return v < lo ? lo : Math.min(v, hi);
    }
}
