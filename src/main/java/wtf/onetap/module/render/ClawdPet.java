package wtf.onetap.module.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.lwjgl.glfw.GLFW;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;

/**
 * Animated Clawd pet drawn on the HUD. Sprite sheets are horizontal strips of frames and are read from
 * assets/onetap/textures/gui/{still,walk,wave,jump}.png. A missing sheet is skipped without an error.
 * Position is stored in the hidden xCoord and yCoord settings. Dragging works only while the chat is open.
 */
@ModuleInformation(name = "ClawdPet", description = "Анимированный Clawd на HUD", category = Category.RENDER)
public final class ClawdPet extends Module {
    private static final String[] THOUGHTS = {"мяу!", "я тут", "хмм...", "z-z-z", "✨"};
    private static final float BASE_W = 100.0F;
    private static final float BASE_H = 94.0F;
    private static final float SPRITE_W = 90.0F;
    private static final float SPRITE_H = 68.0F;
    private static final int[] WALK_DURATIONS = {80, 90, 80, 80, 90, 80, 80, 90, 80, 80, 90, 80, 80, 90, 80, 80, 90, 80, 80, 80};
    private static final int[] WAVE_DURATIONS = {80, 90, 80, 80, 90, 80, 80, 90, 80, 80, 90, 80, 80, 90, 80, 80, 80};
    private static final int[] JUMP_DURATIONS = {80, 90, 80, 80, 90, 80, 80, 90, 80, 80, 90, 80, 80, 90, 80, 80, 90, 80, 80, 90, 90};
    private static final Map<String, GpuTextureView> SPRITES = new HashMap<>();

    public final FloatSetting xCoord = new FloatSetting("xCoord", 32.0F, 0.0F, 2000.0F, 1.0F).visibleWhen(() -> false).build();
    public final FloatSetting yCoord = new FloatSetting("yCoord", 72.0F, 0.0F, 2000.0F, 1.0F).visibleWhen(() -> false).build();
    public final FloatSetting size = new FloatSetting("Размер", 1.0F, 0.65F, 1.8F, 0.05F).build();
    public final BooleanSetting animations = new BooleanSetting("Анимации", true).build();
    public final BooleanSetting thoughtBubble = new BooleanSetting("Облачко мысли", true).build();
    public final BooleanSetting reactToCursor = new BooleanSetting("Реакция на курсор", true).build();

    private boolean dragging;
    private float dragOffsetX;
    private float dragOffsetY;
    private float currentWidth = BASE_W;
    private float currentHeight = BASE_H;
    private final long bornAt = System.currentTimeMillis();
    private long lastThoughtAt = bornAt;
    private int thoughtIndex;

    @Override
    public void onDisable() {
        dragging = false;
    }

    @EventTarget
    private void onRender2D(Render2DEvent event) {
        Minecraft mc = MINECRAFT;
        if (mc.player == null || mc.getWindow() == null) {
            return;
        }

        float scale = size.getFloat();
        currentWidth = BASE_W * scale;
        currentHeight = BASE_H * scale;
        float screenW = mc.getWindow().getGuiScaledWidth();
        float screenH = mc.getWindow().getGuiScaledHeight();
        boolean chatOpen = chatOpen(mc);

        if (dragging) {
            if (!chatOpen) {
                dragging = false;
            } else {
                setX((float) cursorX(mc) - dragOffsetX);
                setY((float) cursorY(mc) - dragOffsetY);
            }
        }

        float px = clamp(x(), 0.0F, Math.max(0.0F, screenW - currentWidth));
        float py = clamp(y(), 0.0F, Math.max(0.0F, screenH - currentHeight));
        if (!dragging) {
            if (px != x()) {
                setX(px);
            }
            if (py != y()) {
                setY(py);
            }
        }

        long now = System.currentTimeMillis();
        long age = now - bornAt;
        if (thoughtBubble.getValue() && now - lastThoughtAt > 7600L) {
            thoughtIndex = (thoughtIndex + 1) % THOUGHTS.length;
            lastThoughtAt = now;
        }

        AnimationFrame frame = chooseFrame(mc, age, now);
        float bob = frame.name.equals("still") ? (float) Math.sin(age * 0.0042) * 1.4F * scale : 0.0F;
        float spriteX = px + 5.0F * scale;
        float spriteY = py + 24.0F * scale + bob;
        float spriteW = SPRITE_W * scale;
        float spriteH = SPRITE_H * scale;

        // Floor shadow under the pet, drawn as a flat translucent pill.
        Render2D.m_6(px + 22.0F * scale, py + 89.0F * scale, 56.0F * scale, 4.0F * scale, 2.0F * scale, ColorUtil.rgba(0, 0, 0, 46));

        GpuTextureView view;
        float u0 = 0.0F;
        float u1 = 1.0F;
        if (frame.name.equals("still")) {
            view = sprite("still");
        } else {
            view = sprite(frame.name);
            u0 = (float) frame.index / frame.count;
            u1 = (float) (frame.index + 1) / frame.count;
        }
        if (view != null) {
            Render2D.m_6(spriteX, spriteY, spriteW, spriteH, 0.0F, ColorUtil.rgba(255, 255, 255, 255), view, u0, 0.0F, u1, 1.0F);
        }

        drawThought(px, py, scale, now, chatOpen);
    }

