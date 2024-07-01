package com.dori.SpringStory.client.character;

import com.dori.SpringStory.connection.dbConvertors.FileTimeConverter;
import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.enums.AccountType;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.utilEntities.FileTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class MapleAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int worldId;
    String name;
    private String password;
    @Enumerated(EnumType.ORDINAL)
    private AccountType accountType;
    private int accountSlots;
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) // fetch eagerly to fix lazy load handling -> https://www.baeldung.com/hibernate-initialize-proxy-exception
    private List<MapleChar> characters;
    private int votePoints;
    private int donationPoints;
    private byte gender;
    @Convert(converter = FileTimeConverter.class)
    private FileTime creationDate;
    @Convert(converter = FileTimeConverter.class)
    private FileTime banExpireDate;
    @Convert(converter = FileTimeConverter.class)
    private FileTime chatUnblockDate;

    @Transient
    private static final Logger log = new Logger(MapleAccount.class);

    public MapleAccount(String name, String password, boolean isAdmin) {
        this.worldId = ServerConstants.DEFAULT_WORLD_ID;
        this.name = name;
        this.password = password;
        this.accountType = isAdmin ? AccountType.Admin : AccountType.Player;
        this.accountSlots = 6; //TODO: maybe change to bigger number later ?
        this.creationDate = FileTime.currentTime();
        this.votePoints = 0;
        this.donationPoints = 0;
        this.gender = 0; // Pretty sure it means male?

        this.characters = new ArrayList<>();
    }
}
