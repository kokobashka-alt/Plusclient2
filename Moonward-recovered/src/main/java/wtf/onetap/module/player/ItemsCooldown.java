package wtf.onetap.module.player;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.event.UseItemEvent;
import wtf.onetap.mixin.render.BossHealthOverlayAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "ItemsCooldown", description = "Ставит предметам задержку, чтобы вы их не спамили", category = Category.PLAYER)
public class ItemsCooldown extends Module {
   private final MultiModeSetting delay = new MultiModeSetting("Задержка", "Золотое яблоко", "Золотое яблоко", "Плод хоруса", "Эндер-жемчуг").build();
   private final FloatSetting goldenAppleCooldown = new FloatSetting("Кд золотого яблока", 4.6F, 1.0F, 16.0F, 0.1F).visibleWhen(() -> this.delay.isSelected("Золотое яблоко")).build();
   private final FloatSetting enderPearlCooldown = new FloatSetting("Кд эндер-жемчугов", 13.5F, 1.0F, 16.0F, 0.1F).visibleWhen(() -> this.delay.isSelected("Эндер-жемчуг")).build();
   private final FloatSetting chorusCooldown = new FloatSetting("Кд хоруса", 3.5F, 1.0F, 16.0F, 0.1F).visibleWhen(() -> this.delay.isSelected("Плод хоруса")).build();
   private final BooleanSetting booleanSetting;
   private final Map<Item, Long> itemMap;

   public ItemsCooldown() {
      VoiceChat.m_9_StringArr();
      this.booleanSetting = new BooleanSetting("Только в PVP режиме", false).build();
      this.itemMap = new HashMap<>();
      this.delay.setValue(new LinkedHashSet<>(List.of("Золотое яблоко", "Плод хоруса", "Эндер-жемчуг")));
   }

   public static ItemsCooldown getInstance() {
      return Onetap.MODULE_MANAGER.get(ItemsCooldown.class);
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && this.m_1_boolean()) {
         this.itemMap.entrySet().removeIf(entry -> (float)(System.currentTimeMillis() - entry.getValue()) >= this.m_1_float(entry.getKey()) * 1000.0F);
         if (MinecraftAccess.MINECRAFT.player.isUsingItem() && this.m_9_boolean(MinecraftAccess.MINECRAFT.player.getUseItem().getItem())) {
            MinecraftAccess.MINECRAFT.player.stopUsingItem();
         }
      }
   }

   @EventTarget
   private void onUseItem(UseItemEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && this.m_9_boolean(MinecraftAccess.MINECRAFT.player.getItemInHand(event.getHand()).getItem())) {
         event.cancel();
      }
   }

   public boolean m_9_boolean(Item item) {
      return this.m_1_boolean() && this.m_0_boolean(item) && this.itemMap.containsKey(item);
   }

   public void m_7(ItemStack itemStack, LocalPlayer localPlayer) {
      if (this.m_1_boolean() && this.m_0_boolean(itemStack.getItem())) {
         this.itemMap.put(itemStack.getItem(), System.currentTimeMillis());
         localPlayer.getCooldowns().addCooldown(itemStack, (int)(this.m_1_float(itemStack.getItem()) * 20.0F));
      }
   }

   private boolean m_1_boolean() {
      if (!this.isEnabled()) {
         return false;
      }

      if (!(Boolean)this.booleanSetting.getValue()) {
         return true;
      }

      if (MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.hud != null) {
         Map map = ((BossHealthOverlayAccessor)MinecraftAccess.MINECRAFT.gui.hud.getBossOverlay()).onetap$getEvents();

         for (LerpingBossEvent event : (Iterable<LerpingBossEvent>)map.values()) {
            String text = event.getName().getString().toLowerCase(Locale.ROOT);
            if (text.contains("pvp") || text.contains("пвп") || text.contains("дуэль")) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private float m_1_float(Item item) {
      if (item == Items.GOLDEN_APPLE) {
         return this.goldenAppleCooldown.getFloat();
      } else if (item == Items.ENDER_PEARL) {
         return this.enderPearlCooldown.getFloat();
      } else {
         return item == Items.CHORUS_FRUIT ? this.chorusCooldown.getFloat() : 0.0F;
      }
   }

   private boolean m_0_boolean(Item item) {
      if (item == Items.GOLDEN_APPLE) {
         return this.delay.isSelected("Золотое яблоко");
      } else {
         return item == Items.ENDER_PEARL ? this.delay.isSelected("Эндер-жемчуг") : item == Items.CHORUS_FRUIT && this.delay.isSelected("Плод хоруса");
      }
   }
}
