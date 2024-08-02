package com.ethan.common.config;

import com.ethan.common.context.ApplicationExt;
import lombok.Getter;

import java.util.Map;
import java.util.Properties;

/**
 * @author Huang Z.Y.
 */
public class Environment implements ApplicationExt {

    public static final String NAME = "environment";
    @Getter
    private Properties properties = new Properties();

    public void loadProperties(Map<String, String> sourceProperties) {
        properties.putAll(sourceProperties);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

}
