package wtf.onetap.module.combat;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.ScreenOpenEvent;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.render.Notifications;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.TextRenderer;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.TextSetting;
import wtf.onetap.util.ChatBadges;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.KeyNames;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PacketUtil;

@ModuleInformation(name = "AutoSwap", description = "Колесо смены предмета в оффхенде по зажатой клавише", category = Category.COMBAT)
public class AutoSwap extends Module {
   private static final String[] TEXT_ARRAY = new String[]{"Шар", "Тотем", "Талисман", "Гепл", "Щит"};
   private final KeybindSetting key = new KeybindSetting("Клавиша", -1).build();
   private final ModeSetting type = new ModeSetting("Тип", "Тройной", "Тройной", "Двойной").build();
   private final ModeSetting item1 = new ModeSetting("Предмет 1", "Шар", TEXT_ARRAY).visibleWhen(this::isDoubleMode).build();
   private final ModeSetting item2 = new ModeSetting("Предмет 2", "Тотем", TEXT_ARRAY).visibleWhen(this::isDoubleMode).build();
   private final ModeSetting swapMode = new ModeSetting("Режим свапа", "Дефолт", "Дефолт", "Грим", "Легит", "HolyWorld").build();
   private final FloatSetting delay = new FloatSetting("Задержка", 50.0F, 0.0F, 150.0F, 1.0F).visibleWhen(() -> ((String)this.swapMode.getValue()).equals("Легит")).build();
   private final TextSetting slot1 = new TextSetting("Слот 1", "{id:\"minecraft:totem_of_undying\",count:1}").visibleWhen(() -> false).build();
   private final TextSetting slot2 = new TextSetting("Слот 2", "").visibleWhen(() -> false).build();
   private final TextSetting slot3 = new TextSetting("Слот 3", "").visibleWhen(() -> false).build();
   private final Animation[] animationArray = new Animation[]{new Animation(110L, 0.0F, Easing.EASING8), new Animation(110L, 0.0F, Easing.EASING8), new Animation(110L, 0.0F, Easing.EASING8)};
   private final ItemStack[] itemStackArray = new ItemStack[3];
   private final TextRenderer[] textRendererArray = new TextRenderer[3];
   private final String[] textArray = new String[3];
   private final Animation animation = new Animation(130L, 0.0F, Easing.EASING8);
   private boolean flag;
   private boolean flag2;
   private int value = -1;

   private boolean isDoubleMode() {
      return ((String)this.type.getValue()).equals("Двойной");
   }

   private boolean matchesType(String text, ItemStack itemStack) {
      if (itemStack.isEmpty()) {
         return false;
      }

      return switch (text) {
         case "Шар" -> itemStack.is(Items.PLAYER_HEAD);
         case "Тотем" -> itemStack.is(Items.TOTEM_OF_UNDYING) && !itemStack.isEnchanted();
         case "Талисман" -> itemStack.is(Items.TOTEM_OF_UNDYING) && itemStack.isEnchanted();
         case "Гепл" -> itemStack.is(Items.GOLDEN_APPLE) || itemStack.is(Items.ENCHANTED_GOLDEN_APPLE);
         case "Щит" -> itemStack.is(Items.SHIELD);
         default -> false;
      };
   }

   private int findSlot(String text) {
      for (int i = 0; i < 36; i++) {
         if (this.matchesType(text, MinecraftAccess.MINECRAFT.player.getInventory().getItem(i))) {
            return i;
         }
      }

      return -1;
   }

   private int m_5(String text, ItemStack itemStack) {
      for (int i = 0; i < 36; i++) {
         ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (this.matchesType(text, itemStack2) && !ItemStack.isSameItemSameComponents(itemStack2, itemStack)) {
            return i;
         }
      }

      return -1;
   }

   private void m_7_void() {
      if (((String)this.item1.getValue()).equals(this.item2.getValue())) {
         int value2 = this.m_5((String)this.item1.getValue(), MinecraftAccess.MINECRAFT.player.getOffhandItem());
         if (value2 != -1) {
            this.m_7_void(value2);
         }
      } else {
         boolean flag3 = this.matchesType((String)this.item1.getValue(), MinecraftAccess.MINECRAFT.player.getOffhandItem());
         String[] stringArray = flag3
            ? new String[]{(String)this.item2.getValue(), (String)this.item1.getValue()}
            : new String[]{(String)this.item1.getValue(), (String)this.item2.getValue()};

         for (String text : stringArray) {
            if (!this.matchesType(text, MinecraftAccess.MINECRAFT.player.getOffhandItem())) {
               int value3 = this.findSlot(text);
               if (value3 != -1) {
                  this.m_7_void(value3);
                  return;
               }
            }
         }
      }
   }

   public boolean m_5_boolean() {
      return this.value != -1;
   }

