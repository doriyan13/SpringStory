package com.dori.SpringStory.scripts.npcs;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.dataHandlers.CharacterCosmeticsDataHandler;
import com.dori.SpringStory.enums.StringDataType;
import com.dori.SpringStory.scripts.api.MenuOption;
import com.dori.SpringStory.scripts.api.NpcScript;
import com.dori.SpringStory.scripts.api.ScriptApi;
import com.dori.SpringStory.utils.NpcMessageUtils;
import com.dori.SpringStory.utils.NpcScriptUtils;

import java.util.*;

import static com.dori.SpringStory.dataHandlers.CharacterCosmeticsDataHandler.getBlackColorFace;
import static com.dori.SpringStory.dataHandlers.CharacterCosmeticsDataHandler.getBlackColorHair;
import static com.dori.SpringStory.utils.NpcScriptUtils.*;

public class GlobalScripts {

    // Regular Cab in Victoria
    // Text for this script is 100% GMS-like
    @NpcScript(id = 1012000)
    public static ScriptApi handleTaxi(MapleChar chr) {
        ScriptApi script = new ScriptApi();
        String npcName = NpcMessageUtils.npcName(1012000);
        script.sayNext("Hello! I'm ").blue(npcName)
                .addMsg(", and I am here to take you to your destination quickly and safely.").blue(npcName)
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

        script.askMenu("Please select your destination.", menuOptions);
        return script;
    }

    @NpcScript(id = 9401769)
    public static ScriptApi handleAqua(MapleChar chr) {
        ScriptApi script = new ScriptApi();
        script.sayNext("Hello! I'm Aqua the goddess, and i dream to be a ").blue("stylist ").addMsg(":D")
                .askMenu("What do you want to do today?",
                        script.addMenuOption("Change your hairstyle", () -> script.askAvatarHair("Choose new hair -", CharacterCosmeticsDataHandler.getAllUniqueHairs())),
                        script.addMenuOption("Change your hairstyle color", () -> script.askAvatarHair("Choose new hair color -", getListOfColoredHairs(getBlackColorHair(chr.getHair())))),
                        script.addMenuOption("Change your eyes", () -> script.askAvatarFace("Choose new eyes -", CharacterCosmeticsDataHandler.getAllUniqueFaces())),
                        script.addMenuOption("Change your eyes color", () -> script.askAvatarFace("Choose new eyes color -", getListOfColoredFaces(getBlackColorFace(chr.getFace())))),
                        script.addMenuOption("Change your skin color", () -> script.askAvatarSkin("Choose new skin -", CharacterCosmeticsDataHandler.getAllUniqueSkins()))
                );
        return script;
    }

    @NpcScript(id = 9401771)
    public static ScriptApi handleDarkness(MapleChar chr) {
        ScriptApi script = new ScriptApi();

        script.sayNext("Hello! I'm Darkness, and i'm the ").purple( "knowledge one ").addMsg(";D")
                .askMenu("What type of info you want to search?",
                        script.addMenuOption("Item", () -> script.askText("Write the Item name you want to get - ", "", 1, 20,
                                (name) -> searchWzDataByName(chr, script, StringDataType.Item, name))
                        ),
                        script.addMenuOption("Map", () -> script.askText("Write the Map name you want to go to - ", "", 1, 20,
                                (name) -> searchWzDataByName(chr, script, StringDataType.Map, name))
                        ),
                        script.addMenuOption("Mob", () -> script.askText("Write the Mob name you want to spawn - ", "", 1, 20,
                                (name) -> searchWzDataByName(chr, script, StringDataType.Mob, name))
                        ),
                        script.addMenuOption("Npc", () -> script.askText("Write the Npc you want to talk to - ", "", 1, 20,
                                (name) -> searchWzDataByName(chr, script, StringDataType.Npc, name))
                        ),
                        script.addMenuOption("Skill", () -> script.askText("Write the name you want to find - ", "", 1, 20,
                                (name) -> searchWzDataByName(chr, script, StringDataType.Skill, name))
                        )
                );
        return script;
    }

    @NpcScript(id = 9401770)
    public static ScriptApi handleMegumin(MapleChar chr) {
        ScriptApi script = new ScriptApi();

        script.sayNext("Hello! I'm Megumin")
                .addNewLine("And i like EXPLOOOOOOOOOOOSION! ").red("<3");
        return script;
    }

    @NpcScript(id = 9401768)
    public static ScriptApi handleKazuma(MapleChar chr) {
        ScriptApi script = new ScriptApi();

        script.sayNext("Hello! I'm Kazuma")
                .addNewLine("pantsu ;D ").red("<3");
        return script;
    }
}
