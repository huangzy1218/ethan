package com.ethan.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;

public class ConfigurationUtilsTest {

    @Test
    void testGetSubProperties() {
        // Create a configuration Map
        Properties properties = new Properties();
        properties.put("ethan.protocol.name", "ethan");
        properties.put("ethan.protocol.port", "1234");
        properties.put("ethan.other.property", "value");

        Map<String, String> subProperties = ConfigurationUtils.getSubProperties(properties, "ethan.protocol.");

        Assertions.assertEquals(2, subProperties.size());
        System.out.println(subProperties);
        Assertions.assertEquals("ethan", subProperties.get("name"));
        Assertions.assertEquals("1234", subProperties.get("port"));
    }
}