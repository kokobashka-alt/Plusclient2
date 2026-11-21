package wtf.onetap.module.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Tracers", description = "Рисует линии к игрокам", category = Category.RENDER)
public class Tracers extends Module {
   private final BooleanSetting netheriteArmor = new BooleanSetting("Незеритовая броня", false).build();

   @EventTarget(priority = EventPriority.HIGH)
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         Camera camera = MinecraftAccess.MINECRAFT.gameRenderer.mainCamera();
         Vec3 vec3 = event.getVec3();
         Vector3fc vector3fc = camera.forwardVector();
         Vec3 vec32 = vec3.add(vector3fc.x() * 3.0, vector3fc.y() * 3.0, vector3fc.z() * 3.0);
         Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
         float floatValue = event.getFloatValue();
         var playerList = this.m_0_List();
         ArrayList list = new ArrayList(playerList.size() * 2);
         int[] intArray = new int[playerList.size()];
         int value = 0;

         for (Player player : playerList) {
            if (player != MinecraftAccess.MINECRAFT.player) {
               double doubleValue = player.xo + (player.getX() - player.xo) * floatValue;
               double doubleValue2 = player.yo + (player.getY() - player.yo) * floatValue;
               double doubleValue3 = player.zo + (player.getZ() - player.zo) * floatValue;
               Vec3 vec33 = new Vec3(doubleValue, doubleValue2, doubleValue3);
               boolean flag = Onetap.FRIEND_MANAGER.isFriend(player.getName().getString());
               boolean flag2 = player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);
               int value2;
               if (flag) {
                  value2 = ColorUtil.rgba(0, 255, 0, 255);
               } else if (flag2) {
                  value2 = ColorUtil.rgba(255, 20, 147, 255);
               } else {
                  value2 = ColorUtil.rgba(255, 255, 255, 255);
               }

               list.add(vec32);
               list.add(vec33);
               intArray[value++] = value2;
            }
         }

         if (!list.isEmpty()) {
            Line3DRenderer.m_7(matrix4f, list, intArray, vec3, 1.0F);
         }
      }
   }

   private List<Player> m_0_List() {
      ArrayList list = new ArrayList();
      if (MinecraftAccess.MINECRAFT.level == null) {
         return list;
      }

      for (AbstractClientPlayer abstractClientPlayer : MinecraftAccess.MINECRAFT.level.players()) {
         if (MinecraftAccess.MINECRAFT.getConnection() != null
            && MinecraftAccess.MINECRAFT.getConnection().getPlayerInfo(abstractClientPlayer.getUUID()) != null
            && abstractClientPlayer.isAlive()
            && !abstractClientPlayer.isSpectator()
            && (!(Boolean)this.netheriteArmor.getValue() || this.m_1_boolean(abstractClientPlayer))) {
            list.add(abstractClientPlayer);
         }
      }

      return list;
   }

   private boolean m_1_boolean(Player player) {
      for (EquipmentSlot equipmentSlot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
         if (this.m_7_boolean(player.getItemBySlot(equipmentSlot))) {
            return true;
         }
      }

      return false;
   }

   private boolean m_7_boolean(ItemStack itemStack) {
      return itemStack.is(Items.NETHERITE_HELMET) || itemStack.is(Items.NETHERITE_CHESTPLATE) || itemStack.is(Items.NETHERITE_LEGGINGS) || itemStack.is(Items.NETHERITE_BOOTS);
   }

   private static String b(byte[] byteArray) {
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
