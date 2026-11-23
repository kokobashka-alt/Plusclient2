package wtf.onetap.util;

public final class Easings {
    public static final double BACK_C1 = 1.70158;
    public static final double BACK_C2 = 2.5949095;
    public static final double BACK_C3 = 2.70158;
    public static final double ELASTIC_C4 = Math.PI * 2.0 / 3.0;
    public static final double ELASTIC_C5 = Math.PI * 4.0 / 9.0;

    public static final EasingFunction LINEAR = t -> t;
    public static final EasingFunction EASE_IN_QUAD = easeInIntegerPower(2);
    public static final EasingFunction EASE_OUT_QUAD = easeOutIntegerPower(2);
    public static final EasingFunction EASE_IN_OUT_QUAD = easeInOutPower(2.0);
    public static final EasingFunction EASE_IN_CUBIC = easeInIntegerPower(3);
    public static final EasingFunction EASE_OUT_CUBIC = easeOutIntegerPower(3);
    public static final EasingFunction EASE_IN_OUT_CUBIC = easeInOutPower(3.0);
    public static final EasingFunction EASE_IN_QUART = easeInIntegerPower(4);
    public static final EasingFunction EASE_OUT_QUART = easeOutIntegerPower(4);
    public static final EasingFunction EASE_IN_OUT_QUART = easeInOutPower(4.0);
    public static final EasingFunction EASE_IN_QUINT = easeInIntegerPower(5);
    public static final EasingFunction EASE_OUT_QUINT = easeOutIntegerPower(5);
    public static final EasingFunction EASE_IN_OUT_QUINT = easeInOutPower(5.0);

    public static final EasingFunction EASE_IN_SINE = t -> 1.0 - Math.cos(t * Math.PI / 2.0);
    public static final EasingFunction EASE_OUT_SINE = t -> Math.sin(t * Math.PI / 2.0);
    public static final EasingFunction EASE_IN_OUT_SINE = t -> -(Math.cos(Math.PI * t) - 1.0) / 2.0;

    public static final EasingFunction EASE_IN_CIRC = t -> 1.0 - Math.sqrt(1.0 - Math.pow(t, 2.0));
    public static final EasingFunction EASE_OUT_CIRC = t -> Math.sqrt(1.0 - Math.pow(t - 1.0, 2.0));
    public static final EasingFunction EASE_IN_OUT_CIRC = t -> t < 0.5
            ? (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * t, 2.0))) / 2.0
            : (Math.sqrt(1.0 - Math.pow(-2.0 * t + 2.0, 2.0)) + 1.0) / 2.0;

    public static final EasingFunction EASE_IN_ELASTIC = t -> t != 0.0 && t != 1.0
            ? Math.pow(-2.0, 10.0 * t - 10.0) * Math.sin((t * 10.0 - 10.75) * (Math.PI * 2.0 / 3.0))
            : t;
    public static final EasingFunction EASE_OUT_ELASTIC = t -> t != 0.0 && t != 1.0
            ? Math.pow(2.0, -10.0 * t) * Math.sin((t * 10.0 - 0.75) * (Math.PI * 2.0 / 3.0)) + 1.0
            : t;
    public static final EasingFunction EASE_IN_OUT_ELASTIC = t -> {
        if (t != 0.0 && t != 1.0) {
            return t < 0.5
                    ? -(Math.pow(2.0, 20.0 * t - 10.0) * Math.sin((20.0 * t - 11.125) * (Math.PI * 4.0 / 9.0))) / 2.0
                    : Math.pow(2.0, -20.0 * t + 10.0) * Math.sin((20.0 * t - 11.125) * (Math.PI * 4.0 / 9.0)) / 2.0 + 1.0;
        }
        return t;
    };

    public static final EasingFunction EASE_IN_EXPO = t -> t != 0.0 ? Math.pow(2.0, 10.0 * t - 10.0) : t;
    public static final EasingFunction EASE_OUT_EXPO = t -> t != 1.0 ? 1.0 - Math.pow(2.0, -10.0 * t) : t;
    public static final EasingFunction EASE_IN_OUT_EXPO = t -> {
        if (t != 0.0 && t != 1.0) {
            return t < 0.5
                    ? Math.pow(2.0, 20.0 * t - 10.0) / 2.0
                    : (2.0 - Math.pow(2.0, -20.0 * t + 10.0)) / 2.0;
        }
        return t;
    };

    public static final EasingFunction EASE_IN_BACK = t -> 2.70158 * Math.pow(t, 3.0) - 1.70158 * Math.pow(t, 2.0);
    public static final EasingFunction EASE_OUT_BACK = t -> 1.0
            + 2.70158 * Math.pow(t - 1.0, 3.0)
            + 1.70158 * Math.pow(t - 1.0, 2.0);
    public static final EasingFunction EASE_IN_OUT_BACK = t -> t < 0.5
            ? Math.pow(2.0 * t, 2.0) * (7.189819 * t - 2.5949095) / 2.0
            : (Math.pow(2.0 * t - 2.0, 2.0) * (3.5949095 * (t * 2.0 - 2.0) + 2.5949095) + 2.0) / 2.0;

    public static final EasingFunction EASE_OUT_BOUNCE = t -> {
        double n1 = 7.5625;
        double d1 = 2.75;
        if (t < 1.0 / d1) {
            return n1 * Math.pow(t, 2.0);
        } else if (t < 2.0 / d1) {
            return n1 * Math.pow(t - 1.5 / d1, 2.0) + 0.75;
        } else if (t < 2.5 / d1) {
            return n1 * Math.pow(t - 2.25 / d1, 2.0) + 0.9375;
        } else {
            return n1 * Math.pow(t - 2.625 / d1, 2.0) + 0.984375;
        }
    };
    public static final EasingFunction EASE_IN_BOUNCE = t -> 1.0 - EASE_OUT_BOUNCE.apply(1.0 - t);
    public static final EasingFunction EASE_IN_OUT_BOUNCE = t -> t < 0.5
            ? (1.0 - EASE_OUT_BOUNCE.apply(1.0 - 2.0 * t)) / 2.0
            : (1.0 + EASE_OUT_BOUNCE.apply(2.0 * t - 1.0)) / 2.0;

    public static EasingFunction easeOutPower(double power) {
        return t -> 1.0 - Math.pow(1.0 - t, power);
    }

    public static EasingFunction easeInIntegerPower(int power) {
        return easeInPower(power);
    }

    public static EasingFunction easeOutIntegerPower(int power) {
        return easeOutPower(power);
    }

    public static EasingFunction easeInPower(double power) {
        return t -> Math.pow(t, power);
    }

    public static EasingFunction easeInOutPower(double power) {
        return t -> t < 0.5
                ? Math.pow(2.0, power - 1.0) * Math.pow(t, power)
                : 1.0 - Math.pow(-2.0 * t + 2.0, power) / 2.0;
    }

    private Easings() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
