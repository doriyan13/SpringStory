package com.dori.Dori90v.world;

import com.dori.Dori90v.client.character.MapleAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MigrateInUser {
    MapleAccount account;
    MapleChannel channel;
    int worldID;
    byte[] machineID;
}
