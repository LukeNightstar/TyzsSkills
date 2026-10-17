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

public class PlayerData implements ValueIOSerializable {

    private final Map<String, Integer> playerSkills = new HashMap<>();
    private final Set<String> playerBookmarks = new HashSet<>();
    private int playerLevel = 1;
    private int playerSP = 0;
    private float playerXP = 0f;

    private final Set<String> compatibility_tags = new HashSet<>();


    //-----------------Skill level-----------------
    public void setSkillLevel(String id, int lvl){
        if(id == null || lvl < 0) return;

        if(lvl == 0 && playerSkills.containsKey(id)) playerSkills.remove(id);
        else playerSkills.put(id, lvl);
    }
    public int getSkillLevel(String id){return playerSkills.getOrDefault(id, 0);}
    public List<String> getOwnedSkillIds(){return List.copyOf(playerSkills.keySet());}


    //-----------------Bookmarks-----------------
    public void triggerBookmark(String id){
        if(playerBookmarks.contains(id)) playerBookmarks.remove(id);
        else playerBookmarks.add(id);
    }
    public List<String> getBookmarks(){return List.copyOf(playerBookmarks);}
    public boolean isBookmarked(String id){return playerBookmarks.contains(id);}


    //-----------------Level-----------------
    public void setLevel(int lvl){playerLevel = Math.max(1, lvl);}
    public int getLevel(){return playerLevel;}


    //-----------------SP-----------------
    public void setSP(int sp){playerSP = Math.max(0, sp);}
    public int getSP(){return playerSP;}


    //-----------------XP-----------------
    public void setXP(float xp){playerXP = Math.max(0f, xp);}
    public float getXP(){return playerXP;}


    //-----------------COMPATIBILITY-----------------
    public void putTag(String tag){if(tag != null) compatibility_tags.add(tag);}
    public boolean hasMigrated(String tag){return tag != null && compatibility_tags.contains(tag);}


    @Override
    public void serialize(ValueOutput output) {
        var skillLevels = output.child("skill_levels");
        for(var entry : playerSkills.entrySet()) skillLevels.putInt(entry.getKey(), entry.getValue());

        var bookmarks = output.list("skill_bookmarks", Codec.STRING);
        playerBookmarks.forEach(bookmarks::add);

        output.putInt("skill_level", playerLevel);
        output.putInt("skill_point", playerSP);
        output.putFloat("skill_xp", playerXP);

        var compatTags = output.list("compatibility_tags", Codec.STRING);
        compatibility_tags.forEach(compatTags::add);
    }

    @Override
    public void deserialize(ValueInput input) {
        playerBookmarks.clear();
        playerSkills.clear();

        input.child("skill_levels").ifPresent(skillsTag -> {
            for(String key : skillsTag.keySet()) playerSkills.put(key, skillsTag.getIntOr(key, 0));
        });

        input.listOrEmpty("skill_bookmarks", Codec.STRING).forEach(playerBookmarks::add);

        playerLevel = input.getIntOr("skill_level", playerLevel);
        playerSP = input.getIntOr("skill_point", playerSP);
        playerXP = input.getFloatOr("skill_xp", playerXP);

        input.listOrEmpty("compatibility_tags", Codec.STRING).forEach(compatibility_tags::add);
    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);

    public static final Supplier<AttachmentType<PlayerData>> DATA = ATTACHMENT_TYPES.register(
            "player_skill_data",
            () -> AttachmentType.serializable(PlayerData::new).build());
}
