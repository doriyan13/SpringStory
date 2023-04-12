package com.dori.Dori90v.connection.netty;

import com.dori.Dori90v.logger.Logger;
import io.netty.channel.*;
import java.util.HashMap;
import java.util.Map;

import static com.dori.Dori90v.constants.ServerConstants.LOGIN_PORT;

// Taken from http://netty.io/wiki/user-guide-for-4.x.html
public class LoginAcceptor implements Runnable{
    // Channel pool -
    public static Map<String, Channel> channelPool = new HashMap<>();
    // Logger -
    private static final Logger logger = new Logger(LoginAcceptor.class);
    @Override
    public void run() {
        // Execute a ServerBootStrap event looper for Login port -
        BaseAcceptor.createAcceptor(LOGIN_PORT, channelPool,null, logger);
    }
}
