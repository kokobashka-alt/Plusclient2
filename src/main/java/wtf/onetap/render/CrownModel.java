package wtf.onetap.render;

import java.util.List;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import wtf.onetap.util.RotationManager;

public class CrownModel extends Model<AvatarRenderState> {
   public static final List<CrownModel.DataRecord> C7_LIST = List.of(
      new CrownModel.DataRecord(
         "crown",
         0,
         0,
         Float.intBitsToFloat(-1063780352),
         Float.intBitsToFloat(-1056178176),
         Float.intBitsToFloat(-1063780352),
         Float.intBitsToFloat(1092091904),
         Float.intBitsToFloat(1083179008),
         Float.intBitsToFloat(1092091904),
         CrownModel.ModeKind.GORE
      ),
      new CrownModel.DataRecord(
         "crown",
         0,
         15,
         Float.intBitsToFloat(-1065353216),
         Float.intBitsToFloat(-1055653888),
         Float.intBitsToFloat(-1065353216),
         Float.intBitsToFloat(1090519040),
         Float.intBitsToFloat(1056964608),
         Float.intBitsToFloat(1090519040),
         CrownModel.ModeKind.GORE
      ),
      new CrownModel.DataRecord(
         "crown",
         34,
         15,
         Float.intBitsToFloat(-1071644672),
         Float.intBitsToFloat(-1055129600),
         Float.intBitsToFloat(-1071644672),
         Float.intBitsToFloat(1084227584),
         Float.intBitsToFloat(1056964608),
         Float.intBitsToFloat(1084227584),
         CrownModel.ModeKind.GORE
      ),
      new CrownModel.DataRecord(
         "brim",
         0,
         31,
         Float.intBitsToFloat(-1064828928),
         Float.intBitsToFloat(-1098907648),
         Float.intBitsToFloat(-1069547520),
         Float.intBitsToFloat(1091043328),
         Float.intBitsToFloat(1056964608),
         Float.intBitsToFloat(1077936128),
         CrownModel.ModeKind.BRIM
      ),
      new CrownModel.DataRecord(
         "brim",
         24,
         31,
         Float.intBitsToFloat(-1068498944),
         Float.intBitsToFloat(-1098907648),
         Float.intBitsToFloat(-1065353216),
         Float.intBitsToFloat(1087373312),
         Float.intBitsToFloat(1056964608),
         1.0F,
         CrownModel.ModeKind.BRIM
      ),
      new CrownModel.DataRecord(
         "mast", 40, 0, Float.intBitsToFloat(-1090519040), Float.intBitsToFloat(-1073741824), Float.intBitsToFloat(-1090519040), 1.0F, 2.0F, 1.0F, CrownModel.ModeKind.STEM
      ),
      new CrownModel.DataRecord(
         "rotor", 46, 0, Float.intBitsToFloat(-1082130432), Float.intBitsToFloat(-1082130432), Float.intBitsToFloat(-1082130432), 2.0F, 1.0F, 2.0F, CrownModel.ModeKind.HUB
      ),
      new CrownModel.DataRecord(
         "rotor",
         56,
         0,
         Float.intBitsToFloat(-1090519040),
         Float.intBitsToFloat(-1077936128),
         Float.intBitsToFloat(-1090519040),
         1.0F,
         Float.intBitsToFloat(1056964608),
         1.0F,
         CrownModel.ModeKind.HUB_CAP
      ),
      new CrownModel.DataRecord(
         "blade0",
         0,
         25,
         1.0F,
         Float.intBitsToFloat(-1098907648),
         Float.intBitsToFloat(-1082130432),
         Float.intBitsToFloat(1087373312),
         Float.intBitsToFloat(1056964608),
         2.0F,
         CrownModel.ModeKind.BLADE_A
      ),
      new CrownModel.DataRecord(
         "blade0",
         18,
         25,
         Float.intBitsToFloat(1089470464),
         Float.intBitsToFloat(-1098907648),
         Float.intBitsToFloat(-1086324736),
         1.0F,
         Float.intBitsToFloat(1056964608),
         Float.intBitsToFloat(1069547520),
         CrownModel.ModeKind.BLADE_A
      ),
      new CrownModel.DataRecord(
         "blade1",
         0,
         28,
         1.0F,
         Float.intBitsToFloat(-1098907648),
         Float.intBitsToFloat(-1082130432),
         Float.intBitsToFloat(1087373312),
         Float.intBitsToFloat(1056964608),
         2.0F,
         CrownModel.ModeKind.BLADE_B
      ),
      new CrownModel.DataRecord(
         "blade1",
         18,
         28,
         Float.intBitsToFloat(1089470464),
         Float.intBitsToFloat(-1098907648),
         Float.intBitsToFloat(-1086324736),
         1.0F,
         Float.intBitsToFloat(1056964608),
         Float.intBitsToFloat(1069547520),
         CrownModel.ModeKind.BLADE_B
      )
   );
   private final ModelPart modelPart;
   private final ModelPart modelPart2;
   private final float floatValue;
   private static String text;

