package com.dori.SpringStory.events.eventsHandlers;

import com.dori.SpringStory.Server;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.enums.ChatType;

public record ValidateChrTempStatsEvent(MapleChar chr)
        implements Runnable{
    @Override
    public void run() {
        if(Server.isChrOnline(chr.getId())){
            chr.resetTemporaryStats();
        }
    }
}
