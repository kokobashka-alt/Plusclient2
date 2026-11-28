package wtf.onetap.module;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;

/**
 * Nursultan HUD as one module: watermark, hotkeys list, active potions, target and staff list. Each element has its
 * own on/off switch and its own position. Positions are changed by dragging an element while the chat is open.
 */
@ModuleInformation(name = "NursultanHud", description = "Худ в стиле Nursultan: ватермарка, бинды, бафы, таргет, стафф", category = Category.RENDER)
public final class NursultanHud extends Module {
   public final FloatSetting size = new FloatSetting("Размер", 1.0F, 0.6F, 2.0F, 0.05F).build();
   private final BooleanSetting themeBar = new BooleanSetting("Таргет худ от темы", false).build();

   private final NursultanWatermark watermark = new NursultanWatermark();
   private final NursultanKeyBindsPanel keyBinds = new NursultanKeyBindsPanel();
   private final NursultanPotionsPanel potions = new NursultanPotionsPanel();
   private final NursultanTargetPanel target = new NursultanTargetPanel(this.themeBar);
   private final NursultanStaffPanel staff = new NursultanStaffPanel();

   private final Slot[] slots = {
      this.slot("Ватермарка", this.watermark, 40.0F, 40.0F),
      this.slot("Привязанные модули", this.keyBinds, 40.0F, 80.0F),
      this.slot("Бафы", this.potions, 40.0F, 200.0F),
      this.slot("Таргет", this.target, 300.0F, 40.0F),
      this.slot("Стафф", this.staff, 40.0F, 340.0F)
   };

   private int dragging = -1;
   private float dragOffsetX;
   private float dragOffsetY;

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      this.renderAll(event);
   }

   @EventTarget
   private void onMouseClick(MouseClickEvent event) {
      this.handleMouse(event);
   }

   @Override
   public void onDisable() {
      this.dragging = -1;
   }

   private void renderAll(Render2DEvent event) {
      Minecraft mc = MINECRAFT;
      if (mc.player == null || mc.getWindow() == null) {
         return;
      }

      float scale = this.size.getFloat();
      if (this.dragging >= 0 && !NursultanElement.chatOpen(mc)) {
         this.dragging = -1;
      }

      if (this.dragging >= 0) {
         Slot dragged = this.slots[this.dragging];
         dragged.x.setValue((float) cursorX(mc) - this.dragOffsetX);
         dragged.y.setValue((float) cursorY(mc) - this.dragOffsetY);
      }

      float screenW = mc.getWindow().getGuiScaledWidth();
      float screenH = mc.getWindow().getGuiScaledHeight();
      for (int i = 0; i < this.slots.length; i++) {
         Slot slot = this.slots[i];
         if (!slot.enabled.getValue()) {
            continue;
         }

         float x = clamp(slot.x.getFloat(), 0.0F, Math.max(0.0F, screenW - slot.element.width()));
         float y = clamp(slot.y.getFloat(), 0.0F, Math.max(0.0F, screenH - slot.element.height()));
         if (i != this.dragging) {
            if (x != slot.x.getFloat()) {
               slot.x.setValue(x);
            }

            if (y != slot.y.getFloat()) {
               slot.y.setValue(y);
            }
         }

         slot.element.draw(event, x, y, scale);
      }
   }

   private void handleMouse(MouseClickEvent event) {
      if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
         return;
      }

      if (event.getAction() == GLFW.GLFW_RELEASE) {
         this.dragging = -1;
         return;
      }

      Minecraft mc = MINECRAFT;
      if (event.getAction() != GLFW.GLFW_PRESS || !NursultanElement.chatOpen(mc) || mc.getWindow() == null) {
         return;
      }

      float cx = (float) cursorX(mc);
      float cy = (float) cursorY(mc);
      for (int i = 0; i < this.slots.length; i++) {
         Slot slot = this.slots[i];
         if (!slot.enabled.getValue()) {
            continue;
         }

         float x = slot.x.getFloat();
         float y = slot.y.getFloat();
         if (cx >= x && cx <= x + slot.element.width() && cy >= y && cy <= y + slot.element.height()) {
            this.dragging = i;
            this.dragOffsetX = cx - x;
            this.dragOffsetY = cy - y;
            return;
         }
      }
   }

   private Slot slot(String name, NursultanElement element, float x, float y) {
      return new Slot(element,
            new BooleanSetting(name, true).build(),
            new FloatSetting("x " + name, x, 0.0F, 4000.0F, 1.0F).visibleWhen(() -> false).build(),
            new FloatSetting("y " + name, y, 0.0F, 4000.0F, 1.0F).visibleWhen(() -> false).build());
   }

   private static double cursorX(Minecraft mc) {
      return mc.mouseHandler.getScaledXPos(mc.getWindow());
   }

   private static double cursorY(Minecraft mc) {
      return mc.mouseHandler.getScaledYPos(mc.getWindow());
   }

   private static float clamp(float value, float min, float max) {
      return Math.max(min, Math.min(max, value));
   }

   private static final class Slot {
      private final NursultanElement element;
      private final BooleanSetting enabled;
      private final FloatSetting x;
      private final FloatSetting y;

      private Slot(NursultanElement element, BooleanSetting enabled, FloatSetting x, FloatSetting y) {
         this.element = element;
         this.enabled = enabled;
         this.x = x;
         this.y = y;
      }
   }
}
