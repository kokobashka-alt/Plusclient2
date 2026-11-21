package wtf.onetap.obf;

public class C69 {
   private long f_3;

   public C69() {
      this.f_3 = System.currentTimeMillis();
   }

   public void m_8_void() {
      this.f_3 = System.currentTimeMillis();
   }

   public boolean m_9_boolean(long longValue) {
      return System.currentTimeMillis() - this.f_3 > longValue;
   }
}
