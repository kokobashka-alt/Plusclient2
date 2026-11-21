package wtf.onetap.module.render;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "TotemAngel", description = "Показывает анимацию ангела при срабатывании тотема", category = Category.RENDER)
public class TotemAngel extends Module {
   private final List<TotemAngel.DataRecord> c7List = new CopyOnWriteArrayList<>();
   private final List<Vec3> vec3List = new ArrayList<>(1536);
   private final int[] valueArray = new int[768];
   private final float[] floatValueArray = new float[10752];
   private final Matrix4f matrix4f = new Matrix4f();
   private final Matrix4f matrix4f2 = new Matrix4f();
   private final Vector3f vector3f = new Vector3f();
   private final float[][] floatValueArray2 = new float[8][3];
   private final float[][] floatValueArray3 = new float[8][3];
   private int value;
   private int value2;

   @Override
   public void onDisable() {
      this.c7List.clear();
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundEntityEventPacket clientboundEntityEventPacket) {
         if (clientboundEntityEventPacket.getEventId() == 35) {
            if (MinecraftAccess.MINECRAFT.level != null) {
               if (clientboundEntityEventPacket.getEntity(MinecraftAccess.MINECRAFT.level) instanceof Player player) {
                  this.m_7_void(player);
               }
            }
         }
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (!this.c7List.isEmpty()) {
         long longValue = System.currentTimeMillis();
         ArrayList list = new ArrayList();
         Vec3 vec3 = event.getVec3();
         Matrix4f matrix4f3 = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
         int value3 = ColorUtil.primaryColor();
         this.value = 0;
         this.value2 = 0;
         this.vec3List.clear();

         for (TotemAngel.DataRecord c7 : this.c7List) {
            float floatValue = (float)(longValue - c7.f_0g) / 3000.0F;
            if (floatValue >= 1.0F) {
               list.add(c7);
            } else {
               this.m_1(vec3, value3, floatValue, c7);
            }
         }

         if (!list.isEmpty()) {
            this.c7List.removeAll(list);
         }

         if (this.value > 0) {
            int value4 = this.value;
            Box3DRenderer.m_8(
               matrix4f3,
               value4,
               bufferBuilder -> {
                  for (int i = 0; i < value4; i++) {
                     int value5 = i * 7;
                     bufferBuilder.addVertex(this.floatValueArray[value5], this.floatValueArray[value5 + 1], this.floatValueArray[value5 + 2])
                        .setColor((int)this.floatValueArray[value5 + 3], (int)this.floatValueArray[value5 + 4], (int)this.floatValueArray[value5 + 5], (int)this.floatValueArray[value5 + 6]);
                  }
               }
            );
         }

         if (this.value2 > 0) {
            Line3DRenderer.m_7(matrix4f3, this.vec3List, this.valueArray, vec3, 1.25F);
         }
      }
   }

   private void m_7_void(Player player) {
      float floatValue = MinecraftAccess.MINECRAFT.getDeltaTracker().getGameTimeDeltaPartialTick(false);
      float floatValue2 = Mth.rotLerp(floatValue, player.yBodyRotO, player.yBodyRot);
      float floatValue3 = Mth.rotLerp(floatValue, player.yHeadRotO, player.yHeadRot);
      float floatValue4 = Mth.lerp(floatValue, player.xRotO, player.getXRot());
      float floatValue5 = Mth.wrapDegrees(floatValue3 - floatValue2);
      float floatValue6 = player.walkAnimation.position(floatValue);
      float floatValue7 = Math.min(player.walkAnimation.speed(floatValue), 1.0F);
      float floatValue8 = Mth.cos(floatValue6 * 0.6662F + (float) Math.PI) * 2.0F * floatValue7 * 0.5F;
      float floatValue9 = Mth.cos(floatValue6 * 0.6662F) * 2.0F * floatValue7 * 0.5F;
      float floatValue10 = 0.0F;
      float floatValue11 = 0.0F;
      float floatValue12 = Mth.cos(floatValue6 * 0.6662F) * 1.4F * floatValue7;
      float floatValue13 = Mth.cos(floatValue6 * 0.6662F + (float) Math.PI) * 1.4F * floatValue7;
      float floatValue14 = 0.0F;
      boolean flag = player.isCrouching();
      if (flag) {
         floatValue14 = 0.5F;
         floatValue8 += 0.4F;
         floatValue9 += 0.4F;
      }

      TotemAngel.ModeKind c5 = this.m_5(player, InteractionHand.MAIN_HAND);
      TotemAngel.ModeKind c52 = this.m_5(player, InteractionHand.OFF_HAND);
      if (c5.flag) {
         c52 = player.getOffhandItem().isEmpty() ? TotemAngel.ModeKind.EMPTY : TotemAngel.ModeKind.ITEM;
      }

      boolean flag2 = player.getMainArm() == HumanoidArm.RIGHT;
      TotemAngel.ModeKind c53 = flag2 ? c5 : c52;
      TotemAngel.ModeKind c54 = flag2 ? c52 : c5;
      float[] floatArray = this.m_5(c53, floatValue8, floatValue10, floatValue4, floatValue5);
      floatValue8 = floatArray[0];
      floatValue10 = floatArray[1];
      float[] floatArray2 = this.m_5(c54, floatValue9, floatValue11, floatValue4, floatValue5);
      floatValue9 = floatArray2[0];
      floatValue11 = floatArray2[1];
      float floatValue15 = (float)Math.toRadians(Math.min(floatValue4, 0.0F));
      this.c7List
         .add(
            new TotemAngel.DataRecord(
               new Vec3(player.getX(), player.getY(), player.getZ()),
               floatValue2,
               floatValue5,
               floatValue15,
               floatValue14,
               floatValue8,
               floatValue10,
               floatValue9,
               floatValue11,
               floatValue12,
               floatValue13,
               flag,
               System.currentTimeMillis()
            )
         );
   }

