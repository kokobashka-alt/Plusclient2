package wtf.onetap.event;

import wtf.onetap.module.Module;

public abstract class Event {
   private static Module[] moduleArray;

   public static void m_3(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_8_C3_sArr() {
      return moduleArray;
   }
}
