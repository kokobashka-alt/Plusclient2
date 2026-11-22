package wtf.onetap.combat.rotation.holyworld;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Single-layer GRU with a linear head, written from scratch so it can be trained inside the game process.
 *
 * Gates: z = sigmoid(Wz x + Uz h + bz), r = sigmoid(Wr x + Ur h + br), n = tanh(Wn x + r * (Un h) + bn),
 * h' = (1 - z) * n + z * h, y = Wo h' + bo.
 */
public final class GruNet {
    private static final int MAGIC = 0x48575255; // "HWRU"
    private static final int VERSION = 1;

    public final int hidden;
    public final double[] params;
    public final double[] noiseStd = new double[Features.N_OUT];
    public long trainedSteps;

    private final int offW;
    private final int offU;
    private final int offB;
    private final int offWo;
    private final int offBo;

    public GruNet(int hidden) {
        this.hidden = hidden;
        int nIn = Features.N_IN;
        this.offW = 0;
        this.offU = offW + 3 * hidden * nIn;
        this.offB = offU + 3 * hidden * hidden;
        this.offWo = offB + 3 * hidden;
        this.offBo = offWo + Features.N_OUT * hidden;
        this.params = new double[offBo + Features.N_OUT];
    }

    public static int parameterCount(int hidden) {
        return 3 * hidden * Features.N_IN + 3 * hidden * hidden + 3 * hidden + Features.N_OUT * hidden + Features.N_OUT;
    }

    public void randomize(Random random) {
        int nIn = Features.N_IN;
        double wScale = Math.sqrt(2.0 / (nIn + hidden));
        double uScale = Math.sqrt(1.0 / hidden);
        for (int i = 0; i < 3 * hidden * nIn; i++) {
            params[offW + i] = random.nextGaussian() * wScale;
        }
        for (int i = 0; i < 3 * hidden * hidden; i++) {
            params[offU + i] = random.nextGaussian() * uScale * 0.5;
        }
        for (int i = 0; i < hidden; i++) {
            params[offB + i] = 1.0; // update gate starts biased toward keeping state
        }
        double oScale = Math.sqrt(1.0 / hidden);
        for (int i = 0; i < Features.N_OUT * hidden; i++) {
            params[offWo + i] = random.nextGaussian() * oScale * 0.3;
        }
        noiseStd[0] = noiseStd[1] = 0;
        trainedSteps = 0;
    }

    public GruNet copy() {
        GruNet other = new GruNet(hidden);
        other.copyFrom(this);
        return other;
    }

    public void copyFrom(GruNet src) {
        if (src.hidden != hidden) {
            throw new IllegalArgumentException("hidden size mismatch");
        }
        System.arraycopy(src.params, 0, params, 0, params.length);
        System.arraycopy(src.noiseStd, 0, noiseStd, 0, noiseStd.length);
        trainedSteps = src.trainedSteps;
    }

    // ---------------------------------------------------------------------------------------------- inference

    /** Stateful runner with its own scratch buffers; one per consumer thread. */
    public final class Runner {
        private final double[] h = new double[hidden];
        private final double[] z = new double[hidden];
        private final double[] r = new double[hidden];
        private final double[] n = new double[hidden];
        private final double[] un = new double[hidden];
        private final double[] next = new double[hidden];

        public void reset() {
            java.util.Arrays.fill(h, 0.0);
        }

        public void step(double[] x, double[] y) {
            forward(x, h, z, r, n, un, next, y);
            System.arraycopy(next, 0, h, 0, hidden);
        }
    }

    public Runner runner() {
        return new Runner();
    }

    private void forward(double[] x, double[] hPrev, double[] z, double[] r, double[] n, double[] un,
                         double[] hNext, double[] y) {
        int nIn = Features.N_IN;
        for (int i = 0; i < hidden; i++) {
            double az = params[offB + i];
            double ar = params[offB + hidden + i];
            double an = params[offB + 2 * hidden + i];
            int wz = offW + i * nIn;
            int wr = offW + (hidden + i) * nIn;
            int wn = offW + (2 * hidden + i) * nIn;
            for (int j = 0; j < nIn; j++) {
                double xj = x[j];
                az += params[wz + j] * xj;
                ar += params[wr + j] * xj;
                an += params[wn + j] * xj;
            }
            int uz = offU + i * hidden;
            int ur = offU + (hidden + i) * hidden;
            int uc = offU + (2 * hidden + i) * hidden;
            double sum = 0;
            for (int k = 0; k < hidden; k++) {
                double hk = hPrev[k];
                az += params[uz + k] * hk;
                ar += params[ur + k] * hk;
                sum += params[uc + k] * hk;
            }
            z[i] = sigmoid(az);
            r[i] = sigmoid(ar);
            un[i] = sum;
            n[i] = Math.tanh(an + r[i] * sum);
            hNext[i] = (1.0 - z[i]) * n[i] + z[i] * hPrev[i];
        }
        for (int o = 0; o < Features.N_OUT; o++) {
            double acc = params[offBo + o];
            int row = offWo + o * hidden;
            for (int k = 0; k < hidden; k++) {
                acc += params[row + k] * hNext[k];
            }
            y[o] = acc;
        }
    }

