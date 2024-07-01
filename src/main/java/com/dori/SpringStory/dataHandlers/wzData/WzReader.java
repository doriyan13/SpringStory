package com.dori.SpringStory.dataHandlers.wzData;

import com.dori.SpringStory.constants.WzConstants;
import com.dori.SpringStory.dataHandlers.wzData.property.*;
import com.dori.SpringStory.utils.WzReaderUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.crypto.ShortBufferException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WzReader implements AutoCloseable {
    private RandomAccessFile file;
    private FileChannel channel;
    private WzReaderConfig config;
    private WzCrypto crypto;

    public ByteBuffer getBuffer(int offset) throws IOException {
        return channel
                .map(FileChannel.MapMode.READ_ONLY, offset, file.length())
                .order(ByteOrder.LITTLE_ENDIAN);
    }

    private int computeVersionHash(int version) {
        int versionHash = 0;
        for (byte c : String.valueOf(version).getBytes()) {
            versionHash = (versionHash * 32) + c + 1;
        }
        return versionHash;
    }

    private int readOffset(@NotNull WzPackage parent,
                           @NotNull ByteBuffer buffer) {
        int start = parent.getStart();
        int hash = parent.getHash();
        int result = buffer.position();
        result = ~(result - start);
        result = result * hash;
        result = result - WzConstants.WZ_OFFSET_CONSTANT;
        result = Integer.rotateLeft(result, result & 0x1F);
        result = result ^ buffer.getInt(); // encrypted offset
        result = result + (start * 2);
        return result;
    }

    private String readString(@NotNull ByteBuffer buffer) throws ShortBufferException {
        int length = buffer.get();
        if (length < 0) {
            length = (length == Byte.MIN_VALUE) ? buffer.getInt() : -length;
            if (length > 0) {
                byte[] data = new byte[length];
                buffer.get(data);
                crypto.cryptAscii(data);
                return new String(data, StandardCharsets.US_ASCII);
            }
        } else if (length > 0) {
            if (length == Byte.MAX_VALUE) {
                length = buffer.getInt();
            }
            if (length > 0) {
                length = length * 2; // UTF16
                byte[] data = new byte[length];
                buffer.get(data);
                crypto.cryptUnicode(data);
                return new String(data, StandardCharsets.UTF_16LE);
            }
        }
        return "";
    }

    public int readCompressedInt(@NotNull ByteBuffer buffer) {
        byte value = buffer.get();
        if (value == Byte.MIN_VALUE) {
            return buffer.getInt();
        } else {
            return value;
        }
    }

    public String readStringBlock(@NotNull WzImage image,
                                  @NotNull ByteBuffer buffer) throws WzReaderError, ShortBufferException {
        byte stringType = buffer.get();
        switch (stringType) {
            case 0x00, 0x73 -> {
                return readString(buffer);
            }
            case 0x01, 0x1B -> {
                int stringOffset = buffer.getInt();
                int originalPosition = buffer.position();
                buffer.position(image.getOffset() + stringOffset);
                String string = readString(buffer);
                buffer.position(originalPosition);
                return string;
            }
            default -> throw new WzReaderError("Unknown string block type : %d%n", stringType);
        }
    }

    public WzPackage readPackage() throws IOException, WzReaderError, ShortBufferException {
        return readPackage(0);
    }

    public WzPackage readPackage(int offset) throws IOException, WzReaderError, ShortBufferException {
        ByteBuffer buffer = getBuffer(offset);
        // Check PKG1 header
        if (buffer.getInt() != 0x31474B50) {
            throw new WzReaderError("PKG1 header missing");
        }
        buffer.getLong(); // size
        int start = buffer.getInt();
        // Check version hash
        buffer.position(start);
        int versionHeader = Short.toUnsignedInt(buffer.getShort());
        int versionHash = computeVersionHash(getConfig().version());
        int computedHeader = 0xFF
                ^ ((versionHash >> 24) & 0xFF)
                ^ ((versionHash >> 16) & 0xFF)
                ^ ((versionHash >> 8) & 0xFF)
                ^ (versionHash & 0xFF);
        if (versionHeader != computedHeader) {
            throw new WzReaderError("Incorrect version");
        }
        WzPackage pkg = new WzPackage(start, versionHash);
        pkg.setDirectory(readDirectory(pkg, buffer));
        return pkg;
    }

    public WzDirectory readDirectory(@NotNull WzPackage parent,
                                     @NotNull ByteBuffer buffer) throws WzReaderError, ShortBufferException {
        Map<String, WzDirectory> directories = new HashMap<>();
        Map<String, WzImage> images = new HashMap<>();
        int size = readCompressedInt(buffer);
        for (int i = 0; i < size; i++) {
            String childName;
            byte childType = buffer.get();
            switch (childType) {
                case 1 -> {
                    // unknown : 01 XX 00 00 00 00 00 OFFSET
                    buffer.getInt();
                    buffer.getShort();
                    readOffset(parent, buffer);
                    continue;
                }
                case 2 -> {
                    // string offset
                    int stringOffset = buffer.getInt();
                    int originalPosition = buffer.position();
                    buffer.position(parent.getStart() + stringOffset);
                    childType = buffer.get();
                    childName = readString(buffer);
                    buffer.position(originalPosition);
                }
                case 3, 4 -> childName = readString(buffer); // directory | image
                default -> throw new WzReaderError("Unknown directory child type : %d", childType);
            }
            readCompressedInt(buffer); // childSize
            readCompressedInt(buffer); // childChecksum
            int childOffset = readOffset(parent, buffer);
            int originalPosition = buffer.position();
            buffer.position(childOffset);
            if (childType == 3) {
                directories.put(childName, readDirectory(parent, buffer));
            } else if (childType == 4) {
                final WzImage image = new WzImage(childOffset);
                if (!(readProperty(image, buffer) instanceof WzListProperty listProperty)) {
                    throw new WzReaderError("Image property is not a list");
                }
                image.setProperty(listProperty);
                images.put(childName, image);
            }
            buffer.position(originalPosition);
        }
        return new WzDirectory(directories, images);
    }

    public WzProperty readProperty(@NotNull WzImage image,
                                   @NotNull ByteBuffer buffer) throws WzReaderError, ShortBufferException {
        String propertyTypeId = readStringBlock(image, buffer);
        WzPropertyType propertyType = WzPropertyType.getById(propertyTypeId);
        return switch (propertyType) {
            case LIST -> WzReaderUtils.readListProperty(image, buffer, this);
            case CANVAS -> WzReaderUtils.readCanvasProperty(image, buffer, this);
            case VECTOR -> WzReaderUtils.readVectorProperty(buffer, this);
            case CONVEX -> WzReaderUtils.readConvexProperty(image, buffer, this);
            case SOUND -> WzReaderUtils.readSoundProperty(buffer, this);
            case UOL -> WzReaderUtils.readUolProperty(image, buffer, this);
        };
    }

    public Map<String, Object> readListItems(@NotNull WzImage image,
                                             @NotNull ByteBuffer buffer) throws WzReaderError, ShortBufferException {
        Map<String, Object> items = new HashMap<>();
        int size = readCompressedInt(buffer);
        for (int i = 0; i < size; i++) {
            String itemName = readStringBlock(image, buffer);
            byte itemType = buffer.get();
            TagVarEnum varType = TagVarEnum.getByValue(itemType);
            switch (varType) {
                case VT_EMPTY -> items.put(itemName, null);
                case VT_I2, VT_UI2 -> items.put(itemName, buffer.getShort());
                case VT_I4, VT_UI4 -> items.put(itemName, readCompressedInt(buffer));
                case VT_I8 -> WzReaderUtils.applyLongItemToList(buffer, items, itemName);
                case VT_R4 -> WzReaderUtils.applyFloatItemToList(buffer, items, itemName);
                case VT_R8 -> items.put(itemName, buffer.getDouble());
                case VT_BSTR -> items.put(itemName, readStringBlock(image, buffer));
                case VT_DISPATCH -> WzReaderUtils.applyPositionItemToList(image, buffer, items, itemName, this);
                default -> throw new WzReaderError("Unknown property item type : %d", itemType);
            }
        }
        return items;
    }

    @Override
    public void close() throws IOException {
        file.close();
        channel.close();
    }

    public static WzReader build(@NotNull Path path,
                                 @NotNull WzReaderConfig config) throws Exception {
        return build(path.toFile(), config);
    }

    public static WzReader build(@NotNull File file,
                                 @NotNull WzReaderConfig config) throws Exception {
        return build(file, config, config.buildEncryptor());
    }

    public static WzReader build(@NotNull File file,
                                 @NotNull WzReaderConfig config,
                                 @NotNull WzCrypto crypto) throws FileNotFoundException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        FileChannel fileChannel = randomAccessFile.getChannel();
        return new WzReader(randomAccessFile, fileChannel, config, crypto);
    }
}
