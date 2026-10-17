package com.tyzsskills.integration.ui.models;

import net.minecraft.client.renderer.RenderPipelines;
import com.tyzsskills.integration.ui.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class UIImage extends UIElement {

    private Identifier texture;
    private int textureSize;

    private final boolean isUniqueTexture;
    private Supplier<ImageStyle> style;


    public UIImage(int offsetX, int offsetY, int width, int height, Identifier texture, int textureSize) {
        super(offsetX, offsetY, width, height);

        this.texture = texture;
        this.textureSize = textureSize;

        isUniqueTexture = true;
    }

    public UIImage(int offsetX, int offsetY, Supplier<ImageStyle> style) {
        super(offsetX, offsetY, style.get().width(), style.get().height());

        this.style = style;

        isUniqueTexture = false;
    }

    @Override
    public void render(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
        if(isUniqueTexture) gui.blit(RenderPipelines.GUI_TEXTURED, texture, getRawX(), getRawY(), 0, 0, getRawWidth(), getRawHeight(), textureSize, textureSize, tinted(-1));
        else {
            var currentStyle = style.get();
            gui.blit(RenderPipelines.GUI_TEXTURED, currentStyle.image().texture(), getRawX(), getRawY(), currentStyle.u(), currentStyle.v(),
                    currentStyle.width(), currentStyle.height(), currentStyle.image().size(), currentStyle.image().size(), tinted(-1));
        }
    }
}
