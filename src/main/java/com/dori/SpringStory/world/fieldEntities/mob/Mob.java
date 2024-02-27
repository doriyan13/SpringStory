package com.dori.SpringStory.world.fieldEntities.mob;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.packets.CMobPool;
import com.dori.SpringStory.constants.GameConstants;
import com.dori.SpringStory.dataHandlers.MobDropHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.MobDropData;
import com.dori.SpringStory.enums.MobControllerType;
import com.dori.SpringStory.enums.MobSummonType;
import com.dori.SpringStory.events.EventManager;
import com.dori.SpringStory.events.eventsHandlers.ReviveMobEvent;
import com.dori.SpringStory.temporaryStats.mobs.MobTemporaryStat;
import com.dori.SpringStory.utils.FieldUtils;
import com.dori.SpringStory.utils.utilEntities.PositionData;
import com.dori.SpringStory.world.fieldEntities.Foothold;
import com.dori.SpringStory.world.fieldEntities.Life;
import com.dori.SpringStory.dataHandlers.dataEntities.MobData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.dori.SpringStory.constants.GameConstants.DEFAULT_MOB_RESPAWN_DELAY;
import static com.dori.SpringStory.enums.EventType.REVIVE_MOB;
import static com.dori.SpringStory.utils.HashUuidCreator.getRandomUuidInLong;

@Data
@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(callSuper = true)
public class Mob extends Life {
    // Fields -
    private long hp;
    private long maxHp;
    private int mp;
    private int maxMp;
    private int level;
    private int exp;
    private short homeFh;
    private MobSummonType appearType;
    private int option;
    private byte teamForMCarnival;
    @JsonIgnore
    private Map<Integer, Long> damageDone = new ConcurrentHashMap<>();
    @JsonIgnore
    private MobData statsData;
    @JsonIgnore
    private boolean respawnable = false;
    @JsonIgnore
    private MobTemporaryStat temporaryStats = new MobTemporaryStat();

    public Mob(int templateId) {
        super(templateId);

        this.homeFh = 0;
        this.appearType = MobSummonType.Normal;
        this.option = 0;
        this.teamForMCarnival = 0;
        this.controller = null;
        this.setMoveAction((byte) 5);
    }

    public Mob(Life life) {
        this.setTemplateId(life.getTemplateId());
        this.setObjectId(life.getObjectId());
        this.setLifeType(life.getLifeType());
        this.setPosition(life.getPosition());
        this.setVPosition(life.getVPosition());
        this.setMobTime(life.getMobTime());
        this.setFlip(life.isFlip());
        this.setHide(life.isHide());
        this.setFh(life.getFh());
        this.setCy(life.getCy());
        this.setRx0(life.getRx0());
        this.setRx1(life.getRx1());
        this.setLimitedName(life.getLimitedName());
        this.setUseDay(life.isUseDay());
        this.setUseNight(life.isUseNight());
        this.setHold(life.isHold());
        this.setNoFoothold(life.isNoFoothold());
        this.setDummy(life.isDummy());
        this.setSpine(life.isSpine());
        this.setMobTimeOnDie(life.isMobTimeOnDie());
        this.setRegenStart(life.getRegenStart());
        this.setMobAliveReq(life.getMobAliveReq());
        // Additional Default mob data -
        this.setMoveAction((byte) 5); // Need to figure out what is it?
        this.homeFh = 0;
        this.appearType = MobSummonType.Normal;
        this.option = 0;
        this.teamForMCarnival = 0;
        this.controller = null;
    }

    public void applyMobData(MobData mobData) {
        this.hp = mobData.getMaxHp();
        this.maxHp = mobData.getMaxHp();
        this.mp = mobData.getMaxMp();
        this.maxMp = mobData.getMaxMp();
        this.level = mobData.getLevel();
        this.exp = mobData.getExp();
        // Link mobData to entity -
        this.statsData = mobData;
    }

    public Mob(MobData mobData) {
        // Super constructor -
        super(mobData.getId());
        // Default Mob data -
        this.homeFh = 0;
        this.appearType = MobSummonType.Normal;
        this.option = 0;
        this.teamForMCarnival = 0;
        this.controller = null;
        this.setMoveAction((byte) 5);
        // Mob Data -
        this.hp = mobData.getMaxHp();
        this.maxHp = mobData.getMaxHp();
        this.mp = mobData.getMaxMp();
        this.maxMp = mobData.getMaxMp();
        this.level = mobData.getLevel();
        this.exp = mobData.getExp();
        // Link mobData to entity -
        this.statsData = mobData;
    }

    public void encode(OutPacket outPacket) {
        //CMob::SetTemporaryStat
        //Temp stats -
        this.getTemporaryStats().encode(outPacket);

        //CMob::Init
        outPacket.encodePosition(getPosition()); //m_ptPosPrev.x | m_ptPosPrev.y
        outPacket.encodeByte(getMoveAction());
        outPacket.encodeShort(getFh()); //  m_nFootholdSN
        outPacket.encodeShort(getHomeFh()); //  m_nHomeFoothold
        outPacket.encodeByte(appearType.getVal());

        if (appearType == MobSummonType.Revived || appearType.getVal() >= 0) {
            outPacket.encodeInt(option); // summon option
        }
        outPacket.encodeByte(getTeamForMCarnival());
        outPacket.encodeInt(0); // nEffectItemID
        outPacket.encodeInt(0); // unk
    }

    public void setController(MapleChar chr, MobControllerType controllerType) {
        // If the mob had an old controller, reset the controller -
        if (getController() != null) {
            // Notify old controller -
            chr.write(CMobPool.mobChangeController(this, MobControllerType.Reset));
        }
        // Set the new Character as the new controller -
        setController(chr);
        // Notify new controller -
        chr.write(CMobPool.mobChangeController(this, controllerType));
    }

