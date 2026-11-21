package wtf.onetap.module.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ModuleToggleEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.hud.HudStylePanel;
import wtf.onetap.gui.widget.TextField;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.module.player.StaffExploit;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.FontAtlas;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.TextRenderer;
import wtf.onetap.render.Theme;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.ChatBadges;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Notifications", description = "Показывает уведомления о модулях, спеке и друзьях", category = Category.RENDER)
public class Notifications extends Module {
   private static final Pattern PATTERN = Pattern.compile("(?iu)(?:^|[^\\p{L}\\p{N}_])(?:s+p+e+(?:c|k)+|с+п+е+к+)(?:\\s+([\\w.\\-]+))?(?:$|[^\\p{L}\\p{N}_])");
   private final List<Notifications.StateHolder> c3List = new ArrayList<>();
   private final List<Notifications.StateHolder> c3List2 = new ArrayList<>();
   private final Map<UUID, Notifications.DataRecord> uUIDMap = new HashMap<>();
   private final Set<UUID> uUIDSet = new HashSet<>();
   private static final Set<Holder<MobEffect>> HOLDER_SET = Set.of(
      MobEffects.STRENGTH, MobEffects.SPEED, MobEffects.FIRE_RESISTANCE, MobEffects.ABSORPTION, MobEffects.HEALTH_BOOST
   );
   private final Map<Holder<MobEffect>, Boolean> holderMap = new HashMap<>();
   private final Animation animation = new Animation(250L, 0.0F, Easing.EASING8);
   private final Animation animation2 = new Animation(150L, 0.0F, Easing.EASING8);
   private final Theme theme = new Theme("Notifications", ColorUtil.rgba(0, 0, 0, 0), Theme.VALUE3, ColorUtil.rgba(155, 155, 155, 255));
   private final TextField textField = new TextField("Notifications", 0.0F, 0.0F);
   private final HudStylePanel hudStylePanel = HudStylePanel.m_7(this.textField, this.theme, "Уведомления");
   private Notifications.StateHolder c3;
   private long longValue;
   private final BooleanSetting spectate = new BooleanSetting("Спек", true).build();
   private final BooleanSetting lowFriendHealth = new BooleanSetting("Низкое хп друга", true).build();
   private final BooleanSetting effectLoss = new BooleanSetting("Потеря эффектов", true).build();
   private final BooleanSetting pickUniqueItems = new BooleanSetting("Подбор уникальных предметов", true).build();
   private static Module[] moduleArray;

