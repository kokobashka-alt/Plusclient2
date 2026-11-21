package wtf.onetap.module.render;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;

@ModuleInformation(name = "ItemPhysics", description = "Физика упавших предметов", category = Category.RENDER)
public class ItemPhysics extends Module {
   private static final Map<ItemEntityRenderState, ItemPhysics.DataRecord> ITEM_ENTITY_RENDER_STATE_MAP = new WeakHashMap<>();

   public static void putState(ItemEntityRenderState itemEntityRenderState, boolean flag) {
      if (itemEntityRenderState != null) {
         ITEM_ENTITY_RENDER_STATE_MAP.put(itemEntityRenderState, new ItemPhysics.DataRecord(flag));
      }
   }

   public static ItemPhysics.DataRecord getState(ItemEntityRenderState itemEntityRenderState) {
      return itemEntityRenderState == null ? null : ITEM_ENTITY_RENDER_STATE_MAP.get(itemEntityRenderState);
   }

   public record DataRecord(boolean f_5_k) {
      public boolean m_5_k() {
         return this.f_5_k;
      }
   }
}