    public void registerCharDmg(int chrID, long damage) {
        long cur = 0;
        if (getDamageDone().containsKey(chrID)) {
            cur = getDamageDone().get(chrID);
        }
        cur += Math.min(damage, getHp());
        getDamageDone().put(chrID, cur);
    }

    public int getMostDamageDoneChr() {
        int chrID = 0;
        long mostDmg = 0;
        for (Map.Entry<Integer, Long> player : getDamageDone().entrySet()) {
            if (player.getValue() > mostDmg) {
                chrID = player.getKey();
                mostDmg = player.getValue();
            }
        }
        return chrID;
    }

    public void distributeExp() {
        int exp = getExp();
        long totalDamage = getDamageDone().values().stream().mapToLong(l -> l).sum();
        getField()
                .getPlayers()
                .values()
                .forEach(chr -> {
                    long playerDmg = getDamageDone().get(chr.getId()) != null ? getDamageDone().get(chr.getId()) : 0;
                    double dmgPercentage = playerDmg / (double) totalDamage;
                    int mobExpRate = chr.getLevel() < 10 ? 1 : GameConstants.EXP_RATE;
                    int expForChr = (int) (exp * dmgPercentage * mobExpRate);
                    if (expForChr > 0) {
                        chr.gainExp(expForChr);
                    }
                });
    }

    public void applyDrops() {
        MapleChar mostDmgPlayer = getField().getPlayers().get(getMostDamageDoneChr());
        int fhID = getFh();
        Foothold fhBelow = getField().findFootHoldBelow(getPosition());
        if (fhID == 0) {
            if (fhBelow != null) {
                fhID = fhBelow.getId();
            }
        }
        // DropRate & MesoRate Increases
        float mostDamageCharDropRate = (mostDmgPlayer != null ? GameConstants.DROP_RATE : 0); // mostDmgPlayer.getTotalStat(BaseStat.dropR)
        float mostDamageCharMesoRate = (mostDmgPlayer != null ? GameConstants.MESO_RATE : 0); // getMostDamageChar().getTotalStat(BaseStat.mesoR)
        float dropRateMob = 0; //TODO: (getTemporaryStat().hasCurrentMobStat(MobStat.Treasure) ? getTemporaryStat().getCurrentOptionsByMobStat(MobStat.Treasure).yOption : 0); // Item Drop Rate
        float mesoRateMob = 0; //TODO: (getTemporaryStat().hasCurrentMobStat(MobStat.Treasure) ? getTemporaryStat().getCurrentOptionsByMobStat(MobStat.Treasure).zOption : 0); // Meso Drop Rate
        float totalMesoRate = mesoRateMob + mostDamageCharMesoRate;
        float totalDropRate = dropRateMob + mostDamageCharDropRate;
        // TODO: in the future add handling for cash items that modify the rates also! (drop coupon)
        List<MobDropData> dropsData = MobDropHandler.getDropsByMobID(getTemplateId());
        // TODO: do proper calc for money by lvl of the mob!
        MobDropData moneyByLvl = new MobDropData(getTemplateId(), (getLevel() * 10 * Math.min(Math.round(totalMesoRate), 1)));
        dropsData.add(moneyByLvl);
        if (!getField().isDropsDisabled()) {
            int ownerID = mostDmgPlayer != null ? mostDmgPlayer.getId() : 0;
            getField().drop(dropsData, getObjectId(), ownerID, getField().getFootholdById(fhID), getPosition(), totalMesoRate, totalDropRate);
        }
    }

    public void die(boolean drops) {
        // Kill the Old Mob and broadcast it -
        getField().removeMob(getObjectId());
        // Distribute exp -
        distributeExp();
        if (drops) {
            // Drops -
            applyDrops();
        }
        // Clear the damaged Mob data -
        MapleChar chr = this.getController();
        this.setHp(getMaxHp());
        this.setMp(getMaxMp());
        this.setController(null);
        getDamageDone().clear();
        long delay = getStatsData().getRespawnDelay() > 0 ? getStatsData().getRespawnDelay() : DEFAULT_MOB_RESPAWN_DELAY;
        // update mob position data -
        PositionData posData = FieldUtils.generateRandomPositionFromList(getField().getMobsSpawnPoints());
        if (posData == null) {
            posData = new PositionData(getPosition(), getFh());
        }
        setPosition(posData.getPosition());
        setFh(posData.getFoothold());
        EventManager.addEvent(getRandomUuidInLong(), REVIVE_MOB, new ReviveMobEvent(this, chr), delay);
    }

    // need to synchronize this method to avoid double damage/kill a mob in the same time
    public synchronized void damage(MapleChar chr, long totalDamage) {
        registerCharDmg(chr.getId(), totalDamage);
        long maxHP = getMaxHp();
        long oldHp = getHp();
        long newHp = oldHp - totalDamage;
        setHp(newHp);
        double percentageDamage = ((double) newHp / maxHP);
        getField().broadcastPacket(CMobPool.hpIndicator(getObjectId(), (byte) (percentageDamage > 0 ? (percentageDamage * 100) : 0)));
        if (oldHp > 0 && newHp <= 0) {
            die(true);
        }
    }

    @Override
    public String toString() {
        return "ID: " + getObjectId() +
                " | Hp: " + hp + "/" + maxHp +
                " | Mp: " + mp + "/" + maxMp +
                " | Lvl: " + level +
                " | Exp: " + exp +
                " | Controller: " + controller.getId() +
                " | Pos: " + getPosition();
    }
}
