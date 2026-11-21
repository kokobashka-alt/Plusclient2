package baritone.utils.schematic.schematica;

import baritone.api.schematic.IStaticSchematic;
import baritone.api.utils.Tuple;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import wtf.onetap.obf.C0;
import wtf.onetap.obf.C5g;
import wtf.onetap.obf.C6z;
import wtf.onetap.obf.w;

public enum SchematicaHelper {
   ;

   public static boolean isSchematicaPresent() {
      try {
         Class.forName(w.a(C6z.class.getName()));
         return true;
      } catch (ClassNotFoundException | NoClassDefFoundError var1) {
         return false;
      }
   }

   public static Optional<Tuple<IStaticSchematic, BlockPos>> getOpenSchematic() {
      return Optional.ofNullable(C0.f_6).map(var0 -> new Tuple<>(new SchematicAdapter(var0), var0.f_5));
   }
}
