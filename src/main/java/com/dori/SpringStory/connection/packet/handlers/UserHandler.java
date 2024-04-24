package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.Server;
import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.character.Skill;
import com.dori.SpringStory.client.character.attack.AttackInfo;
import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.client.effects.parsers.SkillUseEffect;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.headers.InHeader;
import com.dori.SpringStory.connection.packet.packets.CUserRemote;
import com.dori.SpringStory.connection.packet.packets.CWvsContext;
import com.dori.SpringStory.enums.*;
import com.dori.SpringStory.jobs.handlers.MagicianHandler;
import com.dori.SpringStory.jobs.handlers.WarriorHandler;
import com.dori.SpringStory.temporaryStats.characters.CharacterTemporaryStat;
import com.dori.SpringStory.utils.FormulaCalcUtils;
import com.dori.SpringStory.utils.JobUtils;
import com.dori.SpringStory.utils.SkillUtils;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.MapleChannel;
import com.dori.SpringStory.world.fieldEntities.Drop;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.world.fieldEntities.Portal;
import com.dori.SpringStory.world.fieldEntities.movement.MovementData;
import com.dori.SpringStory.dataHandlers.SkillDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.SkillData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.dori.SpringStory.connection.packet.headers.InHeader.*;
import static com.dori.SpringStory.constants.GameConstants.QUICK_SLOT_LENGTH;
import static com.dori.SpringStory.enums.AttackType.*;
import static com.dori.SpringStory.enums.Skills.*;
import static com.dori.SpringStory.utils.FuncKeyMapUtils.handleKeyModifiedToChr;

public class UserHandler {
    @Handler(op = UserMove)
    public static void handleUserMove(MapleClient c, InPacket inPacket) {
        // CVecCtrlUser::EndUpdateActive
        MapleChar chr = c.getChr();
        Field field = chr.getField();

        inPacket.decodeInt(); // dr0
        inPacket.decodeInt(); // dr1
        inPacket.decodeByte(); // Field Key
        inPacket.decodeInt(); // dr2
        inPacket.decodeInt(); // dr3
        inPacket.decodeInt(); // CRC
        inPacket.decodeInt(); // dwKey
        inPacket.decodeInt(); // CRC32

        // CMovePath::Flush -> CMovePath::Encode (line 85)
        MovementData movementInfo = new MovementData(inPacket);
        movementInfo.applyTo(chr);

        //TODO: need to handle char inAffectedArea -
        //field.checkCharInAffectedAreas(chr);

        // Handle sending player move to other players -
        field.broadcastPacket(CUserRemote.move(chr, movementInfo), chr);
        // Fail-safe when the char falls outside the map
        if (chr.getPosition().getY() > 5000) {
            Portal portal = field.findDefaultPortal();
            chr.warp(chr.getField(), portal);
        }
        // client has stopped moving. this might not be the best way
        if (chr.getMoveAction() == 4 || chr.getMoveAction() == 5) {
            //TODO: need to handle TSM (Temporary stat manager)
        }
    }

    @Handler(ops = {UserMeleeAttack, UserShootAttack, UserMagicAttack, UserBodyAttack})
    public static void handleUserAttack(MapleClient c, InPacket inPacket, InHeader header) {
        // CUserLocal::TryDoingMeleeAttack -> line 2878
        MapleChar chr = c.getChr();
        AttackInfo ai = new AttackInfo();
        AttackType type = None;
        switch (header) {
            case UserMeleeAttack -> type = Melee;
            case UserShootAttack -> type = Shoot;
            case UserMagicAttack -> type = Magic;
            case UserBodyAttack -> type = Body;
        }
        if (type != None) {
            ai.decode(type, inPacket);
            chr.getField().broadcastPacket(CUserRemote.attack(chr, ai), chr);
            ai.apply(chr);
        }
    }

