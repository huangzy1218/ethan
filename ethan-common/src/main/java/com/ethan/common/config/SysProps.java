package com.ethan.common.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Use to set and clear System property.
 *
 * @author Huang Z.Y.
 */
public class SysProps {

    private static Map<String, String> map = new LinkedHashMap<String, String>();

    public static void reset() {
        map.clear();
    }

    public static void setProperty(String key, String value) {
        map.put(key, value);
        System.setProperty(key, value);
    }

    public static void clear() {
        for (String key : map.keySet()) {
            System.clearProperty(key);
        }
        reset();
    }

}
