package com.ethan.common.config;

import com.ethan.rpc.model.ApplicationModel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Use to set and clear System property.
 *
 * @author Huang Z.Y.
 */
public class SysProps {

    private static Environment environment = ApplicationModel.defaultModel().modelEnvironment();
    private static Map<String, Object> map = new LinkedHashMap<>();

    public static void reset() {
        map.clear();
    }

    public static void setProperty(String key, Object value) {
        map.put(key, value);
        environment.setProperty(key, value);
        System.setProperty(key, value.toString());
    }

    public static void clear() {
        for (String key : map.keySet()) {
            System.clearProperty(key);
        }
        reset();
    }

}
