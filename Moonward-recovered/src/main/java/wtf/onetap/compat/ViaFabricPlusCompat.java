package wtf.onetap.compat;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import wtf.onetap.obf.w;

public class ViaFabricPlusCompat {
   private static final boolean FLAG;

   private ViaFabricPlusCompat() {
   }

   public static boolean isAvailable() {
      return FLAG;
   }

   public static void m_2(Screen screen) {
      if (FLAG && screen != null) {
         if (!m_4("com.viaversion.viafabricplus.screen.impl.ProtocolSelectionScreen", screen)) {
            if (!m_4("com.viaversion.viafabricplus.screen.impl.ViaFabricPlusScreen", screen)) {
               try {
                  Object object = Class.forName("com.viaversion.viafabricplus.ViaFabricPlus").getMethod("api").invoke(null);
                  Class clazz = object.getClass();
                  Class[] classArray = new Class[0];
                  Object object2 = clazz.getMethod(w.b("screens", clazz, classArray), classArray).invoke(object);
                  clazz = object2.getClass();
                  classArray = new Class[]{Screen.class};
                  clazz.getMethod(w.b("openViaFabricPlusScreen", clazz, classArray), classArray).invoke(object2, screen);
               } catch (ReflectiveOperationException reflectiveOperationException) {
               }
            }
         }
      }
   }

   public static void m_2(Screen screen, ServerData serverData) {
      if (FLAG && screen != null && serverData != null) {
         try {
            var clazz = Class.forName("com.viaversion.viafabricplus.screen.impl.PerServerVersionScreen");
            Constructor constructor = clazz.getConstructor(Screen.class, Consumer.class, Supplier.class);
            Object object = constructor.newInstance(screen, (Consumer<Object>)object2 -> m_9(serverData, object2), (Supplier<Object>)() -> getForcedVersion(serverData));
            Minecraft.getInstance().gui.setScreen((Screen)object);
         } catch (ReflectiveOperationException reflectiveOperationException) {
         }
      }
   }

   public static String m_7_String() {
      if (!FLAG) {
         return "";
      }

      try {
         Object object = Class.forName("com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator").getMethod("getTargetVersion").invoke(null);
         return m_7_String(object);
      } catch (ReflectiveOperationException reflectiveOperationException) {
         try {
            Object object2 = Class.forName("com.viaversion.viafabricplus.ViaFabricPlus").getMethod("api").invoke(null);
            var clazz = object2.getClass();
            Class[] classArray = new Class[0];
            return m_7_String(clazz.getMethod(w.b("targetVersion", clazz, classArray), classArray).invoke(object2));
         } catch (ReflectiveOperationException reflectiveOperationException2) {
            return "";
         }
      }
   }

   public static String m_8(ServerData serverData) {
      return m_7_String(getForcedVersion(serverData));
   }

   public static void m_9(ServerData serverData, ServerData serverData2) {
      m_9(serverData2, getForcedVersion(serverData));
   }

   public static void m_6(ServerData serverData) {
      if (FLAG && serverData != null) {
         try {
            getServerDataClass().getMethod("viaFabricPlus$passDirectConnectScreen", boolean.class).invoke(serverData, true);
         } catch (ReflectiveOperationException reflectiveOperationException) {
         }
      }
   }

   private static boolean m_4(String text, Screen screen) {
      try {
         var clazz = Class.forName(text);

         Object object;
         try {
            object = clazz.getField("INSTANCE").get(null);
         } catch (ReflectiveOperationException reflectiveOperationException) {
            object = clazz.getDeclaredConstructor().newInstance();
         }

         clazz.getMethod("open", Screen.class).invoke(object, screen);
         return true;
      } catch (ReflectiveOperationException reflectiveOperationException2) {
         return false;
      }
   }

   private static Class<?> getServerDataClass() throws java.lang.ClassNotFoundException {
      return Class.forName("com.viaversion.viafabricplus.injection.access.core.IServerData");
   }

   private static Object getForcedVersion(ServerData serverData) {
      if (FLAG && serverData != null) {
         try {
            return getServerDataClass().getMethod("viaFabricPlus$forcedVersion").invoke(serverData);
         } catch (ReflectiveOperationException reflectiveOperationException) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static void m_9(ServerData serverData, Object object) {
      if (FLAG && serverData != null) {
         try {
            var clazz = Class.forName("com.viaversion.viaversion.api.protocol.version.ProtocolVersion");
            getServerDataClass().getMethod("viaFabricPlus$forceVersion", clazz).invoke(serverData, object);
         } catch (ReflectiveOperationException reflectiveOperationException) {
         }
      }
   }

   private static String m_7_String(Object object) {
      if (object == null) {
         return "";
      }

      try {
         var clazz = object.getClass();
         Class[] classArray = new Class[0];
         return clazz.getMethod(w.b("getName", clazz, classArray), classArray).invoke(object) instanceof String text ? text : "";
      } catch (ReflectiveOperationException reflectiveOperationException) {
         return "";
      }
   }

   static {
      boolean flag = false;

      try {
         flag = FabricLoader.getInstance().isModLoaded("viafabricplus");
      } catch (Throwable e) {
      }

      FLAG = flag;
   }
}
