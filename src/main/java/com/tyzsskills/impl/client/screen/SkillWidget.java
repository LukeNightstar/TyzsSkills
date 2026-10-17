package com.tyzsskills.impl.client.screen;

import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.minecraft.util.ARGB;
import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.datafixers.util.Either;
import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.SoundPlayer;
import com.tyzsskills.impl.client.tooltips.SkillTooltipData;
import com.tyzsskills.impl.client.tools.StringTools;
import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.payloads.CActionSkillPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.network.PacketDistributor;


import java.util.ArrayList;
import java.util.List;

public class SkillWidget {

    protected static final Identifier DEFAULT_ICON = Identifier.parse("minecraft:textures/item/barrier.png");

    protected static final Identifier REF_TEXTURE = Identifier.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/background.png");
    protected static final int TEXTURE_W = 325, TEXTURE_H = 325;

    protected static final int U_BACKGROUND = 166, V_BACKGROUND = 142;
    protected static final int U_BACKGROUND_FINAL = 230;

    public static final int WIDTH = 64, HEIGHT = 30; //Widget Size on screen

    protected static final int BTN_W = 9, BTN_H = 9;

    protected static final int U_BUY_BTN = 169 ,V_BUY_BTN = 173;
    protected static final int U_BUY_BTN_HOVER = 178;
    protected static final int U_BUY_ALL_BTN = 260;
    protected static final int U_BUY_ALL_BTN_HOVER = 269;

    protected static final int U_BOOK_BTN_HOVER = 226 ,V_BOOK_BTN_HOVER = 173;
    protected static final int U_BOOK_ACTIVE = 237 , V_BOOK_ACTIVE = 174;
    protected static final int U_BOOK_NEUTRAL = 246;



    protected static final int U_REFUND_BTN = 200 ,V_REFUND_BTN = 173;
    protected static final int U_REFUND_BTN_HOVER = 209;
    protected static final int U_REFUND_ALL_BTN = 283;
    protected static final int U_REFUND_ALL_BTN_HOVER = 292;

    protected final Skill skill;
    protected final Identifier icon;
    protected int x, y;

    public SkillWidget(Skill skill){
        this.skill = skill;

        var candidate = Identifier.tryParse(skill.getIcon());

         if(candidate != null && Minecraft.getInstance().getResourceManager().getResource(candidate).isPresent()){
            this.icon = candidate;
        }
        else this.icon = DEFAULT_ICON;
    }

