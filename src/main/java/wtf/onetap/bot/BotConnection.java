package wtf.onetap.bot;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ServerData.Type;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.server.ServerPackManager.PackPromptStatus;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import wtf.onetap.Onetap;
import wtf.onetap.config.ProxyEntry;
import wtf.onetap.config.ProxyState;
import wtf.onetap.event.ConnectedEvent;
import wtf.onetap.mixin.client.MinecraftAccessor;
import wtf.onetap.mixin.render.BossHealthOverlayAccessor;
import wtf.onetap.module.misc.TapeMouse;
import wtf.onetap.util.ChatUtil;

public class BotConnection implements BotManager.CallbackType {
   public static final BotConnection BOT_CONNECTION = new BotConnection();
   private final ConcurrentMap<BotClient, BotConnection.StateHolder> concurrentMap;
   private final ConcurrentMap<Connection, BotClient> concurrentMap2;
   private final Deque<BotConnection.DataRecord> deque;
   private int value;
   private static int[] valueArray;

   public BotConnection() {
      this.concurrentMap = new ConcurrentHashMap<>();
      this.concurrentMap2 = new ConcurrentHashMap<>();
      this.deque = new ArrayDeque<>();
   }

   @Override
   public void m_3(BotClient botClient) {
      Minecraft mc = getMinecraftOrNull();
      if (mc != null) {
         this.registerState(botClient, BotConnection.StateHolder.m_4(mc));
      }
   }

   @Override
   public void resetInput() {
      Minecraft mc = getMinecraftOrNull();
      if (mc != null && mc.player != null) {
         mc.player.input = new ClientInput();
      }
   }

   @Override
   public void m_9(BotClient botClient) {
      Minecraft mc = getMinecraftOrNull();
      if (mc != null) {
         BotConnection.StateHolder c1 = this.concurrentMap.get(botClient);
         if (c1 == null) {
            detach(mc);
            if (botClient.getText() != null) {
               setUser(mc, createOfflineUser(botClient.getText()));
            }

            this.registerState(botClient, BotConnection.StateHolder.m_4(mc));
         } else {
            c1.m_1_void(mc);
         }

         if (mc.gui != null) {
            mc.gui.setScreen(null);
         }
      }
   }

   @Override
   public void m_7(BotClient botClient) {
      BotConnection.StateHolder c1 = this.concurrentMap.remove(botClient);
      this.concurrentMap2.values().removeIf(botClient2 -> botClient2 == botClient);
      if (c1 != null) {
         Connection connection = c1.connection;
         if (connection != null) {
            this.concurrentMap2.remove(connection);
         }

         if (connection != null && (connection.isConnected() || connection.isConnecting())) {
            connection.disconnect(Component.translatable("multiplayer.disconnect.generic"));
         }
      }
   }