   @EventTarget
   private void onModuleToggle(ModuleToggleEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         long longValue2 = System.currentTimeMillis();
         Optional optional = this.c3List
            .stream()
            .filter(c32 -> c32.itemStack == null && c32.text2 == null && c32.text != null && c32.text.equals(event.getModuleName()))
            .findFirst();
         if (optional.isPresent()) {
            Notifications.StateHolder c33 = (Notifications.StateHolder)optional.get();
            c33.m_6(event.isEnabled(), longValue2, this.m_2(c33, longValue2));
         } else {
            this.c3List.addFirst(new Notifications.StateHolder(event.getModuleName(), event.isEnabled(), longValue2, MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() / 2.0F + 26.0F));
            if (this.c3List.size() > 5) {
               this.c3List.removeLast();
            }
         }
      }
   }

   public HudStylePanel getHudStylePanel() {
      return this.hudStylePanel;
   }

   public boolean m_7_boolean(float floatValue, float floatValue2) {
      return this.isEnabled() && this.textField.m_2_boolean(floatValue, floatValue2);
   }

   private Component formatMessage(Component component) {
      return Component.empty().withColor((Integer)this.theme.getColorSetting3().getValue() & 16777215).append(ChatBadges.m_6(component));
   }

   public void m_3_void(ItemStack itemStack) {
      if (this.isEnabled() && MinecraftAccess.MINECRAFT.player != null && !itemStack.isEmpty()) {
         this.c3List.addFirst(new Notifications.StateHolder(itemStack.copy(), System.currentTimeMillis(), MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() / 2.0F + 26.0F));
         if (this.c3List.size() > 5) {
            this.c3List.removeLast();
         }
      }
   }

   public void m_0(Component component, ItemStack itemStack) {
      if (this.isEnabled() && MinecraftAccess.MINECRAFT.player != null) {
         this.c3List
            .addFirst(
               new Notifications.StateHolder(
                  this.formatMessage(component),
                  itemStack == null ? null : itemStack.copy(),
                  System.currentTimeMillis(),
                  MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() / 2.0F + 26.0F
               )
            );
         if (this.c3List.size() > 5) {
            this.c3List.removeLast();
         }
      }
   }

   public void m_2(Component component, ItemStack itemStack) {
      if (this.isEnabled() && MinecraftAccess.MINECRAFT.player != null) {
         this.m_2(new Notifications.StateHolder(this.formatMessage(component), itemStack == null ? null : itemStack.copy(), System.currentTimeMillis(), this.m_9_float(0)), component.getString(), 1);
      }
   }

   public void m_6(Component component, String text) {
      if (this.isEnabled() && MinecraftAccess.MINECRAFT.player != null) {
         this.m_2(new Notifications.StateHolder(this.formatMessage(component), text, System.currentTimeMillis(), this.m_9_float(0)), component.getString(), 1);
      }
   }

   private void m_2(Notifications.StateHolder c32, String text, int value) {
      long longValue2 = System.currentTimeMillis();
      Notifications.StateHolder c33 = null;

      for (Notifications.StateHolder c34 : this.c3List2) {
         if (text.equals(c34.text3)) {
            c33 = c34;
            break;
         }
      }

      if (c33 != null) {
         this.c3List2.remove(c33);
         c33.value += value;
         c32 = c33;
      } else {
         c32.text3 = text;
         c32.value = value;
      }

      c32.longValue2 = longValue2 + 5000L;
      this.c3List2.addFirst(c32);
      if (this.c3List2.size() > 6) {
         this.c3List2.removeLast();
      }
   }

   private float m_9_float(int value) {
      float floatValue = Onetap.MODULE_MANAGER.get(HUD.class).getFloatSetting();
      return MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() - 4.0F - 18.0F * floatValue - value * 21.5F * floatValue;
   }

   private void m_4(ClientboundTakeItemEntityPacket clientboundTakeItemEntityPacket) {
      if ((Boolean)this.pickUniqueItems.getValue() && clientboundTakeItemEntityPacket.getPlayerId() == MinecraftAccess.MINECRAFT.player.getId()) {
         if (MinecraftAccess.MINECRAFT.level.getEntity(clientboundTakeItemEntityPacket.getItemId()) instanceof ItemEntity itemEntity) {
            ItemStack itemStack = itemEntity.getItem();
            if (!itemStack.isEmpty()
               && (
                  itemStack.has(DataComponents.CUSTOM_NAME)
                     || !itemStack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines().isEmpty()
                     || !Objects.equals(itemStack.get(DataComponents.ITEM_NAME), itemStack.getPrototype().get(DataComponents.ITEM_NAME))
               )) {
               MutableComponent mutableComponent = Component.empty()
                  .append(Component.literal("Вы подняли ").withStyle(ChatFormatting.GRAY))
                  .append(itemStack.getHoverName().copy());
               this.m_2(new Notifications.StateHolder(this.formatMessage(mutableComponent), itemStack.copy(), System.currentTimeMillis(), this.m_9_float(0)), mutableComponent.getString(), clientboundTakeItemEntityPacket.getAmount());
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         long longValue2 = System.currentTimeMillis();
         this.uUIDMap.entrySet().removeIf(entry -> longValue2 >= entry.getValue().m_19());

         for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
            if (player != MinecraftAccess.MINECRAFT.player) {
               this.m_1(player, longValue2, 15000L);
               this.m_1_void(player);
            }
         }

         this.uUIDSet.removeIf(uUID -> MinecraftAccess.MINECRAFT.level.getPlayerByUUID(uUID) == null);
         if (KillAura.livingEntity instanceof Player player2) {
            this.m_1(player2, longValue2, 30000L);
         }

         if ((Boolean)this.effectLoss.getValue()) {
            this.m_4_void();
         }
      } else {
         this.uUIDMap.clear();
         this.uUIDSet.clear();
         this.holderMap.clear();
      }
   }

   private void m_4_void() {
      for (Holder holder : HOLDER_SET) {
         MobEffectInstance mobEffectInstance = MinecraftAccess.MINECRAFT.player.getEffect(holder);
         if (mobEffectInstance == null) {
            this.holderMap.remove(holder);
         } else {
            boolean flag = !mobEffectInstance.isInfiniteDuration() && mobEffectInstance.getDuration() <= 200;
            Boolean bool = this.holderMap.put(holder, flag);
            if (flag && bool != Boolean.TRUE) {
               this.m_6(holder, " закончится через 10 сек!");
            }
         }
      }
   }

   private void m_6(Holder<MobEffect> holder, String text) {
      this.m_6(
         Component.empty()
            .append(Component.literal("Эффект ").withStyle(ChatFormatting.GRAY))
            .append(((MobEffect)holder.value()).getDisplayName().copy().withStyle(ChatFormatting.WHITE))
            .append(Component.literal(text).withStyle(ChatFormatting.GRAY)),
         "\uf6e1"
      );
   }

   private void m_1_void(Player player) {
      if ((Boolean)this.lowFriendHealth.getValue()) {
         if (Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())) {
            if (player.getHealth() < 8.0F) {
               if (this.uUIDSet.add(player.getUUID())) {
                  NameProtect nameProtect = Onetap.MODULE_MANAGER.get(NameProtect.class);
                  String text = player.getName().getString();
                  if (nameProtect != null) {
                     text = nameProtect.replaceInText(text);
                  }

                  this.m_6(
                     Component.empty()
                        .append(Component.literal("Другу ").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(text).withStyle(ChatFormatting.WHITE))
                        .append(Component.literal(" стало больно!").withStyle(ChatFormatting.GRAY)),
                     "\uf21e"
                  );
               }
            } else {
               this.uUIDSet.remove(player.getUUID());
            }
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         switch (event.getPacket()) {
            case ClientboundPlayerChatPacket clientboundPlayerChatPacket:
               Player player = MinecraftAccess.MINECRAFT.level.getPlayerByUUID(clientboundPlayerChatPacket.sender());
               if (player == MinecraftAccess.MINECRAFT.player) {
                  return;
               }

               String text = player != null ? player.getName().getString() : this.getName(clientboundPlayerChatPacket.sender());
               if (text == null) {
                  return;
               }

               String text2 = clientboundPlayerChatPacket.unsignedContent() != null ? clientboundPlayerChatPacket.unsignedContent().getString() : clientboundPlayerChatPacket.body().content();
               this.m_5_void(text, text2);
               break;
            case ClientboundDisguisedChatPacket clientboundDisguisedChatPacket:
               String text3 = clientboundDisguisedChatPacket.chatType().name().getString();
               String text4 = this.resolveName(text3);
               if (text4 == null || MinecraftAccess.MINECRAFT.player.getName().getString().equalsIgnoreCase(text4)) {
                  return;
               }

               this.m_5_void(text4, clientboundDisguisedChatPacket.message().getString());
               break;
            case ClientboundSystemChatPacket clientboundSystemChatPacket:
               if (clientboundSystemChatPacket.overlay()) {
                  return;
               }

               this.m_1_void(clientboundSystemChatPacket.content().getString());
               break;
            case ClientboundTakeItemEntityPacket clientboundTakeItemEntityPacket:
               this.m_4(clientboundTakeItemEntityPacket);
               break;
            default:
         }
      }
   }

   private void m_1_void(String text) {
      String text2 = null;
      String text3 = null;
      int value = Integer.MAX_VALUE;

      for (Notifications.DataRecord c7 : this.uUIDMap.values()) {
         String text4 = c7.m_22();
         if (!text4.isEmpty()) {
            int value2 = indexOfIgnoreCase(text, text4);
            if (value2 >= 0 && value2 < value) {
               String text5 = text.substring(value2 + text4.length()).replaceFirst("^[\\s>»:\\]\\)\\-–—|]+", "").trim();
               if (!text5.isEmpty() && PATTERN.matcher(text5).find()) {
                  text2 = text4;
                  text3 = text5;
                  value = value2;
               }
            }
         }
      }

      if (text2 != null) {
         this.m_5_void(text2, text3);
      }
   }

   private void m_5_void(String text, String text2) {
      if ((Boolean)this.spectate.getValue()) {
         Matcher matcher = PATTERN.matcher(text2);
         if (matcher.find()) {
            String text3 = matcher.group(1);
            String text4 = null;
            if (text3 != null && !text3.isEmpty()) {
               text4 = this.resolveName(text3);
            }

            long longValue2 = System.currentTimeMillis();
            if (longValue2 - this.longValue >= 2500L) {
               this.longValue = longValue2;
               MutableComponent mutableComponent = Component.empty()
                  .append(Component.literal("Игрок ").withStyle(ChatFormatting.GRAY))
                  .append(Component.literal(text).withStyle(ChatFormatting.WHITE));
               if (text4 != null) {
                  mutableComponent.append(Component.literal(" просит наблюдение за ").withStyle(ChatFormatting.GRAY))
                     .append(Component.literal(text4).withStyle(ChatFormatting.WHITE))
                     .append(Component.literal("!").withStyle(ChatFormatting.GRAY));
               } else {
                  mutableComponent.append(Component.literal(" просит наблюдение!").withStyle(ChatFormatting.GRAY));
               }

               this.m_6(mutableComponent, "\uf1e5");
            }
         }
      }
   }

   private String resolveName(String text) {
      if (MinecraftAccess.MINECRAFT.player.getName().getString().equalsIgnoreCase(text)) {
         return MinecraftAccess.MINECRAFT.player.getName().getString();
      }

      for (Notifications.DataRecord c7 : this.uUIDMap.values()) {
         if (c7.m_22().equalsIgnoreCase(text)) {
            return c7.m_22();
         }
      }

      return null;
   }

   private String getName(UUID uUID) {
      Notifications.DataRecord c7 = this.uUIDMap.get(uUID);
      return c7 != null ? c7.m_22() : null;
   }

   private void m_1(Player player, long longValue2, long longValue3) {
      this.uUIDMap
         .merge(
            player.getUUID(), new Notifications.DataRecord(player.getName().getString(), longValue2 + longValue3), (c7, c72) -> new Notifications.DataRecord(c72.m_22(), Math.max(c7.m_19(), c72.m_19()))
         );
   }

   private static int indexOfIgnoreCase(String text, String text2) {
      return text.toLowerCase(Locale.ROOT).indexOf(text2.toLowerCase(Locale.ROOT));
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         long longValue2 = System.currentTimeMillis();
         this.c3List.removeIf(c32 -> longValue2 >= c32.longValue2);
         float floatValue = Onetap.MODULE_MANAGER.get(HUD.class).getFloatSetting();
         float floatValue2 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() / 2.0F + 26.0F;
         boolean flag = MinecraftAccess.MINECRAFT.gui.screen() instanceof ChatScreen;
         this.animation.setDuration(flag ? 250L : 300L);
         float floatValue3 = this.animation.update(flag ? 1.0F : 0.0F);
         if (this.m_1_boolean(floatValue)) {
            floatValue2 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() / 2.0F + 37.0F + 21.5F * floatValue;
         }

         float floatValue4 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth() / 2.0F;
         if (floatValue3 > 0.001F) {
            if (this.c3 == null) {
               this.c3 = new Notifications.StateHolder("Пример уведомления", true, longValue2, floatValue2);
            }

            this.m_1(event, this.c3, floatValue4, 0.5F, floatValue2, floatValue3, 1.0F, floatValue);
         }

         this.m_3_void(flag);
         boolean flag2 = flag || floatValue3 > 0.001F;
         float floatValue5 = 21.5F * floatValue;

         for (int i = 0; i < this.c3List.size(); i++) {
            Notifications.StateHolder c33 = this.c3List.get(i);
            float floatValue6 = c33.animation.update(floatValue2 + (i + (flag2 ? 1 : 0)) * floatValue5);
            this.m_1(event, c33, floatValue4, 0.5F, floatValue6, this.m_2(c33, longValue2), 1.0F, floatValue);
         }

         this.c3List2.removeIf(c34 -> longValue2 >= c34.longValue2);
         float floatValue7 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth() - 4.0F;

         for (int i2 = 0; i2 < this.c3List2.size(); i2++) {
            Notifications.StateHolder c35 = this.c3List2.get(i2);
            float floatValue8 = c35.animation.update(this.m_9_float(i2));
            float floatValue9 = Math.max(0.35F, 1.0F - i2 * 0.13F);
            this.m_1(event, c35, floatValue7, 1.0F, floatValue8, this.m_2(c35, longValue2), floatValue9, floatValue);
         }
      }
   }

   private boolean m_1_boolean(float floatValue) {
      StaffExploit staffExploit = Onetap.MODULE_MANAGER.get(StaffExploit.class);
      if (staffExploit != null && staffExploit.isEnabled()) {
         int value = Math.round(StaffExploit.getProgress() * 100.0F);
         String text = "Поиск модераций ";
         String text2 = value >= 100 ? "завершён" : value + "%";
         var supplier = getFont();
         float floatValue2 = getTextSize();
         float floatValue3 = Draw2D.getTextWidth(text, supplier, floatValue2);
         float floatValue4 = 25.0F + floatValue3 + Draw2D.getTextWidth(text2, supplier, floatValue2);
         float floatValue5 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth() / 2.0F;
         float floatValue6 = floatValue5 - floatValue4 / 2.0F;
         float floatValue7 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() / 2.0F + 37.0F;
         staffExploit.floatValue = floatValue7;
         Render2D.setTransform(floatValue, floatValue5 - floatValue5 * floatValue, floatValue7 - floatValue7 * floatValue);

         try {
            this.theme.m_7_void(floatValue6, floatValue7, floatValue4, 18.0F, 4.0F, 1.0F);
            this.m_2(floatValue6 + 4.0F, floatValue7, 18.0F, 14.0F, "\uf0f3", this.theme.getAccentColor(), this.theme.m_2_int(), 255);
            float floatValue8 = Draw2D.getTextBaseline(supplier, floatValue2, floatValue7, 18.0F);
            Draw2D.drawStyledText(text, supplier, floatValue6 + 21.0F, floatValue8, this.theme.m_8_int(255), floatValue2);
            Draw2D.drawStyledText(text2, supplier, floatValue6 + 21.0F + floatValue3, floatValue8, value >= 100 ? this.theme.m_8_int(255) : this.theme.getAccentColor(), floatValue2);
         } finally {
            Render2D.setTransform(1.0F, 0.0F, 0.0F);
         }

         return true;
      } else {
         return false;
      }
   }

   private void m_3_void(boolean flag) {
      float floatValue = (float)(MinecraftAccess.MINECRAFT.mouseHandler.xpos() / MinecraftAccess.MINECRAFT.getWindow().getGuiScale());
      float floatValue2 = (float)(MinecraftAccess.MINECRAFT.mouseHandler.ypos() / MinecraftAccess.MINECRAFT.getWindow().getGuiScale());
      float floatValue3 = this.animation2.update(flag && this.textField.m_2_boolean(floatValue, floatValue2) && !Onetap.MODULE_MANAGER.get(HUD.class).isActive() ? 1.0F : 0.0F);
      if (!(floatValue3 < 0.01F)) {
         Draw2D.m_4(
            this.textField.m_5_float() - 2.0F,
            this.textField.m_8_float() - 2.0F,
            this.textField.m_6_float() + 4.0F,
            this.textField.m_9_float() + 4.0F,
            0.5F,
            4.0F,
            ColorUtil.rgba(255, 255, 255, (int)(110.0F * floatValue3))
         );
      }
   }

   private void m_1(Render2DEvent event, Notifications.StateHolder c32, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      Render2D.setTransform(floatValue6, floatValue - floatValue * floatValue6, floatValue3 - floatValue3 * floatValue6);

      try {
         this.m_6(event, c32, floatValue, floatValue2, floatValue3, floatValue4, floatValue5);
         if (c32 == this.c3) {
            float floatValue7 = c32.animation2.getValue();
            this.textField.setFloatValue7(floatValue6);
            this.textField.m_8_void(floatValue7, 18.0F);
            this.textField.m_2_void(floatValue - floatValue7 * floatValue6 * floatValue2, floatValue3);
         }
      } finally {
         Render2D.setTransform(1.0F, 0.0F, 0.0F);
      }
   }

   private static Supplier<FontAtlas> getFont() {
      return Fonts.SUPPLIER18;
   }

   private static float getTextSize() {
      return 8.5F;
   }

   private float m_2(Notifications.StateHolder c32, long longValue2) {
      float floatValue = (float)(longValue2 - c32.longValue);
      float floatValue2 = (float)(c32.longValue2 - longValue2);
      return floatValue < 250.0F ? Easing.EASING8.ease(floatValue, 0.0F, 1.0F, 250.0F) : (floatValue2 < 300.0F ? Easing.EASING7.ease(floatValue2, 0.0F, 1.0F, 300.0F) : 1.0F);
   }

   private void m_6(Render2DEvent event, Notifications.StateHolder c32, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      var supplier = getFont();
      float floatValue6 = getTextSize();
      float floatValue7 = c32.animation2.update(m_0(c32));
      m_5_C3_sArr();
      float floatValue8 = 18.0F;
      float floatValue9 = 4.0F;
      float floatValue10 = 12.0F;
      float floatValue11 = 14.0F;
      float floatValue12 = 3.0F;
      float floatValue13 = floatValue - floatValue7 * floatValue2 + (1.0F - floatValue4) * 12.0F;
      floatValue4 *= floatValue5;
      int value = (int)(255.0F * floatValue4);
      float floatValue14 = Draw2D.getTextBaseline(supplier, floatValue6, floatValue3, floatValue8);
      int value2 = this.theme.m_8_int(value);
      int value3 = this.theme.getAccentColor();
      int value4 = this.theme.m_2_int();
      float floatValue15 = (float)(System.currentTimeMillis() % 1000L) / 1000.0F;
      this.theme.m_7_void(floatValue13, floatValue3, floatValue7, floatValue8, 4.0F, floatValue4);
      float floatValue16 = floatValue13 + floatValue9;
      if (c32.textRenderer != null) {
         float floatValue17 = floatValue13 + floatValue9;
         if (c32.itemStack != null) {
            this.m_5(event, c32.itemStack, floatValue16, floatValue3, floatValue8, floatValue10, floatValue4);
            floatValue17 = floatValue16 + floatValue10 + floatValue12;
         } else if (c32.text2 != null) {
            this.m_2(floatValue16, floatValue3, floatValue8, floatValue11, c32.text2, value3, value4, value);
            floatValue17 = floatValue16 + floatValue11 + floatValue12;
         }

         c32.textRenderer.draw(floatValue17, floatValue14, floatValue6, value);
         if (c32.value > 1) {
            Draw2D.drawStyledText(" x" + c32.value, supplier, floatValue17 + c32.textRenderer.getWidth(floatValue6), floatValue14, ColorUtil.withAlpha(value3, value), floatValue6);
         }
      } else if (c32.itemStack != null) {
         this.m_5(event, c32.itemStack, floatValue16, floatValue3, floatValue8, floatValue10, floatValue4);
         String text = "Свапнул на ";
         float floatValue18 = floatValue16 + floatValue10 + floatValue12;
         Draw2D.drawStyledText(text, supplier, floatValue18, floatValue14, value2, floatValue6);
         c32.textRenderer2.draw(floatValue18 + Draw2D.getTextWidth(text, supplier, floatValue6), floatValue14, floatValue6, value);
      } else if (c32.text.equals("Пример уведомления")) {
         this.m_2(floatValue16, floatValue3, floatValue8, floatValue11, "\uf0f3", value3, value4, value);
         float floatValue19 = floatValue16 + floatValue11 + floatValue12;
         Draw2D.drawStyledText(c32.text, supplier, floatValue19, floatValue14, value2, floatValue6);
         this.m_6(floatValue13, floatValue3, floatValue7, floatValue8, floatValue16, floatValue11, floatValue19, Draw2D.getTextWidth(c32.text, supplier, floatValue6));
      } else {
         this.m_1(c32, floatValue16, floatValue3, floatValue8, floatValue11, value3, value4, value);
         String text2 = "Модуль " + c32.text + " ";
         float floatValue20 = floatValue16 + floatValue11 + floatValue12;
         Draw2D.drawStyledText(text2, supplier, floatValue20, floatValue14, value2, floatValue6);
         this.m_5(c32, floatValue20 + Draw2D.getTextWidth(text2, supplier, floatValue6), floatValue14, floatValue4, value3, value4, floatValue15);
      }
   }

   private void m_6(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8) {
      int value = ColorUtil.rgba(255, 255, 255, 230);
      switch (this.hudStylePanel.getActiveColor()) {
         case 0:
            Theme.m_5(floatValue + 0.5F, floatValue2 + 0.5F, floatValue3 - 1.0F, floatValue4 - 1.0F, value);
         case 1:
         default:
            break;
         case 2:
            Theme.m_3(floatValue7, floatValue2 + floatValue4 - 4.0F, floatValue8, value);
            break;
         case 3:
            float floatValue9 = floatValue2 + (floatValue4 - floatValue6) / 2.0F;
            Theme.m_5(floatValue5 - 0.5F, floatValue9 - 0.5F, floatValue6 + 1.0F, floatValue6 + 1.0F, value);
      }
   }

   private void m_5(Notifications.StateHolder c32, float floatValue, float floatValue2, float floatValue3, int value, int value2, float floatValue4) {
      var supplier = getFont();
      float floatValue5 = getTextSize();
      float floatValue6 = c32.animation4.get();
      int value3 = (int)(255.0F * floatValue6 * floatValue3);
      int value4 = (int)(255.0F * (1.0F - floatValue6) * floatValue3);
      int value5 = ColorUtil.rgba(140, 140, 140, 255);
      int value6 = ColorUtil.rgba(100, 100, 100, 255);
      if (value4 > 0) {
         Draw2D.m_9("выключен!", supplier, floatValue, floatValue2 - floatValue6 * 3.0F, ColorUtil.withAlpha(value5, value4), ColorUtil.withAlpha(value6, value4), floatValue5, floatValue4);
      }

      if (value3 > 0) {
         Draw2D.m_9("включен!", supplier, floatValue, floatValue2 + (1.0F - floatValue6) * 3.0F, ColorUtil.withAlpha(value, value3), ColorUtil.withAlpha(value2, value3), floatValue5, floatValue4);
      }
   }

   private static float m_3_float() {
      return 25.0F;
   }

   private static float m_9_float() {
      return 23.0F;
   }

   private static float m_8_float(String text, boolean flag) {
      var supplier = getFont();
      float floatValue = getTextSize();
      float floatValue2 = m_3_float();
      if (text.equals("Пример уведомления")) {
         return Draw2D.getTextWidth(text, supplier, floatValue) + floatValue2;
      }

      String text2 = flag ? " включен!" : " выключен!";
      String text3 = text;
      return Draw2D.getTextWidth("Модуль " + text3 + text2, supplier, floatValue) + floatValue2;
   }

   private static float m_0(Notifications.StateHolder c32) {
      var supplier = getFont();
      float floatValue = getTextSize();
      if (c32.textRenderer != null) {
         float floatValue2 = c32.textRenderer.getWidth(floatValue) + (c32.value > 1 ? Draw2D.getTextWidth(" x" + c32.value, supplier, floatValue) : 0.0F);
         return c32.text2 != null ? floatValue2 + m_3_float() : floatValue2 + (c32.itemStack != null ? m_9_float() : 8.0F);
      } else {
         return c32.itemStack != null ? Draw2D.getTextWidth("Свапнул на ", supplier, floatValue) + c32.textRenderer2.getWidth(floatValue) + m_9_float() : m_8_float(c32.text, c32.flag);
      }
   }

   private void m_5(Render2DEvent event, ItemStack itemStack, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
      MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, itemStack, ItemDisplayContext.GUI, MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, itemStack.hashCode());
      event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
      Render2D.drawItem(trackingItemStackRenderState, floatValue, floatValue2 + (floatValue3 - floatValue4) / 2.0F, floatValue4, ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue5)));
   }

   private void m_2(float floatValue, float floatValue2, float floatValue3, float floatValue4, String text, int value, int value2, int value3) {
      float floatValue5 = floatValue2 + (floatValue3 - floatValue4) / 2.0F;
      Draw2D.m_8(floatValue + 0.5F, floatValue5 + 1.0F, floatValue4 - 1.0F, floatValue4 - 2.0F, 4.0F, 0.5F, ColorUtil.withAlpha(value, value3), ColorUtil.withAlpha(value2, value3));
      float floatValue6 = floatValue4 - 4.0F;
      float floatValue7 = Draw2D.getCenteredX(text, Fonts.SUPPLIER8, floatValue6, floatValue, floatValue4);
      float floatValue8 = Draw2D.getCenteredY(text, Fonts.SUPPLIER8, floatValue6, floatValue5, floatValue4);
      Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER8, floatValue7, floatValue8, ColorUtil.rgba(255, 255, 255, value3), floatValue6);
   }

   private void m_1(Notifications.StateHolder c32, float floatValue, float floatValue2, float floatValue3, float floatValue4, int value, int value2, int value3) {
      float floatValue5 = c32.animation3.get();
      float floatValue6 = 1.0F - floatValue5;
      float floatValue7 = floatValue2 + (floatValue3 - floatValue4) / 2.0F;
      int value4 = ColorUtil.rgba(140, 140, 140, 255);
      int value5 = ColorUtil.rgba(100, 100, 100, 255);
      int value6 = ColorUtil.lerpColor(ColorUtil.withAlpha(value5, value3), ColorUtil.withAlpha(value, value3), floatValue5);
      int value7 = ColorUtil.lerpColor(ColorUtil.withAlpha(value4, value3), ColorUtil.withAlpha(value2, value3), floatValue5);
      Draw2D.m_8(floatValue + 0.5F, floatValue7 + 1.0F, floatValue4 - 1.0F, floatValue4 - 2.0F, 4.0F, 0.5F, value6, value7);
      float floatValue8 = floatValue4 - 4.0F;
      if (floatValue6 > 0.01F) {
         float floatValue9 = floatValue8 * (0.3F + 0.7F * floatValue6);
         float floatValue10 = Draw2D.getCenteredX("\uf00d", Fonts.SUPPLIER8, floatValue9, floatValue, floatValue4);
         float floatValue11 = Draw2D.getCenteredY("\uf00d", Fonts.SUPPLIER8, floatValue9, floatValue7, floatValue4);
         Draw2D.drawTextWithShadow("\uf00d", Fonts.SUPPLIER8, floatValue10, floatValue11, ColorUtil.rgba(235, 235, 235, (int)(255.0F * floatValue6 * value3 / 255.0F)), floatValue9);
      }

      if (floatValue5 > 0.01F) {
         float floatValue12 = floatValue8 * (0.3F + 0.7F * floatValue5);
         float floatValue13 = Draw2D.getCenteredX("\uf00c", Fonts.SUPPLIER8, floatValue12, floatValue, floatValue4);
         float floatValue14 = Draw2D.getCenteredY("\uf00c", Fonts.SUPPLIER8, floatValue12, floatValue7, floatValue4);
         Draw2D.drawTextWithShadow("\uf00c", Fonts.SUPPLIER8, floatValue13, floatValue14, ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue5 * value3 / 255.0F)), floatValue12);
      }
   }

   @Override
   public void onDisable() {
      this.c3List.clear();
      this.c3List2.clear();
      this.hudStylePanel.m_7_void();
      this.c3 = null;
      this.animation.reset();
      this.longValue = 0L;
      this.uUIDMap.clear();
      this.uUIDSet.clear();
      this.holderMap.clear();
   }

   static {
      m_7(null);
   }

   public static void m_7(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_5_C3_sArr() {
      return moduleArray;
   }

   private static final class StateHolder {
      private final String text;
      private final TextRenderer textRenderer;
      private final String text2;
      private long longValue;
      private final Animation animation;
      private final Animation animation2;
      private final Animation animation3;
      private final Animation animation4;
      private final ItemStack itemStack;
      private final TextRenderer textRenderer2;
      private boolean flag;
      private long longValue2;
      private String text3;
      private int value;

      private StateHolder(String text4, boolean flag2, long longValue3, float floatValue) {
         this.value = 1;
         this.text = text4;
         this.textRenderer = null;
         this.text2 = null;
         this.itemStack = null;
         this.textRenderer2 = null;
         this.flag = flag2;
         this.longValue = longValue3;
         this.longValue2 = longValue3 + 1200L;
         this.animation = new Animation(250L, floatValue, Easing.EASING8);
         this.animation2 = new Animation(350L, Notifications.m_8_float(text4, flag2) - 8.0F, Easing.EASING8);
         this.animation3 = new Animation(350L, flag2 ? 0.0F : 1.0F, Easing.EASING8);
         this.animation4 = new Animation(120L, flag2 ? 1.0F : 0.0F, Easing.EASING18);
         this.animation3.setTarget(flag2 ? 1.0F : 0.0F);
      }

      private StateHolder(ItemStack itemStack2, long longValue3, float floatValue) {
         this.value = 1;
         this.text = null;
         this.textRenderer = null;
         this.text2 = null;
         this.itemStack = itemStack2;
         this.textRenderer2 = TextRenderer.m_6(ChatBadges.m_6(itemStack2.getHoverName()), Notifications.getFont(), Fonts.SUPPLIER17);
         this.flag = true;
         this.longValue = longValue3;
         this.longValue2 = longValue3 + 1200L;
         this.animation = new Animation(250L, floatValue, Easing.EASING8);
         this.animation2 = new Animation(350L, Notifications.m_0(this) - 8.0F, Easing.EASING8);
         this.animation3 = new Animation(350L, 1.0F, Easing.EASING8);
         this.animation4 = new Animation(120L, 1.0F, Easing.EASING18);
      }

      private StateHolder(Component component, ItemStack itemStack2, long longValue3, float floatValue) {
         Notifications.m_5_C3_sArr();
         this.value = 1;
         this.text = null;
         this.textRenderer = TextRenderer.m_6(component, Notifications.getFont(), Fonts.SUPPLIER17);
         this.text2 = null;
         this.itemStack = itemStack2;
         this.textRenderer2 = itemStack2 != null && !itemStack2.isEmpty() ? TextRenderer.m_6(itemStack2.getHoverName(), Notifications.getFont(), Fonts.SUPPLIER17) : null;
         this.flag = true;
         this.longValue = longValue3;
         this.longValue2 = longValue3 + 1200L;
         this.animation = new Animation(250L, floatValue, Easing.EASING8);
         this.animation2 = new Animation(350L, Notifications.m_0(this) - 8.0F, Easing.EASING8);
         this.animation3 = new Animation(350L, 1.0F, Easing.EASING8);
         this.animation4 = new Animation(120L, 1.0F, Easing.EASING18);
      }

      private StateHolder(Component component, String text4, long longValue3, float floatValue) {
         this.value = 1;
         this.text = null;
         this.textRenderer = TextRenderer.m_6(component, Notifications.getFont(), Fonts.SUPPLIER17);
         this.text2 = text4;
         this.itemStack = null;
         this.textRenderer2 = null;
         this.flag = true;
         this.longValue = longValue3;
         this.longValue2 = longValue3 + 2500L;
         this.animation = new Animation(250L, floatValue, Easing.EASING8);
         this.animation2 = new Animation(350L, Notifications.m_0(this) - 8.0F, Easing.EASING8);
         this.animation3 = new Animation(350L, 1.0F, Easing.EASING8);
         this.animation4 = new Animation(120L, 1.0F, Easing.EASING18);
      }

      private void m_6(boolean flag2, long longValue3, float floatValue) {
         if (this.flag != flag2) {
            this.flag = flag2;
            this.longValue = longValue3 - (long)(250.0 * (1.0 - Math.cbrt(1.0F - Math.max(0.0F, Math.min(1.0F, floatValue)))));
            this.longValue2 = longValue3 + 1200L;
            this.animation3.setTarget(flag2 ? 1.0F : 0.0F);
            this.animation4.setTarget(flag2 ? 1.0F : 0.0F);
         }
      }
   }

   private record DataRecord(String f_22, long f_19) {
      public String m_22() {
         return this.f_22;
      }

      public long m_19() {
         return this.f_19;
      }
   }
}
