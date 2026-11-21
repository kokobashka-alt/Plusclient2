package wtf.onetap.obf;

public class C6_j {
   public long f_0;

   public C6_j() {
      this.f_0 = System.currentTimeMillis();
      this.m_8_void();
   }

   public void m_8_void() {
      this.f_0 = System.currentTimeMillis();
   }

   public boolean m_9_boolean(long longValue) {
      return System.currentTimeMillis() - this.f_0 > longValue;
   }
}