   private void m_1(Vec3 vec3, int value3, float floatValue, TotemAngel.DataRecord c7) {
      Notifications.m_5_C3_sArr();
      int value4 = ColorUtil.withAlpha(value3, Math.max(0, (int)(140.0F * (1.0F - floatValue))));
      int value5 = ColorUtil.withAlpha(value3, Math.max(0, (int)(28.0F * (1.0F - floatValue))));
      this.matrix4f
         .identity()
         .translate((float)c7.f_5_r.x, (float)c7.f_5_r.y, (float)c7.f_5_r.z)
         .rotateY((float)Math.toRadians(180.0F - c7.f_7_b))
         .scale(-1.0F, -1.0F, 1.0F)
         .translate(0.0F, -1.501F, 0.0F);

      for (int i = 0; i < 3; i++) {
         float floatValue2 = i * 0.006F;
         int value6 = ColorUtil.withAlpha(value4, Math.max(6, (int)((value4 >> 24 & 0xFF) / (1.0F + i * 1.8F))));
         int value7 = ColorUtil.withAlpha(value5, Math.max(4, (int)((value5 >> 24 & 0xFF) / (1.0F + i * 1.8F))));
         float floatValue3 = c7.f_65 ? 0.5F : 0.0F;
         float floatValue4 = c7.f_65 ? 3.2F : 0.0F;
         float floatValue5 = c7.f_65 ? 5.2F : 2.0F;
         float floatValue6 = c7.f_65 ? 4.2F : 0.0F;
         float floatValue7 = c7.f_65 ? 12.2F : 12.0F;
         float floatValue8 = c7.f_65 ? 4.0F : 0.0F;
         this.m_3(vec3, 0.0F, floatValue6, 0.0F, c7.f_76, (float)Math.toRadians(c7.f_7_j), 0.0F, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, value6, value7, floatValue2);
         this.m_3(vec3, 0.0F, floatValue4, 0.0F, floatValue3 + c7.f_7_o, 0.0F, 0.0F, -4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, value6, value7, floatValue2);
         this.m_3(vec3, -5.0F, floatValue5, 0.0F, c7.f_7__, c7.f_7i, 0.0F, -3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, value6, value7, floatValue2);
         this.m_3(vec3, 5.0F, floatValue5, 0.0F, c7.f_7n, c7.f_77, 0.0F, -1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, value6, value7, floatValue2);
         this.m_3(vec3, -1.9F, floatValue7, floatValue8, c7.f_7_z, 0.0F, 0.0F, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, value6, value7, floatValue2);
         this.m_3(vec3, 1.9F, floatValue7, floatValue8, c7.f_7_g, 0.0F, 0.0F, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, value6, value7, floatValue2);
      }
   }

