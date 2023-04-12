package com.dori.Dori90v.client.character;

import com.dori.Dori90v.client.MapleClient;
import com.dori.Dori90v.connection.packet.OutPacket;
import com.dori.Dori90v.enums.BodyPart;
import com.dori.Dori90v.enums.CharacterGender;
import com.dori.Dori90v.enums.DBChar;
import com.dori.Dori90v.inventory.Equip;
import com.dori.Dori90v.inventory.EquipInventory;
import com.dori.Dori90v.inventory.Inventory;
import com.dori.Dori90v.logger.Logger;
import com.dori.Dori90v.utils.ItemUtils;
import com.dori.Dori90v.utils.utilEntities.FileTime;
import com.dori.Dori90v.wzHandlers.ItemDataHandler;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static com.dori.Dori90v.enums.InventoryType.*;

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
    // Transmitted fields -
    @Transient
    private static final Logger logger = new Logger(MapleChar.class);
    @Transient
    private MapleClient mapleClient;
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
    private long mapId;
    private int portal;
    private int subJob;
    // Ranking fields -
    private boolean ranked;
    private int rankNum;
    private int rankMove;
    private int jobRank;
    private int jobRankMove;
    // Inventory fields -
    @JoinColumn(name = "equippedInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private EquipInventory equippedInventory = new EquipInventory(EQUIPPED, 52);
    @JoinColumn(name = "equipInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private EquipInventory equipInventory = new EquipInventory(EQUIP, 52);
    @JoinColumn(name = "consumeInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory consumeInventory = new Inventory(CONSUME, 52);
    @JoinColumn(name = "etcInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory etcInventory = new Inventory(ETC, 52);
    @JoinColumn(name = "installInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory installInventory = new Inventory(INSTALL, 52);
    @JoinColumn(name = "cashInventory")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private EquipInventory cashInventory = new EquipInventory(CASH, 96);

    public MapleChar(int accountID, String name, int gender) {
        // Set char base data -
        this.accountID = accountID;
        this.name = name;
        this.gender = CharacterGender.getGenderByOrdinal(gender); // 0 - boy | 1 - girl
        this.skin = 1;
        this.face = 20000;
        this.hair = 30027;
        this.level = 1;
        this.job = 0; // Beginner
        // Set basic stats -
        this.nStr = 4;
        this.nDex = 4;
        this.nInt = 4;
        this.nLuk = 4;
        // Set HP/MP -
        this.hp = 50;
        this.maxHp = 50;
        this.mp = 50;
        this.maxMp = 50;
        // map pos -
        this.mapId = 100000000;
        // ExtendSP -
        this.extendSP = new ExtendSP();
        // Rank -
        this.ranked = false;
    }

    public MapleChar(int accountID, String name, byte gender, int job, short subJob, int[] charAppearance) {
        // Set char base data -
        this.accountID = accountID;
        this.name = name;
        this.gender = CharacterGender.getGenderByOrdinal(gender); // 0 - boy | 1 - girl
        this.skin = charAppearance[3];
        this.face = charAppearance[0];
        this.hair = charAppearance[1];
        this.hairColor = charAppearance[2];
        this.level = 1;
        this.job = job; // Beginner
        this.subJob = subJob;
        // Set basic stats -
        this.nStr = 4;
        this.nDex = 4;
        this.nInt = 4;
        this.nLuk = 4;
        // Set HP/MP -
        this.hp = 50;
        this.maxHp = 50;
        this.mp = 50;
        this.maxMp = 50;
        // map pos -
        this.mapId = 100000000;
        // ExtendSP -
        this.extendSP = new ExtendSP();
        // Rank -
        this.ranked = false;
        // Handle equips -
        for (int i = 4; i <= 7; i++) { // Start of equips is from the 5th spot (aka i = 4) till the end (which is the 8th spot, aka i = 7)
            Equip equip = ItemDataHandler.getEquipByID(charAppearance[i]);
            if (equip != null) {
                this.getEquippedInventory().addItem(equip);
            }
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
        outPacket.encodeByte(getPortal());
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
        // Fill Equips and possibly the CashWeapon -
        ItemUtils.fillEquipsMaps(this, charEquips, charMaskedEquips);
        Integer cWeapon = charMaskedEquips.get(BodyPart.CashWeapon);

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
        outPacket.encodeInt(cWeapon != null ? cWeapon : 0); // Cash weapon id ?
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

    public void encodeInfo(OutPacket outPacket, DBChar mask) {
        outPacket.encodeLong(mask.getFlag());
        outPacket.encodeByte(0); // CombatOrders ?
        outPacket.encodeByte(0); // idk?
        if(mask.isInMask(DBChar.Character)){
            // Encode character data - (in the future versions they fully encode all the char data again, but now it seems to only encode char stats)
            encodeCharacterStats(outPacket);
            outPacket.encodeByte(0); // buddyList capacity
            // BlessOfFairyOrigin -
            outPacket.encodeBool(false);
            if(false){
                outPacket.encodeString(""); // chr.getBlessOfFairyOrigin
            }
        }
        if(mask.isInMask(DBChar.Money)){
            outPacket.encodeInt(getMeso());
        }
        if(mask.isInMask(DBChar.InventorySize)){
            outPacket.encodeByte(getEquipInventory().getSlots());
            outPacket.encodeByte(getConsumeInventory().getSlots());
            outPacket.encodeByte(getEtcInventory().getSlots());
            outPacket.encodeByte(getInstallInventory().getSlots());
            outPacket.encodeByte(getCashInventory().getSlots());
        }
        if(mask.isInMask(DBChar.AdminShopCount)){
            outPacket.encodeFT(FileTime.fromType(FileTime.Type.MAX_TIME)); // extra pendant slot ?
            outPacket.encodeInt(0); // aEquipExtExpire[0].dwHighDateTime
        }
//        if (mask.isInMask(DBChar.ItemSlotEquip)) {
//
//        }

    }
}
