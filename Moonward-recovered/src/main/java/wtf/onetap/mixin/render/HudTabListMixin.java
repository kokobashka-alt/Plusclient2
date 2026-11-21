package wtf.onetap.mixin.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.BetterMinecraft;
import wtf.onetap.obf.C6_e;

@Mixin(Hud.class)
public class HudTabListMixin {
   @Shadow
   @Final
   private Minecraft minecraft;
   @Shadow
   @Final
   private PlayerTabOverlay tabList;

   @Inject(method = "extractTabList", at = @At("HEAD"), cancellable = true)
   private void onetap$animatedTab(GuiGraphicsExtractor var1, DeltaTracker var2, CallbackInfo var3) {
      BetterMinecraft var4 = Onetap.MODULE_MANAGER.get(BetterMinecraft.class);
      if (var4 != null && var4.isEnabled() && var4.tabAnimation.getValue()) {
         Scoreboard var5 = this.minecraft.level.getScoreboard();
         Objective var6 = var5.getDisplayObjective(DisplaySlot.LIST);
         boolean var7 = this.minecraft.options.keyPlayerList.isDown()
            && (!this.minecraft.isLocalServer() || this.minecraft.player.connection.getListedOnlinePlayers().size() > 1 || var6 != null);
         float var8 = C6_e.m_0_float(var7);
         if (var8 < 0.01F) {
            this.tabList.setVisible(false);
            var3.cancel();
         } else {
            this.tabList.setVisible(true);
            var1.nextStratum();
            Matrix3x2fStack var9 = var1.pose();
            C6_e.m_8_void(var8);
            C6_e.m_8(var9, var1.guiWidth(), var8);

            try {
               this.tabList.extractRenderState(var1, var1.guiWidth(), var5, var6);
            } finally {
               var9.popMatrix();
               C6_e.m_0_void();
            }

            var3.cancel();
         }
      }
   }
}
