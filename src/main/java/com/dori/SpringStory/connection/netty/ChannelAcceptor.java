package com.dori.SpringStory.connection.netty;

import com.dori.SpringStory.world.MapleChannel;
import com.dori.SpringStory.logger.Logger;

public class ChannelAcceptor implements Runnable{
    // Channel -
    public MapleChannel mapleChannel;
    // Logger -
    private static final Logger logger = new Logger(ChannelAcceptor.class);
    @Override
    public void run() {
        // Execute a ServerBootStrap event looper for that channel -
        BaseAcceptor.createAcceptor(mapleChannel.getPort(), null, mapleChannel, logger);
    }
}