   @Override
   public boolean m_8(BotClient botClient, String text) {
      BotConnection.StateHolder c1 = this.concurrentMap.get(botClient);
      if (c1 != null && c1.localPlayer != null && c1.localPlayer.connection != null) {
         if (text.startsWith("/")) {
            c1.localPlayer.connection.sendCommand(text.substring(1));
         } else {
            c1.localPlayer.connection.sendChat(text);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean m_9_boolean() {
      return this.value > 0;
   }

   public void runLocked(Runnable runnable) {
      this.value++;

      try {
         runnable.run();
      } finally {
         this.value--;
      }
   }

   public void m_7_void() {
      if (BotManager.BOT_MANAGER.m_1_boolean()) {
         Onetap.EVENT_BUS.unregister(new ConnectedEvent());
      }
   }

   public boolean m_5(PacketListener packetListener, DisconnectionDetails disconnectionDetails) {
      if (packetListener == null) {
         return false;
      }

      BotClient botClient = this.findClient(packetListener);
      if (botClient == null) {
         return this.m_9_boolean();
      }

      if (BotManager.BOT_MANAGER.isCurrent(botClient)) {
         if (!botClient.m_7_boolean() && !this.m_9_boolean()) {
            BotClient botClient2 = BotManager.BOT_MANAGER.getBotClient();
            BotConnection.StateHolder c1 = botClient2 == null ? null : this.concurrentMap.get(botClient2);
            if (botClient2 != null && botClient2.isFlag() && c1 != null && c1.isActive()) {
               botClient.setFlag(false);
               ChatUtil.log("§cБот отключен: §7" + botClient.getText());
               BotManager.BOT_MANAGER.m_9_boolean();
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         botClient.setFlag(false);
         return true;
      }
   }

   public boolean m_4(PacketListener packetListener) {
      if (packetListener != null && BotManager.BOT_MANAGER.getBotClient() != null) {
         BotClient botClient = this.findClient(packetListener);
         BotClient botClient2 = BotManager.BOT_MANAGER.getBotClient2();
         if (botClient != null) {
            return botClient != botClient2;
         }

         Minecraft mc = getMinecraftOrNull();
         return mc != null && !m_1(mc, packetListener);
      } else {
         return false;
      }
   }

   public void m_5(PacketListener packetListener, ClientboundBossEventPacket clientboundBossEventPacket) {
      BotClient botClient = this.findClient(packetListener);
      if (botClient != null && clientboundBossEventPacket != null) {
         BotConnection.StateHolder c1 = this.concurrentMap.get(botClient);
         Minecraft mc = getMinecraftOrNull();
         if (c1 != null && mc != null && mc.gui != null && mc.gui.hud != null) {
            var map = getBossEventsCopy(mc);
            setBossEvents(mc, c1.uUIDMap);

            try {
               mc.gui.hud.getBossOverlay().update(clientboundBossEventPacket);
               c1.uUIDMap.clear();
               c1.uUIDMap.putAll(getBossEventsCopy(mc));
            } catch (NullPointerException nullPointerException) {
            } finally {
               setBossEvents(mc, map);
            }
         }
      }
   }

   public boolean m_4(PacketListener packetListener, Packet<?> packet, Exception e) {
      BotClient botClient = this.findClient(packetListener);
      boolean flag = botClient != null && BotManager.BOT_MANAGER.isCurrent(botClient) && !this.m_9_boolean();
      return flag ? false : botClient != null || this.m_9_boolean();
   }

   public boolean m_9_boolean(String text) {
      if (text != null && !text.isBlank()) {
         if (BotManager.BOT_MANAGER.findBot(text) != null) {
            return true;
         }

         String text2 = text.toLowerCase(Locale.ROOT);
         BotClient botClient = BotManager.BOT_MANAGER.getBotClient2();
         if (botClient != null && botClient.getText() != null && botClient.getText().equalsIgnoreCase(text)) {
            return true;
         }

         for (Entry entry : this.concurrentMap.entrySet()) {
            if (((BotClient)entry.getKey()).getText() != null && ((BotClient)entry.getKey()).getText().equalsIgnoreCase(text)) {
               return true;
            }

            User user = ((BotConnection.StateHolder)entry.getValue()).user;
            if (user != null && user.getName() != null && text2.equals(user.getName().toLowerCase(Locale.ROOT))) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void m_9(ClientPacketListener clientPacketListener) {
      Minecraft mc = getMinecraftOrNull();
      if (mc != null && mc.player != null && clientPacketListener != null) {
         if (mc.player.connection != clientPacketListener) {
            BotClient botClient = this.findClient(clientPacketListener);
            if (botClient != null || this.m_9_boolean()) {
               mc.player = null;
            }
         }
      }
   }

   @Override
   public void m_8(BotClient botClient) {
      Minecraft mc = getMinecraftOrNull();
      if (mc != null) {
         User user = createOfflineUser(botClient.getText());
         BotConnection.StateHolder c1 = this.concurrentMap.computeIfAbsent(botClient, botClient2 -> new BotConnection.StateHolder());
         c1.user = user;
         ServerAddress serverAddress = ServerAddress.parseString(botClient.getText2());
         ServerData serverData = new ServerData(botClient.getText(), botClient.getText2(), Type.OTHER);
         Thread thread = new Thread(() -> this.m_8(botClient, mc, serverAddress, serverData, user), "Onetap Bot Connector " + botClient.getText());
         thread.setDaemon(true);
         thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(Onetap.LOGGER));
         thread.start();
      }
   }

   @Override
   public void m_0(BotClient botClient) {
      Minecraft mc = getMinecraftOrNull();
      if (mc != null) {
         BotConnection.StateHolder c1 = this.concurrentMap.get(botClient);
         if (c1 != null) {
            Screen screen = mc.gui != null ? mc.gui.screen() : null;
            BotConnection.StateHolder c12 = BotConnection.StateHolder.m_4(mc);
            ClientLevel clientLevel = c1.clientLevel;
            boolean flag = false;

            try {
               this.runLocked(() -> {
                  c1.m_3(mc);
                  m_4(mc, c1);
                  this.m_7(botClient, true);
               });
               flag = mc.level != clientLevel;
            } catch (Throwable e) {
               Onetap.LOGGER.error("Parked bot tick failed: {}", botClient.getText(), e);
               flag = true;
            } finally {
               this.m_2(botClient, mc);
               m_7(mc, c12, screen, flag);
               m_2(botClient);
            }
         }
      }
   }

   @Override
   public void m_4(BotClient botClient) {
      this.m_7(botClient, false);
   }

   private void m_7(BotClient botClient, boolean flag) {
      if (botClient != null && botClient.isFlag2()) {
         Minecraft mc = getMinecraftOrNull();
         if (mc != null && mc.player != null && mc.level != null && mc.gameMode != null) {
            long longValue = System.currentTimeMillis();
            if (botClient.m_5(
               mc.player.getAttackStrengthScale(Float.intBitsToFloat((int)-8310119107991175168L)), mc.player.getCurrentItemAttackStrengthDelay(), longValue
            )) {
               if (flag) {
                  asAccessor(mc).onetap$pick(1.0F);
               }

               TapeMouse.m_3(mc);
            }
         }
      }
   }

   public void m_9(PacketListener packetListener) {
      Minecraft mc = getMinecraftOrNull();
      if (mc != null && packetListener != null && !m_1(mc, packetListener)) {
         BotClient botClient = this.findClient(packetListener);
         if (botClient != null && !BotManager.BOT_MANAGER.isCurrent(botClient)) {
            BotConnection.StateHolder c1 = this.concurrentMap.get(botClient);
            if (c1 == null) {
               this.deque.push(BotConnection.DataRecord.m_8_C2());
            } else {
               Screen screen = mc.gui != null ? mc.gui.screen() : null;
               this.value++;
               this.deque.push(new BotConnection.DataRecord(botClient, BotConnection.StateHolder.m_4(mc), c1.clientLevel, screen));
               c1.m_3(mc);
            }
         } else {
            this.deque.push(BotConnection.DataRecord.m_8_C2());
         }
      } else {
         this.deque.push(BotConnection.DataRecord.m_8_C2());
      }
   }

   public void m_4_void() {
      if (!this.deque.isEmpty()) {
         BotConnection.DataRecord c2 = this.deque.pop();
         if (c2.f_9n != null) {
            this.value--;
            Minecraft mc = getMinecraftOrNull();
            if (mc != null) {
               boolean flag = mc.level != c2.f_6__;
               this.m_2(c2.f_5_n, mc);
               m_7(mc, c2.f_9n, c2.f_87, flag && this.deque.isEmpty());
               m_2(c2.f_5_n);
            }
         }
      }
   }

   private void m_8(BotClient botClient, Minecraft mc, ServerAddress serverAddress, ServerData serverData, User user) {
      m_2_IArr();
      InetSocketAddress inetSocketAddress = null;

      try {
         var optional = ServerNameResolver.DEFAULT.resolveAddress(serverAddress).map(ResolvedServerAddress::asInetSocketAddress);
         if (optional.isEmpty()) {
            mc.execute(() -> {
               ChatUtil.log("§cНе удалось подключиться: §7неизвестный хост");
               botClient.setFlag(false);
            });
            return;
         }

         inetSocketAddress = (InetSocketAddress)optional.get();
         Connection connection = new Connection(PacketFlow.CLIENTBOUND);

         try {
            connection.setBandwidthLogger(mc.getDebugOverlay().getBandwidthLogger());
         } catch (Throwable e) {
         }

         BotConnection.StateHolder c1 = this.concurrentMap.computeIfAbsent(botClient, botClient2 -> new BotConnection.StateHolder());
         c1.user = user;
         c1.connection = connection;
         this.m_6(botClient, connection);
         Connection.connect(inetSocketAddress, EventLoopGroupHolder.remote(mc.options.useNativeTransport()), connection).syncUninterruptibly();
         InetSocketAddress inetSocketAddress2 = inetSocketAddress;
         boolean flag = BotManager.BOT_MANAGER.isCurrent(botClient);
         mc.execute(() -> {
            if (BotManager.BOT_MANAGER.isCurrent(botClient)) {
               asAccessor(mc).setPendingConnection(connection);
            }
         });
         if (flag) {
            try {
               mc.getDownloadedPackSource().configureForServerControl(connection, PackPromptStatus.PENDING);
            } catch (Throwable e2) {
            }
         }

         connection.initiateServerboundPlayConnection(
            inetSocketAddress2.getHostName(),
            inetSocketAddress2.getPort(),
            LoginProtocols.SERVERBOUND,
            LoginProtocols.CLIENTBOUND,
            new ClientHandshakePacketListenerImpl(connection, mc, serverData, null, false, null, component -> {}, new LevelLoadTracker(), null),
            false
         );
         connection.send(new ServerboundHelloPacket(user.getName(), user.getProfileId()));
      } catch (Exception e3) {
         Onetap.LOGGER.error("Bot connect failed", e3);
         String text = inetSocketAddress == null ? botClient.getText2() : inetSocketAddress.toString();
         mc.execute(() -> {
            ChatUtil.log("§cНе удалось подключиться: §7" + text);
            botClient.setFlag(false);
         });
      }
   }

   private static void m_2(BotClient botClient) {
      BotConnection.StateHolder c1 = BOT_CONNECTION.concurrentMap.get(botClient);
      if (c1 != null && c1.localPlayer != null && c1.clientLevel != null) {
         BotManager.BOT_MANAGER.m_5(botClient);
      }
   }

   private void m_2(BotClient botClient, Minecraft mc) {
      if (mc.player != null) {
         mc.player.input = new ClientInput();
      }

      BotConnection.StateHolder c1 = this.concurrentMap.get(botClient);
      BotConnection.StateHolder c12 = BotConnection.StateHolder.m_4(mc);
      if (c12.connection == null && c1 != null) {
         c12.connection = c1.connection;
      }

      if (c12.user == null && c1 != null) {
         c12.user = c1.user;
      }

      if (c1 != null && !BotManager.BOT_MANAGER.isCurrent(botClient)) {
         c12.uUIDMap.clear();
         c12.uUIDMap.putAll(c1.uUIDMap);
      }

      this.registerState(botClient, c12);
      botClient.setFlag(c12.isActive());
   }

   private static void m_7(Minecraft mc, BotConnection.StateHolder c1, Screen screen, boolean flag) {
      if (flag) {
         c1.m_0(mc);
      } else {
         c1.m_3(mc);
      }

      if (mc.gui != null && mc.gui.screen() != screen) {
         mc.gui.setScreen(screen);
      }
   }

   private BotClient findClient(PacketListener packetListener) {
      if (packetListener == null) {
         return null;
      }

      for (Entry entry : this.concurrentMap2.entrySet()) {
         if (((Connection)entry.getKey()).getPacketListener() == packetListener) {
            return (BotClient)entry.getValue();
         }
      }

      for (Entry entry2 : this.concurrentMap.entrySet()) {
         BotConnection.StateHolder c1 = (BotConnection.StateHolder)entry2.getValue();
         if (c1.connection != null && c1.connection.getPacketListener() == packetListener) {
            return (BotClient)entry2.getKey();
         }

         if (c1.localPlayer != null && c1.localPlayer.connection == packetListener) {
            return (BotClient)entry2.getKey();
         }
      }

      return null;
   }

   private void registerState(BotClient botClient, BotConnection.StateHolder c1) {
      this.concurrentMap.put(botClient, c1);
      this.m_6(botClient, c1.connection);
   }

   public ProxyInfo getProxyInfo(Connection connection) {
      if (connection == null) {
         return null;
      }

      BotClient botClient = this.concurrentMap2.get(connection);
      return botClient == null ? null : botClient.getProxyInfo();
   }

   public void m_6(ChannelPipeline channelPipeline, Connection connection) {
      if (channelPipeline != null && connection != null) {
         if (connection.getReceiving() == PacketFlow.CLIENTBOUND) {
            Channel channel = channelPipeline.channel();
            if (!(channel instanceof LocalChannel) && !(channel instanceof LocalServerChannel)) {
               ProxyInfo proxyInfo = this.getProxyInfo(connection);
               if (proxyInfo != null) {
                  channelPipeline.addFirst(new ChannelHandler[]{proxyInfo.m_9_ProxyHandler()});
               } else {
                  ProxyEntry proxyEntry = ProxyState.proxyEntry;
                  if (proxyEntry != null && ProxyState.flag) {
                     InetSocketAddress inetSocketAddress = new InetSocketAddress(proxyEntry.getHost(), proxyEntry.m_4_int());
                     String text = proxyEntry.text2 != null && !proxyEntry.text2.isEmpty() ? proxyEntry.text2 : null;
                     String text2 = proxyEntry.text3 != null && !proxyEntry.text3.isEmpty() ? proxyEntry.text3 : null;
                     if (proxyEntry.c6 == ProxyEntry.ModeKind.SOCKS4) {
                        channelPipeline.addFirst(new ChannelHandler[]{new Socks4ProxyHandler(inetSocketAddress, text)});
                     } else {
                        channelPipeline.addFirst(new ChannelHandler[]{new Socks5ProxyHandler(inetSocketAddress, text, text2)});
                     }

                     ProxyState.proxyEntry2 = proxyEntry;
                  }
               }
            }
         }
      }
   }

   public void m_6(BotClient botClient, Connection connection) {
      if (botClient != null && connection != null) {
         this.concurrentMap2.values().removeIf(botClient2 -> botClient2 == botClient);
         this.concurrentMap2.put(connection, botClient);
      }
   }

   private static boolean m_1(Minecraft mc, PacketListener packetListener) {
      if (mc.player != null && mc.player.connection == packetListener) {
         return true;
      }

      Connection connection = asAccessor(mc).getPendingConnection();
      return connection != null && connection.getPacketListener() == packetListener;
   }

   private static void m_4(Minecraft mc, BotConnection.StateHolder c1) {
      Connection connection = c1.connection;
      if (mc.level != null && mc.player != null) {
         mc.level.tickRateManager().tick();
         if (mc.gameMode != null) {
            mc.gameMode.tick();
         } else if (connection != null) {
            tickConnection(connection);
         }

         mc.level.tickEntities();
         mc.level.tickBlockEntities();
         mc.level.tick(() -> true);
         if (mc.player.connection != null) {
            mc.player.connection.send(ServerboundClientTickEndPacket.INSTANCE);
         }
      } else {
         if (connection != null) {
            tickConnection(connection);
         }
      }
   }

   private static void tickConnection(Connection connection) {
      if (connection.isConnected()) {
         connection.tick();
      } else if (connection.isConnecting()) {
         connection.tick();
      } else {
         connection.handleDisconnection();
      }
   }

   private static void detach(Minecraft mc) {
      mc.player = null;
      mc.gameMode = null;
      mc.level = null;
      asAccessor(mc).onetap$updateLevelInEngines(null, false);
      asAccessor(mc).setPendingConnection(null);
      mc.hitResult = null;
      mc.crosshairPickEntity = null;
      setBossEvents(mc, Map.of());
      if (mc.gui != null && mc.gui.screen() != null) {
         mc.gui.setScreen(null);
      }
   }

   private static Map<UUID, LerpingBossEvent> getBossEventsCopy(Minecraft mc) {
      var map = getBossEvents(mc);
      return map == null ? new HashMap<>() : new HashMap<>(map);
   }

   private static void setBossEvents(Minecraft mc, Map<UUID, LerpingBossEvent> map) {
      var map2 = getBossEvents(mc);
      if (map2 != null) {
         map2.clear();
         if (map != null) {
            map2.putAll(map);
         }
      }
   }

   private static Map<UUID, LerpingBossEvent> getBossEvents(Minecraft mc) {
      if (mc.gui != null && mc.gui.hud != null) {
         try {
            return ((BossHealthOverlayAccessor)mc.gui.hud.getBossOverlay()).onetap$getEvents();
         } catch (Throwable e) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static void setUser(Minecraft mc, User user) {
      asAccessor(mc).setUser(user);
   }

   private static User createOfflineUser(String text) {
      UUID uUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + text).getBytes(StandardCharsets.UTF_8));
      return new User(text, uUID, "0", Optional.empty(), Optional.empty());
   }

   private static MinecraftAccessor asAccessor(Minecraft mc) {
      return (MinecraftAccessor)mc;
   }

   private static Minecraft getMinecraftOrNull() {
      try {
         return Minecraft.getInstance();
      } catch (Throwable e) {
         return null;
      }
   }

   static {
      m_8(new int[2]);
   }

   public static void m_8(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_2_IArr() {
      return valueArray;
   }

   private static final class StateHolder {
      public User user;
      public LocalPlayer localPlayer;
      public ClientLevel clientLevel;
      public MultiPlayerGameMode multiPlayerGameMode;
      public volatile Connection connection;
      public Entity entity;
      public HitResult hitResult;
      public Entity entity2;
      public final Map<UUID, LerpingBossEvent> uUIDMap;

      private StateHolder() {
         this.uUIDMap = new HashMap<>();
      }

      public static BotConnection.StateHolder m_4(Minecraft mc) {
         BotConnection.StateHolder c1 = new BotConnection.StateHolder();
         c1.user = mc.getUser();
         c1.localPlayer = mc.player;
         c1.clientLevel = mc.level;
         c1.multiPlayerGameMode = mc.gameMode;
         Connection connection2 = BotConnection.asAccessor(mc).getPendingConnection();
         if (mc.player != null && mc.player.connection != null) {
            c1.connection = mc.player.connection.getConnection();
         } else {
            c1.connection = connection2;
         }

         try {
            c1.entity = mc.getCameraEntity();
         } catch (Throwable e) {
         }

         c1.hitResult = mc.hitResult;
         c1.entity2 = mc.crosshairPickEntity;
         c1.uUIDMap.putAll(BotConnection.getBossEventsCopy(mc));
         return c1;
      }

      public void m_3(Minecraft mc) {
         if (this.user != null) {
            BotConnection.setUser(mc, this.user);
         }

         mc.player = this.localPlayer;
         mc.level = this.clientLevel;
         mc.gameMode = this.multiPlayerGameMode;
         BotConnection.asAccessor(mc).setPendingConnection(this.localPlayer == null ? this.connection : null);
         mc.hitResult = this.hitResult;
         mc.crosshairPickEntity = this.entity2;

         try {
            if (this.localPlayer != null) {
               mc.setCameraEntity(this.localPlayer);
            } else if (this.entity != null) {
               mc.setCameraEntity(this.entity);
            }
         } catch (Throwable e) {
         }
      }

      public void m_1_void(Minecraft mc) {
         this.m_5(mc, true);
         if (this.localPlayer != null) {
            this.localPlayer.input = new KeyboardInput(mc.options);
         }
      }

      public void m_0(Minecraft mc) {
         this.m_5(mc, false);
      }

      private void m_5(Minecraft mc, boolean flag) {
         Connection connection2 = this.localPlayer == null ? this.connection : null;
         if (this.user != null) {
            BotConnection.setUser(mc, this.user);
         }

         mc.player = this.localPlayer;
         mc.gameMode = this.multiPlayerGameMode;
         mc.level = this.clientLevel;
         BotConnection.asAccessor(mc).onetap$updateLevelInEngines(this.clientLevel, false);
         BotConnection.asAccessor(mc).setPendingConnection(connection2);
         mc.hitResult = this.hitResult;
         mc.crosshairPickEntity = this.entity2;
         BotConnection.setBossEvents(mc, this.uUIDMap);

         try {
            if (this.localPlayer != null) {
               mc.setCameraEntity(this.localPlayer);
            } else if (this.entity != null) {
               mc.setCameraEntity(this.entity);
            } else if (flag) {
               mc.setCameraEntity(null);
            }
         } catch (Throwable e) {
         }
      }

      public boolean isActive() {
         return this.connection == null || !this.connection.isConnected() && !this.connection.isConnecting() ? this.localPlayer != null : true;
      }
   }

   private record DataRecord(BotClient f_5_n, BotConnection.StateHolder f_9n, ClientLevel f_6__, Screen f_87) {
      public static BotConnection.DataRecord m_8_C2() {
         return new BotConnection.DataRecord(null, null, null, null);
      }

      public BotClient m_5_n() {
         return this.f_5_n;
      }

      public BotConnection.StateHolder m_9n_C1() {
         return this.f_9n;
      }

      public ClientLevel m_6__() {
         return this.f_6__;
      }

      public Screen m_87() {
         return this.f_87;
      }
   }
}
