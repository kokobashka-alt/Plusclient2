package wtf.onetap.module.combat;

import net.minecraft.util.Mth;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.network.ServerTpsTracker;

@ModuleInformation(name = "SyncTPS", description = "Синхронизирует удары с TPS сервера", category = Category.COMBAT)
public class SyncTPS extends Module {
   public float getTps() {
      return Mth.clamp(ServerTpsTracker.SERVER_TPS_TRACKER.getTps(), 0.1F, 20.0F);
   }

   public long m_5_long(long longValue) {
      return m_9(longValue, this.isEnabled(), this.getTps());
   }

   public static long m_9(long longValue, boolean flag, float floatValue) {
      if (!flag) {
         return longValue;
      }

      floatValue = Mth.clamp(floatValue, 0.1F, 20.0F);
      if (floatValue >= 20.0F) {
         return longValue;
      }

      float floatValue2 = 20.0F / floatValue;
      float floatValue3 = 1.0F + (20.0F - floatValue) * 0.05F;
      return Math.min((long)((float)longValue * floatValue2 * floatValue3), 3000L);
   }

   public boolean m_1(long longValue, long longValue2, long longValue3) {
      long longValue4 = this.m_5_long(longValue2);
      return longValue3 >= longValue + longValue4;
   }
}
