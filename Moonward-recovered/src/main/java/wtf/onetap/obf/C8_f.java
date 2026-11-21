package wtf.onetap.obf;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import wtf.onetap.Onetap;
import wtf.onetap.util.ChatUtil;

public class C8_f {
   private C8_f() {
   }

   public static Path m_8_Path(Path path) {
      Path path2 = path.toAbsolutePath().normalize();
      String text = SharedConstants.getCurrentVersion().name().toLowerCase(Locale.ROOT);

      for (Path path3 : m_4_List()) {
         for (Path path4 : m_6(path3, text)) {
            Path path5 = path4.toAbsolutePath().normalize();
            if (!path5.equals(path2) && Files.isDirectory(path5)) {
               return path5;
            }
         }
      }

      String text2 = System.getenv("APPDATA");
      if (text2 != null && !text2.isBlank()) {
         Path path6 = Path.of(text2, ".minecraft", "resourcepacks");

         try {
            return Files.createDirectories(path6);
         } catch (IOException e) {
            Onetap.LOGGER.warn("Не удалось создать папку ресурсов {}", path6, e);
            return path;
         }
      } else {
         return path;
      }
   }

   private static List<Path> m_4_List() {
      ChatUtil.m_3_IArr();
      ArrayList list = new ArrayList();

      for (String text : List.of("APPDATA", "LOCALAPPDATA", "USERPROFILE")) {
         String text2 = System.getenv(text);
         if (text2 != null && !text2.isBlank() && Files.isDirectory(Path.of(text2))) {
            try (Stream stream = Files.list(Path.of(text2))) {
               ((java.util.stream.Stream<Path>)(java.util.stream.Stream)stream.filter(object -> Files.isDirectory((java.nio.file.Path)object))).filter(C8_f::m_4).forEach(list::add);
            } catch (IOException | UncheckedIOException e) {
               Onetap.LOGGER.warn("Не удалось проверить каталоги лаунчеров в {}", text2, e);
            }
         }
      }

      list.sort(Comparator.<Path, Boolean>comparing(path -> !path.getFileName().toString().equalsIgnoreCase(".minecraft")).thenComparing(Path::toString));
      return list;
   }

   private static List<Path> m_6(Path path, String text) {
      ArrayList list = new ArrayList();
      Path path2 = path.resolve("home");
      if (Files.isDirectory(path2)) {
         try (Stream stream = Files.list(path2)) {
            stream.filter(object -> Files.isDirectory((java.nio.file.Path)object))
               .filter(object2 -> ((java.nio.file.Path)object2).getFileName().toString().toLowerCase(Locale.ROOT).contains(text))
               .sorted(
                  Comparator.<Path, Boolean>comparing(path3 -> !path3.getFileName().toString().toLowerCase(Locale.ROOT).contains("fabric"))
                     .thenComparing(Path::toString)
               )
               .forEach(object3 -> list.add(((java.nio.file.Path)object3).resolve("resourcepacks")));
         } catch (IOException | UncheckedIOException e) {
            Onetap.LOGGER.warn("Не удалось проверить подпапки версий в {}", path2, e);
         }
      }

      list.add(path.resolve("resourcepacks"));
      return list;
   }

   private static boolean m_4(Path path) {
      String text = path.getFileName().toString().toLowerCase(Locale.ROOT);
      return text.equals(".minecraft") || text.contains("tlauncher") || text.contains("legacy") || text.contains("klauncher");
   }
}
