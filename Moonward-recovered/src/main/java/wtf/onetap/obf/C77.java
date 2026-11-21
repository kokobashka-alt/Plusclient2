package wtf.onetap.obf;

import net.minecraft.core.BlockPos;
import wtf.onetap.module.Module;

public class C77 extends BlockPos {
   private static Module[] f_2;

   public C77() {
      super(6, 6, 6);
   }

   @Override
   public int getX() {
      throw new LinkageError("LOL");
   }

   @Override
   public int getY() {
      throw new LinkageError("LOL");
   }

   @Override
   public int getZ() {
      throw new LinkageError("LOL");
   }

   public static void m_6(Module[] moduleArray) {
      f_2 = moduleArray;
   }

   public static Module[] m_9_C3_sArr() {
      return f_2;
   }

   static {
      m_6(new Module[5]);
   }
}
