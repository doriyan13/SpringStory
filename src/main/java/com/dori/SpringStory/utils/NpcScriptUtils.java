package com.dori.SpringStory.utils;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.packets.CWvsContext;
import com.dori.SpringStory.scripts.api.MenuOption;
import com.dori.SpringStory.scripts.api.ScriptApi;

public interface NpcScriptUtils {

    // TODO: Find GMS-like text for this
    static MenuOption addTaxiMoveOption(ScriptApi script, MapleChar chr, int mapId, boolean beginner, int cost) {
        int finalCost = beginner ? cost / 10 : cost;
        String mapName = NpcMessageUtils.mapName(mapId);
        return script.addMenuOption(NpcMessageUtils.blue(mapName + " (" + (finalCost) + " Mesos)"), () -> {
            script.askYesNo("You don't have anything else to do here, huh? Do you really want to go to "
                    + NpcMessageUtils.blue(mapName) + "? It'll cost you " + NpcMessageUtils.blue(finalCost + " mesos."), response -> {
                if (response) {
                    if (chr.getMeso() >= finalCost) {
                        chr.modifyMeso(-finalCost, true);
                        chr.warp(mapId);
                    } else {
                        script.sayOK("You don't have enough mesos. Sorry to say this, but without them, you won't be able to ride the cab.");
                    }
                } else {
                    script.sayOK("There's a lot to see in this town, too. Come back and find us when you need to go to a different town.");
                }
            });
        });
    }
}
