package wtf.onetap.module.misc;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.utils.PlayerMovementInput;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "UnHook", description = "анхук софта чо писать скрывать клиент", category = Category.MISC)
public class UnHook extends Module {
   private final KeybindSetting returnBind = new KeybindSetting("Бинд возврата", 301).build();
   private final List<Module> moduleList = new ArrayList<>();
   private boolean flag;
   private static int[] valueArray;

   public boolean isFlag() {
      return this.flag;
   }

   public boolean isHidden(Module module) {
      return this.moduleList.contains(module);
   }

   public int getReturnKey() {
      int value = (Integer)this.returnBind.getValue();
      return value >= 32 && value <= 348 ? value : 301;
   }

   @Override
   public void onEnable() {
      m_7_IArr();
      this.moduleList.clear();

      for (Module module : Onetap.MODULE_MANAGER.getModules()) {
         if (module != this && module.isEnabled()) {
            this.moduleList.add(module);
         }
      }

      for (Module module2 : this.moduleList) {
         try {
            module2.setEnabled(false);
         } catch (RuntimeException e) {
            Onetap.LOGGER.error("Не удалось полностью отключить модуль {}", module2.getName(), e);
         }
      }

      if (MinecraftAccess.MINECRAFT.gui.screen() != null && MinecraftAccess.MINECRAFT.gui.screen().getClass().getName().startsWith("wtf.onetap.")) {
         MinecraftAccess.MINECRAFT.gui.setScreen(null);
      }

      Onetap.ROTATION_MODES.syncWithPlayer();
      Onetap.ROTATION_MODES.reset();
      this.flag = true;
      if (MinecraftAccess.MINECRAFT.gui.screen() instanceof TitleScreen) {
         MinecraftAccess.MINECRAFT.gui.setScreen(new TitleScreen());
      }

      for (IBaritone iBaritone : BaritoneAPI.getProvider().getAllBaritones()) {
         try {
            iBaritone.getPathingBehavior().forceCancel();
         } catch (RuntimeException e2) {
            Onetap.LOGGER.error("Не удалось остановить Baritone", e2);
         } finally {
            iBaritone.getInputOverrideHandler().clearAllKeys();
            if (iBaritone instanceof Baritone baritone) {
               baritone.getInputOverrideHandler().getBlockBreakHelper().stopBreakingBlock();
            }

            LocalPlayer localPlayer = iBaritone.getPlayerContext().player();
            if (localPlayer != null && localPlayer.input instanceof PlayerMovementInput) {
               localPlayer.input = new KeyboardInput(iBaritone.getPlayerContext().minecraft().options);
            }
         }
      }

      ChatUtil.m_3_void();
   }

   @Override
   public void onDisable() {
      this.flag = false;
      if (MinecraftAccess.MINECRAFT.gui.screen() instanceof TitleScreen) {
         MinecraftAccess.MINECRAFT.gui.setScreen(new TitleScreen());
      }

      for (Module module : this.moduleList) {
         if (!(module instanceof ClickGUI)) {
            try {
               module.setEnabled(true);
            } catch (RuntimeException e) {
               Onetap.LOGGER.error("Не удалось восстановить модуль {}", module.getName(), e);
            }
         }
      }

      this.moduleList.clear();
   }

   public static void m_2(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_7_IArr() {
      return valueArray;
   }

   static {
      m_2(new int[2]);
   }
}
