package wtf.onetap.module.player;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.components.LerpingBossEvent;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.DropItemEvent;
import wtf.onetap.mixin.render.BossHealthOverlayAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "LockSlot", description = "Запрещает выбрасывать предметы из выбранных слотов", category = Category.PLAYER)
public class LockSlot extends Module {
   private final MultiModeSetting slots = new MultiModeSetting("Слоты", "1", "1", "2", "3", "4", "5", "6", "7", "8", "9").build();
   private final BooleanSetting onlyInPvp = new BooleanSetting("Только в PVP", true).build();

   public LockSlot() {
      this.slots.setValue(new LinkedHashSet());
   }

   @EventTarget
   private void onDropItem(DropItemEvent event) {
      if (!(Boolean)this.onlyInPvp.getValue() || this.m_8_boolean()) {
         String text = String.valueOf(event.getSlot() + 1);
         if (this.slots.isSelected(text)) {
            ChatUtil.log("§cПопытка выброса из слота " + (event.getSlot() + 1) + " заблокирована");
            event.cancel();
         }
      }
   }

   private boolean m_8_boolean() {
      if (MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.hud != null) {
         try {
            Map map = ((BossHealthOverlayAccessor)MinecraftAccess.MINECRAFT.gui.hud.getBossOverlay()).onetap$getEvents();

            for (LerpingBossEvent event : (Iterable<LerpingBossEvent>)map.values()) {
               String text = event.getName().getString().toLowerCase(Locale.ROOT);
               if (text.contains("pvp") || text.contains("пвп") || text.contains("дуэль")) {
                  return true;
               }
            }
         } catch (Exception e) {
         }

         return false;
      } else {
         return false;
      }
   }
}
