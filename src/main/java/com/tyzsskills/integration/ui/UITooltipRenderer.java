package com.tyzsskills.integration.ui;

import com.tyzsskills.integration.ui.models.UIBackground;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.gui.GuiGraphics;

public class UITooltipRenderer {
    public static void render(GuiGraphics gui, UIContainer tooltip, int mouseX, int mouseY, int screenWidth, int screenHeight, UIStyles.BackgroundStyle style) {
        if (tooltip == null) return;

        tooltip.updatePosition(0, 0);

        var padX = style.paddingX();
        var padY = style.paddingY();

        var contentWidth = Math.round(tooltip.getVisualWidth());
        var contentHeight = Math.round(tooltip.getVisualHeight());
        if (contentWidth <= 0 || contentHeight <= 0) return;

        var shiftX = Math.round(tooltip.getVisualX());
        var shiftY = Math.round(tooltip.getVisualY());

        var totalWidth = contentWidth + (padX * 2);
        var totalHeight = contentHeight + (padY * 2);

        var x = mouseX + 10;
        var y = mouseY - 10;

        if (x + totalWidth > screenWidth - 4) x = mouseX - 10 - totalWidth;
        if (y + totalHeight > screenHeight - 4) y = screenHeight - 4 - totalHeight;
        if (y < 4) y = 4;

        gui.pose().pushPose();
        gui.pose().translate(x, y, 400.0f);

        var background = new UIBackground(0, 0, totalWidth, totalHeight, () -> style);
        background.updatePosition(0, 0);
        background.render(gui, -1, -1, 0f);

        tooltip.updatePosition(padX - shiftX, padY - shiftY);
        tooltip.draw(gui, mouseX - x, mouseY - y, 0f);

        gui.pose().popPose();
    }
}
