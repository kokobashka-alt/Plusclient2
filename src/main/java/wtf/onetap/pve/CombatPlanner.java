package wtf.onetap.pve;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import ston.common.StonException;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.ConnectedEvent;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.TickEvent;
import wtf.onetap.platform.Diagnostics;
import wtf.onetap.platform.PlatformConnection;
import wtf.onetap.platform.VmBinding;
import wtf.onetap.util.C9_q;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.LatencyStats;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.RateLimiter;

public class CombatPlanner implements MinecraftAccess {
   public static final CombatPlanner COMBAT_PLANNER = new CombatPlanner();
   private final long longValue;
   private final Map<Long, Double> longMap;
   private final ConcurrentLinkedQueue<CombatPlanner.StateHolder2> concurrentLinkedQueue;
   private final ConcurrentLinkedQueue<JsonObject> concurrentLinkedQueue2;
   private final ConcurrentLinkedQueue<JsonObject> concurrentLinkedQueue3;
   private final PathStep pathStep;
   private final Map<String, CombatPlanner.StateHolder> stringMap;
   private final RateLimiter rateLimiter;
   private final AtomicInteger atomicInteger;
   private final AtomicInteger atomicInteger2;
   private final AtomicInteger atomicInteger3;
   private final AtomicInteger atomicInteger4;
   private final AtomicInteger atomicInteger5;
   private final AtomicInteger atomicInteger6;
   private volatile String text;
   private double doubleValue;
   private double doubleValue2;
   private VmBinding.DataRecord c6;
   private Boolean boolean_;
   private final AtomicBoolean atomicBoolean;
   private long longValue2;
   private double doubleValue3;
   private long longValue3;
   private double doubleValue4;
   private double doubleValue5;
   private boolean flag;
   private boolean flag2;
   @Nullable
   private String text2;
   @Nullable
   private JsonObject jsonObject;
   @Nullable
   private CombatPlanner.CallbackType c3;
   @Nullable
   private JsonObject jsonObject2;
   private boolean flag3;
   private boolean flag4;
   private final Map<Integer, Integer> integerMap;
   private boolean flag5;
   private int value;
   private int value2;
   private double doubleValue6;
   private final Map<Long, Double> longMap2;
   private final ArrayDeque<Double> arrayDeque;
   private static final char[] CHAR_VALUE_ARRAY = "0123456789abcdef".toCharArray();
   private static final LatencyStats.StateHolder C0 = LatencyStats.getOrCreate("combat build");
   private static final LatencyStats.StateHolder C02 = LatencyStats.getOrCreate("combat parse");
   private static final LatencyStats.StateHolder C03 = LatencyStats.getOrCreate("combat plans");
   private static final LatencyStats.StateHolder C04 = LatencyStats.getOrCreate("combat apply");
   private static String text3;

   private CombatPlanner() {
      this.longValue = System.nanoTime();
      this.longMap = new ConcurrentHashMap<>();
      this.concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
      this.concurrentLinkedQueue2 = new ConcurrentLinkedQueue<>();
      this.concurrentLinkedQueue3 = new ConcurrentLinkedQueue<>();
      this.pathStep = new PathStep(this);
      this.stringMap = new ConcurrentHashMap<>();
      this.rateLimiter = new RateLimiter();
      this.atomicInteger = new AtomicInteger();
      this.atomicInteger2 = new AtomicInteger();
      this.atomicInteger3 = new AtomicInteger();
      this.atomicInteger4 = new AtomicInteger();
      this.atomicInteger5 = new AtomicInteger();
      this.atomicInteger6 = new AtomicInteger();
      this.doubleValue = Double.NaN;
      this.doubleValue2 = Double.NEGATIVE_INFINITY;
      this.atomicBoolean = new AtomicBoolean();
      this.doubleValue3 = Double.NaN;
      this.doubleValue4 = -1.0;
      this.flag = true;
      this.flag4 = true;
      this.integerMap = new HashMap<>();
      this.doubleValue6 = Double.NEGATIVE_INFINITY;
      this.longMap2 = new HashMap<>();
      this.arrayDeque = new ArrayDeque<>();
   }

