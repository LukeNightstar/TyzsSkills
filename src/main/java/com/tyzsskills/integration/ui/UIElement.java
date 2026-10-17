package com.tyzsskills.integration.ui;

import com.mojang.datafixers.util.Either;
import com.tyzsskills.api.Enums;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.ARGB;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public abstract class UIElement {

    private int offsetX, offsetY;
    private int rawX, rawY, rawWidth, rawHeight;
    private float scale = 1f;
    protected Enums.ScalePivot scalePivot = Enums.ScalePivot.TOP_LEFT;

    private boolean currentShaded = false;
    protected boolean isCurrentShaded(){return currentShaded;}

    private boolean currentHovered = false;
    public boolean isHovered() { return currentHovered; }

    protected Supplier<UITooltip> tooltip = () -> null;
    protected Supplier<Boolean> visibleWhen = () -> true;
    protected Supplier<Boolean> shadedWhen = () -> false;

    // 26.1 has no global shader color, so shading is applied per draw call through tinted()
    private static final int SHADED_TINT = ARGB.colorFromFloat(1.0f, 0.3f, 0.3f, 0.3f);
    private static int tint = -1;
    public static int tinted(int color) {return ARGB.multiply(color, tint);}

    public UIElement(int offsetX, int offsetY, int width, int height){
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        this.rawWidth = width;
        this.rawHeight = height;
    }

    //Fluents
    public UIElement withVisibility(Supplier<Boolean> supplier) {this.visibleWhen = supplier; return this;}
    public UIElement withShade(Supplier<Boolean> supplier) {this.shadedWhen = supplier; return this;}
    public UIElement withScale(float scale, Enums.ScalePivot scalePivot) {this.scale = scale; this.scalePivot = scalePivot; return this;}
    public UIElement withTooltip(Supplier<UITooltip> supplier){tooltip = supplier; return this;}


    //Draws
    protected abstract void render(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick);

    public final void draw(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick, boolean inheritedVisible, boolean inheritedShaded){
        var visible = inheritedVisible && visibleWhen.get();
        if(!visible) {
            currentHovered = false;
            return;
        }

        currentHovered = isHovering(mouseX, mouseY);
        currentShaded = inheritedShaded || shadedWhen.get();


        if(scale != 1f) drawScaled(gui, mouseX, mouseY, partialTick, currentShaded);
        else drawShadedOrNot(gui, mouseX, mouseY, partialTick, currentShaded);
    }

    public final void draw(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick){
        draw(gui, mouseX, mouseY, partialTick, true, false);
    }

    //Clicks
    protected boolean onClick(int mouseX, int mouseY){return false;}

    public final boolean handleClick(int mouseX, int mouseY, boolean inheritedVisible) {
        boolean visible = inheritedVisible && visibleWhen.get();
        if (!visible) return false;
        return onClick(mouseX, mouseY);
    }

    public final boolean handleClick(int mouseX, int mouseY){
        return handleClick(mouseX, mouseY, true);
    }

    //Scrolls
    protected boolean onScroll(double mouseX, double mouseY, double deltaY) {return false;}

    public final boolean handleScroll(double mouseX, double mouseY, double deltaY, boolean inheritedVisible) {
        boolean visible = inheritedVisible && visibleWhen.get();
        if (!visible) return false;
        return onScroll(mouseX, mouseY, deltaY);
    }

    public final boolean handleScroll(double mouseX, double mouseY, double deltaY){
        return handleScroll(mouseX, mouseY, deltaY, true);
    }

    //Drags
    protected boolean onDrag(double mouseX, double mouseY, int button, double dragX, double dragY){return false;}

    public final boolean handleDrag(double mouseX, double mouseY, int button, double dragX, double dragY, boolean inheritedVisible) {
        boolean visible = inheritedVisible && visibleWhen.get();
        if (!visible) return false;
        return onDrag(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean handleDrag(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return handleDrag(mouseX, mouseY, button, dragX, dragY, true);
    }

    //Releases
    protected boolean onRelease(double mouseX, double mouseY, int button) { return false; }

    public boolean handleRelease(double mouseX, double mouseY, int button, boolean inheritedVisible) {
        boolean visible = inheritedVisible && visibleWhen.get();
        if (!visible) return false;
        return onRelease(mouseX, mouseY, button);
    }

    public boolean handleRelease(double mouseX, double mouseY, int button) {
        return handleRelease(mouseX, mouseY, button, true);
    }

    //Tooltips
    public UITooltip getHoveredTooltip(int mouseX, int mouseY) {
        if (visibleWhen.get() && isHovering(mouseX, mouseY)) return tooltip.get();
        return null;
    }


    //Tools
    public boolean isHovering(int mouseX, int mouseY){
        var visualWidth = getVisualWidth(); var visualHeight = getVisualHeight();
        if (visualWidth <= 0 || visualHeight <= 0) return false;

        var visualX = getVisualX(); var visualY = getVisualY();
        return mouseX >= visualX && mouseX < visualX + visualWidth && mouseY >= visualY && mouseY < visualY + visualHeight;
    }
    public void updatePosition(int parentX, int parentY){
        this.rawX = parentX + this.offsetX;
        this.rawY = parentY + this.offsetY;
    }

    //Utils
    private void drawShadedOrNot(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick, boolean shaded) {
        if (!shaded) { render(gui, mouseX, mouseY, partialTick); return; }
        var previousTint = tint;
        tint = SHADED_TINT;
        render(gui, mouseX, mouseY, partialTick);
        tint = previousTint;
    }
    private void drawScaled(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick, boolean shaded) {
        var pivotX = getPivotX();
        var pivotY = getPivotY();

        gui.pose().pushMatrix();
        gui.pose().translate(pivotX, pivotY);
        gui.pose().scale(scale, scale);
        gui.pose().translate(-pivotX, -pivotY);

        drawShadedOrNot(gui, mouseX, mouseY, partialTick, shaded);

        gui.pose().popMatrix();
    }
    protected float getPivotX(){return scalePivot == Enums.ScalePivot.CENTER ? getRawX() + getRawWidth() / 2f : getRawX();}
    protected float getPivotY(){return scalePivot == Enums.ScalePivot.CENTER ? getRawY() + getRawHeight() / 2f : getRawY();}


    //GETTERS
    //=============RAW=============
    public int getRawX(){return rawX;}
    public int getRawY(){return rawY;}
    public int getRawWidth(){return rawWidth;}
    public int getRawHeight(){return rawHeight;}
    public int getOffsetX(){return offsetX;}
    public int getOffsetY(){return offsetY;}
    public float getScale(){return scale;}
    public boolean isVisible(){return visibleWhen.get();}

    //=============VISUAL=============
    public float getVisualWidth(){return getRawWidth() * scale;}
    public float getVisualHeight(){return getRawHeight() * scale;}
    public float getVisualX(){
        float pivotX = getPivotX();
        return pivotX + (getRawX() - pivotX) * scale;
    }
    public float getVisualY(){
        float pivotY = getPivotY();
        return pivotY + (getRawY() - pivotY) * scale;
    }



    //STATIC
    public static final List<Either<FormattedText, TooltipComponent>> EMPTY_TOOLTIP = List.of();


}
