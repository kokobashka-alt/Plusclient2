package baritone.api.command.datatypes;

import baritone.api.command.argument.IArgConsumer;
import baritone.api.utils.Helper;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;

public enum RelativeFile implements IDatatypePost<File, File> {
   INSTANCE;

   public File apply(IDatatypeContext var1, File var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      if (var2 == null) {
         var2 = new File("./");
      }

      Path var3;
      try {
         var3 = FileSystems.getDefault().getPath(var1.getConsumer().getString());
      } catch (InvalidPathException var5) {
         throw new IllegalArgumentException("invalid path");
      }

      return getCanonicalFileUnchecked(var2.toPath().resolve(var3).toFile());
   }

   @Override
   public Stream<String> tabComplete(IDatatypeContext var1) {
      return Stream.empty();
   }

   private static File getCanonicalFileUnchecked(File var0) {
      try {
         return var0.getCanonicalFile();
      } catch (IOException var2) {
         throw new UncheckedIOException(var2);
      }
   }

   public static Stream<String> tabComplete(IArgConsumer var0, File var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      File var2 = getCanonicalFileUnchecked(var1);
      String var3 = var0.getString();
      Path var4 = FileSystems.getDefault().getPath(var3);
      Path var5 = var4.isAbsolute() ? var4.getRoot() : var2.toPath();
      boolean var6 = !var3.isEmpty() && !var3.endsWith(File.separator);
      File var7 = var4.isAbsolute() ? var4.toFile() : new File(var2, var3);
      return Stream.of(Objects.requireNonNull(getCanonicalFileUnchecked(var6 ? var7.getParentFile() : var7).listFiles()))
         .map(var2x -> (var4.isAbsolute() ? var2x : var5.relativize(var2x.toPath()).toString()) + (var2x.isDirectory() ? File.separator : ""))
         .filter(var1x -> var1x.toLowerCase(Locale.US).startsWith(var3.toLowerCase(Locale.US)))
         .filter(var0x -> !var0x.contains(" "));
   }

   @Deprecated
   public static File gameDir() {
      return gameDir(Helper.mc);
   }

   public static File gameDir(Minecraft var0) {
      File var1 = var0.gameDirectory.getAbsoluteFile();
      return var1.getName().equals(".") ? var1.getParentFile() : var1;
   }
}
