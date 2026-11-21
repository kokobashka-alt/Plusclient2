package wtf.onetap.module.misc;

import java.lang.reflect.Method;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "ItemScroller", description = "Быстрое перемещение предметов в инвентаре (Shift+ЛКМ)", category = Category.MISC)
public class ItemScroller extends Module {
   private final FloatSetting delayMs = new FloatSetting("Задержка (мс)", 50.0F, 0.0F, 200.0F, 1.0F).build();
   private final TimeHelper timeHelper = new TimeHelper();

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gui.screen() != null) {
         if (MinecraftAccess.MINECRAFT.gui.screen() instanceof AbstractContainerScreen abstractContainerScreen) {
            long longValue = MinecraftAccess.MINECRAFT.getWindow().handle();
            boolean flag = GLFW.glfwGetKey(longValue, 340) == 1 || GLFW.glfwGetKey(longValue, 344) == 1;
            boolean flag2 = GLFW.glfwGetMouseButton(longValue, 0) == 1;
            if (flag && flag2) {
               double doubleValue = MinecraftAccess.MINECRAFT.mouseHandler.getScaledXPos(MinecraftAccess.MINECRAFT.getWindow());
               double doubleValue2 = MinecraftAccess.MINECRAFT.mouseHandler.getScaledYPos(MinecraftAccess.MINECRAFT.getWindow());

               try {
                  Method method = AbstractContainerScreen.class.getDeclaredMethod("getHoveredSlot", double.class, double.class);
                  method.setAccessible(true);
                  Slot slot = (Slot)method.invoke(abstractContainerScreen, doubleValue, doubleValue2);
                  if (slot != null && slot.hasItem() && this.timeHelper.hasElapsed(this.delayMs.getInt())) {
                     InventoryUtil.clickSlot(abstractContainerScreen.getMenu().containerId, slot.index, 0, ContainerInput.QUICK_MOVE);
                     this.timeHelper.reset();
                  }
               } catch (Exception e) {
               }
            }
         }
      }
   }
}
