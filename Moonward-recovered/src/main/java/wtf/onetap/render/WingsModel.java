package wtf.onetap.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import wtf.onetap.module.Module;

public class WingsModel extends Model<AvatarRenderState> {
   public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "textures/model/wings.png");
   private final ModelPart modelPart;
   private final ModelPart modelPart2;
   private static Module[] moduleArray;

   public WingsModel() {
      m_1_C3_sArr();
      super(m_5_LayerDefinition().bakeRoot(), RenderTypes::armorCutoutNoCull);
      this.modelPart = super.root.getChild("wing");
      this.modelPart2 = this.modelPart.getChild("wingTip");
   }

   private static LayerDefinition m_5_LayerDefinition() {
      MeshDefinition meshDefinition = new MeshDefinition();
      PartDefinition partDefinition = meshDefinition.getRoot();
      PartDefinition partDefinition2 = partDefinition.addOrReplaceChild(
         "wing",
         CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F).texOffs(-10, 8).addBox(-10.0F, 0.0F, 0.5F, 10.0F, 0.01F, 10.0F),
         PartPose.offset(-2.0F, 0.0F, 0.0F)
      );
      partDefinition2.addOrReplaceChild(
         "wingTip",
         CubeListBuilder.create()
            .texOffs(0, 5)
            .addBox(-10.0F, -0.5F, -0.5F, 10.0F, 1.0F, 1.0F)
            .texOffs(-10, 18)
            .addBox(-10.0F, 0.0F, 0.5F, 10.0F, 0.01F, 10.0F),
         PartPose.offset(-10.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshDefinition, 30, 30);
   }

   public void setupAnim(AvatarRenderState avatarRenderState) {
      float floatValue = avatarRenderState.ageInTicks * 0.35F;
      m_1_C3_sArr();
      this.modelPart.xRot = (float)(Math.toRadians(-80.0) - Math.cos(floatValue) * 0.2F);
      this.modelPart.yRot = (float)(Math.toRadians(30.0) + Math.sin(floatValue) * 0.4F);
      this.modelPart.zRot = (float)Math.toRadians(20.0);
      this.modelPart2.xRot = 0.0F;
      this.modelPart2.yRot = 0.0F;
      this.modelPart2.zRot = (float)(-(Math.sin(floatValue + 2.0F) + 0.5) * 0.75);
   }

   public ModelPart getModelPart() {
      return this.modelPart;
   }

   public ModelPart getModelPart2() {
      return this.modelPart2;
   }

   static {
      m_8(new Module[5]);
   }

   public static void m_8(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_1_C3_sArr() {
      return moduleArray;
   }
}
