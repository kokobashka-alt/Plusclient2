package wtf.onetap.obf;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

public class C5_d {
   private static final Long2ObjectOpenHashMap<ChunkAccess> f_3 = new Long2ObjectOpenHashMap();
   private static final Long2IntOpenHashMap f_0 = new Long2IntOpenHashMap();
   private static Level f_2;

   private C5_d() {
   }

   public static void m_8_void() {
      f_2 = null;
      f_3.clear();
      f_0.clear();
   }

   public static ChunkAccess m_9(Level level, BlockPos blockPos) {
      m_7(level);
      long longValue = ChunkPos.pack(blockPos.getX() >> 4, blockPos.getZ() >> 4);
      ChunkAccess chunkAccess = (ChunkAccess)f_3.get(longValue);
      if (chunkAccess == null) {
         chunkAccess = level.getChunk(blockPos);
         f_3.put(longValue, chunkAccess);
      }

      return chunkAccess;
   }

   public static int m_8(Level level, BlockPos blockPos) {
      m_7(level);
      long longValue = blockPos.asLong();
      int value = f_0.get(longValue);
      if (value == -1) {
         value = level.getMaxLocalRawBrightness(blockPos);
         f_0.put(longValue, value);
      }

      return value;
   }

   private static void m_7(Level level) {
      if (f_2 != level) {
         f_2 = level;
         f_3.clear();
         f_0.clear();
      }
   }

   static {
      f_0.defaultReturnValue(-1);
   }
}