    // ---------------------------------------------------------------------------------------------- training

    /** Scratch space for one training window. */
    public static final class Workspace {
        final double[][] hs;
        final double[][] zs;
        final double[][] rs;
        final double[][] ns;
        final double[][] uns;
        final double[][] ys;

        public Workspace(int hidden, int maxSteps) {
            hs = new double[maxSteps + 1][hidden];
            zs = new double[maxSteps][hidden];
            rs = new double[maxSteps][hidden];
            ns = new double[maxSteps][hidden];
            uns = new double[maxSteps][hidden];
            ys = new double[maxSteps][Features.N_OUT];
        }
    }

    public Workspace workspace(int maxSteps) {
        return new Workspace(hidden, maxSteps);
    }

    /**
     * Runs one window from a zero state and adds the gradient of the summed squared error to {@code grad}.
     * Steps before {@code burnIn} are run but not scored. Returns {sumSquaredError, scoredValues}.
     */
    public double[] accumulate(float[] x, float[] y, int offset, int steps, int burnIn, double[] grad, Workspace ws) {
        int nIn = Features.N_IN;
        double[] input = new double[nIn];
        java.util.Arrays.fill(ws.hs[0], 0.0);
        for (int t = 0; t < steps; t++) {
            for (int j = 0; j < nIn; j++) {
                input[j] = x[(offset + t) * nIn + j];
            }
            forward(input, ws.hs[t], ws.zs[t], ws.rs[t], ws.ns[t], ws.uns[t], ws.hs[t + 1], ws.ys[t]);
        }

        double sse = 0;
        int scored = 0;
        double[] dhNext = new double[hidden];
        double[] dh = new double[hidden];
        double[] dhPrev = new double[hidden];
        double[] dyv = new double[Features.N_OUT];
        double[] dzPre = new double[hidden];
        double[] drPre = new double[hidden];
        double[] dnPre = new double[hidden];
        double[] dUn = new double[hidden];

        for (int t = steps - 1; t >= 0; t--) {
            boolean counted = t >= burnIn;
            for (int o = 0; o < Features.N_OUT; o++) {
                if (counted) {
                    double err = ws.ys[t][o] - y[(offset + t) * Features.N_OUT + o];
                    sse += err * err;
                    scored++;
                    dyv[o] = 2.0 * err;
                } else {
                    dyv[o] = 0.0;
                }
            }
            double[] h = ws.hs[t + 1];
            double[] hPrev = ws.hs[t];
            for (int o = 0; o < Features.N_OUT; o++) {
                grad[offBo + o] += dyv[o];
                int row = offWo + o * hidden;
                for (int k = 0; k < hidden; k++) {
                    grad[row + k] += dyv[o] * h[k];
                }
            }
            for (int k = 0; k < hidden; k++) {
                double acc = dhNext[k];
                for (int o = 0; o < Features.N_OUT; o++) {
                    acc += params[offWo + o * hidden + k] * dyv[o];
                }
                dh[k] = acc;
            }
            double[] z = ws.zs[t];
            double[] r = ws.rs[t];
            double[] n = ws.ns[t];
            double[] un = ws.uns[t];
            for (int i = 0; i < hidden; i++) {
                double dn = dh[i] * (1.0 - z[i]);
                double dz = dh[i] * (hPrev[i] - n[i]);
                dhPrev[i] = dh[i] * z[i];
                dnPre[i] = dn * (1.0 - n[i] * n[i]);
                double dr = dnPre[i] * un[i];
                dUn[i] = dnPre[i] * r[i];
                dzPre[i] = dz * z[i] * (1.0 - z[i]);
                drPre[i] = dr * r[i] * (1.0 - r[i]);
            }
            for (int i = 0; i < hidden; i++) {
                grad[offB + i] += dzPre[i];
                grad[offB + hidden + i] += drPre[i];
                grad[offB + 2 * hidden + i] += dnPre[i];
                int wz = offW + i * nIn;
                int wr = offW + (hidden + i) * nIn;
                int wn = offW + (2 * hidden + i) * nIn;
                for (int j = 0; j < nIn; j++) {
                    double xj = x[(offset + t) * nIn + j];
                    grad[wz + j] += dzPre[i] * xj;
                    grad[wr + j] += drPre[i] * xj;
                    grad[wn + j] += dnPre[i] * xj;
                }
                int uz = offU + i * hidden;
                int ur = offU + (hidden + i) * hidden;
                int uc = offU + (2 * hidden + i) * hidden;
                for (int k = 0; k < hidden; k++) {
                    double hk = hPrev[k];
                    grad[uz + k] += dzPre[i] * hk;
                    grad[ur + k] += drPre[i] * hk;
                    grad[uc + k] += dUn[i] * hk;
                    dhPrev[k] += params[uz + k] * dzPre[i] + params[ur + k] * drPre[i] + params[uc + k] * dUn[i];
                }
            }
            double[] swap = dhNext;
            dhNext = dhPrev;
            dhPrev = swap;
            java.util.Arrays.fill(dhPrev, 0.0);
        }
        return new double[]{sse, scored};
    }

