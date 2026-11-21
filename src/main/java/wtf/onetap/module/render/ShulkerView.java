package wtf.onetap.module.render;

import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Vector2ic;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.gui.ClickGuiScreen;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.WorldToScreen;

@ModuleInformation(name = "ShulkerView", description = "Показывает содержимое шалкеров на земле, в GUI и над игроками", category = Category.RENDER)
public class ShulkerView extends Module {
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/shulker_box.png");
   private final MultiModeSetting display = new MultiModeSetting("Отображать", "В руке", "В руке", "На земле", "В инвентаре").build();
   private final FloatSetting size = new FloatSetting("Размер", 0.5F, 0.3F, 1.0F, 0.1F).build();

   public ShulkerView() {
      this.display.setValue(new LinkedHashSet<>(List.of("В руке", "На земле", "В инвентаре")));
   }

   @EventTarget(priority = EventPriority.HIGH)
   private void onRender2D(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && event.getGuiGraphicsExtractor() != null) {
         if (!this.isScreenBlocking()) {
            CameraRenderState cameraRenderState = MinecraftAccess.MINECRAFT.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
            WorldToScreen.setMatrices(new Matrix4f(cameraRenderState.projectionMatrix), new Matrix4f(cameraRenderState.viewRotationMatrix));
            float floatValue = event.getFloatValue();
            GuiGraphicsExtractor guiGraphicsExtractor = event.getGuiGraphicsExtractor();
            if (this.display.isSelected("В руке")) {
               for (AbstractClientPlayer abstractClientPlayer : MinecraftAccess.MINECRAFT.level.players()) {
                  if (abstractClientPlayer.isAlive() && (abstractClientPlayer != MinecraftAccess.MINECRAFT.player || !MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson())) {
                     ItemStack itemStack = getHeldShulker(abstractClientPlayer);
                     if (itemStack != null) {
                        double doubleValue = Mth.lerp(floatValue, abstractClientPlayer.xo, abstractClientPlayer.getX());
                        double doubleValue2 = Mth.lerp(floatValue, abstractClientPlayer.yo, abstractClientPlayer.getY()) + abstractClientPlayer.getBbHeight() + (abstractClientPlayer == MinecraftAccess.MINECRAFT.player ? 1.2 : 0.5);
                        double doubleValue3 = Mth.lerp(floatValue, abstractClientPlayer.zo, abstractClientPlayer.getZ());
                        this.m_8(guiGraphicsExtractor, itemStack, doubleValue, doubleValue2, doubleValue3);
                     }
                  }
               }
            }

            if (this.display.isSelected("На земле")) {
               for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
                  if (entity instanceof ItemEntity itemEntity && itemEntity.isAlive()) {
                     ItemStack itemStack2 = itemEntity.getItem();
                     if (isShulkerBox(itemStack2) && getContents(itemStack2) != null) {
                        double doubleValue4 = Mth.lerp(floatValue, itemEntity.xo, itemEntity.getX());
                        double doubleValue5 = Mth.lerp(floatValue, itemEntity.yo, itemEntity.getY()) + 1.3;
                        double doubleValue6 = Mth.lerp(floatValue, itemEntity.zo, itemEntity.getZ());
                        this.m_8(guiGraphicsExtractor, itemStack2, doubleValue4, doubleValue5, doubleValue6);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isScreenBlocking() {
      return MinecraftAccess.MINECRAFT.gui.screen() != null || MinecraftAccess.MINECRAFT.gui.overlay() != null || ClickGUI.isActive() || ClickGuiScreen.isOpen();
   }

   public boolean shouldShowInInventory(Slot slot) {
      return !this.display.isSelected("В инвентаре") ? false : m_3_boolean(slot);
   }

   public boolean m_9(Slot slot) {
      return !this.display.isSelected("В инвентаре") ? false : m_3_boolean(slot);
   }

   private static boolean m_3_boolean(Slot slot) {
      if (slot != null && slot.hasItem()) {
         ItemStack itemStack = slot.getItem();
         return isShulkerBox(itemStack) && getContents(itemStack) != null;
      } else {
         return false;
      }
   }

   public void m_7(GuiGraphicsExtractor guiGraphicsExtractor, Slot slot, int value, int value2) {
      if (this.isEnabled() && guiGraphicsExtractor != null && slot != null) {
         ItemStack itemStack = slot.getItem();
         if (isShulkerBox(itemStack) && getContents(itemStack) != null) {
            int value3 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth();
            int value4 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight();
            float floatValue = value + 12.0F;
            float floatValue2 = value2 - 23.205F;
            if (floatValue + 149.6F > value3) {
               floatValue = value - 149.6F - 12.0F;
            }

            if (floatValue2 + 66.3F > value4) {
               floatValue2 = value4 - 66.3F - 8.0F;
            }

            if (floatValue < 0.0F) {
               floatValue = 8.0F;
            }

            if (floatValue2 < 0.0F) {
               floatValue2 = 8.0F;
            }

            this.m_8(guiGraphicsExtractor, itemStack, floatValue, floatValue2, 0.85F, false);
         }
      }
   }

   public void m_0(GuiGraphicsExtractor guiGraphicsExtractor, Slot slot, int value, int value2) {
      if (this.isEnabled() && guiGraphicsExtractor != null && slot != null) {
         ItemStack itemStack = slot.getItem();
         if (isShulkerBox(itemStack) && getContents(itemStack) != null) {
            int value3 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth();
            int value4 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight();
            Font font = MinecraftAccess.MINECRAFT.font;
            var componentList = Screen.getTooltipFromItem(MinecraftAccess.MINECRAFT, itemStack);
            int value5 = 0;
            int value6 = componentList.isEmpty() ? 0 : 4;

            for (Component component : componentList) {
               value5 = Math.max(value5, font.width(component));
               value6 += 9;
            }

            if (!componentList.isEmpty()) {
               value5 += 8;
               value6 += 4;
            }

            if (itemStack.getTooltipImage().isPresent()) {
               value5 = Math.max(value5, 176);
               value6 += 60;
            }

            Vector2ic vector2ic = DefaultTooltipPositioner.INSTANCE.positionTooltip(value3, value4, value, value2, value5, value6);
            float floatValue = vector2ic.x();
            float floatValue2 = vector2ic.y() + value6 + 6.0F;
            if (floatValue2 + 66.3F > value4 - 2) {
               floatValue2 = vector2ic.y() - 66.3F - 6.0F;
            }

            if (floatValue2 < 2.0F) {
               floatValue = vector2ic.x() + value5 + 6.0F;
               floatValue2 = vector2ic.y();
               if (floatValue + 149.6F > value3 - 2) {
                  floatValue = vector2ic.x() - 149.6F - 6.0F;
               }
            }

            if (floatValue < 2.0F) {
               floatValue = 2.0F;
            }

            if (floatValue + 149.6F > value3 - 2) {
               floatValue = value3 - 149.6F - 2.0F;
            }

            if (floatValue2 < 2.0F) {
               floatValue2 = 2.0F;
            }

            if (floatValue2 + 66.3F > value4 - 2) {
               floatValue2 = value4 - 66.3F - 2.0F;
            }

            this.m_8(guiGraphicsExtractor, itemStack, floatValue, floatValue2, 0.85F, false);
         }
      }
   }

   private void m_8(GuiGraphicsExtractor guiGraphicsExtractor, ItemStack itemStack, double doubleValue, double doubleValue2, double doubleValue3) {
      if (getContents(itemStack) != null) {
         WorldToScreen.DataRecord c9 = WorldToScreen.project(doubleValue, doubleValue2, doubleValue3);
         if (c9 != null) {
            double doubleValue4 = MinecraftAccess.MINECRAFT.player.getEyePosition().distanceTo(new Vec3(doubleValue, doubleValue2, doubleValue3));
            float floatValue = (float)(Mth.clamp(1.0 - doubleValue4 / 20.0, 0.3, 1.0) * this.size.getFloat());
            this.m_8(guiGraphicsExtractor, itemStack, c9.m_2r_float(), c9.m_2___float(), floatValue, true);
         }
      }
   }

   private void m_8(GuiGraphicsExtractor guiGraphicsExtractor, ItemStack itemStack, float floatValue, float floatValue2, float floatValue3, boolean flag) {
      var nonNullList = getContents(itemStack);
      if (nonNullList != null) {
         int value = m_0_int(itemStack);
         Matrix3x2fStack matrix3x2fStack = guiGraphicsExtractor.pose();
         matrix3x2fStack.pushMatrix();
         matrix3x2fStack.translate(floatValue, floatValue2);
         matrix3x2fStack.scale(floatValue3, floatValue3);
         if (flag) {
            matrix3x2fStack.translate(-88.0F, -36.0F);
         }

         guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, IDENTIFIER, 0, 0, 0.0F, 0.0F, 176, 17, 256, 256, value);
         guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, IDENTIFIER, 0, 17, 0.0F, 17.0F, 176, 54, 256, 256, value);
         guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, IDENTIFIER, 0, 71, 0.0F, 160.0F, 176, 7, 256, 256, value);

         for (int i = 0; i < nonNullList.size(); i++) {
            ItemStack itemStack2 = (ItemStack)nonNullList.get(i);
            if (!itemStack2.isEmpty()) {
               int value2 = 8 + i % 9 * 18;
               int value3 = 18 + i / 9 * 18;
               guiGraphicsExtractor.item(itemStack2, value2, value3);
               guiGraphicsExtractor.itemDecorations(MinecraftAccess.MINECRAFT.font, itemStack2, value2, value3);
            }
         }

         matrix3x2fStack.popMatrix();
      }
   }

   private static ItemStack getHeldShulker(Player player) {
      ItemStack itemStack = player.getMainHandItem();
      if (isShulkerBox(itemStack) && getContents(itemStack) != null) {
         return itemStack;
      }

      ItemStack itemStack2 = player.getOffhandItem();
      return isShulkerBox(itemStack2) && getContents(itemStack2) != null ? itemStack2 : null;
   }

   private static NonNullList<ItemStack> getContents(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         ItemContainerContents itemContainerContents = itemStack.get(DataComponents.CONTAINER);
         if (itemContainerContents == null) {
            return null;
         }

         var nonNullList = NonNullList.withSize(27, ItemStack.EMPTY);
         itemContainerContents.copyInto(nonNullList);

         for (ItemStack itemStack2 : nonNullList) {
            if (!itemStack2.isEmpty()) {
               return nonNullList;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static boolean isShulkerBox(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         Block block = itemStack.getItem() instanceof BlockItem blockItem ? blockItem.getBlock() : null;
         return block instanceof ShulkerBoxBlock;
      } else {
         return false;
      }
   }

   private static int m_0_int(ItemStack itemStack) {
      if (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock shulkerBoxBlock) {
         DyeColor dyeColor = shulkerBoxBlock.getColor();
         if (dyeColor != null) {
            int value = dyeColor.getTextureDiffuseColor();
            int value2 = (int)((value >> 16 & 0xFF) * 0.85F);
            int value3 = (int)((value >> 8 & 0xFF) * 0.85F);
            int value4 = (int)((value & 0xFF) * 0.85F);
            return 0xFF000000 | value2 << 16 | value3 << 8 | value4;
         }
      }

      return -7776283;
   }
}
