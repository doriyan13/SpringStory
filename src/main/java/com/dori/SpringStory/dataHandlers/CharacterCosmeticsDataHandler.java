package com.dori.SpringStory.dataHandlers;

import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.dataHandlers.dataEntities.CharacterCosmeticsData;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.JsonUtils;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.XMLApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;

import static com.dori.SpringStory.constants.ServerConstants.*;

@Service
public class CharacterCosmeticsDataHandler {
    // Logger -
    private static final Logger logger = new Logger(CharacterCosmeticsDataHandler.class);
    private static final Set<Integer> hairs = new HashSet<>();
    private static final Set<Integer> faces = new HashSet<>();

    private static final String JSON_DATA_FILE_NAME = "CharCosmeticsData";

    public static List<Integer> getAllHairs() {
        return new ArrayList<>(hairs);
    }

    public static List<Integer> getAllUniqueHairs() {
        return hairs
                .stream()
                .filter(hair -> hair % 10 == 0)
                .toList();
    }

    public static List<Integer> getAllFaces() {
        return new ArrayList<>(faces);
    }

    public static List<Integer> getAllUniqueFaces() {
        return faces
                .stream()
                .filter(face -> face % 10 == 0)
                .toList();
    }

    private static void loadCharacterBaseData(boolean hair) {
        File dir = new File(hair ? ServerConstants.HAIR_WZ_DIR : ServerConstants.FACE_WZ_DIR);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                Node node = XMLApi.getRoot(file);
                if (node == null) {
                    continue;
                }
                List<Node> nodes = XMLApi.getAllChildren(node);
                for (Node mainNode : nodes) {
                    Map<String, String> attributes = XMLApi.getAttributes(mainNode);
                    String rootIdStr = attributes.get("name").replace(".img", "");
                    if (MapleUtils.isNumber(rootIdStr)) {
                        int rootId = Integer.parseInt(rootIdStr);
                        if (hair) {
                            hairs.add(rootId);
                        } else {
                            faces.add(rootId);
                        }
                    }
                }
            }
        }
    }

    public static void loadHairData() {
        logger.startLoad("WZ", "Hair Data");
        long startTime = System.currentTimeMillis();
        loadCharacterBaseData(true);
        // Load all the String Data -
        logger.finishLoad(hairs.size(), "WZ", "Hair Data", ((System.currentTimeMillis() - startTime) / 1000.0));
    }

    public static void loadFaceData() {
        logger.startLoad("WZ", "Face Data");
        long startTime = System.currentTimeMillis();
        loadCharacterBaseData(false);
        // Load all the String Data -
        logger.finishLoad(faces.size(), "WZ", "Face Data", ((System.currentTimeMillis() - startTime) / 1000.0));
    }

    private static void exportCharacterCosmeticsToJson() {
        logger.serverNotice("Start creating the JSONs for character cosmetics..");
        MapleUtils.makeDirIfAbsent(JSON_DIR);
        MapleUtils.makeDirIfAbsent(CHARACTER_COSMETICS_JSON_DIR);
        JsonUtils.createJsonFile(new CharacterCosmeticsData(hairs,faces), CHARACTER_COSMETICS_JSON_DIR + JSON_DATA_FILE_NAME + ".json");
        logger.serverNotice("~ Finished creating the character cosmetics JSON file! ~");
    }

    public static void loadJsonCharacterCosmetics() {
        long startTime = System.currentTimeMillis();
        File file = new File(CHARACTER_COSMETICS_JSON_DIR + JSON_DATA_FILE_NAME + ".json");
        logger.startLoad("JSON","Character Cosmetics");
        if (file.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                CharacterCosmeticsData characterCosmeticsData = mapper.readValue(file, CharacterCosmeticsData.class);
                hairs.addAll(characterCosmeticsData.hairs());
                faces.addAll(characterCosmeticsData.faces());
            } catch (Exception e) {
                logger.error("Error occurred while trying to load the file: " + file.getName());
                e.printStackTrace();
            }
            logger.finishLoad(hairs.size(), "JSON", "Hairs",((System.currentTimeMillis() - startTime) / 1000.0));
            logger.finishLoad(faces.size(), "JSON", "Faces",((System.currentTimeMillis() - startTime) / 1000.0));
        } else {
            logger.error("Didn't found Character Cosmetics JSONs to load!");
        }
    }

    private static boolean isJsonDataExist() {
        File skillDir = new File(CHARACTER_COSMETICS_JSON_DIR);
        return skillDir.exists();
    }

    public static void load() {
        if (isJsonDataExist()) {
            loadJsonCharacterCosmetics();
        } else {
            loadHairData();
            loadFaceData();
            exportCharacterCosmeticsToJson();
        }
    }
}
