package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;


public class PurchaseTooltip extends UITooltip {
    private final ISkill skill;
    private final ClientCache cache;

    private int currentLvl;
    private int currentSp;

    private int currentPrice;
    private int currentMaxPrice;
    private final List<Float> currentData = new ArrayList<>();
    private final List<Float> currentMaxData = new ArrayList<>();


    private int latestCacheVersion = -1;

    public PurchaseTooltip(ISkill skill) {
        this.skill = skill;
        this.cache = ClientCache.get();

        this.addChild(new UIText(0, 0, this::getFinalPriceLine));

        updateData();
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        updateData();
        super.render(gui, mouseX, mouseY, partialTick);
    }

    //IMPL
    private void updateData() {
        var currentCacheVersion = cache.getVersion();
        if (latestCacheVersion == currentCacheVersion) return;

        latestCacheVersion = currentCacheVersion;

        currentLvl = cache.getSkillLevel(skill.getID());
        currentSp = cache.getSp();

        currentPrice = skill.getPrice(currentLvl + 1);
        updateMaxPrice();

        updateCurrentData();
        currentMaxData.clear();
    }

    //UTILS
    //==================TEXTS==================
    private MutableComponent getFinalPriceLine(){
        var price = Screen.hasShiftDown() ? currentMaxPrice : currentPrice;
        var priceColor = currentSp >= currentPrice ? ChatFormatting.BLUE : ChatFormatting.RED;

        return currentLvl < skill.getMaximumLevel()

                ? Component.empty()
                .append(Component.translatable("gui.tyzs_skills.cost").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(price + " ").withStyle(priceColor))
                .append(Component.translatable("gui.tyzs_skills.SP").withStyle(priceColor))

                : Component.translatable("config.common.tyzs_skills.max_lvl").withStyle(ChatFormatting.GOLD);
    }


    //==================DATA==================
    private void updateMaxPrice(){
        var availableSp = currentSp;
        var maxPrice = 0;
        for(var i = currentLvl + 1; i <= skill.getMaximumLevel(); i++){
            currentPrice = skill.getPrice(i);
            if(availableSp < currentPrice) break;

            availableSp -= currentPrice;
            maxPrice += currentPrice;
        }
        currentMaxPrice = maxPrice == 0 ? currentPrice : maxPrice;
    }

    private void updateCurrentData(){
        currentData.clear();

        if(skill.getType() == Enums.SkillType.IMMUTABLE){
            for(var set : skill.getValues().values()){
                currentData.add(set.getValue(currentLvl + 1));
            }
        }
        else{
            for(var set : skill.getModifiers()){
                currentData.add(set.getValue(currentLvl + 1));
            }
        }
    }


}