    /** Loss only, no gradient. Returns {sumSquaredError, scoredValues}. */
    public double[] evaluate(float[] x, float[] y, int offset, int steps, int burnIn, Workspace ws) {
        int nIn = Features.N_IN;
        double[] input = new double[nIn];
        java.util.Arrays.fill(ws.hs[0], 0.0);
        double sse = 0;
        int scored = 0;
        for (int t = 0; t < steps; t++) {
            for (int j = 0; j < nIn; j++) {
                input[j] = x[(offset + t) * nIn + j];
            }
            forward(input, ws.hs[t], ws.zs[t], ws.rs[t], ws.ns[t], ws.uns[t], ws.hs[t + 1], ws.ys[t]);
            if (t >= burnIn) {
                for (int o = 0; o < Features.N_OUT; o++) {
                    double err = ws.ys[t][o] - y[(offset + t) * Features.N_OUT + o];
                    sse += err * err;
                    scored++;
                }
            }
        }
        return new double[]{sse, scored};
    }

    /** Per-output sum of squared residuals over a window; used to estimate the noise level. */
    public void residuals(float[] x, float[] y, int offset, int steps, int burnIn, Workspace ws, double[] sseOut, int[] countOut) {
        evaluate(x, y, offset, steps, burnIn, ws);
        for (int t = burnIn; t < steps; t++) {
            for (int o = 0; o < Features.N_OUT; o++) {
                double err = ws.ys[t][o] - y[(offset + t) * Features.N_OUT + o];
                sseOut[o] += err * err;
                countOut[o]++;
            }
        }
    }

    // ---------------------------------------------------------------------------------------------- persistence

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(MAGIC);
        out.writeInt(VERSION);
        out.writeInt(Features.N_IN);
        out.writeInt(Features.N_OUT);
        out.writeInt(hidden);
        out.writeLong(trainedSteps);
        for (double v : noiseStd) {
            out.writeDouble(v);
        }
        out.writeInt(params.length);
        for (double v : params) {
            out.writeDouble(v);
        }
    }

    public static GruNet read(DataInputStream in) throws IOException {
        if (in.readInt() != MAGIC) {
            throw new IOException("not a HolyWorld model file");
        }
        if (in.readInt() != VERSION) {
            throw new IOException("unsupported model version");
        }
        if (in.readInt() != Features.N_IN || in.readInt() != Features.N_OUT) {
            throw new IOException("model was trained with a different feature layout");
        }
        int hidden = in.readInt();
        if (hidden < 1 || hidden > 256) {
            throw new IOException("bad hidden size " + hidden);
        }
        GruNet net = new GruNet(hidden);
        net.trainedSteps = in.readLong();
        for (int i = 0; i < net.noiseStd.length; i++) {
            net.noiseStd[i] = in.readDouble();
        }
        int count = in.readInt();
        if (count != net.params.length) {
            throw new IOException("parameter count mismatch");
        }
        for (int i = 0; i < count; i++) {
            double v = in.readDouble();
            if (Double.isNaN(v) || Double.isInfinite(v)) {
                throw new IOException("model contains a non-finite weight");
            }
            net.params[i] = v;
        }
        return net;
    }

    private static double sigmoid(double v) {
        return 1.0 / (1.0 + Math.exp(-v));
    }
}
