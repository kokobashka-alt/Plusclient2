package baritone.utils.schematic.schematica;

import baritone.api.schematic.IStaticSchematic;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import wtf.onetap.obf.C5g;

public class SchematicAdapter implements IStaticSchematic {
   private final C5g schematic;

   public SchematicAdapter(C5g var1) {
      this.schematic = var1;
   }

   @Override
   public BlockState desiredState(int var1, int var2, int var3, BlockState var4, List<BlockState> var5) {
      return this.getDirect(var1, var2, var3);
   }

   @Override
   public BlockState getDirect(int var1, int var2, int var3) {
      return this.schematic.m_5_C4s().m_4_BlockState(new BlockPos(var1, var2, var3));
   }

   @Override
   public int widthX() {
      return this.schematic.m_5_C4s().m_5_int();
   }

   @Override
   public int heightY() {
      return this.schematic.m_5_C4s().m_8_int();
   }

   @Override
   public int lengthZ() {
      return this.schematic.m_5_C4s().m_2_int();
   }
}
