package com.ethan.common.util;

import java.util.*;

/**
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
     * props: {"name": "dubbo", "port" : "1234"}
     *
     * </pre>
     *
     * @param configMaps
     * @param prefix
     * @param <V>
     * @return
     */
    public static <V extends Object> Map<String, V> getSubProperties(
            Collection<Map<String, V>> configMaps, String prefix) {
        Map<String, V> map = new LinkedHashMap<>();
        for (Map<String, V> configMap : configMaps) {
            getSubProperties(configMap, prefix, map);
        }
        return map;
    }

    private static <V extends Object> Map<String, V> getSubProperties(
            Map<String, V> configMap, String prefix, Map<String, V> resultMap) {
        if (!prefix.endsWith(".")) {
            prefix += ".";
        }

        if (null == resultMap) {
            resultMap = new LinkedHashMap<>();
        }

        if (CollectionUtils.isNotEmptyMap(configMap)) {
            Map<String, V> copy;
            synchronized (configMap) {
                copy = new HashMap<>(configMap);
            }
            for (Map.Entry<String, V> entry : copy.entrySet()) {
                String key = entry.getKey();
                V val = entry.getValue();
                if (StringUtils.startsWithIgnoreCase(key, prefix)
                        && key.length() > prefix.length()
                        && !ConfigurationUtils.isEmptyValue(val)) {

                    String k = key.substring(prefix.length());
                    // convert camelCase/snake_case to kebab-case
                    String newK = StringUtils.convertToSplitName(k, "-");
                    resultMap.putIfAbsent(newK, val);
                    if (!Objects.equals(k, newK)) {
                        resultMap.putIfAbsent(k, val);
                    }
                }
            }
        }
    }

    public static boolean isEmptyValue(Object value) {
        return value == null || value instanceof String && StringUtils.isBlank((String) value);
    }

}
