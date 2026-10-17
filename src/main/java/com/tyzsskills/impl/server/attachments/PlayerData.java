package com.tyzsskills.impl.server.attachments;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.model.Cooldown;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.Supplier;

public class PlayerData implements ValueIOSerializable {

    //SKILLS
    private final Map<String, Integer> playerSkills = new HashMap<>();
    private final Map<String, Integer> playerSkillsView = Collections.unmodifiableMap(playerSkills);
    private final List<String> playerBookmarks = new ArrayList<>();
    private final List<String> playerBookmarksView = Collections.unmodifiableList(playerBookmarks);
    private final List<String> playerDeactivations = new ArrayList<>();
    private final List<String> playerDeactivationsView = Collections.unmodifiableList(playerDeactivations);

    //SPELLS
    private final Map<String, Integer> playerSpellsProperties = new HashMap<>();
    private final Set<String> playerSpells = new HashSet<>();
    private final String[] assignedSpells = new String[3];
    private final List<Cooldown> cooldowns = new ArrayList<>(3);

    //METADATA
    private int playerLevel = 1;
    private int playerSP = 0;
    private float playerXP = 0f;

    //COMPAT
    private final Set<String> compatibility_tags = new HashSet<>();


    //-----------------Skill level-----------------
    public void setSkillLevel(String id, int lvl){
        if(id == null || lvl < 0) return;

        if(lvl == 0 && playerSkills.containsKey(id)) playerSkills.remove(id);
        else playerSkills.put(id, lvl);
    }
    public int getSkillLevel(String id){return playerSkills.getOrDefault(id, 0);}
    public @NotNull @UnmodifiableView Map<String, Integer> getOwnedSkill(){return playerSkillsView;}


    //-----------------Properties-----------------
    private String getPropertyId(String spellId, String propertyKey){return spellId + ":" + propertyKey;}
    public void setSpellPropertyLevel(String spellId, String propertyKey, int lvl){
        if(spellId == null || propertyKey == null || lvl < 1) return;

        var finalId = getPropertyId(spellId, propertyKey);

        if(lvl == 1 && playerSpellsProperties.containsKey(finalId)) playerSpellsProperties.remove(finalId);
        else playerSpellsProperties.put(finalId, lvl);
    }

    public void resetPropertyLevels(){
        playerSpellsProperties.clear();
    }

    public int getPropertyLevel(String spellId, String propertyKey){
        if(isSpellPurchased(spellId)) return playerSpellsProperties.getOrDefault(getPropertyId(spellId, propertyKey), 1);
        else return 0;
    }


    //-----------------Spells-----------------
    public void purchaseSpell(String spellId){
        if(spellId == null) return;
        playerSpells.add(spellId);
    }

    public void resetSpellLevels(){
        playerSpells.clear();
    }

    public boolean isSpellPurchased(String spellId){
        if(spellId == null) return false;
        return playerSpells.contains(spellId);
    }

    public void assignSpellToSlot(int slot, @Nullable String spellId){
        if(slot <= 0 ||slot > 3) return;
        assignedSpells[slot - 1] = spellId;
    }

    public @NotNull @UnmodifiableView List<String> getAssignedSpells(){
        return Collections.unmodifiableList(Arrays.asList(assignedSpells));
    }

    public @Nullable String getSpellInSlot(int slot){
        if(slot <= 0 ||slot > 3) return null;
        return assignedSpells[slot - 1];
    }

    public boolean isSpellAssigned(@NotNull String  spellId){
        return getAssignedSpells().contains(spellId);
    }

    //-----------------Cooldowns-----------------
    public boolean setCooldown(@NotNull String spellId, int startTick){
        if(!isSpellAssigned(spellId)) return false;

        for(var cooldown : cooldowns)
            if(cooldown.spellId.equals(spellId)) return false;

        return cooldowns.add(new Cooldown(spellId, startTick));
    }

    public int getCooldown(@NotNull String spellId){
        if(spellId.isEmpty()) return 0;

        for(var cooldown : cooldowns){
            if(cooldown.spellId.equals(spellId)) return cooldown.ticks;
        }
        return 0;
    }

    public void updateCooldowns(){
        if(cooldowns.isEmpty()) return;

        var iterator = cooldowns.iterator();
        while (iterator.hasNext()){
            var cooldown = iterator.next();
            cooldown.ticks--;

            if(cooldown.ticks <= 0) iterator.remove();
        }
    }


    //-----------------Bookmarks-----------------
    public void triggerBookmark(String id){
        if(playerBookmarks.contains(id)) playerBookmarks.remove(id);
        else playerBookmarks.add(id);
    }
    public @NotNull @UnmodifiableView List<String> getBookmarks(){return playerBookmarksView;}
    public boolean isBookmarked(String id){return playerBookmarks.contains(id);}

    //-----------------Deactivations-----------------
    public void triggerDeactivation(String id){
        if(playerDeactivations.contains(id)) playerDeactivations.remove(id);
        else playerDeactivations.add(id);
    }
    public @NotNull @UnmodifiableView List<String> getDeactivatedSkills(){return playerDeactivationsView;}
    public boolean isDeactivated(String id){return playerDeactivations.contains(id);}

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

        var deactivations = output.list("skill_deactivations", Codec.STRING);
        playerDeactivations.forEach(deactivations::add);

        var propertyLevels = output.child("property_levels");
        for(var entry : playerSpellsProperties.entrySet()) propertyLevels.putInt(entry.getKey(), entry.getValue());

        var spells = output.list("spells", Codec.STRING);
        playerSpells.forEach(spells::add);

        var spellSlots = output.list("spell_slots", Codec.STRING);
        for (String s : assignedSpells) spellSlots.add(s == null ? "" : s);

        var cooldownsTag = output.child("active_cooldowns");
        for (var cd : cooldowns) {
            cooldownsTag.putInt(cd.spellId, cd.ticks);
        }

        output.putInt("skill_level", playerLevel);
        output.putInt("skill_point", playerSP);
        output.putFloat("skill_xp", playerXP);

        var compatTags = output.list("compatibility_tags", Codec.STRING);
        compatibility_tags.forEach(compatTags::add);
    }

    @Override
    public void deserialize(ValueInput input) {
        playerBookmarks.clear();
        playerDeactivations.clear();
        playerSkills.clear();
        playerSpellsProperties.clear();
        playerSpells.clear();
        cooldowns.clear();
        Arrays.fill(assignedSpells, null);

        input.child("skill_levels").ifPresent(skillsTag -> {
            for(String key : skillsTag.keySet()) playerSkills.put(key, skillsTag.getIntOr(key, 0));
        });

        input.listOrEmpty("skill_bookmarks", Codec.STRING).forEach(playerBookmarks::add);
        input.listOrEmpty("skill_deactivations", Codec.STRING).forEach(playerDeactivations::add);

        input.child("property_levels").ifPresent(propTag -> {
            for(String key : propTag.keySet()) playerSpellsProperties.put(key, propTag.getIntOr(key, 1));
        });

        input.listOrEmpty("spells", Codec.STRING).forEach(playerSpells::add);

        var slots = input.listOrEmpty("spell_slots", Codec.STRING).stream().limit(3).toList();
        for (int i = 0; i < slots.size(); i++) {
            var spellId = slots.get(i);
            assignedSpells[i] = spellId.isEmpty() ? null : spellId;
        }

        input.child("active_cooldowns").ifPresent(cooldownsTag -> {
            for (String key : cooldownsTag.keySet()) {
                cooldowns.add(new Cooldown(key, cooldownsTag.getIntOr(key, 0)));
            }
        });

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