    public void render(GuiGraphicsExtractor gui, int x, int y, int mouseX, int mouseY, float partialTick){
        this.x = x;
        this.y = y;

        Font font = Minecraft.getInstance().font;
        boolean isLocked = !skill.isAvailable(ClientCache.getCurrentContext(skill.getID()));

        int currentU = ClientCache.getSkillLevel(skill.getID().toLowerCase()) >= skill.getMaximumLevel() ? U_BACKGROUND_FINAL : U_BACKGROUND;

        if(isLocked){
            drawWithShade(gui, () -> {
                gui.blit(RenderPipelines.GUI_TEXTURED, REF_TEXTURE, x, y, currentU, V_BACKGROUND, WIDTH, HEIGHT, TEXTURE_W, TEXTURE_H, shade);
                gui.blit(RenderPipelines.GUI_TEXTURED, REF_TEXTURE, x+49, y+17, U_BOOK_NEUTRAL, V_BOOK_ACTIVE, 9, 9, TEXTURE_W, TEXTURE_H, shade);
                gui.blit(RenderPipelines.GUI_TEXTURED, icon, x+7, y+7, 0, 0, 16, 16, 16, 16, shade);
            });
        }
        else {
            gui.blit(RenderPipelines.GUI_TEXTURED, REF_TEXTURE, x, y, currentU, V_BACKGROUND, WIDTH, HEIGHT, TEXTURE_W, TEXTURE_H, shade);
            gui.blit(RenderPipelines.GUI_TEXTURED, REF_TEXTURE, x+49, y+17, U_BOOK_NEUTRAL, V_BOOK_ACTIVE, 9, 9, TEXTURE_W, TEXTURE_H, shade);
            gui.blit(RenderPipelines.GUI_TEXTURED, icon, x+7, y+7, 0, 0, 16, 16, 16, 16, shade);
        }


        MutableComponent text = !isLocked ?
                Component.translatable("gui.tyzs_skills.Lvl")
                .append(": " + ClientCache.getSkillLevel(skill.getID()) + "/" + skill.getMaximumLevel())
                :
                Component.translatable("gui.tyzs_skills.locked");



        float scale = 0.58f;
        int fixedWidth = (int)(30 / scale);

        int textHeight = font.wordWrapHeight(text, fixedWidth);
        int padding = 3;

        gui.pose().pushMatrix();
        gui.pose().translate(x+29, y+8);
        gui.pose().scale(scale, scale);

        if(isLocked){
            drawWithShade(gui, () -> {
                renderBackdrop(gui, -padding, -padding, fixedWidth + (padding*2), textHeight + (padding*2), COLOR_BG);
                gui.textWithWordWrap(font, text, 0, 1, fixedWidth, ARGB.multiply(0xFFFFFFFF, shade));
            });
        }
        else{
            renderBackdrop(gui, -padding, -padding, fixedWidth + (padding*2), textHeight + (padding*2), COLOR_BG);
            gui.textWithWordWrap(font, text, 0, 1, fixedWidth, ARGB.multiply(0xFFFFFFFF, shade));
        }


        gui.pose().popMatrix();

        boolean isHoveringBookBtn = isMouseOver(mouseX, mouseY, x+49, y+17, BTN_W, BTN_H);
        if(isHoveringBookBtn){
            if(isLocked) drawWithShade(gui, () -> gui.blit(RenderPipelines.GUI_TEXTURED, REF_TEXTURE, x+48, y+16, U_BOOK_BTN_HOVER, V_BOOK_BTN_HOVER, 11, 11, TEXTURE_W, TEXTURE_H, shade));
            else gui.blit(RenderPipelines.GUI_TEXTURED, REF_TEXTURE, x+48, y+16, U_BOOK_BTN_HOVER, V_BOOK_BTN_HOVER, 11, 11, TEXTURE_W, TEXTURE_H, shade);
        }

       if(ClientCache.isSkillBookmarked(skill.getID().toLowerCase())){
           if(isLocked) drawWithShade(gui, () -> gui.blit(RenderPipelines.GUI_TEXTURED, REF_TEXTURE, x+49, y+17, U_BOOK_ACTIVE, V_BOOK_ACTIVE, BTN_W, BTN_H, TEXTURE_W, TEXTURE_H, shade));
           else gui.blit(RenderPipelines.GUI_TEXTURED, REF_TEXTURE, x+49, y+17, U_BOOK_ACTIVE, V_BOOK_ACTIVE, BTN_W, BTN_H, TEXTURE_W, TEXTURE_H, shade);
        }


       if(canAffordPurchase()){
           boolean isHoverBuyBtn = isMouseOver(mouseX, mouseY, x+38, y+17, BTN_W, BTN_H);

           int currentBuyU;

           if(isShiftPressed() && isMouseOver(mouseX, mouseY, x + 38, y + 17, BTN_W, BTN_H)) currentBuyU = isHoverBuyBtn ? U_BUY_ALL_BTN_HOVER : U_BUY_ALL_BTN;
           else currentBuyU = isHoverBuyBtn ? U_BUY_BTN_HOVER : U_BUY_BTN;

           if(isLocked) drawWithShade(gui, () -> gui.blit(RenderPipelines.GUI_TEXTURED, REF_TEXTURE, x+38, y+17, currentBuyU, V_BUY_BTN, BTN_W, BTN_H, TEXTURE_W, TEXTURE_H, shade));
           else gui.blit(RenderPipelines.GUI_TEXTURED, REF_TEXTURE, x+38, y+17, currentBuyU, V_BUY_BTN, BTN_W, BTN_H, TEXTURE_W, TEXTURE_H, shade);
       }
       if(canAffordRefund()) {
           boolean isHoverRefundBtn = isMouseOver(mouseX, mouseY, x+27, y+17, BTN_W, BTN_H);

           int currentRefundU;
           if(isShiftPressed() && isMouseOver(mouseX, mouseY, x + 27, y + 17, BTN_W, BTN_H)) currentRefundU = isHoverRefundBtn ? U_REFUND_ALL_BTN_HOVER : U_REFUND_ALL_BTN;
           else currentRefundU = isHoverRefundBtn? U_REFUND_BTN_HOVER : U_REFUND_BTN;

           if(isLocked) drawWithShade(gui, () -> gui.blit(RenderPipelines.GUI_TEXTURED, REF_TEXTURE, x+27, y+17, currentRefundU, V_REFUND_BTN, BTN_W, BTN_H, TEXTURE_W, TEXTURE_H, shade));
           else gui.blit(RenderPipelines.GUI_TEXTURED, REF_TEXTURE, x+27, y+17, currentRefundU, V_REFUND_BTN, BTN_W, BTN_H, TEXTURE_W, TEXTURE_H, shade);
       }
    }



