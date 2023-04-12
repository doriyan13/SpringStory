package com.dori.Dori90v.utils;

import com.dori.Dori90v.client.character.MapleChar;
import com.dori.Dori90v.enums.BodyPart;
import com.dori.Dori90v.enums.EquipPrefix;
import com.dori.Dori90v.inventory.Equip;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface ItemUtils {

    private static int getItemPrefix(int nItemID) {
        return nItemID / 10000;
    }

    private static int getGenderFromItemID(int nItemID) {
        if (nItemID / 1000000 != 1 && getItemPrefix(nItemID) != 254 || getItemPrefix(nItemID) == 119 || getItemPrefix(nItemID) == 168) {
            return 2;
        }
        int result;
        switch (nItemID / 1000 % 10) {
            case 0 -> result = 0;
            case 1 -> result = 1;
            default -> result = 2;
        }
        return result;
    }

    static BodyPart getBodyPartFromItem(int itemID) {
        EquipPrefix prefix = EquipPrefix.getByVal(getItemPrefix(itemID));
        BodyPart bodyPart = BodyPart.BPBase;
        if (prefix != null) {
            switch (prefix) {
                case Hat -> bodyPart = BodyPart.Hat;
                case FaceAccessory -> bodyPart = BodyPart.FaceAccessory;
                case EyeAccessory -> bodyPart = BodyPart.EyeAccessory;
                case Earrings -> bodyPart = BodyPart.Earrings;
                case Top, Overall -> bodyPart = BodyPart.Top;
                case Bottom -> bodyPart = BodyPart.Bottom;
                case Shoes -> bodyPart = BodyPart.Shoes;
                case Gloves -> bodyPart = BodyPart.Gloves;
                case Shield, Katana, SecondaryWeapon -> bodyPart = BodyPart.Shield;
                case Cape -> bodyPart = BodyPart.Cape;
                case Ring -> bodyPart = BodyPart.Ring1;
                case Pendant -> bodyPart = BodyPart.Pendant;
                case Belt -> bodyPart = BodyPart.Belt;
                case Medal -> bodyPart = BodyPart.Medal;
                case Shoulder -> bodyPart = BodyPart.Shoulder;
                case MonsterBook -> bodyPart = BodyPart.MonsterBook;
                case PetWear -> bodyPart = BodyPart.PetWear1;
                case TamingMob -> bodyPart = BodyPart.TamingMob;
                case Saddle -> bodyPart = BodyPart.Saddle;
                case EvanHat -> bodyPart = BodyPart.EvanHat;
                case EvanPendant -> bodyPart = BodyPart.EvanPendant;
                case EvanWing -> bodyPart = BodyPart.EvanWing;
                case EvanShoes -> bodyPart = BodyPart.EvanShoes;
                case OneHandedAxe,OneHandedSword,OneHandedBluntWeapon,TwoHandedBluntWeapon,TwoHandedAxe,TwoHandedSword -> bodyPart =BodyPart.Weapon;
                default -> System.out.println("idk? " + prefix);
            }
        }
        return bodyPart;
    }

    static void fillEquipsMaps(MapleChar chr,
                               Map<BodyPart, Integer> charEquips,
                               Map<BodyPart, Integer> charMaskedEquips) {
        for (Equip item : chr.getEquippedInventory().getItems()) {
            BodyPart bodyPart = getBodyPartFromItem(item.getItemId());
            if(bodyPart != BodyPart.BPBase){
                if(bodyPart.getVal() < BodyPart.BPEnd.getVal()){
                    charEquips.put(bodyPart,item.getItemId());
                }
                else if (bodyPart.getVal() > BodyPart.BPEnd.getVal() && bodyPart.getVal() != BodyPart.CashWeapon.getVal()){
                    charMaskedEquips.put(bodyPart,item.getItemId());
                }
                else if(bodyPart.getVal() == BodyPart.CashWeapon.getVal()){
                    charMaskedEquips.put(bodyPart,item.getItemId());
                }
            }
        }
    }

}
