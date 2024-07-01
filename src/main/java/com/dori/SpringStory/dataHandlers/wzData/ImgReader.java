package com.dori.SpringStory.dataHandlers.wzData;

import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.constants.WzConstants;
import com.dori.SpringStory.dataHandlers.wzData.property.WzListProperty;
import com.dori.SpringStory.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface ImgReader {
    // Logger -
    Logger logger = new Logger(ImgReader.class);

    static WzImage readImage(@NotNull Path path) {
        try (WzReader reader = WzReader.build(path, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.VERSION))) {
            WzImage image = new WzImage(0);
            if (!(reader.readProperty(image, reader.getBuffer(0)) instanceof WzListProperty listProperty)) {
                throw new WzReaderError("Image property is not a list");
            }
            image.setProperty(listProperty);
            return image;
        } catch (Exception e) {
            logger.error("Failed to read image from " + path, e);
            return null;
        }
    }

}
