package com.ethan.common.config;

import org.junit.jupiter.api.Test;

import static com.ethan.common.config.AbstractConfig.getTagName;

class ConfigManagerTest {

    private ConfigManager configManager = new ConfigManager();

    @Test
    void testAddConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        configManager.addConfig(registryConfig);
        String tagName = getTagName(RegistryConfig.class);
        AbstractConfig configs = configManager.getConfigs(tagName);
        System.out.println(configs);
    }

}