package wtf.onetap.module.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.C6h;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.render.Render2D;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.WorldToScreen;

@ModuleInformation(name = "ESP", description = "Отображает 2D боксы вокруг игроков, мобов и предметов", category = Category.RENDER)
public class ESP extends Module {
   private final MultiModeSetting display = new MultiModeSetting("Отображать", "Игроки", "Игроки", "Мобы", "Предметы").build();
   private final ModeSetting type = new ModeSetting("Тип", "Обычный", "Обычный", "Углы", "Скелет").build();
   private final BooleanSetting outline = new BooleanSetting("Обводка", true).build();
   private final FloatSetting thickness = new FloatSetting("Толщина", 1.0F, 0.5F, 1.5F, 0.1F).build();
   private final BooleanSetting healthBar = new BooleanSetting("Полоса здоровья", true).build();
   private final BooleanSetting themeColor = new BooleanSetting("Цвет от темы", true).build();
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(255, 255, 255, 255)).visibleWhen(() -> !(Boolean)this.themeColor.getValue()).build();

   @EventTarget(priority = EventPriority.HIGH)
   private void onRender2D(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         CameraRenderState cameraRenderState = MinecraftAccess.MINECRAFT.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
         WorldToScreen.setMatrices(new Matrix4f(cameraRenderState.projectionMatrix), new Matrix4f(cameraRenderState.viewRotationMatrix));
         float floatValue = event.getFloatValue();
         boolean flag = this.display.isSelected("Игроки");
         boolean flag2 = this.display.isSelected("Мобы");
         boolean flag3 = this.display.isSelected("Предметы");
         boolean flag4 = ((String)this.type.getValue()).equals("Скелет");

         for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
            if (entity instanceof Player player && player.isAlive() && flag) {
               if (!flag4 && !this.isFirstPersonSelf(player)) {
                  this.m_2(player, floatValue, true);
               }
            } else if (entity instanceof LivingEntity livingEntity && livingEntity.isAlive() && !(entity instanceof Player) && flag2) {
               this.m_2(livingEntity, floatValue, true);
            } else if (entity instanceof ItemEntity itemEntity && itemEntity.isAlive() && flag3) {
               this.m_2(itemEntity, floatValue, false);
            }
         }
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if (this.display.isSelected("Игроки") && ((String)this.type.getValue()).equals("Скелет")) {
            Vec3 vec3 = event.getVec3();
            ArrayList list = new ArrayList();
            ArrayList list2 = new ArrayList();

            for (AbstractClientPlayer abstractClientPlayer : MinecraftAccess.MINECRAFT.level.players()) {
               if (abstractClientPlayer.isAlive() && !this.isFirstPersonSelf(abstractClientPlayer)) {
                  C6h.DataRecord c6 = C6h.m_2_C6(abstractClientPlayer, event.getFloatValue());
                  if (c6 != null) {
                     EntityRenderState entityRenderState = c6.m_8c();
                     MinecraftAccess.MINECRAFT
                        .getEntityRenderDispatcher()
                        .submit(
                           entityRenderState, event.getCameraRenderState(), entityRenderState.x - vec3.x, entityRenderState.y - vec3.y, entityRenderState.z - vec3.z, new PoseStack(), new ESP.StateHolder(vec3, list)
                        );
                     int value = this.m_1(abstractClientPlayer, 0.0);

                     while (list2.size() < list.size() / 2) {
                        list2.add(value);
                     }
                  }
               }
            }

            if (!list.isEmpty()) {
               Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
               int[] intArray = ((java.util.List<Integer>)list2).stream().mapToInt(Integer::intValue).toArray();
               Line3DRenderer.m_7(matrix4f, list, intArray, vec3, (Float)this.thickness.getValue());
            }
         }
      }
   }

   private boolean isFirstPersonSelf(Player player) {
      return player == MinecraftAccess.MINECRAFT.player && MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson();
   }

   private int m_1(Entity entity, double doubleValue) {
      if (entity instanceof Player player && Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())) {
         return ColorUtil.rgba(50, 205, 50, 255);
      } else {
         if (!(Boolean)this.themeColor.getValue()) {
            return (Integer)this.color.getValue();
         }

         double doubleValue2 = System.currentTimeMillis() / 600.0 + doubleValue;
         return ColorUtil.lerpColor(ColorUtil.primaryColor(), ColorUtil.secondaryColor(), (float)(Math.sin(doubleValue2) * 0.5 + 0.5));
      }
   }

   private void m_2(Entity entity, float floatValue, boolean flag) {
      double doubleValue = entity.xo + (entity.getX() - entity.xo) * floatValue;
      Notifications.m_5_C3_sArr();
      double doubleValue2 = entity.yo + (entity.getY() - entity.yo) * floatValue;
      double doubleValue3 = entity.zo + (entity.getZ() - entity.zo) * floatValue;
      float floatValue2 = entity.getBbWidth() / 2.0F;
      float floatValue3 = entity.getBbHeight();
      Vector3fc vector3fc = MinecraftAccess.MINECRAFT.gameRenderer.mainCamera().leftVector();
      float floatValue4 = vector3fc.x() * floatValue2;
      float floatValue5 = vector3fc.z() * floatValue2;
      float floatValue6 = Float.MAX_VALUE;
      float floatValue7 = -Float.MAX_VALUE;
      float floatValue8 = Float.MAX_VALUE;
      float floatValue9 = -Float.MAX_VALUE;

      for (int i = 0; i < 4; i++) {
         int value = (i & 1) == 0 ? 1 : -1;
         WorldToScreen.DataRecord c9 = WorldToScreen.project(doubleValue + floatValue4 * value, doubleValue2 + ((i & 2) == 0 ? 0.0F : floatValue3), doubleValue3 + floatValue5 * value);
         if (c9 == null) {
            return;
         }

         floatValue6 = Math.min(floatValue6, c9.m_2r_float());
         floatValue7 = Math.max(floatValue7, c9.m_2r_float());
         floatValue8 = Math.min(floatValue8, c9.m_2___float());
         floatValue9 = Math.max(floatValue9, c9.m_2___float());
      }

      float floatValue10 = floatValue7 - floatValue6;
      float floatValue11 = floatValue9 - floatValue8;
      Float floatObj = (Float)this.thickness.getValue();
      int value2 = this.m_1(entity, 0.0);
      int value3 = this.m_1(entity, Math.PI / 2);
      int value4 = this.m_1(entity, Math.PI);
      int value5 = this.m_1(entity, Math.PI * 3.0 / 2.0);
      if (!((String)this.type.getValue()).equals("Углы")) {
         if ((Boolean)this.outline.getValue()) {
            this.m_1_void(floatValue6, floatValue8, floatValue10, floatObj, floatObj);
            this.m_1_void(floatValue6, floatValue9 - floatObj, floatValue10, floatObj, floatObj);
            this.m_1_void(floatValue6, floatValue8, floatObj, floatValue11, floatObj);
            this.m_1_void(floatValue7 - floatObj, floatValue8, floatObj, floatValue11, floatObj);
         }

         Render2D.m_4(floatValue6, floatValue8, floatValue10, floatObj, 0.0F, 0.0F, value2, value5);
         Render2D.m_4(floatValue6, floatValue9 - floatObj, floatValue10, floatObj, 0.0F, 0.0F, value3, value4);
         Render2D.m_4(floatValue6, floatValue8, floatObj, floatValue11, 0.0F, 0.0F, 0.0F, value2, value3, value3, value2);
         Render2D.m_4(floatValue7 - floatObj, floatValue8, floatObj, floatValue11, 0.0F, 0.0F, 0.0F, value5, value4, value4, value5);
      } else {
         float floatValue12 = Math.max(2.0F, floatValue10 / 4.0F);
         if ((Boolean)this.outline.getValue()) {
            this.m_6(floatValue6, floatValue8, floatValue12, true, true, floatObj);
            this.m_6(floatValue7, floatValue8, floatValue12, false, true, floatObj);
            this.m_6(floatValue6, floatValue9, floatValue12, true, false, floatObj);
            this.m_6(floatValue7, floatValue9, floatValue12, false, false, floatObj);
         }

         this.m_7(floatValue6, floatValue8, floatValue12, true, true, value2, floatObj);
         this.m_7(floatValue7, floatValue8, floatValue12, false, true, value5, floatObj);
         this.m_7(floatValue6, floatValue9, floatValue12, true, false, value3, floatObj);
         this.m_7(floatValue7, floatValue9, floatValue12, false, false, value4, floatObj);
      }

      if (flag && (Boolean)this.healthBar.getValue() && entity instanceof LivingEntity livingEntity) {
         float floatValue13 = Math.clamp(livingEntity.getHealth() / livingEntity.getMaxHealth(), 0.0F, 1.0F);
         float floatValue14 = floatObj + 0.25F;
         float floatValue15 = floatValue6 - floatValue14 - 2.0F;
         if ((Boolean)this.outline.getValue()) {
            Draw2D.rect(floatValue15 - floatObj, floatValue8 - floatObj, floatValue14 + floatObj * 2.0F, floatValue11 + floatObj * 2.0F, ColorUtil.rgba(0, 0, 0, 255));
         }

         float floatValue16 = floatValue11 * floatValue13;
         int value6 = ColorUtil.lerpColor(ColorUtil.rgba(231, 76, 60, 255), ColorUtil.rgba(46, 204, 113, 255), floatValue13);
         Render2D.m_4(floatValue15, floatValue9 - floatValue16, floatValue14, floatValue16, 0.0F, 0.0F, 0.0F, value6, ColorUtil.rgba(231, 76, 60, 255), ColorUtil.rgba(231, 76, 60, 255), value6);
      }
   }

   private void m_1_void(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      Draw2D.rect(floatValue - floatValue5, floatValue2 - floatValue5, floatValue3 + floatValue5 * 2.0F, floatValue4 + floatValue5 * 2.0F, ColorUtil.rgba(0, 0, 0, 255));
   }

   private void m_7(float floatValue, float floatValue2, float floatValue3, boolean flag, boolean flag2, int value, float floatValue4) {
      float floatValue5 = flag ? floatValue : floatValue - floatValue3 + floatValue4;
      float floatValue6 = flag2 ? floatValue2 : floatValue2 - floatValue3 + floatValue4;
      Draw2D.rect(floatValue5, floatValue2, floatValue3, floatValue4, value);
      Draw2D.rect(floatValue, floatValue6, floatValue4, floatValue3, value);
   }

   private void m_6(float floatValue, float floatValue2, float floatValue3, boolean flag, boolean flag2, float floatValue4) {
      float floatValue5 = flag ? floatValue : floatValue - floatValue3 + floatValue4;
      float floatValue6 = flag2 ? floatValue2 : floatValue2 - floatValue3 + floatValue4;
      int value = ColorUtil.rgba(0, 0, 0, 255);
      Draw2D.rect(floatValue5 - floatValue4, floatValue2 - floatValue4, floatValue3 + floatValue4 * 2.0F, floatValue4 * 3.0F, value);
      Draw2D.rect(floatValue - floatValue4, floatValue6 - floatValue4, floatValue4 * 3.0F, floatValue3 + floatValue4 * 2.0F, value);
   }

   private static final class StateHolder extends SubmitNodeStorage {
      private final Vec3 vec3;
      private final List<Vec3> vec3List;
      private boolean flag;

      private StateHolder(Vec3 vec32, List<Vec3> vec3List2) {
         this.vec3 = vec32;
         this.vec3List = vec3List2;
      }

      @Override
      public <S> void submitModel(
         Model<? super S> model,
         S ss,
         PoseStack poseStack,
         RenderType renderType,
         int value,
         int value2,
         int value3,
         TextureAtlasSprite textureAtlasSprite,
         int value4,
         CrumblingOverlay crumblingOverlay
      ) {
         if (!this.flag && model instanceof HumanoidModel humanoidModel) {
            this.flag = true;
            model.setupAnim(ss);
            poseStack.pushPose();
            humanoidModel.root().translateAndRotate(poseStack);
            Vector3f[] vector3fArray = this.m_4(poseStack, humanoidModel.head);
            Vector3f[] vector3fArray2 = this.m_4(poseStack, humanoidModel.body);
            Vector3f[] vector3fArray3 = this.m_4(poseStack, humanoidModel.rightArm);
            Vector3f[] vector3fArray4 = this.m_4(poseStack, humanoidModel.leftArm);
            Vector3f[] vector3fArray5 = this.m_4(poseStack, humanoidModel.rightLeg);
            Vector3f[] vector3fArray6 = this.m_4(poseStack, humanoidModel.leftLeg);
            poseStack.popPose();
            this.m_0(vector3fArray[1], vector3fArray[0]);
            this.m_0(vector3fArray2[0], vector3fArray2[1]);
            this.m_0(vector3fArray2[0], vector3fArray3[0]);
            this.m_0(vector3fArray2[0], vector3fArray4[0]);
            this.m_0(vector3fArray3[0], vector3fArray3[1]);
            this.m_0(vector3fArray4[0], vector3fArray4[1]);
            this.m_0(vector3fArray2[1], vector3fArray5[0]);
            this.m_0(vector3fArray2[1], vector3fArray6[0]);
            this.m_0(vector3fArray5[0], vector3fArray5[1]);
            this.m_0(vector3fArray6[0], vector3fArray6[1]);
         }
      }

      private Vector3f[] m_4(PoseStack poseStack, ModelPart modelPart) {
         Vector3f[] vector3fArray = new Vector3f[2];
         Vector3f vector3f = new Vector3f(Float.MAX_VALUE);
         Vector3f vector3f2 = new Vector3f(-Float.MAX_VALUE);
         modelPart.visit(poseStack, (pose, text, value, cube) -> {
            if (text.isEmpty()) {
               vector3f.set(Math.min(vector3f.x, cube.minX), Math.min(vector3f.y, cube.minY), Math.min(vector3f.z, cube.minZ));
               vector3f2.set(Math.max(vector3f2.x, cube.maxX), Math.max(vector3f2.y, cube.maxY), Math.max(vector3f2.z, cube.maxZ));
               float floatValue = (vector3f.x + vector3f2.x) / 32.0F;
               float floatValue2 = (vector3f.z + vector3f2.z) / 32.0F;
               pose.pose().transformPosition(floatValue, vector3f.y / 16.0F, floatValue2, new Vector3f());
               pose.pose().transformPosition(floatValue, vector3f2.y / 16.0F, floatValue2, new Vector3f());
            }
         });
         return vector3fArray;
      }

      private void m_0(Vector3f vector3f, Vector3f vector3f2) {
         this.vec3List.add(this.vec3.add(vector3f.x, vector3f.y, vector3f.z));
         this.vec3List.add(this.vec3.add(vector3f2.x, vector3f2.y, vector3f2.z));
      }
   }
}
