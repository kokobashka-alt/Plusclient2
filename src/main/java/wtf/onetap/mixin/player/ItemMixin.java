package wtf.onetap.mixin.player;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.module.player.ItemsCooldown;

@Mixin(Item.class)
public class ItemMixin {
   @Inject(method = "use", at = @At("HEAD"), cancellable = true)
   private void onetap$use(Level var1, Player var2, InteractionHand var3, CallbackInfoReturnable<InteractionResult> var4) {
      ItemsCooldown var5 = ItemsCooldown.getInstance();
      if (var2 instanceof LocalPlayer && var5 != null && var5.m_9_boolean(var2.getItemInHand(var3).getItem())) {
         var4.setReturnValue(InteractionResult.FAIL);
      }
   }

   @Inject(method = "finishUsingItem", at = @At("RETURN"))
   private void onetap$finishUsing(ItemStack var1, Level var2, LivingEntity var3, CallbackInfoReturnable<ItemStack> var4) {
      ItemsCooldown var5 = ItemsCooldown.getInstance();
      if (var3 instanceof LocalPlayer var6 && var5 != null) {
         var5.m_7(var1, var6);
      }
   }

   @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
   private void onetap$useDuration(ItemStack var1, LivingEntity var2, CallbackInfoReturnable<Integer> var3) {
      ItemsCooldown var4 = ItemsCooldown.getInstance();
      if (var2 instanceof LocalPlayer && var4 != null && var4.m_9_boolean(var1.getItem())) {
         var3.setReturnValue(0);
      }
   }

   @Inject(method = "useOnRelease", at = @At("HEAD"), cancellable = true)
   private void onetap$useOnRelease(ItemStack var1, CallbackInfoReturnable<Boolean> var2) {
      ItemsCooldown var3 = ItemsCooldown.getInstance();
      if (var3 != null && var3.m_9_boolean(var1.getItem())) {
         var2.setReturnValue(false);
      }
   }
}
