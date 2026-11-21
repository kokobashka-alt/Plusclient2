package wtf.onetap.module.render;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns.CooldownInstance;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.AttackEntityEvent;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.ClickGuiScreen;
import wtf.onetap.gui.hud.HudStylePanel;
import wtf.onetap.gui.widget.TextField;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.module.misc.Assistent;
import wtf.onetap.module.misc.ScoreboardHealth;
import wtf.onetap.module.player.InvseeExploit;
import wtf.onetap.module.player.StaffExploit;
import wtf.onetap.network.ServerTpsTracker;
import wtf.onetap.platform.C7a;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.FontAtlas;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.PlayerFaceTexture;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.TextRenderer;
import wtf.onetap.render.Theme;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.LabelSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.setting.Setting;
import wtf.onetap.util.ChatBadges;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.KeyNames;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.MouseUtil;

@ModuleInformation(name = "HUD", description = "Интерфейс этого прекрасного чита", category = Category.RENDER)
public class HUD extends Module {
   private final MultiModeSetting multiModeSetting;
   private final FloatSetting floatSetting;
   private final FloatSetting floatSetting2;
   private final LabelSetting labelSetting;
   private final ModeSetting modeSetting;
   private final ModeSetting modeSetting2;
   private final BooleanSetting booleanSetting;
   private final LabelSetting labelSetting2;
   private final ModeSetting modeSetting3;
   private final BooleanSetting booleanSetting2;
   private final LabelSetting labelSetting3;
   private final List<HUD.DataRecord3> c5List;
   private final LabelSetting labelSetting4;
   private final FloatSetting floatSetting3;
   private final BooleanSetting booleanSetting3;
   private final BooleanSetting booleanSetting4;
   private final BooleanSetting booleanSetting5;
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "images/boykisser.png");
   private static final int[] VALUE_ARRAY = new int[]{
      0,
      1,
      1,
      0,
      0,
      0,
      1,
      1,
      0,
      1,
      1,
      0,
      1,
      1,
      0,
      0,
      1,
      1,
      0,
      1,
      1,
      2,
      2,
      3,
      4,
      4,
      5,
      5,
      6,
      6,
      7,
      6,
      6,
      7,
      7,
      6,
      7,
      7,
      6,
      6,
      7,
      7,
      6,
      8,
      8,
      8,
      9,
      10,
      10,
      11,
      12,
      12,
      11,
      11,
      12,
      13,
      13,
      13,
      12,
      13,
      12,
      12,
      13,
      13,
      13,
      12,
      13,
      12,
      12,
      12,
      14,
      14,
      15,
      16,
      17,
      17,
      17,
      18,
      15,
      19,
      19,
      20,
      20,
      20,
      21,
      22,
      19,
      19,
      19,
      20
   };
   private static final Identifier IDENTIFIER2 = Identifier.withDefaultNamespace("gui");
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
   private final Animation animation;
   private final Map<BooleanSetting, Animation> booleanSettingMap;
   private final Map<String, Animation> stringMap;
   private TextField textField;
   private final Animation animation2;
   private final Map<String, Animation> stringMap2;
   private final Map<String, Animation> stringMap3;
   private final Map<String, Animation> stringMap4;
   private final Map<String, String> stringMap5;
   private final Map<String, String> stringMap6;
   private final Map<String, HUD.DataRecord> stringMap7;
   private final Map<String, HUD.StateHolder3> stringMap8;
   private final Animation animation3;
   private final TextField textField2;
   private final TextField textField3;
   private final TextField textField4;
   private final TextField textField5;
   private final TextField textField6;
   private final TextField textField7;
   private final TextField textField8;
   private final TextField textField9;
   private final TextField textField10;
   private final TextField textField11;
   private final TextField textField12;
   private final Theme theme;
   private final HudStylePanel hudStylePanel;
   private final Theme theme2;
   private final HudStylePanel hudStylePanel2;
   private final Theme theme3;
   private final Theme theme4;
   private final HudStylePanel hudStylePanel3;
   private final HudStylePanel hudStylePanel4;
   private final MouseUtil mouseUtil;
   private final Theme theme5;
   private final HudStylePanel hudStylePanel5;
   private final TextField textField13;
   private final Theme theme6;
   private final HudStylePanel hudStylePanel6;
   private final Animation animation4;
   private final Map<Identifier, HUD.StateHolder4> identifierMap;
   private final TextField textField14;
   private final Theme theme7;
   private final HudStylePanel hudStylePanel7;
   private final Animation animation5;
   private final Map<Integer, HUD.StateHolder2> integerMap;
   private final List<HUD.DataRecord4> c8List;
   private final TextField textField15;
   private final Theme theme8;
   private final HudStylePanel hudStylePanel8;
   private final Animation animation6;
   private final TextField textField16;
   private final Theme theme9;
   private final HudStylePanel hudStylePanel9;
   private final TextField textField17;
   private final Theme theme10;
   private final HudStylePanel hudStylePanel10;
   private final Animation animation7;
   private final Animation animation8;
   private final Animation animation9;
   private final Animation animation10;
   private final Animation animation11;
   private LivingEntity livingEntity;
   private String text;
   private String text2;
   private int value;
   private float floatValue;
   private float floatValue2;
   private long longValue;
   private long longValue2;
   private long longValue3;
   private final List<HUD.StateHolder> c1List;
   private int value2;
   private ItemStack itemStack;
   private static final Pattern PATTERN = Pattern.compile("^\\w{3,16}$");
   private static final Pattern PATTERN2 = Pattern.compile("^[0-9]{1,3}$");
   private static final Set<String> STRING_SET = Set.of("1232", "1234", "Dummy_1", "Dummy_2", "Dummy_3", "classic", "lite", "lite120");
   private static final String[] TEXT_ARRAY = new String[]{"staff", "helper", "moder", "admin", "curator", "fs_"};
   private static final String[] TEXT_ARRAY2 = new String[]{
      "helper",
      "moder",
      "admin",
      "curator",
      "builder",
      "yt",
      "куратор",
      "спектатор",
      "тех.админ",
      "стажёр",
      "s.kurator",
      "поддержка",
      "сотрудник",
      "ꔓ",
      "ꔉ",
      "ꔅ",
      "ꔁ",
      "ꔷ",
      "ꔳ",
      "ꔩ",
      "ꔥ",
      "ꔡ",
      "ꔗ"
   };
   private static final String[] TEXT_ARRAY3 = new String[]{"d.admin", "d-admin", "d_admin", "d admin", "д.админ", "д-админ", "д_админ", "д админ"};
   private static final int VALUE = ColorUtil.rgba(229, 0, 63, 255);
   private static final int VALUE2 = ColorUtil.rgba(80, 220, 70, 255);
   private static final ZoneId ZONE_ID = ZoneId.of("Europe/Moscow");
   private static int[] valueArray;

   public HUD() {
      Notifications.m_5_C3_sArr();
      super();
      this.multiModeSetting = new MultiModeSetting(
            "Отображать",
            "Ватермарка",
            "Ватермарка",
            "Доп. информация",
            "Координаты",
            "TPS",
            "Таргет худ",
            "Кейбинды",
            "Зелья",
            "Серверские бинды",
            "Стафф",
            "Кулдауны",
            "Килфид",
            "Расписание RW",
            "Список модулей",
            "Армор худ",
            "Инвентарь",
            "Тотемы",
            "Boykisser"
         )
         .build();
      this.floatSetting = new FloatSetting("Масштаб", 1.0F, 0.5F, 2.0F, 0.05F).build();
      this.floatSetting2 = new FloatSetting("Размер Boykisser", 64.0F, 24.0F, 192.0F, 1.0F).visibleWhen(() -> this.multiModeSetting.isSelected("Boykisser")).build();
      this.labelSetting = new LabelSetting("Таргет худ").visibleWhen(() -> this.multiModeSetting.isSelected("Таргет худ")).build();
      this.modeSetting = new ModeSetting("Мод таргет худа", "Moonward", "Moonward", "Rich Premium", "Liquid").visibleWhen(() -> this.multiModeSetting.isSelected("Таргет худ")).build();
      this.modeSetting2 = new ModeSetting("Форма", "Полоса", "Полоса", "Кружок").visibleWhen(() -> this.multiModeSetting.isSelected("Таргет худ") && this.isMoonwardModeSetting()).build();
      this.booleanSetting = new BooleanSetting("Частицы при уроне", true).visibleWhen(() -> this.multiModeSetting.isSelected("Таргет худ") && this.isMoonwardModeSetting() && !this.isCircleMode()).build();
      this.labelSetting2 = new LabelSetting("Стафф").visibleWhen(() -> this.multiModeSetting.isSelected("Стафф")).build();
      this.modeSetting3 = new ModeSetting("Показывать стафф", "Все", "Все", "Онлайн", "Ваниш").visibleWhen(() -> this.multiModeSetting.isSelected("Стафф")).build();
      this.booleanSetting2 = new BooleanSetting("Только добавленный стафф", false).visibleWhen(() -> this.multiModeSetting.isSelected("Стафф")).build();
      this.labelSetting3 = new LabelSetting("Расписание RW").visibleWhen(() -> this.multiModeSetting.isSelected("Расписание RW")).build();
      this.c5List = List.of(
         this.m_0("Захват талисмана", "Талисман", EnumSet.allOf(DayOfWeek.class), LocalTime.of(19, 30)),
         this.m_0(
            "Аирдроп",
            "Аирдроп",
            EnumSet.allOf(DayOfWeek.class),
            IntStream.iterate(9, value3 -> value3 <= 23, value4 -> value4 + 2).mapToObj(value5 -> LocalTime.of(value5, 0)).toArray(LocalTime[]::new)
         ),
         this.m_0("Битва с боссом", "Битва с боссом", EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY), LocalTime.of(17, 30)),
         this.m_0("Шахта Дворфов", "Шахта Дворфов", EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY), LocalTime.of(17, 30)),
         this.m_0("Защита Особняка", "Защита Особняка", EnumSet.of(DayOfWeek.SATURDAY), LocalTime.of(17, 30)),
         this.m_0("Колизей", "Колизей", EnumSet.of(DayOfWeek.SUNDAY), LocalTime.of(17, 30))
      );
      this.labelSetting4 = new LabelSetting("Список модулей").visibleWhen(() -> this.multiModeSetting.isSelected("Список модулей")).build();
      this.floatSetting3 = new FloatSetting("Высота строки", 10.5F, 5.0F, 14.0F, 0.5F).visibleWhen(() -> this.multiModeSetting.isSelected("Список модулей")).build();
      this.booleanSetting3 = new BooleanSetting("Фон", true).visibleWhen(() -> this.multiModeSetting.isSelected("Список модулей")).build();
      this.booleanSetting4 = new BooleanSetting("Полоска", true).visibleWhen(() -> this.multiModeSetting.isSelected("Список модулей")).build();
      this.booleanSetting5 = new BooleanSetting("Маленькие буквы", false).visibleWhen(() -> this.multiModeSetting.isSelected("Список модулей")).build();
      this.animation = new Animation(250L, 0.0F, Easing.EASING8);
      this.booleanSettingMap = new HashMap<>();
      this.stringMap = new HashMap<>();
      this.animation2 = new Animation(250L, 0.0F, Easing.EASING8);
      this.stringMap2 = new HashMap<>();
      this.stringMap3 = new HashMap<>();
      this.stringMap4 = new HashMap<>();
      this.stringMap5 = new HashMap<>();
      this.stringMap6 = new HashMap<>();
      this.stringMap7 = new HashMap<>();
      this.stringMap8 = new LinkedHashMap<>();
      this.animation3 = new Animation(250L, 0.0F, Easing.EASING8);
      this.textField2 = new TextField("Watermark", 4.0F, 4.0F);
      this.textField3 = new TextField("TpsInfo", 4.0F, 300.0F);
      this.textField4 = new TextField("CoordsInfo", 4.0F, 316.0F);
      this.textField5 = new TextField("Keybinds", 333.0F, 333.0F);
      this.textField6 = new TextField("Potions", 4.0F, 24.0F);
      this.textField7 = new TextField("ServerBinds", 4.0F, 60.0F);
      this.textField8 = new TextField("Staff", 4.0F, 80.0F);
      this.textField9 = new TextField("TargetHUD", 4.0F, 50.0F);
      this.textField10 = new TextField("ArrayList", 0.0F, 0.0F);
      this.textField11 = new TextField("ArmorHUD", 0.0F, 0.0F);
      this.textField12 = new TextField("Boykisser", 0.0F, 0.0F);
      this.theme = new Theme("TargetHud", true);
      this.hudStylePanel = HudStylePanel.m_8(this.textField9, this.theme, "Таргет", this.modeSetting2, this.booleanSetting, () -> !this.isCircleMode());
      this.theme2 = new Theme("TargetHudLiquid", ColorUtil.rgba(12, 12, 16, 90), ColorUtil.rgba(242, 242, 245, 255), ColorUtil.rgba(125, 125, 125, 255));
      this.hudStylePanel2 = HudStylePanel.m_7(this.textField9, this.theme2, "Таргет");
      this.theme3 = new Theme("Potions");
      this.theme4 = new Theme("Keybinds");
      this.hudStylePanel3 = new HudStylePanel(this.textField6, this.theme3, "Эффекты");
      this.hudStylePanel4 = new HudStylePanel(this.textField5, this.theme4, "Клавиши");
      this.mouseUtil = new MouseUtil(this::getFields, this::isFieldEnabled);
      this.theme5 = new Theme("Staff");
      this.hudStylePanel5 = new HudStylePanel(this.textField8, this.theme5, "Стафф");
      this.textField13 = new TextField("Cooldowns", 4.0F, 120.0F);
      this.theme6 = new Theme("Cooldowns");
      this.hudStylePanel6 = new HudStylePanel(this.textField13, this.theme6, "Кулдауны");
      this.animation4 = new Animation(250L, 0.0F, Easing.EASING8);
      this.identifierMap = new HashMap<>();
      this.textField14 = new TextField("KillFeed", 4.0F, 160.0F);
      this.theme7 = new Theme("KillFeed");
      this.hudStylePanel7 = new HudStylePanel(this.textField14, this.theme7, "Килфид");
      this.animation5 = new Animation(250L, 0.0F, Easing.EASING8);
      this.integerMap = new HashMap<>();
      this.c8List = new ArrayList<>();
      this.textField15 = new TextField("SchedulesRW", 4.0F, 200.0F);
      this.theme8 = new Theme("SchedulesRW");
      this.hudStylePanel8 = new HudStylePanel(this.textField15, this.theme8, "Расписание RW");
      this.animation6 = new Animation(250L, 0.0F, Easing.EASING8);
      this.textField16 = new TextField("InvseeExploit", 800.0F, 200.0F);
      this.theme9 = new Theme("Invsee");
      this.hudStylePanel9 = new HudStylePanel(this.textField16, this.theme9, "Инвентарь");
      this.textField17 = new TextField("Inventory", 800.0F, 300.0F);
      this.theme10 = new Theme("Inventory");
      this.hudStylePanel10 = new HudStylePanel(this.textField17, this.theme10, "Инвентарь");
      this.animation7 = new Animation(250L, 0.0F, Easing.EASING8);
      this.animation8 = new Animation(350L, 0.0F, Easing.EASING8);
      this.animation9 = new Animation(350L, 0.0F, Easing.EASING8);
      this.animation10 = new Animation(250L, 0.0F, Easing.EASING8);
      this.animation11 = new Animation(120L, 1.0F, Easing.EASING8);
      this.text = "";
      this.text2 = "";
      this.value = -1;
      this.floatValue = -1.0F;
      this.floatValue2 = -1.0F;
      this.longValue = 0L;
      this.longValue2 = 0L;
      this.longValue3 = 0L;
      this.c1List = new ArrayList<>();
      this.value2 = Integer.MIN_VALUE;
   }

   @EventTarget
   private void onRender2D2(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         float floatValue3 = (Float)this.floatSetting.getValue();
         this.getFields().forEach(textField18 -> textField18.setFloatValue7(floatValue3));
         this.textField3.setFloatValue7(floatValue3);
         this.textField4.setFloatValue7(floatValue3);
         boolean flag = MinecraftAccess.MINECRAFT.gui.screen() instanceof ChatScreen;
         if (!flag) {
            this.getFields().forEach(TextField::m_7_void);
            this.mouseUtil.m_6_void();
         }

         boolean flag2 = flag && this.getFields().stream().anyMatch(TextField::isFlag);
         this.textField = flag ? this.getHoveredField() : null;
         if (!flag2) {
            this.mouseUtil.m_4_void();
         }

         this.mouseUtil.m_4(flag2);
         boolean flag3 = this.multiModeSetting.isSelected("Доп. информация");
         boolean flag4 = this.multiModeSetting.isSelected("Координаты");
         if (this.multiModeSetting.isSelected("Ватермарка")) {
            this.runWithReset(() -> this.m_6_void(flag3));
         }

         if (flag4) {
            this.runWithReset(this::renderCoordinates);
         }

         if (this.multiModeSetting.isSelected("TPS")) {
            this.runWithReset(() -> this.renderTps(flag4));
         }

         if (this.multiModeSetting.isSelected("Зелья")) {
            this.runWithReset(this::m_4_void);
         }

         if (this.multiModeSetting.isSelected("Кейбинды")) {
            this.runWithReset(this::m_2_void);
         }

         if (this.multiModeSetting.isSelected("Серверские бинды")) {
            this.runWithReset(() -> this.m_3(event));
         }

         if (this.multiModeSetting.isSelected("Стафф")) {
            this.m_03_void();
            this.runWithReset(this::m_0_h_void);
         }

         if (this.multiModeSetting.isSelected("Кулдауны")) {
            this.runWithReset(() -> this.m_8(event));
         }

         if (this.multiModeSetting.isSelected("Килфид")) {
            this.runWithReset(this::m_3_void);
         }

         if (this.multiModeSetting.isSelected("Расписание RW")) {
            this.runWithReset(this::m_0o_void);
         }

         if (this.isFieldEnabled(this.textField16)) {
            this.runWithReset(() -> this.m_0(event, this.textField16, this.theme9, this.hudStylePanel9, "Инвентарь противника", Onetap.MODULE_MANAGER.get(InvseeExploit.class).getItemStackList(), 4));
         }

         if (this.multiModeSetting.isSelected("Инвентарь")) {
            this.runWithReset(() -> this.m_0(event, this.textField17, this.theme10, this.hudStylePanel10, "Инвентарь", MinecraftAccess.MINECRAFT.player.getInventory().getNonEquipmentItems(), 3));
         }

         if (this.multiModeSetting.isSelected("Список модулей")) {
            this.runWithReset(this::m_0y_void);
         }

         if (this.multiModeSetting.isSelected("Армор худ")) {
            this.runWithReset(() -> this.renderArmor(event));
         }

         if (this.multiModeSetting.isSelected("Boykisser")) {
            this.runWithReset(this::m_7_void);
         }

         boolean flag5 = this.multiModeSetting.isSelected("Тотемы");
         if (flag5) {
            this.renderTotemCount(event);
         }

         if (flag3) {
            double doubleValue = MinecraftAccess.MINECRAFT.player.getX() - MinecraftAccess.MINECRAFT.player.xo;
            double doubleValue2 = MinecraftAccess.MINECRAFT.player.getY() - MinecraftAccess.MINECRAFT.player.yo;
            double doubleValue3 = MinecraftAccess.MINECRAFT.player.getZ() - MinecraftAccess.MINECRAFT.player.zo;
            double doubleValue4 = Math.sqrt(doubleValue * doubleValue + doubleValue2 * doubleValue2 + doubleValue3 * doubleValue3) * 20.0;
            String text3 = String.format(Locale.US, "%.2f", doubleValue4);
            Supplier supplier = Fonts.SUPPLIER18;
            float floatValue4 = Draw2D.getTextWidth(text3, supplier, 12.0F);
            int value3 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth();
            int value4 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight();
            Draw2D.drawStyledText(text3, supplier, (value3 - floatValue4) / 2.0F, value4 / 2.0F + (flag5 ? 19.0F : 10.0F), ColorUtil.rgba(255, 255, 255, 255), 12.0F);
         }

         this.mouseUtil.m_0_void();
      }
   }

   private void renderTotemCount(Render2DEvent event) {
      if (this.itemStack == null) {
         this.itemStack = new ItemStack(Items.TOTEM_OF_UNDYING);
      }

      Inventory inventory = MinecraftAccess.MINECRAFT.player.getInventory();
      int value3 = 0;

      for (int i = 0; i < inventory.getContainerSize(); i++) {
         ItemStack itemStack2 = inventory.getItem(i);
         if (itemStack2.is(Items.TOTEM_OF_UNDYING)) {
            value3 += itemStack2.getCount();
         }
      }

      Supplier supplier = Fonts.SUPPLIER18;
      float floatValue3 = 10.0F;
      float floatValue4 = 2.0F;
      float floatValue5 = 8.5F;
      String text3 = value3 + "x";
      float floatValue6 = Draw2D.getTextWidth(text3, supplier, floatValue5);
      float floatValue7 = floatValue3 + floatValue4 + floatValue6;
      float floatValue8 = (MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth() - floatValue7) / 2.0F;
      float floatValue9 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() / 2.0F + 7.0F;
      TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
      MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, this.itemStack, ItemDisplayContext.GUI, MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, 0);
      event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
      Render2D.drawItem(trackingItemStackRenderState, floatValue8, floatValue9, floatValue3, ColorUtil.rgba(255, 255, 255, 255));
      int value4 = value3 > 0 ? ColorUtil.rgba(255, 255, 255, 255) : ColorUtil.rgba(255, 85, 85, 255);
      Draw2D.drawTextWithShadow(text3, supplier, floatValue8 + floatValue3 + floatValue4, Draw2D.getTextBaseline(supplier, floatValue5, floatValue9, floatValue3), value4, floatValue5);
   }

   @EventTarget(priority = EventPriority.LOW)
   private void onRender2D(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         this.runWithReset(() -> this.renderTargetHud(event));
         HudStylePanel hudStylePanel11 = this.animation7.getValue() > 0.01F ? this.getActivePanel() : null;
         if (hudStylePanel11 != this.hudStylePanel) {
            this.hudStylePanel.m_7_void();
         }

         if (hudStylePanel11 != this.hudStylePanel2) {
            this.hudStylePanel2.m_7_void();
         }

         for (HudStylePanel hudStylePanel12 : this.getPanels()) {
            hudStylePanel12.m_0_void();
         }
      }
   }

   @EventTarget(priority = EventPriority.HIGHEST)
   private void onMouseClick(MouseClickEvent event) {
      if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ChatScreen && !ClickGuiScreen.isEditing()) {
         if (event.getAction() != 1) {
            this.getFields().forEach(TextField::m_7_void);

            for (HudStylePanel hudStylePanel11 : this.getPanels()) {
               hudStylePanel11.m_1_void();
            }
         } else {
            float floatValue3 = (float)event.getX();
            float floatValue4 = (float)event.getY();
            if (event.getButton() == 0) {
               boolean flag = false;

               for (HudStylePanel hudStylePanel12 : this.getPanels()) {
                  flag |= hudStylePanel12.m_5_boolean(floatValue3, floatValue4);
               }

               if (flag) {
                  event.cancel();
                  return;
               }
            }

            if (event.getButton() == 1) {
               Notifications notifications = Onetap.MODULE_MANAGER.get(Notifications.class);
               HudStylePanel hudStylePanel13 = null;
               if (this.multiModeSetting.isSelected("Таргет худ") && this.getActivePanel() != null && this.textField9.m_2_boolean(floatValue3, floatValue4)) {
                  hudStylePanel13 = this.getActivePanel();
               } else if (this.multiModeSetting.isSelected("Инвентарь") && this.textField17.m_2_boolean(floatValue3, floatValue4)) {
                  hudStylePanel13 = this.hudStylePanel10;
               } else if (this.isFieldEnabled(this.textField16) && this.textField16.m_2_boolean(floatValue3, floatValue4)) {
                  hudStylePanel13 = this.hudStylePanel9;
               } else if (this.multiModeSetting.isSelected("Расписание RW") && this.textField15.m_2_boolean(floatValue3, floatValue4)) {
                  hudStylePanel13 = this.hudStylePanel8;
               } else if (this.multiModeSetting.isSelected("Килфид") && this.textField14.m_2_boolean(floatValue3, floatValue4)) {
                  hudStylePanel13 = this.hudStylePanel7;
               } else if (this.multiModeSetting.isSelected("Кулдауны") && this.textField13.m_2_boolean(floatValue3, floatValue4)) {
                  hudStylePanel13 = this.hudStylePanel6;
               } else if (this.multiModeSetting.isSelected("Стафф") && this.textField8.m_2_boolean(floatValue3, floatValue4)) {
                  hudStylePanel13 = this.hudStylePanel5;
               } else if (this.multiModeSetting.isSelected("Кейбинды") && this.textField5.m_2_boolean(floatValue3, floatValue4)) {
                  hudStylePanel13 = this.hudStylePanel4;
               } else if (this.multiModeSetting.isSelected("Зелья") && this.textField6.m_2_boolean(floatValue3, floatValue4)) {
                  hudStylePanel13 = this.hudStylePanel3;
               } else if (notifications.m_7_boolean(floatValue3, floatValue4)) {
                  hudStylePanel13 = notifications.getHudStylePanel();
               }

               if (hudStylePanel13 != null) {
                  for (HudStylePanel hudStylePanel14 : this.getPanels()) {
                     if (hudStylePanel14 != hudStylePanel13) {
                        hudStylePanel14.m_7_void();
                     }
                  }

                  hudStylePanel13.toggle();
                  event.cancel();
                  return;
               }
            }

            if (event.getButton() == 1) {
               for (HudStylePanel hudStylePanel15 : this.getPanels()) {
                  hudStylePanel15.m_7_void();
               }
            }

            for (TextField textField18 : this.getFields().reversed()) {
               if (this.isFieldEnabled(textField18) && textField18.m_0(floatValue3, floatValue4, event.getButton())) {
                  event.cancel();
                  return;
               }
            }
         }
      }
   }

   private void runWithReset(Runnable runnable) {
      try {
         runnable.run();
      } finally {
         Render2D.setTransform(1.0F, 0.0F, 0.0F);
      }
   }

   private void applyScale(float floatValue3, float floatValue4) {
      float floatValue5 = (Float)this.floatSetting.getValue();
      Render2D.setTransform(floatValue5, floatValue3 - floatValue3 * floatValue5, floatValue4 - floatValue4 * floatValue5);
   }

   public float getFloatSetting() {
      return (Float)this.floatSetting.getValue();
   }

   private TextField getHoveredField() {
      for (TextField textField18 : this.getFields()) {
         if (textField18.isFlag()) {
            return textField18;
         }
      }

      float floatValue3 = (float)(MinecraftAccess.MINECRAFT.mouseHandler.xpos() / MinecraftAccess.MINECRAFT.getWindow().getGuiScale());
      float floatValue4 = (float)(MinecraftAccess.MINECRAFT.mouseHandler.ypos() / MinecraftAccess.MINECRAFT.getWindow().getGuiScale());

      for (TextField textField19 : this.getFields().reversed()) {
         if (this.isFieldEnabled(textField19) && textField19.m_2_boolean(floatValue3, floatValue4)) {
            return textField19;
         }
      }

      return null;
   }

   public boolean isActive() {
      return this.isEnabled() && this.textField != null;
   }

   private boolean isFieldEnabled(TextField textField18) {
      if (textField18 == this.textField2) {
         return this.multiModeSetting.isSelected("Ватермарка");
      }

      if (textField18 == this.textField6) {
         return this.multiModeSetting.isSelected("Зелья");
      }

      if (textField18 == this.textField5) {
         return this.multiModeSetting.isSelected("Кейбинды");
      }

      if (textField18 == this.textField7) {
         return this.multiModeSetting.isSelected("Серверские бинды");
      }

      if (textField18 == this.textField8) {
         return this.multiModeSetting.isSelected("Стафф");
      }

      if (textField18 == this.textField13) {
         return this.multiModeSetting.isSelected("Кулдауны");
      }

      if (textField18 == this.textField14) {
         return this.multiModeSetting.isSelected("Килфид");
      }

      if (textField18 == this.textField15) {
         return this.multiModeSetting.isSelected("Расписание RW");
      }

      if (textField18 == this.textField9) {
         return this.multiModeSetting.isSelected("Таргет худ");
      }

      if (textField18 == this.textField10) {
         return this.multiModeSetting.isSelected("Список модулей");
      }

      if (textField18 == this.textField11) {
         return this.multiModeSetting.isSelected("Армор худ");
      }

      if (textField18 == this.textField17) {
         return this.multiModeSetting.isSelected("Инвентарь");
      }

      if (textField18 == this.textField12) {
         return this.multiModeSetting.isSelected("Boykisser");
      }

      if (textField18 != this.textField16) {
         return false;
      }

      InvseeExploit invseeExploit = Onetap.MODULE_MANAGER.get(InvseeExploit.class);
      return invseeExploit.isEnabled() && invseeExploit.isSenderMode();
   }

   private List<HudStylePanel> getPanels() {
      return List.of(
         this.hudStylePanel3,
         this.hudStylePanel4,
         this.hudStylePanel5,
         this.hudStylePanel6,
         this.hudStylePanel7,
         this.hudStylePanel8,
         this.hudStylePanel9,
         this.hudStylePanel10,
         this.hudStylePanel,
         this.hudStylePanel2,
         Onetap.MODULE_MANAGER.get(Notifications.class).getHudStylePanel()
      );
   }

   public List<TextField> getFields() {
      return List.of(
         this.textField2,
         this.textField6,
         this.textField5,
         this.textField7,
         this.textField8,
         this.textField13,
         this.textField14,
         this.textField15,
         this.textField16,
         this.textField17,
         this.textField10,
         this.textField11,
         this.textField12,
         this.textField9
      );
   }

   private void m_6_void(boolean flag) {
      this.layoutField(this.textField2, 7.0F);
      float floatValue3 = this.textField2.m_5_float();
      float floatValue4 = this.textField2.m_8_float();
      this.applyScale(floatValue3, floatValue4);
      float floatValue5 = 4.0F;
      float floatValue6 = 16.0F;
      float floatValue7 = 8.25F;
      float floatValue8 = 8.0F;
      float floatValue9 = 3.0F;
      float floatValue10 = floatValue7 * 1.6F;
      float floatValue11 = 3.0F;
      float floatValue12 = 12.0F;
      float floatValue13 = 14.0F;
      int value3 = ColorUtil.rgba(222, 222, 222, 255);
      int value4 = ColorUtil.rgba(135, 135, 135, 255);
      int value5 = ColorUtil.rgba(165, 165, 165, 255);
      int value6 = ColorUtil.primaryColor();
      int value7 = ColorUtil.secondaryColor();
      int value8 = ColorUtil.rgba(125, 125, 125, 255);
      String text3 = C7a.m_3_String();
      String text4 = MinecraftAccess.MINECRAFT.getFps() + " FPS";
      String text5 = LocalTime.now().format(DATE_TIME_FORMATTER);
      float floatValue14 = Draw2D.getTextBaseline(Fonts.SUPPLIER18, floatValue7, floatValue4, floatValue6);
      float floatValue15 = Draw2D.getCenteredY("\uf2bd", Fonts.SUPPLIER8, floatValue8, floatValue4, floatValue6);
      float floatValue16 = Draw2D.getCenteredY("\ue0f5", Fonts.SUPPLIER8, floatValue8, floatValue4, floatValue6);
      float floatValue17 = Draw2D.getCenteredY("\uf017", Fonts.SUPPLIER8, floatValue8, floatValue4, floatValue6);
      float floatValue18 = Draw2D.getCenteredY("•", Fonts.SUPPLIER18, floatValue10, floatValue4, floatValue6);
      float floatValue19 = Draw2D.getTextWidth("•", Fonts.SUPPLIER18, floatValue10);
      float floatValue20 = Draw2D.getTextWidth("De_Otpor", Fonts.SUPPLIER18, floatValue7);
      float floatValue21 = Draw2D.getTextWidth(" 26.2", Fonts.SUPPLIER18, floatValue7);
      float floatValue22 = Draw2D.getTextWidth(text3, Fonts.SUPPLIER18, floatValue7);
      float floatValue23 = Draw2D.getTextWidth(text4, Fonts.SUPPLIER18, floatValue7);
      float floatValue24 = Draw2D.getTextWidth(text5, Fonts.SUPPLIER18, floatValue7);
      float floatValue25 = Draw2D.getTextWidth("\uf2bd", Fonts.SUPPLIER8, floatValue8);
      float floatValue26 = Draw2D.getTextWidth("\ue0f5", Fonts.SUPPLIER8, floatValue8);
      float floatValue27 = Draw2D.getTextWidth("\uf017", Fonts.SUPPLIER8, floatValue8);
      float floatValue28 = floatValue5
         + floatValue12
         + floatValue9
         + floatValue20
         + floatValue21
         + floatValue11
         + floatValue19
         + floatValue11
         + floatValue25
         + floatValue9
         + floatValue22
         + floatValue11
         + floatValue19
         + floatValue11
         + floatValue26
         + floatValue9
         + floatValue23
         + floatValue11
         + floatValue19
         + floatValue11
         + floatValue27
         + floatValue9
         + floatValue24
         + floatValue5;
      float floatValue29 = floatValue6 / 2.0F - 1.0F;
      Draw2D.m_7(floatValue3, floatValue4, floatValue28, floatValue6, floatValue29, floatValue29, floatValue29, floatValue29, 0.0F, 3.0F, ColorUtil.rgba(125, 125, 125, 255));
      float floatValue30 = floatValue3 + floatValue5;
      float floatValue31 = floatValue30;
      float floatValue32 = floatValue4 + (floatValue6 - floatValue12) / 2.0F;
      Draw2D.m_8(floatValue31 + 0.5F, floatValue32 + 1.0F, floatValue12 - 1.0F, floatValue12 - 2.0F, 4.0F, 0.5F, value6, value7);
      float floatValue33 = Draw2D.getCenteredY("\ue900", Fonts.SUPPLIER6, floatValue13, floatValue32, floatValue12);
      float floatValue34 = Draw2D.getCenteredX("\ue900", Fonts.SUPPLIER6, floatValue13, floatValue31, floatValue12);
      Draw2D.drawTextWithShadow("\ue900", Fonts.SUPPLIER6, floatValue34, floatValue33, ColorUtil.rgba(255, 255, 255, 255), floatValue13);
      floatValue30 += floatValue12 + floatValue9;
      float floatValue35 = (float)(System.currentTimeMillis() % 4000L) / 4000.0F;
      Draw2D.m_9("Moonward", Fonts.SUPPLIER18, floatValue30, floatValue14, value6, value7, floatValue7, floatValue35);
      floatValue30 += floatValue20;
      Draw2D.drawStyledText(" 26.2", Fonts.SUPPLIER18, floatValue30, floatValue14, value5, floatValue7);
      floatValue30 += floatValue21 + floatValue11;
      Draw2D.drawStyledText("•", Fonts.SUPPLIER18, floatValue30, floatValue18, value8, floatValue10);
      floatValue30 += floatValue19 + floatValue11;
      Draw2D.drawStyledText("\uf2bd", Fonts.SUPPLIER8, floatValue30, floatValue15, value4, floatValue8);
      floatValue30 += floatValue25 + floatValue9;
      Draw2D.drawStyledText(text3, Fonts.SUPPLIER18, floatValue30, floatValue14, value3, floatValue7);
      floatValue30 += floatValue22 + floatValue11;
      Draw2D.drawStyledText("•", Fonts.SUPPLIER18, floatValue30, floatValue18, value8, floatValue10);
      floatValue30 += floatValue19 + floatValue11;
      Draw2D.drawStyledText("\ue0f5", Fonts.SUPPLIER8, floatValue30, floatValue16, value4, floatValue8);
      floatValue30 += floatValue26 + floatValue9;
      Draw2D.drawStyledText(text4, Fonts.SUPPLIER18, floatValue30, floatValue14, value3, floatValue7);
      floatValue30 += floatValue23 + floatValue11;
      Draw2D.drawStyledText("•", Fonts.SUPPLIER18, floatValue30, floatValue18, value8, floatValue10);
      floatValue30 += floatValue19 + floatValue11;
      Draw2D.drawStyledText("\uf017", Fonts.SUPPLIER8, floatValue30, floatValue17, value4, floatValue8);
      floatValue30 += floatValue27 + floatValue9;
      Draw2D.drawStyledText(text5, Fonts.SUPPLIER18, floatValue30, floatValue14, value3, floatValue7);
      float floatValue36 = 0.0F;
      if (flag) {
         ServerData serverData = MinecraftAccess.MINECRAFT.getCurrentServer();
         String text6 = serverData != null ? serverData.ip : "Одиночная игра";
         PlayerInfo playerInfo = MinecraftAccess.MINECRAFT.getConnection() != null ? MinecraftAccess.MINECRAFT.getConnection().getPlayerInfo(MinecraftAccess.MINECRAFT.player.getUUID()) : null;
         String text7 = (playerInfo != null ? playerInfo.getLatency() : 0) + " ms";
         float floatValue37 = Draw2D.getTextWidth("\uf0ac", Fonts.SUPPLIER8, floatValue8);
         float floatValue38 = Draw2D.getTextWidth(text6, Fonts.SUPPLIER18, floatValue7);
         float floatValue39 = Draw2D.getTextWidth("\uf012", Fonts.SUPPLIER8, floatValue8);
         float floatValue40 = Draw2D.getTextWidth(text7, Fonts.SUPPLIER18, floatValue7);
         float floatValue41 = floatValue5 + floatValue37 + floatValue9 + floatValue38 + floatValue11 + floatValue19 + floatValue11 + floatValue39 + floatValue9 + floatValue40 + floatValue5;
         floatValue36 = 13.0F;
         float floatValue42 = floatValue4 + floatValue6 + 2.0F;
         float floatValue43 = Draw2D.getCenteredY("\uf0ac", Fonts.SUPPLIER8, floatValue8, floatValue42, floatValue36);
         float floatValue44 = Draw2D.getCenteredY("\uf012", Fonts.SUPPLIER8, floatValue8, floatValue42, floatValue36);
         float floatValue45 = Draw2D.getCenteredY("•", Fonts.SUPPLIER18, floatValue10, floatValue42, floatValue36);
         float floatValue46 = Draw2D.getTextBaseline(Fonts.SUPPLIER18, floatValue7, floatValue42, floatValue36);
         float floatValue47 = floatValue3 + (floatValue28 - floatValue41) / 2.0F;
         Draw2D.m_7(
            floatValue47,
            floatValue42,
            floatValue41,
            floatValue36,
            floatValue36 / 2.0F - 1.0F,
            floatValue36 / 2.0F - 1.0F,
            floatValue36 / 2.0F - 1.0F,
            floatValue36 / 2.0F - 1.0F,
            0.0F,
            3.0F,
            ColorUtil.rgba(165, 165, 165, 255)
         );
         float floatValue48 = floatValue47 + floatValue5;
         Draw2D.drawStyledText("\uf0ac", Fonts.SUPPLIER8, floatValue48, floatValue43, value4, floatValue8);
         floatValue48 += floatValue37 + floatValue9;
         Draw2D.drawStyledText(text6, Fonts.SUPPLIER18, floatValue48, floatValue46, value3, floatValue7);
         floatValue48 += floatValue38 + floatValue11;
         Draw2D.drawStyledText("•", Fonts.SUPPLIER18, floatValue48, floatValue45, value8, floatValue10);
         floatValue48 += floatValue19 + floatValue11;
         Draw2D.drawStyledText("\uf012", Fonts.SUPPLIER8, floatValue48, floatValue44, value4, floatValue8);
         floatValue48 += floatValue39 + floatValue9;
         Draw2D.drawStyledText(text7, Fonts.SUPPLIER18, floatValue48, floatValue46, value3, floatValue7);
      }

      this.textField2.m_8_void(floatValue28, floatValue6 + (flag ? floatValue36 + 2.0F : 0.0F));
   }

   private void renderTps(boolean flag) {
      String text3 = String.format(Locale.US, "%.1f TPS", ServerTpsTracker.SERVER_TPS_TRACKER.getTps());
      float floatValue3 = 16.0F;
      float floatValue4 = this.getBadgeWidth(text3);
      float floatValue5 = 4.0F;
      float floatValue6 = flag
         ? this.textField4.m_8_float() - floatValue3 * (Float)this.floatSetting.getValue() - 2.0F
         : MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() - floatValue3 * (Float)this.floatSetting.getValue() - 4.0F;
      this.textField3.m_2_void(floatValue5, floatValue6);
      this.applyScale(floatValue5, floatValue6);
      this.m_0(floatValue5, floatValue6, floatValue4, floatValue3, "\ue0a4", text3);
   }

   private void renderCoordinates() {
      String text3 = String.format(Locale.US, "%.0f %.0f %.0f", MinecraftAccess.MINECRAFT.player.getX(), MinecraftAccess.MINECRAFT.player.getY(), MinecraftAccess.MINECRAFT.player.getZ());
      float floatValue3 = 16.0F;
      float floatValue4 = this.getBadgeWidth(text3);
      float floatValue5 = 4.0F;
      float floatValue6 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight() - floatValue3 * (Float)this.floatSetting.getValue() - 4.0F;
      this.textField4.m_2_void(floatValue5, floatValue6);
      this.applyScale(floatValue5, floatValue6);
      this.m_0(floatValue5, floatValue6, floatValue4, floatValue3, "\uf14e", text3);
   }

   private float getBadgeWidth(String text3) {
      return 16.5F + Draw2D.getTextWidth(text3, Fonts.SUPPLIER18, 8.25F) + 3.0F;
   }

   private void m_0(float floatValue3, float floatValue4, float floatValue5, float floatValue6, String text3, String text4) {
      int value3 = ColorUtil.rgba(222, 222, 222, 255);
      int value4 = ColorUtil.primaryColor();
      int value5 = ColorUtil.secondaryColor();
      float floatValue7 = 8.25F;
      Draw2D.m_7(floatValue3, floatValue4, floatValue5, floatValue6, 4.0F, 4.0F, 4.0F, 4.0F, 0.0F, 3.0F, ColorUtil.rgba(125, 125, 125, 255));
      float floatValue8 = floatValue4 + (floatValue6 - 12.0F) / 2.0F;
      Draw2D.m_8(floatValue3 + 3.0F + 1.0F, floatValue8 + 1.25F, 10.0F, 10.0F, 3.0F, 0.5F, value4, value5);
      float floatValue9 = Draw2D.getCenteredX(text3, Fonts.SUPPLIER8, 8.0F, floatValue3 + 3.0F, 12.0F);
      float floatValue10 = Draw2D.getCenteredY(text3, Fonts.SUPPLIER8, 8.0F, floatValue8, 12.0F);
      Draw2D.drawTextWithShadow(text3, Fonts.SUPPLIER8, floatValue9, floatValue10, ColorUtil.rgba(255, 255, 255, 255), 8.0F);
      float floatValue11 = floatValue3 + 3.0F + 12.0F + 1.5F;
      float floatValue12 = Draw2D.getTextBaseline(Fonts.SUPPLIER18, floatValue7, floatValue4, floatValue6);
      Draw2D.drawStyledText(text4, Fonts.SUPPLIER18, floatValue11, floatValue12, value3, floatValue7);
   }

   private void m_4_void() {
      this.layoutField(this.textField6, 4.0F);
      float floatValue3 = this.textField6.m_5_float();
      float floatValue4 = this.textField6.m_8_float();
      this.applyScale(floatValue3, floatValue4);
      float floatValue5 = 4.0F;
      float floatValue6 = 15.0F;
      float floatValue7 = 10.0F;
      float floatValue8 = 9.0F;
      float floatValue9 = 10.0F;
      float floatValue10 = 3.0F;
      float floatValue11 = floatValue8 * 0.75F;
      float floatValue12 = floatValue11 + floatValue10 * 2.0F;
      float floatValue13 = Draw2D.getTextWidth("Эффекты", Fonts.SUPPLIER18, floatValue9);
      float floatValue14 = floatValue5 + floatValue13 + floatValue7 + floatValue12 + floatValue5;
      var mobEffectInstanceList = MinecraftAccess.MINECRAFT.player.getActiveEffects().stream().sorted(Comparator.reverseOrder()).toList();
      ArrayList<HUD.DataRecord> c0List = new ArrayList<>();

      for (MobEffectInstance mobEffectInstance : mobEffectInstanceList) {
         int value3 = mobEffectInstance.getAmplifier() + 1;
         String text3 = value3 <= 10 ? Component.translatable("enchantment.level." + value3).getString() : String.valueOf(value3);
         String text4 = mobEffectInstance.getEffect().value().getDisplayName().getString() + " " + text3;
         String text5 = mobEffectInstance.isInfiniteDuration() ? "**:**" : StringUtil.formatTickDuration(mobEffectInstance.getDuration(), MinecraftAccess.MINECRAFT.level.tickRateManager().tickrate());
         int value4 = mobEffectInstance.getAmplifier();
         int value5 = mobEffectInstance.isInfiniteDuration() ? -1 : mobEffectInstance.getDuration();
         HUD.DataRecord c0 = new HUD.DataRecord(mobEffectInstance.getDescriptionId(), text4, text5, true, this.getEffectSprite(mobEffectInstance.getEffect()), value4, value5);
         this.stringMap7.put(c0.m_4g(), c0);
         c0List.add(c0);
      }

      for (String text6 : List.copyOf(this.stringMap2.keySet())) {
         if (c0List.stream().noneMatch(c02 -> c02.m_4g().equals(text6))) {
            HUD.DataRecord c03 = this.stringMap7.get(text6);
            if (c03 != null) {
               c0List.add(new HUD.DataRecord(c03.m_4g(), c03.m_4_d_String(), c03.m_4_x(), false, c03.m_1q_TextureAtlasSprite(), c03.m_7e_int(), c03.m_7z()));
            }
         }
      }

      float floatValue15 = 11.5F;
      float floatValue16 = 2.0F;
      float floatValue17 = 8.25F;
      float floatValue18 = 10.0F;
      float floatValue19 = 2.0F;
      float floatValue20 = 0.0F;
      float floatValue21 = 0.0F;
      float floatValue22 = 0.0F;

      for (HUD.DataRecord c04 : c0List) {
         Animation animation12 = this.stringMap2.computeIfAbsent(c04.m_4g(), text7 -> new Animation(250L, 0.0F, Easing.EASING8));
         float floatValue23 = animation12.update(c04.m_68() ? 1.0F : 0.0F);
         if (floatValue23 < 0.01F) {
            if (!c04.m_68()) {
               this.stringMap2.remove(c04.m_4g());
               this.stringMap7.remove(c04.m_4g());
            }
         } else {
            float floatValue24 = Draw2D.getTextWidth(c04.m_4_d_String(), Fonts.SUPPLIER18, floatValue17);
            float floatValue25 = Theme.getLabelWidth(c04.m_4_x(), Fonts.SUPPLIER18);
            float floatValue26 = floatValue18 + floatValue19 + floatValue24 + 8.0F + floatValue25;
            if (floatValue26 > floatValue21) {
               floatValue21 = floatValue26;
            }

            floatValue20 += floatValue15 * floatValue23;
            if (floatValue23 > floatValue22) {
               floatValue22 = floatValue23;
            }
         }
      }

      float floatValue27 = Math.max(floatValue14, floatValue21 + floatValue5 * 2.0F);
      boolean flag = floatValue20 > 0.01F;
      float floatValue28 = 0.5F;
      float floatValue29 = floatValue20 + floatValue16 * floatValue22;
      float floatValue30 = this.animation2.update(floatValue27);
      if (!(floatValue30 < 5.0F)) {
         float floatValue31 = floatValue6 + (floatValue29 > 0.01F ? floatValue28 + floatValue29 : 0.0F);
         float floatValue32 = floatValue4 + floatValue6 + floatValue28;
         this.theme3.m_5(floatValue3, floatValue4, floatValue30, floatValue6, 4.0F, 4.0F, 0.0F, 0.0F);
         if (floatValue29 > 0.01F) {
            this.theme3.m_0(floatValue3, floatValue32, floatValue30, floatValue29, 0.0F, 0.0F, 4.0F, 4.0F);
         }

         this.hudStylePanel3.m_5(floatValue3, floatValue4, floatValue30, floatValue6, floatValue32, floatValue29);
         int value6 = this.theme3.m_8_int(255);
         int value7 = this.theme3.getAccentColor();
         int value8 = this.theme3.m_2_int();
         Draw2D.drawStyledText("Эффекты", Fonts.SUPPLIER18, floatValue3 + floatValue5, Draw2D.getTextBaseline(Fonts.SUPPLIER18, floatValue9, floatValue4, floatValue6), value6, floatValue9);
         float floatValue33 = floatValue3 + floatValue30 - floatValue5 - floatValue12;
         float floatValue34 = floatValue4 + (floatValue6 - floatValue12) / 2.0F;
         Draw2D.m_8(floatValue33 + 0.5F, floatValue34 + 1.0F, floatValue12 - 1.0F, floatValue12 - 2.0F, 4.0F, 0.5F, value7, value8);
         Draw2D.drawTextWithShadow(
            "\uf0c3",
            Fonts.SUPPLIER8,
            Draw2D.getCenteredX("\uf0c3", Fonts.SUPPLIER8, floatValue11, floatValue33, floatValue12),
            Draw2D.getCenteredY("\uf0c3", Fonts.SUPPLIER8, floatValue11, floatValue34, floatValue12),
            ColorUtil.rgba(255, 255, 255, 255),
            floatValue11
         );
         if (flag) {
            float floatValue35 = floatValue32 + (floatValue29 - floatValue20) / 2.0F;
            Render2D.pushScissor(floatValue3, floatValue32, floatValue30, floatValue29);

            for (HUD.DataRecord c05 : c0List) {
               Animation animation13 = this.stringMap2.get(c05.m_4g());
               if (animation13 != null) {
                  float floatValue36 = animation13.update(c05.m_68() ? 1.0F : 0.0F);
                  if (!(floatValue36 < 0.01F)) {
                     String text8 = c05.m_4_d_String();
                     String text9 = c05.m_4_x();
                     int value9 = (int)(255.0F * floatValue36);
                     int value10 = c05.m_7z();
                     boolean flag2 = c05.m_68() && value10 > 0 && value10 < 200;
                     if (flag2) {
                        double doubleValue = value10 < 100 ? 90.0 : 180.0;
                        float floatValue37 = (float)(0.75 + 0.25 * Math.sin(System.currentTimeMillis() / doubleValue));
                        value9 = (int)(value9 * floatValue37);
                     }

                     int value11 = c05.m_7e_int() >= 2 ? ColorUtil.rgba(255, 85, 85, 255) : this.theme3.m_8_int(255);
                     int value12 = flag2 ? ColorUtil.rgba(255, 85, 85, 255) : this.theme3.m_3_int();
                     int value13 = flag2 ? ColorUtil.rgba(255, 85, 85, 51) : ColorUtil.rgba(255, 255, 255, 26);
                     float floatValue38 = floatValue15 * floatValue36;
                     float floatValue39 = floatValue35 - (1.0F - floatValue36) * floatValue15;
                     float floatValue40 = Draw2D.getTextBaseline(Fonts.SUPPLIER18, floatValue17, floatValue39, floatValue38);
                     float floatValue41 = floatValue3 + floatValue5;
                     float floatValue42 = floatValue39 + (floatValue38 - floatValue18) / 2.0F;
                     if (c05.m_1q_TextureAtlasSprite() != null) {
                        TextureAtlas textureAtlas = MinecraftAccess.MINECRAFT.getAtlasManager().getAtlasOrThrow(IDENTIFIER2);
                        Draw2D.m_0(
                           floatValue41,
                           floatValue42,
                           floatValue18,
                           floatValue18,
                           ColorUtil.rgba(255, 255, 255, value9),
                           textureAtlas.getTextureView(),
                           c05.m_1q_TextureAtlasSprite().getU0(),
                           c05.m_1q_TextureAtlasSprite().getV0(),
                           c05.m_1q_TextureAtlasSprite().getU1(),
                           c05.m_1q_TextureAtlasSprite().getV1()
                        );
                     }

                     float floatValue43 = floatValue41 + floatValue18 + floatValue19;
                     Draw2D.drawStyledText(text8, Fonts.SUPPLIER18, floatValue43, floatValue40, Theme.scaleAlpha(value11, value9), floatValue17);
                     if (this.hudStylePanel3.getActiveColor() == 2) {
                        Theme.m_3(floatValue43, floatValue39 + floatValue38 - 1.0F, Draw2D.getTextWidth(text8, Fonts.SUPPLIER18, floatValue17), ColorUtil.rgba(255, 255, 255, 230));
                     }

                     Theme.m_2(text9, Fonts.SUPPLIER18, floatValue3 + floatValue30 - floatValue5, floatValue39, floatValue38, value13, value12, value9, this.hudStylePanel3.getActiveColor() == 3);
                     floatValue35 += floatValue15 * floatValue36;
                  }
               }
            }

            Render2D.popScissor();
         }

         this.textField6.m_8_void(floatValue30, floatValue31);
      }
   }

   private void m_2_void() {
      this.layoutField(this.textField5, 4.0F);
      float floatValue3 = this.textField5.m_5_float();
      float floatValue4 = this.textField5.m_8_float();
      this.applyScale(floatValue3, floatValue4);
      float floatValue5 = 4.0F;
      float floatValue6 = 15.0F;
      float floatValue7 = 10.0F;
      float floatValue8 = 9.0F;
      float floatValue9 = 10.0F;
      float floatValue10 = 3.0F;
      float floatValue11 = floatValue8 * 0.75F;
      float floatValue12 = floatValue11 + floatValue10 * 2.0F;
      ArrayList<HUD.DataRecord2> c3List = new ArrayList<>();

      for (Module module : Onetap.MODULE_MANAGER.getModules()) {
         if (module.getKey() != -1) {
            float floatValue13 = module.getAnimationProgress();
            if (floatValue13 >= 0.005F || module.isEnabled()) {
               c3List.add(new HUD.DataRecord2(module.getName(), module.getKey(), floatValue13));
            }
         }

         for (Setting setting : module.getSettings()) {
            if (setting instanceof BooleanSetting booleanSetting6 && booleanSetting6.getKey() != -1) {
               boolean flag = module.isEnabled() && (Boolean)booleanSetting6.getValue();
               float floatValue14 = this.booleanSettingMap.computeIfAbsent(booleanSetting6, booleanSetting7 -> new Animation(250L, 0.0F, Easing.EASING8)).update(flag ? 1.0F : 0.0F);
               if (floatValue14 >= 0.005F || flag) {
                  c3List.add(new HUD.DataRecord2(module.getName() + " -> " + booleanSetting6.getName(), booleanSetting6.getKey(), floatValue14));
               }
            }
         }
      }

      float floatValue15 = 11.5F;
      float floatValue16 = 2.0F;
      float floatValue17 = 8.25F;
      float floatValue18 = 0.0F;
      float floatValue19 = 0.0F;
      float floatValue20 = 0.0F;

      for (HUD.DataRecord2 c3 : c3List) {
         float floatValue21 = c3.m_90();
         float floatValue22 = Draw2D.getTextWidth(c3.m_47(), Fonts.SUPPLIER18, floatValue17);
         float floatValue23 = Theme.getLabelWidth(KeyNames.getKeyName(c3.m_6l()), Fonts.SUPPLIER18);
         float floatValue24 = floatValue22 + 8.0F + floatValue23;
         if (floatValue24 > floatValue19) {
            floatValue19 = floatValue24;
         }

         floatValue18 += floatValue15 * floatValue21;
         if (floatValue21 > floatValue20) {
            floatValue20 = floatValue21;
         }
      }

      float floatValue25 = Draw2D.getTextWidth("Клавиши", Fonts.SUPPLIER18, floatValue9);
      float floatValue26 = floatValue5 + floatValue25 + floatValue7 + floatValue12 + floatValue5;
      float floatValue27 = Math.max(floatValue26, floatValue19 + floatValue5 * 2.0F);
      boolean flag2 = floatValue18 > 0.01F;
      float floatValue28 = 0.5F;
      float floatValue29 = floatValue18 + floatValue16 * floatValue20;
      float floatValue30 = this.animation.update(floatValue27);
      if (!(floatValue30 < 5.0F)) {
         float floatValue31 = floatValue6 + (floatValue29 > 0.01F ? floatValue28 + floatValue29 : 0.0F);
         float floatValue32 = floatValue4 + floatValue6 + floatValue28;
         this.theme4.m_5(floatValue3, floatValue4, floatValue30, floatValue6, 4.0F, 4.0F, 0.0F, 0.0F);
         if (floatValue29 > 0.01F) {
            this.theme4.m_0(floatValue3, floatValue32, floatValue30, floatValue29, 0.0F, 0.0F, 4.0F, 4.0F);
         }

         this.hudStylePanel4.m_5(floatValue3, floatValue4, floatValue30, floatValue6, floatValue32, floatValue29);
         int value3 = this.theme4.m_8_int(255);
         int value4 = this.theme4.getAccentColor();
         int value5 = this.theme4.m_2_int();
         Draw2D.drawStyledText("Клавиши", Fonts.SUPPLIER18, floatValue3 + floatValue5, Draw2D.getTextBaseline(Fonts.SUPPLIER18, floatValue9, floatValue4, floatValue6), value3, floatValue9);
         float floatValue33 = floatValue3 + floatValue30 - floatValue5 - floatValue12;
         float floatValue34 = floatValue4 + (floatValue6 - floatValue12) / 2.0F;
         Draw2D.m_8(floatValue33 + 0.5F, floatValue34 + 1.0F, floatValue12 - 1.0F, floatValue12 - 2.0F, 4.0F, 0.5F, value4, value5);
         Draw2D.drawTextWithShadow(
            "\uf11c",
            Fonts.SUPPLIER8,
            Draw2D.getCenteredX("\uf11c", Fonts.SUPPLIER8, floatValue11, floatValue33, floatValue12),
            Draw2D.getCenteredY("\uf11c", Fonts.SUPPLIER8, floatValue11, floatValue34, floatValue12),
            ColorUtil.rgba(255, 255, 255, 255),
            floatValue11
         );
         if (flag2) {
            float floatValue35 = floatValue32 + (floatValue29 - floatValue18) / 2.0F;
            Render2D.pushScissor(floatValue3, floatValue32, floatValue30, floatValue29);

            for (HUD.DataRecord2 c32 : c3List) {
               float floatValue36 = c32.m_90();
               String text3 = c32.m_47();
               int value6 = (int)(255.0F * floatValue36);
               float floatValue37 = floatValue35 - (1.0F - floatValue36) * floatValue15;
               float floatValue38 = Draw2D.getTextBaseline(Fonts.SUPPLIER18, floatValue17, floatValue37, floatValue15 * floatValue36);
               Draw2D.drawStyledText(text3, Fonts.SUPPLIER18, floatValue3 + floatValue5, floatValue38, this.theme4.m_8_int(value6), floatValue17);
               if (this.hudStylePanel4.getActiveColor() == 2) {
                  Theme.m_3(floatValue3 + floatValue5, floatValue37 + floatValue15 * floatValue36 - 1.0F, Draw2D.getTextWidth(text3, Fonts.SUPPLIER18, floatValue17), ColorUtil.rgba(255, 255, 255, 230));
               }

               Theme.m_2(
                  KeyNames.getKeyName(c32.m_6l()),
                  Fonts.SUPPLIER18,
                  floatValue3 + floatValue30 - floatValue5,
                  floatValue37,
                  floatValue15 * floatValue36,
                  ColorUtil.rgba(255, 255, 255, 26),
                  this.theme4.m_3_int(),
                  value6,
                  this.hudStylePanel4.getActiveColor() == 3
               );
               floatValue35 += floatValue15 * floatValue36;
            }

            Render2D.popScissor();
         }

         this.textField5.m_8_void(floatValue30, floatValue31);
      }
   }

   private void m_3(Render2DEvent event) {
      Assistent assistent = Onetap.MODULE_MANAGER.get(Assistent.class);
      List list = assistent.isEnabled() ? assistent.m_3_List() : List.of();
      if (list.isEmpty()) {
         this.textField7.m_8_void(0.0F, 0.0F);
      } else {
         this.layoutField(this.textField7, 4.0F);
         float floatValue3 = this.textField7.m_5_float();
         float floatValue4 = this.textField7.m_8_float();
         this.applyScale(floatValue3, floatValue4);
         float floatValue5 = 3.0F;
         float floatValue6 = 3.0F;
         float floatValue7 = 2.0F;
         float floatValue8 = 16.0F;
         float floatValue9 = 12.0F;
         float floatValue10 = 8.25F;
         int value3 = ColorUtil.rgba(222, 222, 222, 255);
         int value4 = ColorUtil.primaryColor();
         int value5 = ColorUtil.rgba(255, 255, 255, 255);
         Supplier supplier = Fonts.SUPPLIER18;
         float floatValue11 = (MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth() - floatValue3 - 4.0F) / (Float)this.floatSetting.getValue();
         float floatValue12 = floatValue3;
         float floatValue13 = floatValue4;
         float floatValue14 = 0.0F;
         float floatValue15 = 0.0F;

         for (Assistent.DataRecord c1 : (Iterable<Assistent.DataRecord>)list) {
            String text3 = KeyNames.getKeyName(c1.m_9_i_int());
            String text4 = "[" + c1.m_9e() + "]";
            float floatValue16 = Draw2D.getTextWidth(text3, supplier, floatValue10);
            float floatValue17 = floatValue5 + floatValue9 + floatValue6 + floatValue16 + floatValue6 + Draw2D.getTextWidth(text4, supplier, floatValue10) + floatValue5;
            if (floatValue12 != floatValue3 && floatValue12 + floatValue17 - floatValue3 > floatValue11) {
               floatValue12 = floatValue3;
               floatValue13 += floatValue8 + floatValue7 + floatValue15;
               floatValue15 = 0.0F;
            }

            float floatValue18 = assistent.m_1(c1.m_4o(), c1.m_7w());
            String text5 = c1.m_4o().getDescriptionId() + ":" + c1.m_7w();
            float floatValue19 = this.stringMap3.computeIfAbsent(text5, text6 -> new Animation(180L, 0.0F, Easing.EASING8)).update(floatValue18 > 0.0F ? 1.0F : 0.0F);
            float floatValue20 = 12.0F * floatValue19;
            floatValue15 = Math.max(floatValue15, floatValue20);
            int value6 = Math.max(0, (int)Math.ceil(floatValue18));
            String text7 = value6 + " с";
            String text8 = this.stringMap5.getOrDefault(text5, "");
            if (!text7.equals(text8)) {
               this.stringMap6.put(text5, text8.isEmpty() ? text7 : text8);
               this.stringMap5.put(text5, text7);
               this.stringMap4.computeIfAbsent(text5, text9 -> new Animation(120L, 1.0F, Easing.EASING8)).setDuration(0.0F);
            }

            float floatValue21 = this.stringMap4.computeIfAbsent(text5, text10 -> new Animation(120L, 1.0F, Easing.EASING8)).update(1.0F);
            Draw2D.m_7(floatValue12, floatValue13, floatValue17, floatValue8, 4.0F, 4.0F, 4.0F, 4.0F, 0.0F, 3.0F, ColorUtil.rgba(125, 125, 125, 255));
            ItemStack itemStack2 = c1.m_4_ItemStack();
            TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
            MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, itemStack2, ItemDisplayContext.GUI, MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, itemStack2.hashCode());
            event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
            Render2D.drawItem(trackingItemStackRenderState, floatValue12 + floatValue5, floatValue13 + (floatValue8 - floatValue9) / 2.0F, floatValue9, value5);
            float floatValue22 = Draw2D.getTextBaseline(supplier, floatValue10, floatValue13, floatValue8);
            float floatValue23 = floatValue12 + floatValue5 + floatValue9 + floatValue6;
            Draw2D.drawStyledText(text3, supplier, floatValue23, floatValue22, value3, floatValue10);
            Draw2D.drawStyledText(text4, supplier, floatValue23 + floatValue16 + floatValue6, floatValue22, value4, floatValue10);
            if (floatValue19 > 0.01F) {
               float floatValue24 = 7.0F;
               String text11 = this.stringMap6.getOrDefault(text5, text7);
               float floatValue25 = Draw2D.getTextWidth(text7, supplier, floatValue24);
               float floatValue26 = Draw2D.getTextWidth(text11, supplier, floatValue24);
               float floatValue27 = Math.max(floatValue25, floatValue26) + 10.0F;
               float floatValue28 = floatValue12 + (floatValue17 - floatValue27) / 2.0F;
               float floatValue29 = floatValue13 + floatValue8 + 1.0F;
               float floatValue30 = 10.0F * floatValue19;
               int value7 = (int)(255.0F * floatValue19);
               if (floatValue30 > 2.0F) {
                  Draw2D.m_7(floatValue28, floatValue29, floatValue27, floatValue30, 4.0F, 4.0F, 4.0F, 4.0F, 0.0F, 3.0F, ColorUtil.rgba(125, 125, 125, (int)(255.0F * floatValue19)));
               }

               float floatValue31 = Draw2D.getTextBaseline(supplier, floatValue24, floatValue29, floatValue30);
               Render2D.pushScissor(floatValue28, floatValue29, floatValue27, floatValue30);
               if (floatValue21 < 0.999F && !text11.equals(text7)) {
                  int value8 = (int)(value7 * (1.0F - floatValue21));
                  Draw2D.drawStyledText(text11, supplier, floatValue28 + (floatValue27 - floatValue26) / 2.0F, floatValue31 + Draw2D.getFontHeight(supplier, floatValue24) * floatValue21, ColorUtil.rgba(222, 222, 222, value8), floatValue24);
                  Draw2D.drawStyledText(
                     text7, supplier, floatValue28 + (floatValue27 - floatValue25) / 2.0F, floatValue31 - Draw2D.getFontHeight(supplier, floatValue24) * (1.0F - floatValue21), ColorUtil.rgba(222, 222, 222, value7), floatValue24
                  );
               } else {
                  Draw2D.drawStyledText(text7, supplier, floatValue28 + (floatValue27 - floatValue25) / 2.0F, floatValue31, ColorUtil.rgba(222, 222, 222, value7), floatValue24);
               }

               Render2D.popScissor();
            }

            floatValue12 += floatValue17 + floatValue6;
            floatValue14 = Math.max(floatValue14, floatValue12 - floatValue6 - floatValue3);
         }

         this.textField7.m_8_void(floatValue14, floatValue13 - floatValue4 + floatValue8 + floatValue15);
      }
   }

   private HudStylePanel getActivePanel() {
      return this.isMoonwardModeSetting() ? this.hudStylePanel : (this.isLiquidModeSetting() ? this.hudStylePanel2 : null);
   }

   private boolean isMoonwardModeSetting() {
      return ((String)this.modeSetting.getValue()).equals("Moonward");
   }

   private boolean isCircleMode() {
      return ((String)this.modeSetting2.getValue()).equals("Кружок");
   }

   private boolean isRichPremiumModeSetting() {
      return ((String)this.modeSetting.getValue()).equals("Rich Premium");
   }

   private boolean isLiquidModeSetting() {
      return ((String)this.modeSetting.getValue()).equals("Liquid");
   }

   private void renderTargetHud(Render2DEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         boolean flag = MinecraftAccess.MINECRAFT.gui.screen() instanceof ChatScreen;
         LivingEntity livingEntity2 = flag ? MinecraftAccess.MINECRAFT.player : KillAura.livingEntity;
         if (livingEntity2 == null && !flag && MinecraftAccess.MINECRAFT.crosshairPickEntity instanceof LivingEntity livingEntity3 && livingEntity3.isAlive()) {
            livingEntity2 = livingEntity3;
         }

         boolean flag2 = this.multiModeSetting.isSelected("Таргет худ") && livingEntity2 != null && livingEntity2.isAlive();
         float floatValue3 = this.animation7.update(flag2 ? 1.0F : 0.0F);
         if (floatValue3 < 0.01F) {
            this.livingEntity = null;
            this.text = "";
            this.text2 = "";
            this.animation11.setDuration(1.0F);
            this.c1List.clear();
            this.value = -1;
            this.floatValue = -1.0F;
            this.floatValue2 = -1.0F;
         } else {
            if (flag2) {
               this.livingEntity = livingEntity2;
            }

            if (this.livingEntity != null) {
               this.layoutField(this.textField9, this.isMoonwardModeSetting() ? 6.0F : (this.isLiquidModeSetting() ? 6.0F : 0.0F));
               float floatValue4 = this.textField9.m_5_float();
               float floatValue5 = this.textField9.m_8_float();
               this.applyScale(floatValue4, floatValue5);
               int value3 = (int)(255.0F * floatValue3);
               LivingEntity livingEntity4 = this.livingEntity;
               float floatValue6 = 3.0F;
               float floatValue7 = 5.0F;
               float floatValue8 = 22.0F;
               float floatValue9 = 21.0F;
               float floatValue10 = 8.5F;
               float floatValue11 = 7.5F;
               float floatValue12 = 6.0F;
               float floatValue13 = Math.max(livingEntity4.getMaxHealth(), 1.0F);
               ScoreboardHealth scoreboardHealth = Onetap.MODULE_MANAGER.get(ScoreboardHealth.class);
               boolean flag3 = livingEntity4 instanceof Player && scoreboardHealth.isEnabled();
               float floatValue14 = flag3 ? scoreboardHealth.m_0_float(livingEntity4) : livingEntity4.getHealth();
               float floatValue15 = Math.max(0.0F, livingEntity4.getAbsorptionAmount());
               float floatValue16 = flag3 ? Math.min(floatValue13, 20.0F) : floatValue13;
               float floatValue17 = Math.min(floatValue14 / floatValue16, 1.0F);
               NameProtect nameProtect = Onetap.MODULE_MANAGER.get(NameProtect.class);
               String text3 = nameProtect != null && nameProtect.isEnabled() ? nameProtect.replaceInText(livingEntity4.getName().getString()) : livingEntity4.getName().getString();
               if (this.isRichPremiumModeSetting()) {
                  this.m_2(event, livingEntity4, floatValue4, floatValue5, floatValue3, value3, floatValue14, floatValue16, floatValue15, text3);
               } else {
                  boolean flag4 = this.isMoonwardModeSetting() && this.isCircleMode();
                  String text4 = flag4 ? String.valueOf(Math.round(floatValue14)) : String.format(Locale.US, "%.1f", floatValue14);
                  if (!text4.equals(this.text)) {
                     this.text2 = this.text.isEmpty() ? text4 : this.text;
                     this.text = text4;
                     this.animation11.setDuration(0.0F);
                  }

                  float floatValue18 = this.animation11.update(1.0F);
                  if (this.isLiquidModeSetting()) {
                     this.m_7(event, livingEntity4, floatValue4, floatValue5, floatValue3, value3, floatValue14, floatValue16, floatValue15, text3, floatValue18);
                  } else {
                     float floatValue19 = 70.0F;
                     if (flag4) {
                        this.m_6(event, livingEntity4, floatValue4, floatValue5, floatValue3, value3, floatValue6, floatValue7, floatValue8, floatValue9, floatValue14, floatValue16, floatValue15, text3, floatValue10, floatValue18);
                     } else {
                        float floatValue20 = floatValue6 + floatValue8 + floatValue7 + floatValue19 + floatValue6;
                        float floatValue21 = floatValue6 * 2.0F + floatValue8;
                        this.m_3(event, livingEntity4, floatValue4, floatValue5, floatValue3);
                        this.m_9(event, livingEntity4, floatValue4, floatValue5, floatValue20, floatValue21, floatValue6, floatValue8, floatValue9, value3, floatValue3);
                        float floatValue22 = floatValue4 + floatValue6 + floatValue8 + floatValue7 - 3.0F;
                        float floatValue23 = floatValue5 + floatValue6 + 4.5F;
                        Draw2D.drawColoredTextRaw(text3, Fonts.SUPPLIER18, floatValue22, floatValue23 - 0.5F - 1.0F, this.theme.m_8_int(value3), 0.2F, 0.8F, floatValue19, floatValue10);
                        if (this.hudStylePanel.getActiveColor() == 2) {
                           Theme.m_3(floatValue22, floatValue23 + 8.0F, Math.min(Draw2D.getTextWidth(text3, Fonts.SUPPLIER18, floatValue10), floatValue19), ColorUtil.rgba(255, 255, 255, 230));
                        }

                        float floatValue24 = floatValue5 + floatValue21 - floatValue6 - floatValue12 - 1.0F;
                        float floatValue25 = this.animation8.update(floatValue17);
                        int[] intArray = this.theme.m_8_IArr(this.getHealthColor(floatValue17));
                        int value4 = intArray[0];
                        int value5 = intArray[1];
                        int value6 = ColorUtil.rgba(0, 0, 0, 255);
                        if (this.hudStylePanel.getActiveColor() == 3) {
                           Theme.m_5(floatValue22 - 0.5F, floatValue24 - 0.5F, floatValue19 + 1.0F, floatValue12 + 1.0F, ColorUtil.rgba(255, 255, 255, 230));
                        }

                        Draw2D.m_7(
                           floatValue22, floatValue24, floatValue19, floatValue12, 2.5F, 0.0F, ColorUtil.withAlpha(ColorUtil.lerpColor(value6, value5, 0.32F), value3), ColorUtil.withAlpha(ColorUtil.lerpColor(value6, value4, 0.32F), value3)
                        );
                        float floatValue26 = floatValue19 * floatValue25;
                        if (floatValue25 > 0.01F) {
                           int value7 = (int)(110.0F * floatValue3);
                           Draw2D.m_6(floatValue22 + 0.5F, floatValue24 + 0.5F, floatValue26 - 1.0F, floatValue12 - 1.0F, 2.5F, 2.5F, ColorUtil.withAlpha(value5, value7), ColorUtil.withAlpha(value4, value7));
                           Draw2D.m_7(floatValue22, floatValue24, floatValue26, floatValue12, 2.5F, 0.0F, ColorUtil.withAlpha(value5, value3), ColorUtil.withAlpha(value4, value3));
                        }

                        int value8 = ColorUtil.rgba(255, 200, 80, 255);
                        int value9 = ColorUtil.rgba(155, 100, 0, 255);
                        float floatValue27 = this.animation10.update(floatValue15 > 0.0F ? 1.0F : 0.0F) * floatValue3;
                        float floatValue28 = Math.min(floatValue15 / floatValue16, 1.0F);
                        float floatValue29 = this.animation9.update(floatValue28);
                        int value10 = livingEntity4.getId();
                        if (this.value != value10) {
                           this.value = value10;
                           this.floatValue = floatValue14;
                           this.floatValue2 = floatValue15;
                           this.c1List.clear();
                        } else {
                           if (this.floatValue > 0.0F && floatValue14 < this.floatValue - 0.01F) {
                              if ((Boolean)this.booleanSetting.getValue()) {
                                 this.m_7(floatValue26, floatValue12, true, value4);
                              }

                              this.floatValue = floatValue14;
                           } else if (floatValue14 > this.floatValue) {
                              this.floatValue = floatValue14;
                           }

                           if (this.floatValue2 > 0.0F && floatValue15 < this.floatValue2 - 0.01F) {
                              if ((Boolean)this.booleanSetting.getValue()) {
                                 this.m_7(floatValue19 * (1.0F - floatValue29) + 0.5F, floatValue12, true, value8);
                              }

                              this.floatValue2 = floatValue15;
                           } else if (floatValue15 > this.floatValue2) {
                              this.floatValue2 = floatValue15;
                           }
                        }

                        long longValue4 = System.currentTimeMillis();
                        if ((Boolean)this.booleanSetting.getValue() && floatValue25 > floatValue17 + 0.008F && longValue4 - this.longValue >= 22L) {
                           this.longValue = longValue4;
                           this.m_7(floatValue26, floatValue12, false, value4);
                        }

                        if ((Boolean)this.booleanSetting.getValue() && floatValue27 > 0.01F && floatValue29 > floatValue28 + 0.008F && longValue4 - this.longValue2 >= 22L) {
                           this.longValue2 = longValue4;
                           this.m_7(floatValue19 * (1.0F - floatValue29) + 0.5F, floatValue12, false, value8);
                        }

                        if (floatValue27 > 0.01F && floatValue29 > 0.001F) {
                           int value11 = (int)(255.0F * floatValue27);
                           int value12 = (int)(155.0F * floatValue27);
                           float floatValue30 = floatValue22 + floatValue19 * (1.0F - floatValue29) + 0.5F;
                           float floatValue31 = floatValue19 * floatValue29;
                           float floatValue32 = floatValue24 - 0.125F;
                           float floatValue33 = floatValue12 + 0.25F;
                           Draw2D.m_5(floatValue30 + 0.5F, floatValue32 + 0.5F, floatValue31 - 1.0F, floatValue33 - 1.0F, 2.5F, 4.0F, ColorUtil.withAlpha(value8, value12), ColorUtil.withAlpha(value9, value12));
                           Draw2D.m_5(floatValue30, floatValue32, floatValue31, floatValue33, 2.5F, ColorUtil.withAlpha(value8, value11), ColorUtil.withAlpha(value9, value11));
                        }

                        this.m_8(floatValue22, floatValue24, floatValue3, longValue4);
                        this.m_3(floatValue22 + floatValue19, floatValue24 - Draw2D.getFontHeight(Fonts.SUPPLIER18, floatValue11) - 1.25F - 1.0F, floatValue11, false, this.theme.m_8_int(255), value3, floatValue18);
                        this.textField9.m_8_void(floatValue20, floatValue21);
                     }
                  }
               }
            }
         }
      }
   }

   private void m_6(
      Render2DEvent event,
      LivingEntity livingEntity2,
      float floatValue3,
      float floatValue4,
      float floatValue5,
      int value3,
      float floatValue6,
      float floatValue7,
      float floatValue8,
      float floatValue9,
      float floatValue10,
      float floatValue11,
      float floatValue12,
      String text3,
      float floatValue13,
      float floatValue14
   ) {
      float floatValue15 = 70.0F;
      float floatValue16 = floatValue3 + floatValue6 + floatValue8 + floatValue7 - 3.0F;
      float floatValue17 = floatValue16 - floatValue3 + floatValue15 + floatValue6;
      float floatValue18 = floatValue6 * 2.0F + floatValue8;
      this.m_9(event, livingEntity2, floatValue3, floatValue4, floatValue17, floatValue18, floatValue6, floatValue8, floatValue9, value3, floatValue5);
      float floatValue19 = floatValue4 + floatValue6;
      float floatValue20 = floatValue8;
      Draw2D.drawColoredTextRaw(text3, Fonts.SUPPLIER18, floatValue16, floatValue19 + 1.5F, this.theme.m_8_int(value3), 0.2F, 0.8F, floatValue15, floatValue13);
      if (this.hudStylePanel.getActiveColor() == 2) {
         Theme.m_3(floatValue16, floatValue19 + 11.0F, Math.min(Draw2D.getTextWidth(text3, Fonts.SUPPLIER18, floatValue13), floatValue15), ColorUtil.rgba(255, 255, 255, 230));
      }

      float floatValue21 = 6.0F;
      float floatValue22 = floatValue4 + floatValue18 - floatValue6 - floatValue21 - 1.0F;
      float floatValue23 = 8.0F;
      float floatValue24 = floatValue22 + (floatValue21 - floatValue23) * 0.5F;
      this.m_2(event, livingEntity2, floatValue16, floatValue24, floatValue23, 8.0F, false, floatValue5);
      float floatValue25 = 8.0F;
      float floatValue26 = 1.5F;
      float floatValue27 = floatValue16 + floatValue15 - floatValue25 - 2.0F;
      float floatValue28 = floatValue19 + floatValue20 * 0.5F + 0.5F;
      float floatValue29 = Math.min(floatValue10 / floatValue11, 1.0F);
      float floatValue30 = this.animation8.update(floatValue29);
      int[] intArray = this.theme.m_8_IArr(this.getHealthColor(floatValue29));
      int value4 = intArray[0];
      int value5 = intArray[1];
      if (this.hudStylePanel.getActiveColor() == 3) {
         Theme.m_5(floatValue27 - floatValue25 - 1.5F, floatValue28 - floatValue25 - 1.5F, floatValue25 * 2.0F + 3.0F, floatValue25 * 2.0F + 3.0F, ColorUtil.rgba(255, 255, 255, 230));
      }

      float floatValue31 = 0.0F;
      float floatValue32 = (float) (Math.PI * 2);
      Draw2D.m_5(floatValue27, floatValue28, floatValue25, floatValue26, 1.0F, floatValue31, 0.0F, ColorUtil.rgba(255, 255, 255, (int)(18.0F * floatValue5)));
      if (floatValue30 > 0.001F) {
         float floatValue33 = floatValue31 - floatValue30 * floatValue32;
         int value6 = (int)(110.0F * floatValue5);
         Draw2D.m_0(floatValue27, floatValue28, floatValue25, floatValue26 + 1.0F, floatValue30, floatValue33, 1.0F, ColorUtil.withAlpha(value5, value6), ColorUtil.withAlpha(value4, value6));
         Draw2D.m_0(floatValue27, floatValue28, floatValue25, floatValue26, floatValue30, floatValue33, 0.0F, ColorUtil.withAlpha(value5, value3), ColorUtil.withAlpha(value4, value3));
      }

      float floatValue34 = this.animation10.update(floatValue12 > 0.0F ? 1.0F : 0.0F) * floatValue5;
      float floatValue35 = this.animation9.update(Math.min(floatValue12 / floatValue11, 1.0F));
      if (floatValue34 > 0.01F && floatValue35 > 0.001F) {
         float floatValue36 = floatValue31 - floatValue35 * floatValue32;
         int value7 = (int)(255.0F * floatValue34);
         int value8 = (int)(155.0F * floatValue34);
         Draw2D.m_5(floatValue27, floatValue28, floatValue25, floatValue26, floatValue35, floatValue36, 1.0F, ColorUtil.withAlpha(ColorUtil.rgba(255, 200, 80, 255), value8));
         Draw2D.m_5(floatValue27, floatValue28, floatValue25, floatValue26, floatValue35, floatValue36, 0.0F, ColorUtil.withAlpha(ColorUtil.rgba(255, 200, 80, 255), value7));
      }

      this.m_3(floatValue27, Draw2D.getTextBaseline(Fonts.SUPPLIER18, 8.0F, floatValue28 - floatValue25, floatValue25 * 2.0F), 8.0F, true, this.theme.m_8_int(255), value3, floatValue14);
      this.textField9.m_8_void(floatValue17, floatValue18);
   }

   private void m_3(float floatValue3, float floatValue4, float floatValue5, boolean flag, int value3, int value4, float floatValue6) {
      float floatValue7 = Draw2D.getFontHeight(Fonts.SUPPLIER18, floatValue5);
      float floatValue8 = Draw2D.getTextWidth(this.text, Fonts.SUPPLIER18, floatValue5);
      float floatValue9 = this.text2.isEmpty() ? floatValue8 : Draw2D.getTextWidth(this.text2, Fonts.SUPPLIER18, floatValue5);
      float floatValue10 = Math.max(floatValue8, floatValue9) + 2.0F;
      float floatValue11 = floatValue7 + 1.0F;
      float floatValue12 = flag ? floatValue3 - floatValue8 * 0.5F : floatValue3 - floatValue8;
      float floatValue13 = flag ? floatValue3 - floatValue9 * 0.5F : floatValue3 - floatValue9;
      Render2D.pushScissor(flag ? floatValue3 - floatValue10 * 0.5F : floatValue3 - floatValue10, floatValue4 - 0.5F, floatValue10 + 1.0F, floatValue7 + 1.0F);
      if (floatValue6 < 0.999F && !this.text2.isEmpty() && !this.text2.equals(this.text)) {
         int value5 = (int)(value4 * (1.0F - floatValue6));
         Draw2D.drawStyledText(this.text2, Fonts.SUPPLIER18, floatValue13, floatValue4 + floatValue11 * floatValue6, ColorUtil.withAlpha(value3, value5), floatValue5);
         Draw2D.drawStyledText(this.text, Fonts.SUPPLIER18, floatValue12, floatValue4 - floatValue11 * (1.0F - floatValue6), ColorUtil.withAlpha(value3, value4), floatValue5);
      } else {
         Draw2D.drawStyledText(this.text, Fonts.SUPPLIER18, floatValue12, floatValue4, ColorUtil.withAlpha(value3, value4), floatValue5);
      }

      Render2D.popScissor();
   }

   private void m_2(Render2DEvent event, LivingEntity livingEntity2, float floatValue3, float floatValue4, float floatValue5, int value3, float floatValue6, float floatValue7, float floatValue8, String text3) {
      float floatValue9 = 3.5F;
      float floatValue10 = 25.0F;
      float floatValue11 = 5.0F;
      float floatValue12 = 4.0F;
      float floatValue13 = 8.5F;
      float floatValue14 = 7.5F;
      Supplier supplier = Fonts.SUPPLIER14;
      int value4 = Math.round(MinecraftAccess.MINECRAFT.player.distanceTo(livingEntity2));
      int value5 = Math.round(floatValue6);
      String text4 = value5 + " HP - " + value4 + "m";
      float floatValue15 = Draw2D.getTextWidth(text3, supplier, floatValue13);
      float floatValue16 = Draw2D.getTextWidth(text4, supplier, floatValue14);
      float floatValue17 = Math.max(75.0F, Math.max(floatValue15, floatValue16) + 4.0F);
      ItemStack itemStack2 = livingEntity2.getItemBySlot(EquipmentSlot.OFFHAND);
      boolean flag = !itemStack2.isEmpty();
      float floatValue18 = 12.0F;
      float floatValue19 = floatValue9 + floatValue10 + floatValue11 + floatValue17 + floatValue9;
      float floatValue20 = floatValue9 * 2.0F + floatValue10;
      Draw2D.m_6(floatValue3, floatValue4, floatValue19, floatValue20, 0.0F, 1.5F, ColorUtil.rgba(20, 20, 20, (int)(130.0F * floatValue5)));
      float floatValue21 = Mth.clamp((livingEntity2.hurtTime - event.getFloatValue()) / 10.0F, 0.0F, 1.0F);
      this.m_6(livingEntity2, floatValue3 + floatValue9, floatValue4 + floatValue9, floatValue10, 0.0F, value3, floatValue21);
      float floatValue22 = floatValue3 + floatValue9 + floatValue10 + floatValue11;
      float floatValue23 = floatValue4 + floatValue9 + 1.0F - 2.0F;
      float floatValue24 = flag ? floatValue17 - floatValue18 - 4.0F : floatValue17;
      Draw2D.drawColoredTextRaw(text3, supplier, floatValue22, floatValue23, ColorUtil.rgba(240, 240, 240, value3), 0.7F, 0.95F, floatValue24, floatValue13);
      float floatValue25 = floatValue23 + 8.5F;
      Draw2D.drawStyledText(text4, supplier, floatValue22, floatValue25, ColorUtil.rgba(220, 220, 220, (int)(225.0F * floatValue5)), floatValue14);
      float floatValue26 = floatValue4 + floatValue9 + floatValue10 - floatValue12 - 2.0F;
      Draw2D.roundedRect(floatValue22, floatValue26, floatValue17, floatValue12, 0.0F, ColorUtil.rgba(255, 255, 255, (int)(18.0F * floatValue5)));
      float floatValue27 = Math.min(floatValue6 / floatValue7, 1.0F);
      float floatValue28 = this.animation8.update(floatValue27);
      float floatValue29 = floatValue17 * floatValue28;
      if (floatValue29 > 0.5F) {
         int value6 = (int)(115.0F * floatValue5);
         int value7 = ColorUtil.primaryColor();
         int value8 = ColorUtil.secondaryColor();
         Draw2D.m_5(floatValue22, floatValue26, floatValue29, floatValue12, 0.0F, 3.0F, ColorUtil.withAlpha(value7, value6), ColorUtil.withAlpha(value8, value6));
         Draw2D.m_5(floatValue22, floatValue26, floatValue29, floatValue12, 0.0F, ColorUtil.withAlpha(value7, value3), ColorUtil.withAlpha(value8, value3));
      }

      float floatValue30 = this.animation10.update(floatValue8 > 0.0F ? 1.0F : 0.0F) * floatValue5;
      float floatValue31 = this.animation9.update(Math.min(floatValue8 / floatValue7, 1.0F));
      if (floatValue30 > 0.01F && floatValue31 > 0.001F) {
         int value9 = (int)(255.0F * floatValue30);
         int value10 = (int)(130.0F * floatValue30);
         float floatValue32 = floatValue22 + floatValue17 * (1.0F - floatValue31);
         float floatValue33 = floatValue17 * floatValue31;
         Draw2D.m_1(floatValue32, floatValue26, floatValue33, floatValue12, 0.0F, 3.0F, ColorUtil.rgba(255, 200, 80, value10));
         Draw2D.roundedRect(floatValue32, floatValue26, floatValue33, floatValue12, 0.0F, ColorUtil.rgba(255, 200, 80, value9));
      }

      if (flag) {
         float floatValue34 = floatValue3 + floatValue19 - floatValue9 - floatValue18;
         float floatValue35 = floatValue23;
         TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
         MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, itemStack2, ItemDisplayContext.GUI, livingEntity2.level(), livingEntity2, (int)(floatValue34 + floatValue35));
         event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
         Render2D.drawItem(trackingItemStackRenderState, floatValue34, floatValue35, floatValue18, ColorUtil.rgba(255, 255, 255, value3));
         Render2D.drawItemDecorations(itemStack2, floatValue34, floatValue35, floatValue18, value3);
      }

      this.textField9.m_8_void(floatValue19, floatValue20);
   }

   private void m_7(Render2DEvent event, LivingEntity livingEntity2, float floatValue3, float floatValue4, float floatValue5, int value3, float floatValue6, float floatValue7, float floatValue8, String text3, float floatValue9) {
      float floatValue10 = 98.0F;
      float floatValue11 = 24.0F;
      float floatValue12 = 6.0F;
      float floatValue13 = 3.0F;
      float floatValue14 = 18.0F;
      float floatValue15 = 4.0F;
      float floatValue16 = 9.0F;
      float floatValue17 = 7.5F;
      float floatValue18 = 5.0F;
      this.theme2.m_7_void(floatValue3, floatValue4, floatValue10, floatValue11, floatValue12, floatValue5);
      if (this.hudStylePanel2.getActiveColor() == 0) {
         Theme.m_5(floatValue3 + 0.5F, floatValue4 + 0.5F, floatValue10 - 1.0F, floatValue11 - 1.0F, ColorUtil.rgba(255, 255, 255, 230));
      }

      int value4 = this.theme2.getAccentColor();
      int value5 = this.theme2.m_2_int();
      float floatValue19 = Math.min(floatValue6 / floatValue7, 1.0F);
      float floatValue20 = this.animation8.update(floatValue19);
      float floatValue21 = floatValue10 * floatValue20;
      if (floatValue21 > 0.5F) {
         float floatValue22 = Math.min(floatValue12, floatValue21 * 0.5F);
         float floatValue23 = Math.max(0.0F, floatValue12 - (floatValue10 - floatValue21));
         Draw2D.m_4(floatValue3, floatValue4, floatValue21, floatValue11, floatValue22, floatValue23, floatValue23, floatValue22, 0.0F, ColorUtil.withAlpha(value4, (int)(12.0F * floatValue5)), ColorUtil.withAlpha(value5, (int)(28.0F * floatValue5)));
         float floatValue24 = Math.min(28.0F, floatValue21);
         float floatValue25 = floatValue3 + floatValue21 - floatValue24;
         float floatValue26 = Math.min(Math.max(0.0F, floatValue12 - (floatValue25 - floatValue3)), floatValue24 * 0.5F);
         Draw2D.m_4(floatValue25, floatValue4, floatValue24, floatValue11, floatValue26, floatValue23, floatValue23, floatValue26, 0.0F, ColorUtil.withAlpha(value5, 0), ColorUtil.withAlpha(value5, (int)(72.0F * floatValue5)));
         if (floatValue21 < floatValue10 - 0.5F) {
            this.m_7(floatValue3 + floatValue21 - 0.75F, floatValue3, floatValue4, floatValue10, floatValue11, floatValue12, value5, floatValue5);
         }

         if (this.hudStylePanel2.getActiveColor() == 3 && floatValue21 > 2.0F) {
            Theme.m_5(floatValue3 + 0.5F, floatValue4 + 0.5F, floatValue21 - 1.0F, floatValue11 - 1.0F, ColorUtil.rgba(255, 255, 255, 230));
         }
      }

      int value6 = ColorUtil.rgba(255, 200, 80, 255);
      float floatValue27 = this.animation10.update(floatValue8 > 0.0F ? 1.0F : 0.0F) * floatValue5;
      float floatValue28 = this.animation9.update(Math.min(floatValue8 / floatValue7, 1.0F));
      if (floatValue27 > 0.01F && floatValue28 > 0.001F) {
         float floatValue29 = floatValue10 * floatValue28;
         float floatValue30 = Math.min(floatValue12, floatValue29 * 0.5F);
         int value7 = ColorUtil.withAlpha(value6, (int)(42.0F * floatValue27));
         Draw2D.m_4(floatValue3 + floatValue10 - floatValue29, floatValue4, floatValue29, floatValue11, 0.0F, floatValue30, floatValue30, 0.0F, 0.0F, value7, value7);
         if (floatValue29 < floatValue10 - 0.5F) {
            this.m_7(floatValue3 + floatValue10 - floatValue29, floatValue3, floatValue4, floatValue10, floatValue11, floatValue12, value6, floatValue27);
         }
      }

      float floatValue31 = Mth.clamp((livingEntity2.hurtTime - event.getFloatValue()) / 10.0F, 0.0F, 1.0F);
      this.m_6(livingEntity2, floatValue3 + floatValue13, floatValue4 + floatValue13, floatValue14, 4.0F, value3, floatValue31);
      float floatValue32 = floatValue3 + floatValue10 - 5.0F;
      float floatValue33 = Draw2D.getFontHeight(Fonts.SUPPLIER18, floatValue16);
      float floatValue34 = floatValue4 + (floatValue11 - floatValue33) * 0.5F;
      this.m_3(floatValue32, floatValue34, floatValue16, false, this.theme2.m_8_int(255), value3, floatValue9);
      if (floatValue27 > 0.01F) {
         String text4 = "+" + (int)Math.ceil(floatValue8) + " ABS";
         float floatValue35 = Draw2D.getTextWidth(text4, Fonts.SUPPLIER18, floatValue18);
         Draw2D.drawStyledText(text4, Fonts.SUPPLIER18, floatValue32 - floatValue35, floatValue34 + floatValue33 + 0.5F, ColorUtil.withAlpha(value6, (int)(255.0F * floatValue27)), floatValue18);
      }

      float floatValue36 = floatValue3 + floatValue13 + floatValue14 + floatValue15;
      float floatValue37 = Draw2D.getTextWidth(this.text, Fonts.SUPPLIER18, floatValue16);
      float floatValue38 = floatValue32 - floatValue37 - 3.0F - floatValue36;
      Draw2D.drawColoredTextRaw(text3, Fonts.SUPPLIER18, floatValue36, floatValue4 + 4.0F, this.theme2.m_8_int(value3), 0.7F, 0.95F, floatValue38, floatValue17);
      if (this.hudStylePanel2.getActiveColor() == 2) {
         Theme.m_3(floatValue36, floatValue4 + 12.0F, Math.min(Draw2D.getTextWidth(text3, Fonts.SUPPLIER18, floatValue17), floatValue38), ColorUtil.rgba(255, 255, 255, 230));
      }

      this.m_2(event, livingEntity2, floatValue36, floatValue4 + 13.5F, 7.0F, 7.5F, true, floatValue5);
      this.textField9.m_8_void(floatValue10, floatValue11);
   }

   private void m_7(float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, int value3, float floatValue9) {
      float floatValue10 = 0.75F;
      floatValue3 = Mth.clamp(floatValue3, floatValue4, floatValue4 + floatValue6 - floatValue10);
      this.m_7(floatValue3 - 1.75F, floatValue10 + 3.5F, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, ColorUtil.withAlpha(value3, (int)(35.0F * floatValue9)));
      this.m_7(floatValue3 - 0.75F, floatValue10 + 1.5F, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, ColorUtil.withAlpha(value3, (int)(70.0F * floatValue9)));
      this.m_7(floatValue3, floatValue10, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, ColorUtil.withAlpha(value3, (int)(255.0F * floatValue9)));
   }

   private void m_7(float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, int value3) {
      Render2D.pushScissor(floatValue3, floatValue6, floatValue4, floatValue8);
      Draw2D.roundedRect(floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, value3);
      Render2D.popScissor();
   }

   private int getHealthColor(float floatValue3) {
      float floatValue4 = Math.min(Math.max(floatValue3, 0.0F), 1.0F);
      int value3 = ColorUtil.rgba(225, 40, 40, 255);
      int value4 = ColorUtil.rgba(230, 210, 40, 255);
      int value5 = ColorUtil.rgba(100, 244, 66, 255);
      return floatValue4 > 0.5F ? ColorUtil.lerpColor(value4, value5, (floatValue4 - 0.5F) * 2.0F) : ColorUtil.lerpColor(value3, value4, floatValue4 * 2.0F);
   }

   private void m_3(Render2DEvent event, LivingEntity livingEntity2, float floatValue3, float floatValue4, float floatValue5) {
      this.m_2(event, livingEntity2, floatValue3, floatValue4 - 14.0F, 12.0F, 13.0F, true, floatValue5);
   }

   private void m_2(Render2DEvent event, LivingEntity livingEntity2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, boolean flag, float floatValue7) {
      int value3 = ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue7));
      int value4 = (int)(255.0F * floatValue7);
      ArrayList list = new ArrayList();
      float floatValue8 = floatValue3;

      record C17(TrackingItemStackRenderState f_8_h, ItemStack f_93, float f_1e) {
         public TrackingItemStackRenderState m_8_h() {
            return this.f_8_h;
         }

         public ItemStack m_93() {
            return this.f_93;
         }

         public float m_1e_float() {
            return this.f_1e;
         }
      }

      for (EquipmentSlot equipmentSlot : new EquipmentSlot[]{
         EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND
      }) {
         ItemStack itemStack2 = livingEntity2.getItemBySlot(equipmentSlot);
         if (itemStack2.isEmpty()) {
            if (!flag) {
               floatValue8 += floatValue6;
            }
         } else {
            TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
            MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, itemStack2, ItemDisplayContext.GUI, livingEntity2.level(), livingEntity2, (int)(floatValue8 + floatValue4));
            event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
            list.add(new C17(trackingItemStackRenderState, itemStack2, floatValue8));
            floatValue8 += floatValue6;
         }
      }

      for (C17 c17 : (Iterable<C17>)list) {
         Render2D.drawItem(c17.f_8_h, c17.f_1e, floatValue4, floatValue5, value3);
      }

      for (C17 c172 : (Iterable<C17>)list) {
         Render2D.drawItemDurability(c172.f_93, c172.f_1e, floatValue4, floatValue5, value4);
      }

      for (C17 c173 : (Iterable<C17>)list) {
         Render2D.drawItemCount(c173.f_93, c173.f_1e, floatValue4, floatValue5, value4);
      }
   }

   private void m_6(LivingEntity livingEntity2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value3, float floatValue7) {
      int value4 = ColorUtil.rgba(222, 25, 25, value3);
      if (livingEntity2 instanceof Player player) {
         Draw2D.m_4(floatValue3, floatValue4, floatValue5, floatValue5, floatValue6, ColorUtil.lerpColor(ColorUtil.rgba(255, 255, 255, value3), value4, floatValue7), PlayerFaceTexture.getFace(player));
      } else {
         float floatValue8 = floatValue5 + 1.0F;
         float floatValue9 = floatValue3 + (floatValue5 - Draw2D.getTextWidth("\uf648", Fonts.SUPPLIER8, floatValue8)) / 2.0F;
         float floatValue10 = floatValue4 + (floatValue5 - floatValue8) / 2.0F;
         Draw2D.drawTextWithShadow("\uf648", Fonts.SUPPLIER8, floatValue9, floatValue10, ColorUtil.lerpColor(ColorUtil.rgba(222, 222, 222, value3), value4, floatValue7), floatValue8);
      }
   }

   private void m_9(LivingEntity livingEntity2, float floatValue3, float floatValue4, float floatValue5, int value3, float floatValue6) {
      this.m_6(livingEntity2, floatValue3, floatValue4, floatValue5, Math.max(0.0F, floatValue5 / 2.0F - 7.0F), value3, floatValue6);
   }

   private void layoutField(TextField textField18, float floatValue3) {
      textField18.setFloatValue8(floatValue3);
      boolean flag = MinecraftAccess.MINECRAFT.gui.screen() instanceof ChatScreen;
      if (flag) {
         textField18.m_9(this.mouseUtil);
      } else {
         textField18.m_7_void();
      }

      float floatValue4 = this.stringMap.computeIfAbsent(textField18.getText(), text3 -> new Animation(150L, 0.0F, Easing.EASING8)).update(flag && textField18 == this.textField ? 1.0F : 0.0F);
      if (floatValue4 > 0.01F) {
         Draw2D.m_4(
            textField18.m_5_float() - 2.0F,
            textField18.m_8_float() - 2.0F,
            textField18.m_6_float() + 4.0F,
            textField18.m_9_float() + 4.0F,
            0.5F,
            floatValue3,
            ColorUtil.withAlpha(ColorUtil.rgba(255, 255, 255, 255), (int)(110.0F * floatValue4))
         );
      }
   }

   private void m_9(Render2DEvent event, LivingEntity livingEntity2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, int value3, float floatValue10) {
      float floatValue11 = 6.0F;
      this.theme.m_7_void(floatValue3, floatValue4, floatValue5, floatValue6, floatValue11, floatValue10);
      if (this.hudStylePanel.getActiveColor() == 0) {
         Theme.m_5(floatValue3 + 0.5F, floatValue4 + 0.5F, floatValue5 - 1.0F, floatValue6 - 1.0F, ColorUtil.rgba(255, 255, 255, 230));
      }

      float floatValue12 = floatValue3 + floatValue7 + (floatValue8 - floatValue9) * 0.5F;
      float floatValue13 = floatValue4 + floatValue7 + (floatValue8 - floatValue9) * 0.5F;
      float floatValue14 = Mth.clamp((livingEntity2.hurtTime - event.getFloatValue()) / 10.0F, 0.0F, 1.0F);
      this.m_9(livingEntity2, floatValue12, floatValue13, floatValue9, value3, floatValue14);
   }

   private void m_03_void() {
      if (MinecraftAccess.MINECRAFT.player.tickCount != this.value2) {
         this.value2 = MinecraftAccess.MINECRAFT.player.tickCount;

         for (HUD.StateHolder3 c6 : this.stringMap8.values()) {
            c6.flag = false;
         }

         ClientPacketListener clientPacketListener = MinecraftAccess.MINECRAFT.getConnection();
         if (clientPacketListener != null && MinecraftAccess.MINECRAFT.level != null) {
            String text3 = MinecraftAccess.MINECRAFT.player.getName().getString();
            Scoreboard scoreboard = MinecraftAccess.MINECRAFT.level.getScoreboard();
            TreeSet treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

            for (PlayerInfo playerInfo : clientPacketListener.getOnlinePlayers()) {
               treeSet.add(playerInfo.getProfile().name());
            }

            LinkedHashMap map = new LinkedHashMap();
            if (treeSet.size() > 1) {
               ArrayList list = new ArrayList<>(scoreboard.getPlayerTeams());
               list.sort(Comparator.comparing(PlayerTeam::getName));

               for (PlayerTeam playerTeam : (Iterable<PlayerTeam>)list) {
                  if (!isNpcTeam(playerTeam)) {
                     for (String text4 : playerTeam.getPlayers()) {
                        if (!treeSet.contains(text4) && m_4(text4, playerTeam)) {
                           map.putIfAbsent(text4, playerTeam.getPlayerPrefix());
                        }
                     }
                  }
               }
            }

            for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
               if (player != MinecraftAccess.MINECRAFT.player) {
                  String text5 = player.getGameProfile().name();
                  PlayerTeam playerTeam2 = scoreboard.getPlayersTeam(text5);
                  if (m_4(text5, playerTeam2) && clientPacketListener.getPlayerInfo(player.getUUID()) == null && !treeSet.contains(text5)) {
                     map.putIfAbsent(text5, playerTeam2 != null ? playerTeam2.getPlayerPrefix() : Component.empty());
                  }
               }
            }

            map.remove(text3);
            LinkedHashMap map2 = new LinkedHashMap();

            for (PlayerInfo playerInfo2 : clientPacketListener.getOnlinePlayers()) {
               String text6 = playerInfo2.getProfile().name();
               if (!text6.equals(text3) && isValidName(text6) && !map.containsKey(text6)) {
                  PlayerTeam playerTeam3 = scoreboard.getPlayersTeam(text6);
                  Component component = playerTeam3 != null ? playerTeam3.getPlayerPrefix() : Component.empty();
                  boolean flag = playerInfo2.getGameMode() == GameType.SPECTATOR;
                  if (this.booleanSetting2.getValue() ? Onetap.STAFF_MANAGER.isStaff(text6) : m_8(text6, component, flag)) {
                     map2.put(text6, component);
                  }
               }
            }

            Comparator comparator = Comparator.<String, Boolean>comparing(text7 -> !Onetap.STAFF_MANAGER.isStaff(text7)).thenComparing(String.CASE_INSENSITIVE_ORDER);
            TreeSet treeSet2 = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            HashSet set = new HashSet();

            for (String text8 : StaffExploit.getStaffSet()) {
               if (text8 != null && !text8.isBlank() && set.add(text8.toLowerCase(Locale.ROOT)) && !this.m_6(treeSet2, text8, StaffExploit.m_9_Component(text8), true)) {
                  break;
               }
            }

            for (Map map3 : List.of(map, map2)) {
               for (String text9 : (Iterable<String>)map3.keySet().stream().sorted(comparator).toList()) {
                  if (!this.m_6(treeSet2, text9, (Component)map3.get(text9), false)) {
                     break;
                  }
               }
            }
         }

         this.stringMap8.entrySet().removeIf(entry -> !entry.getValue().flag && entry.getValue().animation.getValue() < 0.01F);
      }
   }

   private boolean m_6(Set<String> stringSet, String text3, Component component, boolean flag) {
      if (stringSet.size() >= 10) {
         return false;
      }

      PlayerInfo playerInfo = flag ? null : MinecraftAccess.MINECRAFT.getConnection().getPlayerInfoIgnoreCase(text3);
      boolean flag2 = !flag && playerInfo == null;
      boolean flag3 = playerInfo != null && playerInfo.getGameMode() == GameType.SPECTATOR;
      if (this.booleanSetting2.getValue() ? Onetap.STAFF_MANAGER.isStaff(text3) : flag || flag2 || flag3 || !isStaffName(component.getString())) {
         boolean flag4 = flag2 || flag;
         if (((String)this.modeSetting3.getValue()).equals(flag4 ? "Онлайн" : "Ваниш")) {
            return true;
         }

         HUD.StateHolder3 c6 = this.stringMap8.computeIfAbsent(text3.toLowerCase(Locale.ROOT), text4 -> new HUD.StateHolder3(text3));
         if (stringSet.add(text3)) {
            c6.value = stringSet.size();
         }

         c6.flag = true;
         c6.flag2 = flag2;
         c6.playerInfo = playerInfo;
         MutableComponent mutableComponent = hasVisibleText(component) ? Component.literal(text3) : component.copy().append(Component.literal(text3));
         if (c6.textRenderer == null || !mutableComponent.equals(c6.component)) {
            c6.component = mutableComponent;
            c6.textRenderer = TextRenderer.m_6(ChatBadges.m_6(mutableComponent), Fonts.SUPPLIER18, Fonts.SUPPLIER17);
         }

         if (flag2 && c6.longValue == 0L) {
            c6.longValue = System.currentTimeMillis();
         }

         if (!flag2) {
            c6.longValue = 0L;
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean m_8(String text3, Component component, boolean flag) {
      return (Onetap.STAFF_MANAGER.isStaff(text3) || m_2_boolean(component.getString()) || flag) && (flag || !isStaffName(component.getString()));
   }

   private static boolean isValidName(String text3) {
      return text3 != null && PATTERN.matcher(text3).matches() && !STRING_SET.contains(text3) && !PATTERN2.matcher(text3).matches();
   }

   private static boolean m_4(String text3, PlayerTeam playerTeam) {
      if (!isValidName(text3)) {
         return false;
      } else if (!Onetap.STAFF_MANAGER.isStaff(text3) && !isStaffTeam(playerTeam)) {
         Component component = playerTeam != null ? playerTeam.getPlayerPrefix() : Component.empty();
         return !hasVisibleText(component) && m_2_boolean(component.getString());
      } else {
         return true;
      }
   }

   private static boolean isNpcTeam(PlayerTeam playerTeam) {
      return isNpcName(playerTeam.getDisplayName().getString().trim()) || isNpcName(playerTeam.getName());
   }

   private static boolean isNpcName(String text3) {
      String text4 = text3.toLowerCase(Locale.ROOT);
      return text4.startsWith("npc") || text4.startsWith("colliderule_");
   }

   private static boolean isStaffTeam(PlayerTeam playerTeam) {
      return playerTeam != null && (startsWithAny(playerTeam.getName(), TEXT_ARRAY) || startsWithAny(playerTeam.getDisplayName().getString(), TEXT_ARRAY));
   }

   private static boolean startsWithAny(String text3, String[] stringArray) {
      String text4 = text3.toLowerCase(Locale.ROOT).trim();

      for (String text5 : stringArray) {
         if (text4.startsWith(text5)) {
            return true;
         }
      }

      return false;
   }

   private static boolean containsAny(String text3, String[] stringArray) {
      for (String text4 : stringArray) {
         if (text3.contains(text4)) {
            return true;
         }
      }

      return false;
   }

   private static String cleanName(String text3) {
      String text4 = ChatFormatting.stripFormatting(text3);
      return text4 != null && !text4.isEmpty() ? ChatBadges.m_1_String(text4).trim() : "";
   }

   private static boolean hasVisibleText(Component component) {
      boolean[] flagArray = new boolean[]{false};
      component.getVisualOrderText().accept((value3, style, value4) -> !style.isObfuscated() || value4 == 32);
      return flagArray[0] || cleanName(component.getString()).length() > 16;
   }

   private static boolean m_2_boolean(String text3) {
      String text4 = ChatFormatting.stripFormatting(text3);
      if (text4 != null && !text4.isEmpty() && text4.length() <= 24) {
         String text5 = ChatBadges.m_1_String(text4).toLowerCase(Locale.ROOT);
         return !text5.contains("d.helper") && containsAny(text5, TEXT_ARRAY2);
      } else {
         return false;
      }
   }

   private static boolean isStaffName(String text3) {
      String text4 = cleanName(text3).toLowerCase(Locale.ROOT);
      if (text4.isEmpty()) {
         return false;
      }

      if (containsAny(text4, TEXT_ARRAY3)) {
         return true;
      }

      String text5 = text4.replace(" ", "").replace("_", "").replace("-", "").replace(".", "");
      return text5.contains("dadmin") || text5.contains("дадмин");
   }

   private void m_0_h_void() {
      this.layoutField(this.textField8, 4.0F);
      float floatValue3 = this.textField8.m_5_float();
      float floatValue4 = this.textField8.m_8_float();
      this.applyScale(floatValue3, floatValue4);
      float floatValue5 = 4.0F;
      float floatValue6 = 15.0F;
      float floatValue7 = 10.0F;
      float floatValue8 = 9.0F;
      float floatValue9 = 10.0F;
      float floatValue10 = 3.0F;
      float floatValue11 = floatValue8 * 0.75F;
      float floatValue12 = floatValue11 + floatValue10 * 2.0F;
      float floatValue13 = 11.5F;
      float floatValue14 = 2.0F;
      float floatValue15 = 6.0F;
      float floatValue16 = 3.0F;
      float floatValue17 = 9.0F;
      float floatValue18 = 3.0F;
      float floatValue19 = 8.25F;
      float floatValue20 = 5.0F;
      float floatValue21 = 7.0F;
      ArrayList<HUD.StateHolder3> c6List = new ArrayList<>();
      float floatValue22 = 0.0F;
      float floatValue23 = 0.0F;
      float floatValue24 = 0.0F;

      for (HUD.StateHolder3 c6 : this.stringMap8.values()) {
         float floatValue25 = c6.animation.update(c6.flag ? 1.0F : 0.0F);
         if (!(floatValue25 < 0.01F)) {
            c6List.add(c6);
            float floatValue26 = c6.textRenderer != null && !c6.textRenderer.isEmpty() ? c6.textRenderer.getWidth(floatValue19) : Draw2D.getTextWidth(c6.text, Fonts.SUPPLIER18, floatValue19);
            float floatValue27 = c6.flag2 ? Draw2D.getTextWidth(formatDuration(c6.longValue), Fonts.SUPPLIER18, floatValue21) : 0.0F;
            float floatValue28 = floatValue17 + floatValue18 + floatValue15 + floatValue16 + floatValue26 + (floatValue27 > 0.0F ? floatValue20 + floatValue27 : 0.0F) + floatValue5;
            if (floatValue28 > floatValue23) {
               floatValue23 = floatValue28;
            }

            floatValue22 += floatValue13 * floatValue25;
            if (floatValue25 > floatValue24) {
               floatValue24 = floatValue25;
            }
         }
      }

      c6List.sort(Comparator.comparingInt(c62 -> c62.value));
      float floatValue29 = Draw2D.getTextWidth("Стафф", Fonts.SUPPLIER18, floatValue9);
      float floatValue30 = floatValue5 + floatValue29 + floatValue7 + floatValue12 + floatValue5;
      float floatValue31 = Math.max(floatValue30, floatValue23 + floatValue5 * 2.0F);
      boolean flag = floatValue22 > 0.01F;
      float floatValue32 = 0.5F;
      float floatValue33 = floatValue22 + floatValue14 * floatValue24;
      float floatValue34 = this.animation3.update(floatValue31);
      if (floatValue34 < 5.0F) {
         this.textField8.m_8_void(0.0F, 0.0F);
      } else {
         float floatValue35 = floatValue6 + (floatValue33 > 0.01F ? floatValue32 + floatValue33 : 0.0F);
         float floatValue36 = floatValue4 + floatValue6 + floatValue32;
         this.theme5.m_5(floatValue3, floatValue4, floatValue34, floatValue6, 4.0F, 4.0F, 0.0F, 0.0F);
         if (floatValue33 > 0.01F) {
            this.theme5.m_0(floatValue3, floatValue36, floatValue34, floatValue33, 0.0F, 0.0F, 4.0F, 4.0F);
         }

         this.hudStylePanel5.m_5(floatValue3, floatValue4, floatValue34, floatValue6, floatValue36, floatValue33);
         int value3 = this.theme5.m_8_int(255);
         int value4 = this.theme5.getAccentColor();
         int value5 = this.theme5.m_2_int();
         Draw2D.drawStyledText("Стафф", Fonts.SUPPLIER18, floatValue3 + floatValue5, Draw2D.getTextBaseline(Fonts.SUPPLIER18, floatValue9, floatValue4, floatValue6), value3, floatValue9);
         float floatValue37 = floatValue3 + floatValue34 - floatValue5 - floatValue12;
         float floatValue38 = floatValue4 + (floatValue6 - floatValue12) / 2.0F;
         Draw2D.m_8(floatValue37 + 0.5F, floatValue38 + 1.0F, floatValue12 - 1.0F, floatValue12 - 2.0F, 4.0F, 0.5F, value4, value5);
         Draw2D.drawTextWithShadow(
            "\uf132",
            Fonts.SUPPLIER8,
            Draw2D.getCenteredX("\uf132", Fonts.SUPPLIER8, floatValue11, floatValue37, floatValue12),
            Draw2D.getCenteredY("\uf132", Fonts.SUPPLIER8, floatValue11, floatValue38, floatValue12),
            ColorUtil.rgba(255, 255, 255, 255),
            floatValue11
         );
         if (this.hudStylePanel5.getActiveColor() == 3) {
            Theme.m_5(floatValue37 - 0.5F, floatValue38 - 0.5F, floatValue12 + 1.0F, floatValue12 + 1.0F, ColorUtil.rgba(255, 255, 255, 230));
         }

         if (flag) {
            float floatValue39 = floatValue36 + (floatValue33 - floatValue22) / 2.0F;
            Render2D.pushScissor(floatValue3, floatValue36, floatValue34, floatValue33);

            for (HUD.StateHolder3 c63 : c6List) {
               float floatValue40 = c63.animation.update(c63.flag ? 1.0F : 0.0F);
               if (!(floatValue40 < 0.01F)) {
                  int value6 = (int)(255.0F * floatValue40);
                  float floatValue41 = floatValue39 - (1.0F - floatValue40) * floatValue13;
                  float floatValue42 = floatValue13 * floatValue40;
                  float floatValue43 = floatValue3 + floatValue5;
                  float floatValue44 = floatValue41 + (floatValue42 - floatValue17) / 2.0F;
                  GameProfile gameProfile = c63.playerInfo != null ? c63.playerInfo.getProfile() : c63.m_6_GameProfile();
                  GpuTextureView gpuTextureView = PlayerFaceTexture.getFace(gameProfile);
                  if (gpuTextureView != null) {
                     Draw2D.m_4(floatValue43, floatValue44, floatValue17, floatValue17, floatValue17 / 2.0F - 0.5F, ColorUtil.withAlpha(-1, value6), gpuTextureView);
                  }

                  int value7 = c63.playerInfo == null ? VALUE : VALUE2;
                  float floatValue45 = floatValue43 + floatValue17 + floatValue18;
                  float floatValue46 = floatValue41 + (floatValue42 - floatValue15) / 2.0F;
                  int value8 = ColorUtil.withAlpha(value7, value6);
                  int value9 = ColorUtil.withAlpha(ColorUtil.lerpColor(value7, ColorUtil.rgba(0, 0, 0, 255), 0.45F), value6);
                  Draw2D.m_8(floatValue45, floatValue46, floatValue15, floatValue15, floatValue15 / 2.0F - 0.5F, 0.5F, value8, value9);
                  float floatValue47 = floatValue45 + floatValue15 + floatValue16;
                  float floatValue48 = Draw2D.getTextBaseline(Fonts.SUPPLIER18, floatValue19, floatValue41, floatValue42);
                  float floatValue49;
                  if (c63.textRenderer != null && !c63.textRenderer.isEmpty()) {
                     c63.textRenderer.draw(floatValue47, floatValue48, floatValue19, value6);
                     floatValue49 = c63.textRenderer.getWidth(floatValue19);
                  } else {
                     Draw2D.drawStyledText(c63.text, Fonts.SUPPLIER18, floatValue47, floatValue48, this.theme5.m_8_int(value6), floatValue19);
                     floatValue49 = Draw2D.getTextWidth(c63.text, Fonts.SUPPLIER18, floatValue19);
                  }

                  if (this.hudStylePanel5.getActiveColor() == 2) {
                     Theme.m_3(floatValue47, floatValue41 + floatValue42 - 1.0F, floatValue49, ColorUtil.rgba(255, 255, 255, 230));
                  }

                  if (c63.flag2) {
                     String text3 = formatDuration(c63.longValue);
                     float floatValue50 = Draw2D.getTextWidth(text3, Fonts.SUPPLIER18, floatValue21);
                     Draw2D.drawStyledText(text3, Fonts.SUPPLIER18, floatValue3 + floatValue34 - floatValue50 - floatValue5, Draw2D.getTextBaseline(Fonts.SUPPLIER18, floatValue21, floatValue41, floatValue42), ColorUtil.withAlpha(VALUE, value6), floatValue21);
                  }

                  floatValue39 += floatValue13 * floatValue40;
               }
            }

            Render2D.popScissor();
         }

         this.textField8.m_8_void(floatValue34, floatValue35);
      }
   }

   private static String formatDuration(long longValue4) {
      if (longValue4 == 0L) {
         return "";
      }

      long longValue5 = (System.currentTimeMillis() - longValue4) / 1000L;
      long longValue6 = longValue5 / 60L;
      long longValue7 = longValue5 % 60L;
      return String.format(Locale.US, "%d:%02d", longValue6, longValue7);
   }

   private static float getTagWidth(String text3) {
      return 4.0F + Draw2D.getTextWidth(text3, Fonts.SUPPLIER18, 10.0F) + 10.0F + 12.75F + 4.0F;
   }

   private static void m_9(Theme theme11, HudStylePanel hudStylePanel11, String text3, String text4, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      float floatValue7 = floatValue4 + 15.0F + 0.5F;
      theme11.m_5(floatValue3, floatValue4, floatValue5, 15.0F, 4.0F, 4.0F, 0.0F, 0.0F);
      if (floatValue6 > 0.01F) {
         theme11.m_0(floatValue3, floatValue7, floatValue5, floatValue6, 0.0F, 0.0F, 4.0F, 4.0F);
      }

      hudStylePanel11.m_5(floatValue3, floatValue4, floatValue5, 15.0F, floatValue7, floatValue6);
      Draw2D.drawStyledText(text3, Fonts.SUPPLIER18, floatValue3 + 4.0F, Draw2D.getTextBaseline(Fonts.SUPPLIER18, 10.0F, floatValue4, 15.0F), theme11.m_8_int(255), 10.0F);
      float floatValue8 = floatValue3 + floatValue5 - 4.0F - 12.75F;
      float floatValue9 = floatValue4 + 1.125F;
      Draw2D.m_8(floatValue8 + 0.5F, floatValue9 + 1.0F, 11.75F, 10.75F, 4.0F, 0.5F, theme11.getAccentColor(), theme11.m_2_int());
      Draw2D.drawTextWithShadow(
         text4, Fonts.SUPPLIER8, Draw2D.getCenteredX(text4, Fonts.SUPPLIER8, 6.75F, floatValue8, 12.75F), Draw2D.getCenteredY(text4, Fonts.SUPPLIER8, 6.75F, floatValue9, 12.75F), ColorUtil.rgba(255, 255, 255, 255), 6.75F
      );
   }

   private void updateCooldowns() {
      ItemCooldowns itemCooldowns = MinecraftAccess.MINECRAFT.player.getCooldowns();

      for (HUD.StateHolder4 c7 : this.identifierMap.values()) {
         c7.flag = false;
      }

      for (ItemStack itemStack2 : MinecraftAccess.MINECRAFT.player.getInventory().getNonEquipmentItems()) {
         if (!itemStack2.isEmpty()) {
            Identifier identifier = itemCooldowns.getCooldownGroup(itemStack2);
            CooldownInstance cooldownInstance = itemCooldowns.cooldowns.get(identifier);
            if (cooldownInstance != null) {
               int value3 = cooldownInstance.endTime() - itemCooldowns.tickCount;
               if (value3 > 0) {
                  HUD.StateHolder4 c72 = this.identifierMap.computeIfAbsent(identifier, identifier2 -> new HUD.StateHolder4());
                  if (!c72.flag) {
                     c72.flag = true;
                     c72.value = value3;
                     if (c72.itemStack == null || !ItemStack.isSameItemSameComponents(c72.itemStack, itemStack2)) {
                        c72.itemStack = itemStack2.copy();
                        c72.text = itemStack2.getHoverName().getString();
                     }
                  }
               }
            }
         }
      }

      this.identifierMap.values().removeIf(c73 -> !c73.flag && c73.animation.getValue() < 0.01F);
   }

   private void m_8(Render2DEvent event) {
      this.updateCooldowns();
      this.layoutField(this.textField13, 4.0F);
      float floatValue3 = this.textField13.m_5_float();
      float floatValue4 = this.textField13.m_8_float();
      this.applyScale(floatValue3, floatValue4);
      float floatValue5 = 10.0F;
      float floatValue6 = 2.0F;
      float floatValue7 = MinecraftAccess.MINECRAFT.level.tickRateManager().tickrate();
      ArrayList list = new ArrayList<>(this.identifierMap.values());
      list.sort(Comparator.comparingInt(object -> ((wtf.onetap.module.render.HUD.StateHolder4)object).value));
      float floatValue8 = 0.0F;
      float floatValue9 = 0.0F;
      float floatValue10 = 0.0F;

      for (HUD.StateHolder4 c7 : (Iterable<HUD.StateHolder4>)list) {
         float floatValue11 = c7.animation.update(c7.flag ? 1.0F : 0.0F);
         if (!(floatValue11 < 0.01F)) {
            float floatValue12 = floatValue5 + floatValue6 + Draw2D.getTextWidth(c7.text, Fonts.SUPPLIER18, 8.25F) + 8.0F + Theme.getLabelWidth(StringUtil.formatTickDuration(c7.value, floatValue7), Fonts.SUPPLIER18);
            if (floatValue12 > floatValue9) {
               floatValue9 = floatValue12;
            }

            floatValue8 += 11.5F * floatValue11;
            if (floatValue11 > floatValue10) {
               floatValue10 = floatValue11;
            }
         }
      }

      float floatValue13 = Math.max(getTagWidth("Кулдауны"), floatValue9 + 8.0F);
      float floatValue14 = floatValue8 + 2.0F * floatValue10;
      float floatValue15 = this.animation4.update(floatValue13);
      if (!(floatValue15 < 5.0F)) {
         float floatValue16 = floatValue4 + 15.0F + 0.5F;
         m_9(this.theme6, this.hudStylePanel6, "Кулдауны", "\uf251", floatValue3, floatValue4, floatValue15, floatValue14);
         if (floatValue8 > 0.01F) {
            float floatValue17 = floatValue16 + (floatValue14 - floatValue8) / 2.0F;
            Render2D.pushScissor(floatValue3, floatValue16, floatValue15, floatValue14);

            for (HUD.StateHolder4 c72 : (Iterable<HUD.StateHolder4>)list) {
               float floatValue18 = c72.animation.update(c72.flag ? 1.0F : 0.0F);
               if (!(floatValue18 < 0.01F)) {
                  int value3 = (int)(255.0F * floatValue18);
                  float floatValue19 = 11.5F * floatValue18;
                  float floatValue20 = floatValue17 - (1.0F - floatValue18) * 11.5F;
                  float floatValue21 = floatValue3 + 4.0F;
                  TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
                  MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, c72.itemStack, ItemDisplayContext.GUI, MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, 0);
                  event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
                  Render2D.drawItem(trackingItemStackRenderState, floatValue21, floatValue20 + (floatValue19 - floatValue5) / 2.0F, floatValue5, ColorUtil.rgba(255, 255, 255, value3));
                  float floatValue22 = floatValue21 + floatValue5 + floatValue6;
                  Draw2D.drawStyledText(c72.text, Fonts.SUPPLIER18, floatValue22, Draw2D.getTextBaseline(Fonts.SUPPLIER18, 8.25F, floatValue20, floatValue19), this.theme6.m_8_int(value3), 8.25F);
                  if (this.hudStylePanel6.getActiveColor() == 2) {
                     Theme.m_3(floatValue22, floatValue20 + floatValue19 - 1.0F, Draw2D.getTextWidth(c72.text, Fonts.SUPPLIER18, 8.25F), ColorUtil.rgba(255, 255, 255, 230));
                  }

                  Theme.m_2(
                     StringUtil.formatTickDuration(c72.value, floatValue7),
                     Fonts.SUPPLIER18,
                     floatValue3 + floatValue15 - 4.0F,
                     floatValue20,
                     floatValue19,
                     ColorUtil.rgba(255, 255, 255, 26),
                     this.theme6.m_3_int(),
                     value3,
                     this.hudStylePanel6.getActiveColor() == 3
                  );
                  floatValue17 += 11.5F * floatValue18;
               }
            }

            Render2D.popScissor();
         }

         this.textField13.m_8_void(floatValue15, 15.0F + (floatValue14 > 0.01F ? 0.5F + floatValue14 : 0.0F));
      }
   }

   @EventTarget
   private void onAttackEntity(AttackEntityEvent event) {
      if (this.multiModeSetting.isSelected("Килфид") && event.getEntity() instanceof LivingEntity livingEntity2) {
         HUD.StateHolder2 c4 = this.integerMap.computeIfAbsent(livingEntity2.getId(), integer -> new HUD.StateHolder2());
         c4.text = livingEntity2.getName().getString();
         c4.gameProfile = livingEntity2 instanceof Player player ? player.getGameProfile() : null;
         c4.value++;
         c4.longValue = System.currentTimeMillis();
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundLoginPacket || event.getPacket() instanceof ClientboundRespawnPacket) {
         this.integerMap.clear();
      } else if (this.multiModeSetting.isSelected("Килфид") && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         long longValue4 = System.currentTimeMillis();
         if (event.getPacket() instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
            if (!clientboundSystemChatPacket.overlay() && !this.integerMap.isEmpty()) {
               String text3 = clientboundSystemChatPacket.content().getString();
               if (ChatUtil.containsWord(text3, MinecraftAccess.MINECRAFT.player.getName().getString())) {
                  for (int value3 : List.copyOf(this.integerMap.keySet())) {
                     String text4 = this.integerMap.get(value3).text;
                     if (text4 != null && ChatUtil.containsWord(text3, text4)) {
                        this.m_2(value3, 5000L, longValue4);
                     }
                  }
               }
            }
         } else if (event.getPacket() instanceof ClientboundEntityEventPacket clientboundEntityEventPacket) {
            if (clientboundEntityEventPacket.getEntity(MinecraftAccess.MINECRAFT.level) instanceof LivingEntity livingEntity2 && livingEntity2 != MinecraftAccess.MINECRAFT.player) {
               if (clientboundEntityEventPacket.getEventId() == 35) {
                  HUD.StateHolder2 c4 = this.integerMap.computeIfAbsent(livingEntity2.getId(), integer -> new HUD.StateHolder2());
                  c4.value2++;
                  c4.longValue = longValue4;
               } else if (clientboundEntityEventPacket.getEventId() == 3) {
                  this.m_2(livingEntity2.getId(), 30000L, longValue4);
               }
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (!this.integerMap.isEmpty() && MinecraftAccess.MINECRAFT.level != null) {
         long longValue4 = System.currentTimeMillis();
         this.integerMap.values().removeIf(c4 -> longValue4 - c4.longValue > 30000L);

         for (int value3 : List.copyOf(this.integerMap.keySet())) {
            if (MinecraftAccess.MINECRAFT.level.getEntity(value3) instanceof LivingEntity livingEntity2 && (livingEntity2.isDeadOrDying() || livingEntity2 instanceof Player player && player.isSpectator())) {
               this.m_2(value3, 30000L, longValue4);
            }
         }
      }
   }

   private void m_2(int value3, long longValue4, long longValue5) {
      HUD.StateHolder2 c4 = this.integerMap.remove(value3);
      if (c4 != null && c4.value != 0 && longValue5 - c4.longValue <= longValue4) {
         this.c8List.add(new HUD.DataRecord4(c4.text, c4.gameProfile, c4.value2, longValue5));
      }
   }

   private void m_3_void() {
      long longValue4 = System.currentTimeMillis();
      this.layoutField(this.textField14, 4.0F);
      float floatValue3 = this.textField14.m_5_float();
      float floatValue4 = this.textField14.m_8_float();
      this.applyScale(floatValue3, floatValue4);
      float floatValue5 = 9.0F;
      float floatValue6 = 3.0F;
      float floatValue7 = 2.0F;
      float floatValue8 = 0.0F;
      float floatValue9 = 0.0F;
      float floatValue10 = 0.0F;

      for (HUD.DataRecord4 c8 : this.c8List) {
         float floatValue11 = c8.f_5d.update(longValue4 - c8.f_0e < 50000L ? 1.0F : 0.0F);
         if (!(floatValue11 < 0.01F)) {
            float floatValue12 = floatValue5
               + floatValue6
               + Draw2D.getTextWidth(c8.f_4_n, Fonts.SUPPLIER18, 8.25F)
               + 8.0F
               + Theme.getLabelWidth(c8.m_40(), Fonts.SUPPLIER18)
               + floatValue7
               + Theme.getLabelWidth(c8.m_6_String(longValue4), Fonts.SUPPLIER18);
            if (floatValue12 > floatValue9) {
               floatValue9 = floatValue12;
            }

            floatValue8 += 11.5F * floatValue11;
            if (floatValue11 > floatValue10) {
               floatValue10 = floatValue11;
            }
         }
      }

      this.c8List.removeIf(c82 -> longValue4 - c82.f_0e >= 50000L && c82.f_5d.getValue() < 0.01F);
      float floatValue13 = Math.max(getTagWidth("Килфид"), floatValue9 + 8.0F);
      float floatValue14 = floatValue8 + 2.0F * floatValue10;
      float floatValue15 = this.animation5.update(floatValue13);
      if (!(floatValue15 < 5.0F)) {
         float floatValue16 = floatValue4 + 15.0F + 0.5F;
         m_9(this.theme7, this.hudStylePanel7, "Килфид", "\uf54c", floatValue3, floatValue4, floatValue15, floatValue14);
         if (floatValue8 > 0.01F) {
            float floatValue17 = floatValue16 + (floatValue14 - floatValue8) / 2.0F;
            Render2D.pushScissor(floatValue3, floatValue16, floatValue15, floatValue14);

            for (HUD.DataRecord4 c83 : this.c8List) {
               float floatValue18 = c83.f_5d.getValue();
               if (!(floatValue18 < 0.01F)) {
                  int value3 = (int)(255.0F * floatValue18);
                  float floatValue19 = 11.5F * floatValue18;
                  float floatValue20 = floatValue17 - (1.0F - floatValue18) * 11.5F;
                  float floatValue21 = floatValue3 + 4.0F;
                  float floatValue22 = floatValue20 + (floatValue19 - floatValue5) / 2.0F;
                  GpuTextureView gpuTextureView = c83.f_4_u != null ? PlayerFaceTexture.getFace(c83.f_4_u) : null;
                  if (gpuTextureView != null) {
                     Draw2D.m_4(floatValue21, floatValue22, floatValue5, floatValue5, floatValue5 / 2.0F - 0.5F, ColorUtil.withAlpha(-1, value3), gpuTextureView);
                  } else {
                     Draw2D.drawStyledText(
                        "\uf54c",
                        Fonts.SUPPLIER8,
                        Draw2D.getCenteredX("\uf54c", Fonts.SUPPLIER8, floatValue5 - 1.0F, floatValue21, floatValue5),
                        Draw2D.getCenteredY("\uf54c", Fonts.SUPPLIER8, floatValue5 - 1.0F, floatValue22, floatValue5),
                        this.theme7.m_8_int(value3),
                        floatValue5 - 1.0F
                     );
                  }

                  float floatValue23 = floatValue21 + floatValue5 + floatValue6;
                  Draw2D.drawStyledText(c83.f_4_n, Fonts.SUPPLIER18, floatValue23, Draw2D.getTextBaseline(Fonts.SUPPLIER18, 8.25F, floatValue20, floatValue19), this.theme7.m_8_int(value3), 8.25F);
                  if (this.hudStylePanel7.getActiveColor() == 2) {
                     Theme.m_3(floatValue23, floatValue20 + floatValue19 - 1.0F, Draw2D.getTextWidth(c83.f_4_n, Fonts.SUPPLIER18, 8.25F), ColorUtil.rgba(255, 255, 255, 230));
                  }

                  float floatValue24 = floatValue3 + floatValue15 - 4.0F;
                  boolean flag = this.hudStylePanel7.getActiveColor() == 3;
                  Theme.m_2(c83.m_6_String(longValue4), Fonts.SUPPLIER18, floatValue24, floatValue20, floatValue19, ColorUtil.rgba(255, 255, 255, 26), this.theme7.m_3_int(), value3, flag);
                  floatValue24 -= Theme.getLabelWidth(c83.m_6_String(longValue4), Fonts.SUPPLIER18) + floatValue7;
                  boolean flag2 = c83.m_8_x() > 0;
                  Theme.m_2(
                     c83.m_40(),
                     Fonts.SUPPLIER18,
                     floatValue24,
                     floatValue20,
                     floatValue19,
                     flag2 ? ColorUtil.rgba(255, 85, 85, 51) : ColorUtil.rgba(255, 255, 255, 26),
                     flag2 ? ColorUtil.rgba(255, 85, 85, 255) : this.theme7.m_3_int(),
                     value3,
                     flag
                  );
                  floatValue17 += 11.5F * floatValue18;
               }
            }

            Render2D.popScissor();
         }

         this.textField14.m_8_void(floatValue15, 15.0F + (floatValue14 > 0.01F ? 0.5F + floatValue14 : 0.0F));
      }
   }

   private HUD.DataRecord3 m_0(String text3, String text4, EnumSet<DayOfWeek> enumSet, LocalTime... localTimeArray) {
      BooleanSetting booleanSetting6 = new BooleanSetting(text3, true).visibleWhen(() -> this.multiModeSetting.isSelected("Расписание RW")).build();
      return new HUD.DataRecord3(text4, booleanSetting6, enumSet, List.of(localTimeArray), new Animation(250L, 0.0F, Easing.EASING8));
   }

   private static long m_2(HUD.DataRecord3 c5, ZonedDateTime zonedDateTime) {
      for (int i = 0; i <= 7; i++) {
         LocalDate localDate = zonedDateTime.toLocalDate().plusDays(i);
         if (c5.m_7_w().contains(localDate.getDayOfWeek())) {
            for (LocalTime localTime : c5.m_0o_List()) {
               ZonedDateTime zonedDateTime2 = localDate.atTime(localTime).atZone(zonedDateTime.getZone());
               if (zonedDateTime2.isAfter(zonedDateTime)) {
                  return Duration.between(zonedDateTime, zonedDateTime2).toSeconds();
               }
            }
         }
      }

      return Long.MAX_VALUE;
   }

   private static String formatUptime(long longValue4) {
      long longValue5 = longValue4 / 86400L;
      long longValue6 = longValue4 / 3600L % 24L;
      long longValue7 = longValue4 / 60L % 60L;
      long longValue8 = longValue4 % 60L;
      if (longValue5 > 0L) {
         return String.format(Locale.US, "%dд %dч", longValue5, longValue6);
      } else {
         return longValue6 > 0L ? String.format(Locale.US, "%dч %02dм", longValue6, longValue7) : String.format(Locale.US, "%d:%02d", longValue7, longValue8);
      }
   }

   private void m_0o_void() {
      this.layoutField(this.textField15, 4.0F);
      float floatValue3 = this.textField15.m_5_float();
      float floatValue4 = this.textField15.m_8_float();
      this.applyScale(floatValue3, floatValue4);
      ZonedDateTime zonedDateTime = ZonedDateTime.now(ZONE_ID);
      ArrayList list = new ArrayList();

      record C13(HUD.DataRecord3 f_5_q, long f_91, String f_5o) {
         public HUD.DataRecord3 m_5_q() {
            return this.f_5_q;
         }

         public long m_91() {
            return this.f_91;
         }

         public String m_5o() {
            return this.f_5o;
         }
      }

      for (HUD.DataRecord3 c5 : this.c5List) {
         long longValue4 = m_2(c5, zonedDateTime);
         list.add(new C13(c5, longValue4, formatUptime(longValue4)));
      }

      list.sort(Comparator.comparingLong(C13::m_91));
      float floatValue5 = 0.0F;
      float floatValue6 = 0.0F;
      float floatValue7 = 0.0F;

      for (C13 c13 : (Iterable<C13>)list) {
         float floatValue8 = c13.m_5_q().m_6_q_C8_e().update(c13.m_5_q().m_8h().getValue() ? 1.0F : 0.0F);
         if (!(floatValue8 < 0.01F)) {
            float floatValue9 = Draw2D.getTextWidth(c13.m_5_q().m_2_b(), Fonts.SUPPLIER18, 8.25F) + 8.0F + Theme.getLabelWidth(c13.m_5o(), Fonts.SUPPLIER18);
            if (floatValue9 > floatValue6) {
               floatValue6 = floatValue9;
            }

            floatValue5 += 11.5F * floatValue8;
            if (floatValue8 > floatValue7) {
               floatValue7 = floatValue8;
            }
         }
      }

      float floatValue10 = Math.max(getTagWidth("Расписание RW"), floatValue6 + 8.0F);
      float floatValue11 = floatValue5 + 2.0F * floatValue7;
      float floatValue12 = this.animation6.update(floatValue10);
      if (!(floatValue12 < 5.0F)) {
         float floatValue13 = floatValue4 + 15.0F + 0.5F;
         m_9(this.theme8, this.hudStylePanel8, "Расписание RW", "\uf133", floatValue3, floatValue4, floatValue12, floatValue11);
         if (floatValue5 > 0.01F) {
            float floatValue14 = floatValue13 + (floatValue11 - floatValue5) / 2.0F;
            Render2D.pushScissor(floatValue3, floatValue13, floatValue12, floatValue11);

            for (C13 c132 : (Iterable<C13>)list) {
               float floatValue15 = c132.m_5_q().m_6_q_C8_e().getValue();
               if (!(floatValue15 < 0.01F)) {
                  int value3 = (int)(255.0F * floatValue15);
                  float floatValue16 = 11.5F * floatValue15;
                  float floatValue17 = floatValue14 - (1.0F - floatValue15) * 11.5F;
                  float floatValue18 = floatValue3 + 4.0F;
                  Draw2D.drawStyledText(c132.m_5_q().m_2_b(), Fonts.SUPPLIER18, floatValue18, Draw2D.getTextBaseline(Fonts.SUPPLIER18, 8.25F, floatValue17, floatValue16), this.theme8.m_8_int(value3), 8.25F);
                  if (this.hudStylePanel8.getActiveColor() == 2) {
                     Theme.m_3(floatValue18, floatValue17 + floatValue16 - 1.0F, Draw2D.getTextWidth(c132.m_5_q().m_2_b(), Fonts.SUPPLIER18, 8.25F), ColorUtil.rgba(255, 255, 255, 230));
                  }

                  Theme.m_2(
                     c132.m_5o(),
                     Fonts.SUPPLIER18,
                     floatValue3 + floatValue12 - 4.0F,
                     floatValue17,
                     floatValue16,
                     c132.m_91() <= 300L ? ColorUtil.rgba(255, 85, 85, 51) : ColorUtil.rgba(255, 255, 255, 26),
                     c132.m_91() <= 300L ? ColorUtil.rgba(255, 85, 85, 255) : this.theme8.m_3_int(),
                     value3,
                     this.hudStylePanel8.getActiveColor() == 3
                  );
                  floatValue14 += 11.5F * floatValue15;
               }
            }

            Render2D.popScissor();
         }

         this.textField15.m_8_void(floatValue12, 15.0F + (floatValue11 > 0.01F ? 0.5F + floatValue11 : 0.0F));
      }
   }

   private TextureAtlasSprite getEffectSprite(Holder<MobEffect> holder) {
      return MinecraftAccess.MINECRAFT.getAtlasManager().getAtlasOrThrow(IDENTIFIER2).getSprite(Hud.getMobEffectSprite(holder));
   }

   private void m_0y_void() {
      record C19(HUD.DataRecord5 f_2w, float f_4_a, float f_4h, float f_4r, int f_6_l, int f_6r) {
         public HUD.DataRecord5 m_2w() {
            return this.f_2w;
         }

         public float m_4_a() {
            return this.f_4_a;
         }

         public float m_4h() {
            return this.f_4h;
         }

         public float m_4r() {
            return this.f_4r;
         }

         public int m_6_l() {
            return this.f_6_l;
         }

         public int m_6r() {
            return this.f_6r;
         }
      }

      List list = Onetap.MODULE_MANAGER
         .getModules()
         .stream()
         .filter(module -> !(module instanceof ClickGUI) && !(module instanceof HUD))
         .filter(module2 -> module2.getAnimationProgress() > 0.005F || module2.isEnabled())
         .toList();
      boolean flag = MinecraftAccess.MINECRAFT.gui.screen() instanceof ChatScreen;
      if (list.isEmpty() && !flag) {
         this.textField10.m_8_void(0.0F, 0.0F);
      } else {
         Supplier supplier = Fonts.SUPPLIER18;
         float floatValue3 = 7.5F;
         float floatValue4 = (Float)this.floatSetting3.getValue();
         float floatValue5 = 2.0F;
         float floatValue6 = this.booleanSetting4.getValue() ? 1.5F : 0.0F;
         boolean flag2 = (Boolean)this.booleanSetting3.getValue();
         boolean flag3 = (Boolean)this.booleanSetting5.getValue();
         ArrayList<HUD.DataRecord5> c9List = new ArrayList<>();
         if (!list.isEmpty()) {
            for (Module module3 : (Iterable<Module>)list) {
               float floatValue7 = module3.getAnimationProgress();
               if (!(floatValue7 < 0.005F) || module3.isEnabled()) {
                  String text3 = flag3 ? module3.getName().toLowerCase(Locale.ROOT) : module3.getName();
                  String text4 = getModeValue(module3);
                  if (flag3 && text4 != null) {
                     text4 = text4.toLowerCase(Locale.ROOT);
                  }

                  float floatValue8 = Draw2D.getTextWidth(text3, supplier, floatValue3);
                  float floatValue9 = text4 != null && !text4.isEmpty() ? Draw2D.getTextWidth(" " + text4, supplier, floatValue3) : 0.0F;
                  c9List.add(new HUD.DataRecord5(text3, text4, floatValue8 + floatValue9, floatValue8, floatValue7));
               }
            }
         } else {
            c9List.add(m_0("KillAura", "Sloth", floatValue3, flag3, supplier));
            c9List.add(m_0("NoSlow", "Grim", floatValue3, flag3, supplier));
            c9List.add(m_0("AutoTotem", null, floatValue3, flag3, supplier));
            c9List.add(m_0("Sprint", null, floatValue3, flag3, supplier));
         }

         c9List.sort((c9, c92) -> Float.compare(c92.m_48(), c9.m_48()));
         float floatValue10 = 0.0F;

         for (HUD.DataRecord5 c93 : c9List) {
            if (c93.m_48() > floatValue10) {
               floatValue10 = c93.m_48();
            }
         }

         float floatValue11 = floatValue10 + floatValue5 * 2.0F + floatValue6;
         if (!this.textField10.isFlag2()) {
            int value3 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth();
            this.textField10.m_2_void(value3 - floatValue11 * (Float)this.floatSetting.getValue() - 1.0F, 2.0F);
            this.textField10.setFlag2(false);
         }

         this.layoutField(this.textField10, 2.0F);
         float floatValue12 = MinecraftAccess.MINECRAFT.getWindow().getGuiScale();
         float floatValue13 = this.textField10.m_5_float();
         float floatValue14 = Math.round(this.textField10.m_8_float() * floatValue12) / floatValue12;
         this.applyScale(floatValue13, floatValue14);
         int value4 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth();
         boolean flag4 = floatValue13 + floatValue11 * (Float)this.floatSetting.getValue() / 2.0F > value4 / 2.0F;
         float floatValue15 = floatValue13 + floatValue11;
         ArrayList<C19> c19List = new ArrayList<>(c9List.size());
         float floatValue16 = floatValue12 * (Float)this.floatSetting.getValue();
         float floatValue17 = floatValue14;
         int value5 = 0;
         long longValue4 = System.currentTimeMillis();

         for (HUD.DataRecord5 c94 : c9List) {
            float floatValue18 = c94.m_4s();
            float floatValue19 = floatValue14 + Math.round((floatValue17 - floatValue14 + floatValue4 * floatValue18) * floatValue16) / floatValue16;
            if (floatValue19 <= floatValue17) {
               floatValue19 = floatValue17 + 1.0F / floatValue16;
            }

            float floatValue20 = floatValue19 - floatValue17;
            float floatValue21 = c94.m_48() + floatValue5 * 2.0F + floatValue6;
            float floatValue22 = (1.0F - floatValue18) * 16.0F;
            float floatValue23 = flag4 ? floatValue15 - floatValue21 + floatValue22 : floatValue13 - floatValue22;
            float floatValue24 = (float)Math.sin(longValue4 % 3500L / 3500.0 * Math.PI * 2.0 - value5 * 0.18) * 0.5F + 0.5F;
            int value6 = ColorUtil.lerpColor(ColorUtil.primaryColor(), ColorUtil.secondaryColor(), floatValue24);
            int value7 = (int)(255.0F * floatValue18);
            float floatValue25 = floatValue21 - floatValue6;
            if (flag2) {
               int value8 = ColorUtil.rgba(14, 14, 16, (int)(165.0F * floatValue18));
               if (flag4) {
                  Draw2D.m_6(floatValue23, floatValue17, floatValue25, floatValue20, 0.0F, 0.01F, value8);
               } else {
                  Draw2D.m_6(floatValue23 + floatValue6, floatValue17, floatValue25, floatValue20, 0.0F, 0.01F, value8);
               }
            }

            if (floatValue6 > 0.0F) {
               int value9 = ColorUtil.withAlpha(value6, value7);
               if (flag4) {
                  Draw2D.m_6(floatValue23 + floatValue25, floatValue17, floatValue6, floatValue20, 0.0F, 0.01F, value9);
               } else {
                  Draw2D.m_6(floatValue23, floatValue17, floatValue6, floatValue20, 0.0F, 0.01F, value9);
               }
            }

            c19List.add(new C19(c94, floatValue17, floatValue20, floatValue23, value6, value7));
            floatValue17 = floatValue19;
            value5++;
         }

         for (C19 c19 : c19List) {
            float floatValue26 = Draw2D.getTextBaseline(supplier, floatValue3, c19.f_4_a, c19.f_4h);
            float floatValue27 = flag4 ? c19.f_4r + floatValue5 : c19.f_4r + floatValue6 + floatValue5;
            Draw2D.drawStyledText(c19.f_2w.m_4c(), supplier, floatValue27, floatValue26, ColorUtil.withAlpha(c19.f_6_l, c19.f_6r), floatValue3);
            if (c19.f_2w.m_4_z_String() != null && !c19.f_2w.m_4_z_String().isEmpty()) {
               Draw2D.drawStyledText(
                  " " + c19.f_2w.m_4_z_String(), supplier, floatValue27 + c19.f_2w.m_4_f(), floatValue26, ColorUtil.rgba(155, 155, 155, (int)(185.0F * c19.f_2w.m_4s())), floatValue3
               );
            }
         }

         this.textField10.m_8_void(floatValue11, floatValue17 - floatValue14);
      }
   }

   private static HUD.DataRecord5 m_0(String text3, String text4, float floatValue3, boolean flag, Supplier<FontAtlas> supplier) {
      if (flag) {
         text3 = text3.toLowerCase(Locale.ROOT);
         if (text4 != null) {
            text4 = text4.toLowerCase(Locale.ROOT);
         }
      }

      float floatValue4 = Draw2D.getTextWidth(text3, supplier, floatValue3);
      float floatValue5 = floatValue4 + (text4 != null && !text4.isEmpty() ? Draw2D.getTextWidth(" " + text4, supplier, floatValue3) : 0.0F);
      return new HUD.DataRecord5(text3, text4, floatValue5, floatValue4, 1.0F);
   }

   private static String getModeValue(Module module) {
      for (Setting setting : module.getSettings()) {
         if (setting instanceof ModeSetting modeSetting4) {
            return (String)modeSetting4.getValue();
         }
      }

      return null;
   }

   private void renderArmor(Render2DEvent event) {
      ArrayList list = new ArrayList();

      for (EquipmentSlot equipmentSlot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
         ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getItemBySlot(equipmentSlot);
         if (!itemStack2.isEmpty()) {
            list.add(itemStack2);
         }
      }

      if (list.isEmpty()) {
         this.textField11.m_8_void(0.0F, 0.0F);
      } else {
         float floatValue3 = 3.0F;
         float floatValue4 = 16.0F;
         float floatValue5 = 18.0F;
         float floatValue6 = floatValue3 * 2.0F + list.size() * floatValue5 - (floatValue5 - floatValue4);
         float floatValue7 = floatValue3 * 2.0F + floatValue4;
         if (!this.textField11.isFlag2()) {
            int value3 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth();
            int value4 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight();
            this.textField11.m_2_void(value3 / 2.0F + 96.0F, value4 - floatValue7 * (Float)this.floatSetting.getValue() - 1.0F);
            this.textField11.setFlag2(false);
         }

         this.layoutField(this.textField11, 4.0F);
         float floatValue8 = this.textField11.m_5_float();
         float floatValue9 = this.textField11.m_8_float();
         this.applyScale(floatValue8, floatValue9);
         Draw2D.m_7(floatValue8, floatValue9, floatValue6, floatValue7, 4.0F, 4.0F, 4.0F, 4.0F, 0.0F, 3.0F, ColorUtil.rgba(125, 125, 125, 255));
         ArrayList list2 = new ArrayList(list.size());
         float floatValue10 = floatValue8 + floatValue3;

         record C22(TrackingItemStackRenderState f_1l, ItemStack f_9_d, float f_0v) {
            public TrackingItemStackRenderState m_1l_TrackingItemStackRenderState() {
               return this.f_1l;
            }

            public ItemStack m_9_d() {
               return this.f_9_d;
            }

            public float m_0v() {
               return this.f_0v;
            }
         }

         for (ItemStack itemStack3 : (Iterable<ItemStack>)list) {
            TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
            MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, itemStack3, ItemDisplayContext.GUI, MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, itemStack3.hashCode());
            event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
            list2.add(new C22(trackingItemStackRenderState, itemStack3, floatValue10));
            floatValue10 += floatValue5;
         }

         for (C22 c22 : (Iterable<C22>)list2) {
            Render2D.drawItem(c22.f_1l, c22.f_0v, floatValue9 + floatValue3, floatValue4, -1);
         }

         for (C22 c222 : (Iterable<C22>)list2) {
            Render2D.drawItemDurability(c222.f_9_d, c222.f_0v, floatValue9 + floatValue3, floatValue4, 255);
         }

         for (C22 c223 : (Iterable<C22>)list2) {
            Render2D.drawItemCount(c223.f_9_d, c223.f_0v, floatValue9 + floatValue3, floatValue4, 255);
         }

         this.textField11.m_8_void(floatValue6, floatValue7);
      }
   }

   private void m_0(Render2DEvent event, TextField textField18, Theme theme11, HudStylePanel hudStylePanel11, String text3, List<ItemStack> itemStackList, int value3) {
      this.layoutField(textField18, 4.0F);
      float floatValue3 = textField18.m_5_float();
      float floatValue4 = textField18.m_8_float();
      this.applyScale(floatValue3, floatValue4);
      byte byteValue = 9;
      float floatValue5 = 16.0F;
      float floatValue6 = 17.0F;
      float floatValue7 = byteValue * floatValue6 - (floatValue6 - floatValue5);
      float floatValue8 = value3 * floatValue6 - (floatValue6 - floatValue5);
      float floatValue9 = Math.max(getTagWidth(text3), floatValue7 + 8.0F);
      float floatValue10 = floatValue8 + 8.0F;
      m_9(theme11, hudStylePanel11, text3, "\uf06e", floatValue3, floatValue4, floatValue9, floatValue10);
      float floatValue11 = floatValue3 + (floatValue9 - floatValue7) / 2.0F;
      float floatValue12 = floatValue4 + 15.0F + 0.5F + 4.0F;
      ArrayList list = new ArrayList();

      record C14(TrackingItemStackRenderState f_15, ItemStack f_8q, float f_8z, float f_8t) {
         public TrackingItemStackRenderState m_15_TrackingItemStackRenderState() {
            return this.f_15;
         }

         public ItemStack m_8q_ItemStack() {
            return this.f_8q;
         }

         public float m_8z() {
            return this.f_8z;
         }

         public float m_8t() {
            return this.f_8t;
         }
      }

      for (int i = 0; i < value3; i++) {
         for (int i2 = 0; i2 < byteValue; i2++) {
            float floatValue13 = floatValue11 + i2 * floatValue6;
            float floatValue14 = floatValue12 + i * floatValue6;
            Draw2D.roundedRect(floatValue13, floatValue14, floatValue5, floatValue5, 3.0F, ColorUtil.rgba(255, 255, 255, 12));
            int value4 = i < 3 ? byteValue + i * byteValue + i2 : i2;
            ItemStack itemStack2 = value4 < itemStackList.size() ? (ItemStack)itemStackList.get(value4) : ItemStack.EMPTY;
            if (!itemStack2.isEmpty()) {
               TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
               MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, itemStack2, ItemDisplayContext.GUI, MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, itemStack2.hashCode());
               event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
               list.add(new C14(trackingItemStackRenderState, itemStack2, floatValue13, floatValue14));
            }
         }
      }

      for (C14 c14 : (Iterable<C14>)list) {
         Render2D.drawItem(c14.f_15, c14.f_8z, c14.f_8t, floatValue5, -1);
      }

      for (C14 c142 : (Iterable<C14>)list) {
         Render2D.drawItemDurability(c142.f_8q, c142.f_8z, c142.f_8t, floatValue5, 255);
      }

      for (C14 c143 : (Iterable<C14>)list) {
         Render2D.drawItemCount(c143.f_8q, c143.f_8z, c143.f_8t, floatValue5, 255);
      }

      textField18.m_8_void(floatValue9, 15.5F + floatValue10);
   }

   private void m_7_void() {
      float floatValue3 = (Float)this.floatSetting2.getValue();
      float floatValue4 = floatValue3 * 1.0839416F;
      this.textField12.m_8_void(floatValue4, floatValue3);
      if (!this.textField12.isFlag2()) {
         int value3 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledWidth();
         int value4 = MinecraftAccess.MINECRAFT.getWindow().getGuiScaledHeight();
         this.textField12.m_2_void(value3 - floatValue4 * (Float)this.floatSetting.getValue() - 8.0F, value4 - floatValue3 * (Float)this.floatSetting.getValue() - 40.0F);
         this.textField12.setFlag2(false);
      }

      this.layoutField(this.textField12, 4.0F);
      float floatValue5 = this.textField12.m_5_float();
      float floatValue6 = this.textField12.m_8_float();
      this.applyScale(floatValue5, floatValue6);
      int value5 = (int)(System.nanoTime() / 1.0E9 * 19.333F) % VALUE_ARRAY.length;
      int value6 = VALUE_ARRAY[value5];
      float floatValue7 = 0.2F;
      float floatValue8 = 0.2F;
      float floatValue9 = value6 % 5 * floatValue7;
      float floatValue10 = value6 / 5 * floatValue8;
      GpuTextureView gpuTextureView = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(IDENTIFIER).getTextureView();
      Draw2D.m_0(floatValue5, floatValue6, floatValue4, floatValue3, -1, gpuTextureView, floatValue9, floatValue10, floatValue9 + floatValue7, floatValue10 + floatValue8);
   }

   private void m_7(float floatValue3, float floatValue4, boolean flag, int value3) {
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      int value4 = flag ? threadLocalRandom.nextInt(6, 10) : threadLocalRandom.nextInt(1, 3);

      for (int i = 0; i < value4 && this.c1List.size() < 48; i++) {
         HUD.StateHolder c1 = new HUD.StateHolder();
         c1.floatValue = floatValue3 + (threadLocalRandom.nextFloat() - 0.5F) * 1.2F;
         c1.floatValue2 = 0.8F + threadLocalRandom.nextFloat() * Math.max(0.5F, floatValue4 - 1.6F);
         c1.floatValue3 = (threadLocalRandom.nextFloat() - 0.45F) * 0.45F;
         c1.floatValue4 = 0.08F + threadLocalRandom.nextFloat() * 0.42F;
         c1.floatValue5 = 1.15F + threadLocalRandom.nextFloat() * 0.75F;
         c1.longValue = System.currentTimeMillis();
         c1.value = threadLocalRandom.nextInt(600, 950);
         c1.value2 = value3;
         this.c1List.add(c1);
      }
   }

   private void m_8(float floatValue3, float floatValue4, float floatValue5, long longValue4) {
      float floatValue6 = this.longValue3 == 0L ? 0.016F : Math.min((float)(longValue4 - this.longValue3) / 1000.0F, 0.05F);
      this.longValue3 = longValue4;
      if (!this.c1List.isEmpty()) {
         float floatValue7 = floatValue6 * 60.0F;
         var iterator = this.c1List.iterator();

         while (iterator.hasNext()) {
            HUD.StateHolder c1 = (HUD.StateHolder)iterator.next();
            float floatValue8 = (float)(longValue4 - c1.longValue) / c1.value;
            if (floatValue8 >= 1.0F) {
               iterator.remove();
            } else {
               c1.floatValue = c1.floatValue + c1.floatValue3 * floatValue7;
               c1.floatValue2 = c1.floatValue2 + c1.floatValue4 * floatValue7;
               c1.floatValue4 += 0.03F * floatValue7;
               c1.floatValue3 = c1.floatValue3 * (float)Math.pow(0.96, floatValue7);
               float floatValue9 = 1.0F - floatValue8;
               float floatValue10 = c1.floatValue5 * (0.3F + 0.7F * floatValue9);
               int value3 = (int)(220.0F * floatValue9 * floatValue5);
               if (value3 >= 3) {
                  int value4 = ColorUtil.lerpColor(c1.value2, -1, 0.35F);
                  Draw2D.roundedRect(floatValue3 + c1.floatValue - floatValue10 * 0.5F, floatValue4 + c1.floatValue2 - floatValue10 * 0.5F, floatValue10, floatValue10, floatValue10 * 0.5F, ColorUtil.withAlpha(value4, value3));
               }
            }
         }
      }
   }

   static {
      m_2(new int[1]);
   }

   public static void m_2(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_9_IArr() {
      return valueArray;
   }

   private record DataRecord(String f_4g, String f_4_d, String f_4_x, boolean f_68, TextureAtlasSprite f_1q, int f_7e, int f_7z) {
      public String m_4g() {
         return this.f_4g;
      }

      public String m_4_d_String() {
         return this.f_4_d;
      }

      public String m_4_x() {
         return this.f_4_x;
      }

      public boolean m_68() {
         return this.f_68;
      }

      public TextureAtlasSprite m_1q_TextureAtlasSprite() {
         return this.f_1q;
      }

      public int m_7e_int() {
         return this.f_7e;
      }

      public int m_7z() {
         return this.f_7z;
      }
   }

   private static final class StateHolder {
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public float floatValue4;
      public float floatValue5;
      public long longValue;
      public int value;
      public int value2;
   }

   private record DataRecord2(String f_47, int f_6l, float f_90) {
      public String m_47() {
         return this.f_47;
      }

      public int m_6l() {
         return this.f_6l;
      }

      public float m_90() {
         return this.f_90;
      }
   }

   private static final class StateHolder2 {
      public String text;
      public GameProfile gameProfile;
      public int value;
      public int value2;
      public long longValue;
   }

   private record DataRecord3(String f_2_b, BooleanSetting f_8h, EnumSet<DayOfWeek> f_7_w, List<LocalTime> f_0o, Animation f_6_q) {
      public String m_2_b() {
         return this.f_2_b;
      }

      public BooleanSetting m_8h() {
         return this.f_8h;
      }

      public EnumSet<DayOfWeek> m_7_w() {
         return this.f_7_w;
      }

      public List<LocalTime> m_0o_List() {
         return this.f_0o;
      }

      public Animation m_6_q_C8_e() {
         return this.f_6_q;
      }
   }

   private static final class StateHolder3 {
      public final String text;
      public final Animation animation;
      public boolean flag;
      public boolean flag2;
      public int value;
      public PlayerInfo playerInfo;
      public TextRenderer textRenderer;
      public Component component;
      public GameProfile gameProfile;
      public long longValue;

      public StateHolder3(String text3) {
         this.animation = new Animation(250L, 0.0F, Easing.EASING8);
         this.text = text3;
      }

      public GameProfile m_6_GameProfile() {
         if (this.gameProfile == null) {
            this.gameProfile = new GameProfile(UUID.nameUUIDFromBytes(this.text.getBytes(StandardCharsets.UTF_8)), this.text);
         }

         return this.gameProfile;
      }
   }

   private static final class StateHolder4 {
      public final Animation animation;
      public ItemStack itemStack;
      public String text;
      public int value;
      public boolean flag;

      private StateHolder4() {
         this.animation = new Animation(250L, 0.0F, Easing.EASING8);
      }
   }

   private record DataRecord4(String f_4_n, GameProfile f_4_u, int f_8_x, long f_0e, Animation f_5d) {
      public DataRecord4(String text3, GameProfile gameProfile, int value3, long longValue4) {
         this(text3, gameProfile, value3, longValue4, new Animation(250L, 0.0F, Easing.EASING8));
      }

      public String m_40() {
         return this.f_8_x + " тот.";
      }

      public String m_6_String(long longValue4) {
         return Math.min((longValue4 - this.f_0e) / 1000L, 50L) + "с";
      }

      public String m_4_n() {
         return this.f_4_n;
      }

      public GameProfile m_4_u() {
         return this.f_4_u;
      }

      public int m_8_x() {
         return this.f_8_x;
      }

      public long m_0e() {
         return this.f_0e;
      }

      public Animation m_5d() {
         return this.f_5d;
      }

      private static String a(byte[] byteArray) {
         int value3 = 0;
         int value4;
         char[] charArray = new char[value4 = byteArray.length];

         for (int i = 0; i < value4; i++) {
            int value5;
            if ((value5 = 255 & byteArray[i]) < 192) {
               charArray[value3++] = (char)value5;
            } else if (value5 < 224) {
               char ch = (char)((char)(value5 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value3++] = ch;
            } else if (i < value4 - 2) {
               char ch2 = (char)((char)(value5 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value3++] = ch2;
            }
         }

         return new String(charArray, 0, value3);
      }
   }

   private record DataRecord5(String f_4c, String f_4_z, float f_48, float f_4_f, float f_4s) {
      public String m_4c() {
         return this.f_4c;
      }

      public String m_4_z_String() {
         return this.f_4_z;
      }

      public float m_48() {
         return this.f_48;
      }

      public float m_4_f() {
         return this.f_4_f;
      }

      public float m_4s() {
         return this.f_4s;
      }
   }
}
