package wtf.onetap.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import wtf.onetap.annotation.EraseAfterRun;
import wtf.onetap.module.combat.AntiBot;
import wtf.onetap.module.combat.AntiThorns;
import wtf.onetap.module.combat.AutoAnchor;
import wtf.onetap.module.combat.AutoCart;
import wtf.onetap.module.combat.AutoEXP;
import wtf.onetap.module.combat.AutoExplosion;
import wtf.onetap.module.combat.AutoMace;
import wtf.onetap.module.combat.AutoPotion;
import wtf.onetap.module.combat.AutoSwap;
import wtf.onetap.module.combat.AutoTotem;
import wtf.onetap.module.combat.AutoWeb;
import wtf.onetap.module.combat.BackTrack;
import wtf.onetap.module.combat.CrystalAura;
import wtf.onetap.module.combat.ElytraTarget;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.module.combat.LopataRecorder;
import wtf.onetap.module.combat.NoEntityTrace;
import wtf.onetap.module.combat.NoFriendDamage;
import wtf.onetap.module.combat.NoVelocity;
import wtf.onetap.module.combat.PacketCriticals;
import wtf.onetap.module.combat.ProjectHelper;
import wtf.onetap.module.combat.SyncTPS;
import wtf.onetap.module.combat.TargetPearl;
import wtf.onetap.module.misc.AhHelper;
import wtf.onetap.module.misc.Assistent;
import wtf.onetap.module.misc.AutoAuth;
import wtf.onetap.module.misc.AutoEvent;
import wtf.onetap.module.misc.AutoJoiner;
import wtf.onetap.module.misc.BaseFinder;
import wtf.onetap.module.misc.ChatCorrector;
import wtf.onetap.module.misc.ClientSounds;
import wtf.onetap.module.misc.Collector;
import wtf.onetap.module.misc.CreamyKeys;
import wtf.onetap.module.misc.DiscordRPC;
import wtf.onetap.module.misc.FakePlayer;
import wtf.onetap.module.misc.FreeCam;
import wtf.onetap.module.misc.ItemScroller;
import wtf.onetap.module.misc.KillMessage;
import wtf.onetap.module.misc.LeaveTracker;
import wtf.onetap.module.misc.NoInteract;
import wtf.onetap.module.misc.Nuker;
import wtf.onetap.module.misc.PvPSafe;
import wtf.onetap.module.misc.RpHelper;
import wtf.onetap.module.misc.ScoreboardHealth;
import wtf.onetap.module.misc.TapeMouse;
import wtf.onetap.module.misc.UnHook;
import wtf.onetap.module.misc.UseTracker;
import wtf.onetap.module.misc.XCarry;
import wtf.onetap.module.movement.AirStuck;
import wtf.onetap.module.movement.AntiLevitation;
import wtf.onetap.module.movement.AutoJump;
import wtf.onetap.module.movement.Blink;
import wtf.onetap.module.movement.DragonFly;
import wtf.onetap.module.movement.ElytraBooster;
import wtf.onetap.module.movement.ElytraMotion;
import wtf.onetap.module.movement.ElytraResolver;
import wtf.onetap.module.movement.GrimGlide;
import wtf.onetap.module.movement.GuiMove;
import wtf.onetap.module.movement.NoClip;
import wtf.onetap.module.movement.NoSlow;
import wtf.onetap.module.movement.NoWeb;
import wtf.onetap.module.movement.Speed;
import wtf.onetap.module.movement.Sprint;
import wtf.onetap.module.movement.Timer;
import wtf.onetap.module.movement.WaterSpeed;
import wtf.onetap.module.player.AirPlace;
import wtf.onetap.module.player.AntiAFK;
import wtf.onetap.module.player.AnyDeskExploit;
import wtf.onetap.module.player.AreaBreak;
import wtf.onetap.module.player.AutoAccept;
import wtf.onetap.module.player.AutoDuel;
import wtf.onetap.module.player.AutoEat;
import wtf.onetap.module.player.AutoFish;
import wtf.onetap.module.player.AutoLeave;
import wtf.onetap.module.player.AutoPilot;
import wtf.onetap.module.player.AutoTool;
import wtf.onetap.module.player.ChestStealer;
import wtf.onetap.module.player.ClanInvest;
import wtf.onetap.module.player.ClanUpgrade;
import wtf.onetap.module.player.ClickPearl;
import wtf.onetap.module.player.ClickThrough;
import wtf.onetap.module.player.CrystalOptimizer;
import wtf.onetap.module.player.DeathCoords;
import wtf.onetap.module.player.ElytraSwap;
import wtf.onetap.module.player.FastBreak;
import wtf.onetap.module.player.InvseeExploit;
import wtf.onetap.module.player.ItemFix;
import wtf.onetap.module.player.ItemHelper;
import wtf.onetap.module.player.ItemsCooldown;
import wtf.onetap.module.player.LockSlot;
import wtf.onetap.module.player.MoreDistance;
import wtf.onetap.module.player.NoCommands;
import wtf.onetap.module.player.NoDelay;
import wtf.onetap.module.player.NoFall;
import wtf.onetap.module.player.NoPlaceSphere;
import wtf.onetap.module.player.NoPush;
import wtf.onetap.module.player.PotionSwap;
import wtf.onetap.module.player.RPSpoofer;
import wtf.onetap.module.player.SendCoords;
import wtf.onetap.module.player.StaffExploit;
import wtf.onetap.module.player.VoiceChat;
import wtf.onetap.module.pve.ActionRecorder;
import wtf.onetap.module.pve.AutoAppleFarm;
import wtf.onetap.module.pve.AutoBuy;
import wtf.onetap.module.pve.AutoFarm;
import wtf.onetap.module.pve.AutoMine;
import wtf.onetap.module.pve.AutoResell;
import wtf.onetap.module.pve.AutoSell;
import wtf.onetap.module.pve.AutoWarden;
import wtf.onetap.module.pve.CacaoFarm;
import wtf.onetap.module.pve.FunPaySell;
import wtf.onetap.module.pve.MineHelper;
import wtf.onetap.module.pve.MoneyFarm;
import wtf.onetap.module.render.ArmorDurability;
import wtf.onetap.module.render.Arrows;
import wtf.onetap.module.render.AspectRatio;
import wtf.onetap.module.render.Atmosphere;
import wtf.onetap.module.render.BetterMinecraft;
import wtf.onetap.module.render.BlockESP;
import wtf.onetap.module.render.BlockOutline;
import wtf.onetap.module.render.ChunkAnimator;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.module.render.Crosshair;
import wtf.onetap.module.render.DamageNumbers;
import wtf.onetap.module.render.Dro4ka;
import wtf.onetap.module.render.ESP;
import wtf.onetap.module.render.FireworkESP;
import wtf.onetap.module.render.FreeLook;
import wtf.onetap.module.render.FullBright;
import wtf.onetap.module.render.GlowESP;
import wtf.onetap.module.render.HUD;
import wtf.onetap.module.render.HandGlow;
import wtf.onetap.module.render.Hat;
import wtf.onetap.module.render.HitGhosts;
import wtf.onetap.module.render.HitIndicator;
import wtf.onetap.module.render.ItemPhysics;
import wtf.onetap.module.render.JumpCircles;
import wtf.onetap.module.render.KillEffects;
import wtf.onetap.module.render.NameProtect;
import wtf.onetap.module.render.NoRender;
import wtf.onetap.module.render.Notifications;
import wtf.onetap.module.render.Particles;
import wtf.onetap.module.render.Saturation;
import wtf.onetap.module.render.SeeInvisibles;
import wtf.onetap.module.render.ShaderSky;
import wtf.onetap.module.render.ShulkerView;
import wtf.onetap.module.render.SmoothCamera;
import wtf.onetap.module.render.SwingAnimation;
import wtf.onetap.module.render.Tags;
import wtf.onetap.module.render.TargetESP;
import wtf.onetap.module.render.TimeChanger;
import wtf.onetap.module.render.TotemAngel;
import wtf.onetap.module.render.TotemPop;
import wtf.onetap.module.render.Tracers;
import wtf.onetap.module.render.Trajectories;
import wtf.onetap.module.render.ClawdPet;
import wtf.onetap.module.render.UseIndicator;
import wtf.onetap.module.render.ViewModel;
import wtf.onetap.module.render.WaterSplash;
import wtf.onetap.module.render.Weather;
import wtf.onetap.module.render.Wings;
import wtf.onetap.module.render.Zoom;
import wtf.onetap.module.combat.AimAssist;
import wtf.onetap.module.combat.TriggerBot;
import wtf.onetap.module.combat.AntiTotemExploit;

