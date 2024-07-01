package com.dori.SpringStory.client.commands;

import com.dori.SpringStory.Server;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.enums.AccountType;
import com.dori.SpringStory.enums.ChatType;
import com.dori.SpringStory.enums.Stat;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.world.MapleWorld;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerCommands {
    // Logger -
    private static final Logger logger = new Logger(PlayerCommands.class);

    private static PlayerCommands instance;

    private PlayerCommands() {
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static PlayerCommands getInstance() {
        if (instance == null) {
            instance = new PlayerCommands();
        }
        return instance;
    }

    @Command(names = {"help"}, requiredPermission = AccountType.Player)
    public static void help(MapleChar chr, List<String> args) {
        logger.debug("Testing!");
        logger.debug("chr: " + chr.getId());
        logger.debug("args: " + args.toString());
    }

    @Command(names = {"dispose", "ea"}, requiredPermission = AccountType.Player)
    public static void dispose(MapleChar chr, List<String> args) {
        chr.enableAction();
        chr.message("You've been disposed <3", ChatType.GameDesc);
    }

    @Command(names = {"info", "status"}, requiredPermission = AccountType.Player)
    public static void info(MapleChar chr, List<String> args) {
        chr.message("Name: " + chr.getName(), ChatType.SpeakerWorld);
        chr.message("Lvl: " + chr.getLevel() + " | Job: " + chr.getJob() + " | Exp: " + chr.getExp(), ChatType.SpeakerWorld);
        chr.message("Hp: " + chr.getHp() + " / " + chr.getTotalStat(Stat.MaxHp) + " | " + "Mp: " + chr.getMp() + " / " + chr.getTotalStat(Stat.MaxMp), ChatType.SpeakerWorld);
        chr.message("Str: " + chr.getNStr() + " | Dex: " + chr.getNDex() + " | Int: " + chr.getNInt() + " | Luk: " + chr.getNLuk(), ChatType.SpeakerWorld);
        chr.message("Field: " + chr.getField().getId() + " | Pos:" + chr.getPosition() + " | Fh: " + chr.getFoothold(), ChatType.SpeakerWorld);
    }

    @Command(names = {"online"}, requiredPermission = AccountType.Player)
    public static void online(MapleChar chr, List<String> args) {
        MapleWorld world = Server.getWorldById(chr.getMapleClient().getWorldId());
        chr.message("<-------------------------------------------->", ChatType.SpeakerWorld);
        world.getChannelList().forEach(channel -> {
                    Map<String, Integer> onlinePlayers = channel.getFields()
                            .values()
                            .stream()
                            .flatMap(field -> field.getPlayers()
                                    .values()
                                    .stream()
                                    .map(player -> Map.entry(player.getName(), field.getId())))
                            .collect(HashMap::new,
                                    (OnlinePlayersFields, entry) -> OnlinePlayersFields.put(entry.getKey(), entry.getValue()),
                                    HashMap::putAll);
                    chr.message("Channel " + channel.getChannelId() + " : " + onlinePlayers.size(), ChatType.SpeakerWorld);
                    onlinePlayers.forEach((name, fieldID) -> chr.message(name + " -> " + fieldID, ChatType.SpeakerWorld));
                }
        );
        chr.message("<-------------------------------------------->", ChatType.SpeakerWorld);
    }
}
