package com.dori.SpringStory.connection.netty;

import com.dori.SpringStory.logger.Logger;
import io.netty.channel.*;
import java.util.HashMap;
import java.util.Map;

import static com.dori.SpringStory.constants.ServerConstants.CHAT_PORT;

// Taken from http://netty.io/wiki/user-guide-for-4.x.html
public class ChatAcceptor implements Runnable{
    // Channel pool -
    public static Map<String, Channel> channelPool = new HashMap<>();
    // Logger -
    private static final Logger logger = new Logger(ChatAcceptor.class);
    @Override
    public void run() {
        // Execute a ServerBootStrap event looper for Chat port -
        BaseAcceptor.createAcceptor(CHAT_PORT, channelPool,null, logger);
    }
}
