package wtf.onetap.combat.rotation.holyworld;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Aim point and angles used both when recording the player's own tracking and when the model drives the rotation,
 * so the two always see the same geometry. The point is independent of any module state: horizontal center of the
 * hitbox, 70% of its height.
 */
public final class HolyWorldAim {
    private static final double HEIGHT_FRACTION = 0.7;

    private HolyWorldAim() {
    }

    public static final class Angles {
        public final float yaw;
        public final float pitch;
        public final double distance;
        public final double sizeDegrees;

        Angles(float yaw, float pitch, double distance, double sizeDegrees) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.distance = distance;
            this.sizeDegrees = sizeDegrees;
        }
    }

    public static Vec3 point(LivingEntity entity) {
        AABB box = entity.getBoundingBox();
        return new Vec3((box.minX + box.maxX) * 0.5, box.minY + (box.maxY - box.minY) * HEIGHT_FRACTION, (box.minZ + box.maxZ) * 0.5);
    }

    public static Angles angles(Player player, LivingEntity entity) {
        return angles(player, point(entity), entity.getBbWidth() * 0.5);
    }

    /** Angles to an arbitrary point; {@code halfWidth} is the half width of the thing being aimed at. */
    public static Angles angles(Player player, Vec3 point, double halfWidth) {
        Vec3 eye = player.getEyePosition();
        double dx = point.x - eye.x;
        double dy = point.y - eye.y;
        double dz = point.z - eye.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double size = Math.toDegrees(Math.atan2(halfWidth, Math.max(distance, 0.3)));
        return new Angles(Mth.wrapDegrees(yaw), Mth.clamp(pitch, -90.0F, 90.0F), distance, size);
    }
}
