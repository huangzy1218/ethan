package com.ethan.common.util;

import com.ethan.rpc.model.ApplicationModel;

/**
 * Property utility.
 *
 * @author Huang Z.Y.
 */
public class PropertyUtils {

    public static String getProperty(String key, String defaultValue) {
        String value = (String) ApplicationModel.defaultModel().modelEnvironment().getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static int getProperty(String key, int defaultValue) {
        Object value = ApplicationModel.defaultModel().modelEnvironment().getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value.toString());
    }

    public static String getProperty(String key) {
        return (String) ApplicationModel.defaultModel().modelEnvironment().getProperty(key);
    }

}
