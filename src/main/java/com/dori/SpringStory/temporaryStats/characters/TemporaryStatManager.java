package com.dori.SpringStory.temporaryStats.characters;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.Job;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.temporaryStats.TempStatValue;
import com.dori.SpringStory.utils.FormulaCalcUtils;
import com.dori.SpringStory.wzHandlers.wzEntities.SkillData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.dori.SpringStory.enums.SkillStat.time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemporaryStatManager {
    // Fields -
    private Map<Integer, Long> skillsExpiration = new ConcurrentHashMap<>();
    private Map<CharacterTemporaryStat, TempStatData> additionalStats = new ConcurrentHashMap<>();
    private boolean defenseState;
    private boolean defenseAtt;
    private int[] diceInfo = new int[22];
    private int[] diceOption = new int[8];
    // Logger-
    private static final Logger logger = new Logger(TemporaryStatManager.class);

    public void addStat(CharacterTemporaryStat cts, int skillID, int value) {
        if (!additionalStats.containsKey(cts)) {
            additionalStats.put(cts, new TempStatData());
        }
        additionalStats.get(cts).addSkillStats(skillID, value);
    }

    public void markExpiredStat(int skillID) {
        // first remove from all the cts the skill temp stats values -
        additionalStats.values().forEach(statData -> {
            statData.removeSkillStats(skillID);
            if (statData.getSkillsDataDistribution().isEmpty()) {
                statData.setDeleted(true);
            }
        });
    }

    public void removeSkillStats(int skillID) {
        // clean the empty stats from the map -
        additionalStats.values().removeIf(statData -> statData.getSkillsDataDistribution().isEmpty());
    }

    private long getExpirationTime(SkillData skillData, int slv) {
        int duration = FormulaCalcUtils.calcValueFromFormula(skillData.getSkillStatInfo().get(time), slv);
        return System.currentTimeMillis() + (duration * 1000L);
    }

    private long getExpirationTime(String durationInSecFormula, int slv) {
        int duration = FormulaCalcUtils.calcValueFromFormula(durationInSecFormula, slv);
        return System.currentTimeMillis() + (duration * 1000L);
    }

    private boolean isSkillExpired(int skillID) {
        return System.currentTimeMillis() - skillsExpiration.getOrDefault(skillID, 0L) >= 0;
    }

    public void validateStats() {
        skillsExpiration.forEach((skillID,expirationTime)->{
            boolean expired = System.currentTimeMillis() - expirationTime >= 0;
            if (expired) {
                markExpiredStat(skillID);
            }
        });
        //skillsExpiration.keySet().forEach(this::markExpiredStat);
    }

    public void cleanDeletedStats() {
        skillsExpiration.entrySet().removeIf(entry -> {
            boolean expired = System.currentTimeMillis() - entry.getValue() >= 0;
            if (expired) {
                removeSkillStats(entry.getKey());
                return true;
            }
            return false;
        });
    }

    public void applyModifiedStats() {
        additionalStats.values().forEach(statData -> statData.setModified(false));
    }

    public boolean handleCustomSkillsByID(int jobID, int skillID, int slv){
        Job job = Job.getJobById(jobID);
        if(job != null){
            BuffData buffData = BuffDataHandler.getBuffByJobAndSkillID(job, skillID);
            if(buffData != null) {
                int value = FormulaCalcUtils.calcValueFromFormula(buffData.getCalcFormula(), slv);
                if(buffData.isAdditionalValue()){
                    // TODO: need to redo the handling of chr stats! to be able to do generic handling
                }
                if(value != 0) {
                    addStat(buffData.getTempStat(), skillID, value);
                    skillsExpiration.put(skillID, getExpirationTime(buffData.getDurationInSecFormula(), slv));
                    return true;
                }
            }
        }
        return false;
    }

    public boolean attemptToAutoHandleSkillByID(SkillData skillData, int slv) {
        Map<CharacterTemporaryStat, Integer> ctsToAdd = new HashMap<>();
        skillData.getSkillStatInfo().forEach((skillStat, formula) -> {
            CharacterTemporaryStat cts = CharacterTemporaryStat.getCtsFromSkillStat(skillStat);
            if (cts != null) {
                int value = FormulaCalcUtils.calcValueFromFormula(formula, slv);
                if (value != 0) {
                    ctsToAdd.put(cts, value);
                }
            }
        });
        if (!ctsToAdd.isEmpty()) {
            ctsToAdd.forEach((stat, value) -> addStat(stat, skillData.getSkillId(), value));
            skillsExpiration.put(skillData.getSkillId(), getExpirationTime(skillData, slv));
            return true;
        }
        return false;
    }

    public boolean hasMovementEffectingStat() {
        return additionalStats
                .keySet()
                .stream()
                .anyMatch(CharacterTemporaryStat::isMovingEffectingStat);
    }

    private boolean containsSwallow() {
        return additionalStats.containsKey(CharacterTemporaryStat.SwallowAttackDamage)
                || additionalStats.containsKey(CharacterTemporaryStat.SwallowCritical)
                || additionalStats.containsKey(CharacterTemporaryStat.SwallowMaxMP)
                || additionalStats.containsKey(CharacterTemporaryStat.SwallowDefence)
                || additionalStats.containsKey(CharacterTemporaryStat.SwallowEvasion);
    }

    public void encodeMask(OutPacket outPacket, boolean reset) {
        BitSet bits = new BitSet(128);
        // Turn on the used stats bits -
        additionalStats.forEach((cts, statData) -> {
            if ((reset && statData.isDeleted()) || statData.isModified()) {
                bits.set(cts.getBitPos(), true);
            }
        });
        //additionalStats.keySet().forEach(key -> bits.set(key.getBitPos(), true));
        byte[] bytes = bits.toByteArray();

        for (int i = 3; i >= 0; i--) {
            try {
                outPacket.encodeInt(Math.abs(bytes[i]));
            } catch (Exception e) {
                outPacket.encodeInt(0);
            }
        }
    }

    private TempStatValue getTempStatValues(TempStatData statData) {
        TempStatValue tempStatValue = new TempStatValue();
        Optional<Map.Entry<Integer, Integer>> firstSkillData = statData
                .getSkillsDataDistribution()
                .entrySet()
                .stream()
                .findFirst();
        if (firstSkillData.isPresent()) {
            tempStatValue.setValue(statData.getTotal());

            int skillID = firstSkillData.get().getKey(); // need the SkillID
            tempStatValue.setReason(skillID); // SkillID | itemID
            //TODO: need to manage the removal and avoid abs very long old time!!
            int timeForBuff = (int) ((skillsExpiration.get(skillID) - System.currentTimeMillis()));
            tempStatValue.setDuration(timeForBuff); // Duration
        }
        return tempStatValue;
    }

    private void encodeTempStatValue(TempStatValue tempStatValue, OutPacket outPacket, boolean twoStateOrder) {
        if (twoStateOrder) {
            outPacket.encodeInt(tempStatValue.getValue()); // nOption
            outPacket.encodeInt(tempStatValue.getReason()); // rOption
            outPacket.encodeBool(tempStatValue.getDuration() < 0); // is expired duration
            outPacket.encodeInt(Math.abs(tempStatValue.getDuration())); // the duration itself (they do abs also) | swordie and chronos put MAX_INT if it's true?
        } else {
            outPacket.encodeShort(tempStatValue.getValue()); // nOption
            outPacket.encodeInt(tempStatValue.getReason()); // rOption
            outPacket.encodeInt(tempStatValue.getDuration()); // tOption -> how much time to the future in seconds
        }
    }

    private void encodeAdditionalStat(CharacterTemporaryStat stat, OutPacket outPacket, boolean twoStateOrder) {
        TempStatData statData = additionalStats.get(stat);
        if (statData != null && statData.isModified()) {
            encodeTempStatValue(getTempStatValues(statData), outPacket, twoStateOrder);
        }
    }

    public void encodeForLocal(OutPacket outPacket) {
        encodeMask(outPacket, false);
        Arrays.stream(CharacterTemporaryStat.values()).forEach(stat -> encodeAdditionalStat(stat, outPacket, false));

        outPacket.encodeBool(isDefenseAtt()); // bDefenseAtt
        outPacket.encodeBool(isDefenseState()); // bDefenseState

        if (containsSwallow()) {
            outPacket.encodeByte(0); // tSwallowBuffTime | it's a byte so i guess max of 255?
        }
        if (additionalStats.containsKey(CharacterTemporaryStat.Dice)) {
            for (int i = 0; i < getDiceInfo().length; i++) {
                outPacket.encodeInt(getDiceInfo()[i]);
            }
        }
        if (additionalStats.containsKey(CharacterTemporaryStat.BlessingArmor)) {
            outPacket.encodeInt(0); // nBlessingArmorIncPAD
        }
        CharacterTemporaryStat.getEncodingTwoStateOrderRemote().forEach(stat -> encodeAdditionalStat(stat, outPacket, true));
    }
}
