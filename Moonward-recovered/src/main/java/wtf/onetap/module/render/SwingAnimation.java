package wtf.onetap.module.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;

@ModuleInformation(name = "Swing Animation", description = "Кастомная анимация удара и рендер рук", category = Category.RENDER)
public class SwingAnimation extends Module {
   private final ModeSetting mode = new ModeSetting("Мод", "Никакой", "Никакой", "Smooth", "Self", "Self 2", "Down", "Forward", "Touch", "BlockHit", "Pander", "Curt", "3D")
      .build();
   private final ModeSetting version = new ModeSetting("Версия", "New", "Old", "New").visibleWhen(() -> ((String)this.mode.getValue()).equals("3D")).build();
   public final FloatSetting hitSpeed = new FloatSetting("Скорость удара", 1.0F, 0.5F, 2.0F, 0.05F)
      .visibleWhen(() -> ((String)this.mode.getValue()).equals("3D") && ((String)this.version.getValue()).equals("New"))
      .build();
   private final FloatSetting strength = new FloatSetting("Сила", 3.0F, 0.0F, 10.0F, 1.0F)
      .visibleWhen(
         () -> !((String)this.mode.getValue()).equals("Pander")
            && !((String)this.mode.getValue()).equals("BlockHit")
            && !((String)this.mode.getValue()).equals("Forward")
            && !((String)this.mode.getValue()).equals("Curt")
            && !((String)this.mode.getValue()).equals("Никакой")
            && !((String)this.mode.getValue()).equals("3D")
      )
      .build();
   public final FloatSetting speed = new FloatSetting("Скорость", 7.0F, 0.0F, 10.0F, 1.0F)
      .visibleWhen(() -> !((String)this.mode.getValue()).equals("3D") && !((String)this.mode.getValue()).equals("Никакой"))
      .build();
   private final FloatSetting angle = new FloatSetting("Угол", 90.0F, 0.0F, 360.0F, 5.0F)
      .visibleWhen(() -> ((String)this.mode.getValue()).equals("Self") || ((String)this.mode.getValue()).equals("Self 2"))
      .build();
   private final BooleanSetting onlyWithAura = new BooleanSetting("Только с аурой", false)
      .visibleWhen(() -> !((String)this.mode.getValue()).equals("Никакой") && !((String)this.mode.getValue()).equals("3D"))
      .build();

   public boolean m_5_boolean() {
      return this.isEnabled() && ((String)this.mode.getValue()).equals("3D");
   }

   public boolean m_7_boolean() {
      return this.isEnabled() && ((String)this.mode.getValue()).equals("3D") && ((String)this.version.getValue()).equals("New");
   }

   public int m_7(LocalPlayer localPlayer) {
      float floatValue = localPlayer.getCurrentItemAttackStrengthDelay();
      int value = Float.isFinite(floatValue) && floatValue > 0.0F ? Math.max(1, Mth.floor(floatValue)) : 20;
      return Math.max(1, Math.round(value / this.hitSpeed.getFloat()));
   }

   public boolean m_1_boolean() {
      return this.isEnabled()
         && !((String)this.mode.getValue()).equals("Никакой")
         && !((String)this.mode.getValue()).equals("3D")
         && (!(Boolean)this.onlyWithAura.getValue() || KillAura.livingEntity != null);
   }

   public void m_4(PoseStack poseStack, float floatValue, float floatValue2, HumanoidArm humanoidArm) {
      Notifications.m_5_C3_sArr();
      float floatValue3 = (float)Math.sin(floatValue * (Math.PI / 2) * 2.0);
      float floatValue4 = Mth.sin(Mth.sqrt(floatValue) * (float) Math.PI);
      int value = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
      switch ((String)this.mode.getValue()) {
         case "Smooth":
            poseStack.translate(0.56F * value, -0.52F, -0.72F);
            float floatValue5 = (Float)this.strength.getValue() * 10.0F;
            float floatValue6 = Mth.sin(floatValue * floatValue * (float) Math.PI);
            poseStack.mulPose(Axis.YP.rotationDegrees(45.0F + floatValue6 * (-floatValue5 / 4.0F)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(floatValue4 * -(floatValue5 / 4.0F)));
            poseStack.mulPose(Axis.XP.rotationDegrees(floatValue4 * -floatValue5));
            poseStack.mulPose(Axis.YP.rotationDegrees(-45.0F));
            break;
         case "Self 2":
            poseStack.translate(0.56F * value, -0.52F, -0.72F);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-30.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(-(Float)this.angle.getValue() - (Float)this.strength.getValue() * 10.0F * floatValue3));
            break;
         case "Forward":
            poseStack.translate(0.56F * value, -0.52F, -0.72F);
            poseStack.translate(0.0, 0.0, -0.3 * floatValue4);
            poseStack.mulPose(Axis.XP.rotationDegrees(floatValue4 * -35.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(floatValue4 * 35.0F));
            break;
         case "Self":
            poseStack.translate(0.56F * value, -0.52F, -0.72F);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-60.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(-(Float)this.angle.getValue() - (Float)this.strength.getValue() * 10.0F * floatValue3));
            break;
         case "Down":
            poseStack.translate(0.56F * value, -0.52F - floatValue3 * (Float)this.strength.getValue() / 24.0F, -0.72F);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-30.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            break;
         case "Touch":
            poseStack.translate(0.56F * value, -0.52F, -0.72F);
            poseStack.scale(1.0F, 1.0F, 1.0F + floatValue3 * (Float)this.strength.getValue() / 4.0F);
            poseStack.translate(0.0F, 0.0F, -0.265F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-100.0F));
            break;
         case "Curt":
            poseStack.translate(0.56F * value, -0.52F, -0.72F);
            float floatValue7 = Mth.sqrt(floatValue);
            float floatValue8 = Mth.sin(floatValue7 * (float) Math.PI);
            float floatValue9 = Mth.sin(floatValue * (float) Math.PI);
            poseStack.translate(0.4F - floatValue8 * 0.2F, -0.2F + floatValue8 * 0.3F, -0.5F - floatValue9 * 0.2F);
            poseStack.mulPose(Axis.YP.rotationDegrees(91.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-40.0F + floatValue8 * -100.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(-60.0F));
            break;
         case "Pander":
            poseStack.translate(0.56F * value, -0.52F, -0.72F);
            poseStack.scale(0.8F, 0.8F, 0.8F);
            float floatValue10 = floatValue2;
            poseStack.translate(0.3F - floatValue3 * 0.15F, 0.2F - floatValue10 * 0.12F, -0.15F - floatValue3 * 0.13F);
            poseStack.mulPose(Axis.YP.rotationDegrees(76.0F - 10.0F * floatValue3));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-16.0F - 8.0F * floatValue3));
            poseStack.mulPose(Axis.XP.rotationDegrees(-83.0F - 26.0F * floatValue3));
            break;
         case "BlockHit":
            poseStack.translate(0.56F * value, -0.52F, -0.72F);
            float floatValue11 = Mth.sin((float)(floatValue * floatValue * Math.PI));
            poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
            float floatValue12 = Mth.sin((float)(Mth.sqrt(floatValue) * Math.PI));
            poseStack.mulPose(Axis.XP.rotationDegrees(floatValue11 * -20.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(floatValue12 * -20.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(floatValue12 * -80.0F));
            poseStack.translate(0.4F, 0.2F, 0.2F);
            poseStack.translate(-0.5F, 0.08F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(20.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(-80.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(20.0F));
      }
   }
}
