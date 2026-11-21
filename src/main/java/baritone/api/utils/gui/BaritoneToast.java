package baritone.api.utils.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public class BaritoneToast {
   private static final SystemToastId BARITONE_TOAST_ID = new SystemToastId(5000L);

   public static void addOrUpdate(Component var0, Component var1) {
      SystemToast.addOrUpdate(Minecraft.getInstance().gui.toastManager(), BARITONE_TOAST_ID, var0, var1);
   }
}
