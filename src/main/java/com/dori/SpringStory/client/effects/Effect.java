package com.dori.SpringStory.client.effects;


import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.UserEffectTypes;

public interface Effect {
    UserEffectTypes getType();
    void encode(OutPacket outPacket);
}
