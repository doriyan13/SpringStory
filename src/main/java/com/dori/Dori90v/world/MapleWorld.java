package com.dori.Dori90v.world;

import com.dori.Dori90v.client.MapleClient;
import com.dori.Dori90v.enums.WorldState;
import com.dori.Dori90v.enums.WorldStatus;
import com.dori.Dori90v.utils.utilEntities.Position;
import com.dori.Dori90v.utils.utilEntities.Tuple;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import static com.dori.Dori90v.constants.ServerConstants.*;

@Data
@NoArgsConstructor
public class MapleWorld {
    // Fields -
    private int worldID;
    private String name;
    private List<MapleChannel> mapleChannels;
    private WorldState worldState;
    private int worldEventEXP_WSE;
    private int worldEventDrop_WSE;
    private String worldEventDescription;
    private boolean isCharCreationBlocked;
    private Map<Integer, MapleClient> connectedChatClients = new HashMap<>();

    private boolean full;
    private WorldStatus status;

    Set<Tuple<Position, String>> worldSelectMessages;

    public MapleWorld(int worldID, String name, String worldEventDescription, int amountOfChannels){
        this.worldID = worldID;
        this.name = name;
        this.worldEventDescription = worldEventDescription;
        List<MapleChannel> mapleChannelList = new ArrayList<>();
        for (int i = 1; i <= amountOfChannels; i++) {
            mapleChannelList.add(new MapleChannel(name, worldID, i));
        }
        this.mapleChannels = mapleChannelList;

        this.worldState = WorldState.Hot;
        this.worldEventEXP_WSE = WORLD_EVENT_EXP_WSE;
        this.worldEventDrop_WSE = WORLD_EVENT_DROP_WSE;

        this.isCharCreationBlocked = false;

        this.full = false;
        this.status = WorldStatus.NORMAL;

        this.worldSelectMessages = new HashSet<>();
    }

    public boolean isFull() {
        boolean full = true;
        for (MapleChannel mapleChannel : this.getMapleChannels()) {
            if (mapleChannel.getChars().size() < mapleChannel.MAX_SIZE) {
                full = false;
                break;
            }
        }
        return full;
    }

    public MapleChannel getChannelById(byte id) {
        return getMapleChannels().stream().filter(c -> c.getChannelId() == id).findFirst().orElse(null);
    }
}
