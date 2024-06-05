package com.ethan.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Configuration file tool class.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class PropertiesUtils {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static Properties readPropertiesFile(String filename) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String configPath = "";
        if (url != null) {
            configPath = url.getPath() + filename;
        }
        Properties properties = null;
        try (InputStreamReader inputStreamReader = new InputStreamReader(
                Files.newInputStream(Paths.get(configPath)), CHARSET)) {
            properties = new Properties();
            properties.load(inputStreamReader);
        } catch (IOException e) {
            log.error("Occur exception when read properties file [{}]", filename);
        }
        return properties;
    }

}
