package wtf.onetap.render;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import wtf.onetap.mixin.render.EntityRendererAccessor;

public class EntityRenderHelper {
   private final Map<Entity, EntityRenderHelper.StateHolder> entityMap;

   public EntityRenderHelper() {
      this.entityMap = new WeakHashMap<>();
   }

   public EntityRenderState m_9(EntityRenderer<?, ?> entityRenderer, Entity entity, float floatValue) {
      if (!(entity instanceof AbstractClientPlayer)) {
         return createRenderState(entityRenderer, entity, floatValue);
      }

      EntityRenderHelper.StateHolder c6 = this.entityMap.get(entity);
      if (c6 != null && c6.weakReference.get() != entity) {
         this.entityMap.remove(entity);
         c6 = null;
      }

      if (c6 == null || c6.entityRenderer != entityRenderer) {
         this.entityMap.remove(entity);
         c6 = new EntityRenderHelper.StateHolder(entity, entityRenderer, createRenderState(entityRenderer));
         this.entityMap.put(entity, c6);
      }

      EntityRenderer entityRenderer2 = entityRenderer;
      entityRenderer2.extractRenderState(entity, c6.entityRenderState, floatValue);
      ((EntityRendererAccessor)entityRenderer).onetap$finalizeRenderState(entity, c6.entityRenderState);
      return c6.entityRenderState;
   }

   public void clear() {
      this.entityMap.clear();
   }

   private static EntityRenderState createRenderState(EntityRenderer<?, ?> entityRenderer) {
      return entityRenderer.createRenderState();
   }

   private static EntityRenderState createRenderState(EntityRenderer<?, ?> entityRenderer, Entity entity, float floatValue) {
      return ((EntityRenderer)entityRenderer).createRenderState(entity, floatValue);
   }

   private static final class StateHolder {
      private final WeakReference<Entity> weakReference;
      private final EntityRenderer<?, ?> entityRenderer;
      private final EntityRenderState entityRenderState;

      private StateHolder(Entity entity, EntityRenderer<?, ?> entityRenderer2, EntityRenderState entityRenderState2) {
         this.weakReference = new WeakReference<>(entity);
         this.entityRenderer = entityRenderer2;
         this.entityRenderState = entityRenderState2;
      }
   }
}
