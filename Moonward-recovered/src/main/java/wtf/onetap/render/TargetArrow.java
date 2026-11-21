package wtf.onetap.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.util.AngleUtil;
import wtf.onetap.util.MinecraftAccess;

public class TargetArrow implements MinecraftAccess {
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "images/arrow.png");
   private static final TargetArrow.StateHolder C6 = new TargetArrow.StateHolder();
   private static final TargetArrow.StateHolder C62 = new TargetArrow.StateHolder();
   private long longValue;
   private static int[] valueArray;

   public TargetArrow() {
      this.longValue = System.currentTimeMillis();
   }

   public static void m_5_void(double doubleValue, double doubleValue2) {
      C6.doubleValue = doubleValue;
      C6.doubleValue2 = doubleValue2;
      C6.flag = true;
      C6.flag2 = false;
   }

   public static void m_6(double doubleValue, double doubleValue2, String text, boolean flag) {
      C62.doubleValue = doubleValue;
      C62.doubleValue2 = doubleValue2;
      C62.flag = true;
      C62.flag2 = flag;
      C62.text = text;
   }

   public static void m_4_void(String text) {
      C62.text = text;
   }

   public static void m_9_void() {
      C6.flag = false;
      C62.flag = false;
   }

   public static void m_7_void() {
      C62.flag = false;
   }

   public static boolean m_8_boolean() {
      return C6.flag || C62.flag;
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         long longValue2 = System.currentTimeMillis();
         float floatValue = Math.max(0.001F, (float)(longValue2 - this.longValue) / 1000.0F);
         this.longValue = longValue2;
         m_9(C6, floatValue);
         m_9(C62, floatValue);
         if (!(C6.floatValue2 <= 0.002F) || !(C62.floatValue2 <= 0.002F)) {
            GpuTextureView gpuTextureView = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(IDENTIFIER).getTextureView();
            if (gpuTextureView != null) {
               int value = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth();
               float floatValue2 = 46.0F;
               boolean flag = C6.floatValue2 > 0.002F;
               boolean flag2 = C62.floatValue2 > 0.002F;
               int value2 = (flag ? 1 : 0) + (flag2 ? 1 : 0);
               float floatValue3 = value / 2.0F - (value2 - 1) * 56.0F / 2.0F;
               float floatValue4 = floatValue3;
               float floatValue5 = flag ? floatValue3 + 56.0F : floatValue3;
               float floatValue6 = 22.5F;
               float floatValue7 = (flag ? floatValue4 : floatValue5) - floatValue6;
               float floatValue8 = (flag2 ? floatValue5 : floatValue4) + floatValue6;
               Render2D.m_0(gpuTextureView, floatValue7, floatValue2 - floatValue6, floatValue8 - floatValue7, floatValue6 * 2.0F, vertexConsumer -> {
                  if (flag) {
                     m_5(vertexConsumer, C6, floatValue4, floatValue2);
                  }

                  if (flag2) {
                     m_5(vertexConsumer, C62, floatValue5, floatValue2);
                  }
               });
               if (flag) {
                  m_8(C6, floatValue4, floatValue2);
               }

               if (flag2) {
                  m_8(C62, floatValue5, floatValue2);
               }
            }
         }
      } else {
         C6.flag3 = false;
         C62.flag3 = false;
      }
   }

   private static void m_9(TargetArrow.StateHolder c6, float floatValue) {
      if (c6.flag2 && c6.flag) {
         double doubleValue = c6.doubleValue - MinecraftAccess.MINECRAFT.player.getX();
         double doubleValue2 = c6.doubleValue2 - MinecraftAccess.MINECRAFT.player.getZ();
         if (doubleValue * doubleValue + doubleValue2 * doubleValue2 < 9.0) {
            c6.flag = false;
         }
      }

      c6.floatValue2 = AngleUtil.approach(c6.floatValue2, c6.flag ? 1.0F : 0.0F, 4.5F * floatValue);
      if (c6.floatValue2 <= 0.002F) {
         c6.flag3 = false;
         c6.floatValue3 = 0.0F;
      } else {
         double doubleValue3 = c6.doubleValue - MinecraftAccess.MINECRAFT.player.getX();
         double doubleValue4 = c6.doubleValue2 - MinecraftAccess.MINECRAFT.player.getZ();
         c6.doubleValue3 = Math.sqrt(doubleValue3 * doubleValue3 + doubleValue4 * doubleValue4);
         float floatValue2 = (float)(Math.toDegrees(Math.atan2(doubleValue4, doubleValue3)) - 90.0);
         float floatValue3 = AngleUtil.wrappedDelta(MinecraftAccess.MINECRAFT.player.getYRot(), floatValue2);
         if (!c6.flag3) {
            c6.floatValue = floatValue3;
            c6.flag3 = true;
         } else {
            c6.floatValue = AngleUtil.approachDegrees(c6.floatValue, floatValue3, 900.0F * floatValue);
         }

         Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getDeltaMovement();
         double doubleValue5 = Math.sqrt(vec3.x * vec3.x + vec3.z * vec3.z) * 20.0;
         boolean flag = false;
         if (doubleValue5 > 15.0 && c6.doubleValue3 > 1.0) {
            float floatValue4 = (float)Math.toDegrees(Math.atan2(vec3.z, vec3.x));
            float floatValue5 = (float)Math.toDegrees(Math.atan2(doubleValue4, doubleValue3));
            if (Math.abs(AngleUtil.wrappedDelta(floatValue4, floatValue5)) < 45.0F) {
               flag = true;
               c6.doubleValue4 = c6.doubleValue3 / doubleValue5;
            }
         }

         c6.floatValue3 = AngleUtil.approach(c6.floatValue3, flag ? 1.0F : 0.0F, 5.0F * floatValue);
      }
   }

   private static void m_5(VertexConsumer vertexConsumer, TargetArrow.StateHolder c6, float floatValue, float floatValue2) {
      float floatValue3 = (float)Math.toRadians(c6.floatValue);
      int value = c6 == C62 ? ColorUtil.rgb(255, 55, 55) : ColorUtil.primaryColor();
      int value2 = ColorUtil.withAlpha(value, (int)(255.0F * c6.floatValue2));
      m_9(vertexConsumer, floatValue, floatValue2, 15.0F, Mth.cos(floatValue3), Mth.sin(floatValue3), value2);
   }

   private static void m_8(TargetArrow.StateHolder c6, float floatValue, float floatValue2) {
      float floatValue3 = 11.0F;
      float floatValue4 = floatValue2 + 15.0F - 6.0F;
      int value = (int)(255.0F * c6.floatValue2);
      m_3(Math.round(c6.doubleValue3) + " м", floatValue3, floatValue, floatValue4, ColorUtil.rgb(255, 255, 255), value);
      float floatValue5 = floatValue4 + floatValue3 + 2.0F;
      if (c6.text != null && !c6.text.isEmpty()) {
         float floatValue6 = 9.0F;

         for (String text : c6.text.split("\n")) {
            m_3(text, floatValue6, floatValue, floatValue5, ColorUtil.rgb(255, 255, 255), value);
            floatValue5 += floatValue6 + 3.0F;
         }
      }

      float floatValue7 = c6.floatValue2 * c6.floatValue3;
      if (floatValue7 > 0.002F) {
         m_3(m_6_String(c6.doubleValue4), 9.0F, floatValue, floatValue5, ColorUtil.rgb(200, 200, 200), (int)(255.0F * floatValue7));
      }
   }

   private static void m_3(String text, float floatValue, float floatValue2, float floatValue3, int value, int value2) {
      float floatValue4 = Draw2D.getTextWidth(text, Fonts.SUPPLIER15, floatValue);
      Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER15, floatValue2 - floatValue4 / 2.0F, floatValue3, ColorUtil.withAlpha(value, value2), floatValue);
   }

   private static String m_6_String(double doubleValue) {
      long longValue2 = Math.round(doubleValue);
      if (longValue2 >= 60L) {
         long longValue3 = longValue2 / 60L;
         long longValue4 = longValue2 % 60L;
         long longValue5 = longValue4;
         long longValue6 = longValue3;
         return "~" + longValue6 + " мин " + longValue5 + " с";
      } else {
         return "~" + longValue2 + " с";
      }
   }

   private static void m_9(VertexConsumer vertexConsumer, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, int value) {
      int value2 = value >> 24 & 0xFF;
      int value3 = value >> 16 & 0xFF;
      int value4 = value >> 8 & 0xFF;
      int value5 = value & 0xFF;
      m_6(vertexConsumer, floatValue, floatValue2, -floatValue3, floatValue3, floatValue4, floatValue5, 0.0F, 1.0F, value3, value4, value5, value2);
      m_6(vertexConsumer, floatValue, floatValue2, floatValue3, floatValue3, floatValue4, floatValue5, 1.0F, 1.0F, value3, value4, value5, value2);
      m_6(vertexConsumer, floatValue, floatValue2, floatValue3, -floatValue3, floatValue4, floatValue5, 1.0F, 0.0F, value3, value4, value5, value2);
      m_6(vertexConsumer, floatValue, floatValue2, -floatValue3, -floatValue3, floatValue4, floatValue5, 0.0F, 0.0F, value3, value4, value5, value2);
   }

   private static void m_6(
      VertexConsumer vertexConsumer,
      float floatValue,
      float floatValue2,
      float floatValue3,
      float floatValue4,
      float floatValue5,
      float floatValue6,
      float floatValue7,
      float floatValue8,
      int value,
      int value2,
      int value3,
      int value4
   ) {
      vertexConsumer.addVertex(floatValue + floatValue3 * floatValue5 - floatValue4 * floatValue6, floatValue2 + floatValue3 * floatValue6 + floatValue4 * floatValue5, 0.0F).setUv(floatValue7, floatValue8).setColor(value, value2, value3, value4);
   }

   static {
      m_6(new int[2]);
   }

   public static void m_6(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_2_IArr() {
      return valueArray;
   }

   private static final class StateHolder {
      public boolean flag;
      public boolean flag2;
      public String text;
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public float floatValue;
      public boolean flag3;
      public float floatValue2;
      public float floatValue3;
      public double doubleValue4;
   }
}
