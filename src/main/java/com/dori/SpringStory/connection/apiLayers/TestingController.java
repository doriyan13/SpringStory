package com.dori.SpringStory.connection.apiLayers;

import com.dori.SpringStory.Server;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.handlers.MobHandler;
import com.dori.SpringStory.logger.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;
import java.util.Arrays;

@RestController
@RequestMapping(path = "test")
public class TestingController {
    // Logger -
    private final Logger logger = new Logger(TestingController.class);

    @PostMapping("packet")
    public void testPacket(@RequestBody String packet){
        String byteString = "19 1C 53 00 CB 00 00 FF 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 CC DD FF 00 CC DD FF 00 0D 8C B5 9C AC 00 64 00 98 FF 45 00 03 00 64 00 94 00 78 FF 5A 00 16 00 00 00 00 00 03 1C 02 00 5A 00 9B 00 5D FF 00 00 16 00 00 00 00 00 03 4A 00 00 1D 00 9B 00 83 FF 00 00 13 00 00 00 00 00 03 D2 01 00 1D 00 64 00 AC 00 9B 00 00 00 00 00 00 00 00 00";

        String[] byteStrings = packet.split(" ");
        int length = byteStrings.length;
        byte[] byteArray = new byte[length];

        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        for (int i = 0; i < length; i++) {
            buffer.put(i, (byte) Integer.parseInt(byteStrings[i], 16));
        }

        System.out.println(Arrays.toString(byteArray));
    }
}