    @Handler(op = UserHit)
    public static void handleUserHit(MapleClient c, InPacket inPacket) {
        MapleChar chr = c.getChr();
        // CUserLocal::SetDamaged - Line 637
        inPacket.decodeInt(); // timeStamp
        DamageType type = DamageType.getTypeByVal(inPacket.decodeByte());
        inPacket.decodeByte(); // magicElemAttr | Element - 0x00 = element-less, 0x01 = ice, 0x02 = fire, 0x03 = lightning
        int dmg = inPacket.decodeInt();
        int mobID = 0;
        boolean isLeft = false;

        switch (type) {
            case Physical, Magic -> {
                mobID = inPacket.decodeInt();
                inPacket.decodeInt(); // objID
                isLeft = inPacket.decodeBool();
                inPacket.decodeByte(); // top
                inPacket.decodeByte(); // relativeDir
                inPacket.decodeByte(); // damageMissed
                inPacket.decodeByte(); // nX
            }
        }
        chr.getField().broadcastPacket(CUserRemote.hit(chr, type, dmg, mobID, isLeft));
        if (JobUtils.isPaladin(chr.getJob())) {
            Skill divineShield = chr.getSkill(PALADIN_DIVINE_SHIELD.getId());
            if (divineShield != null && divineShield.getCurrentLevel() > 0) {
                SkillData skillData = SkillDataHandler.getSkillDataByID(PALADIN_DIVINE_SHIELD.getId());
                WarriorHandler.getInstance().handleSkill(chr, skillData, divineShield.getCurrentLevel());
            }
        }
        if (JobUtils.isMagician(chr.getJob())) {
            Skill magicGuard = chr.getSkill(MAGICIAN_MAGIC_GUARD.getId());
            if (magicGuard != null && magicGuard.getCurrentLevel() > 0 && chr.getTsm().hasCTS(CharacterTemporaryStat.MagicGuard)) {
                SkillData skillData = SkillDataHandler.getSkillDataByID(MAGICIAN_MAGIC_GUARD.getId());
                Integer reducedDmg = MagicianHandler.getInstance().getDmgAfterMagicGuardReduction(chr, skillData, magicGuard, dmg);
                if (reducedDmg != null) {
                    dmg = reducedDmg;
                }
            }
        }
        chr.modifyHp(-dmg);
    }

    @Handler(op = QuickslotKeyMappedModified)
    public static void handleQuickSlotKeyMappedModified(MapleClient c, InPacket inPacket) {
        List<Integer> quickSlotKeys = new ArrayList<>();
        for (int i = 0; i <= QUICK_SLOT_LENGTH; i++) {
            quickSlotKeys.add(inPacket.decodeInt());
        }
        c.getChr().setQuickSlotKeys(quickSlotKeys);
        //TODO:QUICKSLOT_MAPPED_INIT!!!! (chronos did it!)
    }

    @Handler(op = FuncKeyMappedModified)
    public static void handleFuncKeyMappedModified(MapleClient c, InPacket inPacket) {
        FuncKeyMappingType funcKeyType = FuncKeyMappingType.getMappingTypeByVal(inPacket.decodeInt());
        if (funcKeyType != null) {
            switch (funcKeyType) {
                case PetConsumeItemModified -> {
                    inPacket.decodeInt(); // TODO: need to handle -> ChangePetConsumeItemID
                }
                case PetConsumeMPItemModified -> {
                    int m_nPetConsumeItemID_MP = inPacket.decodeInt(); // TODO: need to handle -> ChangePetConsumeMPItemID
                }
                case KeyModified -> handleKeyModifiedToChr(inPacket, c.getChr());
            }
        }
    }

    @Handler(op = UserSkillUpRequest)
    public static void handleUserSkillUpRequest(MapleClient c, InPacket inPacket) {
        // CWvsContext::SendSkillUpRequest
        MapleChar chr = c.getChr();
        inPacket.decodeInt(); // update_time
        int skillID = inPacket.decodeInt(); // update_time
        chr.lvlUpSkill(skillID);
        //TODO: need to remove!!
        chr.message("SkillID: " + skillID, ChatType.SpeakerWorld);
        SkillData sd = SkillDataHandler.getSkillDataByID(skillID);
        sd.getSkillStatInfo().forEach((stat, val) -> chr.message(stat + ": " + val, ChatType.SpeakerWorld));
        chr.write(CWvsContext.changeSkillRecordResult(chr.getSkills(), true, true));
        //TODO: need to handle certain passive skills -> stat boost / hp|mp boost! | recovery? for example 1110000 (suppose to give more mp recovry but i gues it's totally server sided?)
    }

