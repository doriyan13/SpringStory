package com.dori.Dori90v.connection.netty;

import com.dori.Dori90v.connection.crypto.MapleCrypto;
import com.dori.Dori90v.connection.packet.Packet;
import com.dori.Dori90v.connection.packet.headers.InHeader;
import com.dori.Dori90v.connection.packet.headers.OutHeader;
import com.dori.Dori90v.logger.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Implementation of a Netty encoder pattern so that encryption of MapleStory
 * packets is possible.
 *
 * @author Zygon
 * @author Dori.
 */
public final class PacketEncoder extends MessageToByteEncoder<Packet> {
    private static final Logger logger = new Logger(PacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext chc, Packet outPacket, ByteBuf bb) {
        byte[] data = outPacket.getData();
        NettyClient c = chc.channel().attr(NettyClient.CLIENT_KEY).get();
        MapleCrypto mCr = chc.channel().attr(NettyClient.CRYPTO_KEY).get();

        if (c != null) {
            OutHeader outHeader = OutHeader.getOutHeaderByOp(outPacket.getHeader());
            if(!OutHeader.isSpamHeader(outHeader)) {
                logger.sent(String.valueOf(outPacket.getHeader()), "0x" + Integer.toHexString(outPacket.getHeader()).toUpperCase(), outHeader.name(), outPacket.toString());
            }
            byte[] head = mCr.getOutPacketHeader(data.length);
            MapleCrypto.encryptData(data);

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
