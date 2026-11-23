package wtf.onetap.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.discord.DiscordPresence;
import wtf.onetap.module.Module;
import wtf.onetap.module.player.StaffExploit;
import wtf.onetap.util.ChatBadges;
import wtf.onetap.util.KeyNames;
import wtf.onetap.util.MinecraftAccess;

public class HudRenderer implements MinecraftAccess {
   private static final Identifier IDENTIFIER = Identifier.withDefaultNamespace("gui");
   private static final Pattern PATTERN = Pattern.compile("^\\w{3,16}$");
   private static final Pattern PATTERN2 = Pattern.compile(
      ".*(mod|der|adm|help|wne|мод|хелп|помо|адм|владе|отри|taf|curat|курато|dev|раз|supp|сапп|yt|ютуб|стажер|сотрудник|хелп|помощ|админ|модер).*"
   );
   private static final int VALUE = ColorUtil.rgba(229, 0, 63, 255);
   private static final Animation ANIMATION = new Animation(250L, 0.0F, Easing.EASING8);
   private static final Animation ANIMATION2 = new Animation(250L, 0.0F, Easing.EASING8);
   private static final Animation ANIMATION3 = new Animation(250L, 0.0F, Easing.EASING8);
   private static final Map<String, Animation> STRING_MAP = new HashMap<>();
   private static final Map<String, HudRenderer.DataRecord> STRING_MAP2 = new HashMap<>();
   private static final Map<String, HudRenderer.StateHolder> STRING_MAP3 = new LinkedHashMap<>();

   private HudRenderer() {
   }

   public static Supplier<FontAtlas> getFont(boolean flag) {
      return flag ? Fonts.SUPPLIER2 : Fonts.SUPPLIER4;
   }

   public static void m_9(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, boolean flag) {
      int value = (int)((flag ? 150 : 245) * floatValue5);
      if (value > 0) {
         Draw2D.rect(floatValue, floatValue2, floatValue3, floatValue4, ColorUtil.rgba(22, 23, 26, value));
         if (flag) {
            int value2 = (int)(255.0F * floatValue5);
            Draw2D.m_1(floatValue, floatValue2, 2.0F, floatValue4, 0.0F, 0.0F, ColorUtil.withAlpha(ColorUtil.primaryColor(), value2), ColorUtil.withAlpha(ColorUtil.secondaryColor(), value2));
         } else {
            int value3 = ColorUtil.primaryColor();
            Draw2D.m_6(floatValue - 1.4F, floatValue2, 4.2F, floatValue4, 0.0F, 1.8F, ColorUtil.withAlpha(value3, (int)(80.0F * floatValue5)));
            Draw2D.rect(floatValue, floatValue2, 2.0F, floatValue4, ColorUtil.withAlpha(value3, value));
         }
      }
   }

   public static void m_4(float floatValue, float floatValue2, boolean flag) {
      var supplier = getFont(flag);
      float floatValue3 = 5.0F;
      float floatValue4 = 15.0F;
      float floatValue5 = 7.5F;
      int value = ColorUtil.rgba(235, 235, 235, 255);
      int value2 = ColorUtil.rgba(110, 110, 115, 255);
      String text = MinecraftAccess.MINECRAFT.player != null ? MinecraftAccess.MINECRAFT.player.getName().getString() : (DiscordPresence.hasUser() ? DiscordPresence.getUserName() : "User");
      String text2 = MinecraftAccess.MINECRAFT.getFps() + "fps";
      PlayerInfo playerInfo = MinecraftAccess.MINECRAFT.getConnection() != null && MinecraftAccess.MINECRAFT.player != null ? MinecraftAccess.MINECRAFT.getConnection().getPlayerInfo(MinecraftAccess.MINECRAFT.player.getUUID()) : null;
      String text3 = (playerInfo != null ? playerInfo.getLatency() : 0) + "ms";
      String text4 = "PlusClient";
      String text5 = " | ";
      float floatValue6 = Draw2D.getTextWidth(text4, supplier, floatValue5);
      float floatValue7 = Draw2D.getTextWidth(text5, supplier, floatValue5);
      float floatValue8 = Draw2D.getTextWidth(text, supplier, floatValue5);
      float floatValue9 = Draw2D.getTextWidth(text2, supplier, floatValue5);
      float floatValue10 = Draw2D.getTextWidth(text3, supplier, floatValue5);
      float floatValue11 = floatValue3 + floatValue6 + floatValue7 + floatValue8 + floatValue7 + floatValue9 + floatValue7 + floatValue10 + floatValue3;
      m_9(floatValue, floatValue2, floatValue11, floatValue4, 1.0F, flag);
      float floatValue12 = Draw2D.getTextBaseline(supplier, floatValue5, floatValue2, floatValue4);
      float floatValue13 = floatValue + floatValue3;
      Draw2D.drawStyledText(text4, supplier, floatValue13, floatValue12, value, floatValue5);
      floatValue13 += floatValue6;
      Draw2D.drawStyledText(text5, supplier, floatValue13, floatValue12, value2, floatValue5);
      floatValue13 += floatValue7;
      Draw2D.drawStyledText(text, supplier, floatValue13, floatValue12, value, floatValue5);
      floatValue13 += floatValue8;
      Draw2D.drawStyledText(text5, supplier, floatValue13, floatValue12, value2, floatValue5);
      floatValue13 += floatValue7;
      Draw2D.drawStyledText(text2, supplier, floatValue13, floatValue12, value, floatValue5);
      floatValue13 += floatValue9;
      Draw2D.drawStyledText(text5, supplier, floatValue13, floatValue12, value2, floatValue5);
      floatValue13 += floatValue7;
      Draw2D.drawStyledText(text3, supplier, floatValue13, floatValue12, value, floatValue5);
   }

