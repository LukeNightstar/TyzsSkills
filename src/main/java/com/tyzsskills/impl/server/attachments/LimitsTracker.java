package com.tyzsskills.impl.server.attachments;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import com.tyzsskills.Tyzsskills;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class LimitsTracker implements ValueIOSerializable {

    private int spLifetime = 0;
    private float xpInCycle = 0f;
    private long lastCycleTimestamp = 0L;

    public void incrSp(int amount){
        if(amount > 0) spLifetime += amount;
    }
    public int getSpLifetime(){return spLifetime;}

    public void incrXp(float amount){
        if(amount > 0f) xpInCycle += amount;
    }
    public float getXpInCycle(){return xpInCycle;}

    public void startNewCycle(long timestamp){
        if(timestamp >= 0L){
            lastCycleTimestamp = timestamp;
            xpInCycle = 0f;
        }
    }
    public long getLastCycleTimestamp(){return lastCycleTimestamp;}


    public void resetLimits(){
        spLifetime = 0;
        xpInCycle = 0f;
        lastCycleTimestamp = 0L;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("sp_lifetime", spLifetime);
        output.putFloat("xp_in_cycle", xpInCycle);
        output.putLong("last_cycle_timestamp", lastCycleTimestamp);
    }

    @Override
    public void deserialize(ValueInput input) {
        spLifetime = input.getIntOr("sp_lifetime", spLifetime);
        xpInCycle = input.getFloatOr("xp_in_cycle", xpInCycle);
        lastCycleTimestamp = input.getLongOr("last_cycle_timestamp", lastCycleTimestamp);
    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);
    public static final Supplier<AttachmentType<LimitsTracker>> DATA = ATTACHMENT_TYPES.register(
            "player_limits", () -> AttachmentType.serializable(LimitsTracker::new).copyOnDeath().build());
}

