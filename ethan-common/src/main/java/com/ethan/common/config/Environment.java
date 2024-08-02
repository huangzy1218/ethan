package com.ethan.common.config;

import com.ethan.common.context.ApplicationExt;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

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

    public Object getProperty(String key, Object defaultValue) {
        Object value = properties.get(key);
        if (ObjectUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

}