   public void m_8_void() {
      Onetap.EVENT_BUS.register(this);
      InventoryTracker.INVENTORY_TRACKER.m_1_void();
   }

   public void m_1_void() {
      this.doubleValue6 = this.elapsedMs();
   }

   private boolean m_9_boolean() {
      return this.c3 == null && this.elapsedMs() - this.doubleValue6 <= 10000.0;
   }

   public double elapsedMs() {
      return (System.nanoTime() - this.longValue) / 1000000.0;
   }

   public long getLongValue2() {
      return this.longValue2;
   }

   public double m_4_double() {
      return Double.isNaN(this.doubleValue3) ? Double.NEGATIVE_INFINITY : this.doubleValue3 + 50.0;
   }

   public double m_3_double() {
      return Math.max(this.doubleValue4, 0.0);
   }

   public double getDoubleValue5() {
      return this.doubleValue5;
   }

   public boolean isOnline() {
      return PlatformConnection.PLATFORM_CONNECTION.isOnline();
   }

   public PathStep getPathStep() {
      return this.pathStep;
   }

   public void setPlan(@Nullable JsonObject jsonObject3) {
      this.jsonObject = jsonObject3;
      if (jsonObject3 == null) {
         this.pathStep.m_8_void();
         this.concurrentLinkedQueue2.clear();
      }
   }

   public void setC3(@Nullable CombatPlanner.CallbackType c32) {
      this.c3 = c32;
   }

   public void m_6(String text4, JsonObject jsonObject3) {
      CombatPlanner.StateHolder c2 = this.stringMap.get(text4);
      if (c2 == null || !c2.jsonObject.equals(jsonObject3)) {
         int value3 = c2 == null ? 1 : c2.value + 1;
         this.stringMap.put(text4, new CombatPlanner.StateHolder(value3, jsonObject3.deepCopy()));
      }
   }

   public void m_5(String text4, long longValue4, boolean flag6) {
      JsonObject jsonObject3 = new JsonObject();
      jsonObject3.addProperty("k", text4);
      jsonObject3.addProperty("id", longValue4);
      jsonObject3.addProperty("ok", flag6);
      this.concurrentLinkedQueue3.add(jsonObject3);
   }

