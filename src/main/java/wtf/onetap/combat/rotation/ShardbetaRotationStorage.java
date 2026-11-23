package wtf.onetap.combat.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;

import wtf.onetap.util.MinecraftAccess;

public final class ShardbetaRotationStorage {
    private static float yaw;
    private static float pitch;
    private static float realYaw;
    private static float realPitch;
    private static boolean active;

    private ShardbetaRotationStorage() {}

    public static void begin(float y, float p) {
        LocalPlayer player = MinecraftAccess.MINECRAFT.player;
        if (!active && player != null) {
            realYaw = player.getYRot();
            realPitch = player.getXRot();
        }
        yaw = Mth.wrapDegrees(y);
        pitch = Mth.clamp(p, -90.0f, 90.0f);
        active = true;
    }

    public static void clear() {
        active = false;
    }

    public static boolean active() {
        return active;
    }

    public static float yaw() {
        LocalPlayer player = MinecraftAccess.MINECRAFT.player;
        return active ? yaw : (player != null ? player.getYRot() : 0.0f);
    }

    public static float pitch() {
        LocalPlayer player = MinecraftAccess.MINECRAFT.player;
        return active ? pitch : (player != null ? player.getXRot() : 0.0f);
    }

    public static void sendLook() {
        Minecraft mc = MinecraftAccess.MINECRAFT;
        LocalPlayer player = mc.player;
        if (player == null || player.connection == null) return;
        player.connection.send(new ServerboundMovePlayerPacket.Rot(yaw, pitch, player.onGround(), player.horizontalCollision));
    }

    public static void sendReal() {
        Minecraft mc = MinecraftAccess.MINECRAFT;
        LocalPlayer player = mc.player;
        if (player == null || player.connection == null) return;
        player.connection.send(new ServerboundMovePlayerPacket.Rot(realYaw, realPitch, player.onGround(), player.horizontalCollision));
    }

    public static void applyForHit() {
        LocalPlayer player = MinecraftAccess.MINECRAFT.player;
        if (player != null) {
            player.setYRot(yaw);
            player.setXRot(pitch);
        }
    }

    public static void restoreRender() {
        LocalPlayer player = MinecraftAccess.MINECRAFT.player;
        if (player != null) {
            player.setYRot(realYaw);
            player.setXRot(realPitch);
        }
    }
}