   private void m_3(
      Vec3 vec3,
      float floatValue,
      float floatValue2,
      float floatValue3,
      float floatValue4,
      float floatValue5,
      float floatValue6,
      float floatValue7,
      float floatValue8,
      float floatValue9,
      float floatValue10,
      float floatValue11,
      float floatValue12,
      int value3,
      int value4,
      float floatValue13
   ) {
      if (this.value + 24 <= 1536 && this.value2 + 12 <= 768) {
         float floatValue14 = 0.0625F;
         this.matrix4f2.set(this.matrix4f).translate(floatValue * floatValue14, floatValue2 * floatValue14, floatValue3 * floatValue14);
         if (floatValue6 != 0.0F) {
            this.matrix4f2.rotateZ(floatValue6);
         }

         if (floatValue5 != 0.0F) {
            this.matrix4f2.rotateY(floatValue5);
         }

         if (floatValue4 != 0.0F) {
            this.matrix4f2.rotateX(floatValue4);
         }

         float floatValue15 = floatValue7 * floatValue14 - floatValue13;
         float floatValue16 = floatValue8 * floatValue14 - floatValue13;
         float floatValue17 = floatValue9 * floatValue14 - floatValue13;
         float floatValue18 = (floatValue7 + floatValue10) * floatValue14 + floatValue13;
         float floatValue19 = (floatValue8 + floatValue11) * floatValue14 + floatValue13;
         float floatValue20 = (floatValue9 + floatValue12) * floatValue14 + floatValue13;
         this.m_3(0, floatValue15, floatValue16, floatValue17, vec3);
         this.m_3(1, floatValue18, floatValue16, floatValue17, vec3);
         this.m_3(2, floatValue18, floatValue16, floatValue20, vec3);
         this.m_3(3, floatValue15, floatValue16, floatValue20, vec3);
         this.m_3(4, floatValue15, floatValue19, floatValue17, vec3);
         this.m_3(5, floatValue18, floatValue19, floatValue17, vec3);
         this.m_3(6, floatValue18, floatValue19, floatValue20, vec3);
         this.m_3(7, floatValue15, floatValue19, floatValue20, vec3);
         int value5 = value4 >> 16 & 0xFF;
         int value6 = value4 >> 8 & 0xFF;
         int value7 = value4 & 0xFF;
         int value8 = value4 >> 24 & 0xFF;
         this.m_7(0, 1, 2, 3, value5, value6, value7, value8);
         this.m_7(4, 5, 6, 7, value5, value6, value7, value8);
         this.m_7(0, 1, 5, 4, value5, value6, value7, value8);
         this.m_7(3, 2, 6, 7, value5, value6, value7, value8);
         this.m_7(0, 3, 7, 4, value5, value6, value7, value8);
         this.m_7(1, 2, 6, 5, value5, value6, value7, value8);
         this.m_4_void(0, 1, value3);
         this.m_4_void(1, 2, value3);
         this.m_4_void(2, 3, value3);
         this.m_4_void(3, 0, value3);
         this.m_4_void(4, 5, value3);
         this.m_4_void(5, 6, value3);
         this.m_4_void(6, 7, value3);
         this.m_4_void(7, 4, value3);
         this.m_4_void(0, 4, value3);
         this.m_4_void(1, 5, value3);
         this.m_4_void(2, 6, value3);
         this.m_4_void(3, 7, value3);
      }
   }

   private void m_3(int value3, float floatValue, float floatValue2, float floatValue3, Vec3 vec3) {
      this.matrix4f2.transformPosition(floatValue, floatValue2, floatValue3, this.vector3f);
      this.floatValueArray3[value3][0] = this.vector3f.x;
      this.floatValueArray3[value3][1] = this.vector3f.y;
      this.floatValueArray3[value3][2] = this.vector3f.z;
      this.floatValueArray2[value3][0] = this.vector3f.x - (float)vec3.x;
      this.floatValueArray2[value3][1] = this.vector3f.y - (float)vec3.y;
      this.floatValueArray2[value3][2] = this.vector3f.z - (float)vec3.z;
   }

   private void m_7(int value3, int value4, int value5, int value6, int value7, int value8, int value9, int value10) {
      this.m_5(this.floatValueArray2[value3], value7, value8, value9, value10);
      this.m_5(this.floatValueArray2[value4], value7, value8, value9, value10);
      this.m_5(this.floatValueArray2[value5], value7, value8, value9, value10);
      this.m_5(this.floatValueArray2[value6], value7, value8, value9, value10);
   }

   private void m_5(float[] floatArray, int value3, int value4, int value5, int value6) {
      int value7 = this.value * 7;
      this.floatValueArray[value7] = floatArray[0];
      this.floatValueArray[value7 + 1] = floatArray[1];
      this.floatValueArray[value7 + 2] = floatArray[2];
      this.floatValueArray[value7 + 3] = value3;
      this.floatValueArray[value7 + 4] = value4;
      this.floatValueArray[value7 + 5] = value5;
      this.floatValueArray[value7 + 6] = value6;
      this.value++;
   }

   private void m_4_void(int value3, int value4, int value5) {
      this.vec3List.add(new Vec3(this.floatValueArray3[value3][0], this.floatValueArray3[value3][1], this.floatValueArray3[value3][2]));
      this.vec3List.add(new Vec3(this.floatValueArray3[value4][0], this.floatValueArray3[value4][1], this.floatValueArray3[value4][2]));
      this.valueArray[this.value2] = value5;
      this.value2++;
   }

