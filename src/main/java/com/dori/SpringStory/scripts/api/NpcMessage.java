package com.dori.SpringStory.scripts.api;

import com.dori.SpringStory.enums.NpcMessageType;
import com.dori.SpringStory.scripts.message.NpcMessageData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NpcMessage {
    private NpcMessageType type;
    private NpcMessageData data;
}
