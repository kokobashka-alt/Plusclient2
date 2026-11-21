package baritone.api.utils;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;

public class SettingsUtil {
   public static final String SETTINGS_DEFAULT_NAME = "settings.txt";
   private static final Pattern SETTING_PATTERN = Pattern.compile("^(?<setting>[^ ]+) +(?<value>.+)");

   private static boolean isComment(String var0) {
      return var0.startsWith("#") || var0.startsWith("//");
   }

   private static void forEachLine(Path var0, Consumer<String> var1) throws java.io.IOException {
      String var3;
      try (BufferedReader var2 = Files.newBufferedReader(var0)) {
         while ((var3 = var2.readLine()) != null) {
            if (!var3.isEmpty() && !isComment(var3)) {
               var1.accept(var3);
            }
         }
      }
   }

   public static void readAndApply(Settings var0, String var1) {
      try {
         forEachLine(settingsByName(var1), var1x -> {
            Matcher var2 = SETTING_PATTERN.matcher(var1x);
            if (!var2.matches()) {
               Helper.HELPER.logDirect("Invalid syntax in setting file: " + var1x);
            } else {
               String var3x = var2.group("setting").toLowerCase();
               String var4x = var2.group("value");
               if ("allowjumpat256".equals(var3x)) {
                  var3x = "allowjumpatbuildlimit";
               }

               try {
                  parseAndApply(var0, var3x, var4x);
               } catch (Exception var6) {
                  Helper.HELPER.logDirect("Unable to parse line " + var1x);
                  var6.printStackTrace();
               }
            }
         });
      } catch (NoSuchFileException var3) {
         Helper.HELPER.logDirect("Baritone settings file not found, resetting.");
      } catch (Exception var4) {
         Helper.HELPER.logDirect("Exception while reading Baritone settings, some settings may be reset to default values!");
         var4.printStackTrace();
      }
   }

   public static synchronized void save(Settings var0) {
      try (BufferedWriter var1 = Files.newBufferedWriter(settingsByName("settings.txt"))) {
         for (Settings.Setting var3 : modifiedSettings(var0)) {
            var1.write(settingToString(var3) + "\n");
         }
      } catch (Exception var6) {
         Helper.HELPER.logDirect("Exception thrown while saving Baritone settings!");
         var6.printStackTrace();
      }
   }

   private static Path settingsByName(String var0) {
      return Minecraft.getInstance().gameDirectory.toPath().resolve("baritone").resolve(var0);
   }

