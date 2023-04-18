package com.dori.SpringStory;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.netty.ChannelAcceptor;
import com.dori.SpringStory.world.MapleChannel;
import com.dori.SpringStory.world.MapleWorld;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.world.MigrateInUser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dori.SpringStory.constants.ServerConstants.*;
import static com.dori.SpringStory.constants.ServerConstants.CHANNELS_PER_WORLD;

@Component
public class Server {
    // Logger -
    private final Logger logger = new Logger(Server.class);
    // World list -
    private static final List<MapleWorld> worldList = new ArrayList<>();
    // Migrate in users -
    private static final Map<Integer, MigrateInUser> migrateUsers = new HashMap<>();

    public static List<MapleWorld> getWorlds() {
        return worldList;
    }

    public static MapleWorld getWorldById(int worldID){
        return getWorlds().stream()
                .filter(mapleWorld -> mapleWorld.getWorldID() == worldID)
                .findFirst()
                .orElse(null);
    }

    public static void migrateInNewUser(int accountID, MigrateInUser migrateInUser){
        migrateUsers.put(accountID,migrateInUser);
    }

    public static void addNewOnlineUser(MapleChar chr, MapleClient client){
        MigrateInUser migrateInUser = migrateUsers.get(chr.getAccountID());
        // Verify the user was migrated properly -
        if(migrateInUser != null){
            // Set Client data to have to migrate data -
            client.setWorldId((byte) migrateInUser.getWorldID());
            client.setMachineID(migrateInUser.getMachineID());
            client.setChannel((byte) migrateInUser.getChannel().getChannelId());
            client.setMapleChannelInstance(migrateInUser.getChannel());
            client.setAccount(migrateInUser.getAccount());
            // Add the char into the channel list of characters -
            client.getMapleChannelInstance().addChar(chr);
            // Set the char to be the client character instance -
            client.setChr(chr);
            // Remove from the list of users that need to migrate -
            migrateUsers.remove(chr.getId());
        }
        else {
            // trying to log in with a char that wasn't migrate in ?
            client.close();
        }
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
