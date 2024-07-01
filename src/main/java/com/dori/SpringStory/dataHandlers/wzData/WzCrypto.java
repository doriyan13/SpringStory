package com.dori.SpringStory.dataHandlers.wzData;

import com.dori.SpringStory.constants.WzConstants;
import lombok.Data;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

import static com.dori.SpringStory.constants.WzConstants.BATCH_SIZE;

@Data
public class WzCrypto {
    private Cipher cipher;
    private byte[] cipherMask;

    public WzCrypto(Cipher cipher) {
        this.cipher = cipher;
        this.cipherMask = new byte[]{};
    }

    public void cryptAscii(byte[] data) throws ShortBufferException {
        ensureSize(data.length);
        byte mask = (byte) 0xAA;
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (data[i] ^ cipherMask[i] ^ mask);
            mask++;
        }
    }

    public void cryptUnicode(byte[] data) throws ShortBufferException {
        ensureSize(data.length);
        short mask = (short) 0xAAAA;
        for (int i = 0; i < data.length; i += 2) {
            data[i] = (byte) (data[i] ^ cipherMask[i] ^ (mask & 0xFF));
            data[i + 1] = (byte) (data[i + 1] ^ cipherMask[i + 1] ^ (mask >> 8));
            mask++;
        }
    }

    private synchronized void ensureSize(int size) throws ShortBufferException {
        int curSize = cipherMask.length;
        if (curSize >= size) {
            return;
        }
        int newSize = ((size / BATCH_SIZE) + 1) * BATCH_SIZE;
        byte[] newMask = new byte[newSize];

        if (cipher != null) {
            System.arraycopy(cipherMask, 0, newMask, 0, curSize);
            byte[] block = new byte[16];
            for (int i = curSize; i < newSize; i += 16) {
                cipher.update(block, 0, 16, newMask, i);
            }
        }

        this.cipherMask = newMask;
    }

    public static WzCrypto fromIv(byte[] iv) throws Exception {
        // Empty IV
        if (Arrays.equals(iv, WzConstants.WZ_EMPTY_IV)) {
            return new WzCrypto(null);
        }
        // Initialize key
        byte[] trimmedKey = new byte[32];
        for (int i = 0; i < 128; i += 16) {
            trimmedKey[i / 4] = WzConstants.AES_USER_KEY[i];
        }
        SecretKey key = new SecretKeySpec(trimmedKey, "AES");
        // Initialize IV
        byte[] expandedIv = new byte[16];
        for (int i = 0; i < expandedIv.length; i += iv.length) {
            System.arraycopy(iv, 0, expandedIv, i, iv.length);
        }
        IvParameterSpec ivParam = new IvParameterSpec(expandedIv);
        // Create cipher and return WzCrypto object
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParam);
        return new WzCrypto(cipher);
    }
}
