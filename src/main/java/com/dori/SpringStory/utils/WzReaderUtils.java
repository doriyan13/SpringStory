package com.dori.SpringStory.utils;

import com.dori.SpringStory.constants.WzConstants;
import com.dori.SpringStory.dataHandlers.wzData.WzImage;
import com.dori.SpringStory.dataHandlers.wzData.WzReader;
import com.dori.SpringStory.dataHandlers.wzData.WzReaderError;
import com.dori.SpringStory.dataHandlers.wzData.property.*;
import org.jetbrains.annotations.NotNull;

import javax.crypto.ShortBufferException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface WzReaderUtils {

    static WzProperty readListProperty(@NotNull WzImage image,
                                       @NotNull ByteBuffer buffer,
                                       @NotNull WzReader reader) throws ShortBufferException {
        buffer.getShort(); // reserved
        return new WzListProperty(reader.readListItems(image, buffer));
    }

    static WzProperty readCanvasProperty(@NotNull WzImage image,
                                         @NotNull ByteBuffer buffer,
                                         @NotNull WzReader reader) throws ShortBufferException {
        buffer.position(buffer.position() + 1);
        boolean hasProperties = buffer.get() == 1;
        WzListProperty properties;
        if (hasProperties) {
            buffer.position(buffer.position() + 2);
            properties = new WzListProperty(reader.readListItems(image, buffer));
        } else {
            properties = new WzListProperty(Map.of());
        }
        // Canvas meta
        int width = reader.readCompressedInt(buffer);
        int height = reader.readCompressedInt(buffer);
        int format = reader.readCompressedInt(buffer);
        int format2 = reader.readCompressedInt(buffer);
        buffer.position(buffer.position() + 4);
        // Canvas data
        int dataSize = buffer.getInt() - 1;
        buffer.position(buffer.position() + 1);
        ByteBuffer dataSlice = buffer.slice(buffer.position(), dataSize);
        buffer.position(buffer.position() + dataSize);
        return new WzCanvasProperty(properties, width, height, format, format2, dataSlice);
    }

    static WzProperty readVectorProperty(@NotNull ByteBuffer buffer,
                                         @NotNull WzReader reader) {
        return new WzVectorProperty(reader.readCompressedInt(buffer), reader.readCompressedInt(buffer));
    }

    static WzProperty readConvexProperty(@NotNull WzImage image,
                                         @NotNull ByteBuffer buffer,
                                         @NotNull WzReader reader) throws ShortBufferException {
        List<WzProperty> properties = new ArrayList<>();
        int size = reader.readCompressedInt(buffer);
        for (int i = 0; i < size; i++) {
            properties.add(reader.readProperty(image, buffer));
        }
        return new WzConvexProperty(properties);
    }

    static WzProperty readSoundProperty(@NotNull ByteBuffer buffer,
                                        @NotNull WzReader reader) {
        buffer.position(buffer.position() + 1);
        int dataSize = reader.readCompressedInt(buffer);
        reader.readCompressedInt(buffer); // duration
        // Read header info
        int headerOffset = buffer.position();
        buffer.position(buffer.position() + WzConstants.SOUND_HEADER.length);
        int formatSize = Byte.toUnsignedInt(buffer.get());
        buffer.position(buffer.position() + formatSize);
        // Create slices
        ByteBuffer headerSlice = buffer.slice(headerOffset, buffer.position() - headerOffset);
        ByteBuffer dataSlice = buffer.slice(buffer.position(), dataSize);
        buffer.position(buffer.position() + dataSize);
        return new WzSoundProperty(headerSlice, dataSlice);
    }

    static WzProperty readUolProperty(@NotNull WzImage image,
                                      @NotNull ByteBuffer buffer,
                                      @NotNull WzReader reader) throws ShortBufferException {
        buffer.position(buffer.position() + 1);
        return new WzUolProperty(reader.readStringBlock(image, buffer));
    }

    static void applyFloatItemToList(@NotNull ByteBuffer buffer,
                                     @NotNull Map<String, Object> items,
                                     @NotNull String itemName) {
        byte floatType = buffer.get();
        switch (floatType) {
            case 0x00 -> items.put(itemName, 0f);
            case (byte) 0x80 -> items.put(itemName, buffer.getFloat());
            default -> throw new WzReaderError("Unknown float type : %d", floatType);
        }
    }

    static void applyPositionItemToList(@NotNull WzImage image,
                                        @NotNull ByteBuffer buffer,
                                        @NotNull Map<String, Object> items,
                                        @NotNull String itemName,
                                        @NotNull WzReader reader) throws ShortBufferException {
        int propertySize = buffer.getInt();
        int propertyOffset = buffer.position();
        buffer.position(propertyOffset);
        WzProperty property = reader.readProperty(image, buffer);
        items.put(itemName, property);
        buffer.position(propertyOffset + propertySize);
    }

    static void applyLongItemToList(@NotNull ByteBuffer buffer,
                                     @NotNull Map<String, Object> items,
                                     @NotNull String itemName) {
        long value = buffer.get();
        if (value == Byte.MIN_VALUE) {
            items.put(itemName, buffer.getLong());
        } else {
            items.put(itemName, value);
        }
    }
}
