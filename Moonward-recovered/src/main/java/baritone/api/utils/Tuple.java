package baritone.api.utils;

import java.util.Objects;

public class Tuple<A, B> {
   private A a;
   private B b;

   public Tuple(A var1, B var2) {
      this.a = (A)var1;
      this.b = (B)var2;
   }

   public A getA() {
      return this.a;
   }

   public B getB() {
      return this.b;
   }

   public void setA(A var1) {
      this.a = (A)var1;
   }

   public void setB(B var1) {
      this.b = (B)var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof Tuple var2) ? false : Objects.equals(this.a, var2.a) && Objects.equals(this.b, var2.b);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.a, this.b);
   }
}
