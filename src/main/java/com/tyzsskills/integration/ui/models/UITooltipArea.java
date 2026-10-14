package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.UIElement;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class UITooltipArea extends UIElement {

    public UITooltipArea(int offsetX, int offsetY, int width, int height, Supplier<UITooltip> tooltip){
        super(offsetX, offsetY, width, height);

        this.withTooltip(tooltip);
    }

    @Override
    protected void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {}
}
