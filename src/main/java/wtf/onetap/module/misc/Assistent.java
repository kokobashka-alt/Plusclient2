package wtf.onetap.module.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.BlockStateChangeEvent;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.TargetArrow;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.WorldToScreen;

@ModuleInformation(name = "Assistent", description = "Хелпер для предметов под разные сервера", category = Category.MISC)
public class Assistent extends Module {
   private static final List<Block> BLOCK_LIST = List.of(
      Blocks.QUARTZ_BLOCK,
      Blocks.DEAD_TUBE_CORAL_BLOCK,
      Blocks.INFESTED_MOSSY_STONE_BRICKS,
      Blocks.PURPUR_PILLAR,
      Blocks.END_STONE_BRICKS,
      Blocks.NETHER_BRICKS,
      Blocks.GILDED_BLACKSTONE,
      Blocks.PRISMARINE_BRICKS,
      Blocks.ICE,
      Blocks.NETHER_WART_BLOCK,
      Blocks.RESPAWN_ANCHOR,
      Blocks.NETHERITE_BLOCK,
      Blocks.STAINED_GLASS.white(),
      Blocks.CONCRETE.black(),
      Blocks.POLISHED_BASALT
   );
   private static final List<Assistent.ModeKind> C4_LIST = List.of(Assistent.ModeKind.values());
   private final ModeSetting server = new ModeSetting("Сервер", "FunTime", "FunTime", "HolyWorld", "ReallyWorld", "LonyGrief").build();
   private final BooleanSetting itemTimers = new BooleanSetting("Таймеры предметов", true).visibleWhen(this::isFunTimeOrHolyWorld).build();
   private final BooleanSetting radius = new BooleanSetting("Радиус", true).visibleWhen(this::isFunTimeOrHolyWorld).build();
   private final BooleanSetting radiusHighlight = new BooleanSetting("Подсветка в радиусе", true).visibleWhen(this::isFunTimeOrHolyWorld).build();
   private final BooleanSetting safeguard = new BooleanSetting("Предохранитель", false).visibleWhen(this::isFunTimeOrHolyWorld).build();
   private final KeybindSetting explosiveTrap = new KeybindSetting("Взрывная трапка", -1).visibleWhen(() -> ((String)this.server.getValue()).equals("HolyWorld")).build();
   private final KeybindSetting trap = new KeybindSetting("Трапка", -1).visibleWhen(() -> ((String)this.server.getValue()).equals("HolyWorld")).build();
   private final KeybindSetting stun = new KeybindSetting("Стан", -1).visibleWhen(() -> ((String)this.server.getValue()).equals("HolyWorld")).build();
   private final KeybindSetting fireCharge = new KeybindSetting("Огненный заряд", -1).visibleWhen(() -> ((String)this.server.getValue()).equals("HolyWorld")).build();
   private final KeybindSetting snowball = new KeybindSetting("Ком снега", -1).visibleWhen(() -> ((String)this.server.getValue()).equals("HolyWorld")).build();
   private final KeybindSetting freezeSnowball = new KeybindSetting("Снежок заморозки", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting holyAura = new KeybindSetting("Божья аура", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting trap2 = new KeybindSetting("Трапка", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting layer = new KeybindSetting("Пласт", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting visibleDust = new KeybindSetting("Явная пыль", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting fireTornado = new KeybindSetting("Огненный смерч", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting disorientation = new KeybindSetting("Дезориентация", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting firecracker = new KeybindSetting("Хлопушка", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting holyWater = new KeybindSetting("Святая вода", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting sleepingPotion = new KeybindSetting("Снотворное", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting wrathPotion = new KeybindSetting("Зелье гнева", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting paladinPotion = new KeybindSetting("Зелье паладина", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting assassinPotion = new KeybindSetting("Зелье ассасина", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting radiationPotion = new KeybindSetting("Зелье радиации", -1).visibleWhen(this::isFunTime).build();
   private final KeybindSetting trap3 = new KeybindSetting("Трапка", -1).visibleWhen(() -> ((String)this.server.getValue()).equals("LonyGrief")).build();
   private final KeybindSetting leaver = new KeybindSetting("Ливалка", -1).visibleWhen(() -> ((String)this.server.getValue()).equals("LonyGrief")).build();
   private final BooleanSetting pvpPrefixes = new BooleanSetting("Префиксы /pvp", true).visibleWhen(() -> ((String)this.server.getValue()).equals("LonyGrief")).build();
   private final BooleanSetting autoGps = new BooleanSetting("Авто гпс", true).visibleWhen(() -> ((String)this.server.getValue()).equals("LonyGrief")).build();
   private final BooleanSetting autoPvp = new BooleanSetting("Авто пвп", true).visibleWhen(() -> ((String)this.server.getValue()).equals("LonyGrief")).build();
   private final MultiModeSetting multiModeSetting = new MultiModeSetting(
         "Авто пвп если донат", "Игрок", "Игрок", "Легенда", "Феникс", "Император", "Правитель", "Повелитель", "Д.Админ", "Оверлорд", "Этернити"
      )
      .visibleWhen(() -> ((String)this.server.getValue()).equals("LonyGrief"))
      .build();
   private final KeybindSetting antiFlight = new KeybindSetting("Анти Полет", -1).visibleWhen(() -> ((String)this.server.getValue()).equals("ReallyWorld")).build();
   private final KeybindSetting darkPulse = new KeybindSetting("Тёмный пульс", -1).visibleWhen(() -> ((String)this.server.getValue()).equals("ReallyWorld")).build();
   private final KeybindSetting trap4 = new KeybindSetting("Ловушка", -1).visibleWhen(() -> ((String)this.server.getValue()).equals("ReallyWorld")).build();
   private final KeybindSetting expScroll = new KeybindSetting("Свиток опыта", -1).visibleWhen(() -> ((String)this.server.getValue()).equals("ReallyWorld")).build();
   private final BooleanSetting closeMenu = new BooleanSetting("Закрывать меню", true).visibleWhen(() -> ((String)this.server.getValue()).equals("ReallyWorld")).build();
   private final BooleanSetting autoFix = new BooleanSetting("AutoFix", true).visibleWhen(() -> ((String)this.server.getValue()).equals("ReallyWorld")).build();
   private Item item;
   private String text;
   private Item item2;
   private Item item3;
   private String text2;
   private int value;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private int value2;
   private int value3 = -1;
   private int value4;
   private int value5;
   private boolean flag5;
   private final List<Assistent.DataRecord2> c8List = new ArrayList<>();
   private final List<Assistent.StateHolder2> c3List = new ArrayList<>();
   private final HashMap<Identifier, Long> identifierMap = new HashMap<>();
   private final Set<BlockPos> blockPosSet = new LinkedHashSet<>();
   private int value6;
   private static String text3;

   public Assistent() {
      this.multiModeSetting.setValue(new LinkedHashSet<>(this.multiModeSetting.getOptions()));
   }

   private boolean isFunTime() {
      return ((String)this.server.getValue()).equals("FunTime");
   }

   private boolean isReallyWorld() {
      return ((String)this.server.getValue()).equals("ReallyWorld");
   }

   private boolean isFunTimeOrHolyWorld() {
      return this.isFunTime() || ((String)this.server.getValue()).equals("HolyWorld");
   }

   private Assistent.StateHolder m_8_C0(Item item4) {
      int value7 = ColorUtil.primaryColor();
      if (item4 == Items.NETHER_STAR) {
         return new Assistent.StateHolder(15.0F, value7, true);
      } else if (item4 == Items.PRISMARINE_SHARD) {
         return new Assistent.StateHolder(3.0F, value7, false);
      } else if (item4 == Items.POPPED_CHORUS_FRUIT) {
         return new Assistent.StateHolder(3.0F, value7, true);
      } else if (item4 == Items.FIRE_CHARGE) {
         return new Assistent.StateHolder(3.0F, value7, false);
      } else if (item4 == Items.SNOWBALL) {
         return new Assistent.StateHolder(0.0F, value7, false);
      } else if (item4 == Items.WIND_CHARGE) {
         return new Assistent.StateHolder(0.0F, value7, false);
      } else if (item4 == Items.NETHERITE_SCRAP) {
         return new Assistent.StateHolder(2.0F, -7650029, true);
      } else if (item4 == Items.DRIED_KELP) {
         return new Assistent.StateHolder(0.0F, value7, false);
      } else if (item4 == Items.SUGAR) {
         return new Assistent.StateHolder(10.0F, value7, false);
      } else if (item4 == Items.ENDER_EYE) {
         return new Assistent.StateHolder(10.0F, value7, false);
      } else if (item4 == Items.PHANTOM_MEMBRANE) {
         return new Assistent.StateHolder(0.0F, value7, false);
      } else if (item4 == Items.HEART_OF_THE_SEA) {
         return new Assistent.StateHolder(3.0F, value7, true);
      } else {
         return item4 == Items.FIREWORK_STAR ? new Assistent.StateHolder(0.0F, value7, false) : new Assistent.StateHolder(0.0F, value7, false);
      }
   }

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (!ChatInputHelper.isTextInputOpen()) {
         if (MinecraftAccess.MINECRAFT.gui.screen() == null) {
            if (((String)this.server.getValue()).equals("LonyGrief")) {
               if (event.getKey() == (Integer)this.trap3.getValue()) {
                  this.flag3 = true;
                  return;
               }

               if (event.getKey() == (Integer)this.leaver.getValue()) {
                  this.flag4 = true;
                  return;
               }
            }

            if (this.isReallyWorld()) {
               if (event.getAction() == 1) {
                  if (event.getKey() == (Integer)this.antiFlight.getValue()) {
                     this.m_7_void("анти");
                  } else if (event.getKey() == (Integer)this.darkPulse.getValue()) {
                     this.m_7_void("пульс");
                  } else if (event.getKey() == (Integer)this.trap4.getValue()) {
                     this.item = Items.HEART_OF_THE_SEA;
                     this.text = null;
                     this.item2 = Items.HEART_OF_THE_SEA;
                  } else {
                     if (event.getKey() == (Integer)this.expScroll.getValue()) {
                        this.item = Items.FLOWER_BANNER_PATTERN;
                        this.text = "свиток опыта";
                        this.item2 = Items.FLOWER_BANNER_PATTERN;
                     }
                  }
               }
            } else {
               Item item4 = this.m_8_Item(event.getKey());
               if (item4 != null) {
                  if (event.getAction() == 0) {
                     if (this.item2 == item4) {
                        this.item2 = null;
                        this.text = null;
                     }
                  } else if (event.getAction() != 2) {
                     this.item = item4;
                     this.text = null;
                     this.item2 = item4;
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundLoginPacket || event.getPacket() instanceof ClientboundRespawnPacket) {
         this.identifierMap.clear();
      } else if (MinecraftAccess.MINECRAFT.player != null) {
         if (event.getPacket() instanceof ClientboundCooldownPacket clientboundCooldownPacket) {
            if (clientboundCooldownPacket.duration() > 0) {
               this.identifierMap.put(clientboundCooldownPacket.cooldownGroup(), System.currentTimeMillis() + clientboundCooldownPacket.duration() * 50L);
            } else {
               this.identifierMap.remove(clientboundCooldownPacket.cooldownGroup());
            }
         } else if (this.isReallyWorld()
            && (Boolean)this.closeMenu.getValue()
            && event.getPacket() instanceof ClientboundOpenScreenPacket clientboundOpenScreenPacket
            && clientboundOpenScreenPacket.getTitle().getString().contains("ꈁꀀꈂꌁꈂꀁ§0ꈃꄀ")
            && MinecraftAccess.MINECRAFT.player.tickCount < 100) {
            MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundContainerClosePacket(clientboundOpenScreenPacket.getContainerId()));
            event.cancel();
         } else {
            if (this.isReallyWorld() && (Boolean)this.autoFix.getValue()) {
               String text4 = getRawChatText(event.getPacket());
               if (text4 != null) {
                  String text5 = text4.replaceAll("§x(§[0-9a-fA-F]){6}", "")
                     .replaceAll("§.", "")
                     .replace(' ', ' ')
                     .replace(' ', ' ')
                     .trim()
                     .toLowerCase(Locale.ROOT)
                     .replace('ё', 'е');
                  if (text5.contains("антирелог") && text5.contains("закончился") && MinecraftAccess.MINECRAFT.getConnection() != null) {
                     MinecraftAccess.MINECRAFT.getConnection().sendCommand("fix all");
                  }
               }
            }

            if (((String)this.server.getValue()).equals("LonyGrief") && event.getPacket() instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
               this.m_5(event, clientboundSystemChatPacket);
            }
         }
      }
   }

   @EventTarget
   private void onBlockStateChange(BlockStateChangeEvent event) {
      if (this.isFunTimeOrHolyWorld() && (Boolean)this.itemTimers.getValue()) {
         if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
            Block block = event.getNewState().getBlock();
            Block block2 = event.getOldState().getBlock();
            if (BLOCK_LIST.contains(block) && !BLOCK_LIST.contains(block2)) {
               this.blockPosSet.add(event.getPos());
               this.value6 = 2;
            }
         }
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (this.isFunTimeOrHolyWorld() && (Boolean)this.radius.getValue()) {
            Item item4 = this.m_2_Item();
            if (item4 != null) {
               Assistent.StateHolder c0 = this.m_8_C0(item4);
               if (!(c0.floatValue <= 0.0F)) {
                  Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
                  Vec3 vec3 = event.getVec3();
                  Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getPosition(event.getFloatValue());
                  if (c0.flag) {
                     if (item4 == Items.NETHERITE_SCRAP) {
                        this.m_0(matrix4f, vec3, vec32, c0.value, 1.0F);
                     } else {
                        this.m_0(matrix4f, vec3, vec32, c0.floatValue, c0.value, 1.0F);
                     }
                  } else {
                     this.m_1(matrix4f, vec3, vec32, c0.floatValue, ColorUtil.withAlpha(c0.value, 140));
                  }

                  if ((Boolean)this.radiusHighlight.getValue()) {
                     float floatValue = event.getFloatValue();
                     double doubleValue = c0.floatValue * c0.floatValue;

                     for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
                        if (player != MinecraftAccess.MINECRAFT.player && player.isAlive()) {
                           double doubleValue2 = Mth.lerp(floatValue, player.xo, player.getX());
                           double doubleValue3 = Mth.lerp(floatValue, player.yo, player.getY());
                           double doubleValue4 = Mth.lerp(floatValue, player.zo, player.getZ());
                           double doubleValue5 = doubleValue2 - vec32.x;
                           double doubleValue6 = doubleValue3 - vec32.y;
                           double doubleValue7 = doubleValue4 - vec32.z;
                           if (!(doubleValue5 * doubleValue5 + doubleValue6 * doubleValue6 + doubleValue7 * doubleValue7 > doubleValue)) {
                              double doubleValue8 = doubleValue2 - vec3.x;
                              double doubleValue9 = doubleValue3 - vec3.y;
                              double doubleValue10 = doubleValue4 - vec3.z;
                              AABB aABB = player.getBoundingBox();
                              double doubleValue11 = doubleValue8 + aABB.minX - player.getX();
                              double doubleValue12 = doubleValue9 + aABB.minY - player.getY();
                              double doubleValue13 = doubleValue10 + aABB.minZ - player.getZ();
                              double doubleValue14 = doubleValue8 + aABB.maxX - player.getX();
                              double doubleValue15 = doubleValue9 + aABB.maxY - player.getY();
                              double doubleValue16 = doubleValue10 + aABB.maxZ - player.getZ();
                              int value7 = ColorUtil.withAlpha(c0.value, 63);
                              int value8 = ColorUtil.withAlpha(c0.value, 204);
                              Box3DRenderer.drawBox(matrix4f, (float)doubleValue11, (float)doubleValue12, (float)doubleValue13, (float)doubleValue14, (float)doubleValue15, (float)doubleValue16, value7);
                              this.m_9(matrix4f, vec3, doubleValue11 + vec3.x, doubleValue12 + vec3.y, doubleValue13 + vec3.z, doubleValue14 + vec3.x, doubleValue15 + vec3.y, doubleValue16 + vec3.z, value8);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         long longValue = System.currentTimeMillis();
         if (this.isFunTimeOrHolyWorld() && (Boolean)this.itemTimers.getValue() && !this.c8List.isEmpty()) {
            int value7 = 0;
            float floatValue = 12.0F;
            float floatValue2 = 17.0F;
            float floatValue3 = 20.0F;
            float floatValue4 = 2.5F;
            float floatValue5 = 3.0F;
            float floatValue6 = 7.0F;
            float floatValue7 = Draw2D.getFontHeight(Fonts.SUPPLIER15, floatValue6);
            int value8 = ColorUtil.primaryColor();

            for (Assistent.DataRecord2 c8 : this.c8List) {
               long longValue2 = c8.f_8_m - longValue;
               if (longValue2 > -300L) {
                  float floatValue8;
                  if (longValue2 <= 0L) {
                     floatValue8 = 1.0F + (float)longValue2 / 300.0F;
                  } else {
                     long longValue3 = longValue - c8.f_8_g;
                     floatValue8 = Math.min(1.0F, (float)longValue3 / 300.0F);
                  }

                  if (!(floatValue8 < 0.01F)) {
                     Vec3 vec3 = this.m_6(c8);
                     wtf.onetap.util.WorldToScreen.DataRecord c9 = wtf.onetap.util.WorldToScreen.project(vec3);
                     if (c9 != null) {
                        String text4 = String.format(Locale.ROOT, "%.1fs", Math.max(0.0, longValue2 / 1000.0));
                        float floatValue9 = Draw2D.getTextWidth(text4, Fonts.SUPPLIER15, floatValue6);
                        float floatValue10 = floatValue4 + floatValue2 + floatValue5 + floatValue9 + floatValue4;
                        float floatValue11 = Math.min(1.0F, floatValue8 * 1.1F);
                        float floatValue12 = c8.f_86 > 0L ? Mth.clamp((float)longValue2 / (float)c8.f_86, 0.0F, 1.0F) : 0.0F;
                        Assistent.StateHolder2 c3 = this.m_5_C3(value7++);
                        c3.floatValue = floatValue8;
                        c3.floatValue2 = floatValue11;
                        c3.floatValue5 = floatValue10 * floatValue11;
                        c3.floatValue6 = floatValue3 * floatValue11;
                        c3.floatValue3 = c9.m_2r_float() - c3.floatValue5 / 2.0F;
                        c3.floatValue4 = c9.m_2___float() - c3.floatValue6 / 2.0F;
                        float floatValue13 = c3.floatValue3 + floatValue4 * floatValue11;
                        float floatValue14 = c3.floatValue4 + (c3.floatValue6 - floatValue2 * floatValue11) / 2.0F;
                        c3.floatValue7 = floatValue13;
                        c3.floatValue8 = floatValue14;
                        c3.floatValue9 = floatValue2 * floatValue11;
                        c3.floatValue10 = floatValue13 + floatValue2 * floatValue11 / 2.0F;
                        c3.floatValue11 = floatValue14 + floatValue2 * floatValue11 / 2.0F;
                        c3.floatValue12 = c3.floatValue10 - floatValue * floatValue11 / 2.0F;
                        c3.floatValue13 = c3.floatValue11 - floatValue * floatValue11 / 2.0F;
                        c3.floatValue14 = floatValue * floatValue11;
                        c3.floatValue15 = floatValue2 * floatValue11 / 2.0F;
                        c3.floatValue16 = floatValue12;
                        c3.text = text4;
                        c3.floatValue17 = c3.floatValue3 + floatValue4 * floatValue11 + floatValue2 * floatValue11 + floatValue5 * floatValue11;
                        c3.floatValue18 = c3.floatValue4 + (c3.floatValue6 - floatValue7 * floatValue11) / 2.0F;
                        c3.floatValue19 = floatValue6 * floatValue11;
                        c3.value = ColorUtil.rgba(0, 0, 0, (int)(160.0F * floatValue8));
                        c3.value2 = ColorUtil.withAlpha(value8, (int)(255.0F * floatValue8));
                        c3.value3 = ColorUtil.rgba(255, 255, 255, (int)(38.0F * floatValue8));
                        c3.value4 = ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue8));
                        if (c3.itemStack == null || c3.itemStack.getItem() != c8.f_7_x) {
                           c3.itemStack = new ItemStack(c8.f_7_x);
                        }
                     }
                  }
               }
            }

            for (int i = 0; i < value7; i++) {
               Assistent.StateHolder2 c32 = this.c3List.get(i);
               Draw2D.m_7(
                  c32.floatValue3,
                  c32.floatValue4,
                  c32.floatValue5,
                  c32.floatValue6,
                  6.0F,
                  6.0F,
                  6.0F,
                  6.0F,
                  0.0F,
                  3.0F * c32.floatValue,
                  ColorUtil.rgba(155, 155, 155, (int)(255.0F * c32.floatValue))
               );
               Draw2D.roundedRect(c32.floatValue3, c32.floatValue4, c32.floatValue5, c32.floatValue6, 6.0F, c32.value);
               Draw2D.m_5(c32.floatValue10, c32.floatValue11, c32.floatValue15, 1.0F * c32.floatValue2, 1.0F, (float) (-Math.PI / 2), 0.0F, c32.value3);
               Draw2D.m_5(c32.floatValue10, c32.floatValue11, c32.floatValue15, 1.0F * c32.floatValue2, c32.floatValue16, (float) (-Math.PI / 2), 0.0F, c32.value2);
            }

            for (int i2 = 0; i2 < value7; i2++) {
               this.m_8(event, this.c3List.get(i2));
            }

            for (int i3 = 0; i3 < value7; i3++) {
               Assistent.StateHolder2 c33 = this.c3List.get(i3);
               Draw2D.drawStyledText(c33.text, Fonts.SUPPLIER15, c33.floatValue17, c33.floatValue18, c33.value4, c33.floatValue19);
            }
         }
      }
   }

   private Assistent.StateHolder2 m_5_C3(int value7) {
      while (this.c3List.size() <= value7) {
         this.c3List.add(new Assistent.StateHolder2());
      }

      return this.c3List.get(value7);
   }

   private void m_8(Render2DEvent event, Assistent.StateHolder2 c3) {
      TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
      MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, c3.itemStack, ItemDisplayContext.GUI, MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, c3.itemStack.hashCode());
      event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
      Render2D.drawItem(trackingItemStackRenderState, c3.floatValue12, c3.floatValue13, c3.floatValue14, ColorUtil.rgba(255, 255, 255, (int)(255.0F * c3.floatValue)));
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if ((this.flag || this.item3 != null || this.value2 > 0 || this.value4 != 0) && event.getForward() > 0.0F) {
         event.setForward(0.0F);
      }

      if (this.value4 == 2) {
         event.setSneaking(true);
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         long longValue = System.currentTimeMillis();
         this.c8List.removeIf(c8 -> c8.f_8_m + 300L <= longValue);
         this.m_2_void();
         this.m_4_void(longValue);
         if (((String)this.server.getValue()).equals("LonyGrief")) {
            if (this.flag3) {
               InventoryUtil.useItem(Items.CRYING_OBSIDIAN);
               this.flag3 = false;
            }

            if (this.flag4) {
               this.m_1_void();
               this.flag4 = false;
            }
         }

         if (this.item3 != null) {
            this.releaseForward();
            if (this.value > 0) {
               this.value--;
            }

            if (this.value <= 0) {
               this.m_4(this.item3, this.text2);
               this.item3 = null;
               this.text2 = null;
               this.flag = true;
            }
         }

         if (this.flag) {
            if (MinecraftAccess.MINECRAFT.player.isUsingItem()) {
               this.releaseForward();
            } else {
               this.flag = false;
               this.value2 = 2;
            }
         }

         if (this.value2 > 0) {
            this.releaseForward();
            this.value2--;
            if (this.value2 == 0) {
               this.restoreForward();
            }
         }

         if (this.item != null) {
            Item item4 = this.item;
            String text4 = this.text;
            this.item = null;
            this.text = null;
            if (this.isFunTimeOrHolyWorld() && (Boolean)this.safeguard.getValue() && item4 != Items.SNOWBALL && item4 != Items.FIRE_CHARGE) {
               float floatValue = this.m_8_C0(item4).floatValue;
               if (floatValue > 0.0F) {
                  Vec3 vec3 = MinecraftAccess.MINECRAFT.player.position();
                  boolean flag6 = false;

                  for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
                     if (player != MinecraftAccess.MINECRAFT.player && player.isAlive() && player.position().distanceToSqr(vec3) <= floatValue * floatValue) {
                        flag6 = true;
                        break;
                     }
                  }

                  if (!flag6) {
                     return;
                  }
               }
            }

            if (this.isReallyWorld() && this.m_7(item4, text4, 0, 9) == -1 && this.m_7(item4, text4, 9, 36) != -1) {
               this.releaseForward();
               this.item3 = item4;
               this.text2 = text4;
               this.value = 2;
               this.value2 = 0;
            } else {
               this.m_4(item4, text4);
            }
         }
      }
   }

   public List<Assistent.DataRecord2> m_0_List() {
      long longValue = System.currentTimeMillis();
      this.c8List.removeIf(c8 -> c8.f_8_m + 300L <= longValue);
      return this.c8List;
   }

   public float m_1(Item item4, String text4) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return -1.0F;
      }

      String text5 = text4 == null ? null : text4.toLowerCase(Locale.ROOT);

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack.is(item4) && (text5 == null || itemStack.getHoverName().getString().toLowerCase(Locale.ROOT).contains(text5))) {
            Long longObj = this.identifierMap.get(MinecraftAccess.MINECRAFT.player.getCooldowns().getCooldownGroup(itemStack));
            if (longObj == null) {
               return -1.0F;
            }

            float floatValue = (float)(longObj - System.currentTimeMillis()) / 1000.0F;
            return floatValue > 0.0F ? floatValue : -1.0F;
         }
      }

      return -1.0F;
   }

   public List<Item> m_8_List() {
      if (((String)this.server.getValue()).equals("HolyWorld")) {
         return List.of(Items.NETHER_STAR, Items.PRISMARINE_SHARD, Items.POPPED_CHORUS_FRUIT, Items.FIRE_CHARGE, Items.SNOWBALL);
      } else if (this.isReallyWorld()) {
         return List.of(Items.FIREWORK_STAR, Items.HEART_OF_THE_SEA);
      } else {
         return ((String)this.server.getValue()).equals("LonyGrief")
            ? List.of(Items.CRYING_OBSIDIAN, Items.CLAY_BALL, Items.MAGMA_CREAM)
            : List.of(
               Items.SNOWBALL,
               Items.PHANTOM_MEMBRANE,
               Items.NETHERITE_SCRAP,
               Items.DRIED_KELP,
               Items.SUGAR,
               Items.FIRE_CHARGE,
               Items.ENDER_EYE,
               Items.SPLASH_POTION
            );
      }
   }

   public int m_1_int(Item item4) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return 0;
      }

      int value7 = 0;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack.is(item4)) {
            value7 += itemStack.getCount();
         }
      }

      return value7;
   }

   public int m_7_int(Item item4) {
      if (((String)this.server.getValue()).equals("HolyWorld")) {
         if (item4 == Items.PRISMARINE_SHARD) {
            return (Integer)this.explosiveTrap.getValue();
         }

         if (item4 == Items.POPPED_CHORUS_FRUIT) {
            return (Integer)this.trap.getValue();
         }

         if (item4 == Items.NETHER_STAR) {
            return (Integer)this.stun.getValue();
         }

         if (item4 == Items.FIRE_CHARGE) {
            return (Integer)this.fireCharge.getValue();
         }

         if (item4 == Items.SNOWBALL) {
            return (Integer)this.snowball.getValue();
         }
      } else if (this.isReallyWorld()) {
         if (item4 == Items.FIREWORK_STAR) {
            return (Integer)this.antiFlight.getValue();
         }

         if (item4 == Items.HEART_OF_THE_SEA) {
            return (Integer)this.trap4.getValue();
         }
      } else if (this.isFunTime()) {
         if (item4 == Items.SNOWBALL) {
            return (Integer)this.freezeSnowball.getValue();
         }

         if (item4 == Items.PHANTOM_MEMBRANE) {
            return (Integer)this.holyAura.getValue();
         }

         if (item4 == Items.NETHERITE_SCRAP) {
            return (Integer)this.trap2.getValue();
         }

         if (item4 == Items.DRIED_KELP) {
            return (Integer)this.layer.getValue();
         }

         if (item4 == Items.SUGAR) {
            return (Integer)this.visibleDust.getValue();
         }

         if (item4 == Items.FIRE_CHARGE) {
            return (Integer)this.fireTornado.getValue();
         }

         if (item4 == Items.ENDER_EYE) {
            return (Integer)this.disorientation.getValue();
         }

         if (item4 == Items.SPLASH_POTION) {
            return (Integer)this.firecracker.getValue();
         }
      }

      return -1;
   }

   private Item m_8_Item(int value7) {
      if (((String)this.server.getValue()).equals("HolyWorld")) {
         if (value7 == (Integer)this.explosiveTrap.getValue()) {
            return Items.PRISMARINE_SHARD;
         }

         if (value7 == (Integer)this.trap.getValue()) {
            return Items.POPPED_CHORUS_FRUIT;
         }

         if (value7 == (Integer)this.stun.getValue()) {
            return Items.NETHER_STAR;
         }

         if (value7 == (Integer)this.fireCharge.getValue()) {
            return Items.FIRE_CHARGE;
         }

         if (value7 == (Integer)this.snowball.getValue()) {
            return Items.SNOWBALL;
         }
      } else if (this.isReallyWorld()) {
         if (value7 == (Integer)this.antiFlight.getValue()) {
            return Items.FIREWORK_STAR;
         }

         if (value7 == (Integer)this.trap4.getValue()) {
            return Items.HEART_OF_THE_SEA;
         }
      } else if (this.isFunTime()) {
         if (value7 == (Integer)this.freezeSnowball.getValue()) {
            return Items.SNOWBALL;
         }

         if (value7 == (Integer)this.holyAura.getValue()) {
            return Items.PHANTOM_MEMBRANE;
         }

         if (value7 == (Integer)this.trap2.getValue()) {
            return Items.NETHERITE_SCRAP;
         }

         if (value7 == (Integer)this.layer.getValue()) {
            return Items.DRIED_KELP;
         }

         if (value7 == (Integer)this.visibleDust.getValue()) {
            return Items.SUGAR;
         }

         if (value7 == (Integer)this.fireTornado.getValue()) {
            return Items.FIRE_CHARGE;
         }

         if (value7 == (Integer)this.disorientation.getValue()) {
            return Items.ENDER_EYE;
         }

         if (value7 == (Integer)this.firecracker.getValue()) {
            return Items.SPLASH_POTION;
         }

         if (value7 == (Integer)this.holyWater.getValue()) {
            return Items.SPLASH_POTION;
         }

         if (value7 == (Integer)this.sleepingPotion.getValue()) {
            return Items.SPLASH_POTION;
         }

         if (value7 == (Integer)this.wrathPotion.getValue()) {
            return Items.SPLASH_POTION;
         }

         if (value7 == (Integer)this.paladinPotion.getValue()) {
            return Items.SPLASH_POTION;
         }

         if (value7 == (Integer)this.assassinPotion.getValue()) {
            return Items.SPLASH_POTION;
         }

         if (value7 == (Integer)this.radiationPotion.getValue()) {
            return Items.SPLASH_POTION;
         }
      }

      return null;
   }

   private void m_7_void(String text4) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (this.value4 == 0) {
            int value7 = this.m_7(Items.FIREWORK_STAR, text4, 0, 36);
            if (value7 != -1) {
               this.value3 = value7 < 9 ? 36 + value7 : value7;
               this.releaseForward();
               this.value4 = 1;
               this.value5 = 2;
            }
         }
      }
   }

   private void m_3_void() {
      this.flag5 = !MinecraftAccess.MINECRAFT.options.keyShift.isDown();
      MinecraftAccess.MINECRAFT.options.keyShift.setDown(true);
      if (MinecraftAccess.MINECRAFT.player.input != null) {
         Input input = MinecraftAccess.MINECRAFT.player.input.keyPresses;
         MinecraftAccess.MINECRAFT.player.input.keyPresses = new Input(input.forward(), input.backward(), input.left(), input.right(), input.jump(), true, input.sprint());
      }

      if (MinecraftAccess.MINECRAFT.player.connection != null) {
         MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundPlayerInputPacket(new Input(false, false, false, false, false, true, MinecraftAccess.MINECRAFT.player.isSprinting())));
      }
   }

   private void m_2_void() {
      if (this.value4 != 0) {
         this.releaseForward();
         if (this.value4 == 1) {
            if (this.value5 > 0) {
               this.value5--;
            }

            if (this.value5 <= 0) {
               if (this.value3 == -1) {
                  this.value4 = 0;
               } else {
                  InventoryUtil.clickSlot(0, this.value3, 40, ContainerInput.SWAP);
                  this.m_3_void();
                  this.value4 = 2;
                  this.value5 = 2;
               }
            }
         } else if (this.value4 == 2) {
            MinecraftAccess.MINECRAFT.options.keyShift.setDown(true);
            if (this.value5 > 0) {
               this.value5--;
            }

            if (this.value5 <= 0) {
               if (this.flag5) {
                  MinecraftAccess.MINECRAFT.options.keyShift.setDown(false);
                  this.flag5 = false;
               }

               this.value4 = 3;
               this.value5 = 3;
            }
         } else {
            if (this.value4 == 3) {
               if (this.value5 > 0) {
                  this.value5--;
               }

               if (this.value5 > 0) {
                  return;
               }

               if (this.value3 != -1) {
                  InventoryUtil.clickSlot(0, this.value3, 40, ContainerInput.SWAP);
                  this.value3 = -1;
               }

               if (MinecraftAccess.MINECRAFT.player.connection != null && MinecraftAccess.MINECRAFT.player.input != null) {
                  MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundPlayerInputPacket(MinecraftAccess.MINECRAFT.player.input.keyPresses));
               }

               this.value4 = 0;
               this.value5 = 0;
               this.value2 = 2;
            }
         }
      }
   }

   private String m_8_String(int value7) {
      if (value7 == (Integer)this.freezeSnowball.getValue()) {
         return "заморозка";
      } else if (value7 == (Integer)this.holyAura.getValue()) {
         return "божья аура";
      } else if (value7 == (Integer)this.trap2.getValue()) {
         return "трапка";
      } else if (value7 == (Integer)this.layer.getValue()) {
         return "пласт";
      } else if (value7 == (Integer)this.visibleDust.getValue()) {
         return "явная";
      } else if (value7 == (Integer)this.fireTornado.getValue()) {
         return "огненный смерч";
      } else if (value7 == (Integer)this.disorientation.getValue()) {
         return "дезориентация";
      } else if (value7 == (Integer)this.firecracker.getValue()) {
         return "хлопушка";
      } else if (value7 == (Integer)this.holyWater.getValue()) {
         return "святая";
      } else if (value7 == (Integer)this.sleepingPotion.getValue()) {
         return "снотворное";
      } else if (value7 == (Integer)this.wrathPotion.getValue()) {
         return "гнева";
      } else if (value7 == (Integer)this.paladinPotion.getValue()) {
         return "паладина";
      } else if (value7 == (Integer)this.assassinPotion.getValue()) {
         return "ассасина";
      } else {
         return value7 == this.radiationPotion.getValue() ? "радиации" : null;
      }
   }

   private void m_1_void() {
      int value7 = InventoryUtil.findHotbarSlot(Items.CLAY_BALL);
      int value8 = InventoryUtil.findItemSlot(Items.CLAY_BALL);
      if ((value7 == -1 || MinecraftAccess.MINECRAFT.player.getCooldowns().isOnCooldown(MinecraftAccess.MINECRAFT.player.getInventory().getItem(value7)))
         && (value8 == -1 || MinecraftAccess.MINECRAFT.player.getCooldowns().isOnCooldown(MinecraftAccess.MINECRAFT.player.getInventory().getItem(value8)))) {
         int value9 = InventoryUtil.findHotbarSlot(Items.MAGMA_CREAM);
         int value10 = InventoryUtil.findItemSlot(Items.MAGMA_CREAM);
         if (value9 != -1 && !MinecraftAccess.MINECRAFT.player.getCooldowns().isOnCooldown(MinecraftAccess.MINECRAFT.player.getInventory().getItem(value9))
            || value10 != -1 && !MinecraftAccess.MINECRAFT.player.getCooldowns().isOnCooldown(MinecraftAccess.MINECRAFT.player.getInventory().getItem(value10))) {
            InventoryUtil.useItem(Items.MAGMA_CREAM);
         }
      } else {
         InventoryUtil.useItem(Items.CLAY_BALL);
      }
   }

   private static String getRawChatText(Packet<?> packet) {
      if (packet instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
         return clientboundSystemChatPacket.content().getString();
      } else if (packet instanceof ClientboundDisguisedChatPacket clientboundDisguisedChatPacket) {
         return clientboundDisguisedChatPacket.message().getString();
      } else if (packet instanceof ClientboundPlayerChatPacket clientboundPlayerChatPacket) {
         return clientboundPlayerChatPacket.unsignedContent() != null ? clientboundPlayerChatPacket.unsignedContent().getString() : clientboundPlayerChatPacket.body().content();
      } else {
         return null;
      }
   }

   private void m_5(ReceivePacketEvent event, ClientboundSystemChatPacket clientboundSystemChatPacket) {
      String text4 = clientboundSystemChatPacket.content().getString();
      Matcher matcher = Pattern.compile("Его координаты:\\s*(-?\\d+)\\.\\s*(-?\\d+)\\.\\s*(-?\\d+)").matcher(text4);
      if (text4.contains("Вы успешно телепортировались рядом с игроком") && matcher.find() && (Boolean)this.autoGps.getValue()) {
         Matcher matcher2 = Pattern.compile("рядом с игроком\\s+(\\S+)").matcher(text4);
         String text5 = matcher2.find() ? matcher2.group(1).replaceAll("[.]+$", "") : null;
         TargetArrow.m_6(Double.parseDouble(matcher.group(1)), Double.parseDouble(matcher.group(3)), text5, true);
      }

      Component component = clientboundSystemChatPacket.content();
      String text6 = component.getString();
      String text7 = "ʟᴏɴʏɢʀɪᴇꜰ › Игрок ";
      String text8 = " ищет себе соперника!";
      if ((Boolean)this.autoPvp.getValue()
         && MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen
         && containerScreen.getTitle().getString().contains("Поиск поединка")
         && text4.contains(text7)
         && text4.contains(text8)) {
         String text9 = text6.substring(text7.length(), text6.indexOf(text8)).replaceAll("^\\s+", "");
         if (!text9.isEmpty()) {
            Component component2 = this.m_8_Component(text9);
            if (component2 != null && !component2.getString().isEmpty()) {
               Component component3 = this.m_9(component, component2, text9);
               if (component3.getString().contains("ᴘʟᴀʏᴇʀ") && this.multiModeSetting.isSelected("Игрок")
                  || component3.getString().contains("ʟᴇɢᴇɴᴅᴀ") && this.multiModeSetting.isSelected("Легенда")
                  || component3.getString().contains("ᴘʜᴏᴇɴɪx") && this.multiModeSetting.isSelected("Феникс")
                  || component3.getString().contains("ɪᴍᴘᴇʀᴀᴛᴏʀ") && this.multiModeSetting.isSelected("Император")
                  || component3.getString().contains("ᴘʀᴀᴠɪᴛᴇʟ") && this.multiModeSetting.isSelected("Правитель")
                  || component3.getString().contains("ᴘᴏᴠᴇʟɪᴛᴇʟ") && this.multiModeSetting.isSelected("Повелитель")
                  || component3.getString().contains("ᴅ.ᴀᴅᴍɪɴ") && this.multiModeSetting.isSelected("Д.Админ")
                  || component3.getString().contains("ᴏᴠᴇʀʟᴏʀᴅ") && this.multiModeSetting.isSelected("Оверлорд")
                  || component3.getString().contains("ᴇᴛᴇʀɴɪᴛʏ") && this.multiModeSetting.isSelected("Этернити")) {
                  InventoryUtil.clickSlot(MinecraftAccess.MINECRAFT.player.containerMenu.containerId, 20, 0, ContainerInput.PICKUP);
               }
            }
         }
      }

      if (text6.startsWith(text7) && text6.contains(text8) && (Boolean)this.pvpPrefixes.getValue()) {
         String text10 = text6.substring(text7.length(), text6.indexOf(text8)).replaceAll("^\\s+", "");
         if (!text10.isEmpty()) {
            Component component4 = this.m_8_Component(text10);
            if (component4 != null && !component4.getString().isEmpty()) {
               Component component5 = this.m_9(component, component4, text10);
               MinecraftAccess.MINECRAFT.gui.hud.getChat().addClientSystemMessage(component5);
               event.cancel();
            }
         }
      }
   }

   private Component m_8_Component(String text4) {
      ClientPacketListener clientPacketListener = MinecraftAccess.MINECRAFT.getConnection();
      if (clientPacketListener == null) {
         return Component.empty();
      }

      for (PlayerInfo playerInfo : clientPacketListener.getOnlinePlayers()) {
         if (playerInfo.getProfile().name().equalsIgnoreCase(text4)) {
            Component component = playerInfo.getTabListDisplayName();
            if (component == null) {
               return Component.empty();
            }

            String text5 = component.getString();
            int value7 = this.indexOfIgnoreCase(text5, text4);
            if (value7 == -1) {
               return Component.empty();
            }

            MutableComponent mutableComponent = Component.empty();
            int[] intArray = new int[]{0};
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            this.m_9(mutableComponent, component, value7, intArray, atomicBoolean);
            return mutableComponent;
         }
      }

      return Component.empty();
   }

   private int indexOfIgnoreCase(String text4, String text5) {
      return text4.toLowerCase(Locale.ROOT).indexOf(text5.toLowerCase(Locale.ROOT));
   }

   private void m_9(MutableComponent mutableComponent, Component component, int value7, int[] intArray, AtomicBoolean atomicBoolean) {
      if (!atomicBoolean.get()) {
         if (component.getContents() instanceof LiteralContents literalContents) {
            String text4 = literalContents.text();
            int value8 = text4.length();
            if (intArray[0] + value8 > value7) {
               int value9 = Math.max(0, value7 - intArray[0]);
               if (value9 > 0) {
                  mutableComponent.append(Component.literal(text4.substring(0, value9)).withStyle(component.getStyle()));
               }

               atomicBoolean.set(true);
               return;
            }

            mutableComponent.append(Component.literal(text4).withStyle(component.getStyle()));
         }

         for (Component component2 : component.getSiblings()) {
            this.m_9(mutableComponent, component2, value7, intArray, atomicBoolean);
            if (atomicBoolean.get()) {
               return;
            }
         }
      }
   }

   private Component m_9(Component component, Component component2, String text4) {
      String text5 = component.getString();
      int value7 = this.indexOfIgnoreCase(text5, text4);
      if (value7 == -1) {
         return component;
      }

      MutableComponent mutableComponent = Component.empty();
      int[] intArray = new int[]{0};
      AtomicInteger atomicInteger = new AtomicInteger(text4.length());
      AtomicBoolean atomicBoolean = new AtomicBoolean(false);
      this.m_4(mutableComponent, component, component2, text4, value7, intArray, atomicInteger, atomicBoolean);
      return mutableComponent;
   }

   private void m_4(MutableComponent mutableComponent, Component component, Component component2, String text4, int value7, int[] intArray, AtomicInteger atomicInteger, AtomicBoolean atomicBoolean) {
      if (component.getContents() instanceof LiteralContents literalContents) {
         String text5 = literalContents.text();
         int value8 = text5.length();
         if (!atomicBoolean.get()) {
            if (intArray[0] + value8 < value7) {
               mutableComponent.append(Component.literal(text5).withStyle(component.getStyle()));
            } else {
               int value9 = Math.max(0, value7 - intArray[0]);
               String text6 = text5.substring(0, value9);
               String text7 = text5.substring(value9);
               if (text7.startsWith(" ")) {
                  text7 = text7.substring(1);
               }

               if (!text6.isEmpty()) {
                  mutableComponent.append(Component.literal(text6).withStyle(component.getStyle()));
               }

               mutableComponent.append(component2);
               if (!text7.isEmpty()) {
                  mutableComponent.append(Component.literal(text7).withStyle(component.getStyle()));
               }

               atomicBoolean.set(true);
            }
         } else {
            mutableComponent.append(Component.literal(text5).withStyle(component.getStyle()));
         }
      }

      for (Component component3 : component.getSiblings()) {
         this.m_4(mutableComponent, component3, component2, text4, value7, intArray, atomicInteger, atomicBoolean);
      }
   }

   private void m_9(Item item4, Vec3 vec3, long longValue) {
      long longValue2 = System.currentTimeMillis();
      this.c8List.add(new Assistent.DataRecord2(item4, vec3, longValue2 + longValue, longValue2, longValue));
   }

   private void m_4_void(long longValue) {
      if (this.value6 > 0) {
         this.value6--;
         if (this.value6 <= 0 && !this.blockPosSet.isEmpty()) {
            var optional = BoundingBox.encapsulatingPositions(this.blockPosSet);
            if (optional.isEmpty()) {
               this.blockPosSet.clear();
            } else {
               BoundingBox boundingBox = (BoundingBox)optional.get();

               for (Assistent.ModeKind c4 : C4_LIST) {
                  if (c4.m_9(boundingBox, this.blockPosSet)) {
                     this.blockPosSet.clear();
                     Vec3 vec3 = new Vec3(boundingBox.getCenter().getX() + 0.5, boundingBox.getCenter().getY(), boundingBox.getCenter().getZ() + 0.5);

                     for (Assistent.DataRecord2 c8 : this.c8List) {
                        if (c8.f_7_x == c4.item && c8.f_9_s.distanceToSqr(vec3) < 4.0) {
                           return;
                        }
                     }

                     this.c8List.add(new Assistent.DataRecord2(c4.item, vec3, longValue + c4.longValue, longValue, c4.longValue));
                     return;
                  }
               }

               this.blockPosSet.clear();
            }
         }
      }
   }

   private Item m_2_Item() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return null;
      } else {
         Item item4 = MinecraftAccess.MINECRAFT.player.getMainHandItem().getItem();
         Item item5 = MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem();
         if (this.isTrackedItem(item4)) {
            return item4;
         } else {
            return this.isTrackedItem(item5) ? item5 : null;
         }
      }
   }

   private boolean isTrackedItem(Item item4) {
      if (((String)this.server.getValue()).equals("HolyWorld")) {
         return item4 == Items.PRISMARINE_SHARD
            || item4 == Items.POPPED_CHORUS_FRUIT
            || item4 == Items.NETHER_STAR
            || item4 == Items.FIRE_CHARGE
            || item4 == Items.SNOWBALL;
      } else if (this.isReallyWorld()) {
         return item4 == Items.FIREWORK_STAR || item4 == Items.HEART_OF_THE_SEA;
      } else {
         return ((String)this.server.getValue()).equals("LonyGrief")
            ? item4 == Items.CRYING_OBSIDIAN || item4 == Items.CLAY_BALL || item4 == Items.MAGMA_CREAM
            : item4 == Items.SNOWBALL
               || item4 == Items.PHANTOM_MEMBRANE
               || item4 == Items.NETHERITE_SCRAP
               || item4 == Items.DRIED_KELP
               || item4 == Items.SUGAR
               || item4 == Items.FIRE_CHARGE
               || item4 == Items.ENDER_EYE
               || item4 == Items.SPLASH_POTION;
      }
   }

   private Vec3 getLabelPos(Vec3 vec3) {
      return new Vec3(Math.floor(vec3.x) + 0.5, Math.floor(vec3.y) + 2.5, Math.floor(vec3.z) + 0.5);
   }

   private Vec3 m_6(Assistent.DataRecord2 c8) {
      if (c8.f_7_x == Items.NETHERITE_SCRAP) {
         return c8.f_9_s.add(0.0, 2.6, 0.0);
      } else {
         return c8.f_7_x == Items.DRIED_KELP ? c8.f_9_s.add(0.0, 4.0, 0.0) : c8.f_9_s.add(0.0, 5.0, 0.0);
      }
   }

   private void m_0(Matrix4f matrix4f, Vec3 vec3, Vec3 vec32, int value7, float floatValue) {
      Vec3 vec33 = this.getLabelPos(vec32);
      this.m_0(matrix4f, vec3, vec33, 2.5F, value7, floatValue);
   }

   private void m_0(Matrix4f matrix4f, Vec3 vec3, Vec3 vec32, float floatValue, int value7, float floatValue2) {
      float floatValue3 = floatValue;
      float floatValue4 = (float)(vec32.x - floatValue3 - vec3.x);
      float floatValue5 = (float)(vec32.y - floatValue3 - vec3.y);
      float floatValue6 = (float)(vec32.z - floatValue3 - vec3.z);
      float floatValue7 = (float)(vec32.x + floatValue3 - vec3.x);
      float floatValue8 = (float)(vec32.y + floatValue3 - vec3.y);
      float floatValue9 = (float)(vec32.z + floatValue3 - vec3.z);
      Box3DRenderer.drawBox(matrix4f, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, ColorUtil.withAlpha(value7, (int)(40.8F * floatValue2)));
      Vec3 vec33 = new Vec3(vec32.x - floatValue3, vec32.y - floatValue3, vec32.z - floatValue3);
      Vec3 vec34 = new Vec3(vec32.x + floatValue3, vec32.y - floatValue3, vec32.z - floatValue3);
      Vec3 vec35 = new Vec3(vec32.x + floatValue3, vec32.y - floatValue3, vec32.z + floatValue3);
      Vec3 vec36 = new Vec3(vec32.x - floatValue3, vec32.y - floatValue3, vec32.z + floatValue3);
      Vec3 vec37 = new Vec3(vec32.x - floatValue3, vec32.y + floatValue3, vec32.z - floatValue3);
      Vec3 vec38 = new Vec3(vec32.x + floatValue3, vec32.y + floatValue3, vec32.z - floatValue3);
      Vec3 vec39 = new Vec3(vec32.x + floatValue3, vec32.y + floatValue3, vec32.z + floatValue3);
      Vec3 vec310 = new Vec3(vec32.x - floatValue3, vec32.y + floatValue3, vec32.z + floatValue3);
      int value8 = ColorUtil.withAlpha(value7, (int)(255.0F * floatValue2));
      Line3DRenderer.m_9(
         matrix4f,
         List.of(
            List.of(vec33, vec34, vec35, vec36, vec33),
            List.of(vec37, vec38, vec39, vec310, vec37),
            List.of(vec33, vec37),
            List.of(vec34, vec38),
            List.of(vec35, vec39),
            List.of(vec36, vec310)
         ),
         vec3,
         1.5F,
         value8
      );
   }

   private void m_1(Matrix4f matrix4f, Vec3 vec3, Vec3 vec32, float floatValue, int value7) {
      ArrayList list = new ArrayList(73);

      for (int i = 0; i <= 72; i++) {
         double doubleValue = Math.toRadians(i * 5);
         list.add(vec32.add(Math.cos(doubleValue) * floatValue, 0.05, Math.sin(doubleValue) * floatValue));
      }

      Line3DRenderer.drawLineStrip(matrix4f, list, vec3, 2.0F, value7);
   }

   private void m_9(Matrix4f matrix4f, Vec3 vec3, double doubleValue, double doubleValue2, double doubleValue3, double doubleValue4, double doubleValue5, double doubleValue6, int value7) {
      Vec3 vec32 = new Vec3(doubleValue, doubleValue2, doubleValue3);
      Vec3 vec33 = new Vec3(doubleValue4, doubleValue2, doubleValue3);
      Vec3 vec34 = new Vec3(doubleValue4, doubleValue2, doubleValue6);
      Vec3 vec35 = new Vec3(doubleValue, doubleValue2, doubleValue6);
      Vec3 vec36 = new Vec3(doubleValue, doubleValue5, doubleValue3);
      Vec3 vec37 = new Vec3(doubleValue4, doubleValue5, doubleValue3);
      Vec3 vec38 = new Vec3(doubleValue4, doubleValue5, doubleValue6);
      Vec3 vec39 = new Vec3(doubleValue, doubleValue5, doubleValue6);
      Line3DRenderer.m_9(
         matrix4f,
         List.of(
            List.of(vec32, vec33, vec34, vec35, vec32),
            List.of(vec36, vec37, vec38, vec39, vec36),
            List.of(vec32, vec36),
            List.of(vec33, vec37),
            List.of(vec34, vec38),
            List.of(vec35, vec39)
         ),
         vec3,
         2.0F,
         value7
      );
   }

   private void releaseForward() {
      if (!this.flag2 && MinecraftAccess.MINECRAFT.options.keyUp.isDown()) {
         this.flag2 = true;
      }

      MinecraftAccess.MINECRAFT.options.keyUp.setDown(false);
   }

   private void restoreForward() {
      if (this.flag2) {
         MinecraftAccess.MINECRAFT.options.keyUp.setDown(true);
         this.flag2 = false;
      }
   }

   private void m_4(Item item4, String text4) {
      if (this.m_7(item4, text4, 0, 36) != -1) {
         Runnable runnable = () -> this.m_9(item4, text4);
         if (this.m_7(item4, text4, 0, 9) != -1) {
            runnable.run();
         } else {
            InventoryUtil.m_0(runnable);
         }
      }
   }

   private void m_9(Item item4, String text4) {
      if (!MinecraftAccess.MINECRAFT.player.getCooldowns().isOnCooldown(new ItemStack(item4))) {
         int value7 = this.m_7(item4, text4, 9, 36);
         int value8 = this.m_7(item4, text4, 0, 9);
         int value9 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
         if (!MinecraftAccess.MINECRAFT.player.getMainHandItem().is(item4)
            || text4 != null && !MinecraftAccess.MINECRAFT.player.getMainHandItem().getHoverName().getString().toLowerCase(Locale.ROOT).contains(text4.toLowerCase(Locale.ROOT))) {
            if (!MinecraftAccess.MINECRAFT.player.getOffhandItem().is(item4)
               || text4 != null && !MinecraftAccess.MINECRAFT.player.getOffhandItem().getHoverName().getString().toLowerCase(Locale.ROOT).contains(text4.toLowerCase(Locale.ROOT))) {
               if (value8 != -1) {
                  MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value8));
                  MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
                  MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value9));
               } else if (value7 != -1) {
                  int value10 = InventoryUtil.m_5_int();
                  boolean flag6 = value10 != value9;
                  InventoryUtil.clickSlot(0, value7, value10, ContainerInput.SWAP);
                  MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundContainerClosePacket(0));
                  if (flag6) {
                     MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value10));
                  }

