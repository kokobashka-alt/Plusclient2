package wtf.onetap.render;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public class PropellerCapTexture {
   public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "dynamic/propeller_cap");
   private static DynamicTexture dynamicTexture;
   private static final int[] VALUE_ARRAY = new int[9];
   private static long longValue;

   private PropellerCapTexture() {
   }

   public static void m_9(int[] intArray) {
      if (dynamicTexture == null) {
         dynamicTexture = new DynamicTexture(() -> "Onetap propeller cap", new NativeImage(128, 128, true));
         Minecraft.getInstance().getTextureManager().register(IDENTIFIER, dynamicTexture);
      } else if (Arrays.equals(VALUE_ARRAY, intArray) || System.nanoTime() - longValue < 50000000L) {
         return;
      }

      System.arraycopy(intArray, 0, VALUE_ARRAY, 0, 9);
      longValue = System.nanoTime();
      NativeImage nativeImage = dynamicTexture.getPixels();
      nativeImage.fillRect(0, 0, 128, 128, 0);

      for (CrownModel.DataRecord c7 : CrownModel.C7_LIST) {
         m_4(nativeImage, c7, intArray);
      }

      dynamicTexture.upload();
   }

   private static void m_4(NativeImage nativeImage, CrownModel.DataRecord c7, int[] intArray) {
      float[] floatArray = new float[4];
      float[] floatArray2 = new float[3];

      for (int i = 0; i < 6; i++) {
         m_6(c7, i, floatArray);
         int value = Math.round(floatArray[0] * 2.0F);
         int value2 = Math.round(floatArray[1] * 2.0F);
         int value3 = Math.round(floatArray[2] * 2.0F);
         int value4 = Math.round(floatArray[3] * 2.0F);

         for (int i2 = 0; i2 < value4; i2++) {
            for (int i3 = 0; i3 < value3; i3++) {
               float floatValue = (i3 + 0.5F) / 2.0F;
               float floatValue2 = (i2 + 0.5F) / 2.0F;
               m_7(c7, i, floatValue, floatValue2, floatArray2);
               boolean flag = i >= 2;
               int value5 = m_4(
                  c7,
                  intArray,
                  i,
                  floatArray2[0],
                  floatArray2[2],
                  flag ? floatValue2 : 0.0F,
                  flag ? c7.m_8e_float() : 0.0F,
                  i3,
                  i2,
                  value3,
                  value4,
                  value + i3,
                  value2 + i2
               );
               nativeImage.setPixel(value + i3, value2 + i2, value5);
            }
         }
      }
   }

   public static void m_6(CrownModel.DataRecord c7, int value, float[] floatArray) {
      float floatValue = c7.m_6n_int();
      float floatValue2 = c7.m_67_int();
      float floatValue3 = c7.m_8x();
      float floatValue4 = c7.m_8e_float();
      float floatValue5 = c7.m_8_l();
      switch (value) {
         case 0:
            m_0(floatArray, floatValue + floatValue5, floatValue2, floatValue3, floatValue5);
            break;
         case 1:
            m_0(floatArray, floatValue + floatValue5 + floatValue3, floatValue2, floatValue3, floatValue5);
            break;
         case 2:
            m_0(floatArray, floatValue, floatValue2 + floatValue5, floatValue5, floatValue4);
            break;
         case 3:
            m_0(floatArray, floatValue + floatValue5, floatValue2 + floatValue5, floatValue3, floatValue4);
            break;
         case 4:
            m_0(floatArray, floatValue + floatValue5 + floatValue3, floatValue2 + floatValue5, floatValue5, floatValue4);
            break;
         default:
            m_0(floatArray, floatValue + 2.0F * floatValue5 + floatValue3, floatValue2 + floatValue5, floatValue3, floatValue4);
      }
   }

   public static void m_7(CrownModel.DataRecord c7, int value, float floatValue, float floatValue2, float[] floatArray) {
      float floatValue3 = c7.m_8_j_float();
      float floatValue4 = c7.m_85();
      float floatValue5 = c7.m_8_y_float();
      float floatValue6 = floatValue3 + c7.m_8x();
      float floatValue7 = floatValue4 + c7.m_8e_float();
      float floatValue8 = floatValue5 + c7.m_8_l();
      switch (value) {
         case 0:
            m_0(floatArray, floatValue3 + floatValue, floatValue4, floatValue8 - floatValue2);
            break;
         case 1:
            m_0(floatArray, floatValue3 + floatValue, floatValue7, floatValue8 - floatValue2);
            break;
         case 2:
            m_0(floatArray, floatValue3, floatValue4 + floatValue2, floatValue8 - floatValue);
            break;
         case 3:
            m_0(floatArray, floatValue3 + floatValue, floatValue4 + floatValue2, floatValue5);
            break;
         case 4:
            m_0(floatArray, floatValue6, floatValue4 + floatValue2, floatValue5 + floatValue);
            break;
         default:
            m_0(floatArray, floatValue6 - floatValue, floatValue4 + floatValue2, floatValue8);
      }
   }

   private static void m_0(float[] floatArray, float... floatArray2) {
      System.arraycopy(floatArray2, 0, floatArray, 0, floatArray2.length);
   }

   private static int m_4(
      CrownModel.DataRecord c7, int[] intArray, int value, float floatValue, float floatValue2, float floatValue3, float floatValue4, int value2, int value3, int value4, int value5, int value6, int value7
   ) {
      CrownModel.m_2_String();
      float floatValue5 = 1.0F;
      int value8;

      switch (c7.m_7c()) {
         case GORE: {
            int value9 = m_2_int(floatValue, floatValue2);
            value8 = intArray[0 + value9];
            float floatValue6 = 0.25F;
            if (m_2_int(floatValue + floatValue6, floatValue2) != value9
               || m_2_int(floatValue - floatValue6, floatValue2) != value9
               || m_2_int(floatValue, floatValue2 + floatValue6) != value9
               || m_2_int(floatValue, floatValue2 - floatValue6) != value9) {
               floatValue5 = 0.74F;
            }

            if (floatValue4 > 2.0F && floatValue3 > floatValue4 - 0.5F) {
               floatValue5 *= 0.8F;
            } else if (floatValue4 > 2.0F && floatValue3 > floatValue4 - 1.0F) {
               floatValue5 *= 1.08F;
            }
            break;
         }
         case BRIM:
            value8 = intArray[4];
            if (value != 0 || value2 != 0 && value2 != value4 - 1 && value3 != 0 && value3 != value5 - 1) {
               if (value == 0 && value3 == 2 && (value2 & 1) == 0) {
                  floatValue5 = 1.18F;
               }
            } else {
               floatValue5 = 0.85F;
            }
            break;
         case STEM:
            value8 = intArray[5];
            floatValue5 = floatValue4 > 0.0F ? 0.88F + 0.2F * (1.0F - floatValue3 / floatValue4) : 1.1F;
            break;
         case HUB:
            value8 = intArray[6];
            floatValue5 = value == 0 ? 1.12F : (floatValue3 < 0.5F ? 1.05F : 0.9F);
            break;
         case HUB_CAP:
            value8 = intArray[6];
            floatValue5 = 1.22F;
            break;
         default: {
            value8 = intArray[c7.m_7c() == CrownModel.ModeKind.BLADE_A ? 7 : 8];
            float floatValue7 = floatValue2 - c7.m_8_y_float();
            if (floatValue7 < 0.5F) {
               floatValue5 = 1.16F;
            } else if (floatValue7 > c7.m_8_l() - 0.5F) {
               floatValue5 = 0.82F;
            }
         }
      }

      floatValue5 *= m_2_float(value6, value7);
      return 0xFF000000 | scaleChannel(value8 >> 16, floatValue5) << 16 | scaleChannel(value8 >> 8, floatValue5) << 8 | scaleChannel(value8, floatValue5);
   }

   private static int m_2_int(float floatValue, float floatValue2) {
      double doubleValue = Math.atan2(floatValue, -floatValue2) + (Math.PI / 4);
      return Math.floorMod((int)Math.floor(doubleValue / (Math.PI / 2)), 4);
   }

   private static float m_2_float(int value, int value2) {
      int value3 = value * 73856093 ^ value2 * 19349663;
      value3 ^= value3 >>> 13;
      value3 *= 1540483477;
      value3 ^= value3 >>> 15;
      return 0.94F + (value3 & 0xFF) / 255.0F * 0.12F;
   }

   private static int scaleChannel(int value, float floatValue) {
      return Math.min(255, Math.max(0, (int)((value & 0xFF) * floatValue)));
   }
}
