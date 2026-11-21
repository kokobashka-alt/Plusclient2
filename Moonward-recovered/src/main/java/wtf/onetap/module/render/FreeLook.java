package wtf.onetap.module.render;

import net.minecraft.client.CameraType;
import org.lwjgl.glfw.GLFW;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "FreeLook", description = "Вращение камеры без поворота тела", category = Category.RENDER)
public class FreeLook extends Module {
   private final KeybindSetting key = new KeybindSetting("Клавиша", 342).build();
   private boolean flag;
   private CameraType cameraType = CameraType.FIRST_PERSON;

   @Override
   public void onDisable() {
      this.restoreCamera();
   }

   private boolean isKeyHeld() {
      int value = (Integer)this.key.getValue();
      return value < 0 ? false : GLFW.glfwGetKey(MinecraftAccess.MINECRAFT.getWindow().handle(), value) == 1;
   }

   private void enterFreeLook() {
      if (!this.flag && MinecraftAccess.MINECRAFT.player != null) {
         this.flag = true;
         this.cameraType = MinecraftAccess.MINECRAFT.options.getCameraType();
         MinecraftAccess.MINECRAFT.options.setCameraType(CameraType.THIRD_PERSON_BACK);
         Onetap.ROTATION_MODES.setSpoofedRotation(MinecraftAccess.MINECRAFT.player.getYRot(), MinecraftAccess.MINECRAFT.player.getXRot());
      }
   }

   private void restoreCamera() {
      if (this.flag) {
         this.flag = false;
         MinecraftAccess.MINECRAFT.options.setCameraType(this.cameraType);
         Onetap.ROTATION_MODES.syncWithPlayer();
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         this.restoreCamera();
      } else {
         if (this.isKeyHeld()) {
            if (!this.flag) {
               this.enterFreeLook();
            } else {
               MinecraftAccess.MINECRAFT.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            }
         } else {
            this.restoreCamera();
         }
      }
   }

   public void onMouseDelta(double doubleValue, double doubleValue2) {
      if (this.isEnabled() && MinecraftAccess.MINECRAFT.player != null) {
         if (!this.flag) {
            if (!this.isKeyHeld()) {
               return;
            }

            this.enterFreeLook();
         }

         Onetap.ROTATION_MODES.applyMouseDelta(doubleValue, doubleValue2);
      }
   }

   public boolean isActive() {
      return this.isEnabled() && (this.flag || this.isKeyHeld());
   }

   private static String b(byte[] byteArray) {
      int value = 0;
      int value2;
      char[] charArray = new char[value2 = byteArray.length];

      for (int i = 0; i < value2; i++) {
         int value3;
         if ((value3 = 255 & byteArray[i]) < 192) {
            charArray[value++] = (char)value3;
         } else if (value3 < 224) {
            char ch = (char)((char)(value3 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value++] = ch;
         } else if (i < value2 - 2) {
            char ch2 = (char)((char)(value3 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value++] = ch2;
         }
      }

      return new String(charArray, 0, value);
   }
}
