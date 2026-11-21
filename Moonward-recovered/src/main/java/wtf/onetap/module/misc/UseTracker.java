package wtf.onetap.module.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.render.Notifications;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "UseTracker", description = "Отслеживает тотемы, зелья и использование еды", category = Category.MISC)
public class UseTracker extends Module {
   private final BooleanSetting totemBreak = new BooleanSetting("Снос тотема", true).build();
   private final BooleanSetting receivedPotions = new BooleanSetting("Полученные зелья", true).build();
   private final BooleanSetting eatenItem = new BooleanSetting("Съеденный предмет", true).build();
   private final BooleanSetting syncWithNotification = new BooleanSetting("Синхронизировать с Notification", true).build();
   private final FloatSetting potionRadius = new FloatSetting("Радиус зелий", 40.0F, 10.0F, 100.0F, 1.0F).visibleWhen(this::isReceivedPotions).build();
   private final Map<Integer, UseTracker.StateHolder> integerMap = new HashMap<>();
   private final Map<UUID, ItemStack> uUIDMap = new HashMap<>();
   private final Map<UUID, Integer> uUIDMap2 = new HashMap<>();
   private final Map<UUID, Long> uUIDMap3 = new HashMap<>();

   @Override
   public void onDisable() {
      this.integerMap.clear();
      this.uUIDMap.clear();
      this.uUIDMap2.clear();
      this.uUIDMap3.clear();
      super.onDisable();
   }

   private boolean isReceivedPotions() {
      return (Boolean)this.receivedPotions.getValue();
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if ((Boolean)this.eatenItem.getValue()) {
            this.m_3_void();
         }

         if ((Boolean)this.receivedPotions.getValue()) {
            this.m_2_void();
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && (Boolean)this.totemBreak.getValue()) {
         if (event.getPacket() instanceof ClientboundEntityEventPacket clientboundEntityEventPacket) {
            if (clientboundEntityEventPacket.getEventId() == 35) {
               if (clientboundEntityEventPacket.getEntity(MinecraftAccess.MINECRAFT.level) instanceof Player player) {
                  if (!player.getUUID().equals(MinecraftAccess.MINECRAFT.player.getUUID())) {
                     long longValue = System.currentTimeMillis();
                     long longValue2 = this.uUIDMap3.getOrDefault(player.getUUID(), 0L);
                     if (longValue - longValue2 >= 200L) {
                        this.uUIDMap3.put(player.getUUID(), longValue);
                        ItemStack itemStack;
                        if (player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) {
                           itemStack = player.getOffhandItem();
                        } else if (player.getMainHandItem().is(Items.TOTEM_OF_UNDYING)) {
                           itemStack = player.getMainHandItem();
                        } else {
                           itemStack = new ItemStack(Items.TOTEM_OF_UNDYING);
                        }

                        Component component = this.getItemName(itemStack);
                        String text = player.getName().getString();
                        MutableComponent mutableComponent = Component.empty()
                           .append(Component.literal("Игрок ").withStyle(ChatFormatting.GRAY))
                           .append(Component.literal(text).withStyle(ChatFormatting.WHITE))
                           .append(Component.literal(" потерял ").withStyle(ChatFormatting.GRAY))
                           .append(component.copy());
                        this.m_3(mutableComponent, itemStack);
                     }
                  }
               }
            }
         }
      }
   }