   public static float m_3_float(String text, boolean flag) {
      var supplier = getFont(flag);
      float floatValue = 5.0F;
      float floatValue2 = 7.5F;
      float floatValue3 = 8.0F;
      float floatValue4 = 3.0F;
      return floatValue + floatValue3 + floatValue4 + Draw2D.getTextWidth(text, supplier, floatValue2) + floatValue;
   }

   public static void m_8(float floatValue, float floatValue2, String text, String text2, boolean flag) {
      var supplier = getFont(flag);
      float floatValue3 = 15.0F;
      float floatValue4 = m_3_float(text2, flag);
      m_9(floatValue, floatValue2, floatValue4, floatValue3, 1.0F, flag);
      float floatValue5 = 5.0F;
      float floatValue6 = 7.5F;
      float floatValue7 = 8.0F;
      float floatValue8 = 3.0F;
      float floatValue9 = floatValue + floatValue5;
      float floatValue10 = Draw2D.getCenteredY(text, Fonts.SUPPLIER8, floatValue7, floatValue2, floatValue3);
      Draw2D.drawStyledText(text, Fonts.SUPPLIER8, floatValue9, floatValue10, ColorUtil.primaryColor(), floatValue7);
      Draw2D.drawStyledText(text2, supplier, floatValue9 + floatValue7 + floatValue8, Draw2D.getTextBaseline(supplier, floatValue6, floatValue2, floatValue3), ColorUtil.rgba(235, 235, 235, 255), floatValue6);
   }

   public static void m_0(float floatValue, float floatValue2, boolean flag) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         var supplier = getFont(flag);
         float floatValue3 = 5.0F;
         float floatValue4 = 14.5F;
         float floatValue5 = 8.0F;
         float floatValue6 = 7.5F;
         float floatValue7 = 8.5F;
         float floatValue8 = 3.0F;
         float floatValue9 = 11.0F;
         float floatValue10 = Draw2D.getTextWidth("Potions", supplier, floatValue5);
         var mobEffectInstanceList = MinecraftAccess.MINECRAFT.player.getActiveEffects().stream().sorted(Comparator.reverseOrder()).toList();
         ArrayList<HudRenderer.DataRecord> c9List = new ArrayList<>();

         for (MobEffectInstance mobEffectInstance : mobEffectInstanceList) {
            int value = mobEffectInstance.getAmplifier() + 1;
            String text = value <= 10 ? Component.translatable("enchantment.level." + value).getString() : String.valueOf(value);
            String text2 = mobEffectInstance.getEffect().value().getDisplayName().getString() + " " + text;
            String text3 = mobEffectInstance.isInfiniteDuration()
               ? "**:**"
               : StringUtil.formatTickDuration(mobEffectInstance.getDuration(), MinecraftAccess.MINECRAFT.level.tickRateManager().tickrate());
            int value2 = mobEffectInstance.getAmplifier();
            int value3 = mobEffectInstance.isInfiniteDuration() ? -1 : mobEffectInstance.getDuration();
            HudRenderer.DataRecord c9 = new HudRenderer.DataRecord(mobEffectInstance.getDescriptionId(), text2, text3, true, getEffectSprite(mobEffectInstance.getEffect()), value2, value3);
            STRING_MAP2.put(c9.m_2h(), c9);
            c9List.add(c9);
         }

