package com.dori.SpringStory.world.fieldEntities;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.packets.CClientSocket;
import com.dori.SpringStory.connection.packet.packets.CMobPool;
import com.dori.SpringStory.connection.packet.packets.CNpcPool;
import com.dori.SpringStory.connection.packet.packets.CStage;
import com.dori.SpringStory.enums.MobControllerType;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.world.fieldEntities.mob.Mob;
import com.dori.SpringStory.wzHandlers.MobDataHandler;
import com.dori.SpringStory.wzHandlers.wzEntities.MapData;
import com.dori.SpringStory.wzHandlers.wzEntities.MobData;
import lombok.*;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static com.dori.SpringStory.constants.GameConstants.DEFAULT_FIELD_MOB_CAPACITY;
import static com.dori.SpringStory.constants.GameConstants.DEFAULT_FIELD_MOB_RATE_BY_MOB_GEN_COUNT;
import static com.dori.SpringStory.constants.ServerConstants.*;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class Field extends MapData {
    // Fields -
    private Map<Integer, MapleChar> players = new ConcurrentHashMap<>();
    private Map<Integer, Npc> npcs = new ConcurrentHashMap<>();
    private Map<Integer, Mob> mobs = new ConcurrentHashMap<>();
    private Map<Integer, Drop> drops = new ConcurrentHashMap<>();
    private Queue<Integer> objIdAllocator = new ArrayBlockingQueue<>(MAX_OBJECT_ID_ALLOCATED_TO_FIELD);
    private long creationTime;

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
        // init the Object allocator -
        initObjIdAllocated();
        for (Life life : mapData.getLifes()) {
            if (life.getLifeType().equalsIgnoreCase("n")) {
                this.addNPC(new Npc(life));
            } else if (life.getLifeType().equalsIgnoreCase("m")) {
                Mob mob = new Mob(life);
                MobData mobData = MobDataHandler.getMobDataByID(life.getTemplateId());
                if (mobData != null) {
                    mob.applyMobData(mobData);
                    mob.setRespawnable(true);
                    this.addMob(mob);
                }
            } else {
                //TODO: see what lifes i've missed
                //this.addLife(life.deepCopy());
            }
        }
        this.dropsDisabled = mapData.isDropsDisabled();
        this.creationTime = System.currentTimeMillis();
    }

    public void initObjIdAllocated() {
        for (int i = 1; i <= MAX_OBJECT_ID_ALLOCATED_TO_FIELD; i++) {
            objIdAllocator.add(i);
        }
    }

    protected Integer generateObjID() {
        return objIdAllocator.poll();
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

    public void addPlayer(MapleChar chr, boolean characterData) {
        boolean firstPlayerInField = players.isEmpty();
        MapleClient c = chr.getMapleClient();
        // Add player to the field -
        players.putIfAbsent(chr.getId(), chr);
        // Update for the char instance the field data -
        chr.setField(this);
        chr.setMapId(getId());
        c.write(CStage.onSetField(c.getChr(), c.getChr().getField(), (short) 0, c.getChannel(),
                0, characterData, (byte) 1, (short) 0,
                "", new String[]{""}));
        // Spawn lifes for the client -
        this.spawnLifesForCharacter(chr);
        if (firstPlayerInField) {
            // Assign Controllers For life -
            this.assignControllerToMobs(chr);
        }
        // Apply the chr temporary stats -
        chr.applyTemporaryStats();
    }

    public void removePlayer(MapleChar chr) {
        players.remove(chr.getId());
    }

    public void addNPC(Npc npc) {
        if (npc.getObjectId() <= 0) {
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
        if (mob.getObjectId() <= 0) {
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

    public void spawnMob(Mob mob, MapleChar chr) {
        addMob(mob);
        getPlayers().values().forEach(player -> player.write(CMobPool.mobEnterField(mob)));
        mob.setController(chr);
        chr.write(CMobPool.mobChangeController(mob, MobControllerType.ActiveInit));
    }

    public void removeMob(int objId) {
        Mob mob = getMobs().get(objId);
        if (mob != null) {
            getMobs().remove(mob.getObjectId());
            // return the allocated objectId -
            objIdAllocator.add(mob.getObjectId());
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
