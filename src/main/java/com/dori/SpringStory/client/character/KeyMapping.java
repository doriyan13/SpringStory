package com.dori.SpringStory.client.character;

import com.dori.SpringStory.connection.packet.OutPacket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "keymap")
public class KeyMapping {
    // Fields -
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "key_idx")
    private int key;
    private byte type;
    private int action;

    public KeyMapping(int key, byte type, int action) {
        this.key = key;
        this.type = type;
        this.action = action;
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(getType());
        outPacket.encodeInt(getAction());
    }
}
