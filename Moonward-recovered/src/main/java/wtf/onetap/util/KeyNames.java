package wtf.onetap.util;

import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyNames {
   private static int value;

   private KeyNames() {
   }

   public static String getKeyName(int value2) {
      if (value2 == -1) {
         return "";
      }

      if (value2 < 0) {
         return "Mouse " + -value2;
      }

      String text = Type.KEYSYM.getOrCreate(value2).getName().replace("key.keyboard.", "");
      StringBuilder stringBuilder = new StringBuilder();

      for (String text2 : text.split("[._]")) {
         if (!text2.isEmpty()) {
            stringBuilder.append(Character.toUpperCase(text2.charAt(0))).append(text2.substring(1));
         }
      }

      return stringBuilder.toString();
   }

   public static boolean m_0_boolean(int value2) {
      Window window = Minecraft.getInstance().getWindow();
      if (value2 == -1 || window == null) {
         return false;
      } else {
         return value2 >= 0 ? GLFW.glfwGetKey(window.handle(), value2) == 1 : GLFW.glfwGetMouseButton(window.handle(), -value2 - 1) == 1;
      }
   }

   public static boolean isLeftMouseDown() {
      Window window = Minecraft.getInstance().getWindow();
      return window != null && GLFW.glfwGetMouseButton(window.handle(), 0) == 1;
   }

   public static void m_6_void(int value2) {
      value = value2;
   }

   public static int m_0_int() {
      return value;
   }

   public static int m_7_int() {
      return 0;
   }

   static {
      m_6_void(68);
   }
}
