package com.ethan.common.config;

import com.ethan.common.context.ApplicationExt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Huang Z.Y.
 */
public class Environment implements ApplicationExt {

    public static final String NAME = "environment";
    private final Map<String, Object> properties = new ConcurrentHashMap<>();

    public void loadProperties(Map<String, String> sourceProperties) {
        properties.putAll(sourceProperties);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Map<String, Object> getPropertiesMap() {
        return new ConcurrentHashMap<>(properties);
    }

}