    @Handler(ops = {UserChangeStatRequest, UserChangeStatRequestByItemOption})
    public static void handleUserChangeStatRequest(MapleClient c, InPacket inPacket, InHeader header) {
        MapleChar chr = c.getChr();
        inPacket.decodeInt(); // update time
        inPacket.decodeInt(); // mask seems always to be 1400 -> 1000 + 400 | honestly idk why you need it cause you only encode hp & mp always?
        int hp = inPacket.decodeShort() + chr.getTsm().getPassiveStat(PassiveBuffStat.HP_REGEN);
        int mp = inPacket.decodeShort() + chr.getTsm().getPassiveStat(PassiveBuffStat.MP_REGEN);
        inPacket.decodeByte(); // nOption | Maybe related if you sit or not?, seems that if you sit it's 2? (default is 0)
        if (hp > 0 || mp > 0) {
            HashMap<Stat, Object> stats = new HashMap<>();
            if (hp > 0) {
                chr.setHp(Math.min(chr.getMaxHp(), chr.getHp() + hp));
                stats.put(Stat.Hp, chr.getHp());
            }
            if (mp > 0) {
                chr.setMp(Math.min(chr.getMaxMp(), chr.getMp() + mp));
                stats.put(Stat.Mp, chr.getMp());
            }
            chr.changeStats(stats);
        }
    }

    @Handler(op = PassiveskillInfoUpdate)
    public static void handleUserPassiveSkillInfoUpdate(MapleClient c, InPacket inPacket) {
        inPacket.decodeInt(); // updateTime
        // The most not relevant packet i've seen in sometime xD
    }

    @Handler(op = UserSkillUseRequest)
    public static void handleUserSkillUseRequest(MapleClient c, InPacket inPacket) {
        // CUserLocal::SendSkillUseRequest -> Line 176
        inPacket.decodeInt(); // update_time
        int skillID = inPacket.decodeInt();
        byte slv = inPacket.decodeByte();
        MapleChar chr = c.getChr();
        int throwingStarItemID = 0;
        if (SkillUtils.isAntiRepeatBuffSkill(skillID)) {
            // Anti-repeat buff skill -
            Position chrPos = inPacket.decodePosition();
        }
        if (skillID == NIGHTLORD_SHADOW_STARS.getId()) {
            throwingStarItemID = inPacket.decodeInt();
        }
        // TODO: can be mapped by each skill in the WZ files!
        if (false) { // dwAffectedMemberBitmap
            byte dwAffectedMemberBitmap = inPacket.decodeByte(); // it's a byte map of the effected members from the party that will receive the buff
            if (skillID == PRIEST_DISPEL.getId()) {
                short tDelay = inPacket.decodeShort();
            }
        }
        if (false) { // inPacket.getUnreadAmount() > 2
            byte nMobCount = inPacket.decodeByte();
            for (int i = 0; i < nMobCount; i++) {
                inPacket.decodeInt(); // adwMobID
            }
            inPacket.decodeShort(); // tDelay
        }
        // Handle the skill cts -
        chr.handleSkill(skillID, slv, throwingStarItemID);

        // Handle remote skill effect -
        Effect skillEffect = new SkillUseEffect(skillID,chr.getLevel(),slv);
        chr.getField().broadcastPacket(CUserRemote.remoteEffect(chr.getId(), skillEffect.getType(), skillEffect), chr);
    }

    @Handler(op = UserTransferChannelRequest)
    public static void handleUserTransferChannelRequest(MapleClient c, InPacket inPacket) {
        byte channelID = (byte) (inPacket.decodeByte() + 1); // the channel number here is always -1 then the intended number
        MapleChannel targetChannel = Server.getWorldById(c.getWorldId()).getChannelById(channelID);
        if (targetChannel != null) {
            c.changeChannel(targetChannel);
        }
    }

    @Handler(op = UserDropMoneyRequest)
    public static void handleUserDropMoneyRequest(MapleClient c, InPacket inPacket) {
        MapleChar chr = c.getChr();
        inPacket.decodeInt(); // timestamp
        int mesoAmountToDrop = inPacket.decodeInt();
        // Modify the meso quantity -
        chr.modifyMeso(-mesoAmountToDrop);
        // Drop the meso -
        Drop drop = new Drop(mesoAmountToDrop);
        chr.getField().spawnDrop(drop, chr.getPosition());
    }

    @Handler(op = UserSkillCancelRequest)
    public static void handleUserSkillCancelRequest(MapleClient c, InPacket inPacket) {
        int skillID = inPacket.decodeInt();
        c.getChr().cancelBuff(skillID);
    }

    @Handler(op = UserEmotion)
    public static void handleUserEmotion(MapleClient c, InPacket inPacket) {
        MapleChar chr = c.getChr();

        int emotion = inPacket.decodeInt();
        int duration = inPacket.decodeInt();
        boolean byItemOption = inPacket.decodeBool();

        chr.getField().broadcastPacket(CUserRemote.emotion(chr.getId(),emotion,duration,byItemOption));
    }
}