   private TotemAngel.ModeKind m_5(Player player, InteractionHand interactionHand) {
      ItemStack itemStack = player.getItemInHand(interactionHand);
      if (itemStack.isEmpty()) {
         return TotemAngel.ModeKind.EMPTY;
      }

      if (player.getUsedItemHand() == interactionHand && player.getTicksUsingItem() > 0) {
         ItemUseAnimation itemUseAnimation = itemStack.getUseAnimation();
         if (itemUseAnimation == ItemUseAnimation.BLOCK) {
            return TotemAngel.ModeKind.BLOCK;
         }

         if (itemUseAnimation == ItemUseAnimation.BOW) {
            return TotemAngel.ModeKind.BOW_AND_ARROW;
         }

         if (itemUseAnimation == ItemUseAnimation.SPEAR || itemUseAnimation == ItemUseAnimation.TRIDENT) {
            return TotemAngel.ModeKind.THROW_SPEAR;
         }

         if (itemUseAnimation == ItemUseAnimation.CROSSBOW) {
            return TotemAngel.ModeKind.CROSSBOW_CHARGE;
         }
      } else if (!player.swinging && itemStack.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
         return TotemAngel.ModeKind.CROSSBOW_HOLD;
      }

      return TotemAngel.ModeKind.ITEM;
   }

   private float[] m_5(TotemAngel.ModeKind c5, float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      float floatValue5 = floatValue;
      float floatValue6 = floatValue2;
      switch (c5.ordinal()) {
         case 0:
            floatValue6 = 0.0F;
            break;
         case 1:
            floatValue5 = floatValue5 * 0.5F - (float) (Math.PI / 10);
            break;
         case 2:
            floatValue5 = floatValue5 * 0.5F - 0.9424779F;
            floatValue6 = (float) (Math.PI / 6);
            break;
         case 3:
            floatValue5 = (float) (-Math.PI / 2) + floatValue3;
            floatValue6 = (float)Math.toRadians(floatValue4) - 0.1F;
            break;
         case 4:
            floatValue5 = floatValue5 * 0.5F - (float) Math.PI;
            floatValue6 = 0.0F;
            break;
         case 5:
            floatValue5 = -0.97079635F;
            floatValue6 = 0.8F;
            break;
         case 6:
            floatValue5 = -1.4707963F + floatValue3;
            floatValue6 = 0.3F + (float)Math.toRadians(floatValue4);
      }

      return new float[]{floatValue5, floatValue6};
   }

   private enum ModeKind {
      EMPTY(false),
      ITEM(false),
      BLOCK(false),
      BOW_AND_ARROW(true),
      THROW_SPEAR(false),
      CROSSBOW_CHARGE(true),
      CROSSBOW_HOLD(true);

      private final boolean flag;

      ModeKind(boolean flag2) {
         this.flag = flag2;
      }

      private static String a(byte[] byteArray) {
         int value3 = 0;
         int value4;
         char[] charArray = new char[value4 = byteArray.length];

         for (int i = 0; i < value4; i++) {
            int value5;
            if ((value5 = 255 & byteArray[i]) < 192) {
               charArray[value3++] = (char)value5;
            } else if (value5 < 224) {
               char ch = (char)((char)(value5 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value3++] = ch;
            } else if (i < value4 - 2) {
               char ch2 = (char)((char)(value5 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value3++] = ch2;
            }
         }

         return new String(charArray, 0, value3);
      }
   }

   private record DataRecord(
      Vec3 f_5_r,
      float f_7_b,
      float f_7_j,
      float f_76,
      float f_7_o,
      float f_7__,
      float f_7i,
      float f_7n,
      float f_77,
      float f_7_z,
      float f_7_g,
      boolean f_65,
      long f_0g
   ) {
      public Vec3 m_5_r() {
         return this.f_5_r;
      }

      public float m_7_b() {
         return this.f_7_b;
      }

      public float m_7_j_float() {
         return this.f_7_j;
      }

      public float m_76() {
         return this.f_76;
      }

      public float m_7_o() {
         return this.f_7_o;
      }

      public float m_7__() {
         return this.f_7__;
      }

      public float m_7i_float() {
         return this.f_7i;
      }

      public float m_7n() {
         return this.f_7n;
      }

      public float m_77() {
         return this.f_77;
      }

      public float m_7_z() {
         return this.f_7_z;
      }

      public float m_7_g() {
         return this.f_7_g;
      }

      public boolean m_65_boolean() {
         return this.f_65;
      }

      public long m_0g() {
         return this.f_0g;
      }
   }
}
