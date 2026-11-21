package wtf.onetap.render;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import wtf.onetap.Onetap;

public class PlayerFaceTexture {
   private static final Map<UUID, DynamicTexture> U_U_I_D_MAP = new HashMap<>();
   private static final Set<UUID> U_U_I_D_SET = new HashSet<>();
   private static final Minecraft MINECRAFT = Minecraft.getInstance();
   private static Object object;
   private static final UUID U_U_I_D = new UUID(0L, 0L);

   private PlayerFaceTexture() {
   }

   public static GpuTextureView getFace(Player player) {
      return m_2(player.getUUID(), player.getGameProfile());
   }

   public static GpuTextureView getFace(GameProfile gameProfile) {
      return m_2(gameProfile.id(), gameProfile);
   }

   private static GpuTextureView m_2(UUID uUID, GameProfile gameProfile) {
      if (MINECRAFT.level != object) {
         clearAll();
         object = MINECRAFT.level;
      }

      UUID uUID2 = uUID == null ? U_U_I_D : uUID;
      if (U_U_I_D_SET.contains(uUID2)) {
         return null;
      }

      DynamicTexture dynamicTexture = U_U_I_D_MAP.computeIfAbsent(uUID2, uUID3 -> m_8(gameProfile));
      if (dynamicTexture != null && !dynamicTexture.getPixels().isClosed()) {
         return dynamicTexture.getTextureView();
      }

      U_U_I_D_MAP.remove(uUID2);
      if (dynamicTexture == null) {
         U_U_I_D_SET.add(uUID2);
      }

      return null;
   }

   public static void clearAll() {
      U_U_I_D_MAP.values().forEach(DynamicTexture::close);
      U_U_I_D_MAP.clear();
      U_U_I_D_SET.clear();
   }

   private static DynamicTexture m_8(GameProfile gameProfile) {
      try {
         Identifier identifier = gameProfile.id() == null
            ? DefaultPlayerSkin.getDefaultTexture()
            : MINECRAFT.getSkinManager().createLookup(gameProfile, false).get().body().texturePath();
         NativeImage nativeImage;
         boolean flag;
         if (MINECRAFT.getTextureManager().getTexture(identifier) instanceof DynamicTexture dynamicTexture && (nativeImage = dynamicTexture.getPixels()) != null && !nativeImage.isClosed()) {
            flag = false;
         } else {
            var optional = MINECRAFT.getResourceManager().getResource(identifier);
            if (optional.isEmpty()) {
               return null;
            }

            nativeImage = NativeImage.read(((Resource)optional.get()).open());
            flag = true;
         }

         NativeImage nativeImage2 = new NativeImage(32, 32, true);

         for (int i = 0; i < 32; i++) {
            for (int i2 = 0; i2 < 32; i2++) {
               nativeImage2.setPixel(i, i2, m_3(nativeImage, i / 4, i2 / 4));
            }
         }

         if (flag) {
            nativeImage.close();
         }

         DynamicTexture dynamicTexture2 = new DynamicTexture(() -> "", nativeImage2);
         dynamicTexture2.upload();
         return dynamicTexture2;
      } catch (Exception e) {
         Onetap.LOGGER.warn("Failed to build face for {}", gameProfile.id(), e);
         return null;
      }
   }

   private static int m_3(NativeImage nativeImage, int value, int value2) {
      int value3 = nativeImage.getPixel(8 + value, 8 + value2);
      int value4 = nativeImage.getPixel(40 + value, 8 + value2);
      return ARGB.alpha(value4) > 0 ? m_5_int(value4, value3) : value3;
   }

   private static int m_5_int(int value, int value2) {
      int value3 = ARGB.alpha(value);
      int value4 = ARGB.alpha(value2);
      int value5 = 255 - value3;
      int value6 = value3 + value4 * value5 / 255;
      if (value6 == 0) {
         return 0;
      }

      int value7 = (ARGB.red(value) * value3 + ARGB.red(value2) * value4 * value5 / 255) / value6;
      int value8 = (ARGB.green(value) * value3 + ARGB.green(value2) * value4 * value5 / 255) / value6;
      int value9 = (ARGB.blue(value) * value3 + ARGB.blue(value2) * value4 * value5 / 255) / value6;
      return ARGB.color(value6, value7, value8, value9);
   }

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
