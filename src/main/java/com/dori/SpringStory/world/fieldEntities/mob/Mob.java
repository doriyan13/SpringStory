package com.dori.SpringStory.world.fieldEntities.mob;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.messages.IncEXPMessage;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.packets.CMobPool;
import com.dori.SpringStory.connection.packet.packets.CWvsContext;
import com.dori.SpringStory.constants.GameConstants;
import com.dori.SpringStory.enums.MobControllerType;
import com.dori.SpringStory.enums.MobSummonType;
import com.dori.SpringStory.world.fieldEntities.Life;
import com.dori.SpringStory.wzHandlers.wzEntities.MobData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

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
    private MapleChar controller;
    @JsonIgnore
    private Map<Integer, Long> damageDone = new HashMap<>();
    @JsonIgnore
    private MobData statsData;

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
        //Temp stats - TODO: need to handle it properly!
        outPacket.encodeArr(new byte[16]);

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
        outPacket.encodeInt(0); // this
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

    public void distributeExp(){
        int exp = getExp();
        long totalDamage = getDamageDone().values().stream().mapToLong(l -> l).sum();
        getField().getPlayers().values().forEach(chr ->{
            double dmgPercentage = getDamageDone().get(chr.getId()) / (double) totalDamage;
            int mobExpRate = chr.getLevel() < 10 ? 1 : GameConstants.EXP_RATE;
            int expForChr = (int) (exp * dmgPercentage * mobExpRate);
            chr.gainExp(expForChr);
            // Send the increase exp message -
            IncEXPMessage expMessage = new IncEXPMessage();
            expMessage.setLastHit(true);
            expMessage.setIncEXP(expForChr);
            chr.write(CWvsContext.incExpMessage(expMessage));
        });
    }

    public void die(boolean drops) {
        // Kill the Old Mob -
        getField().removeMob(getObjectId());
        getField().broadcastPacket(CMobPool.mobLeaveField(getObjectId()));
        // Distribute exp -
        distributeExp();
        // Clear the damaged Mob data -
        this.setHp(getMaxHp());
        this.setMp(getMaxMp());
        getDamageDone().clear();
        //TODO: need to add delay before spawning new mob but for now lets imminently spawn new one -
        getField().spawnMob(this, getController());

    }

    public void damage(MapleChar chr, long totalDamage) {
        registerCharDmg(chr.getId(), totalDamage);
        long maxHP = getMaxHp();
        long oldHp = getHp();
        long newHp = oldHp - totalDamage;
        setHp(newHp);
        double percentageDamage = ((double) newHp / maxHP);

        if (oldHp > 0 && newHp <= 0) {
            die(true);
        } else {
            getField().broadcastPacket(CMobPool.hpIndicator(getObjectId(), (byte) (percentageDamage * 100)));
        }
    }
}
