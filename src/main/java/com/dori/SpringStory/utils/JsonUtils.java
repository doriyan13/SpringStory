package com.dori.SpringStory.utils;

import com.dori.SpringStory.logger.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.FileWriter;

public interface JsonUtils {
    // Logger -
    Logger logger = new Logger(JsonUtils.class);

    static void createJsonFile(Object jsonToCreate, String filePath) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String json = ow.writeValueAsString(jsonToCreate);
            FileWriter fw = new FileWriter(filePath);
            fw.write(json);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            logger.error("Error occurred while trying to create the json file to: " + filePath);
            e.printStackTrace();
        }
    }
}
