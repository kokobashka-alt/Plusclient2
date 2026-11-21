package wtf.onetap.module.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.SpectralArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.mixin.entity.projectile.AbstractArrowInvoker;
import wtf.onetap.mixin.entity.projectile.AbstractHurtingProjectileInvoker;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.combat.AntiBot;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.render.Render2D;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.WorldToScreen;

@ModuleInformation(name = "Trajectories", description = "Траектории предметов в руке, летящих снарядов и падающих предметов", category = Category.RENDER)
public class Trajectories extends Module {
   private static final int VALUE = ColorUtil.rgba(255, 70, 70, 255);
   private final BooleanSetting themeColor = new BooleanSetting("Цвет от темы", true).build();
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(140, 180, 255, 210)).visibleWhen(() -> !(Boolean)this.themeColor.getValue()).build();
   private final FloatSetting thickness = new FloatSetting("Толщина", 2.0F, 1.0F, 2.0F, 0.1F).build();
   private final Map<Long, Trajectories.StateHolder> longMap = new LinkedHashMap<>();
   private final List<Trajectories.DataRecord2> c2List = new ArrayList<>();
   private final Map<Long, Trajectories.StateHolder2> longMap2 = new HashMap<>();
   private final Map<Integer, Trajectories.StateHolder3> integerMap = new HashMap<>();
   private final Map<Integer, Trajectories.DataRecord6> integerMap2 = new HashMap<>();
   private final Map<Integer, Trajectories.DataRecord4> integerMap3 = new HashMap<>();
   private final Set<Integer> integerSet = new HashSet<>();
   private long longValue;
   private long longValue2;
   private double doubleValue;
   private float floatValue = 1.0F;
   private static String text;

   @EventTarget(priority = EventPriority.HIGH)
   private void onRender3D(Render3DEvent event) {
      this.c2List.clear();
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         this.m_1_void();
         float floatValue2 = event.getFloatValue();
         Vec3 vec3 = event.getVec3();
         this.m_6(floatValue2, vec3);
         this.m_7_void(floatValue2);
         this.m_4_void(floatValue2);
         Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
         this.m_4(matrix4f, vec3, this.getColor());
      } else {
         this.m_3_void();
      }
   }

   @EventTarget(priority = EventPriority.HIGH)
   private void onRender2D(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null && !this.c2List.isEmpty()) {
         CameraRenderState cameraRenderState = MinecraftAccess.MINECRAFT.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
         wtf.onetap.util.WorldToScreen.setMatrices(new Matrix4f(cameraRenderState.projectionMatrix), new Matrix4f(cameraRenderState.viewRotationMatrix));
         HashSet set = new HashSet();

         for (Trajectories.DataRecord2 c2 : this.c2List) {
            wtf.onetap.util.WorldToScreen.DataRecord c9 = wtf.onetap.util.WorldToScreen.project(c2.f_74.x, c2.f_74.y, c2.f_74.z);
            if (c9 != null) {
               set.add(c2.f_78);
               float floatValue2 = c2.f_8__;
               String text2 = String.format(Locale.ROOT, "%.1fs", c2.f_8_k);
               float floatValue3 = 8.0F;
               float floatValue4 = 9.0F;
               float floatValue5 = 2.0F;
               float floatValue6 = 2.0F;
               float floatValue7 = 10.0F;
               float floatValue8 = floatValue5 * 2.0F + floatValue4 + floatValue6 + Draw2D.getTextWidth(text2, Fonts.SUPPLIER15, floatValue3);
               float floatValue9 = c9.m_2r_float() - floatValue8 / 2.0F;
               float floatValue10 = c9.m_2___float() + 6.0F;
               float floatValue11 = floatValue10 + (floatValue7 - floatValue4) / 2.0F;
               Draw2D.rect(floatValue9, floatValue10, floatValue8, floatValue7, ColorUtil.rgba(0, 0, 0, (int)(110.0F * floatValue2)));
               Render2D.drawItem(this.m_6(event, c2.f_78, c2.f_5w), floatValue9 + floatValue5, floatValue11, floatValue4, ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue2)));
               Render2D.drawItemDecorations(c2.f_5w, floatValue9 + floatValue5, floatValue11, floatValue4, (int)(255.0F * floatValue2));
               Draw2D.drawStyledText(
                  text2,
                  Fonts.SUPPLIER15,
                  floatValue9 + floatValue5 + floatValue4 + floatValue6,
                  Draw2D.getTextBaseline(Fonts.SUPPLIER15, floatValue3, floatValue10, floatValue7),
                  ColorUtil.rgba(245, 245, 245, (int)(255.0F * floatValue2)),
                  floatValue3
               );
            }
         }

         this.longMap2.keySet().removeIf(longObj -> !set.contains(longObj));
      } else {
         this.longMap2.clear();
      }
   }

   @Override
   public void onDisable() {
      this.m_3_void();
   }

   private void m_3_void() {
      this.longMap.clear();
      this.c2List.clear();
      this.longMap2.clear();
      this.integerMap.clear();
      this.integerMap2.clear();
      this.integerMap3.clear();
      this.integerSet.clear();
      this.longValue2 = 0L;
   }

   private void m_1_void() {
      long longValue3 = System.nanoTime();
      this.doubleValue = this.longValue2 == 0L ? 0.016666666666666666 : Mth.clamp((longValue3 - this.longValue2) / 1.0E9, 0.0, 0.25);
      this.longValue2 = longValue3;
      this.longValue++;
   }

   private int getColor() {
      return this.themeColor.getValue() ? ColorUtil.primaryColor() : (Integer)this.color.getValue();
   }

   private void m_6(float floatValue2, Vec3 vec3) {
      HashSet set = new HashSet();
      AntiBot antiBot = Onetap.MODULE_MANAGER.get(AntiBot.class);

      for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
         if (player.isAlive() && !player.isSpectator() && (player == MinecraftAccess.MINECRAFT.player || !this.m_0(player, antiBot))) {
            set.add(player.getId());
            this.m_8(player, floatValue2, vec3);
         }
      }

      this.integerMap.keySet().removeIf(integer -> !set.contains(integer));
   }

   private void m_8(Player player, float floatValue2, Vec3 vec3) {
      Trajectories.StateHolder3 c8 = this.m_0_C8(player);
      ItemStack itemStack = this.m_5_ItemStack(player);
      if (!itemStack.isEmpty()) {
         Trajectories.DataRecord7 c8_k = this.m_9(player, itemStack);
         boolean flag = player == MinecraftAccess.MINECRAFT.player;
         boolean flag2 = flag && MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson();
         Vec3 vec32 = player.getPosition(floatValue2);
         Vec3 vec33 = vec32.add(0.0, Mth.lerp(floatValue2, c8.floatValue, c8.floatValue2) - 0.1, 0.0);
         Vec3 vec34 = c8_k.f_09 ? c8.vec3.lerp(c8.vec32, floatValue2) : Vec3.ZERO;
         AABB aABB = player.getBoundingBox().move(vec32.subtract(player.position())).inflate(0.15);
         Trajectories.ModeKind c0 = itemStack.is(Items.SPLASH_POTION) ? Trajectories.ModeKind.SPLASH : (itemStack.is(Items.LINGERING_POTION) ? Trajectories.ModeKind.LINGERING : Trajectories.ModeKind.MARKER);
         Vec3[] vec3Array = this.m_5(player, itemStack, c8_k, floatValue2);

         for (int i = 0; i < vec3Array.length; i++) {
            this.floatValue = floatValue2;
            Trajectories.DataRecord3 c3 = this.m_4(c8_k.f_4x, vec33, vec3Array[i].scale(c8_k.f_1_z).add(vec34), player, player);
            this.floatValue = 1.0F;
            List list = flag2 ? this.m_5(c3.f_97, vec3) : this.m_8(c3.f_97, aABB);
            this.m_8(
               packPos(0, player.getId(), i),
               new Trajectories.DataRecord5(list, c3, flag2 ? Trajectories.ModeKind3.CAMERA : Trajectories.ModeKind3.SOLID, 0.4, 2.0, flag, c0, ItemStack.EMPTY, 0.0F, 0.0)
            );
         }
      }
   }

   private Trajectories.StateHolder3 m_0_C8(Player player) {
      Trajectories.StateHolder3 c8 = this.integerMap.get(player.getId());
      if (c8 == null) {
         c8 = new Trajectories.StateHolder3(player.tickCount, this.m_2_Vec3(player), player.getEyeHeight());
         this.integerMap.put(player.getId(), c8);
      } else if (c8.value != player.tickCount) {
         boolean flag = c8.value + 1 == player.tickCount;
         Vec3 vec3 = this.m_2_Vec3(player);
         float floatValue2 = player.getEyeHeight();
         c8.vec3 = flag ? c8.vec32 : vec3;
         c8.floatValue = flag ? c8.floatValue2 : floatValue2;
         c8.vec32 = vec3;
         c8.floatValue2 = floatValue2;
         c8.value = player.tickCount;
      }

      return c8;
   }

   private Vec3 m_2_Vec3(Player player) {
      Vec3 vec3 = new Vec3(player.getX() - player.xo, player.onGround() ? 0.0 : player.getY() - player.yo, player.getZ() - player.zo);
      return vec3.lengthSqr() > 16.0 ? Vec3.ZERO : vec3;
   }

   private boolean m_0(Player player, AntiBot antiBot) {
      ClientPacketListener clientPacketListener = MinecraftAccess.MINECRAFT.getConnection();
      return clientPacketListener != null && clientPacketListener.getPlayerInfo(player.getUUID()) == null ? true : antiBot != null && antiBot.isBot(player);
   }

   private ItemStack m_5_ItemStack(Player player) {
      ItemStack itemStack = player.getMainHandItem();
      if (!itemStack.isEmpty() && this.m_9(player, itemStack) != null) {
         return itemStack;
      }

      ItemStack itemStack2 = player.getOffhandItem();
      return !itemStack2.isEmpty() && this.m_9(player, itemStack2) != null ? itemStack2 : ItemStack.EMPTY;
   }

   private Trajectories.DataRecord7 m_9(Player player, ItemStack itemStack) {
      Item item = itemStack.getItem();
      if (item == Items.ENDER_PEARL || item == Items.SNOWBALL || item == Items.EGG || item == Items.BLUE_EGG || item == Items.BROWN_EGG) {
         return new Trajectories.DataRecord7(1.5, Trajectories.DataRecord8.m_0_C8_p(0.03), 0.0F, true);
      }

      if (item == Items.SPLASH_POTION || item == Items.LINGERING_POTION) {
         return new Trajectories.DataRecord7(0.5, Trajectories.DataRecord8.m_0_C8_p(0.05), -20.0F, true);
      }

      if (item == Items.EXPERIENCE_BOTTLE) {
         return new Trajectories.DataRecord7(0.7, Trajectories.DataRecord8.m_0_C8_p(0.07), -20.0F, true);
      }

      if (item instanceof BowItem) {
         if (!this.isUsing(player, itemStack)) {
            return null;
         }

         float floatValue2 = BowItem.getPowerForTime(player.getTicksUsingItem());
         return floatValue2 < 0.1F ? null : new Trajectories.DataRecord7(3.0 * floatValue2, Trajectories.DataRecord8.m_5_C8_p(0.05, 0.6), 0.0F, true);
      } else {
         if (item instanceof CrossbowItem) {
            ChargedProjectiles chargedProjectiles = itemStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
            return !chargedProjectiles.isEmpty() && !chargedProjectiles.contains(Items.FIREWORK_ROCKET) ? new Trajectories.DataRecord7(3.15, Trajectories.DataRecord8.m_5_C8_p(0.05, 0.6), 0.0F, false) : null;
         }

         if (item instanceof TridentItem) {
            if (!this.isUsing(player, itemStack) || player.getTicksUsingItem() < 10) {
               return null;
            } else {
               return EnchantmentHelper.getTridentSpinAttackStrength(itemStack, player) > 0.0F ? null : new Trajectories.DataRecord7(2.5, Trajectories.DataRecord8.m_5_C8_p(0.05, 0.99), 0.0F, true);
            }
         } else {
            return null;
         }
      }
   }

   private boolean isUsing(Player player, ItemStack itemStack) {
      return player.isUsingItem() && player.getUseItem() == itemStack;
   }

   private Vec3[] m_5(Player player, ItemStack itemStack, Trajectories.DataRecord7 c8_k, float floatValue2) {
      float floatValue3 = player.getViewYRot(floatValue2);
      float floatValue4 = player.getViewXRot(floatValue2);
      Vec3 vec3 = this.m_2_Vec3(floatValue3, floatValue4, c8_k.f_9a);
      return itemStack.getItem() instanceof CrossbowItem && this.m_3_int(itemStack) > 0
         ? new Vec3[]{this.m_2_Vec3(floatValue3 - 10.0F, floatValue4, 0.0F), vec3, this.m_2_Vec3(floatValue3 + 10.0F, floatValue4, 0.0F)}
         : new Vec3[]{vec3};
   }

   private Vec3 m_2_Vec3(float floatValue2, float floatValue3, float floatValue4) {
      float floatValue5 = (float) (Math.PI / 180.0);
      double doubleValue2 = -Mth.sin(floatValue2 * floatValue5) * Mth.cos(floatValue3 * floatValue5);
      double doubleValue3 = -Mth.sin((floatValue3 + floatValue4) * floatValue5);
      double doubleValue4 = Mth.cos(floatValue2 * floatValue5) * Mth.cos(floatValue3 * floatValue5);
      return new Vec3(doubleValue2, doubleValue3, doubleValue4).normalize();
   }

   private int m_3_int(ItemStack itemStack) {
      var optional = MinecraftAccess.MINECRAFT.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.MULTISHOT);
      return optional.<Integer>map(reference -> EnchantmentHelper.getItemEnchantmentLevel(reference, itemStack)).orElse(0);
   }

   private List<Vec3> m_5(List<Vec3> vec3List, Vec3 vec3) {
      if (vec3List.isEmpty()) {
         return vec3List;
      }

      Vec3 vec32 = vec3.subtract((Vec3)vec3List.get(0));
      ArrayList list = new ArrayList(vec3List.size());
      double doubleValue2 = 0.0;

      for (int i = 0; i < vec3List.size(); i++) {
         if (i > 0) {
            doubleValue2 += ((Vec3)vec3List.get(i)).distanceTo((Vec3)vec3List.get(i - 1));
         }

         double doubleValue3 = 1.0 - smoothStep(doubleValue2 / 4.0);
         list.add(doubleValue3 > 0.0 ? ((Vec3)vec3List.get(i)).add(vec32.scale(doubleValue3)) : (Vec3)vec3List.get(i));
      }

      return list;
   }

   private List<Vec3> m_8(List<Vec3> vec3List, AABB aABB) {
      for (int i = 0; i < vec3List.size() - 1; i++) {
         Vec3 vec3 = (Vec3)vec3List.get(i);
         Vec3 vec32 = (Vec3)vec3List.get(i + 1);
         if (!aABB.contains(vec32)) {
            ArrayList list = new ArrayList(vec3List.size() - i);
            list.add(aABB.contains(vec3) ? aABB.clip(vec32, vec3).orElse(vec3) : vec3);
            list.addAll(vec3List.subList(i + 1, vec3List.size()));
            return list;
         }
      }

      return List.of();
   }

   private void m_7_void(float floatValue2) {
      boolean flag = MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson();
      HashSet set = new HashSet();

      for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
         if (entity instanceof Projectile projectile && projectile.isAlive()) {
            Trajectories.DataRecord8 c8_p = this.m_2(projectile);
            if (c8_p != null) {
               set.add(projectile.getId());
               Trajectories.DataRecord6 c8_c = this.integerMap2.get(projectile.getId());
               if (c8_c == null || c8_c.f_7_q != projectile.tickCount) {
                  c8_c = new Trajectories.DataRecord6(this.m_4(c8_p, projectile.position(), projectile.getDeltaMovement(), projectile, projectile.getOwner()), projectile.tickCount);
                  this.integerMap2.put(projectile.getId(), c8_c);
               }

               Trajectories.DataRecord3 c3 = c8_c.f_0_v;
               ArrayList list = new ArrayList(c3.f_97.size() + 1);
               list.add(projectile.getPosition(floatValue2));
               list.addAll(c3.f_97);
               boolean flag2 = flag && projectile.getOwner() == MinecraftAccess.MINECRAFT.player;
               Trajectories.ModeKind c0 = projectile instanceof ThrownSplashPotion ? Trajectories.ModeKind.SPLASH : (projectile instanceof ThrownLingeringPotion ? Trajectories.ModeKind.LINGERING : Trajectories.ModeKind.MARKER);
               ItemStack itemStack = c3.f_9_y != null ? this.getProjectileItem(projectile) : ItemStack.EMPTY;
               float floatValue3 = (float)Math.max(0.0, (c3.f_7_m - floatValue2) / 20.0);
               this.m_8(packPos(1, projectile.getId(), 0), new Trajectories.DataRecord5(list, c3, flag2 ? Trajectories.ModeKind3.CAMERA : Trajectories.ModeKind3.SOLID, 2.0, 6.0, false, c0, itemStack, floatValue3, 0.0));
            }
         }
      }

      this.integerMap2.keySet().removeIf(integer -> !set.contains(integer));
   }

   private Trajectories.DataRecord8 m_2(Projectile projectile) {
      if (projectile instanceof ThrowableProjectile) {
         return Trajectories.DataRecord8.m_0_C8_p(projectile.getGravity());
      } else if (projectile instanceof AbstractArrow abstractArrow) {
         AbstractArrowInvoker abstractArrowInvoker = (AbstractArrowInvoker)abstractArrow;
         return abstractArrowInvoker.onetap$isInGround() ? null : Trajectories.DataRecord8.m_5_C8_p(projectile.getGravity(), abstractArrowInvoker.onetap$getWaterInertia());
      } else if (projectile instanceof AbstractHurtingProjectile abstractHurtingProjectile) {
         AbstractHurtingProjectileInvoker abstractHurtingProjectileInvoker = (AbstractHurtingProjectileInvoker)abstractHurtingProjectile;
         return new Trajectories.DataRecord8(Trajectories.ModeKind2.HURTING, 0.0, abstractHurtingProjectileInvoker.onetap$getInertia(), abstractHurtingProjectileInvoker.onetap$getLiquidInertia(), abstractHurtingProjectile.accelerationPower);
      } else {
         return null;
      }
   }

   private ItemStack getProjectileItem(Projectile projectile) {
      if (projectile instanceof ItemSupplier itemSupplier) {
         return itemSupplier.getItem();
      } else if (projectile instanceof ThrownTrident) {
         return new ItemStack(Items.TRIDENT);
      } else if (projectile instanceof SpectralArrow) {
         return new ItemStack(Items.SPECTRAL_ARROW);
      } else if (projectile instanceof AbstractArrow) {
         return new ItemStack(Items.ARROW);
      } else if (projectile instanceof AbstractWindCharge) {
         return new ItemStack(Items.WIND_CHARGE);
      } else if (projectile instanceof WitherSkull) {
         return new ItemStack(Items.WITHER_SKELETON_SKULL);
      } else {
         return projectile instanceof DragonFireball ? new ItemStack(Items.DRAGON_BREATH) : ItemStack.EMPTY;
      }
   }

   private void m_4_void(float floatValue2) {
      HashSet set = new HashSet();

      for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
         if (entity instanceof ItemEntity itemEntity && itemEntity.isAlive()) {
            set.add(itemEntity.getId());
            if (itemEntity.onGround()) {
               this.integerSet.add(itemEntity.getId());
            } else if (!this.integerSet.contains(itemEntity.getId()) && !itemEntity.isInWater() && !itemEntity.isInLava() && !itemEntity.isNoGravity() && !this.isItemStopped(itemEntity)) {
               Trajectories.DataRecord4 c4 = this.integerMap3.get(itemEntity.getId());
               if (c4 == null || c4.f_0u != itemEntity.tickCount) {
                  c4 = this.simulateItem(itemEntity);
                  this.integerMap3.put(itemEntity.getId(), c4);
               }

               ArrayList list = new ArrayList(c4.f_3f.size() + 1);
               list.add(itemEntity.getPosition(floatValue2));
               list.addAll(c4.f_3f);
               Trajectories.DataRecord3 c3 = new Trajectories.DataRecord3(list, c4.f_8i, c4.f_9_t ? Direction.UP : null, null, 0);
               float floatValue3 = (float)Math.max(0.0, c4.f_4__ - floatValue2 / 20.0);
               this.m_8(
                  packPos(2, itemEntity.getId(), 0),
                  new Trajectories.DataRecord5(list, c3, Trajectories.ModeKind3.RIBBON, 0.0, 0.0, false, Trajectories.ModeKind.NONE, c4.f_9_t ? c4.f_4p : ItemStack.EMPTY, floatValue3, 0.25)
               );
            }
         }
      }

      this.integerMap3.keySet().removeIf(integer -> !set.contains(integer));
      this.integerSet.retainAll(set);
   }

   private boolean isItemStopped(ItemEntity itemEntity) {
      return itemEntity.tickCount > 2 && itemEntity.getDeltaMovement().lengthSqr() < 1.0E-6 && itemEntity.getX() == itemEntity.xo && itemEntity.getY() == itemEntity.yo && itemEntity.getZ() == itemEntity.zo;
   }

   private Trajectories.DataRecord4 simulateItem(ItemEntity itemEntity) {
      ArrayList list = new ArrayList();
      Vec3 vec3 = itemEntity.position();
      Vec3 vec32 = itemEntity.getDeltaMovement();
      list.add(vec3);

      for (int i = 1; i <= 200; i++) {
         vec32 = vec32.add(0.0, -0.04, 0.0);
         BlockHitResult blockHitResult = MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec3, vec3.add(vec32), Block.COLLIDER, Fluid.NONE, itemEntity));
         if (blockHitResult.getType() == Type.MISS) {
            vec3 = vec3.add(vec32);
         } else {
            vec3 = blockHitResult.getLocation();
            if (blockHitResult.getDirection() == Direction.UP) {
               list.add(vec3);
               return new Trajectories.DataRecord4(list, vec3, i / 20.0F, itemEntity.tickCount, itemEntity.getItem(), true);
            }

            vec32 = vec32.with(blockHitResult.getDirection().getAxis(), 0.0);
         }

         vec32 = vec32.scale(0.98);
         list.add(vec3);
      }

      return new Trajectories.DataRecord4(list, vec3, 10.0F, itemEntity.tickCount, itemEntity.getItem(), false);
   }

   private Trajectories.DataRecord3 m_4(Trajectories.DataRecord8 c8_p, Vec3 vec3, Vec3 vec32, Entity entity, Entity entity2) {
      Vec3 vec33 = vec3;
      Vec3 vec34 = vec32;
      ArrayList list = new ArrayList();
      list.add(vec33);

      for (int i = 1; i <= 300 && MinecraftAccess.MINECRAFT.level.hasChunkAt(Mth.floor(vec33.x), Mth.floor(vec33.z)); i++) {
         switch (c8_p.f_9z.ordinal()) {
            case 0:
               vec34 = vec34.subtract(0.0, c8_p.f_6e, 0.0).scale(this.isInWater(vec33) ? c8_p.f_6_m : c8_p.f_6_z);
               break;
            case 2:
               vec34 = vec34.add(vec34.normalize().scale(c8_p.f_6_u)).scale(this.isInWater(vec33) ? c8_p.f_6_m : c8_p.f_6_z);
         }

         Vec3 vec35 = vec33.add(vec34);
         Trajectories.DataRecord3 c3 = this.m_7(list, vec33, vec35, entity, entity2, i);
         if (c3 != null) {
            return c3;
         }

         list.add(vec35);
         vec33 = vec35;
         if (c8_p.f_9z == Trajectories.ModeKind2.ARROW) {
            vec34 = vec34.scale(this.isInWater(vec33) ? c8_p.f_6_m : c8_p.f_6_z).subtract(0.0, c8_p.f_6e, 0.0);
         }

         if (vec33.y <= MinecraftAccess.MINECRAFT.level.getMinY() || vec34.lengthSqr() < 1.0E-7) {
            break;
         }
      }

      return new Trajectories.DataRecord3(list, vec33, null, null, list.size() - 1);
   }

   private boolean isInWater(Vec3 vec3) {
      return MinecraftAccess.MINECRAFT.level.getFluidState(BlockPos.containing(vec3)).is(FluidTags.WATER);
   }

   private Trajectories.DataRecord3 m_7(List<Vec3> vec3List, Vec3 vec3, Vec3 vec32, Entity entity, Entity entity2, int value) {
      BlockHitResult blockHitResult = MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec3, vec32, Block.COLLIDER, Fluid.NONE, entity));
      Vec3 vec33 = blockHitResult.getType() == Type.BLOCK ? blockHitResult.getLocation() : vec32;
      Trajectories.DataRecord c1 = this.m_4(vec3, vec33, entity, entity2);
      if (c1 != null) {
         vec3List.add(c1.f_6x);
         return new Trajectories.DataRecord3(vec3List, c1.f_6x, c1.f_0i, c1.f_8v, value);
      } else if (blockHitResult.getType() == Type.BLOCK) {
         vec3List.add(blockHitResult.getLocation());
         return new Trajectories.DataRecord3(vec3List, blockHitResult.getLocation(), blockHitResult.getDirection(), null, value);
      } else {
         return null;
      }
   }

   private Trajectories.DataRecord m_4(Vec3 vec3, Vec3 vec32, Entity entity, Entity entity2) {
      Trajectories.DataRecord c1 = null;
      double doubleValue2 = Double.MAX_VALUE;

      for (Entity entity3 : MinecraftAccess.MINECRAFT
         .level
         .getEntities(
            entity, new AABB(vec3, vec32).inflate(1.0), entity4 -> entity4 != null && entity4 != entity2 && entity4.isAlive() && entity4.isPickable() && !entity4.isSpectator()
         )) {
         AABB aABB = entity3.getBoundingBox().move(entity3.getPosition(this.floatValue).subtract(entity3.position())).inflate(0.3);
         var optional = aABB.clip(vec3, vec32);
         if (!optional.isEmpty()) {
            double doubleValue3 = vec3.distanceToSqr((Vec3)optional.get());
            if (doubleValue3 < doubleValue2) {
               doubleValue2 = doubleValue3;
               c1 = new Trajectories.DataRecord(entity3, (Vec3)optional.get(), this.m_2(aABB, (Vec3)optional.get()));
            }
         }
      }

      return c1;
   }

   private Direction m_2(AABB aABB, Vec3 vec3) {
      Direction[] directionArray = new Direction[]{Direction.UP, Direction.DOWN, Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
      double[] doubleArray = new double[]{
         Math.abs(vec3.y - aABB.maxY),
         Math.abs(vec3.y - aABB.minY),
         Math.abs(vec3.x - aABB.minX),
         Math.abs(vec3.x - aABB.maxX),
         Math.abs(vec3.z - aABB.minZ),
         Math.abs(vec3.z - aABB.maxZ)
      };
      int value = 0;

      for (int i = 1; i < directionArray.length; i++) {
         if (doubleArray[i] < doubleArray[value]) {
            value = i;
         }
      }

      return directionArray[value];
   }

   private void m_8(long longValue3, Trajectories.DataRecord5 c5) {
      var vec3List = this.m_7_List(c5.f_4l);
      if (!vec3List.isEmpty()) {
         Trajectories.StateHolder c6 = this.longMap.computeIfAbsent(longValue3, longObj -> new Trajectories.StateHolder());
         boolean flag = c6.vec3Array == null || c6.doubleValue <= 0.0 || c6.vec3Array[0].distanceToSqr((Vec3)vec3List.get(0)) > 4.0;
         if (flag) {
            c6.vec3Array = vec3List.toArray(new Vec3[0]);
            c6.vec3 = null;
            c6.vec32 = null;
            c6.flag = false;
            c6.doubleValue2 = c5.f_1_l.f_6u != null ? 1.0 : 0.0;
            c6.doubleValue3 = -1.0;
         } else {
            double doubleValue2 = c5.f_2o ? 1.0 : 1.0 - Math.exp(-this.doubleValue * 20.0);
            double doubleValue3 = 0.0;

            for (int i = 0; i < 96; i++) {
               if (i > 0) {
                  doubleValue3 += ((Vec3)vec3List.get(i)).distanceTo((Vec3)vec3List.get(i - 1));
               }

               double doubleValue4 = Mth.clamp(doubleValue3 / 6.0, 0.0, 1.0);
               c6.vec3Array[i] = c6.vec3Array[i].lerp((Vec3)vec3List.get(i), 1.0 - (1.0 - doubleValue2) * doubleValue4);
            }

            c6.vec3Array[0] = (Vec3)vec3List.get(0);
         }

         c6.longValue = this.longValue;
         c6.c5 = c5;
      }
   }

   private List<Vec3> m_7_List(List<Vec3> vec3List) {
      if (vec3List.size() < 2) {
         return List.of();
      }

      double[] doubleArray = new double[vec3List.size()];

      for (int i = 1; i < vec3List.size(); i++) {
         doubleArray[i] = doubleArray[i - 1] + ((Vec3)vec3List.get(i)).distanceTo((Vec3)vec3List.get(i - 1));
      }

      double doubleValue2 = doubleArray[doubleArray.length - 1];
      if (doubleValue2 < 1.0E-4) {
         return List.of();
      }

      ArrayList list = new ArrayList(96);
      int value = 0;

      for (int i2 = 0; i2 < 96; i2++) {
         double doubleValue3 = doubleValue2 * i2 / 95.0;

         while (value < vec3List.size() - 2 && doubleArray[value + 1] < doubleValue3) {
            value++;
         }

         double doubleValue4 = doubleArray[value + 1] - doubleArray[value];
         double doubleValue5 = doubleValue4 < 1.0E-9 ? 0.0 : (doubleValue3 - doubleArray[value]) / doubleValue4;
         list.add(((Vec3)vec3List.get(value)).lerp((Vec3)vec3List.get(value + 1), Mth.clamp(doubleValue5, 0.0, 1.0)));
      }

      return list;
   }

   private void m_4(Matrix4f matrix4f, Vec3 vec3, int value) {
      float floatValue2 = (Float)this.thickness.getValue();
      double doubleValue2 = 1.0 - Math.exp(-this.doubleValue * 8.0);
      var iterator = this.longMap.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         Trajectories.StateHolder c6 = (Trajectories.StateHolder)entry.getValue();
         boolean flag = c6.longValue == this.longValue;
         c6.doubleValue = approach(c6.doubleValue, flag ? 1.0 : 0.0, this.doubleValue * 6.5);
         if (!flag && c6.doubleValue <= 0.0) {
            iterator.remove();
         } else {
            Trajectories.DataRecord5 c5 = c6.c5;
            double doubleValue3 = smoothStep(c6.doubleValue);
            c6.doubleValue2 = Mth.lerp(doubleValue2, c6.doubleValue2, c5.f_1_l.f_6u != null ? 1.0 : 0.0);
            this.m_4(matrix4f, vec3, c6, value, doubleValue3, floatValue2);
            if (c5.f_1_l.f_9_y != null) {
               Vec3 vec32 = c6.vec3Array[c6.vec3Array.length - 1];
               switch (c5.f_2m.ordinal()) {
                  case 1:
                     int value2 = ColorUtil.lerpColor(value, VALUE, (float)c6.doubleValue2);
                     this.m_3(matrix4f, vec3, c6, vec32, scaleAlpha(value2, 0.9019607843137255 * doubleValue3), floatValue2);
                     break;
                  case 2:
                  case 3:
                     this.m_4(matrix4f, vec3, c6, vec32, doubleValue2, value, doubleValue3, floatValue2);
               }

               if (!c5.f_6_w.isEmpty()) {
                  this.c2List.add(new Trajectories.DataRecord2((Long)entry.getKey(), vec32.add(0.0, c5.f_8n, 0.0), c5.f_71, c5.f_6_w, (float)doubleValue3));
               }
            }
         }
      }
   }

   private void m_4(Matrix4f matrix4f, Vec3 vec3, Trajectories.StateHolder c6, int value, double doubleValue2, float floatValue2) {
      Trajectories.DataRecord5 c5 = c6.c5;
      int value2 = c6.vec3Array.length;
      int[] intArray = new int[value2];

      for (int i = 0; i < value2; i++) {
         double doubleValue3 = switch (c5.f_2_l.ordinal()) {
            case 0 -> 0.7450980392156863;
            case 1 -> 0.7450980392156863 * smoothStep((c6.vec3Array[i].distanceTo(vec3) - c5.f_8m) / (c5.f_89 - c5.f_8m));
            case 2 -> Math.min(1.0, Math.min(i, value2 - 1 - i) / 8.0);
            default -> throw new MatchException(null, null);
         };
         intArray[i] = scaleAlpha(value, doubleValue3 * doubleValue2);
      }

      Line3DRenderer.m_1(matrix4f, List.of(c6.vec3Array), intArray, vec3, floatValue2);
   }

   private void m_3(Matrix4f matrix4f, Vec3 vec3, Trajectories.StateHolder c6, Vec3 vec32, int value, float floatValue2) {
      Vec3 vec33 = Vec3.atLowerCornerOf(c6.c5.f_1_l.f_9_y.getUnitVec3i());
      Vec3 vec34 = c6.vec3 == null ? vec33 : c6.vec3.lerp(vec33, 1.0 - Math.exp(-this.doubleValue * 18.0));
      vec34 = vec34.lengthSqr() < 1.0E-6 ? vec33 : vec34.normalize();
      c6.vec3 = vec34;
      Vec3 vec35 = c6.vec32 == null ? null : c6.vec32.subtract(vec34.scale(vec34.dot(c6.vec32)));
      if (vec35 == null || vec35.lengthSqr() < 1.0E-4) {
         vec35 = Math.abs(vec34.y) > 0.9 ? new Vec3(1.0, 0.0, 0.0) : vec34.cross(new Vec3(0.0, 1.0, 0.0));
      }

      vec35 = vec35.normalize();
      c6.vec32 = vec35;
      Vec3 vec36 = vec34.cross(vec35).normalize();
      Vec3 vec37 = vec32.add(vec34.scale(0.004));
      ArrayList list = new ArrayList(49);

      for (int i = 0; i <= 48; i++) {
         double doubleValue2 = (Math.PI * 2) * i / 48.0;
         list.add(vec37.add(vec35.scale(Math.cos(doubleValue2) * 0.35)).add(vec36.scale(Math.sin(doubleValue2) * 0.35)));
      }

      this.m_9(matrix4f, vec3, list, value, floatValue2);
      this.m_9(matrix4f, vec3, List.of(vec37.add(vec35.scale(-0.35)), vec37.add(vec35.scale(0.35))), value, floatValue2);
      this.m_9(matrix4f, vec3, List.of(vec37.add(vec36.scale(-0.35)), vec37.add(vec36.scale(0.35))), value, floatValue2);
   }

   private void m_4(Matrix4f matrix4f, Vec3 vec3, Trajectories.StateHolder c6, Vec3 vec32, double doubleValue2, int value, double doubleValue3, float floatValue2) {
      Notifications.m_5_C3_sArr();
      Trajectories.DataRecord5 c5 = c6.c5;
      boolean flag = c5.f_2m == Trajectories.ModeKind.SPLASH;
      double doubleValue4 = flag ? 4.0 : 3.0;
      double doubleValue5 = this.m_4(vec32, c5.f_1_l.f_9_y, c5.f_1_l.f_6u != null);
      c6.doubleValue4 = c6.flag ? Mth.lerp(1.0 - Math.exp(-this.doubleValue * 14.0), c6.doubleValue4, doubleValue5) : doubleValue5;
      c6.flag = true;
      double doubleValue6 = this.m_4(c5.f_1_l.f_9_l, doubleValue4, flag, c5.f_1_l.f_6u) ? 1.0 : 0.0;
      c6.doubleValue3 = c6.doubleValue3 < 0.0 ? doubleValue6 : Mth.lerp(doubleValue2, c6.doubleValue3, doubleValue6);
      int value2 = ColorUtil.lerpColor(ColorUtil.withAlpha(value, 255), VALUE, (float)c6.doubleValue3);
      double doubleValue7 = doubleValue4 * (0.8 + 0.2 * doubleValue3);
      double doubleValue8 = c6.doubleValue4 + 0.03;
      float floatValue3 = (float)(vec32.x - vec3.x);
      float floatValue4 = (float)(doubleValue8 - vec3.y);
      float floatValue5 = (float)(vec32.z - vec3.z);
      float floatValue6 = (float)doubleValue7;
      float floatValue7 = floatValue6 * 0.7F;
      int value3 = ColorUtil.withAlpha(value2, (int)(10.0 * doubleValue3));
      int value4 = ColorUtil.withAlpha(value2, (int)(35.0 * doubleValue3));
      int value5 = ColorUtil.withAlpha(value2, (int)(80.0 * doubleValue3));
      Box3DRenderer.m_8(matrix4f, 576, bufferBuilder -> {
         for (int i = 0; i < 72; i++) {
            double doubleValue9 = (Math.PI * 2) * i / 72.0;
            double doubleValue10 = (Math.PI * 2) * (i + 1) / 72.0;
            float floatValue8 = (float)Math.cos(doubleValue9);
            float floatValue9 = (float)Math.sin(doubleValue9);
            float floatValue10 = (float)Math.cos(doubleValue10);
            float floatValue11 = (float)Math.sin(doubleValue10);
            this.addVertex(bufferBuilder, floatValue3, floatValue4, floatValue5, value3);
            this.addVertex(bufferBuilder, floatValue3, floatValue4, floatValue5, value3);
            this.addVertex(bufferBuilder, floatValue3 + floatValue10 * floatValue7, floatValue4, floatValue5 + floatValue11 * floatValue7, value4);
            this.addVertex(bufferBuilder, floatValue3 + floatValue8 * floatValue7, floatValue4, floatValue5 + floatValue9 * floatValue7, value4);
            this.addVertex(bufferBuilder, floatValue3 + floatValue8 * floatValue7, floatValue4, floatValue5 + floatValue9 * floatValue7, value4);
            this.addVertex(bufferBuilder, floatValue3 + floatValue10 * floatValue7, floatValue4, floatValue5 + floatValue11 * floatValue7, value4);
            this.addVertex(bufferBuilder, floatValue3 + floatValue10 * floatValue6, floatValue4, floatValue5 + floatValue11 * floatValue6, value5);
            this.addVertex(bufferBuilder, floatValue3 + floatValue8 * floatValue6, floatValue4, floatValue5 + floatValue9 * floatValue6, value5);
         }
      });
      this.m_9(matrix4f, vec3, this.m_1(vec32.x, doubleValue8, vec32.z, doubleValue7), ColorUtil.withAlpha(value2, (int)(230.0 * doubleValue3)), floatValue2);
      float floatValue12 = (float)(System.currentTimeMillis() % 1600L) / 1600.0F;
      int value6 = (int)(140.0F * (1.0F - floatValue12) * (1.0F - floatValue12) * doubleValue3);
      if (value6 > 3) {
         this.m_9(matrix4f, vec3, this.m_1(vec32.x, doubleValue8, vec32.z, doubleValue7 * (0.15 + 0.85 * floatValue12)), ColorUtil.withAlpha(value2, value6), Math.max(1.0F, floatValue2 - 0.5F));
      }
   }

   private double m_4(Vec3 vec3, Direction direction, boolean flag) {
      if (direction == Direction.UP && !flag) {
         return vec3.y;
      }

      Vec3 vec32 = vec3.add(0.0, 0.05, 0.0);
      BlockHitResult blockHitResult = MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec32, vec32.subtract(0.0, 6.0, 0.0), Block.COLLIDER, Fluid.NONE, MinecraftAccess.MINECRAFT.player));
      return blockHitResult.getType() == Type.BLOCK ? blockHitResult.getLocation().y : vec3.y;
   }

   private boolean m_4(Vec3 vec3, double doubleValue2, boolean flag, Entity entity) {
      AABB aABB = flag
         ? AABB.ofSize(vec3, 0.25, 0.25, 0.25).inflate(doubleValue2, 2.0, doubleValue2)
         : new AABB(vec3.x - doubleValue2, vec3.y - 0.5, vec3.z - doubleValue2, vec3.x + doubleValue2, vec3.y + 1.0, vec3.z + doubleValue2);

      for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
         if (player == MinecraftAccess.MINECRAFT.player || Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())) {
            if (player == entity) {
               return true;
            }

            if (player.getBoundingBox().intersects(aABB)) {
               double doubleValue3 = player.getX() - vec3.x;
               double doubleValue4 = player.getZ() - vec3.z;
               double doubleValue5 = flag ? player.position().distanceToSqr(vec3) : doubleValue3 * doubleValue3 + doubleValue4 * doubleValue4;
               if (doubleValue5 < doubleValue2 * doubleValue2) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private List<Vec3> m_1(double doubleValue2, double doubleValue3, double doubleValue4, double doubleValue5) {
      ArrayList list = new ArrayList(73);

      for (int i = 0; i <= 72; i++) {
         double doubleValue6 = (Math.PI * 2) * i / 72.0;
         list.add(new Vec3(doubleValue2 + Math.cos(doubleValue6) * doubleValue5, doubleValue3, doubleValue4 + Math.sin(doubleValue6) * doubleValue5));
      }

      return list;
   }

   private void m_9(Matrix4f matrix4f, Vec3 vec3, List<Vec3> vec3List, int value, float floatValue2) {
      if ((value >>> 24 & 0xFF) != 0) {
         int[] intArray = new int[vec3List.size()];
         Arrays.fill(intArray, value);
         Line3DRenderer.m_1(matrix4f, vec3List, intArray, vec3, floatValue2);
      }
   }

   private void addVertex(BufferBuilder bufferBuilder, float floatValue2, float floatValue3, float floatValue4, int value) {
      bufferBuilder.addVertex(floatValue2, floatValue3, floatValue4).setColor(value);
   }

   private TrackingItemStackRenderState m_6(Render2DEvent event, long longValue3, ItemStack itemStack) {
      Trajectories.StateHolder2 c7 = this.longMap2.computeIfAbsent(longValue3, longObj -> new Trajectories.StateHolder2());
      if (!ItemStack.matches(c7.itemStack, itemStack)) {
         c7.itemStack = itemStack.copy();
         MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(c7.trackingItemStackRenderState, itemStack, ItemDisplayContext.GUI, MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, itemStack.hashCode());
      }

      event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(c7.trackingItemStackRenderState.getModelIdentity());
      return c7.trackingItemStackRenderState;
   }

   private static int scaleAlpha(int value, double doubleValue2) {
      return ColorUtil.withAlpha(value, (int)Mth.clamp((value >>> 24 & 0xFF) * doubleValue2, 0.0, 255.0));
   }

   private static double approach(double doubleValue2, double doubleValue3, double doubleValue4) {
      return doubleValue2 < doubleValue3 ? Math.min(doubleValue3, doubleValue2 + doubleValue4) : Math.max(doubleValue3, doubleValue2 - doubleValue4);
   }

   private static double smoothStep(double doubleValue2) {
      doubleValue2 = Mth.clamp(doubleValue2, 0.0, 1.0);
      return doubleValue2 * doubleValue2 * (3.0 - 2.0 * doubleValue2);
   }

   private static long packPos(int value, int value2, int value3) {
      return (long)value << 40 | (value2 & 4294967295L) << 4 | value3;
   }

   static {
      m_5_void("v7RHpb");
   }

   public static void m_5_void(String text2) {
      text = text2;
   }

   public static String m_9_String() {
      return text;
   }

   private enum ModeKind {
      NONE,
      MARKER,
      SPLASH,
      LINGERING;

      private static String a(byte[] byteArray) {
         int value = 0;
         int value2;
         char[] charArray = new char[value2 = byteArray.length];

         for (int i = 0; i < value2; i++) {
            int value3;
            if ((value3 = 255 & byteArray[i]) < 192) {
               charArray[value++] = (char)value3;
            } else if (value3 < 224) {
               char ch = (char)((char)(value3 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value++] = ch;
            } else if (i < value2 - 2) {
               char ch2 = (char)((char)(value3 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value++] = ch2;
            }
         }

         return new String(charArray, 0, value);
      }
   }

   private record DataRecord(Entity f_8v, Vec3 f_6x, Direction f_0i) {
      public Entity m_8v() {
         return this.f_8v;
      }

      public Vec3 m_6x_Vec3() {
         return this.f_6x;
      }

      public Direction m_0i() {
         return this.f_0i;
      }
   }

   private record DataRecord2(long f_78, Vec3 f_74, float f_8_k, ItemStack f_5w, float f_8__) {
      public long m_78() {
         return this.f_78;
      }

      public Vec3 m_74() {
         return this.f_74;
      }

      public float m_8_k() {
         return this.f_8_k;
      }

      public ItemStack m_5w() {
         return this.f_5w;
      }

      public float m_8__() {
         return this.f_8__;
      }
   }

   private record DataRecord3(List<Vec3> f_97, Vec3 f_9_l, Direction f_9_y, Entity f_6u, int f_7_m) {
      public List<Vec3> m_97() {
         return this.f_97;
      }

      public Vec3 m_9_l_Vec3() {
         return this.f_9_l;
      }

      public Direction m_9_y() {
         return this.f_9_y;
      }

      public Entity m_6u_Entity() {
         return this.f_6u;
      }

      public int m_7_m() {
         return this.f_7_m;
      }
   }

   private record DataRecord4(List<Vec3> f_3f, Vec3 f_8i, float f_4__, int f_0u, ItemStack f_4p, boolean f_9_t) {
      public List<Vec3> m_3f() {
         return this.f_3f;
      }

      public Vec3 m_8i() {
         return this.f_8i;
      }

      public float m_4___float() {
         return this.f_4__;
      }

      public int m_0u() {
         return this.f_0u;
      }

      public ItemStack m_4p_ItemStack() {
         return this.f_4p;
      }

      public boolean m_9_t_boolean() {
         return this.f_9_t;
      }
   }

   private record DataRecord5(List<Vec3> f_4l, Trajectories.DataRecord3 f_1_l, Trajectories.ModeKind3 f_2_l, double f_8m, double f_89, boolean f_2o, Trajectories.ModeKind f_2m, ItemStack f_6_w, float f_71, double f_8n) {
      public List<Vec3> m_4l() {
         return this.f_4l;
      }

      public Trajectories.DataRecord3 m_1_l() {
         return this.f_1_l;
      }

      public Trajectories.ModeKind3 m_2_l_C9() {
         return this.f_2_l;
      }

      public double m_8m() {
         return this.f_8m;
      }

      public double m_89_double() {
         return this.f_89;
      }

      public boolean m_2o() {
         return this.f_2o;
      }

      public Trajectories.ModeKind m_2m() {
         return this.f_2m;
      }

      public ItemStack m_6_w_ItemStack() {
         return this.f_6_w;
      }

      public float m_71_float() {
         return this.f_71;
      }

      public double m_8n() {
         return this.f_8n;
      }
   }

   private static final class StateHolder {
      private Vec3[] vec3Array;
      private Trajectories.DataRecord5 c5;
      private long longValue;
      private double doubleValue;
      private Vec3 vec3;
      private Vec3 vec32;
      private double doubleValue2;
      private double doubleValue3;
      private double doubleValue4;
      private boolean flag;

      private StateHolder() {
         this.doubleValue3 = -1.0;
      }
   }

   private static final class StateHolder2 {
      private ItemStack itemStack;
      private final TrackingItemStackRenderState trackingItemStackRenderState;

      private StateHolder2() {
         this.itemStack = ItemStack.EMPTY;
         this.trackingItemStackRenderState = new TrackingItemStackRenderState();
      }
   }

   private static final class StateHolder3 {
      private int value;
      private Vec3 vec3;
      private Vec3 vec32;
      private float floatValue;
      private float floatValue2;

      private StateHolder3(int value2, Vec3 vec33, float floatValue3) {
         this.value = value2;
         this.vec3 = vec33;
         this.vec32 = vec33;
         this.floatValue = floatValue3;
         this.floatValue2 = floatValue3;
      }
   }

   private record DataRecord6(Trajectories.DataRecord3 f_0_v, int f_7_q) {
      public Trajectories.DataRecord3 m_0_v() {
         return this.f_0_v;
      }

      public int m_7_q() {
         return this.f_7_q;
      }
   }

   private enum ModeKind2 {
      THROWABLE,
      ARROW,
      HURTING;

      private static String a(byte[] byteArray) {
         int value = 0;
         int value2;
         char[] charArray = new char[value2 = byteArray.length];

         for (int i = 0; i < value2; i++) {
            int value3;
            if ((value3 = 255 & byteArray[i]) < 192) {
               charArray[value++] = (char)value3;
            } else if (value3 < 224) {
               char ch = (char)((char)(value3 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value++] = ch;
            } else if (i < value2 - 2) {
               char ch2 = (char)((char)(value3 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value++] = ch2;
            }
         }

         return new String(charArray, 0, value);
      }
   }

   private record DataRecord7(double f_1_z, Trajectories.DataRecord8 f_4x, float f_9a, boolean f_09) {
      public double m_1_z_double() {
         return this.f_1_z;
      }

      public Trajectories.DataRecord8 m_4x() {
         return this.f_4x;
      }

      public float m_9a_float() {
         return this.f_9a;
      }

      public boolean m_09() {
         return this.f_09;
      }
   }

   private record DataRecord8(Trajectories.ModeKind2 f_9z, double f_6e, double f_6_z, double f_6_m, double f_6_u) {
      public static Trajectories.DataRecord8 m_0_C8_p(double doubleValue2) {
         return new Trajectories.DataRecord8(Trajectories.ModeKind2.THROWABLE, doubleValue2, 0.99, 0.8, 0.0);
      }

      public static Trajectories.DataRecord8 m_5_C8_p(double doubleValue2, double doubleValue3) {
         return new Trajectories.DataRecord8(Trajectories.ModeKind2.ARROW, doubleValue2, 0.99, doubleValue3, 0.0);
      }

      public Trajectories.ModeKind2 m_9z_C8_e() {
         return this.f_9z;
      }

      public double m_6e_double() {
         return this.f_6e;
      }

      public double m_6_z() {
         return this.f_6_z;
      }

      public double m_6_m() {
         return this.f_6_m;
      }

      public double m_6_u() {
         return this.f_6_u;
      }
   }

   private enum ModeKind3 {
      SOLID,
      CAMERA,
      RIBBON;

      private static String a(byte[] byteArray) {
         int value = 0;
         int value2;
         char[] charArray = new char[value2 = byteArray.length];

         for (int i = 0; i < value2; i++) {
            int value3;
            if ((value3 = 255 & byteArray[i]) < 192) {
               charArray[value++] = (char)value3;
            } else if (value3 < 224) {
               char ch = (char)((char)(value3 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value++] = ch;
            } else if (i < value2 - 2) {
               char ch2 = (char)((char)(value3 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value++] = ch2;
            }
         }

         return new String(charArray, 0, value);
      }
   }
}
