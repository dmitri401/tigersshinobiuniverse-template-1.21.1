package com.dmitri401.tigersshinobiuniverse.player;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public final class ShinobiStats implements INBTSerializable<CompoundTag> {

    private boolean isNinja = false;
    private ShinobiClan clan = ShinobiClan.CLANLESS;

    private int level = 1;
    private int ninjaExperience = 0;

    private int chakra = 100;
    private int maxChakra = 100;

    private int ninjutsu = 1;
    private int taijutsu = 1;
    private int genjutsu = 1;

    private int strength = 1;
    private int agility = 1;
    private int vitality = 1;

    private int statPoints = 5;

    public boolean isNinja() {
        return isNinja;
    }

    public ShinobiClan getClan() {
        return clan;
    }

    public int getLevel() {
        return level;
    }

    public int getNinjaExperience() {
        return ninjaExperience;
    }

    public int getChakra() {
        return chakra;
    }

    public int getMaxChakra() {
        return maxChakra;
    }

    public int getNinjutsu() {
        return ninjutsu;
    }

    public int getTaijutsu() {
        return taijutsu;
    }

    public int getGenjutsu() {
        return genjutsu;
    }

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public int getVitality() {
        return vitality;
    }

    public int getStatPoints() {
        return statPoints;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public void setNinjaExperience(int ninjaExperience) {
        this.ninjaExperience = Math.max(0, ninjaExperience);
    }

    public void setChakra(int chakra) {
        this.chakra = Math.max(
                0,
                Math.min(chakra, maxChakra)
        );
    }

    public void setMaxChakra(int maxChakra) {
        this.maxChakra = Math.max(1, maxChakra);
        this.chakra = Math.min(
                this.chakra,
                this.maxChakra
        );
    }

    public void setNinjutsu(int ninjutsu) {
        this.ninjutsu = Math.max(1, ninjutsu);
    }

    public void setTaijutsu(int taijutsu) {
        this.taijutsu = Math.max(1, taijutsu);
    }

    public void setGenjutsu(int genjutsu) {
        this.genjutsu = Math.max(1, genjutsu);
    }

    public void setStrength(int strength) {
        this.strength = Math.max(1, strength);
    }

    public void setAgility(int agility) {
        this.agility = Math.max(1, agility);
    }

    public void setVitality(int vitality) {
        this.vitality = Math.max(1, vitality);
    }

    public void setStatPoints(int statPoints) {
        this.statPoints = Math.max(0, statPoints);
    }

    public boolean completeCharacterCreation(
            ShinobiClan selectedClan
    ) {
        if (isNinja || selectedClan == null) {
            return false;
        }

        this.clan = selectedClan;
        this.isNinja = true;

        applyStartingClanBonuses(selectedClan);

        return true;
    }

    private void applyStartingClanBonuses(
            ShinobiClan selectedClan
    ) {
        switch (selectedClan) {
            case UCHIHA -> {
                ninjutsu += 1;
                genjutsu += 1;
            }

            case HYUGA -> {
                taijutsu += 1;
                chakraControlBonus();
            }

            case UZUMAKI -> {
                maxChakra += 50;
                chakra = maxChakra;
                vitality += 1;
            }

            case CLANLESS -> {
                // No automatic clan bonuses.
            }
        }
    }

    private void chakraControlBonus() {
        /*
         * You do not currently have a chakra-control field
         * in this class. Add the bonus here after creating one.
         */
    }

    public boolean consumeChakra(int amount) {
        if (amount <= 0 || chakra < amount) {
            return false;
        }

        chakra -= amount;
        return true;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(
            HolderLookup.Provider provider
    ) {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("IsNinja", isNinja);
        tag.putInt("ClanId", clan.getId());

        tag.putInt("Level", level);
        tag.putInt(
                "NinjaExperience",
                ninjaExperience
        );

        tag.putInt("Chakra", chakra);
        tag.putInt("MaxChakra", maxChakra);

        tag.putInt("Ninjutsu", ninjutsu);
        tag.putInt("Taijutsu", taijutsu);
        tag.putInt("Genjutsu", genjutsu);

        tag.putInt("Strength", strength);
        tag.putInt("Agility", agility);
        tag.putInt("Vitality", vitality);

        tag.putInt("StatPoints", statPoints);

        return tag;
    }

    @Override
    public void deserializeNBT(
            HolderLookup.Provider provider,
            CompoundTag tag
    ) {
        isNinja = tag.getBoolean("IsNinja");

        clan = ShinobiClan.fromIdOrClanless(
                tag.getInt("ClanId")
        );

        level = Math.max(
                1,
                tag.getInt("Level")
        );

        ninjaExperience = Math.max(
                0,
                tag.getInt("NinjaExperience")
        );

        maxChakra = Math.max(
                1,
                tag.getInt("MaxChakra")
        );

        chakra = Math.max(
                0,
                Math.min(
                        tag.getInt("Chakra"),
                        maxChakra
                )
        );

        ninjutsu = Math.max(
                1,
                tag.getInt("Ninjutsu")
        );

        taijutsu = Math.max(
                1,
                tag.getInt("Taijutsu")
        );

        genjutsu = Math.max(
                1,
                tag.getInt("Genjutsu")
        );

        strength = Math.max(
                1,
                tag.getInt("Strength")
        );

        agility = Math.max(
                1,
                tag.getInt("Agility")
        );

        vitality = Math.max(
                1,
                tag.getInt("Vitality")
        );

        statPoints = Math.max(
                0,
                tag.getInt("StatPoints")
        );
    }
}