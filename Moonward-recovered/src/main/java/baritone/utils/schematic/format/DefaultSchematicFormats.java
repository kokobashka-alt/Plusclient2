package baritone.utils.schematic.format;

import baritone.api.schematic.IStaticSchematic;
import baritone.api.schematic.format.ISchematicFormat;
import baritone.utils.schematic.format.defaults.LitematicaSchematic;
import baritone.utils.schematic.format.defaults.MCEditSchematic;
import baritone.utils.schematic.format.defaults.SpongeSchematic;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import org.apache.commons.io.FilenameUtils;

public enum DefaultSchematicFormats implements ISchematicFormat {
   MCEDIT("schematic") {
      @Override
      public IStaticSchematic parse(InputStream var1) throws java.io.IOException {
         return new MCEditSchematic(NbtIo.readCompressed(var1, NbtAccounter.unlimitedHeap()));
      }
   },
   SPONGE("schem") {
      @Override
      public IStaticSchematic parse(InputStream var1) throws java.io.IOException {
         CompoundTag var2 = NbtIo.readCompressed(var1, NbtAccounter.unlimitedHeap());
         int var3 = var2.getInt("Version").orElse(-1);
         switch (var3) {
            case 1:
            case 2:
               return new SpongeSchematic(var2);
            default:
               throw new UnsupportedOperationException("Unsupported Version of a Sponge Schematic");
         }
      }
   },
   LITEMATICA("litematic") {
      @Override
      public IStaticSchematic parse(InputStream var1) throws java.io.IOException {
         CompoundTag var2 = NbtIo.readCompressed(var1, NbtAccounter.unlimitedHeap());
         int var3 = var2.getInt("Version").orElse(-1);
         switch (var3) {
            case 4:
            case 5:
               throw new UnsupportedOperationException("This litematic Version is too old.");
            case 6:
               throw new UnsupportedOperationException("This litematic Version is too old.");
            case 7:
               return new LitematicaSchematic(var2);
            default:
               throw new UnsupportedOperationException("Unsuported Version of a Litematica Schematic");
         }
      }
   };

   private final String extension;

   DefaultSchematicFormats(String var3) {
      this.extension = var3;
   }

   @Override
   public boolean isFileType(File var1) {
      return this.extension.equalsIgnoreCase(FilenameUtils.getExtension(var1.getAbsolutePath()));
   }

   @Override
   public List<String> getFileExtensions() {
      return Collections.singletonList(this.extension);
   }
}
