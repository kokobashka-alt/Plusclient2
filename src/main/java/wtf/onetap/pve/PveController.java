package wtf.onetap.pve;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.AttackEntityEvent;
import wtf.onetap.event.BlockBreakEvent;
import wtf.onetap.event.BlockPlacedEvent;
import wtf.onetap.event.BlockStateChangeEvent;
import wtf.onetap.event.ContainerClickEvent;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.event.UseItemEvent;
import wtf.onetap.event.UseItemOnEvent;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.gui.ClickGuiScreen;
import wtf.onetap.mixin.render.BossHealthOverlayAccessor;
import wtf.onetap.module.pve.C4q;
import wtf.onetap.platform.PlatformConnection;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.util.LatencyStats;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.RateLimiter;

public class PveController implements MinecraftAccess {
   private static final LatencyStats.StateHolder C0 = LatencyStats.getOrCreate("pve observe");
   private static final LatencyStats.StateHolder C02 = LatencyStats.getOrCreate("pve apply");
   private final PlatformConnection platformConnection;
   private final PveSnapshot pveSnapshot;
   private final RemoteActionHandler remoteActionHandler;
   private final ConcurrentLinkedQueue<JsonObject> concurrentLinkedQueue;
   private final Map<String, JsonObject> stringMap;
   private volatile JsonObject jsonObject;
   private volatile boolean flag;
   private long longValue;
   private final RateLimiter rateLimiter;
   private long longValue2;
   private long longValue3;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private JsonObject jsonObject2;

   public PveController(PlatformConnection platformConnection2) {
      this.pveSnapshot = new PveSnapshot();
      this.remoteActionHandler = new RemoteActionHandler();
      this.concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
      this.stringMap = new ConcurrentHashMap<>();
      this.rateLimiter = new RateLimiter();
      this.flag2 = true;
      this.jsonObject2 = m_8_JsonObject();
      this.platformConnection = platformConnection2;
   }

   public JsonObject getJson(String text) {
      return this.stringMap.getOrDefault(text, new JsonObject());
   }

   public void enqueue(String text, String text2, String text3, JsonObject jsonObject3) {
      JsonObject jsonObject4 = new JsonObject();
      jsonObject4.addProperty("k", text);
      if (text2 != null) {
         jsonObject4.addProperty("m", text2);
      }

      if (text3 != null) {
         jsonObject4.addProperty("a", text3);
      }

      if (jsonObject3 != null) {
         jsonObject4.add("d", jsonObject3);
      }

      this.concurrentLinkedQueue.add(jsonObject4);
   }

