package com.dori.SpringStory.world;

import com.dori.SpringStory.client.character.MapleAccount;
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
