package com.dori.SpringStory.scripts.npcs;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.scripts.api.NpcScript;
import com.dori.SpringStory.scripts.api.ScriptApi;
import com.dori.SpringStory.utils.NpcScriptUtils;

import static com.dori.SpringStory.enums.Job.Beginner;

public class Henesys {
    // Town ID - 100000000
    @NpcScript(id = 1012000)
    public static ScriptApi handleTaxi(MapleChar chr) {

        ScriptApi script = new ScriptApi();
        boolean beginner = chr.getJob() == Beginner.getId();
        script.sayNext("Hello, I drive the Regular Cab. If you want to go from town to town safely and fast, then ride our cab. We'll glady take you to your destination with an affordable price.");
        if (beginner) {
            script.askMenu("We have a special 90% discount for beginners.",
                            NpcScriptUtils.addTaxiMoveOption(script,chr,104000000,chr.getJob() == 0, 1000),
                            NpcScriptUtils.addTaxiMoveOption(script,chr,102000000,chr.getJob() == 0, 1000),
                            NpcScriptUtils.addTaxiMoveOption(script,chr,101000000,chr.getJob() == 0, 800),
                            NpcScriptUtils.addTaxiMoveOption(script,chr,103000000,chr.getJob() == 0, 1000),
                            NpcScriptUtils.addTaxiMoveOption(script,chr,120000000,chr.getJob() == 0, 800)
                            );
        }
        return script;
    }
}
