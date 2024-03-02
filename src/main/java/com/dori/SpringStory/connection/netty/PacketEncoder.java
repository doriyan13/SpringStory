package com.dori.SpringStory.connection.netty;

import com.dori.SpringStory.connection.crypto.InitializationVector;
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
 * @author Joo
 * @author Dori.
 */
public final class PacketEncoder extends MessageToByteEncoder<OutPacket> {
    private static final Logger logger = new Logger(PacketEncoder.class);
    private static final Map<Integer, OutHeader> outPacketHeaders = new HashMap<>();
    private final ShroomAESCipher cipher;
    private boolean first;

    public PacketEncoder(InitializationVector iv, short version) {
        this(new ShroomAESCipher(iv, version));
    }

    public PacketEncoder(ShroomAESCipher sendCypher) {
        this.cipher = sendCypher;
        this.first = true;
    }

    public static void initOutPacketOpcodesHandling() {
        Arrays.stream(OutHeader.values()).forEach(opcode -> outPacketHeaders.put(opcode.getValue(), opcode));
    }

    void encodeToBuffer(OutPacket pkt, ByteBuf out) {
        int len = pkt.getLength();
        out.writeIntLE(cipher.encodeHeader(len));
        int ix = out.writerIndex();
        out.writeBytes(pkt.content());

        // Encrypt shanda
        if (ENABLE_ENCRYPTION) {
            ShandaCipher.encryptData(out, ix, len);
        }

        // Encrypt aes
        cipher.crypt(out, ix, len);
    }

    @Override
    protected void encode(ChannelHandlerContext chc, OutPacket pkt, ByteBuf out) {
        try {
            // Skip encryption for the first packet(Handshake)
            if (!first) {
                OutHeader outHeader = outPacketHeaders.get(pkt.getHeader());
                if (!OutHeader.isSpamHeader(outHeader)) {
                    logger.sent(String.valueOf(pkt.getHeader()),
                            "0x" + Integer.toHexString(pkt.getHeader()).toUpperCase(), outHeader.name(),
                            pkt.toString());
                }
                this.encodeToBuffer(pkt, out);
            } else {
                logger.debug("Plain sending packet: " + pkt);
                out.writeBytes(pkt.content());
                this.first = false;
            }
        } catch (Exception e) {
            logger.error("Error occurred while enncoding OutPacket!", e);
        } finally {
            pkt.release();
        }
    }
}
