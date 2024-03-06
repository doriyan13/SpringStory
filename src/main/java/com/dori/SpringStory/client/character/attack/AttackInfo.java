package com.dori.SpringStory.client.character.attack;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.enums.AttackType;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.JobUtils;
import com.dori.SpringStory.utils.SkillUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttackInfo {
    private byte fieldKey;
    private AttackType type;
    private byte hits;
    private int mobCount;
    private int skillId;
    private byte slv;
    private int option;
    private int keyDown;
    private boolean jablin;
    private short atkAction;
    private byte atkActionType;
    private byte atkSpeed;
    private int atkTime; // tick?
    private boolean left = false;
    private short bulletPos;
    private List<DamageInfo> mobAttackInfo = new ArrayList<>();
    private static Logger logger = new Logger(AttackInfo.class);

    public void decode(AttackType type, InPacket inPacket) {
        // FieldKey -
        this.fieldKey = inPacket.decodeByte();
        // Reactor Melee attack extra handling -
        if (type == AttackType.Melee && inPacket.getUnreadAmount() == 60) {
            inPacket.decodeByte(); // Reactors extra byte
        }
        // Type -
        this.type = type;

        //TODO: verify that in Shoot attack at the start isn't a extra byte for boxAtk?

        // drInfo -
        inPacket.decodeInt(); // drInfo.dr0
        inPacket.decodeInt(); // drInfo.dr1

        byte mask = inPacket.decodeByte();
        this.hits = (byte) (mask & 0xF);
        this.mobCount = (mask >>> 4) & 0xF;

        // drInfo (again?) -
        inPacket.decodeInt(); // drInfo.dr2
        inPacket.decodeInt(); // drInfo.dr3

        this.skillId = inPacket.decodeInt();
        this.slv = inPacket.decodeByte();

        if (type == AttackType.Magic) {
            //TODO: some loop need to happen for 6 times of decodeInt?
            inPacket.decodeInt();
            inPacket.decodeInt();
            inPacket.decodeInt();
            inPacket.decodeInt();
            inPacket.decodeInt();
            inPacket.decodeInt();
        }

        inPacket.decodeInt(); // rnd
        inPacket.decodeInt(); // crc32

        inPacket.decodeInt(); // idk? | v660 - line 2936
        inPacket.decodeInt(); // idk? | v646 - line 2937

        if (SkillUtils.isKeyDownSkill(getSkillId())) {
            this.keyDown = inPacket.decodeInt();
        }

        this.option = inPacket.decodeByte();

        if (type == AttackType.Shoot) { // CUserLocal::TryDoingShootAttack -> Line 2320
            this.jablin = inPacket.decodeByte() != 0;
        }

        short mask2 = inPacket.decodeShort();
        this.left = ((mask2 >>> 15) & 1) != 0;
        this.atkAction = (short) (mask2 & 0x7FFF);

        inPacket.decodeInt(); // crc ? | v653 line 2944
        this.atkActionType = inPacket.decodeByte();
        this.atkSpeed = inPacket.decodeByte();
        this.atkTime = inPacket.decodeInt();

        int finalAttackLastSkillID = inPacket.decodeInt(); // Battle mage thing? | TODO: verify if it acting diff in other atk types?

        if (type == AttackType.Shoot) {
            this.bulletPos = inPacket.decodeShort();
            short pnCashItemPos = inPacket.decodeShort();
            byte nShootRange0a = inPacket.decodeByte();
            //TODO: need to hook all the skills and handle this - | need to handle it properly!!
            // is_shoot_skill_not_consuming_bullit -> Line 2331 | 0x006EEAF0
            if (true /*is_shoot_skill_not_consuming_bullet(skillId)*/) {
                int pnItemID = inPacket.decodeInt();
                System.out.println("pnItemID - " + pnItemID);
            }
        }
        for (int i = 0; i < this.mobCount; i++) {
            DamageInfo di = new DamageInfo();
            di.decode(inPacket, getHits());
            di.setSkillId(this.skillId);
            mobAttackInfo.add(di);
        }
    }

    public void apply(MapleChar chr) {
        if (skillId != 0) {
            Item stackToConsumeFrom = chr.getConsumeInventory().getItemByIndex(getBulletPos());
            SkillUtils.applySkillConsumptionToChar(skillId, chr.getSkill(skillId).getCurrentLevel(), chr, stackToConsumeFrom);
        }
        this.mobAttackInfo.forEach(mai -> mai.apply(chr));
        // Hero combo attack stacks handling -
        if (JobUtils.isHero(chr.getJob()) && !mobAttackInfo.isEmpty()) {
            chr.raiseAttackCombo();
        }
    }
}