                  MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
                  if (flag6) {
                     MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value9));
                  }

                  InventoryUtil.clickSlot(0, value7, value10, ContainerInput.SWAP);
                  MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundContainerClosePacket(0));
               }
            } else {
               MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.OFF_HAND);
            }
         } else {
            MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
         }
      }
   }

   private int m_7(Item item4, String text4, int value7, int value8) {
      if (text4 == null) {
         return InventoryUtil.findItemSlotInRange(item4, value7, value8);
      }

      String text5 = text4.toLowerCase(Locale.ROOT);
      return InventoryUtil.findSlotInRange(itemStack -> itemStack.is(item4) && itemStack.getHoverName().getString().toLowerCase(Locale.ROOT).contains(text5), value7, value8);
   }

   @Override
   public void onDisable() {
      this.item = null;
      this.text = null;
      this.item2 = null;
      this.item3 = null;
      this.text2 = null;
      this.flag3 = false;
      this.flag4 = false;
      this.flag = false;
      this.value2 = 0;
      if (this.flag5) {
         MinecraftAccess.MINECRAFT.options.keyShift.setDown(false);
         this.flag5 = false;
      }

      this.value3 = -1;
      this.value4 = 0;
      this.value5 = 0;
      this.c8List.clear();
      this.c3List.clear();
      this.identifierMap.clear();
      this.blockPosSet.clear();
      this.value6 = 0;
      TargetArrow.m_7_void();
      super.onDisable();
   }

   public List<Assistent.DataRecord> m_3_List() {
      ArrayList list = new ArrayList();
      if (((String)this.server.getValue()).equals("HolyWorld")) {
         this.m_5(list, Items.PRISMARINE_SHARD, null, (Integer)this.explosiveTrap.getValue());
         this.m_5(list, Items.POPPED_CHORUS_FRUIT, null, (Integer)this.trap.getValue());
         this.m_5(list, Items.NETHER_STAR, null, (Integer)this.stun.getValue());
         this.m_5(list, Items.FIRE_CHARGE, null, (Integer)this.fireCharge.getValue());
         this.m_5(list, Items.SNOWBALL, null, (Integer)this.snowball.getValue());
      } else if (this.isReallyWorld()) {
         this.m_5(list, Items.FIREWORK_STAR, "анти", (Integer)this.antiFlight.getValue());
         this.m_5(list, Items.FIREWORK_STAR, "пульс", (Integer)this.darkPulse.getValue());
         this.m_5(list, Items.HEART_OF_THE_SEA, null, (Integer)this.trap4.getValue());
         this.m_5(list, Items.FLOWER_BANNER_PATTERN, "свиток опыта", (Integer)this.expScroll.getValue());
      } else if (((String)this.server.getValue()).equals("LonyGrief")) {
         this.m_5(list, Items.CRYING_OBSIDIAN, null, (Integer)this.trap3.getValue());
         this.m_5(list, Items.CLAY_BALL, null, (Integer)this.leaver.getValue());
         this.m_5(list, Items.MAGMA_CREAM, null, (Integer)this.leaver.getValue());
      } else if (this.isFunTime()) {
         this.m_5(list, Items.SNOWBALL, "заморозка", (Integer)this.freezeSnowball.getValue());
         this.m_5(list, Items.PHANTOM_MEMBRANE, null, (Integer)this.holyAura.getValue());
         this.m_5(list, Items.NETHERITE_SCRAP, null, (Integer)this.trap2.getValue());
         this.m_5(list, Items.DRIED_KELP, null, (Integer)this.layer.getValue());
         this.m_5(list, Items.SUGAR, "явная", (Integer)this.visibleDust.getValue());
         this.m_5(list, Items.FIRE_CHARGE, null, (Integer)this.fireTornado.getValue());
         this.m_5(list, Items.ENDER_EYE, null, (Integer)this.disorientation.getValue());
         this.m_5(list, Items.SPLASH_POTION, "хлопушка", (Integer)this.firecracker.getValue());
         this.m_5(list, Items.SPLASH_POTION, "святая", (Integer)this.holyWater.getValue());
         this.m_5(list, Items.SPLASH_POTION, "снотворное", (Integer)this.sleepingPotion.getValue());
         this.m_5(list, Items.SPLASH_POTION, "гнева", (Integer)this.wrathPotion.getValue());
         this.m_5(list, Items.SPLASH_POTION, "паладина", (Integer)this.paladinPotion.getValue());
         this.m_5(list, Items.SPLASH_POTION, "ассасина", (Integer)this.assassinPotion.getValue());
         this.m_5(list, Items.SPLASH_POTION, "радиации", (Integer)this.radiationPotion.getValue());
      }

      return list;
   }

   private void m_5(List<Assistent.DataRecord> c1List, Item item4, String text4, int value7) {
      if (value7 != -1) {
         c1List.add(new Assistent.DataRecord(item4, text4, value7, this.m_8(item4, text4), getItemId(text4)));
      }
   }

   private static int getItemId(String text4) {
      if (text4 == null) {
         return 0;
      }

      return switch (text4.toLowerCase(Locale.ROOT)) {
         case "хлопушка" -> 16711680;
         case "святая" -> 16777215;
         case "снотворное" -> 4737096;
         case "гнева" -> 10040115;
         case "паладина" -> 65535;
         case "ассасина" -> 3355443;
         case "радиации" -> 3329330;
         default -> 0;
      };
   }

   private int m_8(Item item4, String text4) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return 0;
      }

      String text5 = text4 == null ? null : text4.toLowerCase(Locale.ROOT);
      int value7 = 0;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack.is(item4) && (text5 == null || itemStack.getHoverName().getString().toLowerCase(Locale.ROOT).contains(text5))) {
            value7 += itemStack.getCount();
         }
      }

      return value7;
   }

   static {
      m_6_void("XVyOk");
   }

   public static void m_6_void(String text4) {
      text3 = text4;
   }

   public static String m_1_String() {
      return text3;
   }

   private static final class StateHolder {
      public final float floatValue;
      public final int value;
      public final boolean flag;

      public StateHolder(float floatValue2, int value7, boolean flag6) {
         this.floatValue = floatValue2;
         this.value = value7;
         this.flag = flag6;
      }
   }

   public record DataRecord(Item f_4o, String f_7w, int f_9_i, int f_9e, int f_9k) {
      public ItemStack m_4_ItemStack() {
         UnHook.m_7_IArr();
         ItemStack itemStack = new ItemStack(this.f_4o);
         if (this.f_9k != 0) {
            itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(this.f_9k), List.of(), Optional.empty()));
         }

         return itemStack;
      }

      public Item m_4o() {
         return this.f_4o;
      }

      public String m_7w() {
         return this.f_7w;
      }

      public int m_9_i_int() {
         return this.f_9_i;
      }

      public int m_9e() {
         return this.f_9e;
      }

      public int m_9k_int() {
         return this.f_9k;
      }
   }

   private static final class StateHolder2 {
      private ItemStack itemStack;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private float floatValue7;
      private float floatValue8;
      private float floatValue9;
      private float floatValue10;
      private float floatValue11;
      private float floatValue12;
      private float floatValue13;
      private float floatValue14;
      private float floatValue15;
      private float floatValue16;
      private String text;
      private float floatValue17;
      private float floatValue18;
      private float floatValue19;
      private int value;
      private int value2;
      private int value3;
      private int value4;
   }

   private enum ModeKind {
      DRAGON_TRAP("Драконья трапка", new int[][]{{7, 7, 7}, {7, 7, 6}}, true, 30000L, Items.NETHERITE_SCRAP),
      TRAPKA("Трапка", new int[][]{{5, 5, 5}, {5, 5, 4}, {5, 6, 5}, {5, 6, 4}}, true, 15000L, Items.NETHERITE_SCRAP),
      DRAGON_PLAST("Драконий пласт", new int[][]{{7, 7, 2}, {7, 7, 1}}, true, 20000L, Items.DRIED_KELP),
      PLAST("Пласт", new int[][]{{5, 5, 2}, {5, 5, 1}}, true, 20000L, Items.DRIED_KELP),
      GARMOSHKA("Пласт", new int[][]{{5, 5, 5}, {5, 6, 5}}, false, 20000L, Items.DRIED_KELP);

      public final String text;
      public final int[][] valueArray;
      public final boolean flag;
      public final long longValue;
      public final Item item;

      ModeKind(String text4, int[][] arrayArray, boolean flag6, long longValue2, Item item4) {
         this.text = text4;
         this.valueArray = arrayArray;
         this.flag = flag6;
         this.longValue = longValue2;
         this.item = item4;
      }

      public boolean m_9(BoundingBox boundingBox, Set<BlockPos> blockPosSet2) {
         int[] intArray = new int[]{boundingBox.getXSpan(), boundingBox.getYSpan(), boundingBox.getZSpan()};
         UnHook.m_7_IArr();
         Arrays.sort(intArray);

         for (int[] intArray2 : this.valueArray) {
            int[] intArray3 = (int[])intArray2.clone();
            Arrays.sort(intArray3);
            if (Arrays.equals(intArray, intArray3)) {
               boolean flag6 = this.flag
                  == blockPosSet2.stream()
                     .noneMatch(
                        blockPos -> blockPos.getX() > boundingBox.minX()
                           && blockPos.getX() < boundingBox.maxX()
                           && blockPos.getY() > boundingBox.minY()
                           && blockPos.getY() < boundingBox.maxY()
                           && blockPos.getZ() > boundingBox.minZ()
                           && blockPos.getZ() < boundingBox.maxZ()
                     );
               if (!flag6) {
                  return false;
               }

               int value7 = intArray3[0] * intArray3[1] * intArray3[2];
               int value8 = Math.max(0, intArray3[0] - 2) * Math.max(0, intArray3[1] - 2) * Math.max(0, intArray3[2] - 2);
               int value9 = this.flag ? value7 - value8 : value7;
               return blockPosSet2.size() >= value9 * 0.7;
            }
         }

         return false;
      }

      private static String a(byte[] byteArray) {
         int value7 = 0;
         int value8;
         char[] charArray = new char[value8 = byteArray.length];

         for (int i = 0; i < value8; i++) {
            int value9;
            if ((value9 = 255 & byteArray[i]) < 192) {
               charArray[value7++] = (char)value9;
            } else if (value9 < 224) {
               char ch = (char)((char)(value9 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value7++] = ch;
            } else if (i < value8 - 2) {
               char ch2 = (char)((char)(value9 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value7++] = ch2;
            }
         }

         return new String(charArray, 0, value7);
      }
   }

   public record DataRecord2(Item f_7_x, Vec3 f_9_s, long f_8_m, long f_8_g, long f_86) {
      public Item m_7_x() {
         return this.f_7_x;
      }

      public Vec3 m_9_s() {
         return this.f_9_s;
      }

      public long m_8_m() {
         return this.f_8_m;
      }

      public long m_8_g() {
         return this.f_8_g;
      }

      public long m_86_long() {
         return this.f_86;
      }
   }
}
