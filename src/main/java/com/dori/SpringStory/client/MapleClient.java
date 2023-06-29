package com.dori.SpringStory.client;

import com.dori.SpringStory.client.character.MapleAccount;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.netty.NettyClient;
import com.dori.SpringStory.world.MapleChannel;
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
}
