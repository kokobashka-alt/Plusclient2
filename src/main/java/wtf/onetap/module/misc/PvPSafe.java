package wtf.onetap.module.misc;

import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.mixin.render.BossHealthOverlayAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.render.Notifications;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "PvP Safe", description = "Блокирует команды в PvP", category = Category.MISC)
public class PvPSafe extends Module {
   private final BooleanSetting blockHub = new BooleanSetting("Блокировать хаб", true).build();
   private final BooleanSetting blockAnarchy = new BooleanSetting("Блокировать анархию", true).build();
   private static final Pattern PATTERN = Pattern.compile("^(hub|lobby|leave|quit|exit)(\\s.*)?$", 2);
   private static final Pattern PATTERN2 = Pattern.compile("^an(archy)?(\\s*\\d+)(\\s.*)?$", 2);
   private boolean flag;

   public boolean m_9_boolean() {
      return this.isEnabled() && this.m_1_boolean() && !this.flag;
   }

   public void openWarningScreen() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gui != null) {
         MinecraftAccess.MINECRAFT.gui.setScreen(new PvPSafe.StateHolder());
      }
   }

   public void disconnect() {
      this.flag = true;
      MinecraftAccess.MINECRAFT.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE);
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (this.isEnabled()) {
         if (event.getPacket() instanceof ServerboundChatCommandPacket serverboundChatCommandPacket) {
            String text = serverboundChatCommandPacket.command().trim();
            if (this.m_8_boolean(text) && this.m_1_boolean()) {
               event.cancel();
               Notifications notifications = Onetap.MODULE_MANAGER.get(Notifications.class);
               if (notifications != null) {
                  notifications.m_0(Component.literal("Команда /" + text.split("\\s+")[0] + " заблокирована в PvP"), null);
               }
            }
         }
      }
   }

   private boolean m_8_boolean(String text) {
      return this.blockHub.getValue() && PATTERN.matcher(text).matches() ? true : (Boolean)this.blockAnarchy.getValue() && PATTERN2.matcher(text).matches();
   }

   private boolean m_1_boolean() {
      if (MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.hud != null) {
         Map map = ((BossHealthOverlayAccessor)MinecraftAccess.MINECRAFT.gui.hud.getBossOverlay()).onetap$getEvents();

         for (LerpingBossEvent event : (Iterable<LerpingBossEvent>)map.values()) {
            String text = event.getName().getString().toLowerCase();
            if (text.contains("pvp") || text.contains("пвп")) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public void onDisable() {
      this.flag = false;
   }

   private final class StateHolder extends Screen {
      private StateHolder() {
         super(Component.literal("Подтверждение"));
      }

      @Override
      public void init() {
         int value = super.width / 2;
         UnHook.m_7_IArr();
         int value2 = super.height / 2;
         this.addRenderableWidget(Button.builder(Component.literal("Да"), button -> PvPSafe.this.disconnect()).bounds(value - 110, value2 + 10, 100, 20).build());
         this.addRenderableWidget(Button.builder(Component.literal("Нет"), button2 -> MinecraftAccess.MINECRAFT.gui.setScreen(null)).bounds(value + 10, value2 + 10, 100, 20).build());
      }

      @Override
      public void extractBackground(GuiGraphicsExtractor guiGraphicsExtractor, int value, int value2, float floatValue) {
         guiGraphicsExtractor.fill(0, 0, super.width, super.height, (int)-7953703186092519404L);
      }

      @Override
      public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int value, int value2, float floatValue) {
         super.extractRenderState(guiGraphicsExtractor, value, value2, floatValue);
         Font font2 = this.getFont();
         String text = "Отключиться от сервера во время PvP?";
         int value3 = font2.width(text);
         guiGraphicsExtractor.text(font2, text, super.width / 2 - value3 / 2, super.height / 2 - 20, -1);
      }
   }
}