   public void m_6_void() {
      this.remoteActionHandler.reset();
      this.flag = false;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      this.longValue3++;
      this.flag4 = false;
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         JsonObject jsonObject3 = this.jsonObject;
         if (jsonObject3 != null) {
            this.jsonObject = null;
            long longValue4 = System.nanoTime();
            this.m_2(jsonObject3);
            C02.recordSince(longValue4);
         }

         var c4qList = this.platformConnection.getEnabledModules();
         if (!this.platformConnection.isOnline()) {
            if (this.flag3) {
               this.m_6_void();
            }

            this.flag3 = false;
         } else {
            this.remoteActionHandler.m_2_w_void();
            if (c4qList.isEmpty() && !this.flag3) {
               this.concurrentLinkedQueue.clear();
            } else {
               TickLossReporter.TICK_LOSS_REPORTER.m_0_void();
               if (this.flag && System.currentTimeMillis() - this.longValue < 3000L) {
                  TickLossReporter.TICK_LOSS_REPORTER.m_3_void();
               } else if (this.rateLimiter.m_6_boolean()) {
                  this.m_5_void(c4qList);
                  this.flag3 = !c4qList.isEmpty();
               }
            }
         }
      }
   }

   private void m_2(JsonObject jsonObject3) {
      if (jsonObject3.has("ack")) {
         for (Entry entry : jsonObject3.getAsJsonObject("ack").entrySet()) {
            C4q c4q = this.platformConnection.getModule((String)entry.getKey());
            if (c4q != null) {
               c4q.setValue2(((JsonElement)entry.getValue()).getAsInt());
            }
         }
      }

      if (PveSnapshot.getBoolean(jsonObject3, "resync")) {
         this.pveSnapshot.reset();
         this.platformConnection.m_6_Collection().forEach(C4q::m_3_void);
      }

      if (jsonObject3.has("p") && jsonObject3.get("p").isJsonObject()) {
         this.jsonObject2 = jsonObject3.getAsJsonObject("p");
      }

      this.remoteActionHandler.m_6(jsonObject3.getAsJsonArray("a"), this.platformConnection);
      this.remoteActionHandler.m_1_void(jsonObject3.has("i") && jsonObject3.get("i").isJsonObject() ? jsonObject3.getAsJsonObject("i") : null);
      if (jsonObject3.has("st")) {
         for (Entry entry2 : jsonObject3.getAsJsonObject("st").entrySet()) {
            this.stringMap.put((String)entry2.getKey(), ((JsonElement)entry2.getValue()).getAsJsonObject());
         }
      }

      if (this.platformConnection.getEnabledModules().isEmpty()) {
         this.remoteActionHandler.reset();
         this.jsonObject2 = m_8_JsonObject();
      }
   }

   private void m_5_void(List<C4q> c4qList) {
      JsonObject jsonObject3 = new JsonObject();
      jsonObject3.addProperty("seq", ++this.longValue2);
      jsonObject3.addProperty("t", this.longValue3);
      if (this.flag2) {
         jsonObject3.addProperty("fresh", true);
      }

      JsonArray jsonArray = new JsonArray();
      JsonObject jsonObject4 = new JsonObject();

      for (C4q c4q : c4qList) {
         jsonArray.add(c4q.getText());
         JsonObject jsonObject5 = c4q.m_0_JsonObject();
         if (jsonObject5 != null) {
            jsonObject4.add(c4q.getText(), jsonObject5);
         }
      }

      jsonObject3.add("mods", jsonArray);
      if (!jsonObject4.isEmpty()) {
         jsonObject3.add("cfg", jsonObject4);
      }

      JsonArray jsonArray2 = new JsonArray();

      JsonObject jsonObject6;
      while ((jsonObject6 = this.concurrentLinkedQueue.poll()) != null) {
         jsonArray2.add(jsonObject6);
      }

      if (!jsonArray2.isEmpty()) {
         jsonObject3.add("ev", jsonArray2);
      }

      long longValue4 = System.nanoTime();
      jsonObject3.add("o", this.pveSnapshot.m_4_JsonObject(this.jsonObject2));
      C0.recordSince(longValue4);
      this.flag = true;
      long longValue5 = System.currentTimeMillis();
      this.longValue = longValue5;
      TickLossReporter.TICK_LOSS_REPORTER.m_7_void();
      this.platformConnection.getStonLink().call("pve.tick", jsonObject3.toString(), 3000L).whenComplete((text, e) -> {
         if (e == null) {
            TickLossReporter.TICK_LOSS_REPORTER.addSample(System.currentTimeMillis() - longValue5);
            this.jsonObject = JsonParser.parseString(text).getAsJsonObject();
            this.flag2 = false;
         } else {
            TickLossReporter.TICK_LOSS_REPORTER.m_2_void(getErrorMessage(e));
         }

         this.flag = false;
      });
   }

   private static JsonObject m_8_JsonObject() {
      JsonObject jsonObject3 = new JsonObject();
      jsonObject3.addProperty("inv", true);
      jsonObject3.addProperty("scr", true);
      jsonObject3.addProperty("sb", true);
      return jsonObject3;
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (!this.platformConnection.getEnabledModules().isEmpty()) {
         Packet packet = event.getPacket();
         if (!(packet instanceof ClientboundLoginPacket) && !(packet instanceof ClientboundRespawnPacket)) {
            String text = null;
            String text2 = null;
            if (packet instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
               text = "sys";
               text2 = clientboundSystemChatPacket.content().getString();
            } else if (packet instanceof ClientboundDisguisedChatPacket clientboundDisguisedChatPacket) {
               text = "dis";
               text2 = clientboundDisguisedChatPacket.message().getString();
            } else if (packet instanceof ClientboundPlayerChatPacket clientboundPlayerChatPacket) {
               text = "player";
               text2 = clientboundPlayerChatPacket.unsignedContent() != null ? clientboundPlayerChatPacket.unsignedContent().getString() : clientboundPlayerChatPacket.body().content();
            }

            if (text2 != null && !text2.isEmpty()) {
               JsonObject jsonObject3 = new JsonObject();
               jsonObject3.addProperty("k", "chat");
               jsonObject3.addProperty("a", text);
               jsonObject3.addProperty("s", text2);
               this.concurrentLinkedQueue.add(jsonObject3);
            }
         } else {
            this.enqueue("world", null, null, null);
         }
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (this.m_5_boolean()) {
         event.setForward(0.0F);
         event.setStrafe(0.0F);
      } else {
         this.remoteActionHandler.m_5(event);
      }
   }

   private boolean m_5_boolean() {
      return PveSnapshot.getBoolean(this.remoteActionHandler.getJsonObject(), "lock") && MinecraftAccess.MINECRAFT.gui.screen() instanceof AbstractContainerScreen;
   }

   @EventTarget
   private void onBlockStateChange(BlockStateChangeEvent event) {
      if (this.jsonObject2.has("blw") && !this.platformConnection.getEnabledModules().isEmpty()) {
         String text = BuiltInRegistries.BLOCK.getKey(event.getNewState().getBlock()).toString();
         String text2 = BuiltInRegistries.BLOCK.getKey(event.getOldState().getBlock()).toString();
         var stringList = PveSnapshot.toStringList(this.jsonObject2.getAsJsonArray("blw"));
         if (stringList.contains(text) && !stringList.contains(text2)) {
            this.enqueue("blk", null, text, m_9_JsonObject(event.getPos()));
         }
      }
   }

   @EventTarget
   private void onBlockBreak(BlockBreakEvent event) {
      if (this.remoteActionHandler.isNoBreak()) {
         event.cancel();
         if (!this.flag4) {
            this.flag4 = true;
            this.enqueue("guard", null, null, null);
         }
      } else if (this.m_0_boolean() && MinecraftAccess.MINECRAFT.level != null) {
         JsonObject jsonObject3 = m_9_JsonObject(event.getPos());
         jsonObject3.addProperty("face", event.getDirection().getSerializedName());
         jsonObject3.addProperty("state", MinecraftAccess.MINECRAFT.level.getBlockState(event.getPos()).toString());
         this.enqueue("tap", null, "break_block", jsonObject3);
      }
   }

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (!ClickGuiScreen.isEditing() && this.m_5_boolean()) {
         if (event.getKey() == 256 && event.getAction() == 1) {
            this.enqueue("key", null, "256", null);
         }

         event.cancel();
      } else {
         if (event.getAction() == 1 && !ChatInputHelper.isTextInputOpen() && MinecraftAccess.MINECRAFT.player != null) {
            boolean flag5 = false;

            for (C4q c4q : this.platformConnection.getEnabledModules()) {
               for (Entry entry : c4q.buildMap().entrySet()) {
                  int value = (Integer)entry.getValue();
                  if (value != -1 && value == event.getKey()) {
                     flag5 |= this.platformConnection.m_3_boolean(c4q.getText(), (String)entry.getKey());
                  }
               }
            }

            if (flag5) {
               event.cancel();
               return;
            }
         }

         if (this.m_0_boolean()) {
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("key", event.getKey());
            jsonObject3.addProperty("action", event.getAction());
            this.enqueue("tap", null, "key", jsonObject3);
         }
      }
   }

   @EventTarget
   private void onMouseClick(MouseClickEvent event) {
      if (this.m_0_boolean()) {
         JsonObject jsonObject3 = new JsonObject();
         jsonObject3.addProperty("button", event.getButton());
         jsonObject3.addProperty("action", event.getAction());
         jsonObject3.addProperty("x", event.getX());
         jsonObject3.addProperty("y", event.getY());
         this.enqueue("tap", null, "mouse", jsonObject3);
      }
   }

   @EventTarget
   private void onUseItemOn(UseItemOnEvent event) {
      if (this.m_0_boolean() && MinecraftAccess.MINECRAFT.level != null) {
         BlockHitResult blockHitResult = event.getBlockHitResult();
         JsonObject jsonObject3 = m_9_JsonObject(blockHitResult.getBlockPos());
         jsonObject3.addProperty("hand", handName(event.getInteractionHand()));
         jsonObject3.addProperty("face", blockHitResult.getDirection().getSerializedName());
         jsonObject3.addProperty("hx", blockHitResult.getLocation().x);
         jsonObject3.addProperty("hy", blockHitResult.getLocation().y);
         jsonObject3.addProperty("hz", blockHitResult.getLocation().z);
         jsonObject3.addProperty("state", MinecraftAccess.MINECRAFT.level.getBlockState(blockHitResult.getBlockPos()).toString());
         this.enqueue("tap", null, "use_block", jsonObject3);
      }
   }

   @EventTarget
   private void onUseItem(UseItemEvent event) {
      if (this.m_0_boolean()) {
         JsonObject jsonObject3 = new JsonObject();
         jsonObject3.addProperty("hand", handName(event.getHand()));
         this.enqueue("tap", null, "use_item", jsonObject3);
      }
   }

   @EventTarget
   private void onBlockPlaced(BlockPlacedEvent event) {
      if (this.m_0_boolean() && MinecraftAccess.MINECRAFT.level != null) {
         JsonObject jsonObject3 = m_9_JsonObject(event.getBlockPos());
         JsonObject jsonObject4 = new JsonObject();
         if (event.getItemStack().isEmpty()) {
            jsonObject4.addProperty("empty", true);
         } else {
            jsonObject4.addProperty("id", BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem()).toString());
            jsonObject4.addProperty("name", event.getItemStack().getHoverName().getString());
            jsonObject4.addProperty("count", event.getItemStack().getCount());
         }

         jsonObject3.add("item", jsonObject4);
         jsonObject3.addProperty("state", MinecraftAccess.MINECRAFT.level.getBlockState(event.getBlockPos()).toString());
         this.enqueue("tap", null, "placed_block", jsonObject3);
      }
   }

   @EventTarget
   private void onAttackEntity(AttackEntityEvent event) {
      if (this.m_0_boolean()) {
         Entity entity = event.getEntity();
         JsonObject jsonObject3 = new JsonObject();
         jsonObject3.addProperty("kind", "entity");
         jsonObject3.addProperty("entity_id", entity.getId());
         jsonObject3.addProperty("id", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
         jsonObject3.addProperty("name", entity.getName().getString());
         jsonObject3.addProperty("x", entity.getX());
         jsonObject3.addProperty("y", entity.getY());
         jsonObject3.addProperty("z", entity.getZ());
         this.enqueue("tap", null, "attack_entity", jsonObject3);
      }
   }

   @EventTarget
   private void onContainerClick(ContainerClickEvent event) {
      if (this.m_0_boolean()) {
         JsonObject jsonObject3 = new JsonObject();
         jsonObject3.addProperty("container_id", event.getContainerId());
         jsonObject3.addProperty("slot", event.getSlot());
         jsonObject3.addProperty("button", event.getButton());
         jsonObject3.addProperty("input", event.getInput().name());
         this.enqueue("tap", null, "container_click", jsonObject3);
      }
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (this.m_0_boolean() && event.getPacket() instanceof ServerboundChatCommandPacket serverboundChatCommandPacket) {
         JsonObject jsonObject3 = new JsonObject();
         jsonObject3.addProperty("command", serverboundChatCommandPacket.command());
         this.enqueue("tap", null, "command", jsonObject3);
      }
   }

   private boolean m_0_boolean() {
      return PveSnapshot.getBoolean(this.jsonObject2, "taps") && !this.platformConnection.getEnabledModules().isEmpty();
   }

   private static JsonObject m_9_JsonObject(BlockPos blockPos) {
      JsonObject jsonObject3 = new JsonObject();
      jsonObject3.addProperty("x", blockPos.getX());
      jsonObject3.addProperty("y", blockPos.getY());
      jsonObject3.addProperty("z", blockPos.getZ());
      return jsonObject3;
   }

   private static String handName(InteractionHand interactionHand) {
      return interactionHand == InteractionHand.MAIN_HAND ? "main" : "off";
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      JsonObject jsonObject3 = this.remoteActionHandler.getJsonObject();
      if (jsonObject3.has("hud") && jsonObject3.get("hud").isJsonArray() && MinecraftAccess.MINECRAFT.player != null) {
         float floatValue = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth();
         float floatValue2 = Fonts.SUPPLIER14.get().m_1_float(14.0F);
         float floatValue3 = this.getBossBarCount() * 45.0F;

         for (JsonElement jsonElement : jsonObject3.getAsJsonArray("hud")) {
            JsonArray jsonArray = jsonElement.getAsJsonObject().getAsJsonArray("parts");
            float floatValue4 = 0.0F;

            for (JsonElement jsonElement2 : jsonArray) {
               floatValue4 += Draw2D.getTextWidth(jsonElement2.getAsJsonObject().get("t").getAsString(), Fonts.SUPPLIER14, 14.0F);
            }

            float floatValue5 = (floatValue - floatValue4) / 2.0F;

            for (JsonElement jsonElement3 : jsonArray) {
               JsonObject jsonObject4 = jsonElement3.getAsJsonObject();
               String text = jsonObject4.get("t").getAsString();
               Draw2D.drawStyledText(text, Fonts.SUPPLIER14, floatValue5, floatValue3, jsonObject4.get("c").getAsInt(), 14.0F);
               floatValue5 += Draw2D.getTextWidth(text, Fonts.SUPPLIER14, 14.0F);
            }

            floatValue3 += floatValue2;
         }
      }
   }

   private int getBossBarCount() {
      return MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.hud != null ? ((BossHealthOverlayAccessor)MinecraftAccess.MINECRAFT.gui.hud.getBossOverlay()).onetap$getEvents().size() : 0;
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      LatencyStats.StateHolder.m_6_C3_sArr();
      JsonObject jsonObject3 = this.remoteActionHandler.getJsonObject();
      if (jsonObject3.has("vis") && jsonObject3.get("vis").isJsonObject()) {
         JsonObject jsonObject4 = jsonObject3.getAsJsonObject("vis");
         Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
         Vec3 vec3 = event.getVec3();
         if (jsonObject4.has("lines")) {
            ArrayList list = new ArrayList();

            for (JsonElement jsonElement : jsonObject4.getAsJsonArray("lines")) {
               JsonArray jsonArray = jsonElement.getAsJsonArray();
               list.add(new Vec3(jsonArray.get(0).getAsDouble(), jsonArray.get(1).getAsDouble(), jsonArray.get(2).getAsDouble()));
            }

            if (list.size() >= 2) {
               Line3DRenderer.drawLines(matrix4f, list, vec3, 3.0F, jsonObject4.has("lc") ? jsonObject4.get("lc").getAsInt() : -1);
            }
         }

         if (jsonObject4.has("boxes")) {
            for (JsonElement jsonElement2 : jsonObject4.getAsJsonArray("boxes")) {
               JsonObject jsonObject5 = jsonElement2.getAsJsonObject();
               double doubleValue = jsonObject5.get("size").getAsDouble() / 2.0;
               double doubleValue2 = jsonObject5.get("x").getAsDouble();
               double doubleValue3 = jsonObject5.get("y").getAsDouble();
               double doubleValue4 = jsonObject5.get("z").getAsDouble();
               Box3DRenderer.drawBox(
                  matrix4f,
                  (float)(doubleValue2 - doubleValue - vec3.x),
                  (float)(doubleValue3 - doubleValue - vec3.y),
                  (float)(doubleValue4 - doubleValue - vec3.z),
                  (float)(doubleValue2 + doubleValue - vec3.x),
                  (float)(doubleValue3 + doubleValue - vec3.y),
                  (float)(doubleValue4 + doubleValue - vec3.z),
                  jsonObject5.get("c").getAsInt()
               );
            }
         }
      }
   }

   public static String getErrorMessage(Throwable e) {
      Throwable e2 = e.getCause() != null ? e.getCause() : e;
      return e2.getMessage() != null ? e2.getMessage() : e2.getClass().getSimpleName();
   }
}
