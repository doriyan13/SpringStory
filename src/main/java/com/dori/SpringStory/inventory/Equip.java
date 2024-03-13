package com.dori.SpringStory.inventory;

import com.dori.SpringStory.connection.dbConvertors.FileTimeConverter;
import com.dori.SpringStory.connection.dbConvertors.InlinedIntArrayConverter;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.constants.GameConstants;
import com.dori.SpringStory.dataHandlers.dataEntities.EquipData;
import com.dori.SpringStory.enums.EquipAttribute;
import com.dori.SpringStory.enums.EquipBaseStat;
import com.dori.SpringStory.utils.utilEntities.FileTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name = "equips")
@PrimaryKeyJoinColumn(name = "dbItemId")
public class Equip extends Item {
    private int equipItemID;
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
    private List<ItemSkill> itemSkills = new ArrayList<>();
    @Transient
    private short iucMax = GameConstants.MAX_HAMMER_SLOTS;
    @Transient
    private boolean hasIUCMax = false;

    public Equip(EquipData equipData) {
        this.itemId = equipData.getItemID();
        this.type = ItemType.EQUIP;
        this.quantity = 1;
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
        this.cash = equipData.isCash();
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
        this.options = equipData.getOptions();
    }


    public void encode(OutPacket outPacket) {
        // GW_ItemSlotEquip::RawDecode:
        // Encode item type (equip) -
        outPacket.encodeByte(getType().getVal());
        // Encode base item data - GW_ItemSlotBase::RawDecode
        super.encodeItemSlotBase(outPacket);
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
        if (!isCash()) { // if serialNumber == 0 | literally the code in the client O.o
            outPacket.encodeLong(getId()); // liCashItemSN
        }
        outPacket.encodeLong(0); // ftEquipped
        outPacket.encodeInt(0); // nPrevBonusExpRate
    }

    public long getBaseStat(EquipBaseStat equipBaseStat) {
        return switch (equipBaseStat) {
            case tuc -> getTuc();
            case cuc -> getCuc();
            case iStr -> getIStr();
            case iDex -> getIDex();
            case iInt -> getIInt();
            case iLuk -> getILuk();
            case iMaxHP -> getIMaxHp();
            case iMaxMP -> getIMaxMp();
            case iPAD -> getIPad();
            case iMAD -> getIMad();
            case iPDD -> getIPDD();
            case iMDD -> getIMDD();
            case iACC -> getIAcc();
            case iEVA -> getIEva();
            case iCraft -> getICraft();
            case iSpeed -> getISpeed();
            case iJump -> getIJump();
            case attribute -> getAttribute();
            case levelUpType -> getLevelUpType();
            case level -> getLevel();
            case exp -> getExp();
            case durability -> getDurability();
            case iuc -> getIuc();
            case iReduceReq -> getIReduceReq();
            case specialAttribute -> getSpecialAttribute();
            case durabilityMax -> getDurabilityMax();
            case iIncReq -> getIIncReq();
            case growthEnchant -> getGrowthEnchant();
            case psEnchant -> getPsEnchant();
            case bdr -> getBdr();
            case imdr -> getImdr();
            case damR -> getDamR();
            case statR -> getStatR();
            case cuttable -> getCuttable();
            case exGradeOption -> getExGradeOption();
            case hyperUpgrade -> getHyperUpgrade();
            default -> 0;
        };
    }

    public void setBaseStat(EquipBaseStat equipBaseStat, short amount) {
        switch (equipBaseStat) {
            case tuc -> setTuc(amount);
            case cuc -> setCuc(amount);
            case iStr -> setIStr(amount);
            case iDex -> setIDex(amount);
            case iInt -> setIInt(amount);
            case iLuk -> setILuk(amount);
            case iMaxHP -> setIMaxHp(amount);
            case iMaxMP -> setIMaxMp(amount);
            case iPAD -> setIPad(amount);
            case iMAD -> setIMad(amount);
            case iPDD -> setIPDD(amount);
            case iMDD -> setIMDD(amount);
            case iACC -> setIAcc(amount);
            case iEVA -> setIEva(amount);
            case iCraft -> setICraft(amount);
            case iSpeed -> setISpeed(amount);
            case iJump -> setIJump(amount);
            case attribute -> setAttribute(amount);
            case levelUpType -> setLevelUpType(amount);
            case level -> setLevel(amount);
            case exp -> setExp(amount);
            case durability -> setDurability(amount);
            case iuc -> setIuc(amount);
            case iReduceReq -> setIReduceReq((byte) amount);
            case specialAttribute -> setSpecialAttribute(amount);
            case durabilityMax -> setDurabilityMax(amount);
            case iIncReq -> setIIncReq(amount);
            case growthEnchant -> setGrowthEnchant(amount);
            case psEnchant -> setPsEnchant(amount);
            case bdr -> setBdr(amount);
            case imdr -> setImdr(amount);
            case damR -> setDamR(amount);
            case statR -> setStatR(amount);
            case cuttable -> setCuttable(amount);
            case exGradeOption -> setExGradeOption(amount);
            case hyperUpgrade -> setHyperUpgrade(amount);
        }
    }

    public void addStat(EquipBaseStat stat, int amount) {
        int cur = (int) getBaseStat(stat);
        int newStat = Math.max(cur + amount, 0); // stat cannot be negative
        setBaseStat(stat, (short) newStat);
    }

    public boolean hasAttribute(EquipAttribute equipAttribute) {
        return (getAttribute() & equipAttribute.getVal()) != 0;
    }

    public void addAttribute(EquipAttribute ea) {
        short attr = getAttribute();
        attr |= (short) ea.getVal();
        setAttribute(attr);
    }

    public void removeAttribute(EquipAttribute equipAttribute) {
        if (!hasAttribute(equipAttribute)) {
            return;
        }
        short attr = getAttribute();
        attr ^= (short) equipAttribute.getVal();
        setAttribute(attr);
    }
}
