package com.dori.SpringStory.client.character;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    private int type;
    // private Map<SkillStat, String> skillStatInfo = new HashMap<>();
}
