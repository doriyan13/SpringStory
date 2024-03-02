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
package com.dori.SpringStory.connection.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Represents a packet to be sent over a TCP socket for MapleStory.
 * Very simply, it is an abstraction of raw data that applies some extra 
 * functionality because it is a MapleStory packet.
 *
 */
public class Packet implements ByteBufHolder {
    public static final int MAX_PKT_LEN = 4096 * 2;
    protected static final int MAX_BUF_LEN = 2048;
    protected static final Charset CHARSET = StandardCharsets.ISO_8859_1;
    protected ByteBuf buf;

    public Packet(ByteBuf buf) {
        this.buf = buf;
    }

    public int getLength() {
        return this.buf.readableBytes();
    }

    public int getHeader() {
        if (this.buf.readableBytes() < 2) {
            return 0xFFFF;
        }
        return this.buf.getShortLE(0);
    }
    
    @Override
    public String toString() {
        return "[Pck] | " + this.buf.toString();
    }

    @Override
    public int refCnt() {
        return this.buf.refCnt();
    }

    @Override
    public boolean release(int arg0) {
        return this.buf.release(arg0);
    }

    @Override
    public ByteBuf content() {
        return this.buf;
    }

    @Override
    public Packet copy() {
        return new Packet(this.buf.copy());
    }

    @Override
    public Packet duplicate() {
        return new Packet(this.buf.duplicate());
    }

    @Override
    public ByteBufHolder retainedDuplicate() {
        return new Packet(this.buf.retainedDuplicate());
    }

    @Override
    public ByteBufHolder replace(ByteBuf content) {
        return new Packet(content);
    }

    @Override
    public ByteBufHolder retain() {
        return new Packet(this.buf.retain());
    }

    @Override
    public ByteBufHolder retain(int increment) {
        return new Packet(this.buf.retain(increment));
    }

    @Override
    public ByteBufHolder touch() {      
        return new Packet(this.buf.touch());
    }

    @Override
    public ByteBufHolder touch(Object hint) {
        return new Packet(this.buf.touch(hint));
    }

    @Override
    public boolean release() {
        return this.buf.release();
    }

}
