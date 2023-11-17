package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.character.Skill;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CField;
import com.dori.SpringStory.connection.packet.packets.CWvsContext;
import com.dori.SpringStory.enums.AccountType;
import com.dori.SpringStory.enums.AdminCommandTypes;
import com.dori.SpringStory.enums.ChatType;
import com.dori.SpringStory.enums.Stat;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.world.fieldEntities.mob.Mob;
import com.dori.SpringStory.dataHandlers.MobDataHandler;
import com.dori.SpringStory.dataHandlers.SkillDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.MobData;
import com.dori.SpringStory.dataHandlers.dataEntities.SkillData;

import static com.dori.SpringStory.connection.packet.headers.InHeader.Admin;

public class AdminHandler {
    // Logger -
    private static final Logger logger = new Logger(AdminHandler.class);

    @Handler(op = Admin)
    public static void handleAdmin(MapleClient c, InPacket inPacket) {
        // Admin packets are for admin commands that maple gm had access to
        byte cmdType = inPacket.decodeByte();
        boolean adminAccount = c.getAccount().getAccountType() == AccountType.Admin || c.getAccount().getAccountType() == AccountType.GameMaster;
        MapleChar chr = c.getChr();
        AdminCommandTypes adminCmdType = AdminCommandTypes.getCommandByFlag(cmdType);
        if(adminAccount) {
            switch (adminCmdType){
                case CREATE_ITEM -> {
                    int itemID = inPacket.decodeInt();// the item id to create and add
                }
                case DELETE_ITEM -> {
                    byte invTypeToClear = inPacket.decodeByte(); // the inventory ID to clear
                }
                case GAIN_EXP -> {
                    int amountOfExpToAdd = inPacket.decodeInt();
                    if (amountOfExpToAdd > 0) {
                        chr.gainExp(amountOfExpToAdd);
                    }
                }
                case BAN -> {
                    String chrNameToBan = inPacket.decodeString();
                    // Need to handle ban a chr and close his connection (pretty sure there is a packet for it)
                }
                case BLOCK -> {
                    String chrNameToBlock = inPacket.decodeString();
                    byte reason = inPacket.decodeByte();
                    int durationInDays = inPacket.decodeInt();
                    String type = inPacket.decodeString(); // HACK/BOT/AD/HARASS/CURSE/SCAM/MISCONDUCT/SELL/ICASH/TEMP/GM/IPROGRAM/MEGAPHONE
                }
                case WARN -> {
                    String chrName = inPacket.decodeString();
                    String warningMsg = inPacket.decodeString();
                    //serverNotice type 1, to send into that char?
                    boolean successfullySendWarn = false;
                    chr.write(CField.adminResult(successfullySendWarn ? 4 : 5,false));
                }
                case IDK_3 -> {
                    inPacket.decodeByte();
                    inPacket.decodeString();
                    if(inPacket.getUnreadAmount() > 0) {
                        inPacket.decodeString();
                        inPacket.decodeString();
                    }
                }
                case HIDE -> {
                    boolean hidden = inPacket.decodeBool();
                    chr.write(CField.adminResult(AdminCommandTypes.HIDE.getCmdFlag(), hidden));
                }
                case GET_USERS_IN_FIELD -> {
                    inPacket.decodeByte(); // seems to be sent always as 0 ? | IDK
                }
                case U_CLIP -> {
                    inPacket.decodeByte(); // seems to be always 0xF ?
                    // need to figure out what it does?
                }
                case KILL -> {
                    String idk1 = inPacket.decodeString(); // mobToKill?
                    String idk2 = inPacket.decodeString(); // Amount?
                }
                case QUEST_RESET -> {
                    short questID = inPacket.decodeShort();
                    // need to remove quest completion
                }
                case QUEST -> {
                    int idk = inPacket.decodeInt(); // maybe quest id?
                    int idk2 = inPacket.decodeInt(); // maybe chr id?
                }
                case SUMMON -> {
                    int mobID = inPacket.decodeInt();
                    int amount = inPacket.decodeInt();
                    MobData mobData = MobDataHandler.getMobDataByID(mobID);
                    if (mobData != null) {
                        for (int i = 0; i < amount; i++) {
                            Field field = chr.getField();
                            Mob mob = MobDataHandler.getMobByID(mobID);
                            if (mob != null) {
                                Position pos = chr.getPosition();
                                mob.setPosition(pos.deepCopy());
                                mob.setVPosition(pos.deepCopy());
                                mob.setHomePosition(pos.deepCopy());
                                mob.setFh(chr.getFoothold());
                                mob.setHomeFh(chr.getFoothold());
                                mob.setRespawnable(false);
                                mob.setField(field);
                                field.spawnMob(mob, chr);
                            }
                        }
                    }
                }
                case MOB_HP -> {
                    int mobID = inPacket.decodeInt();
                    Mob mob = chr.getField().getMobs().get(mobID);
                    if (mob != null) {
                        chr.message(mob.toString(), ChatType.SpeakerWorld);
                    }
                }
                case SET_JOB -> {
                    chr.setJob(inPacket.decodeInt());
                }
                case ADD_AP -> chr.gainAP(inPacket.decodeInt());
                case ADD_SP -> chr.gainSP(inPacket.decodeInt());
                case SET_STR -> {
                    int newStr = inPacket.decodeInt();
                    if(newStr > 0 && newStr <= Short.MAX_VALUE) {
                        chr.setNStr(newStr);
                        chr.updateStat(Stat.Str, newStr);
                    }
                }
                case SET_DEX -> {
                    int newDex = inPacket.decodeInt();
                    if(newDex > 0 && newDex <= Short.MAX_VALUE) {
                        chr.setNDex(newDex);
                        chr.updateStat(Stat.Dex, newDex);
                    }
                }
                case SET_INT -> {
                    int newInt = inPacket.decodeInt();
                    if(newInt > 0 && newInt <= Short.MAX_VALUE) {
                        chr.setNInt(newInt);
                        chr.updateStat(Stat.Inte, newInt);
                    }
                }
                case SET_LUK -> {
                    int newLuk = inPacket.decodeInt();
                    if(newLuk > 0 && newLuk <= Short.MAX_VALUE) {
                        chr.setNLuk(newLuk);
                        chr.updateStat(Stat.Luk, newLuk);
                    }
                }
                case SKILL -> {
                    int skillID = inPacket.decodeInt();
                    int slv = inPacket.decodeInt();
                    SkillData skillData = SkillDataHandler.getSkillDataByID(skillID);
                    if (skillData != null && slv > 0) {
                        Skill skill = new Skill(skillData);
                        skill.setCurrentLevel(slv);
                        chr.addSkill(skill);
                        chr.write(CWvsContext.changeSkillRecordResult(chr.getSkills(), true, true));
                    }
                }
                case ARTIFACT_RANKING -> {
                    //TODO: need to implement after i implement rankings
                }
                case PQ_ANSWER -> {
                    //TODO: need to implement after handling PQs
                }
                case SET_LVL -> {
                    int lvl = inPacket.decodeByte();
                    int amountOfLevels = lvl - chr.getLevel();
                    if (amountOfLevels > 0) {
                        chr.lvlUp(amountOfLevels);
                    } else {
                        chr.setLevel(lvl);
                        chr.updateStat(Stat.Level, lvl);
                        chr.fullHeal();
                    }
                }
                case PARTY_CHECK -> {
                    inPacket.decodeString(); // party name i think?
                    // TODO when i handle parties
                }
                case PSD_VIEW -> {
                    inPacket.decodeByte(); // idk?
                }
                case PSD_SET -> {
                    inPacket.decodeString(); //stat name?
                    inPacket.decodeInt(); // amount
                }
                case PSD_UPDATE -> {
                    //TODO: idk?
                }
                case GET_COOLDOWN -> {
                    inPacket.decodeInt(); // skillID that need to get his cooldown
                    //TODO: when i handle cooldowns
                }
                default -> logger.warning("Unhandled admin command type: " + cmdType);
            }
        }
    }
}
