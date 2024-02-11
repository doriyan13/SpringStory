package com.dori.SpringStory.utils;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.scripts.api.MenuOption;
import com.dori.SpringStory.scripts.api.ScriptApi;

public interface NpcScriptUtils {

    static MenuOption addTaxiMoveOption(ScriptApi script,
                                        MapleChar chr,
                                        int mapId,
                                        boolean beginner,
                                        int cost) {
        int finalCost = beginner ? cost / 10 : cost;
        String mapName = NpcMessageUtils.mapName(mapId);
        return script.addMenuOption( mapName + "(" + (finalCost) + " mesos)", () -> {
            script.askYesNo("You don't have anything else to do here, huh? Do you really want to go to "
                    + NpcMessageUtils.bold(mapName) + "? It'll cost you " + NpcMessageUtils.bold(finalCost) + " mesos.", response -> {
                if (response) {
                    if (chr.getMeso() - finalCost >= 0) {
                        chr.modifyMeso(-finalCost);
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
