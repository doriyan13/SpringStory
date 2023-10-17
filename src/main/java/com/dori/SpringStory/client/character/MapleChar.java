package com.dori.SpringStory.client.character;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.events.EventManager;
import com.dori.SpringStory.events.eventsHandlers.ValidateChrTempStatsEvent;
import com.dori.SpringStory.temporaryStats.characters.TemporaryStatManager;
import com.dori.SpringStory.connection.dbConvertors.InlinedIntArrayConverter;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.packets.CStage;
import com.dori.SpringStory.connection.packet.packets.CUserLocal;
import com.dori.SpringStory.connection.packet.packets.CWvsContext;
import com.dori.SpringStory.enums.*;
import com.dori.SpringStory.inventory.Equip;
import com.dori.SpringStory.inventory.Inventory;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.ItemUtils;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.SkillUtils;
import com.dori.SpringStory.utils.utilEntities.FileTime;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.world.fieldEntities.Portal;
import com.dori.SpringStory.wzHandlers.ItemDataHandler;
import com.dori.SpringStory.wzHandlers.SkillDataHandler;
import com.dori.SpringStory.wzHandlers.wzEntities.SkillData;
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
    private Inventory equippedInventory = new Inventory(EQUIPPED, DEFAULT_INVENTORY_SIZE);
    @JoinColumn(name = "equipInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory equipInventory = new Inventory(EQUIP, DEFAULT_INVENTORY_SIZE);
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
    private Inventory cashInventory = new Inventory(CASH, DEFAULT_CASH_INVENTORY_SIZE);
    // Skill fields -
    @JoinColumn(name = "charId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Skill> skills;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "skillCoolTimes", joinColumns = @JoinColumn(name = "charID"))
    @MapKeyColumn(name = "skillId")
    @Column(name = "nextUsableTime")
    private Map<Integer, Long> skillCoolTimes;
    @Convert(converter = InlinedIntArrayConverter.class)
    private List<Integer> quickSlotKeys;
    // Non-DB fields -
    @Transient
    private static final Logger logger = new Logger(MapleChar.class);
    @Transient
    private MapleClient mapleClient;
    @Transient
    private Position position;
    @Transient
    private Field field;
    @Transient
    private short foothold;
    @Transient
    private byte moveAction;
    @Transient
    private TemporaryStatManager tsm = new TemporaryStatManager();

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
        this.hair = charAppearance[1] + charAppearance[2]; // Hair contain: hair + hairColor values!
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
                getEquippedInventory().addItem(equip);
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

    private void encodeSkillCoolTime(OutPacket outPacket) {
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
            getConsumeInventory().encode(outPacket);
        }
        if (mask.isInMask(DBChar.ItemSlotInstall)) {
            getInstallInventory().encode(outPacket);
        }
        if (mask.isInMask(DBChar.ItemSlotEtc)) {
            getEtcInventory().encode(outPacket);
        }
        if (mask.isInMask(DBChar.ItemSlotCash)) {
            getCashInventory().encode(outPacket);
        }
        if (mask.isInMask(DBChar.SkillRecord)) {
            outPacket.encodeShort(getSkills().size());
            // For each skill encode the skill data -
            getSkills().forEach(skill -> skill.encodeRecord(outPacket));
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
            outPacket.encodeString("", 13); // sPairCharacterName
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

    public void addSkillCoolTime(int skillID, int timeInSec) {
        getSkillCoolTimes().put(skillID, System.currentTimeMillis() + timeInSec * 1_000L);
    }

    public void warp(Field to, Portal targetPortal) {
        // Update the char instance in both the old and new map -
        getField().removePlayer(this);
        to.addPlayer(this);
        // Update for the char instance the field data -
        this.setField(to);
        this.setMapId(to.getId());
        // Update the position -
        this.setPosition(targetPortal.getPosition());
        // Update the portal ID of the instance -
        this.setPortalId(targetPortal.getId());
        // Set the field for the character to spawn in -
        write(CStage.onSetField(this, field, (short) 0, (int) getMapleClient().getChannel(),
                0, false, (byte) 1, (short) 0,
                "", new String[]{""}));
        // Spawn lifes for the client -
        field.spawnLifesForCharacter(this);
        // Assign Controllers For life -
        field.assignControllerToMobs(this);
    }

    public Inventory getInventoryByType(InventoryType invType) {
        return switch (invType) {
            case EQUIPPED -> getEquippedInventory();
            case EQUIP -> getEquipInventory();
            case CONSUME -> getConsumeInventory();
            case ETC -> getEtcInventory();
            case INSTALL -> getInstallInventory();
            case CASH -> getCashInventory();
        };
    }

    public void unEquip(Item item) {
        getEquippedInventory().removeItem(item);
        getEquipInventory().addItem(item);
        // TODO: need in the future to add handling for updating remove avatarLook && Item Skills!
    }

    public void equip(Item item) {
        getEquipInventory().removeItem(item);
        getEquippedInventory().addItem(item);
    }

    public void swapItems(Item item, Item swappedItem, boolean fromEquippedInv) {
        // If it's equipped, need to un-equip the item -
        if (fromEquippedInv) {
            unEquip(item);
        } else {
            // If there is an item to swap, first un-equip the equipped item -
            if (swappedItem != null) {
                unEquip(swappedItem);
            }
            // If it's a not equipped item, need to equip the item -
            equip(item);
        }
    }

    public void changeStats(Map<Stat, Object> stats) {
        write(CWvsContext.statChanged(stats, true, (byte) 0, 0, 0));
    }

    public void updateStat(Stat stat, Object value) {
        Map<Stat, Object> stats = new HashMap<>();
        stats.put(stat, value);
        changeStats(stats);
    }

    public void enableAction() {
        // Handle the famous dispose -
        changeStats(new HashMap<>());
    }

    public void modifyHp(int amount) {
        int newHp;
        if (amount > 0) {
            newHp = Math.min(amount + getHp(), getMaxHp());
            setHp(newHp);
            updateStat(Stat.Hp, newHp);
        } else if (amount < 0) {
            newHp = Math.max(amount + getHp(), 0);
            setHp(newHp);
            updateStat(Stat.Hp, newHp);
        }
    }

    public void modifyMp(int amount) {
        int newMp;
        if (amount > 0) {
            newMp = Math.min(Math.abs(amount + getMp()), getMaxMp());
            setMp(newMp);
            updateStat(Stat.Mp, newMp);
        } else if (amount < 0) {
            newMp = Math.max(amount + getMp(), 0);
            setMp(newMp);
            updateStat(Stat.Mp, newMp);
        }
    }

    public void fullHeal() {
        int amountOfHpToHeal = getMaxHp() - getHp();
        int amountOfMpToHeal = getMaxMp() - getMp();

        if (amountOfHpToHeal > 0) {
            modifyHp(amountOfHpToHeal);
        }
        if (amountOfMpToHeal > 0) {
            modifyMp(amountOfMpToHeal);
        }
    }

    public void lvlUp(int amountOfLevels) {
        int optionalNewLvl = amountOfLevels + getLevel();
        int amountOfLvlUps = MAX_LVL - optionalNewLvl > 0 ? amountOfLevels : MAX_LVL - getLevel();
        if (amountOfLevels > 0) {
            Map<Stat, Object> stats = new HashMap<>();
            int apToAdd = amountOfLvlUps * 5;
            int spToAdd = amountOfLvlUps * 3;
            int hpToAdd = 0;
            int mpToAdd = 0;
            for (int i = 0; i < amountOfLvlUps; i++) {
                hpToAdd += MapleUtils.getRandom(15, 50);
                mpToAdd += MapleUtils.getRandom(15, 50);
            }
            setLevel(getLevel() + amountOfLvlUps);
            stats.put(Stat.Level, getLevel());
            setAp(getAp() + apToAdd);
            stats.put(Stat.AbilityPoint, getAp());
            setSp(getSp() + spToAdd);
            stats.put(Stat.SkillPoint, getSp());
            setMaxHp(getMaxHp() + hpToAdd);
            stats.put(Stat.MaxHp, getMaxHp());
            setHp(getMaxHp());
            stats.put(Stat.Hp, getHp());
            setMaxMp(getMaxMp() + mpToAdd);
            stats.put(Stat.MaxMp, getMaxMp());
            setMp(getMaxMp());
            stats.put(Stat.Mp, getMp());
            if (getLevel() == MAX_LVL) {
                setExp(0);
            }
            stats.put(Stat.Exp, getExp());

            changeStats(stats);
        }
    }

    public void gainExp(int amountOfExp) {
        if (level < MAX_LVL) {
            int amountOfLevels = 0;
            int totalExp = amountOfExp + exp;
            boolean needToCalcExp = true;
            while (needToCalcExp) {
                int diffExp = totalExp - EXP_TABLE[level - 1];
                if (diffExp > 0) {
                    exp = 0;
                    totalExp = diffExp;
                    amountOfLevels++;
                } else {
                    needToCalcExp = false;
                    exp = Math.max(totalExp, exp);
                }
            }
            if (amountOfLevels > 0) {
                lvlUp(amountOfLevels);
            } else {
                updateStat(Stat.Exp, getExp());
            }
        }
    }

    public void message(String msg, ChatType type) {
        write(CUserLocal.chatMsg(msg, type));
    }

    public void noticeMsg(String msg) {
        write(CUserLocal.noticeMsg(msg));
    }

    public Skill getSkill(int skillID) {
        return getSkills().stream().filter(skill -> skill.getSkillId() == skillID).findFirst().orElse(null);
    }

    public void lvlUpSkill(int skillID) {
        Skill currSkill = getSkill(skillID);
        if (currSkill == null) {
            currSkill = SkillDataHandler.getSkillByID(skillID);
            if (currSkill == null) {
                logger.error("Trying to lvl up a non existing skill- " + skillID);
                return;
            } else {
                getSkills().add(currSkill);
            }
        }
        currSkill.setCurrentLevel(currSkill.getCurrentLevel() + 1);
        SkillData skillData = SkillDataHandler.getSkillDataByID(skillID);
        if (skillData != null && skillData.isPassive()) {
            //TODO: need to handle passive skills and manage it (for example add maxHP / maxMP) and such!!
        }
        setSp(getSp() - 1);
        updateStat(Stat.SkillPoint, getSp());
    }

    private void addSkill(Skill skill) {
        if (skill.getMasterLevel() > 0 && getSkill(skill.getSkillId()) == null) {
            getSkills().add(skill);
        }
    }

    public void setJob(int jobID) {
        Job job = Job.getJobById(jobID);
        if (job != null) {
            message("Change " + getName() + " to the Job: " + job.name(), ChatType.GameDesc);
            Set<Skill> skills = SkillDataHandler.getSkillsByJobID(job.getId());
            if (!skills.isEmpty()) {
                skills.forEach(this::addSkill);
                write(CWvsContext.changeSkillRecordResult(getSkills(), true, true));
            }
            this.job = jobID;
            updateStat(Stat.SubJob, jobID);
        } else {
            message("Didn't receive a valid Job id, Please contact admin!", ChatType.SpeakerChannel);
        }
    }

    public void resetTemporaryStats() {
        getTsm().validateStats();
        write(CWvsContext.temporaryStatReset(getTsm()));
        getTsm().cleanDeletedStats();
    }

    public void handleSkill(int skillID, int slv) {
        SkillData skillData = SkillDataHandler.getSkillDataByID(skillID);
        if (skillData != null) {
            boolean success = tsm.handleCustomSkillsByID(getJob(), skillID, slv) || tsm.attemptToAutoHandleSkillByID(skillData, slv);
            if (success) {
                resetTemporaryStats();
                write(CWvsContext.temporaryStatSet(getTsm()));
                getTsm().applyModifiedStats();
                // After setting the chr stats the chr get locked and need to be released -
                enableAction();
            } else {
                message("The skill: " + skillID + ", need manual handling!", ChatType.SpeakerWorld);
            }
            SkillUtils.applySkillConsumptionToChar(skillID, slv, this);
            EventManager.addEvent(new ValidateChrTempStatsEvent(this),getTsm().getSkillExpirationTimeInSec(skillID) + 1); // adding 1 sec delay to make the server response feel more natural in the client
        }
    }
}
