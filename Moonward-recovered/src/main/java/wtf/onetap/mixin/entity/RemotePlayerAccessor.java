package wtf.onetap.mixin.entity;

import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RemotePlayer.class)
public interface RemotePlayerAccessor {
   @Accessor("lerpDeltaMovement")
   Vec3 onetap$getLerpDeltaMovement();
}
