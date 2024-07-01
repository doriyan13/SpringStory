package com.dori.SpringStory.constants;

import com.dori.SpringStory.logger.LoggerLevels;

public interface ServerConstants {
    // Logger -
    Integer LOG_LVL = Integer.parseInt(System.getenv().getOrDefault("LOG_LVL", "" + LoggerLevels.DEBUG.getLvl()));
    // IPs -
    String HOST_IP = "127.0.0.1";
    // Ports -
    int LOGIN_PORT = 8484;
    int CHANNEL_PORT_BASE = 8486;
    int CHAT_PORT = 8483;
    // Shutdown -
    long MAX_SHUTDOWN_TIME_IN_MIN = 1;
    // Encryption -
    boolean ENABLE_ENCRYPTION = Boolean.parseBoolean(System.getenv().getOrDefault("ENABLE_ENCRYPTION", "true"));
    // Maple Version stuff -
    short VERSION = 95;
    String MINOR_VERSION = "1";
    byte LOCALE = 8;
    // Maple World stuff -
    byte DEFAULT_WORLD_ID = 0; //1- Bera | 0 - Scania
    int CHANNELS_PER_WORLD = 2;
    int WORLD_EVENT_EXP_WSE = 100;
    int WORLD_EVENT_DROP_WSE = 100;
    String WORLD_NAME = "Dori";
    String EVENT_MSG = "SpringStory";
    String RECOMMEND_MSG = "I recommend this world to you";
    String[] WORLD_SELECT_BACKGROUND_IMAGES = {"effect","ghostShip","dragonRider","adventure","golden","signboard","dualBlade","201007","dual","visitors"};
    // Auto Login stuff - (security handle)
    boolean AUTO_LOGIN = true;
    String AUTO_LOGIN_USERNAME = "admin";
    String AUTO_LOGIN_PASSWORD = "admin";
    // Dir reading / reflection stuff -
    String DIR = System.getProperty("user.dir");
    String HANDLERS_DIR = DIR + "/src/main/java/com/dori/SpringStory/connection/packet/handlers";
    String NPC_SCRIPTS_DIR = DIR + "/src/main/java/com/dori/SpringStory/scripts/npcs";
    String QUEST_SCRIPTS_DIR = DIR + "/src/main/java/com/dori/SpringStory/scripts/quests";
    String WZ_DIR = DIR + "/wz";
    // WZ reading stuff -
    int AMOUNT_OF_LOADERS = 9;
    int MAX_LOADING_TIME_IN_MIN = 1;
    boolean PRINT_WZ_UNK = Boolean.getBoolean(System.getenv().getOrDefault("PRINT_WZ_UNK","true"));
    String MAP_WZ_DIR = ServerConstants.WZ_DIR + "/Map.wz/Map";
    String WORLD_MAP_WZ_DIR = ServerConstants.WZ_DIR + "/Map.wz/WorldMap";
    String EQUIP_BASE_WZ_DIR = ServerConstants.WZ_DIR + "/Character.wz";
    String SKILL_WZ_DIR = ServerConstants.WZ_DIR + "/Skill.wz";
    String[] EQUIP_SUB_WZ_DIR = new String[]{"Accessory", "Cap", "Cape", "Coat", "Dragon", "Face", "Glove",
            "Hair", "Longcoat", "Pants", "PetEquip", "Ring", "Shield", "Shoes", "Weapon", "MonsterBook"};
    String ITEM_BASE_WZ_DIR = ServerConstants.WZ_DIR + "/Item.wz";
    String[] ITEM_SUB_WZ_DIRS = new String[]{"Cash", "Consume", "Etc", "Install", "Special"}; // exclude Pets - will be handled separately
    String PET_WZ_DIR = ServerConstants.WZ_DIR + "/Item.wz/Pet";
    String STRING_WZ_DIR = WZ_DIR + "/String.wz/";
    String MOB_WZ_DIR = WZ_DIR + "/Mob.wz/";
    String NPC_WZ_DIR = WZ_DIR + "/Npc.wz/";
    String CHARACTER_WZ_DIR = WZ_DIR + "/Character.wz/";
    String HAIR_WZ_DIR = CHARACTER_WZ_DIR + "/Hair/";
    String FACE_WZ_DIR = CHARACTER_WZ_DIR + "/Face/";
    String JSON_DIR = DIR + "/json";
    // Image reading stuff -
    String IMG_DIR = System.getenv().getOrDefault("IMG_DIR", DIR + "/imgs");
    // Map Data -
    String MAP_JSON_DIR = JSON_DIR + "/Map/";
    String WORLD_MAP_JSON_DIR = JSON_DIR + "/WorldMap/";
    String WORLD_MAP_JSON_FILE = "worldMaps.json";
    // Item Data -
    String ITEM_JSON_DIR = JSON_DIR + "/Item/";
    String ITEM_OPTION_JSON_DIR = JSON_DIR + "/ItemOptions/";
    String EQUIP_JSON_DIR = JSON_DIR + "/Equip/";
    // Skill Data -
    String SKILL_JSON_DIR = JSON_DIR + "/Skill/";
    // Mob Data -
    String MOB_JSON_DIR = JSON_DIR + "/Mob/";
    // Buffs Data -
    String BUFF_JSON_DIR = JSON_DIR + "/Buff/";
    // Mob Drops -
    String MOB_DROP_JSON_DIR = JSON_DIR + "/MobDrops/";
    // Npc Data -
    String NPC_JSON_DIR = JSON_DIR + "/Npc/";
    // Character Cosmetics Data -
    String CHARACTER_COSMETICS_JSON_DIR = JSON_DIR + "/CharacterCosmetics/";
    int MOB_DROP_DEFAULT_MIN_QUANTITY = 1;
    int MOB_DROP_DEFAULT_MAX_QUANTITY = 1;
    double MOB_DROP_DEFAULT_CHANCE = 0.01;
    // Quest Data -
    String QUEST = "/Quest/";
    String QUEST_IMG_DIR = IMG_DIR + QUEST;
    String QUEST_INFO_IMG = "QuestInfo.img";
    String QUEST_ACT_IMG = "Act.img";
    String QUEST_CHECK_IMG = "Check.img";
    String QUEST_JSON_DIR = JSON_DIR + QUEST;
    // Commands stuff -
    String COMMANDS_DIR = DIR + "/src/main/java/com/dori/SpringStory/client/commands";
    // Field -
    int MAX_OBJECT_ID_ALLOCATED_TO_FIELD = 30_000;
    int DROP_REMAIN_ON_GROUND_TIME = 120; // 2 minutes
    int FIELD_DEPRECATION_TIME_IN_MIN = 15;
}
