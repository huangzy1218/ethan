package com.ethan.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Configuration utility.
 *
 * @author Huang Z.Y.
 */
public class ConfigurationUtils {

    /**
     * Search props and extract sub properties.
     * <pre>
     * # properties
     * ethan.protocol.name=dubbo
     * ethan.protocol.port=1234
     *
     * # extract protocol props
     * Map props = getSubProperties("ethan.protocol.");
     *
     * # result
     * props: {"name": "ethan", "port" : "1234"}
     * </pre>
     *
     * @param prefix
     * @return Properties key-value map
     */
    public static <V> Map<String, V> getSubProperties(Properties properties, String prefix) {
        Map<String, V> required = new HashMap<>();
        Set<String> propertyNames = properties.stringPropertyNames();
        for (String name : propertyNames) {
            if (name.startsWith(prefix)) {
                // Type conversion is required here because the getProperty method of Properties returns String
                @SuppressWarnings("unchecked")
                V value = (V) properties.getProperty(name);
                String subName = name.substring(prefix.length());
                required.put(subName, value);
            }
        }
        return required;
    }

}
