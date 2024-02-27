package com.dori.SpringStory.connection.netty;

import com.dori.SpringStory.connection.crypto.ShandaCipher;
import com.dori.SpringStory.connection.crypto.ShroomAESCipher;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.logger.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import static com.dori.SpringStory.constants.ServerConstants.ENABLE_ENCRYPTION;

public class PacketDecoder extends ReplayingDecoder<Integer> {
    private static final Logger log = new Logger(PacketDecoder.class);
    public static final int MAX_PACKET_LEN = 2 * 4096;

    private final ShroomAESCipher receiveCypher;

    public PacketDecoder(ShroomAESCipher receiveCypher) {
        super(-1);

        this.receiveCypher = receiveCypher;
    }

    @Override
    protected void decode(ChannelHandlerContext chc,
                          ByteBuf inPacketData,
                          List<Object> out) {
        int packetLength = state();
        if(packetLength == -1) {
            final int packetLen = receiveCypher.decodeHeader(inPacketData.readIntLE());
            checkpoint(packetLen);
            if(packetLen < 0 || packetLen > MAX_PACKET_LEN)
                throw new EncoderException("Packet length out of limits");
            packetLength = packetLen;
        }
        ByteBuf pktBuf = inPacketData.readRetainedSlice(packetLength);
        this.checkpoint(-1);
        receiveCypher.crypt(pktBuf, 0, packetLength);
        if (ENABLE_ENCRYPTION) {
            ShandaCipher.decryptData(pktBuf, packetLength);
        }
        out.add(new InPacket(pktBuf));
    }
}
