package com.dori.SpringStory.scripts.npcs;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.dataHandlers.CharacterCosmeticsDataHandler;
import com.dori.SpringStory.scripts.api.NpcScript;
import com.dori.SpringStory.scripts.api.ScriptApi;

public class Henesys {
    @NpcScript(id = 9300010)
    public static ScriptApi handleMrMoneybags(MapleChar chr) {
        ScriptApi script = new ScriptApi();
        script.sayNext("Test Hair:")
                .askAvatarLook("Choose hair -", CharacterCosmeticsDataHandler.getAllUniqueHairs());
        return script;
    }
}
