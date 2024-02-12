package com.dori.SpringStory.scripts.npcs;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.enums.Skills;
import com.dori.SpringStory.scripts.api.NpcScript;
import com.dori.SpringStory.scripts.api.ScriptApi;

public class Henesys {
    @NpcScript(id = 9300010)
    public static ScriptApi handleMrMoneybags(MapleChar chr) {
        ScriptApi script = new ScriptApi();
        script.sayOK("Hello")
                .addNewLine("This is a new ").purple("Line")
                .addNewLine("This is the thirdLine! ").addMsg("see skill: ").skillImage(Skills.THIEFMASTER_CHAKRA.getId())
                .addNewLine("This is the ").red("4th").addMsg(" Line, see item: ").itemImage(1002959);
        return script;
    }
}
