package com.dori.SpringStory.world;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleAccount;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.utilEntities.Tuple;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.wzHandlers.MapDataHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapleChannel {
    private int port;
    private String name;
    private int worldId, channelId;
    private boolean adultChannel;
    // TODO: The map of fields will only hold normal maps, boss maps i will have to manage in diff map !
    private Map<Integer, Field> fields;
    private Map<Integer, MapleChar> chars = new HashMap<>();
    public final int MAX_SIZE = 1000;

    private MapleChannel(String name, MapleWorld world, int channelId, boolean adultChannel) {
        this.name = name;
        this.worldId = world.getWorldID();
        this.channelId = channelId;
        this.adultChannel = adultChannel;
        this.port = ServerConstants.LOGIN_PORT + 100 + channelId;
        this.fields = new HashMap<>();
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
        this.fields = new HashMap<>();
    }

    public int getGaugePx() {
        return Math.max(1, (chars.size() * 64) / MAX_SIZE);
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

    public MapleAccount getAccountByID(int accID) {
        for (MapleChar chr : getChars().values()) {
            if (chr.getAccountID() == accID) {
                return chr.getMapleClient().getAccount();
            }
        }
        return null;
    }

    public void broadcastPacket(OutPacket outPacket) {
        for (MapleChar chr : getChars().values()) {
            chr.write(outPacket);
        }
    }

    public Field getField(int fieldID){
        Field newField;
        if(fields.get(fieldID) == null){
            newField = MapDataHandler.getMapByID(fieldID);
            if(newField != null){
                this.fields.put(newField.getId(), newField);
            }
        } else {
            newField = fields.get(fieldID);
        }
        return newField;
    }

}
