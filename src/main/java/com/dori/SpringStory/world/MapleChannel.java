package com.dori.SpringStory.world;

import com.dori.SpringStory.client.character.MapleAccount;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.enums.ServiceType;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.services.MapleCharService;
import com.dori.SpringStory.services.ServiceManager;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.dataHandlers.MapDataHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapleChannel {
    // Fields -
    private int port;
    private String name;
    private int worldId, channelId;
    private boolean adultChannel;
    // TODO: The map of fields will only hold normal maps, boss maps i will have to manage in diff map !
    private Map<Integer, Field> fields;
    private Map<Integer, MapleChar> chars;
    public final int MAX_SIZE = 1000;
    // Logger -
    private static final Logger logger = new Logger(MapleChannel.class);

    private MapleChannel(String name, MapleWorld world, int channelId, boolean adultChannel) {
        this.name = name;
        this.worldId = world.getWorldID();
        this.channelId = channelId;
        this.adultChannel = adultChannel;
        this.port = ServerConstants.LOGIN_PORT + 100 + channelId;
        this.fields = new ConcurrentHashMap<>();
        this.chars = new ConcurrentHashMap<>();
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
        this.fields = new ConcurrentHashMap<>();
        this.chars = new ConcurrentHashMap<>();
    }

    public void shutdown() {
        for (MapleChar chr : chars.values()) {
            logger.warning("Logging out - ID: " + chr.getId() + " | Name - " + chr.getName());
            // Save the Character progress in the DB -
            ((MapleCharService) ServiceManager.getService(ServiceType.Character)).update((long) chr.getId(), chr);
            logger.warning("Saved - ID: " + chr.getId() + " | Name - " + chr.getName() + " Progress successfully");
            // Close the user client -
            chr.getMapleClient().close();
        }
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

    public Field getField(int fieldID) {
        Field newField = fields.get(fieldID);
        if (newField == null && MapDataHandler.getMapByID(fieldID) instanceof Field newFieldFromDataHandler) {
            newField = newFieldFromDataHandler;
            this.fields.put(newField.getId(), newField);
        }
        return newField;
    }

    public Field getField(String fieldName) {
        Integer fieldID = MapDataHandler.getGoToMaps().get(fieldName.toLowerCase());
        return fieldID != null ? getField(fieldID) : null;
    }

    public void clearUnUsedFields() {
        getFields().entrySet().removeIf(fieldEntry -> {
            Field field = fieldEntry.getValue();
            if (field.getPlayers().isEmpty() && field.getDeprecationStartTime() == 0) {
                field.setDeprecationStartTime(System.currentTimeMillis());
            } else if (!field.getPlayers().isEmpty()) {
                field.setDeprecationStartTime(0);
            }
            boolean shouldRemove = field.getDeprecationStartTime() != 0 && System.currentTimeMillis() - field.getDeprecationStartTime() >= ServerConstants.FIELD_DEPRECATION_TIME_IN_MIN * 60_000;
            if (shouldRemove) {
                field.shutdownField();
                logger.serverNotice("The Field " + fieldEntry.getKey() + " was removed!");
            }
            return shouldRemove;
        });
    }

}