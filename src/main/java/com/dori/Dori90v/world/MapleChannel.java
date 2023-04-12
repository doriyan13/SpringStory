package com.dori.Dori90v.world;

import com.dori.Dori90v.client.MapleClient;
import com.dori.Dori90v.client.character.MapleChar;
import com.dori.Dori90v.constants.ServerConstants;
import com.dori.Dori90v.utils.MapleUtils;
import com.dori.Dori90v.utils.utilEntities.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapleChannel {
    private int port;
    private String name;
    private int worldId, channelId;
    private boolean adultChannel;
//    private List<Field> fields;
    private Map<Integer, Tuple<Byte, MapleClient>> transfers;
    private Map<Integer, MapleChar> chars = new HashMap<>();
    public final int MAX_SIZE = 1000;

    private MapleChannel(String name, MapleWorld world, int channelId, boolean adultChannel) {
        this.name = name;
        this.worldId = world.getWorldID();
        this.channelId = channelId;
        this.adultChannel = adultChannel;
        this.port = ServerConstants.LOGIN_PORT + 100 + channelId;
//        this.fields = new CopyOnWriteArrayList<>();
        this.transfers = new HashMap<>();
    }

    public MapleChannel(MapleWorld world, int channelId) {
        this(world.getName() + "-" + channelId, world, channelId, false);
    }

    public MapleChannel(String worldName, int worldId, int channelId) {
        this.name = worldName + "-" + channelId;
        this.worldId = worldId;
        this.channelId = channelId;
        this.adultChannel = false;
        this.port = ServerConstants.LOGIN_PORT + (100 * worldId) + channelId;
//        this.fields = new CopyOnWriteArrayList<>();
        this.transfers = new HashMap<>();
    }

    public int getGaugePx() {
        return Math.max(1, (chars.size() * 64) / MAX_SIZE);
    }

    public Map<Integer, Tuple<Byte, MapleClient>> getTransfers() {
        if (transfers == null) {
            transfers = new HashMap<>();
        }
        return transfers;
    }

    public void addClientInTransfer(byte channelId, int characterId, MapleClient mapleClient) {
        getTransfers().put(characterId, new Tuple<>(channelId, mapleClient));
    }

    public void removeClientFromTransfer(int characterId) {
        getTransfers().remove(characterId);
    }

    public void addChar(MapleChar chr) {
        getChars().put(chr.getId(), chr);
    }

    public void removeChar(MapleChar chr) {
        getChars().remove(chr.getId());
    }

    public MapleChar getCharById(int id) {
        return getChars().get(id);
    }

    public MapleChar getCharByName(String name) {
        return MapleUtils.findWithPred(getChars().values(), chr -> chr.getName().equals(name));
    }

//    public Account getAccountByID(int accID) {
//        for (Char chr : getChars().values()) {
//            if (chr.getAccId() == accID) {
//                return chr.getAccount();
//            }
//        }
//        return null;
//    }
//
//    public void broadcastPacket(OutPacket outPacket) {
//        for (Char chr : getChars().values()) {
//            chr.write(outPacket);
//        }
//    }
//
//    public void clearCache() {
//        Set<Field> toRemove = new HashSet<>();
//        for (Field field : getFields()) {
//            if (field.getChars().size() == 0 && field.getDrops().size() == 0) {
//                toRemove.add(field);
//            }
//        }
//        getFields().removeAll(toRemove);
//    }

}