   public static List<Settings.Setting> modifiedSettings(Settings var0) {
      ArrayList var1 = new ArrayList();

      for (Settings.Setting var3 : var0.allSettings) {
         if (var3.value == null) {
            System.out.println("NULL SETTING?" + var3.getName());
         } else if (!var3.isJavaOnly() && var3.value != var3.defaultValue) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public static String settingTypeToString(Settings.Setting var0) {
      return var0.getType().getTypeName().replaceAll("(?:\\w+\\.)+(\\w+)", "$1");
   }

   public static <T> String settingValueToString(Settings.Setting<T> var0, T var1) {
      SettingsUtil.Parser var2 = SettingsUtil.Parser.getParser(var0.getType());
      if (var2 == null) {
         throw new IllegalStateException("Missing " + var0.getValueClass() + " " + var0.getName());
      } else {
         return var2.toString(var0.getType(), var1);
      }
   }

   public static String settingValueToString(Settings.Setting var0) {
      return settingValueToString(var0, var0.value);
   }

   public static String settingDefaultToString(Settings.Setting var0) {
      return settingValueToString(var0, var0.defaultValue);
   }

   public static String maybeCensor(int var0) {
      return BaritoneAPI.getSettings().censorCoordinates.value ? "<censored>" : Integer.toString(var0);
   }

   public static String settingToString(Settings.Setting var0) {
      return var0.isJavaOnly() ? var0.getName() : var0.getName() + " " + settingValueToString(var0);
   }

   @Deprecated
   public static boolean javaOnlySetting(Settings.Setting var0) {
      return var0.isJavaOnly();
   }

   public static void parseAndApply(Settings var0, String var1, String var2) {
      Settings.Setting var3 = var0.byLowerName.get(var1);
      if (var3 == null) {
         throw new IllegalStateException("No setting by that name");
      }

      var var4 = var3.getValueClass();
      SettingsUtil.Parser var5 = SettingsUtil.Parser.getParser(var3.getType());
      Object var6 = var5.parse(var3.getType(), var2);
      if (!var4.isInstance(var6)) {
         throw new IllegalStateException(var5 + " parser returned incorrect type, expected " + var4 + " got " + var6 + " which is " + var6.getClass());
      }

      var3.value = var6;
   }

   private interface ISettingParser<T> {
      T parse(Type var1, String var2);

      String toString(Type var1, T var2);

      boolean accepts(Type var1);
   }

   private enum Parser implements SettingsUtil.ISettingParser {
      DOUBLE(Double.class, Double::parseDouble),
      BOOLEAN(Boolean.class, Boolean::parseBoolean),
      INTEGER(Integer.class, Integer::parseInt),
      FLOAT(Float.class, Float::parseFloat),
      LONG(Long.class, Long::parseLong),
      STRING(String.class, String::new),
      MIRROR(Mirror.class, Mirror::valueOf, Enum::name),
      ROTATION(net.minecraft.world.level.block.Rotation.class, net.minecraft.world.level.block.Rotation::valueOf, Enum::name),
      COLOR(
         Color.class,
         var0 -> new Color(Integer.parseInt(var0.split(",")[0]), Integer.parseInt(var0.split(",")[1]), Integer.parseInt(var0.split(",")[2])),
         var0 -> var0.getRed() + "," + var0.getGreen() + "," + var0.getBlue()
      ),
      VEC3I(
         Vec3i.class,
         var0 -> new Vec3i(Integer.parseInt(var0.split(",")[0]), Integer.parseInt(var0.split(",")[1]), Integer.parseInt(var0.split(",")[2])),
         var0 -> var0.getX() + "," + var0.getY() + "," + var0.getZ()
      ),
      BLOCK(Block.class, var0 -> BlockUtils.stringToBlockRequired(var0.trim()), BlockUtils::blockToString),
      ITEM(
         Item.class,
         var0 -> BuiltInRegistries.ITEM.get(Identifier.parse(var0.trim())).map(Reference::value).orElse(null),
         var0 -> BuiltInRegistries.ITEM.getKey(var0).toString()
      ),
      LIST {
         @Override
         public Object parse(Type var1, String var2) {
            Type var3 = ((ParameterizedType)var1).getActualTypeArguments()[0];
            SettingsUtil.Parser var4 = SettingsUtil.Parser.getParser(var3);
            return Stream.of(var2.split(",")).map(var2x -> var4.parse(var3, var2x)).collect(Collectors.toList());
         }

         @Override
         public String toString(Type var1, Object var2) {
            Type var3 = ((ParameterizedType)var1).getActualTypeArguments()[0];
            SettingsUtil.Parser var4 = SettingsUtil.Parser.getParser(var3);
            return (String)((List)var2).stream().map(var2x -> var4.toString(var3, var2x)).collect(Collectors.joining(","));
         }

         @Override
         public boolean accepts(Type var1) {
            return List.class.isAssignableFrom(TypeUtils.resolveBaseClass(var1));
         }
      },
      MAPPING {
         @Override
         public Object parse(Type var1, String var2) {
            Type var3 = ((ParameterizedType)var1).getActualTypeArguments()[0];
            Type var4 = ((ParameterizedType)var1).getActualTypeArguments()[1];
            SettingsUtil.Parser var5 = SettingsUtil.Parser.getParser(var3);
            SettingsUtil.Parser var6 = SettingsUtil.Parser.getParser(var4);
            return Stream.of(var2.split(",(?=[^,]*->)"))
               .map(var0 -> var0.split("->"))
               .collect(Collectors.toMap(var2x -> var5.parse(var3, var2x[0]), var2x -> var6.parse(var4, var2x[1])));
         }

         @Override
         public String toString(Type var1, Object var2) {
            Type var3 = ((ParameterizedType)var1).getActualTypeArguments()[0];
            Type var4 = ((ParameterizedType)var1).getActualTypeArguments()[1];
            SettingsUtil.Parser var5 = SettingsUtil.Parser.getParser(var3);
            SettingsUtil.Parser var6 = SettingsUtil.Parser.getParser(var4);
            return ((Map<?, ?>)var2)
               .entrySet()
               .stream()
               .map(var4x -> var5.toString(var3, var4x.getKey()) + "->" + var6.toString(var4, var4x.getValue()))
               .collect(Collectors.joining(","));
         }

         @Override
         public boolean accepts(Type var1) {
            return Map.class.isAssignableFrom(TypeUtils.resolveBaseClass(var1));
         }
      };

      private final Class<?> cla$$;
      private final Function<String, Object> parser;
      private final Function<Object, String> toString;

      Parser() {
         this.cla$$ = null;
         this.parser = null;
         this.toString = null;
      }

      <T> Parser(Class<T> var3, Function<String, T> var4) {
         this(var3, var4, Object::toString);
      }

      <T> Parser(Class<T> var3, Function<String, T> var4, Function<T, String> var5) {
         this.cla$$ = var3;
         this.parser = var4::apply;
         this.toString = var1x -> (String)var5.apply((T)var1x);
      }

      @Override
      public Object parse(Type var1, String var2) {
         Object var3 = this.parser.apply(var2);
         Objects.requireNonNull(var3);
         return var3;
      }

      @Override
      public String toString(Type var1, Object var2) {
         return this.toString.apply(var2);
      }

      @Override
      public boolean accepts(Type var1) {
         return var1 instanceof Class && this.cla$$.isAssignableFrom((Class<?>)var1);
      }

      public static SettingsUtil.Parser getParser(Type var0) {
         return Stream.of(values()).filter(var1 -> var1.accepts(var0)).findFirst().orElse(null);
      }
   }
}
