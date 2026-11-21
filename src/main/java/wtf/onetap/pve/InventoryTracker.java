package wtf.onetap.pve;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.platform.Diagnostics;
import wtf.onetap.util.MinecraftAccess;

public class InventoryTracker implements MinecraftAccess {
   public static final InventoryTracker INVENTORY_TRACKER = new InventoryTracker();
   private final BlockPlan blockPlan;
   private volatile boolean flag;
   private volatile boolean flag2;
   private int value;
   private double doubleValue;
   private long longValue;
   private int value2;
   private int value3;
   private String text;

   private InventoryTracker() {
      this.blockPlan = new BlockPlan();
      this.flag2 = true;
      this.value = Integer.MIN_VALUE;
      this.doubleValue = Double.NaN;
      this.text = "";
   }

   public void m_1_void() {
      Onetap.EVENT_BUS.register(this);
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (this.flag) {
         this.blockPlan.m_2(event.getPacket());
      }
   }

   public void m_7_void(BlockPos blockPos) {
      if (this.flag) {
         this.blockPlan.m_0_void(blockPos);
      }
   }

   public void m_5_void() {
      this.flag2 = true;
   }

   public boolean m_2_boolean() {
      return this.flag && !this.flag2 && this.blockPlan.getValue3() == 0;
   }

   public int m_1_int() {
      return this.flag && !this.flag2 ? this.blockPlan.getValue3() : -1;
   }

   public void m_6_void(JsonObject jsonObject, String text2) {
      if (!this.flag || this.flag2) {
         this.blockPlan.m_6_void();
         this.value = Integer.MIN_VALUE;
         this.doubleValue = CombatPlanner.COMBAT_PLANNER.elapsedMs();
         this.longValue = CombatPlanner.COMBAT_PLANNER.getLongValue2();
         this.value2 = 0;
         this.value3 = 0;
         this.text = this.flag2 && this.flag ? "server lost it" : text2;
      }

      this.flag = true;
      this.flag2 = false;
      JsonArray jsonArray = this.m_0_JsonArray();
      int value4 = jsonArray.hashCode();
      JsonObject jsonObject2 = this.blockPlan.m_5_JsonObject(value4);
      if (jsonObject2 != null) {
         jsonObject.add("w", jsonObject2);
      }

      jsonObject.addProperty("invh", value4);
      if (value4 != this.value) {
         jsonObject.add("inv", jsonArray);
         this.value = value4;
      }

      if (!Double.isNaN(this.doubleValue)) {
         this.value2 = this.value2 + this.blockPlan.getValue4();
         this.value3 = this.value3 + this.blockPlan.getValue5();
         if (this.blockPlan.getValue3() == 0) {
            Diagnostics.info(
               "[combat] world on the server ({}): {} bricks, {} KB in {} tick(s)", this.text, this.value2, this.value3 / 1024, CombatPlanner.COMBAT_PLANNER.getLongValue2() - this.longValue + 1L
            );
            this.doubleValue = Double.NaN;
         }
      }
   }

   public void m_2_void(String text2) {
      if (this.flag) {
         this.flag = false;
         this.doubleValue = Double.NaN;
         this.blockPlan.m_6_void();
         Diagnostics.info("[combat] world no longer sent ({})", text2);
      }
   }

   private JsonArray m_0_JsonArray() {
      JsonArray jsonArray = new JsonArray();

      for (int i = 0; i <= 40; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack.isEmpty()) {
            jsonArray.add(JsonNull.INSTANCE);
         } else {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("i", BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString());
            jsonObject.addProperty("c", itemStack.getCount());
            if (itemStack.getUseAnimation() != ItemUseAnimation.NONE) {
               jsonObject.addProperty("u", false);
            }

            if (itemStack.get(DataComponents.EQUIPPABLE) != null) {
               jsonObject.addProperty("eq", true);
            }

            if (itemStack.is(ItemTags.SWORDS)) {
               jsonObject.addProperty("sw", true);
            }

            jsonArray.add(jsonObject);
         }
      }

      return jsonArray;
   }
}
