package wtf.onetap.module.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Texture3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Arrows", description = "Стрелки к игрокам от центра экрана", category = Category.RENDER)
public class Arrows extends Module {
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "images/triangle.png");
   private final FloatSetting radius = new FloatSetting("Радиус", 50.0F, 10.0F, 300.0F, 1.0F).build();
   private final FloatSetting size = new FloatSetting("Размер", 18.0F, 5.0F, 40.0F, 1.0F).build();
   private final FloatSetting inventoryExpansion = new FloatSetting("Расширение инвентаря", 100.0F, 0.0F, 200.0F, 1.0F).build();
   private final BooleanSetting sprintAnimation = new BooleanSetting("Анимация спринта", true).build();
   private final BooleanSetting appearAnimation = new BooleanSetting("Анимация появления", true).build();
   private final Animation animation = new Animation(300L, 0.0F, Easing.EASING8);
   private final Animation animation2 = new Animation(250L, 0.0F, Easing.EASING8);
   private final Map<UUID, Animation> uUIDMap = new HashMap<>();
   private final Map<UUID, Arrows.StateHolder> uUIDMap2 = new HashMap<>();
   private final List<Arrows.DataRecord> c5List = new ArrayList<>();
   private final Matrix4f matrix4f = new Matrix4f();

   @Override
   public void onDisable() {
      this.uUIDMap.clear();
      this.uUIDMap2.clear();
      this.c5List.clear();
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      Notifications.m_5_C3_sArr();
      this.c5List.clear();
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         CameraRenderState cameraRenderState = MinecraftAccess.MINECRAFT.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
         if (cameraRenderState.initialized) {
            float floatValue = event.getFloatValue();
            float floatValue2 = this.size.getFloat();
            float floatValue3 = this.radius.getFloat();
            if ((Boolean)this.sprintAnimation.getValue()) {
               this.animation.update(MinecraftAccess.MINECRAFT.player.isSprinting() ? 1.0F : 0.0F);
               floatValue3 += this.animation.getValue() * 10.0F;
            }

            this.animation2.update(MinecraftAccess.MINECRAFT.gui.screen() instanceof InventoryScreen ? 1.0F : 0.0F);
            floatValue3 += this.animation2.getValue() * this.inventoryExpansion.getFloat();
            float floatValue4 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth() * 0.5F;
            float floatValue5 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() * 0.5F;
            float floatValue6 = cameraRenderState.yRot;
            Vec3 vec3 = cameraRenderState.pos;
            int value = ColorUtil.primaryColor();
            HashSet<UUID> uUIDSet = new HashSet<>();

            for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
               if (player != MinecraftAccess.MINECRAFT.player
                  && player.isAlive()
                  && MinecraftAccess.MINECRAFT.getConnection() != null
                  && MinecraftAccess.MINECRAFT.getConnection().getPlayerInfo(player.getUUID()) != null) {
                  UUID uUID = player.getUUID();
                  uUIDSet.add(uUID);
                  double doubleValue = player.xo + (player.getX() - player.xo) * floatValue;
                  double doubleValue2 = player.zo + (player.getZ() - player.zo) * floatValue;
                  double doubleValue3 = doubleValue - vec3.x;
                  double doubleValue4 = doubleValue2 - vec3.z;
                  double doubleValue5 = Mth.cos(floatValue6 * (float) (Math.PI / 180.0));
                  double doubleValue6 = Mth.sin(floatValue6 * (float) (Math.PI / 180.0));
                  double doubleValue7 = -(doubleValue4 * doubleValue5 - doubleValue3 * doubleValue6);
                  double doubleValue8 = -(doubleValue3 * doubleValue5 + doubleValue4 * doubleValue6);
                  float floatValue7 = (float)(Math.atan2(doubleValue7, doubleValue8) * 180.0 / Math.PI);
                  Arrows.StateHolder c4 = this.uUIDMap2.computeIfAbsent(uUID, uUID2 -> new Arrows.StateHolder());
                  c4.floatValue = floatValue3 * Mth.cos((float)Math.toRadians(floatValue7)) + floatValue4;
                  c4.floatValue2 = floatValue3 * Mth.sin((float)Math.toRadians(floatValue7)) + floatValue5;
                  c4.floatValue3 = floatValue7;
                  c4.flag = Onetap.FRIEND_MANAGER.isFriend(player.getName().getString());
               }
            }

            for (UUID uUID3 : uUIDSet) {
               this.uUIDMap.computeIfAbsent(uUID3, uUID4 -> new Animation(400L, 0.0F, Easing.EASING20)).update(1.0F);
            }

            this.uUIDMap.forEach((uUID5, animation3) -> {
               if (!uUIDSet.contains(uUID5)) {
                  animation3.update(0.0F);
               }
            });
            this.uUIDMap.entrySet().removeIf(entry -> entry.getValue().getValue() <= 0.01F && entry.getValue().isFinished());
            this.uUIDMap2.keySet().removeIf(uUID6 -> !this.uUIDMap.containsKey(uUID6));

            for (Entry entry2 : this.uUIDMap.entrySet()) {
               Arrows.StateHolder c42 = this.uUIDMap2.get(entry2.getKey());
               if (c42 != null) {
                  float floatValue8 = this.appearAnimation.getValue() ? ((Animation)entry2.getValue()).getValue() : 1.0F;
                  if (!(floatValue8 <= 0.01F)) {
                     float floatValue9 = Mth.lerp(floatValue8, floatValue4, c42.floatValue);
                     float floatValue10 = Mth.lerp(floatValue8, floatValue5, c42.floatValue2);
                     int value2 = Mth.clamp((int)(200.0F * floatValue8), 0, 255);
                     int value3 = c42.flag ? ColorUtil.rgba(50, 205, 50, 255) : value;
                     int value4 = ColorUtil.withAlpha(value3, value2);
                     this.c5List.add(new Arrows.DataRecord(floatValue9, floatValue10, c42.floatValue3, floatValue2, value4));
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (!this.c5List.isEmpty()) {
         GpuTextureView gpuTextureView = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(IDENTIFIER).getTextureView();
         if (gpuTextureView != null) {
            float floatValue = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth();
            float floatValue2 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight();
            this.matrix4f.identity().setOrtho(0.0F, floatValue, floatValue2, 0.0F, -1000.0F, 1000.0F);
            Texture3DRenderer.m_8(this.matrix4f, gpuTextureView, this.c5List.size(), bufferBuilder -> {
               for (Arrows.DataRecord c5 : this.c5List) {
                  this.m_3(bufferBuilder, c5);
               }
            });
         }
      }
   }

   private void m_3(BufferBuilder bufferBuilder, Arrows.DataRecord c5) {
      float floatValue = c5.f_5s * 0.5F;
      float floatValue2 = (float)Math.toRadians(c5.f_5u + 90.0F);
      float floatValue3 = Mth.cos(floatValue2);
      float floatValue4 = Mth.sin(floatValue2);
      int value = c5.f_6q >> 24 & 0xFF;
      int value2 = c5.f_6q >> 16 & 0xFF;
      int value3 = c5.f_6q >> 8 & 0xFF;
      int value4 = c5.f_6q & 0xFF;
      m_6(bufferBuilder, c5, -floatValue, c5.f_5s, floatValue3, floatValue4, 0.0F, 1.0F, value2, value3, value4, value);
      m_6(bufferBuilder, c5, floatValue, c5.f_5s, floatValue3, floatValue4, 1.0F, 1.0F, value2, value3, value4, value);
      m_6(bufferBuilder, c5, floatValue, 0.0F, floatValue3, floatValue4, 1.0F, 0.0F, value2, value3, value4, value);
      m_6(bufferBuilder, c5, -floatValue, 0.0F, floatValue3, floatValue4, 0.0F, 0.0F, value2, value3, value4, value);
   }

   private static void m_6(
      BufferBuilder bufferBuilder, Arrows.DataRecord c5, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value, int value2, int value3, int value4
   ) {
      bufferBuilder.addVertex(c5.f_5_m + floatValue * floatValue3 - floatValue2 * floatValue4, c5.f_5_j + floatValue * floatValue4 + floatValue2 * floatValue3, 0.0F).setUv(floatValue5, floatValue6).setColor(value, value2, value3, value4);
   }

   private static final class StateHolder {
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public boolean flag;
   }

   private record DataRecord(float f_5_m, float f_5_j, float f_5u, float f_5s, int f_6q) {
      public float m_5_m() {
         return this.f_5_m;
      }

      public float m_5_j() {
         return this.f_5_j;
      }

      public float m_5u() {
         return this.f_5u;
      }

      public float m_5s() {
         return this.f_5s;
      }

      public int m_6q() {
         return this.f_6q;
      }
   }
}
