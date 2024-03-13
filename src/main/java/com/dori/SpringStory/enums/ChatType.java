package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum ChatType {
    Normal(0),
    Whisper(1), // Green whisper txt
    GroupParty(2), // Pink party txt
    GroupFriend(3), // Orange txt
    GroupGuild(4), // Purple txt
    GroupAlliance(5), // Green txt
    GameDesc(6), // Pink/purple txt
    Tip(7), // Gray txt
    Notice(8), // Yellow Tip txt
    Notice2(9), // Less-Yellow Tip txt
    AdminChat(10), // Notice (Blue system text)
    SystemNotice(11), //Admin
    SpeakerChannel(12), // Red txt
    SpeakerWorld(13), // Blue (world speaker) txt
    Mob(19), // Black with red background txt
    Expedition(20), // White with red background txt
    ItemMessage(21), // Black with yellow background txt
    AvatarMegaphone(25), // Yellow txt (small txt)
    PickupSpeakerWorld(26), // Blue txt (small txt)
    WorldName(27) // White (small txt)
    ;
    private final short val;

    ChatType(short val) {
        this.val = val;
    }

    ChatType(int i) {
        this((short) i);
    }

}