   private void m_3_void() {
      for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
         if (!player.getUUID().equals(MinecraftAccess.MINECRAFT.player.getUUID())) {
            UUID uUID = player.getUUID();
            if (player.isUsingItem()) {
               if (!this.uUIDMap.containsKey(uUID)) {
                  this.uUIDMap.put(uUID, player.getActiveItem().copy());
                  this.uUIDMap2.put(uUID, player.tickCount);
               }
            } else {
               ItemStack itemStack = this.uUIDMap.remove(uUID);
               Integer integer = this.uUIDMap2.remove(uUID);
               if (itemStack != null && !itemStack.isEmpty() && integer != null && player.tickCount - integer >= 31) {
                  ItemUseAnimation itemUseAnimation = itemStack.getUseAnimation();
                  String text;
                  if (itemUseAnimation == ItemUseAnimation.DRINK) {
                     text = "выпил";
                  } else {
                     if (itemUseAnimation != ItemUseAnimation.EAT) {
                        continue;
                     }

                     text = "съел";
                  }

                  Component component = this.getItemName(itemStack);
                  String text2 = player.getName().getString();
                  MutableComponent mutableComponent = Component.empty()
                     .append(Component.literal("Игрок ").withStyle(ChatFormatting.GRAY))
                     .append(Component.literal(text2).withStyle(ChatFormatting.WHITE))
                     .append(Component.literal(" " + text + " ").withStyle(ChatFormatting.GRAY))
                     .append(component.copy());
                  this.m_3(mutableComponent, itemStack);
               }
            }
         }
      }
   }

   private void m_2_void() {
      UnHook.m_7_IArr();
      HashSet set = new HashSet();
      float floatValue = this.potionRadius.getFloat();

      for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
         if (entity instanceof AbstractThrownPotion abstractThrownPotion && !(MinecraftAccess.MINECRAFT.player.distanceTo(abstractThrownPotion) > floatValue)) {
            int value = abstractThrownPotion.getId();
            set.add(value);
            UseTracker.StateHolder c6 = this.integerMap.get(value);
            if (c6 == null) {
               this.integerMap.put(value, new UseTracker.StateHolder(abstractThrownPotion.getItem().copy(), abstractThrownPotion.getX(), abstractThrownPotion.getY(), abstractThrownPotion.getZ()));
            } else {
               c6.doubleValue = abstractThrownPotion.getX();
               c6.doubleValue2 = abstractThrownPotion.getY();
               c6.doubleValue3 = abstractThrownPotion.getZ();
            }
         }
      }

      HashSet set2 = new HashSet<>(this.integerMap.keySet());
      set2.removeAll(set);

      for (int value2 : (Iterable<Integer>)set2) {
         UseTracker.StateHolder c62 = this.integerMap.remove(value2);
         if (c62 != null) {
            AABB aABB = new AABB(c62.doubleValue - 4.0, c62.doubleValue2 - 2.0, c62.doubleValue3 - 4.0, c62.doubleValue + 4.0, c62.doubleValue2 + 2.0, c62.doubleValue3 + 4.0);
            ArrayList<LivingEntity> livingEntityList = new ArrayList<>();
            MinecraftAccess.MINECRAFT.level.getEntities(EntityTypeTest.forClass(LivingEntity.class), aABB, livingEntity -> true, livingEntityList);

            for (LivingEntity livingEntity2 : livingEntityList) {
               if (livingEntity2 instanceof Player player) {
                  double doubleValue = player.getX() - c62.doubleValue;
                  double doubleValue2 = player.getZ() - c62.doubleValue3;
                  double doubleValue3 = Math.sqrt(doubleValue * doubleValue + doubleValue2 * doubleValue2);
                  if (!(doubleValue3 > 4.0)) {
                     double doubleValue4 = Math.max(0.0, 1.0 - doubleValue3 / 4.0) * 100.0;
                     ChatFormatting chatFormatting = doubleValue4 >= 65.0 ? ChatFormatting.GREEN : (doubleValue4 >= 35.0 ? ChatFormatting.YELLOW : ChatFormatting.RED);
                     Component component = this.getItemName(c62.itemStack);
                     String text = player.getName().getString();
                     MutableComponent mutableComponent = Component.empty()
                        .append(Component.literal("Игрок ").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(text).withStyle(ChatFormatting.WHITE))
                        .append(Component.literal(" получил ").withStyle(ChatFormatting.GRAY))
                        .append(component.copy())
                        .append(Component.literal(" (").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(String.format("%.0f%%", doubleValue4)).withStyle(chatFormatting))
                        .append(Component.literal(")").withStyle(ChatFormatting.GRAY));
                     this.m_3(mutableComponent, c62.itemStack);
                  }
               }
            }
         }
      }
   }

   private void m_3(Component component, ItemStack itemStack) {
      ChatUtil.m_8_void(component);
      if ((Boolean)this.syncWithNotification.getValue()) {
         Notifications notifications = Onetap.MODULE_MANAGER.get(Notifications.class);
         if (notifications != null) {
            notifications.m_2(component, itemStack);
         }
      }
   }

   private Component getItemName(ItemStack itemStack) {
      return itemStack != null && !itemStack.isEmpty() ? itemStack.getHoverName().copy() : Component.literal("предмет").withStyle(ChatFormatting.WHITE);
   }

   private static class StateHolder {
      private final ItemStack itemStack;
      private double doubleValue;
      private double doubleValue2;
      private double doubleValue3;

      private StateHolder(ItemStack itemStack2, double doubleValue4, double doubleValue5, double doubleValue6) {
         this.itemStack = itemStack2;
         this.doubleValue = doubleValue4;
         this.doubleValue2 = doubleValue5;
         this.doubleValue3 = doubleValue6;
      }
   }
}