         for (String text4 : List.copyOf(STRING_MAP.keySet())) {
            if (c9List.stream().noneMatch(c92 -> c92.m_2h().equals(text4))) {
               HudRenderer.DataRecord c93 = STRING_MAP2.get(text4);
               if (c93 != null) {
                  c9List.add(new HudRenderer.DataRecord(c93.m_2h(), c93.m_2_p(), c93.m_23(), false, c93.m_1_x_TextureAtlasSprite(), c93.m_5_t(), c93.m_5_v()));
               }
            }
         }

         float floatValue11 = 0.0F;
         float floatValue12 = 0.0F;

         for (HudRenderer.DataRecord c94 : c9List) {
            Animation animation = STRING_MAP.computeIfAbsent(c94.m_2h(), text5 -> new Animation(250L, 0.0F, Easing.EASING8));
            float floatValue13 = animation.update(c94.m_3c() ? 1.0F : 0.0F);
            if (floatValue13 < 0.01F) {
               if (!c94.m_3c()) {
                  STRING_MAP.remove(c94.m_2h());
                  STRING_MAP2.remove(c94.m_2h());
               }
            } else {
               float floatValue14 = Draw2D.getTextWidth(c94.m_2_p(), supplier, floatValue6);
               float floatValue15 = Draw2D.getTextWidth(c94.m_23(), supplier, floatValue6);
               float floatValue16 = floatValue7 + floatValue8 + floatValue14 + 10.0F + floatValue15;
               if (floatValue16 > floatValue12) {
                  floatValue12 = floatValue16;
               }

               floatValue11 += floatValue9 * floatValue13;
            }
         }

