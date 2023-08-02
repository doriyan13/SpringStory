package com.dori.SpringStory.wzHandlers.wzEntities;

import com.dori.SpringStory.world.fieldEntities.Foothold;
import com.dori.SpringStory.world.fieldEntities.Life;
import com.dori.SpringStory.world.fieldEntities.Portal;
import com.dori.SpringStory.constants.GameConstants;
import com.dori.SpringStory.enums.FieldType;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.dori.SpringStory.constants.ServerConstants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapData {
    protected int id;
    protected int vrTop, vrLeft, vrBottom, vrRight;
    protected float mobRate;
    protected FieldType fieldType;
    protected Set<Portal> portals = new HashSet<>();
    protected Set<Foothold> footholds = new HashSet<>();
    protected Map<Integer, Life> lifes = new HashMap<>();
    protected String onFirstUserEnter = "", onUserEnter = "";
    protected int fixedMobCapacity;
    protected long fieldLimit;
    protected int returnMap, forcedReturn, createMobInterval, timeOut, timeLimit, lvLimit, lvForceMove;
    protected int consumeItemCoolTime, link;
    protected boolean town, swim, fly, reactorShuffle, expeditionOnly, partyOnly, needSkillForFly;
    protected Map<Integer, List<String>> directionInfo = new HashMap<>();
    protected boolean dropsDisabled;
    protected String fieldScript = "";
    protected boolean everLast;
    protected boolean personalShop;
    protected int decHP;
    protected int decInterval;
    protected int protectItem;
    protected float recovery;
    protected float dropRate;


    public MapData(int fieldID) {
        this.id = fieldID;
        this.portals = new HashSet<>();
        this.footholds = new HashSet<>();
        this.lifes = new ConcurrentHashMap<>();
        this.fixedMobCapacity = GameConstants.DEFAULT_FIELD_MOB_CAPACITY; // default
    }

    public void addFoothold(Foothold foothold) {
        getFootholds().add(foothold);
    }

    public Foothold findFootHoldBelow(Position pos) {
        Set<Foothold> footholds = getFootholds().stream().filter(
                fh -> fh.getX1() <= pos.getX() && fh.getX2() >= pos.getX()).collect(Collectors.toSet());
        Foothold res = null;
        int lastY = Integer.MAX_VALUE;
        for (Foothold fh : footholds) {
            int y = fh.findYFromX(pos.getX());
            if (res == null && y >= pos.getY()) {
                res = fh;
                lastY = y;
            } else {
                if (y < lastY && y >= pos.getY()) {
                    res = fh;
                    lastY = y;
                }
            }
        }
        return res;
    }

    public void addPortal(Portal portal) {
        getPortals().add(portal);
    }

    private boolean isObjIdUsed(int objID){
        return lifes.get(objID) != null;
    }

    protected Integer generateObjID(){
        boolean notFoundID = true;
        Integer newOid = -1;
        int safeIndexCounter = MAX_RETRIES;
        SecureRandom random = new SecureRandom();

        while (notFoundID) {
            newOid = random.nextInt(MAX_OBJ_ID) + MIN_OBJ_ID;
            safeIndexCounter--;
            if (!isObjIdUsed(newOid)) {
                notFoundID = false;
            }
            // Adding safe counter to avoid infinite loop -
            if (safeIndexCounter <= 0) {
                newOid = null;
                notFoundID = false;
            }
        }
        return newOid;
    }

    public void addLife(Life life){
        if(life.getObjectId() < 0){
            Integer newObjID = generateObjID();
            if (newObjID != null){
                life.setObjectId(newObjID);
            }
        }
        // Only if the object ID is valid add life to list -
        if(life.getObjectId() != -1){
            lifes.putIfAbsent(life.getObjectId(), life);
        }
    }

    public void addDirectionInfo(int node, List<String> scripts) {
        directionInfo.put(node, scripts);
    }
}
