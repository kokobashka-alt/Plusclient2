package wtf.onetap.event;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import wtf.onetap.module.Module;

public class Render2DEvent extends Event {
   private final float floatValue;
   private final GuiGraphicsExtractor guiGraphicsExtractor;
   private static Module[] moduleArray;

   public Render2DEvent(float floatValue2, GuiGraphicsExtractor guiGraphicsExtractor2) {
      this.floatValue = floatValue2;
      this.guiGraphicsExtractor = guiGraphicsExtractor2;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public GuiGraphicsExtractor getGuiGraphicsExtractor() {
      return this.guiGraphicsExtractor;
   }

   public static void m_1(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_6_C3_sArr() {
      return moduleArray;
   }
}
