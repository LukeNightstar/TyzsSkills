package com.tyzsskills.impl.server.attachments;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import com.tyzsskills.Tyzsskills;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class StatsTracker implements ValueIOSerializable {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);
    public static final Supplier<AttachmentType<StatsTracker>> DATA = ATTACHMENT_TYPES.register(
            "player_stats", () -> AttachmentType.serializable(StatsTracker::new).copyOnDeath().build());


    private float allTimeXp = 0f;
    private int totalSpEarned = 0;
    private int totalSpSpent = 0;

    public StatsTracker(){}

    public void addXp(float amount){
        if(amount > 0) this.allTimeXp += amount;
    }

    public void addSpEarned(int amount){
        if(amount > 0) this.totalSpEarned += amount;
    }

    public void addSpSpent(int amount){
        if(amount > 0) this.totalSpSpent+= amount;
    }

    public void resetStats(){
        this.allTimeXp = 0f;
        this.totalSpEarned = 0;
        this.totalSpSpent = 0;
    }

    public void copyFrom(StatsTracker old){
        this.allTimeXp = old.allTimeXp;
        this.totalSpEarned = old.totalSpEarned;
        this.totalSpSpent = old.totalSpSpent;
    }

    //Getters
    public float getAllTimeXp() {return this.allTimeXp;}
    public int getTotalSpEarned() {return this.totalSpEarned;}
    public int getTotalSpSpent() {return this.totalSpSpent;}

    @Override
    public void serialize(ValueOutput output) {
        output.putFloat("allTimeXp", allTimeXp);
        output.putInt("totalSpEarned", totalSpEarned);
        output.putInt("totalSpSpent", totalSpSpent);
    }

    @Override
    public void deserialize(ValueInput input) {
        allTimeXp = input.getFloatOr("allTimeXp", allTimeXp);
        totalSpEarned = input.getIntOr("totalSpEarned", totalSpEarned);
        totalSpSpent = input.getIntOr("totalSpSpent", totalSpSpent);
    }
}
