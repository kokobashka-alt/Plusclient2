package wtf.onetap;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.onetap.annotation.EraseAfterRun;
import wtf.onetap.autobuy.ItemUtil;
import wtf.onetap.autobuy.PurchaseHistoryOverlay;
import wtf.onetap.bot.BotConnection;
import wtf.onetap.bot.BotManager;
import wtf.onetap.combat.rotation.RotationModes;
import wtf.onetap.command.CommandManager;
import wtf.onetap.config.ConfigManager;
import wtf.onetap.config.FriendManager;
import wtf.onetap.config.MacroManager;
import wtf.onetap.config.ProxyConfig;
import wtf.onetap.config.StaffManager;
import wtf.onetap.config.WarpManager;
import wtf.onetap.discord.DiscordPresence;
import wtf.onetap.event.EventBus;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.gui.ClickGuiScreen;
import wtf.onetap.gui.MascotManager;
import wtf.onetap.module.ModuleManager;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.network.PingHandler;
import wtf.onetap.network.ServerInfoListener;
import wtf.onetap.platform.PlatformConnection;
import wtf.onetap.platform.VmBinding;
import wtf.onetap.render.AtmosphereRenderer;
import wtf.onetap.render.GlowEspRenderer;
import wtf.onetap.render.HandGlowRenderer;
import wtf.onetap.render.SaturationRenderer;
import wtf.onetap.render.TargetArrow;
import wtf.onetap.render.ThemeManager;
import wtf.onetap.util.C6_f;

public class Onetap implements ClientModInitializer {
   public static final String MOD_ID = "onetap";
   public static final Logger LOGGER = LoggerFactory.getLogger("onetap");
   public static final EventBus EVENT_BUS = new EventBus();
   public static final ModuleManager MODULE_MANAGER = new ModuleManager();
   public static final CommandManager COMMAND_MANAGER = new CommandManager();
   public static final BotManager BOT_MANAGER = BotManager.BOT_MANAGER;
   public static final ConfigManager CONFIG_MANAGER = new ConfigManager();
   public static final FriendManager FRIEND_MANAGER = new FriendManager();
   public static final StaffManager STAFF_MANAGER = new StaffManager();
   public static final MacroManager MACRO_MANAGER = new MacroManager();
   public static final ThemeManager THEME_MANAGER = new ThemeManager();
   public static final MascotManager MASCOT_MANAGER = new MascotManager();
   public static final RotationModes ROTATION_MODES = new RotationModes();
   private static int value;

   @EraseAfterRun
   public void onInitializeClient() {
      LOGGER.info("[Onetap] initializing core (layer 0)...");
      MODULE_MANAGER.registerAll();
      VmBinding.m_6_void();
      BOT_MANAGER.m_2(BotConnection.BOT_CONNECTION);
      COMMAND_MANAGER.registerCommands();
      ItemUtil.m_5_IArr();
      PlatformConnection.PLATFORM_CONNECTION.m_2_void();
      EVENT_BUS.register(new ChatInputHelper());
      EVENT_BUS.register(ROTATION_MODES);
      EVENT_BUS.register(new ServerInfoListener());
      EVENT_BUS.register(new PingHandler());
      EVENT_BUS.register(new TargetArrow());
      EVENT_BUS.register(new WarpManager());
      EVENT_BUS.register(new C6_f());
      EVENT_BUS.register(PurchaseHistoryOverlay.m_7_C5_v());
      FRIEND_MANAGER.m_9_void();
      STAFF_MANAGER.m_6_void();
      MACRO_MANAGER.m_5_void();
      THEME_MANAGER.m_3_void();
      MASCOT_MANAGER.m_4_void();
      DiscordPresence.startUserLookup();

      try {
         CONFIG_MANAGER.loadConfig("autocfg");
      } catch (Exception e) {
      }

      try {
         ProxyConfig.m_9_void();
      } catch (Exception e2) {
      }

      GlowEspRenderer.m_4_void();
      HandGlowRenderer.m_3_void();
      AtmosphereRenderer.m_9_void();
      SaturationRenderer.m_5_void();
      ClickGuiScreen.createInstance();
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         try {
            ClickGUI.disable();
         } catch (Exception e3) {
         }

         DiscordPresence.m_1_void();
      }));
      LOGGER.info("[Onetap] core ready: {} modules, {} commands", MODULE_MANAGER.getModules().size(), COMMAND_MANAGER.getStringMap().size());
   }

   static {
      m_8_void(18);
   }

   public static void m_8_void(int value2) {
      value = value2;
   }

   public static int m_6_int() {
      return value;
   }

   public static int m_3_int() {
      return 0;
   }
}
