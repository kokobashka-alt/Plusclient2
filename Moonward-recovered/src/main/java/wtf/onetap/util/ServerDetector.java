package wtf.onetap.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;

public class ServerDetector implements MinecraftAccess {
   public static final ServerDetector SERVER_DETECTOR = new ServerDetector();
   private static final Pattern PATTERN = Pattern.compile("(?:гриф|grief)\\s*[#№-]?\\s*(\\d+)");
   private static final Pattern PATTERN2 = Pattern.compile("(?:#|№)\\s*(\\d+)");
   private static final Pattern PATTERN3 = Pattern.compile("(?<!\\d)1[.,]16(?!\\d)");
   private final TimeHelper timeHelper;
   private final TimeHelper timeHelper2;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private int value;
   private ServerDetector.DataRecord c0;
   private ServerDetector.ModeKind2 c3;
   private int value2;
   private ContainerScreen containerScreen;
   private ClientLevel clientLevel;
   private ClientLevel clientLevel2;
   private boolean flag4;
   private boolean flag5;
   private ClientLevel clientLevel3;
   private long longValue;
   private long longValue2;
   private volatile int value3;
   private volatile boolean flag6;
   private static int[] valueArray;

   private ServerDetector() {
      this.timeHelper = new TimeHelper();
      this.timeHelper2 = new TimeHelper();
      this.c3 = ServerDetector.ModeKind2.IDLE;
      this.value2 = -1;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public boolean m_9_boolean(int value4) {
      if (!this.m_3(value4, null)) {
         return false;
      }

      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         this.m_7_void();
      }

      if (this.flag2) {
         m_6_boolean();
      }

      return true;
   }

   public boolean m_0_boolean(int value4) {
      return MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.connection != null && this.m_3(value4, null) && this.sendHub();
   }

   public boolean m_7(ServerDetector.DataRecord c02) {
      return MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.connection != null && c02 != null && this.m_3(0, c02) && this.sendHub();
   }

   public void stop() {
      if (this.flag) {
         this.flag = false;
         this.reset();
         this.c0 = null;
         this.c3 = ServerDetector.ModeKind2.IDLE;
         Onetap.EVENT_BUS.unregister(this);
      }
   }

   public boolean m_1(AbstractContainerScreen<?> abstractContainerScreen) {
      return this.flag
         && MinecraftAccess.MINECRAFT.player != null
         && MinecraftAccess.MINECRAFT.level != null
         && MinecraftAccess.MINECRAFT.player.isAlive()
         && !MinecraftAccess.MINECRAFT.player.isRemoved()
         && !this.flag4
         && !this.flag5
         && (!this.flag3 || this.clientLevel2 == MinecraftAccess.MINECRAFT.level)
         && MinecraftAccess.MINECRAFT.player.containerMenu == abstractContainerScreen.getMenu()
         && this.m_3(abstractContainerScreen);
   }

   private boolean m_3(int value4, ServerDetector.DataRecord c02) {
      if (c02 == null ? value4 >= 1 && value4 <= 56 : c02.m_3w() >= 1) {
         if (!this.flag) {
            this.flag = true;
            Onetap.EVENT_BUS.register(this);
         }

         this.reset();
         this.value = value4;
         this.c0 = c02;
         this.c3 = c02 == null ? ServerDetector.ModeKind2.IDLE : ServerDetector.ModeKind2.WAITING_FOR_HUB;
         this.flag2 = true;
         this.flag6 = c02 == null;
         return true;
      } else {
         return false;
      }
   }

   private boolean sendHub() {
      this.flag5 = true;
      this.clientLevel3 = MinecraftAccess.MINECRAFT.level;
      MinecraftAccess.MINECRAFT.player.connection.sendCommand("hub");
      return true;
   }

   private void reset() {
      this.value3++;
      this.flag6 = false;
      this.containerScreen = null;
      this.clientLevel = null;
      this.clientLevel2 = null;
      this.flag5 = false;
      this.clientLevel3 = null;
      this.longValue = 0L;
      this.flag4 = false;
      this.longValue2 = 0L;
      this.timeHelper.reset();
      this.timeHelper2.reset();
      this.flag2 = false;
      this.flag3 = false;
      this.value2 = -1;
   }

