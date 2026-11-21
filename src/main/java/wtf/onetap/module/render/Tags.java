package wtf.onetap.module.render;

import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.misc.ScoreboardHealth;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.TextRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.ChatBadges;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.WorldToScreen;

@ModuleInformation(name = "Tags", description = "Отображает информацию над сущностями", category = Category.RENDER)
public class Tags extends Module {
   private final MultiModeSetting display = new MultiModeSetting("Отображать", "Игроки", "Игроки", "Мобы", "Предметы").build();
   private final BooleanSetting itemOutput = new BooleanSetting("Вывод предметов", false).visibleWhen(() -> this.display.isSelected("Игроки")).build();
   private final BooleanSetting itemIcon = new BooleanSetting("Иконка предмета", false).visibleWhen(() -> this.display.isSelected("Предметы")).build();
   private final BooleanSetting showArmor = new BooleanSetting("Показывать броню", false).visibleWhen(() -> this.display.isSelected("Игроки")).build();
   private final ModeSetting donate = new ModeSetting("Донат", "Текст", "Текст", "Картинка").build();
   private static final EquipmentSlot[] EQUIPMENT_SLOT_ARRAY = new EquipmentSlot[]{
      EquipmentSlot.MAINHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND
   };
   private static final Pattern PATTERN = Pattern.compile("(?<![\\w\\[])\\w{3,16}(?![\\w\\]])");
   private final List<Tags.CallbackType> c5List = new ArrayList<>();
   private final Map<Integer, Tags.StateHolder[]> integerMap = new HashMap<>();
   private final Set<Integer> integerSet = new HashSet<>();
   private final Map<Integer, Tags.StateHolder> integerMap2 = new HashMap<>();
   private final Set<Integer> integerSet2 = new HashSet<>();
   private final Map<String, TextRenderer> stringMap = new HashMap<>();
   private final Set<UUID> uUIDSet = new HashSet<>();
   private final Tags.StateHolder c3 = new Tags.StateHolder();
   private ItemStack itemStack;
   private Level level;
   private static String[] textArray;

