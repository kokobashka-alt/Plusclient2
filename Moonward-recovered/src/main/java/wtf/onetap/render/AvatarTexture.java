package wtf.onetap.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import wtf.onetap.Onetap;
import wtf.onetap.platform.C7a;
import wtf.onetap.util.ChatUtil;

public class AvatarTexture {
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "avatar");
   private static DynamicTexture dynamicTexture;
   private static volatile boolean flag;
   private static volatile String text;

   public static Identifier m_5_Identifier() {
      return IDENTIFIER;
   }

   public static GpuTextureView getTextureView() {
      return dynamicTexture != null ? dynamicTexture.getTextureView() : null;
   }

   public static boolean m_4_boolean() {
      return dynamicTexture != null;
   }

   public static void m_9_void() {
      ChatUtil.m_3_IArr();
      String text2 = C7a.m_8_String();
      if (text2 == null ? text != null : !text2.equals(text)) {
         text = text2;
         Minecraft.getInstance().execute(AvatarTexture::release);
         if (text2 != null) {
            flag = true;
            Thread.startVirtualThread(() -> {
               try {
                  HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(text2)).header("User-Agent", "Onetap/1.0").build();
                  HttpResponse httpResponse = HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofByteArray());
                  if (httpResponse.statusCode() != 200) {
                     throw new IllegalStateException("HTTP " + httpResponse.statusCode());
                  }

                  NativeImage nativeImage = NativeImage.read((byte[])httpResponse.body());
                  Minecraft.getInstance().execute(() -> {
                     if (!text2.equals(text)) {
                        nativeImage.close();
                     } else {
                        release();
                        dynamicTexture = new DynamicTexture(() -> "Onetap Avatar", nativeImage);
                        dynamicTexture.upload();
                        Minecraft.getInstance().getTextureManager().register(IDENTIFIER, dynamicTexture);
                        flag = false;
                     }
                  });
               } catch (Exception e) {
                  Onetap.LOGGER.warn("[Onetap] Failed to load avatar", e);
                  if (text2.equals(text)) {
                     text = null;
                  }

                  flag = false;
               }
            });
         }
      }
   }

   private static void release() {
      if (dynamicTexture != null) {
         Minecraft.getInstance().getTextureManager().release(IDENTIFIER);
         dynamicTexture.close();
         dynamicTexture = null;
      }
   }

   public static void m_6_void() {
      release();
      text = null;
      flag = false;
   }

   private AvatarTexture() {
   }
}
