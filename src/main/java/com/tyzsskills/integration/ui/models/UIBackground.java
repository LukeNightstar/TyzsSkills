package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.UIElement;
import net.minecraft.client.gui.GuiGraphics;
import oshi.util.tuples.Pair;

import java.util.function.Supplier;

public class UIBackground extends UIElement {
    private final int backgroundColor;

    private Supplier<Pair<Boolean, Integer>> drawBorder = () -> new Pair<>(false, 0);

    //Fluent
    public UIBackground withBorder(Supplier<Pair<Boolean, Integer>> border){this.drawBorder = border; return this;}

    public UIBackground(int offsetX, int offsetY, int width, int height, int backgroundColor) {
        super(offsetX, offsetY, width, height);
        this.backgroundColor = backgroundColor;
    }


    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        gui.fill(getRawX(), getRawY() + 1, getRawX() + getRawWidth(), getRawY() + getRawHeight() - 1, backgroundColor);
        gui.fill(getRawX() + 1, getRawY(), getRawX() + getRawWidth() - 1, getRawY() + 1, backgroundColor);
        gui.fill(getRawX() + 1, getRawY() + getRawHeight() - 1, getRawX() + getRawWidth() - 1, getRawY() + getRawHeight(), backgroundColor);


        var border = drawBorder.get();
        if(!border.getA()) return;
        var borderColor = drawBorder.get().getB();

        gui.fill(getRawX() + 1, getRawY(), getRawX() + getRawWidth() - 1, getRawY() + 1, borderColor);
        gui.fill(getRawX() + 1, getRawY() + getRawHeight() - 1, getRawX() + getRawWidth() - 1, getRawY() + getRawHeight(), borderColor);
        gui.fill(getRawX(), getRawY() + 1, getRawX() + 1, getRawY() + getRawHeight() - 1, borderColor);
        gui.fill(getRawX() + getRawWidth() - 1, getRawY() + 1, getRawX() + getRawWidth(), getRawY() + getRawHeight() - 1, borderColor);
    }
}
