package com.dori.SpringStory.world.fieldEntities;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.packets.CMobPool;
import com.dori.SpringStory.connection.packet.packets.CNpcPool;
import com.dori.SpringStory.enums.MobControllerType;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.world.fieldEntities.mob.Mob;
import com.dori.SpringStory.wzHandlers.MobDataHandler;
import com.dori.SpringStory.wzHandlers.wzEntities.MapData;
import com.dori.SpringStory.wzHandlers.wzEntities.MobData;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

import static com.dori.SpringStory.constants.GameConstants.DEFAULT_FIELD_MOB_CAPACITY;
import static com.dori.SpringStory.constants.GameConstants.DEFAULT_FIELD_MOB_RATE_BY_MOB_GEN_COUNT;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class Field extends MapData {
    // Fields -
    private Map<Integer, MapleChar> players = new HashMap<>();
    private Map<Integer, Npc> npcs = new HashMap<>();
    private Map<Integer, Mob> mobs = new HashMap<>();

    public Field(int id) {
        super(id);
    }

    public Field(MapData mapData) {
        // TODO: maybe the id management will change in the future!
        this.id = mapData.getId();
        this.fieldType = mapData.getFieldType();
        this.town = mapData.isTown();
        this.swim = mapData.isSwim();
        this.fly = mapData.isFly();
        this.fieldLimit = mapData.getFieldLimit();
        this.returnMap = mapData.getReturnMap();
        this.forcedReturn = mapData.getForcedReturn();
        this.mobRate = mapData.getMobRate();
        this.onFirstUserEnter = mapData.getOnFirstUserEnter();
        this.onUserEnter = mapData.getOnUserEnter();
        this.fieldScript = mapData.getFieldScript();
        this.reactorShuffle = mapData.isReactorShuffle();
        this.partyOnly = mapData.isPartyOnly();
        this.expeditionOnly = mapData.isExpeditionOnly();
        this.needSkillForFly = mapData.isNeedSkillForFly();
        this.fixedMobCapacity = mapData.getFixedMobCapacity() != 0 ? mapData.getFixedMobCapacity() : DEFAULT_FIELD_MOB_CAPACITY;
        this.fixedMobCapacity = mapData.getFixedMobCapacity() != 0 ? mapData.getFixedMobCapacity() : (int) DEFAULT_FIELD_MOB_RATE_BY_MOB_GEN_COUNT;
        this.createMobInterval = mapData.getCreateMobInterval();
        this.timeOut = mapData.getTimeOut();
        this.timeLimit = mapData.getTimeLimit();
        this.consumeItemCoolTime = mapData.getConsumeItemCoolTime();
        this.link = mapData.getLink();
        this.setVrTop(mapData.getVrTop());
        this.setVrLeft(mapData.getVrLeft());
        this.setVrBottom(mapData.getVrBottom());
        this.setVrRight(mapData.getVrRight());
        for (Foothold fh : mapData.getFootholds()) {
            this.addFoothold(fh.deepCopy());
        }
        for (Portal portal : mapData.getPortals()) {
            this.addPortal(portal.deepCopy());
        }
        for (Life life : mapData.getLifes().values()) {
            if (life.getLifeType().equalsIgnoreCase("n")) {
                this.addNPC(new Npc(life));
            } else if (life.getLifeType().equalsIgnoreCase("m")) {
                Mob mob = new Mob(life);
                MobData mobData = MobDataHandler.getMobDataByID(life.getTemplateId());
                if(mobData != null){
                    mob.applyMobData(mobData);
                    this.addMob(mob);
                }
            } else {
                this.addLife(life.deepCopy());
            }
        }
        this.players = new HashMap<>();
        this.dropsDisabled = mapData.isDropsDisabled();
    }

    public Portal getPortalByName(String name) {
        return MapleUtils.findWithPred(getPortals(), portal -> portal.getName().equals(name));
    }

    public Portal getPortalByID(int id) {
        return MapleUtils.findWithPred(getPortals(), portal -> portal.getId() == id);
    }

    public Portal findDefaultPortal() {
        Portal portal = getPortalByName("sp");
        return portal != null ? portal : getPortalByID(0);
    }

    public void addPlayer(MapleChar chr) {
        players.put(chr.getId(), chr);
    }

    public void removePlayer(MapleChar chr) {
        players.remove(chr.getId(), chr);
    }

    public void addNPC(Npc npc) {
        if (npc.getObjectId() < 0) {
            Integer newObjID = generateObjID();
            if (newObjID != null) {
                npc.setObjectId(newObjID);
            }
        }
        // Only if the object ID is valid add life to list -
        if (npc.getObjectId() != -1) {
            npcs.putIfAbsent(npc.getObjectId(), npc);
        }
    }

    public void addMob(Mob mob) {
        if (mob.getObjectId() < 0) {
            Integer newObjID = generateObjID();
            if (newObjID != null) {
                mob.setObjectId(newObjID);
            }
        }
        // Only if the object ID is valid add life to list -
        if (mob.getObjectId() != -1) {
            mob.setField(this);
            mobs.putIfAbsent(mob.getObjectId(), mob);
        }
    }

    public void spawnLifesForCharacter(MapleChar chr) {
        // TODO: Maybe need to refactor and rethink when to respawn npc/mobs? cuz if there is more then 1 player it can act weird
        // Spawn NPCs for the client -
        npcs.values().forEach(npc -> chr.write(CNpcPool.npcEnterField(npc)));
        // Spawn Mobs for the client -
        mobs.values().forEach(mob -> chr.write(CMobPool.mobEnterField(mob)));
    }

    public void assignControllerToMobs(MapleChar chr) {
        //TODO: assigning a char suppose to be random and not the new char that enter the map each time!

        // Assign Controller to Mobs for the client -
        mobs.values().forEach(mob -> {
            mob.setController(chr);
            chr.write(CMobPool.mobChangeController(mob, MobControllerType.ActiveInit));
        });
    }

    public void spawnMob(Mob mob, MapleChar chr){
        addMob(mob);
        chr.write(CMobPool.mobEnterField(mob));
        mob.setController(chr);
        chr.write(CMobPool.mobChangeController(mob, MobControllerType.ActiveInit));
    }

    public void removeMob(int objId) {
        Mob mob = getMobs().get(objId);
        if (mob != null) {
            getMobs().remove(mob.getObjectId());
        }
    }

    public void broadcastPacket(OutPacket outPacket) {
        getPlayers().values().forEach(chr -> chr.write(outPacket));
    }

    public void broadcastPacket(OutPacket outPacket, MapleChar exceptChr) {
        // No point broadcast a packet when you're alone in the map -
        if (getPlayers().size() > 1) {
            getPlayers().values().forEach(
                    chr -> {
                        if (chr.getId() != exceptChr.getId()) {
                            chr.write(outPacket);
                        }
                    }
            );
        }
    }
}
