package baritone.utils.schematic.format.defaults;

import baritone.utils.schematic.StaticSchematic;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.fixes.ItemIdFix;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MCEditSchematic extends StaticSchematic {
   public MCEditSchematic(CompoundTag var1) {
      String var2 = var1.getString("Materials").orElseThrow();
      if (!var2.equals("Alpha")) {
         throw new IllegalStateException("bad schematic " + var2);
      }

      this.x = var1.getInt("Width").orElse(0);
      this.y = var1.getInt("Height").orElse(0);
      this.z = var1.getInt("Length").orElse(0);
      byte[] var3 = var1.getByteArray("Blocks").orElseThrow();
      byte[] var4 = null;
      if (var1.contains("AddBlocks")) {
         byte[] var5 = var1.getByteArray("AddBlocks").orElseThrow();
         var4 = new byte[var5.length * 2];

         for (int var6 = 0; var6 < var5.length; var6++) {
            var4[var6 * 2 + 0] = (byte)(var5[var6] >> 4 & 15);
            var4[var6 * 2 + 1] = (byte)(var5[var6] >> 0 & 15);
         }
      }

      this.states = new BlockState[this.x][this.z][this.y];

      for (int var12 = 0; var12 < this.y; var12++) {
         for (int var13 = 0; var13 < this.z; var13++) {
            for (int var7 = 0; var7 < this.x; var7++) {
               int var8 = (var12 * this.z + var13) * this.x + var7;
               int var9 = var3[var8] & 255;
               if (var4 != null) {
                  var9 |= var4[var8] << 8;
               }

               Identifier var10 = Identifier.tryParse(ItemIdFix.getItem(var9));
               Block var11 = var10 == null ? Blocks.AIR : BuiltInRegistries.BLOCK.get(var10).map(Reference::value).orElse(Blocks.AIR);
               this.states[var7][var13][var12] = var11.defaultBlockState();
            }
         }
      }
   }
}
