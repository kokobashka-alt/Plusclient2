package wtf.onetap.module.render;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;

@ModuleInformation(name = "SmoothCamera", description = "Делает движение камеры плавным", category = Category.RENDER)
public class SmoothCamera extends Module {
   public final FloatSetting smoothness = new FloatSetting("Плавность", 0.2F, 0.15F, 0.5F, 0.01F).build();
   private float floatValue;
   private float floatValue2;
   private Vec3 vec3;

   @Override
   public void onEnable() {
      this.vec3 = null;
   }

   @Override
   public void onDisable() {
      this.vec3 = null;
   }

   public void m_5(float floatValue3, float floatValue4, Vec3 vec32) {
      if (this.vec3 == null) {
         this.floatValue = floatValue3;
         this.floatValue2 = floatValue4;
         this.vec3 = vec32;
      } else {
         float floatValue5 = (Float)this.smoothness.getValue();
         this.floatValue = this.floatValue + Mth.wrapDegrees(floatValue3 - this.floatValue) * floatValue5;
         this.floatValue2 = this.floatValue2 + (floatValue4 - this.floatValue2) * floatValue5;
         this.vec3 = new Vec3(
            this.vec3.x + (vec32.x - this.vec3.x) * floatValue5, this.vec3.y + (vec32.y - this.vec3.y) * floatValue5, this.vec3.z + (vec32.z - this.vec3.z) * floatValue5
         );
      }
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public Vec3 getVec3() {
      return this.vec3;
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
