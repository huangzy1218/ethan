package com.ethan.common.util;

import lombok.Setter;
import org.springframework.core.env.Environment;

/**
 * @author Huang Z.Y.
 */
public class PropertyUtils {

    @Setter
    private static Environment environment;

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

}
