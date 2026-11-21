package wtf.onetap.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;

public class ContainerClickEvent extends Event implements Cancellable {
   private final int containerId;
   private final int slot;
   private final int button;
   private final ContainerInput input;
   private final Player player;
   private boolean cancelled;

   public ContainerClickEvent(int value, int value2, int value3, ContainerInput containerInput, Player player2) {
      this.containerId = value;
      this.slot = value2;
      this.button = value3;
      this.input = containerInput;
      this.player = player2;
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getSlot() {
      return this.slot;
   }

   public int getButton() {
      return this.button;
   }

   public ContainerInput getInput() {
      return this.input;
   }

   public Player getPlayer() {
      return this.player;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void cancel() {
      this.cancelled = true;
   }
}
