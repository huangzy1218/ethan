package com.ethan.common.config;

import com.ethan.rpc.model.ApplicationModel;
import org.junit.jupiter.api.Test;

import static com.ethan.common.config.AbstractConfig.getTagName;

class ConfigManagerTest {

    private ConfigManager configManager = ApplicationModel.defaultModel().getApplicationConfigManager();

    @Test
    void testAddConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        configManager.addConfig(registryConfig);
        String tagName = getTagName(RegistryConfig.class);
        AbstractConfig configs = configManager.getConfigs(tagName);
        System.out.println(configs);
    }

//    @Test
//    void testGetConfigIdFromProps() {
//        SysProps.setProperty("ethan.registry.host", "127.0.0.1");
//        SysProps.setProperty("ethan.registry.port", "8080");
//        Map<String, Object> configIdFromProps = configManager.getConfigIdFromProps(RegistryConfig.class);
//        System.out.println(configIdFromProps);
//    }

    @Test
    void testLoadConfigsOfTypeFromProps() {
        SysProps.setProperty("ethan.registry.host", "127.0.0.1");
        SysProps.setProperty("ethan.registry.port", "8080");
        configManager.loadConfigsOfTypeFromProps(RegistryConfig.class);
    }

}