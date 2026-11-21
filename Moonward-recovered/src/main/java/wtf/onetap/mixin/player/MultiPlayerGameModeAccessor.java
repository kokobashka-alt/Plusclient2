package wtf.onetap.mixin.player;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeAccessor {
   @Accessor("destroyBlockPos")
   BlockPos onetap$getDestroyBlockPos();

   @Accessor("destroyProgress")
   float onetap$getDestroyProgress();

   @Accessor("carriedIndex")
   void onetap$setCarriedIndex(int var1);

   @Invoker("startPrediction")
   void onetap$startPrediction(ClientLevel var1, PredictiveAction var2);

   @Accessor("destroyDelay")
   void onetap$setDestroyDelay(int var1);

   @Accessor("destroyDelay")
   int onetap$getDestroyDelay();
}
