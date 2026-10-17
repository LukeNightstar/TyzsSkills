package com.tyzsskills.impl.server.events;

import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.util.ProblemReporter;
import net.minecraft.server.permissions.Permissions;
import com.tyzsskills.impl.server.active.*;
import com.tyzsskills.impl.server.attachments.BlockMarker;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.effects.GenericEffects;
import com.tyzsskills.impl.server.xp.XpGainRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RuntimeEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(!(event.getEntity() instanceof ServerPlayer player)) return;

        CompatibilityManager.processMigration(player);
        CompatibilityManager.processMigrationV2(player);
        CompatibilityManager.processMigrationV3(player);

        ClientSynchronizer.syncMainData(player);
        ClientSynchronizer.syncSkillList(player);
        ClientSynchronizer.syncSkillLevels(player);
        ClientSynchronizer.syncConfig(player);
        ClientSynchronizer.syncSkillBookmarks(player);
        ClientSynchronizer.syncStats(player);
        ClientSynchronizer.syncCategories(player);

        DebugManager.checkForInconsistencies(player);

        GenericEffects.restoreEffects(player);

        if (player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) && ErrorManager.hasErrors()) {
            ErrorManager.printErrors(player);
        }

    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event){

        if(!(event.getOriginal() instanceof ServerPlayer oldPlayer) ||
                !(event.getEntity() instanceof ServerPlayer newPlayer)) return;

        var oldData = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, oldPlayer.registryAccess());
        oldPlayer.getData(PlayerData.DATA).serialize(oldData);
        newPlayer.getData(PlayerData.DATA).deserialize(TagValueInput.create(ProblemReporter.DISCARDING, newPlayer.registryAccess(), oldData.buildResult()));

        if(event.isWasDeath()) {
            DeathPenalties.applySpPenalty(newPlayer);
            DeathPenalties.applyLvlPenalty(newPlayer);
            DeathPenalties.applyXpPenalty(newPlayer);
            DeathPenalties.applySkillPenalty(newPlayer);
        }

        GenericEffects.restoreEffects(newPlayer);
        SkillEffectsEvents.onPlayerClone(event);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event){
        if(event.isCanceled()) return;
        if(!(event.getEntity() instanceof ServerPlayer player)) return;

        if(event.getState().getBlock() instanceof CropBlock ||
                event.getState().getBlock() instanceof NetherWartBlock) return;

        if(XpGainRegistry.getBlockValue(event.getState() , player) > 0
        || event.getState().is(BlockTags.LEAVES) || event.getState().is(BlockTags.LOGS) || event.getState().is(Tags.Blocks.ORES)){
            BlockMarker.MarkBlock((net.minecraft.world.level.Level)event.getLevel(), event.getPos());
        }
    }


}
