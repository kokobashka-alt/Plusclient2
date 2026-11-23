package wtf.onetap.module.combat;

import net.minecraft.world.entity.LivingEntity;

public interface AuraRotationStrategy {
    void onReset();

    void onTick(LivingEntity target, boolean attackReady, float range);

    void onRender(LivingEntity target, float partialTicks);
}