   private void m_5(ServerDetector.ModeKind3 c6) {
      long longValue3 = c6 == ServerDetector.ModeKind3.COOLDOWN ? 3000L : 100L;
      this.longValue2 = Math.max(this.longValue2, System.currentTimeMillis() + longValue3);
      this.timeHelper.reset();
      this.timeHelper2.reset();
      if (c6 == ServerDetector.ModeKind3.BUSY) {
         ChatUtil.log("Гриф #" + this.value + ": большой поток, пытаюсь войти — сервер шлёт нахуй.");
      } else if (c6 == ServerDetector.ModeKind3.FULL) {
         ChatUtil.log("Гриф #" + this.value + ": сервер переполнен, пытаюсь войти — сервер шлёт нахуй.");
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      int value4 = this.value3;
      if (this.flag6 && event.getPacket() instanceof ClientboundSystemChatPacket clientboundSystemChatPacket && !clientboundSystemChatPacket.overlay()) {
         ServerDetector.ModeKind3 c6 = m_9_C6(clientboundSystemChatPacket.content().getString());
         if (c6 != null) {
            event.cancel();
            MinecraftAccess.MINECRAFT.execute(() -> {
               if (this.flag && this.value3 == value4) {
                  this.m_5(c6);
               }
            });
         }
      } else if (event.getPacket() instanceof ClientboundLoginPacket || event.getPacket() instanceof ClientboundRespawnPacket) {
         MinecraftAccess.MINECRAFT.execute(() -> {
            if (this.flag && this.value3 == value4) {
               this.flag4 = this.flag3;
               this.containerScreen = null;
               this.clientLevel = null;
               if (!this.flag3) {
                  this.flag5 = false;
                  this.flag2 = true;
                  if (this.c0 != null) {
                     this.c3 = ServerDetector.ModeKind2.WAITING_FOR_HUB;
                  }

                  this.timeHelper2.reset();
               }
            }
         });
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (!this.flag3
            || !this.flag4 && this.clientLevel2 == MinecraftAccess.MINECRAFT.level && (this.c0 == null || System.currentTimeMillis() - this.longValue < 1000L || !this.m_9_boolean())) {
            if (MinecraftAccess.MINECRAFT.player.isAlive() && !MinecraftAccess.MINECRAFT.player.isRemoved()) {
               if (this.flag5) {
                  if (MinecraftAccess.MINECRAFT.level == this.clientLevel3) {
                     return;
                  }

                  this.flag5 = false;
                  this.timeHelper2.reset();
               }

               this.m_7_void();
               if (System.currentTimeMillis() >= this.longValue2) {
                  if (this.flag2) {
                     if (this.timeHelper2.hasElapsed(400L) && m_6_boolean()) {
                        this.value2 = -1;
                        if (this.c0 != null) {
                           this.c3 = ServerDetector.ModeKind2.SELECT_LIGHT;
                        }

                        this.timeHelper2.reset();
                        this.timeHelper.reset();
                     }
                  } else if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen2
                     && MinecraftAccess.MINECRAFT.player.containerMenu instanceof ChestMenu chestMenu
                     && this.timeHelper.hasElapsed(100L)) {
                     if (this.c0 != null) {
                        this.m_4(chestMenu);
                     } else {
                        Slot slot = this.m_8(chestMenu);
                        if (slot != null) {
                           this.m_6_void();
                           this.m_8_void(slot.index);
                           this.timeHelper.reset();
                        } else {
                           boolean flag7 = normalize(containerScreen2.getTitle().getString()).contains("выбор сервера");

                           for (Slot slot2 : chestMenu.slots) {
                              if (slot2.container != MinecraftAccess.MINECRAFT.player.getInventory() && slot2.hasItem()) {
                                 String text = normalize(slot2.getItem().getHoverName().getString());
                                 if (flag7 && slot2.getItem().is(Items.TNT)
                                    || text.contains("гриф выживание (1.21.11)")
                                    || text.contains("следующая страница")) {
                                    this.clickSlot(slot2.index);
                                    this.timeHelper.reset();
                                    return;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         } else {
            this.stop();
         }
      }
   }

   private void m_4(ChestMenu chestMenu) {
      if (this.c3 != ServerDetector.ModeKind2.IDLE && this.c3 != ServerDetector.ModeKind2.WAITING_FOR_HUB && chestMenu.containerId != this.value2) {
         Slot slot = this.m_7(chestMenu, true);
         if (slot == null) {
            slot = this.m_7(chestMenu, false);
         }

         if (slot != null) {
            if (this.c3 == ServerDetector.ModeKind2.SELECT_SERVER) {
               this.m_6_void();
            }

            this.m_8_void(slot.index);
            this.value2 = chestMenu.containerId;
            this.timeHelper.reset();

            this.c3 = switch (this.c3.ordinal()) {
               case 2 -> ServerDetector.ModeKind2.SELECT_CATEGORY;
               case 3 -> ServerDetector.ModeKind2.SELECT_SERVER;
               case 4 -> ServerDetector.ModeKind2.COMPLETE;
               default -> this.c3;
            };
         }
      }
   }

   private void m_6_void() {
      if (!this.flag3) {
         this.clientLevel2 = MinecraftAccess.MINECRAFT.level;
         this.longValue = System.currentTimeMillis();
      }

      this.flag3 = true;
   }

   private boolean m_9_boolean() {
      return this.c0 != null ? this.c0.equals(getCurrent()) : getAnarchyNumber() == this.value;
   }

   private boolean m_3(AbstractContainerScreen<?> abstractContainerScreen) {
      if (!(abstractContainerScreen instanceof ContainerScreen)) {
         return false;
      }

      if (this.c0 != null) {
         return this.c3 != ServerDetector.ModeKind2.IDLE && this.c3 != ServerDetector.ModeKind2.WAITING_FOR_HUB;
      }

      String text = normalize(abstractContainerScreen.getTitle().getString());
      if (!text.contains("выбор сервера") && !text.contains("гриф")) {
         for (Slot slot : abstractContainerScreen.getMenu().slots) {
            if (MinecraftAccess.MINECRAFT.player != null && slot.container != MinecraftAccess.MINECRAFT.player.getInventory() && slot.hasItem()) {
               String text2 = normalize(slot.getItem().getHoverName().getString());
               if (text2.contains("следующая страница") || PATTERN.matcher(text2).find()) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private void m_7_void() {
      if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen2 && containerScreen2.getMenu() == MinecraftAccess.MINECRAFT.player.containerMenu && this.m_3(containerScreen2)) {
         this.containerScreen = containerScreen2;
         this.clientLevel = MinecraftAccess.MINECRAFT.level;
         this.flag2 = false;
      } else if (this.containerScreen != null && this.clientLevel == MinecraftAccess.MINECRAFT.level && MinecraftAccess.MINECRAFT.player.containerMenu == this.containerScreen.getMenu()) {
         MinecraftAccess.MINECRAFT.gui.setScreen(this.containerScreen);
      } else {
         this.containerScreen = null;
         this.clientLevel = null;
         if (!this.flag2) {
            this.flag2 = true;
            this.timeHelper2.reset();
         }
      }
   }

   private Slot m_8(ChestMenu chestMenu) {
      for (Slot slot : chestMenu.slots) {
         if (slot.container != MinecraftAccess.MINECRAFT.player.getInventory() && slot.hasItem() && parseAnarchyNumber(slot.getItem().getHoverName().getString()) == this.value) {
            return slot;
         }
      }

      return null;
   }

   private Slot m_7(ChestMenu chestMenu, boolean flag7) {
      for (Slot slot : chestMenu.slots) {
         if (slot.container != MinecraftAccess.MINECRAFT.player.getInventory() && slot.hasItem()) {
            String text = flag7 ? normalize(slot.getItem().getHoverName().getString()) : m_6_String(slot.getItem());

            boolean flag8 = switch (this.c3.ordinal()) {
               case 2 -> isLite(text) && isAnarchyName(text) && PATTERN3.matcher(text).find();
               case 3 -> this.c0.m_3_q().m_4_boolean(text);
               case 4 -> this.c0.m_3_q().m_4_boolean(text) && m_3_boolean(text, this.c0.m_3w());
               default -> false;
            };
            if (flag8) {
               return slot;
            }
         }
      }

      return null;
   }

   private void m_8_void(int value4) {
      this.clickSlot(value4);
      if (!MinecraftAccess.MINECRAFT.player.containerMenu.getCarried().isEmpty()) {
         this.clickSlot(value4);
      }
   }

   private void clickSlot(int value4) {
      MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(MinecraftAccess.MINECRAFT.player.containerMenu.containerId, value4, 0, ContainerInput.PICKUP, MinecraftAccess.MINECRAFT.player);
   }

   public static boolean m_6_boolean() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         int value4 = InventoryUtil.findHotbarSlot(Items.COMPASS);
         if (value4 == -1) {
            return false;
         }

         if (MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() != value4) {
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value4);
            MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value4));
         }

         MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
         return true;
      } else {
         return false;
      }
   }

   public static int getAnarchyNumber() {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return -1;
      }

      Scoreboard scoreboard = MinecraftAccess.MINECRAFT.level.getScoreboard();

      for (Objective objective : scoreboard.getObjectives()) {
         int value4 = parseAnarchyNumber(objective.getDisplayName().getString());
         if (value4 != -1) {
            return value4;
         }
      }

      Objective objective2 = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
      if (objective2 != null) {
         for (PlayerScoreEntry playerScoreEntry : scoreboard.listPlayerScores(objective2)) {
            MutableComponent mutableComponent = PlayerTeam.formatNameForTeam(scoreboard.getPlayersTeam(playerScoreEntry.owner()), playerScoreEntry.ownerName());
            int value5 = parseAnarchyNumber(mutableComponent.getString());
            if (value5 != -1) {
               return value5;
            }
         }
      }

      return -1;
   }

   public static ServerDetector.DataRecord getCurrent() {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return null;
      }

      Scoreboard scoreboard = MinecraftAccess.MINECRAFT.level.getScoreboard();
      Objective objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
      ServerDetector.DataRecord c02 = m_0(scoreboard, objective);
      if (c02 != null) {
         return c02;
      }

      for (Objective objective2 : scoreboard.getObjectives()) {
         if (objective2 != objective) {
            c02 = m_0(scoreboard, objective2);
            if (c02 != null) {
               return c02;
            }
         }
      }

      return null;
   }

   private static ServerDetector.DataRecord m_0(Scoreboard scoreboard, Objective objective) {
      if (objective == null) {
         return null;
      }

      ServerDetector.DataRecord c02 = parseScoreboardTitle(objective.getDisplayName().getString());
      if (c02 != null) {
         return c02;
      }

      for (PlayerScoreEntry playerScoreEntry : scoreboard.listPlayerScores(objective)) {
         c02 = parseScoreboardTitle(playerScoreEntry.ownerName().getString());
         if (c02 != null) {
            return c02;
         }

         MutableComponent mutableComponent = PlayerTeam.formatNameForTeam(scoreboard.getPlayersTeam(playerScoreEntry.owner()), playerScoreEntry.ownerName());
         c02 = parseScoreboardTitle(mutableComponent.getString());
         if (c02 != null) {
            return c02;
         }

         c02 = parseScoreboardTitle(playerScoreEntry.owner());
         if (c02 != null) {
            return c02;
         }
      }

      return null;
   }

   private static ServerDetector.DataRecord parseScoreboardTitle(String text) {
      String text2 = normalize(text);
      if (!isLite(text2)) {
         return null;
      }

      ServerDetector.ModeKind c2 = ServerDetector.ModeKind.fromName(text2);
      if (c2 == null) {
         return null;
      }

      Matcher matcher = PATTERN2.matcher(text2);
      if (!matcher.find()) {
         return null;
      }

      try {
         return new ServerDetector.DataRecord(c2, Integer.parseInt(matcher.group(1)));
      } catch (NumberFormatException numberFormatException) {
         return null;
      }
   }

   private static int parseAnarchyNumber(String text) {
      Matcher matcher = PATTERN.matcher(normalize(text));
      if (!matcher.find()) {
         return -1;
      }

      try {
         return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException numberFormatException) {
         return -1;
      }
   }

   private static boolean m_3_boolean(String text, int value4) {
      Matcher matcher = PATTERN2.matcher(text);

      while (matcher.find()) {
         try {
            if (Integer.parseInt(matcher.group(1)) == value4) {
               return true;
            }
         } catch (NumberFormatException numberFormatException) {
            return false;
         }
      }

      return false;
   }

   private static ServerDetector.ModeKind3 m_9_C6(String text) {
      String text2 = normalize(text).replace("\\n", " ").strip();
      boolean flag7 = text2.startsWith("не удалось подключиться к серверу newgrief-") || text2.startsWith("не удалось подключиться к серверу grief-");
      if (flag7 && text2.contains("большой поток игроков")) {
         return ServerDetector.ModeKind3.BUSY;
      } else if (flag7 && text2.contains("сервер переполнен")) {
         return ServerDetector.ModeKind3.FULL;
      } else {
         return text2.startsWith("reallyworld") && text2.contains("подождите несколько секунд") && text2.contains("подключением") ? ServerDetector.ModeKind3.COOLDOWN : null;
      }
   }

   private static boolean isLite(String text) {
      return text.contains("лайт") || text.contains("light");
   }

   private static boolean isAnarchyName(String text) {
      return text.contains("анарх") || text.contains("anarch");
   }

   private static String normalize(String text) {
      String text2 = StringUtil.stripColor(text);
      if (text2 == null) {
         text2 = text;
      }

      return text2.toLowerCase(Locale.ROOT).replace('ё', 'е');
   }

   private static String m_6_String(ItemStack itemStack) {
      StringBuilder stringBuilder = new StringBuilder(itemStack.getHoverName().getString());
      ItemLore itemLore = itemStack.getComponents().get(DataComponents.LORE);
      if (itemLore != null) {
         for (Component component : itemLore.lines()) {
            stringBuilder.append(' ').append(component.getString());
         }
      }

      return normalize(stringBuilder.toString());
   }

   static {
      m_6(null);
   }

   public static void m_6(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_3_IArr() {
      return valueArray;
   }

   public record DataRecord(ServerDetector.ModeKind f_3_q, int f_3w) {
      public ServerDetector.ModeKind m_3_q() {
         return this.f_3_q;
      }

      public int m_3w() {
         return this.f_3w;
      }
   }

   public enum ModeKind {
      SOLO("соло", "solo"),
      DUO("дуо", "duo"),
      TRIO("трио", "trio"),
      C2("клан", "clan");

      public final String[] aliases;

      ModeKind(String... stringArray) {
         this.aliases = stringArray;
      }

      public boolean m_4_boolean(String text) {
         ChatUtil.m_3_IArr();

         for (String text2 : this.aliases) {
            if (text.contains(text2)) {
               return true;
            }
         }

         return false;
      }

      public static ServerDetector.ModeKind fromName(String text) {
         for (ServerDetector.ModeKind c2 : values()) {
            if (c2.m_4_boolean(text)) {
               return c2;
            }
         }

         return null;
      }

      private static String a(byte[] byteArray) {
         int value4 = 0;
         int value5;
         char[] charArray = new char[value5 = byteArray.length];

         for (int i = 0; i < value5; i++) {
            int value6;
            if ((value6 = 255 & byteArray[i]) < 192) {
               charArray[value4++] = (char)value6;
            } else if (value6 < 224) {
               char ch = (char)((char)(value6 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value4++] = ch;
            } else if (i < value5 - 2) {
               char ch2 = (char)((char)(value6 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value4++] = ch2;
            }
         }

         return new String(charArray, 0, value4);
      }
   }

   private enum ModeKind2 {
      IDLE,
      WAITING_FOR_HUB,
      SELECT_LIGHT,
      SELECT_CATEGORY,
      SELECT_SERVER,
      COMPLETE;

      private static String a(byte[] byteArray) {
         int value4 = 0;
         int value5;
         char[] charArray = new char[value5 = byteArray.length];

         for (int i = 0; i < value5; i++) {
            int value6;
            if ((value6 = 255 & byteArray[i]) < 192) {
               charArray[value4++] = (char)value6;
            } else if (value6 < 224) {
               char ch = (char)((char)(value6 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value4++] = ch;
            } else if (i < value5 - 2) {
               char ch2 = (char)((char)(value6 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value4++] = ch2;
            }
         }

         return new String(charArray, 0, value4);
      }
   }

   private enum ModeKind3 {
      BUSY,
      FULL,
      COOLDOWN;

      private static String a(byte[] byteArray) {
         int value4 = 0;
         int value5;
         char[] charArray = new char[value5 = byteArray.length];

         for (int i = 0; i < value5; i++) {
            int value6;
            if ((value6 = 255 & byteArray[i]) < 192) {
               charArray[value4++] = (char)value6;
            } else if (value6 < 224) {
               char ch = (char)((char)(value6 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value4++] = ch;
            } else if (i < value5 - 2) {
               char ch2 = (char)((char)(value6 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value4++] = ch2;
            }
         }

         return new String(charArray, 0, value4);
      }
   }
}
