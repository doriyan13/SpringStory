package com.dori.SpringStory.dataHandlers.dataEntities;

import com.dori.SpringStory.utils.MapleUtils;
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
@ToString

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
        this.mobId = mobID;
        this.minQ = amount;
        this.maxQ = amount;
        this.chance = 1f;
    }

    public boolean willDrop(float dropRate) {
        float randomValue = new Random().nextFloat();
        return randomValue <= (chance * dropRate);
    }

    public boolean isMoney() {
        return getItemId() == 0;
    }

    public int getQuantity() {
        if(getMinQ() == getMaxQ()) {
            return getMinQ();
        }
        return MapleUtils.getRandom(getMinQ(), getMaxQ());
    }
}
