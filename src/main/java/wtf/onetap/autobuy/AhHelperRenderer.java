package wtf.onetap.autobuy;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import wtf.onetap.module.pve.C4q;
import wtf.onetap.platform.PlatformConnection;
import wtf.onetap.util.LatencyStats;

public class AhHelperRenderer {
   private AhHelperRenderer() {
   }

   public static void m_9(GuiGraphicsExtractor guiGraphicsExtractor, AbstractContainerScreen<?> abstractContainerScreen) {
      LatencyStats.StateHolder.m_6_C3_sArr();
      C4q c4q = PlatformConnection.PLATFORM_CONNECTION.getModule("ahhelper");
      if (c4q != null && c4q.isEnabled()) {
         JsonObject jsonObject = PlatformConnection.PLATFORM_CONNECTION.getJson("ahhelper");
         AbstractContainerMenu abstractContainerMenu = abstractContainerScreen.getMenu();
         if (jsonObject.has("w") && jsonObject.get("w").getAsInt() == abstractContainerMenu.containerId) {
            Slot slot = getSlot(abstractContainerScreen, jsonObject.has("cheap") ? jsonObject.get("cheap").getAsInt() : -1);
            Slot slot2 = getSlot(abstractContainerScreen, jsonObject.has("best") ? jsonObject.get("best").getAsInt() : -1);
            int value = pulseAlpha(m_3(c4q, "Дешевый предмет", -11796661));
            int value2 = pulseAlpha(m_3(c4q, "Выгодный предмет", -16776961));
            if (slot != null && slot != slot2) {
               m_1(guiGraphicsExtractor, slot, value);
            }

            if (slot2 != null) {
               m_1(guiGraphicsExtractor, slot2, value2);
            }
         }
      }
   }

   private static Slot getSlot(AbstractContainerScreen<?> abstractContainerScreen, int value) {
      var nonNullList = abstractContainerScreen.getMenu().slots;
      return value >= 0 && value < nonNullList.size() ? (Slot)nonNullList.get(value) : null;
   }

   private static int m_3(C4q c4q, String text, int value) {
      return c4q.getSettingValue(text) instanceof Integer integer ? integer : value;
   }

   private static void m_1(GuiGraphicsExtractor guiGraphicsExtractor, Slot slot, int value) {
      guiGraphicsExtractor.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, scaleAlpha(value, 0.3F));
      guiGraphicsExtractor.outline(slot.x - 1, slot.y - 1, 18, 18, value | 0xFF000000);
   }

   private static int pulseAlpha(int value) {
      return scaleAlpha(value, (float)Math.abs(Math.sin(System.currentTimeMillis() / 3.0 * Math.PI / 180.0)));
   }

   private static int scaleAlpha(int value, float floatValue) {
      int value2 = Math.round((value >>> 24 & 0xFF) * floatValue);
      return value2 << 24 | value & 16777215;
   }
}
