package com.dori.SpringStory.dataHandlers.dataEntities;

import com.dori.SpringStory.utils.MapleUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Random;

@Data
@NoArgsConstructor
@AllArgsConstructor

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MobDropData {
    private int mobId;
    private int itemId;
    private int minQ;
    private int maxQ;
    private int questId;
    private double chance;

    public MobDropData(int mobID, int amount) {
        // Meso Drop constructor -
        this.mobId = mobID;
        this.minQ = amount;
        this.maxQ = amount;
        this.chance = 0.6f; // More GMS like rate for meso drop
    }

    public MobDropData(int mobID, int itemID, int amount, double chance) {
        this.mobId = mobID;
        this.itemId = itemID;
        this.minQ = amount;
        this.maxQ = amount;
        this.chance = chance;
    }

    public MobDropData(int mobID, int itemID, int minQ, int maxQ, double chance) {
        this.mobId = mobID;
        this.itemId = itemID;
        this.minQ = minQ;
        this.maxQ = maxQ;
        this.chance = chance;
    }

    public boolean willDrop(float dropRate) {
        float randomValue = new Random().nextFloat();
        return randomValue <= (chance * dropRate);
    }

    @JsonIgnore
    public boolean isMoney() {
        return getItemId() == 0;
    }

    public int getQuantity() {
        if(getMinQ() == getMaxQ()) {
            return getMinQ();
        }
        return MapleUtils.getRandom(getMinQ(), getMaxQ());
    }

    @Override
    public String toString() {
        return "MobDropData: " +
                "mobId: " + mobId +
                " | itemId: " + itemId +
                " | quantity: " + getQuantity() +
                " | chance: " + chance;
    }
}
