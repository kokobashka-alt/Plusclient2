package wtf.onetap.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.datafixers.util.Pair;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ServerData.ServerPackStatus;
import net.minecraft.client.multiplayer.ServerData.State;
import net.minecraft.client.multiplayer.ServerData.Type;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.minecraft.util.Util;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource.LevelCandidates;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.validation.ContentValidationException;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.compat.ViaFabricPlusCompat;
import wtf.onetap.config.ProxyState;
import wtf.onetap.mixin.client.MinecraftAccessor;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.render.BlurRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.TextRenderer;
import wtf.onetap.util.ChatBadges;
import wtf.onetap.util.KeyNames;
import wtf.onetap.util.NickGenerator;

public class TitleScreenRenderer {
   private static final Identifier[] IDENTIFIER_ARRAY = new Identifier[]{
      Identifier.fromNamespaceAndPath("onetap", "textures/gui/main_menu_bg.png"),
      Identifier.fromNamespaceAndPath("onetap", "textures/gui/main_menu_bg_1.png"),
      Identifier.fromNamespaceAndPath("onetap", "textures/gui/main_menu_bg_2.png"),
      Identifier.fromNamespaceAndPath("onetap", "textures/gui/main_menu_bg_3.png")
   };
   private static final Path PATH = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("menu_bg.txt");
   private static final Path PATH2 = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("accounts.json");
   private static final Path PATH3 = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("servers_starred.json");
   private static final Path PATH4 = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("nick.txt");
   private static final Pattern PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,16}$");
   private static final Pattern PATTERN2 = Pattern.compile("^Player\\d+$");
   private static final Path PATH5 = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("menu_blur.txt");
   private static final String[] TEXT_ARRAY = new String[]{"\uf57d", "\uf0ac", "\uf2bd", "\uf013", "\uf011"};
   private static final String[] TEXT_ARRAY2 = new String[]{"Одиночная игра", "Сетевая игра", "Аккаунт", "Настройки", "Выход"};
   private final Animation[] animationArray;
   private final Animation animation;
   private final Animation animation2;
   private final Animation animation3;
   private final Animation animation4;
   private final Animation animation5;
   private final Animation animation6;
   private final ServerListEntry serverListEntry;
   private final Animation animation7;
   private final Animation animation8;
   private final ServerEntry serverEntry;
   private final MainMenu mainMenu;
   private final Animation animation9;
   private final Animation animation10;
   private final ServerEditScreen serverEditScreen;
   private final ChangelogPanel changelogPanel;
   private final Animation animation11;
   private final Animation animation12;
   private final Animation animation13;
   private final Animation animation14;
   private int value;
   private float floatValue;
   private final Animation animation15;
   private boolean flag;
   private int value2;
   private final Map<String, TitleScreenRenderer.StateHolder> stringMap;
   private final EnumMap<MainMenu.ModeKind, Animation> enumMap;
   private final Set<String> stringSet;
   private ServerList serverList;
   private ServerData serverData;
   private List<LevelSummary> levelSummaryList;
   private ServerStatusPinger serverStatusPinger;
   private final Map<String, FaviconTexture> stringMap2;
   private final Map<String, byte[]> stringMap3;
   public static final TitleScreenRenderer TITLE_SCREEN_RENDERER = new TitleScreenRenderer();
   private TitleScreen titleScreen;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private final List<TitleScreenRenderer.StateHolder2> c4List;
   private final Animation animation16;
   private float floatValue2;
   private float floatValue3;
   private boolean flag5;
   private int value3;
   private String text;
   private String text2;
   private String text3;
   private String text4;
   private long longValue;
   private String text5;
   private static String[] textArray;

   private TitleScreenRenderer() {
      this.animationArray = new Animation[]{
         new Animation(140L, 0.0F, Easing.EASING8),
         new Animation(140L, 0.0F, Easing.EASING8),
         new Animation(140L, 0.0F, Easing.EASING8),
         new Animation(140L, 0.0F, Easing.EASING8),
         new Animation(140L, 0.0F, Easing.EASING8)
      };
      this.animation = new Animation(220L, 0.0F, Easing.EASING8);
      this.animation2 = new Animation(140L, 0.0F, Easing.EASING8);
      this.animation3 = new Animation(140L, 0.0F, Easing.EASING8);
      this.animation4 = new Animation(140L, 0.0F, Easing.EASING8);
      this.animation5 = new Animation(220L, 0.0F, Easing.EASING8);
      this.animation6 = new Animation(200L, 1.0F, Easing.EASING8);
      this.serverListEntry = new ServerListEntry(16, "Ник");
      this.animation7 = new Animation(200L, 0.0F, Easing.EASING8);
      this.animation8 = new Animation(180L, 0.0F, Easing.EASING8);
      this.serverEntry = new ServerEntry();
      this.mainMenu = new MainMenu();
      this.animation9 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation10 = new Animation(180L, 0.0F, Easing.C02);
      this.serverEditScreen = new ServerEditScreen();
      this.changelogPanel = new ChangelogPanel();
      this.animation11 = new Animation(650L, 1.0F, Easing.EASING8);
      this.animation12 = new Animation(140L, 0.0F, Easing.EASING8);
      this.animation13 = new Animation(500L, 0.0F, Easing.EASING8);
      this.animation14 = new Animation(140L, 0.0F, Easing.EASING8);
      this.value = (int)Math.clamp(readFloat(PATH, 0.0F), 0.0F, IDENTIFIER_ARRAY.length - 1);
      this.floatValue = Math.clamp(readFloat(PATH5, 4.0F), 0.0F, 5.0F);
      this.animation15 = new Animation(260L, this.floatValue, Easing.EASING8);
      this.value2 = this.value;
      this.stringMap = new HashMap<>();
      this.enumMap = new EnumMap<>(MainMenu.ModeKind.class);
      this.stringSet = new HashSet<>();
      this.levelSummaryList = List.of();
      this.stringMap2 = new HashMap<>();
      this.stringMap3 = new HashMap<>();
      this.c4List = new ArrayList<>();
      this.animation16 = new Animation(180L, 0.0F, Easing.EASING8);
      this.text = "";
      this.text2 = "";
      this.text3 = "";
      this.text4 = "";
   }

   public void m_1_b_void() {
      this.flag2 = false;
      this.m_1(ServerEntry.ModeKind.SERVERS);
      this.serverEntry.show();
   }

   public void m_7(TitleScreen titleScreen2) {
      this.setTitleScreen(titleScreen2);
      this.loadSettings();
      if (!this.flag4) {
         this.flag4 = true;
         if (PATTERN.matcher(this.serverListEntry.getText2()).matches()
            && !matchesPattern(this.serverListEntry.getText2())
            && !this.serverListEntry.getText2().equals(Minecraft.getInstance().getUser().getName())) {
            this.m_4(true);
         }

         this.serverListEntry.clear();
      }

      if (this.serverEntry.getC7() != ServerEntry.ModeKind.NONE && this.serverEntry.isFlag()) {
         this.mainMenu.setFlag2(this.serverEntry.getC7() == ServerEntry.ModeKind.SERVERS && ViaFabricPlusCompat.isAvailable());
         this.m_5_void();
         this.serverEntry.show();
      }
   }

   public void m_9(TitleScreen titleScreen2, GuiGraphicsExtractor guiGraphicsExtractor, int value4, int value5) {
      this.setTitleScreen(titleScreen2);
      Render2D.setTransform(1.0F, 0.0F, 0.0F);
      this.m_1_void();
      float floatValue4 = this.animation.update(this.flag2 ? 1.0F : 0.0F);
      float floatValue5 = this.serverEntry.m_1_float();
      float floatValue6 = this.serverEditScreen.m_9_float();
      if (!this.serverEntry.isOpen() || this.flag2 || this.serverEditScreen.isVisible()) {
         this.mainMenu.m_3_void();
      } else if (KeyNames.isLeftMouseDown() && this.mainMenu.isFlag()) {
         this.mainMenu.m_7_boolean(value4, value5, this.titleScreen.width, this.titleScreen.height);
      } else if (!KeyNames.isLeftMouseDown()) {
         this.mainMenu.m_3_void();
      }

      if (this.serverStatusPinger != null) {
         this.serverStatusPinger.tick();
      }

      float floatValue7 = this.titleScreen.width / 2.0F;
      float floatValue8 = this.titleScreen.height / 2.0F;
      if (floatValue5 < 0.99F) {
         float floatValue9 = 1.0F - floatValue5;
         float floatValue10 = 1.0F - 0.08F * floatValue5;
         Render2D.setTransform(floatValue10, floatValue7 - floatValue7 * floatValue10, floatValue8 - floatValue8 * floatValue10);

         try {
            this.m_9(value4, value5, floatValue9);
         } finally {
            Render2D.setTransform(1.0F, 0.0F, 0.0F);
         }

         this.m_6(value4, value5, floatValue9);
         this.changelogPanel.m_1(value4, value5, this.titleScreen.width, this.m_4m_float(), floatValue9, this.m_7_boolean());
      }

      if (floatValue5 > 0.01F) {
         this.mainMenu.m_2_float();
         boolean flag6 = this.serverEditScreen.isVisible() || this.flag2;
         this.m_8(flag6 ? -1000 : value4, flag6 ? -1000 : value5, floatValue5);
      } else if (!this.serverEntry.isVisible()) {
         this.stopPingerAndClearFavicons();
      }

      if (floatValue6 > 0.01F) {
         this.m_8_void();
         Draw2D.roundedRect(0.0F, 0.0F, this.titleScreen.width, this.titleScreen.height, 0.0F, ColorUtil.rgba(0, 0, 0, (int)(140.0F * floatValue6)));
         float floatValue11 = 0.92F + 0.08F * floatValue6;
         Render2D.setTransform(floatValue11, floatValue7 - floatValue7 * floatValue11, floatValue8 - floatValue8 * floatValue11);

         try {
            this.serverEditScreen.m_3(this.titleScreen.width, this.titleScreen.height, value4, value5, floatValue6);
         } finally {
            Render2D.setTransform(1.0F, 0.0F, 0.0F);
         }
      }

      this.changelogPanel.m_2(value4, value5, this.titleScreen.width, this.titleScreen.height);
      if (floatValue4 > 0.01F) {
         this.m_4(value4, value5, floatValue4);
      }

      this.m_0_void();
   }

   public boolean m_8(TitleScreen titleScreen2, MouseButtonEvent event, boolean flag6) {
      this.setTitleScreen(titleScreen2);
      float floatValue4 = (float)event.x();
      float floatValue5 = (float)event.y();
      float floatValue6 = this.animation.get();
      if (this.flag2 || floatValue6 > 0.05F) {
         float floatValue7 = this.m_4_z_float();
         float floatValue8 = this.m_4_w_float();
         if (!isHovered(floatValue4, floatValue5, floatValue7, floatValue8, 280.0F, this.m_4_o_float())) {
            if (event.button() == 0) {
               this.m_1_q_void();
            }

            return true;
         } else if (event.button() == 1) {
            int value4 = this.m_2_int(floatValue5);
            if (value4 >= 0) {
               this.c4List.get(value4).flag2 = true;
               this.saveSettings();
            }

            return true;
         } else {
            if (event.button() != 0) {
               return true;
            }

            if (isHovered(floatValue4, floatValue5, floatValue7 + 280.0F - 34.0F, floatValue8 + 6.0F, 26.0F, 26.0F)) {
               this.m_1_q_void();
               return true;
            }

            if (isHovered(floatValue4, floatValue5, this.m_49_float(), this.m_4n_float(), 24.0F, 24.0F)) {
               this.m_3_void(NickGenerator.m_8(TITLE_SCREEN_RENDERER::hasToast));
               this.flag3 = false;
               return true;
            }

            if (isHovered(floatValue4, floatValue5, this.m_46_float(), this.m_4n_float(), 78.0F, 24.0F)) {
               this.flag3 = false;
               this.m_4(true);
               this.m_1_r_void();
               return true;
            }

            if (isHovered(floatValue4, floatValue5, this.m_4h(), this.m_4n_float(), this.m_7_float(), 24.0F)) {
               this.flag3 = true;
               this.serverListEntry.setFocused(true);
               this.serverListEntry.m_5_void(floatValue4, event.hasShiftDown());
               return true;
            }

            if (this.m_43() > 0.0F && isHovered(floatValue4, floatValue5, this.m_5_float() - 3.0F, this.m_9_float(), 9.0F, this.m_4r())) {
               this.flag5 = true;
               this.floatValue3 = floatValue5 >= this.m_4_float() && floatValue5 <= this.m_4_float() + this.m_4_k() ? floatValue5 - this.m_4_float() : this.m_4_k() / 2.0F;
               this.m_8_void(floatValue5);
               return true;
            }

            int value5 = this.m_2_int(floatValue5);
            if (value5 >= 0) {
               TitleScreenRenderer.StateHolder2 c4 = this.c4List.get(value5);
               float floatValue9 = 22.0F * Math.max(0.5F, c4.animation.getValue());
               if (isHovered(floatValue4, floatValue5, floatValue7 + 8.0F, this.m_8_float(value5), 22.0F, floatValue9)) {
                  c4.flag3 = !c4.flag3;
                  this.saveSettings();
                  return true;
               } else if (isHovered(floatValue4, floatValue5, floatValue7 + 280.0F - 54.0F, this.m_8_float(value5), 22.0F, floatValue9)) {
                  c4.flag2 = true;
                  this.saveSettings();
                  return true;
               } else {
                  this.m_3_void(c4.text);
                  this.m_4(true);
                  this.m_1_r_void();
                  return true;
               }
            } else {
               return true;
            }
         }
      } else {
         if (event.button() != 0) {
            return true;
         }

         if (this.serverEditScreen.isVisible()) {
            if (!this.serverEditScreen.isOpen()) {
               return true;
            }

            this.serverEditScreen.m_5(floatValue4, floatValue5, this.titleScreen.width, this.titleScreen.height, event.hasShiftDown());
            if (this.serverEditScreen.m_9_boolean()) {
               this.m_1e_void();
            }

            if (this.serverEditScreen.m_0_f_boolean()) {
               this.m_1l_void();
            }

            this.serverEditScreen.m_1_boolean();
            return true;
         } else if (!this.serverEntry.isVisible()) {
            if (this.changelogPanel.m_9_boolean(floatValue4, floatValue5, this.titleScreen.width, this.titleScreen.height, this.m_4m_float())) {
               return true;
            }

            if (event.button() == 0 && this.m_9_boolean(floatValue4, floatValue5)) {
               return true;
            }

            int value6 = this.m_3_int(event.x(), event.y());
            if (value6 < 0) {
               return false;
            }

            switch (value6) {
               case 0:
                  this.m_1(ServerEntry.ModeKind.WORLDS);
                  break;
               case 1:
                  if (!getMinecraft().allowsMultiplayer()) {
                     return true;
                  }

                  this.m_1(ServerEntry.ModeKind.SERVERS);
                  break;
               case 2:
                  this.reset();
                  break;
               case 3:
                  getMinecraft().gui.setScreen(new OptionsScreen(titleScreen2, getMinecraft().options, false));
                  break;
               case 4:
                  getMinecraft().stop();
            }

            return true;
         } else if (!this.serverEntry.isOpen()) {
            return true;
         } else {
            this.mainMenu.m_6_C6((float)event.x(), (float)event.y(), this.titleScreen.width, this.titleScreen.height);
            if (this.m_5(event, flag6)) {
               this.m_16_void();
               return true;
            } else {
               this.m_1(this.mainMenu.m_2_C9());
               return true;
            }
         }
      }
   }

   public boolean m_0_boolean() {
      return this.flag2 ? this.flag3 : this.serverEditScreen.isVisible();
   }

   public boolean m_8(CharacterEvent event) {
      if (this.serverEditScreen.isOpen()) {
         if (!event.isAllowedChatCharacter()) {
            return true;
         }

         this.serverEditScreen.m_7_boolean(event.codepoint());
         return true;
      } else {
         if (!this.flag2 || !this.flag3) {
            return false;
         }

         if (!event.isAllowedChatCharacter()) {
            return true;
         }

         this.serverListEntry.insert(String.valueOf((char)event.codepoint()), TitleScreenRenderer::isAllowedChar);
         return true;
      }
   }

   public boolean m_6(KeyEvent event) {
      if (this.serverEditScreen.isVisible() && !this.flag2) {
         if (!this.serverEditScreen.isOpen()) {
            return true;
         }

         this.serverEditScreen.m_7(event, text6 -> getMinecraft().keyboardHandler.setClipboard(text6), () -> getMinecraft().keyboardHandler.getClipboard());
         if (this.serverEditScreen.m_9_boolean()) {
            this.m_1e_void();
         }

         this.serverEditScreen.m_1_boolean();
         return true;
      } else if (this.serverEntry.isVisible() && !this.flag2) {
         if (event.key() == 256) {
            this.serverEntry.hide();
            return true;
         }

         if (event.key() == 257 || event.key() == 335) {
            this.m_16_void();
            return true;
         }

         if (event.key() == 294 && this.serverEntry.getC7() == ServerEntry.ModeKind.SERVERS) {
            this.m_1_n_void();
            return true;
         }

         if (this.serverEntry.getC7() == ServerEntry.ModeKind.SERVERS && event.hasShiftDown()) {
            if (event.isUp()) {
               this.m_1_void(-1);
               return true;
            }

            if (event.isDown()) {
               this.m_1_void(1);
               return true;
            }
         }

         return true;
      } else {
         if (!this.flag2) {
            return event.key() == 256 && this.changelogPanel.consumeFlag();
         }

         if (event.key() == 256) {
            this.m_1_q_void();
            return true;
         }

         if (this.flag3) {
            if (event.isSelectAll()) {
               this.serverListEntry.selectAll();
               return true;
            }

            if (event.isCopy()) {
               getMinecraft().keyboardHandler.setClipboard(this.serverListEntry.getSelection());
               return true;
            }

            if (event.isPaste()) {
               this.serverListEntry.paste(getMinecraft().keyboardHandler.getClipboard(), TitleScreenRenderer::isAllowedChar);
               return true;
            }

            if (event.isCut()) {
               getMinecraft().keyboardHandler.setClipboard(this.serverListEntry.getSelection());
               this.serverListEntry.deleteOrClear();
               return true;
            }

            if (this.serverListEntry.onKey(event)) {
               return true;
            }

            if (event.key() == 257 || event.key() == 335) {
               this.flag3 = false;
               this.m_4(true);
               this.m_1_r_void();
               return true;
            }
         }

         return true;
      }
   }

   public boolean m_9_boolean(double doubleValue, double doubleValue2, double doubleValue3) {
      if (this.flag2) {
         this.floatValue2 -= (float)doubleValue3 * 22.0F * 2.0F;
         this.m_1_y_void();
         return true;
      } else if (this.changelogPanel.m_5_boolean(doubleValue3, this.titleScreen.height)) {
         return true;
      } else if (this.m_6((float)doubleValue, (float)doubleValue2, doubleValue3)) {
         return true;
      } else {
         return this.serverEntry.isOpen() && !this.flag2 && !this.serverEditScreen.isVisible()
            ? this.mainMenu.m_4(doubleValue3, (float)doubleValue, (float)doubleValue2, this.titleScreen.width, this.titleScreen.height)
            : false;
      }
   }

   private void m_9(int value4, int value5, float floatValue4) {
      float floatValue5 = this.m_0_float();
      float floatValue6 = this.m_4i_float();
      float floatValue7 = this.m_4_c_float();
      Draw2D.drawPanel(floatValue5, floatValue6, 168.0F, 32.0F, floatValue4);
      this.m_7_void(floatValue5, floatValue6, floatValue4);
      Draw2D.drawPanel(floatValue5, floatValue7, 168.0F, this.m_4p_float(), floatValue4);
      float floatValue8 = this.animation.getValue();
      int value6 = !(floatValue8 > 0.2F) && !this.serverEntry.isVisible() && !this.changelogPanel.isVisible() ? this.m_3_int(value4, value5) : -1;
      float floatValue9 = floatValue7 + 4.0F;

      for (int i = 0; i < TEXT_ARRAY2.length; i++) {
         this.m_7(floatValue5, floatValue9 + i * 25.0F, i, this.animationArray[i].update(i == value6 ? 1.0F : 0.0F), floatValue4);
      }
   }

   private void m_8(int value4, int value5, float floatValue4) {
      MainMenu.DataRecord c1 = this.mainMenu.m_8_C1(this.titleScreen.width, this.titleScreen.height);
      Draw2D.drawPanel(c1.m_08(), c1.m_0s(), c1.m_0l(), c1.m_0k(), floatValue4);
      this.m_2(c1, floatValue4);
      Draw2D.drawPanel(c1.m_0_c(), c1.m_0p(), c1.m_04(), c1.m_0m(), floatValue4);
      this.mainMenu.syncAnimations();
      MainMenu.DataRecord4 c6 = this.mainMenu.m_7_C6(value4, value5, this.titleScreen.width, this.titleScreen.height);
      var c2List = this.mainMenu.getEntries();
      if (c2List.isEmpty()) {
         String text6 = this.mainMenu.getEmptyText();
         float floatValue5 = Draw2D.getTextWidth(text6, Fonts.SUPPLIER13, 13.0F);
         Draw2D.drawStyledText(
            text6,
            Fonts.SUPPLIER13,
            c1.m_0_c() + (c1.m_04() - floatValue5) / 2.0F,
            c1.m_0p() + (c1.m_0m() - 13.0F) / 2.0F,
            ColorUtil.rgba(200, 200, 200, (int)(140.0F * floatValue4)),
            13.0F
         );
      } else {
         Render2D.pushScissor(c1.m_0_c() + 8.0F, c1.m_0p() + 8.0F, c1.m_04() - 16.0F, c1.m_0m() - 16.0F);

         try {
            float floatValue6 = c1.m_0p() + 8.0F - this.mainMenu.getAnimation();

            for (int i = 0; i < c2List.size(); i++) {
               float floatValue7 = floatValue6 + this.mainMenu.getEntryY(i);
               if (!(floatValue7 + 36.0F < c1.m_0p()) && !(floatValue7 > c1.m_0p() + c1.m_0m())) {
                  MainMenu.DataRecord2 c2 = (MainMenu.DataRecord2)c2List.get(i);
                  boolean flag6 = c6.m_4_s() == i;
                  boolean flag7 = flag6 && c6.m_6p_C9() == MainMenu.ModeKind.PLAY;
                  boolean flag8 = flag6
                     && (
                        c6.m_6p_C9() == MainMenu.ModeKind.ROW
                           || c6.m_6p_C9() == MainMenu.ModeKind.PLAY
                           || c6.m_6p_C9() == MainMenu.ModeKind.STAR
                           || c6.m_6p_C9() == MainMenu.ModeKind.MOVE_UP
                           || c6.m_6p_C9() == MainMenu.ModeKind.MOVE_DOWN
                     );
                  boolean flag9 = this.mainMenu.getValue() == i;
                  TitleScreenRenderer.StateHolder c12 = this.m_3(c2);
                  c12.animation.update(flag8 ? 1.0F : 0.0F);
                  c12.animation2.update(flag9 ? 1.0F : 0.0F);
                  c12.animation3.update(flag7 && c2.m_4_j() ? 1.0F : 0.0F);
                  c12.animation4.update(flag6 && c6.m_6p_C9() == MainMenu.ModeKind.STAR ? 1.0F : 0.0F);
                  c12.animation5.update(!c2.m_46_boolean() && !flag8 ? 0.0F : 1.0F);
                  c12.animation6.update(c2.m_46_boolean() ? 1.0F : 0.0F);
                  c12.animation7.update(flag6 && c6.m_6p_C9() == MainMenu.ModeKind.MOVE_UP ? 1.0F : 0.0F);
                  c12.animation8.update(flag6 && c6.m_6p_C9() == MainMenu.ModeKind.MOVE_DOWN ? 1.0F : 0.0F);
                  c12.animation9.update(flag8 && this.mainMenu.getC7() == ServerEntry.ModeKind.SERVERS ? 1.0F : 0.0F);
                  this.m_1(c1, floatValue7, c2, i, c12, floatValue4);
               }
            }
         } finally {
            Render2D.popScissor();
         }
      }

      if (c1.m_7k()) {
         boolean flag10 = c6.m_6p_C9() == MainMenu.ModeKind.SCROLLBAR && value5 >= c1.m_0a() && value5 <= c1.m_0a() + c1.m_0_z_float();
         float floatValue8 = this.animation9.update(!flag10 && !this.mainMenu.isFlag() ? 0.0F : 1.0F);
         float floatValue9 = this.animation10.update(this.mainMenu.isFlag() ? 1.0F : 0.0F);
         Draw2D.m_7(c1.m_0r(), c1.m_0_j(), c1.m_01(), c1.m_0_d(), c1.m_0a(), c1.m_0_z_float(), floatValue8, floatValue9, floatValue4);
      }

      float floatValue10 = 24.0F;

      for (MainMenu.DataRecord3 c3 : c1.m_6i_List()) {
         floatValue10 = Math.max(floatValue10, c3.m_52() + c3.m_5_e() - c1.m_0_t());
      }

      Draw2D.drawPanel(c1.m_0_c(), c1.m_0_t() - 4.0F, c1.m_04(), floatValue10 + 8.0F, floatValue4);

      for (MainMenu.DataRecord3 c32 : c1.m_6i_List()) {
         boolean flag11 = c6.m_6p_C9() == c32.m_88() && c6.m_4_s() < 0;
         Draw2D.m_3(
            c32.m_5_w(),
            c32.m_52(),
            c32.m_5g(),
            24.0F,
            this.getButtonIcon(c32.m_88()),
            this.getButtonLabel(c32.m_88()),
            this.m_6(c32.m_88()).update(flag11 ? 1.0F : 0.0F),
            floatValue4
         );
      }
   }

   private void m_2(MainMenu.DataRecord c1, float floatValue4) {
      float floatValue5 = 26.0F;
      float floatValue6 = c1.m_08() + 8.0F;
      float floatValue7 = c1.m_0s() + (c1.m_0k() - floatValue5) / 2.0F;
      Draw2D.m_8(
         floatValue6 + 0.5F,
         floatValue7 + 1.0F,
         floatValue5 - 1.0F,
         floatValue5 - 2.0F,
         6.0F,
         0.5F,
         ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(255.0F * floatValue4)),
         ColorUtil.withAlpha(ColorUtil.secondaryColor(), (int)(255.0F * floatValue4))
      );
      float floatValue8 = 30.0F;
      float floatValue9 = Draw2D.getCenteredY("\ue900", Fonts.SUPPLIER6, floatValue8, floatValue7, floatValue5);
      float floatValue10 = Draw2D.getCenteredX("\ue900", Fonts.SUPPLIER6, floatValue8, floatValue6, floatValue5);
      Draw2D.drawTextWithShadow("\ue900", Fonts.SUPPLIER6, floatValue10, floatValue9, ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue4)), floatValue8);
      String text6 = this.mainMenu.getC7() == ServerEntry.ModeKind.SERVERS ? "\uf0ac" : "\uf57d";
      float floatValue11 = floatValue6 + floatValue5 + 10.0F;
      float floatValue12 = Draw2D.getTextBaseline(Fonts.SUPPLIER18, 16.0F, c1.m_0s(), c1.m_0k());
      float floatValue13 = (float)(System.currentTimeMillis() % 4000L) / 4000.0F;
      Draw2D.m_9(
         this.mainMenu.getTitle(),
         Fonts.SUPPLIER18,
         floatValue11,
         floatValue12,
         ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(255.0F * floatValue4)),
         ColorUtil.withAlpha(ColorUtil.secondaryColor(), (int)(255.0F * floatValue4)),
         16.0F,
         floatValue13
      );
      float floatValue14 = c1.m_08() + c1.m_0l() - 28.0F;
      float floatValue15 = Draw2D.getCenteredY(text6, Fonts.SUPPLIER8, 14.0F, c1.m_0s(), c1.m_0k());
      Draw2D.drawStyledText(text6, Fonts.SUPPLIER8, floatValue14, floatValue15, ColorUtil.rgba(235, 235, 235, (int)(200.0F * floatValue4)), 14.0F);
   }

   private void m_1(MainMenu.DataRecord c1, float floatValue4, MainMenu.DataRecord2 c2, int value4, TitleScreenRenderer.StateHolder c12, float floatValue5) {
      float floatValue6 = c1.m_0d_float();
      float floatValue7 = c1.m_05();
      float floatValue8 = 36.0F;
      float floatValue9 = c12.animation.getValue();
      float floatValue10 = c12.animation2.getValue();
      float floatValue11 = c12.animation3.getValue();
      if (floatValue10 > 0.01F) {
         Draw2D.roundedRect(floatValue6, floatValue4, floatValue7, floatValue8, 5.0F, ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(48.0F * floatValue10 * floatValue5)));
      }

      if (floatValue9 > 0.01F) {
         Draw2D.roundedRect(floatValue6, floatValue4, floatValue7, floatValue8, 5.0F, ColorUtil.rgba(255, 255, 255, (int)(22.0F * floatValue9 * floatValue5)));
      }

      ServerData serverData2 = null;
      boolean flag6 = this.mainMenu.getC7() == ServerEntry.ModeKind.SERVERS;
      if (flag6) {
         serverData2 = this.m_5(c2);
         if (serverData2 != null) {
            this.startPing(serverData2);
         }
      }

      float floatValue12 = c1.m_0_k_float();
      float floatValue13 = floatValue4 + 2.0F;
      if (flag6) {
         this.m_2(c1, floatValue4, c12, floatValue5);
         this.m_4(c1, floatValue4, value4, c12, floatValue5);
      }

      this.m_4(c2, floatValue12, floatValue13, floatValue5);
      if (floatValue11 > 0.01F) {
         Draw2D.roundedRect(floatValue12, floatValue13, 32.0F, 32.0F, 4.0F, ColorUtil.rgba(0, 0, 0, (int)(150.0F * floatValue11 * floatValue5)));
         float floatValue14 = 11.0F + 3.0F * floatValue11;
         Draw2D.drawStyledText(
            "\uf04b",
            Fonts.SUPPLIER8,
            Draw2D.getCenteredX("\uf04b", Fonts.SUPPLIER8, floatValue14, floatValue12, 32.0F),
            Draw2D.getCenteredY("\uf04b", Fonts.SUPPLIER8, floatValue14, floatValue13, 32.0F),
            ColorUtil.rgba(255, 255, 255, (int)(245.0F * floatValue11 * floatValue5)),
            floatValue14
         );
      }

      int value5 = ColorUtil.rgba(230, 230, 230, (int)(195.0F * floatValue5));
      int value6 = ColorUtil.lerpColor(value5, ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue5)), floatValue9);
      float floatValue15 = floatValue12 + 32.0F + 8.0F;
      float floatValue16 = serverData2 == null ? 0.0F : 78.0F;
      float floatValue17 = floatValue7 - (floatValue15 - floatValue6) - floatValue16 - 8.0F;
      String text6 = c2.m_8j();
      String text7 = serverData2 == null ? "" : ViaFabricPlusCompat.m_8(serverData2);
      if (!text7.isEmpty()) {
         text6 = text6 + "  " + text7;
      }

      int value7 = ColorUtil.rgba(180, 180, 180, (int)((120.0F + 50.0F * floatValue9) * floatValue5));
      if (serverData2 != null) {
         Draw2D.drawColoredTextRaw(text6, Fonts.SUPPLIER14, floatValue15, floatValue4 + 2.0F, value6, 0.8F, 1.0F, floatValue17, 12.0F);
         var componentList = m_1(serverData2.motd);
         if (!componentList.isEmpty()) {
            int value8 = (int)((230.0F + 25.0F * floatValue9) * floatValue5);
            Render2D.pushScissor(floatValue15, floatValue4 + 14.0F, floatValue17, 22.0F);

            try {
               for (int i = 0; i < componentList.size(); i++) {
                  TextRenderer.m_6((Component)componentList.get(i), Fonts.SUPPLIER16, Fonts.SUPPLIER17).draw(floatValue15, floatValue4 + 15.0F + i * 10, 10.0F, value8);
               }
            } finally {
               Render2D.popScissor();
            }
         }

         this.m_2(c1, floatValue4, serverData2, floatValue5);
      } else {
         Draw2D.drawColoredTextRaw(text6, Fonts.SUPPLIER14, floatValue15, floatValue4 + 6.0F, value6, 0.8F, 1.0F, floatValue17, 13.0F);
         Draw2D.drawColoredTextRaw(c2.m_8r_String(), Fonts.SUPPLIER13, floatValue15, floatValue4 + 20.0F, value7, 0.8F, 1.0F, floatValue17, 10.0F);
      }
   }

   private void m_2(MainMenu.DataRecord c1, float floatValue4, ServerData serverData2, float floatValue5) {
      String text6 = getPingText(serverData2);
      boolean flag6 = serverData2.state() == State.INITIAL || serverData2.state() == State.PINGING;
      boolean flag7 = serverData2.state() == State.UNREACHABLE;
      int value4 = MainMenu.m_9(serverData2.ping, flag6, flag7);
      if (value4 < 0) {
         value4 = MainMenu.getSpinnerFrame(System.currentTimeMillis());
      }

      float floatValue6 = c1.m_0d_float() + c1.m_05() - 4.0F;
      float floatValue7 = MainMenu.m_0_float();
      float floatValue8 = floatValue6 - floatValue7;
      int value5 = ColorUtil.withAlpha(getPingColor(serverData2), (int)(240.0F * floatValue5));
      int value6 = ColorUtil.rgba(255, 255, 255, (int)(28.0F * floatValue5));

      for (int i = 0; i < 5; i++) {
         float floatValue9 = 4.0F + i * 2.2F;
         float floatValue10 = floatValue8 + i * 3.7F;
         float floatValue11 = floatValue4 + 36.0F - 6.0F - floatValue9;
         Draw2D.roundedRect(floatValue10, floatValue11, 2.2F, floatValue9, 0.8F, i < value4 ? value5 : value6);
      }

      Draw2D.drawStyledText(text6, Fonts.SUPPLIER14, floatValue6 - Draw2D.getTextWidth(text6, Fonts.SUPPLIER14, 11.0F), floatValue4 + 4.0F, ColorUtil.rgba(230, 230, 230, (int)(230.0F * floatValue5)), 11.0F);
      String text7 = getStatusText(serverData2);
      if (!text7.isEmpty()) {
         float floatValue12 = Draw2D.getTextWidth(text7, Fonts.SUPPLIER16, 10.0F);
         float floatValue13 = floatValue8 - 6.0F - 40.0F + (40.0F - floatValue12);
         Draw2D.drawStyledText(text7, Fonts.SUPPLIER16, floatValue13, floatValue4 + 36.0F - 16.0F, ColorUtil.withAlpha(getPingColor(serverData2), (int)(220.0F * floatValue5)), 10.0F);
      }
   }

   private void m_2(MainMenu.DataRecord c1, float floatValue4, TitleScreenRenderer.StateHolder c12, float floatValue5) {
      float floatValue6 = c12.animation5.getValue();
      if (!(floatValue6 < 0.01F)) {
         float floatValue7 = c12.animation4.getValue();
         float floatValue8 = 10.0F + floatValue7 * 1.5F;
         int value4 = ColorUtil.lerpColor(ColorUtil.rgba(210, 210, 210, 255), ColorUtil.rgba(255, 204, 48, 255), c12.animation6.getValue());
         Draw2D.drawStyledText(
            "\uf005",
            Fonts.SUPPLIER8,
            Draw2D.getCenteredX("\uf005", Fonts.SUPPLIER8, floatValue8, c1.m_0_f_float(), 16.0F),
            Draw2D.getCenteredY("\uf005", Fonts.SUPPLIER8, floatValue8, floatValue4, 36.0F),
            ColorUtil.withAlpha(value4, (int)(255.0F * floatValue6 * floatValue5)),
            floatValue8
         );
      }
   }

   private void m_4(MainMenu.DataRecord c1, float floatValue4, int value4, TitleScreenRenderer.StateHolder c12, float floatValue5) {
      float floatValue6 = c12.animation9.getValue();
      if (!(floatValue6 < 0.01F)) {
         float floatValue7 = c1.m_0_f_float();
         float floatValue8 = 16.0F;
         int value5 = ColorUtil.rgba(210, 210, 210, 255);
         int value6 = ColorUtil.rgba(255, 255, 255, 255);
         if (this.mainMenu.m_3_boolean(value4)) {
            float floatValue9 = c12.animation7.getValue();
            float floatValue10 = 8.0F + floatValue9 * 2.0F;
            Draw2D.drawStyledText(
               "\uf077",
               Fonts.SUPPLIER8,
               Draw2D.getCenteredX("\uf077", Fonts.SUPPLIER8, floatValue10, floatValue7, floatValue8),
               Draw2D.getCenteredY("\uf077", Fonts.SUPPLIER8, floatValue10, floatValue4, 12.0F),
               ColorUtil.withAlpha(ColorUtil.lerpColor(value5, value6, floatValue9), (int)(255.0F * floatValue6 * floatValue5)),
               floatValue10
            );
         }

         if (this.mainMenu.m_7_boolean(value4)) {
            float floatValue11 = c12.animation8.getValue();
            float floatValue12 = 8.0F + floatValue11 * 2.0F;
            Draw2D.drawStyledText(
               "\uf078",
               Fonts.SUPPLIER8,
               Draw2D.getCenteredX("\uf078", Fonts.SUPPLIER8, floatValue12, floatValue7, floatValue8),
               Draw2D.getCenteredY("\uf078", Fonts.SUPPLIER8, floatValue12, floatValue4 + 36.0F - 12.0F, 12.0F),
               ColorUtil.withAlpha(ColorUtil.lerpColor(value5, value6, floatValue11), (int)(255.0F * floatValue6 * floatValue5)),
               floatValue12
            );
         }
      }
   }

   private void m_7_void(float floatValue4, float floatValue5, float floatValue6) {
      float floatValue7 = 26.0F;
      float floatValue8 = floatValue4 + 8.0F;
      float floatValue9 = floatValue5 + (32.0F - floatValue7) / 2.0F;
      Draw2D.m_8(
         floatValue8 + 0.5F,
         floatValue9 + 1.0F,
         floatValue7 - 1.0F,
         floatValue7 - 2.0F,
         6.0F,
         0.5F,
         ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(255.0F * floatValue6)),
         ColorUtil.withAlpha(ColorUtil.secondaryColor(), (int)(255.0F * floatValue6))
      );
      float floatValue10 = 30.0F;
      float floatValue11 = Draw2D.getCenteredY("\ue900", Fonts.SUPPLIER6, floatValue10, floatValue9, floatValue7);
      float floatValue12 = Draw2D.getCenteredX("\ue900", Fonts.SUPPLIER6, floatValue10, floatValue8, floatValue7);
      Draw2D.drawTextWithShadow("\ue900", Fonts.SUPPLIER6, floatValue12, floatValue11, ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue6)), floatValue10);
      float floatValue13 = floatValue8 + floatValue7 + 8.0F;
      float floatValue14 = Draw2D.getTextBaseline(Fonts.SUPPLIER18, 16.0F, floatValue5, 32.0F);
      float floatValue15 = (float)(System.currentTimeMillis() % 4000L) / 4000.0F;
      Draw2D.m_9("PlusClient", Fonts.SUPPLIER18, floatValue13, floatValue14, ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(255.0F * floatValue6)), ColorUtil.withAlpha(ColorUtil.secondaryColor(), (int)(255.0F * floatValue6)), 16.0F, floatValue15);
   }

   private void m_7(float floatValue4, float floatValue5, int value4, float floatValue6, float floatValue7) {
      if (floatValue6 > 0.01F) {
         Draw2D.roundedRect(floatValue4 + 4.0F, floatValue5, 160.0F, 24.0F, 5.0F, ColorUtil.rgba(255, 255, 255, (int)(22.0F * floatValue6 * floatValue7)));
         float floatValue8 = 10.0F + 6.0F * floatValue6;
         Draw2D.roundedRect(floatValue4 + 4.0F + 3.0F, floatValue5 + (24.0F - floatValue8) / 2.0F, 2.5F, floatValue8, 1.2F, ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(230.0F * floatValue6 * floatValue7)));
      }

      int value5 = ColorUtil.rgba(230, 230, 230, (int)(195.0F * floatValue7));
      int value6 = ColorUtil.primaryColor();
      int value7 = ColorUtil.lerpColor(value5, ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue7)), floatValue6);
      int value8 = ColorUtil.lerpColor(value5, ColorUtil.withAlpha(value6, (int)(255.0F * floatValue7)), floatValue6);
      float floatValue9 = 3.0F * floatValue6;
      float floatValue10 = Draw2D.getTextBaseline(Fonts.SUPPLIER16, 12.0F, floatValue5, 24.0F);
      float floatValue11 = 0.0F;

      for (String text6 : TEXT_ARRAY2) {
         floatValue11 = Math.max(floatValue11, Draw2D.getTextWidth(text6, Fonts.SUPPLIER16, 12.0F));
      }

      float floatValue12 = m_4_h() + 8.0F + floatValue11;
      float floatValue13 = floatValue4 + (168.0F - floatValue12) / 2.0F + m_4_h() + 8.0F + floatValue9;
      float floatValue14 = Draw2D.getTextWidth(TEXT_ARRAY[value4], Fonts.SUPPLIER8, 15.0F);
      float floatValue15 = floatValue13 - 8.0F - floatValue14;
      float floatValue16 = Draw2D.getCenteredY(TEXT_ARRAY[value4], Fonts.SUPPLIER8, 15.0F, floatValue5, 24.0F);
      Draw2D.drawTextWithShadow(TEXT_ARRAY[value4], Fonts.SUPPLIER8, floatValue15, floatValue16, value8, 15.0F);
      Draw2D.drawTextWithShadow(TEXT_ARRAY2[value4], Fonts.SUPPLIER16, floatValue13, floatValue10, value7, 12.0F);
   }

   private void m_4(int value4, int value5, float floatValue4) {
      Draw2D.roundedRect(0.0F, 0.0F, this.titleScreen.width, this.titleScreen.height, 0.0F, ColorUtil.rgba(0, 0, 0, (int)(170.0F * floatValue4)));
      float floatValue5 = 0.92F + 0.08F * floatValue4;
      ClickGUI.m_3_String();
      float floatValue6 = this.titleScreen.width / 2.0F;
      float floatValue7 = this.titleScreen.height / 2.0F;
      Render2D.setTransform(floatValue5, floatValue6 - floatValue6 * floatValue5, floatValue7 - floatValue7 * floatValue5);

      try {
         this.m_6_void();
         float floatValue8 = this.m_4_z_float();
         float floatValue9 = this.m_4_w_float();
         float floatValue10 = this.m_4_o_float();
         Draw2D.drawPanel(floatValue8, floatValue9, 280.0F, floatValue10, floatValue4);
         String text6 = "ЛКМ - выбор    ПКМ - удаление";
         float floatValue11 = Draw2D.getTextWidth(text6, Fonts.SUPPLIER13, 10.0F);
         Draw2D.drawStyledText(text6, Fonts.SUPPLIER13, floatValue8 + (280.0F - floatValue11) / 2.0F, floatValue9 - 28.0F, ColorUtil.rgba(220, 220, 220, (int)(170.0F * floatValue4)), 10.0F);
         int value6 = ColorUtil.rgba(235, 235, 235, (int)(230.0F * floatValue4));
         Draw2D.drawStyledText("Аккаунты", Fonts.SUPPLIER15, floatValue8 + 16.0F, floatValue9 + 14.0F, value6, 13.0F);
         float floatValue12 = floatValue8 + 280.0F - 34.0F;
         float floatValue13 = floatValue9 + 6.0F;
         float floatValue14 = this.animation4.update(isHovered(value4, value5, floatValue12, floatValue13, 26.0F, 26.0F) ? 1.0F : 0.0F);
         float floatValue15 = 16.0F + floatValue14 * 3.0F;
         float floatValue16 = Draw2D.getTextWidth("\uf00d", Fonts.SUPPLIER8, floatValue15);
         float floatValue17 = floatValue12 + (26.0F - floatValue16) / 2.0F;
         float floatValue18 = Draw2D.getCenteredY("\uf00d", Fonts.SUPPLIER8, floatValue15, floatValue13, 26.0F);
         int value7 = ColorUtil.lerpColor(ColorUtil.rgba(220, 220, 220, (int)(180.0F * floatValue4)), ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue4)), floatValue14);
         Draw2D.drawStyledText("\uf00d", Fonts.SUPPLIER8, floatValue17, floatValue18, value7, floatValue15);
         float floatValue19 = this.m_4n_float();
         float floatValue20 = this.m_4h();
         float floatValue21 = this.m_7_float();
         Draw2D.roundedRect(floatValue20, floatValue19 + 24.0F - 1.0F, floatValue21, 1.0F, 0.0F, ColorUtil.rgba(255, 255, 255, (int)((this.flag3 ? 180 : 70) * floatValue4)));
         this.serverListEntry.setFocused(this.flag3);
         if (this.serverListEntry.isFlag3()) {
            if (KeyNames.isLeftMouseDown()) {
               this.serverListEntry.m_6_void(value4);
            } else {
               this.serverListEntry.m_5_void();
            }
         }

         this.serverListEntry.m_3_void(floatValue20 + 2.0F, floatValue19, floatValue21 - 4.0F, 24.0F, floatValue4);
         if (this.flag5) {
            if (KeyNames.isLeftMouseDown()) {
               this.m_8_void(value5);
            } else {
               this.flag5 = false;
            }
         }

         float floatValue22 = this.m_49_float();
         float floatValue23 = this.animation3.update(isHovered(value4, value5, floatValue22, floatValue19, 24.0F, 24.0F) ? 1.0F : 0.0F);
         if (floatValue23 > 0.01F) {
            Draw2D.roundedRect(floatValue22, floatValue19, 24.0F, 24.0F, 4.0F, ColorUtil.rgba(255, 255, 255, (int)(32.0F * floatValue23 * floatValue4)));
         }

         float floatValue24 = 13.0F + floatValue23 * 1.5F;
         float floatValue25 = Draw2D.getCenteredY("\uf1b2", Fonts.SUPPLIER8, floatValue24, floatValue19, 24.0F);
         float floatValue26 = floatValue22 + (24.0F - Draw2D.getTextWidth("\uf1b2", Fonts.SUPPLIER8, floatValue24)) / 2.0F;
         int value8 = ColorUtil.lerpColor(ColorUtil.rgba(220, 220, 220, (int)(180.0F * floatValue4)), ColorUtil.primaryColor(), floatValue23);
         Draw2D.drawStyledText("\uf1b2", Fonts.SUPPLIER8, floatValue26, floatValue25, ColorUtil.withAlpha(value8, (int)(220.0F * floatValue4)), floatValue24);
         float floatValue27 = this.m_46_float();
         float floatValue28 = this.animation2.update(isHovered(value4, value5, floatValue27, floatValue19, 78.0F, 24.0F) ? 1.0F : 0.0F);
         if (floatValue28 > 0.01F) {
            Draw2D.roundedRect(floatValue27, floatValue19, 78.0F, 24.0F, 4.0F, ColorUtil.rgba(255, 255, 255, (int)(28.0F * floatValue28 * floatValue4)));
         }

         int value9 = ColorUtil.lerpColor(ColorUtil.rgba(220, 220, 220, (int)(170.0F * floatValue4)), ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue4)), floatValue28);
         float floatValue29 = Draw2D.getTextWidth("Добавить", Fonts.SUPPLIER15, 12.0F);
         float floatValue30 = Draw2D.getTextBaseline(Fonts.SUPPLIER15, 12.0F, floatValue19, 24.0F);
         Draw2D.drawStyledText("Добавить", Fonts.SUPPLIER15, floatValue27 + (78.0F - floatValue29) / 2.0F, floatValue30, value9, 12.0F);
         String text7 = getMinecraft().getUser().getName();
         float floatValue31 = this.m_3_float();
         float floatValue32 = this.m_2_float();
         float floatValue33 = floatValue31 - this.animation16.getValue();
         boolean flag6 = value5 >= floatValue31 && value5 <= floatValue31 + floatValue32;
         Render2D.pushScissor(floatValue8, floatValue31, 280.0F, floatValue32);
         float floatValue34 = Float.NaN;
         int value10 = ColorUtil.rgba(255, 204, 48, 255);
         int value11 = ColorUtil.rgba(210, 210, 210, 255);

         for (TitleScreenRenderer.StateHolder2 c4 : this.c4List) {
            float floatValue35 = c4.animation.getValue();
            float floatValue36 = 22.0F * floatValue35;
            if (!(floatValue36 < 8.0F)) {
               float floatValue37 = floatValue33 + c4.animation9.getValue();
               boolean flag7 = flag6 && isHovered(value4, value5, floatValue8 + 8.0F, floatValue37, 264.0F, floatValue36);
               boolean flag8 = flag6 && isHovered(value4, value5, floatValue8 + 8.0F, floatValue37, 22.0F, floatValue36);
               boolean flag9 = flag6 && isHovered(value4, value5, floatValue8 + 280.0F - 54.0F, floatValue37, 22.0F, floatValue36);
               boolean flag10 = c4.text.equalsIgnoreCase(text7);
               float floatValue38 = c4.animation2.update(!flag7 && !flag10 ? 0.0F : 1.0F);
               float floatValue39 = c4.animation8.update(flag10 ? 1.0F : 0.0F);
               float floatValue40 = c4.animation3.update(!flag10 && !flag7 ? 0.0F : 1.0F);
               float floatValue41 = c4.animation4.update(flag9 && floatValue40 > 0.2F ? 1.0F : 0.0F);
               float floatValue42 = c4.animation5.update(flag8 ? 1.0F : 0.0F);
               float floatValue43 = c4.animation6.update(!c4.flag3 && !flag7 && !flag10 ? 0.0F : 1.0F);
               float floatValue44 = c4.animation7.update(c4.flag3 ? 1.0F : 0.0F);
               if (floatValue39 > 0.01F) {
                  Draw2D.m_0(
                     floatValue8 + 8.0F,
                     floatValue37,
                     264.0F,
                     floatValue36,
                     4.0F,
                     0.0F,
                     ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(80.0F * floatValue39 * floatValue4 * floatValue35)),
                     ColorUtil.withAlpha(ColorUtil.secondaryColor(), (int)(80.0F * floatValue39 * floatValue4 * floatValue35))
                  );
               } else if (floatValue38 > 0.01F) {
                  Draw2D.roundedRect(floatValue8 + 8.0F, floatValue37, 264.0F, floatValue36, 4.0F, ColorUtil.rgba(255, 255, 255, (int)(36.0F * floatValue38 * floatValue4 * floatValue35)));
               }

               if (flag10) {
                  floatValue34 = floatValue37 + (floatValue36 - 11.0F) / 2.0F;
               }

               if (floatValue36 >= 14.0F) {
                  if (floatValue43 > 0.01F) {
                     float floatValue45 = 10.0F + floatValue42 * 1.5F;
                     float floatValue46 = Draw2D.getCenteredY("\uf005", Fonts.SUPPLIER8, floatValue45, floatValue37, floatValue36);
                     int value12 = ColorUtil.lerpColor(value11, value10, floatValue44);
                     Draw2D.drawStyledText("\uf005", Fonts.SUPPLIER8, floatValue8 + 12.0F, floatValue46, ColorUtil.withAlpha(value12, (int)(255.0F * floatValue43 * floatValue4 * floatValue35)), floatValue45);
                  }

                  int value13 = (int)((flag10 ? 245.0F : 155.0F + 70.0F * floatValue38) * floatValue4 * floatValue35);
                  Draw2D.drawStyledText(c4.text, Fonts.SUPPLIER14, floatValue8 + 30.0F, Draw2D.getTextBaseline(Fonts.SUPPLIER14, 12.0F, floatValue37, floatValue36), ColorUtil.rgba(240, 240, 240, value13), 12.0F);
                  if (floatValue40 > 0.01F) {
                     float floatValue47 = 11.0F + floatValue41 * 2.0F;
                     float floatValue48 = Draw2D.getCenteredY("\uf1f8", Fonts.SUPPLIER8, floatValue47, floatValue37, floatValue36);
                     int value14 = ColorUtil.lerpColor(
                        ColorUtil.rgba(200, 200, 200, (int)(180.0F * floatValue40 * floatValue4 * floatValue35)), ColorUtil.rgba(255, 110, 110, (int)(240.0F * floatValue4 * floatValue35)), floatValue41
                     );
                     Draw2D.drawStyledText("\uf1f8", Fonts.SUPPLIER8, floatValue8 + 280.0F - 50.0F, floatValue48, value14, floatValue47);
                  }
               }
            }
         }

         if (Float.isNaN(floatValue34)) {
            this.animation8.update(0.0F);
            this.text5 = null;
         } else {
            boolean flag11 = this.text5 == null || this.text5.equals(text7);
            this.animation7.updateOrSnap(floatValue34, flag11);
            this.text5 = text7;
            float floatValue49 = this.animation7.getValue();
            float floatValue50 = this.animation8.update(1.0F);
            if (floatValue49 + 11.0F > floatValue31 && floatValue49 < floatValue31 + floatValue32) {
               Draw2D.drawStyledText("\uf00c", Fonts.SUPPLIER8, floatValue8 + 280.0F - 28.0F, floatValue49, ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(220.0F * floatValue4 * floatValue50)), 11.0F);
            }
         }

         Render2D.popScissor();
         if (this.m_43() > 0.0F) {
            Draw2D.m_7(this.m_5_float(), this.m_9_float(), 3.0F, this.m_4r(), this.m_4_float(), this.m_4_k(), 0.0F, 0.0F, floatValue4);
         }
      } finally {
         Render2D.setTransform(1.0F, 0.0F, 0.0F);
      }
   }

   private void m_0_void() {
      boolean flag6 = this.longValue != 0L && System.nanoTime() < this.longValue;
      float floatValue4 = this.animation5.update(flag6 ? 1.0F : 0.0F);
      if (!(floatValue4 < 0.01F) && !this.text.isEmpty()) {
         float floatValue5 = this.animation6.update(1.0F);
         float floatValue6 = this.text2.isEmpty() ? 0.0F : Draw2D.getTextWidth(this.text2, Fonts.SUPPLIER15, 12.0F);
         float floatValue7 = Draw2D.getTextWidth(this.text, Fonts.SUPPLIER15, 12.0F);
         float floatValue8 = 8.0F;
         float floatValue9 = floatValue8 + 13.0F + 6.0F;
         float floatValue10 = floatValue6 + (floatValue7 - floatValue6) * floatValue5 + floatValue9 + 9.0F;
         float floatValue11 = 26.0F;
         float floatValue12 = (this.titleScreen.width - floatValue10) / 2.0F;
         float floatValue13 = 16.0F + (1.0F - floatValue4) * 10.0F;
         Draw2D.drawPanel(floatValue12, floatValue13, floatValue10, floatValue11, floatValue4);
         float floatValue14 = Draw2D.getTextBaseline(Fonts.SUPPLIER15, 12.0F, floatValue13, floatValue11);
         float floatValue15 = floatValue12 + floatValue9;
         int value4 = (int)(240.0F * floatValue4);
         if (floatValue5 < 0.99F && !this.text2.isEmpty()) {
            this.m_2(this.text4, floatValue12 + floatValue8, floatValue13 - floatValue5 * 7.0F, floatValue11, (int)(value4 * (1.0F - floatValue5)));
            this.m_2(this.text3, floatValue12 + floatValue8, floatValue13 + (1.0F - floatValue5) * 7.0F, floatValue11, (int)(value4 * floatValue5));
            Draw2D.drawStyledText(this.text2, Fonts.SUPPLIER15, floatValue15, floatValue14 - floatValue5 * 7.0F, ColorUtil.rgba(240, 240, 240, (int)(value4 * (1.0F - floatValue5))), 12.0F);
            Draw2D.drawStyledText(this.text, Fonts.SUPPLIER15, floatValue15, floatValue14 + (1.0F - floatValue5) * 7.0F, ColorUtil.rgba(240, 240, 240, (int)(value4 * floatValue5)), 12.0F);
         } else {
            this.m_2(this.text3, floatValue12 + floatValue8, floatValue13, floatValue11, value4);
            Draw2D.drawStyledText(this.text, Fonts.SUPPLIER15, floatValue15, floatValue14, ColorUtil.rgba(240, 240, 240, value4), 12.0F);
         }
      }
   }

   private void m_2(String text6, float floatValue4, float floatValue5, float floatValue6, int value4) {
      int value5 = "\uf06a".equals(text6) ? ColorUtil.rgba(255, 105, 105, value4) : ColorUtil.withAlpha(ColorUtil.primaryColor(), value4);
      Draw2D.drawStyledText(text6, Fonts.SUPPLIER8, Draw2D.getCenteredX(text6, Fonts.SUPPLIER8, 13.0F, floatValue4, 13.0F), Draw2D.getCenteredY(text6, Fonts.SUPPLIER8, 13.0F, floatValue5, floatValue6), value5, 13.0F);
   }

   private void m_1(ServerEntry.ModeKind c7) {
      this.stopPingerAndClearFavicons();
      this.mainMenu.m_9(c7);
      this.mainMenu.setFlag2(c7 == ServerEntry.ModeKind.SERVERS && ViaFabricPlusCompat.isAvailable());
      this.mainMenu.clear();
      this.serverEntry.m_6(c7);
      this.m_5_void();
   }

   private void m_5_void() {
      ServerEntry.ModeKind c7 = this.serverEntry.getC7();
      if (c7 == ServerEntry.ModeKind.WORLDS) {
         this.reloadWorlds();
      } else if (c7 == ServerEntry.ModeKind.SERVERS) {
         this.reloadServerList();
      }
   }

   private void reloadWorlds() {
      try {
         LevelCandidates levelCandidates = getMinecraft().getLevelSource().findLevelCandidates();
         getMinecraft().getLevelSource().loadLevelSummaries(levelCandidates).thenAcceptAsync(this::m_1, getMinecraft()).exceptionally(e -> null);
      } catch (Exception e2) {
      }
   }

   private void m_1(List<LevelSummary> levelSummaryList2) {
      if (this.serverEntry.getC7() == ServerEntry.ModeKind.WORLDS) {
         this.closeFavicons();
         this.levelSummaryList = List.copyOf((Collection<? extends LevelSummary>)levelSummaryList2);
         ArrayList list = new ArrayList(levelSummaryList2.size());

         for (LevelSummary levelSummary : levelSummaryList2) {
            list.add(new MainMenu.DataRecord2(levelSummary.getLevelId(), levelSummary.getLevelName(), getGameModeText(levelSummary), levelSummary.primaryActionActive()));
            this.loadWorldFavicon(levelSummary);
         }

         this.mainMenu.setEntries(list);
      }
   }

   private static String getGameModeText(LevelSummary levelSummary) {
      if (levelSummary.isHardcore()) {
         return "Хардкор";
      }

      return switch (levelSummary.getGameMode()) {
         case SURVIVAL -> "Выживание";
         case CREATIVE -> "Творчество";
         case ADVENTURE -> "Приключение";
         case SPECTATOR -> "Наблюдатель";
      };
   }

   private void reloadServerList() {
      this.loadFavorites();
      ServerList serverList2 = new ServerList(getMinecraft());
      serverList2.load();
      this.serverList = serverList2;
      this.closeFavicons();
      MainMenu.DataRecord2 c2 = this.mainMenu.getSelected();
      ArrayList list = new ArrayList(serverList2.size());

      for (int i = 0; i < serverList2.size(); i++) {
         ServerData serverData2 = serverList2.get(i);
         list.add(new MainMenu.DataRecord2(serverData2.ip, serverData2.name, "", !serverData2.ip.isEmpty(), this.stringSet.contains(joinLines(serverData2.name, serverData2.ip))));
         this.loadFavicon(serverData2);
      }

      list.sort((object, object2) -> {
         if (((wtf.onetap.gui.MainMenu.DataRecord2)object).m_46_boolean() != ((wtf.onetap.gui.MainMenu.DataRecord2)object2).m_46_boolean()) {
            return ((wtf.onetap.gui.MainMenu.DataRecord2)object).m_46_boolean() ? -1 : 1;
         } else {
            return 0;
         }
      });
      this.mainMenu.setEntries(list);
      if (c2 != null) {
         this.mainMenu.m_6_void(c2.m_8_o(), c2.m_8j());
      }

      this.stopPinger();
      this.serverStatusPinger = new ServerStatusPinger();
   }

   private void m_16_void() {
      MainMenu.DataRecord2 c2 = this.mainMenu.getSelected();
      if (c2 != null && c2.m_4_j()) {
         ServerEntry.ModeKind c7 = this.serverEntry.getC7();
         if (c7 == ServerEntry.ModeKind.WORLDS) {
            this.stopPingerAndClearFavicons();
            getMinecraft().createWorldOpenFlows().openWorld(c2.m_8_o(), () -> getMinecraft().gui.setScreen(this.titleScreen));
         } else if (c7 == ServerEntry.ModeKind.SERVERS) {
            ServerData serverData2 = this.m_5(c2);
            if (serverData2 == null) {
               serverData2 = new ServerData(c2.m_8j(), c2.m_8_o(), Type.OTHER);
            }

            this.stopPingerAndClearFavicons();
            ConnectScreen.startConnecting(this.titleScreen, getMinecraft(), ServerAddress.parseString(serverData2.ip), serverData2, false, null);
         }
      }
   }

   private ServerData m_5(MainMenu.DataRecord2 c2) {
      if (this.serverList == null) {
         return null;
      }

      for (int i = 0; i < this.serverList.size(); i++) {
         ServerData serverData2 = this.serverList.get(i);
         if (serverData2.ip.equals(c2.m_8_o()) && serverData2.name.equals(c2.m_8j())) {
            return serverData2;
         }
      }

      for (int i2 = 0; i2 < this.serverList.size(); i2++) {
         ServerData serverData3 = this.serverList.get(i2);
         if (serverData3.ip.equals(c2.m_8_o())) {
            return serverData3;
         }
      }

      return null;
   }

   private void m_1(MainMenu.ModeKind c9) {
      switch (c9) {
         case BACK:
            this.serverEntry.hide();
            break;
         case PLAY:
            this.m_16_void();
            break;
         case PROXY:
            this.m_1b_void();
            break;
         case VIA:
            ViaFabricPlusCompat.m_2(this.titleScreen);
            break;
         case REFRESH:
            this.m_1_n_void();
            break;
         case ADD:
            this.m_12_void();
            break;
         case DIRECT:
            this.m_1_k_void();
            break;
         case EDIT:
            this.m_1i_void();
            break;
         case DELETE:
            this.m_2_void();
            break;
         case CREATE:
            CreateWorldScreen.openFresh(getMinecraft(), this::returnToTitle);
            break;
         case RECREATE:
            this.m_1_x_void();
            break;
         case STAR:
            this.m_1_p_void();
            break;
         case MOVE_UP:
            this.m_1_void(-1);
            break;
         case MOVE_DOWN:
            this.m_1_void(1);
      }
   }

   private void returnToTitle() {
      getMinecraft().gui.setScreen(this.titleScreen);
   }

   private void m_1_n_void() {
      if (this.serverEntry.getC7() == ServerEntry.ModeKind.SERVERS) {
         this.reloadServerList();
      }
   }

   private void m_12_void() {
      this.serverData = new ServerData("", "", Type.OTHER);
      this.serverEditScreen.open(ServerEditScreen.ModeKind.ADD);
      this.serverEditScreen.m_3_void(ServerPackStatus.PROMPT.ordinal());
      this.m_4_void();
   }

   private void m_9_void(boolean flag6) {
      if (flag6 && this.serverList != null && this.serverData != null) {
         ServerData serverData2 = this.serverList.unhide(this.serverData.ip);
         if (serverData2 != null) {
            serverData2.copyNameIconFrom(this.serverData);
            ViaFabricPlusCompat.m_9(this.serverData, serverData2);
         } else {
            this.serverList.add(this.serverData, false);
         }

         this.serverList.save();
      }

      this.returnToTitle();
      this.m_5_void();
   }

   private void m_1_k_void() {
      this.serverData = new ServerData(Component.translatable("selectServer.defaultName").getString(), "", Type.OTHER);
      this.serverEditScreen.open(ServerEditScreen.ModeKind.DIRECT);
      this.serverEditScreen.setFields(getMinecraft().options.lastMpIp, "", "");
      this.m_4_void();
   }

   private void m_6_void(boolean flag6) {
      if (!flag6) {
         this.returnToTitle();
      } else {
         if (this.serverList != null) {
            ServerData serverData2 = this.serverList.get(this.serverData.ip);
            if (serverData2 == null) {
               this.serverList.add(this.serverData, true);
               this.serverList.save();
               ViaFabricPlusCompat.m_6(this.serverData);
               this.connectTo(this.serverData);
            } else {
               ViaFabricPlusCompat.m_6(serverData2);
               this.connectTo(serverData2);
            }
         } else {
            ViaFabricPlusCompat.m_6(this.serverData);
            this.connectTo(this.serverData);
         }
      }
   }

   private void connectTo(ServerData serverData2) {
      this.stopPingerAndClearFavicons();
      ConnectScreen.startConnecting(this.titleScreen, getMinecraft(), ServerAddress.parseString(serverData2.ip), serverData2, false, null);
   }

   private void m_1i_void() {
      if (this.mainMenu.getC7() == ServerEntry.ModeKind.SERVERS) {
         ServerData serverData2 = this.getSelectedServer();
         if (serverData2 != null) {
            this.serverData = new ServerData(serverData2.name, serverData2.ip, Type.OTHER);
            this.serverData.copyFrom(serverData2);
            ViaFabricPlusCompat.m_9(serverData2, this.serverData);
            this.serverEditScreen.open(ServerEditScreen.ModeKind.EDIT);
            this.serverEditScreen.setFields(serverData2.name, serverData2.ip, "");
            this.serverEditScreen.m_3_void(serverData2.getResourcePackStatus().ordinal());
            this.m_4_void();
         }
      } else {
         LevelSummary levelSummary = this.getSelectedLevel();
         if (levelSummary != null) {
            this.m_0(levelSummary);
         }
      }
   }

   private void m_7_void(boolean flag6) {
      ServerData serverData2 = this.getSelectedServer();
      if (flag6 && serverData2 != null && this.serverData != null) {
         String text6 = joinLines(serverData2.name, serverData2.ip);
         serverData2.name = this.serverData.name;
         serverData2.ip = this.serverData.ip;
         serverData2.copyFrom(this.serverData);
         ViaFabricPlusCompat.m_9(this.serverData, serverData2);
         if (this.stringSet.remove(text6)) {
            this.stringSet.add(joinLines(serverData2.name, serverData2.ip));
            this.saveFavoriteList();
         }

         if (this.serverList != null) {
            this.serverList.save();
         }
      }

      this.returnToTitle();
      this.m_5_void();
   }

   private void m_2_void() {
      if (this.mainMenu.getC7() == ServerEntry.ModeKind.SERVERS) {
         ServerData serverData2 = this.getSelectedServer();
         if (serverData2 != null) {
            getMinecraft()
               .gui
               .setScreen(
                  new ConfirmScreen(
                     flag6 -> {
                        if (flag6 && this.serverList != null) {
                           this.stringSet.remove(joinLines(serverData2.name, serverData2.ip));
                           this.saveFavoriteList();
                           this.serverList.remove(serverData2);
                           this.serverList.save();
                        }

                        this.returnToTitle();
                        this.m_5_void();
                     },
                     Component.translatable("selectServer.deleteQuestion"),
                     Component.translatable("selectServer.deleteWarning", serverData2.name),
                     Component.translatable("selectServer.deleteButton"),
                     CommonComponents.GUI_CANCEL
                  )
               );
         }
      } else {
         LevelSummary levelSummary = this.getSelectedLevel();
         if (levelSummary != null) {
            getMinecraft()
               .gui
               .setScreen(
                  new ConfirmScreen(
                     flag7 -> {
                        if (flag7) {
                           getMinecraft().gui.setScreen(new ProgressScreen(true));
                           this.deleteWorld(levelSummary.getLevelId());
                        }

                        this.returnToTitle();
                        this.m_5_void();
                     },
                     Component.translatable("selectWorld.deleteQuestion"),
                     Component.translatable("selectWorld.deleteWarning", levelSummary.getLevelName()),
                     Component.translatable("selectWorld.deleteButton"),
                     CommonComponents.GUI_CANCEL
                  )
               );
         }
      }
   }

   private void deleteWorld(String text6) {
      try (LevelStorageAccess levelStorageAccess = getMinecraft().getLevelSource().createAccess(text6)) {
         levelStorageAccess.deleteLevel();
      } catch (Exception e) {
         SystemToast.onWorldDeleteFailure(getMinecraft(), text6);
      }
   }

   private void m_0(LevelSummary levelSummary) {
      String text6 = levelSummary.getLevelId();

      LevelStorageAccess levelStorageAccess;
      try {
         levelStorageAccess = getMinecraft().getLevelSource().validateAndCreateAccess(text6);
      } catch (Exception e) {
         SystemToast.onWorldAccessFailure(getMinecraft(), text6);
         this.m_5_void();
         return;
      }

      try {
         EditWorldScreen editWorldScreen = EditWorldScreen.create(getMinecraft(), levelStorageAccess, flag6 -> {
            levelStorageAccess.safeClose();
            this.returnToTitle();
            this.m_5_void();
         });
         getMinecraft().gui.setScreen(editWorldScreen);
      } catch (Exception e2) {
         levelStorageAccess.safeClose();
         SystemToast.onWorldAccessFailure(getMinecraft(), text6);
         this.m_5_void();
      }
   }

   private void m_1_x_void() {
      LevelSummary levelSummary = this.getSelectedLevel();
      if (levelSummary != null) {
         try (LevelStorageAccess levelStorageAccess = getMinecraft().getLevelSource().validateAndCreateAccess(levelSummary.getLevelId())) {
            var pair = getMinecraft().createWorldOpenFlows().recreateWorldData(levelStorageAccess);
            Path path = CreateWorldScreen.createTempDataPackDirFromExistingWorld(levelStorageAccess.getLevelPath(LevelResource.DATAPACK_DIR), getMinecraft());
            ((WorldCreationContext)pair.getSecond()).validate();
            getMinecraft()
               .gui
               .setScreen(
                  CreateWorldScreen.createFromExisting(
                     getMinecraft(), this::returnToTitle, (LevelSettings)pair.getFirst(), (WorldCreationContext)pair.getSecond(), path
                  )
               );
         } catch (ContentValidationException contentValidationException) {
            getMinecraft().gui.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(this::returnToTitle));
         } catch (Exception e) {
            getMinecraft()
               .gui
               .setScreen(
                  new AlertScreen(
                     this::returnToTitle, Component.translatable("selectWorld.recreate.error.title"), Component.translatable("selectWorld.recreate.error.text")
                  )
               );
         }
      }
   }

   private ServerData getSelectedServer() {
      MainMenu.DataRecord2 c2 = this.mainMenu.getSelected();
      return c2 == null ? null : this.m_5(c2);
   }

   private LevelSummary getSelectedLevel() {
      MainMenu.DataRecord2 c2 = this.mainMenu.getSelected();
      if (c2 == null) {
         return null;
      }

      for (LevelSummary levelSummary : this.levelSummaryList) {
         if (levelSummary.getLevelId().equals(c2.m_8_o())) {
            return levelSummary;
         }
      }

      return null;
   }

   private Animation m_6(MainMenu.ModeKind c9) {
      return this.enumMap.computeIfAbsent(c9, c92 -> new Animation(140L, 0.0F, Easing.EASING8));
   }

   private String getButtonLabel(MainMenu.ModeKind c9) {
      return switch (c9) {
         case BACK -> "Назад";
         case PLAY -> this.mainMenu.getButtonText();
         case PROXY -> "Прокси";
         case VIA -> this.getViaLabel();
         case REFRESH -> "Обновить";
         case ADD -> "Добавить";
         case DIRECT -> "Прямое";
         case EDIT -> "Изменить";
         case DELETE -> "Удалить";
         case CREATE -> "Создать";
         case RECREATE -> "Пересоздать";
         default -> "";
      };
   }

   private String getButtonIcon(MainMenu.ModeKind c9) {
      return switch (c9) {
         case BACK -> "\uf060";
         case PLAY -> "\uf04b";
         case PROXY -> "\uf233";
         case VIA -> "\uf121";
         case REFRESH -> "\uf021";
         case ADD, CREATE -> "\uf067";
         case DIRECT -> "\uf0ac";
         case EDIT -> "\uf040";
         case DELETE -> "\uf1f8";
         case RECREATE -> "\uf0c5";
         default -> "\uf013";
      };
   }

   private void m_1b_void() {
      this.serverEditScreen.open(ServerEditScreen.ModeKind.PROXY);
      this.serverEditScreen.setFields(ProxyState.proxyEntry.text, ProxyState.proxyEntry.text2, ProxyState.proxyEntry.text3);
      this.serverEditScreen.setFlag2(ProxyState.flag);
   }

   private String getViaLabel() {
      String text6 = ViaFabricPlusCompat.m_7_String();
      return !text6.isEmpty() && text6.length() <= 12 ? text6 : "Via";
   }

   private void m_4_void() {
      this.serverEditScreen.setFlag6(ViaFabricPlusCompat.isAvailable());
      this.m_8_void();
   }

   private void m_8_void() {
      if (this.serverEditScreen.isEditing()) {
         if (this.serverEditScreen.getC1() == ServerEditScreen.ModeKind.DIRECT) {
            this.serverEditScreen.m_5_void(ViaFabricPlusCompat.m_7_String());
         } else {
            this.serverEditScreen.m_5_void(ViaFabricPlusCompat.m_8(this.serverData));
         }
      }
   }

   private void m_1l_void() {
      if (this.serverEditScreen.getC1() == ServerEditScreen.ModeKind.DIRECT) {
         ViaFabricPlusCompat.m_2(this.titleScreen);
      } else {
         ViaFabricPlusCompat.m_2(this.titleScreen, this.serverData);
      }
   }

   private void m_1e_void() {
      switch (this.serverEditScreen.getC1()) {
         case PROXY:
            this.serverEditScreen.applyProxy();
            break;
         case ADD:
            this.serverData = new ServerData(
               this.serverEditScreen.getEntryText(0).isBlank() ? Component.translatable("selectServer.defaultName").getString() : this.serverEditScreen.getEntryText(0),
               this.serverEditScreen.getEntryText(1),
               Type.OTHER
            );
            this.serverData.setResourcePackStatus(ServerPackStatus.values()[this.serverEditScreen.getValue2()]);
            this.m_9_void(true);
            break;
         case EDIT:
            if (this.serverData != null) {
               this.serverData.name = this.serverEditScreen.getEntryText(0).isBlank() ? Component.translatable("selectServer.defaultName").getString() : this.serverEditScreen.getEntryText(0);
               this.serverData.ip = this.serverEditScreen.getEntryText(1);
               this.serverData.setResourcePackStatus(ServerPackStatus.values()[this.serverEditScreen.getValue2()]);
            }

            this.m_7_void(true);
            break;
         case DIRECT:
            this.serverData.ip = this.serverEditScreen.getEntryText(0);
            getMinecraft().options.lastMpIp = this.serverEditScreen.getEntryText(0);
            getMinecraft().options.save();
            this.m_6_void(true);
      }
   }

   private boolean m_5(MouseButtonEvent event, boolean flag6) {
      if (!flag6) {
         return false;
      }

      MainMenu.DataRecord4 c6 = this.mainMenu.m_7_C6((float)event.x(), (float)event.y(), this.titleScreen.width, this.titleScreen.height);
      return c6.m_6p_C9() == MainMenu.ModeKind.ROW && this.mainMenu.getSelected() != null;
   }

   private TitleScreenRenderer.StateHolder m_3(MainMenu.DataRecord2 c2) {
      return this.stringMap.computeIfAbsent(c2.m_8j() + "\n" + c2.m_8_o(), text6 -> new TitleScreenRenderer.StateHolder());
   }

   private void m_1_p_void() {
      MainMenu.DataRecord2 c2 = this.mainMenu.getSelected();
      if (c2 != null && this.mainMenu.getC7() == ServerEntry.ModeKind.SERVERS) {
         String text6 = joinLines(c2.m_8j(), c2.m_8_o());
         if (c2.m_46_boolean()) {
            this.stringSet.remove(text6);
         } else {
            this.stringSet.add(text6);
         }

         this.mainMenu.m_7_void(this.mainMenu.getValue());
         this.saveFavoriteList();
      }
   }

   private void m_1_void(int value4) {
      int value5 = this.mainMenu.getValue();
      int value6 = value5 + value4;
      if (value5 >= 0 && value6 >= 0) {
         var c2List = this.mainMenu.getEntries();
         if (value6 < c2List.size()) {
            ServerData serverData2 = this.m_5((MainMenu.DataRecord2)c2List.get(value5));
            ServerData serverData3 = this.m_5((MainMenu.DataRecord2)c2List.get(value6));
            if (this.mainMenu.swapEntries(value5, value6) && this.serverList != null && serverData2 != null && serverData3 != null) {
               int value7 = this.indexOf(serverData2);
               int value8 = this.indexOf(serverData3);
               if (value7 >= 0 && value8 >= 0) {
                  this.serverList.replace(value7, serverData3);
                  this.serverList.replace(value8, serverData2);
                  this.serverList.save();
               }
            }
         }
      }
   }

   private int indexOf(ServerData serverData2) {
      if (this.serverList == null) {
         return -1;
      }

      for (int i = 0; i < this.serverList.size(); i++) {
         if (this.serverList.get(i) == serverData2) {
            return i;
         }
      }

      return -1;
   }

   private void loadFavorites() {
      this.stringSet.clear();

      try {
         if (!Files.exists(PATH3)) {
            return;
         }

         JsonArray jsonArray = JsonParser.parseString(Files.readString(PATH3, StandardCharsets.UTF_8)).getAsJsonArray();

         for (int i = 0; i < jsonArray.size(); i++) {
            this.stringSet.add(jsonArray.get(i).getAsString());
         }
      } catch (Exception e) {
      }
   }

   private void saveFavoriteList() {
      try {
         Files.createDirectories(PATH3.getParent());
         JsonArray jsonArray = new JsonArray();

         for (String text6 : this.stringSet) {
            jsonArray.add(text6);
         }

         Files.writeString(PATH3, jsonArray.toString(), StandardCharsets.UTF_8);
      } catch (Exception e) {
      }
   }

   private static String joinLines(String text6, String text7) {
      return text6 + "\n" + text7;
   }

   private void loadWorldFavicon(LevelSummary levelSummary) {
      FaviconTexture faviconTexture = FaviconTexture.forWorld(getMinecraft().getTextureManager(), levelSummary.getLevelId());
      Path path = levelSummary.getIcon();
      if (path != null && Files.isRegularFile(path)) {
         try (InputStream inputStream = Files.newInputStream(path)) {
            faviconTexture.upload(NativeImage.read(inputStream));
         } catch (Exception e) {
            faviconTexture.clear();
         }
      }

      this.stringMap2.put(levelSummary.getLevelId(), faviconTexture);
   }

   private void loadFavicon(ServerData serverData2) {
      FaviconTexture faviconTexture = FaviconTexture.forServer(getMinecraft().getTextureManager(), serverData2.ip);
      this.setFavicon(faviconTexture, serverData2.getIconBytes());
      this.stringMap3.put(serverData2.ip, serverData2.getIconBytes());
      this.stringMap2.put(serverData2.ip, faviconTexture);
   }

   private void setFavicon(FaviconTexture faviconTexture, byte[] byteArray) {
      if (byteArray == null) {
         faviconTexture.clear();
      } else {
         try {
            faviconTexture.upload(NativeImage.read(byteArray));
         } catch (Exception e) {
            faviconTexture.clear();
         }
      }
   }

   private void m_4(MainMenu.DataRecord2 c2, float floatValue4, float floatValue5, float floatValue6) {
      FaviconTexture faviconTexture = this.stringMap2.get(c2.m_8_o());
      if (faviconTexture != null && this.serverList != null && this.mainMenu.getC7() == ServerEntry.ModeKind.SERVERS) {
         ServerData serverData2 = this.m_5(c2);
         if (serverData2 != null) {
            byte[] byteArray = serverData2.getIconBytes();
            if (!Arrays.equals(byteArray, this.stringMap3.get(c2.m_8_o()))) {
               this.setFavicon(faviconTexture, byteArray);
               this.stringMap3.put(c2.m_8_o(), byteArray);
            }
         }
      }

      Identifier identifier = faviconTexture != null ? faviconTexture.textureLocation() : Identifier.withDefaultNamespace("textures/misc/unknown_server.png");
      GpuTextureView gpuTextureView = getMinecraft().getTextureManager().getTexture(identifier).getTextureView();
      int value4 = ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue6));
      Draw2D.m_4(floatValue4, floatValue5, 32.0F, 32.0F, 4.0F, value4, gpuTextureView);
   }

   private static List<Component> m_1(Component component) {
      if (component == null) {
         return List.of();
      }

      ArrayList list = new ArrayList(2);
      MutableComponent[] mutableComponentArray = new MutableComponent[]{Component.empty()};
      ChatBadges.m_6(component).visit((style, text6) -> {
         int value4 = 0;

         for (int i = 0; i < text6.length(); i++) {
            char ch = text6.charAt(i);
            if (ch == '\n' || ch == '\r') {
               if (i > value4) {
                  mutableComponentArray[0].append(Component.literal(text6.substring(value4, i)).withStyle(style));
               }

               if (!mutableComponentArray[0].getString().isBlank()) {
                  list.add(mutableComponentArray[0]);
                  if (list.size() == 2) {
                     return Optional.of(Boolean.TRUE);
                  }
               }

               Component.empty();
               if (ch == '\r' && i + 1 < text6.length() && text6.charAt(i + 1) == '\n') {
                  i++;
               }

               value4 = i + 1;
            }
         }

         if (value4 < text6.length()) {
            mutableComponentArray[0].append(Component.literal(text6.substring(value4)).withStyle(style));
         }

         return Optional.empty();
      }, Style.EMPTY);
      if (list.size() < 2 && !mutableComponentArray[0].getString().isBlank()) {
         list.add(mutableComponentArray[0]);
      }

      return list;
   }

   private static String getPingText(ServerData serverData2) {
      State state = serverData2.state();
      boolean flag6 = state == State.INITIAL || state == State.PINGING;
      Integer integer = serverData2.players == null ? null : serverData2.players.online();
      Integer integer2 = serverData2.players == null ? null : serverData2.players.max();
      return MainMenu.formatPlayers(integer, integer2, flag6);
   }

   private static String getStatusText(ServerData serverData2) {
      State state = serverData2.state();
      boolean flag6 = state == State.INITIAL || state == State.PINGING;
      boolean flag7 = state == State.UNREACHABLE;
      return MainMenu.formatPing(serverData2.ping, flag6, flag7);
   }

   private static int getPingColor(ServerData serverData2) {
      if (serverData2.state() == State.UNREACHABLE) {
         return ColorUtil.rgba(255, 110, 110, 255);
      }

      if (serverData2.state() != State.PINGING && serverData2.state() != State.INITIAL) {
         long longValue2 = serverData2.ping;
         if (longValue2 < 80L) {
            return ColorUtil.rgba(120, 220, 140, 255);
         } else {
            return longValue2 < 150L ? ColorUtil.rgba(230, 200, 90, 255) : ColorUtil.rgba(255, 110, 110, 255);
         }
      } else {
         return ColorUtil.primaryColor();
      }
   }

   private void startPing(ServerData serverData2) {
      if (this.serverStatusPinger != null && serverData2.state() == State.INITIAL) {
         serverData2.setState(State.PINGING);
         ServerStatusPinger serverStatusPinger2 = this.serverStatusPinger;
         Util.backgroundExecutor()
            .execute(
               () -> {
                  if (this.serverStatusPinger == serverStatusPinger2) {
                     try {
                        serverStatusPinger2.pingServer(
                           serverData2,
                           () -> {},
                           () -> serverData2.setState(serverData2.protocol == SharedConstants.getCurrentVersion().protocolVersion() ? State.SUCCESSFUL : State.INCOMPATIBLE),
                           EventLoopGroupHolder.remote(getMinecraft().options.useNativeTransport())
                        );
                     } catch (Exception e) {
                        serverData2.setState(State.UNREACHABLE);
                        serverData2.motd = Component.translatable("multiplayer.status.cannot_connect");
                     }
                  }
               }
            );
      }
   }

   private void stopPinger() {
      if (this.serverStatusPinger != null) {
         this.serverStatusPinger.removeAll();
         this.serverStatusPinger = null;
      }
   }

   private void closeFavicons() {
      for (FaviconTexture faviconTexture : this.stringMap2.values()) {
         if (!faviconTexture.isClosed()) {
            faviconTexture.close();
         }
      }

      this.stringMap2.clear();
      this.stringMap3.clear();
   }

   private void stopPingerAndClearFavicons() {
      this.stopPinger();
      this.closeFavicons();
   }

   private void reset() {
      this.flag2 = true;
      this.flag3 = true;
   }

   private void m_1_q_void() {
      this.flag2 = false;
      this.flag3 = false;
      this.flag5 = false;
   }

   private void m_3_void(String text6) {
      this.serverListEntry.setTextLimited(text6);
   }

   private void m_4(boolean flag6) {
      String text6 = this.serverListEntry.getText2();
      if (!ServerListEntry.isValid(text6)) {
         if (flag6) {
            this.m_3_void("\uf06a", "Ник от 3 до 16 символов");
         }
      } else {
         if (!matchesPattern(text6) && !this.hasToast(text6)) {
            this.c4List.add(new TitleScreenRenderer.StateHolder2(text6, false, this.value3++));
         }

         boolean flag7 = !text6.equals(getMinecraft().getUser().getName());
         if (flag7) {
            UUID uUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + text6).getBytes(StandardCharsets.UTF_8));
            User user = new User(text6, uUID, "0", Optional.empty(), Optional.empty());
            ((MinecraftAccessor)getMinecraft()).setUser(user);
         }

         this.saveSettings();
         if (flag6) {
            this.m_3_void("\uf2bd", "Зашёл как " + text6);
         }
      }
   }

   private void m_1_r_void() {
      this.serverListEntry.clear();
      this.flag3 = false;
   }

   private static boolean isAllowedChar(int value4) {
      return value4 == 95 || Character.isLetterOrDigit(value4);
   }

   private boolean hasToast(String text6) {
      for (TitleScreenRenderer.StateHolder2 c4 : this.c4List) {
         if (!c4.flag2 && c4.text.equalsIgnoreCase(text6)) {
            return true;
         }
      }

      return false;
   }

   private void m_3_void(String text6, String text7) {
      if (!text7.equals(this.text)) {
         this.text2 = this.text;
         this.text4 = this.text3;
         this.text = text7;
         this.text3 = text6;
         this.animation6.setDuration(0.0F);
      }

      this.longValue = System.nanoTime() + 2400000000L;
   }

   private static float m_4_h() {
      float floatValue4 = 0.0F;

      for (String text6 : TEXT_ARRAY) {
         floatValue4 = Math.max(floatValue4, Draw2D.getTextWidth(text6, Fonts.SUPPLIER8, 15.0F));
      }

      return floatValue4;
   }

   private float m_0_float() {
      return (this.titleScreen.width - 168.0F) / 2.0F;
   }

   private float m_4p_float() {
      return 8.0F + TEXT_ARRAY2.length * 25.0F - 1.0F;
   }

   private float m_4i_float() {
      return this.m_4_c_float() - 32.0F - 10.0F;
   }

   private float m_4_c_float() {
      return (this.titleScreen.height - this.m_4p_float()) / 2.0F;
   }

   private float m_4_z_float() {
      return (this.titleScreen.width - 280.0F) / 2.0F;
   }

   private void m_6_void() {
      for (TitleScreenRenderer.StateHolder2 c4 : this.c4List) {
         c4.animation.update(c4.flag2 ? 0.0F : 1.0F);
      }

      this.c4List.removeIf(c42 -> c42.flag2 && c42.animation.isFinished() && c42.animation.getValue() < 0.03F);
      ArrayList list = new ArrayList<>(this.c4List);
      list.sort((object, object2) -> {
         if (((wtf.onetap.gui.TitleScreenRenderer.StateHolder2)object).flag3 != ((wtf.onetap.gui.TitleScreenRenderer.StateHolder2)object2).flag3) {
            return ((wtf.onetap.gui.TitleScreenRenderer.StateHolder2)object).flag3 ? -1 : 1;
         } else {
            return Integer.compare(((wtf.onetap.gui.TitleScreenRenderer.StateHolder2)object).value, ((wtf.onetap.gui.TitleScreenRenderer.StateHolder2)object2).value);
         }
      });
      float floatValue4 = 0.0F;

      for (TitleScreenRenderer.StateHolder2 c43 : (Iterable<TitleScreenRenderer.StateHolder2>)list) {
         if (!c43.flag) {
            c43.animation9.setDuration(floatValue4);
            c43.flag = true;
         } else {
            c43.animation9.updateWithThreshold(floatValue4, 11.0F);
         }

         floatValue4 += 22.0F * c43.animation.getValue();
      }

      this.m_1_y_void();
      this.animation16.update(this.floatValue2);
   }

   private float m_6_float() {
      float floatValue4 = 0.0F;

      for (TitleScreenRenderer.StateHolder2 c4 : this.c4List) {
         floatValue4 += 22.0F * c4.animation.getValue();
      }

      return floatValue4;
   }

   private float m_2_float() {
      float floatValue4 = Math.max(66.0F, Math.min(242.0F, this.titleScreen.height - 160));
      return Math.min(this.m_6_float(), floatValue4);
   }

   private float m_43() {
      return Math.max(0.0F, this.m_6_float() - this.m_2_float());
   }

   private void m_1_y_void() {
      float floatValue4 = this.m_43();
      this.floatValue2 = Math.max(0.0F, Math.min(this.floatValue2, floatValue4));
      if (this.animation16.getValue() > floatValue4) {
         this.animation16.setDuration(floatValue4);
      }
   }

   private float m_3_float() {
      return this.m_4n_float() + 24.0F + 8.0F;
   }

   private float m_5_float() {
      return this.m_4_z_float() + 280.0F - 6.0F;
   }

   private float m_9_float() {
      return this.m_3_float();
   }

   private float m_4r() {
      return this.m_2_float();
   }

   private float m_4_k() {
      return Math.min(this.m_4r(), Math.max(18.0F, this.m_4r() * this.m_2_float() / Math.max(this.m_6_float(), 1.0F)));
   }

   private float m_4_float() {
      float floatValue4 = this.m_43();
      float floatValue5 = floatValue4 <= 0.0F ? 0.0F : Math.min(1.0F, this.animation16.getValue() / floatValue4);
      return this.m_9_float() + (this.m_4r() - this.m_4_k()) * floatValue5;
   }

   private void m_8_void(float floatValue4) {
      float floatValue5 = this.m_4r() - this.m_4_k();
      if (!(floatValue5 <= 0.0F)) {
         float floatValue6 = Math.max(0.0F, Math.min(1.0F, (floatValue4 - this.m_9_float() - this.floatValue3) / floatValue5));
         this.floatValue2 = floatValue6 * this.m_43();
         this.animation16.setDuration(this.floatValue2);
      }
   }

   private float m_4_o_float() {
      return 68.0F + this.m_2_float() + 16.0F;
   }

   private float m_4_w_float() {
      return (this.titleScreen.height - this.m_4_o_float()) / 2.0F;
   }

   private float m_4h() {
      return this.m_4_z_float() + 16.0F;
   }

   private float m_4n_float() {
      return this.m_4_w_float() + 42.0F;
   }

   private float m_7_float() {
      return 126.0F;
   }

   private float m_49_float() {
      return this.m_4h() + this.m_7_float() + 8.0F;
   }

   private float m_46_float() {
      return this.m_49_float() + 24.0F + 8.0F;
   }

   private float m_8_float(int value4) {
      return this.m_3_float() - this.animation16.getValue() + this.c4List.get(value4).animation9.getValue();
   }

   private int m_2_int(float floatValue4) {
      if (!(floatValue4 < this.m_3_float()) && !(floatValue4 > this.m_3_float() + this.m_2_float())) {
         float floatValue5 = this.m_3_float() - this.animation16.getValue();

         for (int i = 0; i < this.c4List.size(); i++) {
            TitleScreenRenderer.StateHolder2 c4 = this.c4List.get(i);
            float floatValue6 = 22.0F * c4.animation.getValue();
            float floatValue7 = floatValue5 + c4.animation9.getValue();
            if (!c4.flag2 && floatValue6 > 8.0F && floatValue4 >= floatValue7 && floatValue4 <= floatValue7 + floatValue6) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private int m_3_int(double doubleValue, double doubleValue2) {
      float floatValue4 = this.m_0_float() + 4.0F;
      float floatValue5 = this.m_4_c_float() + 4.0F;
      float floatValue6 = 160.0F;

      for (int i = 0; i < TEXT_ARRAY2.length; i++) {
         if (doubleValue >= floatValue4 && doubleValue <= floatValue4 + floatValue6 && doubleValue2 >= floatValue5 && doubleValue2 <= floatValue5 + 24.0F) {
            return i;
         }

         floatValue5 += 25.0F;
      }

      return -1;
   }

   private void m_1_void() {
      TextureManager textureManager = getMinecraft().getTextureManager();
      float floatValue4 = this.animation11.update(1.0F);
      GpuTextureView gpuTextureView = textureManager.getTexture(IDENTIFIER_ARRAY[floatValue4 < 1.0F ? this.value2 : this.value]).getTextureView();
      BlurRenderer.m_4(gpuTextureView);
      this.m_4(gpuTextureView, -1);
      if (floatValue4 < 1.0F) {
         this.m_4(textureManager.getTexture(IDENTIFIER_ARRAY[this.value]).getTextureView(), ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue4)));
      }

      float floatValue5 = this.animation15.update(this.floatValue);
      int value4 = (int)floatValue5;
      float floatValue6 = floatValue5 - value4;
      if (value4 == 0) {
         if (floatValue6 > 0.01F) {
            Draw2D.m_5_void(0.0F, 0.0F, this.titleScreen.width, this.titleScreen.height, floatValue6, -1);
         }
      } else {
         Draw2D.m_5_void(0.0F, 0.0F, this.titleScreen.width, this.titleScreen.height, value4, -1);
         if (floatValue6 > 0.01F) {
            Render2D.m_4(0.0F, 0.0F, this.titleScreen.width, this.titleScreen.height, value4 + 1, floatValue6);
         }
      }

      textureManager.getTexture(IDENTIFIER_ARRAY[(this.value + 1) % IDENTIFIER_ARRAY.length]);
   }

   private void m_4(GpuTextureView gpuTextureView, int value4) {
      float floatValue4 = (float)gpuTextureView.texture().getWidth(0) / gpuTextureView.texture().getHeight(0);
      float floatValue5 = (float)this.titleScreen.width / this.titleScreen.height;
      float floatValue6 = floatValue5 < floatValue4 ? (1.0F - floatValue5 / floatValue4) / 2.0F : 0.0F;
      float floatValue7 = floatValue5 > floatValue4 ? (1.0F - floatValue4 / floatValue5) / 2.0F : 0.0F;
      Render2D.m_4(0.0F, 0.0F, this.titleScreen.width, this.titleScreen.height, 0.0F, value4, gpuTextureView, floatValue6, floatValue7, 1.0F - floatValue6, 1.0F - floatValue7, FilterMode.LINEAR);
   }

   private void m_6(int value4, int value5, float floatValue4) {
      float floatValue5 = this.m_4_a();
      float floatValue6 = this.m_4m_float();
      if (this.flag) {
         if (KeyNames.isLeftMouseDown()) {
            this.m_0_void(this.m_1_float(value4));
         } else {
            this.flag = false;
            writeFile(PATH5, Float.toString(this.floatValue));
         }
      }

      boolean flag6 = this.m_7_boolean();
      float floatValue7 = this.animation14.update(!this.flag && (!flag6 || !isHovered(value4, value5, floatValue5, floatValue6, 104.0F, 24.0F)) ? 0.0F : 1.0F);
      float floatValue8 = this.animation12.update(flag6 && !this.flag && isHovered(value4, value5, this.m_8_float(), floatValue6, 24.0F, 24.0F) ? 1.0F : 0.0F);
      Draw2D.drawPanel(floatValue5, floatValue6, 128.0F, 24.0F, floatValue4);
      Draw2D.roundedRect(floatValue5 + 104.0F, floatValue6 + 6.0F, 1.0F, 12.0F, 0.5F, ColorUtil.rgba(255, 255, 255, (int)(30.0F * floatValue4)));
      int value6 = ColorUtil.rgba(230, 230, 230, (int)(195.0F * floatValue4));
      int value7 = ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(255.0F * floatValue4));
      Draw2D.drawStyledText(
         "\uf043",
         Fonts.SUPPLIER8,
         Draw2D.getCenteredX("\uf043", Fonts.SUPPLIER8, 12.0F, floatValue5, 24.0F),
         Draw2D.getCenteredY("\uf043", Fonts.SUPPLIER8, 12.0F, floatValue6, 24.0F),
         ColorUtil.lerpColor(value6, value7, floatValue7),
         12.0F
      );
      float floatValue9 = this.m_4_t_float();
      float floatValue10 = this.m_1_float();
      float floatValue11 = floatValue6 + 12.0F;
      float floatValue12 = 3.0F + floatValue7;
      float floatValue13 = floatValue10 * (this.flag ? this.floatValue : this.animation15.getValue()) / 5.0F;
      Draw2D.roundedRect(floatValue9, floatValue11 - floatValue12 / 2.0F, floatValue10, floatValue12, floatValue12 / 2.0F, ColorUtil.rgba(255, 255, 255, (int)(40.0F * floatValue4)));
      Draw2D.roundedRect(floatValue9, floatValue11 - floatValue12 / 2.0F, floatValue13, floatValue12, floatValue12 / 2.0F, ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(230.0F * floatValue4)));
      float floatValue14 = 7.0F + 2.0F * floatValue7;
      Draw2D.roundedRect(floatValue9 + floatValue13 - floatValue14 / 2.0F, floatValue11 - floatValue14 / 2.0F, floatValue14, floatValue14, floatValue14 / 2.0F, ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue4)));
      float floatValue15 = this.m_8_float();
      if (floatValue8 > 0.01F) {
         Draw2D.roundedRect(floatValue15 + 3.0F, floatValue6 + 3.0F, 18.0F, 18.0F, 5.0F, ColorUtil.rgba(255, 255, 255, (int)(22.0F * floatValue8 * floatValue4)));
      }

      float floatValue16 = this.animation13.get();
      float floatValue17 = 12.0F + 2.0F * floatValue8;
      float floatValue18 = (float)Math.sin((floatValue16 - Math.floor(floatValue16)) * Math.PI) * 3.0F;
      Draw2D.drawStyledText(
         "\uf302",
         Fonts.SUPPLIER8,
         Draw2D.getCenteredX("\uf302", Fonts.SUPPLIER8, floatValue17, floatValue15, 24.0F),
         Draw2D.getCenteredY("\uf302", Fonts.SUPPLIER8, floatValue17, floatValue6, 24.0F) - floatValue18,
         ColorUtil.lerpColor(value6, value7, floatValue8),
         floatValue17
      );
      float floatValue19 = 3.0F;
      float floatValue20 = 3.0F;
      float floatValue21 = floatValue15 + (24.0F - (IDENTIFIER_ARRAY.length * floatValue19 + (IDENTIFIER_ARRAY.length - 1) * floatValue20)) / 2.0F;

      for (int i = 0; i < IDENTIFIER_ARRAY.length; i++) {
         Draw2D.roundedRect(
            floatValue21 + i * (floatValue19 + floatValue20),
            floatValue6 + 24.0F + 4.0F,
            floatValue19,
            floatValue19,
            floatValue19 / 2.0F,
            i == this.value ? value7 : ColorUtil.rgba(255, 255, 255, (int)(90.0F * floatValue4))
         );
      }
   }

   private boolean m_9_boolean(float floatValue4, float floatValue5) {
      if (!this.m_7_boolean()) {
         return false;
      } else {
         float floatValue6 = this.m_4m_float();
         if (isHovered(floatValue4, floatValue5, this.m_8_float(), floatValue6, 24.0F, 24.0F)) {
            this.m_1q_void();
            return true;
         } else if (isHovered(floatValue4, floatValue5, this.m_4_a(), floatValue6, 104.0F, 24.0F)) {
            this.flag = true;
            this.m_0_void(this.m_1_float(floatValue4));
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean m_6(float floatValue4, float floatValue5, double doubleValue) {
      if (this.m_7_boolean() && isHovered(floatValue4, floatValue5, this.m_4_a(), this.m_4m_float(), 104.0F, 24.0F)) {
         this.m_0_void(this.floatValue + 0.5F * (float)Math.signum(doubleValue));
         writeFile(PATH5, Float.toString(this.floatValue));
         return true;
      } else {
         return false;
      }
   }

   private float m_1_float(float floatValue4) {
      return Math.clamp((floatValue4 - this.m_4_t_float()) / this.m_1_float(), 0.0F, 1.0F) * 5.0F;
   }

   private void m_0_void(float floatValue4) {
      this.floatValue = Math.clamp(floatValue4, 0.0F, 5.0F);
   }

   private void m_1q_void() {
      if (this.animation11.getValue() >= 0.5F) {
         this.value2 = this.value;
      }

      this.value = (this.value + 1) % IDENTIFIER_ARRAY.length;
      this.animation11.reset(0.0F);
      this.animation13.setTarget(this.animation13.updateOrSnap() + 1.0F);
      writeFile(PATH, Integer.toString(this.value));
   }

   private static void writeFile(Path path, String text6) {
      try {
         Files.createDirectories(path.getParent());
         Files.writeString(path, text6, StandardCharsets.UTF_8);
      } catch (Exception e) {
         Onetap.LOGGER.error("[Onetap] Failed to write {}", path, e);
      }
   }

   private static float readFloat(Path path, float floatValue4) {
      try {
         if (Files.exists(path)) {
            return Float.parseFloat(Files.readString(path, StandardCharsets.UTF_8).trim());
         }
      } catch (Exception e) {
         Onetap.LOGGER.error("[Onetap] Failed to read {}", path, e);
      }

      return floatValue4;
   }

   private float m_4_a() {
      return this.titleScreen.width - 10.0F - 104.0F - 24.0F;
   }

   private float m_4m_float() {
      return this.titleScreen.height - 10.0F - 24.0F - 7.0F;
   }

   private float m_8_float() {
      return this.m_4_a() + 104.0F;
   }

   private float m_4_t_float() {
      return this.m_4_a() + 24.0F;
   }

   private float m_1_float() {
      return 70.0F;
   }

   private boolean m_7_boolean() {
      return !this.serverEntry.isVisible() && !this.flag2 && !this.serverEditScreen.isVisible() && !this.changelogPanel.isVisible();
   }

   private void setTitleScreen(TitleScreen titleScreen2) {
      this.titleScreen = titleScreen2;
   }

   private static Minecraft getMinecraft() {
      return Minecraft.getInstance();
   }

   private static boolean isHovered(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9) {
      return floatValue4 >= floatValue6 && floatValue4 <= floatValue6 + floatValue8 && floatValue5 >= floatValue7 && floatValue5 <= floatValue7 + floatValue9;
   }

   private static boolean matchesPattern(String text6) {
      return text6 != null && PATTERN2.matcher(text6).matches();
   }

   private void loadSettings() {
      this.c4List.clear();
      this.value3 = 0;
      boolean flag6 = false;

      try {
         if (Files.exists(PATH2)) {
            JsonObject jsonObject = JsonParser.parseString(Files.readString(PATH2, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!this.flag4 && jsonObject.has("nick")) {
               this.serverListEntry.m_6_void(jsonObject.get("nick").getAsString());
            }

            if (jsonObject.has("accounts")) {
               JsonArray jsonArray = jsonObject.getAsJsonArray("accounts");

               for (int i = 0; i < jsonArray.size(); i++) {
                  String text6 = jsonArray.get(i).getAsString();
                  if (matchesPattern(text6)) {
                     flag6 = true;
                  } else {
                     this.c4List.add(new TitleScreenRenderer.StateHolder2(text6, true, this.value3++));
                  }
               }
            }

            if (jsonObject.has("starred")) {
               JsonArray jsonArray2 = jsonObject.getAsJsonArray("starred");

               for (int i2 = 0; i2 < jsonArray2.size(); i2++) {
                  String text7 = jsonArray2.get(i2).getAsString();
                  if (matchesPattern(text7)) {
                     flag6 = true;
                  } else {
                     for (TitleScreenRenderer.StateHolder2 c4 : this.c4List) {
                        if (c4.text.equalsIgnoreCase(text7)) {
                           c4.flag3 = true;
                        }
                     }
                  }
               }
            }
         } else if (!this.flag4 && Files.exists(PATH4)) {
            this.serverListEntry.m_6_void(Files.readString(PATH4, StandardCharsets.UTF_8).trim());
            if (PATTERN.matcher(this.serverListEntry.getText2()).matches() && !matchesPattern(this.serverListEntry.getText2())) {
               this.c4List.add(new TitleScreenRenderer.StateHolder2(this.serverListEntry.getText2(), true, this.value3++));
            }
         }
      } catch (Exception e) {
         Onetap.LOGGER.error("[Onetap] Failed to read {}, backing it up", PATH2, e);

         try {
            if (Files.exists(PATH2)) {
               Files.move(PATH2, PATH2.resolveSibling("accounts.json.bak"), StandardCopyOption.REPLACE_EXISTING);
            }
         } catch (Exception e2) {
            Onetap.LOGGER.error("[Onetap] Failed to back up {}", PATH2, e2);
         }
      }

      String text8 = getMinecraft().getUser().getName();
      if (PATTERN.matcher(text8).matches() && !matchesPattern(text8) && !this.hasToast(text8)) {
         this.c4List.add(new TitleScreenRenderer.StateHolder2(text8, true, this.value3++));
      }

      if (!this.flag4 && this.serverListEntry.isEmpty() && !matchesPattern(text8)) {
         this.serverListEntry.m_6_void(text8);
      }

      if (flag6) {
         this.saveSettings();
      }
   }

   private void saveSettings() {
      try {
         Files.createDirectories(PATH2.getParent());
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("nick", this.serverListEntry.getText2());
         JsonArray jsonArray = new JsonArray();

         for (TitleScreenRenderer.StateHolder2 c4 : this.c4List) {
            if (!c4.flag2) {
               jsonArray.add(c4.text);
            }
         }

         jsonObject.add("accounts", jsonArray);
         JsonArray jsonArray2 = new JsonArray();

         for (TitleScreenRenderer.StateHolder2 c42 : this.c4List) {
            if (!c42.flag2 && c42.flag3) {
               jsonArray2.add(c42.text);
            }
         }

         jsonObject.add("starred", jsonArray2);
         Path path = PATH2.resolveSibling("accounts.json.tmp");
         Files.writeString(path, jsonObject.toString(), StandardCharsets.UTF_8);
         Files.move(path, PATH2, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (Exception e) {
         Onetap.LOGGER.error("[Onetap] Failed to save {}", PATH2, e);
      }
   }

   static {
      m_1((String[])null);
   }

   public static void m_1(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_8_StringArr() {
      return textArray;
   }

   private static final class StateHolder {
      public final Animation animation;
      public final Animation animation2;
      public final Animation animation3;
      public final Animation animation4;
      public final Animation animation5;
      public final Animation animation6;
      public final Animation animation7;
      public final Animation animation8;
      public final Animation animation9;

      private StateHolder() {
         this.animation = new Animation(140L, 0.0F, Easing.EASING8);
         this.animation2 = new Animation(180L, 0.0F, Easing.EASING8);
         this.animation3 = new Animation(140L, 0.0F, Easing.EASING8);
         this.animation4 = new Animation(140L, 0.0F, Easing.EASING8);
         this.animation5 = new Animation(180L, 0.0F, Easing.EASING8);
         this.animation6 = new Animation(200L, 0.0F, Easing.EASING8);
         this.animation7 = new Animation(140L, 0.0F, Easing.EASING8);
         this.animation8 = new Animation(140L, 0.0F, Easing.EASING8);
         this.animation9 = new Animation(180L, 0.0F, Easing.EASING8);
      }
   }

   private static final class StateHolder2 {
      public final String text;
      public final int value;
      public final Animation animation;
      public final Animation animation2;
      public final Animation animation3;
      public final Animation animation4;
      public final Animation animation5;
      public final Animation animation6;
      public final Animation animation7;
      public final Animation animation8;
      public final Animation animation9;
      public boolean flag;
      public boolean flag2;
      public boolean flag3;

      public StateHolder2(String text6, boolean flag6, int value4) {
         this.animation = new Animation(220L, 0.0F, Easing.EASING8);
         this.animation2 = new Animation(140L, 0.0F, Easing.EASING8);
         ClickGUI.m_3_String();
         this.animation3 = new Animation(180L, 0.0F, Easing.EASING8);
         this.animation4 = new Animation(140L, 0.0F, Easing.EASING8);
         this.animation5 = new Animation(140L, 0.0F, Easing.EASING8);
         this.animation6 = new Animation(180L, 0.0F, Easing.EASING8);
         this.animation7 = new Animation(200L, 0.0F, Easing.EASING8);
         this.animation8 = new Animation(180L, 0.0F, Easing.EASING8);
         this.animation9 = new Animation(260L, 0.0F, Easing.EASING8);
         this.text = text6;
         this.value = value4;
         if (flag6) {
            this.animation.setDuration(1.0F);
         }
      }
   }
}
