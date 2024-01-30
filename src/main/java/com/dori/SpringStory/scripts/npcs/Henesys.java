package com.dori.SpringStory.scripts.npcs;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.scripts.api.NpcScript;
import com.dori.SpringStory.scripts.api.ScriptApi;

public class Henesys {
    // Town ID - 100000000
    @NpcScript(id = 1012000)
    public static ScriptApi handleTaxi(MapleChar chr) {
        ScriptApi test = new ScriptApi();
        test.sayNext("Hey")
                .sayNext("This is a test")
                .askYesNo("are you ready?", result -> {
                    if (result) {
                        test.askMenu(
                                test.addMenuOption("first option",
                                        () -> test.askNumber("choose a num between 1 - 10", 1, 10,
                                                (answer) -> test.sayOK("DONE"))
                                ),
                                test.addMenuOption("second",
                                        () -> test.sayNext("didn't want to end")
                                                .sayOK("Bye again!"))
                        );
                    } else {
                        test.sayOK("Bye");
                    }
                });
        return test;
    }
}
