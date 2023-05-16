package com.dori.SpringStory.client.character;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.*;
import com.dori.SpringStory.inventory.Equip;
import com.dori.SpringStory.inventory.EquipInventory;
import com.dori.SpringStory.inventory.Inventory;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.ItemUtils;
import com.dori.SpringStory.utils.utilEntities.FileTime;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.world.fieldEntities.Portal;
import com.dori.SpringStory.wzHandlers.ItemDataHandler;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import static com.dori.SpringStory.constants.GameConstants.*;
import static com.dori.SpringStory.enums.InventoryType.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "characters")
public class MapleChar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "accountID")
    private int accountID;
    @Column(name = "char_name")
    private String name;
    private int rewardPoints;
    // Character stats -
    private CharacterGender gender;
    private int skin;
    private int face;
    private int hair;
    private int hairColor;
    private int level;
    private int job;
    private int nStr;
    private int nDex;
    private int nInt;
    private int nLuk;
    private int hp;
    private int maxHp;
    private int mp;
    private int maxMp;
    private int ap;
    private int sp;
    private int exp;
    private int pop; // fame
    private int meso;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "extend_sp")
    private ExtendSP extendSP; // it's the Skill Points for each job and need to manage the job number by lvl 'getJobLevelByCharLevel' it's relevant for DualBlade and Evan (which have extra jobs)!!
    private int mapId;
    private int portalId;
    private int subJob;
    // Ranking fields -
    private boolean ranked;
    private int rankNum;
    private int rankMove;
    private int jobRank;
    private int jobRankMove;
    // Linked Character - (Blessing of fairy)
    private int linkedCharacterLvl;
    private String linkedCharacterName;
    // Inventory fields -
    @JoinColumn(name = "equippedInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private EquipInventory equippedInventory = new EquipInventory(EQUIPPED, DEFAULT_INVENTORY_SIZE);
    @JoinColumn(name = "equipInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private EquipInventory equipInventory = new EquipInventory(EQUIP, DEFAULT_INVENTORY_SIZE);
    @JoinColumn(name = "consumeInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory consumeInventory = new Inventory(CONSUME, DEFAULT_INVENTORY_SIZE);
    @JoinColumn(name = "etcInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory etcInventory = new Inventory(ETC, DEFAULT_INVENTORY_SIZE);
    @JoinColumn(name = "installInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory installInventory = new Inventory(INSTALL, DEFAULT_INVENTORY_SIZE);
    @JoinColumn(name = "cashInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private EquipInventory cashInventory = new EquipInventory(CASH, DEFAULT_CASH_INVENTORY_SIZE);
    // Skill fields -
    @JoinColumn(name = "charId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Skill> skills;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "skillCoolTimes", joinColumns = @JoinColumn(name = "charID"))
    @MapKeyColumn(name = "skillId")
    @Column(name = "nextUsableTime")
    private Map<Integer, Long> skillCoolTimes;
    // Non-DB fields -
    @Transient
    private static final Logger logger = new Logger(MapleChar.class);
    @Transient
    private MapleClient mapleClient;
    @Transient
    private Position position;
    @Transient
    private Field field;

    public MapleChar(int accountID, String name, int gender) {
        // Set char base data -
        this.accountID = accountID;
        this.name = name;
        this.gender = CharacterGender.getGenderByOrdinal(gender); // 0 - boy | 1 - girl
        this.skin = DEFAULT_SKIN;
        this.face = DEFAULT_FACE;
        this.hair = DEFAULT_HAIR;
        this.level = DEFAULT_START_LVL;
        this.job = Job.Beginner.getId(); // Beginner
        // Set basic stats -
        this.nStr = BASE_START_STAT;
        this.nDex = BASE_START_STAT;
        this.nInt = BASE_START_STAT;
        this.nLuk = BASE_START_STAT;
        // Set HP/MP -
        this.hp = BASE_START_HP;
        this.maxHp = BASE_START_HP;
        this.mp = BASE_START_MP;
        this.maxMp = BASE_START_MP;
        // map pos -
        this.mapId = DEFAULT_MAP_ID;
        // ExtendSP -
        this.extendSP = new ExtendSP();
        // Rank -
        this.ranked = false;
        // LinkedChar -
        this.linkedCharacterLvl = 0;
        this.linkedCharacterName = "";
        // Skills -
        this.skills = new HashSet<>();
        this.skillCoolTimes = new HashMap<>();
    }

    public MapleChar(int accountID, String name, byte gender, int job, short subJob, int[] charAppearance) {
        // Set char base data -
        this.accountID = accountID;
        this.name = name;
        this.gender = CharacterGender.getGenderByOrdinal(gender); // 0 - boy | 1 - girl
        // Character appearance -
        this.face = charAppearance[0];
        this.hair = charAppearance[1];
        this.hairColor = charAppearance[2];
        this.skin = charAppearance[3];
        this.level = DEFAULT_START_LVL;
        this.job = job;
        this.subJob = subJob;
        // Set basic stats -
        this.nStr = BASE_START_STAT;
        this.nDex = BASE_START_STAT;
        this.nInt = BASE_START_STAT;
        this.nLuk = BASE_START_STAT;
        // Set HP/MP -
        this.hp = BASE_START_HP;
        this.maxHp = BASE_START_HP;
        this.mp = BASE_START_MP;
        this.maxMp = BASE_START_MP;
        // map pos -
        this.mapId = DEFAULT_MAP_ID;
        // ExtendSP -
        this.extendSP = new ExtendSP();
        // Rank -
        this.ranked = false;
        // Handle equips -
        for (int i = 4; i <= 7; i++) { // Start of equips is from the 5th spot (aka i = 4) till the end (which is the 8th spot, aka i = 7)
            Equip equip = ItemDataHandler.getEquipByID(charAppearance[i]);
            if (equip != null) {
                equip.setBagIndex(ItemUtils.getBodyPartFromItem(equip.getItemId()).getVal());
                this.getEquippedInventory().addItem(equip);
            }
        }
        // LinkedChar -
        this.linkedCharacterLvl = 0;
        this.linkedCharacterName = "";
        // Skills -
        this.skills = new HashSet<>();
        this.skillCoolTimes = new HashMap<>();
    }

    /**
     * Writes a packet to this Char's client.
     *
     * @param outPacket The OutPacket to write.
     */
    public void write(OutPacket outPacket) {
        if (getMapleClient() != null) {
            getMapleClient().write(outPacket);
        }
    }

    public void encodeRank(OutPacket outPacket) {
        outPacket.encodeBool(isRanked());
        if (isRanked()) {
            outPacket.encodeInt(getRankNum());
            outPacket.encodeInt(getRankMove());
            outPacket.encodeInt(getJobRank());
            outPacket.encodeInt(getJobRankMove());
        }
    }

    public void encodeBasicStats(OutPacket outPacket) {
        outPacket.encodeShort(getNStr());
        outPacket.encodeShort(getNDex());
        outPacket.encodeShort(getNInt());
        outPacket.encodeShort(getNLuk());
        outPacket.encodeInt(getHp());
        outPacket.encodeInt(getMaxHp());
        outPacket.encodeInt(getMp());
        outPacket.encodeInt(getMaxMp());
    }

    public void encodeCharacterStats(OutPacket outPacket) {
        outPacket.encodeInt(getId());
        outPacket.encodeString(getName(), 13);
        outPacket.encodeByte(getGender().getValue());
        outPacket.encodeByte(getSkin());
        outPacket.encodeInt(getFace());
        outPacket.encodeInt(getHair());
        //TODO: encode pets properly -
        for (int i = 0; i < 3; i++) {
            outPacket.encodeLong(0); // Pet unique id
        }
        outPacket.encodeByte(getLevel());
        outPacket.encodeShort(getJob());
        // Encode Char basic stats -
        encodeBasicStats(outPacket);
        //avoid popup ?, idk i just encode the AP normally -
        outPacket.encodeShort(getAp()); // Math.min(199,getAp())

        //TODO: Check if job is special -> GM / Manager / Evan / DualBlade? ->  a1 / 1000 == 3 || a1 / 100 == 22 || a1 == 2001
        if (false) {
            getExtendSP().encode(outPacket);
        } else {
            outPacket.encodeShort(getSp()); // remaining SP
        }
        outPacket.encodeInt(getExp());
        outPacket.encodeShort(getPop()); // fame
        outPacket.encodeInt(0); // Gach exp -> nTempEXP
        outPacket.encodeInt((int) this.getMapId());
        outPacket.encodeByte(this.getPortalId());
        outPacket.encodeInt(0); // nPlayTime ?
        outPacket.encodeShort(getSubJob()); // 1 == DualBlade/ not sure about evan?
    }

    public void encodeAvatarLook(OutPacket outPacket) {
        outPacket.encodeByte(getGender().getValue());
        outPacket.encodeByte(getSkin());
        outPacket.encodeInt(getFace());
        outPacket.encodeByte(0); // IDK? in ida there isn't anything about it?
        outPacket.encodeInt(getHair());

        Map<BodyPart, Integer> charEquips = new HashMap<>();
        Map<BodyPart, Integer> charMaskedEquips = new HashMap<>();
        List<Integer> cWeapon = new ArrayList<>();
        // Fill Equips and possibly the CashWeapon -
        ItemUtils.fillEquipsMaps(this, charEquips, charMaskedEquips, cWeapon);
        Integer cWeaponID = cWeapon.isEmpty() ? null : cWeapon.get(0);

        //for -> myEquips (visible items)
        charEquips.forEach((BodyPart, itemID) -> {
            outPacket.encodeByte(BodyPart.getVal());
            outPacket.encodeInt(itemID);
        });
        outPacket.encodeByte(-1); // 0xFF
        //for -> myEquips (masked items)
        charMaskedEquips.forEach((BodyPart, itemID) -> {
            outPacket.encodeByte(BodyPart.getVal());
            outPacket.encodeInt(itemID);
        });
        outPacket.encodeByte(-1); // 0xFF
        outPacket.encodeInt(cWeaponID != null ? cWeaponID : 0); // Cash weapon id ?
        // Pets -
        for (int z = 0; z < 3; z++) {
            // Always encode 3 ints, but notice you encode the pets if exists and 0 if it doesn't -
            if (false) { // Is there a pet for that slot (0 - first | 1 - second | 2 - third)
                //TODO: need to encode petID for each equipped pet
                outPacket.encodeInt(0/*PetID*/);
            }
            outPacket.encodeInt(0);
        }
    }

    public void encode(OutPacket outPacket) {
        // Encode CharacterStats -
        encodeCharacterStats(outPacket);
        // Encode AvatarLook -
        encodeAvatarLook(outPacket);
        // Has family -> deprecated :
        outPacket.encodeByte(0);
        // Encode Rank -
        encodeRank(outPacket);
    }

    private void encodeCharacter(OutPacket outPacket) {
        // Encode character data - (in the future versions they fully encode all the char data again, but now it seems to only encode char stats)
        encodeCharacterStats(outPacket);
        outPacket.encodeByte(0); // buddyList capacity
        // BlessOfFairyOrigin -
        outPacket.encodeBool(getLinkedCharacterLvl() > 0);
        if (getLinkedCharacterLvl() > 0) {
            outPacket.encodeString(getLinkedCharacterName()); // BlessOfFairyOrigin
        }
    }

    private void encodeInventorySize(OutPacket outPacket) {
        outPacket.encodeByte(getEquipInventory().getSlots());
        outPacket.encodeByte(getConsumeInventory().getSlots());
        outPacket.encodeByte(getEtcInventory().getSlots());
        outPacket.encodeByte(getInstallInventory().getSlots());
        outPacket.encodeByte(getCashInventory().getSlots());
    }

    private void encodeAdminShopCount(OutPacket outPacket) {
        outPacket.encodeFT(FileTime.fromType(FileTime.Type.MAX_TIME)); // extra pendant slot ?
        outPacket.encodeInt(0); // aEquipExtExpire[0].dwHighDateTime
    }

    private void encodeEquipments(OutPacket outPacket) {
        // Normal equipped items -
        getEquippedInventory().encodeEquips(outPacket, EquipType.Equipped, false);
        // Cash equipped items -
        getEquippedInventory().encodeEquips(outPacket, EquipType.Cash, true);
        // Equip inventory -
        getEquipInventory().encodeEquips(outPacket, EquipType.Normal, false);
        // Evan -
        getEquippedInventory().encodeEquips(outPacket, EquipType.Evan, false);
        // Mechanic -
        getEquippedInventory().encodeEquips(outPacket, EquipType.Mechanic, false);
    }

    private void encodeSkillCoolTime(OutPacket outPacket){
        long currTime = System.currentTimeMillis();
        Map<Integer, Long> skillsCoolTimes = new HashMap<>();
        getSkillCoolTimes().forEach((skillID, coolTimeExpirationTimeStamp) -> {
            if (coolTimeExpirationTimeStamp - currTime > 0) {
                skillsCoolTimes.put(skillID, coolTimeExpirationTimeStamp);
            }
        });
        outPacket.encodeShort(skillsCoolTimes.size());
        for (Map.Entry<Integer, Long> skillCoolTime : skillsCoolTimes.entrySet()) {
            outPacket.encodeInt(skillCoolTime.getKey()); // nSkillId
            outPacket.encodeShort((short) ((skillCoolTime.getValue() - currTime) / 1000)); // nSkillCoolTime (in seconds!)
        }
    }

    public void encodeInfo(OutPacket outPacket, DBChar mask) {
        outPacket.encodeLong(mask.getFlag());
        outPacket.encodeByte(0); // CombatOrders ?
        outPacket.encodeByte(0); // idk?
        if (mask.isInMask(DBChar.Character)) {
            encodeCharacter(outPacket);
        }
        if (mask.isInMask(DBChar.Money)) {
            outPacket.encodeInt(getMeso());
        }
        if (mask.isInMask(DBChar.InventorySize)) {
            encodeInventorySize(outPacket);
        }
        if (mask.isInMask(DBChar.AdminShopCount)) {
            encodeAdminShopCount(outPacket);
        }
        if (mask.isInMask(DBChar.ItemSlotEquip)) {
            encodeEquipments(outPacket);
        }
        if (mask.isInMask(DBChar.ItemSlotConsume)) {
            getConsumeInventory().encodeInventory(outPacket);
        }
        if (mask.isInMask(DBChar.ItemSlotInstall)) {
            getInstallInventory().encodeInventory(outPacket);
        }
        if (mask.isInMask(DBChar.ItemSlotEtc)) {
            getEtcInventory().encodeInventory(outPacket);
        }
        if (mask.isInMask(DBChar.ItemSlotCash)) {
            getCashInventory().encodeInventory(outPacket);
        }
        if (mask.isInMask(DBChar.SkillRecord)) {
            outPacket.encodeShort(getSkills().size());
            // For each skill encode the skill data -
            getSkills().forEach(skill -> skill.encode(outPacket));
        }
        if (mask.isInMask(DBChar.SkillCooltime)) {
            encodeSkillCoolTime(outPacket);
        }
        //TODO: need to handle properly
        if (mask.isInMask(DBChar.QuestRecord)) {
            outPacket.encodeShort(0); // amount of not completed quests
            //for (Quest quest : getQuests)
            outPacket.encodeShort(0); // nQuestID
            outPacket.encodeString(""); // sQRValue
        }
        //TODO: need to handle properly
        if (mask.isInMask(DBChar.QuestComplete)) {
            outPacket.encodeShort(0); // amount of completed quests
            //for (Quest quest : getQuests)
            outPacket.encodeShort(0); // nQuestID
            outPacket.encodeFT(FileTime.fromType(FileTime.Type.PLAIN_ZERO)); // completed time, need to give proper time!
        }
        if (mask.isInMask(DBChar.MiniGameRecord)) {
            //TODO!
            outPacket.encodeShort(0);
        }
        if (mask.isInMask(DBChar.CoupleRecord)) { // it's ringInfo
            //TODO!
            outPacket.encodeShort(0);
            outPacket.encodeShort(0); // lFriendRecord size -
            // for (lFriendRecord : lFriendRecord.all)
            // encode ring -
            // {
                outPacket.encodeInt(0); // dwPairCharacterID
                outPacket.encodeString("",13); // sPairCharacterName
                outPacket.encodeLong(0); // liSN
                outPacket.encodeLong(0); // liPairSN
                outPacket.encodeInt(0); // dwFriendItemID
            // }
            outPacket.encodeShort(0);
        }
        if (mask.isInMask(DBChar.MapTransfer)) {
            //TODO!
        }
        if (mask.isInMask(DBChar.MonsterBookCover)) {
            //TODO!
            outPacket.encodeInt(0); // nMonsterBookCoverID
            outPacket.encodeByte(0); // idk?
        }
        if (mask.isInMask(DBChar.MonsterBookCard)) {
            //TODO!
            outPacket.encodeShort(0); // cards size -
            // for (card : cards)
            // encode card -
            // {
            outPacket.encodeInt(0); // usCardID
            outPacket.encodeByte(9); // nCardCount
            // }
        }
        if (mask.isInMask(DBChar.NewYearCard)) {
            //TODO!
            outPacket.encodeShort(0);
        }
        if (mask.isInMask(DBChar.QuestRecordEx)) {
            //TODO!
            outPacket.encodeShort(0);
        }
        if (mask.isInMask(DBChar.WildHunterInfo)) {
            //TODO!
            outPacket.encodeByte(0x28); // not sure?
            // for (int i = 0; i < adwTempMobID.Length; i++)
            // {
                outPacket.encodeInt(0); // adwTempMobID[i]
            // }
        }
        if (mask.isInMask(DBChar.QuestCompleteOld)) {
            //TODO!
            outPacket.encodeShort(0);
        }
        if (mask.isInMask(DBChar.VisitorLog)) {
            //TODO!
            outPacket.encodeShort(0); // m_mVisitorQuestLog
        }
    }

    public void addSkillCoolTime(int skillID, int timeInSec){
        getSkillCoolTimes().put(skillID, System.currentTimeMillis() + timeInSec * 1_000L);
    }

    public void warp(Field from, Field to, Portal targetPortal){
        // Update the char instance in both the old and new map -
        from.removePlayer(this);
        to.addPlayer(this);
        // Update for the char instance the field data -
        this.setField(to);
        this.setMapId(to.getId());
        // Update the position -
        this.setPosition(targetPortal.getPosition());
        // Update the portal ID of the instance -
        this.setPortalId(targetPortal.getId());
    }
}
