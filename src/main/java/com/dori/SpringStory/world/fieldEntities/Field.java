package com.dori.SpringStory.world.fieldEntities;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.packets.*;
import com.dori.SpringStory.constants.GameConstants;
import com.dori.SpringStory.dataHandlers.ItemDataHandler;
import com.dori.SpringStory.dataHandlers.NpcDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.MobDropData;
import com.dori.SpringStory.dataHandlers.dataEntities.NpcData;
import com.dori.SpringStory.enums.*;
import com.dori.SpringStory.events.EventManager;
import com.dori.SpringStory.events.eventsHandlers.RemoveDropFromField;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.utils.utilEntities.PositionData;
import com.dori.SpringStory.world.fieldEntities.mob.Mob;
import com.dori.SpringStory.dataHandlers.MobDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.MapData;
import com.dori.SpringStory.dataHandlers.dataEntities.MobData;
import lombok.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import static com.dori.SpringStory.constants.GameConstants.DEFAULT_FIELD_MOB_CAPACITY;
import static com.dori.SpringStory.constants.GameConstants.QUICK_SLOT_MAPPING_SIZE;
import static com.dori.SpringStory.constants.ServerConstants.*;
import static com.dori.SpringStory.utils.HashUuidCreator.getRandomUuidInLong;

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
    private Queue<Integer> objIdAllocator = new PriorityBlockingQueue<>(MAX_OBJECT_ID_ALLOCATED_TO_FIELD);
    private long creationTime;
    private long deprecationStartTime;
    private List<PositionData> mobsSpawnPoints = new ArrayList<>();

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
                Npc npc = new Npc(life);
                NpcData npcData = NpcDataHandler.getMobDataByID(life.getTemplateId());
                if (npcData != null) {
                    npc.setMove(npcData.isMove());
                }
                this.addNPC(npc);
            } else if (life.getLifeType().equalsIgnoreCase("m")) {
                Mob mob = new Mob(life);
                MobData mobData = MobDataHandler.getMobDataByID(life.getTemplateId());
                if (mobData != null) {
                    mob.applyMobData(mobData);
                    mob.setRespawnable(true);
                    this.addMob(mob);
                    // Add spawn point -
                    mobsSpawnPoints.add(new PositionData(mob.getPosition(), mob.getFh()));
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
            objIdAllocator.offer(i);
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

    public Foothold getFootholdById(int fh) {
        return getFootholds().stream()
                .filter(f -> f.getId() == fh)
                .findFirst()
                .orElse(null);
    }

    public void spawnPlayer(MapleChar chr, boolean characterData) {
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
        if (firstPlayerInField && !chr.isHidden()) {
            // Assign Controllers For life -
            this.assignControllerToMobs(chr);
            this.assignControllerToNpcs(chr);
        }
        // Init the player passive stats -
        chr.initPassiveStats();
        // Init the player Equip stats -
        chr.initEquipStats();
        // Apply the chr temporary stats -
        chr.applyTemporaryStats();
        // Init function keys mapping -
        chr.write(CFuncKeyMappedMan.funcKeyMappedManInit(chr.getKeymap()));
        // Init Quick slots mapping -
        if (chr.getQuickSlotKeys().size() == QUICK_SLOT_MAPPING_SIZE) {
            chr.write(CField.quickSlotMappedInit(chr.getQuickSlotKeys()));
        }
        this.players.forEach((key, value) -> {
            if (key != chr.getId()) {
                chr.write(CUserPool.userEnterField(value));
            }
        });
        // broadcast spawn to other characters in field -
        if (!chr.isHidden()) {
            broadcastPacket(CUserPool.userEnterField(chr), chr);
        }
    }

    public void removePlayer(MapleChar chr) {
        players.remove(chr.getId());
        // broadcast leave of field to other characters in field -
        broadcastPacket(CUserPool.userLeaveField(chr));
    }

    private void addNPC(Npc npc) {
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
        // Spawn NPCs for the client -
        npcs.values().forEach(npc -> chr.write(CNpcPool.npcEnterField(npc)));
        // Spawn Mobs for the client -
        mobs.values().forEach(mob -> chr.write(CMobPool.mobEnterField(mob)));
        // Spawn Drops for the client -
        drops.values().forEach(drop -> chr.write(CDropPool.dropEnterField(drop, DropEnterType.INSTANT, DropOwnType.USER_OWN, drop.getOwnerID(), drop.getPosition(), (short) 0, true)));
    }

    public void assignControllerToMobs(MapleChar chr) {
        //TODO: assigning a char suppose to be random and not the new char that enter the map each time!

        // Assign Controller to Mobs for the client -
        mobs.values().forEach(mob -> {
            mob.setController(chr);
            chr.write(CMobPool.mobChangeController(mob, MobControllerType.ActiveInit));
        });
    }

    public void assignControllerToNpcs(MapleChar chr) {
        //TODO: assigning a char suppose to be random and not the new char that enter the map each time!

        // Assign Controller to Mobs for the client -
        npcs.values().forEach(npc -> {
            npc.setController(chr);
            chr.write(CNpcPool.npcChangeController(npc, true, false));
        });
    }

    public void spawnMobById(int mobId, MapleChar controller) {
        Mob mob = MobDataHandler.getMobByID(mobId);
        if (mob != null) {
            //TODO: i want to redo the position concept - randomize it on the initial spawn points a map have
            Position pos = controller.getPosition();
            mob.setPosition(pos.deepCopy());
            mob.setVPosition(pos.deepCopy());
            mob.setHomePosition(pos.deepCopy());
            mob.setFh(controller.getFoothold());
            mob.setHomeFh(controller.getFoothold());
            mob.setRespawnable(false);
            mob.setField(this);
            spawnMob(mob, controller);
        }
    }

    public void spawnMob(Mob mob, MapleChar controller) {
        addMob(mob);
        getPlayers().values().forEach(player -> player.write(CMobPool.mobEnterField(mob)));
        mob.setController(controller);
        controller.write(CMobPool.mobChangeController(mob, MobControllerType.ActiveInit));
    }

    public void removeMob(int objId) {
        Mob mob = getMobs().get(objId);
        if (mob != null) {
            getMobs().remove(mob.getObjectId());
            // return the allocated objectId -
            objIdAllocator.offer(mob.getObjectId());
            // Clear the mob objectId -
            mob.setObjectId(0);
            // broadcast the remove of the mob from the field -
            broadcastPacket(CMobPool.mobLeaveField(objId));
        }
    }

    public void broadcastPacket(OutPacket outPacket) {
        getPlayers().values().forEach(chr -> chr.write((OutPacket) outPacket.clone()));
    }

    public void broadcastPacket(OutPacket outPacket, MapleChar exceptChr) {
        // No point broadcast a packet when you're alone in the map -
        if (getPlayers().size() > 1) {
            getPlayers().values().forEach(
                    chr -> {
                        if (chr.getId() != exceptChr.getId()) {
                            chr.write((OutPacket) outPacket.clone());
                        }
                    }
            );
        }
    }

    private Drop generateDropByMobDropData(MobDropData dropData, int ownerID, float mesoRate) {
        Drop drop = null;
        if (!dropData.isMoney()) {
            Item item = ItemDataHandler.getItemByID(dropData.getItemId());
            if (item == null) {
                item = ItemDataHandler.getEquipByID(dropData.getItemId());
            }
            if (item != null) {
                drop = new Drop(dropData);
                drop.setOwnerID(ownerID);
            }
        } else {
            drop = new Drop(dropData);
            drop.setOwnerID(ownerID);
            if (dropData.isMoney()) {
                drop.setQuantity((int) (drop.getQuantity() * ((100 + mesoRate) / 100D)));
            }
        }
        return drop;
    }

    public void drop(List<MobDropData> dropsData, int srcID, int ownerID, Foothold fh, Position position, float mesoRate, float dropRate) {
        int x = position.getX();
        int diff = 0;
        int minX = position.getX();
        int maxX = position.getX();
        if (fh != null) {
            minX = fh.getX1();
            maxX = fh.getX2();
        }
        for (MobDropData dropData : dropsData) {
            if (dropData.willDrop(dropRate) && dropData.getQuestId() == 0) {
                x = (x + diff) > maxX ? maxX - 10 : (x + diff) < minX ? minX + 10 : x + diff;
                Position toPos = (fh == null) ? position.deepCopy() : new Position(x, fh.findYFromX(x));
                Drop drop = generateDropByMobDropData(dropData, ownerID, mesoRate);
                if (drop != null) {
                    diff = diff < 0 ? Math.abs(diff - GameConstants.DROP_DIFF) : -(diff + GameConstants.DROP_DIFF);
                    spawnDrop(drop, srcID, toPos, true);
                }
            }
        }
    }

    private void addDrop(Drop drop) {
        if (drop.getId() <= 0) {
            Integer newObjID = generateObjID();
            if (newObjID != null) {
                drop.setId(newObjID);
            }
        }
        // Only if the object ID is valid add life to list -
        if (drop.getId() != -1) {
            drops.putIfAbsent(drop.getId(), drop);
        }
    }

    public void spawnDrop(Drop drop, int srcID, Position srcPos, boolean isTradeableItem) {
        short replay = 100;
        addDrop(drop);
        DropEnterType dropEnterType = DropEnterType.FLOATING;
        if (!isTradeableItem) {
            dropEnterType = DropEnterType.FADE_AWAY;
        }
        // Set the drop position -
        int x = srcPos.getX();
        int y = srcPos.getY();
        Foothold fh = findFootHoldBelow(new Position(x, y - GameConstants.DROP_HEIGHT));
        drop.setPosition(new Position(x, fh.findYFromX(x)));
        // Set the fromPos -
        Position fromPos = new Position(drop.getPosition());
        fromPos.setY(fromPos.getY() - 20);

        broadcastPacket(CDropPool.dropEnterField(drop, dropEnterType, DropOwnType.USER_OWN, srcID, fromPos, replay, true));
        EventManager.addEvent(getRandomUuidInLong(), EventType.REMOVE_DROP_FROM_FIELD, new RemoveDropFromField(drop, this), DROP_REMAIN_ON_GROUND_TIME);
    }

    public void spawnDrop(Drop drop, Position fromPos) {
        spawnDrop(drop, drop.getOwnerID(), fromPos, true);
    }

    public void removeDrop(int dropID, int pickupID, int petID) {
        Drop dropToRemove = getDrops().get(dropID);
        if (dropToRemove != null) {
            getDrops().remove(dropToRemove.getId());
            // Return the allocated objectId -
            objIdAllocator.offer(dropToRemove.getId());
            // Broadcast the remove of the drop from the field -
            if (petID >= 0) {
                broadcastPacket(CDropPool.dropLeaveField(DropLeaveType.PET_PICKUP, pickupID, dropID, (short) 0, petID));
            } else if (pickupID > 0) {
                broadcastPacket(CDropPool.dropLeaveField(DropLeaveType.USER_PICKUP, pickupID, dropID, (short) 0, 0));
            } else {
                broadcastPacket(CDropPool.dropLeaveField(DropLeaveType.TIME_OUT, pickupID, dropID, (short) 0, 0));
            }
        }
    }

    public void shutdownField() {
        // Clear mobs -
        getMobs().values().forEach(mob -> mob.setField(null));
        getMobs().clear();
        // Clear Npcs -
        getNpcs().values().forEach(npc -> npc.setField(null));
        getNpcs().clear();
        // Clear Drops -
        getDrops().keySet().forEach(dropObjID -> EventManager.cancelEvent(MapleUtils.concat((long) getId(), dropObjID), EventType.REMOVE_DROP_FROM_FIELD));
        getDrops().clear();
    }
}
