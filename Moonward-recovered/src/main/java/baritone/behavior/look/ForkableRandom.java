package baritone.behavior.look;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

public class ForkableRandom {
   private final long[] s;

   public ForkableRandom() {
      this(System.nanoTime() ^ System.currentTimeMillis());
   }

   public ForkableRandom(long var1) {
      AtomicLong var3 = new AtomicLong(var1);
      LongSupplier var4 = () -> {
         long var1x = var3.addAndGet(-7046029254386353131L);
         var1x = (var1x ^ var1x >>> 30) * -4658895280553007687L;
         var1x = (var1x ^ var1x >>> 27) * -7723592293110705685L;
         return var1x ^ var1x >>> 31;
      };
      this.s = new long[]{var4.getAsLong(), var4.getAsLong(), var4.getAsLong(), var4.getAsLong()};
   }

   private ForkableRandom(long[] var1) {
      this.s = var1;
   }

   public double nextDouble() {
      return (this.next() >>> 11) * 1.110223E-16F;
   }

   public long next() {
      long var1 = rotl(this.s[0] + this.s[3], 23) + this.s[0];
      long var3 = this.s[1] << 17;
      this.s[2] = this.s[2] ^ this.s[0];
      this.s[3] = this.s[3] ^ this.s[1];
      this.s[1] = this.s[1] ^ this.s[2];
      this.s[0] = this.s[0] ^ this.s[3];
      this.s[2] = this.s[2] ^ var3;
      this.s[3] = rotl(this.s[3], 45);
      return var1;
   }

   public ForkableRandom fork() {
      return new ForkableRandom(Arrays.copyOf(this.s, 4));
   }

   private static long rotl(long var0, int var2) {
      return var0 << var2 | var0 >>> 64 - var2;
   }
}
