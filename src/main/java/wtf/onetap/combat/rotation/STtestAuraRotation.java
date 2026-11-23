package wtf.onetap.module.combat;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.RotationEngine;

public final class STtestAuraRotation implements AuraRotationStrategy {
    private final RotationEngine engine = new RotationEngine();
    private LivingEntity lastTarget;
    private int lastTick;
    private boolean active;

    @Override
    public void onReset() {
        this.engine.reset();
        this.lastTarget = null;
        this.active = false;
    }

    @Override
    public void onTick(LivingEntity target, boolean attackReady, float range) {
        LocalPlayer player = MinecraftAccess.MINECRAFT.player;
        if (player == null || target == null) {
            onReset();
            return;
        }

        this.lastTick = player.tickCount;
        this.active = true;
        long now = System.nanoTime();

        if (this.lastTarget != target) {
            this.lastTarget = target;
            this.engine.seed(player.getYRot(), player.getXRot(), now);
        }

        this.engine.setMaxDistance(range);
        AABB box = target.getBoundingBox();
        Vec3 eye = player.getEyePosition();
        this.engine.update(eye, box, now);

        float yaw = this.engine.getYaw();
        float pitch = Mth.clamp(this.engine.getPitch(), -90.0F, 90.0F);
        player.setYRot(yaw);
        player.yHeadRot = yaw;
        player.setXRot(pitch);
    }

    @Override
    public void onRender(LivingEntity target, float partialTicks) {
        LocalPlayer player = MinecraftAccess.MINECRAFT.player;
        if (player == null || target == null || !this.active) return;
        if (player.tickCount - this.lastTick > 1) return;

        AABB box = target.getBoundingBox().move(target.getPosition(partialTicks).subtract(target.position()));
        Vec3 eye = player.getEyePosition(partialTicks);
        this.engine.update(eye, box, System.nanoTime());

        float yaw = this.engine.getYaw();
        float pitch = Mth.clamp(this.engine.getPitch(), -90.0F, 90.0F);
        player.setYRot(yaw);
        player.yHeadRot = yaw;
        player.setXRot(pitch);
    }
}
