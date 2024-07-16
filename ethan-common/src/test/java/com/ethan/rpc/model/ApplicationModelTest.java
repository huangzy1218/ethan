package com.ethan.rpc.model;

import com.ethan.common.config.ConfigManager;
import com.ethan.common.config.RegistryConfig;
import org.junit.jupiter.api.Test;

public class ApplicationModelTest {

    @Test
    void testGetRegistryConfig() {
        ConfigManager configManager = ApplicationModel.defaultModel().getApplicationConfigManager();
        configManager.addConfig(new RegistryConfig());
        System.out.println(configManager.getRegistry());

    }

}