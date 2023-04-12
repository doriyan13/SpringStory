package com.dori.Dori90v.client;

import com.dori.Dori90v.client.character.MapleAccount;
import com.dori.Dori90v.client.character.MapleChar;
import com.dori.Dori90v.connection.netty.NettyClient;
import com.dori.Dori90v.world.MapleChannel;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