public class ModuleManager {
   private final List<Module> modules;
   private final Map<Class<?>, Module> modulesByClass;
   private static String[] textArray;

   public ModuleManager() {
      this.modules = new ArrayList<>();
      this.modulesByClass = new HashMap<>();
   }

   public <T extends Module> T get(Class<T> clazz) {
      return (T)this.modulesByClass.get(clazz);
   }

   public void register(Module... moduleArray) {
      Module[] moduleArray2 = moduleArray;
      int value = moduleArray2.length;
      m_1_StringArr();

      for (int i = 0; i < value; i++) {
         Module module = moduleArray2[i];
         module.finishSettings();
         this.modulesByClass.put(module.getClass(), module);
         this.modules.add(module);
      }
   }

   public List<Module> getModules() {
      return this.modules;
   }

   public List<Module> getByCategory(Category category) {
      return this.modules.stream().filter(module -> module.getCategory() == category).toList();
   }

   public Optional<Module> findByName(String text) {
      return this.modules.stream().filter(module -> module.getName().equalsIgnoreCase(text)).findFirst();
   }

   @EraseAfterRun
   public void registerAll() {
      m_1_StringArr();
      this.register(
         new HUD(),
         new Particles(),
         new Notifications(),
         new NoWeb(),
         new Speed(),
         new WaterSpeed(),
         new Sprint(),
         new Timer(),
         new GuiMove(),
         new ClickGUI(),
         new FullBright(),
         new NoSlow(),
         new DiscordRPC(),
         new FakePlayer(),
         new FreeCam(),
         new ClanUpgrade(),
         new AntiBot(),
         new KillAura(),
         new PacketCriticals(),
         new NoFriendDamage(),
         new NoEntityTrace(),
         new LopataRecorder(),
         new ElytraTarget(),
         new ProjectHelper(),
         new AutoExplosion(),
         new SyncTPS(),
         new NoDelay(),
         new NoPush(),
         new NoFall(),
         new AirStuck(),
         new NoClip(),
         new ElytraSwap(),
         new AntiLevitation(),
         new ElytraBooster(),
         new ElytraMotion(),
         new ElytraResolver(),
         new AutoJump(),
         new DragonFly(),
         new NoRender(),
         new Tags(),
         new TargetESP(),
         new UseIndicator(),
         new ClawdPet(),
         new WatermarkHud(),
         new CoordinatesHud(),
         new SpeedHud(),
         new TotemCounterHud(),
         new NursultanHud(),
         new ClickPearl(),
         new AutoSwap(),
         new AutoTotem(),
         new AutoMace(),
         new ESP(),
         new AspectRatio(),
         new SeeInvisibles(),
         new DeathCoords(),
         new LeaveTracker(),
         new AutoMine(),
         new AutoWarden(),
         new ScoreboardHealth(),
         new ItemScroller(),
         new AhHelper(),
         new Collector(),
         new NoVelocity(),
         new AntiThorns(),
         new AutoBuy(),
         new AutoSell(),
         new MoneyFarm(),
         new AutoFarm(),
         new AutoResell(),
         new CacaoFarm(),
         new AutoAppleFarm(),
         new ActionRecorder(),
         new UseTracker(),
         new KillMessage(),
         new Assistent(),
         new AutoEvent(),
         new MineHelper(),
         new ClientSounds(),
         new CreamyKeys(),
         new UnHook(),
         new RpHelper(),
         new TargetPearl(),
         new AutoCart(),
         new ChestStealer(),
         new ItemFix(),
         new AutoTool(),
         new ItemsCooldown(),
         new AnyDeskExploit(),
         new PotionSwap(),
         new AutoEat(),
         new CrystalOptimizer(),
         new SwingAnimation(),
         new ViewModel(),
         new Crosshair(),
         new FireworkESP(),
         new FreeLook(),
         new SmoothCamera(),
         new Zoom(),
         new ItemPhysics(),
         new AirPlace(),
         new HitIndicator(),
         new ShulkerView(),
         new Wings(),
         new Tracers(),
         new TotemAngel(),
         new TotemPop(),
         new Hat(),
         new HitGhosts(),
         new Trajectories(),
         new GlowESP(),
         new Arrows(),
         new HandGlow(),
         new Atmosphere(),
         new Dro4ka(),
         new Weather(),
         new TimeChanger(),
         new Saturation(),
         new ShaderSky(),
         new DamageNumbers(),
         new KillEffects(),
         new JumpCircles(),
         new ChunkAnimator(),
         new WaterSplash(),
         new BetterMinecraft(),
         new BlockOutline(),
         new BlockESP(),
         new BaseFinder(),
         new AutoLeave(),
         new AutoAccept(),
         new AutoDuel(),
         new StaffExploit(),
         new InvseeExploit(),
         new ClickThrough(),
         new MoreDistance(),
         new ItemHelper(),
         new ClanInvest(),
         new NameProtect(),
         new TapeMouse(),
         new AutoEXP(),
         new AutoPotion(),
         new LockSlot(),
         new Nuker(),
         new ArmorDurability(),
         new CrystalAura(),
         new AutoAnchor(),
         new AutoWeb(),
         new NoPlaceSphere(),
         new FastBreak(),
         new AreaBreak(),
         new BackTrack(),
         new SendCoords(),
         new VoiceChat(),
         new NoInteract(),
         new NoCommands(),
         new ChatCorrector(),
         new FunPaySell(),
         new GrimGlide(),
         new Blink(),
         new PvPSafe(),
         new AutoAuth(),
         new XCarry(),
         new RPSpoofer(),
         new AntiAFK(),
         new AutoPilot(),
         new AutoFish(),
         new AutoJoiner(),
         new AimAssist(),
         new TriggerBot(),
         new AntiTotemExploit()
      );
   }

   public static void m_3(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_1_StringArr() {
      return textArray;
   }
}
