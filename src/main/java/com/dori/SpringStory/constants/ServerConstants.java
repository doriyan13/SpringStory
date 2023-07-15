package com.dori.SpringStory.constants;

import com.dori.SpringStory.logger.LoggerLevels;

public interface ServerConstants {
    // Logger -
    Integer LOG_LVL = Integer.parseInt(System.getenv().getOrDefault("LOG_LVL", "" + LoggerLevels.DEBUG.getLvl()));
    // IPs -
    String HOST_IP = "127.0.0.1";
    // Ports -
    int LOGIN_PORT = 8484;
    int CHAT_PORT = 8483;
    // Maple Version stuff -
    short VERSION = 95;
    String MINOR_VERSION = "1";
    byte LOCALE = 8;
    // Maple World stuff -
    byte DEFAULT_WORLD_ID = 0; //1- Bera | 0 - Scania
    int CHANNELS_PER_WORLD = 1;
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
    String WZ_DIR = DIR + "/wz";
    // WZ reading stuff -
    int AMOUNT_OF_LOADERS = 5;
    int MAX_LOADING_TIME_IN_MIN = 2;
    boolean PRINT_WZ_UNK = System.getenv("PRINT_WZ_UNK") != null && Boolean.getBoolean(System.getenv("PRINT_WZ_UNK"));
    String MAP_WZ_DIR = ServerConstants.WZ_DIR + "/Map.wz/Map";
    String WORLD_MAP_WZ_DIR = ServerConstants.WZ_DIR + "/Map.wz/WorldMap";
    String EQUIP_BASE_WZ_DIR = ServerConstants.WZ_DIR + "/Character.wz";
    String SKILL_WZ_DIR = ServerConstants.WZ_DIR + "/Skill.wz";
    String[] EQUIP_SUB_WZ_DIR = new String[]{"Accessory", "Cap", "Cape", "Coat", "Dragon", "Face", "Glove",
            "Hair", "Longcoat", "Pants", "PetEquip", "Ring", "Shield", "Shoes", "Weapon", "MonsterBook"};
    String ITEM_BASE_WZ_DIR = ServerConstants.WZ_DIR + "/Item.wz";
    String[] ITEM_SUB_WZ_DIRS = new String[]{"Cash", "Consume", "Etc", "Install", "Special"}; // exclude Pets - will be handled separately
    String PET_WZ_DIR = ServerConstants.WZ_DIR + "/Item.wz/Pet";
    // Life stuff -
    int MAX_RETRIES = 10_000;
    int MAX_OBJ_ID = 9_999_999;
    int MIN_OBJ_ID = 1_000_000;
    // Commands stuff -
    String COMMANDS_DIR = DIR + "/src/main/java/com/dori/SpringStory/client/commands";
}
