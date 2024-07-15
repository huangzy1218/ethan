package com.ethan.common.util;

import com.ethan.common.URL;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UrlUtilsTest {

    @Test
    public void testIsServiceKeyMatch() {
        // Create mock URLs
        URL pattern = URL.valueOf("ethan://localhost:10010/DemoService?interface=com.example.DemoService&group=group1&version=1.0");
        URL value = URL.valueOf("ethan://localhost:10010/DemoService?interface=com.example.DemoService&group=group1&version=1.0");

        // Test for a match
        assertTrue(UrlUtils.isServiceKeyMatch(pattern, value));

        // Change version
        URL valueDifferentVersion = URL.valueOf("ethan://localhost:10010/DemoService?interface=com.example.DemoService&group=group1&version=2.0");
        assertFalse(UrlUtils.isServiceKeyMatch(pattern, valueDifferentVersion));

        // Change group
        URL valueDifferentGroup = URL.valueOf("ethan://localhost:10010/DemoService?interface=com.example.DemoService&group=group2&version=1.0");
        assertFalse(UrlUtils.isServiceKeyMatch(pattern, valueDifferentGroup));

        // Test with wildcards
        URL patternWithWildcard = URL.valueOf("ethan://localhost:10010/DemoService?interface=com.example.DemoService&group=*&version=1.0");
        assertTrue(UrlUtils.isServiceKeyMatch(patternWithWildcard, valueDifferentGroup));
    }

}