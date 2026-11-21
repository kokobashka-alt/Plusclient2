package recovered.jaff;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;

public final class CallSites {
   private CallSites() {
   }

   public static CallSite jaffBootstrap(Lookup var0, String var1, MethodType var2, String var3) throws Throwable {
      String[] var4 = var3.split("\n", 4);
      if (var4.length != 4) {
         throw new BootstrapMethodError("Malformed recovered call site");
      }

      ClassLoader var5 = var0.lookupClass().getClassLoader();
      var var6 = Class.forName(var4[1].replace('/', '.'), false, var5);
      MethodType var7 = MethodType.fromMethodDescriptorString(var4[3], var5);

      MethodHandle var8 = switch (var4[0]) {
         case "S" -> var0.findStatic(var6, var4[2], var7);
         case "V", "I" -> var0.findVirtual(var6, var4[2], var7);
         case "P" -> var0.findSpecial(var6, var4[2], var7, var0.lookupClass());
         default -> throw new BootstrapMethodError("Unknown recovered call kind: " + var4[0]);
      };
      return new ConstantCallSite(var8.asType(var2));
   }
}
