package com.dori.SpringStory.connection.netty;

import com.dori.SpringStory.connection.crypto.ShandaCipher;
import com.dori.SpringStory.connection.crypto.ShroomAESCipher;
import com.dori.SpringStory.connection.packet.OutPacket;
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
public final class PacketEncoder extends MessageToByteEncoder<OutPacket> {
    private static final Logger logger = new Logger(PacketEncoder.class);
    private static final Map<Integer, OutHeader> outPacketHeaders = new HashMap<>();
    private final ShroomAESCipher sendCypher;

    public PacketEncoder(ShroomAESCipher sendCypher) {
        this.sendCypher = sendCypher;
    }

    public static void initOutPacketOpcodesHandling() {
        Arrays.stream(OutHeader.values()).forEach(opcode -> outPacketHeaders.put(opcode.getValue(), opcode));
    }

    @Override
    protected void encode(ChannelHandlerContext chc, OutPacket outPacket, ByteBuf bb) {
        try {
            ByteBuf bufferData = outPacket.getBufferData();
            int len = bufferData.readableBytes();
            NettyClient c = chc.channel().attr(NettyClient.CLIENT_KEY).get();
            if (c != null) {
                OutHeader outHeader = outPacketHeaders.get(outPacket.getHeader());
                if (!OutHeader.isSpamHeader(outHeader)) {
                    logger.sent(String.valueOf(outPacket.getHeader()), "0x" + Integer.toHexString(outPacket.getHeader()).toUpperCase(), outHeader.name(), outPacket.toString());
                }
                bb.writeIntLE(sendCypher.encodeHeader(len));
                if (ENABLE_ENCRYPTION) {
                    ShandaCipher.encryptData(bufferData, len);
                }
                sendCypher.crypt(bufferData, 0, len);
                c.acquireEncoderState();
                try {
                    bb.writeBytes(bufferData);
                } finally {
                    c.releaseEncodeState();
                }
            } else {
                logger.debug("Plain sending packet: " + outPacket);
                bb.writeBytes(bufferData);
            }
        } catch (Exception e) {
            logger.error("Error occurred while parsing OutPacket!! ", e);
        } finally {
            outPacket.release();
        }
    }
}
