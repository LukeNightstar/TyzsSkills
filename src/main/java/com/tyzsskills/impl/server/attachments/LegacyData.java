package com.tyzsskills.impl.server.attachments;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import com.tyzsskills.Tyzsskills;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LegacyData implements ValueIOSerializable {

    private final Map<String, Double> oldVariables = new HashMap<>();

    public double getOldValue(String key){
        return oldVariables.getOrDefault(key, 0.0);
    }

    public boolean hasData(){
        return !oldVariables.isEmpty();
    }


    @Override
    public void serialize(ValueOutput output) {
    }

    @Override
    public void deserialize(ValueInput input) {
        for(String key : input.keySet()){
            try{
                double value = input.getDoubleOr(key, 0.0);
                oldVariables.put(key, value);
            }
            catch (Exception ignored){
            }
        }
    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);

    public static final Supplier<AttachmentType<LegacyData>> PLAYER_VARIABLES = ATTACHMENT_TYPES.register(
            "player_variables", () -> AttachmentType.serializable(LegacyData::new).build()
    );
}
