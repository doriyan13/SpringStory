package com.dori.SpringStory.client.character.temporaryStats;

import com.dori.SpringStory.client.character.CharacterTemporaryStat;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.world.fieldEntities.mob.TempStatValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemporaryStatManager {
    // Fields -
    private Map<Integer, Long> skillsExpiration = new ConcurrentHashMap<>();
    private Map<CharacterTemporaryStat, TempStatData> additionalStats = new ConcurrentHashMap<>();
    // Logger-
    private static final Logger logger = new Logger(TemporaryStatManager.class);

    public boolean hasMovementEffectingStat() {
        return additionalStats
                .keySet()
                .stream()
                .anyMatch(CharacterTemporaryStat::isMovingEffectingStat);
    }

    private boolean containsSwallow(){
        return additionalStats.containsKey(CharacterTemporaryStat.SwallowAttackDamage) || additionalStats.containsKey(CharacterTemporaryStat.SwallowCritical) ||
                additionalStats.containsKey(CharacterTemporaryStat.SwallowMaxMP) || additionalStats.containsKey(CharacterTemporaryStat.SwallowDefence) ||
                additionalStats.containsKey(CharacterTemporaryStat.SwallowEvasion);
    }

    private void encodeMask(OutPacket outPacket) {
        BitSet bits = new BitSet(128);
        // Turn on the used stats bits -
        additionalStats.keySet().forEach(key -> bits.set(key.getBitPos(), true));
        byte[] bytes = bits.toByteArray();

        for (int i = 3; i >= 0; i--) {
            try {
                outPacket.encodeInt(Math.abs(bytes[i]));
            } catch (Exception e) {
                outPacket.encodeInt(0);
            }
        }
    }

    public void encodeForLocal(OutPacket outPacket) {
        encodeMask(outPacket);
        Arrays.stream(CharacterTemporaryStat.values()).forEach(stat -> {
            TempStatData statData = additionalStats.get(stat);
            TempStatValue tempStatValue = new TempStatValue();
            if(statData != null) {
                Optional<Map.Entry<Integer, Integer>> firstSkillData = statData.getSkillsDataDistribution().entrySet().stream().findFirst();
                if (firstSkillData.isPresent()) {
                    // TODO: verify if there is a better way to do it?
                    tempStatValue.setN(statData.getTotal());
                    int skillID = firstSkillData.get().getValue();
                    tempStatValue.setR(skillID);
                    int timeForBuff = (int) ((skillsExpiration.get(skillID) - System.currentTimeMillis()) / 1000);
                    tempStatValue.setT(timeForBuff);
                }
            }
            outPacket.encodeShort(tempStatValue.getN()); // nOption
            outPacket.encodeInt(tempStatValue.getR()); // rOption
            outPacket.encodeInt(tempStatValue.getT()); // tOption -> how much time to the future in seconds
        });

        outPacket.encodeBool(false); // bDefenseAtt
        outPacket.encodeBool(false); // bDefenseState

        if(containsSwallow()){
            outPacket.encodeByte(0);
        }
        if(additionalStats.containsKey(CharacterTemporaryStat.Dice)){
            for (int i = 0; i < 22; i++) {
                outPacket.encodeInt(0);
            }
        }

        if(additionalStats.containsKey(CharacterTemporaryStat.BlessingArmor)){
            outPacket.encodeInt(0);
        }

        //TODO: encode TwoStateOrderRemote!!!
    }
}
