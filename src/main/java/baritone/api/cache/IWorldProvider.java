package baritone.api.cache;

import java.util.function.Consumer;

public interface IWorldProvider {
   IWorldData getCurrentWorld();

   default void ifWorldLoaded(Consumer<IWorldData> var1) {
      IWorldData var2 = this.getCurrentWorld();
      if (var2 != null) {
         var1.accept(var2);
      }
   }
}
