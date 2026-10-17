package com.tyzsskills.impl.client.models;

import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import com.tyzsskills.impl.client.screen.SkillWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class CustomScrollView extends ObjectSelectionList<CustomScrollView.Entry> {

    private final Identifier texture;
    private final int textureW, textureH;
    private final int uScroll, vScroll;
    private final int uScrollHover, vScrollHover;
    private final int scrollWidth, scrollHeight;

    private boolean isScrolling = false;
    private double scrollClickOffset = 0;

    /*
     * Constructor:
     * x,y : Screen coordinates (where to draw list container)
     * textureW, textureH : Container size
     * itemHeight : Line height (1 entry on the container)
     * uBackground, vBackground : Images coordinates on ref texture
     */

    public CustomScrollView(Minecraft minecraft, int x, int y, int width, int height, int itemHeight,
                                Identifier texture, int textureW, int textureH,
                               int uScroll, int vScroll, int uScrollHover, int vScrollHover, int scrollWidth, int scrollHeight){

        super (minecraft, width, height, y, itemHeight);

        this.setX(x);

        this.texture = texture;
        this.textureW = textureW;
        this.textureH = textureH;
        this.uScroll = uScroll;
        this.vScroll = vScroll;
        this.uScrollHover = uScrollHover;
        this.vScrollHover = vScrollHover;
        this.scrollWidth = scrollWidth;
        this.scrollHeight = scrollHeight;

    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick){

        gui.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);

        this.extractListItems(gui, mouseX, mouseY, partialTick);

        gui.disableScissor();

        int maxScroll = this.maxScrollAmount();
        if(maxScroll > 0){
            int scrollBarX = this.scrollBarX();
            int scrollBarY = this.getScrollBarTop();

            boolean isHoveringBar = mouseX >= scrollBarX && mouseX < scrollBarX + scrollWidth
                    && mouseY >= scrollBarY && mouseY < scrollBarY + scrollHeight;

            int currentU = uScroll;
            int currentV = vScroll;

            if(isHoveringBar || isScrolling) {currentU = uScrollHover; currentV = vScrollHover;}

            gui.blit(RenderPipelines.GUI_TEXTURED, texture, scrollBarX, scrollBarY, currentU, currentV, scrollWidth, scrollHeight, textureW, textureH);

        }
    }

    public void clearEntries(){
        super.clearEntries();
    }

    public void AddEntry(SkillEntry entry){
        super.addEntry(entry);
    }

    public SkillWidget getHoveredWidget(int mouseX, int mouseY) {
        CustomScrollView.Entry entry = this.getEntryAtPosition(mouseX, mouseY);

        if (entry instanceof SkillEntry skillEntry) {
            return skillEntry.getHoveredWidget(mouseX, mouseY);
        }

        return null;
    }

    private int getScrollBarTop() {
        int maxScroll = this.maxScrollAmount();
        if (maxScroll <= 0) return this.getY();

        int barTop = this.getY() + (int)((float)this.scrollAmount() * (float)(this.height - scrollHeight) / (float)maxScroll);

        if (barTop < this.getY()) barTop = this.getY();
        if (barTop > this.getY() + this.height - scrollHeight) barTop = this.getY() + this.height - scrollHeight;

        return barTop;
    }

    @Override
    protected int scrollBarX(){
        return this.getX() + this.width - scrollWidth - 2;
    }

    @Override
    public int getRowWidth(){
        return this.width - scrollWidth - 4;
    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor gui){
        //Enleve le fond e terre
    }

    public abstract static class Entry extends ObjectSelectionList.Entry<Entry>{
        @Override
        public @NotNull Component getNarration(){
            return Component.empty();
        }
    }

    @Override
    protected void extractSelection(GuiGraphicsExtractor gui, Entry entry, int outlineColor) {
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        this.isScrolling = false;
        double mouseX = event.x(), mouseY = event.y();

        if (this.maxScrollAmount() > 0) {
            int barX = this.scrollBarX();
            int barY = this.getScrollBarTop();

            if (mouseX >= barX && mouseX <= barX + scrollWidth &&
                    mouseY >= barY && mouseY <= barY + scrollHeight) {

                this.isScrolling = true;
                this.scrollClickOffset = mouseY - barY;
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.isScrolling = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (this.isScrolling) {
            double mouseY = event.y();
            int maxScroll = this.maxScrollAmount();
            int trackHeight = this.height;

            if (trackHeight > scrollHeight) {
                double d0 = mouseY - this.getY() - this.scrollClickOffset;

                double newScroll = d0 * (double)maxScroll / (double)(trackHeight - scrollHeight);
                this.setScrollAmount(newScroll);
            }
            return true;
        }
        return false;

    }
}
