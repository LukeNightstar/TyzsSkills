package com.tyzsskills.integration.ui.models;

import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.integration.ui.records.UIStyles;

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
}
