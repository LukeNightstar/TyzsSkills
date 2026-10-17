package com.tyzsskills.impl.server.attachments;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import com.tyzsskills.Tyzsskills;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class BlockMarker implements ValueIOSerializable {

    private final Set<Long> placedBLocks = new HashSet<>();

    public void addBlock(BlockPos pos){placedBLocks.add(pos.asLong());}
    public void removeBlock(BlockPos pos){placedBLocks.remove(pos.asLong());}

    public boolean isPlacedByPlayer(BlockPos pos){return placedBLocks.contains(pos.asLong());}

    @Override
    public void serialize(ValueOutput output) {
        var list = output.list("blocks", Codec.LONG);
        for(var pos : placedBLocks){list.add(pos);}
    }

    @Override
    public void deserialize(ValueInput input) {
        placedBLocks.clear();
        input.listOrEmpty("blocks", Codec.LONG).forEach(placedBLocks::add);
    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);

    public static final Supplier<AttachmentType<BlockMarker>> PLACED_BLOCKS = ATTACHMENT_TYPES.register(
            "placed_blocks", () -> AttachmentType.serializable(BlockMarker::new).build()
    );

    public static void MarkBlock(Level level, BlockPos pos){
        if(level.isClientSide()) return;
        LevelChunk chunk = level.getChunkAt(pos);
        chunk.getData(PLACED_BLOCKS).addBlock(pos);
        chunk.markUnsaved();
    }

    public static void RemoveBlock(Level level, BlockPos pos){
        if(level.isClientSide()) return;
        LevelChunk chunk = level.getChunkAt(pos);
        chunk.getData(PLACED_BLOCKS).removeBlock(pos);
        chunk.markUnsaved();
    }


    public static boolean IsPlayerPlaced(Level level, BlockPos pos){
        if(level.isClientSide()) return false;
        LevelChunk chunk = level.getChunkAt(pos);
        return chunk.getData(PLACED_BLOCKS).isPlacedByPlayer(pos);
    }
}
