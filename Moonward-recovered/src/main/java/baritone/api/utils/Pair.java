package baritone.api.utils;

import java.util.Objects;

public class Pair<A, B> {
   private final A a;
   private final B b;

   public Pair(A var1, B var2) {
      this.a = (A)var1;
      this.b = (B)var2;
   }

   public A first() {
      return this.a;
   }

   public B second() {
      return this.b;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && var1.getClass() == Pair.class) {
         Pair var2 = (Pair)var1;
         return Objects.equals(this.a, var2.a) && Objects.equals(this.b, var2.b);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 31 * Objects.hashCode(this.a) + Objects.hashCode(this.b);
   }
}
