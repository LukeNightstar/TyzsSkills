package com.tyzsskills.integration.ui.models;

import com.tyzsskills.api.Enums;
import com.tyzsskills.integration.ui.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class UIText extends UIElement {

    private final Font FONT = Minecraft.getInstance().font;
    private final Supplier<MutableComponent> textExtractor;

    private Supplier<UIStyles.BackgroundStyle> backgroundStyle;
    private Supplier<Boolean> showBackgroundBorder = () -> false;

    private Enums.TextAlignment alignment = Enums.TextAlignment.LEFT;
    private int wrapWidth = -1;

    //fluent
    public UIText withWrapWidth(int wrapWidth){this.wrapWidth = wrapWidth; return this;}
    public UIText withAlignment(Enums.TextAlignment alignment){this.alignment = alignment; return this;}
    public UIText withBackground(Supplier<UIStyles.BackgroundStyle> backgroundStyle, Supplier<Boolean> showBackgroundBorder) {
        this.backgroundStyle = backgroundStyle; this.showBackgroundBorder = showBackgroundBorder; return this;}

    public UIText(int offsetX, int offsetY, Supplier<MutableComponent> textExtractor) {
        super(offsetX, offsetY, 0, 0);
        this.textExtractor = textExtractor;
    }

    //PARENT
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        var finalText = textExtractor.get();
        if (finalText == null) return;

        var drawX = getTextRawX();
        var drawY = super.getRawY();
        var textWidth = getTextRawWidth();
        var textHeight = getTextRawHeight();

        renderBackground(gui, drawX, drawY, textWidth, textHeight);

        if (wrapWidth > 0) gui.drawWordWrap(FONT, finalText, drawX, drawY, wrapWidth, 0xFFFFFFFF);
        else gui.drawString(FONT, finalText, drawX, drawY, 0xFFFFFFFF, false);
    }


    @Override
    public int getRawWidth() {
        int textW = getTextRawWidth();
        return textW > 0 ? textW + (getPaddingX() * 2) : 0;
    }
    @Override
    public int getRawHeight() {
        var textH = getTextRawHeight();
        return textH > 0 ? textH + (getPaddingY() * 2) : 0;
    }
    @Override
    public int getRawX() {
        return getTextRawX() - getPaddingX();
    }
    @Override
    public int getRawY() {
        return super.getRawY() - getPaddingY();
    }
    @Override
    protected float getPivotX() {
        var rawX = getRawX();
        var rawW = getRawWidth();

        if (wrapWidth > 0) return scalePivot == Enums.ScalePivot.CENTER ? rawX + (rawW / 2f) : super.getRawX();

        return switch (alignment) {
            case RIGHT -> getTextRawX() + getTextRawWidth();
            case CENTER -> rawX + (rawW / 2f);
            case LEFT -> scalePivot == Enums.ScalePivot.CENTER ? rawX + (rawW / 2f) : super.getRawX();
        };
    }
    @Override
    protected float getPivotY() {return scalePivot == Enums.ScalePivot.CENTER ? getRawY() + (getRawHeight() / 2f) : getRawY();}


    //IMPLEMENTATION
    private void renderBackground(GuiGraphics gui, int textX, int textY, int textW, int textH){
        if(backgroundStyle == null) return;

        var currentStyle = backgroundStyle.get();

        var bgX = textX - currentStyle.paddingX();
        var bgY = textY - currentStyle.paddingY();
        var bgW = textW + (currentStyle.paddingX() * 2);
        var bgH = textH + (currentStyle.paddingY() * 2);

        gui.fill(bgX, bgY + 1, bgX + bgW, bgY + bgH - 1, currentStyle.backgroundColor());
        gui.fill(bgX + 1, bgY, bgX + bgW - 1, bgY + 1, currentStyle.backgroundColor());
        gui.fill(bgX + 1, bgY + bgH - 1, bgX + bgW - 1, bgY + bgH, currentStyle.backgroundColor());

        if (!showBackgroundBorder.get()) return;

        gui.fill(bgX + 1, bgY, bgX + bgW - 1, bgY + 1, currentStyle.borderColor());
        gui.fill(bgX + 1, bgY + bgH - 1, bgX + bgW - 1, bgY + bgH, currentStyle.borderColor());
        gui.fill(bgX, bgY + 1, bgX + 1, bgY + bgH - 1, currentStyle.borderColor());
        gui.fill(bgX + bgW - 1, bgY + 1, bgX + bgW, bgY + bgH - 1, currentStyle.borderColor());

    }

    //UTILS
    private int getTextRawWidth() {
        var finalText = textExtractor.get();
        if (finalText == null) return 0;

        if (wrapWidth > 0) {
            var lines = FONT.split(finalText, wrapWidth);
            var maxLineW = 0;
            for (var line : lines) maxLineW = Math.max(maxLineW, FONT.width(line));
            return maxLineW;
        }
        return FONT.width(finalText);
    }
    private int getTextRawHeight() {
        var finalText = textExtractor.get();
        if (finalText == null) return 0;

        if (wrapWidth > 0) {
            var lines = FONT.split(finalText, wrapWidth);
            return Math.max(FONT.lineHeight, lines.size() * FONT.lineHeight);
        }
        return FONT.lineHeight;
    }
    private int getTextRawX() {
        var baseX = super.getRawX();
        if (wrapWidth > 0) return baseX;

        var boxWidth = super.getRawWidth();
        var textW = getTextRawWidth();
        return switch (alignment) {
            case RIGHT -> baseX + boxWidth - textW;
            case CENTER -> baseX + (boxWidth - textW) / 2;
            case LEFT -> baseX;
        };
    }
    private int getPaddingX() {return backgroundStyle != null && backgroundStyle.get() != null ? backgroundStyle.get().paddingX() : 0;}
    private int getPaddingY() {return backgroundStyle != null && backgroundStyle.get() != null ? backgroundStyle.get().paddingY() : 0;}

}
