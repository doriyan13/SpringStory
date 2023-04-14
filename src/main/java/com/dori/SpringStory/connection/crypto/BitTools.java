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
package com.dori.SpringStory.connection.crypto;

/**
 * Artifact brought over from Invictus.  Contains multiple tools
 * that are used for bit-wise logic. Will not be documented since the 
 * logic is pretty straightforward.
 */
public final class BitTools {

    private BitTools() {
    }

    public static byte[] multiplyBytes(byte[] in, int count, int mul) {
        final int size = count * mul;
        byte[] ret = new byte[size];
        for (int x = 0; x < size; x++) {
            ret[x] = in[x % count];
        }
        return ret;
    }

    public static byte rollLeft(byte in, int count) {
        return (byte) (((in & 0xFF) << (count % 8) & 0xFF)
                | ((in & 0xFF) << (count % 8) >> 8));
    }

    public static byte rollRight(byte in, int count) {
        int tmp = (int) in & 0xFF;
        tmp = (tmp << 8) >>> (count % 8);
        return (byte) ((tmp & 0xFF) | (tmp >>> 8));
    }
}
