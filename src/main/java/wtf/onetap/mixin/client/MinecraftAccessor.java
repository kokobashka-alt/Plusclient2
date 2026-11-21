package wtf.onetap.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
   @Accessor("rightClickDelay")
   void setRightClickDelay(int var1);

   @Mutable
   @Accessor("user")
   void setUser(User var1);

   @Accessor("pendingConnection")
   void setPendingConnection(Connection var1);

   @Accessor("pendingConnection")
   Connection getPendingConnection();

   @Invoker("pick")
   void onetap$pick(float var1);

   @Invoker("startAttack")
   boolean onetap$startAttack();

   @Invoker("continueAttack")
   void onetap$continueAttack(boolean var1);

   @Invoker("startUseItem")
   void onetap$startUseItem();

   @Invoker("updateLevelInEngines")
   void onetap$updateLevelInEngines(ClientLevel var1, boolean var2);
}
