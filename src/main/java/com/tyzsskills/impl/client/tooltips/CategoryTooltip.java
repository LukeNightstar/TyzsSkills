package com.tyzsskills.impl.client.tooltips;


import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.Category;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIImage;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CategoryTooltip extends UITooltip {

    private static final UIStyles.BackgroundStyle TOOLTIP_CUSTOM =
            new UIStyles.BackgroundStyle(Styles.TOOLTIP.backgroundStartColor(),
                    Styles.TOOLTIP.backgroundEndColor(), Styles.TOOLTIP.borderStartColor(), Styles.TOOLTIP.borderEndColor(),
                    3, 3);


    public CategoryTooltip(Category category) {
        super(() -> TOOLTIP_CUSTOM);

        var parsedIcon = ResourceLocation.tryParse(category.icon());
        var icon = parsedIcon == null ? Styles.DEFAULT_SKILL_ICON.texture() : parsedIcon;
        var displayName = Component.translatable(category.displayName());

        addChild(new UIImage(0, 0, 16, 16, icon, 16).withScale(.6f, Enums.ScalePivot.CENTER));
        addChild(new UIText(13,  4, () -> displayName).withScale(.77f, Enums.ScalePivot.CENTER));
    }
}
