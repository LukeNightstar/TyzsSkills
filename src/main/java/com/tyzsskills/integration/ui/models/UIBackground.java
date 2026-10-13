package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.gui.GuiGraphics;
import oshi.util.tuples.Pair;

import java.util.function.Supplier;

public class UIBackground extends UIElement {
    private Supplier<Boolean> drawBorder = () -> true;
    private final Supplier<UIStyles.BackgroundStyle> style;

    //Fluent
    public UIBackground withBorder(Supplier<Boolean> drawBorder){this.drawBorder = drawBorder; return this;}

    public UIBackground(int offsetX, int offsetY, int width, int height, Supplier<UIStyles.BackgroundStyle> style) {
        super(offsetX, offsetY, width, height);
        this.style = style;
    }


    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        var currentStyle = style.get();

        gui.fill(getRawX(), getRawY() + 1, getRawX() + getRawWidth(), getRawY() + getRawHeight() - 1, currentStyle.backgroundColor());
        gui.fill(getRawX() + 1, getRawY(), getRawX() + getRawWidth() - 1, getRawY() + 1, currentStyle.backgroundColor());
        gui.fill(getRawX() + 1, getRawY() + getRawHeight() - 1, getRawX() + getRawWidth() - 1, getRawY() + getRawHeight(), currentStyle.backgroundColor());

        if(!drawBorder.get()) return;

        gui.fill(getRawX() + 1, getRawY(), getRawX() + getRawWidth() - 1, getRawY() + 1, currentStyle.borderColor());
        gui.fill(getRawX() + 1, getRawY() + getRawHeight() - 1, getRawX() + getRawWidth() - 1, getRawY() + getRawHeight(), currentStyle.borderColor());
        gui.fill(getRawX(), getRawY() + 1, getRawX() + 1, getRawY() + getRawHeight() - 1, currentStyle.borderColor());
        gui.fill(getRawX() + getRawWidth() - 1, getRawY() + 1, getRawX() + getRawWidth(), getRawY() + getRawHeight() - 1, currentStyle.borderColor());

    }
}
