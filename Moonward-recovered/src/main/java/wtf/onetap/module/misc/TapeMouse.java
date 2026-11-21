package wtf.onetap.module.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.network.ServerTpsTracker;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "TapeMouse", description = "Автокликер", category = Category.MISC)
public class TapeMouse extends Module {
   private final FloatSetting cooldown = new FloatSetting("Кулдаун", 0.9F, 0.0F, 1.2F, 0.01F).build();
   private final BooleanSetting considerTps = new BooleanSetting("Учитывать TPS", false).build();
   private long longValue;

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gui.screen() == null && MinecraftAccess.MINECRAFT.gameMode != null) {
         long longValue2 = System.currentTimeMillis();
         if (m_3(MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(0.5F), MinecraftAccess.MINECRAFT.player.getCurrentItemAttackStrengthDelay(), this.longValue, longValue2)) {
            m_3(MinecraftAccess.MINECRAFT);
            this.longValue = longValue2;
         }
      }
   }

   public static boolean m_3(float floatValue, float floatValue2, long longValue2, long longValue3) {
      UnHook.m_7_IArr();
      TapeMouse tapeMouse = Onetap.MODULE_MANAGER.get(TapeMouse.class);
      float floatValue3 = tapeMouse != null ? tapeMouse.cooldown.getFloat() : 0.9F;
      boolean flag = tapeMouse != null && (Boolean)tapeMouse.considerTps.getValue();
      float floatValue4 = flag ? Mth.clamp(ServerTpsTracker.SERVER_TPS_TRACKER.getTps(), 0.1F, 20.0F) : 20.0F;
      return m_0(floatValue, floatValue2, floatValue3, flag, floatValue4, longValue2, longValue3);
   }

   public static boolean m_0(float floatValue, float floatValue2, float floatValue3, boolean flag, float floatValue4, long longValue2, long longValue3) {
      if (floatValue < Math.min(floatValue3, 1.0F)) {
         return false;
      } else {
         return floatValue3 <= 1.0F && !flag ? true : (float)(longValue3 - longValue2) >= floatValue3 * floatValue2 * 1000.0F / floatValue4;
      }
   }

   public static void m_3(Minecraft mc) {
      if (mc.hitResult instanceof EntityHitResult entityHitResult) {
         mc.gameMode.attack(mc.player, entityHitResult.getEntity());
      } else {
         mc.player.resetAttackStrengthTicker();
      }

      mc.player.swing(InteractionHand.MAIN_HAND);
   }
}
