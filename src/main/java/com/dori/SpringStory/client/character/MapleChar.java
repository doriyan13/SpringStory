package com.dori.SpringStory.client.character;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.messages.IncEXPMessage;
import com.dori.SpringStory.connection.dbConvertors.InlinedIntArrayConverter;
import com.dori.SpringStory.events.EventManager;
import com.dori.SpringStory.events.eventsHandlers.ValidateChrTempStatsEvent;
import com.dori.SpringStory.jobs.JobHandler;
import com.dori.SpringStory.scripts.api.ScriptApi;
import com.dori.SpringStory.temporaryStats.characters.CharacterTemporaryStat;
import com.dori.SpringStory.temporaryStats.characters.TemporaryStatManager;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.packets.CUserLocal;
import com.dori.SpringStory.connection.packet.packets.CWvsContext;
import com.dori.SpringStory.enums.*;
import com.dori.SpringStory.inventory.Equip;
import com.dori.SpringStory.inventory.Inventory;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.*;
import com.dori.SpringStory.utils.utilEntities.FileTime;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Drop;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.world.fieldEntities.Portal;
import com.dori.SpringStory.dataHandlers.ItemDataHandler;
import com.dori.SpringStory.dataHandlers.SkillDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.ItemData;
import com.dori.SpringStory.dataHandlers.dataEntities.SkillData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import static com.dori.SpringStory.constants.GameConstants.*;
import static com.dori.SpringStory.enums.EventType.VALIDATE_CHARACTER_TEMP_STATS;
import static com.dori.SpringStory.enums.InventoryOperation.Add;
import static com.dori.SpringStory.enums.InventoryOperation.UpdateQuantity;
import static com.dori.SpringStory.enums.InventoryType.*;
import static com.dori.SpringStory.enums.Stat.*;
import static com.dori.SpringStory.enums.Stat.MaxHp;
import static com.dori.SpringStory.enums.Stat.MaxMp;
import static com.dori.SpringStory.temporaryStats.characters.CharacterTemporaryStat.*;

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
    @Convert(converter = InlinedIntArrayConverter.class)
    private List<Integer> extendSP;
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
    @MapKey(name = "skillId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Map<Integer, Skill> skills;
    // Key mapping -
    @Convert(converter = InlinedIntArrayConverter.class)
    private List<Integer> quickSlotKeys;
    @MapKey(name = "key")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Map<Integer, KeyMapping> keymap;
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
    @Transient
    private Map<Integer, Long> skillCoolTimes = new HashMap<>();
    @Transient
    private ScriptApi script;

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
        this.extendSP = List.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        // Rank -
        this.ranked = false;
        // LinkedChar -
        this.linkedCharacterLvl = 0;
        this.linkedCharacterName = "";
        // Skills -
        this.skills = new HashMap<>();
        // KeyMapping -
        this.keymap = new HashMap<>();
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
        this.job = subJob != 1 ? job : Job.Thief.getId();
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
        this.mapId = FieldUtils.getStartingFieldByJob(this.job);
        // ExtendSP -
        this.extendSP = List.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
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
        this.skills = new HashMap<>();
        // KeyMapping -
        this.keymap = new HashMap<>();
    }

    public int getStat(Stat stat) {
        return switch (stat) {
            case MaxHp ->
                    getMaxHp() + (getMaxHp() * getTsm().getPassiveStat(PassiveBuffStat.MAX_HP) / 100) + (getMaxHp() * getTsm().getCTS(CharacterTemporaryStat.MaxHp) / 100);
            case MaxMp ->
                    getMaxMp() + (getMaxMp() * getTsm().getPassiveStat(PassiveBuffStat.MAX_MP) / 100) + (getMaxMp() * getTsm().getCTS(CharacterTemporaryStat.MaxMp) / 100);
            default -> 0;
        };
    }

    public void initPassiveStats() {
        getSkills().forEach((skillId, skill) -> {
            SkillData skillData = SkillDataHandler.getSkillDataByID(skillId);
            if (skillData.isPassive()) {
                applyPassiveSkillDataStats(skillData, skill.getCurrentLevel());
            }
        });
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

    public void encodeExtendedSP(OutPacket outPacket) {
        outPacket.encodeByte(getExtendSP().size());
        for (int i = 0; i < getExtendSP().size(); i++) {
            outPacket.encodeByte(i);
            outPacket.encodeByte(getExtendSP().get(i));
        }
    }

    public void encodeSP(OutPacket outPacket) {
        if (JobUtils.isExtendedJob(getJob())) {
            encodeExtendedSP(outPacket);
        } else {
            outPacket.encodeShort(getSp()); // remaining SP
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
        // Encode SP -
        encodeSP(outPacket);
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
        Integer cWeaponID = cWeapon.isEmpty() ? null : cWeapon.getFirst();
        // TODO: this is wrong and need to be fixed!
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
        outPacket.encodeByte(getTsm().getCTS(CombatOrders)); // CombatOrders
        outPacket.encodeByte(0); // idk? | Some Loop?
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
            getSkills().values().forEach(skill -> skill.encodeRecord(outPacket));
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
            for (int i = 0; i < 5; i++) {
                outPacket.encodeInt(0);
            }
            for (int i = 0; i < 10; i++) {
                outPacket.encodeInt(0);
            }
            // adwMapTransfer.ForEach(p.Encode4);
            // adwMapTransferEx.ForEach(p.Encode4);
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
            //outPacket.encodeInt(0); // usCardID
            //outPacket.encodeByte(9); // nCardCount
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
            //outPacket.encodeInt(0); // adwTempMobID[i] -> jaguar ID!
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
        // Update the position -
        this.setPosition(targetPortal.getPosition());
        // Update the portal ID of the instance -
        this.setPortalId(targetPortal.getId());
        // Add to the new Field -
        to.spawnPlayer(this, false);
    }

    public void warp(int mapID) {
        Field toField = getMapleClient().getMapleChannelInstance().getField(mapID);
        if (toField != null) {
            Portal targetPortal = toField.findDefaultPortal();
            warp(toField, targetPortal);
        } else {
            message("Un-valid Map ID!", ChatType.SpeakerChannel);
        }
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
            newHp = Math.min(amount + getHp(), getStat(MaxHp));
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
            newMp = Math.min(Math.abs(amount + getMp()), getStat(MaxMp));
            setMp(newMp);
            updateStat(Stat.Mp, newMp);
        } else if (amount < 0) {
            newMp = Math.max(amount + getMp(), 0);
            setMp(newMp);
            updateStat(Stat.Mp, newMp);
        }
    }

    public void fullHeal() {
        int amountOfHpToHeal = getStat(MaxHp) - getHp();
        int amountOfMpToHeal = getStat(MaxMp) - getMp();

        if (amountOfHpToHeal > 0) {
            modifyHp(amountOfHpToHeal);
        }
        if (amountOfMpToHeal > 0) {
            modifyMp(amountOfMpToHeal);
        }
    }

    public void addSp(int spToAdd) {
        if (JobUtils.isExtendedJob(getJob())) {
            int extendedSpIndex = JobUtils.getExtendedSpIndexByJob(getJob());
            getExtendSP().add(extendedSpIndex, getExtendSP().get(extendedSpIndex) + spToAdd);
        } else {
            setSp(getSp() + spToAdd);
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
            addSp(spToAdd);
            stats.put(Stat.SkillPoint, JobUtils.isExtendedJob(getJob()) ? getExtendSP() : getSp());
            setMaxHp(getMaxHp() + hpToAdd);
            stats.put(MaxHp, getMaxHp());
            setHp(getMaxHp());
            stats.put(Stat.Hp, getHp());
            setMaxMp(getMaxMp() + mpToAdd);
            stats.put(MaxMp, getMaxMp());
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
            // Send the increase exp message -
            IncEXPMessage expMessage = new IncEXPMessage();
            expMessage.setLastHit(true);
            expMessage.setIncEXP(amountOfExp);
            write(CWvsContext.incExpMessage(expMessage));
        }
    }

    public void gainAP(int amountOfAp) {
        if (amountOfAp > 0) {
            int newAP = ap + amountOfAp;
            setAp(newAP);
            updateStat(AbilityPoint, newAP);
        }
    }

    public void gainSP(int amountOfSp) {
        if (amountOfSp > 0) {
            addSp(amountOfSp);
            updateStat(SkillPoint, JobUtils.isExtendedJob(getJob()) ? getExtendSP() : getSp());
        }
    }

    public void message(String msg, ChatType type) {
        write(CUserLocal.chatMsg(msg, type));
    }

    public void noticeMsg(String msg) {
        write(CUserLocal.noticeMsg(msg));
    }

    public Skill getSkill(int skillID) {
        return getSkills().get(skillID);
    }

    private void applyPassiveSkillDataStats(SkillData skillData, int slv) {
        for (Map.Entry<SkillStat, String> entry : skillData.getSkillStatInfo().entrySet()) {
            PassiveBuffStat stat = Stat.getStatBySkillStat(entry.getKey());
            if (stat != null) {
                getTsm().addPassiveStat(stat, skillData.getSkillId(), FormulaCalcUtils.calcValueFromFormula(entry.getValue(), slv));
            }
        }
    }

    public void lvlUpSkill(int skillID) {
        Skill currSkill = getSkill(skillID);
        if (currSkill == null) {
            currSkill = SkillDataHandler.getSkillByID(skillID);
            if (currSkill == null) {
                logger.error("Trying to lvl up a non existing skill- " + skillID);
                return;
            } else {
                getSkills().put(skillID, currSkill);
            }
        }
        currSkill.setCurrentLevel(currSkill.getCurrentLevel() + 1);
        SkillData skillData = SkillDataHandler.getSkillDataByID(skillID);
        if (skillData != null && skillData.isPassive()) {
            applyPassiveSkillDataStats(skillData, currSkill.getCurrentLevel());
        }
        addSp(-1);
        updateStat(Stat.SkillPoint, JobUtils.isExtendedJob(getJob()) ? getExtendSP() : getSp());
    }

    public void addSkill(Skill skill) {
        if (skill.getMasterLevel() > 0 && getSkill(skill.getSkillId()) == null) {
            getSkills().put(skill.getSkillId(), skill);
        }
        // TODO: need to remove, just for TESTING!!!! (NOT GMS LIKE)
        else if (skill.getMasterLevel() == 0) {
            skill.setMasterLevel(30);
            skill.setMaxLevel(30);
            getSkills().put(skill.getSkillId(), skill);
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

    public void applyTemporaryStats() {
        resetTemporaryStats();
        write(CWvsContext.temporaryStatSet(getTsm()));
        getTsm().applyModifiedStats();
        // After setting the chr stats the chr get locked and need to be released -
        enableAction();
    }

    private boolean attemptHandleActiveSkill(SkillData skillData, int slv) {
        JobHandler handler = JobHandler.getHandlerByJobID(getJob());
        if (handler != null) {
            return handler.handleSkill(this, skillData, slv);
        }
        return false;
    }

    public void handleSkill(int skillID, int slv) {
        SkillData skillData = SkillDataHandler.getSkillDataByID(skillID);
        if (skillData != null) {
            if (!attemptHandleActiveSkill(skillData, slv)) {
                if (tsm.attemptHandleCustomSkillsByID(this, skillData, slv)
                        || tsm.attemptToAutoHandleSkillByID(skillData, slv)) {
                    applyTemporaryStats();
                    EventManager.addEvent(MapleUtils.concat(getId(), skillID), VALIDATE_CHARACTER_TEMP_STATS, new ValidateChrTempStatsEvent(this), getTsm().getSkillExpirationTimeInSec(skillID) + 1); // adding 1 sec delay to make the server response feel more natural in the client
                } else {
                    // Must do it cuz if not the client will be locked for skills that don't modify stats
                    enableAction();
                }
            }
            SkillUtils.applySkillConsumptionToChar(skillID, slv, this);
        }
    }

    public void raiseAttackCombo() {
        int amountOfStacks = getTsm().getCTS(ComboCounter);
        int maxCombo = SkillUtils.getMaxComboAttackForChr(this);
        int amountToRaise = 1;
        if (amountOfStacks != 0 && amountOfStacks + amountToRaise <= maxCombo) {
            Skill advanceCombo = getSkill(Skills.HERO_ADVANCED_COMBO.getId());
            if (advanceCombo != null && advanceCombo.getCurrentLevel() > 0) {
                SkillData skillData = SkillDataHandler.getSkillDataByID(advanceCombo.getSkillId());
                if (MapleUtils.succeedProp(FormulaCalcUtils.calcValueFromFormula(skillData.getSkillStatInfo().get(SkillStat.prop), advanceCombo.getCurrentLevel()))) {
                    amountToRaise = amountOfStacks + 2 <= maxCombo ? 2 : 1; // If it doesn't overflow then double stack, if it overflows keep at 1.
                }
            }
            getTsm().addStat(ComboCounter, Skills.CRUSADER_COMBO_ATTACK.getId(), amountOfStacks + amountToRaise);
            applyTemporaryStats();
        }
    }

    public void resetAttackCombo() {
        if (getTsm().getCTS(ComboCounter) != 0) {
            getTsm().addStat(ComboCounter, Skills.CRUSADER_COMBO_ATTACK.getId(), 1);
            applyTemporaryStats();
        }
    }

    public void addEquip(Equip equip) {
        // setEquip SN -
        equip.setSerialNumber(MapleUtils.concat(getId(), System.currentTimeMillis()));
        getEquipInventory().addItem(equip);
        write(CWvsContext.inventoryOperation(true, Add, (short) equip.getBagIndex(), (short) -1, equip));
    }

    public void addItem(Item item) {
        Inventory inventory = getInventoryByType(item.getInvType());
        ItemData itemData = ItemDataHandler.getItemDataByID(item.getItemId());
        boolean newItem = false;
        if (inventory != null && itemData != null && item.getQuantity() > 0) {
            Item itemInInv = inventory.getItemByItemID(item.getItemId());
            if (itemInInv != null && inventory.getType().isStackable()) {
                int updatedQuantity = item.getQuantity() + itemInInv.getQuantity();
                int amountToNewStack = updatedQuantity - itemData.getSlotMax();
                if (amountToNewStack > 0) {
                    item.setQuantity(amountToNewStack);
                    updatedQuantity = itemData.getSlotMax();
                    newItem = true;
                }
                itemInInv.setQuantity(updatedQuantity);
                write(CWvsContext.inventoryOperation(true, UpdateQuantity, (short) itemInInv.getBagIndex(), (short) -1, itemInInv));
            } else {
                newItem = true;
            }
        } else {
            logger.error("Got illegal item: " + item);
        }
        if (newItem) {
            inventory.addItem(item);
            write(CWvsContext.inventoryOperation(true, Add, (short) item.getBagIndex(), (short) -1, item));
        }
    }

    public Drop dropItem(Item item, int quantity) {
        Item itemToDrop = item;
        // Partial Drop instead of full drop -
        if (quantity != 0) {
            ItemData itemData = ItemDataHandler.getItemDataByID(item.getItemId());
            itemToDrop = new Item(itemData);
            item.removeQuantity(quantity);
            itemToDrop.setQuantity(quantity);
        }
        // Reset position for when it's picked up -
        itemToDrop.setBagIndex(0);
        // Remove from inventory -
        Inventory inventory = getInventoryByType(itemToDrop.getInvType());
        inventory.removeItem(itemToDrop);
        // Return a Drop instance -
        Drop drop = new Drop(itemToDrop);
        // Set owner -
        drop.setOwnerID(getId());

        return drop;
    }

    public Drop dropItem(Item item) {
        return dropItem(item, 0);
    }

    public void modifyMeso(int amount, boolean showInChat) {
        int newMoneyAmount;
        if (amount > 0) {
            newMoneyAmount = Math.min(amount + getMeso(), MAX_MESO);
            setMeso(newMoneyAmount);
            updateStat(Money, newMoneyAmount);
        } else if (amount < 0 && (amount + getMeso() >= 0)) {
            newMoneyAmount = amount + getMeso();
            setMeso(newMoneyAmount);
            updateStat(Money, newMoneyAmount);
        }

        if (showInChat) {
            write(CWvsContext.incMoneyMessage(amount));
        }
    }

    public void modifyMeso(int amount) {
        modifyMeso(amount, false);
    }

    public void pickupItem(Drop drop) {
        getField().removeDrop(drop.getId(), getId(), -1);
        EventManager.cancelEvent(MapleUtils.concat((long) getField().getId(), drop.getId()), EventType.REMOVE_DROP_FROM_FIELD);
        if (drop.getItem() != null) {
            // Handle item pickup -
            Item item = drop.getItem();
            switch (item.getType()) {
                case EQUIP -> {
                    addEquip((Equip) item);
                    write(CWvsContext.dropPickupMessage(item.getItemId(), PickupMessageType.ITEM_WITHOUT_QUANTITY, (short) 0, 1));
                }
                case BUNDLE -> {
                    addItem(item);
                    write(CWvsContext.dropPickupMessage(item.getItemId(), PickupMessageType.ITEM_WITH_QUANTITY, (short) 0, 1));
                }
            }
        } else if (drop.getQuantity() > 0 && drop.isMoney()) {
            // Handle meso pickup -
            int quantity = drop.getQuantity();
            setMeso(getMeso() + quantity);
            updateStat(Money, getMeso());
            write(CWvsContext.dropPickupMessage(quantity, PickupMessageType.MESO, (short) 0, quantity));
        }
    }

    public void consumeItem(InventoryType invType,
                            int itemID,
                            int amount) {
        Inventory itemInv = getInventoryByType(invType);
        Item item = itemInv.getItemByItemID(itemID);
        if (item != null) {
            modifyItem(itemInv, item, -amount);
        } else {
            logger.error("The player: " + getName() + ", try to modify an item that don't exist!! - " + itemID + " | inv - " + invType);
        }
    }

    private void modifyItem(Inventory inventory,
                            Item item,
                            int amount) {
        InventoryOperation inventoryOperation = inventory.updateItemQuantity(item, amount);
        write(CWvsContext.inventoryOperation(true, inventoryOperation, (short) item.getBagIndex(), (short) -1, item));
    }

    public void boundScript(ScriptApi script, int npcID) {
        script.setNpcID(npcID);
        this.script = script;
    }

    public void clearScript() {
        this.script = null;
    }
}
