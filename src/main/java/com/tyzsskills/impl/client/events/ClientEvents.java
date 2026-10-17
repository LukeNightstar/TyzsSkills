package com.tyzsskills.impl.client.events;


import net.minecraft.resources.Identifier;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.screen.MainGUI;
import com.tyzsskills.impl.client.tooltips.CategoryTooltip;
import com.tyzsskills.impl.client.tooltips.CategoryTooltipData;
import com.tyzsskills.impl.client.tooltips.SkillTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

public class ClientEvents {
    // 26.1 tooltips are drawn from sprites, so the old background/border colors live in
    // textures/gui/sprites/tooltip/skill_*.png and are selected through the tooltip style
    private static final Identifier SKILL_STYLE = Identifier.fromNamespaceAndPath(Tyzsskills.MODID, "skill");
    private static final Identifier SKILL_MAXED_STYLE = Identifier.fromNamespaceAndPath(Tyzsskills.MODID, "skill_maxed");

    @SubscribeEvent
    public static void onTooltipTexture(RenderTooltipEvent.Texture event) {
        if (!(Minecraft.getInstance().screen instanceof MainGUI)) return;

        for(var component : event.getComponents()){
            if(component instanceof SkillTooltip tooltip){
                var skill = tooltip.getSkill();
                boolean isMaxed = ClientCache.getSkillLevel(skill.getID()) >= skill.getMaximumLevel();

                event.setTexture(isMaxed ? SKILL_MAXED_STYLE : SKILL_STYLE);
                break;
            }
        }
    }
}
