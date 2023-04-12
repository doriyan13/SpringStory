package com.dori.Dori90v.client.character;

import com.dori.Dori90v.connection.dbConvertors.FileTimeConverter;
import com.dori.Dori90v.constants.ServerConstants;
import com.dori.Dori90v.enums.AccountType;
import com.dori.Dori90v.logger.Logger;
import com.dori.Dori90v.utils.utilEntities.FileTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "account")
@Table(
        name = "accounts"
)
public class MapleAccount {
    @Transient
    private static final Logger log = new Logger(MapleAccount.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int worldId;
    String name;
    private String password;
    @Enumerated(EnumType.ORDINAL)
    private AccountType accountType;
    private int accountSlots;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) // fetch eagerly to fix lazy load handling -> https://www.baeldung.com/hibernate-initialize-proxy-exception
    @JoinColumn(name = "accountID")
    private Set<MapleChar> characters = new HashSet<>();
    private int votePoints;
    private int donationPoints;

    private byte gender;

    @Convert(converter = FileTimeConverter.class)
    private FileTime creationDate;
    @Convert(converter = FileTimeConverter.class)
    private FileTime banExpireDate;
    @Convert(converter = FileTimeConverter.class)
    private FileTime chatUnblockDate;

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

        this.characters = new HashSet<>();
    }
}
