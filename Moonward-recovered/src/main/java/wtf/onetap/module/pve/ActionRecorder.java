package wtf.onetap.module.pve;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.AttackEntityEvent;
import wtf.onetap.event.BlockBreakEvent;
import wtf.onetap.event.BlockPlacedEvent;
import wtf.onetap.event.ContainerClickEvent;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.event.UseItemEvent;
import wtf.onetap.event.UseItemOnEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Action Recorder", description = "Записывает действия для создания PvE-модулей", category = Category.PVE)
public class ActionRecorder extends Module {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
   private final JsonArray jsonArray = new JsonArray();
   private final Map<BlockPos, ActionRecorder.DataRecord> blockPosMap = new LinkedHashMap<>();
   private long longValue;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private float floatValue;
   private float floatValue2;
   private String text;
   private String text2;
   private String text3;
   private BlockPos blockPos;
   private String text4;
   private long longValue2;
   private boolean flag;

   @Override
   public void onEnable() {
      this.jsonArray.asList().clear();
      this.blockPosMap.clear();
      this.longValue = System.currentTimeMillis();
      this.text = null;
      this.text2 = null;
      this.text3 = null;
      this.blockPos = null;
      this.text4 = null;
      this.longValue2 = 0L;
      this.flag = false;
      JsonObject jsonObject = this.m_4_JsonObject("session_started");
      JsonArray jsonArray2 = this.m_0_JsonArray();
      jsonObject.getAsJsonObject("player").add("inventory", jsonArray2);
      this.text3 = jsonArray2.toString();
      ChatUtil.log("§fAction Recorder §7» §7Запись начата. Выключите модуль, чтобы сохранить JSON.");
   }

