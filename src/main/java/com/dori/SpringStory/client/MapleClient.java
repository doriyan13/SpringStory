package com.dori.SpringStory.client;

import com.dori.SpringStory.Server;
import com.dori.SpringStory.client.character.MapleAccount;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.netty.NettyClient;
import com.dori.SpringStory.connection.packet.packets.CClientSocket;
import com.dori.SpringStory.connection.packet.packets.CStage;
import com.dori.SpringStory.enums.ServiceType;
import com.dori.SpringStory.services.MapleCharService;
import com.dori.SpringStory.services.ServiceManager;
import com.dori.SpringStory.world.MapleChannel;
import com.dori.SpringStory.world.MigrateInUser;
import com.dori.SpringStory.world.fieldEntities.Field;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@EqualsAndHashCode(callSuper = true)
public class MapleClient extends NettyClient {
    private MapleChar chr;
    private Lock lock;
    private MapleAccount account;
    private byte channel;
    private byte worldId;
    private MapleChannel mapleChannelInstance;
    private byte[] machineID;
    private byte oldChannel;

    public MapleClient(io.netty.channel.Channel channel) {
        super(channel);
        lock = new ReentrantLock(true);
    }

    public void logout() {
        MapleChar chr = getChr();
        // Remove the character from the list of online characters -
        chr.getMapleClient().getMapleChannelInstance().removeChar(chr);
        ((MapleCharService) ServiceManager.getService(ServiceType.Character)).update((long) chr.getId(), chr);
        // Remove client from list of connected clients -
        Server.removeClient(this);
        // Remove player from field -
        chr.getField().removePlayer(chr);
    }

    public void changeChannel(MapleChannel targetChannel) {
        // Disconnect client for migrate -
        logout();
        // Switch channel instances -
        setOldChannel(getChannel());
        setChannel((byte) targetChannel.getChannelId());
        setMapleChannelInstance(targetChannel);
        // Migrate chr -
        Server.migrateInNewUser(this);
        // Send Migrate to the client (reconnect to the new client and reset the client) -
        chr.write(CClientSocket.migrateCommand(true, chr.getMapleClient().getMachineID(), chr.getMapleClient().getMapleChannelInstance().getPort()));
    }
}
