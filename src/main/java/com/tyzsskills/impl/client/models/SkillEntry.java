package com.tyzsskills.impl.client.models;

import net.minecraft.client.input.MouseButtonEvent;
import com.tyzsskills.impl.client.screen.SkillWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SkillEntry extends CustomScrollView.Entry{
    private final List<SkillWidget> widgets = new ArrayList<>();
    private final int spacing = 2;

    public SkillEntry(){

    }

    public void addWidget(SkillWidget widget){
        this.widgets.add(widget);
    }

    public SkillWidget getHoveredWidget(double mouseX, double mouseY) {
        for (SkillWidget widget : widgets) {
            if (widget.isMouseOver((int)mouseX, (int)mouseY)) {
                return widget;
            }
        }
        return null;
    }

    @Override
    public void extractContent(GuiGraphicsExtractor gui, int mouseX, int mouseY, boolean isHovered, float partialTick) {
        int currentX = this.getContentX();
        int top = this.getContentY();

        for(SkillWidget widget : widgets){
            widget.render(gui, currentX, top, mouseX, mouseY, partialTick);

            currentX+= SkillWidget.WIDTH + spacing;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick){
        for(SkillWidget widget : widgets){
            if(widget.mouseClicked(event.x(), event.y(), event.button())){
                return true;
            }
        }
        return  false;
    }

    @Override
    public @NotNull Component getNarration() {
        return Component.empty();
    }

}
