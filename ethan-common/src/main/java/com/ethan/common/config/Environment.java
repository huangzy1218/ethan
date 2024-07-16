package com.ethan.common.config;

import com.ethan.common.context.ApplicationExt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Huang Z.Y.
 */
public class Environment implements ApplicationExt {

    public static final String NAME = "environment";
    private final Map<String, String> properties = new ConcurrentHashMap<>();
    private final Map<String, Object> configurationMaps = new ConcurrentHashMap<>();

    public void loadProperties(Map<String, String> sourceProperties) {
        properties.putAll(sourceProperties);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public Map<String, String> getAllProperties() {
        return new ConcurrentHashMap<>(properties);
    }

    public void loadConfigurations(Map<String, Object> configs) {
        configurationMaps.putAll(configs);
    }

    public Object getConfiguration(String key) {
        return configurationMaps.get(key);
    }

    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    public boolean hasConfiguration(String key) {
        return configurationMaps.containsKey(key);
    }
    
    public boolean hasSubProperties(String prefix) {
        return properties.keySet().stream().anyMatch(key -> key.startsWith(prefix));
    }

    public Map<String, Object> getConfigurationMaps() {
        return new ConcurrentHashMap<>(configurationMaps);
    }

}
