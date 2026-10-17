package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.active.ComponentManager;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class SkillInfoTooltip extends UITooltip {
    private static final int DESCRIPTION_WIDTH = 122;
    private static final int DESCRIPTION_Y = 12;

    private final ISkill skill;
    private final ClientCache cache;
    private final UIContainer requirementContainer;

    private boolean latestShiftState = false;
    private int latestCacheVersion = -1;

    private MutableComponent currentDescription;

    public SkillInfoTooltip(ISkill skill) {
        this.skill = skill;
        this.cache = ClientCache.get();

        this.addChild(new UIText(0, 0, () -> ComponentManager.getSkillFormattedName(skill).copy()));
        this.addChild(new UIText(0, DESCRIPTION_Y, () -> currentDescription).withWrapWidth(DESCRIPTION_WIDTH));

        requirementContainer = new UIContainer(0, 0);
        this.addChild(requirementContainer);

        updateData();
    }

    @Override
    public void updatePosition(int parentX, int parentY) {
        updateData();
        super.updatePosition(parentX, parentY);
    }

    //IMPL
    private void updateData() {
        var currentCacheVersion = cache.getVersion();
        var currentShiftState = Minecraft.getInstance().hasShiftDown();

        if (latestCacheVersion == currentCacheVersion && latestShiftState == currentShiftState) return;
        latestCacheVersion = currentCacheVersion; latestShiftState = currentShiftState;

        currentDescription = ComponentManager.getSkillDescriptionText(skill);
        refreshRequirementLines();
    }

    private void refreshRequirementLines() {
        requirementContainer.clear();

        var lines = ComponentManager.getSkillRequirements(skill);
        if (lines.isEmpty()) return;

        var font = Minecraft.getInstance().font;
        var startY = DESCRIPTION_Y + font.split(currentDescription, DESCRIPTION_WIDTH).size() * font.lineHeight + 4;

        if (!latestShiftState) {
            var hint = Component.translatable("gui.tyzs_skills.requirements").withStyle(ChatFormatting.DARK_GRAY);
            requirementContainer.addChild(new UIText(0, startY, () -> hint));
            return;
        }

        for (var line : lines) {
            var copy = line.copy();
            requirementContainer.addChild(new UIText(0, startY, () -> copy));
            startY += 11;
        }
    }
}
