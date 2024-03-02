package com.dori.SpringStory.connection.crypto;

import com.dori.SpringStory.utils.CryptoUtils;
import io.netty.buffer.ByteBuf;

public interface ShandaCipher {

    static void encryptData(ByteBuf buf,
                            int offset,
                            int length) {
        for (int j = 0; j < 6; j++) {
            byte remember = 0;
            byte dataLength = (byte) (length & 0xFF);
            if (j % 2 == 0) {
                for (int i = 0; i < length; i++) {
                    int m = i + offset;
                    byte cur = buf.getByte(m);
                    cur = CryptoUtils.byteRotateLeft(cur, 3);
                    cur += dataLength;
                    cur ^= remember;
                    remember = cur;
                    cur = CryptoUtils.byteRotateRight(cur, (int) dataLength & 0xFF);
                    cur = ((byte) ((~cur) & 0xFF));
                    cur += 0x48;
                    dataLength--;
                    buf.setByte(m, cur);
                }
            } else {
                for (int i = length - 1; i >= 0; i--) {
                    int m = i + offset;
                    byte cur = buf.getByte(m);
                    cur = CryptoUtils.byteRotateLeft(cur, 4);
                    cur += dataLength;
                    cur ^= remember;
                    remember = cur;
                    cur ^= 0x13;
                    cur = CryptoUtils.byteRotateRight(cur, 3);
                    dataLength--;
                    buf.setByte(m, cur);
                }
            }
        }
    }

    static void decryptData(ByteBuf buf,
                            int offset,
                            int length) {
        for (int j = 1; j <= 6; j++) {
            byte remember = 0;
            byte dataLength = (byte) (length & 0xFF);
            byte nextRemember;
            if (j % 2 == 0) {
                for (int i = 0; i < length; i++) {
                    int m = i + offset;
                    byte cur = buf.getByte(m);
                    cur -= 0x48;
                    cur = ((byte) ((~cur) & 0xFF));
                    cur = CryptoUtils.byteRotateLeft(cur, (int) dataLength & 0xFF);
                    nextRemember = cur;
                    cur ^= remember;
                    remember = nextRemember;
                    cur -= dataLength;
                    cur = CryptoUtils.byteRotateRight(cur, 3);
                    buf.setByte(m, cur);
                    dataLength--;
                }
            } else {
                for (int i = length - 1; i >= 0; i--) {
                    int m = i + offset;
                    byte cur = buf.getByte(m);
                    cur = CryptoUtils.byteRotateLeft(cur, 3);
                    cur ^= 0x13;
                    nextRemember = cur;
                    cur ^= remember;
                    remember = nextRemember;
                    cur -= dataLength;
                    cur = CryptoUtils.byteRotateRight(cur, 4);
                    buf.setByte(m, cur);
                    dataLength--;
                }
            }
        }
    }

}