   public boolean m_3_boolean() {
      return this.flag && this.value == -1;
   }

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (!ChatInputHelper.isTextInputOpen()) {
         if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gui.screen() == null && MinecraftAccess.MINECRAFT.gui.overlay() == null) {
            if (event.getAction() == 1 && !this.flag) {
               if ((Integer)this.key.getValue() >= 0 && event.getKey() == (Integer)this.key.getValue()) {
                  if (this.isDoubleMode()) {
                     this.m_7_void();
                  } else {
                     this.flag = true;
                     this.flag2 = false;
                     MinecraftAccess.MINECRAFT.mouseHandler.releaseMouse();
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onScreenOpen(ScreenOpenEvent event) {
      if (this.flag && this.value == -1) {
         this.flag = false;
      }

      this.flag2 = false;
      this.animation.reset(0.0F);
   }

   @EventTarget
   private void onMouseClick(MouseClickEvent event) {
      if (this.flag && this.value == -1) {
         event.cancel();
         if (event.getAction() == 1) {
            int value2 = this.m_1_int(this.animation.getValue());
            if (value2 != -1) {
               if (event.getButton() == 0) {
                  this.value = value2;
                  MinecraftAccess.MINECRAFT.gui.setScreen(new InventoryScreen(MinecraftAccess.MINECRAFT.player));
               } else if (event.getButton() == 1) {
                  this.m_9_void(value2, ItemStack.EMPTY);
               }
            }
         }
      }
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      if (this.value != -1) {
         if (MinecraftAccess.MINECRAFT.gui.screen() == null) {
            this.value = -1;
            this.flag = false;
         }
      } else if (MinecraftAccess.MINECRAFT.player == null) {
         this.flag = false;
         this.flag2 = false;
      } else if (this.flag2) {
         float floatValue = this.animation.update(0.0F);
         if (this.animation.isFinished()) {
            this.flag2 = false;
         } else {
            this.m_6(event, floatValue);
         }
      } else if (this.flag) {
         if (!KeyNames.m_0_boolean((Integer)this.key.getValue())) {
            int value2 = this.m_1_int(1.0F);
            int value3 = value2 == -1 ? -1 : this.m_5_int(this.m_7_ItemStack(value2));
            if (value3 != -1) {
               this.m_7_void(value3);
            }

            this.flag = false;
            this.flag2 = true;
            MinecraftAccess.MINECRAFT.mouseHandler.grabMouse();
         } else {
            float floatValue2 = this.animation.update(1.0F);
            this.m_6(event, floatValue2);
         }
      }
   }

   private void m_6(Render2DEvent event, float floatValue) {
      float floatValue2 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth() / 2.0F;
      float floatValue3 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() / 2.0F;
      int value2 = this.m_1_int(floatValue);
      int value3 = ColorUtil.primaryColor();
      float floatValue4 = 24.0F * floatValue;
      Draw2D.m_1(floatValue2 - floatValue4, floatValue3 - floatValue4, floatValue4 * 2.0F, floatValue4 * 2.0F, floatValue4, 4.0F, ColorUtil.rgba(0, 0, 0, 120));
      Draw2D.m_7(floatValue2 - floatValue4, floatValue3 - floatValue4, floatValue4 * 2.0F, floatValue4 * 2.0F, floatValue4, 3.0F, ColorUtil.rgba(165, 165, 165, 235));
      Draw2D.m_5(floatValue2, floatValue3, floatValue4, 1.2F * floatValue, 1.0F, 0.0F, 1.0F, ColorUtil.rgba(255, 255, 255, 22));

      for (int i = 0; i < 3; i++) {
         double doubleValue = (-Math.PI / 2) + i * (float) (Math.PI * 2.0 / 3.0);
         float floatValue5 = (float)(doubleValue - 0.9871976F);
         ItemStack itemStack = this.m_7_ItemStack(i);
         boolean flag3 = itemStack != null && this.m_5_int(itemStack) != -1;
         float floatValue6 = this.animationArray[i].update(i == value2 ? 1.0F : 0.0F);
         float floatValue7 = (54.0F + floatValue6 * 6.0F) * floatValue;
         float floatValue8 = (22.0F + floatValue6 * 3.0F) * floatValue;
         int value4 = itemStack == null ? ColorUtil.rgba(34, 34, 34, 150) : (flag3 ? ColorUtil.rgba(40, 40, 40, 185) : ColorUtil.rgba(40, 40, 40, 115));
         int value5 = itemStack == null ? ColorUtil.rgba(125, 125, 125, 205) : (flag3 ? value3 : ColorUtil.rgba(205, 75, 75, 215));
         Draw2D.m_5(floatValue2, floatValue3, floatValue7, floatValue8, 0.31423473F, floatValue5, 1.5F, ColorUtil.lerpColor(value4, value5, floatValue6));
         float floatValue9 = floatValue2 + (float)Math.cos(doubleValue) * floatValue7;
         float floatValue10 = floatValue3 + (float)Math.sin(doubleValue) * floatValue7;
         if (itemStack == null) {
            int value6 = (int)(110.0F + 110.0F * floatValue6);
            this.m_5("\uf067", floatValue9, floatValue10, 11.0F * floatValue, ColorUtil.rgba(220, 220, 220, value6));
         } else {
            float floatValue11 = (18.0F + floatValue6 * 2.0F) * floatValue;
            this.m_3(event, itemStack, floatValue9 - floatValue11 / 2.0F, floatValue10 - floatValue11 / 2.0F, floatValue11, flag3 ? 255 : 90);
         }
      }

      this.m_1(event, floatValue2, floatValue3, value2, floatValue);
      this.m_9(floatValue2, floatValue3, value2, floatValue);
   }

   private void m_1(Render2DEvent event, float floatValue, float floatValue2, int value2, float floatValue3) {
      if (value2 == -1) {
         this.m_5("\uf186", floatValue, floatValue2, 14.0F * floatValue3, ColorUtil.rgba(150, 150, 150, 170));
      } else {
         ItemStack itemStack = this.m_7_ItemStack(value2);
         if (itemStack == null) {
            this.m_5("\uf067", floatValue, floatValue2, 13.0F * floatValue3, ColorUtil.rgba(210, 210, 210, 200));
         } else {
            boolean flag3 = this.m_5_int(itemStack) != -1;
            float floatValue4 = 22.0F * floatValue3;
            this.m_3(event, itemStack, floatValue - floatValue4 / 2.0F, floatValue2 - floatValue4 / 2.0F, floatValue4, flag3 ? 255 : 110);
         }
      }
   }

   private void m_9(float floatValue, float floatValue2, int value2, float floatValue3) {
      if (value2 != -1) {
         float floatValue4 = 9.0F * floatValue3;
         float floatValue5 = floatValue2 + 74.0F * floatValue3;
         float floatValue6 = floatValue4 + 4.0F;
         float floatValue7 = 3.0F;
         ItemStack itemStack = this.m_7_ItemStack(value2);
         if (itemStack == null) {
            float floatValue8 = Draw2D.getTextWidth("Пусто", Fonts.SUPPLIER18, floatValue4);
            Draw2D.rect(floatValue - (floatValue8 + floatValue7 * 2.0F) / 2.0F, floatValue5 - 2.0F, floatValue8 + floatValue7 * 2.0F, floatValue6, ColorUtil.rgba(0, 0, 0, 75));
            Draw2D.drawTextWithShadow("Пусто", Fonts.SUPPLIER18, floatValue - floatValue8 / 2.0F, floatValue5, ColorUtil.rgba(140, 140, 140, 255), floatValue4);
         } else {
            TextRenderer textRenderer = this.textRendererArray[value2];
            if (textRenderer != null) {
               int value3 = this.m_5_int(itemStack) != -1 ? 255 : 110;
               float floatValue9 = textRenderer.getWidth(floatValue4);
               Draw2D.rect(floatValue - (floatValue9 + floatValue7 * 2.0F) / 2.0F, floatValue5 - 2.0F, floatValue9 + floatValue7 * 2.0F, floatValue6, ColorUtil.rgba(0, 0, 0, 75));
               textRenderer.draw(floatValue - floatValue9 / 2.0F, floatValue5, floatValue4, value3);
            }
         }
      }
   }

   private void m_5(String text, float floatValue, float floatValue2, float floatValue3, int value2) {
      float floatValue4 = floatValue3 * 2.0F;
      float floatValue5 = Draw2D.getCenteredX(text, Fonts.SUPPLIER8, floatValue3, floatValue - floatValue4 / 2.0F, floatValue4);
      float floatValue6 = Draw2D.getCenteredY(text, Fonts.SUPPLIER8, floatValue3, floatValue2 - floatValue4 / 2.0F, floatValue4);
      Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER8, floatValue5, floatValue6, value2, floatValue3);
   }

   private void m_3(Render2DEvent event, ItemStack itemStack, float floatValue, float floatValue2, float floatValue3, int value2) {
      TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
      MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, itemStack, ItemDisplayContext.GUI, MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, itemStack.hashCode());
      event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
      Render2D.drawItem(trackingItemStackRenderState, floatValue, floatValue2, floatValue3, ColorUtil.rgba(255, 255, 255, value2));
   }

   public void m_3_void(Slot slot) {
      if (this.value != -1) {
         this.m_9_void(this.value, slot != null ? slot.getItem() : ItemStack.EMPTY);
         this.value = -1;
         this.flag = false;
         MinecraftAccess.MINECRAFT.gui.setScreen(null);
      }
   }

   private boolean m_7(ItemStack itemStack, ItemStack itemStack2) {
      return !itemStack2.isEmpty() && itemStack2.is(itemStack.getItem()) ? itemStack.getHoverName().getString().equals(itemStack2.getHoverName().getString()) : false;
   }

   private int m_5_int(ItemStack itemStack) {
      if (itemStack != null && MinecraftAccess.MINECRAFT.player != null) {
         for (int i = 0; i < 36; i++) {
            if (this.m_7(itemStack, MinecraftAccess.MINECRAFT.player.getInventory().getItem(i))) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private int m_1_int(float floatValue) {
      float floatValue2 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth() / 2.0F;
      float floatValue3 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() / 2.0F;
      double doubleValue = MinecraftAccess.MINECRAFT.mouseHandler.getScaledXPos(MinecraftAccess.MINECRAFT.getWindow()) - floatValue2;
      double doubleValue2 = MinecraftAccess.MINECRAFT.mouseHandler.getScaledYPos(MinecraftAccess.MINECRAFT.getWindow()) - floatValue3;
      float floatValue4 = 27.0F * floatValue;
      if (doubleValue * doubleValue + doubleValue2 * doubleValue2 < floatValue4 * floatValue4) {
         return -1;
      }

      double doubleValue3 = (Math.atan2(doubleValue2, doubleValue) + (Math.PI * 5.0 / 6.0)) % (Math.PI * 2);
      if (doubleValue3 < 0.0) {
         doubleValue3 += Math.PI * 2;
      }

      return Math.min(2, (int)(doubleValue3 / (float) (Math.PI * 2.0 / 3.0)));
   }

   private TextSetting getSlotSetting(int value2) {
      return value2 == 0 ? this.slot1 : (value2 == 1 ? this.slot2 : this.slot3);
   }

   private ItemStack m_7_ItemStack(int value2) {
      String text = (String)this.getSlotSetting(value2).getValue();
      if (text.isEmpty()) {
         return null;
      }

      if (!text.equals(this.textArray[value2])) {
         if (MinecraftAccess.MINECRAFT.level == null) {
            return null;
         }

         this.itemStackArray[value2] = this.m_2_ItemStack(text);
         this.textRendererArray[value2] = this.itemStackArray[value2] == null ? null : TextRenderer.m_6(ChatBadges.m_6(this.itemStackArray[value2].getHoverName()), Fonts.SUPPLIER18, Fonts.SUPPLIER17);
         this.textArray[value2] = text;
      }

      return this.itemStackArray[value2];
   }

   private void m_9_void(int value2, ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         ItemStack itemStack2 = itemStack.copy();
         itemStack2.setCount(1);
         this.getSlotSetting(value2).setValue(this.m_8_String(itemStack2));
      } else {
         this.getSlotSetting(value2).setValue("");
      }
   }

   private ItemStack m_2_ItemStack(String text) {
      try {
         RegistryOps registryOps = MinecraftAccess.MINECRAFT.level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
         return (ItemStack)ItemStack.CODEC.parse(registryOps, TagParser.parseCompoundFully(text)).result().filter(object -> !((net.minecraft.world.item.ItemStack)object).isEmpty()).orElse(null);
      } catch (Exception e) {
         return null;
      }
   }

   private String m_8_String(ItemStack itemStack) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return "";
      }

      try {
         RegistryOps registryOps = MinecraftAccess.MINECRAFT.level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
         return (String)ItemStack.CODEC.encodeStart(registryOps, itemStack).result().map(Object::toString).orElse("");
      } catch (Exception e) {
         return "";
      }
   }

   private void m_7_void(int value2) {
      int value3 = value2 < 9 ? value2 + 36 : value2;
      ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(value2).copy();
      Runnable runnable = () -> {
         InventoryUtil.clickSlot(0, value3, 0, ContainerInput.PICKUP);
         InventoryUtil.clickSlot(0, 45, 0, ContainerInput.PICKUP);
         InventoryUtil.clickSlot(0, value3, 0, ContainerInput.PICKUP);
         Onetap.MODULE_MANAGER.get(Notifications.class).m_3_void(itemStack);
      };
      switch ((String)this.swapMode.getValue()) {
         case "HolyWorld":
            InventoryUtil.m_0(() -> {
               PacketUtil.sendPacket(new ServerboundPlayerCommandPacket(MinecraftAccess.MINECRAFT.player, Action.OPEN_INVENTORY));
               InventoryUtil.clickSlot(0, value3, 40, ContainerInput.SWAP);
               PacketUtil.sendPacket(new ServerboundContainerClosePacket(0));
               Onetap.MODULE_MANAGER.get(Notifications.class).m_3_void(itemStack);
            });
            break;
         default:
            InventoryUtil.runWithMode((String)this.swapMode.getValue(), runnable, (Float)this.delay.getValue());
      }
   }
}
