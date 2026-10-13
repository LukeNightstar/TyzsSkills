package com.tyzsskills.integration.ui;

import com.mojang.datafixers.util.Either;
import com.tyzsskills.api.Enums;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
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

    protected Supplier<Boolean> visibleWhen = () -> true;
    protected Supplier<Boolean> shadedWhen = () -> false;
    protected Supplier<List<Either<FormattedText, TooltipComponent>>> tooltip = () -> EMPTY_TOOLTIP;

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

    //Tooltips
    private UIElement withRawTooltip(Supplier<List<Either<FormattedText, TooltipComponent>>> supplier) {
        this.tooltip = supplier != null ? supplier : () -> EMPTY_TOOLTIP;
        return this;
    }
    public UIElement withTooltip(Supplier<? extends FormattedText> supplier) {
        return this.withRawTooltip(() -> {
            FormattedText text = supplier.get();
            if (text == null) return EMPTY_TOOLTIP;
            return List.of(Either.left(text));
        });
    }
    public UIElement withTooltipLines(Supplier<? extends List<? extends FormattedText>> supplier) {
        return this.withRawTooltip(() -> {
            var lines = supplier.get();
            if (lines == null || lines.isEmpty()) return EMPTY_TOOLTIP;
            return lines.stream().<Either<FormattedText, TooltipComponent>>map(Either::left).toList();
        });
    }
    public UIElement withCustomTooltip(Supplier<? extends TooltipComponent> supplier) {
        return this.withRawTooltip(() -> {
            TooltipComponent data = supplier.get();
            if (data == null) return EMPTY_TOOLTIP;
            return List.of(Either.right(data));
        });
    }

    //Draws
    protected abstract void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick);

    public final void draw(GuiGraphics gui, int mouseX, int mouseY, float partialTick, boolean inheritedVisible, boolean inheritedShaded){
        var visible = inheritedVisible && visibleWhen.get();
        if(!visible) return;

        currentShaded = inheritedShaded || shadedWhen.get();

        if(scale != 1f) drawScaled(gui, mouseX, mouseY, partialTick, currentShaded);
        else drawShadedOrNot(gui, mouseX, mouseY, partialTick, currentShaded);
    }

    public final void draw(GuiGraphics gui, int mouseX, int mouseY, float partialTick){
        draw(gui, mouseX, mouseY, partialTick, true, false);
    }

    public void drawTooltips(GuiGraphics gui, Font font, int mouseX, int mouseY) {
        var tooltips = getTooltips(mouseX, mouseY);
        if (tooltips.isEmpty()) return;

        gui.renderComponentTooltipFromElements(font, tooltips, mouseX, mouseY, ItemStack.EMPTY);
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
    protected List<Either<FormattedText, TooltipComponent>> resolveTooltips(int mouseX, int mouseY) {
        return tooltip.get();
    }

    public final List<Either<FormattedText, TooltipComponent>> getTooltips(int mouseX, int mouseY, boolean inheritedVisible) {
        boolean visible = inheritedVisible && visibleWhen.get();
        if (!visible || !isHovering(mouseX, mouseY)) return EMPTY_TOOLTIP;
        return resolveTooltips(mouseX, mouseY);
    }

    public final List<Either<FormattedText, TooltipComponent>> getTooltips(int mouseX, int mouseY) {
        return getTooltips(mouseX, mouseY, true);
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
    private void drawShadedOrNot(GuiGraphics gui, int mouseX, int mouseY, float partialTick, boolean shaded) {
        if (!shaded) { render(gui, mouseX, mouseY, partialTick); return; }
        gui.setColor(0.3f, 0.3f, 0.3f, 1.0f);
        render(gui, mouseX, mouseY, partialTick);
        gui.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
    private void drawScaled(GuiGraphics gui, int mouseX, int mouseY, float partialTick, boolean shaded) {
        var pivotX = getPivotX();
        var pivotY = getPivotY();

        gui.pose().pushPose();
        gui.pose().translate(pivotX, pivotY, 0);
        gui.pose().scale(scale, scale, 1.0f);
        gui.pose().translate(-pivotX, -pivotY, 0);

        drawShadedOrNot(gui, mouseX, mouseY, partialTick, shaded);

        gui.pose().popPose();
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
