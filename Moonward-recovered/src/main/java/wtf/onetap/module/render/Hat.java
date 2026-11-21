package wtf.onetap.module.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Texture3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.RotationManager;

@ModuleInformation(name = "Hat", description = "Рисует шляпу или нимб над головой", category = Category.RENDER)
public class Hat extends Module {
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "images/glow.png");
   private static final float[] FLOAT_VALUE_ARRAY = new float[65];
   private static final float[] FLOAT_VALUE_ARRAY2 = new float[65];
   private float floatValue;
   private long longValue = System.currentTimeMillis();
   private final ModeSetting mode = new ModeSetting("Режим", "Шляпа", "Шляпа", "Нимб", "Призраки", "Кепка").build();
   private final ModeSetting color = new ModeSetting("Цвет", "Тема", "Тема", "Свой", "Радуга").build();
   private final ColorSetting customColor = new ColorSetting("Свой цвет", ColorUtil.rgba(140, 180, 255, 255)).visibleWhen(() -> ((String)this.color.getValue()).equals("Свой")).build();
   private final BooleanSetting workOnFriends = new BooleanSetting("Работать на друзей", false).build();
   private final FloatSetting ghostSize = new FloatSetting("Размер призраков", 1.0F, 0.3F, 2.0F, 0.05F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Призраки")).build();
   private final FloatSetting tailLength = new FloatSetting("Длина хвоста", 80.0F, 10.0F, 130.0F, 5.0F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Призраки")).build();
   private final FloatSetting ghostSpeed = new FloatSetting("Скорость призраков", 75.0F, 5.0F, 360.0F, 5.0F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Призраки")).build();
   private final BooleanSetting classicColors = new BooleanSetting("Классические цвета", true).visibleWhen(() -> ((String)this.mode.getValue()).equals("Кепка")).build();
   private final FloatSetting windSensitivity = new FloatSetting("Чувствительность к ветру", 1.0F, 0.2F, 3.0F, 0.05F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Кепка")).build();
   private static final int[] VALUE_ARRAY;
   private final int[] valueArray = new int[9];
   private final List<Player> playerList = new ArrayList<>();
   private final Matrix4f matrix4f = new Matrix4f();
   private final Vector3f vector3f = new Vector3f();

   @Override
   public void onDisable() {
      RotationManager.ROTATION_MANAGER.m_9_void();
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null && this.m_1_boolean()) {
         RotationManager rotationManager = RotationManager.ROTATION_MANAGER;
         rotationManager.tick();

         for (AbstractClientPlayer abstractClientPlayer : MinecraftAccess.MINECRAFT.level.players()) {
            if (abstractClientPlayer.isAlive() && !abstractClientPlayer.isSpectator() && this.m_9_boolean(abstractClientPlayer.getId())) {
               rotationManager.m_4(abstractClientPlayer, (Float)this.windSensitivity.getValue());
            }
         }

         rotationManager.purge();
      } else {
         RotationManager.ROTATION_MANAGER.m_9_void();
      }
   }

   public boolean m_1_boolean() {
      return ((String)this.mode.getValue()).equals("Кепка");
   }

   public boolean m_9_boolean(int value) {
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.level == null) {
         return false;
      } else if (value == MinecraftAccess.MINECRAFT.player.getId()) {
         return true;
      } else {
         return !this.workOnFriends.getValue() ? false : MinecraftAccess.MINECRAFT.level.getEntity(value) instanceof Player player && Onetap.FRIEND_MANAGER.isFriend(player.getName().getString());
      }
   }

   public int[] m_3_IArr() {
      if (!(Boolean)this.classicColors.getValue() && MinecraftAccess.MINECRAFT.level != null) {
         long longValue2 = MinecraftAccess.MINECRAFT.level.getGameTime() % 4096L;

         for (int i = 0; i < 4; i++) {
            int value = this.getHatColor((float) (Math.PI * 2) * i / 4.0F, (float)longValue2);
            this.valueArray[0 + i] = i % 2 == 0 ? value : ColorUtil.lerpColor(value, -16777216, 0.2F);
         }

         this.valueArray[4] = ColorUtil.lerpColor(this.getHatColor(0.0F, (float)longValue2), -16777216, 0.3F);
         this.valueArray[5] = VALUE_ARRAY[5];
         this.valueArray[6] = VALUE_ARRAY[6];
         this.valueArray[7] = this.getHatColor(0.0F, (float)longValue2);
         this.valueArray[8] = this.getHatColor((float) Math.PI, (float)longValue2);
         return this.valueArray;
      } else {
         System.arraycopy(VALUE_ARRAY, 0, this.valueArray, 0, 9);
         return this.valueArray;
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if (!this.m_1_boolean()) {
            this.m_2_void();
            if (!this.playerList.isEmpty()) {
               Vec3 vec3 = event.getVec3();
               float floatValue2 = event.getFloatValue();
               float floatValue3 = (float)(MinecraftAccess.MINECRAFT.level.getGameTime() % 4096L) + floatValue2;
               boolean flag = ((String)this.mode.getValue()).equals("Нимб");
               boolean flag2 = ((String)this.mode.getValue()).equals("Призраки");
               float[][] arrayArray = new float[this.playerList.size()][3];
               byte byteValue = 0;

               for (int i = 0; i < this.playerList.size(); i++) {
                  Player player = this.playerList.get(i);
                  float[] floatArray = arrayArray[i];
                  floatArray[0] = (float)(player.xo + (player.getX() - player.xo) * floatValue2);
                  floatArray[1] = (float)(player.yo + (player.getY() - player.yo) * floatValue2) + player.getBbHeight() + 0.03F;
                  floatArray[2] = (float)(player.zo + (player.getZ() - player.zo) * floatValue2);
                  if (!flag2) {
                     byteValue += flag ? 64 : this.m_8(floatArray, vec3);
                  }
               }

               if (flag2) {
                  this.m_1(event, arrayArray, floatValue3);
               } else if (byteValue != 0) {
                  if (flag) {
                     this.m_0(event, arrayArray, floatValue3);
                  } else {
                     float floatValue4 = (float)vec3.x;
                     float floatValue5 = (float)vec3.y;
                     float floatValue6 = (float)vec3.z;
                     this.matrix4f.set(event.getMatrix4f()).mul(event.getMatrix4f2());
                     Box3DRenderer.m_8(this.matrix4f, byteValue * 4, bufferBuilder -> {
                        Notifications.m_5_C3_sArr();

                        for (float[] floatArray2 : arrayArray) {
                           float floatValue7 = floatArray2[0] - floatValue4;
                           float floatValue8 = floatArray2[1] - floatValue5;
                           float floatValue9 = floatArray2[2] - floatValue6;

                           for (int i2 = 0; i2 < 64; i2++) {
                              if (m_1(floatArray2, vec3, i2)) {
                                 float floatValue10 = (float) (Math.PI * 2) * i2 / 64.0F;
                                 float floatValue11 = floatValue10 + 0.09817477F;
                                 int value = this.getHatColor(floatValue10 + 0.049087387F, floatValue3);
                                 addVertex(bufferBuilder, floatValue7 + FLOAT_VALUE_ARRAY[i2] * 0.55F, floatValue8, floatValue9 + FLOAT_VALUE_ARRAY2[i2] * 0.55F, this.getHatColor(floatValue10, floatValue3));
                                 addVertex(bufferBuilder, floatValue7 + FLOAT_VALUE_ARRAY[i2 + 1] * 0.55F, floatValue8, floatValue9 + FLOAT_VALUE_ARRAY2[i2 + 1] * 0.55F, this.getHatColor(floatValue11, floatValue3));
                                 addVertex(bufferBuilder, floatValue7, floatValue8 + 0.3F, floatValue9, value);
                                 addVertex(bufferBuilder, floatValue7, floatValue8 + 0.3F, floatValue9, value);
                              }
                           }

                           if (vec3.y < floatArray2[1]) {
                              for (int i3 = 0; i3 < 64; i3++) {
                                 float floatValue12 = (float) (Math.PI * 2) * i3 / 64.0F;
                                 int value2 = this.getHatColor(floatValue12 + 0.049087387F, floatValue3);
                                 addVertex(bufferBuilder, floatValue7, floatValue8, floatValue9, value2);
                                 addVertex(bufferBuilder, floatValue7 + FLOAT_VALUE_ARRAY[i3] * 0.55F, floatValue8, floatValue9 + FLOAT_VALUE_ARRAY2[i3] * 0.55F, this.getHatColor(floatValue12, floatValue3));
                                 addVertex(bufferBuilder, floatValue7 + FLOAT_VALUE_ARRAY[i3 + 1] * 0.55F, floatValue8, floatValue9 + FLOAT_VALUE_ARRAY2[i3 + 1] * 0.55F, this.getHatColor(floatValue12 + 0.09817477F, floatValue3));
                                 addVertex(bufferBuilder, floatValue7, floatValue8, floatValue9, value2);
                              }
                           }
                        }
                     });
                  }
               }
            }
         }
      }
   }

   private void m_0(Render3DEvent event, float[][] arrayArray, float floatValue2) {
      GpuTextureView gpuTextureView = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(IDENTIFIER).getTextureView();
      double doubleValue = event.getVec3().x;
      double doubleValue2 = event.getVec3().y;
      double doubleValue3 = event.getVec3().z;
      float floatValue3 = 0.14F;
      Texture3DRenderer.m_8(event.getMatrix4f(), gpuTextureView, arrayArray.length * 64 * 2, bufferBuilder -> {
         for (float[] floatArray : arrayArray) {
            for (int i = 0; i < 64; i++) {
               float floatValue4 = (float) (Math.PI * 2) * i / 64.0F;
               int value = this.getHatColor(floatValue4, floatValue2);
               int value2 = value >>> 24 & 0xFF;
               double doubleValue4 = floatArray[0] + FLOAT_VALUE_ARRAY[i] * 0.45F - doubleValue;
               double doubleValue5 = floatArray[1] + 0.15 - doubleValue2;
               double doubleValue6 = floatArray[2] + FLOAT_VALUE_ARRAY2[i] * 0.45F - doubleValue3;
               this.m_5(event, doubleValue4, doubleValue5, doubleValue6, floatValue3 * 2.2F);
               this.m_3(bufferBuilder, ColorUtil.withAlpha(value, value2 / 5));
               this.m_5(event, doubleValue4, doubleValue5, doubleValue6, floatValue3);
               this.m_3(bufferBuilder, ColorUtil.withAlpha(value, (int)(value2 * 0.9F)));
            }
         }
      });
   }

   private void m_1(Render3DEvent event, float[][] arrayArray, float floatValue2) {
      long longValue2 = System.currentTimeMillis();
      float floatValue3 = (float)(longValue2 - this.longValue) / 1000.0F;
      this.longValue = longValue2;
      this.floatValue = this.floatValue + floatValue3 * (Float)this.ghostSpeed.getValue();
      if (this.floatValue >= 360.0F) {
         this.floatValue -= 360.0F;
      }

      float floatValue4 = 0.5F;
      byte byteValue = 3;
      float floatValue5 = (Float)this.tailLength.getValue();
      byte byteValue2 = 30;
      GpuTextureView gpuTextureView = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(IDENTIFIER).getTextureView();
      double doubleValue = event.getVec3().x;
      double doubleValue2 = event.getVec3().y;
      double doubleValue3 = event.getVec3().z;
      Texture3DRenderer.m_8(event.getMatrix4f(), gpuTextureView, arrayArray.length * byteValue * (byteValue2 + 1), bufferBuilder -> {
         for (float[] floatArray : arrayArray) {
            for (int i = 0; i < byteValue; i++) {
               float floatValue6 = i * (360.0F / byteValue) + this.floatValue;

               for (int i2 = 0; i2 <= byteValue2; i2++) {
                  float floatValue7 = (float)i2 / byteValue2;
                  float floatValue8 = floatValue6 + floatValue7 * floatValue5;
                  float floatValue9 = (float)Math.toRadians(floatValue8);
                  float floatValue10 = (float)Math.sin(floatValue9) * floatValue4;
                  float floatValue11 = -((float)Math.cos(floatValue9)) * floatValue4;
                  float floatValue12 = 1.5F + floatValue7 * floatValue7 * 11.0F;
                  int value = (int)(10.0F + floatValue7 * 245.0F);
                  float floatValue13 = (float)Math.toRadians(i * 120.0F + i2);
                  int value2 = ColorUtil.withAlpha(this.getHatColor(floatValue13, floatValue2), value);
                  float floatValue14 = floatValue12 * 0.02F * (Float)this.ghostSize.getValue();
                  this.m_5(event, floatArray[0] + floatValue10 - doubleValue, floatArray[1] + 0.22F - doubleValue2, floatArray[2] + floatValue11 - doubleValue3, floatValue14);
                  this.m_3(bufferBuilder, value2);
               }
            }
         }
      });
   }

   private void m_5(Render3DEvent event, double doubleValue, double doubleValue2, double doubleValue3, float floatValue2) {
      this.matrix4f.set(event.getMatrix4f2());
      this.matrix4f.translate((float)doubleValue, (float)doubleValue2, (float)doubleValue3);
      this.matrix4f.rotate(event.getQuaternionf());
      this.matrix4f.scale(floatValue2);
   }

   private void m_3(BufferBuilder bufferBuilder, int value) {
      int value2 = value >>> 24 & 0xFF;
      int value3 = value >> 16 & 0xFF;
      int value4 = value >> 8 & 0xFF;
      int value5 = value & 0xFF;
      this.m_0(bufferBuilder, -0.5F, 0.5F, 0.0F, 1.0F, value3, value4, value5, value2);
      this.m_0(bufferBuilder, 0.5F, 0.5F, 1.0F, 1.0F, value3, value4, value5, value2);
      this.m_0(bufferBuilder, 0.5F, -0.5F, 1.0F, 0.0F, value3, value4, value5, value2);
      this.m_0(bufferBuilder, -0.5F, -0.5F, 0.0F, 0.0F, value3, value4, value5, value2);
   }

   private void m_0(BufferBuilder bufferBuilder, float floatValue2, float floatValue3, float floatValue4, float floatValue5, int value, int value2, int value3, int value4) {
      this.matrix4f.transformPosition(floatValue2, floatValue3, 0.0F, this.vector3f);
      bufferBuilder.addVertex(this.vector3f.x, this.vector3f.y, this.vector3f.z).setUv(floatValue4, floatValue5).setColor(value, value2, value3, value4);
   }

   private int m_8(float[] floatArray, Vec3 vec3) {
      int value = 0;

      for (int i = 0; i < 64; i++) {
         if (m_1(floatArray, vec3, i)) {
            value++;
         }
      }

      if (vec3.y < floatArray[1]) {
         value += 64;
      }

      return value;
   }

   private static boolean m_1(float[] floatArray, Vec3 vec3, int value) {
      float floatValue2 = 0.3F * (FLOAT_VALUE_ARRAY[value] + FLOAT_VALUE_ARRAY[value + 1]);
      float floatValue3 = 0.3F * (FLOAT_VALUE_ARRAY2[value] + FLOAT_VALUE_ARRAY2[value + 1]);
      return floatValue2 * (vec3.x - floatArray[0]) + 0.55F * (vec3.y - floatArray[1] - 0.09900001F) + floatValue3 * (vec3.z - floatArray[2]) > 0.0;
   }

   private int getHatColor(float floatValue2, float floatValue3) {
      return switch ((String)this.color.getValue()) {
         case "Свой" -> this.customColor.getValue();
         case "Радуга" -> ColorUtil.m_3_int(floatValue3 * 0.02F + floatValue2 / (float) (Math.PI * 2));
         default -> ColorUtil.lerpColor(ColorUtil.primaryColor(), ColorUtil.secondaryColor(), 0.5F + 0.5F * (float)Math.cos(floatValue2 - floatValue3 * 0.08F));
      };
   }

   private void m_2_void() {
      this.playerList.clear();
      if (!MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson() && MinecraftAccess.MINECRAFT.player.isAlive() && !MinecraftAccess.MINECRAFT.player.isSpectator()) {
         this.playerList.add(MinecraftAccess.MINECRAFT.player);
      }

      if ((Boolean)this.workOnFriends.getValue()) {
         for (AbstractClientPlayer abstractClientPlayer : MinecraftAccess.MINECRAFT.level.players()) {
            if (abstractClientPlayer != MinecraftAccess.MINECRAFT.player && abstractClientPlayer.isAlive() && !abstractClientPlayer.isSpectator() && Onetap.FRIEND_MANAGER.isFriend(abstractClientPlayer.getName().getString())) {
               this.playerList.add(abstractClientPlayer);
            }
         }
      }
   }

   private static void addVertex(BufferBuilder bufferBuilder, float floatValue2, float floatValue3, float floatValue4, int value) {
      bufferBuilder.addVertex(floatValue2, floatValue3, floatValue4).setColor(value >> 16 & 0xFF, value >> 8 & 0xFF, value & 0xFF, value >>> 24 & 0xFF);
   }

   static {
      for (int i = 0; i <= 64; i++) {
         FLOAT_VALUE_ARRAY[i] = (float)Math.cos(Float.intBitsToFloat(1086918619) * i / Float.intBitsToFloat(1115684864));
         FLOAT_VALUE_ARRAY2[i] = (float)Math.sin(Float.intBitsToFloat(1086918619) * i / Float.intBitsToFloat(1115684864));
      }

      VALUE_ARRAY = new int[]{
         ColorUtil.rgb(214, 44, 44),
         ColorUtil.rgb(244, 196, 30),
         ColorUtil.rgb(46, 92, 214),
         ColorUtil.rgb(58, 168, 62),
         ColorUtil.rgb(36, 72, 178),
         ColorUtil.rgb(150, 150, 158),
         ColorUtil.rgb(250, 206, 52),
         ColorUtil.rgb(222, 46, 46),
         ColorUtil.rgb(48, 98, 222)
      };
   }
}
