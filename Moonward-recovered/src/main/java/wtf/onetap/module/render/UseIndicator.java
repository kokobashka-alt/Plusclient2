package wtf.onetap.module.render;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Map;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import org.joml.Matrix4f;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Render2D;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.WorldToScreen;

@ModuleInformation(name = "UseIndicator", description = "Кольцо с предметом в центре модели при использовании", category = Category.RENDER)
public class UseIndicator extends Module {
   private final BooleanSetting themeColor = new BooleanSetting("Цвет от темы", true).build();
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(140, 180, 255, 255)).visibleWhen(() -> !(Boolean)this.themeColor.getValue()).build();
   private final FloatSetting size = new FloatSetting("Размер", 1.0F, 0.6F, 1.6F, 0.05F).build();
   private final Map<Integer, UseIndicator.StateHolder> integerMap = new HashMap<>();

   @Override
   public void onDisable() {
      this.integerMap.clear();
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         CameraRenderState cameraRenderState = MinecraftAccess.MINECRAFT.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
         WorldToScreen.setMatrices(new Matrix4f(cameraRenderState.projectionMatrix), new Matrix4f(cameraRenderState.viewRotationMatrix));
         float floatValue = event.getFloatValue();

         for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
            if (player.isAlive() && (player != MinecraftAccess.MINECRAFT.player || !MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson()) && m_3_boolean(player)) {
               this.integerMap.computeIfAbsent(player.getId(), integer -> new UseIndicator.StateHolder(player));
            }
         }

         var iterator = this.integerMap.entrySet().iterator();

         while (iterator.hasNext()) {
            Entry entry = (Entry)iterator.next();
            if (!(MinecraftAccess.MINECRAFT.level.getEntity((Integer)entry.getKey()) instanceof Player player2 && player2.isAlive())) {
               iterator.remove();
            } else if (player2 == MinecraftAccess.MINECRAFT.player && MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson()) {
               iterator.remove();
            } else {
               boolean flag = m_3_boolean(player2);
               UseIndicator.StateHolder c7 = (UseIndicator.StateHolder)entry.getValue();
               float floatValue2 = c7.animation.update(flag ? 1.0F : 0.0F);
               if (!flag && floatValue2 < 0.01F) {
                  iterator.remove();
               } else {
                  if (flag) {
                     c7.m_6_void(player2);
                  }

                  if (!c7.itemStack.isEmpty()) {
                     int value = flag ? player2.getUseItemRemainingTicks() : 0;
                     int value2 = flag ? player2.getUseItem().getUseDuration(player2) : 0;
                     float floatValue3 = m_5(flag, value, value2, floatValue, c7.floatValue);
                     if (flag) {
                        c7.floatValue = floatValue3;
                     }

                     this.m_4(event, player2, c7, floatValue2, floatValue3, floatValue);
                  }
               }
            }
         }
      } else {
         this.integerMap.clear();
      }
   }

   public static float m_5(boolean flag, int value, int value2, float floatValue, float floatValue2) {
      if (!flag) {
         return floatValue2;
      } else {
         return value2 <= 0 ? 1.0F : Math.clamp((value2 - value + floatValue) / value2, 0.0F, 1.0F);
      }
   }

   private void m_4(Render2DEvent event, Player player, UseIndicator.StateHolder c7, float floatValue, float floatValue2, float floatValue3) {
      double doubleValue = player.xo + (player.getX() - player.xo) * floatValue3;
      Notifications.m_5_C3_sArr();
      double doubleValue2 = player.yo + (player.getY() - player.yo) * floatValue3 + player.getBbHeight() * 0.5;
      double doubleValue3 = player.zo + (player.getZ() - player.zo) * floatValue3;
      WorldToScreen.DataRecord c9 = WorldToScreen.project(doubleValue, doubleValue2, doubleValue3);
      if (c9 != null) {
         float floatValue4 = floatValue * this.size.getFloat();
         if (!(floatValue4 < 0.02F)) {
            float floatValue5 = c9.m_2r_float();
            float floatValue6 = c9.m_2___float();
            float floatValue7 = 16.0F * floatValue4;
            float floatValue8 = 2.1F * floatValue4;
            int value = (int)(255.0F * floatValue);
            int value2 = (int)(100.0F * floatValue);
            int value3 = this.themeColor.getValue() ? ColorUtil.primaryColor() : (Integer)this.color.getValue();
            int value4 = this.themeColor.getValue() ? ColorUtil.secondaryColor() : ColorUtil.lerpColor(value3, -16777216, 0.35F);
            float floatValue9 = floatValue7 * 0.78F;
            Draw2D.roundedRect(floatValue5 - floatValue9, floatValue6 - floatValue9, floatValue9 * 2.0F, floatValue9 * 2.0F, floatValue9, ColorUtil.rgba(0, 0, 0, (int)(110.0F * floatValue)));
            Draw2D.m_5(floatValue5, floatValue6, floatValue7, floatValue8, 1.0F, -1.5707963F, 0.0F, ColorUtil.rgba(255, 255, 255, (int)(22.0F * floatValue)));
            if (floatValue2 > 0.001F) {
               m_5(floatValue5, floatValue6, floatValue7, floatValue8 + 1.6F * floatValue4, floatValue2, -1.5707963F, 2.4F * floatValue4, ColorUtil.withAlpha(value4, value2), ColorUtil.withAlpha(value3, value2), 24);
               m_5(floatValue5, floatValue6, floatValue7, floatValue8, floatValue2, -1.5707963F, 0.0F, ColorUtil.withAlpha(value4, value), ColorUtil.withAlpha(value3, value), 24);
            }

            float floatValue10 = floatValue7 * 0.78F;
            TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
            MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, c7.itemStack, ItemDisplayContext.GUI, player.level(), player, player.getId());
            event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
            Render2D.drawItem(trackingItemStackRenderState, floatValue5 - floatValue10 * 0.5F, floatValue6 - floatValue10 * 0.5F, floatValue10, ColorUtil.rgba(255, 255, 255, value));
         }
      }
   }

   private static boolean m_3_boolean(Player player) {
      if (!player.isUsingItem()) {
         return false;
      } else {
         ItemStack itemStack = player.getUseItem();
         if (!itemStack.isEmpty() && itemStack.getUseDuration(player) > 0) {
            ItemUseAnimation itemUseAnimation = itemStack.getUseAnimation();
            return itemUseAnimation != ItemUseAnimation.BOW && itemUseAnimation != ItemUseAnimation.TRIDENT && itemUseAnimation != ItemUseAnimation.CROSSBOW && itemUseAnimation != ItemUseAnimation.SPEAR;
         } else {
            return false;
         }
      }
   }

   private static void m_5(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, int value, int value2, int value3) {
      if (!(floatValue3 <= 0.0F) && !(floatValue4 <= 0.0F) && !(floatValue5 <= 0.0F) && value3 >= 1) {
         double doubleValue = System.currentTimeMillis() / 200.0;
         float floatValue8 = floatValue5 * (float) (Math.PI * 2);
         float floatValue9 = floatValue8 / value3;

         for (int i = 0; i < value3; i++) {
            float floatValue10 = floatValue6 + i * floatValue9;
            float floatValue11 = (float)(Math.sin(doubleValue + floatValue10) * 0.5 + 0.5);
            int value4 = ColorUtil.lerpColor(value, value2, floatValue11);
            Draw2D.m_5(floatValue, floatValue2, floatValue3, floatValue4, floatValue9 / (float) (Math.PI * 2), floatValue10, floatValue7, value4);
         }
      }
   }

   private static final class StateHolder {
      public final Animation animation;
      public ItemStack itemStack;
      public float floatValue;

      public StateHolder(Player player) {
         this.animation = new Animation(280L, 0.0F, Easing.EASING8);
         this.itemStack = ItemStack.EMPTY;
         this.m_6_void(player);
      }

      public void m_6_void(Player player) {
         Notifications.m_5_C3_sArr();
         ItemStack itemStack2 = player.getUseItem();
         if (!itemStack2.isEmpty()) {
            if (this.itemStack.isEmpty() || !ItemStack.isSameItem(this.itemStack, itemStack2)) {
               this.itemStack = itemStack2.copy();
            }
         }
      }
   }
}
