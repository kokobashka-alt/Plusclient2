package baritone.selection;

import baritone.Baritone;
import baritone.api.event.events.RenderEvent;
import baritone.api.event.listener.AbstractGameEventListener;
import baritone.api.selection.ISelection;
import baritone.utils.IRenderer;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.AABB;

public class SelectionRenderer implements IRenderer, AbstractGameEventListener {
   private final SelectionManager manager;

   SelectionRenderer(Baritone var1, SelectionManager var2) {
      this.manager = var2;
      var1.getGameEventHandler().registerEventListener(this);
   }

   public static void renderSelections(PoseStack var0, ISelection[] var1) {
      float var2 = settings.selectionOpacity.value;
      boolean var3 = settings.renderSelectionIgnoreDepth.value;
      float var4 = settings.selectionLineWidth.value;
      if (settings.renderSelection.value && var1.length != 0) {
         BufferBuilder var5 = IRenderer.startLines(settings.colorSelection.value, var2);

         for (ISelection var9 : var1) {
            IRenderer.emitAABB(var5, var0, var9.aabb(), 0.005, var4);
         }

         if (settings.renderSelectionCorners.value) {
            IRenderer.glColor(settings.colorSelectionPos1.value, var2);

            for (ISelection var16 : var1) {
               IRenderer.emitAABB(var5, var0, new AABB(var16.pos1()), var4);
            }

            IRenderer.glColor(settings.colorSelectionPos2.value, var2);

            for (ISelection var17 : var1) {
               IRenderer.emitAABB(var5, var0, new AABB(var17.pos2()), var4);
            }
         }

         IRenderer.endLines(var5, var3);
      }
   }

   @Override
   public void onRenderPass(RenderEvent var1) {
      renderSelections(var1.getModelViewStack(), this.manager.getSelections());
   }
}
