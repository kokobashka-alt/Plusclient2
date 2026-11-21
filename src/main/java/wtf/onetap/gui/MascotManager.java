package wtf.onetap.gui;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.figuramc.figura.FiguraMod;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.lua.api.keybind.FiguraKeybind;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.TickEvent;
import wtf.onetap.setting.KeybindSetting;

public class MascotManager {
   private static final MascotManager.DataRecord[] C1_ARRAY = new MascotManager.DataRecord[]{new MascotManager.DataRecord("custom", null), new MascotManager.DataRecord("blue_archive", "Blue Archive")};
   private final Path path;
   private final Path path2;
   private final Path path3;
   private volatile List<MascotManager.StateHolder> c8List;
   private volatile boolean flag;
   private boolean flag2;
   private String text;
   private final KeybindSetting keybindSetting;
   private FiguraKeybind figuraKeybind;
   private int value;
   private Object object;
   private Path path4;
   private static String text2;

   public MascotManager() {
      this.path = FabricLoader.getInstance().getGameDir().resolve("onetap");
      this.path2 = this.path.resolve("figura");
      this.path3 = this.path.resolve("model.json");
      this.c8List = List.of();
      this.keybindSetting = new KeybindSetting("Колесо эмоций", -1);
   }

   public void m_4_void() {
      this.m_5_void();
      Onetap.EVENT_BUS.register(this);
      Onetap.EVENT_BUS.register(new FiguraPanel());
      ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> AvatarManager.clearAvatars(FiguraMod.getLocalPlayerUUID()));
      Thread.startVirtualThread(() -> {
         try {
            this.m_6_void();
            this.c8List = this.m_5_List();
            this.flag = true;
         } catch (IOException e) {
            Onetap.LOGGER.error("[Onetap] Failed to unpack Figura avatars", e);
         }
      });
   }

   public List<MascotManager.StateHolder> getC8List() {
      return this.c8List;
   }

   public boolean m_0(MascotManager.StateHolder c8) {
      return c8 == null ? this.text == null : c8.text.equals(this.text);
   }

   public void m_7(MascotManager.StateHolder c8) {
      String text3 = c8 == null ? null : c8.text;
      if (!Objects.equals(text3, this.text)) {
         this.text = text3;
         this.m_7_void();
      }
   }

   public List<FiguraKeybind> m_4_List() {
      return this.m_0_List().stream().filter(FiguraPanel::isKnownKeybind).toList();
   }

   private List<FiguraKeybind> m_0_List() {
      if (this.text == null) {
         return List.of();
      }

      Avatar avatar = AvatarManager.getLoadedAvatar(FiguraMod.getLocalPlayerUUID());
      return avatar != null && avatar.luaRuntime != null ? avatar.luaRuntime.keybinds.keyBindings : List.of();
   }

   public KeybindSetting getKeybindSetting() {
      return this.keybindSetting;
   }

   public void m_2(FiguraKeybind figuraKeybind2) {
      if (this.figuraKeybind != null) {
         this.figuraKeybind.setDown(false, 0);
      }

      figuraKeybind2.setDown(true, 0);
      this.figuraKeybind = figuraKeybind2;
      this.value = (int)5527444668809216002L;
   }

   private void m_2_void() {
      for (FiguraKeybind figuraKeybind2 : this.m_0_List()) {
         if (!figuraKeybind2.getKey().equals(InputConstants.UNKNOWN.getName())) {
            figuraKeybind2.setKey(InputConstants.UNKNOWN);
         }
      }
   }

   @EventTarget
   public void onTick(TickEvent event) {
      Minecraft mc = Minecraft.getInstance();
      if (!this.flag2) {
         this.flag2 = true;
         m_9(mc);
      }

      if (this.figuraKeybind != null && --this.value <= 0) {
         this.figuraKeybind.setDown(false, 0);
         this.figuraKeybind = null;
      }

      if (this.flag && mc.player != null) {
         this.m_2_void();
         ClientPacketListener clientPacketListener = mc.getConnection();
         Path path5 = this.text == null ? null : this.path2.resolve(this.text);
         if (path5 != null && !Files.isRegularFile(path5.resolve("avatar.json"))) {
            path5 = null;
         }

         if (clientPacketListener != this.object || !Objects.equals(path5, this.path4)) {
            if (path5 != null) {
               AvatarManager.loadLocalAvatar(path5);
            } else if (this.path4 != null && clientPacketListener == this.object) {
               AvatarManager.clearAvatars(mc.player.getUUID());
            }

            this.object = clientPacketListener;
            this.path4 = path5;
         }
      }
   }

   private static void m_9(Minecraft mc) {
      boolean flag3 = false;

      for (KeyMapping keyMapping : mc.options.keyMappings) {
         if (keyMapping.getName().startsWith("figura.") && keyMapping.isDefault() && !keyMapping.isUnbound()) {
            keyMapping.setKey(InputConstants.UNKNOWN);
            flag3 = true;
         }
      }

      if (flag3) {
         KeyMapping.resetMapping();
         mc.options.save();
      }
   }

   private void m_6_void() throws java.io.IOException {
      m_2_String();
      Path path5 = this.path2.resolve(".version");
      if (!Files.isRegularFile(path5) || !"6".equals(Files.readString(path5).trim())) {
         for (MascotManager.DataRecord c1 : C1_ARRAY) {
            String text3 = "assets/onetap/figura/" + c1.m_9_m() + ".zip";
            Path path6 = FabricLoader.getInstance()
               .getModContainer("onetap")
               .flatMap(modContainer -> modContainer.findPath(text3))
               .orElseThrow(() -> new IOException("Missing " + text3));
            Path path7 = this.path2.resolve(c1.m_9_m());
            Files.createDirectories(path7);

            ZipEntry zipEntry;
            try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(path6))) {
               while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                  Path path8 = path7.resolve(zipEntry.getName()).normalize();
                  if (path8.startsWith(path7)) {
                     if (zipEntry.isDirectory()) {
                        Files.createDirectories(path8);
                     } else {
                        Files.createDirectories(path8.getParent());
                        Files.copy(zipInputStream, path8, StandardCopyOption.REPLACE_EXISTING);
                     }
                  }
               }
            }
         }

         Files.writeString(path5, "6");
      }
   }

   private List<MascotManager.StateHolder> m_5_List() throws java.io.IOException {
      ArrayList list = new ArrayList();

      for (MascotManager.DataRecord c1 : C1_ARRAY) {
         List list2;
         try (Stream stream = Files.list(this.path2.resolve(c1.m_9_m()))) {
            list2 = stream.filter(object2 -> Files.isRegularFile(((java.nio.file.Path)object2).resolve("avatar.json"))).sorted().toList();
         }

         for (Path path5 : (Iterable<Path>)list2) {
            list.add(new MascotManager.StateHolder(c1, path5));
         }
      }

      return List.copyOf(list);
   }

   private void m_5_void() {
      if (Files.exists(this.path3)) {
         try (BufferedReader bufferedReader = Files.newBufferedReader(this.path3, StandardCharsets.UTF_8)) {
            JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();
            this.text = jsonObject.has("model") ? jsonObject.get("model").getAsString() : null;
            if (jsonObject.has("wheel")) {
               this.keybindSetting.setValue(jsonObject.get("wheel").getAsInt());
            }
         } catch (IOException | RuntimeException e) {
            Onetap.LOGGER.error("[Onetap] Failed to load model", e);
         }
      }
   }

   public void m_7_void() {
      try {
         Files.createDirectories(this.path);
         JsonObject jsonObject = new JsonObject();
         if (this.text != null) {
            jsonObject.addProperty("model", this.text);
         }

         jsonObject.addProperty("wheel", (Number)this.keybindSetting.getValue());

         try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.path3, StandardCharsets.UTF_8)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject, bufferedWriter);
         }
      } catch (IOException e) {
         Onetap.LOGGER.error("[Onetap] Failed to save model", e);
      }
   }

   static {
      m_3_void(null);
   }

   public static void m_3_void(String text3) {
      text2 = text3;
   }

   public static String m_2_String() {
      return text2;
   }

   private record DataRecord(String f_9_m, String f_96) {
      public String m_9_m() {
         return this.f_9_m;
      }

      public String m_96_String() {
         return this.f_96;
      }
   }

   public static final class StateHolder {
      private final String text;
      private final String text2;
      private final String text3;
      private final Path path;
      private DynamicTexture dynamicTexture;
      private boolean flag;

      private StateHolder(MascotManager.DataRecord c1, Path path5) {
         this.path = path5;
         this.text3 = c1.m_96_String();
         String text4 = path5.getFileName().toString();
         this.text = c1.m_9_m() + "/" + text4;
         int value2 = text4.indexOf(95);
         this.text2 = (value2 < 0 ? text4 : text4.substring(value2 + 1)).replace('_', ' ');
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public GpuTextureView m_9_GpuTextureView() {
         if (this.dynamicTexture == null && !this.flag) {
            try {
               this.dynamicTexture = new DynamicTexture(() -> "Onetap Figura " + this.text, NativeImage.read(Files.readAllBytes(this.path.resolve("avatar.png"))));
               this.dynamicTexture.upload();
            } catch (IOException e) {
               this.flag = true;
               Onetap.LOGGER.warn("[Onetap] Failed to load thumbnail of {}", this.text, e);
            }
         }

         return this.dynamicTexture != null ? this.dynamicTexture.getTextureView() : null;
      }
   }
}
