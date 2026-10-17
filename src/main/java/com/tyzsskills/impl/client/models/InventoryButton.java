package com.tyzsskills.impl.client.models;

import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.SoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class InventoryButton extends AbstractWidget {


    private static final Identifier TEXTURE_NORMAL = Identifier.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/background.png");

    private final Runnable onPress;
    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public InventoryButton(int x, int y, Runnable onPress) {
        super(x, y, 20, 19, Component.literal("Skills Button"));
        this.onPress = onPress;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isDragging) {
            this.setX(mouseX - this.dragOffsetX);
            this.setY(mouseY - this.dragOffsetY);
        }

        int currentU = this.isHovered ? 256 : 232;


        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE_NORMAL, this.getX(), this.getY(), currentU, 211, 20, 19, 325, 325);

        if (this.isHovered() && !this.isDragging) {
            guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font,
                    Component.translatable("button.tyzs_skills.inventory_btn"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.isHoveredOrFocused()) {
            int button = event.button();
            if (button == 1) {
                this.isDragging = true;
                this.dragOffsetX = (int) (event.x() - this.getX());
                this.dragOffsetY = (int) (event.y() - this.getY());
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            } else if (button == 0) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onPress.run();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.isDragging) {
            this.isDragging = false;
            Config.INVENTORY_BUTTON_X.set(this.getX());
            Config.INVENTORY_BUTTON_Y.set(this.getY());
            Config.CLIENT_SPEC.save();
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @Override
    public void playDownSound(SoundManager handler) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null) SoundPlayer.PlayUIClick();
    }
}
