package com.dori.SpringStory.connection.crypto;

import com.dori.SpringStory.utils.CryptoUtils;
import io.netty.buffer.ByteBuf;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ShroomAESCipher {
    private final static SecretKeySpec AES_KEY = new SecretKeySpec(
            new byte[]{
                    0x13, 0x00, 0x00, 0x00,
                    0x08, 0x00, 0x00, 0x00,
                    0x06, 0x00, 0x00, 0x00,
                    (byte) 0xB4, 0x00, 0x00, 0x00,
                    0x1B, 0x00, 0x00, 0x00,
                    0x0F, 0x00, 0x00, 0x00,
                    0x33, 0x00, 0x00, 0x00,
                    0x52, 0x00, 0x00, 0x00}, "AES");

    public static final byte[] IG_SHUFFLE = new byte[]{
            (byte) 0xEC, (byte) 0x3F, (byte) 0x77, (byte) 0xA4, (byte) 0x45, (byte) 0xD0, (byte) 0x71, (byte) 0xBF,
            (byte) 0xB7, (byte) 0x98, (byte) 0x20, (byte) 0xFC, (byte) 0x4B, (byte) 0xE9, (byte) 0xB3, (byte) 0xE1,
            (byte) 0x5C, (byte) 0x22, (byte) 0xF7, (byte) 0x0C, (byte) 0x44, (byte) 0x1B, (byte) 0x81, (byte) 0xBD,
            (byte) 0x63, (byte) 0x8D, (byte) 0xD4, (byte) 0xC3, (byte) 0xF2, (byte) 0x10, (byte) 0x19, (byte) 0xE0,
            (byte) 0xFB, (byte) 0xA1, (byte) 0x6E, (byte) 0x66, (byte) 0xEA, (byte) 0xAE, (byte) 0xD6, (byte) 0xCE,
            (byte) 0x06, (byte) 0x18, (byte) 0x4E, (byte) 0xEB, (byte) 0x78, (byte) 0x95, (byte) 0xDB, (byte) 0xBA,
            (byte) 0xB6, (byte) 0x42, (byte) 0x7A, (byte) 0x2A, (byte) 0x83, (byte) 0x0B, (byte) 0x54, (byte) 0x67,
            (byte) 0x6D, (byte) 0xE8, (byte) 0x65, (byte) 0xE7, (byte) 0x2F, (byte) 0x07, (byte) 0xF3, (byte) 0xAA,
            (byte) 0x27, (byte) 0x7B, (byte) 0x85, (byte) 0xB0, (byte) 0x26, (byte) 0xFD, (byte) 0x8B, (byte) 0xA9,
            (byte) 0xFA, (byte) 0xBE, (byte) 0xA8, (byte) 0xD7, (byte) 0xCB, (byte) 0xCC, (byte) 0x92, (byte) 0xDA,
            (byte) 0xF9, (byte) 0x93, (byte) 0x60, (byte) 0x2D, (byte) 0xDD, (byte) 0xD2, (byte) 0xA2, (byte) 0x9B,
            (byte) 0x39, (byte) 0x5F, (byte) 0x82, (byte) 0x21, (byte) 0x4C, (byte) 0x69, (byte) 0xF8, (byte) 0x31,
            (byte) 0x87, (byte) 0xEE, (byte) 0x8E, (byte) 0xAD, (byte) 0x8C, (byte) 0x6A, (byte) 0xBC, (byte) 0xB5,
            (byte) 0x6B, (byte) 0x59, (byte) 0x13, (byte) 0xF1, (byte) 0x04, (byte) 0x00, (byte) 0xF6, (byte) 0x5A,
            (byte) 0x35, (byte) 0x79, (byte) 0x48, (byte) 0x8F, (byte) 0x15, (byte) 0xCD, (byte) 0x97, (byte) 0x57,
            (byte) 0x12, (byte) 0x3E, (byte) 0x37, (byte) 0xFF, (byte) 0x9D, (byte) 0x4F, (byte) 0x51, (byte) 0xF5,
            (byte) 0xA3, (byte) 0x70, (byte) 0xBB, (byte) 0x14, (byte) 0x75, (byte) 0xC2, (byte) 0xB8, (byte) 0x72,
            (byte) 0xC0, (byte) 0xED, (byte) 0x7D, (byte) 0x68, (byte) 0xC9, (byte) 0x2E, (byte) 0x0D, (byte) 0x62,
            (byte) 0x46, (byte) 0x17, (byte) 0x11, (byte) 0x4D, (byte) 0x6C, (byte) 0xC4, (byte) 0x7E, (byte) 0x53,
            (byte) 0xC1, (byte) 0x25, (byte) 0xC7, (byte) 0x9A, (byte) 0x1C, (byte) 0x88, (byte) 0x58, (byte) 0x2C,
            (byte) 0x89, (byte) 0xDC, (byte) 0x02, (byte) 0x64, (byte) 0x40, (byte) 0x01, (byte) 0x5D, (byte) 0x38,
            (byte) 0xA5, (byte) 0xE2, (byte) 0xAF, (byte) 0x55, (byte) 0xD5, (byte) 0xEF, (byte) 0x1A, (byte) 0x7C,
            (byte) 0xA7, (byte) 0x5B, (byte) 0xA6, (byte) 0x6F, (byte) 0x86, (byte) 0x9F, (byte) 0x73, (byte) 0xE6,
            (byte) 0x0A, (byte) 0xDE, (byte) 0x2B, (byte) 0x99, (byte) 0x4A, (byte) 0x47, (byte) 0x9C, (byte) 0xDF,
            (byte) 0x09, (byte) 0x76, (byte) 0x9E, (byte) 0x30, (byte) 0x0E, (byte) 0xE4, (byte) 0xB2, (byte) 0x94,
            (byte) 0xA0, (byte) 0x3B, (byte) 0x34, (byte) 0x1D, (byte) 0x28, (byte) 0x0F, (byte) 0x36, (byte) 0xE3,
            (byte) 0x23, (byte) 0xB4, (byte) 0x03, (byte) 0xD8, (byte) 0x90, (byte) 0xC8, (byte) 0x3C, (byte) 0xFE,
            (byte) 0x5E, (byte) 0x32, (byte) 0x24, (byte) 0x50, (byte) 0x1F, (byte) 0x3A, (byte) 0x43, (byte) 0x8A,
            (byte) 0x96, (byte) 0x41, (byte) 0x74, (byte) 0xAC, (byte) 0x52, (byte) 0x33, (byte) 0xF0, (byte) 0xD9,
            (byte) 0x29, (byte) 0x80, (byte) 0xB1, (byte) 0x16, (byte) 0xD3, (byte) 0xAB, (byte) 0x91, (byte) 0xB9,
            (byte) 0x84, (byte) 0x7F, (byte) 0x61, (byte) 0x1E, (byte) 0xCF, (byte) 0xC5, (byte) 0xD1, (byte) 0x56,
            (byte) 0x3D, (byte) 0xCA, (byte) 0xF4, (byte) 0x05, (byte) 0xC6, (byte) 0xE5, (byte) 0x08, (byte) 0x49};

    public static final byte[] IG_SEED = { (byte) 0xf2, 0x53, (byte) 0x50, (byte) 0xc6 };
    
    private static Cipher cipher = initCipher();

    private static Cipher initCipher() {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, AES_KEY);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private final short mapleVersion;
    private byte[] iv;

    public ShroomAESCipher(InitializationVector iv, short mapleVersion) {
        this.iv = iv.getBytes();
        this.mapleVersion = mapleVersion;
    }

    public ShroomAESCipher(byte[] iv, short mapleVersion) {
        this.iv = iv;
        this.mapleVersion = mapleVersion;
    }


    private void nextKey(byte[] key) {
        try {
            byte[] newKey =  cipher.doFinal(key);
            System.arraycopy(newKey, 0, key, 0, 16);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void cryptBlock(ByteBuf buf, int offset, int length, byte[] key) {
        final int CHUNK_LEN = 16;
        int chunks = length / CHUNK_LEN;
        int remaining = length % CHUNK_LEN;

        for (int i = 0; i < chunks; i++) {
            nextKey(key);
            for(int j = 0; j < CHUNK_LEN; j++) {
                int m = offset + j;
                buf.setByte(m, buf.getByte(m) ^ key[j]);
            }
            offset += CHUNK_LEN;
        }

        nextKey(key);
        for(int i = 0; i < remaining; i++) {
            int m = offset + i;
            buf.setByte(m, (byte)(buf.getByte(m) ^ key[i]));
        }
    }

    public void crypt(ByteBuf data, int offset, int length) {
        final int FIRST_BLOCK_LEN = 0x5B0;
        final int BLOCK_LEN = FIRST_BLOCK_LEN + 4;

        // Expand iv to key
        byte[] key = expandToKey();

        // Decode only first block, fast path for most packets
        if(length < FIRST_BLOCK_LEN) {
            cryptBlock(data, offset, length, key.clone());
            // Update iv
            updateIv();
            return;
        }

        // Decode first block
        cryptBlock(data, offset, FIRST_BLOCK_LEN, key.clone());
        offset += FIRST_BLOCK_LEN;
        length -= FIRST_BLOCK_LEN;

        int blocks = length / BLOCK_LEN;
        int remaining = length % BLOCK_LEN;

        // Decode all pending blocks
        for(int i = 0; i < blocks; i++) {
            cryptBlock(data, offset, BLOCK_LEN, key.clone());
            offset += BLOCK_LEN;
        }

        // Decode last block
        if(remaining > 0) {
            cryptBlock(data, offset, remaining, key.clone());
        }

        // Update iv
        updateIv();
    }

    private void updateIv() {
        this.iv = CryptoUtils.intToLittleEndian(igHash(this.iv));
    }

    public byte[] expandToKey() {
        final int KEY_LEN = 16;
        byte[] ret = new byte[KEY_LEN];
        for (int x = 0; x < KEY_LEN; x++) {
            ret[x] = this.iv[x % 4];
        }
        return ret;
    }

    public int encodeHeader(int length) {
        short keyHigh = CryptoUtils.littleEndianToShort(new byte[] {
                iv[2],
                iv[3]
        });

        int low = keyHigh ^ this.mapleVersion;
        int high = low ^ length;
        return  low & 0xFFFF | (high << 16);
    }


    public int decodeHeader(int hdr) {
        short keyHigh = CryptoUtils.littleEndianToShort(new byte[] {
                iv[2],
                iv[3]
        });
        short hdrLow = (short) (hdr & 0xFFFF);
        short hdrHigh = (short) ((hdr >> 16) & 0xFFFF);
        short versionCheck = (short) (keyHigh ^ hdrLow);
        if(versionCheck != mapleVersion)
            throw new RuntimeException("Attempted to decode a packet with an invalid header " + hdr);
        return hdrHigh ^ hdrLow;
    }

    @Override
    public String toString() {
        return "IV: " +  new InitializationVector(iv) + " MapleVersion: " + mapleVersion;
    }

    public static int igHash(byte[] data) {
        int hash = CryptoUtils.littleEndianToInt(IG_SEED);
        for (byte b : data) {
            hash = igHashUpdate(hash, b);
        }
        return hash;
    }

    static byte shuffle(byte b) {
        return IG_SHUFFLE[b & 0xFF];
    }

    public  static int igHashUpdate(int key, byte b) {
        byte[] k = CryptoUtils.intToLittleEndian(key);
        k[0] += (byte) (shuffle(k[1]) - b);
        k[1] -= (byte) (k[2] ^ shuffle(b));
        k[2] ^= (byte) (shuffle(k[3]) + b);
        k[3] -= (byte) (k[0] - shuffle(b));

        return Integer.rotateLeft(CryptoUtils.littleEndianToInt(k), 3);
    }
}
