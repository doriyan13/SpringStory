package com.dori.SpringStory.client.character.quest;

import com.dori.SpringStory.enums.QuestStatus;
import com.dori.SpringStory.utils.utilEntities.FileTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor

@Entity
@Table(name = "quest_managers")
@NoArgsConstructor
public class QuestManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @MapKey(name = "qrKey")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Map<Integer, Quest> quests = new HashMap<>();

    public Set<Quest> getQuests() {
        return quests.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    public Set<Quest> getStartedQuests() {
        return quests.values().stream()
                .filter(qr -> qr.getStatus() == QuestStatus.Started)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Quest> getCompletedQuests() {
        return quests.values().stream()
                .filter(qr -> qr.getStatus() == QuestStatus.Completed)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Quest> getExQuests() {
        return quests.values().stream()
                .filter(qr -> qr.getStatus() == QuestStatus.PartyQuest)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void addQuest(@NotNull Quest questRecord) {
        quests.put(questRecord.getQrKey(), questRecord);
    }

    public Optional<Quest> removeQuest(int questId) {
        return Optional.ofNullable(quests.remove(questId));
    }

    public Optional<Quest> getQuest(int questId) {
        return Optional.ofNullable(quests.get(questId));
    }

    public boolean hasQuestStarted(int questId) {
        Quest qr = quests.get(questId);
        return qr != null && qr.getStatus() == QuestStatus.Started;
    }

    public Quest forceStartQuest(int questId) {
        Quest qr = new Quest(questId, QuestStatus.Started);
        addQuest(qr);
        return qr;
    }

    public Quest forceCompleteQuest(int questId) {
        Quest qr = quests.getOrDefault(questId, new Quest(questId, QuestStatus.Completed));
        qr.setCompletedTime(FileTime.currentTime());
        qr.setStatus(QuestStatus.Completed);
        addQuest(qr);
        return qr;
    }

    public Quest setQuestInfoEx(int questId,
                                @NotNull String value) {
        Quest qr = new Quest(questId, QuestStatus.Started);
        qr.setQrValue(value);
        addQuest(qr);
        return qr;
    }
}
