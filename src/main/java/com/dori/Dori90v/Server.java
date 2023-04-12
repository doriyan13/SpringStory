package com.dori.Dori90v;

import com.dori.Dori90v.connection.netty.ChannelAcceptor;
import com.dori.Dori90v.world.MapleChannel;
import com.dori.Dori90v.world.MapleWorld;
import com.dori.Dori90v.logger.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.dori.Dori90v.constants.ServerConstants.*;
import static com.dori.Dori90v.constants.ServerConstants.CHANNELS_PER_WORLD;

@Component
public class Server {
    // Logger -
    private final Logger logger = new Logger(Server.class);
    // World list -
    private static final List<MapleWorld> worldList = new ArrayList<>();

    public static List<MapleWorld> getWorlds() {
        return worldList;
    }

    public static MapleWorld getWorldById(int worldID){
        return getWorlds().stream()
                .filter(mapleWorld -> mapleWorld.getWorldID() == worldID)
                .findFirst()
                .orElse(null);
    }

    public static boolean isWorldExist(int worldID){
        return getWorldById(worldID) != null;
    }

    public static void initMapleWorlds(){
        // init world -
        worldList.add(new MapleWorld(DEFAULT_WORLD_ID, WORLD_NAME, EVENT_MSG, CHANNELS_PER_WORLD));
        for (MapleWorld world : getWorlds()) {
            for (MapleChannel mapleChannel : world.getMapleChannels()) {
                ChannelAcceptor ca = new ChannelAcceptor();
                ca.mapleChannel = mapleChannel;
                new Thread(ca).start();
            }
        }
    }
}
