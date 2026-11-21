package baritone.api;

import baritone.api.cache.IWorldScanner;
import baritone.api.command.ICommandSystem;
import baritone.api.schematic.ISchematicSystem;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

public interface IBaritoneProvider {
   IBaritone getPrimaryBaritone();

   List<IBaritone> getAllBaritones();

   default IBaritone getBaritoneForPlayer(LocalPlayer var1) {
      for (IBaritone var3 : this.getAllBaritones()) {
         if (Objects.equals(var1, var3.getPlayerContext().player())) {
            return var3;
         }
      }

      return null;
   }

   default IBaritone getBaritoneForMinecraft(Minecraft var1) {
      for (IBaritone var3 : this.getAllBaritones()) {
         if (Objects.equals(var1, var3.getPlayerContext().minecraft())) {
            return var3;
         }
      }

      return null;
   }

   default IBaritone getBaritoneForConnection(ClientPacketListener var1) {
      for (IBaritone var3 : this.getAllBaritones()) {
         LocalPlayer var4 = var3.getPlayerContext().player();
         if (var4 != null && var4.connection == var1) {
            return var3;
         }
      }

      return null;
   }

   IBaritone createBaritone(Minecraft var1);

   boolean destroyBaritone(IBaritone var1);

   IWorldScanner getWorldScanner();

   ICommandSystem getCommandSystem();

   ISchematicSystem getSchematicSystem();
}
