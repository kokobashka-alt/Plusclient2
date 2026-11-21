package baritone.api.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeUtils {
   private TypeUtils() {
   }

   public static Class<?> resolveBaseClass(Type var0) {
      return var0 instanceof Class ? (Class)var0 : (var0 instanceof ParameterizedType ? (Class)((ParameterizedType)var0).getRawType() : null);
   }
}
