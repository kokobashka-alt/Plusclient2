package baritone.utils.schematic.format.defaults;

import baritone.utils.schematic.StaticSchematic;
import baritone.utils.type.VarInt;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class SpongeSchematic extends StaticSchematic {
   public SpongeSchematic(CompoundTag var1) {
      this.x = var1.getInt("Width").orElse(0);
      this.y = var1.getInt("Height").orElse(0);
      this.z = var1.getInt("Length").orElse(0);
      this.states = new BlockState[this.x][this.z][this.y];
      Int2ObjectArrayMap var2 = new Int2ObjectArrayMap();
      CompoundTag var3 = var1.getCompound("Palette").orElse(new CompoundTag());

      for (String var5 : var3.keySet()) {
         int var6 = var3.getInt(var5).orElse(0);
         SpongeSchematic.SerializedBlockState var7 = SpongeSchematic.SerializedBlockState.getFromString(var5);
         if (var7 == null) {
            throw new IllegalArgumentException("Unable to parse palette tag");
         }

         BlockState var8 = var7.deserialize();
         if (var8 == null) {
            throw new IllegalArgumentException("Unable to deserialize palette tag");
         }

         var2.put(var6, var8);
      }

      byte[] var12 = var1.getByteArray("BlockData").orElseThrow();
      int[] var13 = new int[this.x * this.y * this.z];
      int var14 = 0;

      for (int var15 = 0; var15 < var13.length; var15++) {
         if (var14 >= var12.length) {
            throw new IllegalArgumentException("No remaining bytes in BlockData for complete schematic");
         }

         VarInt var17 = VarInt.read(var12, var14);
         var13[var15] = var17.getValue();
         var14 += var17.getSize();
      }

      for (int var16 = 0; var16 < this.y; var16++) {
         for (int var18 = 0; var18 < this.z; var18++) {
            for (int var9 = 0; var9 < this.x; var9++) {
               int var10 = (var16 * this.z + var18) * this.x + var9;
               BlockState var11 = (BlockState)var2.get(var13[var10]);
               if (var11 == null) {
                  throw new IllegalArgumentException("Invalid Palette Index " + var10);
               }

               this.states[var9][var18][var16] = var11;
            }
         }
      }
   }

   private static final class SerializedBlockState {
      private static final Pattern REGEX = Pattern.compile("(?<location>(\\w+:)?\\w+)(\\[(?<properties>(\\w+=\\w+,?)+)])?");
      private final Identifier identifier;
      private final Map<String, String> properties;
      private BlockState blockState;

      private SerializedBlockState(Identifier var1, Map<String, String> var2) {
         this.identifier = var1;
         this.properties = var2;
      }

      private BlockState deserialize() {
         if (this.blockState == null) {
            Block var1 = BuiltInRegistries.BLOCK.get(this.identifier).map(Reference::value).orElse(Blocks.AIR);
            this.blockState = var1.defaultBlockState();
            this.properties.keySet().stream().sorted(String::compareTo).forEachOrdered(var2 -> {
               Property var3 = var1.getStateDefinition().getProperty(var2);
               if (var3 != null) {
                  this.blockState = setPropertyValue(this.blockState, var3, this.properties.get(var2));
               }
            });
         }

         return this.blockState;
      }

      private static SpongeSchematic.SerializedBlockState getFromString(String var0) {
         Matcher var1 = REGEX.matcher(var0);
         if (!var1.matches()) {
            return null;
         }

         try {
            String var2 = var1.group("location");
            String var3 = var1.group("properties");
            Identifier var4 = Identifier.parse(var2);
            HashMap var5 = new HashMap();
            if (var3 != null) {
               for (String var9 : var3.split(",")) {
                  String[] var10 = var9.split("=");
                  var5.put(var10[0], var10[1]);
               }
            }

            return new SpongeSchematic.SerializedBlockState(var4, var5);
         } catch (Exception var11) {
            var11.printStackTrace();
            return null;
         }
      }

      private static <T extends Comparable<T>> BlockState setPropertyValue(BlockState var0, Property<T> var1, String var2) {
         var var3 = var1.getValue(var2);
         if (var3.isPresent()) {
            return var0.setValue(var1, (T)var3.get());
         } else {
            throw new IllegalArgumentException("Invalid value for property " + var1);
         }
      }
   }
}
