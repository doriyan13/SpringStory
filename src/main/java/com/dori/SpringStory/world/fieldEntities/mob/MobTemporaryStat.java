package com.dori.SpringStory.world.fieldEntities.mob;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.MobTemporaryStatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import static com.dori.SpringStory.enums.MobTemporaryStatType.MCounter;
import static com.dori.SpringStory.enums.MobTemporaryStatType.PCounter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobTemporaryStat {
    private List<BurnedInfo> burnedInfoList = new ArrayList<>();
    private Map<MobTemporaryStatType, TempStatValue> stats = new TreeMap<>(Comparator.comparing(MobTemporaryStatType::getBitPos)); // The Order of the map matter!

    public int[] getMask() {
        int[] maskArr = new int[4];
        // Build the mask array by pos -
        stats.keySet().forEach(stat -> maskArr[stat.getPos()] |= stat.getValue());
        return maskArr;
    }

    public void encode(OutPacket outPacket) {
        int[] maskArr = getMask();
        int pCounter = -1;
        int mCounter = -1;

        for (int i = maskArr.length - 1; i >= 0; i--) {
            outPacket.encodeInt(maskArr[i]);
        }

        for (Map.Entry<MobTemporaryStatType, TempStatValue> mobStat : stats.entrySet()) {
            MobTemporaryStatType statType = mobStat.getKey();
            TempStatValue statData = mobStat.getValue();

            switch (statType) {
                case Burned -> {
                    outPacket.encodeInt(burnedInfoList.size());
                    if (!burnedInfoList.isEmpty()) {
                        burnedInfoList.forEach(burnedInfo -> burnedInfo.encode(outPacket));
                    }
                }
                case Disable -> {
                    outPacket.encodeBool(false); // bInvincible
                    outPacket.encodeBool(false); // bDisable
                }
                default -> {
                    if (statType == PCounter) {
                        pCounter = statData.getN();
                    } else if (statType == MCounter) {
                        mCounter = statData.getN();
                    }
                    outPacket.encodeShort(statData.getN());
                    outPacket.encodeInt(statData.getR());
                    outPacket.encodeShort(statData.getT() / 500);
                }
            }
        }
        if(pCounter != -1){
            outPacket.encodeInt(pCounter);
        }
        if(mCounter != -1){
            outPacket.encodeInt(mCounter);
        }
        if (pCounter != -1 || mCounter != -1){
            outPacket.encodeInt(Math.max(pCounter, mCounter)); //nCounterProb -> TODO: mobStat that maybe need to add to MobData
        }
        stats.clear();
    }
}
