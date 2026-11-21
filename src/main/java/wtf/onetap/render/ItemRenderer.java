package wtf.onetap.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemRenderer {
   private final TrackingItemStackRenderState trackingItemStackRenderState;
   private ItemStack itemStack;
   private static String text;

   public ItemRenderer() {
      this.trackingItemStackRenderState = new TrackingItemStackRenderState();
      this.itemStack = ItemStack.EMPTY;
   }

   public void m_2(GuiGraphicsExtractor guiGraphicsExtractor, ItemStack itemStack2, float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      if (!itemStack2.isEmpty()) {
         if (!ItemStack.matches(this.itemStack, itemStack2)) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) {
               return;
            }

            this.itemStack = itemStack2.copy();
            mc.getItemModelResolver().updateForTopItem(this.trackingItemStackRenderState, itemStack2, ItemDisplayContext.GUI, mc.level, mc.player, 0);
         }

         guiGraphicsExtractor.guiRenderState.getItemModelIdentities().add(this.trackingItemStackRenderState.getModelIdentity());
         Render2D.drawItem(this.trackingItemStackRenderState, floatValue, floatValue2, floatValue3, ColorUtil.rgba(255, 255, 255, (int)(Float.intBitsToFloat((int)-3789181109888155648L) * floatValue4)));
      }
   }

   public static void m_2_void(String text2) {
      text = text2;
   }

   public static String m_0_String() {
      return text;
   }
}
