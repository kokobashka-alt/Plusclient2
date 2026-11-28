package wtf.onetap.module;

import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;

@ModuleInformation(name = "TotemCounter", description = "Количество тотемов бессмертия в инвентаре", category = Category.RENDER)
public final class TotemCounterHud extends HudElement {
   private static final float ICON = 10.0F;
   private ItemStack totem;

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      this.hudRender(event);
   }

   @EventTarget
   private void onMouseClick(MouseClickEvent event) {
      this.hudMouse(event);
   }

   @Override
   protected void drawElement(Render2DEvent event, float x, float y, float scale) {
      Inventory inventory = MINECRAFT.player.getInventory();
      int count = 0;
      for (int i = 0; i < inventory.getContainerSize(); i++) {
         ItemStack stack = inventory.getItem(i);
         if (stack.is(Items.TOTEM_OF_UNDYING)) {
            count += stack.getCount();
         }
      }

      float icon = ICON * scale;
      float fontSize = 8.5F * scale;
      String text = count + "x";
      float textX = x + icon + 2.0F * scale;
      float width = textX - x + Draw2D.getTextWidth(text, Fonts.SUPPLIER18, fontSize);
      float height = Math.max(icon, Draw2D.getFontHeight(Fonts.SUPPLIER18, fontSize));

      if (this.totem == null) {
         this.totem = new ItemStack(Items.TOTEM_OF_UNDYING);
      }

      TrackingItemStackRenderState state = new TrackingItemStackRenderState();
      MINECRAFT.getItemModelResolver().updateForTopItem(state, this.totem, ItemDisplayContext.GUI, MINECRAFT.level, MINECRAFT.player, 0);
      event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(state.getModelIdentity());
      Render2D.drawItem(state, x, y, icon, ColorUtil.rgba(255, 255, 255, 255));

      int textColor = count > 0 ? ColorUtil.rgba(255, 255, 255, 255) : ColorUtil.rgba(255, 85, 85, 255);
      float textY = Draw2D.getTextBaseline(Fonts.SUPPLIER18, fontSize, y, icon);
      Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER18, textX, textY, textColor, fontSize);

      this.setSize(width, height);
   }
}
