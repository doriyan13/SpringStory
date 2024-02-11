package com.dori.SpringStory.scripts.npcs;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.scripts.api.MenuOption;
import com.dori.SpringStory.scripts.api.NpcScript;
import com.dori.SpringStory.scripts.api.ScriptApi;
import com.dori.SpringStory.utils.NpcMessageUtils;
import com.dori.SpringStory.utils.NpcScriptUtils;

import java.util.*;

public class Common {

    // Regular Cab in Victoria
    // Text for this script is 100% GMS-like
    @NpcScript(id = 1012000)
    public static ScriptApi handleTaxi(MapleChar chr) {
        ScriptApi script = new ScriptApi();
        String npcName = NpcMessageUtils.npcName(1012000);
        script.sayNext("Hello! I'm")
                .blue(npcName)
                .addMsg(", and I am here to take you to your destination quickly and safely.")
                .blue(npcName)
                .addMsg(" values your satisfaction, so you can always reach your destination at an affordable price.")
                .addMsg(" I am here to serve you.");

        // Generate a list of destinations the player can go to
        List<Integer> taxiMaps = Arrays.asList(100000000, 101000000, 102000000, 103000000, 104000000, 105000000, 120000000);
        List<MenuOption> menuOptions = new ArrayList<>();
        for (Integer mapId : taxiMaps) {
            if (chr.getMapId() != mapId) {
                // Once big bang hit, all taxi options cost 1,000 meso
                menuOptions.add(NpcScriptUtils.addTaxiMoveOption(script, chr, mapId, chr.getJob() == 0, 1000));
            }
        }

        script.askMenu("Please select your destination.\r\n", menuOptions.toArray(MenuOption[]::new));
        return script;
    }
}
