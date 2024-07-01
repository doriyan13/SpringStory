package com.dori.SpringStory.utils;

import com.dori.SpringStory.dataHandlers.wzData.property.WzListProperty;
import com.dori.SpringStory.dataHandlers.wzData.property.WzVectorProperty;
import com.dori.SpringStory.utils.utilEntities.Rect;
import org.springframework.stereotype.Service;

@Service
public interface WzUtils {
    static int getInteger(Object object) {
        if (object instanceof Short value) {
            return value;
        } else if (object instanceof Integer value) {
            return value;
        } else if (object instanceof String value) {
            return Integer.parseInt(value);
        }
        throw new RuntimeException("Unexpected or missing value while extracting Integer");
    }

    static int getInteger(Object object, int defaultValue) {
        if (object instanceof Short value) {
            return value;
        } else if (object instanceof Integer value) {
            return value;
        } else if (object instanceof String value) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    static String getString(Object object) {
        if (object instanceof Integer value) {
            return String.valueOf(value);
        } else if (object instanceof String value) {
            return value;
        }
        throw new RuntimeException("Unexpected or missing value while extracting String");
    }

    static String getString(Object object, String defaultValue) {
        if (object instanceof Integer value) {
            return String.valueOf(value);
        } else if (object instanceof String value) {
            return value;
        }
        return defaultValue;
    }

    static Rect getRect(WzListProperty prop) {
        WzVectorProperty lt = prop.get("lt");
        WzVectorProperty rb = prop.get("rb");
        return new Rect(
                lt.x(),
                lt.y(),
                rb.x(),
                rb.y()
        );
    }
}
