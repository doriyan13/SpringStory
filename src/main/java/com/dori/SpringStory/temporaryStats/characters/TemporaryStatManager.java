package com.dori.SpringStory.temporaryStats.characters;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.EquipBaseStat;
import com.dori.SpringStory.enums.PassiveBuffStat;
import com.dori.SpringStory.enums.Job;
import com.dori.SpringStory.events.EventManager;
import com.dori.SpringStory.events.eventsHandlers.ConsumeChrHpOrMpEvent;
import com.dori.SpringStory.events.eventsHandlers.RegenChrEvent;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.temporaryStats.TempStatValue;
import com.dori.SpringStory.utils.FormulaCalcUtils;
import com.dori.SpringStory.utils.utilEntities.UnsignedInt128BitBlock;
import com.dori.SpringStory.dataHandlers.dataEntities.SkillData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.dori.SpringStory.enums.EventType.CONSUME_CHARACTER_HP;
import static com.dori.SpringStory.enums.EventType.REGEN_CHARACTER;
import static com.dori.SpringStory.enums.SkillStat.time;
import static com.dori.SpringStory.temporaryStats.characters.CharacterTemporaryStat.Pad;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemporaryStatManager {
    // Fields -
    private Map<Integer, Long> skillsExpiration = new ConcurrentHashMap<>();
    private TempStatCollection<CharacterTemporaryStat> additionalStats = new TempStatCollection<>();
    private TempStatCollection<PassiveBuffStat> passiveStats = new TempStatCollection<>();
    private TempStatCollection<EquipBaseStat> equipStats = new TempStatCollection<>();
    private boolean defenseState;
    private boolean defenseAtt;
    private int[] diceInfo = new int[22];
    private int[] diceOption = new int[8];
    // Logger-
    private static final Logger logger = new Logger(TemporaryStatManager.class);

    public void addTempStat(CharacterTemporaryStat cts,
                            int skillID,
                            int value,
                            int durationInSec) {
        additionalStats.addStat(cts, skillID, value);
        skillsExpiration.put(skillID, getExpirationTime(durationInSec));
    }

    public int getCTS(CharacterTemporaryStat stat) {
        return additionalStats.getStat(stat);
    }

    public boolean hasCTS(CharacterTemporaryStat stat) {
        return additionalStats.hasStat(stat);
    }

    public int getPassiveStat(PassiveBuffStat stat) {
        return passiveStats.getStat(stat);
    }

    public Set<CharacterTemporaryStat> markExpiredStat(int skillID) {
        // first remove from all the cts the skill temp stats values  and collect the ones to remove -
        return additionalStats.markAndGetStatsToRemoveById(skillID);
    }

    private long getExpirationTime(SkillData skillData, int slv) {
        int duration = FormulaCalcUtils.calcValueFromFormula(skillData.getSkillStatInfo().get(time), slv);
        return System.currentTimeMillis() + (duration * 1000L);
    }

    private long getExpirationTime(String durationInSecFormula, int slv) {
        int duration = FormulaCalcUtils.calcValueFromFormula(durationInSecFormula, slv);
        return System.currentTimeMillis() + (duration * 1000L);
    }

    private long getExpirationTime(int durationInSec) {
        return System.currentTimeMillis() + (durationInSec * 1000L);
    }

    public boolean isSkillExpired(int skillID) {
        return System.currentTimeMillis() - skillsExpiration.getOrDefault(skillID, 0L) >= 0;
    }

    public Long getSkillExpirationTimeInSec(int skillID) {
        return (skillsExpiration.getOrDefault(skillID, 0L) - System.currentTimeMillis()) / 1000;
    }

    public void validateStats() {
        if (!skillsExpiration.isEmpty()) {
            skillsExpiration.forEach((skillID, expirationTime) -> {
                boolean expired = System.currentTimeMillis() - expirationTime >= 0;
                if (expired) {
                    markExpiredStat(skillID);
                }
            });
        }
    }

    public void cleanDeletedStats() {
        skillsExpiration.entrySet().removeIf(entry -> {
            boolean expired = System.currentTimeMillis() - entry.getValue() >= 0;
            if (expired) {
                // clean the empty stats from the map -
                additionalStats.removeStatById(entry.getKey());
                return true;
            }
            return false;
        });
    }

    private void handleSpecialBuffEffect(@NotNull MapleChar chr,
                                         @NotNull BuffData buffData,
                                         int value,
                                         int slv) {
        int duration = FormulaCalcUtils.calcValueFromFormula(buffData.getDurationInSecFormula(), slv);
        switch (buffData.getTempStat()) {
            case Regen ->
                    EventManager.addEvent(chr.getId(), REGEN_CHARACTER, new RegenChrEvent(chr, value, buffData.isHealth(), buffData.getIntervalInSec(), (duration / buffData.getIntervalInSec())), buffData.getIntervalInSec());
            case DragonBlood ->
                    EventManager.addEvent(chr.getId(), CONSUME_CHARACTER_HP, new ConsumeChrHpOrMpEvent(chr, value, true, buffData.getIntervalInSec(), (duration / buffData.getIntervalInSec())), buffData.getIntervalInSec());
        }
    }

    private void handleCustomBuff(@NotNull MapleChar chr,
                                  @NotNull SkillData skillData,
                                  @NotNull BuffData buffData,
                                  int slv,
                                  int throwingStarItemID) {
        int value = throwingStarItemID != 0 ? (throwingStarItemID % 10_000 + 1) : FormulaCalcUtils.calcValueFromFormula(buffData.getCalcFormula(), slv);
        if (value != 0) {
            additionalStats.addStat(buffData.getTempStat(), skillData.getSkillId(), value);
            skillsExpiration.put(skillData.getSkillId(), getExpirationTime(buffData.getDurationInSecFormula(), slv));
            handleSpecialBuffEffect(chr, buffData, value, slv);
        }
    }

    public boolean attemptHandleCustomSkillsByID(@NotNull MapleChar chr,
                                                 @NotNull SkillData skillData,
                                                 int slv,
                                                 int throwingStarItemID) {
        Job job = Job.getJobById(skillData.getRootId());
        if (job != null) {
            Set<BuffData> buffs = BuffDataHandler.getBuffsByJobAndSkillID(job, skillData.getSkillId());
            if (buffs != null) {
                buffs.forEach(buffData -> handleCustomBuff(chr, skillData, buffData, slv, throwingStarItemID));
                return true;
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
            ctsToAdd.forEach((stat, value) -> additionalStats.addStat(stat, skillData.getSkillId(), value));
            skillsExpiration.put(skillData.getSkillId(), getExpirationTime(skillData, slv));
            return true;
        }
        return false;
    }

    public boolean hasMovementEffectingStat() {
        return additionalStats
                .getStats()
                .keySet()
                .stream()
                .anyMatch(CharacterTemporaryStat::isMovingEffectingStat);
    }

    private boolean containsSwallow() {
        return additionalStats.hasStat(CharacterTemporaryStat.SwallowAttackDamage)
                || additionalStats.hasStat(CharacterTemporaryStat.SwallowCritical)
                || additionalStats.hasStat(CharacterTemporaryStat.SwallowMaxMP)
                || additionalStats.hasStat(CharacterTemporaryStat.SwallowDefence)
                || additionalStats.hasStat(CharacterTemporaryStat.SwallowEvasion);
    }

    public void encodeMask(OutPacket outPacket, boolean reset) {
        encodeMask(outPacket, reset, false);
    }

    public void encodeMask(OutPacket outPacket, boolean reset, boolean remote) {
        UnsignedInt128BitBlock bits = new UnsignedInt128BitBlock();
        // Turn on the used stats bits -
        additionalStats.getStats().forEach((cts, statData) -> {
            if ((reset && statData.isDeleted()) || statData.isModified() || remote) {
                bits.setBit(cts.getBitPos());
            }
        });
        int[] intBlocks = bits.getArrayLE();
        for (int intBlock : intBlocks) {
            outPacket.encodeInt(intBlock);
        }
    }

    private TempStatValue getTempStatValues(TempStatData statData) {
        TempStatValue tempStatValue = new TempStatValue();
        Optional<Map.Entry<Integer, Integer>> firstSkillData = statData
                .getDataDistribution()
                .entrySet()
                .stream()
                .findFirst();
        if (firstSkillData.isPresent()) {
            tempStatValue.setValue(statData.getTotal());

            int skillID = firstSkillData.get().getKey(); // need the SkillID
            tempStatValue.setReason(skillID); // SkillID | itemID
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
        TempStatData statData = additionalStats.getStatData(stat);
        if (statData != null && statData.isModified()) {
            encodeTempStatValue(getTempStatValues(statData), outPacket, twoStateOrder);
        }
    }

    public void encodeForLocal(OutPacket outPacket) {
        encodeMask(outPacket, false);
        CharacterTemporaryStat.getEncodingLocalStats().forEach(stat -> encodeAdditionalStat(stat, outPacket, false));

        outPacket.encodeBool(isDefenseAtt()); // bDefenseAtt
        outPacket.encodeBool(isDefenseState()); // bDefenseState

        if (containsSwallow()) {
            outPacket.encodeByte(0); // tSwallowBuffTime | it's a byte so i guess max of 255?
        }
        if (additionalStats.hasStat(CharacterTemporaryStat.Dice)) {
            for (int i = 0; i < getDiceInfo().length; i++) {
                outPacket.encodeInt(getDiceInfo()[i]);
            }
        }
        if (additionalStats.hasStat(CharacterTemporaryStat.BlessingArmor)) {
            outPacket.encodeInt(getCTS(Pad)); // nBlessingArmorIncPAD
        }
        CharacterTemporaryStat.getEncodingTwoStateOrderRemote().forEach(stat -> encodeAdditionalStat(stat, outPacket, true));
    }

    private void encodeRemoteStat(CharacterTemporaryStat stat, OutPacket outPacket) {
        TempStatData statData = additionalStats.getStatData(stat);
        if (statData == null) {
            return;
        }
        TempStatValue value = getTempStatValues(statData);
        switch (stat) {
            case Speed, ComboCounter, Cyclone -> outPacket.encodeByte(value.getValue());
            case Morph, Ghost -> outPacket.encodeShort(value.getValue());
            case SpiritJavelin, RespectPImmune, RespectMImmune, DefenseAtt, DefenseState, MagicShield ->
                    outPacket.encodeInt(value.getValue());
            case WeaponCharge, Stun, Darkness, Seal, Weakness, ShadowPartner, Attract, BanMap, DojangShield,
                 ReverseInput, RepeatEffect, StopPortion, StopMotion, Fear, Frozen, SuddenDeath, FinalCut,
                 Mechanic, DarkAura, BlueAura, YellowAura -> outPacket.encodeInt(value.getReason());
            case Poison -> {
                outPacket.encodeShort(value.getValue()); // overwritten with 1
                outPacket.encodeInt(value.getReason());
            }
        }
    }

    public void encodeForRemote(OutPacket outPacket) {
        encodeMask(outPacket, false, true);
        CharacterTemporaryStat.getEncodingRemoteStats()
                .forEach(stat -> encodeRemoteStat(stat, outPacket));
        outPacket.encodeBool(isDefenseAtt()); // bDefenseAtt
        outPacket.encodeBool(isDefenseState()); // bDefenseState
        CharacterTemporaryStat.getEncodingTwoStateOrderRemote()
                .forEach(stat -> encodeAdditionalStat(stat, outPacket, true));
    }
}
