package wtf.onetap.combat.rotation.lopata;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

/**
 * Single-layer GRU with a linear head, inference only. Weights come from tools/lopata/train_lopata.py.
 *
 * File format, little endian: magic int 0x41504F4C, version int 1, F int, H int, then float32 arrays in this order:
 * featMean[F], featStd[F], outScale[2], Wi[3H*F], Wh[3H*H], bi[3H], bhn[H], Wo[4*H], bo[4].
 * Gate rows are ordered r, z, n. Equations (PyTorch style, one merged bias for r and z):
 *   r = sigmoid(Wi_r x + Wh_r h + bi_r)      z = sigmoid(Wi_z x + Wh_z h + bi_z)
 *   n = tanh(Wi_n x + bi_n + r * (Wh_n h + bhn))      h' = (1 - z) * n + z * h
 * Head: y = Wo h' + bo. mean_deg[i] = y[i] * outScale[i]; log_sigma[i] = 2.5 * tanh(y[2 + i]) - 1.5 (normalised units).
 */
public final class LopataNet {
   public static final int MAGIC = 0x41504F4C;
   public static final int VERSION = 1;
   private static final int MAX_DIM = 4096;
   /** Normalised inputs are clamped to this range, same constant as CLIP in train_lopata.py. */
   private static final float INPUT_CLIP = 8.0F;

   private final int features;
   private final int hidden;
   private final float[] featMean;
   private final float[] featStd;
   private final float[] outScale;
   private final float[] wi;
   private final float[] wh;
   private final float[] bi;
   private final float[] bhn;
   private final float[] wo;
   private final float[] bo;

   private final float[] h;
   private final float[] x;
   private final float[] gi;
   private final float[] gh;

   /** Result of the last {@link #step}: mean and sigma in degrees for yaw and pitch. */
   public final float[] mean = new float[2];
   public final float[] sigma = new float[2];

   private LopataNet(int features, int hidden, float[] featMean, float[] featStd, float[] outScale, float[] wi, float[] wh, float[] bi, float[] bhn, float[] wo, float[] bo) {
      this.features = features;
      this.hidden = hidden;
      this.featMean = featMean;
      this.featStd = featStd;
      this.outScale = outScale;
      this.wi = wi;
      this.wh = wh;
      this.bi = bi;
      this.bhn = bhn;
      this.wo = wo;
      this.bo = bo;
      this.h = new float[hidden];
      this.x = new float[features];
      this.gi = new float[3 * hidden];
      this.gh = new float[3 * hidden];
   }

   public int features() {
      return this.features;
   }

   public int hidden() {
      return this.hidden;
   }

   public static LopataNet load(InputStream stream) throws IOException {
      DataInputStream in = new DataInputStream(stream);
      byte[] all = in.readAllBytes();
      ByteBuffer buf = ByteBuffer.wrap(all).order(ByteOrder.LITTLE_ENDIAN);
      if (all.length < 16) {
         throw new IOException("model file too short");
      }

      if (buf.getInt() != MAGIC) {
         throw new IOException("bad magic, not a lopata model");
      }

      int version = buf.getInt();
      if (version != VERSION) {
         throw new IOException("unsupported model version " + version);
      }

      int f = buf.getInt();
      int hd = buf.getInt();
      if (f != LopataFeatures.COUNT) {
         throw new IOException("model expects " + f + " features, client builds " + LopataFeatures.COUNT);
      }

      if (hd < 1 || hd > MAX_DIM) {
         throw new IOException("implausible hidden size " + hd);
      }

      long floats = 2L * f + 2 + 3L * hd * f + 3L * hd * hd + 3L * hd + hd + 4L * hd + 4;
      if ((long) buf.remaining() != floats * 4L) {
         throw new IOException("size mismatch: expected " + floats * 4L + " payload bytes, got " + buf.remaining());
      }

      float[] mean = read(buf, f);
      float[] std = read(buf, f);
      float[] scale = read(buf, 2);
      float[] wi = read(buf, 3 * hd * f);
      float[] wh = read(buf, 3 * hd * hd);
      float[] bi = read(buf, 3 * hd);
      float[] bhn = read(buf, hd);
      float[] wo = read(buf, 4 * hd);
      float[] bo = read(buf, 4);
      for (float[] a : new float[][]{mean, std, scale, wi, wh, bi, bhn, wo, bo}) {
         for (float v : a) {
            if (!Float.isFinite(v)) {
               throw new IOException("model contains non-finite weights");
            }
         }
      }

      if (scale[0] <= 0.0F || scale[1] <= 0.0F) {
         throw new IOException("non-positive output scale");
      }

      return new LopataNet(f, hd, mean, std, scale, wi, wh, bi, bhn, wo, bo);
   }

   private static float[] read(ByteBuffer buf, int n) {
      float[] out = new float[n];
      for (int i = 0; i < n; i++) {
         out[i] = buf.getFloat();
      }

      return out;
   }

   public void resetState() {
      java.util.Arrays.fill(this.h, 0.0F);
   }

   private static float sigmoid(float v) {
      return (float) (1.0 / (1.0 + Math.exp(-v)));
   }

   /** Advances the hidden state by one tick with raw (unnormalised) features and fills {@link #mean} and {@link #sigma}. */
   public void step(float[] raw) {
      int hd = this.hidden;
      for (int i = 0; i < this.features; i++) {
         float std = Math.max(this.featStd[i], 1.0E-6F);
         this.x[i] = Math.max(-INPUT_CLIP, Math.min(INPUT_CLIP, (raw[i] - this.featMean[i]) / std));
      }

      for (int row = 0; row < 3 * hd; row++) {
         float a = this.bi[row];
         int base = row * this.features;
         for (int i = 0; i < this.features; i++) {
            a += this.wi[base + i] * this.x[i];
         }

         this.gi[row] = a;
         float b = 0.0F;
         int hb = row * hd;
         for (int i = 0; i < hd; i++) {
            b += this.wh[hb + i] * this.h[i];
         }

         this.gh[row] = b;
      }

      for (int j = 0; j < hd; j++) {
         float r = sigmoid(this.gi[j] + this.gh[j]);
         float z = sigmoid(this.gi[hd + j] + this.gh[hd + j]);
         float n = (float) Math.tanh(this.gi[2 * hd + j] + r * (this.gh[2 * hd + j] + this.bhn[j]));
         this.h[j] = (1.0F - z) * n + z * this.h[j];
      }

      float[] y = new float[4];
      for (int o = 0; o < 4; o++) {
         float a = this.bo[o];
         int base = o * hd;
         for (int i = 0; i < hd; i++) {
            a += this.wo[base + i] * this.h[i];
         }

         y[o] = a;
      }

      for (int i = 0; i < 2; i++) {
         this.mean[i] = y[i] * this.outScale[i];
         float logSigma = (float) (2.5 * Math.tanh(y[2 + i]) - 1.5);
         this.sigma[i] = (float) Math.exp(logSigma) * this.outScale[i];
      }
   }

   /** Draws one step from the predicted Gaussian, scaled by temperature (1 = as trained, 0 = mean only). */
   public void sample(Random random, double temperature, float[] outStep) {
      for (int i = 0; i < 2; i++) {
         outStep[i] = (float) (this.mean[i] + random.nextGaussian() * this.sigma[i] * temperature);
      }
   }
}