   public CrownModel(float floatValue2) {
      super(m_5_LayerDefinition(floatValue2 > 0.0F).bakeRoot(), RenderTypes::entityCutout);
      this.floatValue = floatValue2;
      this.modelPart = super.root.getChild("mast");
      this.modelPart2 = this.modelPart.getChild("rotor");
   }

   private static LayerDefinition m_5_LayerDefinition(boolean flag) {
      MeshDefinition meshDefinition = new MeshDefinition();
      PartDefinition partDefinition = meshDefinition.getRoot();
      partDefinition.addOrReplaceChild("crown", m_0("crown", flag), PartPose.ZERO);
      partDefinition.addOrReplaceChild("brim", m_0("brim", flag), PartPose.offsetAndRotation(0.0F, -4.5F, -4.75F, 0.2F, 0.0F, 0.0F));
      PartDefinition partDefinition2 = partDefinition.addOrReplaceChild("mast", m_0("mast", flag), PartPose.offset(0.0F, -9.75F, 0.0F));
      PartDefinition partDefinition3 = partDefinition2.addOrReplaceChild("rotor", m_0("rotor", flag), PartPose.offset(0.0F, -2.0F, 0.0F));
      m_5(partDefinition3, "blade0", 0.0F);
      m_5(partDefinition3, "blade1", (float) Math.PI);
      return LayerDefinition.create(meshDefinition, 64, 64);
   }

   private static void m_5(PartDefinition partDefinition, String text2, float floatValue2) {
      partDefinition.addOrReplaceChild(text2, m_0(text2, false), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.32F, floatValue2, 0.0F));
   }

   private static CubeListBuilder m_0(String text2, boolean flag) {
      m_2_String();
      CubeListBuilder cubeListBuilder = CubeListBuilder.create();
      if (flag) {
         return cubeListBuilder;
      }

      for (CrownModel.DataRecord c7 : C7_LIST) {
         if (c7.m_0__().equals(text2)) {
            cubeListBuilder.texOffs(c7.m_6n_int(), c7.m_67_int())
               .addBox(c7.m_8_j_float(), c7.m_85(), c7.m_8_y_float(), c7.m_8x(), c7.m_8e_float(), c7.m_8_l());
         }
      }

      return cubeListBuilder;
   }

   public void setupAnim(AvatarRenderState avatarRenderState) {
      m_2_String();
      RotationManager.StateHolder c7 = RotationManager.ROTATION_MANAGER.m_4_C7(avatarRenderState.id);
      if (c7 == null) {
         this.modelPart.xRot = 0.0F;
         this.modelPart.zRot = 0.0F;
         this.modelPart2.yRot = 0.0F;
      } else {
         float floatValue2 = avatarRenderState.ageInTicks - (float)Math.floor(avatarRenderState.ageInTicks);
         this.modelPart.xRot = -c7.m_7_float(floatValue2);
         this.modelPart.zRot = c7.m_1_float(floatValue2);
         this.modelPart2.yRot = c7.m_9(floatValue2) - c7.m_5_float() * this.floatValue;
      }
   }

   static {
      m_9_void(null);
   }

   public static void m_9_void(String text2) {
      text = text2;
   }

   public static String m_2_String() {
      return text;
   }

   public enum ModeKind {
      GORE,
      BRIM,
      STEM,
      HUB,
      HUB_CAP,
      BLADE_A,
      BLADE_B;

      private static String a(byte[] byteArray) {
         int value = 0;
         int value2;
         char[] charArray = new char[value2 = byteArray.length];

         for (int i = 0; i < value2; i++) {
            int value3;
            if ((value3 = 255 & byteArray[i]) < 192) {
               charArray[value++] = (char)value3;
            } else if (value3 < 224) {
               char ch = (char)((char)(value3 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value++] = ch;
            } else if (i < value2 - 2) {
               char ch2 = (char)((char)(value3 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value++] = ch2;
            }
         }

         return new String(charArray, 0, value);
      }
   }

   public record DataRecord(String f_0__, int f_6n, int f_67, float f_8_j, float f_85, float f_8_y, float f_8x, float f_8e, float f_8_l, CrownModel.ModeKind f_7c) {
      public String m_0__() {
         return this.f_0__;
      }

      public int m_6n_int() {
         return this.f_6n;
      }

      public int m_67_int() {
         return this.f_67;
      }

      public float m_8_j_float() {
         return this.f_8_j;
      }

      public float m_85() {
         return this.f_85;
      }

      public float m_8_y_float() {
         return this.f_8_y;
      }

      public float m_8x() {
         return this.f_8x;
      }

      public float m_8e_float() {
         return this.f_8e;
      }

      public float m_8_l() {
         return this.f_8_l;
      }

      public CrownModel.ModeKind m_7c() {
         return this.f_7c;
      }
   }
}
