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

import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.Packet;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.jetbrains.annotations.NotNull;


/**
 * Abstraction for Netty channels that contains some attribute keys
 * for important resources used by the net.springStory.ms.client during encryption,
 * decryption, and general functions. <B>Note: Some methods cannot be
 * overridden by descendants due to the nature of the functionality they
 * provide</B>
 * 
 * @author Zygon
 */
public class NettyClient {
    /**
     * Attribute key for this NettyClient object.
     */
    public static final AttributeKey<NettyClient> CLIENT_KEY = AttributeKey.valueOf("C");

    /**
     * Stored length used for packet decryption. This is used for
     * storing the packet length for the next packet that is readable.
     * Since TCP sessions ensure that all data arrives to the server in order,
     * we can decodeByte packet data in the correct order.
     */
    private int storedLength = -1;
    /**
     * Channel object associated with this specific client. Used for all
     * I/O operations regarding a MapleStory game session.
     */
    protected final Channel ch;
    
    /**
     * InPacket object for this specific session since this can help
     * scaling compared to keeping OutPacket for each session.
     */
    private final InPacket r;

    public NettyClient() {
        ch = null;
        r = new InPacket();
    }

    /**
     * Construct a new NettyClient with the corresponding Channel that
     * will be used to write to as well as the send and recv seeds or IVs.
     * @param c the channel object associated with this net.springStory.ms.client session.
     */
    public NettyClient(Channel c) {
        ch = c;
        r = new InPacket();
    }
    
    /**
     * Gets the InPacket object associated with this NettyClient.
     * @return a net.springStory.ms.connection.packet reader.
     */
    public final InPacket getReader() {
        return r;
    }
    
    /**
     * Gets the stored length for the next net.springStory.ms.connection.packet to be read. Used as
     * a decoding state variable to determine when it is ok to proceed with
     * decoding a net.springStory.ms.connection.packet.
     * @return stored length for next net.springStory.ms.connection.packet.
     */
    public final int getStoredLength() {
        return storedLength;
    }
    
    /**
     * Sets the stored length for the next net.springStory.ms.connection.packet to be read.
     * @param val length of the next net.springStory.ms.connection.packet to be read.
     */
    public final void setStoredLength(int val) {
        storedLength = val;
    }
    
    /**
     * Writes a message to the channel. Gets encoded later in the
     * pipeline.
     * @param msg the message to be sent.
     */
    public void write(@NotNull Packet msg) {
        ch.writeAndFlush(msg);
    }
    
    /**
     * Closes this channel and session.
     */
    public void close() {
        ch.close();
    }
    
    /**
     * Gets the remote IP address for this session.
     * @return the remote IP address.
     */
    public String getIP() {
        return ch.remoteAddress().toString().split(":")[0].substring(1);
    }
}
