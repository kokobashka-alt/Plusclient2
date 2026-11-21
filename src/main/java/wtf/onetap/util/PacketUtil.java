package wtf.onetap.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.phys.Vec3;

public class PacketUtil implements MinecraftAccess {
   private static final List<Packet<?>> PACKET_LIST = new ArrayList<>();
   private static Vec3 vec3;
   private static Connection connection;
   private static int value = -1;
   private static boolean flag;

   private PacketUtil() {
   }

   public static boolean consumeTracked(Packet<?> packet) {
      if (!PACKET_LIST.contains(packet)) {
         return false;
      }

      PACKET_LIST.remove(packet);
      return true;
   }

   public static void sendPacket(Packet<?> packet) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.connection != null) {
         PACKET_LIST.add(packet);
         MinecraftAccess.MINECRAFT.player.connection.send(packet);
      }
   }

   public static boolean m_9(Connection connection2, Packet<?> packet) {
      if (packet instanceof ServerboundSetCarriedItemPacket serverboundSetCarriedItemPacket) {
         if (connection2 != connection) {
            connection = connection2;
            value = -1;
         }

         return serverboundSetCarriedItemPacket.getSlot() == value;
      } else {
         return false;
      }
   }

   public static void m_4(Connection connection2, Packet<?> packet) {
      if (packet instanceof ServerboundSetCarriedItemPacket serverboundSetCarriedItemPacket) {
         connection = connection2;
         value = serverboundSetCarriedItemPacket.getSlot();
      } else {
         if (packet instanceof ServerboundMovePlayerPacket serverboundMovePlayerPacket && serverboundMovePlayerPacket.hasPosition()) {
            vec3 = new Vec3(serverboundMovePlayerPacket.getX(0.0), serverboundMovePlayerPacket.getY(0.0), serverboundMovePlayerPacket.getZ(0.0));
         }
      }
   }

   public static int m_1_int() {
      return value;
   }

   public static Vec3 m_1_Vec3() {
      return vec3;
   }

   static {
      m_0_void(false);
   }

   public static void m_0_void(boolean flag2) {
      flag = flag2;
   }

   public static boolean m_1_boolean() {
      return flag;
   }

   public static boolean m_4_boolean() {
      return true;
   }
}
