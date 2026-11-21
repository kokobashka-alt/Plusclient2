package baritone.utils.type;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

public class VarInt {
   private final int value;
   private final byte[] serialized;
   private final int size;

   public VarInt(int var1) {
      this.value = var1;
      this.serialized = serialize0(this.value);
      this.size = this.serialized.length;
   }

   public final int getValue() {
      return this.value;
   }

   public final int getSize() {
      return this.size;
   }

   public final byte[] serialize() {
      return this.serialized;
   }

   private static byte[] serialize0(int var0) {
      ByteArrayList var1 = new ByteArrayList();

      int var2;
      for (var2 = var0; (var2 & 128) != 0; var2 >>>= 7) {
         var1.add((byte)(var2 & 127 | 128));
      }

      var1.add((byte)(var2 & 0xFF));
      return var1.toByteArray();
   }

   public static VarInt read(byte[] var0) {
      return read(var0, 0);
   }

   public static VarInt read(byte[] var0, int var1) {
      int var2 = 0;
      int var3 = 0;
      int var4 = var1;

      byte var5;
      do {
         var5 = var0[var4++];
         var2 |= (var5 & 127) << var3++ * 7;
         if (var3 > 5) {
            throw new IllegalArgumentException("VarInt size cannot exceed 5 bytes");
         }
      } while ((var5 & 128) != 0);

      return new VarInt(var2);
   }
}
