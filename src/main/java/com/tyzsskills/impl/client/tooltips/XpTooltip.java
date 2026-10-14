package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.network.chat.Component;

public class XpTooltip extends UITooltip {
    public XpTooltip() {
        var cache = ClientCache.get();

        addChild(new UIText(0, 0, () -> Component.literal(cache.getXp() + "/" + FormatTools.defaultFloat(cache.getXpGoal())))
                .withScale(.75f, Enums.ScalePivot.CENTER));
    }
}
