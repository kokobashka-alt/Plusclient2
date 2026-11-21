package wtf.onetap.module.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;

@ModuleInformation(name = "View Model", description = "Кастомное расположение рук", category = Category.RENDER)
public class ViewModel extends Module {
   public final FloatSetting leftHandX = new FloatSetting("Левая рука по X", 0.0F, -2.0F, 2.0F, 0.1F).build();
   public final FloatSetting leftHandY = new FloatSetting("Левая рука по Y", 0.0F, -2.0F, 2.0F, 0.1F).build();
   public final FloatSetting leftHandZ = new FloatSetting("Левая рука по Z", 0.0F, -2.0F, 2.0F, 0.1F).build();
   public final FloatSetting rightHandX = new FloatSetting("Правая рука по X", 0.0F, -2.0F, 2.0F, 0.1F).build();
   public final FloatSetting rightHandY = new FloatSetting("Правая рука по Y", 0.0F, -2.0F, 2.0F, 0.1F).build();
   public final FloatSetting rightHandZ = new FloatSetting("Правая рука по Z", 0.0F, -2.0F, 2.0F, 0.1F).build();

   public static void m_4(PoseStack poseStack, HumanoidArm humanoidArm, ItemStack itemStack) {
      Notifications.m_5_C3_sArr();
      ViewModel viewModel = Onetap.MODULE_MANAGER.get(ViewModel.class);
      if (viewModel != null && viewModel.isEnabled() && itemStack != null && !itemStack.isEmpty() && !itemStack.has(DataComponents.MAP_ID)) {
         if (humanoidArm == HumanoidArm.LEFT) {
            poseStack.translate(viewModel.leftHandX.getFloat(), viewModel.leftHandY.getFloat(), viewModel.leftHandZ.getFloat());
         } else {
            poseStack.translate(viewModel.rightHandX.getFloat(), viewModel.rightHandY.getFloat(), viewModel.rightHandZ.getFloat());
         }
      }
   }
}
