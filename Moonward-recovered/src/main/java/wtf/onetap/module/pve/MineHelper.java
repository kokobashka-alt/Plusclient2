package wtf.onetap.module.pve;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.BlockBreakEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.render.BossHealthOverlayAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.render.Notifications;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "Mine Helper", description = "Хелпер авто-шахты FunTime/SpookyTime", category = Category.PVE)
public class MineHelper extends Module {
   private static final Pattern PATTERN = Pattern.compile("\\d{2}:\\d{2}");
   private final MultiModeSetting actions = new MultiModeSetting("Действия", "Следующая шахта", "Следующая шахта", "Сохранить кирку").build();
   private MineHelper.StateHolder c7;
   private int value;
   private final TimeHelper timeHelper = new TimeHelper();

   public boolean m_7_boolean() {
      return this.isEnabled() && this.actions.isSelected("Сохранить кирку") && this.value > 0;
   }

   public MineHelper() {
      this.actions.setValue(new LinkedHashSet<>(List.of("Следующая шахта", "Сохранить кирку")));
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if (this.actions.isSelected("Следующая шахта") && this.c7 != null) {
            float floatValue = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth();
            float floatValue2 = Fonts.SUPPLIER14.get().m_1_float(14.0F);
            float floatValue3 = this.getBossBarCount() * 45.0F;
            int value2 = ColorUtil.rgba(230, 230, 230, 255);
            this.m_9(floatValue, floatValue3, "Следующая: ", this.c7.getText(), value2, this.getRarityColor(this.c7.getText()));
            this.m_9(floatValue, floatValue3 + floatValue2, "Осталось: ", this.c7.formatTime(), value2, this.getTimeColor(this.c7.getRemainingSeconds()));
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if (this.value > 0) {
            this.value--;
         }

         if (this.actions.isSelected("Следующая шахта")) {
            ArrayList<net.minecraft.world.entity.decoration.ArmorStand> armorStandList = new ArrayList<>();

            for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
               if (entity instanceof ArmorStand armorStand) {
                  String text = this.getArmorStandName(armorStand);
                  if (text.contains("Авто-Шахта") || text.contains("Следующая:") || text.contains("Обновление через:") || PATTERN.matcher(text).matches()) {
                     armorStandList.add(armorStand);
                  }
               }
            }

            armorStandList.sort(Comparator.comparingDouble(armorStand2 -> -armorStand2.getY()));
            var optional = this.m_8_Optional(armorStandList);
            this.c7 = (MineHelper.StateHolder)optional.orElse(null);
         } else {
            this.c7 = null;
         }

         if (this.actions.isSelected("Сохранить кирку")) {
            ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedItem();
            if (itemStack.isEmpty() || !itemStack.is(ItemTags.PICKAXES) || !itemStack.isDamageableItem() || getDurabilityFraction(itemStack) > 0.15F) {
               return;
            }

            this.value = Math.max(this.value, 40);
         }
      }
   }

   @EventTarget
   private void onBlockBreak(BlockBreakEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && this.actions.isSelected("Сохранить кирку")) {
         if (this.value > 0) {
            event.cancel();
         } else {
            ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedItem();
            if (!itemStack.isEmpty() && itemStack.is(ItemTags.PICKAXES) && itemStack.isDamageableItem()) {
               if (getDurabilityFraction(itemStack) <= 0.15F) {
                  if (this.timeHelper.hasElapsed(5000L)) {
                     Notifications notifications = Onetap.MODULE_MANAGER.get(Notifications.class);
                     if (notifications != null) {
                        notifications.m_0(Component.literal("Кирка почти сломана!"), itemStack.copy());
                     }

                     this.timeHelper.reset();
                  }

                  event.cancel();
               }
            }
         }
      }
   }

   private Optional<MineHelper.StateHolder> m_8_Optional(List<ArmorStand> armorStandList) {
      if (armorStandList.size() < 4) {
         return Optional.empty();
      }

      String text = this.getArmorStandName((ArmorStand)armorStandList.get(0));
      String text2 = this.getArmorStandName((ArmorStand)armorStandList.get(1));
      String text3 = this.getArmorStandName((ArmorStand)armorStandList.get(2));
      String text4 = this.getArmorStandName((ArmorStand)armorStandList.get(3));
      if (text.contains("Авто-Шахта") && text2.contains("Следующая:") && text3.contains("Обновление через:") && PATTERN.matcher(text4).matches()) {
         String[] stringArray = text4.split(":");
         if (stringArray.length != 2) {
            return Optional.empty();
         }

         int value2 = Integer.parseInt(stringArray[0]) * 60 + Integer.parseInt(stringArray[1]);
         String text5 = text2.replace("Следующая: ", "").trim();
         return Optional.of(new MineHelper.StateHolder(text5, value2, System.currentTimeMillis()));
      } else {
         return Optional.empty();
      }
   }

   private String getArmorStandName(ArmorStand armorStand) {
      return armorStand.getCustomName() != null ? armorStand.getCustomName().getString() : "";
   }

   private int getTimeColor(int value2) {
      if (value2 <= 10) {
         return -43691;
      } else {
         return value2 <= 60 ? -171 : -11141291;
      }
   }

   private int getRarityColor(String text) {
      String text2 = text.toLowerCase(Locale.ROOT);
      if (text2.contains("мифическ")) {
         return -5614081;
      } else {
         return text2.contains("легендарн") ? -22016 : -5592406;
      }
   }

   private static float getDurabilityFraction(ItemStack itemStack) {
      return 1.0F - (float)itemStack.getDamageValue() / itemStack.getMaxDamage();
   }

   private int getBossBarCount() {
      return MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.hud != null ? ((BossHealthOverlayAccessor)MinecraftAccess.MINECRAFT.gui.hud.getBossOverlay()).onetap$getEvents().size() : 0;
   }

   private void m_9(float floatValue, float floatValue2, String text, String text2, int value2, int value3) {
      float floatValue3 = Draw2D.getTextWidth(text, Fonts.SUPPLIER14, 14.0F);
      float floatValue4 = Draw2D.getTextWidth(text2, Fonts.SUPPLIER14, 14.0F);
      float floatValue5 = (floatValue - (floatValue3 + floatValue4)) / 2.0F;
      Draw2D.drawStyledText(text, Fonts.SUPPLIER14, floatValue5, floatValue2, value2, 14.0F);
      Draw2D.drawStyledText(text2, Fonts.SUPPLIER14, floatValue5 + floatValue3, floatValue2, value3, 14.0F);
   }

   @Override
   public void onDisable() {
      this.c7 = null;
      this.value = 0;
   }

   private static final class StateHolder {
      private final String text;
      private final int value;
      private final long longValue;

      private StateHolder(String text2, int value2, long longValue2) {
         this.text = text2;
         this.value = value2;
         this.longValue = longValue2;
      }

      private String getText() {
         return this.text;
      }

      private int getRemainingSeconds() {
         long longValue2 = (System.currentTimeMillis() - this.longValue) / 1000L;
         return (int)Math.max(0L, this.value - longValue2);
      }

      private boolean m_3_boolean() {
         return this.getRemainingSeconds() <= 0;
      }

      private String formatTime() {
         int value2 = this.getRemainingSeconds();
         return String.format("%02d:%02d", value2 / 60, value2 % 60);
      }

      private static String a(byte[] byteArray) {
         int value2 = 0;
         int value3;
         char[] charArray = new char[value3 = byteArray.length];

         for (int i = 0; i < value3; i++) {
            int value4;
            if ((value4 = 255 & byteArray[i]) < 192) {
               charArray[value2++] = (char)value4;
            } else if (value4 < 224) {
               char ch = (char)((char)(value4 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value2++] = ch;
            } else if (i < value3 - 2) {
               char ch2 = (char)((char)(value4 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value2++] = ch2;
            }
         }

         return new String(charArray, 0, value2);
      }
   }
}
