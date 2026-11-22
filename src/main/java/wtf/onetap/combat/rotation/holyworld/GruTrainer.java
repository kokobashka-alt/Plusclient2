package wtf.onetap.combat.rotation.holyworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/** Mini-batch Adam training of {@link GruNet} on recorded episodes (truncated BPTT over fixed windows). */
public final class GruTrainer {
    public static final class Config {
        public int epochs = 40;
        public int window = 40;
        public int burnIn = 6;
        public int batch = 16;
        public int stepsPerEpoch = 40;
        public double learningRate = 0.005;
        public double finalLearningRate = 0.0005;
        public double clipNorm = 1.0;
        public long seed = 12345L;
    }

    public interface Listener {
        void onEpoch(int epoch, int epochs, double trainLoss, double validationLoss, double bestLoss);
    }

    public static final class Result {
        public final double bestLoss;
        public final double baselineLoss;
        public final int epochsRun;
        public final boolean usedValidation;
        public final boolean stopped;

        Result(double bestLoss, double baselineLoss, int epochsRun, boolean usedValidation, boolean stopped) {
            this.bestLoss = bestLoss;
            this.baselineLoss = baselineLoss;
            this.epochsRun = epochsRun;
            this.usedValidation = usedValidation;
            this.stopped = stopped;
        }
    }

    private GruTrainer() {
    }

    public static Result train(GruNet net, List<Dataset.Episode> episodes, Config cfg, Listener listener, AtomicBoolean stop) {
        if (episodes.isEmpty()) {
            throw new IllegalArgumentException("no training data");
        }
        Random random = new Random(cfg.seed);
        List<Dataset.Episode> train = new ArrayList<>();
        List<Dataset.Episode> validation = new ArrayList<>();
        if (episodes.size() >= 6) {
            for (int i = 0; i < episodes.size(); i++) {
                (i % 6 == 5 ? validation : train).add(episodes.get(i));
            }
        } else {
            train.addAll(episodes);
        }
        boolean useValidation = !validation.isEmpty();
        List<Dataset.Episode> scoreSet = useValidation ? validation : train;

        int p = net.params.length;
        double[] grad = new double[p];
        double[] m = new double[p];
        double[] v = new double[p];
        double[] best = net.params.clone();
        double bestLoss = Double.MAX_VALUE;
        GruNet.Workspace ws = net.workspace(cfg.window);
        long adamStep = 0;

        double baseline = baseline(scoreSet, cfg);
        double[] weights = new double[train.size()];
        double total = 0;
        for (int i = 0; i < weights.length; i++) {
            total += train.get(i).length;
            weights[i] = total;
        }

        int epochsRun = 0;
        boolean stopped = false;
        for (int epoch = 1; epoch <= cfg.epochs; epoch++) {
            if (stop.get()) {
                stopped = true;
                break;
            }
            double progress = cfg.epochs <= 1 ? 1.0 : (epoch - 1) / (double) (cfg.epochs - 1);
            double lr = cfg.finalLearningRate + 0.5 * (cfg.learningRate - cfg.finalLearningRate) * (1.0 + Math.cos(Math.PI * progress));
            double trainSse = 0;
            double trainCount = 0;
            for (int step = 0; step < cfg.stepsPerEpoch; step++) {
                java.util.Arrays.fill(grad, 0.0);
                double scored = 0;
                for (int b = 0; b < cfg.batch; b++) {
                    Dataset.Episode e = pick(train, weights, total, random);
                    int steps = Math.min(cfg.window, e.length);
                    int offset = e.length == steps ? 0 : random.nextInt(e.length - steps + 1);
                    double[] r = net.accumulate(e.x, e.y, offset, steps, Math.min(cfg.burnIn, steps - 1), grad, ws);
                    trainSse += r[0];
                    trainCount += r[1];
                    scored += r[1];
                }
                if (scored <= 0) {
                    continue;
                }
                double norm = 0;
                for (int i = 0; i < p; i++) {
                    grad[i] /= scored;
                    norm += grad[i] * grad[i];
                }
                norm = Math.sqrt(norm);
                double scale = norm > cfg.clipNorm ? cfg.clipNorm / norm : 1.0;
                adamStep++;
                double c1 = 1.0 - Math.pow(0.9, adamStep);
                double c2 = 1.0 - Math.pow(0.999, adamStep);
                for (int i = 0; i < p; i++) {
                    double g = grad[i] * scale;
                    m[i] = 0.9 * m[i] + 0.1 * g;
                    v[i] = 0.999 * v[i] + 0.001 * g * g;
                    net.params[i] -= lr * (m[i] / c1) / (Math.sqrt(v[i] / c2) + 1e-8);
                }
            }
            net.trainedSteps += cfg.stepsPerEpoch;
            double trainLoss = trainCount > 0 ? trainSse / trainCount : Double.NaN;
            double score = score(net, scoreSet, cfg, ws);
            if (!Double.isNaN(score) && score < bestLoss) {
                bestLoss = score;
                System.arraycopy(net.params, 0, best, 0, p);
            }
            epochsRun = epoch;
            if (listener != null) {
                listener.onEpoch(epoch, cfg.epochs, trainLoss, score, bestLoss);
            }
        }

        if (bestLoss != Double.MAX_VALUE) {
            System.arraycopy(best, 0, net.params, 0, p);
        }
        estimateNoise(net, scoreSet, cfg, ws);
        return new Result(bestLoss, baseline, epochsRun, useValidation, stopped);
    }