   @EventTarget(priority = EventPriority.HIGHEST)
   private void onTick(TickEvent event) {
      this.doubleValue3 = this.elapsedMs();
      this.longMap2.putIfAbsent(this.longValue2 + 1L, this.doubleValue3);
      this.longMap2.keySet().removeIf(longObj -> longObj < this.longValue2 - 64L);
      this.m_6_void();
      this.integerMap.clear();
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && this.isOnline()) {
         for (LivingEntity livingEntity : MinecraftAccess.MINECRAFT.level.getEntitiesOfClass(LivingEntity.class, MinecraftAccess.MINECRAFT.player.getBoundingBox().inflate(16.0))) {
            this.integerMap.put(livingEntity.getId(), livingEntity.tickCount);
         }
      }
   }

   @EventTarget(priority = EventPriority.HIGHEST)
   private void onConnected(ConnectedEvent event) {
      this.longValue2++;
      this.m_6_void();
   }

   @EventTarget(priority = EventPriority.LOWEST)
   private void onConnected2(ConnectedEvent event) {
      if (this.flag3) {
         this.flag3 = false;
         if (this.flag4) {
            Diagnostics.warn("combat link: the end of the client tick is not reached, sending mid-tick");
         }

         this.flag4 = false;
      }

      if (MinecraftAccess.MINECRAFT.player != null) {
         this.jsonObject2 = EntityState.m_1_JsonObject(MinecraftAccess.MINECRAFT.player);
         this.m_7_void();
         Entity entity = this.pathStep.getEntity();
         Integer integer = entity != null ? this.integerMap.get(entity.getId()) : null;
         this.flag5 = integer != null && entity.tickCount != integer;
         this.flag3 = true;
         if (!this.flag4) {
            this.m_9_void();
         }
      }
   }

   private void m_7_void() {
      if (this.jsonObject == null) {
         if (this.value > 0) {
            this.m_2_void();
         }
      } else {
         this.value++;
         if (!this.pathStep.m_0_boolean()) {
            this.value2++;
         }

         if (this.value >= 200) {
            this.m_2_void();
         }
      }
   }

   private void m_2_void() {
      Diagnostics.info("[KA] {} ticks aiming through the server: {} without a current plan; {}", this.value, this.value2, this.pathStep.m_8_String());
      this.value = 0;
      this.value2 = 0;
   }

   public boolean isFlag4() {
      return this.flag4;
   }

   public void m_3_void() {
      if (this.flag4) {
         this.m_9_void();
      }
   }

   public void m_4_void() {
      if (this.flag4) {
         this.m_9_void();
      }

      this.flag4 = true;
   }

   private void m_9_void() {
      if (this.flag3) {
         this.flag3 = false;
         VmBinding.DataRecord2 c7 = VmBinding.m_5_C7();
         if (c7 != null) {
            this.atomicInteger.incrementAndGet();
         }

         this.m_0_void();
         C9_q.m_7_boolean();
         ArrayList list = new ArrayList();

         C9_q.DataRecord2 c8;
         while ((c8 = C9_q.m_1_C8()) != null) {
            list.add(c8);
         }

         boolean flag6 = this.isOnline();
         if (flag6 != this.flag2) {
            this.flag2 = flag6;
            this.flag = true;
            this.longMap.clear();
            this.arrayDeque.clear();
            this.concurrentLinkedQueue2.clear();
            this.pathStep.m_8_void();
            this.stringMap.values().forEach(CombatPlanner.StateHolder::m_3_void);
            InventoryTracker.INVENTORY_TRACKER.m_5_void();
         }

         if (flag6 && MinecraftAccess.MINECRAFT.player != null) {
            boolean flag7 = this.m_9_boolean();
            if (this.c3 == null && !flag7) {
               InventoryTracker.INVENTORY_TRACKER.m_2_void(this.doubleValue6 == Double.NEGATIVE_INFINITY ? "no fight" : "no target for 10 s");
            }

            if (this.jsonObject == null && this.c3 == null && !flag7 && c7 == null && list.isEmpty()) {
               if (!this.flag) {
                  this.m_3(null, null);
                  this.flag = true;
               }
            } else {
               this.purgeOld();
               TickLossReporter.TICK_LOSS_REPORTER2.m_0_void();
               if (this.longMap.size() >= 16) {
                  TickLossReporter.TICK_LOSS_REPORTER2.m_3_void();
               } else if (this.rateLimiter.m_6_boolean()) {
                  this.m_7(this.jsonObject, this.c3, flag7, c7, list);
               }
            }
         }
      }
   }

   private void m_3(@Nullable JsonObject jsonObject3, @Nullable CombatPlanner.CallbackType c32) {
      this.m_7(jsonObject3, c32, false, null, Collections.emptyList());
   }

   private void m_7(@Nullable JsonObject jsonObject3, @Nullable CombatPlanner.CallbackType c32, boolean flag6, VmBinding.@Nullable DataRecord2 c7, List<C9_q.DataRecord2> c8List) {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      JsonObject jsonObject4 = new JsonObject();
      long longValue4 = ++this.longValue3;
      double doubleValue7 = this.elapsedMs();
      m_4_String();
      jsonObject4.addProperty("seq", longValue4);
      jsonObject4.addProperty("t", this.longValue2);
      jsonObject4.addProperty("ct", doubleValue7);
      if (this.doubleValue3 <= doubleValue7 && this.doubleValue3 > doubleValue7 - 50.0) {
         jsonObject4.addProperty("ts", this.doubleValue3);
      }

      if (this.doubleValue4 >= 0.0) {
         jsonObject4.addProperty("rtt", this.doubleValue4);
         jsonObject4.addProperty("jit", this.doubleValue5);
      }

      Double doubleObj = this.m_0_Double();
      if (doubleObj != null) {
         jsonObject4.addProperty("mg", doubleObj);
      }

      if (this.flag) {
         jsonObject4.addProperty("fresh", true);
      }

      this.flag = false;
      JsonObject jsonObject5 = new JsonObject();
      this.stringMap.forEach((text4, c2) -> {
         if (!c2.flag) {
            jsonObject5.add(text4, c2.m_3_JsonObject());
         }
      });
      if (!jsonObject5.isEmpty()) {
         jsonObject4.add("cfg", jsonObject5);
      }

      jsonObject4.add("me", this.jsonObject2 != null ? this.jsonObject2 : EntityState.m_1_JsonObject(localPlayer));
      this.jsonObject2 = null;
      if (this.flag4) {
         jsonObject4.add("nx", EntityState.m_9(MinecraftAccess.MINECRAFT.player));
      }

      if (jsonObject3 != null) {
         JsonObject jsonObject6 = jsonObject3.deepCopy();
         this.pathStep.m_7_void(jsonObject6);
         if (this.flag4) {
            this.pathStep.m_8(jsonObject6, this.flag5);
         }

         jsonObject4.add("ka", jsonObject6);
      }

      double doubleValue8 = this.elapsedMs();
      if (c32 != null) {
         jsonObject4.add("ca", c32.m_7_JsonObject());
      } else if (flag6) {
         JsonObject jsonObject7 = new JsonObject();
         jsonObject7.addProperty("warm", true);
         InventoryTracker.INVENTORY_TRACKER.m_6_void(jsonObject7, "fight, CrystalAura off");
         if (jsonObject3 == null
            && c7 == null
            && c8List.isEmpty()
            && !jsonObject7.has("w")
            && !jsonObject7.has("inv")
            && !jsonObject4.has("fresh")
            && !jsonObject4.has("cfg")
            && this.concurrentLinkedQueue3.isEmpty()) {
            return;
         }

         jsonObject4.add("ca", jsonObject7);
      }

      double doubleValue9 = this.elapsedMs() - doubleValue8;
      JsonArray jsonArray = new JsonArray();

      JsonObject jsonObject8;
      while ((jsonObject8 = this.concurrentLinkedQueue3.poll()) != null) {
         jsonArray.add(jsonObject8);
      }

      if (!jsonArray.isEmpty()) {
         jsonObject4.add("ev", jsonArray);
      }

      boolean flag7 = c7 != null;
      if (flag7) {
         this.atomicInteger2.incrementAndGet();
         JsonObject jsonObject9 = new JsonObject();
         jsonObject9.addProperty("v", c7.m_4y());
         jsonObject9.addProperty("tick", c7.m_7_a_long());
         JsonArray jsonArray2 = new JsonArray();

         for (int value3 : c7.m_4m_IArr()) {
            jsonArray2.add(value3);
         }

         jsonObject9.add("f", jsonArray2);
         JsonArray jsonArray3 = new JsonArray();

         for (int value4 : c7.m_4_m()) {
            jsonArray3.add(value4);
         }

         jsonObject9.add("o", jsonArray3);
         jsonObject9.addProperty("hd", toHex(c7.m_3_h()));
         jsonObject9.addProperty("sig", toHex(c7.m_3y()));
         jsonObject4.add("n2", jsonObject9);
      }

      boolean flag8 = !c8List.isEmpty();
      if (flag8) {
         JsonArray jsonArray4 = new JsonArray();

         for (C9_q.DataRecord2 c8 : c8List) {
            JsonObject jsonObject10 = new JsonObject();
            jsonObject10.addProperty("v", c8.m_3_y());
            jsonObject10.addProperty("c", c8.m_0_o());
            jsonObject10.addProperty("tick", c8.m_8_s());
            JsonArray jsonArray5 = new JsonArray();

            for (int value5 : c8.m_3k()) {
               jsonArray5.add(value5);
            }

            jsonObject10.add("i", jsonArray5);
            JsonArray jsonArray6 = new JsonArray();

            for (int value6 : c8.m_3x()) {
               jsonArray6.add(value6);
            }

            jsonObject10.add("o", jsonArray6);
            jsonObject10.addProperty("sig", toHex(c8.m_2_n_BArr()));
            jsonArray4.add(jsonObject10);
         }

         jsonObject4.add("cores", jsonArray4);
      }

      byte[] byteArray = VmBinding.m_1_BArr();
      byte[] byteArray2 = C9_q.m_1_BArr();
      if (byteArray != null) {
         jsonObject4.addProperty("n2oc", toHex(byteArray));
      }

      if (byteArray2 != null) {
         jsonObject4.addProperty("cooc", toHex(byteArray2));
      }

      String text5 = jsonObject4.toString();
      double doubleValue10 = this.elapsedMs();
      TickLossReporter.TICK_LOSS_REPORTER2.m_2(doubleValue10 - doubleValue7, doubleValue9, text5.length());
      C0.m_4_void((long)((doubleValue10 - doubleValue7) * 1000000.0));
      this.longMap.put(longValue4, doubleValue7);
      TickLossReporter.TICK_LOSS_REPORTER2.m_7_void();
      long longValue5 = this.longValue2;
      PlatformConnection.PLATFORM_CONNECTION.getStonLink().call("combat.tick", text5, 2000L).whenComplete((text6, e) -> {
         Double doubleObj2 = this.longMap.remove(longValue4);
         if (e != null) {
            Throwable e2 = e.getCause() != null ? e.getCause() : e;
            if (flag7) {
               this.atomicInteger6.incrementAndGet();
               this.text = e2.getClass().getSimpleName() + ": " + e2.getMessage();
            }

            TickLossReporter.TICK_LOSS_REPORTER2.m_2_void(e2.getMessage() != null ? e2.getMessage() : e2.getClass().getSimpleName());
            if (e2 instanceof StonException stonException && "client_outdated".equals(stonException.getReason()) && this.atomicBoolean.compareAndSet(false, true)) {
               MinecraftAccess.MINECRAFT.execute(() -> ChatUtil.log(stonException.getMessage()));
            }

            if (!(e2 instanceof CancellationException)) {
               InventoryTracker.INVENTORY_TRACKER.m_5_void();
            }
         } else if (doubleObj2 != null) {
            double doubleValue11 = this.elapsedMs();
            this.m_8_void(doubleValue11 - doubleObj2);
            TickLossReporter.TICK_LOSS_REPORTER2.addSample(doubleValue11 - doubleObj2);
            long longValue6 = System.nanoTime();
            JsonObject jsonObject11 = JsonParser.parseString(text6).getAsJsonObject();
            C02.recordSince(longValue6);
            JsonElement jsonElement = jsonObject11.get("sv");
            TickLossReporter.TICK_LOSS_REPORTER2.m_4(text6.length(), doubleValue11 - doubleValue10 - (jsonElement != null && jsonElement.isJsonPrimitive() ? jsonElement.getAsDouble() : 0.0));
            JsonElement jsonElement2 = jsonObject11.remove("ka");
            if (jsonElement2 != null && jsonElement2.isJsonObject()) {
               this.concurrentLinkedQueue2.add(jsonElement2.getAsJsonObject());
            }

            JsonElement jsonElement3 = jsonObject11.remove("n2m");
            if (jsonElement3 != null && jsonElement3.isJsonPrimitive()) {
               String text7 = jsonElement3.getAsString();
               if (!text7.contentEquals(this.text2 != null ? this.text2 : "")) {
                  byte[] byteArray3 = fromHex(text7);
                  if (byteArray3 != null && VmBinding.m_1_boolean(byteArray3) && C9_q.m_5_boolean(byteArray3)) {
                     this.text2 = text7;
                  }
               }
            }

            JsonElement jsonElement4 = jsonObject11.remove("coo");
            if (jsonElement4 != null && jsonElement4.isJsonPrimitive()) {
               byte[] byteArray4 = fromHex(jsonElement4.getAsString());
               if (byteArray4 != null) {
                  C9_q.m_1((int)-3836847396805869569L, 0L, byteArray4);
               }
            }

            JsonElement jsonElement5 = jsonObject11.remove("n2o");
            if (jsonElement5 != null && jsonElement5.isJsonPrimitive()) {
               byte[] byteArray5 = fromHex(jsonElement5.getAsString());
               if (byteArray5 != null) {
                  VmBinding.m_7(0L, byteArray5);
               }
            }

            JsonElement jsonElement6 = jsonObject11.remove("n2");
            if (jsonElement6 != null && jsonElement6.isJsonObject()) {
               this.atomicInteger3.incrementAndGet();
               JsonObject jsonObject12 = jsonElement6.getAsJsonObject();
               byte[] byteArray6 = fromHex(jsonObject12.has("sig") ? jsonObject12.get("sig").getAsString() : null);
               if (byteArray6 != null && jsonObject12.has("tick") && VmBinding.m_7(jsonObject12.get("tick").getAsLong(), byteArray6)) {
                  this.atomicInteger4.incrementAndGet();
               }
            } else if (flag7) {
               this.atomicInteger5.incrementAndGet();
            }

            JsonElement jsonElement7 = jsonObject11.remove("n2k");
            if (jsonElement7 != null && jsonElement7.isJsonPrimitive()) {
               byte[] byteArray7 = fromHex(jsonElement7.getAsString());
               if (byteArray7 != null) {
                  VmBinding.m_8_boolean(byteArray7);
               }
            }

            JsonElement jsonElement8 = jsonObject11.remove("cores");
            if (jsonElement8 != null && jsonElement8.isJsonArray()) {
               for (JsonElement jsonElement9 : jsonElement8.getAsJsonArray()) {
                  if (jsonElement9.isJsonObject()) {
                     JsonObject jsonObject13 = jsonElement9.getAsJsonObject();
                     byte[] byteArray8 = fromHex(jsonObject13.has("sig") ? jsonObject13.get("sig").getAsString() : null);
                     if (byteArray8 != null && jsonObject13.has("tick") && jsonObject13.has("c")) {
                        C9_q.m_1(jsonObject13.get("c").getAsInt(), jsonObject13.get("tick").getAsLong(), byteArray8);
                     }
                  }
               }
            }

            this.concurrentLinkedQueue.add(new CombatPlanner.StateHolder2(jsonObject11, longValue5, doubleValue11));
         }
      });
   }

   private void m_0_void() {
      double doubleValue7 = this.elapsedMs();
      if (doubleValue7 - this.doubleValue2 >= 1000.0) {
         this.doubleValue2 = doubleValue7;
         VmBinding.DataRecord c62 = VmBinding.m_3_C6();
         if (c62 != null) {
            if (this.boolean_ != null && c62.m_32() != this.boolean_) {
               Diagnostics.warn(
                  "[Neuro2] {} at step {}: newest verified step {}, any verified: {}, keys: {}",
                  c62.m_32() ? "DEGRADED, its turns are dropped" : "recovered",
                  c62.m_45(),
                  c62.m_4u(),
                  c62.m_34(),
                  c62.m_3_a()
               );
            }

            this.boolean_ = c62.m_32();
         }

         this.c6 = c62;
      }

      VmBinding.DataRecord c63 = this.c6;
      boolean flag6 = Double.isNaN(this.doubleValue);
      if (flag6) {
         this.doubleValue = doubleValue7;
      }

      if (flag6 || !(doubleValue7 - this.doubleValue < 10000.0)) {
         this.doubleValue = doubleValue7;
         int value3 = this.atomicInteger.getAndSet(0);
         int value4 = this.atomicInteger2.getAndSet(0);
         int value5 = this.atomicInteger3.getAndSet(0);
         int value6 = this.atomicInteger4.getAndSet(0);
         int value7 = this.atomicInteger5.getAndSet(0);
         int value8 = this.atomicInteger6.getAndSet(0);
         String text4 = this.text;
         this.text = null;
         if (!flag6) {
            if (value3 != 0 || value4 != 0 || value5 != 0 || value8 != 0) {
               Diagnostics.info(
                  "[Neuro2] last 10 s: {} signed, {} sent, {} answered with the server's signature ({} accepted), {} answered without one, {} failed{}; native: {}",
                  value3,
                  value4,
                  value5,
                  value6,
                  value7,
                  value8,
                  text4 != null ? " (last: " + text4 + ")" : "",
                  c63
               );
            }
         }
      }
   }

   private void m_8_void(double doubleValue7) {
      if (this.doubleValue4 < 0.0) {
         this.doubleValue4 = doubleValue7;
         this.doubleValue5 = doubleValue7 / 2.0;
      } else {
         this.doubleValue5 = this.doubleValue5 * 0.75 + Math.abs(doubleValue7 - this.doubleValue4) * 0.25;
         this.doubleValue4 = this.doubleValue4 * 0.875 + doubleValue7 * 0.125;
      }
   }

   private void purgeOld() {
      double doubleValue7 = this.elapsedMs();
      this.longMap.entrySet().removeIf(entry -> doubleValue7 - entry.getValue() > 2000.0);
   }

   private static String toHex(byte[] byteArray) {
      char[] charArray = new char[byteArray.length * 2];

      for (int i = 0; i < byteArray.length; i++) {
         charArray[i * 2] = CHAR_VALUE_ARRAY[byteArray[i] >> 4 & 15];
         charArray[i * 2 + 1] = CHAR_VALUE_ARRAY[byteArray[i] & 15];
      }

      return new String(charArray);
   }

   @Nullable
   private static byte[] fromHex(@Nullable String text4) {
      if (text4 != null && (text4.length() & 1) == 0) {
         byte[] byteArray = new byte[text4.length() / 2];

         for (int i = 0; i < byteArray.length; i++) {
            int value3 = Character.digit(text4.charAt(i * 2), 16);
            int value4 = Character.digit(text4.charAt(i * 2 + 1), 16);
            if (value3 < 0 || value4 < 0) {
               return null;
            }

            byteArray[i] = (byte)(value3 << 4 | value4);
         }

         return byteArray;
      } else {
         return null;
      }
   }

   private void m_6_void() {
      this.m_3_t_void();
      if (!this.concurrentLinkedQueue.isEmpty()) {
         long longValue4 = System.nanoTime();

         CombatPlanner.StateHolder2 c9;
         while ((c9 = this.concurrentLinkedQueue.poll()) != null) {
            this.m_4(c9);
            this.m_8(c9.jsonObject);
         }

         C04.recordSince(longValue4);
      }
   }

   private void m_4(CombatPlanner.StateHolder2 c9) {
      JsonElement jsonElement = c9.jsonObject.get("sv");
      if (jsonElement != null && jsonElement.isJsonPrimitive()) {
         TickLossReporter.TICK_LOSS_REPORTER2.m_6_void(jsonElement.getAsDouble());
      }

      JsonElement jsonElement2 = c9.jsonObject.get("shed");
      if (jsonElement2 != null && jsonElement2.isJsonPrimitive() && jsonElement2.getAsBoolean()) {
         TickLossReporter.TICK_LOSS_REPORTER2.m_8_void();
      }

      Double doubleObj = this.longMap2.get(c9.longValue + 1L);
      if (doubleObj != null) {
         double doubleValue7 = doubleObj - c9.doubleValue;
         this.arrayDeque.addLast(doubleValue7);

         while (this.arrayDeque.size() > 40) {
            this.arrayDeque.removeFirst();
         }

         TickLossReporter.TICK_LOSS_REPORTER2.m_5_void(doubleValue7);
      }
   }

   @Nullable
   private Double m_0_Double() {
      if (this.arrayDeque.size() < 10) {
         return null;
      }

      double[] doubleArray = this.arrayDeque.stream().mapToDouble(Double::doubleValue).sorted().toArray();
      return doubleArray[(int)Math.floor((doubleArray.length - 1) * 0.3)];
   }

   public void m_3_t_void() {
      if (!this.concurrentLinkedQueue2.isEmpty()) {
         long longValue4 = System.nanoTime();

         JsonObject jsonObject3;
         while ((jsonObject3 = this.concurrentLinkedQueue2.poll()) != null) {
            if (this.jsonObject != null) {
               this.pathStep.m_0_void(jsonObject3);
            }
         }

         C03.recordSince(longValue4);
      }
   }

   private void m_8(JsonObject jsonObject3) {
      if (jsonObject3.has("ack")) {
         for (Entry entry : jsonObject3.getAsJsonObject("ack").entrySet()) {
            CombatPlanner.StateHolder c2 = this.stringMap.get(entry.getKey());
            if (c2 != null && c2.value == ((JsonElement)entry.getValue()).getAsInt()) {
               c2.flag = true;
            }
         }
      }

      if (jsonObject3.has("resync") && jsonObject3.get("resync").getAsBoolean()) {
         this.stringMap.values().forEach(CombatPlanner.StateHolder::m_3_void);
         InventoryTracker.INVENTORY_TRACKER.m_5_void();
         Diagnostics.info("[combat] the server asked for the world again");
      }

      if (jsonObject3.has("ca") && jsonObject3.get("ca").isJsonObject() && this.c3 != null) {
         this.c3.m_7_void(jsonObject3.getAsJsonObject("ca"));
      }

      if (jsonObject3.has("msg")) {
         for (JsonElement jsonElement : jsonObject3.getAsJsonArray("msg")) {
            ChatUtil.log(jsonElement.getAsString());
         }
      }
   }

   static {
      m_3_void("WOglJb");
   }

   public static void m_3_void(String text4) {
      text3 = text4;
   }

   public static String m_4_String() {
      return text3;
   }

   private static final class StateHolder {
      public final int value;
      public final JsonObject jsonObject;
      public volatile boolean flag;

      public StateHolder(int value3, JsonObject jsonObject3) {
         this.value = value3;
         this.jsonObject = jsonObject3;
      }

      public void m_3_void() {
         this.flag = false;
      }

      public JsonObject m_3_JsonObject() {
         JsonObject jsonObject3 = new JsonObject();
         jsonObject3.addProperty("rev", this.value);
         jsonObject3.add("s", this.jsonObject);
         return jsonObject3;
      }

      private static String a(byte[] byteArray) {
         int value3 = 0;
         int value4;
         char[] charArray = new char[value4 = byteArray.length];

         for (int i = 0; i < value4; i++) {
            int value5;
            if ((value5 = 255 & byteArray[i]) < 192) {
               charArray[value3++] = (char)value5;
            } else if (value5 < 224) {
               char ch = (char)((char)(value5 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value3++] = ch;
            } else if (i < value4 - 2) {
               char ch2 = (char)((char)(value5 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value3++] = ch2;
            }
         }

         return new String(charArray, 0, value3);
      }
   }

   public interface CallbackType {
      JsonObject m_7_JsonObject();

      void m_7_void(JsonObject jsonObject3);
   }

   private static final class StateHolder2 {
      public final JsonObject jsonObject;
      public final long longValue;
      public final double doubleValue;

      public StateHolder2(JsonObject jsonObject3, long longValue4, double doubleValue7) {
         this.jsonObject = jsonObject3;
         this.longValue = longValue4;
         this.doubleValue = doubleValue7;
      }
   }
}
