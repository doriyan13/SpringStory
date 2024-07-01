package com.dori.SpringStory.client.character.quest;

import com.dori.SpringStory.connection.dbConvertors.FileTimeConverter;
import com.dori.SpringStory.enums.QuestStatus;
import com.dori.SpringStory.utils.utilEntities.FileTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name = "quests")
public class Quest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int qrKey;
    private String qrValue;

    @Column(name = "status")
    private QuestStatus status;

    @Convert(converter = FileTimeConverter.class)
    private FileTime completedTime;

    public Quest(int qrKey, QuestStatus status) {
        this.qrKey = qrKey;
        this.status = status;
    }
}
