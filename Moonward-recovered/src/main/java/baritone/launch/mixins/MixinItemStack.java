package baritone.launch.mixins;

import baritone.api.utils.accessor.IItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements IItemStack {
   @Unique
   private int baritoneHash;

   @Shadow
   public abstract int getDamageValue();

   @Shadow
   public abstract Item getItem();

   private void recalculateHash() {
      Item var1 = this.getItem();
      this.baritoneHash = var1 == null ? -1 : var1.hashCode() + this.getDamageValue();
   }

   @Inject(method = "setDamageValue", at = @At("TAIL"))
   private void onItemDamageSet(CallbackInfo var1) {
      this.recalculateHash();
   }

   @Override
   public int getBaritoneHash() {
      if (this.baritoneHash == 0) {
         this.recalculateHash();
      }

      return this.baritoneHash;
   }
}
