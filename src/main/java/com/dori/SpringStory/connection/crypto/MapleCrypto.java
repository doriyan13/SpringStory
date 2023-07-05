package com.dori.SpringStory.connection.crypto;

import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.HexTool;
import lombok.Data;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * Artifact from Invictus that improved the old MapleAESOFB by
 * making it easily scaled with session groups while using Apache's 
 * MINA library. Ported over for usage within a Netty setup since it will
 * work exactly the same.
 *
 * @author OdinMS (original author)
 * @author Zygon (ported and modified)
 * @author Dori
 */
@Data
public final class MapleCrypto {

    private Cipher cipher;
    /**
     * Used for checking the packets being sent and received. These are
     * initialized at the starting part of the program by providing the
     * current version the server is running.
     *
     */
    private static short sVersion, rVersion;
    private byte[] riv,siv;
    /**
     * Used for renewing the cryptography seed for sending or receiving 
     * packets.
     *
     */
    private static final byte[] funnyBytes = new byte[]{
            (byte) 0xEC, (byte) 0x3F, (byte) 0x77, (byte) 0xA4, (byte) 0x45, (byte) 0xD0, (byte) 0x71, (byte) 0xBF, (byte) 0xB7, (byte) 0x98, (byte) 0x20, (byte) 0xFC,
            (byte) 0x4B, (byte) 0xE9, (byte) 0xB3, (byte) 0xE1, (byte) 0x5C, (byte) 0x22, (byte) 0xF7, (byte) 0x0C, (byte) 0x44, (byte) 0x1B, (byte) 0x81, (byte) 0xBD, (byte) 0x63, (byte) 0x8D, (byte) 0xD4, (byte) 0xC3,
            (byte) 0xF2, (byte) 0x10, (byte) 0x19, (byte) 0xE0, (byte) 0xFB, (byte) 0xA1, (byte) 0x6E, (byte) 0x66, (byte) 0xEA, (byte) 0xAE, (byte) 0xD6, (byte) 0xCE, (byte) 0x06, (byte) 0x18, (byte) 0x4E, (byte) 0xEB,
            (byte) 0x78, (byte) 0x95, (byte) 0xDB, (byte) 0xBA, (byte) 0xB6, (byte) 0x42, (byte) 0x7A, (byte) 0x2A, (byte) 0x83, (byte) 0x0B, (byte) 0x54, (byte) 0x67, (byte) 0x6D, (byte) 0xE8, (byte) 0x65, (byte) 0xE7,
            (byte) 0x2F, (byte) 0x07, (byte) 0xF3, (byte) 0xAA, (byte) 0x27, (byte) 0x7B, (byte) 0x85, (byte) 0xB0, (byte) 0x26, (byte) 0xFD, (byte) 0x8B, (byte) 0xA9, (byte) 0xFA, (byte) 0xBE, (byte) 0xA8, (byte) 0xD7,
            (byte) 0xCB, (byte) 0xCC, (byte) 0x92, (byte) 0xDA, (byte) 0xF9, (byte) 0x93, (byte) 0x60, (byte) 0x2D, (byte) 0xDD, (byte) 0xD2, (byte) 0xA2, (byte) 0x9B, (byte) 0x39, (byte) 0x5F, (byte) 0x82, (byte) 0x21,
            (byte) 0x4C, (byte) 0x69, (byte) 0xF8, (byte) 0x31, (byte) 0x87, (byte) 0xEE, (byte) 0x8E, (byte) 0xAD, (byte) 0x8C, (byte) 0x6A, (byte) 0xBC, (byte) 0xB5, (byte) 0x6B, (byte) 0x59, (byte) 0x13, (byte) 0xF1,
            (byte) 0x04, (byte) 0x00, (byte) 0xF6, (byte) 0x5A, (byte) 0x35, (byte) 0x79, (byte) 0x48, (byte) 0x8F, (byte) 0x15, (byte) 0xCD, (byte) 0x97, (byte) 0x57, (byte) 0x12, (byte) 0x3E, (byte) 0x37, (byte) 0xFF,
            (byte) 0x9D, (byte) 0x4F, (byte) 0x51, (byte) 0xF5, (byte) 0xA3, (byte) 0x70, (byte) 0xBB, (byte) 0x14, (byte) 0x75, (byte) 0xC2, (byte) 0xB8, (byte) 0x72, (byte) 0xC0, (byte) 0xED, (byte) 0x7D, (byte) 0x68,
            (byte) 0xC9, (byte) 0x2E, (byte) 0x0D, (byte) 0x62, (byte) 0x46, (byte) 0x17, (byte) 0x11, (byte) 0x4D, (byte) 0x6C, (byte) 0xC4, (byte) 0x7E, (byte) 0x53, (byte) 0xC1, (byte) 0x25, (byte) 0xC7, (byte) 0x9A,
            (byte) 0x1C, (byte) 0x88, (byte) 0x58, (byte) 0x2C, (byte) 0x89, (byte) 0xDC, (byte) 0x02, (byte) 0x64, (byte) 0x40, (byte) 0x01, (byte) 0x5D, (byte) 0x38, (byte) 0xA5, (byte) 0xE2, (byte) 0xAF, (byte) 0x55,
            (byte) 0xD5, (byte) 0xEF, (byte) 0x1A, (byte) 0x7C, (byte) 0xA7, (byte) 0x5B, (byte) 0xA6, (byte) 0x6F, (byte) 0x86, (byte) 0x9F, (byte) 0x73, (byte) 0xE6, (byte) 0x0A, (byte) 0xDE, (byte) 0x2B, (byte) 0x99,
            (byte) 0x4A, (byte) 0x47, (byte) 0x9C, (byte) 0xDF, (byte) 0x09, (byte) 0x76, (byte) 0x9E, (byte) 0x30, (byte) 0x0E, (byte) 0xE4, (byte) 0xB2, (byte) 0x94, (byte) 0xA0, (byte) 0x3B, (byte) 0x34, (byte) 0x1D,
            (byte) 0x28, (byte) 0x0F, (byte) 0x36, (byte) 0xE3, (byte) 0x23, (byte) 0xB4, (byte) 0x03, (byte) 0xD8, (byte) 0x90, (byte) 0xC8, (byte) 0x3C, (byte) 0xFE, (byte) 0x5E, (byte) 0x32, (byte) 0x24, (byte) 0x50,
            (byte) 0x1F, (byte) 0x3A, (byte) 0x43, (byte) 0x8A, (byte) 0x96, (byte) 0x41, (byte) 0x74, (byte) 0xAC, (byte) 0x52, (byte) 0x33, (byte) 0xF0, (byte) 0xD9, (byte) 0x29, (byte) 0x80, (byte) 0xB1, (byte) 0x16,
            (byte) 0xD3, (byte) 0xAB, (byte) 0x91, (byte) 0xB9, (byte) 0x84, (byte) 0x7F, (byte) 0x61, (byte) 0x1E, (byte) 0xCF, (byte) 0xC5, (byte) 0xD1, (byte) 0x56, (byte) 0x3D, (byte) 0xCA, (byte) 0xF4, (byte) 0x05,
            (byte) 0xC6, (byte) 0xE5, (byte) 0x08, (byte) 0x49
    };
    // Maple 95v key -
    private static final SecretKeySpec SECRET_KEY = new SecretKeySpec(new byte[]{
            // At ZInflator::`vftable (entry point to get to the AES key)
            // Found in ZLZ.dll at sub_10001340() | if i'm not wrong xD
            // Good Ref -> https://forum.ragezone.com/threads/trying-to-learn-ida.994320/ | auto generator to get the key for low version: https://forum.ragezone.com/threads/release-gms-key-retriever.895646/page-2
            0x13, 0x00, 0x00, 0x00, 0x08,
            0x00, 0x00, 0x00, 0x06, 0x00,
            0x00, 0x00, (byte) 0xB4, 0x00,
            0x00, 0x00, 0x1B, 0x00, 0x00,
            0x00, 0x0F, 0x00, 0x00, 0x00,
            0x33, 0x00, 0x00, 0x00, 0x52,
            0x00, 0x00, 0x00
    }, "AES");

