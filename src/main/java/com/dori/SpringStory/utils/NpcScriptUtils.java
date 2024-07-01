package com.dori.SpringStory.utils;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.dataHandlers.ItemDataHandler;
import com.dori.SpringStory.dataHandlers.MobDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.MobData;
import com.dori.SpringStory.dataHandlers.dataEntities.StringData;
import com.dori.SpringStory.enums.StringDataType;
import com.dori.SpringStory.scripts.api.MenuOption;
import com.dori.SpringStory.scripts.api.ScriptApi;
import com.dori.SpringStory.scripts.handlers.ScriptHandler;
import com.dori.SpringStory.services.StringDataService;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    static List<Integer> getListOfColoredHairs(int hairID) {
        List<Integer> listOfAllColors = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            listOfAllColors.add(hairID + i);
        }
        return listOfAllColors;
    }

    static List<Integer> getListOfColoredFaces(int faceID) {
        List<Integer> listOfAllColors = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            listOfAllColors.add(faceID + (i * 100));
        }
        return listOfAllColors;
    }

    private static void actionToChrByType(MapleChar chr,
                                          StringDataType type,
                                          long id) {
        switch (type) {
            case Npc -> ScriptHandler.getInstance().handleNpcScript(chr, (int) id);
            case Map -> chr.warp((int) id);
            case Item -> chr.addItem((int) id, 1);
            case Mob -> chr.getField().spawnMobById((int) id, chr);
            // Maybe in the future i can also do add skill to player and such?
        }
        ;
    }

    private static String generateDisplayByType(StringDataType type,
                                                int id) {
        return switch (type) {
            case Npc -> NpcMessageUtils.npcName(id);
            case Map -> NpcMessageUtils.mapName(id);
            case Item -> ItemDataHandler.getItemDataByID(id) != null || ItemDataHandler.getEquipDataByID(id) != null
                    ? NpcMessageUtils.itemImage(id) : null;
            case Mob -> MobDataHandler.getMobDataByID(id) instanceof MobData mobData && mobData.isDisplayable()
                    ? NpcMessageUtils.wzImage("Mob/" + (id /1_000_000 == 0 ? "0" : "")  + id + ".img/stand/0") : null;
            case Skill -> NpcMessageUtils.skillImage(id);
            case null, default -> "";
        };
    }

    static void searchWzDataByName(@NotNull MapleChar chr,
                                   @NotNull ScriptApi script,
                                   @NotNull StringDataType type,
                                   @NotNull String name) {
        Optional<List<StringData>> results = StringDataService.getInstance().findStringByNameAndType(name, type);
        results.ifPresent(resultsData -> {
                    List<MenuOption> menuOptions = resultsData
                            .stream()
                            .map(entity -> {
                                        String entry = generateDisplayByType(type, (int) entity.getId());
                                        if (entry != null && !entry.isEmpty()) {
                                            return script.addMenuOption(entry + " - " + NpcMessageUtils.blue(entity.toString()),
                                                    () -> actionToChrByType(chr, type, entity.getId()));
                                        }
                                        return null;
                                    })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    script.askMenu("Query Results - ", menuOptions);
                }
        );
    }
}