    @EventTarget
    private void onMouseClick(MouseClickEvent event) {
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }
        Minecraft mc = MINECRAFT;
        if (event.getAction() == GLFW.GLFW_RELEASE) {
            dragging = false;
            return;
        }
        if (event.getAction() == GLFW.GLFW_PRESS && chatOpen(mc) && mc.getWindow() != null) {
            float cx = (float) cursorX(mc);
            float cy = (float) cursorY(mc);
            if (cx >= x() && cx <= x() + currentWidth && cy >= y() && cy <= y() + currentHeight) {
                dragging = true;
                dragOffsetX = cx - x();
                dragOffsetY = cy - y();
            }
        }
    }

    private AnimationFrame chooseFrame(Minecraft mc, long age, long now) {
        if (!animations.getValue()) {
            return new AnimationFrame("still", 0, 1);
        }
        if (reactToCursor.getValue() && isCursorOverPet(mc)) {
            int index = frameAt(now - bornAt, WAVE_DURATIONS);
            return new AnimationFrame("wave", index, WAVE_DURATIONS.length);
        }
        long cycle = age % 18000L;
        if (cycle < 5200L) {
            return new AnimationFrame("still", 0, 1);
        }
        if (cycle < 10000L) {
            return new AnimationFrame("walk", frameAt(cycle - 5200L, WALK_DURATIONS), WALK_DURATIONS.length);
        }
        if (cycle < 13000L) {
            return new AnimationFrame("wave", frameAt(cycle - 10000L, WAVE_DURATIONS), WAVE_DURATIONS.length);
        }
        if (cycle < 15600L) {
            return new AnimationFrame("jump", frameAt(cycle - 13000L, JUMP_DURATIONS), JUMP_DURATIONS.length);
        }
        return new AnimationFrame("still", 0, 1);
    }

    private boolean isCursorOverPet(Minecraft mc) {
        if (mc.getWindow() == null) {
            return false;
        }
        float mx = (float) cursorX(mc);
        float my = (float) cursorY(mc);
        float scale = size.getFloat();
        return mx >= x() + 5.0F * scale && mx <= x() + 95.0F * scale && my >= y() + 24.0F * scale && my <= y() + 92.0F * scale;
    }

    private static double cursorX(Minecraft mc) {
        return mc.mouseHandler.getScaledXPos(mc.getWindow());
    }

    private static double cursorY(Minecraft mc) {
        return mc.mouseHandler.getScaledYPos(mc.getWindow());
    }

    private static boolean chatOpen(Minecraft mc) {
        return mc.gui != null && mc.gui.screen() instanceof ChatScreen;
    }

    private static int frameAt(long elapsedMs, int[] durations) {
        int total = 0;
        for (int duration : durations) {
            total += duration;
        }
        long time = Math.floorMod(elapsedMs, (long) total);
        for (int i = 0; i < durations.length; i++) {
            if (time < durations[i]) {
                return i;
            }
            time -= durations[i];
        }
        return durations.length - 1;
    }

    private void drawThought(float x, float y, float scale, long now, boolean chatOpen) {
        if (!thoughtBubble.getValue()) {
            return;
        }
        String text = chatOpen ? "?" : THOUGHTS[thoughtIndex];
        float fontSize = 7.0F * scale;
        float textW = Draw2D.getTextWidth(text, Fonts.SUPPLIER14, fontSize);
        float padX = 5.5F * scale;
        float padY = 3.2F * scale;
        float bubbleW = textW + padX * 2.0F;
        float bubbleH = fontSize + padY * 2.0F;
        float radius = 6.0F * scale;
        float spriteCX = x + 50.0F * scale;
        float bubbleX = clamp(spriteCX - bubbleW * 0.5F, x, x + 100.0F * scale - bubbleW);
        float bubbleY = y + 24.0F * scale - bubbleH - 4.0F * scale;

        float fade = clamp((float) (now - lastThoughtAt) / 160.0F, 0.0F, 1.0F);
        fade = fade * fade * (3.0F - 2.0F * fade);

        int shadowAlpha = (int) (46 * fade);
        int bgAlpha = (int) (245 * fade);
        int borderAlpha = (int) (34 * fade);
        int fgAlpha = (int) (255 * fade);

        float shadowGrow = 3.0F * scale;
        Render2D.m_6(bubbleX - shadowGrow * 0.5F, bubbleY + shadowGrow * 0.5F, bubbleW + shadowGrow, bubbleH + shadowGrow,
                radius + shadowGrow * 0.5F, ColorUtil.rgba(0, 0, 0, shadowAlpha));

        float border = Math.max(0.75F, 0.75F * scale);
        Render2D.m_6(bubbleX - border, bubbleY - border, bubbleW + border * 2.0F, bubbleH + border * 2.0F, radius + border,
                ColorUtil.rgba(255, 255, 255, borderAlpha));
        Render2D.m_6(bubbleX, bubbleY, bubbleW, bubbleH, radius, ColorUtil.rgba(22, 22, 26, bgAlpha));

        float textY = bubbleY + bubbleH * 0.5F - fontSize * 0.5F;
        Draw2D.drawStyledText(text, Fonts.SUPPLIER14, bubbleX + padX, textY, ColorUtil.rgba(240, 240, 244, fgAlpha), fontSize);
    }

    /** Loads a sprite sheet once and caches its GPU view. A missing file is cached as null and skipped. */
    private static GpuTextureView sprite(String name) {
        if (SPRITES.containsKey(name)) {
            return SPRITES.get(name);
        }
        GpuTextureView view = null;
        Identifier source = Identifier.fromNamespaceAndPath("onetap", "textures/gui/" + name + ".png");
        Optional<Resource> resource = MINECRAFT.getResourceManager().getResource(source);
        if (resource.isPresent()) {
            try (InputStream in = resource.get().open()) {
                NativeImage image = NativeImage.read(in);
                Identifier target = Identifier.fromNamespaceAndPath("onetap", "dynamic/clawd_" + name);
                DynamicTexture texture = new DynamicTexture(() -> "Onetap clawd " + name, image);
                MINECRAFT.getTextureManager().register(target, texture);
                view = texture.getTextureView();
            } catch (IOException ignored) {
                view = null;
            }
        }
        SPRITES.put(name, view);
        return view;
    }

    private float x() {
        return xCoord.getFloat();
    }

    private float y() {
        return yCoord.getFloat();
    }

    private void setX(float value) {
        xCoord.setValue(value);
    }

    private void setY(float value) {
        yCoord.setValue(value);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class AnimationFrame {
        private final String name;
        private final int index;
        private final int count;

        private AnimationFrame(String name, int index, int count) {
            this.name = name;
            this.index = index;
            this.count = count;
        }
    }
}
