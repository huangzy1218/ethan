package com.ethan.common.util;

import org.junit.Test;

import java.io.IOException;

public class PropertyUtilsTest {

    @Test
    public void testProperties() throws IOException {
        PropertyUtils.getProperty("ethan.config");
    }

}