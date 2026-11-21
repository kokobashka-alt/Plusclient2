package wtf.onetap.module.player;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "CrystalOptimizer", description = "Мгновенно убирает кристалл на клиенте при ударе", category = Category.PLAYER)
public class CrystalOptimizer extends Module {
   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (event.getPacket() instanceof ServerboundAttackPacket) {
         this.m_2_void();
      }
   }

   public void m_2_void() {
      VoiceChat.m_9_StringArr();
      if (MinecraftAccess.MINECRAFT.hitResult instanceof EntityHitResult entityHitResult) {
         Entity entity = entityHitResult.getEntity();
         if (entity instanceof EndCrystal) {
            LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
            if (localPlayer != null) {
               double doubleValue = this.m_8_double(localPlayer);
               if (doubleValue > 0.0) {
                  this.removeEntity(entity);
               }
            }
         }
      }
   }

   private double m_8_double(LocalPlayer localPlayer) {
      double doubleValue = localPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE);
      double doubleValue2 = this.m_4_double(localPlayer.getMainHandItem());
      MobEffectInstance mobEffectInstance = localPlayer.getEffect(MobEffects.STRENGTH);
      double doubleValue3 = mobEffectInstance != null ? 3.0 * (mobEffectInstance.getAmplifier() + 1) : 0.0;
      MobEffectInstance mobEffectInstance2 = localPlayer.getEffect(MobEffects.WEAKNESS);
      double doubleValue4 = mobEffectInstance2 != null ? 4.0 * (mobEffectInstance2.getAmplifier() + 1) : 0.0;
      return Math.max(0.0, doubleValue + doubleValue2 + doubleValue3 - doubleValue4);
   }

   private double m_4_double(ItemStack itemStack) {
      if (itemStack.isEmpty()) {
         return 0.0;
      }

      double[] doubleArray = new double[]{0.0};
      itemStack.forEachModifier(EquipmentSlot.MAINHAND, (holder, attributeModifier) -> {
         if (holder.is(Attributes.ATTACK_DAMAGE)) {
            attributeModifier.amount();
         }
      });
      return doubleArray[0];
   }

   private void removeEntity(Entity entity) {
      entity.remove(RemovalReason.KILLED);
      entity.gameEvent(GameEvent.ENTITY_DIE);
   }
}