   @EventTarget(priority = EventPriority.HIGH)
   private void onRender2D(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if (this.itemStack == null) {
            this.itemStack = new ItemStack(Items.ELYTRA);
         }

         boolean flag = this.display.isSelected("Игроки");
         boolean flag2 = this.display.isSelected("Мобы");
         boolean flag3 = this.display.isSelected("Предметы");
         boolean flag4 = flag && (Boolean)this.itemOutput.getValue();
         boolean flag5 = flag && (Boolean)this.showArmor.getValue();
         boolean flag6 = flag3 && (Boolean)this.itemIcon.getValue();
         if (flag || flag2 || flag3) {
            if (!flag5 && !this.integerMap.isEmpty()) {
               this.integerMap.clear();
            }

            if (!flag6 && !this.integerMap2.isEmpty()) {
               this.integerMap2.clear();
            }

            if (this.stringMap.size() > 256) {
               this.stringMap.clear();
            }

            if (this.level != MinecraftAccess.MINECRAFT.level) {
               this.uUIDSet.clear();
               this.level = MinecraftAccess.MINECRAFT.level;
            }

            this.c5List.clear();
            if (flag5) {
               this.integerSet.clear();
            }

            if (flag6) {
               this.integerSet2.clear();
            }

            CameraRenderState cameraRenderState = MinecraftAccess.MINECRAFT.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
            WorldToScreen.setMatrices(new Matrix4f(cameraRenderState.projectionMatrix), new Matrix4f(cameraRenderState.viewRotationMatrix));
            float floatValue = event.getFloatValue();
            var entityList = MinecraftAccess.MINECRAFT.level.entitiesForRendering();
            ScoreboardHealth scoreboardHealth = flag ? Onetap.MODULE_MANAGER.get(ScoreboardHealth.class) : null;
            NameProtect nameProtect = Onetap.MODULE_MANAGER.get(NameProtect.class);
            float floatValue2 = !flag && !flag2 ? 0.0F : Draw2D.getTextWidth("[", Fonts.SUPPLIER15, 8.0F);
            float floatValue3 = !flag && !flag2 ? 0.0F : Draw2D.getTextWidth("[]", Fonts.SUPPLIER15, 8.0F);
            Vec3 vec3 = cameraRenderState.pos;

            for (Entity entity : entityList) {
               float floatValue4 = (float)entity.distanceToSqr(vec3.x, vec3.y, vec3.z);
               if (entity instanceof ItemEntity itemEntity && itemEntity.isAlive() && flag3) {
                  this.m_9(event, itemEntity, floatValue, flag6, floatValue4);
               }

               if (entity instanceof Player player && player.isAlive() && flag) {
                  if (player != MinecraftAccess.MINECRAFT.player || !MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson()) {
                     float floatValue5 = scoreboardHealth.m_0_float(player);
                     Component component = this.m_6_Component(player);
                     if (nameProtect != null && nameProtect.isEnabled()) {
                        component = nameProtect.replaceInComponent(component);
                     }

                     boolean flag7 = !Onetap.FRIEND_MANAGER.isEmpty() && Onetap.FRIEND_MANAGER.isFriend(player.getName().getString());
                     if (player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
                        this.uUIDSet.add(player.getUUID());
                     }

                     this.m_7(event, player, component, floatValue5, floatValue, floatValue2, floatValue3, flag7, this.uUIDSet.contains(player.getUUID()), floatValue4);
                     if (flag4) {
                        this.m_3(player, floatValue, floatValue4);
                     }

                     if (flag5) {
                        this.m_9(event, player, floatValue, floatValue4);
                     }
                  }
               } else if (entity instanceof LivingEntity livingEntity && livingEntity.isAlive() && flag2) {
                  this.m_7(event, livingEntity, livingEntity.getDisplayName(), livingEntity.getHealth(), floatValue, floatValue2, floatValue3, false, false, floatValue4);
               }
            }

            if (flag5) {
               this.integerMap.keySet().removeIf(integer -> !this.integerSet.contains(integer));
            }

            if (flag6) {
               this.integerMap2.keySet().removeIf(integer2 -> !this.integerSet2.contains(integer2));
            }

            this.c5List.sort((c5, c52) -> Float.compare(c52.depth(), c5.depth()));

            for (Tags.CallbackType c53 : this.c5List) {
               c53.m_0_void();
            }
         }
      }
   }

   private static boolean m_6(WorldToScreen.DataRecord c9) {
      Window window = MinecraftAccess.MINECRAFT.getWindow();
      float floatValue = window.getGuiScaledWidth();
      float floatValue2 = window.getGuiScaledHeight();
      return c9.m_2r_float() >= -80.0F && c9.m_2r_float() <= floatValue + 80.0F && c9.m_2___float() >= -80.0F && c9.m_2___float() <= floatValue2 + 80.0F;
   }

   private TextRenderer m_7(Component component) {
      boolean flag = ((String)this.donate.getValue()).equals("Картинка");
      Component component2 = flag ? ChatBadges.m_8_Component(component) : ChatBadges.m_6(component);
      String text = m_6(component2, flag);
      TextRenderer textRenderer = this.stringMap.get(text);
      if (textRenderer != null) {
         return textRenderer;
      }

      TextRenderer textRenderer2 = flag ? TextRenderer.m_2(component2, Fonts.SUPPLIER15, Fonts.SUPPLIER17) : TextRenderer.m_6(component2, Fonts.SUPPLIER15, Fonts.SUPPLIER17);
      this.stringMap.put(text, textRenderer2);
      return textRenderer2;
   }

   private static String m_6(Component component, boolean flag) {
      StringBuilder stringBuilder = new StringBuilder(flag ? "G" : "T");
      component.visit((style, text) -> {
         TextColor textColor = style.getColor();
         stringBuilder.append(textColor == null ? -1 : textColor.getValue()).append('\u0000');
         if (flag) {
            stringBuilder.append(style.getFont()).append('\u0000');
         }

         stringBuilder.append(text).append('\u0001');
         return Optional.empty();
      }, Style.EMPTY);
      return stringBuilder.toString();
   }

   private void m_7(Render2DEvent event, LivingEntity livingEntity, Component component, float floatValue, float floatValue2, float floatValue3, float floatValue4, boolean flag, boolean flag2, float floatValue5) {
      WorldToScreen.DataRecord c9 = WorldToScreen.project(
         livingEntity.xo + (livingEntity.getX() - livingEntity.xo) * floatValue2,
         livingEntity.yo + (livingEntity.getY() - livingEntity.yo) * floatValue2 + livingEntity.getBbHeight() + 0.5,
         livingEntity.zo + (livingEntity.getZ() - livingEntity.zo) * floatValue2
      );
      if (c9 != null && m_6(c9)) {
         float floatValue6 = getScale(floatValue5);
         TextRenderer textRenderer = this.m_7(component);
         String text = this.formatHealth(floatValue);
         float floatValue7 = textRenderer.getWidth(8.0F);
         float floatValue8 = Draw2D.getTextWidth(text, Fonts.SUPPLIER15, 8.0F);
         float floatValue9 = flag ? Draw2D.getTextWidth("[F]", Fonts.SUPPLIER15, 8.0F) : 0.0F;
         float floatValue10 = flag ? Draw2D.getTextWidth("F", Fonts.SUPPLIER15, 8.0F) : 0.0F;
         float floatValue11 = flag ? floatValue9 + 2.0F : 0.0F;
         float floatValue12 = floatValue7 + floatValue11 + floatValue8 + floatValue4 + 6.0F;
         float floatValue13 = c9.m_2r_float() - floatValue12 * floatValue6 / 2.0F;
         float floatValue14 = c9.m_2___float();
         float floatValue15 = floatValue13 + (4.0F + floatValue7) * floatValue6;
         float floatValue16 = floatValue15 + floatValue11 * floatValue6;
         ArrayList list = new ArrayList();
         list.add(new Tags.DataRecord4("[", floatValue16, ColorUtil.rgba(245, 245, 245, 255)));
         list.add(new Tags.DataRecord4(text, floatValue16 + floatValue3 * floatValue6, ColorUtil.rgba(255, 90, 110, 255)));
         list.add(new Tags.DataRecord4("]", floatValue16 + (floatValue3 + floatValue8) * floatValue6, ColorUtil.rgba(245, 245, 245, 255)));
         if (flag) {
            list.add(new Tags.DataRecord4("[", floatValue15, ColorUtil.rgba(100, 255, 100, 255)));
            list.add(new Tags.DataRecord4("F", floatValue15 + floatValue3 * floatValue6, ColorUtil.rgba(255, 255, 255, 255)));
            list.add(new Tags.DataRecord4("]", floatValue15 + (floatValue3 + floatValue10) * floatValue6, ColorUtil.rgba(100, 255, 100, 255)));
         }

         this.c5List.add(new Tags.DataRecord3(floatValue5, floatValue13, floatValue14, floatValue12 * floatValue6, textRenderer, floatValue13 + 2.0F * floatValue6, list, floatValue6));
         if (flag2) {
            float floatValue17 = floatValue13 + (floatValue12 + 3.0F) * floatValue6;
            float floatValue18 = floatValue14 - floatValue6;
            this.c5List.add(new Tags.DataRecord2(floatValue5, floatValue17, floatValue18, 12.0F * floatValue6, 12.0F * floatValue6, floatValue6));
            this.c5List.add(new Tags.DataRecord(floatValue5, this.m_6_TrackingItemStackRenderState(event), this.itemStack, floatValue17 + 0.5F * floatValue6, floatValue18 + 0.5F * floatValue6, floatValue6));
         }
      }
   }

   private TrackingItemStackRenderState m_6_TrackingItemStackRenderState(Render2DEvent event) {
      if (!ItemStack.matches(this.c3.itemStack, this.itemStack)) {
         this.c3.itemStack = this.itemStack.copy();
         MinecraftAccess.MINECRAFT
            .getItemModelResolver()
            .updateForTopItem(this.c3.trackingItemStackRenderState, this.itemStack, ItemDisplayContext.GUI, MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, this.itemStack.hashCode());
      }

      event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(this.c3.trackingItemStackRenderState.getModelIdentity());
      return this.c3.trackingItemStackRenderState;
   }

   private static float getScale(float floatValue) {
      float floatValue2 = (float)Math.sqrt(floatValue);
      return 1.0F + -0.14999998F * (floatValue2 / (floatValue2 + 30.0F));
   }

   private void m_3(Player player, float floatValue, float floatValue2) {
      ItemStack itemStack2 = player.getMainHandItem();
      ItemStack itemStack3 = player.getOffhandItem();
      if (!itemStack2.isEmpty() || !itemStack3.isEmpty()) {
         WorldToScreen.DataRecord c9 = WorldToScreen.project(
            player.xo + (player.getX() - player.xo) * floatValue, player.yo + (player.getY() - player.yo) * floatValue - 0.5, player.zo + (player.getZ() - player.zo) * floatValue
         );
         if (c9 != null && m_6(c9)) {
            float floatValue3 = getScale(floatValue2);
            float floatValue4 = 14.0F;
            float floatValue5 = 2.0F;
            float floatValue6 = c9.m_2r_float();
            float floatValue7 = c9.m_2___float();
            if (!itemStack2.isEmpty()) {
               floatValue7 = this.m_7(itemStack2.getHoverName(), floatValue6, floatValue7, floatValue4, floatValue5, floatValue2, floatValue3);
            }

            if (!itemStack3.isEmpty()) {
               this.m_7(itemStack3.getHoverName(), floatValue6, floatValue7, floatValue4, floatValue5, floatValue2, floatValue3);
            }
         }
      }
   }

   private float m_7(Component component, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      TextRenderer textRenderer = this.m_7(component);
      if (textRenderer.isEmpty()) {
         return floatValue2;
      }

      float floatValue7 = textRenderer.getWidth(8.0F);
      float floatValue8 = floatValue - (floatValue7 + floatValue4 * 2.0F) * floatValue6 / 2.0F;
      this.c5List.add(new Tags.DataRecord3(floatValue5, floatValue8, floatValue2, (floatValue7 + floatValue4 * 2.0F) * floatValue6, textRenderer, floatValue8 + floatValue4 * floatValue6, List.of(), floatValue6));
      return floatValue2 + floatValue3 * floatValue6;
   }

   private void m_9(Render2DEvent event, Player player, float floatValue, float floatValue2) {
      int value = 0;

      for (EquipmentSlot equipmentSlot : EQUIPMENT_SLOT_ARRAY) {
         if (!player.getItemBySlot(equipmentSlot).isEmpty()) {
            value++;
         }
      }

      if (value != 0) {
         WorldToScreen.DataRecord c9 = WorldToScreen.project(
            player.xo + (player.getX() - player.xo) * floatValue,
            player.yo + (player.getY() - player.yo) * floatValue + player.getBbHeight() + 0.5,
            player.zo + (player.getZ() - player.zo) * floatValue
         );
         if (c9 != null && m_6(c9)) {
            int value2 = player.getId();
            this.integerSet.add(value2);
            Tags.StateHolder[] c3Array = this.integerMap.computeIfAbsent(value2, integer -> new Tags.StateHolder[EQUIPMENT_SLOT_ARRAY.length]);
            float floatValue3 = getScale(floatValue2);
            float floatValue4 = (value * 11.0F + 2.0F) * floatValue3;
            float floatValue5 = c9.m_2r_float() - floatValue4 / 2.0F;
            float floatValue6 = c9.m_2___float() - 15.0F * floatValue3;
            this.c5List.add(new Tags.DataRecord2(floatValue2, floatValue5, floatValue6, floatValue4, 13.0F * floatValue3, floatValue3));
            float floatValue7 = floatValue5 + floatValue3;

            for (int i = 0; i < EQUIPMENT_SLOT_ARRAY.length; i++) {
               ItemStack itemStack2 = player.getItemBySlot(EQUIPMENT_SLOT_ARRAY[i]);
               if (!itemStack2.isEmpty()) {
                  float floatValue8 = floatValue6 + floatValue3;
                  this.c5List.add(new Tags.DataRecord(floatValue2, this.m_2(event, c3Array, i, itemStack2), itemStack2, floatValue7, floatValue8, floatValue3));
                  floatValue7 += 11.0F * floatValue3;
               }
            }
         }
      }
   }

   private TrackingItemStackRenderState m_2(Render2DEvent event, Tags.StateHolder[] c3Array, int value, ItemStack itemStack2) {
      Tags.StateHolder c32 = c3Array[value];
      if (c32 == null) {
         c32 = new Tags.StateHolder();
      }

      if (!ItemStack.matches(c32.itemStack, itemStack2)) {
         c32.itemStack = itemStack2.copy();
         MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(c32.trackingItemStackRenderState, itemStack2, ItemDisplayContext.GUI, MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, itemStack2.hashCode());
      }

      event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(c32.trackingItemStackRenderState.getModelIdentity());
      return c32.trackingItemStackRenderState;
   }

   private TrackingItemStackRenderState m_1(Render2DEvent event, int value, ItemStack itemStack2) {
      Tags.StateHolder c32 = this.integerMap2.computeIfAbsent(value, integer -> new Tags.StateHolder());
      if (!ItemStack.matches(c32.itemStack, itemStack2)) {
         c32.itemStack = itemStack2.copy();
         MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(c32.trackingItemStackRenderState, itemStack2, ItemDisplayContext.GUI, MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, itemStack2.hashCode());
      }

      event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(c32.trackingItemStackRenderState.getModelIdentity());
      return c32.trackingItemStackRenderState;
   }

   private void m_9(Render2DEvent event, ItemEntity itemEntity, float floatValue, boolean flag, float floatValue2) {
      WorldToScreen.DataRecord c9 = WorldToScreen.project(itemEntity.xo + (itemEntity.getX() - itemEntity.xo) * floatValue, itemEntity.yo + (itemEntity.getY() - itemEntity.yo) * floatValue + 0.3, itemEntity.zo + (itemEntity.getZ() - itemEntity.zo) * floatValue);
      if (c9 != null && m_6(c9)) {
         float floatValue3 = getScale(floatValue2);
         TextRenderer textRenderer = this.m_7(itemEntity.getItem().getHoverName());
         int value = itemEntity.getItem().getCount();
         float floatValue4 = textRenderer.getWidth(8.0F);
         String text = value > 1 ? "x" + value : "";
         float floatValue5 = text.isEmpty() ? 0.0F : Draw2D.getTextWidth(text, Fonts.SUPPLIER15, 8.0F);
         float floatValue6 = floatValue4 + floatValue5 + (text.isEmpty() ? 4.0F : 6.0F);
         float floatValue7 = c9.m_2r_float() - floatValue6 * floatValue3 / 2.0F;
         List list = text.isEmpty() ? List.of() : List.of(new Tags.DataRecord4(text, floatValue7 + (4.0F + floatValue4) * floatValue3, ColorUtil.rgba(165, 165, 165, 255)));
         this.c5List.add(new Tags.DataRecord3(floatValue2, floatValue7, c9.m_2___float(), floatValue6 * floatValue3, textRenderer, floatValue7 + 2.0F * floatValue3, list, floatValue3));
         if (flag) {
            this.integerSet2.add(itemEntity.getId());
            float floatValue8 = 13.0F * floatValue3;
            float floatValue9 = c9.m_2r_float() - floatValue8 / 2.0F;
            float floatValue10 = c9.m_2___float() - 15.0F * floatValue3;
            this.c5List.add(new Tags.DataRecord2(floatValue2, floatValue9, floatValue10, floatValue8, 13.0F * floatValue3, floatValue3));
            this.c5List.add(new Tags.DataRecord(floatValue2, this.m_1(event, itemEntity.getId(), itemEntity.getItem()), itemEntity.getItem(), floatValue9 + floatValue3, floatValue10 + floatValue3, floatValue3));
         }
      }
   }

   private String formatHealth(float floatValue) {
      int value = Math.round(Math.max(0.0F, floatValue) * 10.0F);
      return value / 10 + "." + value % 10;
   }

   private Component m_6_Component(Player player) {
      PlayerInfo playerInfo = MinecraftAccess.MINECRAFT.getConnection() == null ? null : MinecraftAccess.MINECRAFT.getConnection().getPlayerInfo(player.getUUID());
      if (playerInfo != null) {
         Component component = playerInfo.getTabListDisplayName();
         if (component != null) {
            return m_7(ChatBadges.m_8_Component(component), player.getName().getString());
         }
      }

      return player.getDisplayName();
   }

   private static Component m_7(Component component, String text) {
      if (component.getString().contains(text)) {
         return component;
      }

      MutableComponent mutableComponent = Component.empty();
      boolean[] flagArray = new boolean[1];
      component.visit((style, text2) -> {
         Matcher matcher = flagArray[0] ? null : PATTERN.matcher(text2);
         if (matcher != null && matcher.find()) {
            text2 = text2.substring(0, matcher.start()) + text + text2.substring(matcher.end());
         }

         mutableComponent.append(Component.literal(text2).setStyle(style));
         return Optional.empty();
      }, Style.EMPTY);
      return flagArray[0] ? mutableComponent : component;
   }

   static {
      m_1(new String[4]);
   }

   public static void m_1(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_7_StringArr() {
      return textArray;
   }

   private record DataRecord(float depth, TrackingItemStackRenderState f_07, ItemStack f_2f, float f_1_i, float f_1_d, float f_14) implements Tags.CallbackType {
      @Override
      public void m_0_void() {
         Render2D.drawItem(this.f_07, this.f_1_i, this.f_1_d, 11.0F * this.f_14, ColorUtil.rgba(255, 255, 255, 255));
         Render2D.drawItemDecorations(this.f_2f, this.f_1_i, this.f_1_d, 11.0F * this.f_14, 255);
      }

      public TrackingItemStackRenderState m_07() {
         return this.f_07;
      }

      public ItemStack m_2f() {
         return this.f_2f;
      }

      public float m_1_i() {
         return this.f_1_i;
      }

      public float m_1_d_float() {
         return this.f_1_d;
      }

      public float m_14_float() {
         return this.f_14;
      }
   }

   private record DataRecord2(float depth, float f_4_g, float f_41, float f_4_p, float f_4t, float f_4_k) implements Tags.CallbackType {
      @Override
      public void m_0_void() {
         Draw2D.rect(this.f_4_g, this.f_41, this.f_4_p, this.f_4t, ColorUtil.rgba(0, 0, 0, 75));
      }

      public float m_4_g() {
         return this.f_4_g;
      }

      public float m_41() {
         return this.f_41;
      }

      public float m_4_p() {
         return this.f_4_p;
      }

      public float m_4t() {
         return this.f_4t;
      }

      public float m_4_k() {
         return this.f_4_k;
      }
   }

   private static final class StateHolder {
      private ItemStack itemStack;
      private final TrackingItemStackRenderState trackingItemStackRenderState;

      private StateHolder() {
         this.itemStack = ItemStack.EMPTY;
         this.trackingItemStackRenderState = new TrackingItemStackRenderState();
      }
   }

   private sealed interface CallbackType permits Tags.DataRecord3, Tags.DataRecord2, Tags.DataRecord {
      float depth();

      void m_0_void();
   }

   private record DataRecord3(float depth, float f_35, float f_3q, float f_3u, TextRenderer f_0_e, float f_3_b, List<Tags.DataRecord4> f_3g, float f_3_s) implements Tags.CallbackType {
      @Override
      public void m_0_void() {
         Draw2D.rect(this.f_35 - this.f_3_s, this.f_3q - this.f_3_s, this.f_3u + 2.0F * this.f_3_s, 12.0F * this.f_3_s, ColorUtil.rgba(0, 0, 0, 75));
         Notifications.m_5_C3_sArr();
         this.f_0_e.drawOpaque(this.f_3_b, this.f_3q, 8.0F * this.f_3_s);

         for (Tags.DataRecord4 c9 : this.f_3g) {
            Draw2D.drawStyledText(c9.m_1_v(), Fonts.SUPPLIER15, c9.m_2_s(), this.f_3q, c9.m_6_y_int(), 8.0F * this.f_3_s);
         }
      }

      public float m_35() {
         return this.f_35;
      }

      public float m_3q() {
         return this.f_3q;
      }

      public float m_3u() {
         return this.f_3u;
      }

      public TextRenderer m_0_e() {
         return this.f_0_e;
      }

      public float m_3_b() {
         return this.f_3_b;
      }

      public List<Tags.DataRecord4> m_3g() {
         return this.f_3g;
      }

      public float m_3_s() {
         return this.f_3_s;
      }
   }

   private record DataRecord4(String f_1_v, float f_2_s, int f_6_y) {
      public String m_1_v() {
         return this.f_1_v;
      }

      public float m_2_s() {
         return this.f_2_s;
      }

      public int m_6_y_int() {
         return this.f_6_y;
      }
   }
}
