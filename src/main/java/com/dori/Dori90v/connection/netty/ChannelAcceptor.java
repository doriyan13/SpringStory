package com.dori.Dori90v.connection.netty;

import com.dori.Dori90v.world.MapleChannel;
import com.dori.Dori90v.logger.Logger;

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