    // Logger -
    private final static Logger logger = new Logger(MapleCrypto.class);

    public static void initialize(){
        sVersion = (short) ((((0xFFFF - ServerConstants.VERSION) >> 8) & 0xFF) | (((0xFFFF - ServerConstants.VERSION) << 8) & 0xFF00));
        rVersion =  (short) (((ServerConstants.VERSION >> 8) & 0xFF) | ((ServerConstants.VERSION << 8) & 0xFF00));
    }

    public MapleCrypto(byte[] siv, byte[] riv){
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            System.err.println("ERROR" + e);
        }

        this.setSiv(siv);
        this.setRiv(riv);
    }

    /**
     * Gets the packet length from a header.
     *
     * @param packetHeader The header as an integer.
     * @return The length of the packet.
     */
    public static int getPacketLength(int packetHeader) {
        int packetLength = ((packetHeader >>> 16) ^ (packetHeader & 0xFFFF));
        packetLength = ((packetLength << 8) & 0xFF00) | ((packetLength >>> 8) & 0xFF); // fix endianness
        return packetLength;
    }

    /**
     * Gets a new IV from <code>oldIv</code>
     *
     * @param oldIv The old IV to get a new IV from.
     * @return The new IV.
     */
    public static byte[] getNewIv(byte[] oldIv) {
        byte[] in = {(byte) 0xf2, 0x53, (byte) 0x50, (byte) 0xc6}; // magic
        for (int x = 0; x < 4; x++) {
            funnyShit(oldIv[x], in);
        }
        return in;
    }

    /**
     * Does funny stuff. <code>this.OldIV</code> must not equal
     * <code>in</code> Modifies <code>in</code> and returns it for
     * convenience.
     *
     * @param inputByte The byte to apply the funny stuff to.
     * @param in        Something needed for all this to occur.
     * @return The modified version of <code>in</code>.
     */
    public static void funnyShit(byte inputByte, byte[] in) {
        byte elina = in[1];
        byte anna = inputByte;
        byte moritz = funnyBytes[(int) elina & 0xFF];
        moritz -= inputByte;
        in[0] += moritz;
        moritz = in[2];
        moritz ^= funnyBytes[(int) anna & 0xFF];
        elina -= (int) moritz & 0xFF;
        in[1] = elina;
        elina = in[3];
        moritz = elina;
        elina -= (int) in[0] & 0xFF;
        moritz = funnyBytes[(int) moritz & 0xFF];
        moritz += inputByte;
        moritz ^= in[2];
        in[2] = moritz;
        elina += (int) funnyBytes[(int) anna & 0xFF] & 0xFF;
        in[3] = elina;

        int merry = ((int) in[0]) & 0xFF;
        merry |= (in[1] << 8) & 0xFF00;
        merry |= (in[2] << 16) & 0xFF0000;
        merry |= (in[3] << 24) & 0xFF000000;
        int ret_value = merry >>> 0x1d;
        merry <<= 3;
        ret_value |= merry;

        in[0] = (byte) (ret_value & 0xFF);
        in[1] = (byte) ((ret_value >> 8) & 0xFF);
        in[2] = (byte) ((ret_value >> 16) & 0xFF);
        in[3] = (byte) ((ret_value >> 24) & 0xFF);
    }

    public byte[] cryptInPacket(byte[] data) {
        byte[] cryptPacket = crypt(data, getRiv());
        setRiv(getNewIv(getRiv()));
        return cryptPacket;
    }

    public byte[] cryptOutPacket(byte[] data) {
        byte[] cryptPacket = crypt(data, getSiv());
        setSiv(getNewIv(getSiv()));
        return cryptPacket;
    }

    /**
     * Encrypts <code>data</code> and generates a new IV.
     *
     * @param data The bytes to encrypt.
     * @return The encrypted bytes.
     */
    public byte[] crypt(byte[] data, byte[] currIV) {
        int remaining = data.length;
        int llength = 0x5B0;
        int start = 0;

        try {
            while (remaining > 0) {
                byte[] myIv = BitTools.multiplyBytes(currIV, 4, 4);
                if (remaining < llength) {
                    llength = remaining;
                }
                for (int x = start; x < (start + llength); x++) {
                    if ((x - start) % myIv.length == 0) {
                        byte[] newIv = cipher.doFinal(myIv);
                        System.arraycopy(newIv, 0, myIv, 0, myIv.length);
                    }
                    data[x] ^= myIv[(x - start) % myIv.length];
                }
                start += llength;
                remaining -= llength;
                llength = 0x5B4;
            }
            currIV = getNewIv(currIV);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return data;
    }

    public byte[] getInPacketHeader(int length) {
        return getPacketHeader(length, getRiv(), rVersion);
    }

    public byte[] getOutPacketHeader(int length) {
        return getPacketHeader(length, getSiv(), sVersion);
    }

    /**
     * Generates a packet header for a packet that is <code>length</code>
     * long.
     *
     * @param length How long the packet that this header is for is.
     * @return The header.
     */
    public byte[] getPacketHeader(int length, byte[] iv, short mapleVersion) {
        int iiv = (((iv[3]) & 0xFF) | ((iv[2] << 8) & 0xFF00)) ^ mapleVersion;
        int mlength = (((length << 8) & 0xFF00) | (length >>> 8)) ^ iiv;

        return new byte[]{(byte) ((iiv >>> 8) & 0xFF), (byte) (iiv & 0xFF), (byte) ((mlength >>> 8) & 0xFF), (byte) (mlength & 0xFF)};
    }

    public boolean checkInPacket(byte[] packet){
        return checkPacket(packet, getRiv(), rVersion);
    }

    public boolean checkOutPacket(byte[] packet){
        return checkPacket(packet, getSiv(), sVersion);
    }

    /**
     * Check the packet to make sure it has a header.
     *
     * @param packet The packet to check.
     * @return <code>True</code> if the packet has a correct header,
     * <code>false</code> otherwise.
     */
    public boolean checkPacket(byte[] packet, byte[] iv, short mapleVersion) {
        return ((((packet[0] ^ iv[2]) & 0xFF) == ((mapleVersion >> 8) & 0xFF)) && (((packet[1] ^ iv[3]) & 0xFF) == (mapleVersion & 0xFF)));
    }

    public boolean checkInPacket(int packetHeader) {
        return checkPacket(new byte[]{(byte) ((packetHeader >> 24) & 0xFF), (byte) ((packetHeader >> 16) & 0xFF)},getRiv(),rVersion);
    }

    public boolean checkOutPacket(int packetHeader) {
        return checkPacket(new byte[]{(byte) ((packetHeader >> 24) & 0xFF), (byte) ((packetHeader >> 16) & 0xFF)},getSiv(),sVersion);
    }

    /**
     * Encrypts <code>data</code> with Maple's encryption routines.
     *
     * @param data The data to encrypt.
     */
    public static void encryptData(final byte[] data) {

        for (int j = 0; j < 6; j++) {
            byte remember = 0;
            byte dataLength = (byte) (data.length & 0xFF);
            // printByteArray(data);
            if (j % 2 == 0) {
                for (int i = 0; i < data.length; i++) {
                    byte cur = data[i];
                    cur = BitTools.rollLeft(cur, 3);
                    cur += dataLength;
                    cur ^= remember;
                    remember = cur;
                    cur = BitTools.rollRight(cur, (int) dataLength & 0xFF);
                    cur = ((byte) ((~cur) & 0xFF));
                    cur += 0x48;
                    dataLength--;
                    data[i] = cur;
                }
            } else {
                for (int i = data.length - 1; i >= 0; i--) {
                    byte cur = data[i];
                    cur = BitTools.rollLeft(cur, 4);
                    cur += dataLength;
                    cur ^= remember;
                    remember = cur;
                    cur ^= 0x13;
                    cur = BitTools.rollRight(cur, 3);
                    dataLength--;
                    data[i] = cur;
                }
            }
        }
    }

    /**
     * Decrypts <code>data</code> with Maple's encryption routines.
     *
     * @param data The data to decrypt.
     */
    public static void decryptData(final byte[] data) {
        for (int j = 1; j <= 6; j++) {
            byte remember = 0;
            byte dataLength = (byte) (data.length & 0xFF);
            byte nextRemember = 0;

            if (j % 2 == 0) {
                for (int i = 0; i < data.length; i++) {
                    byte cur = data[i];
                    cur -= 0x48;
                    cur = ((byte) ((~cur) & 0xFF));
                    cur = BitTools.rollLeft(cur, (int) dataLength & 0xFF);
                    nextRemember = cur;
                    cur ^= remember;
                    remember = nextRemember;
                    cur -= dataLength;
                    cur = BitTools.rollRight(cur, 3);
                    data[i] = cur;
                    dataLength--;
                }
            } else {
                for (int i = data.length - 1; i >= 0; i--) {
                    byte cur = data[i];
                    cur = BitTools.rollLeft(cur, 3);
                    cur ^= 0x13;
                    nextRemember = cur;
                    cur ^= remember;
                    remember = nextRemember;
                    cur -= dataLength;
                    cur = BitTools.rollRight(cur, 4);
                    data[i] = cur;
                    dataLength--;
                }
            }
        }
    }

    /**
     * Returns the IV of this instance as a string.
     */
    @Override
    public String toString() {
        return "SentIV: " + HexTool.toHexString(this.getSiv()) + ", ReceiveIV: " + HexTool.toHexString(this.getRiv());
    }
}
