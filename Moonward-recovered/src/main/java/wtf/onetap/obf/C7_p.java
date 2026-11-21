package wtf.onetap.obf;

import wtf.onetap.Onetap;
import wtf.onetap.module.render.ChunkAnimator;

public class C7_p {
   private C7_p() {
   }

   public static boolean m_1_boolean() {
      ChunkAnimator chunkAnimator = Onetap.MODULE_MANAGER.get(ChunkAnimator.class);
      return chunkAnimator != null && chunkAnimator.isEnabled();
   }

   public static Double m_1_Double() {
      ChunkAnimator chunkAnimator = Onetap.MODULE_MANAGER.get(ChunkAnimator.class);
      return chunkAnimator == null ? null : (double)chunkAnimator.getDuration();
   }

   public static int m_5_int() {
      ChunkAnimator chunkAnimator = Onetap.MODULE_MANAGER.get(ChunkAnimator.class);
      return chunkAnimator == null ? -1 : chunkAnimator.m_3_int();
   }
}
