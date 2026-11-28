package wtf.onetap.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import org.lwjgl.glfw.GLFW;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;

/**
 * Base for the HUD elements ported from Moonward. Each element keeps its position in the hidden xCoord and yCoord
 * settings, is clamped to the screen and can be dragged while the chat is open.
 *
 * The event bus registers only the methods declared in the concrete class, so every concrete element declares its
 * own @EventTarget handlers and forwards them to hudRender and hudMouse below.
 */
public abstract class HudElement extends Module {
   public final FloatSetting xCoord = new FloatSetting("xCoord", 40.0F, 0.0F, 4000.0F, 1.0F).visibleWhen(() -> false).build();
   public final FloatSetting yCoord = new FloatSetting("yCoord", 40.0F, 0.0F, 4000.0F, 1.0F).visibleWhen(() -> false).build();
   public final FloatSetting size = new FloatSetting("Размер", 1.0F, 0.6F, 2.0F, 0.05F).build();

   private float width;
   private float height;
   private boolean dragging;
   private float dragOffsetX;
   private float dragOffsetY;

   /** Draws the element with its top-left corner at (x, y). Must call setSize with the drawn width and height. */
   protected abstract void drawElement(Render2DEvent event, float x, float y, float scale);

   protected final void setSize(float w, float h) {
      this.width = w;
      this.height = h;
   }

   @Override
   public void onDisable() {
      this.dragging = false;
   }

   protected final void hudRender(Render2DEvent event) {
      Minecraft mc = MINECRAFT;
      if (mc.player == null || mc.getWindow() == null) {
         return;
      }

      float scale = this.size.getFloat();
      if (this.dragging) {
         if (!chatOpen(mc)) {
            this.dragging = false;
         } else {
            this.xCoord.setValue((float) cursorX(mc) - this.dragOffsetX);
            this.yCoord.setValue((float) cursorY(mc) - this.dragOffsetY);
         }
      }

      float screenW = mc.getWindow().getGuiScaledWidth();
      float screenH = mc.getWindow().getGuiScaledHeight();
      float x = clamp(this.xCoord.getFloat(), 0.0F, Math.max(0.0F, screenW - this.width));
      float y = clamp(this.yCoord.getFloat(), 0.0F, Math.max(0.0F, screenH - this.height));
      if (!this.dragging) {
         if (x != this.xCoord.getFloat()) {
            this.xCoord.setValue(x);
         }

         if (y != this.yCoord.getFloat()) {
            this.yCoord.setValue(y);
         }
      }

      this.drawElement(event, x, y, scale);
   }

   protected final void hudMouse(MouseClickEvent event) {
      if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
         return;
      }

      if (event.getAction() == GLFW.GLFW_RELEASE) {
         this.dragging = false;
         return;
      }

      Minecraft mc = MINECRAFT;
      if (event.getAction() == GLFW.GLFW_PRESS && chatOpen(mc) && mc.getWindow() != null) {
         float cx = (float) cursorX(mc);
         float cy = (float) cursorY(mc);
         float x = this.xCoord.getFloat();
         float y = this.yCoord.getFloat();
         if (cx >= x && cx <= x + this.width && cy >= y && cy <= y + this.height) {
            this.dragging = true;
            this.dragOffsetX = cx - x;
            this.dragOffsetY = cy - y;
         }
      }
   }

   protected static boolean chatOpen(Minecraft mc) {
      return mc.gui != null && mc.gui.screen() instanceof ChatScreen;
   }

   protected static double cursorX(Minecraft mc) {
      return mc.mouseHandler.getScaledXPos(mc.getWindow());
   }

   protected static double cursorY(Minecraft mc) {
      return mc.mouseHandler.getScaledYPos(mc.getWindow());
   }

   protected static float clamp(float value, float min, float max) {
      return Math.max(min, Math.min(max, value));
   }
}
