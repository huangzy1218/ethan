package com.ethan.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationUtilsTest {

    @Test
    void testGetSubProperties() {
        // Create a configuration Map
        Map<String, String> configMap = new HashMap<>();
        configMap.put("ethan.protocol.name", "ethan");
        configMap.put("ethan.protocol.port", "1234");
        configMap.put("ethan.other.property", "value");
        
        Map<String, String> subProperties = ConfigurationUtils.getSubProperties(configMap, "ethan.protocol.");

        Assertions.assertEquals(2, subProperties.size());
        Assertions.assertEquals("ethan", subProperties.get("name"));
        Assertions.assertEquals("1234", subProperties.get("port"));
    }
}