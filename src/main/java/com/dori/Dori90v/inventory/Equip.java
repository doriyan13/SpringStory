package com.dori.Dori90v.inventory;

import com.dori.Dori90v.connection.dbConvertors.FileTimeConverter;
import com.dori.Dori90v.connection.dbConvertors.InlinedIntArrayConverter;
import com.dori.Dori90v.connection.packet.OutPacket;
import com.dori.Dori90v.constants.GameConstants;
import com.dori.Dori90v.enums.EnchantStat;
import com.dori.Dori90v.enums.InventoryType;
import com.dori.Dori90v.utils.utilEntities.FileTime;
import com.dori.Dori90v.wzHandlers.wzEntities.EquipData;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name = "equips")
public class Equip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    private boolean isCash;
    private long serialNumber;
    private int itemId;
    private int equipItemID;
    private int bagIndex;
    private String owner = "";
    @Column(name = "type")
    private ItemType type;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "inventoryType")
    private InventoryType invType;
    @Convert(converter = FileTimeConverter.class)
    private FileTime equippedDate = FileTime.fromType(FileTime.Type.PLAIN_ZERO);
    @Convert(converter = FileTimeConverter.class)
    private FileTime dateExpire = FileTime.fromType(FileTime.Type.MAX_TIME);
    private int prevBonusExpRate;
    private short tuc;
    private short cuc;
    private short iStr;
    private short iDex;
    private short iInt;
    private short iLuk;
    private short iMaxHp;
    private short iMaxMp;
    private short iPad;
    private short iMad;
    private short iPDD;
    private short iMDD;
    private short iAcc;
    private short iEva;
    private short iCraft;
    private short iSpeed;
    private short iJump;
    private short attribute;
    private short levelUpType;
    private short level;
    private short exp;
    private short durability = 100; // suppose to be 100
    private short iuc;
    private short grade;
    private byte iReduceReq;
    private short specialAttribute;
    private short durabilityMax;
    private short iIncReq;
    private short growthEnchant;
    private short psEnchant;
    private short bdr;
    private short imdr;
    private boolean bossReward;
    private boolean superiorEqp;
    private short damR;
    private short statR;
    private short cuttable;
    private short exGradeOption;
    private short hyperUpgrade;
    private short itemState;
    private short chuc;
    private short rStr;
    private short rDex;
    private short rInt;
    private short rLuk;
    private short rLevel;
    private short rJob;
    private short rPop;
    @Convert(converter = InlinedIntArrayConverter.class)
    private List<Integer> options = new ArrayList<>(); // base + add pot + anvil
    private int specialGrade;
    private boolean fixedPotential;
    private boolean noPotential;
    private boolean tradeBlock;
    @Column(name = "is_only")
    private boolean only;
    private boolean notSale;
    private int attackSpeed;
    private int price;
    private int charmEXP;
    private boolean expireOnLogout;
    private boolean exItem;
    private boolean equipTradeBlock;
    private String iSlot = "";
    private String vSlot = "";
    private int fixedGrade;
    @Transient
    private Map<EnchantStat, Integer> enchantStats = new HashMap<>();
    @Transient
    private List<ItemSkill> itemSkills = new ArrayList<>();
    @Transient
    private short iucMax = GameConstants.MAX_HAMMER_SLOTS;
    @Transient
    private boolean hasIUCMax = false;

    public Equip(EquipData equipData) {
        this.itemId = equipData.getItemID();
        this.type = ItemType.EQUIP;
        this.iSlot = equipData.getISlot();
        this.vSlot = equipData.getVSlot();
        this.rLevel = equipData.getRLevel();
        this.rStr = equipData.getRStr();
        this.rDex = equipData.getRDex();
        this.rInt = equipData.getRInt();
        this.rLuk = equipData.getRLuk();
        this.iStr = equipData.getIStr();
        this.iDex = equipData.getIDex();
        this.iInt = equipData.getIInt();
        this.iLuk = equipData.getILuk();
        this.iPDD = equipData.getIPDD();
        this.iMDD = equipData.getIMDD();
        this.iMaxHp = equipData.getIMaxHp();
        this.iMaxMp = equipData.getIMaxMp();
        this.iPad = equipData.getIPad();
        this.iMad = equipData.getIMad();
        this.iEva = equipData.getIEva();
        this.iAcc = equipData.getIAcc();
        this.iSpeed = equipData.getISpeed();
        this.iJump = equipData.getIJump();
        this.damR = equipData.getDamR();
        this.statR = equipData.getStatR();
        this.imdr = equipData.getImdr();
        this.bdr = equipData.getBdr();
        this.tuc = equipData.getTuc();
        this.hasIUCMax = equipData.isHasIUCMax();
        this.iucMax = equipData.getIucMax();
        this.equipItemID = equipData.getItemID();
        this.price = equipData.getPrice();
        this.attackSpeed = equipData.getAttackSpeed();
        this.isCash = equipData.isCash();
        this.expireOnLogout = equipData.isExpireOnLogout();
        this.exItem = equipData.isExItem();
        this.notSale = equipData.isNotSale();
        this.only = equipData.isOnly();
        this.tradeBlock = equipData.isTradeBlock();
        this.fixedPotential = equipData.isFixedPotential();
        this.noPotential = equipData.isNoPotential();
        this.bossReward = equipData.isBossReward();
        this.superiorEqp = equipData.isSuperiorEqp();
        this.iReduceReq = equipData.getIReduceReq();
        this.specialGrade = equipData.getSpecialGrade();
        this.charmEXP = equipData.getCharmEXP();
        this.options = equipData.getOptions();
    }

    public void encodeBaseItemData(OutPacket outPacket) {
        outPacket.encodeInt(getItemId());
        outPacket.encodeByte(isCash());
        if (isCash()) {
            outPacket.encodeLong(getId());
        }
        outPacket.encodeFT(getDateExpire());
    }

    public void encode(OutPacket outPacket) {
        // GW_ItemSlotEquip::RawDecode:
        // Encode item type (equip) -
        outPacket.encodeByte(getType().getVal());
        // Encode base item data - GW_ItemSlotBase::RawDecode
        encodeBaseItemData(outPacket);
        outPacket.encodeByte(tuc); // ruc ? IDK why they call it in the client ruc but in the wz it's tuc :kek
        outPacket.encodeByte(cuc);

        outPacket.encodeShort(iStr);
        outPacket.encodeShort(iDex);
        outPacket.encodeShort(iInt);
        outPacket.encodeShort(iLuk);
        outPacket.encodeShort(iMaxHp);
        outPacket.encodeShort(iMaxMp);
        outPacket.encodeShort(iPad);
        outPacket.encodeShort(iMad);
        outPacket.encodeShort(iPDD);
        outPacket.encodeShort(iMDD);
        outPacket.encodeShort(iAcc);
        outPacket.encodeShort(iEva);

        outPacket.encodeShort(iCraft);
        outPacket.encodeShort(iSpeed);
        outPacket.encodeShort(iJump);
        outPacket.encodeString(owner);
        outPacket.encodeShort(attribute);

        outPacket.encodeByte(levelUpType);
        outPacket.encodeByte(level);
        outPacket.encodeInt(exp);
        outPacket.encodeInt(durability);

        outPacket.encodeInt(iuc);

        outPacket.encodeByte(grade);
        outPacket.encodeByte(chuc);

        for (int i = 0; i < 3; i++) {
            outPacket.encodeShort(getOptions().get(i));
        }
        outPacket.encodeShort(0); // Socket 1
        outPacket.encodeShort(0); // Socket 2
        if (serialNumber == 0) { // literally the code in the client O.o
            outPacket.encodeLong(serialNumber); // liCashItemSN
        }
        outPacket.encodeLong(0); // ftEquipped
        outPacket.encodeInt(0); // nPrevBonusExpRate
    }
}