         float floatValue17 = Math.max(90.0F, floatValue12 + floatValue3 * 2.0F);
         boolean flag2 = floatValue11 > 0.01F;
         float floatValue18 = Math.min(1.0F, floatValue11 / floatValue9);
         float floatValue19 = Math.max(3.0F, floatValue11 + floatValue18);
         float floatValue20 = ANIMATION.update(floatValue17);
         if (!(floatValue20 < 5.0F)) {
            float floatValue21 = floatValue4 + floatValue19;
            m_9(floatValue, floatValue2, floatValue20, floatValue21, 1.0F, flag);
            float floatValue22 = floatValue + (floatValue20 - floatValue10) / 2.0F;
            float floatValue23 = Draw2D.getTextBaseline(supplier, floatValue5, floatValue2, floatValue4);
            Draw2D.drawStyledText("Potions", supplier, floatValue22, floatValue23, ColorUtil.rgba(235, 235, 235, 255), floatValue5);
            if (flag2) {
               float floatValue24 = floatValue2 + floatValue4;
               int value4 = ColorUtil.primaryColor();
               Render2D.pushScissor(floatValue, floatValue2 + floatValue4, floatValue20, floatValue19);

               for (HudRenderer.DataRecord c95 : c9List) {
                  Animation animation2 = STRING_MAP.get(c95.m_2h());
                  if (animation2 != null) {
                     float floatValue25 = animation2.update(c95.m_3c() ? 1.0F : 0.0F);
                     if (!(floatValue25 < 0.01F)) {
                        String text6 = c95.m_2_p();
                        String text7 = c95.m_23();
                        float floatValue26 = Draw2D.getTextWidth(text7, supplier, floatValue6);
                        int value5 = (int)(255.0F * floatValue25);
                        int value6 = c95.m_5_v();
                        if (c95.m_3c() && value6 > 0 && value6 < 200) {
                           float floatValue27 = (float)(0.7 + 0.3 * Math.sin(System.currentTimeMillis() / 180.0));
                           value5 = (int)(value5 * floatValue27);
                        }

                        int value7 = c95.m_5_t() >= 2 ? ColorUtil.rgba(255, 85, 85, 255) : ColorUtil.rgba(222, 222, 222, 255);
                        float floatValue28 = floatValue9 * floatValue25;
                        float floatValue29 = floatValue24 - (1.0F - floatValue25) * floatValue9;
                        float floatValue30 = Draw2D.getTextBaseline(supplier, floatValue6, floatValue29, floatValue28);
                        float floatValue31 = floatValue + floatValue3;
                        float floatValue32 = floatValue29 + (floatValue28 - floatValue7) / 2.0F;
                        if (c95.m_1_x_TextureAtlasSprite() != null) {
                           TextureAtlas textureAtlas = MinecraftAccess.MINECRAFT.getAtlasManager().getAtlasOrThrow(IDENTIFIER);
                           Draw2D.m_0(
                              floatValue31,
                              floatValue32,
                              floatValue7,
                              floatValue7,
                              ColorUtil.rgba(255, 255, 255, value5),
                              textureAtlas.getTextureView(),
                              c95.m_1_x_TextureAtlasSprite().getU0(),
                              c95.m_1_x_TextureAtlasSprite().getV0(),
                              c95.m_1_x_TextureAtlasSprite().getU1(),
                              c95.m_1_x_TextureAtlasSprite().getV1()
                           );
                        }

                        float floatValue33 = floatValue31 + floatValue7 + floatValue8;
                        Draw2D.drawStyledText(text6, supplier, floatValue33, floatValue30, ColorUtil.withAlpha(value7, value5), floatValue6);
                        Draw2D.drawStyledText(text7, supplier, floatValue + floatValue20 - floatValue26 - floatValue3, floatValue30, ColorUtil.withAlpha(value4, value5), floatValue6);
                        floatValue24 += floatValue9 * floatValue25;
                     }
                  }
               }

               Render2D.popScissor();
            }
         }
      }
   }

   public static void m_5(float floatValue, float floatValue2, boolean flag) {
      var supplier = getFont(flag);
      float floatValue3 = 5.0F;
      float floatValue4 = 14.5F;
      float floatValue5 = 8.0F;
      float floatValue6 = 7.5F;
      float floatValue7 = 11.0F;
      var moduleList = Onetap.MODULE_MANAGER.getModules().stream().filter(module -> module.getKey() != -1).toList();
      float floatValue8 = 0.0F;
      float floatValue9 = 0.0F;

      for (Module module2 : moduleList) {
         float floatValue10 = module2.getAnimationProgress();
         if (!(floatValue10 < 0.005F) || module2.isEnabled()) {
            float floatValue11 = Draw2D.getTextWidth(module2.getName(), supplier, floatValue6);
            float floatValue12 = Draw2D.getTextWidth("[" + KeyNames.getKeyName(module2.getKey()) + "]", supplier, floatValue6);
            float floatValue13 = floatValue11 + 12.0F + floatValue12;
            if (floatValue13 > floatValue9) {
               floatValue9 = floatValue13;
            }

            floatValue8 += floatValue7 * floatValue10;
         }
      }

      float floatValue14 = Draw2D.getTextWidth("Key binds", supplier, floatValue5);
      float floatValue15 = Math.max(90.0F, floatValue9 + floatValue3 * 2.0F);
      boolean flag2 = floatValue8 > 0.01F;
      float floatValue16 = Math.min(1.0F, floatValue8 / floatValue7);
      float floatValue17 = Math.max(3.0F, floatValue8 + floatValue16);
      float floatValue18 = ANIMATION2.update(floatValue15);
      if (!(floatValue18 < 5.0F)) {
         float floatValue19 = floatValue4 + floatValue17;
         m_9(floatValue, floatValue2, floatValue18, floatValue19, 1.0F, flag);
         float floatValue20 = floatValue + (floatValue18 - floatValue14) / 2.0F;
         float floatValue21 = Draw2D.getTextBaseline(supplier, floatValue5, floatValue2, floatValue4);
         Draw2D.drawStyledText("Key binds", supplier, floatValue20, floatValue21, ColorUtil.rgba(235, 235, 235, 255), floatValue5);
         if (flag2) {
            float floatValue22 = floatValue2 + floatValue4;
            int value = ColorUtil.primaryColor();
            Render2D.pushScissor(floatValue, floatValue2 + floatValue4, floatValue18, floatValue17);

            for (Module module3 : moduleList) {
               float floatValue23 = module3.getAnimationProgress();
               if (!(floatValue23 < 0.005F) || module3.isEnabled()) {
                  String text = module3.getName();
                  int value2 = (int)(255.0F * floatValue23);
                  float floatValue24 = floatValue22 - (1.0F - floatValue23) * floatValue7;
                  float floatValue25 = Draw2D.getTextBaseline(supplier, floatValue6, floatValue24, floatValue7 * floatValue23);
                  Draw2D.drawStyledText(text, supplier, floatValue + floatValue3, floatValue25, ColorUtil.rgba(225, 225, 225, value2), floatValue6);
                  String text2 = "[" + KeyNames.getKeyName(module3.getKey()) + "]";
                  float floatValue26 = Draw2D.getTextWidth(text2, supplier, floatValue6);
                  Draw2D.drawStyledText(text2, supplier, floatValue + floatValue18 - floatValue26 - floatValue3, floatValue25, ColorUtil.withAlpha(value, value2), floatValue6);
                  floatValue22 += floatValue7 * floatValue23;
               }
            }

            Render2D.popScissor();
         }
      }
   }

   public static void m_8(float floatValue, float floatValue2, boolean flag) {
      m_8_void(flag);
      var supplier = getFont(flag);
      float floatValue3 = 5.0F;
      float floatValue4 = 14.5F;
      float floatValue5 = 8.0F;
      float floatValue6 = 7.5F;
      float floatValue7 = 11.0F;
      ArrayList<HudRenderer.StateHolder> c7List = new ArrayList<>();
      float floatValue8 = 0.0F;
      float floatValue9 = 0.0F;
      float floatValue10 = 0.0F;

      for (HudRenderer.StateHolder c7 : STRING_MAP3.values()) {
         float floatValue11 = c7.animation.update(c7.flag ? 1.0F : 0.0F);
         if (!(floatValue11 < 0.01F)) {
            c7List.add(c7);
            float floatValue12;
            if (c7.textRenderer != null && !c7.textRenderer.isEmpty()) {
               floatValue12 = c7.textRenderer.getWidth(floatValue6);
            } else {
               floatValue12 = Draw2D.getTextWidth("STAFF ", supplier, floatValue6) + Draw2D.getTextWidth(c7.text, supplier, floatValue6);
            }

            float floatValue13 = c7.flag2 ? Draw2D.getTextWidth(formatDuration(c7.longValue), supplier, 7.0F) + 6.0F : 0.0F;
            float floatValue14 = floatValue12 + floatValue13 + floatValue3 * 2.0F + (c7.flag3 ? 7.0F : 0.0F);
            if (floatValue14 > floatValue9) {
               floatValue9 = floatValue14;
            }

            floatValue8 += floatValue7 * floatValue11;
            if (floatValue11 > floatValue10) {
               floatValue10 = floatValue11;
            }
         }
      }

      c7List.sort(Comparator.comparing(c72 -> !c72.flag3));
      boolean flag2 = MinecraftAccess.MINECRAFT.gui.screen() instanceof ChatScreen;
      if (!c7List.isEmpty() || flag2) {
         float floatValue15 = Draw2D.getTextWidth("StaffList", supplier, floatValue5);
         float floatValue16 = floatValue15 + floatValue3 * 2.0F;
         float floatValue17 = Math.max(floatValue16, floatValue9);
         boolean flag3 = floatValue8 > 0.01F;
         float floatValue18 = flag3 ? floatValue8 + 2.0F : 0.0F;
         float floatValue19 = ANIMATION3.update(floatValue17);
         if (!(floatValue19 < 5.0F)) {
            float floatValue20 = floatValue4 + floatValue18;
            m_9(floatValue, floatValue2, floatValue19, floatValue20, 1.0F, flag);
            float floatValue21 = floatValue + (floatValue19 - floatValue15) / 2.0F;
            float floatValue22 = Draw2D.getTextBaseline(supplier, floatValue5, floatValue2, floatValue4);
            Draw2D.drawStyledText("StaffList", supplier, floatValue21, floatValue22, ColorUtil.rgba(235, 235, 235, 255), floatValue5);
            if (flag3) {
               float floatValue23 = floatValue2 + floatValue4 + 1.0F;
               Render2D.pushScissor(floatValue, floatValue23, floatValue19, floatValue18);

               for (HudRenderer.StateHolder c73 : c7List) {
                  float floatValue24 = c73.animation.update(c73.flag ? 1.0F : 0.0F);
                  if (!(floatValue24 < 0.01F)) {
                     int value = (int)(255.0F * floatValue24);
                     float floatValue25 = floatValue23 - (1.0F - floatValue24) * floatValue7;
                     float floatValue26 = Draw2D.getTextBaseline(supplier, floatValue6, floatValue25, floatValue7 * floatValue24);
                     float floatValue27 = floatValue + floatValue3;
                     if (c73.flag3) {
                        Draw2D.roundedRect(floatValue27, floatValue25 + (floatValue7 * floatValue24 - 4.0F) / 2.0F, 4.0F, 4.0F, 2.0F, ColorUtil.rgba(202, 40, 40, value));
                        floatValue27 += 7.0F;
                     }

                     if (c73.textRenderer != null && !c73.textRenderer.isEmpty()) {
                        c73.textRenderer.draw(floatValue27, floatValue26, floatValue6, value);
                     } else {
                        float floatValue28 = Draw2D.getTextWidth("STAFF ", supplier, floatValue6);
                        Draw2D.drawStyledText("STAFF ", supplier, floatValue27, floatValue26, ColorUtil.rgba(230, 175, 0, value), floatValue6);
                        Draw2D.drawStyledText(c73.text, supplier, floatValue27 + floatValue28, floatValue26, ColorUtil.rgba(225, 225, 225, value), floatValue6);
                     }

                     if (c73.flag2) {
                        String text = formatDuration(c73.longValue);
                        float floatValue29 = Draw2D.getTextWidth(text, supplier, 7.0F);
                        Draw2D.drawStyledText(text, supplier, floatValue + floatValue19 - floatValue29 - floatValue3, Draw2D.getTextBaseline(supplier, 7.0F, floatValue25, floatValue7 * floatValue24), ColorUtil.withAlpha(VALUE, value), 7.0F);
                     }

                     floatValue23 += floatValue7 * floatValue24;
                  }
               }

               Render2D.popScissor();
            }
         }
      }
   }

   public static void m_4(float floatValue, float floatValue2, float floatValue3, float floatValue4, String text, int value, int value2) {
      float floatValue5 = Draw2D.getCenteredY(text, Fonts.SUPPLIER8, floatValue4, floatValue2, floatValue3);
      Draw2D.drawStyledText(text, Fonts.SUPPLIER8, floatValue, floatValue5, ColorUtil.withAlpha(value, value2), floatValue4);
   }

   public static void m_0(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, int value, int value2) {
      float floatValue6 = 1.0F - floatValue5;
      if (floatValue6 > 0.01F) {
         float floatValue7 = Draw2D.getCenteredY("\uf00d", Fonts.SUPPLIER8, floatValue4, floatValue2, floatValue3);
         Draw2D.drawStyledText("\uf00d", Fonts.SUPPLIER8, floatValue, floatValue7, ColorUtil.rgba(235, 235, 235, (int)(255.0F * floatValue6 * value2 / 255.0F)), floatValue4);
      }

      if (floatValue5 > 0.01F) {
         float floatValue8 = Draw2D.getCenteredY("\uf00c", Fonts.SUPPLIER8, floatValue4, floatValue2, floatValue3);
         Draw2D.drawStyledText("\uf00c", Fonts.SUPPLIER8, floatValue, floatValue8, ColorUtil.withAlpha(value, (int)(255.0F * floatValue5 * value2 / 255.0F)), floatValue4);
      }
   }

   private static void m_8_void(boolean flag) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         var supplier = getFont(flag);

         for (HudRenderer.StateHolder c7 : STRING_MAP3.values()) {
            c7.flag = false;
         }

         ClientPacketListener clientPacketListener = MinecraftAccess.MINECRAFT.getConnection();
         TreeSet treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
         TreeMap treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
         if (clientPacketListener != null) {
            for (PlayerInfo playerInfo : clientPacketListener.getOnlinePlayers()) {
               String text = playerInfo.getProfile().name();
               treeSet.add(text);
               treeMap.put(text, playerInfo);
            }
         }

         LinkedHashMap map = new LinkedHashMap();
         TreeSet treeSet2 = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
         TreeSet treeSet3 = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

         for (String text2 : StaffExploit.getStaffSet()) {
            if (text2 != null && !text2.isBlank() && treeSet2.add(text2)) {
               treeSet3.add(text2);
               map.put(text2, StaffExploit.m_9_Component(text2));
            }
         }

         if (MinecraftAccess.MINECRAFT.level != null) {
            Scoreboard scoreboard = MinecraftAccess.MINECRAFT.level.getScoreboard();

            for (PlayerTeam playerTeam : scoreboard.getPlayerTeams()) {
               for (String text3 : playerTeam.getPlayers()) {
                  String text4 = text3;
                  if (PATTERN.matcher(text4).matches() && !text4.equalsIgnoreCase(MinecraftAccess.MINECRAFT.player.getName().getString())) {
                     String text5 = playerTeam.getPlayerPrefix() == null ? "" : playerTeam.getPlayerPrefix().getString().toLowerCase(Locale.ROOT);
                     boolean flag2 = !treeSet.contains(text4);
                     if ((flag2 || PATTERN2.matcher(text5).matches() || Onetap.STAFF_MANAGER.isStaff(text4)) && treeSet2.add(text4)) {
                        map.put(text4, playerTeam.getPlayerPrefix());
                     }
                  }
               }
            }
         }

         for (String text6 : Onetap.STAFF_MANAGER.getStaff()) {
            if (!text6.equalsIgnoreCase(MinecraftAccess.MINECRAFT.player.getName().getString()) && treeSet2.add(text6)) {
               map.put(text6, null);
            }
         }

         for (Entry entry : (Iterable<Entry>)map.entrySet()) {
            String text7 = (String)entry.getKey();
            boolean flag3 = treeSet3.contains(text7);
            boolean flag4 = !flag3 && !treeSet.contains(text7);
            PlayerInfo playerInfo2 = (PlayerInfo)treeMap.get(text7);
            boolean flag5 = false;
            if (MinecraftAccess.MINECRAFT.level != null) {
               for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
                  if (player.getName().getString().equalsIgnoreCase(text7)) {
                     flag5 = true;
                     break;
                  }
               }
            }

            HudRenderer.StateHolder c72 = STRING_MAP3.computeIfAbsent(text7.toLowerCase(Locale.ROOT), text8 -> new HudRenderer.StateHolder(text7));
            c72.flag3 = flag3;
            c72.flag = true;
            c72.flag2 = flag4;
            c72.flag4 = flag5;
            Component component;
            if (!flag3 && playerInfo2 != null) {
               component = MinecraftAccess.MINECRAFT.gui.hud.getTabList().getNameForDisplay(playerInfo2);
            } else {
               Component component2 = (Component)entry.getValue();
               component = component2 == null ? Component.literal(text7) : component2.copy().append(Component.literal(text7));
            }

            c72.textRenderer = TextRenderer.m_6(ChatBadges.m_6(component), supplier, Fonts.SUPPLIER17);
            if (flag4 && c72.longValue == 0L) {
               c72.longValue = System.currentTimeMillis();
            }

            if (!flag4) {
               c72.longValue = 0L;
            }
         }

         STRING_MAP3.entrySet().removeIf(entry2 -> !entry2.getValue().flag && entry2.getValue().animation.getValue() < 0.01F);
      }
   }

   private static TextureAtlasSprite getEffectSprite(Holder<MobEffect> holder) {
      return MinecraftAccess.MINECRAFT.getAtlasManager().getAtlasOrThrow(IDENTIFIER).getSprite(Hud.getMobEffectSprite(holder));
   }

   private static String formatDuration(long longValue) {
      if (longValue == 0L) {
         return "";
      }

      long longValue2 = (System.currentTimeMillis() - longValue) / 1000L;
      long longValue3 = longValue2 / 60L;
      long longValue4 = longValue2 % 60L;
      return String.format(Locale.US, "%d:%02d", longValue3, longValue4);
   }

   private static final class StateHolder {
      public final String text;
      public final Animation animation;
      public boolean flag;
      public boolean flag2;
      public boolean flag3;
      public boolean flag4;
      public TextRenderer textRenderer;
      public long longValue;

      public StateHolder(String text2) {
         this.animation = new Animation(250L, 0.0F, Easing.EASING8);
         this.text = text2;
      }
   }

   private record DataRecord(String f_2h, String f_2_p, String f_23, boolean f_3c, TextureAtlasSprite f_1_x, int f_5_t, int f_5_v) {
      public String m_2h() {
         return this.f_2h;
      }

      public String m_2_p() {
         return this.f_2_p;
      }

      public String m_23() {
         return this.f_23;
      }

      public boolean m_3c() {
         return this.f_3c;
      }

      public TextureAtlasSprite m_1_x_TextureAtlasSprite() {
         return this.f_1_x;
      }

      public int m_5_t() {
         return this.f_5_t;
      }

      public int m_5_v() {
         return this.f_5_v;
      }
   }
}
