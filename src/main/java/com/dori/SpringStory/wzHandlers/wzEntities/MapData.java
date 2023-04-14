package com.dori.SpringStory.wzHandlers.wzEntities;

import com.dori.SpringStory.world.fieldEntities.Foothold;
import com.dori.SpringStory.world.fieldEntities.Life;
import com.dori.SpringStory.world.fieldEntities.Portal;
import com.dori.SpringStory.constants.GameConstants;
import com.dori.SpringStory.enums.FieldType;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.utils.utilEntities.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.dori.SpringStory.constants.ServerConstants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MapData {
    private int id;
    private int vrTop, vrLeft, vrBottom, vrRight;
    private float mobRate;
    private FieldType fieldType;
    private Set<Portal> portals;
    private Set<Foothold> footholds;
    private Map<Integer, Life> lifes;
    private String onFirstUserEnter = "", onUserEnter = "";
    private int fixedMobCapacity;
    private long fieldLimit;
    private int returnMap, forcedReturn, createMobInterval, timeOut, timeLimit, lvLimit, lvForceMove;
    private int consumeItemCoolTime, link;
    private boolean town, swim, fly, reactorShuffle, expeditionOnly, partyOnly, needSkillForFly;
    private Map<Integer, List<String>> directionInfo;
    private boolean dropsDisabled;
    private String fieldScript = "";
    private boolean everLast;
    private boolean personalShop;
    private int decHP;
    private int decInterval;
    private int protectItem;
    private float recovery;
    private float dropRate;


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
            int y = fh.getYFromX(pos.getX());
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

    public Tuple<Foothold, Foothold> getMinMaxNonWallFH() {
        Set<Foothold> footholds = getFootholds().stream().filter(fh -> !fh.isWall()).collect(Collectors.toSet());
        Foothold left = footholds.iterator().next(), right = footholds.iterator().next(); // return values

        for (Foothold fh : footholds) {
            if (fh.getX1() < left.getX1()) {
                left = fh;
            } else if (fh.getX1() > right.getX1()) {
                right = fh;
            }
        }
        return new Tuple<>(left, right);
    }

    public void addPortal(Portal portal) {
        getPortals().add(portal);
    }

    private boolean isObjIdUsed(int objID){
        return lifes.get(objID) != null;
    }

    private Integer generateObjID(){
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
