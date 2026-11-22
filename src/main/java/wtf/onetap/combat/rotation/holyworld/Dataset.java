package wtf.onetap.combat.rotation.holyworld;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Recorded tracking episodes: contiguous ticks of the player following one target. */
public final class Dataset {
    private static final int MAGIC = 0x48575344; // "HWSD"
    private static final int VERSION = 1;
    public static final int MIN_EPISODE_TICKS = 24;
    public static final int MAX_TOTAL_TICKS = 80_000;

    public static final class Episode {
        public final float[] x;
        public final float[] y;
        public final int length;

        public Episode(float[] x, float[] y, int length) {
            this.x = x;
            this.y = y;
            this.length = length;
        }
    }

    /** Collects ticks of one episode. */
    public static final class Builder {
        private float[] x = new float[Features.N_IN * 64];
        private float[] y = new float[Features.N_OUT * 64];
        private int length;

        public void add(double[] features, double yawDelta, double pitchDelta) {
            if (length * Features.N_IN + Features.N_IN > x.length) {
                x = java.util.Arrays.copyOf(x, x.length * 2);
                y = java.util.Arrays.copyOf(y, y.length * 2);
            }
            for (int j = 0; j < Features.N_IN; j++) {
                x[length * Features.N_IN + j] = (float) features[j];
            }
            y[length * Features.N_OUT] = (float) Features.deltaToOutput(yawDelta);
            y[length * Features.N_OUT + 1] = (float) Features.deltaToOutput(pitchDelta);
            length++;
        }

        public int length() {
            return length;
        }

        public Episode build() {
            return new Episode(java.util.Arrays.copyOf(x, length * Features.N_IN), java.util.Arrays.copyOf(y, length * Features.N_OUT), length);
        }
    }

    private final List<Episode> episodes = new ArrayList<>();
    private int totalTicks;

    public synchronized boolean add(Episode episode) {
        if (episode.length < MIN_EPISODE_TICKS) {
            return false;
        }
        episodes.add(episode);
        totalTicks += episode.length;
        while (totalTicks > MAX_TOTAL_TICKS && episodes.size() > 1) {
            totalTicks -= episodes.remove(0).length;
        }
        return true;
    }

    public synchronized List<Episode> snapshot() {
        return new ArrayList<>(episodes);
    }

    public synchronized int episodeCount() {
        return episodes.size();
    }

    public synchronized int totalTicks() {
        return totalTicks;
    }

    public synchronized void clear() {
        episodes.clear();
        totalTicks = 0;
    }

    public synchronized void write(DataOutputStream out) throws IOException {
        out.writeInt(MAGIC);
        out.writeInt(VERSION);
        out.writeInt(Features.N_IN);
        out.writeInt(Features.N_OUT);
        out.writeInt(episodes.size());
        for (Episode e : episodes) {
            out.writeInt(e.length);
            for (int i = 0; i < e.length * Features.N_IN; i++) {
                out.writeFloat(e.x[i]);
            }
            for (int i = 0; i < e.length * Features.N_OUT; i++) {
                out.writeFloat(e.y[i]);
            }
        }
    }

    public synchronized void read(DataInputStream in) throws IOException {
        if (in.readInt() != MAGIC || in.readInt() != VERSION) {
            throw new IOException("not a HolyWorld dataset file");
        }
        if (in.readInt() != Features.N_IN || in.readInt() != Features.N_OUT) {
            throw new IOException("dataset was recorded with a different feature layout");
        }
        int count = in.readInt();
        if (count < 0 || count > 1_000_000) {
            throw new IOException("bad episode count");
        }
        List<Episode> loaded = new ArrayList<>();
        int ticks = 0;
        for (int i = 0; i < count; i++) {
            int length = in.readInt();
            if (length < 1 || length > MAX_TOTAL_TICKS) {
                throw new IOException("bad episode length " + length);
            }
            float[] x = new float[length * Features.N_IN];
            float[] y = new float[length * Features.N_OUT];
            for (int k = 0; k < x.length; k++) {
                x[k] = in.readFloat();
            }
            for (int k = 0; k < y.length; k++) {
                y[k] = in.readFloat();
            }
            loaded.add(new Episode(x, y, length));
            ticks += length;
        }
        episodes.clear();
        episodes.addAll(loaded);
        totalTicks = ticks;
        while (totalTicks > MAX_TOTAL_TICKS && episodes.size() > 1) {
            totalTicks -= episodes.remove(0).length;
        }
    }
}
