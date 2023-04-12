package com.dori.Dori90v.inventory;

import com.dori.Dori90v.connection.dbConvertors.FileTimeConverter;
import com.dori.Dori90v.connection.dbConvertors.InlinedIntArrayConverter;
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
public class Equip{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    private boolean isCash;
    private long serialNumber;
    private int itemId;
    private int equipItemID;
    @Column(name = "type")
    private ItemType type;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "inventoryType")
    private InventoryType invType;
    @Convert(converter = FileTimeConverter.class)
    private FileTime equippedDate = FileTime.fromType(FileTime.Type.PLAIN_ZERO);
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
    private short durability;
    private short iuc;
    private short iPvpDamage;
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
    private short soulOptionId;
    private short soulSocketId;
    private short soulOption;
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

    public Equip(EquipData equipData){
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
        this.isCash= equipData.isCash();
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
}
