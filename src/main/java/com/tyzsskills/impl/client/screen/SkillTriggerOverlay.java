package com.tyzsskills.impl.client.screen;

import net.minecraft.util.ARGB;
import net.minecraft.client.renderer.RenderPipelines;
import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.model.Skill;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class SkillTriggerOverlay implements GuiLayer {


    private record Notification(Identifier icon, long creationTime, long lastUpdate){}

    private static final List<Notification> activeNotifications = new ArrayList<>();

    private static final long fadeIn = 200L;
    private static final long fadeOut = 500L;

    private static final int spacing = 32;

    private static final float scale = .65f;

    private static final Identifier DEFAULT_ICON = Identifier.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/skills/default.png");


    public static void ShowSkillIcon(String id){
        if(!Config.SHOW_SKILL_OVERLAY.get()) return;
        Skill skill = ClientCache.getSkill(id.toLowerCase());
        if(skill != null){
            var icon = Identifier.tryParse(skill.getIcon());
            if(icon == null || Minecraft.getInstance().getResourceManager().getResource(icon).isEmpty()){
                icon = DEFAULT_ICON;
            }

            long now = System.currentTimeMillis();

            for(int i = 0; i<activeNotifications.size(); i++){
                Notification current = activeNotifications.get(i);
                if(current.icon.equals(icon)){
                    activeNotifications.set(i, new Notification(icon, current.creationTime, now));
                    return;
                }
            }

            activeNotifications.add(new Notification(icon, now, now));
        }
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        if (activeNotifications.isEmpty()) return;

        var mc = Minecraft.getInstance();
        if(mc.player == null) return;
        if(mc.player.isCreative() && !Config.SHOW_OVERLAYS_IN_CREATIVE.get()) return;

        int height = mc.getWindow().getGuiScaledHeight();
        long now = System.currentTimeMillis();
        long duration = (long)(Config.SKILL_DURATION.get() * 1000);

        activeNotifications.removeIf(n -> (now - n.lastUpdate) > duration);
        if (activeNotifications.isEmpty()) return;


        int index = 0;

        for (var notif : activeNotifications) {
            renderSingleNotification(guiGraphics, notif, index, height, now);
            index++;
        }
    }



    private void renderSingleNotification(GuiGraphicsExtractor guiGraphics, Notification notif, int index, int screenHeight, long now){
        long age = now - notif.creationTime;
        long idleTime = now - notif.lastUpdate;
        long duration = (long)(Config.SKILL_DURATION.get() * 1000);
        float alpha = 1f;

        if(age < fadeIn) alpha = (float)age / fadeIn;
        else if (idleTime > (duration - fadeOut)) {
            alpha = (float)(duration - idleTime) / fadeOut;
        }
        else {
            float timeInPhase = age - fadeIn;
            float speed = .01f;
            alpha = .8f + .2f * Mth.sin(timeInPhase * speed);
        }

        alpha = Mth.clamp(alpha, 0f, 1f);
        if (alpha <= 0.05f) return;

        int cfgBg = ClientCache.parseColor(Config.SKILL_BG_COLOR.get(), 0xAA000000);
        int cfgBorder = ClientCache.parseColor(Config.SKILL_BD_COLOR.get(), 0xFFD6AD55);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(scale, scale);

        int iconSize = 16;
        int padding = 5;
        int boxSize = iconSize + padding * 2;


        int scaledScreenHeight = (int)(screenHeight/scale);
        int startY = (int)(scaledScreenHeight * .25f);

        int offsetX = (int)(Config.SKILL_OFFSET_X.get() / scale);
        int offsetY = (int)(Config.SKILL_OFFSET_Y.get() / scale);

        int drawX = (padding + 2) + offsetX;

        int drawY = startY + (index * spacing) - offsetY;

        int boxY = drawY - padding;
        int boxX = drawX - padding;

        int bgFinal = ((int)(((cfgBg >> 24) & 0xFF) * alpha) << 24) | (cfgBg & 0x00FFFFFF);
        int borderFinal = ((int)(((cfgBorder >> 24) & 0xFF) * alpha) << 24) | (cfgBorder & 0x00FFFFFF);

        renderTooltipStyleRect(guiGraphics, boxX, boxY, boxSize, boxSize, bgFinal);
        renderTooltipStyleBorder(guiGraphics, boxX, boxY, boxSize, boxSize, borderFinal);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, notif.icon(), drawX, drawY, 0, 0, iconSize, iconSize, iconSize, iconSize, ARGB.white(alpha));

        guiGraphics.pose().popMatrix();
    }

    public static void Clear(){
        activeNotifications.clear();
    }

    private void renderTooltipStyleRect(GuiGraphicsExtractor gui, int x, int y, int width, int height, int color) {
        gui.fill(x, y + 1, x + width, y + height - 1, color);
        gui.fill(x + 1, y, x + width - 1, y + 1, color);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, color);
    }

    private void renderTooltipStyleBorder(GuiGraphicsExtractor gui, int x, int y, int width, int height, int color) {
        gui.fill(x + 1, y, x + width - 1, y + 1, color);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, color);
        gui.fill(x, y + 1, x + 1, y + height - 1, color);
        gui.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }
}
