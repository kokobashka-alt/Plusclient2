package baritone.api.utils;

import baritone.api.BaritoneAPI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import wtf.onetap.util.ChatUtil;

public interface Helper {
   Helper HELPER = new Helper() {};
   String FORCE_COMMAND_PREFIX = String.format("<<%s>>", UUID.randomUUID());
   @Deprecated
   Minecraft mc = Minecraft.getInstance();
   GuiMessageTag MESSAGE_TAG = new GuiMessageTag(16733695, null, Component.literal("Baritone message."), "Baritone");

   static Component getPrefix() {
      Calendar var0 = Calendar.getInstance();
      boolean var1 = var0.get(2) == 3 && var0.get(5) <= 3;
      MutableComponent var2 = Component.literal(var1 ? "Baritoe" : (BaritoneAPI.getSettings().shortBaritonePrefix.value ? "B" : "Baritone"));
      var2.setStyle(var2.getStyle().withColor(ChatFormatting.LIGHT_PURPLE));
      MutableComponent var3 = Component.literal("");
      var3.setStyle(var2.getStyle().withColor(ChatFormatting.DARK_PURPLE));
      var3.append("[");
      var3.append(var2);
      var3.append("]");
      return var3;
   }

   default void logToast(Component var1, Component var2) {
      Minecraft.getInstance().execute(() -> {
         if (!ChatUtil.isUnHooked()) {
            BaritoneAPI.getSettings().toaster.value.accept(var1, var2);
         }
      });
   }

   default void logToast(String var1, String var2) {
      this.logToast(Component.literal(var1), Component.literal(var2));
   }

   default void logToast(String var1) {
      this.logToast(getPrefix(), Component.literal(var1));
   }

   default void logNotification(String var1) {
      this.logNotification(var1, false);
   }

   default void logNotification(String var1, boolean var2) {
      if (BaritoneAPI.getSettings().desktopNotifications.value) {
         this.logNotificationDirect(var1, var2);
      }
   }

   default void logNotificationDirect(String var1) {
      this.logNotificationDirect(var1, false);
   }

   default void logNotificationDirect(String var1, boolean var2) {
      Minecraft.getInstance().execute(() -> {
         if (!ChatUtil.isUnHooked()) {
            BaritoneAPI.getSettings().notifier.value.accept(var1, var2);
         }
      });
   }

   default void logDebug(String var1) {
      if (BaritoneAPI.getSettings().chatDebug.value) {
         this.logDirect(var1, false);
      }
   }

   default void logDirect(boolean var1, Component... var2) {
      MutableComponent var3 = Component.literal("");
      if (!var1 && !BaritoneAPI.getSettings().useMessageTag.value) {
         var3.append(getPrefix());
         var3.append(Component.literal(" "));
      }

      Arrays.asList(var2).forEach(var3::append);
      if (var1) {
         this.logToast(getPrefix(), var3);
      } else {
         Minecraft.getInstance().execute(() -> {
            if (!ChatUtil.isUnHooked()) {
               BaritoneAPI.getSettings().logger.value.accept(var3);
            }
         });
      }
   }

   default void logDirect(Component... var1) {
      this.logDirect(BaritoneAPI.getSettings().logAsToast.value, var1);
   }

   default void logDirect(String var1, ChatFormatting var2, boolean var3) {
      Stream.of(var1.split("\n")).forEach(var3x -> {
         MutableComponent var4 = Component.literal(var3x.replace("\t", "    "));
         var4.setStyle(var4.getStyle().withColor(var2));
         this.logDirect(var3, var4);
      });
   }

   default void logDirect(String var1, ChatFormatting var2) {
      this.logDirect(var1, var2, BaritoneAPI.getSettings().logAsToast.value);
   }

   default void logDirect(String var1, boolean var2) {
      this.logDirect(var1, ChatFormatting.GRAY, var2);
   }

   default void logDirect(String var1) {
      this.logDirect(var1, BaritoneAPI.getSettings().logAsToast.value);
   }

   default void logUnhandledException(Throwable var1) {
      HELPER.logDirect(
         "An unhandled exception occurred. The error is in your game's log, please report this at https://github.com/cabaletta/baritone/issues",
         ChatFormatting.RED
      );
      var1.printStackTrace();
   }
}
