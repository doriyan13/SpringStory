/*
    This file is part of Desu: MapleStory v62 Server Emulator
    Copyright (C) 2014  Zygon <watchmystarz@hotmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.dori.SpringStory.connection.netty;

import com.dori.SpringStory.connection.crypto.MapleCrypto;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.logger.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static com.dori.SpringStory.constants.ServerConstants.ENABLE_ENCRYPTION;

/**
 * Implementation of a Netty decoder pattern so that decryption of MapleStory
 * packets is possible. Follows steps using the special MapleAES as well as
 * ShandaCrypto (which became non-used after v149.2 in GMS).
 *
 * @author Zygon
 */
public class PacketDecoder extends ByteToMessageDecoder {
    private static final Logger log = new Logger(PacketDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf in, List<Object> out) {
        NettyClient c = chc.channel().attr(NettyClient.CLIENT_KEY).get();
        MapleCrypto mCr = chc.channel().attr(NettyClient.CRYPTO_KEY).get();
        if (c != null) {
            if (c.getStoredLength() == -1) {
                if (in.readableBytes() >= 4) {
                    int h = in.readInt();
                    if (ENABLE_ENCRYPTION && !mCr.checkInPacket(h)) {
                        log.error(String.format("[PacketDecoder] | Incorrect packet seq! Dropping client %s.", c.getIP()));
                        c.close();
                        return;
                    }
                    c.setStoredLength(MapleCrypto.getPacketLength(h));
                } else {
                    return;
                }
            }
            if (in.readableBytes() >= c.getStoredLength()) {
                // don't need to create a byte[] and just use is a byteBuffer -
                byte[] dec = new byte[c.getStoredLength()];
                in.readBytes(dec);
                c.setStoredLength(-1);
                
                dec = mCr.cryptInPacket(dec);
                if (ENABLE_ENCRYPTION) {
                    MapleCrypto.decryptData(dec);
                }

                InPacket inPacket = new InPacket(dec);
                out.add(inPacket);
            }
        }
    }
}
