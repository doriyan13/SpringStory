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

    private int QRKey;
    private String qrValue;

    @Column(name = "status")
    private QuestStatus status;

    @Convert(converter = FileTimeConverter.class)
    private FileTime completedTime;

    @Transient
    private Map<String, String> properties = new HashMap<>();

    //TODO: need to fix / figure out how to handle it properly?
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "questID")
//    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
//    private List<QuestProgressRequirement> progressRequirements;

//    public Quest() {
//        progressRequirements = new ArrayList<>();
//    }

    public Quest(int QRKey, QuestStatus status) {
        this.QRKey = QRKey;
        this.status = status;
    }
}
