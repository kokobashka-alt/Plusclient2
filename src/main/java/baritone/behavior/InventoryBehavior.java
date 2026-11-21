package baritone.behavior;

import baritone.Baritone;
import baritone.api.event.events.TickEvent;
import baritone.api.utils.Helper;
import baritone.utils.ToolSet;
import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class InventoryBehavior extends Behavior implements Helper {
   int ticksSinceLastInventoryMove;
   int[] lastTickRequestedMove;

   public InventoryBehavior(Baritone var1) {
      super(var1);
   }

   @Override
   public void onTick(TickEvent var1) {
      if (Baritone.settings().allowInventory.value) {
         if (var1.getType() != TickEvent.Type.OUT) {
            if (this.ctx.player().containerMenu == this.ctx.player().inventoryMenu) {
               this.ticksSinceLastInventoryMove++;
               if (this.firstValidThrowaway() >= 9) {
                  this.requestSwapWithHotBar(this.firstValidThrowaway(), 8);
               }

               int var2 = this.bestToolAgainst(Blocks.STONE);
               if (var2 >= 9) {
                  this.requestSwapWithHotBar(var2, 0);
               }

               if (this.lastTickRequestedMove != null) {
                  this.logDebug("Remembering to move " + this.lastTickRequestedMove[0] + " " + this.lastTickRequestedMove[1] + " from a previous tick");
                  this.requestSwapWithHotBar(this.lastTickRequestedMove[0], this.lastTickRequestedMove[1]);
               }
            }
         }
      }
   }

   public boolean attemptToPutOnHotbar(int var1, Predicate<Integer> var2) {
      OptionalInt var3 = this.getTempHotbarSlot(var2);
      return !var3.isPresent() || this.requestSwapWithHotBar(var1, var3.getAsInt());
   }

   public OptionalInt getTempHotbarSlot(Predicate<Integer> var1) {
      ArrayList<Integer> var2 = new ArrayList<>();

      for (int var3 = 1; var3 < 8; var3++) {
         if (this.ctx.player().getInventory().getNonEquipmentItems().get(var3).isEmpty() && !var1.test(var3)) {
            var2.add(var3);
         }
      }

      if (var2.isEmpty()) {
         for (int var4 = 1; var4 < 8; var4++) {
            if (!var1.test(var4)) {
               var2.add(var4);
            }
         }
      }

      return var2.isEmpty() ? OptionalInt.empty() : OptionalInt.of((Integer)var2.get(new Random().nextInt(var2.size())));
   }

   private boolean requestSwapWithHotBar(int var1, int var2) {
      this.lastTickRequestedMove = new int[]{var1, var2};
      if (this.ticksSinceLastInventoryMove < Baritone.settings().ticksBetweenInventoryMoves.value) {
         this.logDebug("Inventory move requested but delaying " + this.ticksSinceLastInventoryMove + " " + Baritone.settings().ticksBetweenInventoryMoves.value);
         return false;
      } else if (Baritone.settings().inventoryMoveOnlyIfStationary.value && !this.baritone.getInventoryPauserProcess().stationaryForInventoryMove()) {
         this.logDebug("Inventory move requested but delaying until stationary");
         return false;
      } else {
         this.ctx
            .playerController()
            .windowClick(this.ctx.player().inventoryMenu.containerId, var1 < 9 ? var1 + 36 : var1, var2, ContainerInput.SWAP, this.ctx.player());
         this.ticksSinceLastInventoryMove = 0;
         this.lastTickRequestedMove = null;
         return true;
      }
   }

   private int firstValidThrowaway() {
      var var1 = this.ctx.player().getInventory().getNonEquipmentItems();

      for (int var2 = 0; var2 < var1.size(); var2++) {
         if (Baritone.settings().acceptableThrowawayItems.value.contains(((ItemStack)var1.get(var2)).getItem())) {
            return var2;
         }
      }

      return -1;
   }

   private int bestToolAgainst(Block var1) {
      var var2 = this.ctx.player().getInventory().getNonEquipmentItems();
      int var3 = -1;
      double var4 = -1.0;

      for (int var6 = 0; var6 < var2.size(); var6++) {
         ItemStack var7 = (ItemStack)var2.get(var6);
         if (!var7.isEmpty()
            && (
               !Baritone.settings().itemSaver.value
                  || var7.getDamageValue() + Baritone.settings().itemSaverThreshold.value < var7.getMaxDamage()
                  || var7.getMaxDamage() <= 1
            )
            && var7.getItem().components().has(DataComponents.TOOL)) {
            double var8 = ToolSet.calculateSpeedVsBlock(var7, var1.defaultBlockState());
            if (var8 > var4) {
               var4 = var8;
               var3 = var6;
            }
         }
      }

      return var3;
   }

   public boolean hasGenericThrowaway() {
      for (Item var2 : Baritone.settings().acceptableThrowawayItems.value) {
         if (this.throwaway(false, var1 -> var2.equals(var1.getItem()))) {
            return true;
         }
      }

      return false;
   }

   public boolean selectThrowawayForLocation(boolean var1, int var2, int var3, int var4) {
      BlockState var5 = this.baritone.getBuilderProcess().placeAt(var2, var3, var4, this.baritone.bsi.get0(var2, var3, var4));
      if (var5 != null
         && this.throwaway(
            var1,
            var2x -> var2x.getItem() instanceof BlockItem
               && var5.equals(
                  ((BlockItem)var2x.getItem())
                     .getBlock()
                     .getStateForPlacement(
                        new BlockPlaceContext(
                           new UseOnContext(
                              this.ctx.world(),
                              this.ctx.player(),
                              InteractionHand.MAIN_HAND,
                              var2x,
                              new BlockHitResult(
                                 new Vec3(this.ctx.player().position().x, this.ctx.player().position().y, this.ctx.player().position().z),
                                 Direction.UP,
                                 this.ctx.playerFeet(),
                                 false
                              )
                           ) {}
                        )
                     )
               )
         )) {
         return true;
      }

      if (var5 != null
         && this.throwaway(var1, var1x -> var1x.getItem() instanceof BlockItem && ((BlockItem)var1x.getItem()).getBlock().equals(var5.getBlock()))) {
         return true;
      }

      for (Item var7 : Baritone.settings().acceptableThrowawayItems.value) {
         if (this.throwaway(var1, var1x -> var7.equals(var1x.getItem()))) {
            return true;
         }
      }

      return false;
   }

   public boolean throwaway(boolean var1, Predicate<? super ItemStack> var2) {
      return this.throwaway(var1, var2, Baritone.settings().allowInventory.value);
   }

   public boolean throwaway(boolean var1, Predicate<? super ItemStack> var2, boolean var3) {
      LocalPlayer var4 = this.ctx.player();
      var var5 = var4.getInventory().getNonEquipmentItems();

      for (int var6 = 0; var6 < 9; var6++) {
         ItemStack var7 = (ItemStack)var5.get(var6);
         if (var2.test(var7)) {
            if (var1) {
               var4.getInventory().setSelectedSlot(var6);
            }

            return true;
         }
      }

      if (var2.test(var4.getItemBySlot(EquipmentSlot.OFFHAND))) {
         for (int var8 = 0; var8 < 9; var8++) {
            ItemStack var10 = (ItemStack)var5.get(var8);
            if (var10.isEmpty() || var10.getItem().components().has(DataComponents.TOOL)) {
               if (var1) {
                  var4.getInventory().setSelectedSlot(var8);
               }

               return true;
            }
         }
      }

      if (var3) {
         for (int var9 = 9; var9 < 36; var9++) {
            if (var2.test((ItemStack)var5.get(var9))) {
               if (var1) {
                  this.requestSwapWithHotBar(var9, 7);
                  var4.getInventory().setSelectedSlot(7);
               }

               return true;
            }
         }
      }

      return false;
   }
}
