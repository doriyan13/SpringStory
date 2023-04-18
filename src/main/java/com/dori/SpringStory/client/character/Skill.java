package com.dori.SpringStory.client.character;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.utils.utilEntities.FileTime;
import com.dori.SpringStory.wzHandlers.wzEntities.SkillData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.dori.SpringStory.utils.SkillUtils.isSkillNeedMasterLevel;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "skills")
public class Skill {
    // Fields -
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    private int skillId;
    private int currentLevel;
    private int masterLevel;
    private int maxLevel;

    public Skill(SkillData skillData) {
        this.skillId = skillData.getSkillId();
        this.currentLevel = 0;
        this.masterLevel = skillData.getMasterLevel();
        this.maxLevel = skillData.getMaxLevel();
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(getSkillId());
        outPacket.encodeInt(getCurrentLevel());
        outPacket.encodeFT(FileTime.fromType(FileTime.Type.MAX_TIME));
        if (isSkillNeedMasterLevel(getSkillId())) {
            outPacket.encodeInt(getMasterLevel());
        }
    }
}