    public List<Either<FormattedText, TooltipComponent>> getTooltip(int mouseX, int mouseY){
        List<Either<FormattedText, TooltipComponent>> tooltip = new ArrayList<>();
        int currentLevel = ClientCache.getSkillLevel(this.skill.getID().toLowerCase());

        boolean isLocked = !skill.isAvailable(ClientCache.getCurrentContext(skill.getID()));

        if(isMouseOver(mouseX, mouseY, x+4, y+4, 22, 22)) {
            tooltip.add(Either.right(new SkillTooltipData(this.skill)));
            return tooltip;
        }


        if(isMouseOver(mouseX, mouseY, x+27, y+17, BTN_W, BTN_H) && canRefund() && !isLocked) {
            if(isShiftPressed()) {
                for (var line : StringTools.getTooltipAction(skill, currentLevel, Enums.TooltipType.REFUND, false, true))
                    tooltip.add(Either.left(line));
            }
            else {
                for(var line : StringTools.getTooltipAction(skill, currentLevel, Enums.TooltipType.REFUND, false))
                    tooltip.add(Either.left(line));
            }
            return tooltip;
        }

        if(isMouseOver(mouseX, mouseY, x+38, y+17, BTN_W, BTN_H) && canBuy() && !isLocked){ //BUY
            if(isShiftPressed()){
                for (var line : StringTools.getTooltipAction(skill, currentLevel, Enums.TooltipType.PURCHASE, canAffordPurchase(), true))
                    tooltip.add(Either.left(line));
            }
            else {
                for(var line : StringTools.getTooltipAction(skill, currentLevel, Enums.TooltipType.PURCHASE, canAffordPurchase()))
                    tooltip.add(Either.left(line));
            }
            return tooltip;
        }
        return tooltip;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return isMouseOver(mouseX, mouseY, x, y, WIDTH, HEIGHT);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button){
        if(isMouseOver((int)mouseX, (int)mouseY, x+38, y+17, BTN_W, BTN_H)){
            if(!canAffordPurchase()) return false;

            SoundPlayer.PlayUIClick();

            if(isShiftPressed()) ClientCache.predictBuyMax(skill);
            else ClientCache.predictBuy(skill);

            var actionTask = isShiftPressed() ? 3 : 0;
            ClientPacketDistributor.sendToServer(new CActionSkillPayload(skill.getID().toLowerCase(), actionTask));

            return true;
        }

        if(isMouseOver((int)mouseX, (int)mouseY, x+27, y+17, BTN_W, BTN_H)){
            if(!canAffordRefund()) return false;

            SoundPlayer.PlayUIClick();

            if(isShiftPressed()) ClientCache.predictRefundMax(skill);
            else ClientCache.predictRefund(skill);

            var actionTask = isShiftPressed() ? 4 : 1;
            ClientPacketDistributor.sendToServer(new CActionSkillPayload(skill.getID().toLowerCase(), actionTask));

            return true;
        }

        if(isMouseOver((int)mouseX, (int)mouseY, x+49, y+17, BTN_W, BTN_H)) {
            SoundPlayer.PlayUIClick();
            ClientCache.predictBookmark(skill);
            ClientPacketDistributor.sendToServer(new CActionSkillPayload(skill.getID().toLowerCase(), 2));

            var mc = Minecraft.getInstance();
            if(mc.screen instanceof MainGUI gui) gui.refreshList();
        }

        return false;
    }


    protected boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    //Utils
    protected static final int COLOR_BG = 0xD5000000;
    protected static final int COLOR_BORDER = 0xFFD6AD55;

    private static final int SHADED = ARGB.colorFromFloat(1.0F, 0.3F, 0.3F, 0.3F);
    private int shade = -1;

    protected void drawWithShade (GuiGraphicsExtractor gui, Runnable blit){
        shade = SHADED;
        blit.run();
        shade = -1;
    }

    private boolean isShiftPressed(){return Minecraft.getInstance().hasShiftDown();}
    protected void renderBackdrop(GuiGraphicsExtractor gui, int x, int y, int width, int height, int color) {
        // Fond
        gui.fill(x, y + 1, x + width, y + height - 1, ARGB.multiply(color, shade));
        gui.fill(x + 1, y, x + width - 1, y + 1, ARGB.multiply(color, shade));
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, ARGB.multiply(color, shade));

        // Bordure
        if(ClientCache.getSkillLevel(skill.getID().toLowerCase()) < skill.getMaximumLevel()) return;
        gui.fill(x + 1, y, x + width - 1, y + 1, COLOR_BORDER); // Haut
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, COLOR_BORDER); // Bas
        gui.fill(x, y + 1, x + 1, y + height - 1, COLOR_BORDER); // Gauche
        gui.fill(x + width - 1, y + 1, x + width, y + height - 1, COLOR_BORDER); // Droite
    }

    protected boolean canAffordPurchase(){
        return skill.canBuy(ClientCache.getCurrentContext(skill.getID()), ClientCache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));
    }

    protected boolean canAffordRefund(){
        return skill.canRefund(ClientCache.getCurrentContext(skill.getID()), ClientCache.getConfigBool(Config.REFUND_SYSTEM_KEY, false));
    }

    protected boolean canBuy(){
        return ClientCache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true) && skill.isPurchasable();
    }

    protected boolean canRefund(){
        return ClientCache.getConfigBool(Config.REFUND_SYSTEM_KEY, true) && skill.isRefundable();
    }
}
