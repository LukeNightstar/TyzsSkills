package com.tyzsskills.integration.ui.models;

import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class UITooltip extends UIContainer{

    public final Supplier<UIStyles.BackgroundStyle> tooltipStyle;

    public UITooltip(){
        this(() -> Styles.TOOLTIP);
    }

    public UITooltip(Supplier<UIStyles.BackgroundStyle> style){
        super(0, 0);
        tooltipStyle = style;
    }

    public UITooltip(MutableComponent text){
        this();
        this.addChild(new UIText(0, 0, () -> text));
    }

    public UITooltip(MutableComponent text, Supplier<UIStyles.BackgroundStyle> style){
        this(() -> Styles.TOOLTIP);
        this.addChild(new UIText(0, 0, () -> text));
    }



}
