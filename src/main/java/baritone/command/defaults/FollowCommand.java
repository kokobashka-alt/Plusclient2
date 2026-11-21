package baritone.command.defaults;

import baritone.KeepName;
import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.EntityClassById;
import baritone.api.command.datatypes.IDatatypeFor;
import baritone.api.command.datatypes.NearbyPlayer;
import baritone.api.command.exception.CommandErrorMessageException;
import baritone.api.command.helpers.TabCompleteHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class FollowCommand extends Command {
   public FollowCommand(IBaritone var1) {
      super(var1, "follow");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.command.defaults.FollowCommand.NoEntitiesException, baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      var2.requireMin(1);
      ArrayList var5 = new ArrayList();
      ArrayList<net.minecraft.world.entity.EntityType> var6 = new ArrayList<>();
      FollowCommand.FollowGroup var3;
      if (var2.hasExactlyOne()) {
         this.baritone.getFollowProcess().follow((var3 = var2.getEnum(FollowCommand.FollowGroup.class)).filter);
      } else {
         var2.requireMin(2);
         var3 = null;
         FollowCommand.FollowList var4 = var2.getEnum(FollowCommand.FollowList.class);

         while (var2.hasAny()) {
            Object var7 = var2.getDatatypeFor(var4.datatype);
            if (var7 instanceof EntityType) {
               var6.add((EntityType)var7);
            } else if (var7 != null) {
               var5.add((Entity)var7);
            }
         }

         this.baritone.getFollowProcess().follow(var6.isEmpty() ? var5::contains : var1x -> var6.stream().anyMatch(var1xx -> var1x.getType().equals(var1xx)));
      }

      if (var3 != null) {
         this.logDirect(String.format("Following all %s", var3.name().toLowerCase(Locale.US)));
      } else if (var6.isEmpty()) {
         if (var5.isEmpty()) {
            throw new FollowCommand.NoEntitiesException();
         }

         this.logDirect("Following these entities:");
         ((java.util.List<Entity>)var5).stream().map(Entity::toString).forEach(this::logDirect);
      } else {
         this.logDirect("Following these types of entities:");
         var6.stream().map(BuiltInRegistries.ENTITY_TYPE::getKey).map(Objects::requireNonNull).map(Identifier::toString).forEach(this::logDirect);
      }
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandInvalidTypeException {
      if (var2.hasExactlyOne()) {
         return new TabCompleteHelper().append(FollowCommand.FollowGroup.class).append(FollowCommand.FollowList.class).filterPrefix(var2.getString()).stream();
      }

      IDatatypeFor var3;
      try {
         var3 = var2.getEnum(FollowCommand.FollowList.class).datatype;
      } catch (NullPointerException var5) {
         return Stream.empty();
      }

      while (var2.has(2)) {
         if (var2.peekDatatypeOrNull(var3) == null) {
            return Stream.empty();
         }

         var2.get();
      }

      return var2.tabCompleteDatatype(var3);
   }

   @Override
   public String getShortDesc() {
      return "Follow entity things";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "The follow command tells Baritone to follow certain kinds of entities.",
         "",
         "Usage:",
         "> follow entities - Follows all entities.",
         "> follow entity <entity1> <entity2> <...> - Follow certain entities (for example 'skeleton', 'horse' etc.)",
         "> follow players - Follow players",
         "> follow player <username1> <username2> <...> - Follow certain players"
      );
   }

   @KeepName
   private enum FollowGroup {
      ENTITIES(LivingEntity.class::isInstance),
      PLAYERS(Player.class::isInstance);

      final Predicate<Entity> filter;

      FollowGroup(Predicate<Entity> var3) {
         this.filter = var3;
      }
   }

   @KeepName
   private enum FollowList {
      ENTITY(EntityClassById.INSTANCE),
      PLAYER(NearbyPlayer.INSTANCE);

      final IDatatypeFor datatype;

      FollowList(IDatatypeFor var3) {
         this.datatype = var3;
      }
   }

   public static class NoEntitiesException extends CommandErrorMessageException {
      protected NoEntitiesException() {
         super("No valid entities in range!");
      }
   }
}
