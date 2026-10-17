package com.tyzsskills.integration.ui.models;

import com.tyzsskills.api.Enums;
import com.tyzsskills.integration.ui.UIElement;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

public class UIEntity extends UIElement {
    private final Supplier<LivingEntity> entityExtractor;
    private final int entityScale;


    public UIEntity(int offsetX, int offsetY, int width, int height, Supplier<LivingEntity> entityExtractor, int entityScale){
        super(offsetX, offsetY, width, height);

        this.entityExtractor = entityExtractor;
        this.entityScale = entityScale;
    }

    @Override
    public void render(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
        var entity = entityExtractor.get();
        if(entity == null) return;

        // 26.1 draws entities as a picture-in-picture inside the given box and handles the look-at-mouse rotation itself
        InventoryScreen.extractEntityInInventoryFollowsMouse(gui, getRawX(), getRawY(), getRawX() + getRawWidth(), getRawY() + getRawHeight(),
                entityScale, 0.0625f, mouseX, mouseY, entity);
    }

    @Override
    public UIElement withScale(float scale, Enums.ScalePivot scalePivot) {return this;}
    @Override
    public UIElement withShade(Supplier<Boolean> supplier) {return this;}
}
