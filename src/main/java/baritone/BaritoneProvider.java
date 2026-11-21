package baritone;

import baritone.api.IBaritone;
import baritone.api.IBaritoneProvider;
import baritone.api.cache.IWorldScanner;
import baritone.api.command.ICommandSystem;
import baritone.api.schematic.ISchematicSystem;
import baritone.cache.FasterWorldScanner;
import baritone.command.CommandSystem;
import baritone.command.ExampleBaritoneControl;
import baritone.utils.schematic.SchematicSystem;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;

public class BaritoneProvider implements IBaritoneProvider {
   private final List<IBaritone> all = new CopyOnWriteArrayList<>();
   private final List<IBaritone> allView = Collections.unmodifiableList(this.all);

   public BaritoneProvider() {
      Baritone var1 = (Baritone)this.createBaritone(Minecraft.getInstance());
      var1.registerBehavior(ExampleBaritoneControl::new);
   }

   @Override
   public IBaritone getPrimaryBaritone() {
      return this.all.get(0);
   }

   @Override
   public List<IBaritone> getAllBaritones() {
      return this.allView;
   }

   @Override
   public synchronized IBaritone createBaritone(Minecraft var1) {
      IBaritone var2 = this.getBaritoneForMinecraft(var1);
      if (var2 == null) {
         this.all.add(var2 = new Baritone(var1));
      }

      return var2;
   }

   @Override
   public synchronized boolean destroyBaritone(IBaritone var1) {
      return var1 != this.getPrimaryBaritone() && this.all.remove(var1);
   }

   @Override
   public IWorldScanner getWorldScanner() {
      return FasterWorldScanner.INSTANCE;
   }

   @Override
   public ICommandSystem getCommandSystem() {
      return CommandSystem.INSTANCE;
   }

   @Override
   public ISchematicSystem getSchematicSystem() {
      return SchematicSystem.INSTANCE;
   }
}
