package wtf.onetap.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.util.MinecraftAccess;

/**
 * One drawable part of the Nursultan HUD. The owning NursultanHud module chooses where it is drawn and stores its
 * position. An element reports its drawn size through setSize so the module can clamp and drag it.
 */
abstract class NursultanElement implements MinecraftAccess {
   private float width;
   private float height;

   /** Draws the element with its top-left corner at (x, y) and calls setSize with the size it drew. */
   abstract void draw(Render2DEvent event, float x, float y, float scale);

   protected final void setSize(float w, float h) {
      this.width = w;
      this.height = h;
   }

   float width() {
      return this.width;
   }

   float height() {
      return this.height;
   }

   static boolean chatOpen(Minecraft mc) {
      return mc.gui != null && mc.gui.screen() instanceof ChatScreen;
   }

   static int withAlpha(int color, int alpha) {
      return ColorUtil.withAlpha(color, alpha);
   }
}