   @Override
   public void onDisable() {
      this.m_4_JsonObject("session_finished");
      this.m_3_void();
      this.blockPosMap.clear();
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         this.m_1_void();
         this.m_7_void();
         this.m_4_void();
         this.m_2_y_void();
         this.m_2_void();
      }
   }

   @EventTarget
   private void onBlockBreak(BlockBreakEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null) {
         BlockPos blockPos2 = event.getPos();
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2);
         ActionRecorder.DataRecord c7 = this.blockPosMap.get(blockPos2);
         if (c7 == null || !c7.f_4k.equals(blockState)) {
            JsonObject jsonObject = this.m_4_JsonObject("break_block");
            jsonObject.add("target", this.blockToJson(blockPos2, event.getDirection(), null));
            this.m_0(blockPos2, blockState, jsonObject);
         }
      }
   }

   @EventTarget
   private void onUseItemOn(UseItemOnEvent event) {
      JsonObject jsonObject = this.m_4_JsonObject("use_block");
      jsonObject.addProperty("hand", handName(event.getInteractionHand()));
      jsonObject.add("target", this.m_0(event.getBlockHitResult()));
      if (MinecraftAccess.MINECRAFT.level != null) {
         this.m_0(event.getBlockHitResult().getBlockPos(), MinecraftAccess.MINECRAFT.level.getBlockState(event.getBlockHitResult().getBlockPos()), jsonObject);
      }
   }

   @EventTarget
   private void onUseItem(UseItemEvent event) {
      JsonObject jsonObject = this.m_4_JsonObject("use_item");
      jsonObject.addProperty("hand", handName(event.getHand()));
   }

   @EventTarget
   private void onBlockPlaced(BlockPlacedEvent event) {
      long longValue3 = System.currentTimeMillis();
      BlockPos blockPos2 = event.getBlockPos();
      String text5 = event.getItemStack().isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem()).toString();
      if (!blockPos2.equals(this.blockPos) || !text5.equals(this.text4) || longValue3 - this.longValue2 >= 100L) {
         this.blockPos = blockPos2.immutable();
         this.text4 = text5;
         this.longValue2 = longValue3;
         JsonObject jsonObject = this.m_4_JsonObject("placed_block");
         jsonObject.add("item", itemToJson(event.getItemStack()));
         jsonObject.add("target", this.blockToJson(blockPos2, (Direction)null, (Vec3)null));
      }
   }

   @EventTarget
   private void onAttackEntity(AttackEntityEvent event) {
      JsonObject jsonObject = this.m_4_JsonObject("attack_entity");
      jsonObject.add("target", entityToJson(event.getEntity()));
   }

   @EventTarget
   private void onContainerClick(ContainerClickEvent event) {
      JsonObject jsonObject = this.m_4_JsonObject("container_click");
      jsonObject.addProperty("container_id", event.getContainerId());
      jsonObject.addProperty("slot", event.getSlot());
      jsonObject.addProperty("button", event.getButton());
      jsonObject.addProperty("input", event.getInput().name());
      jsonObject.add("container", this.m_6_JsonObject());
   }

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      JsonObject jsonObject = this.m_4_JsonObject("key");
      jsonObject.addProperty("key", event.getKey());
      jsonObject.addProperty("action", event.getAction());
   }

   @EventTarget
   private void onMouseClick(MouseClickEvent event) {
      JsonObject jsonObject = this.m_4_JsonObject("mouse");
      jsonObject.addProperty("button", event.getButton());
      jsonObject.addProperty("action", event.getAction());
      jsonObject.addProperty("x", event.getX());
      jsonObject.addProperty("y", event.getY());
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (event.getPacket() instanceof ServerboundChatCommandPacket serverboundChatCommandPacket) {
         JsonObject jsonObject = this.m_4_JsonObject("command");
         jsonObject.addProperty("command", serverboundChatCommandPacket.command());
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      String text5 = m_0_String(event.getPacket());
      if (text5 != null && !text5.isBlank()) {
         JsonObject jsonObject = this.m_4_JsonObject("chat");
         jsonObject.addProperty("message", text5);
      }
   }

   private void m_1_void() {
      double doubleValue4 = MinecraftAccess.MINECRAFT.player.getX();
      double doubleValue5 = MinecraftAccess.MINECRAFT.player.getY();
      double doubleValue6 = MinecraftAccess.MINECRAFT.player.getZ();
      float floatValue3 = MinecraftAccess.MINECRAFT.player.getYRot();
      float floatValue4 = MinecraftAccess.MINECRAFT.player.getXRot();
      if (!this.flag || doubleValue4 != this.doubleValue || doubleValue5 != this.doubleValue2 || doubleValue6 != this.doubleValue3 || floatValue3 != this.floatValue || floatValue4 != this.floatValue2) {
         JsonObject jsonObject = this.createEvent("movement");
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("x", doubleValue4);
         jsonObject2.addProperty("y", doubleValue5);
         jsonObject2.addProperty("z", doubleValue6);
         jsonObject.add("position", jsonObject2);
         jsonObject.addProperty("yaw", floatValue3);
         jsonObject.addProperty("pitch", floatValue4);
         this.jsonArray.add(jsonObject);
         this.doubleValue = doubleValue4;
         this.doubleValue2 = doubleValue5;
         this.doubleValue3 = doubleValue6;
         this.floatValue = floatValue3;
         this.floatValue2 = floatValue4;
         this.flag = true;
      }
   }

   private void m_7_void() {
      JsonObject jsonObject = this.m_1_JsonObject();
      String text5 = m_1_String(jsonObject);
      if (!text5.equals(this.text)) {
         JsonObject jsonObject2 = this.createEvent("look_target");
         if (jsonObject != null) {
            jsonObject2.add("target", jsonObject);
         }

         this.jsonArray.add(jsonObject2);
         this.text = text5;
      }
   }

   private void m_4_void() {
      String text5 = MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen
         ? containerScreen.getTitle().getString()
         : (MinecraftAccess.MINECRAFT.gui.screen() == null ? "" : MinecraftAccess.MINECRAFT.gui.screen().getClass().getSimpleName());
      if (!text5.equals(this.text2)) {
         JsonObject jsonObject = this.createEvent(text5.isEmpty() ? "screen_closed" : "screen_opened");
         if (!text5.isEmpty()) {
            jsonObject.addProperty("screen", text5);
            jsonObject.add("container", this.m_6_JsonObject());
         }

         this.jsonArray.add(jsonObject);
         this.text2 = text5;
      }
   }

   private void m_2_void() {
      long longValue3 = System.currentTimeMillis();
      var iterator = this.blockPosMap.entrySet().iterator();

      while (iterator.hasNext()) {
         ActionRecorder.DataRecord c7 = (ActionRecorder.DataRecord)((Entry)iterator.next()).getValue();
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(c7.f_9f);
         if (!c7.f_4k.equals(blockState)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("state", stateToString(blockState));
            jsonObject.addProperty("after_ms", longValue3 - this.longValue);
            c7.f_2n.add("result", jsonObject);
            iterator.remove();
         } else if (longValue3 - c7.f_5i > 3000L) {
            c7.f_2n.addProperty("result", "unchanged_after_3s");
            iterator.remove();
         }
      }
   }

   private void m_2_y_void() {
      JsonArray jsonArray2 = this.m_0_JsonArray();
      String text5 = jsonArray2.toString();
      if (!text5.equals(this.text3)) {
         JsonObject jsonObject = this.createEvent("inventory_changed");
         jsonObject.add("inventory", jsonArray2);
         this.jsonArray.add(jsonObject);
         this.text3 = text5;
      }
   }

   private void m_0(BlockPos blockPos2, BlockState blockState, JsonObject jsonObject) {
      this.blockPosMap.put(blockPos2.immutable(), new ActionRecorder.DataRecord(blockPos2.immutable(), blockState, jsonObject, System.currentTimeMillis()));
   }

   private JsonObject m_4_JsonObject(String text5) {
      JsonObject jsonObject = this.createEvent(text5);
      jsonObject.add("player", this.playerToJson());
      JsonObject jsonObject2 = this.m_1_JsonObject();
      if (jsonObject2 != null) {
         jsonObject.add("look_target", jsonObject2);
      }

      this.jsonArray.add(jsonObject);
      return jsonObject;
   }

   private JsonObject createEvent(String text5) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("at_ms", System.currentTimeMillis() - this.longValue);
      jsonObject.addProperty("type", text5);
      return jsonObject;
   }

   private JsonObject playerToJson() {
      JsonObject jsonObject = new JsonObject();
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         jsonObject.addProperty("dimension", MinecraftAccess.MINECRAFT.level.dimension().toString());
         jsonObject.addProperty("x", MinecraftAccess.MINECRAFT.player.getX());
         jsonObject.addProperty("y", MinecraftAccess.MINECRAFT.player.getY());
         jsonObject.addProperty("z", MinecraftAccess.MINECRAFT.player.getZ());
         jsonObject.addProperty("yaw", MinecraftAccess.MINECRAFT.player.getYRot());
         jsonObject.addProperty("pitch", MinecraftAccess.MINECRAFT.player.getXRot());
         jsonObject.addProperty("selected_slot", MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot());
         jsonObject.add("main_hand", itemToJson(MinecraftAccess.MINECRAFT.player.getMainHandItem()));
         jsonObject.add("off_hand", itemToJson(MinecraftAccess.MINECRAFT.player.getOffhandItem()));
         return jsonObject;
      } else {
         return jsonObject;
      }
   }

   private JsonArray m_0_JsonArray() {
      JsonArray jsonArray2 = new JsonArray();
      if (MinecraftAccess.MINECRAFT.player == null) {
         return jsonArray2;
      }

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("slot", i);
            jsonObject.add("item", itemToJson(itemStack));
            jsonArray2.add(jsonObject);
         }
      }

      return jsonArray2;
   }

   private JsonObject m_6_JsonObject() {
      JsonObject jsonObject = new JsonObject();
      if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen) {
         jsonObject.addProperty("id", ((ChestMenu)containerScreen.getMenu()).containerId);
         jsonObject.addProperty("title", containerScreen.getTitle().getString());
         JsonArray jsonArray2 = new JsonArray();

         for (Slot slot : ((ChestMenu)containerScreen.getMenu()).slots) {
            if (slot.hasItem()) {
               JsonObject jsonObject2 = new JsonObject();
               jsonObject2.addProperty("slot", slot.index);
               jsonObject2.add("item", itemToJson(slot.getItem()));
               jsonArray2.add(jsonObject2);
            }
         }

         jsonObject.add("slots", jsonArray2);
         return jsonObject;
      } else {
         return jsonObject;
      }
   }

   private JsonObject m_1_JsonObject() {
      if (MinecraftAccess.MINECRAFT.hitResult instanceof BlockHitResult blockHitResult) {
         return this.m_0(blockHitResult);
      } else {
         return MinecraftAccess.MINECRAFT.hitResult instanceof EntityHitResult entityHitResult ? entityToJson(entityHitResult.getEntity()) : null;
      }
   }

   private JsonObject m_0(BlockHitResult blockHitResult) {
      return this.blockToJson(blockHitResult.getBlockPos(), blockHitResult.getDirection(), blockHitResult.getLocation());
   }

   private JsonObject blockToJson(BlockPos blockPos2, Direction direction, Vec3 vec3) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("kind", "block");
      jsonObject.addProperty("x", blockPos2.getX());
      jsonObject.addProperty("y", blockPos2.getY());
      jsonObject.addProperty("z", blockPos2.getZ());
      if (MinecraftAccess.MINECRAFT.level != null) {
         jsonObject.addProperty("state", stateToString(MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2)));
      }

      if (direction != null) {
         jsonObject.addProperty("face", direction.getSerializedName());
      }

      if (vec3 != null) {
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("x", vec3.x);
         jsonObject2.addProperty("y", vec3.y);
         jsonObject2.addProperty("z", vec3.z);
         jsonObject.add("hit", jsonObject2);
      }

      return jsonObject;
   }

   private static JsonObject entityToJson(Entity entity) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("kind", "entity");
      jsonObject.addProperty("entity_id", entity.getId());
      jsonObject.addProperty("id", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
      jsonObject.addProperty("name", ChatFormatting.stripFormatting(entity.getName().getString()));
      jsonObject.addProperty("x", entity.getX());
      jsonObject.addProperty("y", entity.getY());
      jsonObject.addProperty("z", entity.getZ());
      return jsonObject;
   }

   private static JsonObject itemToJson(ItemStack itemStack) {
      JsonObject jsonObject = new JsonObject();
      if (itemStack != null && !itemStack.isEmpty()) {
         jsonObject.addProperty("id", BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString());
         jsonObject.addProperty("name", ChatFormatting.stripFormatting(itemStack.getHoverName().getString()));
         jsonObject.addProperty("count", itemStack.getCount());
         if (itemStack.isDamageableItem()) {
            jsonObject.addProperty("damage", itemStack.getDamageValue());
            jsonObject.addProperty("max_damage", itemStack.getMaxDamage());
         }

         return jsonObject;
      } else {
         jsonObject.addProperty("empty", true);
         return jsonObject;
      }
   }

   private static String stateToString(BlockState blockState) {
      return blockState.toString();
   }

   private static String m_1_String(JsonObject jsonObject) {
      if (jsonObject == null) {
         return "";
      } else {
         return "block".equals(jsonObject.get("kind").getAsString())
            ? jsonObject.get("x") + ":" + jsonObject.get("y") + ":" + jsonObject.get("z") + ":" + jsonObject.get("state") + ":" + jsonObject.get("face")
            : jsonObject.get("entity_id").toString();
      }
   }

   private static String handName(InteractionHand interactionHand) {
      return interactionHand == InteractionHand.MAIN_HAND ? "main" : "off";
   }

   private static String m_0_String(Packet<?> packet) {
      String text5 = null;
      if (packet instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
         text5 = clientboundSystemChatPacket.content().getString();
      } else if (packet instanceof ClientboundDisguisedChatPacket clientboundDisguisedChatPacket) {
         text5 = clientboundDisguisedChatPacket.message().getString();
      } else if (packet instanceof ClientboundPlayerChatPacket clientboundPlayerChatPacket) {
         text5 = clientboundPlayerChatPacket.unsignedContent() != null ? clientboundPlayerChatPacket.unsignedContent().getString() : clientboundPlayerChatPacket.body().content();
      }

      return text5 == null ? null : ChatFormatting.stripFormatting(text5);
   }

   private void m_3_void() {
      Path path = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("records").resolve("pve_" + DATE_TIME_FORMATTER.format(LocalDateTime.now()) + ".json");
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("format", "onetap-pve-recording-v1");
      jsonObject.addProperty("started_at", Instant.ofEpochMilli(this.longValue).toString());
      jsonObject.addProperty("duration_ms", System.currentTimeMillis() - this.longValue);
      jsonObject.add("events", this.jsonArray);

      try {
         Files.createDirectories(path.getParent());

         try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(jsonObject, bufferedWriter);
         }

         ChatUtil.log("§fAction Recorder §7» §7Запись сохранена: §f" + path.getFileName());
      } catch (IOException e) {
         ChatUtil.log("§fAction Recorder §7» §cНе удалось сохранить запись.");
      }
   }

   private record DataRecord(BlockPos f_9f, BlockState f_4k, JsonObject f_2n, long f_5i) {
      public BlockPos m_9f_BlockPos() {
         return this.f_9f;
      }

      public BlockState m_4k() {
         return this.f_4k;
      }

      public JsonObject m_2n() {
         return this.f_2n;
      }

      public long m_5i() {
         return this.f_5i;
      }
   }
}
