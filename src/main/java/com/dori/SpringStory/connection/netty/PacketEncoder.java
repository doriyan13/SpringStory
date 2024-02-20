package com.dori.SpringStory.connection.netty;

import com.dori.SpringStory.connection.crypto.MapleCrypto;
import com.dori.SpringStory.connection.packet.Packet;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.logger.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.dori.SpringStory.constants.ServerConstants.ENABLE_ENCRYPTION;

/**
 * Implementation of a Netty encoder pattern so that encryption of MapleStory
 * packets is possible.
 *
 * @author Zygon
 * @author Dori.
 */
public final class PacketEncoder extends MessageToByteEncoder<Packet> {
    private static final Logger logger = new Logger(PacketEncoder.class);
    private static final Map<Integer,OutHeader> outPacketHeaders = new HashMap<>();

    public static void initOutPacketOpcodesHandling() {
        Arrays.stream(OutHeader.values()).forEach(opcode -> outPacketHeaders.put(opcode.getValue(),opcode));
    }

    @Override
    protected void encode(ChannelHandlerContext chc, Packet outPacket, ByteBuf bb) {
        byte[] data = outPacket.getData();
        NettyClient c = chc.channel().attr(NettyClient.CLIENT_KEY).get();
        MapleCrypto mCr = chc.channel().attr(NettyClient.CRYPTO_KEY).get();

        if (c != null) {
            OutHeader outHeader = outPacketHeaders.get(outPacket.getHeader());
            if(!OutHeader.isSpamHeader(outHeader)) {
                logger.sent(String.valueOf(outPacket.getHeader()), "0x" + Integer.toHexString(outPacket.getHeader()).toUpperCase(), outHeader.name(), outPacket.toString());
            }
            byte[] head = mCr.getOutPacketHeader(data.length);
            if (ENABLE_ENCRYPTION) {
                MapleCrypto.encryptData(data);
            }

            c.acquireEncoderState();
            try {
                mCr.cryptOutPacket(data);
            } finally {
                c.releaseEncodeState();
            }
            
            bb.writeBytes(head);
            bb.writeBytes(data);
            
        } else {
            logger.debug("Plain sending packet: " + outPacket);
            bb.writeBytes(data);
        }
    }
}
