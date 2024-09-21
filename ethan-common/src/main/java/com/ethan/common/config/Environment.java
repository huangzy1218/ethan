package com.ethan.common.config;

import com.ethan.common.context.ApplicationExt;
import lombok.Getter;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
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

    public void loadProperties(PropertySource<?> sourceProperties) {
        MapPropertySource mapSource = (MapPropertySource) sourceProperties;
        Map<String, Object> propertiesMap = mapSource.getSource();
        for (Map.Entry<String, Object> entry : propertiesMap.entrySet()) {
            String key = entry.getKey();
            String valueStr = entry.getValue().toString();
            Object value = tryParseInteger(valueStr) != null ? Integer.parseInt(valueStr) : valueStr;
            this.properties.put(key, value);
        }
    }

    private Integer tryParseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
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
