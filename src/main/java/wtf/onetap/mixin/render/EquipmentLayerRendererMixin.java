package wtf.onetap.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo.LayerType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.ArmorDurability;

@Mixin(EquipmentLayerRenderer.class)
public class EquipmentLayerRendererMixin {
   private static final String RENDER_LAYERS = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V";

   @Inject(
      method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
      at = @At("HEAD")
   )
   private void onetap$begin(
      LayerType var1,
      ResourceKey var2,
      Model var3,
      Object var4,
      ItemStack var5,
      PoseStack var6,
      SubmitNodeCollector var7,
      int var8,
      Identifier var9,
      int var10,
      int var11,
      CallbackInfo var12
   ) {
      ArmorDurability var13 = Onetap.MODULE_MANAGER.get(ArmorDurability.class);
      if (var13 != null && var13.isEnabled() && var13.isVisibleFor(var4)) {
         var13.setItemStack(var5);
      }
   }

   @ModifyArg(
      method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
      ),
      index = 6
   )
   private int onetap$tint(int var1) {
      ArmorDurability var2 = Onetap.MODULE_MANAGER.get(ArmorDurability.class);
      return var2 != null ? var2.m_7_int(var1) : var1;
   }

   @Inject(
      method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
      at = @At("RETURN")
   )
   private void onetap$end(
      LayerType var1,
      ResourceKey var2,
      Model var3,
      Object var4,
      ItemStack var5,
      PoseStack var6,
      SubmitNodeCollector var7,
      int var8,
      Identifier var9,
      int var10,
      int var11,
      CallbackInfo var12
   ) {
      ArmorDurability var13 = Onetap.MODULE_MANAGER.get(ArmorDurability.class);
      if (var13 != null) {
         var13.m_4_void();
      }
   }
}
