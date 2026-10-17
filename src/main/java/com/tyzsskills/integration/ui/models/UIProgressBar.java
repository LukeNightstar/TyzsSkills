package com.tyzsskills.integration.ui.models;

import net.minecraft.client.renderer.RenderPipelines;
import com.tyzsskills.integration.ui.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.function.Supplier;

public class UIProgressBar extends UIElement {

    private final Supplier<UIStyles.ImageStyle> style;
    private Supplier<Float> value = () -> 0f;

    public UIProgressBar(int offsetX, int offsetY, Supplier<UIStyles.ImageStyle> style, Supplier<Float> value){
        super(offsetX, offsetY, style.get().width(), style.get().height());

        this.style = style;
        this.value = value;
    }

    @Override
    protected void render(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
        var ratio = Math.min(1f, value.get());
        if(ratio <= 0f) return;

        var currentStyle = style.get();

        var widthToDraw = (int)(ratio * currentStyle.width());
        if(widthToDraw > 0f) {
            gui.blit(RenderPipelines.GUI_TEXTURED, currentStyle.image().texture(), getRawX(), getRawY(), currentStyle.u(), currentStyle.v(),
                    widthToDraw, currentStyle.height(), currentStyle.image().size(), currentStyle.image().size(), tinted(-1));
        }
    }
}