    private static Dataset.Episode pick(List<Dataset.Episode> train, double[] cumulative, double total, Random random) {
        double target = random.nextDouble() * total;
        int lo = 0;
        int hi = cumulative.length - 1;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (cumulative[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return train.get(lo);
    }

    /** Mean squared error of the best constant predictor (the mean label); a useful floor to compare against. */
    private static double baseline(List<Dataset.Episode> set, Config cfg) {
        double[] sum = new double[Features.N_OUT];
        long count = 0;
        for (Dataset.Episode e : set) {
            for (int t = cfg.burnIn; t < e.length; t++) {
                for (int o = 0; o < Features.N_OUT; o++) {
                    sum[o] += e.y[t * Features.N_OUT + o];
                }
                count++;
            }
        }
        if (count == 0) {
            return Double.NaN;
        }
        double[] mean = {sum[0] / count, sum[1] / count};
        double sse = 0;
        for (Dataset.Episode e : set) {
            for (int t = cfg.burnIn; t < e.length; t++) {
                for (int o = 0; o < Features.N_OUT; o++) {
                    double d = e.y[t * Features.N_OUT + o] - mean[o];
                    sse += d * d;
                }
            }
        }
        return sse / (count * Features.N_OUT);
    }

    private static double score(GruNet net, List<Dataset.Episode> set, Config cfg, GruNet.Workspace ws) {
        double sse = 0;
        double count = 0;
        int stride = Math.max(1, cfg.window / 2);
        for (Dataset.Episode e : set) {
            int steps = Math.min(cfg.window, e.length);
            for (int offset = 0; offset + steps <= e.length; offset += stride) {
                double[] r = net.evaluate(e.x, e.y, offset, steps, Math.min(cfg.burnIn, steps - 1), ws);
                sse += r[0];
                count += r[1];
            }
        }
        return count > 0 ? sse / count : Double.NaN;
    }

    private static void estimateNoise(GruNet net, List<Dataset.Episode> set, Config cfg, GruNet.Workspace ws) {
        double[] sse = new double[Features.N_OUT];
        int[] count = new int[Features.N_OUT];
        int stride = Math.max(1, cfg.window / 2);
        for (Dataset.Episode e : set) {
            int steps = Math.min(cfg.window, e.length);
            for (int offset = 0; offset + steps <= e.length; offset += stride) {
                net.residuals(e.x, e.y, offset, steps, Math.min(cfg.burnIn, steps - 1), ws, sse, count);
            }
        }
        for (int o = 0; o < Features.N_OUT; o++) {
            net.noiseStd[o] = count[o] > 0 ? Math.sqrt(sse[o] / count[o]) : 0.0;
        }
    }
}
