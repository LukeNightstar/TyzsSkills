package com.tyzsskills.impl.server.attachments;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import com.tyzsskills.Tyzsskills;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.*;
import java.util.function.Supplier;

public class ExplorationProgression implements ValueIOSerializable {

    private final Set<String> visitedBiomes = new HashSet<>();
    private final Set<String> visitedDimensions = new HashSet<>();
    private long lastSleepDay = -1L;

    public void addBiome(String biomeID){
        visitedBiomes.add(biomeID.toLowerCase());
    }

    public boolean hasDiscoveredBiome(String biomeID){
        return visitedBiomes.contains(biomeID.toLowerCase());
    }

    public void addDimension(String dimensionID){
        if(!hasDiscoveredDimension(dimensionID)) visitedDimensions.add(dimensionID.toLowerCase());
    }
    public boolean hasDiscoveredDimension(String dimensionID){
        return visitedDimensions.contains(dimensionID.toLowerCase());
    }

    public boolean hasDiscoveredAnyDimension(){
        return !visitedDimensions.isEmpty();
    }

    public boolean hasAlreadySlept(long currentDay){
        return currentDay <= lastSleepDay;
    }

    public void setSleepDay(long currentDay){
        if(currentDay >= 0L) lastSleepDay = currentDay;
    }

    public void resetPlayerData(){
        visitedBiomes.clear();
        visitedDimensions.clear();
        lastSleepDay = -1L;
    }


    @Override
    public void serialize(ValueOutput output) {
        var biomeList = output.list("biomes", Codec.STRING);
        visitedBiomes.forEach(biomeList::add);

        var dimensionList = output.list("dimensions", Codec.STRING);
        visitedDimensions.forEach(dimensionList::add);

        output.putLong("last_sleep_day", lastSleepDay);
    }

    @Override
    public void deserialize(ValueInput input) {
        visitedBiomes.clear();
        visitedDimensions.clear();

        input.listOrEmpty("biomes", Codec.STRING).forEach(visitedBiomes::add);
        input.listOrEmpty("dimensions", Codec.STRING).forEach(visitedDimensions::add);

        lastSleepDay = input.getLongOr("last_sleep_day", lastSleepDay);
    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);

    public static final Supplier<AttachmentType<ExplorationProgression>> DATA = ATTACHMENT_TYPES.register(
            "exploration_xp_data",
            () -> AttachmentType.serializable(ExplorationProgression::new)
                    .copyOnDeath()
                    .build()
    );
}
